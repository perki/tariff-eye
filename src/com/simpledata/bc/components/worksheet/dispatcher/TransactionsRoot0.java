/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/** 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: TransactionsRoot0.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
 
/// Calculus dispatching package. 
package com.simpledata.bc.components.worksheet.dispatcher;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionTransaction;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.money.TransactionValue;

/**
 * Default Dispatcher that must be added to any Transactions<BR>
 * This is the Version 0 of those Dispatchers
 * XXX: Is it a good idea to version things by adding numbers to their names ? What
 * is the goal here ? clarify. (ksc) 
 */
public class TransactionsRoot0 extends DispatcherRoot {
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "Transaction Root V0";

	private static final Logger m_log =
		Logger.getLogger( TransactionsRoot0.class ); 
	
	
	public TransactionsRoot0(
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
		// Empty
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedNewOptions()
	 */
	public Class[] getAcceptedNewOptions() {
		// accept an infinite numbers of TransactionsOptions
		return new Class[] { OptionTransaction.class };
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedRemoteOptions()
	 */
	public Class[] getAcceptedRemoteOptions() {
		// none
		return 	new Class[0];
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#_canRemoveOption(BCOption bco)
	 */
	public boolean _canRemoveOption(BCOption bco) { 
		return true; 
	}
	
	/**
	 * return the proportion represented by the options contained in this
	 * WorkSheet over myself
	 * @param dr must be of the exact same class type that this dispatcher
	 * @return valueOf(dr) / valueOf(me)
	 */
	public final double proportionOf(DispatcherRoot dr) {
		
		if (! (dr instanceof TransactionsRoot0)) {
			m_log.error(dr+" Must be of my class");
			return 0;
		}
		
		Money mine = getVolumeOfTransactions();
		if (mine.getValueDefCurDouble() == 0) return 0;
		
		Money his = ((TransactionsRoot0) dr).getVolumeOfTransactions();
		
		return his.getValueDefCurDouble() / mine.getValueDefCurDouble() ;
	}
	
	
	/**
	 * get the volume of transaction that has been used under this point
	 */
	public final Money getVolumeOfTransactions() {
		ArrayList/*<OptionTransaction>*/ opts =
				getOptionsApplicable(OptionTransaction.class);
		
		Money result = new Money(0d);
		
		TransactionValue tv;
		for (Iterator/*<OptionTransaction>*/ i = opts.iterator(); i.hasNext();){
			tv = ((OptionTransaction) i.next()).getTransactionValue();
			result.operation(tv.getMoneyValue(),tv.getAverageNumber());
		}
		
		return result;
	}
	
	
	//	------------- XML ---------------//
	/** XML CONSTRUCTOR **/
	public TransactionsRoot0() {
		// Empty
	}
	
	// Visitor implementation
	
	public void visit(TarifTreeVisitor v) {
		v.caseTransactionsRoot0( this ); 
	}
}
/**
 * $Log: TransactionsRoot0.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:37  perki
 * First commit on sourceforge
 *
 * Revision 1.25  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.24  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.23  2004/09/09 12:14:11  perki
 * Cleaning WorkSheet
 *
 * Revision 1.22  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.21  2004/08/17 11:45:59  kaspar
 * ! Decoupled visitor architecture from datamodel. No illegal
 *   dependencies left, hopefully
 *
 * Revision 1.20  2004/07/19 09:36:53  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.19  2004/07/04 10:57:45  perki
 * *** empty log message ***
 *
 * Revision 1.18  2004/05/19 16:39:58  perki
 * *** empty log message ***
 *
 * Revision 1.17  2004/03/23 19:45:18  perki
 * New Calculus Model
 *
 * Revision 1.16  2004/03/18 15:43:32  perki
 * new option model
 *
 * Revision 1.15  2004/02/26 13:24:34  perki
 * new componenents
 *
 * Revision 1.14  2004/02/26 08:55:03  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/02/22 10:43:56  perki
 * File loading and saving
 *
 * Revision 1.12  2004/02/19 23:57:25  perki
 * now 1Gig of ram
 *
 * Revision 1.11  2004/02/19 21:32:16  perki
 * now 1Gig of ram
 *
 * Revision 1.10  2004/02/19 19:47:34  perki
 * The dream is coming true
 *
 * Revision 1.9  2004/02/18 16:59:29  perki
 * turlututu
 *
 * Revision 1.8  2004/02/17 11:39:21  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.7  2004/02/17 09:51:05  perki
 * zibouw
 *
 * Revision 1.6  2004/02/16 19:03:57  perki
 * bouarf
 *
 * Revision 1.5  2004/02/16 18:59:15  perki
 * bouarf
 *
 * Revision 1.4  2004/02/06 10:04:22  perki
 * Lots of cleaning
 *
 * Revision 1.3  2004/02/05 15:11:39  perki
 * Zigouuuuuuuuuuuuuu
 *
 * Revision 1.2  2004/02/05 11:07:28  perki
 * Transactions are welcome aboard
 *
 * Revision 1.1  2004/02/05 07:46:54  perki
 * Transactions are welcome aboard
 *
 *
 */
