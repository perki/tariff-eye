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
 * @version $Id: SubreportDispSequencer.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */

/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;


/**
 * Master Report Subclass that defines a typesafe 
 * way of adding data to the MasterReport template. 
 * This class should only be used with variants of the 
 * MasterReport.jrxml. 
 * The report that is dynamically created can be retrieved.
 */
class SubreportDispSequencer extends SpecializedSubreport {
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME = "dispSequencer";
	
	/**
	 * Produce an empty row instance. SubreportMasterReport is 
	 * a producer of DataRows. 
	 */
	DataRow produceDataRow() {
		return new DataRow(); 
	}
	
	/**
	 * Construct the Subreport. Look at the definition of this
	 * method to see what fields must be defined in the file
	 * called REPORT_NAME.jrxml.
	 * 
	 * @param ctx Context for this rendering action.
	 * @param report Report name to scan for subreport template. 
	 */
	SubreportDispSequencer( RenderEventManager ctx, String report ) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addJasperField("Title");
		m_report.addReportField("Subreport");
		
		m_report.freezeDefinitions();
	}
	
	public void addHeader(String text) {
		m_report.addAppValue("Header",text);
	}
	
	/**
	 * This represents one row of data in the MasterReport
	 * report. 
	 * 
	 * This is a way of abstracting away 
	 * all of the addData methods in the different Subreports. 
	 */
	public static class DataRow extends SpecializedDataRow {
	    private static final int ARRAYSIZE = 2; 
	    
	    public String title; 
	    public SubreportTreeItem subreport;
	    
	    public DataRow() {
	    	// empty
	    }
		/**
		 * Convert this whole row into an Array of Objects. This
		 * array has size ARRAYSIZE and can be used for feeding the
		 * data to Jasper. 
		 */
		public Object[] toObjectArray() {
			Object[] result = {
				title, 
				subreport
			};
			
			assert result.length == ARRAYSIZE : 
				"Returned array must have good size.";
			return result;
		}
	}
}