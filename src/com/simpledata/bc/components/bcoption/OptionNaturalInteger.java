/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: OptionNaturalInteger.java,v 1.2 2007/04/02 17:04:24 perki Exp $
*/
package com.simpledata.bc.components.bcoption;

import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;

/**
* An Option that accepts only natural interger ( >= 0);
*/
public class OptionNaturalInteger extends BCOption {
	
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE = "NaturalInteger Option";
	
	private int xIntValue = 0;
	
	/**
	* Constructor.. 
	*/
	public OptionNaturalInteger (WorkSheet workSheet,String title) {
		super(workSheet,title);
		xIntValue = 0;
	}
	
	/**
	* get the Value of this option
	*/
	public int getInValue() {
		return xIntValue;
	}
	
	
	/**
	* set the Value of this option
	*/
	public void setIntValue(int value) {
		if (value < 0) value = 0;
		if (this.xIntValue != value) {
			this.xIntValue = value;
			fireDataChange();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.BCOption#getValue()
	 */
	public String getValue() {
		return ""+xIntValue;
	}
	
	protected int getStatusPrivate() { return STATE_OK; }
	
	//------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public OptionNaturalInteger() {}
	
	/**
	 * XML
	 */
	public int getXIntValue() {
		return xIntValue;
	}

	/**
	 * XML
	 */
	public void setXIntValue(int i) {
		xIntValue= i;
	}

}
/** 
*$Log: OptionNaturalInteger.java,v $
*Revision 1.2  2007/04/02 17:04:24  perki
*changed copyright dates
*
*Revision 1.1  2006/12/03 12:48:38  perki
*First commit on sourceforge
*
*Revision 1.9  2004/05/20 09:39:43  perki
**** empty log message ***
*
*Revision 1.8  2004/04/12 12:30:28  perki
*Calculus
*
*Revision 1.7  2004/03/23 19:45:18  perki
*New Calculus Model
*
*Revision 1.6  2004/03/18 15:43:32  perki
*new option model
*
*Revision 1.5  2004/02/25 15:34:26  perki
**** empty log message ***
*
*Revision 1.4  2004/02/22 10:43:56  perki
*File loading and saving
*
*Revision 1.3  2004/02/19 19:47:34  perki
*The dream is coming true
*
*Revision 1.2  2004/02/05 09:58:11  perki
*Transactions are welcome aboard
*
*Revision 1.1  2004/02/05 07:46:54  perki
*Transactions are welcome aboard
*
*/

