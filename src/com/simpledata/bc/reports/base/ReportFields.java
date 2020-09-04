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
 * @version $Id: ReportFields.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import java.util.Map;
import java.util.HashMap;

import com.simpledata.bc.tools.Lang;

/**
 * This is a collection of named fields that can be 
 * translated into Jasper's Parameters that each report
 * has. There will be two kinds of fields: Application 
 * generated values and Strings that just need translation. 
 */
class ReportFields {
	private HashMap/*<String, String>*/ m_fields;
		
	/**
	 * Construct a Report Fields collection. 
	 */
	public ReportFields() {
		m_fields = new HashMap();
	}
	
	/**
	 * Add an application string to the collection. 
	 * @param name Name of Application string. 
	 * @param value Value of Application. 
	 */
	void addAppValue( String name, Object value ) {
		m_fields.put( name, value );
	}
	
	/**
	 * Add a translation string to the collection. 
	 * Only the translation key must be given, since the 
	 * translation can be retrieved using the application 
	 * ways of doing so. 
	 * @param name Name of string to translate. 
	 */
	void addTranslatedString( String name ) {
		m_fields.put( name, Lang.translate( name ) ); 
	}
	
	/**
	 * Return the number of fields contained here. 
	 * @return Size of the fields collection. 
	 */
	int size() {
		return m_fields.size();
	}
	
	/**
	 * Access of fields in HashMap format. This folds out
	 * all contained values and creates the format that
	 * Jasper needs. 
	 */
	public Map produceJasperFields() {
		return m_fields;
	}
}