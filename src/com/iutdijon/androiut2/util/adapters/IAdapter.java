package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;

/**
 * Une interface implementée par tout les adaptateurs de l'application
 * @author Morgan Funtowicz
 *
 * @param <R> Type des données en entrée
 * @param <T> Type des données en sortie
 */
public interface IAdapter<R, T>{

	/**
	 * Converti les données du type R vers le type T
	 * @param data Les données à convertir
	 * @return Les données converti
	 * @throws IOException
	 */
	T parse(R data) throws Exception;
}
