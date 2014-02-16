package com.iutdijon.androiut2.util.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Observable;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.provider.CalendarContract.Events;

/**
 * Permet d'adapter un fichier ICalendar vers des instructions compréhensibles par l'API Google Calendar
 * Cette fonctionnalité n'est supportée que pour les versions d'Android après Ice Scream Sandwich 
 * @author Morgan Funtowicz
 *
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CalendarAdapter extends Observable implements IAdapter<String, Void> {

	private final Context context;
	public CalendarAdapter(Context c) {
		context = c;
	}
	
	@Override
	public Void parse(String data) throws Exception {
		
		String[] splitted = data.split("\r\n");
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.FRANCE);
		
		ContentResolver cr = context.getContentResolver();
		AsyncQueryHandler handler = new AsyncQueryHandler(cr) {};
		
		for (int i = 0; i < splitted.length; i++) {
			String line = splitted[i];
			
			if(line.equalsIgnoreCase("BEGIN:VEVENT")){
				ContentValues values = new ContentValues(6);
				values.put(Events.CALENDAR_ID, 1);
				values.put(Events.EVENT_TIMEZONE, "Europe/Paris");
				do{
					line = splitted[++i];
					final String value = line.substring(line.indexOf(":")+1);
					if(line.startsWith("SUMMARY")){
						values.put(Events.TITLE, value);
						values.put(Events.DESCRIPTION, value);
					}else if(line.startsWith("LOCATION")){
						values.put(Events.EVENT_LOCATION, value);
					}else if(line.startsWith("DTSTART")){	
						values.put(Events.DTSTART, format.parse(value.replaceAll("[TZ]", "")).getTime());
					}else if(line.startsWith("DTEND")){
						values.put(Events.DTEND, format.parse(value.replaceAll("[TZ]", "")).getTime());
					}
				}while(!line.equalsIgnoreCase("END:VEVENT"));
				
				handler.startInsert(i, null, Events.CONTENT_URI, values);
			}
		}		
		return null;
	}
}
