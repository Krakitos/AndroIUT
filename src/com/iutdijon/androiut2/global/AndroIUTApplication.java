package com.iutdijon.androiut2.global;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.util.PreferencesManager;


/**
 * Classe permettant un accès global à certaines variables
 * @author Morgan Funtowicz
 *
 */
public class AndroIUTApplication {

	public static final String PREFS_NAME = "AndroIUT_prefs";
	public static final String APP_NAME = "AndroIUT";
	
	private static AndroIUTApplication instance;
	
	private static UserAccount userAccount;
	
	public AndroIUTApplication() {
		
	}
	public void setUser(UserAccount account){
		userAccount = account;
		PreferencesManager.getInstance().setString(PreferencesManager.REMEMBER_ME_OPTION, account.getLogin());
	}
	public UserAccount getAccount(){
		return userAccount;
	}
	
	synchronized public static AndroIUTApplication getInstance(){
		if(instance==null){
			instance = new AndroIUTApplication();
		}
		return instance;
	}

}
