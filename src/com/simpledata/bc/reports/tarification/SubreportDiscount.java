/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 15 nov. 2004
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;

/**
 * SubreportDiscount. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportDiscount.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
public class SubreportDiscount extends SpecializedSubreport {

	// #### CONSTANTS #########################################################
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME      = "discount";
	
	/** Fields */
	private static final String SUBREPORT       = "Subreport";
	private static final String DISCOUNT_INFO   = "DiscountInfo";
	
	// #### CONSTRUCTOR #######################################################
	
	SubreportDiscount(RenderEventManager ctx, 
					  String report, 
					  String discountInfo,
					  SubreportTreeItem subreport) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
	
		m_report.addReportField(SUBREPORT);
		m_report.freezeDefinitions();
		
		m_report.addAppValue(DISCOUNT_INFO, discountInfo);
		
		SubreportDiscount.DataRow row = new SubreportDiscount.DataRow();
		row.subreport = subreport;
		addData(row);
	}
	
	// #### INNER CLASS #######################################################
	
	public static final class DataRow extends SpecializedDataRow {
		SubreportTreeItem subreport;

		public Object[] toObjectArray() {
			Object[] result = {subreport};
			return result;
		}
	}
}
