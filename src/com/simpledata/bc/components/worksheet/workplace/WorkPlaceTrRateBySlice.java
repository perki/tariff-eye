/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: WorkPlaceTrRateBySlice.java,v 1.2 2007/04/02 17:04:25 perki Exp $
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
import com.simpledata.bc.components.bcoption.OptionTransaction;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.components.worksheet.workplace.tools.RateBySlice;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.money.TransactionValue;

/**
 * WorkPlace for Transaction handles rates by Slices
 * only works on TransactionsRoot0
 */
public class WorkPlaceTrRateBySlice extends WorkPlaceAbstract {
	private static final Logger m_log 
		= Logger.getLogger( WorkPlaceTrRateBySlice.class ); 
	
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "WorkPlaceTrRateBySlice";
	
	private RateBySlice rbs;
	
	public static final int APPLY_ON_INCOMING_TO_BANK = 2;
	public static final int APPLY_ON_OUTGOING_FROM_BANK = 1;
	public static final int APPLY_ON_INCOMING_AND_OUTGOING = 0;
	
	private static final int APPLY_ON_DEFAULT = 
		APPLY_ON_INCOMING_AND_OUTGOING;
	
	/** one of APPLY_ON_* **/
	private int applyOn;
	
	/** calculus method */
	public static final int EACH_TRANSACTION = 0;
	public static final int TRANSACTION_VOLUME_TOTAL = 1;
	
	private int m_calculusMethod;
	
	/**
	 * @param parent
	 * @param title
	 * @param id
	 */
	public WorkPlaceTrRateBySlice(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id, key);
		
		m_calculusMethod = 0;
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
	 */
	public void initializeData() {
		rbs= new RateBySlice(true);
		setApplyOn(APPLY_ON_DEFAULT);
	}
	
	/**
	 * change the type of transaction this workplace should be applied<BR>
	 * fires a NamedEvent.WORKSHEET_DATA_MODIFIED;
	 *
	 * Note: This method takes part in loading from XML.
	 * @param applyOnType one of APPLY_ON_*
	 */
	public void setApplyOn(int applyOnType) {
		if (applyOn == applyOnType) return ;
		applyOn = applyOnType;
		fireNamedEvent(NamedEvent.WORKSHEET_DATA_MODIFIED);
		optionDataChanged(null,null);
	}
	
	/**
	 * get the type of transaction this workplace will use
	 */
	public int getApplyOn() {
		return applyOn;
	}
	
	public void setCalculusMethod(int method) {
		if (m_calculusMethod == method) return;
		m_calculusMethod = method;
		fireNamedEvent(NamedEvent.WORKSHEET_DATA_MODIFIED);
		optionDataChanged(null, null);
	}
	
	public int getCalculusMethod() {
		return m_calculusMethod;
	}
	
	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_ONLY_RATE;
	}
	
	/**
	 * get the TransactionsRoot0 tarif I'm working on
	 */
	public TransactionsRoot0 getTransactionsRoot0() {
		if (getTarif() != null) {
			WorkSheet ws= getTarif().getWorkSheet();
			//	check if I'm attached to a valid tarif
			if (ws != null && (TransactionsRoot0.class.isInstance(ws))) {
				return (TransactionsRoot0) ws;
			}
			m_log.fatal( "My Tarif does not hold an valid root WS" );
			
		} 
		m_log.warn( "Not on a Tarif" );
		return null;
	}
	
