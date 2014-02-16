package com.iutdijon.androiut2.util.bridge;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

/**
 * Permet de cr�er des liens entre un Mime et les application capables d'afficher ce MIME
 * Si aucune application n'est trouv�e, elle affiche un message 
 * @author Morgan Funtowicz
 *
 */
public class BridgeFinder {

	/**
	 * Permet de r�cup�rer une application, si elle exite, capable d'afficher le contenu d�crit par le fichier
	 * @param file Le fichier contenant les donn�es � afficher
	 * @param choose Permet de choisir si une popup doit s'afficher pour choisir quelle application utilis�e, dans le cas
	 * ou plusieurs application pourraient ouvrir un m�me type de fichier
	 * @return
	 */
	public static Intent getDefaultApplicationFromFileName(File file, boolean choose){
		if(!choose) return getDefaultApplicationFromFileName(file);
		
		Intent i = new Intent(Intent.ACTION_VIEW);
		
		String mime = getExtensionFromFile(file.toString());
		
		i.setDataAndType(Uri.fromFile(file), mime);
		
		Intent chooser = Intent.createChooser(i, "");
		return chooser;
	}
	
	/**
	 * Permet de r�cup�rer une application, si elle exite, capable d'afficher le contenu d�crit par le fichier
	 * @param file Le fichier contenant les donn�es � afficher
	 * @return
	 */
	public static Intent getDefaultApplicationFromFileName(File file){
		Intent i = new Intent(Intent.ACTION_VIEW);
		
		String mime = getExtensionFromFile(file.toString());
		
		i.setDataAndType(Uri.fromFile(file), mime);
		return i;
	}
	
	/**
	 * Permet de r�cup�rer l'extension d'un fichier
	 * @param filename Le nom du fichier � ouvrir
	 * @return L'extension du fichier
	 */
	public static String getExtensionFromFile(String filename){
		String mime = null;
		String ext = MimeTypeMap.getFileExtensionFromUrl(filename.replaceAll("\\s", "").replaceAll(",",""));
		
		mime = ext == null ? "text/*" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
		
		return mime;
	}
}
