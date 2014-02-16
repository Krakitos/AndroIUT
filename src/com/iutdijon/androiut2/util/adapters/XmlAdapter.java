package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

/**
 * Classe abstraite, base du parsing d'un XML.
 * Elle prend en entrée un InputStream qui contient
 * les données brutes du XML.
 * @author Morgan Funtowicz
 *
 * @param <T> Type renvoyé par la méthode parse
 */
public abstract class XmlAdapter<T> implements IAdapter<InputStream, T>{
	
	XmlPullParser parser = Xml.newPullParser();
	public XmlAdapter() {
	}

	@Override
	/**
	 * Début du parsing d'un document XML
	 */
	public abstract T parse(InputStream data) throws Exception;
	
	/**
	 * Lit le flux de donner
	 * @param parser Le parser utilisé
	 * @return Une map avec les champs désirés
	 * @throws Exception Lever en cas de XML malformé
	 */
	protected abstract HashMap readFeed(XmlPullParser parser) throws Exception;
	
	/**
	 * Lit le texte d'une balise
	 * @param parser Le parser contenant l'InputStream
	 * @return Une chaine de caractère
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
