/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Log: SimpleFileView.java,v $
* Revision 1.2  2007/04/02 17:04:25  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* $Id: SimpleFileView.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/
package com.simpledata.filetools;


import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
* Interface to implements to use the SimpleFileViewer.
* @see SimpleFileBrowser
* Allows to specify Icons and a Panel that would present informations on the selected file
*/
public interface SimpleFileView  {
	
	/**
	* return an image Icon for this FileType (can be null);
	*/
	public ImageIcon getIcon(File f);
	
	/**
	* return a list of extensions, if one of them is null then ANY file will be accepted
	*/
	public String[] getExtensions();
	
	/**
	* return the description of this fileType -- goes into the filter selection panel
	*/
	public String getDescription();
	
	/**
	* Return the file type of this file.. don't really know when it's used
	* @see #getTypeDescription(File f)
	*/
	public String getTypeDescription(File f);
	
	/**
	* Return a JPanel containing informations about this file. can return null
	*/
	public JPanel getPanel(File f);
	
	/**
	 * An extended interface of SimpleFileView with some more methods
	 */
	public interface Extended extends SimpleFileView {
		/** 
		 * return true if this file view accept to open this file 
		 * (No test are done on the extension anymore). But you can
		 * use this piece of code to reproduce normal behaviour:
		 * <PRE><CODE>
		 * if (f.isDirectory()) {
		 *		return true;
		 *	}
		 *	String extension = FileUtilities.getExtension(f);
		 *	return SimpleFileBrowser.extensionMatches(this,extension);
		 *	</CODE></PRE>
		 **/
		public boolean accept(File f);
	}
}
