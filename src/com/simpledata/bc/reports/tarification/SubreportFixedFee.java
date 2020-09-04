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
import com.simpledata.bc.reports.common.SpecializedSubreport;


/**
 * Fixed fee subreport. This displays the fixed fee that is 
 * taken on this point. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: SubreportFixedFee.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
class SubreportFixedFee extends SpecializedSubreport {
	
	/** Name of the subreport that corresponds to usage of this Subreport */
	public static final String REPORT_NAME           = "fixedFee";
	
	// Texts
	private static final String TEXT_FIXED_FEE       = "TextFixedFee";
		
	// Values
	private static final String VAL_FIXED_FEE        = "ValueFixedFee";

	/**
	 * Construct the Subreport. Look at the definition of this
	 * method to see what fields must be defined in the file
	 * called REPORT_NAME.jrxml.
	 *
	 * @param ctx Context used while rendering this report. 
	 * @param report Report to render. 
	 * @param feeWithCurrency Fee amount with currency appended. 
	 */
	SubreportFixedFee( RenderEventManager ctx, String report, String feeWithCurrency ) {
		m_report = new Subreport( ctx, report, REPORT_NAME ); 
		
		m_report.addTranslatedString( TEXT_FIXED_FEE );
		m_report.addAppValue( VAL_FIXED_FEE, feeWithCurrency );
	}
}