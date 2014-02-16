package com.iutdijon.androiut2.util.loaders;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.ade.activities.OnImageDownloadedCallback;
import com.iutdijon.androiut2.util.view.ImageZoomView;

/**
 * Classe permettant de télécharger une image dans un processus à part
 * @author Morgan Funtowicz
 *
 */
public class BitmapDownloaderTask extends AsyncTask<HashMap<String, String>, Void, Bitmap> {

    private final WeakReference<ImageZoomView> imageViewReference;
    private static final String iut_webapi_url = "http://iutdijon.u-bourgogne.fr/intra/iq/AndroIUT/webapi.php";
    private final Context context;
    private ProgressDialog progressBar;
    private OnImageDownloadedCallback callback;
    
    public BitmapDownloaderTask(Context c, ImageZoomView mZoomView) {
        imageViewReference = new WeakReference<ImageZoomView>(mZoomView);
        context = c;
    }

    @Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	progressBar = ProgressDialog.show(context, "", context.getText(R.string.ftp_command_running));
    }
    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(HashMap<String, String>... params) {
         // params comes from the execute() call: params[0] is the url.
        return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
//        if (isCancelled()) {
//            bitmap = null;
//        }
    	if(progressBar != null){
    		if(progressBar.isShowing()){
	    		progressBar.dismiss();
	    		progressBar = null;
    		}
    	}
        if (imageViewReference != null) {
        	ImageZoomView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImage(bitmap);
            }
        }
        if(callback != null){
        	callback.onDownloaded();
        }
    }
    /**
     * Démarre le téléchargement de l'image suivant les paramètres éventuellement passé à l'URL
     * Les paramètres sont envoyés via la méthode POST avec le protocol HTTP
     * @param parameters La map contenant tous les paramètres à ajouter à la requête
     * @return Un Bitmap ou null si l'image est invalide
     */
    static Bitmap downloadBitmap(HashMap<String, String> parameters) {
        final AndroidHttpClient client = AndroIUTHTTPConnection.getHTTPClient();
        final HttpPost postRequest = new HttpPost();
        
        try {
        	Set<String> parameters_keys = parameters.keySet();

    		HttpPost post_query = new HttpPost(iut_webapi_url);

    		ArrayList<NameValuePair> post_params = new ArrayList<NameValuePair>();
    		post_params.add(new BasicNameValuePair("function", "getPlanning"));
    		for(String key : parameters_keys){
    			post_params.add(new BasicNameValuePair(key, parameters.get(key)));
    		}

    		post_query.setEntity(new UrlEncodedFormEntity(post_params));
    		AndroidHttpClient.modifyRequestToAcceptGzipResponse(post_query);
    		
    		
    		HttpResponse response = client.execute(post_query);

            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { 
                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + iut_webapi_url); 
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = AndroidHttpClient.getUngzippedContent(entity); 
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();  
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            postRequest.abort();
            Log.w("ImageDownloader", e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }
    
    /**
     * Permet de définir un callback lorsque l'image est téléchargée
     * @param callback
     */
    public void setOnImageDownloadedCallback(OnImageDownloadedCallback callback){
    	this.callback = callback;
    }
}