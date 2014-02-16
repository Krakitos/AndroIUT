package com.iutdijon.androiut2.schooling.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Classe abstraite servant de base à toutes données liées à la scolarité d'un étudiant
 * @author Morgan Funtowicz
 *
 */
public abstract class SchoolingData {
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
	
	protected String date;
	
	public SchoolingData(String date) {
		this.date = date;
	}
	
	public String getDate(){
		return date;
	}
	@Override
	public String toString() {
		Date d;
		String result = "";
		try {
			d = formatter.parse(date);
			result += DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.FRENCH).format(d);
		} catch (ParseException e) {
			e.printStackTrace();
			return "<b><u>"+ date + "</u></b> ";
		} 
		return "<b><u>"+ result+ "</u></b> ";
	}
}
