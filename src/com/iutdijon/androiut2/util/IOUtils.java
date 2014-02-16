package com.iutdijon.androiut2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Classe stockant les fonctions outils, permettant de v�rifier certains param�tres li�s au r�seau
 * @author Morgan Funtowicz
 *
 */
public class IOUtils {
	/**
	 * V�rifie que l'appareil � une connexion disponible dans le contexte actuel
	 * @param context Le contexte actuel de l'application
	 * @return Un boolean, true si une connexion est disponible, false sinon
	 */
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	/**
	 * Permet de v�rifier si la connexion wifi utilis�e est celle de l'universit� ou non
	 * @param c Le contexte actuel de l'application
	 * @return Un boolean, true si la connexion est celle de l'universit�, false sinon
	 */
	public static boolean isUniversityWifi(Context c){
		WifiManager wifiMgr = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		
		if(wifiMgr.getConnectionInfo().getSSID() == null){
			return false;
		}
		
		return wifiMgr.getConnectionInfo().getSSID().contains("universite");

		
	}
}
