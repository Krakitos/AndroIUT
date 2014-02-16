package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iutdijon.androiut2.iut.data.UserFactory;
import com.iutdijon.androiut2.iut.data.account.UserAccount;

/**
 * Permet de créer une instance {@link UserAccount} à partir des informations renvoyer par le serveur 
 * @author Morgan Funtowicz
 *
 */
public class UserAdapter extends XmlAdapter<UserAccount> {
	
	public UserAdapter() {
		
	}
	@Override
	public UserAccount parse(InputStream data) throws Exception {
		UserAccount account = null;
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(data,null);
		parser.nextTag();
		HashMap<String,String> fields = readFeed(parser);

		if("true".equalsIgnoreCase(fields.get("connected")) || "restricted".equalsIgnoreCase(fields.get("connected"))){
			account = UserFactory.getAccount(fields.get("type"), fields);
		}			
		return account;
	}
	@Override
	protected HashMap<String, String> readFeed(XmlPullParser parser) throws XmlPullParserException {
		HashMap<String, String> fields = new HashMap<String, String>();

		try {
			parser.require(XmlPullParser.START_TAG, null, "user");
			while (parser.next() != XmlPullParser.END_TAG) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        String name = parser.getName();
		        String value = readText(parser);
		        fields.put(name, value);
		    }  
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    return fields;
	}

}
