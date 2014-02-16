package com.iutdijon.androiut2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Classe stockant les fonctions outils, permettant de vérifier certains paramètres liés au réseau
 * @author Morgan Funtowicz
 *
 */
public class IOUtils {
	/**
	 * Vérifie que l'appareil à une connexion disponible dans le contexte actuel
	 * @param context Le contexte actuel de l'application
	 * @return Un boolean, true si une connexion est disponible, false sinon
	 */
	public static boolean isOnline(Context context) {
	    ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	/**
	 * Permet de vérifier si la connexion wifi utilisée est celle de l'université ou non
	 * @param c Le contexte actuel de l'application
	 * @return Un boolean, true si la connexion est celle de l'université, false sinon
	 */
	public static boolean isUniversityWifi(Context c){
		WifiManager wifiMgr = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		
		if(wifiMgr.getConnectionInfo().getSSID() == null){
			return false;
		}
		
		return wifiMgr.getConnectionInfo().getSSID().contains("universite");

		
	}
}
