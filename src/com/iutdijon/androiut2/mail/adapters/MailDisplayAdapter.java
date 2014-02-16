package com.iutdijon.androiut2.mail.adapters;


import java.util.HashMap;

import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.Part;

import android.os.AsyncTask;
import android.os.Bundle;

import com.iutdijon.androiut2.mail.activities.MailReaderActivity;
import com.iutdijon.androiut2.mail.services.EmailUtils;

/**
 * Classe permettant de récupérer de manière asynchrone le contenu du message dont on souhaite afficher le contenu
 * @author Morgan Funtowicz
 *
 */
public class MailDisplayAdapter extends AsyncTask<Message, Void, Bundle>{

	public MailDisplayAdapter(){
		
	}
	@Override
	protected Bundle doInBackground(Message... params) {
		
		Bundle mail_data = new Bundle();
		Message email = params[0];
		
		try {
			//On annonce au serveur qu'on a vu le message
			email.setFlag(Flag.SEEN, true);
			
			HashMap<String, Part> attachments = EmailUtils.getAttachments(email);
			String[] attachments_name;
			
			if(attachments != null){
				attachments_name = new String[attachments.size()];
				mail_data.putStringArray("attachment", attachments.keySet().toArray(attachments_name));
				MailReaderActivity.attachments = attachments;
			}
			
			
			mail_data.putString("sender", EmailUtils.parseSenderAddress(email.getFrom()[0].toString()));
			mail_data.putString("subject", email.getSubject());
			mail_data.putString("content", EmailUtils.extractMessage(email));
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mail_data;
	}
	
	

}
