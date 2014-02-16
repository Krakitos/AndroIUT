package com.iutdijon.androiut2.util.loaders;

import android.app.Activity;

/**
 * Permet de créer une requête dans un processus séparé en ajoutant les paramètres via la méthode POST
 * @author Morgan Funtowicz
 *
 * @param <P> Le type des paramètres en entrée
 * @param <G> Le type des paramètres de progression
 * @param <R> Le type des paramètres de sortie
 */
public abstract class PostAsyncServiceCall<P, G, R> extends AsyncServiceCall<P, G, R> {
	
	public PostAsyncServiceCall(Activity context) {
		super(context);
		setProgressStringId(com.iutdijon.androiut2.R.string.processing_wait);
	}
}
