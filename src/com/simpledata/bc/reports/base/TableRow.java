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
 * @version $Id: TableRow.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;


/**
 * TableRow represents one row of data in a report
 * table. Each table row holds a reference to 
 * the fields definition that it is supposed to follow: 
 * If the types defined there don't match on addition
 * of cells, there will be an exception.  
 */
class TableRow {
	/** Data is contained here */
	private Object[] m_data; 
	/** Definition that this data complies with */
	private TableFields m_defs; 
		
	/**
	 * Return Jasper Object contained at column idx in data. 
	 * This will throw an assertion error if the column is not
	 * a Jasper column.
	 * @return one of the Jasper types described below. 
	 */
	Object getData( int idx ) {
		assert m_defs.isSubreportField( idx ) == false : 
			"The field ("+idx+") should be a jasper field.";
		
		return m_data[ idx ];
	}
	
	/**
	 * Return the subreport contained at column idx. 
	 * @return Subreport contained here or null.
	 */
	SubreportTreeItem getReport( int idx ) {
		assert m_defs.isSubreportField( idx ) : 
			"The field ("+idx+") should be a subreport field.";
		
		return (SubreportTreeItem) m_data[ idx ];
	}
	
	/**
	 * Constructor. 
	 * @param definition Field definition that this data 
	 *        row must comply with. 
	 */
	public TableRow( TableFields definition ) {
		assert definition.isFrozen() : 
			"Column definition must be frozen. ";
		
		m_defs = definition; 
		m_data = new Object[ definition.size() ]; 
	}
	
	/**
	 * Put the data item <code>item</code> at the 
	 * location i in the row. 
	 * @param i Index of column the item must be stored in. 
	 * @param item Data item to store. 
	 */
	public void put( int i, Object item ) {
		if ( m_defs.isSubreportField( i ) ) {
			assert item instanceof SubreportTreeItem || item == null : 
				"The location "+i+" demands a subreport or null."; 
		}
		
		assert i < m_data.length && i >= 0 : 
			"i must be in range of data array."; 
		
		m_data[i] = item;
	}
	
}