package com.iutdijon.androiut2.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe permettant de stocker des informations pr�f�rentielles li�s � l'utilisateur de mani�re persistante en m�moire
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
	 * Initilialise et r�cup�re les �ventuelles pr�f�rences sauvegard�es
	 * @param c
	 */
	public void init(Context c){
		preferences = c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	/**
	 * V�rifie que le gestionnaire de pr�f�rences est bien initiliser
	 */
	public void checkPrefs(){
		if(preferences == null){
			throw new NullPointerException("Preferences Manager must be initialized by calling init() method");
		}
	}
	
	/**
	 * R�cup�re la valeur d'une pr�f�rences si elle existe
	 * @param entry L'identifiant de la pr�f�rance
	 * @return Un boolean, si l'identifiant n'est pas trouv�, la fonction renvoie false
	 */
	public boolean getBoolean(String entry){
		checkPrefs();
		return preferences.getBoolean(entry, false);
	}
	/**
	 * Enregistre une pr�f�rence en m�moire
	 * @param entry L'identifiant de la pr�f�rences
	 * @param value La valeur a associ�e
	 */
	public void setBoolean(String entry, boolean value){
		checkPrefs();
		preferences.edit().putBoolean(entry, value).commit();
	}
	
	/**
	 * R�cup�re la valeur d'une pr�f�rences si elle existe
	 * @param entry L'identifiant de la pr�f�r�nces 
	 * @return Une chaine de caract�res, si l'identifiant n'est pas trouv�, la fonction renvoie "" 
	 */
	public String getString(String entry){
		checkPrefs();
		return preferences.getString(entry, "");
	}
	/**
	 * Enregistre une pr�f�rence en m�moire
	 * @param entry L'identifiant de la pr�f�rences
	 * @param value La valeur a associ�e
	 */
	public void setString(String entry, String value){
		checkPrefs();
		preferences.edit().putString(entry, value).commit();
	}
	/**
	 * R�cup�re la valeur d'une pr�f�rences si elle existe
	 * @param entry L'identifiant de la pr�f�r�nces 
	 * @return Une entier, si l'identifiant n'est pas trouv�, la fonction renvoie -1
	 */
	public int getInt(String entry){
		checkPrefs();
		return preferences.getInt(entry, -1);
	}
	/**
	 * Enregistre une pr�f�rence en m�moire
	 * @param entry L'identifiant de la pr�f�rences
	 * @param value La valeur a associ�e
	 */
	public void setInt(String entry, int value){
		checkPrefs();
		preferences.edit().putInt(entry, value).commit();
	}
	
	/**
	 * Singleton permettant d'acc�der � l'instance
	 * @return L'instance unique en m�moire
	 */
	public static PreferencesManager getInstance(){
		if(instance == null){
			instance = new PreferencesManager();
		}
		return instance;
	}

}
