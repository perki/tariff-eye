/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: WorkSheetManager.java,v 1.2 2007/04/02 17:04:30 perki Exp $
*/
package com.simpledata.bc.components.worksheet;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.dispatcher.DispatcherBounds;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherCase;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherRoot;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSequencer;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSimple;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFixedFee;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateBySliceOnAmount;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateOnAmount;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceSimple;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datatools.ComponentManager;
import com.simpledata.bc.tools.Lang;

/**
* maybe not the best idea.. I had.<BR>
* This class is designed to give a list of usables 
* Dispatcher and WorkPlaces in a Tarif context.<BR>
*
*/
public class WorkSheetManager {
	
	private static final Logger m_log = Logger.getLogger( WorkSheetManager.class ); 
	
	public static final Class[] USER_INVISIBLE_CLASSES = { WorkPlaceSimple.class ,
	        EmptyWorkSheet.class , WorkPlaceTransferOptions.class,
	        DispatcherSimple.class };
	
	/** 
	 * this does contains the Dispatchers normaly accepted by all Tarifs	 
	 * * @return Classes of dispatchers. 
	 */
	public static Class[] defaultsDispatchers () {
		return new Class[] {
			EmptyWorkSheet.class,
			DispatcherSequencer.class,
			DispatcherSimple.class,
			DispatcherBounds.class,
			//DispatcherIf.class,
			DispatcherCase.class};
	}

	/** 
	 * this does contains the WorkPlaces normlay accepted by all Tarifs
	 */
	public static Class[] defaultsWorkplaces () {
		return new Class[] { WorkPlaceFixedFee.class,
						WorkPlaceWithOnlyOptions.class ,
						WorkPlaceRateOnAmount.class,
						WorkPlaceRateBySliceOnAmount.class,
						WorkPlaceSimple.class };
	}

	/** tools that return all defaults worksheets 
	 * (defaultsWorkPlaces + defaultsWorkSheets**/
	public static Class[] defaultsWorksheets () {
		return sumClassArray(defaultsDispatchers(), defaultsWorkplaces());
	}
	
	


	/** tool that add to Arrays into on **/
	public static Class[] sumClassArray(Class[] a1, Class[] a2) {
		Class res[]= new Class[a1.length + a2.length];
		for (int i= 0; i < a1.length; i++)
			res[i]= a1[i];
		for (int i= 0; i < a2.length; i++)
			res[i + a1.length]= a2[i];
		return res;
	}

	/**
	* get translationKey for the title of this WorkSheet
	*/
	public static String getWorkSheetTitle(Class c) {
		if (c == null) {
			m_log.error( "Class c is null" );
			return null;
		}
		String s= null;
		try {
			s= (String) c.getField("WORKSHEET_TITLE").get(new String());
		} catch (NoSuchFieldException e) {

			m_log.error( "WorkSheetManager: NoSuchFieldException", e );
		} catch (IllegalAccessException e) {
			m_log.error( "WorkSheetManager: IllegalAccessException", e );
		}
		return s;
	}
	
	/**
	 *  tools that return all the dispatchers that implements 
	 * Dispatcher.SupportInsertion <BR>
	 *Will return false if ws is a DispatcherRoot
	 *<BR>
	 * get the insertable Dispatcher accepted by this Dispatcher
	 */
	public static final Class[] insertableDispatchers(WorkSheet ws) {
	
	    WorkSheetContainer parent = ws.getWscontainer();
	    
	    if (ws instanceof DispatcherRoot ) 
	        return new Class[0];
	    
	    if (parent == null) {
		    m_log.error("Cannot insert on WS with null parent",new Exception());
		    return new Class[0];
		}
	    
	    String key = parent.getWorkSheetKey(ws);
	    Class ds[]=parent.getAcceptedNewWorkSheets(key);
	    if (ds == null) {
	        ds = WorkSheetManager.defaultsWorksheets();
	    }
	    ArrayList al = new ArrayList();
	    for (int i = 0; i < ds.length ; i++) {
	        if (Dispatcher.SupportInsertion.class.isAssignableFrom(ds[i])) {
	            al.add(ds[i]);
	        }
	    }
	    return (Class[]) al.toArray(new Class[0]);
	}
	
