package com.iutdijon.androiut2.mail.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.util.UIUtil;
import com.iutdijon.androiut2.util.bridge.BridgeFinder;
import com.iutdijon.androiut2.util.loaders.GetAsyncServiceCall;

/**
 * Classe gérant l'affichage d'un email à des fins de lecture.
 * Elle utilise une WebView afin de rendre le plus fidèlement les emails dont la plupart sont écrit en HTML
 * Elle affiche l'expéditeur, l'objet, le message ainsi que les pièces jointes.
 * @author Morgan Funtowicz
 *
 */
public class MailReaderActivity extends Activity implements OnChildClickListener {

	private WebView text_holder;
	private TextView text_header;
	private ExpandableListView text_footer;
	
	/*
	 * TODO : Reprendre la gestion de la lecture des pièces jointes, bricolage actuellement avec la variable statique
	 */
	public static HashMap<String, Part> attachments;

	public MailReaderActivity() {

	}

	/**
	 * Création de la vue utilisateur avec les données mémoires du message
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		Bundle data = getIntent().getBundleExtra("data");

		setContentView(R.layout.activity_mail_reading);

		text_holder = (WebView) findViewById(R.id.mail_content_textview);
		text_header = (TextView) findViewById(R.id.mail_header_textview);
		text_footer = (ExpandableListView) findViewById(R.id.mail_footer_attachment);
				
		text_header.setText(Html.fromHtml("<b>" + data.getString("sender") + "</b><br/>" + data.getString("subject")));

		String[] attachments = data.getStringArray("attachment");

		if (attachments != null) {
			List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
	        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
	        
	        //Titre de la liste
	        Map<String, String> curGroupMap = new HashMap<String, String>();
            groupData.add(curGroupMap);
            curGroupMap.put("NAME", getString(R.string.attachments_label));
	        
            //Contenu du sous groupe avec la liste des pièces jointes
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int i = 0; i < attachments.length; i++) {
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put("NAME", attachments[i]);
            }
            
            childData.add(children);
            
            SimpleExpandableListAdapter mAdapter = new SimpleExpandableListAdapter(
                    this,
                    groupData,
                    R.layout.expandable_list_view,
                    new String[] { "NAME" },
                    new int[] {android.R.id.text1},
                    childData,
                    R.layout.expandable_list_view_child,
                    new String[] { "NAME" },
                    new int[] { android.R.id.text2}
            );
            text_footer.setAdapter(mAdapter);
            text_footer.setOnChildClickListener(this);
		}
		text_holder.loadDataWithBaseURL("", data.getString("content"),"text/html", "utf-8", null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(attachments != null){
			attachments = null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
		String attachment_name = ((HashMap<String, String>) text_footer.getExpandableListAdapter().getChild(groupPosition, childPosition)).get("NAME");
		if(attachments.containsKey(attachment_name)){

			new GetAsyncServiceCall<Part, Void, File>(this) {

				@Override
				protected File run(Part... params) throws IOException {
					
					Part p = params[0];
					File temp = null;
					
					try {
						temp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), p.getFileName());
						if(p instanceof MimeBodyPart){
							((MimeBodyPart)p).saveFile(temp);
						}
					} catch (MessagingException e) {
						UIUtil.showMessage(MailReaderActivity.this, e.getMessage());
						e.printStackTrace();
					}
					
					return temp;
				}
				
				@Override
				protected void onPostRun(File result) {
					super.onPostRun(result);
					
					try{
						Intent reader = BridgeFinder.getDefaultApplicationFromFileName(result);
						
						if(reader == null){
							UIUtil.showMessage(MailReaderActivity.this, R.string.no_application_found);
						}
						startActivity(reader);
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}.execute(attachments.get(attachment_name));
		}else{
			UIUtil.showMessage(this, R.string.downloading_attachment);
			return false;
		}
		return true;
	}
}
