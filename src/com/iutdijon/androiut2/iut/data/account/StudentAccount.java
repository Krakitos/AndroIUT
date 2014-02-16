package com.iutdijon.androiut2.iut.data.account;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;

import javax.crypto.NoSuchPaddingException;

import org.apache.http.auth.InvalidCredentialsException;

/**
 * Classe stockant les informations sur un étudiant
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
	 * Créer un compte utilisateur pour étudiant
	 * @param fields Les informations renvoyées par le serveur
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
	 * Créer un compte utilisateur pour un étudiant avec les informations essentielles
	 * @param login Le login utilisateur
	 * @param group Le groupe de l'étudiant
	 * @param half_group Le demi-groupe de l'étudiant
	 * @param studentNum Le numéro étudiant de l'utilisateur
	 * @param name Le nom de l'étudiant 
	 * @param forname Le prénom de l'étudiant
	 * @param promotion La promotion de l'étudiant
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
	 * Renvoie la promotion de l'étudiant
	 * @return La promotion de l'étudiant
	 */
	public String getPromotion(){
		return promotion;
	}
	
	/**
	 * Renvoie le groupe de l'étudiant
	 * @return Le groupe de l'étudiant
	 */
	public String getGroup(){
		return group;
	}
	
	/**
	 * Renvoie le demi-groupe de l'étudiant
	 * @return Le demi-groupe de l'étudiant
	 */
	public String getHalfGroup(){
		return half_group;
	}
	
	/**
	 * Renvoie le numéro étudiant 
	 * @return Le numéro étudiant
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
