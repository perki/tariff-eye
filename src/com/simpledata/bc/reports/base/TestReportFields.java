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
 * @version $Id: TestReportFields.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit test for Report Fields collection. 
 */
public class TestReportFields 
	extends TestCase {
		
	protected ReportFields m_fields;
		
	public TestReportFields( String name ) {
		super( name ); 
	}
		
	protected void setUp() {
		m_fields = new ReportFields();
	}
		
	public void testAdditionDouble() {
		m_fields.addAppValue("foo", "bar"); 
		m_fields.addAppValue("foo", "bar"); 
		m_fields.addAppValue("foo", "bar"); 
		m_fields.addAppValue("foo", "bar"); 
		m_fields.addAppValue("foo", "bar"); 
		assertEquals(
			"Hashmap behaviour violated.",
			1, m_fields.size()
		);
	}
	
	public void testAdditionMixedDouble() {
		m_fields.addAppValue( "foo", "bar" ); 
		m_fields.addTranslatedString( "foo" );
		assertEquals(
			"Hashmap behaviour violated.",
			1, m_fields.size()
		);
	}
	
	public void testAdditionMixed() {
		m_fields.addAppValue( "foo", "bar" ); 
		m_fields.addTranslatedString( "foobar" );
		assertEquals(
			2, m_fields.size()
		);
	}
	
	public void testAdditionKeys() {
		m_fields.addAppValue( "foo", "bar" ); 
		m_fields.addAppValue( "bar", "baz" ); 
		
		Map map = m_fields.produceJasperFields();
		assertTrue(
			map != null
		);
		assertTrue(
			map.containsKey( "foo" )
		);
		assertTrue(
			map.containsKey( "bar" )
		);
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTable.class );
	}
}