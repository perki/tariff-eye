/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.bc; 

import java.util.Date;

import com.simpledata.bc.uitools.Splash;

import org.apache.log4j.Logger;

/**
 * A thread that sleeps and waits for X seconds to
 * elapse. This is used for waiting while displaying the
 * splash screen. 
 */
class SplashWaiterThread extends Thread {
	private static final Logger m_log =
	    	Logger.getLogger( SplashWaiterThread.class ); 
	private long splashTotalTime;
	private Splash owner;
	
	public SplashWaiterThread(Splash splash, long totalTime) {
		super( "splash" ); 
		this.owner = splash;
		this.splashTotalTime = totalTime;
	}

	public void run() {
		long splashBeginTime = new Date().getTime();
		while (((new Date()).getTime() - splashBeginTime) < splashTotalTime) {				
			try {
			    
				Thread.sleep(100);
			} catch (InterruptedException e) {
				m_log.error( "BC:awakened prematurely", e );
			}
		}
		this.owner.byebye();
	}
		
}