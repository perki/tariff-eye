/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: WorkPlaceRateOnAmount.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
 
/** 
 * Package that contains all Workplaces. Workplace s are a unit 
 * of computation and Dispatcher s are a way of finding the correct
 * applicable unit of computation. 
 */
package com.simpledata.bc.components.worksheet.workplace;

import java.util.ArrayList;
import java.util.Vector;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.AbstractOptionMoneyAmount;
import com.simpledata.bc.components.bcoption.OptionPerBaseTen;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.util.CollectionsToolKit;

/**
 * WorkPlace that takes a Rate (Percent on an Amount)<BR>
 * it accepts two options :<BR> 
 * OptionPercentage that cannot be removed<BR>
 * and an Option of type AbstractOptionMoneyAmount<BR>
 */
public class WorkPlaceRateOnAmount extends WorkPlaceAbstract {
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "Rate on Amount";

	/**
	* constructor.. should not be called by itself. 
	* use WorkSheet.createWorkSheet(Dispatcher d,Class c)
	*/
	public WorkPlaceRateOnAmount(
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
		
	}
	

	/**
	 * Calculate the commission taken at this point
	 */
	public void privateComCalc(ComCalculus calc,Money value) {
		OptionPerBaseTen op=
			(OptionPerBaseTen) getOptions(OptionPerBaseTen.class).get(0);
		
		AbstractOptionMoneyAmount oma=
			(AbstractOptionMoneyAmount) getOptions(
				AbstractOptionMoneyAmount.class).get(0);
		
		value.operation(oma.moneyValueTotal(calc),op.getDoubleValue());
	}
	

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedNewOptions()
	 */
	public Class[] getAcceptedNewOptions() {
		ArrayList res= new ArrayList();
		// add the rate (if any)
		res.add(OptionPerBaseTen.class);
		if (getOptions(AbstractOptionMoneyAmount.class).size() == 0) {
			CollectionsToolKit.addToCollection(
			        res,AbstractOptionMoneyAmount.defaultOptions());
		}
		return (Class[]) res.toArray(new Class[0]);
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedRemoteOptions()
	 */
	public Class[] getAcceptedRemoteOptions() {
		return 	getAcceptedNewOptions();
	}
	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_FULL;
	}
	
	public boolean isValid() {
		if (getOptions(OptionPerBaseTen.class).size() != 1)
			return false;
		if (getOptions(AbstractOptionMoneyAmount.class).size() != 1)
			return false;
		return true;
	}

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#_canRemoveOption(BCOption bco)
	 */
	public boolean _canRemoveOption(BCOption bco) {
		return true;
	}
	
	/**
	 * @see datamodel.WorkSheet#copy(WorkSheetContainer, java.lang.String)
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		return WorkSheetManager.createWorkSheet(parent,this.getClass(),key);
	}

	//	------------- XML ---------------//
	/** XML CONSTRUCTOR **/
	public WorkPlaceRateOnAmount() {}

		
	// Visitor
	public void visit(TarifTreeVisitor v) {
		v.caseWorkPlaceRateOnAmount(this); 
	}
}

/**
 *  $Log: WorkPlaceRateOnAmount.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.33  2004/11/16 10:36:50  perki
 *  Corrig� bug #11
 *
 *  Revision 1.32  2004/11/15 14:24:28  perki
 *  Bug grave reparer dans WorkPlaceRateOnAmount
 *
 *  Revision 1.31  2004/11/08 17:49:36  perki
 *  done bug #17
 *
 *  Revision 1.30  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.29  2004/09/22 06:47:04  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.28  2004/09/09 18:38:46  perki
 *  Rate by slice on amount are welcome aboard
 *
 *  Revision 1.27  2004/09/09 16:38:44  jvaucher
 *  - Finished the OptionCommissionAmountUnder, used by RateOnAmount WorkPlace
 *  - A bit of cleaning in the DoubleSideMap
 *
 *  Revision 1.26  2004/09/09 14:12:06  jvaucher
 *  - Calculus for DispatcherBounds
 *  - OptionCommissionAmountUnder... not finished
 *
 *  Revision 1.25  2004/09/09 13:41:33  perki
 *  Added context to MoneyAmount
 *
 *  Revision 1.24  2004/09/08 16:35:14  perki
 *  New Calculus System
 *
 *  Revision 1.23  2004/08/17 11:45:59  kaspar
 *  ! Decoupled visitor architecture from datamodel. No illegal
 *    dependencies left, hopefully
 *
 *  Revision 1.22  2004/08/05 00:23:44  carlito
 *  DispatcherCase bugs corrected and aspect improved
 *
 *  Revision 1.21  2004/08/04 06:03:12  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.20  2004/07/26 20:36:09  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: WorkPlaceRateOnAmount.java,v 1.2 2007/04/02 17:04:25 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.19  2004/07/19 09:36:54  kaspar
 *  * Added Visitor for visiting the whole Tarif structure called
 *    TarifTreeVisitor
 *  * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 *  * removed superfluous hsqldb.jar
 *
 *  Revision 1.18  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.17  2004/07/04 14:54:53  perki
 *  *** empty log message ***
 *
 *  Revision 1.16  2004/06/28 10:38:47  perki
 *  Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 *  Revision 1.15  2004/05/23 12:16:22  perki
 *  new dicos
 *
 *  Revision 1.14  2004/05/21 13:19:49  perki
 *  new states
 *
 *  Revision 1.13  2004/05/20 10:36:15  perki
 *  *** empty log message ***
 *
 *  Revision 1.12  2004/05/18 15:11:25  perki
 *  Better icons management
 *
 *  Revision 1.11  2004/05/12 13:38:06  perki
 *  Log is clever
 *
 *  Revision 1.10  2004/05/05 09:02:57  perki
 *  cleaning
 *
 *  Revision 1.9  2004/04/12 17:34:52  perki
 *  *** empty log message ***
 *
 *  Revision 1.8  2004/04/12 12:33:09  perki
 *  Calculus
 *
 *  Revision 1.7  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.6  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.5  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.4  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.3  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.2  2004/03/02 14:42:48  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */