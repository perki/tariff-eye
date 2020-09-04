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
 * Template listener interface should be implemented
 * by classes interested in events connected to template  
 * handling. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: TemplateEventListener.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public interface TemplateEventListener {
	
	
	/**
	 * Notify the listener of a template compilation that will
	 * start after all listeners are notified. 
	 *
	 * Don't try to add or remove listeners to the factory 
	 * while handling the event, you will deadlock. 
	 * 
	 * @param report Report that contains the template. 
	 * @param subreport Subreport that identifies the template. 
	 */
	public void notifyBeforeTemplateCompilation( String report, String subreport );
	
	/**
	 * Notify the listener of a template compilation that will
	 * end after all listeners are notified. 
	 *
	 * Don't try to add or remove listeners to the factory 
	 * while handling the event, you will deadlock. 
	 *
	 * @param report Report that contains the template. 
	 * @param subreport Subreport that identifies the template. 
	 */
	public void notifyAfterTemplateCompilation( String report, String subreport );
}