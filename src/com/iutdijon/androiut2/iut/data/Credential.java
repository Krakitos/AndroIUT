package com.iutdijon.androiut2.iut.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.net.http.AndroidHttpClient;

import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.util.adapters.UserAdapter;

/**
 * Credential gère toute l'authentification de l'étudiant sur le serveur d'authentification de l'IUT
 * Il créer {@link com.iutdijon.androiut2.iut.data.account.UserAccount} en fonction des données renvoyées par le serveur
 * Si la connexion est refusée par le serveur un {@link com.iutdijon.androiut2.iut.data.account.UserAccount} null est renvoyé
 * @author Morgan Funtowicz
 *
 */
public class Credential {
	private final String login;
	private final String password;
	
	
	/**
	 * Crée une tache d'authentification sur le serveur de l'IUT avec le login et password entré par l'utilisateur
	 * @param login Login entré par l'utilisa"teur
	 * @param password Password entré par l'utilisateur
	 */
	public Credential(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	/**
	 * Renvoie le mot de passe entré par l'utilisateur
	 * @return Mot de passe de l'utilisateur
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Renvoie le login utilisé lors de la connexion
	 * @return Login utilisé lors de la connexion
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Vérifie que l'utilisateur est bien un membre de l'IUT
	 * @return Un {@link com.iutdijon.androiut2.iut.data.account.UserAccount} si l'utilisateur est reconnu, null si inconnu
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public UserAccount checkCredential() throws Exception{

		HttpPost post_request = new HttpPost(AndroIUTConstantes.WEBAPI_URL);
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("function", "connect"));
		params.add(new BasicNameValuePair("login", login));
		params.add(new BasicNameValuePair("password", password));
		
		
		post_request.setEntity(new UrlEncodedFormEntity(params));
		
		//TODO : Optimisation compression GZIP des transaction client / serveur
//		String params = "function=connect&login="+login+"&password="+password;
//		
//		post_request.setEntity(AndroidHttpClient.getCompressedEntity(params.getBytes(), null)); 
		
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(post_request);

		AndroidHttpClient client = AndroidHttpClient.newInstance("AndroIUT");
		HttpResponse response = client.execute(post_request);

		InputStream stream = AndroidHttpClient.getUngzippedContent(response.getEntity());
		UserAccount account = new UserAdapter().parse(stream);
		
		client.close();
		
		if(account != null){
			account.setLogin(login);
			account.setPassword(password);
		}
		return account;
	}

}
