/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 30 nov. 2004
 */
package com.simpledata.bc.tarifmanager;

import java.util.Date;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;

/**
 * This class provides methods for the subscriptio management.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubscriptionToolBox.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class SubscriptionToolBox {
	
	// #### CONSTANTS - Math ##################################################
	
	private final static long ONE_DAY_IN_MS = 86400000l; /* how long */
	private final static long ONE_MONTH_IN_MS = 30 * ONE_DAY_IN_MS; 
	
	// #### STATIC METHODS - Subscription expiration date #####################
	
	/**
	 * @return Time in milliseconds when the subscription expires. Return 0 if
	 * the information cannot be retreived.
	 */
	public final static long expiresTimeInMs() {
		long result = 0l; // zero if unaviable
		
		Date lastUpdateRemote = 
			(Date)BC.getParameter(Params.KEY_LAST_UPDATE_REMOTE_TIMESTAMP);
		Date lastUpdateLocal = 
			(Date)BC.getParameter(Params.KEY_LAST_UPDATE_LOCAL_TIMESTAMP);
		Date expirationDateRemote = 
			(Date)BC.getParameter(Params.KEY_SUBSCRIBTION_EXPIRES);
		if (lastUpdateRemote != null && 
			lastUpdateLocal != null  &&
			expirationDateRemote != null) {
			Date nowLocal = new Date();
			/* compute the expiration Date with local value */
			long connectionDelta = nowLocal.getTime() - 
								   lastUpdateLocal.getTime();
			assert (connectionDelta >= 0) :
				"How the last connection date may be in future ?";
			long expirationDelta = expirationDateRemote.getTime() -
								   lastUpdateRemote.getTime();
			result = expirationDelta - connectionDelta;
		}
		return result;
	}
	
	/**
	 * @return true iff once can compute the expiration delay.
	 */
	public final static boolean expiresTimeAviable() {
		return (expiresTimeInMs() != 0);
	}
	
	/**
	 * @return true iff the subscription has expired. False otherwise (or 
	 * when the information cannot be retreived.
	 */
	public final static boolean hasExpired() {
		return (expiresTimeInMs() < 0);
	}
	
	/**
	 * @return delay in days before the subscription expires. Zero or negative
	 * if the information isn't aviable or if the subscription has expired.
	 * Test the validity with the corresponding test methods.
	 */
	public final static int expiresTimeInDay() {
		return (int)(expiresTimeInMs() / ONE_DAY_IN_MS);
	}
	
	/**
	 * @return elay in monthes before the subscription expires. Zero or negative
	 * if the information isn't aviable or if the subscription has expired.
	 * Test the validity with the corresponding test methods.
	 */
	public final static int expiresTimeInMonth() {
		return (int)(expiresTimeInMs() / ONE_MONTH_IN_MS);
	}
	
	/**
	 * @param days period to test.
	 * @return true iff the subscription expires until a number of days.
	 */
	public final static boolean expiresWhile(int days) {
		return (expiresTimeAviable() && ((expiresTimeInDay() - days) >= 0));
	}
}
