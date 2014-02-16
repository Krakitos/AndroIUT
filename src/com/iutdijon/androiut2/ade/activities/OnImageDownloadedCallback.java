package com.iutdijon.androiut2.ade.activities;

/**
 * Interface utilis�e pour surveiller la progression d'un t�l�chargement d'image
 * @author Morgan Funtowicz
 *
 */
public interface OnImageDownloadedCallback {
	/*
	 * Appell� lorsque le t�l�chargement est fini
	 */
	void onDownloaded();
}
