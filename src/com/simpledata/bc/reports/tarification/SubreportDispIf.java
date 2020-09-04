/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;

/**
 * Subreport for creating the piece that describes an 'if'-type
 * dispatcher. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportDispIf.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
class SubreportDispIf extends SpecializedSubreport {

    /** Name of the subreport template that corresponds to usage of this Subreport */
    public static final String REPORT_NAME = "dispIf";
    
    /** Description of the dispatcher */
    private static final String DESCRIPTION = "Description";
    

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
	 * @param description Description shown on the subreport, 
	 * comming from the com.simpledata.bc.datamodel.Named interface
	 */
	SubreportDispIf( RenderEventManager ctx, String report, String description ) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addAppValue(DESCRIPTION, description);
		
		m_report.addJasperField("Title");
		m_report.addReportField("Subreport");
		
		m_report.freezeDefinitions();
	}
	
	/**
	 * This represents one row of data in the MasterReport
	 * report. 
	 * 
	 * Internal: This could be a way of abstracting away 
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
				"Result array must have proper length.";
			return result;
		}
	}
}