	/**
	* Insert this dispatcher class as a parent for this WorkSheet at key : ""
	* @c must be a subclass of Dispatcher & Dispactcher.SupportInsertion
	*/
	public static final void insertDispatcher(WorkSheet ws,Class c) { 
		WorkSheetContainer parent = ws.getWscontainer();
		if (parent == null) {
		    m_log.error("Cannot insert on WS with null parent",new Exception());
		    return;
		}
		
		if ((! Dispatcher.class.isAssignableFrom(c)) 
		        || (! Dispatcher.SupportInsertion.class.isAssignableFrom(c)) ) {
		    m_log.error("Only Dispatchers may be inserted :"+c);
		    return;
		}
		
		String key = parent.getWorkSheetKey(ws);
		WorkSheet newD = createWorkSheet(parent,c,key);
		if (newD == null) { 
		    m_log.error("Failed: trying to rollback",new Exception());
		    ws.setParent(parent,key);
		    return;
		}
		ws.setParent((Dispatcher) newD,
		        ((Dispatcher.SupportInsertion) newD).getInsertionKey());
	}

	/**
	* create a WorkSheet of the specified Class 
	* @param wc a Tarif or a Dispatcher
	* @param c is one of the Class[] obtained with #getWorkPlaces(Tarif t) or #getDispatcherFor(Tarif t)
	* @param key used for Dispatcher to specifiy which WorkSheet it's
	* @return the create workSheet null if failed
	*/
	public static WorkSheet createWorkSheet(
		WorkSheetContainer wc,
		Class c,
		String key) {
		
		assert wc != null : "WorkSheetContainer cannot be null";
		
		WorkSheet ws= _createWorkSheet(wc, c, key);
		if (ws == null) {
			Class[] cs = wc.getAcceptedNewWorkSheets(key);
			StringBuffer sb = new StringBuffer();
			if (cs != null) {
				for (int i = 0; i < cs.length; i++)
					sb.append(", ").append(cs[i]);
			}
			m_log.error( "failed creation of a "
					+ c 
					+ " on container:"
					+ wc
					+ " with key"
					+ key
					+ "\n"
					+ "Accepted Classes are:"+sb );
		}
		return ws;
	}

	private static WorkSheet _createWorkSheet(
		WorkSheetContainer wc,
		Class c,
		String key) {
		if (key == null) { // null key.. not a problem but need to be not null
			key= "";
		}
			
		
		// check if this Container accepts this WorkSheet
		if (!wc.acceptsNewWorkSheet(c, key))
			return null;

		// create parameters Array
		Object[] initArgs= new Object[4];
		initArgs[0]= wc;
		initArgs[1]= Lang.translate(getWorkSheetTitle(c));
		initArgs[2]= "";
		initArgs[3]=key;
		
		// create parameters class type to get the right constructor
		Class[] paramsType= ComponentManager.getClassArray(initArgs);
		// stupid hack because of stupid java!
		paramsType[0]= WorkSheetContainer.class;

		WorkSheet ws=
			(WorkSheet) ComponentManager.getInstanceOf(c, paramsType, initArgs);
		if (ws == null)
			return null; // failed
		return ws;
	}
	
	/** 
	 * create a dummy WorkSHeet for this Tarification<BR>
	 * mainly used by copy()
	 * **/
	public static WorkSheetContainer createDummyWorkSheet(Tarification t) {
		return new DummyWorkSheetContainer(t);
	}
	
	/**
	 * return true if this WorkSheet is "floating" means not attached to the
	 * tarification
	 */
	public static boolean isFloating(WorkSheet ws) {
		if (ws.getTarif() == null) return true;
		if (ws.getWscontainer() instanceof DummyWorkSheetContainer) return true;
		return false;
	}

}



