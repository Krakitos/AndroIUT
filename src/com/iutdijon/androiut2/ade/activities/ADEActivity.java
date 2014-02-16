package com.iutdijon.androiut2.ade.activities;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.ade.service.ADEICalendarDownloader;
import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.iut.data.account.StudentAccount;
import com.iutdijon.androiut2.iut.data.account.TeacherAccount;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.util.PreferencesManager;
import com.iutdijon.androiut2.util.UIUtil;
import com.iutdijon.androiut2.util.loaders.ImageDownloader;
import com.iutdijon.androiut2.util.view.ImageZoomView;
import com.iutdijon.androiut2.util.zoom.DynamicZoomControl;
import com.iutdijon.androiut2.util.zoom.LongPressZoomListener;
import com.iutdijon.androiut2.util.zoom.PinchZoomListener;

/**
 * Classe associée à l'UI du service ADE.
 * Les deux boutons nextWeekBtn et previousWeekBtn permettent de naviguer dans les
 * différentes semaines du planning. L'affichage de l'image utilise ImageZoomView 
 * qui permet de gérer le pinch-to-zoom.
 * Le menu permet d'importer tous les évènements du planning ADE dans le calendrier Google. 
 * 
 * @author Morgan Funtowicz
 *
 */
public class ADEActivity extends Activity implements OnImageDownloadedCallback, DatePickerDialog.OnDateSetListener, OnClickListener {
	private HashMap<String, String> params;
	private UserAccount account = AndroIUTApplication.getInstance().getAccount();
	
	private String ade_server_id = null; 
	
	private AlertDialog use_saved_ade_server_id = null;
	
	/** Navigation button to change planning week */
	private ImageButton nextWeekBtn;
	private ImageButton previousWeekBtn;
	
	/** On touch listener for zoom view */
    private LongPressZoomListener mZoomListener;
    private PinchZoomListener mPinchZoomListener;
    
    /** Image zoom view */
    private ImageZoomView mZoomView;

    /** Zoom control */
    private DynamicZoomControl mZoomControl;

    /** Decoded bitmap image */
    private Bitmap mBitmap;
    
    private int weekGap = 0;
    
    /**
     * Définition des listeners pour les composants.
     * Création de l'interface utilisateur.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_ade);
		
		//Initialisation de la gestion du zoom
		mZoomControl = new DynamicZoomControl();
		
		mZoomListener = new LongPressZoomListener(getApplicationContext());
        mZoomListener.setZoomControl(mZoomControl);
        
        mPinchZoomListener = new PinchZoomListener(getApplicationContext());
        mPinchZoomListener.setZoomControl(mZoomControl);
		
        mZoomView = (ImageZoomView) findViewById(R.id.planning_View);
        mZoomView.setZoomState(mZoomControl.getZoomState());
        mZoomView.setOnTouchListener(mPinchZoomListener);

        mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());
        resetZoomState();
        
        nextWeekBtn = (ImageButton) findViewById(R.id.ade_next_week);
        previousWeekBtn = (ImageButton) findViewById(R.id.ade_previous_week);
        		
	}
	/**
	 * Demarrage de l'activité, on demande d'affichage de la semaine en cours.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		
		if(!AndroIUTApplication.getInstance().getAccount().isRestricted()){
			if(account instanceof StudentAccount){
				StudentAccount s_account = (StudentAccount) account;
				ade_server_id = s_account.getGroup()+'|'+s_account.getHalfGroup();
			}else{
				ade_server_id = ((TeacherAccount)account).getId();
			}
			getPlanning("now");
		}else if(ade_server_id != null){
			getPlanning("now");
		}else{
			UserAccount account = AndroIUTApplication.getInstance().getAccount();
			
			if(PreferencesManager.getInstance().getInt(account.getLogin()+"_ADE") != -1){
				AlertDialog.Builder builder = new Builder(this);
				builder.setNegativeButton(R.string.no, this);
				builder.setPositiveButton(R.string.yes, this);
				builder.setTitle(R.string.use_last_ade_id);
				use_saved_ade_server_id = builder.create();
				use_saved_ade_server_id.show();
				
			}else{
				if(account instanceof StudentAccount){
					String section = ((StudentAccount)account).getPromotion();
					startActivityForResult(new Intent(this, ADEListResourcesChooser.class).putExtra(ADEListResourcesChooser.RESOURCE_BASE_INTENT_EXTRA, section), 0);
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
				
		ade_server_id = String.valueOf(resultCode);
		PreferencesManager.getInstance().setInt(account.getLogin()+"_ADE", resultCode);
		
		//Doit rediriger vers le onStart()
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	/**
	 * Destruction de l'activité, libération de la mémoire utilisée par l'image si elle non nulle.
	 * Suppression des listeners
	 */
	@Override
    protected void onDestroy() {
		
		if(mBitmap != null) mBitmap.recycle();
        
        mZoomView.setOnTouchListener(null);
        mZoomControl.getZoomState().deleteObservers();
        
        super.onDestroy();
	}
	/**
	 * Réinitilise le zoom à 1;
	 */
    private void resetZoomState() {
        mZoomControl.getZoomState().setPanX(0.5f);
        mZoomControl.getZoomState().setPanY(0.5f);
        mZoomControl.getZoomState().setZoom(1f);
        mZoomControl.getZoomState().notifyObservers();
    }
    
