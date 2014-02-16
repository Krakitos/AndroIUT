package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iutdijon.androiut2.schooling.data.Absence;
import com.iutdijon.androiut2.schooling.data.Mark;

/**
 * Permet de récupérer les informations sur les absences à partir du XML renvoyer sur le serveur
 * @author Morgan Funtowicz
 *
 */
public class AbsencesAdapter extends XmlAdapter<HashMap<String, Set<Mark>>> {
	
	private static final String DATA_FIELD = "data";
	private static final String ABSENCE_FIELD = "absence";
	private static final String SUBJECT_FIELD = "matiere";
	private static final String DAY_FIELD = "jour";
	private static final String DURATION_FIELD = "duree";
	private static final String HOUR_FIELD = "heure";

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Set<Mark>> parse(InputStream data) throws Exception {
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(data, null);
		parser.nextTag();
		
		return readFeed(parser);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected HashMap readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		HashMap<String, ArrayList<Absence>> values = new HashMap<String, ArrayList<Absence>>();
		
		parser.require(XmlPullParser.START_TAG, null, DATA_FIELD);
		
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
			Absence a = readEntry(parser);
			if(values.containsKey(a.getDate())){
				values.get(a.getDate()).add(a);
			}else{
				
				ArrayList<Absence> list = new ArrayList<Absence>();
				list.add(a);
				values.put(a.getDate(), list);
			}
		}
		
		return values;
	}
	
	private Absence readEntry(XmlPullParser parser) throws XmlPullParserException, IOException{
		Absence a = null;
		String date ="";
		String discipline="";
		String hour=""; 
		String duration="";
		
		parser.require(XmlPullParser.START_TAG, null, ABSENCE_FIELD);
		while(parser.next() != XmlPullParser.END_TAG){
			
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
			String name = parser.getName();
			if(name.equals(SUBJECT_FIELD)){
				discipline = readDiscipline(parser);
			}else if(name.equals(HOUR_FIELD)){
				 hour = readHour(parser);
			}else if(name.equals(DURATION_FIELD)){
				 duration = readDuration(parser);
			}else if(name.equals(DAY_FIELD)){
				 date = readDate(parser);
			}else {
	            skip(parser);
	        }
		}
		
		a = new Absence(date, discipline, hour, duration);
		return a;
	}
	
	/**
	 * Permet d'ignorer certains noeuds du XML
	 * @param parser Le parser utilisé pour lire le fichier XML
	 * @throws XmlPullParserException 
	 * @throws IOException
	 */
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }

	/**
	 * Lit une discipline dans le XML
	 * @param parser Le parser utilisé pour lire le fichier XML
	 * @return La valeur de la discipline
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readDiscipline(XmlPullParser parser) throws XmlPullParserException, IOException{
		
		parser.require(XmlPullParser.START_TAG, null, SUBJECT_FIELD);
		String discipline = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, SUBJECT_FIELD);
	    
	    return discipline;
	}
	
	/**
	 * Lit l'heure dans le XML
	 * @param parser Le parser utilisé pour lire le fichier XML
	 * @return La valeur de l'heure
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readHour(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, HOUR_FIELD);
		String mark = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, HOUR_FIELD);
	    
	    return mark;
	}
	
	/**
	 * Lit une durée dans le XML
	 * @param parser Le parser utilisé pour lire le fichier XML
	 * @return La valeur de la durée
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readDuration(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, DURATION_FIELD);
		String coef = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, DURATION_FIELD);
	    
	    return coef;
	}
	
	/**
	 * Lit une date dans le XML
	 * @param parser Le parser utilisé pour lire le fichier XML
	 * @return La valeur de la date
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readDate(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, DAY_FIELD);
		String date = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, DAY_FIELD);
	    
	    return date;
	}

}
