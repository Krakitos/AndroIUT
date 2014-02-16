package com.iutdijon.androiut2.ftp.data;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Classe contenant les informations sur la dernière commande exécutée sur le serveur
 * @author Morgan Funtowicz
 *
 */
public class FTPCommandResult {
	private final int commandID;
	private final boolean requestError;
	private File stored_file = null;
	private FTPFile[] directory_list = null;
	
	/**
	 * Classe wrapper résultant de l'exécution d'une commande sur le FTP
	 * @param command L'ID de la command {@link FTPCommand}
	 * @param error Boolean indiquant si la requête à lever une erreur
	 */
	public FTPCommandResult(int command, boolean error) {
		commandID = command;
		requestError = error;
	}
	/**
	 * Classe wrapper résultant de l'exécution d'une commande sur le FTP
	 * @param command L'ID de la command {@link FTPCommand}
	 * @param error Boolean indiquant si la requête à lever une erreur
	 * @param list La liste des fichiers dans le dossier actuel
	 */
	public FTPCommandResult(int command, boolean error, FTPFile[] list){
		commandID = command;
		requestError = error;
		directory_list = list;
	}
	/**
	 * Classe wrapper résultant de l'exécution d'une commande sur le FTP
	 * @param command L'ID de la command {@link FTPCommand}
	 * @param error Boolean indiquant si la requête à lever une erreur
	 * @param list La liste des fichiers dans le dossier actuel
	 * @param stored Le chemin vers le fichier sauvegarder
	 */
	public FTPCommandResult(int command, boolean error, FTPFile[] list, File stored){
		commandID = command;
		requestError = error;
		directory_list = list;
		stored_file = stored;
	}
	
	/**
	 * Renvoie l'identifiant {@link FTPCommand} de la commande exécutée
	 * @return L'identifiant {@link FTPCommand} de la commande exécutée
	 */
	public int getCommandID(){
		return commandID;
	}
	/**
	 * Indique si une erreur à lieu pendant l'exécution de la requête
	 * @return Renvoie true si une erreur à eu lieu, false sinon
	 */
	public boolean getErrorDuringRequest(){
		return requestError;
	}
	/**
	 * Renvoie la liste actualisée des fichiers dans le répertoire courant
	 * @return Renvoie un tableau de {@link http://commons.apache.org/net/api-3.2/org/apache/commons/net/ftp/FTPFile.html} contenant les fichiers du répertoire courant
	 */
	public FTPFile[] getDirectoryFiles(){
		return directory_list;
	}
	/**
	 * Renvoie l'URI vers le fichier téléchargé sur le mobile
	 * @return Une Uri pouvant être exploiter par les application d'Android
	 */
	public File getStoredFile(){
		return stored_file;
	}
}