    /**
     * Permet de changer de semaine dans le planning
     * @param v Le bouton cliqué
     */
    public void onNavigationButtonClick(View v){
    	if(v.getId() == R.id.ade_next_week){
    		++weekGap;
    	}else{
    		--weekGap;
    	}
    	getPlanning("now");
    }
    
    /**
     * Requête le serveur via un {@link ImageDownloader}
     * pour récupérer l'image de la semaine voulue.
     * La date est passé sous forme de string et convertie automatiquement par le serveur. La valeur par défaut est "now"
     * Le gap correspond au décalage par rapport à la semaine actuelle, permettant de naviger dans le planning.
     * La fonction détermine automatiquement les dimensions de l'image en fonction de celles de l'écran.
     * @param date Date souhaitée.
     */
    public void getPlanning(String date){
    	
    	nextWeekBtn.setClickable(false);
    	previousWeekBtn.setClickable(false);
    	
    	params = new HashMap<String, String>();
		params.put("function", "getPlanning");
		params.put("project", "28");
		params.put("date", date);
		
		params.put("screenW", String.valueOf(getWindowManager().getDefaultDisplay().getWidth()));
		params.put("screenH", String.valueOf(getWindowManager().getDefaultDisplay().getHeight()));
		
		if(weekGap != 0){
			params.put("gap", String.valueOf(weekGap));
		}
		
		params.put("resources", ade_server_id);
		
		new ImageDownloader().download(this, params, mZoomView, this);
    }
    /*
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_ade, menu);
		return true;
	}
	
	/**
	 * Permet de gérer le clique sur un bouton du menu, utilisé par le bouton d'importation du calendrier
	 */
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                resetZoomState();
                return true;
            case R.id.export_ics_btn:
            	if(Build.VERSION.SDK_INT >= 14){
            		showDatePicker();
            	}else{
            		UIUtil.showMessage(this, R.string.old_sdk_version);
            	}
            	return true;
            default :
            	return super.onOptionsItemSelected(item);
            	
        }
    }

	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(dialog == use_saved_ade_server_id){
			if(which == DialogInterface.BUTTON_POSITIVE){
				//Récupération de l'id
				ade_server_id = String.valueOf(PreferencesManager.getInstance().getInt(account.getLogin()+"_ADE"));
				getPlanning("now");
			}else if(which == DialogInterface.BUTTON_NEGATIVE){
				//Demande de l'id
				if(account instanceof StudentAccount){
					String section = ((StudentAccount)account).getPromotion();
					startActivityForResult(new Intent(this, ADEListResourcesChooser.class).putExtra(ADEListResourcesChooser.RESOURCE_BASE_INTENT_EXTRA, section), 0);
				}
			}
		}
	}

	/**
	 * Affiche une popup permettant de saisir la date maximum pour le calendrier à apporter
	 */
	private void showDatePicker() {
		
		final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        
		DatePickerDialog dialog = new DatePickerDialog(this, this, year, month, day);
		dialog.setTitle(R.string.select_end_date);
		dialog.show();
	}
	
	/**
	 * Télécharge le fichier ICalendar depuis le serveur.
	 * @param endDate La date de fin souhaitée
	 * @throws FileNotFoundException
	 */
	private void downloadICalendarFromADE(long endDate) throws FileNotFoundException{
		ADEICalendarDownloader downloader = new ADEICalendarDownloader(this);
		UserAccount account = AndroIUTApplication.getInstance().getAccount();
		
		final StringBuilder resource = new StringBuilder();
		if(account.getType().equalsIgnoreCase(UserAccount.STUDENT_ACCOUNT)){
			resource.append( ((StudentAccount)account).getGroup()+"|"+ ((StudentAccount)account).getHalfGroup());
		}else{
			resource.append(account.getForname() + " " + account.getName());
		}
		downloader.execute(resource.toString(), String.valueOf(endDate));
	}
	@Override
	public View onCreateView(String name, Context context,AttributeSet attrs) {
		View v = super.onCreateView(name, context, attrs);
		
		return v;
	}
	
	/**
	 * Callback de l'interface {@link OnImageDownloadedCallback} appelé lorsque le téléchargement 
	 * de l'image est fini.
	 */
	@Override
	public void onDownloaded() {
		nextWeekBtn.setClickable(true);
		previousWeekBtn.setClickable(true);
	}
	
	/**
	 * Callback de l'interface {@link OnDateSetListener} appelé lorsque l'utilisateur saisi
	 * la date maximale pour l'importation du planning dans le calendrier de l'utilisateur.
	 */
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
		final Calendar asked = Calendar.getInstance();
		asked.set(year, monthOfYear, dayOfMonth, 18, 0, 0);
				
		try {
			downloadICalendarFromADE(asked.getTime().getTime());
		} catch (FileNotFoundException e) {
			UIUtil.showMessage(this, R.string.error_connection_to_server);
			e.printStackTrace();
		}
	}
}

