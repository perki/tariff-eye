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
import com.simpledata.bc.tools.Lang;


/**
 * AssetsRateBySlice Subreport defines fields and data for the 
 * assetsRateBySlice.jrxml template. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: SubreportAssetsRateBySlice.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
class SubreportAssetsRateBySlice extends SpecializedSubreport {
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME           = "assetsRateBySlice";
	
	// Table header (see template)
	private final static String TITLE_FROM           = "HeadFrom";
	private final static String TITLE_EFFECTIVERATE  = "HeadEffectiveRate";
	private final static String TITLE_MARGINALRATE   = "HeadMarginalRate";
	private final static String TITLE_MINFEE         = "HeadMinFee";
	
	// App Strings
	private final static String PAGE_APPLIES         = "TextApplies";
	private final static String PAGE_RATE            = "TextRate";
	private final static String PAGE_CURRENCY        = "TextCurrency";
		
	// Keys 
	private final static String VALUE_APPLIES        = "ValueApplies";
	private final static String VALUE_RATE           = "ValueRate";
	private final static String VALUE_CURRENCY       = "ValueCurrency";
		
	
	/**
	 * Produce an empty row instance. SubreportMasterReport is 
	 * a producer of DataRows. 
	 */
	DataRow produceDataRow() {
		return new DataRow(); 
	}
	
	/**
	 * Return the text for the chosen option. 
	 */
	String getAppliesText( int option ) {
		switch ( option ) {
			case 0:
				return Lang.translate("Per amount");
			case 1:
				return Lang.translate("On sum of amounts");
		}
		assert false : 
			"Translation table for reporting should be in sync with the one in WorkPlaceAssetsRateBySlicePanel";
		
		return "Unknown Option";
	}
	
	/**
	 * Construct the Subreport. Look at the definition of this
	 * method to see what fields must be defined in the file
	 * called REPORT_NAME.jrxml.
	 *
	 * @param ctx Context for this rendering action.
	 * @param report What report should be produced ? See 
	 *               Subreport class for a discussion of the hierarchy
	 *               between reports and subreports. 
	 * @param applyOn One of WorkPlaceTrRateBySlice s APPLY_ON_* 
	 *                constants. 
	 */
	SubreportAssetsRateBySlice( RenderEventManager ctx, String report, int applyOn, boolean isMarginal, String currency ) {
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
		
		m_report.addTranslatedString( PAGE_APPLIES );
		m_report.addTranslatedString( PAGE_RATE );
		m_report.addTranslatedString( PAGE_CURRENCY );
		
		m_report.addAppValue( VALUE_APPLIES, getAppliesText( applyOn ) );
		m_report.addAppValue( 
			VALUE_RATE, 
			isMarginal ? 
				Lang.translate( "Marginal rate applies." ) 
				: Lang.translate( "Effective Rate applies" ) 
		);
		m_report.addAppValue( VALUE_CURRENCY, currency );
	}
	
	
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