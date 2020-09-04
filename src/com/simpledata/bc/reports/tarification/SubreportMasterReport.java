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
 * @version $Id: SubreportMasterReport.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */

/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.tarification;

import java.awt.Image;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;
import com.simpledata.bc.reports.templates.TemplateFactory;

/**
 * Master Report Subclass that defines a typesafe 
 * way of adding data to the MasterReport template. 
 * This class should only be used with variants of the 
 * MasterReport.jrxml. 
 *
 * The report that is dynamically created can be retrieved.
 * 
 * Note: The groups in MasterReport template have names that
 * MUST match those defined in TableOfContentsScriptlet.GROUP_* !
 */
class SubreportMasterReport extends SpecializedSubreport {
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME = "MasterReport";
	public static final String REPORTTOC_NAME = "staticToc";
	
	/** Parameter */
	private static final String REPORT_TITLE = "ReportTitle";
	
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
	 * @param ctx The context used in rendering this report. 
	 * @param report Report the template should be retrieved from. 
	 */
	SubreportMasterReport( RenderEventManager ctx, String report) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		// Construct report field definition.
		m_report.addJasperField( "Title1" ); 
		m_report.addJasperField( "Title2" ); 
		m_report.addJasperField( "Title3" ); 
		
		m_report.addJasperField( "TariffTitle" );
		m_report.addJasperField( "TariffDescription" );
		m_report.addJasperField( "Level" );
		m_report.addReportField( "Subreport" ); 
		
		m_report.freezeDefinitions();
		
		m_report.addAppValue( 
			"TOCReport", 
			TemplateFactory.getJasperTemplate(
				report, 
				REPORTTOC_NAME
			)
		);
		
		m_report.addTranslatedString( "TextTOCTitle" );
		m_report.addTranslatedString( "FileNameText" );
		m_report.addTranslatedString( "PublishDateText" );
	}
	
	void setMasterTitle(String title) {
		m_report.addAppValue(REPORT_TITLE, title);
	}
	
	void setSubTitle(String subtitle) {
		m_report.addAppValue("ReportSubtitle", subtitle);
	}
	
	void setPublishDate(String value) {
		m_report.addAppValue("PublishDateValue", value);
	}
	
	void setFileName(String value) {
		m_report.addAppValue("FileNameValue", value);
	}
	
	void setTarificationIcon (Image image) {
		m_report.addAppValue("TarificationIcon", image);
	}
	
	/**
	 * This represents one row of data in the MasterReport
	 * report. 
	 * 
	 * Internal: This could be a way of abstracting away 
	 * all of the addData methods in the different Subreports. 
	 */
	public static class DataRow extends SpecializedDataRow {
		private static final int TITLES = 3; 
		private static final int ARRAYSIZE = TITLES+4; 
		
		/** Title on i'th level. */
		String[] titles; 
		/** Subreport down below that contains the 
		 *  real report content. 
		 */ 
		SubreportTreeItem subreport; 
		/** Number of shown level in the report */
		int level;
		/** Title of the Tariff */
		String tariffTitle;
		/** Description of the Tariff */
		String tariffDescription;
		/**
		 * Initializes the arrays to the correct sizes. 
		 */
		DataRow() {
			titles = new String[ TITLES ];
		}
		/**
		 * Convert this whole row into an Array of Objects. This
		 * array has size ARRAYSIZE and can be used for feeding the
		 * data to Jasper. 
		 */
		public Object[] toObjectArray() {
			Object[] data = new Object[ ARRAYSIZE ];
			for (int i=0; i<TITLES; ++i) 
				data[i] = titles[i];
			data[TITLES]   = tariffTitle;
			data[TITLES+1] = tariffDescription;
			data[TITLES+2] = new Integer(level);
			data[TITLES+3] = subreport;
			
			return data;
		}
	}
}