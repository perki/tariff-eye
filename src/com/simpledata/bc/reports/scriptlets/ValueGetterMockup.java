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


/**
 * ValueGetterMockup defines minimal interface that 
 * a class must support if it wants to simulate variables
 * and fields from jasper while unit testing. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: ValueGetterMockup.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public interface ValueGetterMockup {
	public Object getVariableValue( String variableName );
	public void setVariableValue( String variableName, Object value );
		
	public Object getFieldValue( String fieldName ) ;
}
