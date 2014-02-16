package com.iutdijon.androiut2.iut.data.account;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import org.apache.http.auth.InvalidCredentialsException;

/**
 * Classe stockant les informations sur un �tudiant
 * {@link com.iutdijon.androiut2.iut.data.account.UserAccount}
 * @author Morgan Funtowicz
 *
 */
public class StudentAccount extends UserAccount {
	
	protected String promotion;
	protected String group;
	protected String half_group;
	protected String studentNum;
	
	/**
	 * Cr�er un compte utilisateur pour �tudiant
	 * @param fields Les informations renvoy�es par le serveur
	 * @throws InvalidCredentialsException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidParameterSpecException
	 * @throws InvalidAlgorithmParameterException
	 */
	public StudentAccount(HashMap<String, String> fields) throws InvalidCredentialsException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
		super(fields);
	}
	
	/**
	 * Cr�er un compte utilisateur pour un �tudiant avec les informations essentielles
	 * @param login Le login utilisateur
	 * @param group Le groupe de l'�tudiant
	 * @param half_group Le demi-groupe de l'�tudiant
	 * @param studentNum Le num�ro �tudiant de l'utilisateur
	 * @param name Le nom de l'�tudiant 
	 * @param forname Le pr�nom de l'�tudiant
	 * @param promotion La promotion de l'�tudiant
	 */
	public StudentAccount(String login, String group, String half_group,String studentNum, String name, String forname, String promotion) {
		super(login, name, forname);
	}
	
	@Override
	protected void parse(HashMap<String, String> fields) throws InvalidCredentialsException{
		super.parse(fields);
		this.group = fields.get("idgroupe");
		this.half_group = fields.get("demigroupe");
		this.studentNum = fields.get("num").replaceAll("\\s", "");
		this.promotion = fields.get("iddepartement");
	}
	
	/**
	 * Renvoie la promotion de l'�tudiant
	 * @return La promotion de l'�tudiant
	 */
	public String getPromotion(){
		return promotion;
	}
	
	/**
	 * Renvoie le groupe de l'�tudiant
	 * @return Le groupe de l'�tudiant
	 */
	public String getGroup(){
		return group;
	}
	
	/**
	 * Renvoie le demi-groupe de l'�tudiant
	 * @return Le demi-groupe de l'�tudiant
	 */
	public String getHalfGroup(){
		return half_group;
	}
	
	/**
	 * Renvoie le num�ro �tudiant 
	 * @return Le num�ro �tudiant
	 */
	public String getStudentNum(){
		return studentNum;
	}
	@Override
	public String toString() {
		String val = super.toString() + " " + studentNum + " " + promotion + " " + group + " "+ half_group;
		return val;
	}

}
