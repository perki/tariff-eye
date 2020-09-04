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
package com.simpledata.bc.reports.common;

import java.util.HashSet;
import java.util.Set;

import com.simpledata.bc.reports.base.EmptyRenderEventListener;
import com.simpledata.bc.reports.templates.TemplateFactory;

/**
 * The report counting render context counts the number
 * of distinct, uncompiled reports that are used in a 
 * report. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: ReportCountListener.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class ReportCountListener extends EmptyRenderEventListener {
	private int m_reports;
	private Set/*<String>*/ m_seenReports; // what reports are already counted in ? 
	
	/**
	 * Constructs a report counter render context. 
	 */
	public ReportCountListener() {
		m_reports = 0;
		
		m_seenReports = new HashSet();
	}
	
	/** 
	 * Notifies the context of a template being added to the
	 * report.
	 * 
	 * @param report Name of report.
	 * @param subreport Name of template. 
	 */
	public void notifyOfTemplateAddition( String report, String subreport ) {
		String r = report + " - " + subreport; 
		
		if ( ! m_seenReports.contains( r ) ) {
		
			if ( ! TemplateFactory.isTemplateAvailable( report, subreport ) ) {
				m_reports += 1; 
			}
			
			m_seenReports.add( r );
		}
	}
	
	/**
	 * Return the number of reports that are not 
	 * yet cached. 
	 * @return Number of reports that must be compiled. >= 0.
	 */
	public int getReportsCount() {
		return m_reports; 
	}
}