/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

/**
 * @version $Id: WorkPlaceAssetsRateBySlice.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
 
/** 
 * Package that contains all Workplaces. Workplace s are a unit 
 * of computation and Dispatcher s are a way of finding the correct
 * applicable unit of computation. 
 */
package com.simpledata.bc.components.worksheet.workplace;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.workplace.tools.RateBySlice;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.InstanceFinder;

/**
 * a work place for Assets based on rate by slice<BR>
 * make the distinction between perAmount or SumOfAmounts
 */
public class WorkPlaceAssetsRateBySlice extends WorkPlaceAbstract {
	private static final Logger m_log = 
	    Logger.getLogger( WorkPlaceAssetsRateBySlice.class );
	
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "WorkPlaceAssetsRateBySlice";
	
	private RateBySlice rbs;

	/** 
	 * variable set to true if it's a PerAmount calculus
	 * or false if it's on the SumOfTheAmount
	 */
	private Boolean perAmount;

	/**
	 * @param parent
	 * @param title
	 * @param id
	 * @param key
	 */
	public WorkPlaceAssetsRateBySlice(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id, key);
	}
	

	/**
	 * get the AssetsRoot0 tarif I'm working on
	 */
	public AssetsRoot0 getAssetsRoot0() {
		if (getTarif() != null) {
			WorkSheet ws= getTarif().getWorkSheet();
			//	check if I'm attached to a valid tarif
			if (ws != null && (AssetsRoot0.class.isInstance(ws))) {
				return (AssetsRoot0) ws;
			} 
			//TODO this is fatal
			m_log.fatal( "My Tarif does not hold an valid root WS tarif:["
			        +getTarif().toStringFull()+":"+getTarif().getClass()+
			        "] ws :["+ws+
			        "] complainer:["+this.getFullNID()+"]");
			
			InstanceFinder.find("shadowWS.log",getTarification(),this);	
			InstanceFinder.find("shadow.log",getTarification(),getTarif());	
			
			
			//TODO FIXME XXX Horrible Hack!!
			AssetsRoot0 hack = (AssetsRoot0) WorkSheetManager.createWorkSheet(
			        getTarif(),AssetsRoot0.class,"");
			
			return hack;
		} 
		m_log.error( "Not on a Tarif" );
		return null;
	}
	
	
