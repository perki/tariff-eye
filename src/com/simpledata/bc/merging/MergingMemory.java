/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: MergingMemory.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.filetools.Secu;
import com.simpledata.filetools.SecuSelf;
import com.simpledata.filetools.SimpleException;
import com.simpledata.filetools.encoders.SelfDT_XMLEncoder;

/**
 * Contains all the info on mergeing that has been done.. to avoid
 * proposing again and again options for mergeing
 */
public class MergingMemory {
    
    private static final Logger m_log = Logger.getLogger( MergingMemory.class );
	
    /** set to true if need save **/
    public static boolean desync = false;
    
    public static Hashtable mm;
    
    /** save the actual mergeing memory to a file **/
    public synchronized static void save() {
        if (mm == null) return;
        if (! desync) return;
        File f = new File(Resources.mergingMemoryPath());
        SecuSelf ssdw 
        = new SecuSelf(new SelfDT_XMLEncoder(new String("DUMMY")),
                new SelfDT_XMLEncoder(mm));
   
        try {
            ssdw.commit(new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            m_log.error("Failed to save MM ",e);
        } catch (SimpleException e) {
            m_log.error("Failed to save MM ",e);
        } catch (IOException e) {
            m_log.error("Failed to save MM ",e);
        }
        m_log.warn("Saved MergingMemory :"+f);
        desync = false;
    }
    
    public synchronized static boolean exists(Tarif source,Tarif dest) {
        Vector v = (Vector) mm().get(source.getNID());
        if (v == null) return false;
        return (v.contains(dest.getNID()));
    }
    
    public synchronized static void add(Proposition p,Tarif dest) {
        Vector hm = getInit(p.tarifSource);
        if (hm.contains(dest.getNID())) return;
        
        // remove tarifs that are also known in the destination tarification
       for (int i = 0; i < p.matches.length ; i++) {
           hm.remove(p.matches[i].getTarif().getNID());
       }
        
        hm.add(dest.getNID());
        desync = true;
    }
    
    private synchronized static 
    	Vector/*<String>*/ getInit(Tarif source) {
        Vector v = (Vector) mm().get(source.getNID());
        if (v == null) {
            v = new Vector();
            mm().put(source.getNID(),v);
            desync = true;
        }
        return v;
    }
   
    
    /** get the actual merging Memory (load from file if needed) **/
    private synchronized static Hashtable mm() {
        if (mm == null) {
            File f = new File(Resources.mergingMemoryPath());
            if (f.exists()) {
	            try {
	                mm = (Hashtable) Secu.getData(f,"");
	                desync = false;
	            } catch (SimpleException e) {
	                m_log.error("Failed to load MM ",e);
	            }
	           
            }
            if (mm == null)   mm = new Hashtable();
            desync = true;
        }
        
        return mm;
    }
    
}
