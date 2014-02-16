package com.iutdijon.androiut2.util.loaders;

import java.util.HashMap;

import android.content.Context;

import com.iutdijon.androiut2.ade.activities.OnImageDownloadedCallback;
import com.iutdijon.androiut2.util.view.ImageZoomView;

/**
 * Classe wrapper utilisée pour créer une requête de téléchargement d'image
 * @author Morgan Funtowicz
 *
 */
public class ImageDownloader {

	/**
	 * Démarre le téléchargement d'une image 
	 * @param c Le contexte d'application
	 * @param params Les paramètres à ajouter à la requête
	 * @param mZoomView Le conteneur de l'image
	 */
    public void download(Context c, HashMap<String, String> params, ImageZoomView mZoomView) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(c, mZoomView);
            task.execute(params);
    }
    
    /**
	 * Démarre le téléchargement d'une image 
	 * @param c Le contexte d'application
	 * @param params Les paramètres à ajouter à la requête
	 * @param mZoomView Le conteneur de l'image
     * @param callback Défini un callback qui sera appellé lorsque l'image sera téléchargée
     */
    public void download(Context c, HashMap<String, String> params, ImageZoomView mZoomView, OnImageDownloadedCallback callback) {
        BitmapDownloaderTask task = new BitmapDownloaderTask(c, mZoomView);
        task.setOnImageDownloadedCallback(callback);
        task.execute(params);
    }
}
