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
 * reports. This class defines a no-op version of a rendering
 * context. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: EmptyRenderEventListener.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class EmptyRenderEventListener implements RenderEventListener {
	/** 
	 * Notify the render context of a template addition. 
	 * 
	 * @see com.simpledata.bc.reports.base.RenderContext#notifyOfTemplateAddition(java.lang.String, java.lang.String)
	 */
	public void notifyOfTemplateAddition( String report, String subreport ) {
		// empty on purpose
	}
}