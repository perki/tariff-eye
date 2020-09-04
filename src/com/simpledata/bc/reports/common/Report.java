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

import com.simpledata.bc.reports.base.SubreportTreeItem;

/**
 * Report class holds result of report production. 
 * It contains the main subreport, the number of 
 * templates that will have to be compiled, 
 * etc... A simple structure like class, no functionality. 
 *
 * @author Simpledata SARL, 2004, all rights reserved
 * @version $Id: Report.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public class Report {
	/** Report that has been calculated */
	public SubreportTreeItem report; 
	/** 
	 * Number of templates that are yet uncompiled in the
	 * Report. 
	 */
	public int templatesToCompile; 
		
	/**
	 * Constructs a Report structure. 
	 * @param report Report that has been calculated. 
	 * @param templates Number of templates that are yet uncompiled in the
	 *                  Report.
	 */
	public Report( SubreportTreeItem report, int templates ) {
		this.report = report; 
		this.templatesToCompile = templates; 
	}
}