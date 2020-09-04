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
 * @version $Id: SpecializedDataRow.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */

/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.common;

/**
 * Type that defines minimal methods for a row of 
 * data to be added to a specialized report. 
 * This interface is more concerned with how data is 
 * extracted from the row than it is concerned with 
 * how data is input into the row. 
 */
public abstract class SpecializedDataRow {
	/**
	 * Transform the contents of this data row into an 
	 * untyped array of Objects. The type information that
	 * gets lost is all the same verified afterwards, this 
	 * is just the way jasper wants its arrays. 
	 */
	public abstract Object[] toObjectArray();
}