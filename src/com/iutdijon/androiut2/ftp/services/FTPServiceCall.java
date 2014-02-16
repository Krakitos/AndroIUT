package com.iutdijon.androiut2.ftp.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;

import com.iutdijon.androiut2.ftp.data.FTPCommand;
import com.iutdijon.androiut2.ftp.data.FTPCommandResult;
import com.iutdijon.androiut2.ftp.data.FTPCommandWrapper;
import com.iutdijon.androiut2.util.observers.Observable;
import com.iutdijon.androiut2.util.observers.Observer;

/*
 * Classe permettant de g�rer le c�t� client du FTP.
 * Elle utilise un thread s�par� afin de d�charger tout les traitements r�seaux de l'interface utilisateur
 * comme impos� par le syst�me d'exploitation dans le but de fluidifier l'interface.
 * Elle utilise une file de commande pour stock�e les commandes � ex�cut�es sur le serveur.
 * Le Handler utilis� permet l'IPC pour recevoir / envoyer des commandes / r�sultats entre l'IHM et l'actuel service
 */
public class FTPServiceCall extends Thread implements Observer{		
	
	public static final String UNIVERSITY_FTP_SERVER_URL = AndroIUTConstantes.FTP_URL;
	public static final int UNIVERSITY_FTP_SERVER_PORT = 990;
	
	public static final int FTP_COMMAND_START = 1;
	public static final int FTP_COMMAND_END = 2;
	
	private Activity context = null;
	private FTPSClient client = null;
		
	private AtomicBoolean loop;
	private ConcurrentLinkedQueue<FTPCommandWrapper> stack;
	private ProgressDownload downloadProgressBar;
	
	private final Handler mHandler;
	
	private final String host;
	private final int port;
	
	/**
	 * Cr�er une connexion FTPS vers un serveur de la FAC.
	 * Le constructeur n'initialise pas la connexion au serveur, il ne fait que cr�er le client FTP.
	 * @param handler Un Handler pour communiquer avec l'IHM
	 * @param host L'adresse du servuer
	 * @param port Le port de connexion au serveur
	 */
	public FTPServiceCall(Activity c, Handler handler, String host, int port) {
		super();
		mHandler = handler;
		context = c;
		
		this.host = host;
		this.port = port;
		
		loop = new AtomicBoolean(true);
		stack = new ConcurrentLinkedQueue<FTPCommandWrapper>();
		
		client = new FTPSClient(true);
		//client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		initClient();
		
	}
	
