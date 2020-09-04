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

import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.common.Report;
import com.simpledata.bc.reports.common.ReportCountListener;
import com.simpledata.bc.reports.common.ReportFactory;
import com.simpledata.bc.reports.common.ReportRenderContext;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uicomponents.compact.CompactNode;

/**
 * Factory to produce Subreport Hierarchy from 
 * a CompactExplorer tree. 
 * 
 * The resulting Subreport can then be used for
 * actual Report creation. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: TarificationReportFactory.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class TarificationReportFactory implements ReportFactory {
	/**
	 * Produce a tarification report from CompactExplorer tree. Don't call 
	 * this method, use the static one instead. 
	 * 
	 * @param tree Tree that the report will be produced of. 
	 * @return Report that was generated or null in case of failure.
	 */
	public Report produceReport( CompactExplorer tree ) {
		CompactNode root = tree.getTreeRoot();
		
		ReportRenderContext ctx = new ReportRenderContext();
		CompactNodeLinearizer l = new CompactNodeLinearizer( ctx ); 
		ReportCountListener counter = new ReportCountListener();
		RenderEventManager evtmgr = ctx.getEventManager();
		
		assert evtmgr != null : 
			"Event manager cannot be constructed null";
		
		// no remove of this listener, since the class holding it is 
		// local scope
		evtmgr.addListener( counter ); 
		
		Tarification tarification = tree.getTarification();
		ctx.computeToc(tree);
		l.generate(tarification); 
		
		Report r = new Report( l.getSubreport(), counter.getReportsCount() );
		return r;
	}
	
	/**
	 * Produce a report from CompactExplorer tree. 
	 * 
	 * @param tree Compact tree to generate report for. 
	 * @return Generated report or null in case of failure. 
	 */
	static Report produceSubreports( CompactExplorer tree ) {
		TarificationReportFactory factory = new TarificationReportFactory(); 
		
		return factory.produceReport( tree ); 
	}
}
