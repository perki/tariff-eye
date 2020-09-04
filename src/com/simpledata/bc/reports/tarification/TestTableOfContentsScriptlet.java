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
 * @version $Id: TestTableOfContentsScriptlet.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */

/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.tarification;

import junit.framework.TestCase;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import com.simpledata.bc.reports.scriptlets.ValueGetterMockup;


/**
 * Unit Test for Table class and attached helpers 
 * TableFields, TableRow. 
 */
public class TestTableOfContentsScriptlet
	extends TestCase {
		
	protected TableOfContentsScriptlet m_scriptlet;
	protected ValueMockup m_mockup;
		
	public TestTableOfContentsScriptlet( String name ) {
		super( name ); 
	}
		
	protected void setUp() {
		m_scriptlet = new TableOfContentsScriptlet();
		m_mockup = new ValueMockup();

		m_scriptlet.setUnitTestMockup( m_mockup );
	}
	
	public void testBasicDatasource() {
		JRRewindableDataSource ds = m_scriptlet.getDataSource();
		
		try {
			assertFalse( ds.next() );
		}
		catch (Exception e) {
			assertTrue( 
				"Exception caught",
				false
			);
		}
	}
	
	public void testDatasourceFill() {
		boolean testOk = true;
		
		try {
			m_scriptlet.beforeGroupInit( "Title" );
			m_scriptlet.beforeGroupInit( "SubTitle" );
			m_scriptlet.beforeGroupInit( "SubSubTitle" );
		}
		catch (Exception e) {
			testOk = false;
			e.printStackTrace();
		}
		
		assertTrue( 
			testOk 
		);
	}
	
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTableOfContentsScriptlet.class );
	}
	
	
	/**
	 * Internal class that permits simulation of variable
	 * and field content. 
	 */
	class ValueMockup implements ValueGetterMockup {
		// this returns page number 30 regardless of variable name
		public Object getVariableValue( String variableName ) {
			return new Integer( 30 );
		}
		
		// this returns fieldname + "foobar"
		public Object getFieldValue( String fieldName ) {
			return fieldName + " mockup";
		}
		
		public void setVariableValue(String variableName, Object value) {
			// left empty
		}
	}
}