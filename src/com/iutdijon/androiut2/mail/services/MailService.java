package com.iutdijon.androiut2.mail.services;

import java.util.MissingResourceException;

import javax.mail.Message;
import javax.mail.MessagingException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.mail.adapters.MailsListAdapter;
import com.iutdijon.androiut2.util.UIUtil;

/**
 * Service permettant de gérer toutes les requêtes sur le serveur des emails
 * @author Morgan Funtowicz
 *
 */
public class MailService extends AsyncTask<String, Void, Message[]>{
	
	public static int MAIL_SIZE_LIMIT = 15;
	
	private MailReader reader;
	private ListActivity context;

	private ProgressDialog progressBar;
	
	/**
	 * Construit un nouvel objet MailServices
	 * @param context Un référence vers l'activité qui contiendra le résultat des requêtes
	 */
	public MailService(ListActivity context) {
		reader = new MailReader(MailReader.Protocol.IMAPS, "iut-dijon.u-bourgogne.fr");
		
		this.context = context;
	}
	
	@Override
	protected Message[] doInBackground(String... params) {
		if(params.length < 2) throw new MissingResourceException("Mail Reader execute params have to be login/password", null, null);
		
		Message[] emails = null;
		try {
			reader.connect(params[0], params[1]);
			emails = reader.getMessages(MAIL_SIZE_LIMIT);
		} catch (MessagingException e) {
			progressBar.dismiss();
			UIUtil.showMessage(context, R.string.error_connection_to_server);
			e.printStackTrace();
		}
		
		return emails;	
	}
	
	@Override
	protected void onPreExecute() {
		progressBar = ProgressDialog.show(context, "", context.getText(R.string.downloading_emails));
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(Message[] result) {
		if(progressBar.isShowing()){
			progressBar.dismiss();
		}
		context.setListAdapter(new MailsListAdapter(context, result));
		super.onPostExecute(result);
	}
}
