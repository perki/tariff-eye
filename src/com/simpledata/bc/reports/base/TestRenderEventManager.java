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
 * @version $Id: TestRenderEventManager.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import junit.framework.TestCase;

/**
 * Unit test for Report Fields collection. 
 */
public class TestRenderEventManager 
	extends TestCase {
		
	protected RenderEventManager m_manager; 
		
	public TestRenderEventManager( String name ) {
		super( name ); 
	}
		
	protected void setUp() {
		m_manager = new RenderEventManager(); 
	}
		
	public void testAddListener() {
		class TestListener implements RenderEventListener {
			String m_report; 
			String m_subreport; 
			
			public void notifyOfTemplateAddition( String report, String subreport ) {
				m_report = report; 
				m_subreport = subreport; 
			}
		}
		
		TestListener l = new TestListener();
		
		m_manager.addListener( l ); 
		
		m_manager.notifyOfTemplateAddition( "foo", "bar" ); 
		
		assertEquals( "foo", l.m_report ); 
		assertEquals( "bar", l.m_subreport ); 
		
		m_manager.removeListener( l ); 
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTable.class );
	}
}