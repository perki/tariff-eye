/**
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: Template.java,v 1.1 2006/12/03 12:48:35 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import net.sf.jasperreports.engine.JasperReport;

import com.simpledata.bc.reports.templates.TemplateFactory;

/**
 * Class that holds all information as to which 
 * Jasper template this subreport should use. Combined 
 * with the information on what report is produced, 
 * this leads to loading the correct JasperReport. 
 */
public class Template {
	
	/** Report that this template is part of. */
	private String m_report; 
	/** Subreport that this template represents */
	private String m_subReport;
	
	/**
	 * Constructor. See Subreport constructor for 
	 * an exhaustive discussion on what the parameters mean. 
	 * @param report Report to create. 
	 * @param subReport Subreport to create. 
	 */
	Template( String report, String subReport ) {
		m_report = report;
		m_subReport = subReport;
	}
	
	/**
	 * Produce and load the jasper report associated with this
	 * template. 
	 * 
	 * @return A compiled jasper report. 
	 */
	JasperReport getReport() {
		return TemplateFactory.getJasperTemplate( m_report, m_subReport ); 
	}
	
	// Public interface ---------------------------------------------
	
	/**
	 * Returns whether this template is available without time
	 * delay or whether it needs to be compiled at a cost of about
	 * one second. 
	 * 
	 * @return True if the template is immediately available. 
	 */
	public boolean isAvailable() {
		return false; 
	}
}