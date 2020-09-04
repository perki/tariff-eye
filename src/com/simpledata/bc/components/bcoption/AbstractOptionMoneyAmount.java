/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: AbstractOptionMoneyAmount.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.money.Money;

/**
 * An Abstract class for all money amount
 */
public abstract class AbstractOptionMoneyAmount extends BCOption  {
	

	/**
	 * Should be updated to get the of sublcasses of this interface
	 */
	public static Class[] defaultOptions() {
		return new Class[] {
			OptionMoneyAmount.class,
			OptionMoneyAmountUnder.class,
			OptionMoneyAmountSum.class,
			OptionCommissionAmountUnder.class
		};
	}
	
	
	
	/**
	 * 
	 */
	protected AbstractOptionMoneyAmount(WorkSheet workSheet, String title) {
		super(workSheet, title);
	}
	
	/** 
	 * return the number of lines of this money Value 
	 * @param cc may be null. In some cases AbstractOptionMoneyAmount may require 
	 * to do some calculus... when possible pass the current ComCalculus 
	 * **/
	public abstract int numberOfLines(ComCalculus cc);
	
	/** return the Money value of this Option 
	 * @param cc may be null. In some cases AbstractOptionMoneyAmount may require 
	 * to do some calculus... when possible pass the current ComCalculus 
	 * **/
	public abstract Money moneyValue(ComCalculus cc);
	
	/** 
	 * return the Total Money value of this Option 
	 * (numberOfLines * moneyValue) 
	 * @param cc may be null. In some cases AbstractOptionMoneyAmount may require 
	 * to do some calculus... when possible pass the current ComCalculus 
	 * **/
	public final Money moneyValueTotal(ComCalculus cc) {
		if (numberOfLines(cc) == 1) return moneyValue(cc);
		Money m = (Money) moneyValue(cc).copy();
		m.operationFactor(numberOfLines(cc));
		return m;
	}
	
	
	
	
	//------------- XML ------------------//
	/** XML **/
	protected AbstractOptionMoneyAmount() {}

}


/**
 *  $Log: AbstractOptionMoneyAmount.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.10  2004/11/08 17:49:36  perki
 *  done bug #17
 *
 *  Revision 1.9  2004/10/12 08:12:35  perki
 *  *** empty log message ***
 *
 *  Revision 1.8  2004/09/09 16:38:44  jvaucher
 *  - Finished the OptionCommissionAmountUnder, used by RateOnAmount WorkPlace
 *  - A bit of cleaning in the DoubleSideMap
 *
 *  Revision 1.7  2004/09/09 13:41:33  perki
 *  Added context to MoneyAmount
 *
 *  Revision 1.6  2004/09/03 11:47:53  kaspar
 *  ! Log.out -> log4j first half
 *
 *  Revision 1.5  2004/08/04 06:03:11  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.4  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.3  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.2  2004/03/02 14:42:47  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */