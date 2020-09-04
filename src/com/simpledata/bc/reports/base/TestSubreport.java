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
 * @version $Id: TestSubreport.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import junit.framework.TestCase;

/**
 * Unit Test for Subreport.  
 */
public class TestSubreport 
	extends TestCase {
		
	protected Subreport m_report; 
		
	public TestSubreport( String name ) {
		super( name ); 
	}
		
	protected void setUp() {
		RenderEventManager manager = new RenderEventManager();
		
		m_report = new Subreport( manager, "test", "Test" );
		m_report.addJasperField( "Jasper" );
		m_report.addReportField( "Report" );
		m_report.freezeDefinitions(); 
	}
	
	public void testAddData( ) {
		Object[] data = {
			"Test", 
			null
		};
		
		m_report.addRow( data ); 
	}
	
	/**
   * This test will fail unless it is run with 
	 * the -ea argument to the JVM. 
	 */
	public void testAddDataMismatchColumnCount( ) {
		Object[] data = {
			"Test"
		};
		boolean ok = false; 
		try {
			m_report.addRow( data ); 
		} 
		catch (AssertionError e) {
			System.out.println(e.toString());
			ok = true; 
		}
		assertTrue( ok ); 
	}
	
	/** getTable should never return null pointer */
	public void testGetTable() {
		assertTrue(
			m_report.getTable() != null
		);
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTable.class );
	}
}