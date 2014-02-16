package com.iutdijon.androiut2.mail.services;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;

import android.annotation.SuppressLint;
import android.util.Base64;
/**
 * Classe regroupant toutes les fonctions communes aux emails récupérer via le protocol IMAP
 * @author Morgan Funtowicz
 *
 */
@SuppressLint("DefaultLocale")
public class EmailUtils {
	
	public static String extractMessage(Message msg) throws Exception {
		String msg_body = "";
		Object content = msg.getContent();
		if (content instanceof Multipart) {
			msg_body = handleMultipart((Multipart) content);
		} else {
			msg_body = handlePart(msg);
		}
		return msg_body;

	}

	
	public static String parseSenderAddress(String sender) throws UnsupportedEncodingException{
		return MimeUtility.decodeText(sender);
	}
	
	/**
	 * Permet de récupérer le contenu textuel d'un message avec le MIME : multipart/* de manière récursive
	 * @param multipart La multipartie à traiter
	 * @return Le texte contenu dans la multipartie et sous partie de cette dernière
	 * @throws MessagingException Levée quand un problème survient dans la structure du message
	 * @throws IOException Levée lorsqu'une erreur réseau intervient
	 */
	public static String handleMultipart(Multipart multipart)throws MessagingException, IOException {
		StringBuilder data = new StringBuilder();		
		if(multipart.getContentType().toLowerCase().contains("multipart/alternative")){
			
			boolean hasHtml = false;
			for (int i = 0; i < multipart.getCount(); i++) {
				if(multipart.getBodyPart(i).getContentType().toLowerCase().contains("html")){
					hasHtml = true;
					data.append(handlePart(multipart.getBodyPart(i)));
				}
			}
			if(!hasHtml){
				for (int i = 0; i < multipart.getCount(); i++) {
					if(multipart.getBodyPart(i).getContentType().toLowerCase().contains("multipart")){
						BodyPart bp = multipart.getBodyPart(i);
						
						if(bp.getContent()instanceof Multipart){
							Multipart related = (Multipart) bp.getContent();
							data.append(handleMultipart(related));
						}
					}
				}
			}
		}else{
			for (int i = 0, n = multipart.getCount(); i < n; i++) {
				data.append(handlePart(multipart.getBodyPart(i)));
			}
		}
		return data.toString();
	}

	/**
	 * Permet de récupérer le contenu textuel d'un message avec le MIME : part/* 
	 * @param part La partie à traiter
	 * @return Le texte contenu dans part
	 * @throws MessagingException Levée quand un problème survient dans la structure du message
	 * @throws IOException Levée lorsqu'une erreur réseau intervient
	 */
	public static String handlePart(Part part) throws MessagingException, IOException {
		String disposition = part.getDisposition();
		String contentType = part.getContentType();
		String content = "";
		if (disposition == null) { // Body uniquement - Vérifier si de type plain
			if (contentType.toLowerCase().indexOf("text") != -1) {
				
				InputStream is = part.getInputStream();
				content = IOUtils.toString(is, getContentEncoding(part.getContentType()));
				
			} else { 
				if(contentType.toLowerCase().contains("multipart")){
					if(part.getContent() instanceof MimeMultipart){
						content = handleMultipart((Multipart) part.getContent());
					}else{
						Logger.getLogger("EmailUtils").severe("Multipart in body part with other type than MimeMultipart");
					}

				}
			}
		} else if (disposition.equalsIgnoreCase(Part.INLINE)) { 
			String fileExt = part.getFileName().substring(part.getFileName().lastIndexOf(".")+1);
			
			byte[] attach_stream = new byte[part.getSize()];
			InputStream is = part.getInputStream();
			BufferedInputStream bais = new BufferedInputStream(is);
			bais.read(attach_stream);
			
			String base64 = new String(Base64.encode(attach_stream, Base64.DEFAULT));
			
			if(fileExt.equalsIgnoreCase("jpeg") || fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("png") || fileExt.equalsIgnoreCase("webp")){
				content += "<img src='data:image/"+fileExt+";base64,"+base64+"' />";
			}
		}
		return content;
	}
	
	/**
	 * Permet de récupérer la liste des pièces jointes d'un email
	 * @param m L'email dont on souhaite récupérer la liste des pièces jointes
	 * @return Un tableau de string avec le nom des pièces jointes
	 * @throws MessagingException Levée quand un problème survient dans la structure du message
	 * @throws IOException Levée lorsqu'une erreur réseau intervient
	 */
	public static HashMap<String, Part> getAttachments(Message m) throws IOException, MessagingException{
		
		HashMap<String, Part> attachments = new  HashMap<String, Part>(); 
		Object p = m.getContent();
		if(p instanceof Multipart){
			Multipart mp = (Multipart) p;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bodyPart = mp.getBodyPart(i);
				String disposition = bodyPart.getDisposition();
				
				if(disposition != null){
					if(disposition.equalsIgnoreCase(Part.ATTACHMENT)){
						attachments.put(MimeUtility.decodeText(bodyPart.getFileName()), bodyPart);
					}
				}
			}
		}else if(p instanceof Part){
			Part part = (Part) p;
			if(part.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)){
				attachments.put(MimeUtility.decodeText(part.getFileName()), part);
			}
		}
		return attachments.size() == 0 ? null : attachments;
	}
	
	/**
	 * Permet de parser le contentType d'un email afin de déterminer son encodage
	 * @param contentType Le champ ContentType d'un email
	 * @return Son encodage
	 */
	public static String getContentEncoding(String contentType){

		if(contentType.equalsIgnoreCase("text/plain")){
			return "ISO-8859-1";
		}
		StringBuilder encoding = new StringBuilder();
		int charsetPos = contentType.indexOf("charset=") + 8;
		
		char c;
		while(charsetPos < contentType.length() && (c = contentType.charAt(charsetPos)) != ';'){
			encoding.append(c);
			++charsetPos;
		}
		return encoding.toString();
	}
}
