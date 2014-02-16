package com.iutdijon.androiut2.util.loaders;

import android.net.http.AndroidHttpClient;

public class AndroIUTHTTPConnection {
	
	public static AndroidHttpClient getHTTPClient(){
		AndroidHttpClient client = AndroidHttpClient.newInstance("AndroIUT");
		return client;
	}

}
