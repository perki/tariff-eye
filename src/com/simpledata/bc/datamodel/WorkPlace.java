/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: WorkPlace.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/

package com.simpledata.bc.datamodel;

/**
* WorkPlace is a type of WorkSheet linked to a Tarif
*/
public abstract class WorkPlace extends WorkSheet {
	
	/**
	* constructor.. should not be called by itself. use WorkSheet#createWorkSheet(Dispatcher d,Class c)
	*/
	public WorkPlace 
		(WorkSheetContainer parent,String title,String id, String key) {
		super(parent,title,id, key); // call Named
	}
	
	
	//--------- XML ----//
	/** XML **/
	public WorkPlace() {}
	
}

/**
* $Log: WorkPlace.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:36  perki
* First commit on sourceforge
*
* Revision 1.13  2004/08/17 11:46:00  kaspar
* ! Decoupled visitor architecture from datamodel. No illegal
*   dependencies left, hopefully
*
* Revision 1.12  2004/08/02 15:45:48  perki
* Repartition viewer on simulator
*
* Revision 1.11  2004/07/04 14:54:53  perki
* *** empty log message ***
*
* Revision 1.10  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.9  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.8  2004/02/19 19:47:34  perki
* The dream is coming true
*
* Revision 1.7  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.6  2004/02/01 18:27:51  perki
* dimmanche soir
*
* Revision 1.5  2004/01/20 10:29:58  perki
* The Dispatcher Force be with you my son
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
*/