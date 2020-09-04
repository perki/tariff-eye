/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SelfDC_DES.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.simpledata.filetools.SecuSelf;
import com.simpledata.filetools.SimpleException;
import com.simpledata.util.LicenseUtils;

/**
 * DES Encyprion
 */
public class SelfDC_DES implements SelfDConverter {
    
    public static final String PARAM_KEY = "DES_KEY";
    
    
    private String key;
    /** @param key an 8 characters String **/
    public SelfDC_DES(String key) {
        this.key = key;
    }
    
    /**
     * @see SelfD#getID()
     */
    public byte[] getID() throws IOException {
        return SelfDC_Dummy.getIDPlusBytes(SecuSelf.C_DES,getSign(key));
    }
    
    /**
     * getSignature
     */
    private static byte[] getSign(String key) {
        return LicenseUtils.mdSimple(key,16).getBytes();
    }
    
    /** @param ivps null when decyrpting */
    private static Cipher getCipher(IvParameterSpec ivps,String key) 
    throws SimpleException
    {
        try {
	        // create a key
	        DESKeySpec desKeySpec;
	        desKeySpec = new DESKeySpec( key.getBytes());
	        
	        SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("DES");
	        SecretKey desKey= keyFactory.generateSecret(desKeySpec);
	        
	        // use Data Encryption Standard
	        Cipher des= Cipher.getInstance("DES/CBC/PKCS5Padding");
	        
	        if (ivps != null) { // decrypt
	            des.init(Cipher.DECRYPT_MODE,  desKey,ivps);
	        } else { // encrypt
	            des.init(Cipher.ENCRYPT_MODE,desKey);
	        }
	        
	        
	        return des;
        } catch (InvalidKeyException e) {
            throw new SimpleException(200, e);
        } catch (NoSuchAlgorithmException e) {
            throw new SimpleException(200, e);
        } catch (InvalidKeySpecException e) {
            throw new SimpleException(200, e);
        } catch (NoSuchPaddingException e) {
            throw new SimpleException(200, e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new SimpleException(200, e);
        }
    }
    
    /**
     * @see SelfD#setDestination(java.io.OutputStream)
     */
    public OutputStream setDestination(OutputStream destination) 
    throws SimpleException, IOException {
        DataOutputStream dout = new DataOutputStream(destination);
        Cipher des = getCipher(null,key);
        
        //      write the initialization vector onto the output
		byte[] iv= des.getIV();

		dout.writeInt(iv.length);
		dout.write(iv);
        return new CipherOutputStream(destination, des);
    }
    
    /**
     * return true if I can decode a those informations and those params<BR>
     * NOT EFFECTIVE WITH V0 of table of content<BR>
     * 
     */
    public static boolean 
    	canDecode(SelfD.DecodeFlow params,byte[] sparams,int at) {
        // check parameters
        Object key = params.getParams(PARAM_KEY);
        if (key == null 
                || !(key instanceof String) 
                || ((String) key).length() != 8) {
            params.cannotDecode(at,SecuSelf.C_DES,
                    "DES KEY not found in parameters");
            return false;
        }
            
        if (sparams.length == 0) {// cannot check .. will fail later
            return true;
        }
        
        // sparams contains md5 summed DES KEY
        byte d[] = getSign(""+key);
        
        if (sparams.length != d.length) {
            params.cannotDecode(at,SecuSelf.C_DES,
            "DES KEY not valid");
            return false;
        }
        
        for (int i=0;i<sparams.length;i++) {
            if (sparams[i] != d[i]) {
                params.cannotDecode(at,SecuSelf.C_DES,
                "DES KEY not valid");
                return false;
            }
        }
            
        return true;
    }
    
    /**
     * return a Decoder stream<BR>
     * <B>requires SelfDC_DES.PARAM_KEY : in the DecodeFlow parameters</B>
     */
    public static InputStream getDecoder(InputStream source,
            SelfD.DecodeFlow params,byte[] sparams) 
    	throws IOException,SimpleException {
        // check parameters
        
        if (! canDecode(params,sparams,0))
            throw new SimpleException(SimpleException.cannotDecrypt,
            	"Invalid Parameters for DES Encryption");
        
        Object key = params.getParams(PARAM_KEY);
        
        DataInputStream din = new DataInputStream(source);
        //      Read the initialization vector
		int ivSize= din.readInt();
		byte[] iv= new byte[ivSize];
		din.readFully(iv);
		IvParameterSpec ivps= new IvParameterSpec(iv);
		
        return new CipherInputStream(source, 
                getCipher(ivps,""+key));
    }
    
}
