/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: BCOption.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/

package com.simpledata.bc.datamodel;

import java.util.Iterator;
import java.util.ArrayList;

import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.event.NamedEvent;

/**
* An option is a container for a question
*/
public abstract class BCOption extends Named {
	
		/** Unique String to identify this Option Type **/
		public static final  String OPTION_TITLE = "UNKNOWN";
	
		/** RealClass type **/
		public final static String CLASS_TYPE = "OPTION";
		
		/** the states of the options **/
		public static final int STATE_OK = 1;
		public static final int STATE_INVALID = 2;
		public static final int STATE_TO_BE_MODIFIED = 3;
		
		
		/** is this option hidden **/
		private boolean xHidden;
		
		/** return true if this Option should be hidden **/
		public boolean isHidden() { return xHidden ; }
		
		/** set if this option should be hidden **/
		public void setHidden(boolean b) {
			xHidden = b;
			fireNamedEvent(NamedEvent.OPTION_DATA_CHANGED);
			// no need to call fireDataChange here because no new calculus
			// will be started
		}
		
		/** get the state of this BCOption 
		 * @return one of  BCOption.STATE_*
		 */
		public int getStatus() {
			// this may be extended for more global state management
			// ie .. if an HIDDEN state should be mixed with
			return getStatusPrivate();
		}
		
		/** return one of the BCOption.STATE_* **/
		protected abstract int getStatusPrivate();
		
		/**
		* Constructor.. Do not use !<BR>
		* Use subclasses constructor or  OptionManager.createOption()
		* @see com.simpledata.bc.components.bcoption.OptionManager#createOption(WorkSheet ws, Class c)
		*/
		protected BCOption(WorkSheet ws,String title) {
			super(CLASS_TYPE,ws.getTarification(),title);
			// register this Option to the Tarification
			ws.addOption(this);
		}
		
		/**
		 * Fire any data change!!<BR>
		 * This will start a new calculus
		 */
		public void fireDataChange() {
			fireDataChange(null);
		}
		
		/**
		 * Fire any data change!!<BR>
		 * This one is linked to a CalculusId (can be null)
		 */
		public void fireDataChange(ComCalculus cc) {
			// tell all workSheet that register me to recalculate
			Iterator e = getWorkSheets().iterator();
			while (e.hasNext()) 
				((WorkSheet) e.next()).optionDataChanged(this,cc);
			
			fireNamedEvent(NamedEvent.OPTION_DATA_CHANGED);
		}
		
		/**
		* get the Worksheets that register this option
		*/
		public ArrayList getWorkSheets() {
			return getTarification().optionsLinks.getRightOf(this);
		}
		
		//---------------- XML ---------------//
		/** XML **/
		public BCOption() {
		}
		
		/** XML **/
		public boolean isXHidden() {
			return xHidden;
		}
		/** XML **/
		public void setXHidden(boolean hidden) {
			xHidden = hidden;
		}
}

/* $Log: BCOption.java,v $
/* Revision 1.2  2007/04/02 17:04:23  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:36  perki
/* First commit on sourceforge
/*
/* Revision 1.21  2004/08/02 07:59:52  kaspar
/* ! CVS merge
/*
/* Revision 1.20  2004/07/08 14:58:59  perki
/* Vectors to ArrayList
/*
/* Revision 1.19  2004/05/20 09:39:43  perki
/* *** empty log message ***
/*
/* Revision 1.18  2004/04/12 17:34:52  perki
/* *** empty log message ***
/*
/* Revision 1.17  2004/04/12 12:30:28  perki
/* Calculus
/*
/* Revision 1.16  2004/03/23 19:45:18  perki
/* New Calculus Model
/*
/* Revision 1.15  2004/03/18 15:43:33  perki
/* new option model
/*
/* Revision 1.14  2004/03/17 14:28:53  perki
/* *** empty log message ***
/*
/* Revision 1.13  2004/02/22 18:09:20  perki
/* good night
/*
* Revision 1.12  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.11  2004/02/19 19:47:34  perki
* The dream is coming true
*
* Revision 1.10  2004/02/05 09:58:11  perki
* Transactions are welcome aboard
*
* Revision 1.9  2004/02/04 11:11:35  perki
* *** empty log message ***
*
* Revision 1.8  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.7  2004/01/23 17:30:59  perki
* *** empty log message ***
*
* Revision 1.6  2004/01/18 15:21:18  perki
* named and jdoc debugging
*/