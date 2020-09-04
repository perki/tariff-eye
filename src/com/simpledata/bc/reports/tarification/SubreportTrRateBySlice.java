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

import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTrRateBySlice;
import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;
import com.simpledata.bc.tools.Lang;



/**
 * RateBySlice Subreport defines fields and data for the 
 * trRateBySlice.jrxml template. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: SubreportTrRateBySlice.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
class SubreportTrRateBySlice extends SpecializedSubreport {
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME           = "trRateBySlice";
	
	// Table header (see template)
	private final static String TITLE_FROM           = "HeadFrom";
	private final static String TITLE_EFFECTIVERATE  = "HeadEffectiveRate";
	private final static String TITLE_MARGINALRATE   = "HeadMarginalRate";
	private final static String TITLE_MINFEE         = "HeadMinFee";
	
	// App Strings
	private final static String PAGE_APPLIES         = "TextApplies";
	private final static String PAGE_RATE            = "TextRate";
	private final static String PAGE_CURRENCY        = "TextCurrency";
	private final static String TEXT_METHOD          = "TextMethod";
	private final static String SELL_TEXT            = "SellText";
	private final static String BUY_TEXT             = "BuyText";
	private final static String EACH_TEXT            = "EachText";
	private final static String SUM_TEXT             = "SumText";
	private final static String MARGINAL_TEXT        = "MarginalText";
	private final static String EFFECTIVE_TEXT       = "EffectiveText";
		
	// Keys 
	private final static String VALUE_CURRENCY       = "ValueCurrency";
	private final static String SELL_APPLIES         = "SellApplies";
	private final static String BUY_APPLIES          = "BuyApplies";
	private final static String MARGINAL_RATE        = "MarginalRate";
	private final static String EACH_METHOD          = "EachMethod";
	
	
	// Locale
	private final static String TXT_EACH_TRANSACTION = 
		"Rate slice computed for each transation";
	private final static String TXT_VOLUME_TOTAL =
		"Rate slice computed for the total transaction volume";
	
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
	void setAppliesOn(int option) {
		switch ( option ) {
		case WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_AND_OUTGOING:
			m_report.addAppValue(SELL_APPLIES, new Boolean(true));
		m_report.addAppValue(BUY_APPLIES, new Boolean(true));
		break;
		case WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_TO_BANK:
			m_report.addAppValue(SELL_APPLIES, new Boolean(false));
		m_report.addAppValue(BUY_APPLIES, new Boolean(true));
		break;
		case WorkPlaceTrRateBySlice.APPLY_ON_OUTGOING_FROM_BANK:
			m_report.addAppValue(SELL_APPLIES, new Boolean(true));
		m_report.addAppValue(BUY_APPLIES, new Boolean(false));
		break;
		default:
			assert false : 
				"Translation table for reporting should be in sync with the one in WorkPlaceTrRateBySlicePanel";
			
		}
	}
	
	/**
	 * Construct the Subreport. Look at the definition of this
	 * method to see what fields must be defined in the file
	 * called REPORT_NAME.jrxml.
	 *
	 * @param ctx Context for this report render action.
	 * @param report What report should be produced ? See 
	 *               Subreport class for a discussion of the hierarchy
	 *               between reports and subreports. 
	 * @param applyOn One of WorkPlaceTrRateBySlice s APPLY_ON_* 
	 *                constants. 
	 */
	SubreportTrRateBySlice( RenderEventManager ctx, 
							String report, 
							int applyOn,
							int method,
							boolean isMarginal, 
							String currency ) {
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
		m_report.addTranslatedString( TEXT_METHOD );
		m_report.addTranslatedString( SELL_TEXT );
		m_report.addTranslatedString( BUY_TEXT );
		m_report.addTranslatedString( EACH_TEXT );
		m_report.addTranslatedString( SUM_TEXT );
		m_report.addTranslatedString( MARGINAL_TEXT );
		m_report.addTranslatedString( EFFECTIVE_TEXT );
		
		m_report.addAppValue( MARGINAL_RATE , new Boolean(isMarginal));
		
		m_report.addAppValue( VALUE_CURRENCY, currency );
		m_report.addAppValue( EACH_METHOD, new Boolean(
				method == WorkPlaceTrRateBySlice.EACH_TRANSACTION));
		setAppliesOn(applyOn);
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
			// emtpy
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