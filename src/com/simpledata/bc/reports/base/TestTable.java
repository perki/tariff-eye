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
 * @version $Id: TestTable.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import junit.framework.TestCase;

/**
 * Unit Test for Table class and attached helpers 
 * TableFields, TableRow. 
 */
public class TestTable 
	extends TestCase {
		
	protected TableFields m_fielddef; 
	protected TableRow m_dataRow; 
	protected Table m_data; 
	protected boolean m_setup; 
		
	public TestTable( String name ) {
		super( name ); 
		m_setup = false; 
	}
		
	protected void setUp() {
		m_fielddef = new TableFields(); 
		m_fielddef.addJasperField("foo"); 
		m_fielddef.addReportField("bar"); 
		
		TableFields rowfields = new TableFields(); 
		rowfields.addJasperField("foo"); 
		rowfields.addReportField("bar"); 
		rowfields.freeze();
		m_dataRow = new TableRow( rowfields ); 
		
		m_data = new Table(); 
		m_setup = true; 
	}
		
	public void testFieldDefinition() {
		TableFields def = m_fielddef;
		
		assertTrue(
			"def must be instance of TableFields.",
			def instanceof TableFields
		); 
		
		assertEquals( 
			"There should be two fields.",
			2, def.size()
		); 
		
		assertTrue( 
			"Second field should be Subreport", 
			def.isSubreportField( 1 )
		); 
		
		assertFalse( 
			"First field should NOT be Subreport",
			def.isSubreportField( 0 )	
		); 
	}
	
	public void testFieldFreeze() {
		boolean ok = false; 
		m_fielddef.freeze(); 
		try {
			m_fielddef.addJasperField( "test" ); 
		}
		catch (AssertionError e) {
			ok = true; 
		}
		assertTrue( ok ); 
	}
		
	public void testDataRowException() {
		boolean excepted = false; 
		try {
			m_dataRow.put( 1, "String and not Subreport" ); 
		}
		catch (AssertionError e) {
			excepted = true; 
		}
		
		assertTrue(
			"Data Row should not accept wrong type.",
			excepted
		); 
	}
	
	public void testTableFields() {
		m_data.addJasperField( "Foo" ); 
		m_data.addReportField( "Bar" ); 
		assertTrue( m_data.columns() == 2 ); 
	}
	
	public void testTableData() {
		String[] data = { "a", "b" };
		m_data.addJasperField( "Foo" ); 
		m_data.addJasperField( "Bar" ); 
		m_data.freezeDefinitions(); 
		
		m_data.addDataRow( data ); 
		
		assertEquals( 2, m_data.columns() ); 
		assertEquals( 1, m_data.rows() ); 
	}
	
	public void testTableDataRetrieve() {
		String[] data = { "a", "b" };
		m_data.addJasperField( "Foo" ); 
		m_data.addJasperField( "Bar" ); 
		m_data.freezeDefinitions(); 
		
		m_data.addDataRow( data ); 
		
		assertEquals(
			"a", m_data.getNormalCell( 0, 0 )
		);
		assertEquals(
			"b", m_data.getNormalCell( 0, 1 )
		);
		
		boolean ok = false;
		try {
			m_data.getReportCell( 0, 0 );
		}
		catch (AssertionError e) {
			ok = true;
		}
		assertTrue( ok );
	}
	
	public void testTableDataRetrieveReports() {
		Object[] data = { null };
		m_data.addReportField( "Foo" ); 
		m_data.freezeDefinitions(); 
		m_data.addDataRow( data ); 
		
		boolean ok = false;
		try {
			m_data.getNormalCell( 0, 0 );
		}
		catch (AssertionError e) {
			ok = true;
		}
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTable.class );
	}
}