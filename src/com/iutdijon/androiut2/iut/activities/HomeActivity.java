package com.iutdijon.androiut2.iut.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.ade.activities.ADEActivity;
import com.iutdijon.androiut2.ftp.activities.FTPActivity;
import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.mail.activities.MailActivity;
import com.iutdijon.androiut2.schooling.activities.SchoolingTabActivity;
import com.iutdijon.androiut2.util.IOUtils;
import com.iutdijon.androiut2.util.UIUtil;

/**
 * Menu principal de l'application, permettant d'accéder aux différents services de l'application
 * @author Morgan Funtowicz
 *
 */
public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_home);
		
		//On bloque l'accès aux notes/absences pour les professeurs
		if(AndroIUTApplication.getInstance().getAccount().getType().equalsIgnoreCase(UserAccount.TEACHER_ACCOUNT) || AndroIUTApplication.getInstance().getAccount().isRestricted()){
			findViewById(R.id.marks_btn).setClickable(false);
			UIUtil.showMessage(this, R.string.restricted_mode_info);
		}
	}
	/**
	 * Démarre le service des emails
	 * @param target Le bouton emails dans l'IHM
	 */
	public void startEmailService(View target){
		Intent emails_service = new Intent(this, MailActivity.class);
		startActivity(emails_service);
	}
	
	/**
	 * Démarre le service du planning
	 * @param target Le bouton du planning dans l'IHM
	 */
	public void startADEService(View target){
		Intent ade_service = new Intent(this, ADEActivity.class);
		startActivity(ade_service);
	}
	
	/**
	 * Démarre le service du FTP
	 * @param target Le bouton FTP dans l'IHM
	 */
	public void startFTPService(View target){
		if(IOUtils.isUniversityWifi(this)){
			UIUtil.showMessage(this, R.string.ftp_on_university_wifi_disable);
		}else{
			Intent ftp_service = new Intent(this, FTPActivity.class);
			startActivity(ftp_service);
		}
	}
	
	/**
	 * Démmare le service scolarité 
	 * @param target Le bouton Scolarité dans l'IHM
	 */
	public void startSchoolingService(View target){
		Intent schooling_service = new Intent(this, SchoolingTabActivity.class);
		startActivity(schooling_service);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

}
