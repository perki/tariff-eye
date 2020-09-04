/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.bc;


/**
 * Threaded launch of the small browser window that
 * is called assistant. 
 * 
 * @version $Id: HtmlBrowserLauncher.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
class HtmlBrowserLauncher implements Runnable {
	private Desktop owner;
	
	public HtmlBrowserLauncher( Desktop desk ) {
		this.owner = desk;
	}
	
	public void run() {
		this.owner.launchAssistant();
	}
}