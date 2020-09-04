/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: DispatcherSequencer.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.components.worksheet.dispatcher;

import java.util.ArrayList;
import java.util.Iterator;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Money;

/**
 * A dispatcher that makes a sequence of WorkSheets
 */
public class DispatcherSequencer 
	extends DispatcherAbstract implements Dispatcher.SupportInsertion {
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public static String WORKSHEET_TITLE = "Sequence Dispatcher";
	
	/** header used as key to specifiy if it's a replacement or a NEW one **/
	private final static String NEW_HEADER = "NEW ";
	
	/**
	 * The vector containing my WorkSheets
	 **/
	private ArrayList xWorkSheets;
	
	
	
	
	/**
	 * @param parent
	 * @param title
	 * @param id
	 */
	public DispatcherSequencer(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id, key);
	}
	
	public boolean isValid() { return true ; }

	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_FULL;
	}
	
	/**
	 * @see WorkSheetContainer#setWorkSheet(WorkSheet, java.lang.String)
	 * @param key is the position of the new work sheet if key is "NEW 1" 1 
	 * will not be removed and the new one inserted at position 1
	 */
	public final boolean setWorkSheet(WorkSheet ws, String key) {
		if (! acceptsNewWorkSheet(ws.getClass(),key)) return false;
		
		// check if key starts with "NEW "
		boolean remove = true;
		if (key.startsWith(NEW_HEADER)) {
			remove = false;
			key = key.substring(NEW_HEADER.length());
		}
		
		// key should be translatable to an Integer
		// if we fail.. we add this workSheet at the end
		int pos = getWorkSheets().size();
		try {
			pos = (new Integer(key)).intValue();
		} catch (NumberFormatException e) { 
			return false;
		}
		if (pos > xWorkSheets.size()) pos = xWorkSheets.size();
		if (pos < 0) pos = xWorkSheets.size();
		if (xWorkSheets.size() > pos) {
			if (remove)
				xWorkSheets.remove(pos);
		}
		xWorkSheets.add(pos,ws);
		
		fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
		return true;
	}
	
	/**
	 * get the WorkSheets
	 */
	private final  ArrayList getWorkSheets() {
		if (xWorkSheets == null) xWorkSheets = new ArrayList();
		return xWorkSheets;
	}

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheetContainer#getChildWorkSheets()
	 */
	public final ArrayList getChildWorkSheets() {
		return (ArrayList) getWorkSheets().clone();
	}


	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedNewOptions()
	 */
	public final Class[] getAcceptedNewOptions() {
		return new Class[0];
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedRemoteOptions()
	 */
	public final Class[] getAcceptedRemoteOptions() {
		// none
		return 	getAcceptedNewOptions();
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#_canRemoveOption(BCOption bco)
	 */
	public final boolean _canRemoveOption(BCOption bco) { return false; }
	

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheetContainer#getWorkSheetKey(com.simpledata.bc.datamodel.WorkSheet)
	 */
	public final String getWorkSheetKey(WorkSheet ws) {
		return ""+xWorkSheets.indexOf(ws);
	}
	
	/**
	 * same as getWorkSheetKey()but integer
	 */
	public final int getWorkSheetIndex(WorkSheet ws) {
		return xWorkSheets.indexOf(ws);
	}
	
	/**
	 * move a Workseet
	 * @param delta -1:move it up 1: move it down 
	 */
	public final void moveWorkSheet(int source,int delta) {
		int dest = source + delta;
		if (dest < 0) dest = 0;
		if (dest > (xWorkSheets.size() -1)) dest = (xWorkSheets.size() -1);
		
		if (dest == source) return ; // do nothing
		Object temp = xWorkSheets.get(source);
		xWorkSheets.remove(source);
		xWorkSheets.add(dest,temp);
		fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
	}
	

	/**
	 * remove a WorkSheet
	 */
	public void removeWorkSheet(int index) {
		if ((index >= 0) && (index < xWorkSheets.size())) {
			xWorkSheets.remove(index);
			fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
		}
	}
	
	/**
	 * create a new (empty) worksheet after this one
	 * @param index the worksheet before the new one (can be null)
	 */
	public final void createWorkSheetAfter(int index) {
		WorkSheetManager.createWorkSheet(this,
				EmptyWorkSheet.class,NEW_HEADER+(index+1));
	}
	
	

	/**
	 * same as getWorkSheetAt but with integer
	 */
	public final WorkSheet getWorkSheetAtIndex(int pos) {
		if (pos < 0 || pos > (xWorkSheets.size() -1)) return null;
		return (WorkSheet) xWorkSheets.get(pos);
	}

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheetContainer#getWorkSheetAt(java.lang.String)
	 */
	public final WorkSheet getWorkSheetAt(String key) {
		int pos = 0;
		try {
			pos = (new Integer(key)).intValue();
		} catch (NumberFormatException e) { 
			return null;
		}
		return getWorkSheetAtIndex(pos);
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
	 */
	public void initializeData() { }
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#copy(com.simpledata.bc.datamodel.WorkSheetContainer, java.lang.String)
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		DispatcherSequencer copy = 
			(DispatcherSequencer) WorkSheetManager.createWorkSheet(
					parent,this.getClass(),key);
		if (xWorkSheets != null) {
			Iterator e = xWorkSheets.iterator();
			while (e.hasNext()) {
				((WorkSheet) e.next()).copy(copy,"-1");
			}
		}
		return copy;
	}
	
	//------------- Commission ---------------//
	
	/**
	 * Calculate the commission taken at this point
	 */
	public final void privateComCalc(ComCalculus cc,Money value) {
		Iterator e = xWorkSheets.iterator();
		while (e.hasNext()) {
			value.operation(cc.getCom((WorkSheet) e.next()),1);
		}
	}
	
	//-------------- Insertion ---------//
	public String getInsertionKey() {
	    return "NEW 1";
	}
	
	
	
	//	------------- XML ---------------//
	  /** XML CONSTRUCTOR **/
	  public DispatcherSequencer() {}

	
	/**
	 * @return
	 */
	public final ArrayList getXWorkSheets() {
		return xWorkSheets;
	}

	/**
	 * @param vector
	 */
	public final void setXWorkSheets(ArrayList vector) {
		xWorkSheets= vector;
	}

	// Visitor implementation
	public void visit(TarifTreeVisitor v) {
		v.caseDispatcherSequencer( this ); 
	}

}


/**
 *  $Log: DispatcherSequencer.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:37  perki
 *  First commit on sourceforge
 *
 *  Revision 1.38  2004/11/16 10:36:51  perki
 *  Corrig� bug #11
 *
 *  Revision 1.37  2004/11/15 18:41:24  perki
 *  Introduction to inserts
 *
 *  Revision 1.36  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.35  2004/09/08 16:35:14  perki
 *  New Calculus System
 *
 *  Revision 1.34  2004/08/17 11:45:59  kaspar
 *  ! Decoupled visitor architecture from datamodel. No illegal
 *    dependencies left, hopefully
 *
 *  Revision 1.33  2004/07/19 09:36:53  kaspar
 *  * Added Visitor for visiting the whole Tarif structure called
 *    TarifTreeVisitor
 *  * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 *  * removed superfluous hsqldb.jar
 *
 *  Revision 1.32  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.31  2004/05/23 12:16:22  perki
 *  new dicos
 *
 *  Revision 1.30  2004/05/22 08:39:35  perki
 *  Lot of cleaning
 *
 *  Revision 1.29  2004/05/21 16:12:05  perki
 *  *** empty log message ***
 *
 *  Revision 1.28  2004/05/21 13:19:49  perki
 *  new states
 *
 *  Revision 1.27  2004/05/20 17:05:30  perki
 *  One step ahead
 *
 *  Revision 1.26  2004/05/20 10:36:15  perki
 *  *** empty log message ***
 *
 *  Revision 1.25  2004/05/18 15:11:25  perki
 *  Better icons management
 *
 *  Revision 1.24  2004/05/14 11:48:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.23  2004/05/14 07:52:52  perki
 *  baby dispatcher is going nicer
 *
 *  Revision 1.22  2004/04/12 17:34:52  perki
 *  *** empty log message ***
 *
 *  Revision 1.21  2004/04/12 12:33:09  perki
 *  Calculus
 *
 *  Revision 1.20  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.19  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.18  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.17  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.16  2004/03/04 14:32:07  perki
 *  copy goes to hollywood
 *
 *  Revision 1.15  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.14  2004/02/26 13:24:34  perki
 *  new componenents
 *
 *  Revision 1.13  2004/02/26 08:55:03  perki
 *  *** empty log message ***
 *
 *  Revision 1.12  2004/02/23 18:46:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.11  2004/02/22 10:43:56  perki
 *  File loading and saving
 *
 *  Revision 1.10  2004/02/20 00:07:40  perki
 *  now 1Gig of ram de la balle de balle
 *
 *  Revision 1.9  2004/02/19 23:57:25  perki
 *  now 1Gig of ram
 *
 *  Revision 1.8  2004/02/19 19:47:34  perki
 *  The dream is coming true
 *
 *  Revision 1.7  2004/02/18 16:59:29  perki
 *  turlututu
 *
 *  Revision 1.6  2004/02/17 16:57:39  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.5  2004/02/17 15:55:02  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.4  2004/02/17 11:39:21  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.3  2004/02/17 11:13:24  perki
 *  zigow
 *
 *  Revision 1.2  2004/02/17 09:51:05  perki
 *  zibouw
 *
 *  Revision 1.1  2004/02/16 18:59:15  perki
 *  bouarf
 *
 */