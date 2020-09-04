/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

/*
 * $Id: SelfDC_GZIP.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.simpledata.filetools.SecuSelf;

/**
 * Compressor STream
 */
public class SelfDC_GZIP implements SelfDConverter {

    /**
     * @see SelfD#getID()
     */
    public byte[] getID() throws IOException {
        return SelfDC_Dummy.getSimpleID(SecuSelf.C_GZIP);
    }

    /**
     * @see SelfD#setDestination(java.io.OutputStream)
     */
    public OutputStream setDestination(OutputStream destination) 
    throws IOException {
        return new GZIPOutputStream(destination);
    }
    
    
    /**
     * return a Decoder stream
     */
    public static InputStream getDecoder(InputStream source) 
    	throws IOException {
        return new GZIPInputStream(source);
    }
}
