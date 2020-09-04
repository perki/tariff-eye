/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: WorkSheetContainer.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/

package com.simpledata.bc.datamodel;

import java.util.ArrayList;

import com.simpledata.bc.datamodel.calculus.ComModifier;

/**
* Interface for WorkSheet Containers.<BR>
* Tarif and Dispatcher implements this interface.
*/
public interface WorkSheetContainer extends ComModifier {
	/**
	 * NEVER CALL THIS<BR> WorkSheet.setParent() is the UNIQUE WAY TO SET 
	 * THE POSITION OF A WS;
	* set a workSheet with a specified key<BR>
	* @param ws te WorkSheet to attach
	* @param key is used by some containers with multiple WorkSheet to specify WHERE to attach the a WorkSheet 
	* @return true if succeded.. (ie new WorkSheet Accepted)
	*/
	public boolean setWorkSheet(WorkSheet ws,String key);
	
	/**
	* WorkSheet relative to this key<BR>
	*/
	public WorkSheet getWorkSheetAt(String key);
	
	/**
	* get the key relative to this WorkSheet<BR>
	*/
	public String getWorkSheetKey(WorkSheet ws);
	
	/**
	* get the list of WorkSheets attached to this Container
	* The ArrayList MUST contains only WorkSheets
	*/
	public ArrayList getChildWorkSheets();
	
	/**
	* get the underlaying Tarif<BR>
	*/
	public Tarif getTarif();
	
	/**
	 * return the list of WorkSheet type that can be created at this place
	 * @param key is used by some containers with multiple WorkSheet to specify WHERE to attach the a WorkSheet 
	 * @return null to indicates ANY. and Class[0] for NONE
	 */
	public Class[] getAcceptedNewWorkSheets(String key);
	
	/**
	 * get if this Tarif accept this WorkSheet for this key<BR>
	 * @param key is used by some containers with multiple WorkSheet to specify WHERE to attach the a WorkSheet 
	 */
	public boolean acceptsNewWorkSheet(Class c,String key);
			
}
/* $Log: WorkSheetContainer.java,v $
/* Revision 1.2  2007/04/02 17:04:23  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:36  perki
/* First commit on sourceforge
/*
/* Revision 1.10  2004/11/15 18:41:24  perki
/* Introduction to inserts
/*
/* Revision 1.9  2004/09/08 16:35:14  perki
/* New Calculus System
/*
/* Revision 1.8  2004/07/08 14:59:00  perki
/* Vectors to ArrayList
/*
/* Revision 1.7  2004/03/23 18:02:18  perki
/* New WorkSHeet Panel model
/*
* Revision 1.6  2004/03/04 14:32:07  perki
* copy goes to hollywood
*
* Revision 1.5  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.4  2004/02/17 11:39:21  perki
* zobi la mouche n'a pas de bouche
*
* Revision 1.3  2004/02/06 10:04:22  perki
* Lots of cleaning
*
* Revision 1.2  2004/02/05 15:11:39  perki
* Zigouuuuuuuuuuuuuu
*
* Revision 1.1  2004/01/20 08:37:10  perki
* Better WorkSheetContainer design
*
*/