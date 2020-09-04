/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 7 avr. 2004
 *
 * $Id: STreeTableNode.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc.uitools.streetable;


/**
 * This class is an extension of STreeNode that provides 
 * a text to display in the table attached to the STree
 */
public interface STreeTableNode {

	/**
	 * Gets the value that should be displayed in the column at index columnIndex
	 * The column indexes start at 0
	 * @param columnIndex index of the requested column in the table
	 * @return an Object containing the data you want to display<br>
	 * if you return a Component it will be displayed as it is in the table<br>
	 * else the method toString() will be called on the object
	 */
	public Object getValueAt(int columnIndex);
	
	/**
	 * Determine if the cell at column index columnIndex should be highlighted
	 * @param columnIndex
	 * @return true if highlighted, otherwise false
	 */
	public boolean isHighLighted(int columnIndex);
	
}
/*
 * $Log: STreeTableNode.java,v $
 * Revision 1.2  2007/04/02 17:04:28  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.2  2004/04/12 13:47:04  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/04/09 18:01:01  carlito
 * *** empty log message ***
 *
 */
