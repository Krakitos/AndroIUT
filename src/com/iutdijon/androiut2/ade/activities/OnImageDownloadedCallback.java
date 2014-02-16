package com.iutdijon.androiut2.ade.activities;

/**
 * Interface utilisée pour surveiller la progression d'un téléchargement d'image
 * @author Morgan Funtowicz
 *
 */
public interface OnImageDownloadedCallback {
	/*
	 * Appellé lorsque le téléchargement est fini
	 */
	void onDownloaded();
}
