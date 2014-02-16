package com.iutdijon.androiut2.ftp.activities;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.ftp.adapters.FTPFileListAdapter;
import com.iutdijon.androiut2.ftp.data.FTPCommand;
import com.iutdijon.androiut2.ftp.data.FTPCommandResult;
import com.iutdijon.androiut2.ftp.data.FTPCommandWrapper;
import com.iutdijon.androiut2.ftp.services.FTPServiceCall;
import com.iutdijon.androiut2.global.AndroIUTApplication;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.util.IOUtils;
import com.iutdijon.androiut2.util.UIUtil;
import com.iutdijon.androiut2.util.bridge.BridgeFinder;

/**
 * IHM pour le service FTP de l'IUT. <br/> 
 * Les méthodes qui gèrent le cycle de vie de l'IHM sont surchargées pour faire correspondre
 * le cycle de vie du Socket utilisé par le client FTP au cycle de vie de l'IHM. Et éviter 
 * d'user la batterie inutilement.
 * 
 * @author Morgan Funtowicz
 *
 */
public class FTPActivity extends ListActivity implements OnItemLongClickListener{

	private FTPServiceCall client;
	private UserAccount account;
	
	private static Handler mHandler;
	private static ProgressDialog progressBar; 
	
	private static String path = "./";
	
	public FTPActivity() {
		account = AndroIUTApplication.getInstance().getAccount();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(IOUtils.isUniversityWifi(this)){
			UIUtil.showMessage(this, R.string.ftp_on_university_wifi_disable);
			return;
		}
		
		if(savedInstanceState != null){
			if(savedInstanceState.getString("path") != null){
				path = savedInstanceState.getString("path");
			}
		}
		
		mHandler = new FTPCommandHandler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == FTPServiceCall.FTP_COMMAND_START){
					onComandStart((FTPCommandWrapper) msg.obj);
				}else{
					onCommandExecuted((FTPCommandResult) msg.obj);
				}
			}
		};
		
		setContentView(R.layout.activity_ftp);
		
		getListView().setOnItemLongClickListener(this);
		
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String operatorName = telephonyManager.getNetworkOperatorName().toLowerCase();
		
		if(operatorName.contains("bouygues") || operatorName.contains("free")){
			UIUtil.showMessage(this, R.string.ftp_on_bouy_free_operator);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		client = new FTPServiceCall(this, mHandler, FTPServiceCall.UNIVERSITY_FTP_SERVER_URL, FTPServiceCall.UNIVERSITY_FTP_SERVER_PORT);
		client.start();
		client.sendCommand(new FTPCommandWrapper(FTPCommand.FTP_LOGIN, account.getLogin()+":"+account.getPassword()));
		
		if(!path.equalsIgnoreCase("./")){
			client.sendCommand(new FTPCommandWrapper(FTPCommand.FTP_CD, path));
		}
		
		
	}
	
	@Override
	protected void onPause() {
		
		if( progressBar != null && progressBar.isShowing()){
			progressBar.dismiss();
			progressBar = null;
		}
		client.sendCommand(new FTPCommandWrapper(FTPCommand.FTP_LOGOUT, null));
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("path", path);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		
		if(state.getString("path") != null){
			path = state.getString("path");
		}
		
		if(client != null){
			client.sendCommand(new FTPCommandWrapper(FTPCommand.FTP_CD, path));
		}
		
	}
	
	@Override
	protected void onDestroy() {
		client = null;
		path = "./";
		super.onDestroy();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		FTPFile file = (FTPFile) l.getItemAtPosition(position);
		
		if(file.isDirectory()){
			
			if(file.getName().equalsIgnoreCase("..")){
				path = path.substring(0, path.length()-1);
				path = path.substring(0, path.lastIndexOf('/')+1);
			}else{
				path = path.concat(file.getName()+"/");
			}
			client.sendCommand(new FTPCommandWrapper(FTPCommand.FTP_CD, file.getName()));
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		FTPFile file = (FTPFile) getListView().getItemAtPosition(position);
		if(file.isFile()){
			client.sendCommand(new FTPCommandWrapper(FTPCommand.FTP_GET, file.getName()));
		}
		return file.isFile();
	}
	
	private void onCommandExecuted(FTPCommandResult result) {
		
		if( progressBar != null && progressBar.isShowing()){
			progressBar.dismiss();
			progressBar = null;
		}
		
		if(result != null){
			if(result.getDirectoryFiles() != null) {

				FTPFileListAdapter adapter = (FTPFileListAdapter) getListAdapter();
				
				if(adapter == null){
					adapter = new FTPFileListAdapter(this,  R.layout.ftp_list_item, result.getDirectoryFiles());
					setListAdapter(adapter);
				}else{
					((FTPFileListAdapter)getListAdapter()).updateFileList(result.getDirectoryFiles());
				}
				
			}else if(result.getStoredFile() != null){
				
				File file = result.getStoredFile();
		
				try{
					Intent viewer = BridgeFinder.getDefaultApplicationFromFileName(file);
					startActivity(viewer);
					
				}catch(ActivityNotFoundException ex){
					UIUtil.showMessage(this, "Aucune application n'est installée pour ouvrir ce type de fichier");
				}
			}
		}
	}
	private void onComandStart(FTPCommandWrapper command){
		if(command.getCommandID() != FTPCommand.FTP_LOGOUT){
			progressBar  = ProgressDialog.show(FTPActivity.this, "", getText(R.string.ftp_command_running),true,true);
		}
	}
	
	static class FTPCommandHandler extends Handler{
		
	}
}
