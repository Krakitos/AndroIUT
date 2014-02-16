package com.iutdijon.androiut2.mail.services;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

/**
 * Classe 
 * @author Morgan Funtowicz
 *
 */
public class MailReader {
	
	public static enum Protocol { IMAP("imap"), POP("pop"), IMAPS("imaps"), POPS("pops"); 
		private final String protocol; 
		Protocol(String p){ 
			protocol = p;
		}
		@Override
		public String toString(){
			return protocol;
		}
	};
	
	private Protocol protocol= null;
	private String host = null;
	private boolean secured = false;
	
	private Properties props;
	private Session session;
	private Store store;
	private static Folder inbox;
	
	/**
	 * Créer un MailReader en spécifiant s'il doit utiliser une surcouche SSL
	 * @param pProtocol Le protocol de récupération à utiliser <b>MailReader.Protocol</b>
	 * @param pHost L'adresse du serveur où récupérer les mails
	 */
	public MailReader(Protocol pProtocol, String pHost){
		protocol = pProtocol;
		host = pHost;
		if(protocol == Protocol.IMAPS || protocol == Protocol.POPS) secured = true;
		init();
	}
	
	public void connect(String username, String password) throws MessagingException{
		if(store!=null){
			store.connect(host, username, password);
		}
	}
	public void disconnect(){
		try {
			store.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}finally{
			store = null;
		}
	}
	/**
	 * Récupère tout les messages du dossier
	 * @param limit Nombre maximal de mail à charger (limite l'impacte en mémoire)
	 * @return Un tableau avec les messages du dossier;
	 * @throws MessagingException
	 */
	public Message[] getMessages(int limit) throws MessagingException{
		
		if(inbox == null){
			inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);
		}
		
		int nbMessage = inbox.getMessageCount();
		if(limit >= inbox.getMessageCount()) limit = nbMessage-1;
		else if(nbMessage == 0) limit = 0;
		
		Message[] messages = inbox.getMessages(inbox.getMessageCount()-limit, inbox.getMessageCount());

		inbox.fetch(messages, fetchProfile());
		return messages;
	}
	/**
	 * Récupère tout les messages non lus.
	 * @return Un tableau de messages non lus.
	 * @throws MessagingException
	 */
	public Message[] getUnreadedMessages() throws MessagingException{
		inbox = store.getFolder("inbox");
		inbox.open(Folder.READ_WRITE);
		
		Message[] messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
		
		inbox.fetch(messages, fetchProfile());
		return messages;
	} 

	/**
	 * Initialise le lecteur
	 */
	private void init()  {
		initReaderProtocol();
		initSession();
		try {
			initStore();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
	
	private void initStore() throws NoSuchProviderException{
		store = session.getStore(protocol.toString());
	}
	
	private void initSession(){
		if(props!= null){
			session = Session.getDefaultInstance(props);
		}
	}
	private void initReaderProtocol(){
		props = System.getProperties();
		props.setProperty("mail.store.protocol", protocol.toString());		
	}
	
	private FetchProfile fetchProfile(){
		FetchProfile fp = new FetchProfile();
		fp.add(FetchProfile.Item.CONTENT_INFO);
		fp.add(FetchProfile.Item.ENVELOPE);
		fp.add(FetchProfile.Item.FLAGS);
		
		
		return fp;
	}
	
	/**
	 * 
	 * @return Renvoie le protocol pour le lecteur
	 */
	public Protocol getProtocol(){
		return protocol;
	}
	
	/**
	 * 
	 * @return Renvoie l'adresse du serveur courant
	 */
	public String getHost(){
		return host;
	}
	
	/**
	 * 
	 * @return Renvoie un true si la connexion est sécurisée, false sinon
	 */
	public boolean isLayered(){
		return secured;
	}
}