//	
//	/** mainly used for copy **/
//	public void setParent(WorkSheetContainer wsc, String key) {
//		if (wsc != null) {
//			Tarif t= wsc.getTarif();
//			if (t != null) {
//				// check for root workSheet
//				WorkSheet ws= t.getWorkSheet();
//				//	check if I'm attached to a valid tarif
//				if (ws == null || !(TransactionsRoot0.class.isInstance(ws))) {
//					m_log.error( "cannot work on " + t );
//					return;
//				} 
//			}
//		}
//		super.setParent(wsc, key);
//	}
	
	/**
	 * return true if this WorkPlace applies for this Transaction
	 */
	private boolean appliesOn(TransactionValue tv) {
		switch (applyOn) {
			case APPLY_ON_INCOMING_AND_OUTGOING:
				return true;
			case  APPLY_ON_INCOMING_TO_BANK:
				return tv.inGoingToBank();
			case  APPLY_ON_OUTGOING_FROM_BANK:
				return ! tv.inGoingToBank();
		}
		return false;
	}
	
	/**
	 * 
	 */
	public void privateComCalc(ComCalculus cc,Money value) {
		TransactionValue temp= null;
		boolean computeEachSlice = (m_calculusMethod == EACH_TRANSACTION);
		
		Money totalVolume = new Money(0.0);
		Money totalFee    = new Money(0.0);
		
		if (getTransactionsRoot0() != null) {
			
			Iterator e= getTransactionsRoot0().getOptionsApplicable(
					OptionTransaction.class).iterator();
			
			while (e.hasNext()) {
				temp= ((OptionTransaction) e.next()).getTransactionValue();
				if (temp != null) {
					if (appliesOn(temp)) {
						totalVolume.operation(temp.getMoneyValue(), temp.getAverageNumber());
						if (computeEachSlice) {
							Money localCom = getRbs().getCom(temp.getMoneyValue());
							totalFee.operation(localCom, temp.getAverageNumber());
						}
						
					}
				}
			}
			if (! computeEachSlice) {
				totalFee = getRbs().getCom(totalVolume);
			}
		}
		value.operation(totalFee,1);
	}
	
	public boolean isValid() { return true ; }

	/**
	 * get the used rate By Slice
	 */
	public final RateBySlice getRbs() {
		
		return rbs;
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
		return true;
	}

	

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#copy(com.simpledata.bc.datamodel.WorkSheetContainer, java.lang.String)
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		WorkPlaceTrRateBySlice ws=
			(WorkPlaceTrRateBySlice) WorkSheetManager.createWorkSheet(
				parent,
				this.getClass(),
				key);
		ws.setRbs((RateBySlice) rbs.copy());
		ws.setApplyOn(getApplyOn());
		return ws;
	}

	//----------------- XML ---------------------//

	/** XML CONSTRUCTOR **/
	public WorkPlaceTrRateBySlice() {
	    // TODO XXX remove when ficles are translated
//	  if (Currency.INIT_AT_XML_CREATION) initializeData();       
	}

	
	/**
	 * XML
	 */
	public void setRbs(RateBySlice slice) {
		rbs= slice;
	}

	// Visitor
	public void visit(TarifTreeVisitor v) {
		v.caseWorkPlaceTrRateBySlice(this); 
	}

}

