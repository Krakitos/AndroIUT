package com.iutdijon.androiut2.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe permettant de stocker des informations préférentielles liés à l'utilisateur de manière persistante en mémoire
 * @author Morgan Funtowicz
 *
 */
public class PreferencesManager {
	private static PreferencesManager instance = null;
	private static SharedPreferences preferences = null;
	
	private static final String PREFS_NAME = "AndroIUT_prefs";
	
	public static final String REMEMBER_ME_OPTION = "remember_me";
	public static final String REMEMBER_ME_PASSWORD = "password";
	public static final String REMEMBER_ME_CHECKBOX_VAL = "rememberMeOpt";
	
	public PreferencesManager() {
		
	}
	/**
	 * Initilialise et récupère les éventuelles préférences sauvegardées
	 * @param c
	 */
	public void init(Context c){
		preferences = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	/**
	 * Vérifie que le gestionnaire de préférences est bien initiliser
	 */
	public void checkPrefs(){
		if(preferences == null){
			throw new NullPointerException("Preferences Manager must be initialized by calling init() method");
		}
	}
	
	/**
	 * Récupère la valeur d'une préférences si elle existe
	 * @param entry L'identifiant de la préférance
	 * @return Un boolean, si l'identifiant n'est pas trouvé, la fonction renvoie false
	 */
	public boolean getBoolean(String entry){
		checkPrefs();
		return preferences.getBoolean(entry, false);
	}
	/**
	 * Enregistre une préférence en mémoire
	 * @param entry L'identifiant de la préférences
	 * @param value La valeur a associée
	 */
	public void setBoolean(String entry, boolean value){
		checkPrefs();
		preferences.edit().putBoolean(entry, value).commit();
	}
	
	/**
	 * Récupère la valeur d'une préférences si elle existe
	 * @param entry L'identifiant de la préférénces 
	 * @return Une chaine de caractères, si l'identifiant n'est pas trouvé, la fonction renvoie "" 
	 */
	public String getString(String entry){
		checkPrefs();
		return preferences.getString(entry, "");
	}
	/**
	 * Enregistre une préférence en mémoire
	 * @param entry L'identifiant de la préférences
	 * @param value La valeur a associée
	 */
	public void setString(String entry, String value){
		checkPrefs();
		preferences.edit().putString(entry, value).commit();
	}
	/**
	 * Récupère la valeur d'une préférences si elle existe
	 * @param entry L'identifiant de la préférénces 
	 * @return Une entier, si l'identifiant n'est pas trouvé, la fonction renvoie -1
	 */
	public int getInt(String entry){
		checkPrefs();
		return preferences.getInt(entry, -1);
	}
	/**
	 * Enregistre une préférence en mémoire
	 * @param entry L'identifiant de la préférences
	 * @param value La valeur a associée
	 */
	public void setInt(String entry, int value){
		checkPrefs();
		preferences.edit().putInt(entry, value).commit();
	}
	
	/**
	 * Singleton permettant d'accéder à l'instance
	 * @return L'instance unique en mémoire
	 */
	public static PreferencesManager getInstance(){
		if(instance == null){
			instance = new PreferencesManager();
		}
		return instance;
	}

}
