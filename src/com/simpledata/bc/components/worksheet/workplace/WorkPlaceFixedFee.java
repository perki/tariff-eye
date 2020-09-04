/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/** 
 * Package that contains all Workplaces. Workplace s are a unit 
 * of computation and Dispatcher s are a way of finding the correct
 * applicable unit of computation. 
 */
package com.simpledata.bc.components.worksheet.workplace;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;

/**
 * A WorkPlace that contains a Fixed Fee
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: WorkPlaceFixedFee.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public class WorkPlaceFixedFee extends WorkPlaceAbstract {
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "FixedFeeWorkPlace";

	private Money myFee;

	/**
	* constructor.. should not be called by itself. 
	* use WorkSheet.createWorkSheet(Dispatcher d,Class c)
	*/
	public WorkPlaceFixedFee(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {

		super(parent, title, id, key);
		
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
	 */
	public void initializeData() { 
		setMyFee(new Money(0d));
	}	

	/**
	 * Calculates the commission taken at this point.
	 * @param calc Calculus that stores the result. 
	 */
	public  synchronized void privateComCalc(ComCalculus cc,Money v) {
		v.setValue(getMyFee());
	}

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedNewOptions()
	 */
	public Class[] getAcceptedNewOptions() {
		return new Class[0];
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedRemoteOptions()
	 */
	public Class[] getAcceptedRemoteOptions() {
		// all
		return 	getAcceptedNewOptions();
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#_canRemoveOption(BCOption bco)
	 */
	public boolean _canRemoveOption(BCOption bco) { return true; }
	
	public boolean isValid() { return true ; }
	
	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_FULL;
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#copy(com.simpledata.bc.datamodel.WorkSheetContainer, java.lang.String)
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		WorkPlaceFixedFee copy = 
			(WorkPlaceFixedFee) WorkSheetManager.createWorkSheet(
			        parent,this.getClass(),key);
		copy.setMyFee((Money) myFee.copy());
		return copy;
	}
	
	//	------------- XML ---------------//
	  /** XML CONSTRUCTOR **/
	  public WorkPlaceFixedFee() {}
	
	
	/**
	 * @return Returns the myFee.
	 */
	public Money getMyFee() {
		return myFee;
	}
	/**
	 * @param myFee The myFee to set.
	 */
	public void setMyFee(Money myFee) {
		this.myFee = myFee;
	}
	
	// Visitor
	public void visit(TarifTreeVisitor v) {
		v.caseWorkPlaceFixedFee(this); 
	}
}

/*
 * $Log: WorkPlaceFixedFee.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.14  2004/11/16 10:36:51  perki
 * Corrig� bug #11
 *
 * Revision 1.13  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.12  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.11  2004/08/24 12:57:16  kaspar
 * ! Documentation spelling fixed
 * + Added some documentation, trying to clarify
 * ! Changed invalid line endings
 *
 * Revision 1.10  2004/08/17 11:45:59  kaspar
 * ! Decoupled visitor architecture from datamodel. No illegal
 *   dependencies left, hopefully
 *
 * Revision 1.9  2004/08/05 00:23:44  carlito
 * DispatcherCase bugs corrected and aspect improved
 *
 * Revision 1.8  2004/07/26 20:36:09  kaspar
 * + trRateBySlice subreport that shows for all
 *   RateBySlice Workplaces. First Workplace subreport.
 * + Code comments in a lot of classes. Beautifying, moving
 *   of $Id: WorkPlaceFixedFee.java,v 1.2 2007/04/02 17:04:25 perki Exp $ tag.
 * + Long promised caching of reports, plus some rudimentary
 *   progress tracking.
 *
 * Revision 1.7  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 * Revision 1.6  2004/07/19 09:36:54  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.5  2004/05/23 12:16:22  perki
 * new dicos
 *
 * Revision 1.4  2004/05/21 13:19:49  perki
 * new states
 *
 * Revision 1.3  2004/05/20 10:36:15  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/18 15:11:25  perki
 * Better icons management
 *
 * Revision 1.1  2004/05/05 12:38:51  perki
 * Plus FixedFee panel
 *
 */