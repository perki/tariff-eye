/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 15 sept. 2004
 */
package com.simpledata.bc.reports.tarification;

import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFutFeeBySlice;
import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;
import com.simpledata.bc.tools.Lang;

/**
 * This class represents the Subreport for FutFeeBySlice WorkPlace.
 * The corresponding template is futFeeBySlice.jrxml
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportFutFeeBySlice.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class SubreportFutFeeBySlice extends SpecializedSubreport {
	
	// #### CONSTANTS #########################################################
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME           = "futFeeBySlice";
	
	// Table header (see template)
	private final static String TITLE_FROM           = "HeadFrom";
	private final static String TITLE_FEE            = "HeadFee";
	
	// App Strings
	private final static String PAGE_CURRENCY        = "TextCurrency";
	private final static String TEXT_APPLIES         = "TextApplies";
		
	// Keys 
	private final static String VALUE_CURRENCY       = "ValueCurrency";
	private final static String VALUE_APPLIES        = "ValueApplies";
	
	// Locale
	private final static String APPLIES_ON_CLOSING   = "Applies on closing";
	private final static String APPLIES_ON_OPENING   = "Applies on opening";
	private final static String APPLIES_ON_ALL       = "Applies on all futures";

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
	 * @param currency The currency used for this rate by slice.
	 * @param applies Shown in the report. Tell how the fee is applied regards
	 *                to WorkPlaceFutFeeBySlice constants. 
	 */
	SubreportFutFeeBySlice( RenderEventManager ctx, 
								  String report, 
								  String currency,
								  int applies) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addJasperField("TableFrom");
		m_report.addJasperField("TableFee");
		
		m_report.freezeDefinitions();
		
		m_report.addTranslatedString( TITLE_FROM );
		m_report.addTranslatedString( TITLE_FEE );
		
		m_report.addTranslatedString( PAGE_CURRENCY );
		m_report.addTranslatedString( TEXT_APPLIES );
		
		switch (applies) {
		case WorkPlaceFutFeeBySlice.APPLY_ON_CLOSEING:
			m_report.addAppValue(VALUE_APPLIES, Lang.translate(APPLIES_ON_CLOSING));
			break;
		case WorkPlaceFutFeeBySlice.APPLY_ON_OPENING:
			m_report.addAppValue(VALUE_APPLIES, Lang.translate(APPLIES_ON_OPENING));
			break;
		case WorkPlaceFutFeeBySlice.APPLY_ON_OPENING_AND_CLOSEING:
			m_report.addAppValue(VALUE_APPLIES, Lang.translate(APPLIES_ON_ALL));
			break;
		}
		
		m_report.addAppValue( VALUE_CURRENCY, currency );
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
		public String fee;
		
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
				fee
			};
			return result;
		}
	}
}
