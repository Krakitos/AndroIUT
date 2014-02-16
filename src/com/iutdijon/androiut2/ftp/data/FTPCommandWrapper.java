package com.iutdijon.androiut2.ftp.data;

import java.io.InputStream;

/**
 * Classe contenant les informations d'une commande à envoyer sur le serveur
 * @author Morgan Funtowicz
 *
 */
public class FTPCommandWrapper {

	private final int id;
	private final String path;
	private InputStream stream = null;
	/**
	 * Classe wrapper représentant une commande sur le FTP
	 * @param id L'identifiant de la commande {@link com.iutdijon.androiut2.ftp.data.FTPCommand}
	 * @param path Le nom du fichier avec le chemin absolu ou relatif ( si dans le dossier courant)
	 */
	public FTPCommandWrapper(int id, String path) {
		this.id = id;
		this.path = path;
		this.stream = null;
	}
	
	/**
	 * Classe wrapper représentant une commande sur le FTP
	 * Ce constructeur est utile dans le cas de l'utilisation de la commande PUT pour envoyer un fichier sur le FTP
	 * @param id L'identifiant de la commande {@link com.iutdijon.androiut2.ftp.data.FTPCommand}
	 * @param path Le nom du fichier avec le chemin absolu ou relatif ( si dans le dossier courant)
	 * @param input Le stream contenant les données à mettre sur le serveur
	 */
	public FTPCommandWrapper(int id, String path, InputStream input){
		this.id = id;
		this.path = path;
		this.stream = input;
	}
	
	/**
	 * Renvoie l'identifiant de la commande FTP
	 * @return Identifiant de la commande FTP {@link com.iutdijon.androiut2.ftp.data.FTPCommand}
	 */
	public int getCommandID(){
		return id;
	}
	/**
	 * Renvoie le chemin vers le fichier cible
	 * @return  Chaine contenant le chemin (absolu ou relatif) vers le fichier cible
	 */
	public String getPath(){
		return path;
	}
	/**
	 * Renvoie le stream avec les données brute du fichier à déposer sur le serveur
	 * @return Données brutes du fichier à envoyer sur le serveur ou null si commande != PUT
	 */
	public InputStream getStream(){
		return stream;
	}
	
	@Override
	public String toString() {
		return "[FTPCommandWrapper sending command : "+id+" on path : "+path + " with stream " + (stream != null) +"]"; 
	}
}
