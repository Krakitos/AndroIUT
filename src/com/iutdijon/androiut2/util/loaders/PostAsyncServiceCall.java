package com.iutdijon.androiut2.util.loaders;

import android.app.Activity;

/**
 * Permet de cr�er une requ�te dans un processus s�par� en ajoutant les param�tres via la m�thode POST
 * @author Morgan Funtowicz
 *
 * @param <P> Le type des param�tres en entr�e
 * @param <G> Le type des param�tres de progression
 * @param <R> Le type des param�tres de sortie
 */
public abstract class PostAsyncServiceCall<P, G, R> extends AsyncServiceCall<P, G, R> {
	
	public PostAsyncServiceCall(Activity context) {
		super(context);
		setProgressStringId(com.iutdijon.androiut2.R.string.processing_wait);
	}
}
