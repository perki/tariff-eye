/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionTransaction.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.money.TransactionValue;

import org.apache.log4j.Logger;

/**
 *  Options for transactions contains an amount of money and a number 
 */
public class OptionTransaction extends BCOption 
							implements Copiable.TransferableOption {
	private static final Logger m_log = Logger.getLogger( OptionTransaction.class );
								
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Transaction";

	/** my value **/
	private TransactionValue transactionValue;
	
	/**
	 * 
	 */
	public OptionTransaction(WorkSheet workSheet, String title) {
		super(workSheet, title);
	}

	/**
	 * Note also used by XML
	 * @return
	 */
	public TransactionValue getTransactionValue() {
		if (transactionValue == null) 
			transactionValue=
				new TransactionValue(
					new Money(0d),
					0);
		return transactionValue;
	}

	/**
	 * Note also used by XML
	 * @param value
	 */
	public void setTransactionValue(TransactionValue value) {
		if (! getTransactionValue().xequals(value)) {
			transactionValue = value;
			fireDataChange();
		}
	}

	protected int getStatusPrivate() { 
		if (getTransactionValue().getAverageNumber() < 1)
			return STATE_INVALID;
		return STATE_OK; 
		
	}
	
	//---------- IMPLEMENTS Copiable.TransferableOption --------------//
	
	/**
	 * <B>IMPLEMENTS Copiable.TransferableOption</B><BR>
	 * @see com.simpledata.bc.datamodel.Copiable.
	 * TransferableOption#canCopyValuesInto(Class destination)
	 */
	public boolean canCopyValuesInto(Class destination) {
		return (destination == OptionTransaction.class);
	}

	/**
	 * <B>IMPLEMENTS Copiable.TransferableOption</B><BR>
	 * @see com.simpledata.bc.datamodel.Copiable.
	 * TransferableOption#copyValuesInto(com.simpledata.bc.datamodel.BCOption)
	 */
	public void copyValuesInto(BCOption destination) {
		if (! canCopyValuesInto(destination.getClass())) {
			m_log.error( "cannot copy value of ["+destination.getClass()+
					"] into a ["+getClass()+"]" );
		}
		OptionTransaction ot = (OptionTransaction) destination;
		ot.setTitle(new String(getTitle()));
		ot.setDescription(new String(getDescription()));
		ot.getTransactionValue().setAverageNumber(
				getTransactionValue().getAverageNumber());
		ot.getTransactionValue().setInGoingToBank(
				getTransactionValue().inGoingToBank());
		ot.getTransactionValue().setMoneyValue((Money)
				getTransactionValue().getMoneyValue().copy());
	}
	
	//	------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public OptionTransaction() {}


}

/**
 *  $Log: OptionTransaction.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.19  2004/11/10 17:47:47  perki
 *  Closed bug #50 : TransactionValues did not save the direction of their transactions
 *
 *  Revision 1.18  2004/09/22 06:47:04  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.17  2004/09/14 14:46:29  perki
 *  *** empty log message ***
 *
 *  Revision 1.16  2004/09/03 11:47:53  kaspar
 *  ! Log.out -> log4j first half
 *
 *  Revision 1.15  2004/08/04 06:03:11  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.14  2004/07/07 17:27:09  perki
 *  *** empty log message ***
 *
 *  Revision 1.13  2004/05/31 16:15:11  carlito
 *  *** empty log message ***
 *
 *  Revision 1.12  2004/05/20 09:39:43  perki
 *  *** empty log message ***
 *
 *  Revision 1.11  2004/05/14 11:48:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.10  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.9  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.8  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.7  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.6  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.5  2004/02/22 10:43:56  perki
 *  File loading and saving
 *
 *  Revision 1.4  2004/02/19 21:32:16  perki
 *  now 1Gig of ram
 *
 *  Revision 1.3  2004/02/19 19:47:34  perki
 *  The dream is coming true
 *
 *  Revision 1.2  2004/02/05 15:11:39  perki
 *  Zigouuuuuuuuuuuuuu
 *
 *  Revision 1.1  2004/02/05 11:07:28  perki
 *  Transactions are welcome aboard
 *
 */