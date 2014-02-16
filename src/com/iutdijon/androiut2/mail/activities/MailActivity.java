package com.iutdijon.androiut2.mail.activities;


import javax.mail.Message;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.mail.adapters.MailDisplayAdapter;
import com.iutdijon.androiut2.mail.services.MailService;
import com.iutdijon.androiut2.util.UIUtil;

/**
 * 
 * @author Morgan Funtowicz
 * Classe associée à l'IHM permettant de voir la liste des e-mails pour l'utilisateur.
 * La classe demande au modèle de charger un nombre restreint de messages, correspondant aux 15 derniers
 * cette limite est représentée par MailService.MAIL_SIZE_LIMIT.
 * Le bouton load_more permet de charger 10 messages supplémentaires depuis le serveur. 
 */
public class MailActivity extends ListActivity implements View.OnClickListener{
	private ProgressDialog progressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_mail);
		
		Button load_more = (Button)findViewById(R.id.email_more);
		load_more.setOnClickListener(this);
		
		refreshUiMailList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_mail, menu);
		return true;
	}
	
	
	/**
	 * Cette méthode permet de gérer le clique sur un message dans la liste
	 * Elle utilise {@link MailDisplayAdapter} afin de faire le lien entre les données mémoires
	 * d'un message et la représentation sur l'écran de celui-ci.
	 * Durant le chargement d'un message, une progresse barre apparait afin d'informer l'utilisateur
	 * de la progression du téléchargement.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Message m = (Message) l.getItemAtPosition(position);
		
		MailDisplayAdapter adapter = new MailDisplayAdapter(){
			
			@Override
			protected void onPreExecute() {
				progressBar = ProgressDialog.show(MailActivity.this, "", getText(R.string.downloading_email));
				super.onPreExecute();
			}
			
			@Override
			protected void onPostExecute(Bundle result) {
				try {
					final Intent mail_display_intent = new Intent(MailActivity.this, MailReaderActivity.class);	
					mail_display_intent.putExtra("data", result);
					startActivity(mail_display_intent);
				} catch (Exception e) {
					e.printStackTrace();
					UIUtil.showMessage(MailActivity.this, "Erreur durant la récupération du message depuis le serveur ...");
				}finally{
					if(progressBar.isShowing()) progressBar.dismiss();
				}
			};
		};
		adapter.execute(m);
	}
	
	@Override
	/**
	 * Callback du clique sur le bouton load_more permettant de charger plus d'email.
	 * L'appui sur ce bouton implique un rafraichissement de la liste des e-mails.
	 */
	public void onClick(View v) {
		MailService.MAIL_SIZE_LIMIT += 10;
		refreshUiMailList();
	}
	
	private void refreshUiMailList(){
		UserAccount account = AndroIUTApplication.getInstance().getAccount();

		if(account == null){
			throw new NullPointerException("Le compte utilisateur est null");
		}
		new MailService(this).execute(account.getLogin(), account.getPassword());
	}

}
