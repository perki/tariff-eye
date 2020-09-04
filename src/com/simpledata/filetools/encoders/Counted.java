/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.filetools.encoders; 

import com.simpledata.filetools.Secu;

/**
 * Helper class that helps counting from zeroAt to finalAt. 
 *
 * @version $Id: Counted.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
class Counted {
	public static long ADVERTISE_ALL_N_BYTES= 1024;
	public long counter;
	long zeroAt;
	long finalAt;
	long last;
	long lastAdvertise;
	String title;
	Secu.Monitor monitor;

	Counted(Secu.Monitor monitor, String title, long zeroAt, long finalAt) {

		this.monitor= monitor;

		this.title= title;
		this.zeroAt= zeroAt;
		this.finalAt= finalAt;
		counter= 0;
		last= 0;
		lastAdvertise= 1;
	}

	/**
	 * @param i
	 */
	public void countPlus(int i) {
		if (monitor == null)
			return;
		long n= i;
		countPlus(n);
	}

	/**
	 * @param i
	 */
	public void countPlus(long i) {
		if (monitor == null)
			return;
		counter += i;
		lastAdvertise++;

		long act= getPercent();
		if ((act != last) || (lastAdvertise > ADVERTISE_ALL_N_BYTES)) {
			monitor.valueChange(title, act, counter);
			last= act;
			lastAdvertise= 1;
		}

	}

	/** when a read occures **/
	public int count(int value) {
		if (monitor == null)
			return value;

		if (value == -1) {
			counter= finalAt - 1;
		}
		countPlus(1);

		return value;
	}

	/** in case of error return -1 **/
	public long getPercent() {
		long res= -1;
		if (finalAt != zeroAt)
			res= (100 * (counter - zeroAt)) / (finalAt - zeroAt);

		if (res < 0)
			res= -1;
		if (res > 100)
			res= -1;
		return res;
	}
}

