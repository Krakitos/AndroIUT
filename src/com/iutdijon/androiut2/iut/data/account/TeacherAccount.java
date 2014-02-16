package com.iutdijon.androiut2.iut.data.account;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import org.apache.http.auth.InvalidCredentialsException;

/**
 * Classe stockant les informations sur un professeur
 * {@link com.iutdijon.androiut2.iut.data.account.UserAccount}
 * @author Morgan Funtowicz
 *
 */
public class TeacherAccount extends UserAccount {
	
	private String ade_id;
	
	/**
	 * Créer un compte utilisateur pour un professeur
	 * @param fields Champs renvoyés par le serveur de connexion
	 * @throws InvalidCredentialsException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidParameterSpecException
	 * @throws InvalidAlgorithmParameterException
	 */
	public TeacherAccount(HashMap<String, String> fields) throws InvalidCredentialsException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
		super(fields);
	}

	/**
	 * Créer un compte utilisateur pour un professeur avec les informations essentielle
	 * @param login Le login utilisateur
	 * @param name Le nom de l'utilisateur
	 * @param forname Le prénom de l'utilisateur
	 */
	public TeacherAccount(String login, String name, String forname){
		super(login, name, forname);
	}
	
	@Override
	protected void parse(HashMap<String, String> fields) throws InvalidCredentialsException {
		super.parse(fields);
		ade_id = fields.get("num");
	}
	/**
	 * Retourne l'id ADE d'un professeur
	 * @return id du professeur sur ADE
	 */
	public String getId(){
		return ade_id;
	}
	@Override
	public String toString(){
		return super.toString() + " " +ade_id;
	}
}
