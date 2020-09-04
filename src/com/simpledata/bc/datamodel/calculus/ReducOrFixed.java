/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ReducOrFixed.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.calculus;

import java.io.Serializable;

import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;

/**
 * This is a calculus that is made "After" teh calculus of the workSheet has
 * been made, It is used to calculate a reduction or a fixed price
 * instead of the normal Tarification.
 */
public class ReducOrFixed implements Serializable {
	private Currency xCurrency;
	private double xMax;
	private double xMin;
	private double xReduRate;
	private WorkSheet xOwner;
	
	public static int ACCEPT_REDUC_NO = 0;
	public static int ACCEPT_REDUC_FULL = 1;
	public static int ACCEPT_REDUC_ONLY_RATE = 2;
	
	/** create a ReducOrFixed attached to this WorkSheet **/
	public ReducOrFixed(WorkSheet daddy) {
		xOwner = daddy;
		xMin = 0d;
		xMax = -1d;
		xReduRate = 0d;
		xCurrency = Currency.getDefaultCurrency();
	}
	
	/** 
	 * get the Currency  
	 * **/
	public Currency getCurrency() {
		return xCurrency;
	}
	
	
	/** set the Currency **/
	public void setCurrency(Currency c) {
		if (xOwner.getAcceptedReducType() != ACCEPT_REDUC_FULL) return;
		if (c.xequals(xCurrency)) return ;
		xCurrency.setCurrencyCode(c.currencyCode());
		fireDataChange();
	}
	
	/** 
	 * get the Max value  <BR>
	 * @return -1 if the Max value is not used
	 * **/
	public double getMax() {
		return xMax;
	}
	
	
	/** get the Minimum Value **/
	public double getMin() {
		return xMin;
	}
	
	/** set the Minimum Value **/
	public void setMin(double min) {
		if (xOwner.getAcceptedReducType() != ACCEPT_REDUC_FULL) return;
		if (min >= 0 && min != xMin) {
			xMin = min;
			if (xMax != -1 && xMax < xMin) xMax = xMin;
			fireDataChange();
		}
	}
	
	/** 
	 * set the MaximumValue 
	 * @param max "-1" if not used13
	 * **/
	public void setMax(double max) {
		if (xOwner.getAcceptedReducType() != ACCEPT_REDUC_FULL) return;
		if (max == -1 || max >= 0) {
			if (max != xMax) {
				xMax = max;
				if (max != -1 && xMax < xMin) xMin = xMax;
				fireDataChange();
			}
		}
	}
	
	/** 
	 * get the Reduction rate<BR>
	 * 0 means 0% of reduction 1 means free
	 * @return a number between 0 and 1.
	 **/
	public double getReduRate() {
		return xReduRate;
	}
	
	/** 
	 * set the reduction rate<BR>
	 * @param reduRate betwen 0 and 1 included
	 * 0 means 0% of reduction 1 means free
	 **/
	public void setReduRate(double reduRate) {
		if (xOwner.getAcceptedReducType() == ACCEPT_REDUC_NO) return;
		if (reduRate != xReduRate) {
			if (reduRate >= 0 && reduRate <= 1) {
				xReduRate = reduRate;
			}
			fireDataChange();
		}
	}
	
	/** forward a data change event to owner **/
	public void fireDataChange() {
		xOwner.fireReductionOrFixedDataChange();
	}
	
	/**
	 * Apply this reduc or fixed price to this source calculus
	 * @param value the Money amount to modify (if needed)
	 */
	public void apply(Money value) {
		
		
		double srcValueMyCur = value.getValueDouble(xCurrency);
		double withReduRate = srcValueMyCur * (1 - getReduRate());
		
		// Maximum applies
		if (getMax() >= 0 && withReduRate > getMax()) {
			value.setValue(new Money(getMax(),xCurrency));
			return ;
		}
		
		// Minimum applies
		if (withReduRate < getMin()) {
			value.setValue(new Money(getMin(),xCurrency));
			return ;
		}
		
		// rate applies
		value.operationFactor(1 - getReduRate());
	}
	
	
	//-------------------- XML -----------------//
	
	
	/** XML **/
	public ReducOrFixed() {}
	/** XML **/
	
	/** XML **/
	public WorkSheet getXOwner() {
		return xOwner;
	}
	/** XML **/
	public void setXOwner(WorkSheet owner) {
		xOwner = owner;
	}
	/** XML **/
	public double getXReduRate() {
		return xReduRate;
	}
	/** XML **/
	public void setXReduRate(double reduRate) {
		xReduRate = reduRate;
	}

	/** XML **/
	public Currency getXCurrency() {
		return xCurrency;
	}
	/** XML **/
	public void setXCurrency(Currency currency) {
		xCurrency = currency;
	}
	/** XML **/
	public double getXMax() {
		return xMax;
	}
	/** XML **/
	public void setXMax(double max) {
		xMax = max;
	}
	/** XML **/
	public double getXMin() {
		return xMin;
	}
	/** XML **/
	public void setXMin(double min) {
		xMin = min;
	}
}

/*
 * $Log: ReducOrFixed.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:45  perki
 * First commit on sourceforge
 *
 * Revision 1.9  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.8  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.7  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.6  2004/05/23 12:16:22  perki
 * new dicos
 *
 * Revision 1.5  2004/05/22 17:20:46  perki
 * Reducs are visibles
 *
 * Revision 1.4  2004/05/22 08:39:35  perki
 * Lot of cleaning
 *
 * Revision 1.3  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.2  2004/05/20 17:05:30  perki
 * One step ahead
 *
 * Revision 1.1  2004/05/20 10:36:32  perki
 * *** empty log message ***
 *
 */