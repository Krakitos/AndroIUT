package com.iutdijon.androiut2.util.adapters;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Créer un Adapter permettant de convertir des données brutes en Bitmap
 * @author Morgan Funtowicz
 *
 */
public class BitmapAdapter implements IAdapter<InputStream, Bitmap> {

	public BitmapAdapter() {
	}

	@Override
	public Bitmap parse(InputStream url) throws IOException {
		return BitmapFactory.decodeStream(url);
	}

}
