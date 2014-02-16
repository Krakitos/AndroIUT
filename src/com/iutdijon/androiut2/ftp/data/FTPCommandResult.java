package com.iutdijon.androiut2.ftp.data;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Classe contenant les informations sur la derni�re commande ex�cut�e sur le serveur
 * @author Morgan Funtowicz
 *
 */
public class FTPCommandResult {
	private final int commandID;
	private final boolean requestError;
	private File stored_file = null;
	private FTPFile[] directory_list = null;
	
	/**
	 * Classe wrapper r�sultant de l'ex�cution d'une commande sur le FTP
	 * @param command L'ID de la command {@link FTPCommand}
	 * @param error Boolean indiquant si la requ�te � lever une erreur
	 */
	public FTPCommandResult(int command, boolean error) {
		commandID = command;
		requestError = error;
	}
	/**
	 * Classe wrapper r�sultant de l'ex�cution d'une commande sur le FTP
	 * @param command L'ID de la command {@link FTPCommand}
	 * @param error Boolean indiquant si la requ�te � lever une erreur
	 * @param list La liste des fichiers dans le dossier actuel
	 */
	public FTPCommandResult(int command, boolean error, FTPFile[] list){
		commandID = command;
		requestError = error;
		directory_list = list;
	}
	/**
	 * Classe wrapper r�sultant de l'ex�cution d'une commande sur le FTP
	 * @param command L'ID de la command {@link FTPCommand}
	 * @param error Boolean indiquant si la requ�te � lever une erreur
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
	 * Renvoie l'identifiant {@link FTPCommand} de la commande ex�cut�e
	 * @return L'identifiant {@link FTPCommand} de la commande ex�cut�e
	 */
	public int getCommandID(){
		return commandID;
	}
	/**
	 * Indique si une erreur � lieu pendant l'ex�cution de la requ�te
	 * @return Renvoie true si une erreur � eu lieu, false sinon
	 */
	public boolean getErrorDuringRequest(){
		return requestError;
	}
	/**
	 * Renvoie la liste actualis�e des fichiers dans le r�pertoire courant
	 * @return Renvoie un tableau de {@link http://commons.apache.org/net/api-3.2/org/apache/commons/net/ftp/FTPFile.html} contenant les fichiers du r�pertoire courant
	 */
	public FTPFile[] getDirectoryFiles(){
		return directory_list;
	}
	/**
	 * Renvoie l'URI vers le fichier t�l�charg� sur le mobile
	 * @return Une Uri pouvant �tre exploiter par les application d'Android
	 */
	public File getStoredFile(){
		return stored_file;
	}
}
