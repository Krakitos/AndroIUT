package com.iutdijon.androiut2.util.observers;


/**
 * Interface permetant d'impl�menter un Observable
 * @author Morgan Funtowicz
 *
 */
public interface Observable {
	void addObserver(Observer o);
	void removeObserver(Observer o);
	void notify(int progress);
}
