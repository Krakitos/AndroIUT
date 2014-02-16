package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Classe abstraite, base du parsing d'un XML.
 * Elle prend en entr�e un InputStream qui contient
 * les donn�es brutes du XML.
 * @author Morgan Funtowicz
 *
 * @param <T> Type renvoy� par la m�thode parse
 */
public abstract class XmlAdapter<T> implements IAdapter<InputStream, T>{
	
	XmlPullParser parser = Xml.newPullParser();
	public XmlAdapter() {
	}

	@Override
	/**
	 * D�but du parsing d'un document XML
	 */
	public abstract T parse(InputStream data) throws Exception;
	
	/**
	 * Lit le flux de donner
	 * @param parser Le parser utilis�
	 * @return Une map avec les champs d�sir�s
	 * @throws Exception Lever en cas de XML malform�
	 */
	protected abstract HashMap readFeed(XmlPullParser parser) throws Exception;
	
	/**
	 * Lit le texte d'une balise
	 * @param parser Le parser contenant l'InputStream
	 * @return Une chaine de caract�re
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
}
