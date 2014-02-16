package com.iutdijon.androiut2.iut.services;

import java.io.IOException;

import android.app.Activity;

import com.iutdijon.androiut2.iut.data.Credential;
import com.iutdijon.androiut2.iut.data.account.UserAccount;
import com.iutdijon.androiut2.util.loaders.GetAsyncServiceCall;

/**
 * Classe permettant de requêter la webapi pour authentifier un utilisateur
 * Ce service est asynchrone afin de traiter toutes les requêtes réseau en dehors de l'IHM
 * @author Morgan Funtowicz
 *
 */
public class IUTLoginServiceCall extends GetAsyncServiceCall<Credential, Void, UserAccount> {

	public IUTLoginServiceCall(Activity context) {
		super(context);
	}

	@Override
	protected UserAccount run(Credential... params) throws IOException {
		UserAccount account = null;
		try {
			account = params[0].checkCredential();
		} catch (Exception e) {
			account = null;
		}
		return account;
	}

}
