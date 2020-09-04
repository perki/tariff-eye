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
 * @version $Id: SubreportTreeItem.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public interface SubreportTreeItem {
	
	
	/**
	 * Return the report template for this Subreport. 
	 * @return template 
	 */
	public JasperReport getReport();
	
	/**
	 * Return the data table for this Subreport. 
	 * @return data table. 
	 */
	public AbstractTableModel getDatasource();
	
	/**
	 * Returns the parameters for the Jasper report. 
	 */
	public Map getFields();
	
	/**
	 * Return the data table for this Subreport. 
	 * This is probably introducing some coupling, 
	 * but I cannot see how one would do differently. 
	 * @return data table. 
	 */
	public Table getTable();
		
	/**
	 * Render the report to be able to display or 
	 * save it. 
	 * @return Jasper device indepent report format. 
	 */
	public JasperPrint renderReport();
}