	/**
	 * Envoie une commande au Thread afin qu'elle soit ex�cut�e le plus t�t possible
	 * @param command La commande � trait�e
	 */
	public void sendCommand(FTPCommandWrapper command){
		stack.add(command);
	}
	/**
	 * Dispatche la commande vers la bonne fonction
	 * @param command Identifiant {@link FTPCommand} de la commande � ex�cuter
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private void execCommand(FTPCommandWrapper command){		
		mHandler.obtainMessage(FTP_COMMAND_START, command).sendToTarget();
		
		final int id = command.getCommandID();
		FTPCommandResult result = null;
		
		switch(id){
			case FTPCommand.FTP_CD :{
				result = changeDirectory(command.getPath());
				break;
			}
			case FTPCommand.FTP_LIST : {
				result = list(command.getPath());
				break;
			}
			case FTPCommand.FTP_LOGIN : {
				String[] login_info = command.getPath().split(":");	
				if(login_info.length != 2){
					result =  new FTPCommandResult(id, true);
				}else{
					result = connect(login_info[0], login_info[1]);
				}
				break;
			}
			case FTPCommand.FTP_LOGOUT : {
				result = disconnect(); 
				break;
			}
			case FTPCommand.FTP_DELETE : {
				result = deleteFile(command.getPath());
				break;
			}
			case FTPCommand.FTP_PUT : {
				result = putFile(command.getPath(), command.getStream());
				break;
			}
			case FTPCommand.FTP_GET : {
				result = downloadFile(command.getPath());
				break;
			}default :{
				result = new FTPCommandResult(id, true);
			}
		}
		
		mHandler.obtainMessage(FTP_COMMAND_END, result).sendToTarget();
	}
	
	/**
	 * Envoie une requ�te de connexion au serveur
	 * @param username Le nom d'utilisateur
	 * @param password Le mot de passe utilisateur
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult connect(String username, String password){
		FTPFile[] list = null;
		try {
			client.connect(host, port);
			client.login(username, password);
			client.sendCommand("UTF8","ON");
			client.execPBSZ(0);
			client.execPROT("P");
			client.type(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			list = client.listFiles();
		} catch (Exception e){
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_LOGIN, true);
		}
		return new FTPCommandResult(FTPCommand.FTP_LOGIN, false, list);
	}
	
	/**
	 * Met fin � la session FTP avec le serveur
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult disconnect(){
		try {
			client.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_LOGOUT, true);
		}
		loop.set(false);
		return new FTPCommandResult(FTPCommand.FTP_LOGOUT, false);
	}
	
	/**
	 * Envoie une requ�te pour r�cup�rer la liste des fichiers dans le dossier path
	 * @param path Dossier dans lequel on souhaite lister les fichiers "/" par d�faut
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult list(String path){
		FTPFile[] list = null;
		
		if(!client.isConnected()){
			return new FTPCommandResult(FTPCommand.FTP_LIST, true);
		}
		try {
			list = path == null ? client.listFiles() : client.listFiles(path);
		} catch (IOException e) {
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_LIST, true);
		}
		return new FTPCommandResult(FTPCommand.FTP_LIST, false, list);
	}
	
	/**
	 * Envoie une requ�te pour change le r�pertoire courant et naviguer dans l'arborescence 
	 * @param path Dossier dans lequel on souhaite se placer
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult changeDirectory(String path){
		FTPFile[] list = null;
		
		if(!client.isConnected()){
			return new FTPCommandResult(FTPCommand.FTP_CD, true);
		}
		try {
			client.changeWorkingDirectory(path);
			list = client.listFiles();
		} catch (IOException e) {
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_CD, true);
		}
		return new FTPCommandResult(FTPCommand.FTP_CD, false, list);
	}
	
	/**
	 * Envoie une requ�te pour supprimer le fichier path
	 * @param path Nom du fichier devant �tre supprim�
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult deleteFile(String path){
		FTPFile[] list = null;
		
		if(!client.isConnected()){
			return new FTPCommandResult(FTPCommand.FTP_DELETE, true);
		}
		try {
			client.deleteFile(path);
			list = client.listFiles();
		} catch (IOException e) {
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_DELETE, true);
		}
		return new FTPCommandResult(FTPCommand.FTP_DELETE, false, list);
	}
	
	/**
	 * Demande au serveur de t�l�charger un fichier et de le placer dans la m�moire du p�riph�rique
	 * @param path Le nom du fichier � t�l�charger
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult downloadFile(String path){
		File file;
		
		if(!client.isConnected()){
			return new FTPCommandResult(FTPCommand.FTP_GET, true);
		}
		try {
			//On supprime la progressBar ind�termin�e ( tournante sur elle m�me )
			mHandler.obtainMessage(FTP_COMMAND_END).sendToTarget();
			
			file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), path);
			
			FileOutputStream fos = new FileOutputStream(file);
			
			//On lance la ProgressBar repr�sentative de l'avancement du DL
			downloadProgressBar = new ProgressDownload(context);
			context.runOnUiThread(downloadProgressBar);
			
			FTPDownloadMonitor monitor = new FTPDownloadMonitor(fos, client);
			monitor.addObserver(this);
			monitor.startDownload(path);
			monitor.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_GET, true);
		}
		return new FTPCommandResult(FTPCommand.FTP_GET, false, null, file);
	}
	
	/**
	 * Envoie une requ�te pour sauvegarder une fichier sur le FTP
	 * @param path Le nom du fichier � sauvegarder
	 * @param stream Les donn�es brutes du fichier
	 * @return {@link FTPCommandResult} contenant les informations sur l'ex�cution de la commande par le serveur
	 */
	private FTPCommandResult putFile(String path, InputStream stream){
		FTPFile[] list;
		if(!client.isConnected()){
			return new FTPCommandResult(FTPCommand.FTP_PUT, true);
		}
		try {
			client.storeFile(path, stream);
			list = client.listFiles();
		} catch (IOException e) {
			e.printStackTrace();
			return new FTPCommandResult(FTPCommand.FTP_PUT, true);
		}
		return new FTPCommandResult(FTPCommand.FTP_PUT, false, list);
	}
	
	/**
	 * Configure le client FTP pour concorder avec le serveur de la fac
	 */
	private void initClient() {
		FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		config.setServerLanguageCode("EN");
		
		client.configure(config);
        client.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
	}

	@Override
	public void run() {
		while(loop.get()){
			if(!stack.isEmpty()){
				FTPCommandWrapper command = stack.poll();				
				execCommand(command);
				command = null;
			}
			try {
				sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(Observable o, int progress) {
		if(downloadProgressBar != null){
			downloadProgressBar.updateProgress(progress);
		}
	}


}

