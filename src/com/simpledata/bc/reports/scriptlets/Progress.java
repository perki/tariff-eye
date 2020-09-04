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
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: Progress.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class Progress extends JRDefaultScriptlet
{
	/**
	 *
	 */
	public void beforeReportInit() throws JRScriptletException
	{
		System.out.println("call beforeReportInit");
	}

	/**
	 *
	 */
	public void afterReportInit() throws JRScriptletException
	{
		System.out.println("call afterReportInit");
	}

	/**
	 *
	 */
	public void beforePageInit() throws JRScriptletException
	{
		System.out.println("call   beforePageInit : PAGE_NUMBER = " + this.getVariableValue("PAGE_NUMBER"));
	}

	/**
	 *
	 */
	public void afterPageInit() throws JRScriptletException
	{
		System.out.println("call   afterPageInit  : PAGE_NUMBER = " + this.getVariableValue("PAGE_NUMBER"));
	}

	/**
	 *
	 */
	public void beforeColumnInit() throws JRScriptletException
	{
		System.out.println("call     beforeColumnInit");
	}

	/**
	 *
	 */
	public void afterColumnInit() throws JRScriptletException
	{
		System.out.println("call     afterColumnInit");
	}

	/**
	 *
	 */
	public void beforeGroupInit(String groupName) throws JRScriptletException
	{
		System.out.println("call     beforeGroupInit(\""+ groupName +"\")");
	}

	/**
	 *
	 */
	public void afterGroupInit(String groupName) throws JRScriptletException
	{
		System.out.println("call     afterGroupInit(\""+ groupName +"\")");
	}

	/**
	 *
	 */
	public void beforeDetailEval() throws JRScriptletException
	{
		System.out.println("          detail");
	}

	/**
	 *
	 */
	public void afterDetailEval() throws JRScriptletException
	{
	}
}
