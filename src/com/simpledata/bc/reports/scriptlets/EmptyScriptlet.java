/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

/**
 * Package containing all jasper scriptlets. A Scriptlet is a 
 * class that contains code that Jasper calls back into at 
 * various report generation events. 
 */
package com.simpledata.bc.reports.scriptlets;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

/**
 * Empty scriptlet defines all methods in default 
 * scriptlet as empty methods. It can be extended without
 * having to implement all abstract methods of default
 * scriptlet. 
 * 
 * Additional functionality includes mocking of the 
 * methods getVariableValue and getFieldValue by an 
 * object that implements ValueGetterMockup interface. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: EmptyScriptlet.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class EmptyScriptlet extends JRDefaultScriptlet
{
	// TESTING: if non null, some methods are mocked up
	private ValueGetterMockup m_mockup; 
		
	/**
	 * Sets the value getter for mocking value retrieval 
	 * functionality. This is used for unit tests only. 
	 *
	 * If set to NULL, mocking is stopped. 
	 *
	 * @param mockup An implementation of ValueGetterMockup. 
	 */
	public void setUnitTestMockup( ValueGetterMockup mockup ) {
		m_mockup = mockup;
	}
	
	/**
	 * Override superclass getVariableValue for handling 
	 * mockup delegation. This is used for unit tests only. 
	 */
	public Object getVariableValue( String variableName ) 
		throws JRScriptletException {
		if (m_mockup != null) 
			return m_mockup.getVariableValue( variableName );
		
		return super.getVariableValue( variableName );
	}
	
	/**
	 * Override superclass getVariableValue for handling 
	 * mockup delegation. This is used for unit tests only. 
	 */
	public void setVariableValue( String variableName, Object value ) 
		throws JRScriptletException {
		if (m_mockup != null) {
			m_mockup.setVariableValue( variableName, value );

			return;
			/* NOT REACHED */
		}
		
		super.setVariableValue( variableName, value );
	}
	
	/**
	 * Override superclass getFieldValue for handling 
	 * mockup delegation. This is used for unit tests only. 
	 */
	public Object getFieldValue( String fieldName ) 
		throws JRScriptletException {
		if (m_mockup != null) 
			return m_mockup.getFieldValue( fieldName );
		
		return super.getFieldValue( fieldName );
	}
	
	public void beforeReportInit() throws JRScriptletException {
	}
	public void afterReportInit() throws JRScriptletException {
	}
	public void beforePageInit() throws JRScriptletException {
	}
	public void afterPageInit() throws JRScriptletException {
	}
	public void beforeColumnInit() throws JRScriptletException {
	}
	public void afterColumnInit() throws JRScriptletException {
	}
	public void beforeGroupInit(String groupName) throws JRScriptletException {
	}
	public void afterGroupInit(String groupName) throws JRScriptletException {
	}
	public void beforeDetailEval() throws JRScriptletException {
	}
	public void afterDetailEval() throws JRScriptletException {
	}
}
