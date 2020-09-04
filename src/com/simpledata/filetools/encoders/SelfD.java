/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SelfD.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools.encoders;

import java.io.IOException;
import java.io.OutputStream;

import com.simpledata.filetools.SimpleException;

/**
 * Stream reader / converter
 */
public interface SelfD {
    
    
    public interface DecodeFlow {
        
        public Object getParams(Object key);
        /** 
         * happen when something cannot be decoded <BR>
         * @param at (-1 for head) (1 for body) 0 for undef
         **/
        public void cannotDecode(int at,byte id,String messsage);
        
        /** 
         * errors that should be treated as warning <BR>
         * @param at (-1 for head) (1 for body) 0 for undef
         **/
        public void decodeWarning(int at,byte id,String messsage,Exception e);
        
        /** 
         * errors that should be treated as fatal <BR>
         * @param at (-1 for head) (1 for body) 0 for undef
         * @throws SimpleException
         **/
        public void decodeFatal(int at,byte id,String messsage,Exception e) 
        throws SimpleException;
    }

    /** 
     * return [byte:UNIQUE ID][int:length of info check][Decode info check]<BR> 
     * Some stream may write some informations into the TOC that describe how
     * they may be decrypted
     * **/
    public byte[] getID()  throws IOException;
    
    /** 
     * set the Destination of this Stream<BR> 
     * And return this one.<BR>
     * TERMINAL Stream MUST return null
     * **/
    public OutputStream setDestination(OutputStream destination) 
    throws SimpleException, IOException ;
    
    
}
