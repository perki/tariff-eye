/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionPerBaseTen.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.uitools.SNumField;

/**
 * A simple extention of OptionDoublePositive that holds a percentage
 * a permillage .. etc...
 */
public class OptionPerBaseTen extends  OptionDoublePositive {
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Percentage,PerMil �/. �/..";
	
	public final static int[] ACCEPTED_DIVIDERS = {1,100,1000};
	
	/** the divider **/
	public int xDivider = 100;

	
	/**
	* Constructor.. 
	*
	* FIXME: This constructor manages to callback to toString of this
	* class before constructor initializes the xDivider member variable. 
	*/
	public OptionPerBaseTen(WorkSheet workSheet, String title) {
		super(workSheet, title, true);
	}
	
	/** get the Divider */
	public int getDivider() {
		return xDivider;
	}

	/** 
	 * set the divider. must be one of OptionPerBaseTen.ACCEPTED_DIVIDERS
	 * @return true if succeded
	 * */
	public boolean setDivider(int divider) {
		boolean ok = false;
		for (int i = 0; i < ACCEPTED_DIVIDERS.length; i++)
			if (ACCEPTED_DIVIDERS[i] == divider) ok = true;
		if (! ok) return false;
		
		if (xDivider != divider) {
			xDivider= divider;
			fireDataChange();
		}
		return true;
	}
	
	
	//	------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public OptionPerBaseTen() {
		setNegativeAllowed(true);
	}
	
	/** XML */
	public int getXDivider() {
		if (xDivider == 0) return 100;
		return xDivider;
	}

	/** XML */
	public void setXDivider(int i) {
		xDivider= i;
	}
	
	public String toString() {
		double rate = getDoubleValue(); 
		int divider = getDivider(); 
		
		String divText = "%";
		switch (divider) {
			case 1: 
				divText = ""; 
				break; 
			case 1000: 
				divText = "�";
				break;
			case 100: 
			default:
				divText = "%";
				break;
		}
		
		return SNumField.formatNumber( rate * divider, 2, true ) + divText;
	}

}


/**
 *  $Log: OptionPerBaseTen.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.7  2004/09/10 16:29:48  jvaucher
 *  Allows negative percentage for discount
 *
 *  Revision 1.6  2004/08/17 13:54:09  kaspar
 *  + Comment that describes remaining issue
 *
 *  Revision 1.4  2004/08/02 15:33:09  kaspar
 *  ! beautified line endings on two options
 *  + Introduction of working code for WorkplaceRateOnAmount
 *
 *  Revision 1.3  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.2  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */