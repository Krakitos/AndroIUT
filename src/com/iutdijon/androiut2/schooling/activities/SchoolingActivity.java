package com.iutdijon.androiut2.schooling.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.schooling.service.SchoolingDataServiceCall;

/**
 * Classe associée à l'affichage des informations sur la scolarité
 * @author Morgan Funtowicz
 *
 */
public class SchoolingActivity extends ListActivity{

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		
		setContentView(R.layout.schooling_list_view);
		
		String function = getIntent().getStringExtra("function");
		
		String studentNum = AndroIUTApplication.getInstance().getAccount().getNum();
		SchoolingDataServiceCall service = new SchoolingDataServiceCall(this, studentNum, function);
		service.execute();
	}
}