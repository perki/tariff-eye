/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionDoublePositive.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;

/**
 *An Option that accepts only doubles >= 0;
 * BUT it can also accept negatives if you want (!!!)
 */
public class OptionDoublePositive extends BCOption {
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Double Positive";

	private double xDoubleValue= 0;

	/** Allow negative positive (!!!) */
	private boolean m_negativeAllowed;
	
	/**
	* Constructor.. 
	*/
	public OptionDoublePositive(WorkSheet workSheet, String title) {
		this (workSheet, title, false);
	}
	
	/** Local constructor */
	protected OptionDoublePositive(WorkSheet workSheet, String title, boolean negativeAllowed) {
		super(workSheet, title);
		xDoubleValue= 0;
		m_negativeAllowed = negativeAllowed;
	}

	/**
	* get the Value of this option
	*/
	public double getDoubleValue() {
		return xDoubleValue;
	}

	/**
	* set the Value of this option
	*/
	public void setDoubleValue(double value) {
		if (value < 0 && (!m_negativeAllowed)) value= 0;
		if (this.xDoubleValue != value) {
			this.xDoubleValue = value;
			fireDataChange();
		}
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.BCOption#getValue()
	 */
	public String getValue() {
		return "" + xDoubleValue;
	}
	
	protected int getStatusPrivate() { return STATE_OK; }

	//------------------- XML ------------------//
	
	/** XML CONSTRUCTOR **/
	public OptionDoublePositive() {
		m_negativeAllowed = false;
	}
	
	/** XML */
	public void setNegativeAllowed(boolean value) {
		m_negativeAllowed = value;
	}
	
	/** XML */
	public boolean getNegativeAllowed() {
		return m_negativeAllowed;
	}

	/** XML **/
	public double getXDoubleValue() {
		return xDoubleValue;
	}
	/** XML **/
	public void setXDoubleValue(double d) {
		xDoubleValue= d;
	}

}

/**
 *  $Log: OptionDoublePositive.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.8  2004/09/10 16:29:48  jvaucher
 *  Allows negative percentage for discount
 *
 *  Revision 1.7  2004/08/02 15:33:09  kaspar
 *  ! beautified line endings on two options
 *  + Introduction of working code for WorkplaceRateOnAmount
 *
 *  Revision 1.6  2004/05/20 09:39:43  perki
 *  *** empty log message ***
 *
 *  Revision 1.5  2004/04/12 12:30:27  perki
 *  Calculus
 *
 *  Revision 1.4  2004/03/23 19:45:18  perki
 *  New Calculus Model
 *
 *  Revision 1.3  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.2  2004/03/12 14:06:10  perki
 *  Vaseline machine
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */