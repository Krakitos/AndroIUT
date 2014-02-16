package com.iutdijon.androiut2.iut.data.account;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Set;

import javax.crypto.NoSuchPaddingException;

import org.apache.http.auth.InvalidCredentialsException;

import com.iutdijon.androiut2.util.security.SecureCenter;

/**
 * UserAccount est une classe abstraite stockant les informations de base sur un utilisateur
 * Les informations sont stock�es dans une map crypt�e avec l'algorithme AES sur une cl� 128 bits
 * @author Morgan Funtowicz
 *
 */
public class UserAccount {

	protected boolean isLogged = false;
	protected boolean isRestricted = false;
	protected SecureCenter storage = null;
	
	public static final String STUDENT_ACCOUNT = "student";
	public static final String TEACHER_ACCOUNT = "teacher";
	
	/**
	 * Initialise le compte utilisateur avec tout les champs renvoy� par le serveur
	 * les champs sont plac�s dans une map crypt�e cr��e lors de l'initialisation 
	 * du compte utilisateur avec une cl� al�atoire g�n�r�e dynamiquement, �ventuellement
	 * initialis�e avec un champ (sessionkey) renvoy� par le serveur. 
	 * @param fields Une map avec tout les champs du XML renvoy� par le l'UserAdapter
	 * @throws InvalidCredentialsException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidParameterSpecException
	 * @throws InvalidAlgorithmParameterException
	 */
	public UserAccount(HashMap<String,String> fields) throws InvalidCredentialsException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
		storage = fields.containsKey("sessionkey") ? new SecureCenter(fields.get("sessionkey")) : new SecureCenter();
		
		parse(fields);
	}
	
	/**
	 * Initialise un compte utilisateur avec les informations principales
	 * @param login Le login de l'utilisateur
	 * @param name Le nom de l'utilisateur
	 * @param forname Le pr�nom de l'utillisateur
	 */
	public UserAccount(String login, String name, String forname){
		storage.put("login", login);
		storage.put("nom", name);
		storage.put("prenom", forname);
	}
	
	/**
	 * Parse les champs renvoy�s par le serveur et initialise le compte utilisateur
	 * @param fields Param�tres utilisateur renvoy�s par le serveur
	 * @throws InvalidCredentialsException
	 */
	protected void parse(HashMap<String, String> fields) throws InvalidCredentialsException{
		Set<String> keys = fields.keySet();
		for (String key : keys) {
			storage.put(key, fields.get(key));
		}
		//this.isLogged = Boolean.parseBoolean(fields.get("connected"));
		if(fields.get("connected").equalsIgnoreCase("restricted")){
			isLogged = true;
			isRestricted = true;
		}else if(fields.get("connected").equalsIgnoreCase("true")){
			isLogged = true;
			isRestricted = false;
		}else{
			isLogged = false;
			isRestricted = false;
		}
	}
	/**
	 * D�fini le login utilisateur pour la session
	 */
	public void setLogin(String login){
		storage.put("login", login);
	}
	/**
	 * Renvoie le login de l'utilisateur
	 * @return Le login de l'utilisateur
	 */
	public String getLogin() {
		return storage.get("login");
	}
	
	/**
	 * Renvoie le nom de l'utilisateur
	 * @return Le nom de l'utilisateur
	 */
	public String getName(){
		return storage.get("nom");
	}
	
	/**
	 * Renvoie le pr�nom de l'utilisateur
	 * @return Le pr�nom de l'utilisateur
	 */
	public String getForname(){
		return storage.get("prenom");
	}
	
	/**
	 * Return le type du compte
	 * @return Renvoie une des chaines UserAccount.STUDENT_ACCOUNT ou UserAccount.TEACHER_ACCOUNT
	 */
	public String getType(){
		return storage.get("type");
	}
	
	/**
	 * Renvoie l'email de l'utilisateur
	 * @return L'email de l'utilisateur
	 */
	public String getMail(){
		return storage.get("mail");
	}
	
	/**
	 * Renvoie le statut de l'utilisateur sur le serveur
	 * @return Le statut de l'utilisateur sur le serveur
	 */
	public boolean isLogged() {
		return isLogged;
	}
	
	/**
	 * Renvoie les privil�ges de l'utilisateur, de le case de connexion d'utilisateur n'appartenant pas � la base de donn�es
	 * de l'IUT Info, mais authentifier par le LDAP de l'IUT, alors la connexion est autoris�e, mais l'acc�s � certains
	 * service n'est pas possible (Notes/Abs par exemple).
	 * @return True si l'�tudiant est identifi� mais pas pr�sent de la base de l'IUT info, false si pr�sent
	 */
	public boolean isRestricted(){
		return isRestricted;
	}
	/**
	 * D�fini le mot de passe de l'utilisateur
	 * @param password Mot de passe utilisateur
	 */
	public void setPassword(String password) {
		storage.put("password", password);
	}
	
	/**
	 * Renvoie le mot de passe de l'utilisateur
	 * @return Le mot de passe de l'utilisateur
	 */
	public String getPassword(){
		return storage.get("password");
	}
	
	public String getNum(){
		return storage.get("num");
	}
	@Override
	public String toString() {
		return getType()+" Restricted mode : "+isRestricted+" "+getLogin()+" "+getName()+" "+getForname();
	}
}
