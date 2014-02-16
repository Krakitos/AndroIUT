/*
 * UIUtil.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.iutdijon.androiut2.util;

import java.io.IOException;

import android.content.Context;
import android.widget.Toast;

import com.iutdijon.androiut2.R;

/**
 * Classe permettant d'afficher des popup sur une IHM
 * @author Morgan Funtowicz
 *
 */
public final class UIUtil {
	
	private UIUtil() {
	}
	
	/**
	 * Affiche un Toast sur l'IHM correspondant à un probleme de réseau
	 * @param context Le contexte de l'application
	 * @param exception L'exception réseau
	 */
	public static void showMessage(Context context, Throwable exception) {
		if (exception instanceof IOException) {
			showMessage(context, R.string.network_acces_failure);
		} else {
			showMessage(context, exception.getMessage());
		}
	}

	/**
	 * Affiche un Toast sur l'IHM correspondant à un probleme de réseau
	 * @param context Le contexte de l'application
	 * @param message Le message à afficher
	 */
	public static void showMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Affiche un Toast sur l'IHM correspondant à un probleme de réseau
	 * @param context Le contexte de l'application
	 * @param resId L'identifiant de la ressource à afficher
	 */
	public static void showMessage(Context context, int resId) {
		showMessage(context, context.getResources().getString(resId));
	}
	
	/**
	 * Renvoie l'identifiant d'un message en fonction de l'erreur
	 * @param exception
	 * @return
	 */
	public static int getMessageId(Throwable exception) {
		exception.printStackTrace();
		if (exception instanceof IOException) {
			return R.string.network_acces_failure;
		} else {
			return R.string.unknow_error;
		}
	}
}