/**
 *  $Log: WorkPlaceTrRateBySlice.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:40  perki
 *  First commit on sourceforge
 *
 *  Revision 1.54  2004/11/16 10:36:51  perki
 *  Corrig� bug #11
 *
 *  Revision 1.53  2004/11/10 17:47:47  perki
 *  Closed bug #50 : TransactionValues did not save the direction of their transactions
 *
 *  Revision 1.52  2004/09/29 16:40:06  perki
 *  Fixef Futures
 *
 *  Revision 1.51  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.50  2004/09/22 06:47:04  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.49  2004/09/16 09:55:50  jvaucher
 *  Introduced the total volume calculation for the transactions rateBySlice workplace.
 *
 *  Revision 1.48  2004/09/10 16:51:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.47  2004/09/09 18:38:46  perki
 *  Rate by slice on amount are welcome aboard
 *
 *  Revision 1.46  2004/09/08 16:35:14  perki
 *  New Calculus System
 *
 *  Revision 1.45  2004/09/03 11:47:53  kaspar
 *  ! Log.out -> log4j first half
 *
 *  Revision 1.44  2004/08/17 11:46:00  kaspar
 *  ! Decoupled visitor architecture from datamodel. No illegal
 *    dependencies left, hopefully
 *
 *  Revision 1.43  2004/08/04 06:03:12  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.42  2004/07/26 20:36:09  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: WorkPlaceTrRateBySlice.java,v 1.2 2007/04/02 17:04:25 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.41  2004/07/22 15:12:35  carlito
 *  lots of cleaning
 *
 *  Revision 1.40  2004/07/19 09:36:54  kaspar
 *  * Added Visitor for visiting the whole Tarif structure called
 *    TarifTreeVisitor
 *  * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 *  * removed superfluous hsqldb.jar
 *
 *  Revision 1.39  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.38  2004/07/04 14:54:53  perki
 *  *** empty log message ***
 *
 *  Revision 1.37  2004/05/23 12:16:22  perki
 *  new dicos
 *
 *  Revision 1.36  2004/05/21 13:19:50  perki
 *  new states
 *
 *  Revision 1.35  2004/05/20 10:36:15  perki
 *  *** empty log message ***
 *
 *  Revision 1.34  2004/05/18 17:10:19  perki
 *  Better icons management
 *
 *  Revision 1.33  2004/05/18 15:11:25  perki
 *  Better icons management
 *
 *  Revision 1.32  2004/05/18 13:49:46  perki
 *  Better copy / paste
 *
 *  Revision 1.31  2004/05/14 14:20:19  perki
 *  *** empty log message ***
 *
 *  Revision 1.30  2004/05/14 11:48:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.29  2004/05/12 10:47:13  perki
 *  when calculus becomes better
 *
 *  Revision 1.28  2004/05/12 10:11:12  perki
 *  *** empty log message ***
 *
 *  Revision 1.27  2004/05/05 09:02:57  perki
 *  cleaning
 *
 *  Revision 1.26  2004/04/12 17:34:52  perki
 *  *** empty log message ***
 *
 *  Revision 1.25  2004/04/12 12:33:09  perki
 *  Calculus
 *
 *  Revision 1.24  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.23  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.22  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.21  2004/03/04 16:38:05  perki
 *  copy goes to hollywood
 *
 *  Revision 1.20  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.19  2004/03/03 20:36:48  perki
 *  bonne nuit les petits
 *
 *  Revision 1.18  2004/03/03 14:42:11  perki
 *  Un petit bateau
 *
 *  Revision 1.17  2004/02/26 13:24:34  perki
 *  new componenents
 *
 *  Revision 1.16  2004/02/26 08:55:03  perki
 *  *** empty log message ***
 *
 *  Revision 1.15  2004/02/22 10:43:56  perki
 *  File loading and saving
 *
 *  Revision 1.14  2004/02/20 03:14:06  perki
 *  appris un truc
 *
 *  Revision 1.13  2004/02/19 23:57:25  perki
 *  now 1Gig of ram
 *
 *  Revision 1.12  2004/02/19 21:32:16  perki
 *  now 1Gig of ram
 *
 *  Revision 1.11  2004/02/19 20:19:28  perki
 *  nicer
 *
 *  Revision 1.10  2004/02/19 19:47:34  perki
 *  The dream is coming true
 *
 *  Revision 1.9  2004/02/19 15:40:25  perki
 *  Tango Bravo
 *
 *  Revision 1.8  2004/02/19 11:23:21  perki
 *  Zoulou
 *
 *  Revision 1.7  2004/02/16 18:59:15  perki
 *  bouarf
 *
 *  Revision 1.6  2004/02/06 10:04:22  perki
 *  Lots of cleaning
 *
 *  Revision 1.5  2004/02/06 07:44:55  perki
 *  lot of cleaning in UIs
 *
 *  Revision 1.4  2004/02/05 19:20:57  perki
 *  *** empty log message ***
 *
 */