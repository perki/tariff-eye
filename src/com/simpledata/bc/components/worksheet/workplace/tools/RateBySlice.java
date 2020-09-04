/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: RateBySlice.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
 
/**
 * Tools for the Workplace classes. 
 */
package com.simpledata.bc.components.worksheet.workplace.tools;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Copiable;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.OrderedMapOfDoubles;

/**
 * This is a utility class for all workplace using Rate by Slices
 * Based on a Currency. 
 * Container for all RateBySlice values: Values are stored in an 
 * ordered Hash. 
 */
public class RateBySlice implements Serializable, Copiable, DataBySlice {
	private static final Logger m_log = Logger.getLogger( RateBySlice.class );
		
	/** the slices **/
	private OrderedMapOfDoubles omod= null;

	/** currency of the slices values **/
	public Currency xCurrency= null;

	/** define if based on a marginal rate or effective rate **/
	private boolean isMarginalRate = true;
	
	/** Constructor **/
	public RateBySlice(boolean isMarginal) {
		omod= new OrderedMapOfDoubles(new RateBySliceValue(new Double(0.0d)));
		this.isMarginalRate = isMarginal;
		setCurrency(new Currency(null));
	}

	
	
	/**
	 * get the used Currency<BR>
	 * Note: Also use by XML
	 */
	public Currency getCurrency() {
	   return xCurrency;
	}
	/**
	 * change the currency
	 * <BR>Note: Also use by XML
	 */
	public void setCurrency(Currency currency) {
		this.xCurrency= currency;
	}
	
	/**
	 * get the used OrderedMapOfDoubles
	 * <BR>Note: Also use by XML
	 */
	public OrderedMapOfDoubles getOmod() {
		return omod;
	}
	
	/**
	 * get the Comission taken by this RBS for this Money amount<BR>
	 * NOTE: as requested if amount value == 0 then result = 0!
	 */
	public Money getCom(Money amount) {
		Money result = new Money(0d);
		
		final double moneyValue = amount.getValueDouble(getCurrency());
		
		// as requested a transaction or assets with a value = 0
		// will be treated as non existant...
		if (moneyValue == 0) return result;
		
		
		
		Object o = omod.getValueLastLower(moneyValue);
		if (o == null || ! (o instanceof RateBySliceValue)) {
			m_log.error( "Cannot find a RateBySliveValue" );
			return result;
		}
		
		RateBySliceValue rbs = (RateBySliceValue) o;
		
		// the marginal fee is dropped if not needed
		Money marginalCalc = null;
		if (isMarginalRate)  marginalCalc = new Money(0d);
		 
		double lastKeyValue = 0d; // used to know slices weigth (in money)
		
		// get a minimum (if applicable)
		double minimumFee = 0d;
		double rate = 0d;
		// find the last minimum fee applicable
		// and the last rate applicable
		for (int i = 0 ; i < omod.size() ; i++) {
			RateBySliceValue rbs2 = (RateBySliceValue) omod.getValueAtIndex(i);
			
			// calculate the marginal fee based on
			// the previous rate and previous keyValue
			if (isMarginalRate && 
					(omod.getKeyAtIndex(i) - lastKeyValue) > 0 && rate > 0)
			marginalCalc.operation(
					new Money(omod.getKeyAtIndex(i) - lastKeyValue),rate);
			
			
			if (rbs2.isFixed())
				minimumFee = rbs2.getXFixedMin().get();
			if (rbs2.isRate())
				rate = rbs2.getXRate().doubleValue() / 100;
			
			lastKeyValue = omod.getKeyAtIndex(i);
			if (rbs2 == rbs)
				break;
		}
		
		// calculate rate for the remaining money value 
		if (isMarginalRate && (moneyValue - lastKeyValue) > 0 && rate > 0) {
			marginalCalc.operation(new Money(moneyValue - lastKeyValue),rate);
		}
		
		// calculate the fee for this amount
		double fee = moneyValue * rate;
		
		if (isMarginalRate) {
			fee = marginalCalc.getValueDefCurDouble();
		}
			
		if (minimumFee > fee) {
			result.operation(new Money(minimumFee,getCurrency()),1);
		} else {
			if (isMarginalRate) {
				result.operation(marginalCalc,1);
			} else {
				result.operation(amount,rate);
			}
		}
		return result;
	}

	
	/**
	 * copy (like clone)
	 */
	public Copiable copy() {
		RateBySlice copy = new RateBySlice();
		copy.setOmod((OrderedMapOfDoubles) omod.copy());
		copy.setCurrency((Currency) getCurrency().copy());
		copy.setIsMarginalRate(getIsMarginalRate());
		return copy;
	}
	
	/**
	 * Note: also used by XML
	 * @return true if this is a marginal rate base
	 */
	public boolean getIsMarginalRate() {
		return isMarginalRate;
	}
	/**
	 * Note: also used by XML
	 * @param b true if this is a marginal rate base
	 */
	public void setIsMarginalRate(boolean b) {
		this.isMarginalRate = b;
	}
	
	 /**
     * @see DataBySlice#createLineAt(double)
     */
    public void createLineAt(double key) {
    	omod.put(key, new RateBySliceValue());
    }
	
	
	//-------------- XML ----------------//
    
	/**
	 * XML DO NOT USE!! 
	 * @see #(boolean isMarginal)
	 */
	public RateBySlice() { }
    
	/**
	 * XML
	 */
	public void setOmod(OrderedMapOfDoubles doubles) {
		omod= doubles;
	}


   

	
}

/**
 *  $Log: RateBySlice.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:40  perki
 *  First commit on sourceforge
 *
 *  Revision 1.29  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.28  2004/09/23 06:27:56  perki
 *  LOt of cleaning with the Logger
 *
 *  Revision 1.27  2004/09/22 09:44:54  perki
 *  repaired Currency bug
 *
 *  Revision 1.26  2004/09/22 06:47:04  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.25  2004/09/14 07:48:24  perki
 *  Futures
 *
 *  Revision 1.24  2004/09/10 16:51:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.23  2004/09/08 16:35:14  perki
 *  New Calculus System
 *
 *  Revision 1.22  2004/09/03 11:47:53  kaspar
 *  ! Log.out -> log4j first half
 *
 *  Revision 1.21  2004/07/26 20:36:09  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: RateBySlice.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.20  2004/05/13 07:45:33  perki
 *  Marginal and effective rates
 *
 *  Revision 1.19  2004/05/12 13:38:06  perki
 *  Log is clever
 *
 *  Revision 1.17  2004/05/12 10:11:12  perki
 *  *** empty log message ***
 *
 *  Revision 1.16  2004/05/11 15:53:00  perki
 *  more calculus
 *
 *  Revision 1.15  2004/05/05 08:26:54  perki
 *  cleaning
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
 *  Revision 1.11  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.10  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.9  2004/03/16 16:30:11  perki
 *  *** empty log message ***
 *
 *  Revision 1.8  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.7  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.6  2004/03/03 20:36:48  perki
 *  bonne nuit les petits
 *
 *  Revision 1.5  2004/03/03 18:19:22  perki
 *  ziuiasdhgasjk
 *
 *  Revision 1.4  2004/03/03 14:42:11  perki
 *  Un petit bateau
 *
 *  Revision 1.3  2004/02/22 10:43:57  perki
 *  File loading and saving
 *
 *  Revision 1.2  2004/02/19 21:32:16  perki
 *  now 1Gig of ram
 *
 *  Revision 1.1  2004/02/19 20:20:11  perki
 *  nicer
 *
 */