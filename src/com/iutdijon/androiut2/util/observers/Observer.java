package com.iutdijon.androiut2.util.observers;

/**
 * Interface permettant d'implémenter un Observateur
 * @author Morgan Funtowicz
 *
 */
public interface Observer {
	public void update(Observable o, int progress);
}
