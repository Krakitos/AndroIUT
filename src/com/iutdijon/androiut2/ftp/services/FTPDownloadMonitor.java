package com.iutdijon.androiut2.ftp.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.iutdijon.androiut2.util.observers.Observable;
import com.iutdijon.androiut2.util.observers.Observer;

/**
 * Classe permettant de surveiller l'avancement d'un téléchargement de fichier depuis le serveur
 * @author Morgan Funtowicz
 *
 */
public class FTPDownloadMonitor extends CountingOutputStream implements Observable{

	private FTPClient client;
	private int fileSize = 0;
	
	private ArrayList<Observer> observers;
	
	/**
	 * Construit un nouvel objet FTPDownloadMonitor
	 * @param out Le stream permettant de sauvegarder les données transférées
	 * @param client Le client supportant la connexion active
	 */
	public FTPDownloadMonitor(OutputStream out, FTPClient client) {
		super(out);
		this.client = client;
		observers = new ArrayList<Observer>();
	}
	
	/**
	 * Permet de démarrer le téléchargement du fichier
	 * @param path chemin vers le fichier à télécharger
	 * @throws IOException Levée si le fichier n'est pas présent dans le dossier
	 */
	public void startDownload(String path) throws IOException{
		client.sendCommand("SIZE", path);
		String size_reply = client.getReplyString();
		
		if(size_reply.indexOf(" ") == -1){		
			fileSize = Integer.parseInt(size_reply);
			
		}else{
			String[] splited = size_reply.split(" ");
			fileSize = Integer.parseInt(splited[1].replaceAll("\\D", ""));
		}
		client.setFileType(FTP.BINARY_FILE_TYPE);
		client.retrieveFile(path, this);
	}
	
	@Override
	protected synchronized void beforeWrite(int n) {
		super.beforeWrite(n);
		notify(100*getCount()/fileSize);
	}

	@Override
	public void addObserver(Observer o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		observers.remove(o);
	}

	@Override
	public void notify(int progress) {
		for (Iterator<Observer> iterator = observers.iterator(); iterator.hasNext();) {
			
			Observer type = iterator.next();
			type.update(this, progress);
		}
	}	
}
