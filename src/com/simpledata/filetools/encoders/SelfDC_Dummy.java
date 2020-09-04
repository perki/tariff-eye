/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SelfDC_Dummy.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.simpledata.filetools.SecuSelf;

/**
 * Dummy Converter (does nothing .. mainly for testing purposes)
 */
public class SelfDC_Dummy implements SelfDConverter {
    
    /** tool for many encoders that do not need to add decrypt infos **/
    public static byte[] getSimpleID(byte id) throws IOException{
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        bais.write(id);
        ((new DataOutputStream(bais))).writeInt(0);
        return bais.toByteArray();
    }
    
    /** tool for many encoders that may add some bytes to the stream**/
    public static byte[] getIDPlusBytes(byte id,byte[] data) throws IOException{
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        bais.write(id);
        ((new DataOutputStream(bais))).writeInt(data.length);
        bais.write(data);
        return bais.toByteArray();
    }
    
    /**
     * @see com.simpledata.filetools.encoders.SelfD#getID()
     */
    public byte[] getID() throws IOException {
      return getSimpleID(SecuSelf.C_DUMMY);
    }

    /**
     * @see SelfD#setDestination(OutputStream)
     */
    public OutputStream setDestination(OutputStream destination) 
    	throws IOException { 
        return destination;
    }
    
    /**
     * return a Decoder stream
     */
    public static InputStream getDecoder(InputStream source)
    	throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // fill up baos with the source
        byte b[] = new byte[1024];
        int l;
        while ((l = source.read(b)) >= 0) {
            baos.write(b,0,l);
        }
        System.out.println(baos.toString());
        
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
