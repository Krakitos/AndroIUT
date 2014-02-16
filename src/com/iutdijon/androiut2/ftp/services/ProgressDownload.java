package com.iutdijon.androiut2.ftp.services;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

/**
 * ProgressBar asynchrone affichant la progression du téléchargement d'un  
 * @author Morgan Funtowicz
 *
 */
public  class ProgressDownload implements Runnable {
	
	private ProgressDialog progressBar;
	private Context context;
	
	public ProgressDownload(Context c) {
		this.context = c;
	}
	
	/**
	 * Met à jour la valeur de la progressbar
	 * @param progress
	 */
	public  void updateProgress(int progress){
		if(progressBar != null){
			if(progress == 99){
				progressBar.setProgress(100);
				progressBar.dismiss();
			}else{
				progressBar.setProgress(progress);
			}
		}
	}
	
	@Override
	public  void run(){
		progressBar = new ProgressDialog(context);
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setIndeterminate(false);
		progressBar.setMax(100);
		progressBar.setCanceledOnTouchOutside(false);
		progressBar.setCancelable(true);
		if(Build.VERSION.SDK_INT >= 11){
			updateNumberFormat();
		}
		progressBar.show();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void updateNumberFormat() {
		progressBar.setProgressNumberFormat(null);
	}
}
