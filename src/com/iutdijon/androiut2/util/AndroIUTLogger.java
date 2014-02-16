package com.iutdijon.androiut2.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.text.format.DateFormat;

import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.util.loaders.AndroIUTHTTPConnection;


public class AndroIUTLogger implements Thread.UncaughtExceptionHandler {

	private static Writer string_writer;
	private static PrintWriter print_writer;
	private static Thread.UncaughtExceptionHandler super_handler;
	
	private final Context application_context;
	
	public AndroIUTLogger(Context c) {
		application_context = c;
		string_writer = new StringWriter();
		print_writer = new PrintWriter(string_writer);
		
		super_handler = Thread.getDefaultUncaughtExceptionHandler();
		
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		e.printStackTrace();
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected Void doInBackground(Void... params) {
				try{
					e.printStackTrace(print_writer);
					String message = string_writer.toString();
					String user = AndroIUTApplication.getInstance().getAccount() != null ? AndroIUTApplication.getInstance().getAccount().getMail() : "unknow"; 
					
					AndroidHttpClient client = AndroIUTHTTPConnection.getHTTPClient();
	
					HttpPost post_request = new HttpPost("http://iutdijon.u-bourgogne.fr/pedago/iq/projandroid/webapi.php");
	
					ArrayList<NameValuePair> log_params = new ArrayList<NameValuePair>();
					log_params.add(new BasicNameValuePair("function", "logging"));
					log_params.add(new BasicNameValuePair("client", user));
					log_params.add(new BasicNameValuePair("error_message", message));
					log_params.add(new BasicNameValuePair("error_date", DateFormat.format("dd-MM-yyyy", Calendar.getInstance(Locale.FRANCE)).toString()));
					log_params.add(new BasicNameValuePair("os_version", Build.VERSION.RELEASE));
					log_params.add(new BasicNameValuePair("app_version", application_context.getPackageManager().getPackageInfo(application_context.getPackageName(), 0).versionName));
	
					post_request.setEntity(new UrlEncodedFormEntity(log_params));
					client.execute(post_request);
					client.close();
					
				}catch(Exception ex){
					ex.printStackTrace();
				}finally{
					super_handler.uncaughtException(t, e);
				}
				
				return null;
			}			
		}.execute();
	}

}
