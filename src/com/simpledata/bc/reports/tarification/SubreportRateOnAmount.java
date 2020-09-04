/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;


/**
 * Rate on amount. Displays a pourcentage and on what the 
 * pourcentage is applied, can either be one or more tarifs
 * or an amount. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: SubreportRateOnAmount.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
class SubreportRateOnAmount extends SpecializedSubreport {
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME      = "rateOnAmount";
	
	// Texts
	private static final String TEXT_RATE       = "TextRate";
		
	// Values
	private static final String VAL_RATE        = "ValueRate";
		
	// Fields
	private static final String FIELD_FROM      = "AmountOrReference"; 

	/**
	 * Construct the Subreport. Look at the definition of this
	 * method to see what fields must be defined in the file
	 * called REPORT_NAME.jrxml.
	 *
	 * @param ctx Context used while rendering this report. 
	 * @param report Report to render. 
	 */
	SubreportRateOnAmount( RenderEventManager ctx, String report ) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addJasperField( FIELD_FROM ); 
		m_report.freezeDefinitions();
		
		m_report.addTranslatedString( TEXT_RATE );
	}
	
	/**
	 * Sets the rate to display. 
	 * 
	 * @param rate Pourcentage rate to be applied. 
	 */
	void setRate( String rate ) {
		m_report.addAppValue( VAL_RATE, rate );
	}
	
	/**
	 * Produce an empty row instance. SubreportMasterReport is 
	 * a producer of DataRows. 
	 */
	DataRow produceDataRow() {
		return new DataRow(); 
	}
	
	/**
	 * This represents one row of data in the MasterReport
	 * report. 
	 */
	public static final class DataRow extends SpecializedDataRow {
		public Object amountOrReference; // toString will be called on this
		
		DataRow() {
			// empty
		}
		
		/**
		 * Convert this whole row into an Array of Objects. This
		 * array has size 4 and can be used for feeding the
		 * data to Jasper. 
		 */
		public Object[] toObjectArray() {
			Object[] result = {
				amountOrReference
			};
			return result;
		}
	}
}