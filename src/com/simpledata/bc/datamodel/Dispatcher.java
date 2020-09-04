/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: Dispatcher.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */


package com.simpledata.bc.datamodel;

/**
* Dispatcher is a type of WorkSheet linked to a Tarif
*/
public abstract class Dispatcher 
	extends WorkSheet implements WorkSheetContainer{
	
	
	/**
	* constructor.. should not be called by itself. use WorkSheet#createWorkSheet(workSheet d,Class c)
	*/
	protected Dispatcher (WorkSheetContainer parent,
	        String title,String id, String key) {
		super(parent,title,id, key); // call Named
	}
	

	/**
	 * get if this Tarif accept this WorkSheet for this key<BR>
	 */
	public final boolean acceptsNewWorkSheet(Class c,String key) {
		Class[] cs = getAcceptedNewWorkSheets("");
		if (cs == null) { return true ;}
		for (int i = 0 ; i < cs.length; i++) {
			if (cs[i] == c)
				return true;
		}
		return false;
	}
	
	
	/**
	 * return the list of accepted new worksheets.. <BR>
	 * This method may be overriden. defaults is a recursive call to parent
	 * @see WorkSheetContainer#getAcceptedNewWorkSheets(java.lang.String)
	 */
	public Class[] getAcceptedNewWorkSheets(String key) {
		// asks the container for what it accepts
		return getWscontainer().getAcceptedNewWorkSheets(key);
	}
	
	/**
	 * Dispatcher that supports to be inserted must implement this interface
	 */
	public interface SupportInsertion {
	    /** return the key to use when inserted **/
	    public String getInsertionKey();
	}
	
	//------------------ XML ENCODING -----------------//
	/** 
	 * XML Constructor 
	 */
	public Dispatcher() {
		// left empty
	}
}
/**
* $Log: Dispatcher.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:36  perki
* First commit on sourceforge
*
* Revision 1.23  2004/11/15 18:41:24  perki
* Introduction to inserts
*
* Revision 1.22  2004/08/17 11:46:00  kaspar
* ! Decoupled visitor architecture from datamodel. No illegal
*   dependencies left, hopefully
*
* Revision 1.21  2004/08/17 08:04:21  kaspar
* ! Conversion of line endings
*
* Revision 1.20  2004/07/04 10:57:45  perki
* *** empty log message ***
*
* Revision 1.19  2004/03/23 18:02:18  perki
* New WorkSHeet Panel model
*
* Revision 1.18  2004/03/04 14:32:07  perki
* copy goes to hollywood
*
* Revision 1.17  2004/02/24 13:33:48  carlito
* *** empty log message ***
*
* Revision 1.16  2004/02/23 18:46:04  perki
* *** empty log message ***
*
* Revision 1.15  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.14  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.13  2004/02/18 16:59:29  perki
* turlututu
*
* Revision 1.12  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.11  2004/02/16 11:17:10  perki
* new event model
*
* Revision 1.10  2004/02/16 10:56:41  perki
* new event model
*
* Revision 1.9  2004/02/06 10:04:22  perki
* Lots of cleaning
*
* Revision 1.8  2004/02/05 15:11:39  perki
* Zigouuuuuuuuuuuuuu
*
* Revision 1.7  2004/01/20 11:17:31  perki
* A la recherche de la Foi
*
* Revision 1.6  2004/01/20 08:58:36  perki
* Zorglub vaincra............
*
* Revision 1.5  2004/01/20 08:37:10  perki
* Better WorkSheetContainer design
*
* Revision 1.4  2004/01/19 10:07:50  perki
* Yehahh
*
* Revision 1.3  2004/01/19 09:45:34  perki
* WorkPlace creation done
*
* Revision 1.2  2004/01/18 15:21:18  perki
* named and jdoc debugging
*
* Revision 1.1  2003/12/16 17:10:42  perki
* *** empty log message ***
*
* 
*/