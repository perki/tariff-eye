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

import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uicomponents.compact.CompactNode;

/**
 * Report render context for the Tarification report. 
 * This class holds all kinds of variables that should 
 * be local to one report generation. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: ReportRenderContext.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public class ReportRenderContext {
	/** Manager of event listeners on base model. */
	private RenderEventManager m_manager; 
	/** The TocManager handle structure and links */
	private TocManager m_toc;
		
	/**
	 * Constructs a report render context. 
	 */
	public ReportRenderContext() {
		m_manager = new RenderEventManager();
		m_toc = null;
	}
	
	/**
	 * Return this contexts event manager. 
	 * 
	 * @return Event manager for this render context. 
	 */
	public RenderEventManager getEventManager() {
		assert m_manager != null : 
			"There must be an event manager.";
		
		return m_manager;
	}
	
	/**
	 * @return The TocManager for this context. The computeToc method must be
	 * called before trying to get the TocManager. 
	 */
	public TocManager getTocManager() {
		assert m_toc != null :
			"The toc must be produced before getted.";
		
		return m_toc;
	}
	
	/**
	 * This method will generate the TocManager object for this context.
	 * @param exp The CompactTreeExplorer used as structure for the report.
	 */
	public void computeToc(CompactExplorer exp) {
		m_toc = new TocManager(exp);
	}
	
	/**
	 * Proxy method from TocManager. 
	 * @param node Node to retrieve the section number.
	 * @return SectionNumber of the given node.
	 */
	public SectionNumber sectionNumber(CompactNode node) {
		return m_toc.sectionNumber(node);
	}
	
	/**
	 * Proxy method from TocManager. 
	 * @param t Tariff to retrieve the section number.
	 * @return SectionNumber of the given node.
	 */
	public SectionNumber sectionNumber(Tarif t) {
		return m_toc.sectionNumber(t);
	}
}

