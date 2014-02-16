package com.iutdijon.androiut2.iut.data;

import java.util.HashMap;

import com.iutdijon.androiut2.iut.data.account.StudentAccount;
import com.iutdijon.androiut2.iut.data.account.TeacherAccount;
import com.iutdijon.androiut2.iut.data.account.UserAccount;

/**
 * Factory cr�ant tout les {@link com.iutdijon.androiut2.iut.data.account.UserAccount}
 * @author Morgan Funtowicz
 *
 */
public class UserFactory {
	public UserFactory() {
		
	}
	/**
	 * Cr�er un {@link com.iutdijon.androiut2.iut.data.account.UserAccount} en fonction du type de compte
	 * @param type Le type du compte � cr�er
	 * @param params Les param�tres utilisateurs trouv� par {@link com.iutdijon.androiut2.util.adapters.UserAdapter}
	 * @return Renvoie un {@link com.iutdijon.androiut2.iut.data.account.UserAccount} 
	 */
	public static UserAccount getAccount(String type, HashMap<String, String> params){
		UserAccount account = null;
		try{
			if("student".equalsIgnoreCase(type) || "etudiant".equalsIgnoreCase(type)){
				account = new StudentAccount(params);
			}else if("teacher".equalsIgnoreCase(type)){
				account = new TeacherAccount(params);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return account;
	}
}
