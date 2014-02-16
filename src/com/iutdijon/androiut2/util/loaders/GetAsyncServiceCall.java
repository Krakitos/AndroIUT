/*
 * GetAsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.iutdijon.androiut2.util.loaders;

import android.app.Activity;

/**
 * Permet de créer une requête HTTP Asynchrone en utilisant GET pour passer les paramètres
 * @author Morgan Funtowicz
 *
 * @param <P> Le type des paramètres en entrée
 * @param <G> Le type des paramètres de progression
 * @param <R> Le type des paramètres de sortie
 */
public abstract class GetAsyncServiceCall<P, G, R> extends AsyncServiceCall<P, G, R> {

	public GetAsyncServiceCall(Activity context) {
		super(context);
		//
		setProgressStringId(com.iutdijon.androiut2.R.string.refreshing_wait);
	}
}
