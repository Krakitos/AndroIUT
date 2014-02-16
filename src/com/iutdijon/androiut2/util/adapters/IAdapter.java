package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;

/**
 * Une interface implement�e par tout les adaptateurs de l'application
 * @author Morgan Funtowicz
 *
 * @param <R> Type des donn�es en entr�e
 * @param <T> Type des donn�es en sortie
 */
public interface IAdapter<R, T>{

	/**
	 * Converti les donn�es du type R vers le type T
	 * @param data Les donn�es � convertir
	 * @return Les donn�es converti
	 * @throws IOException
	 */
	T parse(R data) throws Exception;
}
