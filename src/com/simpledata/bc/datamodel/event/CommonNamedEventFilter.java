/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: CommonNamedEventFilter.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.event;


/**
 * This class is the usual filter used when once adds a listener, giving only
 * a class and or an eventCode for matching. It replaces the old filter.
 */
public class CommonNamedEventFilter implements EventFilter{
	// FIELDS
	
	/** The event code matched by this filter */
	private int matchedEventCode;
	/** The class of the source named object matched by this filter */
	private Class c;
	
	// CONSTRUCTOR
	
	/**
	 * Construct a new Filter to bind with a Listener
	 * 
	 * @param eventCode Code of the matched event, uses -1 to match all events
	 * @param c The class of the source the filter has to match. I.e. the origin
	 * which fire the event, not the object you're listening to. 
	 * Uses null to listen to all classes.
	 */
	public CommonNamedEventFilter(int eventCode, Class c) {
		this.matchedEventCode = eventCode;
		this.c = c;
	}
	
	// METHOD - implements EventFilter
	
	/** 
	 * @param ne the event to filter.
	 * @return true iif the event if it matches the filter. 
	 */
	public boolean match(NamedEvent ne) {
		if (c != null && c != ne.getSource().getClass()) 
			return false; // Class doesn't match
		if (matchedEventCode > -1 && matchedEventCode != ne.getEventCode()) 
			return false; // Event doesn't match
		// else : sounds good
		return true;
	}
}


/**
 *  $Log: CommonNamedEventFilter.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:36  perki
 *  First commit on sourceforge
 *
 *  Revision 1.1  2004/11/12 14:28:39  jvaucher
 *  New NamedEvent framework. New bugs ?
 *
 *  Revision 1.5  2004/08/25 14:20:50  kaspar
 *  + Logging facility for events. Not that it helps...
 *
 *  Revision 1.4  2004/05/11 15:53:00  perki
 *  more calculus
 *
 *  Revision 1.3  2004/05/10 19:00:51  perki
 *  Better amount option viewer
 *
 *  Revision 1.2  2004/03/02 16:01:55  carlito
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/02/23 18:46:04  perki
 *  *** empty log message ***
 *
 */