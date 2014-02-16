package com.iutdijon.androiut2.util.loaders;

import java.util.HashMap;

import android.content.Context;

import com.iutdijon.androiut2.ade.activities.OnImageDownloadedCallback;
import com.iutdijon.androiut2.util.view.ImageZoomView;

/**
 * Classe wrapper utilis�e pour cr�er une requ�te de t�l�chargement d'image
 * @author Morgan Funtowicz
 *
 */
public class ImageDownloader {

	/**
	 * D�marre le t�l�chargement d'une image 
	 * @param c Le contexte d'application
	 * @param params Les param�tres � ajouter � la requ�te
	 * @param mZoomView Le conteneur de l'image
	 */
    public void download(Context c, HashMap<String, String> params, ImageZoomView mZoomView) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(c, mZoomView);
            task.execute(params);
    }
    
    /**
	 * D�marre le t�l�chargement d'une image 
	 * @param c Le contexte d'application
	 * @param params Les param�tres � ajouter � la requ�te
	 * @param mZoomView Le conteneur de l'image
     * @param callback D�fini un callback qui sera appell� lorsque l'image sera t�l�charg�e
     */
    public void download(Context c, HashMap<String, String> params, ImageZoomView mZoomView, OnImageDownloadedCallback callback) {
        BitmapDownloaderTask task = new BitmapDownloaderTask(c, mZoomView);
        task.setOnImageDownloadedCallback(callback);
        task.execute(params);
    }
}
