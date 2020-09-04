/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SelfDT_SerializableXMLArmored.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.simpledata.filetools.SecuSelf;
import com.simpledata.filetools.SimpleException;

/**
 * Save data both as Serializable and XMLEncoded..<BR>
 * This will enable FAST loading of files.. If De-Serialization fails
 * then it will use the XML encoded data.
 */
public class SelfDT_SerializableXMLArmored implements SelfDTerminal {

    private SelfDT_Serializable serializer;
    private SelfDT_XMLEncoder xmlizer;
    
    private static final byte[] BOUNDARY = new byte[1024];
    static {
           for (int i = 0; i < BOUNDARY.length; i++) {
               BOUNDARY[i] = (byte) i;
           }
    }
    
    public SelfDT_SerializableXMLArmored(Serializable data) {
        serializer = new SelfDT_Serializable(data,false);
        xmlizer = new SelfDT_XMLEncoder(data);
    }
    
    /**
     * @see SelfD#getID()
     */
    public byte[] getID() throws IOException {
        return SelfDC_Dummy.getSimpleID(SecuSelf.T_SERIALIZABLE_XMLARMORED);
    }

    /**
     * @see SelfD#setDestination(OutputStream)
     */
    public OutputStream setDestination(OutputStream destination)
            throws SimpleException, IOException {
        OutputStream os = serializer.setDestination(destination);
        destination.write(BOUNDARY);
        xmlizer.setDestination(destination);
        os.close();
        
      
        
        return null;
    }
    /**
     * return an Object from this Stream
     */
    public static Object getObject(InputStream source,SelfD.DecodeFlow sd) 
    	throws IOException , SimpleException {
        try {
            return SelfDT_Serializable.getObject(source);
        } catch (Exception e) {
            // ignore exceptions
        }
        // failed.. we must find the boundary
        
        int count = 0;
        byte[] buff = new byte[1];
        while (count < BOUNDARY.length && source.read(buff) > 0) {
            if (buff[0] == BOUNDARY[count]) count++;
        }

        return SelfDT_XMLEncoder.getObject(source,sd);
    }
}
