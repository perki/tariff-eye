/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Reporting: Template handling
 */
package com.simpledata.bc.reports.templates; 

/**
 * Empty Template listener. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: EmptyTemplateEventListener.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class EmptyTemplateEventListener {
	public void notifyBeforeTemplateCompilation( String report, String subreport ) {
	}
	public void notifyAfterTemplateCompilation( String report, String subreport ) {
	}
}