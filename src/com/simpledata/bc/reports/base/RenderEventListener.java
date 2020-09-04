/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;


/**
 * RenderContext interface defines interface to be used
 * by all classes that want to hold context while rendering
 * reports. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: RenderEventListener.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
public interface RenderEventListener {
	
	/** 
	 * Notifies the context of a template being added to the
	 * report. 
	 * 
	 * @param report Name of report.
	 * @param subreport Name of template. 
	 */
	public void notifyOfTemplateAddition( String report, String subreport );
	
	
}