/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * JUnit Test for TemplateFactory class. 
 * @version $Id: TestTemplateFactory.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 * @author Simpledata 2004, all rights reserved. 
 */

/**
 * Tests package contains all tests for subpackages 
 * of the reporting package.  
 */
package com.simpledata.bc.reports.templates;  


import junit.framework.TestCase;
import net.sf.jasperreports.engine.JasperReport;


/**
 * Test case for TemplateFactory
 */
public class TestTemplateFactory
	extends TestCase {
	
	/**
	 * Init the test case. 
	 */
	public TestTemplateFactory( String name ) {
		super(name);
	}
	
	/// test the returned class of TemplateFactory.getJasperTemplate
	/// for a report that must exist. 
	public void testReportClass() {
		Object o = TemplateFactory.getJasperTemplate( "tarification", "MasterReport" ); 
		assertTrue( "Must be instance of JasperReport", o instanceof JasperReport ); 
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		junit.textui.TestRunner.run( TestTemplateFactory.class );
	}
}