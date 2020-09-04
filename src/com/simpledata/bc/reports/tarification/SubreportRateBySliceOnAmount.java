/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 13 sept. 2004
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;
import com.simpledata.bc.tools.Lang;

/**
 * This class implements the java layer for using the template 
 * rateBySliceOnAmount subreport.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportRateBySliceOnAmount.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
public class SubreportRateBySliceOnAmount extends SpecializedSubreport {

	// #### CONSTANTS #########################################################
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME           = "rateBySliceOnAmount";
	
	// Table header (see template)
	private final static String TITLE_FROM           = "HeadFrom";
	private final static String TITLE_EFFECTIVERATE  = "HeadEffectiveRate";
	private final static String TITLE_MARGINALRATE   = "HeadMarginalRate";
	private final static String TITLE_MINFEE         = "HeadMinFee";
	
	// App Strings
	private final static String PAGE_RATE            = "TextRate";
	private final static String PAGE_CURRENCY        = "TextCurrency";
	private final static String TEXT_REFERENCE       = "TextReference";
		
	// Keys 
	private final static String VALUE_RATE           = "ValueRate";
	private final static String VALUE_CURRENCY       = "ValueCurrency";
	private final static String VALUE_REFERENCE      = "ValueReference";

	// #### CONSTRUCTOR #######################################################
	
	/**
	 * Construct the Subreport. Look at the definition of this
	 * method to see what fields must be defined in the file
	 * called REPORT_NAME.jrxml.
	 *
	 * @param ctx Context for this report render action.
	 * @param report What report should be produced ? See 
	 *               Subreport class for a discussion of the hierarchy
	 *               between reports and subreports. 
	 * @param isMarginal Is this rate by slice using a marginal rate calculation
	 * @param currency The currency used for this rate by slice.
	 * @param reference How is the amount refered. 
	 */
	SubreportRateBySliceOnAmount( RenderEventManager ctx, 
								  String report, 
								  boolean isMarginal, 
								  String currency,
								  String reference) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addJasperField("TableFrom");
		m_report.addJasperField("TableMarginal");
		m_report.addJasperField("TableEffective");
		m_report.addJasperField("TableMinFee");
		
		m_report.freezeDefinitions();
		
		m_report.addTranslatedString( TITLE_FROM );
		m_report.addTranslatedString( TITLE_MARGINALRATE );
		m_report.addTranslatedString( TITLE_EFFECTIVERATE );
		m_report.addTranslatedString( TITLE_MINFEE );
		
		m_report.addTranslatedString( PAGE_RATE );
		m_report.addTranslatedString( PAGE_CURRENCY );
		m_report.addTranslatedString( TEXT_REFERENCE );
		
		m_report.addAppValue( 
			VALUE_RATE, 
			isMarginal ? 
				Lang.translate( "Marginal rate applies." ) : 
				Lang.translate( "Effective Rate applies" ) 
		);
		m_report.addAppValue( VALUE_CURRENCY, currency );
		m_report.addAppValue( VALUE_REFERENCE, reference );
	}
	
	// #### METHODS ###########################################################
	/**
	 * Produce an empty row instance. SubreportMasterReport is 
	 * a producer of DataRows. 
	 */
	DataRow produceDataRow() {
		return new DataRow(); 
	}
	
	// #### INNER CLASSES #####################################################
	/**
	 * This represents one row of data in the MasterReport
	 * report. 
	 */
	public static final class DataRow extends SpecializedDataRow {
		public String startValue;
		public String margRate;
		public String effRate;
		public String minFee;
		
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
				startValue, 
				margRate, 
				effRate, 
				minFee
			};
			return result;
		}
	}
}
