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
 * @version $Id: TestReportRenderContext.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */

/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.common;

import junit.framework.TestCase;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.tarification.TestTableOfContentsScriptlet;

/**
 * Unit Test for ReportRenderContext.
 */
public class TestReportRenderContext
	extends TestCase {
		
	protected ReportRenderContext m_context;
		
	public TestReportRenderContext( String name ) {
		super( name ); 
	}
		
	protected void setUp() {
		m_context = new ReportRenderContext();
	}
	
	// Tests for document links --------------------------------------
	
	public void testDocumentLinksForward() {
		String ankor1 = "bluna"; 
		
		//Object link = m_context.getLinkFor( ankor1, "foo" ); 
		
		//m_context.resolveLink( ankor1, "bar" ); 
		
		//assertEquals( "foobar", link.toString() );
	}
	
	public void testDocumentLinksBackward() {
		String ankor1 = "bluna"; 
		
		//m_context.resolveLink( ankor1, "bar" ); 
		
		//Object link = m_context.getLinkFor( ankor1, "foo" ); 
		
		//assertEquals( "foobar", link.toString() );
	}
	
	// Render Event Manager ------------------------------------------
	
	public void testRenderEventManager() {
		RenderEventManager manager = m_context.getEventManager(); 
		
		assertTrue( manager != null ); 
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTableOfContentsScriptlet.class );
	}
}