//	/** mainly used for copy **/
//	public void setParent(WorkSheetContainer wsc, String key) {
//		if (wsc != null) {
//			Tarif t= wsc.getTarif();
//			if (t != null) {
//				// check for root workSheet
//				WorkSheet ws= t.getWorkSheet();
//				//	check if I'm attached to a valid tarif
//				if (ws == null || !(AssetsRoot0.class.isInstance(ws))) {
//					m_log.error( "cannot work on " + t );
//					return;
//				} 
//			} else {
//				m_log.error( "Empty Tarif " + t );
//			}
//		}
//		super.setParent(wsc, key);
//	}
//	
	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_ONLY_RATE;
	}
	
	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedNewOptions()
	 */
	public Class[] getAcceptedNewOptions() {
		// Accept no new options
		return new Class[0];
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedRemoteOptions()
	 */
	public Class[] getAcceptedRemoteOptions() {
		// none
		return 	getAcceptedNewOptions();
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#_canRemoveOption(BCOption bco)
	 */
	public boolean _canRemoveOption(BCOption bco) {
		return false;
	}
	
	public boolean isValid() { return true ; }

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
	 */
	public void initializeData() {
	    perAmount= new Boolean(false);
	    rbs= new RateBySlice(true);
	}

	/**
	 * Calculate the commission taken at this point
	 */
	public void privateComCalc(ComCalculus cc,Money value) {
		if (getPerAmount().booleanValue()) {
			getComPerAmount(cc,value);
		} else {
			getComSumOfAmounts(cc,value);
		}
	}
	

	
	
	/**
	 * get the script for a per amount value
	 */
	private void getComPerAmount(ComCalculus cc,Money addition) {
		Iterator e= getAssetsRoot0().getOptionsApplicable(
					OptionMoneyAmount.class).iterator();
		OptionMoneyAmount temp ;
		while (e.hasNext()) {
			temp = (OptionMoneyAmount) e.next();
			addition.operation(getRbs().getCom(temp.moneyValue(cc)),
			        temp.numberOfLines(cc));
		}
	}

	/**
	 * get the script for a sum of amount value
	 */
	private void getComSumOfAmounts(ComCalculus cc,Money rateCom) {
		Money addition = new Money(0d);
		Iterator e= getAssetsRoot0().getOptionsApplicable(
				OptionMoneyAmount.class).iterator();
		OptionMoneyAmount temp ;
		while (e.hasNext()) {
			temp = (OptionMoneyAmount) e.next();
			addition.operation(
					temp.moneyValue(cc),temp.numberOfLines(cc));
		}
		rateCom.operation(getRbs().getCom(addition),1);
	}

	/**
	 * get the PerAmount Boolean. true if per Amount. false if Sum of Amounts
	 */
	public Boolean getPerAmount() {
		
		return perAmount;
	}

	/**
	 * get the RateBySlice data
	 */
	public RateBySlice getRbs() {
			
		return rbs;
	}

	/**
	 * set the PerAmountValue
	 */
	public void setPerAmount(Boolean bool) {
		perAmount= bool;
	}

	/**
	 * @see WorkSheet#copy(WorkSheetContainer, java.lang.String)
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		WorkPlaceAssetsRateBySlice ws=
			(WorkPlaceAssetsRateBySlice) WorkSheetManager.createWorkSheet(
				parent,
				this.getClass(),
				key);
		ws.setPerAmount(new Boolean(perAmount.booleanValue()));
		ws.setRbs((RateBySlice) getRbs().copy());
		return ws;
	}
	
	
	// Visitor
	public void visit(TarifTreeVisitor v) {
		v.caseWorkPlaceAssetsRateBySlice(this); 
	}

	//	------------- XML ---------------//
	/** XML CONSTRUCTOR **/
	public WorkPlaceAssetsRateBySlice() {  }


	/**
	 * XML
	 */
	public void setRbs(RateBySlice slice) {
		rbs= slice;
	}


}

/**
 *  $Log: WorkPlaceAssetsRateBySlice.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:40  perki
 *  First commit on sourceforge
 *
 *  Revision 1.41  2004/11/16 10:36:50  perki
 *  Corrig� bug #11
 *
 *  Revision 1.40  2004/11/15 18:41:24  perki
 *  Introduction to inserts
 *
 *  Revision 1.39  2004/10/15 06:38:59  perki
 *  Lot of cleaning in code (comments and todos
 *
 *  Revision 1.38  2004/09/29 16:40:06  perki
 *  Fixef Futures
 *
 *  Revision 1.37  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.36  2004/09/22 06:47:04  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.35  2004/09/16 17:26:37  perki
 *  *** empty log message ***
 *
 *  Revision 1.34  2004/09/10 16:51:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.33  2004/09/10 14:48:50  perki
 *  Welcome Futures......
 *
 *  Revision 1.32  2004/09/09 13:41:33  perki
 *  Added context to MoneyAmount
 *
 *  Revision 1.31  2004/09/08 16:35:14  perki
 *  New Calculus System
 *
 *  Revision 1.30  2004/09/03 11:47:53  kaspar
 *  ! Log.out -> log4j first half
 *
 *  Revision 1.29  2004/08/17 11:45:59  kaspar
 *  ! Decoupled visitor architecture from datamodel. No illegal
 *    dependencies left, hopefully
 *
 *  Revision 1.28  2004/08/05 00:23:44  carlito
 *  DispatcherCase bugs corrected and aspect improved
 *
 *  Revision 1.27  2004/08/04 06:03:12  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.26  2004/08/02 18:22:01  perki
 *  Repartition viewer on simulator
 *
 *  Revision 1.25  2004/07/26 20:36:09  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: WorkPlaceAssetsRateBySlice.java,v 1.2 2007/04/02 17:04:25 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.24  2004/07/22 15:12:35  carlito
 *  lots of cleaning
 *
 *  Revision 1.23  2004/07/19 09:36:54  kaspar
 *  * Added Visitor for visiting the whole Tarif structure called
 *    TarifTreeVisitor
 *  * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 *  * removed superfluous hsqldb.jar
 *
 *  Revision 1.22  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.21  2004/07/04 14:54:53  perki
 *  *** empty log message ***
 *
 *  Revision 1.20  2004/05/23 12:16:22  perki
 *  new dicos
 *
 *  Revision 1.19  2004/05/20 10:36:15  perki
 *  *** empty log message ***
 *
 *  Revision 1.18  2004/05/18 15:11:25  perki
 *  Better icons management
 *
 *  Revision 1.17  2004/05/12 17:13:54  perki
 *  zob
 *
 *  Revision 1.16  2004/05/12 10:47:13  perki
 *  when calculus becomes better
 *
 *  Revision 1.15  2004/05/12 10:11:12  perki
 *  *** empty log message ***
 *
 *  Revision 1.14  2004/04/12 17:34:52  perki
 *  *** empty log message ***
 *
 *  Revision 1.13  2004/04/12 12:33:09  perki
 *  Calculus
 *
 *  Revision 1.12  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.11  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.10  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.9  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.8  2004/03/04 16:38:05  perki
 *  copy goes to hollywood
 *
 *  Revision 1.7  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.6  2004/03/03 20:36:48  perki
 *  bonne nuit les petits
 *
 *  Revision 1.5  2004/03/03 14:42:11  perki
 *  Un petit bateau
 *
 *  Revision 1.4  2004/02/26 13:24:34  perki
 *  new componenents
 *
 *  Revision 1.3  2004/02/26 08:55:03  perki
 *  *** empty log message ***
 *
 *  Revision 1.2  2004/02/22 10:43:56  perki
 *  File loading and saving
 *
 */