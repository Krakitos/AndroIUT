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
 * Permet de cr�er une requ�te HTTP Asynchrone en utilisant GET pour passer les param�tres
 * @author Morgan Funtowicz
 *
 * @param <P> Le type des param�tres en entr�e
 * @param <G> Le type des param�tres de progression
 * @param <R> Le type des param�tres de sortie
 */
public abstract class GetAsyncServiceCall<P, G, R> extends AsyncServiceCall<P, G, R> {

	public GetAsyncServiceCall(Activity context) {
		super(context);
		//
		setProgressStringId(com.iutdijon.androiut2.R.string.refreshing_wait);
	}
}
