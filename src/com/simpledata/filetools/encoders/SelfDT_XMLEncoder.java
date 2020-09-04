/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SelfDT_XMLEncoder.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.filetools.SecuSelf;
import com.simpledata.filetools.SimpleException;

/**
 * Producte XML Encoding Streams using XMLEncoder
 */
public class SelfDT_XMLEncoder implements SelfDTerminal {

    Object myData;
     
    public SelfDT_XMLEncoder(Object data) {
        myData = data;
    }
    

    /**
     * @see SelfD#getID()
     */
    public byte[] getID() throws IOException {
        return SelfDC_Dummy.getSimpleID(SecuSelf.T_XMLENCODER);
    }

    /**
     * @see SelfD#setDestination(java.io.OutputStream)
     * @return  always return null
     */
    public OutputStream 
    	setDestination(OutputStream destination) 
    	throws IOException, SimpleException {
        XMLEncoder oos = new XMLEncoder(destination);
        XMLExceptionListener el = new XMLExceptionListener();
        oos.setExceptionListener(el);
        oos.writeObject(myData);
        oos.close();
        for (Iterator i = el.errors.iterator(); i.hasNext();)
            throw new SimpleException(0,(Exception) i.next());
        return null;
    }
    
    /**
     * return an Object from this Stream
     */
    public static Object getObject(InputStream source,SelfD.DecodeFlow sd) {
    
        
        XMLDecoder ois = new XMLDecoder(source);
        XMLExceptionListener el = new XMLExceptionListener(source);
        ois.setExceptionListener(el);
        Object result = ois.readObject();
        
        for (Iterator i = el.errors.iterator(); i.hasNext();)
            sd.decodeWarning(0,SecuSelf.T_XMLENCODER,"XML WARNING GETOBJECT",
                    (Exception) i.next());
        
        return result;
       
    }
    
   
}
/** class that handle events **/
class XMLExceptionListener implements ExceptionListener {
    
    final Logger m_log = Logger.getLogger( 
            XMLExceptionListener.class );
    
    
    ArrayList errors;
    InputStream is;
    XMLExceptionListener() {
        errors = new ArrayList();
    }
    
    XMLExceptionListener(InputStream is) {
        this();
        this.is = is;
    }
   
    
	public void exceptionThrown(Exception e) {
	    errors.add(e);
	}
};