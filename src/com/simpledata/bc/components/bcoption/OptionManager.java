/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: OptionManager.java,v 1.2 2007/04/02 17:04:24 perki Exp $
*/
package com.simpledata.bc.components.bcoption;

import java.util.Iterator;
import java.util.ArrayList;

import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datatools.ComponentManager;
import com.simpledata.util.CollectionsToolKit;

import org.apache.log4j.Logger;

/**
* deals with Options
*/
public class OptionManager {
	private static final Logger m_log = Logger.getLogger( OptionManager.class ); 
	
	/**
	 * Contains a list of options for Containers that accepts various options
	 */
	public static Class[] defaultsOptions() {
		Class[] res=
			new Class[] {
				OptionSimple.class,
				OptionTransaction.class,
				OptionNaturalInteger.class,
				OptionDoublePositive.class,
				OptionPerBaseTen.class,
				//OptionBoolean.class,
				OptionCase.class,
				OptionCommissionAmountUnder.class,
				OptionMoneyAmountSum.class};
		ArrayList v= CollectionsToolKit.getArrayList(res);
		CollectionsToolKit.addToCollection(
			v,
			AbstractOptionMoneyAmount.defaultOptions());
		return (Class[]) v.toArray(new Class[0]);
	}

	/**
	* get the list of usables options for this WorkSheet
	*/
	public static OptionDef[] getOptionsFor(WorkSheet ws) {
		ArrayList create= CollectionsToolKit.getArrayList(ws.getAcceptedNewOptions());
		ArrayList rem= CollectionsToolKit.getArrayList(ws.getAcceptedRemoteOptions());
		ArrayList all= (ArrayList) create.clone();
		CollectionsToolKit.addToCollection(all, rem);
		OptionDef[] result= new OptionDef[all.size()];

		for (int i= 0; i < result.length; i++) {
			final Class c= (Class) all.get(i);
			final boolean canC= create.contains(c);
			final ArrayList remV=
				rem.contains(c) ? getRemoAccessibleOptFor(ws, c) : null;
			result[i]= new OptionDef() {
				public Class getMyClass() {
					return c;
				}
				public boolean canCreate() {
					return canC;
				}
				public boolean canRemote() {
					return (remV != null) && (remV.size() > 0);
				}
				public ArrayList getRemotes() {
					return remV;
				}
			};
		}

		return result;
	}

	/** used to pass information on option accepted **/
	public interface OptionDef {
		/** the class of this Option **/
		public Class getMyClass();
		/** true if can create a new one **/
		public boolean canCreate();
		/** true if can use a remote one **/
		public boolean canRemote();
		/** return ArrayList of all remotly Accesible Options (null if none) **/
		public ArrayList getRemotes();
	}

	/**
	 * get the remotly accesible option of this ClassType
	 */
	private static ArrayList getRemoAccessibleOptFor(WorkSheet ws, Class c) {
		ArrayList result = new ArrayList();
		Iterator e = ws.getTarification().getAllOptions().iterator();
		Object dummy = null;
		while (e.hasNext()) {
			dummy = e.next();
			if (c.isInstance(dummy))
				result.add(dummy);
		}
		return result;
	}

	/**
	* create an Option of the specified Class for this WorkSheet<BR>
	* Yo can also use directly constructor from subclasses of BCOptions
	* @param ws The WorkSheet this Option is created for
	* @param c is one of the Class[] obtained with #getOptionsFor(WorkSheet ws)
	* @return BCOption or null in case of error. 
	*/
	public static BCOption createOption(WorkSheet ws, Class c) {
		// check if valid
		Class[] cs= ws.getAcceptedNewOptions();
		boolean ok= false;
		for (int i= 0; i < cs.length; i++) {
			if (cs[i] == c) {
				ok= true;
				break;
			}
		}
		
		if ( !ok ) {
			m_log.error( "Tried to create an option: " + c + " on " + ws );
			return null;
		}

		// create parameters Array
		Object[] initArgs= new Object[2];
		initArgs[0]= ws;
		initArgs[1]= getOptionTitle(c);
		// create parameters class type to get the right constructor
		Class[] paramsType= ComponentManager.getClassArray(initArgs);
		// paramsType[0] will be subclass of WorkSheet, so we uptype 
		// to that. 
		paramsType[0]= WorkSheet.class;
		return (BCOption) ComponentManager.getInstanceOf(
			c,
			paramsType,
			initArgs);
	}

