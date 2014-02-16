package com.iutdijon.androiut2.schooling.data;

/**
 * Classe repr�sentant une absence en m�moire
 * @author Morgan Funtowicz
 *
 */
public class Absence extends SchoolingData{
	
	private final String hour;
	private final String duration;
	private final String discipline;
	
	public Absence(String date, String discipline, String hour, String duration) {
		super(date);
		this.hour = hour;
		this.duration = duration;
		this.discipline = discipline;
	}

	public String getHour(){
		return hour;
	}
	
	public String getDuration(){
		return duration;
	}
	public String getDiscipline(){
		return discipline;
	}
	
	@Override
	public String toString() {
		return super.toString() + " � "+ hour + " en "+ discipline + " pendant " + duration+"h";
	}
}
