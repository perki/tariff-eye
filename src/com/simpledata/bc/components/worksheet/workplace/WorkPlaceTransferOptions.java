/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: WorkPlaceTransferOptions.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
 
/** 
 * Package that contains all Workplaces. Workplace s are a unit 
 * of computation and Dispatcher s are a way of finding the correct
 * applicable unit of computation. 
 */
package com.simpledata.bc.components.worksheet.workplace;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherRoot;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.Tarif.TarifTransferRoot;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;

/**
 * A dummy WorkPlace it indicated to it's parent (less defined) Tarif
 * that it must used the option defined here for its calculus.<BR>
 * 
 * This WorkPlace indicates that this Tarif makes no calculus and is just here
 * as an option container for it's parent.
 */
public class WorkPlaceTransferOptions extends WorkPlaceAbstract {
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE = "TransferOptions";
	
	private static final Logger m_log = Logger.getLogger( 
			WorkPlaceTransferOptions.class ); 
	
	/**
	 * @param parent
	 * @param title
	 * @param id
	 * @param key
	 */
	public WorkPlaceTransferOptions(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id, key);
	}
	
	
	/** 
	 * return the parents of the Tarif containing this WorkPlace.. 
	 * If there is 0 or more than 1 parent.. then this WorkSheet is not valid
	 */
	public ArrayList/*<Tarif>*/ getParentTarif() {
		if (getTarif() == null) return new ArrayList();
		ArrayList/*<Tarif>*/ result = 
		    (ArrayList) getTarif().getDirectParents().clone();
		
		for (Iterator i=result.iterator();i.hasNext();){
		   if (! (i.next() instanceof TarifTransferRoot))
		       i.remove();
		}
		
		return result;
	}
	
	/**
	 * if there is only one single parent Tarif return it else null 
	 */
	public Tarif getSingleParentTarif() {
		if (getParentTarif().size() == 1) {
			return (Tarif) getParentTarif().get(0);
		}
		return null;
	}
	
	/** 
	 * get parent's first worksheet or null if not possible
	 */
	private WorkSheet getParentFirstWS() {
	if (getValidParentTarif() == null ) return null;	
		return getSingleParentTarif().getWorkSheet();
	}
	
	/**
	 * return a valid parent or null if none
	 */
	public Tarif getValidParentTarif() {
	    Tarif t = getSingleParentTarif();

	    
		if (t != null && t.isValid() && (t instanceof TarifTransferRoot))
		        return t;

		return null;
	}

	
	/**
	 * this worksheet is valid if the tarif has one single tarif. And if this
	 * Tarif is valid && directly plugged on a DispatcherRoot
	 */
	public boolean isValid() {
	    
	    if (getWscontainer() == null || 
	             (! (getWscontainer() instanceof DispatcherRoot)))
	        return false;
	    
		return getValidParentTarif() != null;
	}

	/**
	 * nothing to do here
	 */
	public void initializeData() {
		// nada
	}

	/**
	 * return parent's accepted new options
	 */
	public Class[] getAcceptedNewOptions() {
		if (getParentFirstWS() == null) return new Class[0];
		return getParentFirstWS().getAcceptedNewOptions();
	}

	/**
	 *return parent's accepted remote options
	 */
	public Class[] getAcceptedRemoteOptions() {
		if (getParentFirstWS() == null) return new Class[0];
		return getParentFirstWS().getAcceptedRemoteOptions();
	}

	/**
	 * forward to parent's worksheet
	 */
	protected boolean _canRemoveOption(BCOption bco) {
		if (getParentFirstWS() == null) return false;
		return getParentFirstWS().canRemoveOption(bco);
	}

	/**
	 * No calculation to do here
	 */
	protected final void privateComCalc(ComCalculus cc,Money value) {
		if (! readyToParticipate()) return;
		
		DispatcherRoot parentTarifWS = (DispatcherRoot)getParentFirstWS();
		DispatcherRoot myParentWS = (DispatcherRoot)getWscontainer();
		
		// be sure my parent is up to date
		value.setValue(cc.getCom(getParentFirstWS()));
		value.operationFactor(parentTarifWS.proportionOf(myParentWS));
		
		// refresh my parent WorkSheet
		cc.refresh(getParentFirstWS());
	}

	/**
	 * return true if this TransferOption can participate in calculus
	 */
	protected boolean readyToParticipate() {
		String message = "";
		if (!isValid()) 					message +="not valid:";
		if (getParentFirstWS() == null) 	message +="parent's WS is null:";
		if (! (getParentFirstWS() instanceof DispatcherRoot))
							message +="parent's WS is not a DispatcherRoot:";
		if (! (getWscontainer() instanceof DispatcherRoot))
							message +="my parent is not a DispatcherRoot:";
		if (getParentFirstWS().getClass() != getWscontainer().getClass())
			message +="parent's WS is not of the same class than me:";
		
		if (! message.equals("")) {
			m_log.debug(this+" is not ready for calculus because: "+message);
			return false;
		}
		
		return true;
	}
	
	
	/**
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		return WorkSheetManager.createWorkSheet(parent,this.getClass(),key);
	}

	/**
	 * This work place accept no reductions
	 */
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_NO;
	}
	
	//--------------- XML ------------//
	/** XML */
	public WorkPlaceTransferOptions() {}
		
	// Visitor
	public void visit(TarifTreeVisitor v) {
		v.caseWorkPlaceTransferOptions(this); 
	}
}

/*
 * $Log: WorkPlaceTransferOptions.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.19  2004/11/16 10:36:51  perki
 * Corrig� bug #11
 *
 * Revision 1.18  2004/11/15 18:41:24  perki
 * Introduction to inserts
 *
 * Revision 1.17  2004/10/14 16:39:07  perki
 * *** empty log message ***
 *
 * Revision 1.16  2004/10/11 17:48:08  perki
 * Bobby
 *
 * Revision 1.15  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.14  2004/09/09 12:14:11  perki
 * Cleaning WorkSheet
 *
 * Revision 1.13  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.12  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.11  2004/09/02 15:51:46  perki
 * Lot of change in calculus method
 *
 * Revision 1.10  2004/08/17 11:46:00  kaspar
 * ! Decoupled visitor architecture from datamodel. No illegal
 *   dependencies left, hopefully
 *
 * Revision 1.9  2004/08/02 18:22:01  perki
 * Repartition viewer on simulator
 *
 * Revision 1.8  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 * Revision 1.7  2004/07/26 20:36:09  kaspar
 * + trRateBySlice subreport that shows for all
 *   RateBySlice Workplaces. First Workplace subreport.
 * + Code comments in a lot of classes. Beautifying, moving
 *   of $Id: WorkPlaceTransferOptions.java,v 1.2 2007/04/02 17:04:25 perki Exp $ tag.
 * + Long promised caching of reports, plus some rudimentary
 *   progress tracking.
 *
 * Revision 1.6  2004/07/19 09:36:54  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.5  2004/07/08 14:58:59  perki
 * Vectors to ArrayList
 *
 * Revision 1.4  2004/07/04 14:54:53  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/07/04 11:23:39  perki
 * temp unstable state
 *
 * Revision 1.2  2004/07/04 10:57:45  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/07/02 09:37:31  perki
 * *** empty log message ***
 *
 */