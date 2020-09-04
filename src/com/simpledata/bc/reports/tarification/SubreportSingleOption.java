/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 28 sept. 2004
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;

/**
 * Interface for the SingleOption subreport.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportSingleOption.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
class SubreportSingleOption extends SpecializedSubreport {
	
	// #### CONSTANTS #########################################################
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME      = "singleOption";
	
	/** JR Parameters names */
	private static final String OPTION_TYPE 	= "OptionType";
	private static final String OPTION_NAME 	= "OptionName";
	
	/** JR Field name */
	private static final String OPTION_DETAIL 	= "OptionDetail";
	
	// #### CONSTRUCTOR #######################################################
	
	SubreportSingleOption(RenderEventManager ctx, String report, String type, String name) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addJasperField(OPTION_DETAIL);
		m_report.freezeDefinitions();
		
		m_report.addAppValue(OPTION_TYPE, type);
		m_report.addAppValue(OPTION_NAME, name);
	}
	
	// #### METHODS ###########################################################
	
	DataRow produceDataRow() {
		return new SubreportSingleOption.DataRow();
	}

	// #### INNER CLASS #######################################################
	
	public static final class DataRow extends SpecializedDataRow {
		String details;

		public Object[] toObjectArray() {
			Object[] result = {details};
			return result;
		}		
	}
}
