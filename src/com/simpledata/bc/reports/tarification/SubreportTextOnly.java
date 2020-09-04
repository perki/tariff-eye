/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 29 sept. 2004
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;

/**
 * Interface for TextOnly subreport.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportTextOnly.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class SubreportTextOnly extends SpecializedSubreport {
	// #### CONSTANTS #########################################################
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME      = "textOnly";
	
	/** Parameter */
	public static final String TEXT			    = "Text";
	
	// #### CONSTRUCTOR #######################################################
	
	SubreportTextOnly(RenderEventManager ctx, String report, String text) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
	
		m_report.freezeDefinitions();
		
		m_report.addAppValue(TEXT, text);
	}
}


