/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Log: CopyFile.java,v $
* Revision 1.2  2007/04/02 17:04:25  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* $Id: CopyFile.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/

package com.simpledata.filetools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

/**
* Util to copy files
*/
public class CopyFile {
	
	private static final Logger m_log = Logger.getLogger( CopyFile.class );
	
	public static void main (String args[]) {
		to(new File(args[0]),new File(args[1]));
	}
	
	
	
	/**
	 * Copy url src, into place of file dest
	 */
	public static boolean to(URL src, File dest)
	{
	    InputStream fis = null;
	    
	    try {
	        fis = src.openStream();
	    } catch (IOException e) {
	        m_log.error( 
	                "Error in opening" + src+
	                " does not exist!\n",e
	        );
	        return false;
	    }
	    return to(fis,dest);
	}
	
	/**
	* Copy file src, into place of file dest
	* !! Overwrite existing files !!
	*/
	public static boolean to(File src, File dest)
	{
	    FileInputStream fis = null;
	    
	    try {
	        fis = new FileInputStream(src);
	    } catch (FileNotFoundException e) {
	        m_log.error( 
	                "Error in copy" + src+
	                " does not exist!\n",e
	        );
	        return false;
	    }
	    return to(fis,dest);
	}
	
    /**
	* Copy stream is, into place of file dest
	* !! Overwrite existing files !!
	*/
	public static boolean to(InputStream fis, File dest)
	{
		
		FileOutputStream fos = null;
		dest.delete();
		 try {
			fos = new FileOutputStream(dest);
			
			byte charBuff[] = new byte[128];
			
			int length = 0;
			length = fis.read(charBuff);
			while (length != -1) {
				fos.write(charBuff,0,length);
				length = fis.read(charBuff);
			}
		}
		catch(FileNotFoundException fnfe){
			m_log.error( 
				"Error in StreamCopy" + 
				" does not exist!\n",fnfe
			);
		}
		catch(IOException ioe) {
			m_log.error(
				"Error in CopyFile Error reading/writing files!", ioe
			);
		}
		try {
			if (fos != null)
				fos.close();
			
			if (fis != null)
				fis.close();
		}
		catch(IOException ioe)
		{
				return false;
		}
		
		return true;
	}
	
	
	
}