/** $Log: WorkSheetManager.java,v $
/** Revision 1.2  2007/04/02 17:04:30  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:44  perki
/** First commit on sourceforge
/**
/** Revision 1.47  2005/01/26 14:36:28  jvaucher
/** Issue#73: Copy bug yields the soft to be unable to save with XML encoder.
/** Added a null constructor to DummyWorkSheetContainer to bypass the problem
/**
/** Revision 1.46  2004/11/17 09:09:49  perki
/** first step of discount extraction
/**
/** Revision 1.45  2004/11/16 10:36:51  perki
/** Corrig� bug #11
/**
/** Revision 1.44  2004/11/15 18:54:04  carlito
/** DispatcherIf removed... workSheet and option cleaning...
/**
/** Revision 1.43  2004/11/15 18:41:24  perki
/** Introduction to inserts
/**
/** Revision 1.42  2004/09/23 09:23:41  carlito
/** WorkSheets hidden in popup
TransferOption button activated..
/**
/** Revision 1.41  2004/09/22 17:35:41  carlito
/** Added list of invisibles to WorkSheetManager--->
/**
/** Revision 1.40  2004/09/14 15:30:53  jvaucher
/** Minor locale changes
/**
/** Revision 1.39  2004/09/09 18:38:46  perki
/** Rate by slice on amount are welcome aboard
/**
/** Revision 1.38  2004/09/08 16:35:14  perki
/** New Calculus System
/**
/** Revision 1.37  2004/09/07 16:21:03  jvaucher
/** - Implemented the DispatcherBounds to resolve the feature request #24
/** The calculus on this dispatcher is not yet implemented
/** - Review the feature of auto select at startup for th SNumField
/**
/** Revision 1.36  2004/09/03 11:47:53  kaspar
/** ! Log.out -> log4j first half
/**
/** Revision 1.35  2004/08/17 11:45:59  kaspar
/** ! Decoupled visitor architecture from datamodel. No illegal
/**   dependencies left, hopefully
/**
/** Revision 1.34  2004/07/08 14:58:59  perki
/** Vectors to ArrayList
/**
/** Revision 1.33  2004/06/16 10:17:00  carlito
/** *** empty log message ***
/**
/** Revision 1.32  2004/05/18 19:09:47  carlito
/** *** empty log message ***
/**
/** Revision 1.31  2004/05/18 13:49:46  perki
/** Better copy / paste
/**
/** Revision 1.30  2004/05/14 08:46:18  perki
/** *** empty log message ***
/**
/** Revision 1.29  2004/05/05 12:38:13  perki
/** Plus FixedFee panel
/**
/** Revision 1.28  2004/03/04 14:32:07  perki
/** copy goes to hollywood
/**
/** Revision 1.27  2004/03/02 14:42:48  perki
/** breizh cola. le cola du phare ouest
/**
/** Revision 1.26  2004/02/26 13:24:34  perki
/** new componenents
/**
/** Revision 1.25  2004/02/22 18:09:20  perki
/** good night
/**
/** Revision 1.24  2004/02/20 03:14:06  perki
/** appris un truc
/**
/** Revision 1.23  2004/02/19 23:57:25  perki
/** now 1Gig of ram
/**
/** Revision 1.22  2004/02/18 16:59:29  perki
/** turlututu
/**
/** Revision 1.21  2004/02/17 11:39:21  perki
/** zobi la mouche n'a pas de bouche
/**
/** Revision 1.20  2004/02/16 18:59:15  perki
/** bouarf
/**
/** Revision 1.19  2004/02/06 10:04:22  perki
/** Lots of cleaning
/**
/** Revision 1.18  2004/02/06 07:44:55  perki
/** lot of cleaning in UIs
/**
/** Revision 1.17  2004/02/05 15:11:39  perki
/** Zigouuuuuuuuuuuuuu
/**
/** Revision 1.16  2004/02/05 07:45:52  perki
/** *** empty log message ***
/**
/** Revision 1.15  2004/02/04 15:42:16  perki
/** cleaning
/**
* Revision 1.14  2004/02/02 07:00:50  perki
* sevral code cleaning
*
* Revision 1.13  2004/02/01 18:27:51  perki
* dimmanche soir
*
* Revision 1.12  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.11  2004/01/30 15:18:12  perki
* *** empty log message ***
*
* Revision 1.10  2004/01/29 13:40:40  perki
* *** empty log message ***
*
* Revision 1.9  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.8  2004/01/22 15:40:51  perki
* Bouarf
*
* Revision 1.7  2004/01/22 13:03:31  perki
* *** empty log message ***
*
* Revision 1.6  2004/01/20 11:17:31  perki
* A la recherche de la Foi
*
* Revision 1.5  2004/01/20 08:58:36  perki
* Zorglub vaincra............
*
* Revision 1.4  2004/01/20 08:37:10  perki
* Better WorkSheetContainer design
*
* Revision 1.3  2004/01/19 19:20:46  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/19 17:04:29  perki
* pfiuuu
*
* Revision 1.1  2004/01/19 17:02:05  perki
* *** empty log message ***
*
* Revision 1.3  2004/01/19 10:07:50  perki
* Yehahh
*
* Revision 1.2  2004/01/19 09:45:34  perki
* WorkPlace creation done
*
* Revision 1.1  2004/01/18 18:44:11  perki
* *** empty log message ***
*
*/
