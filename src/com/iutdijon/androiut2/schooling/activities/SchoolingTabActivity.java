package com.iutdijon.androiut2.schooling.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.schooling.service.SchoolingDataServiceCall;
import com.iutdijon.androiut2.util.UIUtil;

/**
 * Classe permettant de gérer les onglets dans l'UI du service scolarité
 * Elle surcharge TabActivity qui est dépréciée, mais utile car nous visons les versions > Gingerbread
 * @author Morgan Funtowicz
 *
 */

@SuppressWarnings("deprecation")
public class SchoolingTabActivity extends TabActivity {

	public SchoolingTabActivity() {
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		
		TabHost host = getTabHost();
		
		Intent intentMarks = new Intent(this, SchoolingActivity.class);
		intentMarks.putExtra("function", SchoolingDataServiceCall.FUNCTION_GET_MARKS);
		
		TabSpec tabSpecMarks = host.newTabSpec(getText(R.string.marks_tab_name).toString())
				.setIndicator(getText(R.string.marks_tab_name))
				.setContent(intentMarks);
		
		
		Intent intentAbsences = new Intent(this, SchoolingActivity.class);
		intentAbsences.putExtra("function", SchoolingDataServiceCall.FUNCTION_GET_ABSENCES);
		
		TabSpec tabSpecAbsences = host.newTabSpec(getText(R.string.absences_tab_name).toString())
				.setIndicator(getText(R.string.absences_tab_name))
				.setContent(intentAbsences);
		try{
			host.addTab(tabSpecMarks);
			host.addTab(tabSpecAbsences);
		}catch(Exception e){
			UIUtil.showMessage(this, R.string.error_connection_to_server);
			finish();
		}
		
	}
}
