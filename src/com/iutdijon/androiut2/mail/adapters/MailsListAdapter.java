package com.iutdijon.androiut2.mail.adapters;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.MessagingException;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iutdijon.androiut2.R;
import com.iutdijon.androiut2.mail.services.EmailUtils;

/**
 * Classe permettant de faire le lien entre la représentation mémoire d'un message et l'affichage de celui -ci
 * @author Morgan Funtowicz
 *
 */
public class MailsListAdapter extends BaseAdapter{

	private static final SimpleDateFormat date_formater = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
	private static final SimpleDateFormat today_date_formater = new SimpleDateFormat("HH:mm", Locale.getDefault()); 
	
	private LayoutInflater inflater;
	private Message[] mails;
	
	public MailsListAdapter( Context pContext, Message[] pMails) {
		inflater = LayoutInflater.from(pContext);
		mails = pMails;
	}

	@Override
	public int getCount() {
		return mails.length;
	}
	
	/**
	 * Les messages sont reçus à partir de la date la plus ancienne index 0, on inverse le sens de lecture de la liste
	 */
	@Override
	public Object getItem(int position) {
		return mails[mails.length-position-1];
	}

	@Override
	public long getItemId(int position) {
		return mails.length-position-1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.mail_list_item, null);
			
			holder.sender = (TextView) convertView.findViewById(R.id.mail_item_list_sender);
			holder.object = (TextView) convertView.findViewById(R.id.mail_item_list_subject);
			holder.date = (TextView) convertView.findViewById(R.id.mail_item_list_date);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Message target = mails[mails.length-(position+1)];
		
		try {
			String formattedDate = "";
			
			Calendar receivedDate = Calendar.getInstance();
			receivedDate.setTime(target.getReceivedDate());
			if(Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == receivedDate.get(Calendar.DAY_OF_YEAR)){
				formattedDate = today_date_formater.format(target.getReceivedDate());
			}else{
				formattedDate = date_formater.format(target.getReceivedDate());
			}
		
			try{
				if(target.isSet(Flag.SEEN)){
					holder.sender.setText(Html.fromHtml("<font color='#FFFFFF' size=>"+EmailUtils.parseSenderAddress(target.getFrom()[0].toString())+"</font>"));
					holder.object.setText(Html.fromHtml("<small><font color='#999999'>"+target.getSubject()+"</font><small>"));
					holder.date.setText(Html.fromHtml("<small><font color='#999999'>"+formattedDate+"</font><small>"));
				}else{
					holder.sender.setText(Html.fromHtml("<b><font color='#FFFFFF'>"+EmailUtils.parseSenderAddress(target.getFrom()[0].toString())+"</font></b>"));
					holder.object.setText(Html.fromHtml("<small><b><font color='#999999'>"+target.getSubject()+"</font></b></small>"));
					holder.date.setText(Html.fromHtml("<small><font color='#999999'><b>"+formattedDate+"</b></font><small>"));
				}
			}catch(UnsupportedEncodingException e){
				e.printStackTrace();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return convertView;
		
	}
	
	/**
	 * Classe permettant de faire du cache de la vue dans le but d'optimiser les performances de l'application
	 * @author Morgan Funtowicz
	 *
	 */
	private class ViewHolder{
		public TextView sender;
		public TextView object;
		public TextView date;
	}

	
}
