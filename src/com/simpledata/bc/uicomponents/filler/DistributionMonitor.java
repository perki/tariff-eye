/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.uicomponents.filler;

/**
 * An interface to monitor the distribution. 
 * 
 * @version $Id: DistributionMonitor.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public interface DistributionMonitor {
	/** 
	 * Start of a distribution.
	 * @param length number of steps to be made.
	 */
	public void distributionMonitorStart( int length );
	
	/**
	 * Distribution in process, one step has been achieved.
	 */
	public void distributionMonitorStep();
	
	/**
	 * Done.
	 */
	public void distributionMonitorDone();
}