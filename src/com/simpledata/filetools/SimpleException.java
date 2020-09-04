/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: SimpleException.java,v 1.2 2007/04/02 17:04:25 perki Exp $
* $Log: SimpleException.java,v $
* Revision 1.2  2007/04/02 17:04:25  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.8  2004/11/23 14:39:53  perki
* *** empty log message ***
*
* Revision 1.7  2004/10/13 16:01:56  perki
* Error compliance added to monitors
*
* Revision 1.6  2004/03/06 11:48:23  perki
* Tirelipapon sur le chiwawa
*
* Revision 1.5  2004/01/29 16:34:51  perki
* Sevral cleaning
*
* Revision 1.4  2004/01/29 13:03:04  carlito
* warnings and imports corrected ...
*
* Revision 1.3  2003/10/21 15:14:43  perki
* *** empty log message ***
*
* Revision 1.2  2003/10/10 14:14:48  perki
* *** empty log message ***
*
* Revision 1.1  2003/10/10 11:27:23  perki
* more filetools
*
* Revision 1.2  2003/09/17 15:23:20  perki
* logs
*
*
*/
package com.simpledata.filetools;

import org.apache.log4j.Logger;


/**
* Contains error codes for SimpleBC
*/
public class SimpleException extends Exception
{
    private static final Logger m_log = 
        Logger.getLogger( SimpleException.class ); 
    
	/// Error codes
	// 100 - 199 io Exception
	public static final int cannotIOSecuGetData=100;// 100 - Sec.getData : Cannot open/write/close data file
	public static final int cannotIOSecuSave=101;// 101 - Sec.save : Cannot open/write/close data file
	public static final int cannotIOSecuGetHead=102;// 102 - Sec.getHead : Cannot open data file
	
	public static final int IOException=199;//Standard IO Exception
	
	// 200 - 299 crypt Errors
	public static final int cannotDecrypt=200;// 200 - Sec.getData : Cannot Decrypt
	public static final int cannotEncrypt=201;// 201 - Sec.save : Cannot Encrypt
	
	// 300 - 399 invalid data
    public static final int INVALIDVERSION=301;
    public static final int INVALIDMODEL=302;
	public static final int WRONGDATASOURCE=303;
	
	
	
	
	
	// 201 - Sec.save : Cannot Encrypt
	private int code = 0;
	
	/**
	* Use this method to get the error value
	*/
	public int getCode () {
		return code;
	}
	
	/**
	* Constructor : print the error code and text
	*/
   public SimpleException(int c, Throwable e)
   {
	   super(e);
	   this.code = c;
	   m_log.error("SimpleException"+c+": ",e);
   }
   
   /**
	* Constructor : print the error code and text
	*/
   public SimpleException(int c, String s)
   {
	   super(s);
	   this.code = c;
	   m_log.error("SimpleException"+c+": "+s);
   }
}
