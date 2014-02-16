package com.iutdijon.androiut2.ade.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.net.http.AndroidHttpClient;

import com.iutdijon.androiut2.util.adapters.CalendarAdapter;
import com.iutdijon.androiut2.util.loaders.GetAsyncServiceCall;

/**
 * Service asynchrone permettant de récupérer les données ICalendar sur le serveur
 * @author Morgan Funtowicz
 *
 */
public class ADEICalendarDownloader extends GetAsyncServiceCall<String, Void, String> {
	
	public ADEICalendarDownloader(Activity context) throws FileNotFoundException {
		super(context);
	}

	@Override
	protected String run(String... params) throws IOException {
		String stream = null;
		if(params.length == 2){
			final String resource = params[0];
			final String end = params[1];
			
			final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
			
			HttpPost post_query = new HttpPost(AndroIUTConstantes.WEBAPI_URL);
			
			ArrayList<NameValuePair> post_params = new ArrayList<NameValuePair>();
    		post_params.add(new BasicNameValuePair("function", "getICSPlanning"));
    		post_params.add(new BasicNameValuePair("resource", resource));
    		post_params.add(new BasicNameValuePair("end", end));
    		
    		post_query.setEntity(new UrlEncodedFormEntity(post_params));
    		HttpResponse response = client.execute(post_query);
    		
    		if(response.getEntity().getContentLength() > 0){
    			stream = IOUtils.toString(response.getEntity().getContent(), "ISO-8859-1");
    		}
    		client.close();
		}
		return stream;
	}
	@Override
	protected void onPostRun(String result) {
		super.onPostRun(result);
		if(result != null){
			try {
				new CalendarAdapter(getContext()).parse(result);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
