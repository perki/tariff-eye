/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: TransactionValue.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.money;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * Contains an Average Amount of Money and an  Average number 
 */
public class TransactionValue implements Serializable {
    private static final Logger m_log = 
        Logger.getLogger( TransactionValue.class ); 
    
    Money moneyValue ;
	int averageNumber;
	boolean inGoing_to_bank;
	
	
	public TransactionValue (Money money,int n) {
		this(money,n,true);
	}
	
	public TransactionValue (Money money,int n,boolean inGoing_to_bank) {
		moneyValue = money;
		averageNumber = n;
		this.inGoing_to_bank = inGoing_to_bank;
	}
	
	
	/**
	 * return true if those object are equivalent
	 */
	public boolean xequals(TransactionValue tv) {
		if (getMoneyValue() == null) return false;
		if (! getMoneyValue().xequals(tv.getMoneyValue())) return false;
		if (getAverageNumber() != tv.getAverageNumber()) return false;
		if (inGoingToBank() != tv.inGoingToBank()) return false;
		return true;
	}
	
	
	/**
	 * @return
	 */
	public int getAverageNumber() {
		return (averageNumber < 1) ? 1 : averageNumber;
	}

	/**
	 * @return
	 */
	public Money getMoneyValue() {
		return moneyValue;
	}

	/**
	 * @param i
	 */
	public void setAverageNumber(int i) {
		if (i < 1) i = 1;
		averageNumber = i;
	}

	/**
	 * @param money
	 */
	public void setMoneyValue(Money money) {
		moneyValue = money;
	}
	
	/**
	 * @return Returns true if this transaction is inGoing_to_bank.
	 */
	public boolean inGoingToBank() {
		return inGoing_to_bank;
	}
	/**
	 * @param inGoing_to_bank true if this transaction is inGoing_to_bank.
	 */
	public void setInGoingToBank(boolean inGoing_to_bank) {
		this.inGoing_to_bank = inGoing_to_bank;
	}
	
	//---------- XML ----//
	/** XML **/
	public TransactionValue () {}
	
	
    /** XML */
    public boolean getInGoing_to_bank() {
        return inGoing_to_bank;
    }
    /** XML */
    public void setInGoing_to_bank(boolean inGoing_to_bank) {
        this.inGoing_to_bank = inGoing_to_bank;
    }
}


/**
 *  $Log: TransactionValue.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:36  perki
 *  First commit on sourceforge
 *
 *  Revision 1.9  2004/11/10 17:47:47  perki
 *  Closed bug #50 : TransactionValues did not save the direction of their transactions
 *
 *  Revision 1.8  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.7  2004/09/22 06:47:05  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.6  2004/08/04 06:03:12  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.5  2004/05/14 11:48:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.4  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.3  2004/02/22 15:57:25  perki
 *  Xstream sucks
 *
 *  Revision 1.2  2004/02/22 10:43:57  perki
 *  File loading and saving
 *
 *  Revision 1.1  2004/02/05 11:08:29  perki
 *  Transactions are welcome aboard
 *
 */