/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: FileUtilities.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/

package com.simpledata.filetools;

import java.io.File;

/**
* Various tools for file manipulation and informations
*/
public class FileUtilities {
	
    /**
	* Utility , return the extension of a filename
	*/
	public static String getNameWithoutExtension(String filename) {
		return getNameWithoutExtension(new File(filename));
	}
    
	/**
	* Utility , return the extension of a filename
	*/
	public static String getExtension(String filename) {
		return getExtension(new File(filename));
	}
	
	/**
	* Utility , return the name of a file.without it's extension<BR>
	* C:/bobb.toto/bobby.txt -> C:/bobb.toto/bobby
	* 
	*/
	public static String getNameWithoutExtension(File f) {
		String fn= "";
		
		// check if this file has an extension
		String s= f.getName();
		int i= s.lastIndexOf('.');
		if (i < 0) return f.getAbsolutePath();
		
		// now get the the full name
		s= f.getAbsolutePath();
		i= s.lastIndexOf('.');
		

		if (i > 0 && i < s.length() - 1) {
			fn= s.substring(0,i);
		}
		return fn;
	}
	
	/**
	* Utility , return the extension of a file.
	* (always to lowercase)
	*/
	public static String getExtension(File f) {
		String ext= "";
		String s= f.getName();
		int i= s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext= s.substring(i + 1).toLowerCase();
		}
		return ext;
	}
	/**
	* Force the extension of a file
	* Add a specified extension to a file, if actual extension is different.
	* Note: if file is "toto.txt" and Force extension is called with "html" as extension
	* the resultion file will be "toto.txt.html"
	*/
	public static File forceExtension(File file, String ext) {
		String ext2= getExtension(file);
		if (ext2 != null) {
			if (getExtension(file).equals(ext)) {
				return file;
			}
		}
		return new File(file.getAbsolutePath() + "." + ext);
	}
}
/*
* $Log: FileUtilities.java,v $
* Revision 1.2  2007/04/02 17:04:25  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.8  2004/10/14 13:44:30  perki
* *** empty log message ***
*
* Revision 1.7  2004/06/20 16:09:39  perki
* *** empty log message ***
*
* Revision 1.6  2004/06/18 18:25:23  perki
* *** empty log message ***
*
* Revision 1.5  2004/03/08 09:02:46  perki
* houba houba hop
*
* Revision 1.4  2004/03/06 11:48:23  perki
* Tirelipapon sur le chiwawa
*
* Revision 1.3  2003/10/29 08:39:57  perki
* *** empty log message ***
*
* Revision 1.2  2003/10/29 08:34:03  perki
* *** empty log message ***
*
* Revision 1.1  2003/10/29 08:03:16  perki
* File Utilities
*
*
*/
