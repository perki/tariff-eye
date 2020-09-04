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
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;

/**
 * Interface for the OptionsList subreport.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportOptionsList.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
public class SubreportOptionsList extends SpecializedSubreport {
	
	// #### CONSTANTS #########################################################
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME      = "optionsList";
	
	/** Fields */
	private static final String SINGLE_OPTION    = "SingleOption";
	
	// #### CONSTRUCTOR #######################################################
	
	SubreportOptionsList(RenderEventManager ctx, String report) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
	
		m_report.addReportField(SINGLE_OPTION);
		m_report.freezeDefinitions();
	}
	
	// #### METHODS ###########################################################
	
	DataRow produceDataRow() {
		return new SubreportOptionsList.DataRow();
	}
	
	// #### INNER CLASS #######################################################
	
	public static final class DataRow extends SpecializedDataRow {
		SubreportTreeItem singleOption;

		public Object[] toObjectArray() {
			Object[] result = {singleOption};
			return result;
		}
	}
}
