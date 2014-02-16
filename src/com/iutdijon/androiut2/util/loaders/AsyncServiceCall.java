/*
 * AsyncServiceCall.java
 * 23/09/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.iutdijon.androiut2.util.loaders;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.iutdijon.androiut2.util.IOUtils;
import com.iutdijon.androiut2.util.UIUtil;

/**
 * Classe permettant de d�charger tout traitement dans un processus s�parer
 * Elle permet aussi de g�rer tout l'affichage de dialogue de progression
 * et la possibilit� de recommencer une op�ration en cas de probl�me
 * @author Morgan Funtowicz
 *
 * @param <P> Le type des param�tres en entr�e
 * @param <G> Le type des param�tres de progression
 * @param <R> Le type des param�tres de sortie
 */
public abstract class AsyncServiceCall<P, G, R> extends AsyncTask<P, G, Throwable> {
	
	private Activity context;
	private int progressStringId;
	private int successStringId;
	private int failureStringId;
	private ProgressDialog progressDialog;
	private R resultRun;
	private boolean retry;
	private boolean retryEnabled;
	
	/**
	 * @param ctx
	 */
	public AsyncServiceCall(Activity ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("Context must not be null.");
		}

		setContext(ctx);
		progressStringId = -1;
		successStringId = -1;
		failureStringId = -1;
		retryEnabled = true;
	}
	
	/**
	 * Retourne l'identifiant de la chaine utilis�e dans la progress bar
	 * @return L'identifiant de la chaine utilis�e dans la progress bar
	 */
	public int getProgressStringId() {
		return progressStringId;
	}

	/**
	 * D�finit la chaine � utilis�e comme texte affich� dans la progress bar
	 * @param strId Identifiant de la ressource
	 */
	public void setProgressStringId(int strId) {
		this.progressStringId = strId;
	}

	/**
	 * Retourne l'identifiant de la chaine utilis�e dans la popup en cas de succ�s
	 * @return L'identifiant de la chaine utilis�e dans la popup en cas de succ�s
	 */
	public int getSuccessStringId() {
		return successStringId;
	}

	/**
	 * D�finit la chaine � utilis�e comme texte dans la popup en cas de succ�s
	 * @param strId Identifiant de la ressource
	 */
	public void setSuccessStringId(int strId) {
		this.successStringId = strId;
	}

	/**
	 * Retourne l'identifiant de la chaine utilis�e dans la popup en cas d'�chec
	 * @return L'identifiant de la chaine utilis�e dans la popup en cas d'�chec
	 */
	public int getFailureStringId() {
		return failureStringId;
	}

	/**
	 * D�finit la chaine � utiliser comme texte affich� dans la progress bar
	 * @param strId Identifiant de la ressource
	 */
	public void setFailureStringId(int strId) {
		this.failureStringId = strId;
	}
	
	/**
	 * D�fini si l'application doit r�essayer en cas d'�chec 
	 * @param enabled True si elle doit recommencer, false sinon
	 */
	public void setRetry(boolean enabled) {
		retryEnabled = enabled;
	}
	
	/**
	 * Renvoie le contexte utilis� lors de la cr�ation du processus
	 * @return
	 */
	protected Activity getContext() {
		return context;
	}

	/**
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected final void onPreExecute() {
		if (getProgressStringId() != -1) {
			progressDialog = ProgressDialog.show(getContext(),"",getContext().getString(getProgressStringId()),false);
		}
		//
		onPreRun();
	}

	/**
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected final Throwable doInBackground(P... params) {
		Throwable error = null;
		//
		do {
			try {
				if (!IOUtils.isOnline(getContext())) {
					return new IOException();
				}
				//
				resultRun = run(params);
				//
				return null;
			} catch (Throwable e) {
				if (retryEnabled) {
					retry(e);
				}
				error = e;
			}
		} while (retry);
		//
		return error;
	}
	/**
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected final void onPostExecute(Throwable result) {
		
		try{
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//
		if (result != null) {
			if (!retryEnabled) {
				if (getFailureStringId() != -1) {
					UIUtil.showMessage(getContext(), getFailureStringId());
				} else {
					UIUtil.showMessage(getContext(), result);
				}
			}
			//
			onFailedRun(result);
		} else {
			if (getSuccessStringId() != -1) {
				UIUtil.showMessage(getContext(), getSuccessStringId());
			}
			
			onPostRun(resultRun);
		}
	};

	protected void onPreRun() {}
	protected abstract R run(P... params) throws IOException;
	protected void onPostRun(R result) {}

	protected void onFailedRun(Throwable result) {}
	
	/**
	 * Relance le processus
	 * @param exception L'exception ayant conduit � l'echec de l'essai pr�c�dent
	 */
	private void retry(Throwable exception) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		//
		builder.setTitle(getContext().getString(com.iutdijon.androiut2.R.string.app_name));
		builder.setMessage(getRetryMessage(exception));
		builder.setCancelable(false);
		builder.setPositiveButton(
			getContext().getString(com.iutdijon.androiut2.R.string.yes),
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				retry = true;
				//
				synchronized (AsyncServiceCall.this) {
					AsyncServiceCall.this.notify();
				}
			}
		});
		builder.setNegativeButton(
			getContext().getString(com.iutdijon.androiut2.R.string.no),
			new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				retry = false;
				//
				synchronized (AsyncServiceCall.this) {
					AsyncServiceCall.this.notify();
				}
			}
		});
		//
		getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				builder.create().show();
			}
		});
		//
		synchronized (AsyncServiceCall.this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Renvoie une popup pour demander si l'on doit recommencer ou non le traitement en cas d'�chec
	 * @param exception L'exception ayant entrain�e l'�chec
	 * @return
	 */
	private String getRetryMessage(Throwable exception) {
		String retryMsg = getContext().getString(com.iutdijon.androiut2.R.string.try_again);
		//
		if (getFailureStringId() != -1) {
			retryMsg =
				getContext().getString(getFailureStringId()) + " " + retryMsg;
		} else {
			retryMsg =
				getContext().getString(
					UIUtil.getMessageId(exception)) + " " +retryMsg;
		}
		//
		return retryMsg;
	}

	/**
	 * D�fini le contexte d'application du processus
	 * @param context the context to set
	 */
	public void setContext(Activity context) {
		this.context = context;
	}
}