	/**
	 * add an existing option (remote option) to this worksheet
	 * 
	 */
	public static boolean addRemoteOptionTo(WorkSheet ws, BCOption bco) {
		//		check if valid
		Class[] cs= ws.getAcceptedRemoteOptions();
		boolean ok= false;
		for (int i= 0; i < cs.length; i++)
			if (cs[i].isInstance(bco)) {
				ok= true;
				break;
			}
		if (!ok) {
			m_log.error( "tried to link option: " + bco + " on " + ws );
			return false;
		}

		ws.addOption(bco);
		return true;
	}

	/**
	* get translationKey for the title of this Option
	*/
	public static String getOptionTitle(Class c) {
		if (c == null) {
			m_log.error( "Class c is null" );
			return null;
		}
		String s= null;
		try {
			s= (String) c.getField("OPTION_TITLE").get(new String());
		} catch (NoSuchFieldException e) {
			m_log.error( "OptionManager: NoSuchFieldException", e );
		} catch (IllegalAccessException e) {
			m_log.error( "OptionManager: IllegalAccessException", e );
		}
		return s;
	}

}
/** $Log: OptionManager.java,v $
/** Revision 1.2  2007/04/02 17:04:24  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:38  perki
/** First commit on sourceforge
/**
/** Revision 1.23  2004/11/15 18:54:04  carlito
/** DispatcherIf removed... workSheet and option cleaning...
/**
/** Revision 1.22  2004/10/12 08:07:51  perki
/** Sum Of Amount 2
/**
/** Revision 1.21  2004/09/09 14:12:06  jvaucher
/** - Calculus for DispatcherBounds
/** - OptionCommissionAmountUnder... not finished
/**
/** Revision 1.20  2004/09/03 11:47:53  kaspar
/** ! Log.out -> log4j first half
/**
/** Revision 1.19  2004/08/17 13:51:49  kaspar
/** ! #26: crash on RateOnAmount fixed. Cause: assert in the wrong place,
/**   mixup between object init and event callbacks (as usual)
/**
/** Revision 1.18  2004/07/08 14:58:59  perki
/** Vectors to ArrayList
/**
/** Revision 1.17  2004/06/28 10:38:47  perki
/** Finished sons detection for Tarif, and half corrected bug for edition in STable
/**
/** Revision 1.16  2004/06/16 10:17:00  carlito
/** *** empty log message ***
/**
/** Revision 1.15  2004/05/20 09:39:43  perki
/** *** empty log message ***
/**
/** Revision 1.14  2004/05/18 19:09:47  carlito
/** *** empty log message ***
/**
/** Revision 1.13  2004/04/12 12:30:28  perki
/** Calculus
/**
/** Revision 1.12  2004/03/18 18:08:59  perki
/** barbapapa
/**
/** Revision 1.11  2004/03/18 15:43:32  perki
/** new option model
/**
/** Revision 1.10  2004/03/02 14:42:47  perki
/** breizh cola. le cola du phare ouest
/**
/** Revision 1.9  2004/02/26 14:34:20  perki
/** Ou alors la terre est un croissant comme la lune
/**
/** Revision 1.8  2004/02/06 10:04:22  perki
/** Lots of cleaning
/**
/** Revision 1.7  2004/02/05 11:07:28  perki
/** Transactions are welcome aboard
/**
/** Revision 1.6  2004/02/04 15:42:16  perki
/** cleaning
/**
* Revision 1.5  2004/02/04 11:11:35  perki
* *** empty log message ***
*
* Revision 1.4  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.3  2004/01/29 13:40:40  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.1  2004/01/28 15:32:16  perki
* Il neige plus
*
*/
