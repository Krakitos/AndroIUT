package com.iutdijon.androiut2.util.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * HashMap<String, String> utilisant un principe de cryptage pour les valeurs stockées en mémoire
 * @author Morgan Funtowicz
 */
public class SecureCenter extends HashMap<String, String>{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7777320017164141637L;
	
	private Cipher reader = null;
    private Cipher writer = null;
    private IvParameterSpec iv = null;
    private Base64 base64 = null;
    
    public SecureCenter() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
        initCiphers();
        base64 = new Base64();
    }
    public SecureCenter(String initialisation) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
        initCiphers(initialisation);
        base64 = new Base64();
    }
    @Override
    public String get(Object o) {
        String val = "";
        try {
            val = decrypt(super.get(o));
        } catch (Exception ex) {
            Logger.getLogger(SecureCenter.class.getName()).log(Level.SEVERE, null, ex);
            val = "";
        }

        return val;
    }

     @Override
    public String put(String k, String v) {
        String val = "";
        try {
            val = super.put(k, encrypt(v));
        }catch(Exception e){
            e.printStackTrace();
        }
        return val;
    }
    
    /**
     * Initialize le crypteur / décrypteur
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws InvalidAlgorithmParameterException
     */
    protected final void initCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException{
        reader = Cipher.getInstance("AES/CBC/PKCS5Padding");
        writer = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        
        writer.init(Cipher.ENCRYPT_MODE, key);
        iv = writer.getParameters().getParameterSpec(IvParameterSpec.class);
        
        reader.init(Cipher.DECRYPT_MODE, key, iv);
       
    }
    
    /**
     * Initialize le crypteur / décrypteur avec une pass-phrase
     * @param init Pass-phrase d'initialisation
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws InvalidAlgorithmParameterException
     */
    protected final void initCiphers(String init) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, InvalidAlgorithmParameterException{
        reader = Cipher.getInstance("AES/CBC/PKCS5Padding");
        writer = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        KeyGenerator keygen = KeyGenerator.getInstance("AES");
        keygen.init(128);
        SecretKey key = keygen.generateKey();
        
        writer.init(Cipher.ENCRYPT_MODE, key, new SecureRandom(init.getBytes()));
        iv = writer.getParameters().getParameterSpec(IvParameterSpec.class);
        
        reader.init(Cipher.DECRYPT_MODE, key, iv);
       
    }
    
    /**
     * Décrypte une entrée
     * @param in Entrée cryptée
     * @return Entrée décryptée
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private String decrypt(String in) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        return new String(reader.doFinal(base64.decode(in.getBytes())));
    }
    
    /**
     * Crypte une entrée
     * @param in Entrée en clair
     * @return Entrée cryptée
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private String encrypt(String in) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        return new String(base64.encode(writer.doFinal(in.getBytes())));
    }
}
