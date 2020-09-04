/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Install4JTools.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.install;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;

/**
 * Contains tools relatives to install4j installer.<BR>
 * Retreive a license file from a special directory created by install4j
 */
public class Install4JTools {
    public final static Logger m_log = Logger.getLogger(Install4JTools.class);
    
    
    /**
     * the name any license file will start with: 
     * (for licenses file in the user pack)
     */
    private static final String LICENSE_BASE = "license";
   
    
    public interface LicenseStream {
        public long length();
        public InputStream is();
    }
   
    /**
     * get a stream with the license file<BR>
     * @return null if failed
     */
    public static LicenseStream streamToLicenseFile() {
        File pack = new File(Resources.licenseJar());
        if (! pack.exists()) {
            m_log.error("Cannot find the install4j file: ["+pack+"]");
            return null;
        }
        
        ZipFile zf;
        try {
            zf = new ZipFile(pack,ZipFile.OPEN_READ);
        } catch (IOException e) {
            m_log.error("Cannot open ["+pack+"] ",e);
            return null;
        }
        
        
        
        // search for a file starting with "BClicense" in the package
        ZipEntry entry = null;
        for (Enumeration/*<ZipEntry>*/ e = zf.entries(); 
        		e.hasMoreElements() && entry == null;) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            m_log.debug("ZIP: "+ze.getName());
            if (ze.getName().startsWith(LICENSE_BASE)) { // found
                entry = ze;
            }
        }
       
       
        ByteArrayOutputStream out = null;
        boolean ok = true;
        if (entry != null)  {
	        
	        
	        InputStream in = null;
	        try {
	            out = new ByteArrayOutputStream();
	            in = zf.getInputStream(entry);
	            byte[] buffer = new byte[1024];
	            int    bytesRead;
	            while ((bytesRead = in.read(buffer)) != -1 ) {
	                out.write(buffer,0,bytesRead);
	            }
	           
	        } catch (FileNotFoundException e2) {
	            ok = false;
	        } catch (IOException e2) {
	            ok = false;
	        }
        
	        try { in.close(); } catch (IOException e1) { }
        } else {
            ok = false;
            m_log.error(LICENSE_BASE+"* not found in ["+pack+"]");
        }
        
        try { zf.close(); } catch (IOException e1) { }
        
        
        if (! ok || out == null)
            return null;
        
        final ByteArrayInputStream bais
        	=	new ByteArrayInputStream(out.toByteArray());
        final long length = out.size();
        
        return new LicenseStream() {
            public long length() { return length; }
            public InputStream is() {  return bais; }
        };
    }
    
    /**
     * get the default license, form the install4j user package 
     * and copy it into .bcdata/license.dat
     * @param destination should be ".bcdata/license.dat"
     * @return true if succeded
     */
    public static boolean installLicenseFile(File destination) {
        LicenseStream ls = streamToLicenseFile();
        if (ls == null) return false;
        
        boolean ok = true;
        
        // create necessary dirs
        destination.getParentFile().mkdirs();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int    bytesRead;
            while ((bytesRead = ls.is().read(buffer)) != -1 ) {
                out.write(buffer,0,bytesRead);
            }
        } catch (FileNotFoundException e2) {
            m_log.error("Cannot OPEN/WRITE to ["+destination+"]");
            ok = false;
        } catch (IOException e2) {
            m_log.error("Cannot OPEN/WRITE to ["+destination+"]");
            ok = false;
        }
        
        try { out.close(); } catch (IOException e1) { }
        
        return ok;
    }
    
}
