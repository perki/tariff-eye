/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SelfDT_Serializable.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import com.simpledata.filetools.SecuSelf;
import com.simpledata.filetools.SimpleException;

/**
 * Encoder that serialize Object that implements Serializables
 */
public class SelfDT_Serializable implements SelfDTerminal {
    
    private Serializable myData;
    private boolean doClose;
   
    public SelfDT_Serializable(Serializable data) {
       this(data,true);
    }
    
    /** @param doClose set to false, If you do not want this to be close 
     * automaticaly
     * @param data
     * @param doClose
     */
    protected SelfDT_Serializable(Serializable data,boolean doClose) {
        myData = data;
        this.doClose = doClose;
    }
    
    /**
     * @see SelfD#getID()
     */
    public byte[] getID() throws IOException {
        return SelfDC_Dummy.getSimpleID(SecuSelf.T_SERIALIZABLE);
    }

    /**
     * @see SelfD#setDestination(java.io.OutputStream)
     * @return  the ObjectOutputStream if you need to perform other operation
     */
    public OutputStream setDestination(OutputStream destination) 
    	throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(destination);
        oos.writeObject(myData);
        if (doClose) {
            oos.close();
        } else {
            oos.flush();
        }
        return oos;
    }
    
    
    /**
     * return an Object from this Stream
     */
    public static Object getObject(InputStream source) 
    	throws IOException , SimpleException {
        ObjectInputStream ois = new ObjectInputStream(source);
        try {
            return ois.readObject();
        } catch (InvalidClassException e) {
            throw new SimpleException(
                    SimpleException.INVALIDMODEL,e);
        } catch (ClassNotFoundException e) {
            throw new SimpleException(
                    SimpleException.INVALIDMODEL,e);
        }
    }

}
