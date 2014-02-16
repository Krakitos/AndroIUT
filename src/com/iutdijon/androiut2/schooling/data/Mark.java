package com.iutdijon.androiut2.schooling.data;

/**
 * Classe représentant une note en mémoire
 * @author Morgan Funtowicz
 *
 */
public class Mark extends SchoolingData {
	
	private String mark;
	private String coef;
	private String discipline;
	private String title;
	
	public Mark(String date, String discipline, String mark, String title, String coef) {
		super(date);
		
		this.discipline = discipline;
		this.mark = mark;
		this.title = title;
		this.coef = coef;
	}
	
	public String getMark(){
		return mark;
	}
	public String getDiscipline(){
		return discipline;
	}
	public String getTitle(){
		return title;
	}
	public String getCoef(){
		return coef;
	}
	@Override
	public String toString() {
		float mark_comp = Float.parseFloat(mark);
		
		String color = String.format("<font color=%s>%s/20</font>", mark_comp >= 10 ? "green" : "red", mark);
		
		return super.toString() + title +" <br/> "+color+"<i> coef : "+coef+"</i>";
	}
}
