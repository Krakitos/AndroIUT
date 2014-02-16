package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.iutdijon.androiut2.schooling.data.Mark;

/**
 * Adaptateur permettant de lire le fichier XML renvoyer par le serveur et de le convertir en 
 * un tableau associatif sous forme : matière => Liste de notes 
 * @author Morgan Funtowicz
 *
 */
public class MarksAdapter extends XmlAdapter<HashMap<String, ArrayList<Mark>>> {

	private static final String DATA_FIELD = "data";
	private static final String MARK_DATA_FIELD = "mark";
	private static final String MARK_FIELD = "note";
	private static final String SUBJECT_FIELD = "matiere";
	private static final String COEF_FIELD = "coefficient";
	private static final String DAY_FIELD = "jour";
	private static final String INFO_FIELD = "info";
	
	
	/**
	 * Voir {@link XmlAdapter}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, ArrayList<Mark>> parse(InputStream data) throws Exception {
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(data, null);
		parser.nextTag();
		
		return readFeed(parser);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected HashMap readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		HashMap<String, ArrayList<Mark>> values = new HashMap<String, ArrayList<Mark>>();
		
		parser.require(XmlPullParser.START_TAG, null, DATA_FIELD);
		
		while(parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
			Mark m = readEntry(parser);
			if(values.containsKey(m.getDiscipline())){
				values.get(m.getDiscipline()).add(m);
			}else{
				
				ArrayList<Mark> list = new ArrayList<Mark>();
				list.add(m);
				values.put(m.getDiscipline(), list);
			}
		}
		
		return values;
	}
	
	/**
	 * Lit une entrée "mark" 
	 * @param parser Le parser contenant l'InputStream
	 * @return La note lue depuis le flux {@link Mark}
	 * @throws XmlPullParserException Lors de la lecture
	 * @throws IOException Lors d'un problème de lecture
	 */
	private Mark readEntry(XmlPullParser parser) throws XmlPullParserException, IOException{
		Mark mark = null;
		String date ="";
		String discipline="";
		String note="";
		String title=""; 
		String coef="";
		
		parser.require(XmlPullParser.START_TAG, null, MARK_DATA_FIELD);
		while(parser.next() != XmlPullParser.END_TAG){
			
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
			String name = parser.getName();
			if(name.equals(SUBJECT_FIELD)){
				discipline = readDiscipline(parser);
			}else if(name.equals(MARK_FIELD)){
				 note = readMark(parser);
			}else if(name.equals(COEF_FIELD)){
				 coef = readCoef(parser);
			}else if(name.equals(DAY_FIELD)){
				 date =readDate(parser);
			}else if(name.equals(INFO_FIELD)){
				title = readInfo(parser);
			}else {
	            skip(parser);
	        }
		}
		
		mark = new Mark(date, discipline, note, title, coef);
		return mark;
	}
	
	/**
	 * Permet de sauter certaines lignes d'un XML
	 * @param parser Le parser content l'InputStream
	 * @throws XmlPullParserException Lors de la lecture
	 * @throws IOException Lors d'un problème de lecture
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
	 * Lit le champ contenant la matière dans le noeud "mark"
	 * @param parser Le parser contenant l'InputStream
	 * @return Le texte du noeud
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
	 * Lit le champ contenant la note dans le noeud "mark"
	 * @param parser Le parser contenant l'InputStream
	 * @return Le texte du noeud
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readMark(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, MARK_FIELD);
		String mark = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, MARK_FIELD);
	    
	    return mark;
	}
	
	/**
	 * Lit le champ contenant le coefficient dans le noeud "mark"
	 * @param parser Le parser contenant l'InputStream
	 * @return Le texte du noeud
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readCoef(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, COEF_FIELD);
		String coef = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, COEF_FIELD);
	    
	    return coef;
	}
	
	/**
	 * Lit le champ contenant la date dans le noeud "mark"
	 * @param parser Le parser contenant l'InputStream
	 * @return Le texte du noeud
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readDate(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, DAY_FIELD);
		String date = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, DAY_FIELD);
	    
	    return date;
	}
	
	/**
	 * Lit le champ contenant les informations à propos de l'examen dans le noeud "mark"
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private String readInfo(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, null, INFO_FIELD);
		String info = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, INFO_FIELD);
	    
	    return info;
	}

}
