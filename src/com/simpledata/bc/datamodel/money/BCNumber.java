/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: BCNumber.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.money;

import java.io.Serializable;
import java.math.BigDecimal;

import com.simpledata.bc.datamodel.Copiable;

/**
 * A class that handles numbers (mainly money numbers)
 */
public class BCNumber   implements Copiable, Serializable {
	
	private double xD;
	
	public BCNumber(double d) {
		xD = d;
	}
	
	/** get the double value **/
	public double get() {
		return xD;
	}
	
	/** set the double value **/
	public void set(double d) {
		xD = d;
	}
	
	public String toString() {
		return BCNumber.toString(get());
	}
	
	public static String toString(double d) {
		return new BigDecimal(d).toString();
	}

	/**
	 * @see com.simpledata.bc.datamodel.Copiable#copy()
	 */
	public Copiable copy() {
		return new BCNumber(get());
	}
	
	//--------------------- XML ------------------//
	
	/** XML */
	public BCNumber() {}

	/** XML */
	public double getXD() {
		return xD;
	}

	/** XML */
	public void setXD(double double1) {
		xD= double1;
	}
	

}


/**
 *  $Log: BCNumber.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:36  perki
 *  First commit on sourceforge
 *
 *  Revision 1.4  2004/05/22 08:39:35  perki
 *  Lot of cleaning
 *
 *  Revision 1.3  2004/03/18 16:26:54  perki
 *  new option model
 *
 *  Revision 1.2  2004/03/16 16:30:11  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/03/16 14:09:49  perki
 *  Big Numbers are welcome aboard
 *
 */