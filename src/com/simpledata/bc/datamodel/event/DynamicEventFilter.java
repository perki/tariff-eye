/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 11 nov. 2004
 */
package com.simpledata.bc.datamodel.event;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import com.simpledata.bc.datamodel.Named;

/**
 * This class implements a dynamic filter for the named events. You can
 * modify the matching rules of the filter at run time.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: DynamicEventFilter.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class DynamicEventFilter implements EventFilter {

	// FIELDS
	/** matched events */
	private final HashSet m_matchedEvents;
	/** matched source origins */
	private final HashSet m_matchedSources;
	
	// CONSTRUCTOR
	
	/**
	 * Constructs a new DynamicEventFilter. 
	 * @param matchedEvent original array on matched event coded.
	 * @param matchedSources original array on matched sources.
	 */
	public DynamicEventFilter(int[] matchedEvent, Class[] matchedSources) {
		// init fields
		m_matchedEvents = new HashSet(matchedEvent.length);
		m_matchedSources = new HashSet(Arrays.asList(matchedSources));
		// fill
		for (int i = 0; i < matchedEvent.length ; i++) {
			m_matchedEvents.add(new Integer(matchedEvent[i]));
		}
		validate();
	}
	
	// METHODS - EventFilter
	
	/**
	 * Does the filter match ?
	 * @see com.simpledata.bc.datamodel.event.EventFilter#match(com.simpledata.bc.datamodel.event.NamedEvent)
	 */
	public boolean match(NamedEvent ne) {
		return (m_matchedEvents.contains(new Integer(ne.getEventCode())) &&
				m_matchedSources.contains(ne.getSource().getClass()));
	}
	
	// METHOD - Filter modification
	
	/**
	 * Add a new event code to the filter
	 * @param eventCode eventCode to add. One of NamedEvent constants.
	 */
	public void addEventCode(int eventCode) {
		assert (eventCode >= 0) :
			"Do not use negative event code. To use the ALL_EVENTS constant"+
			" use CommonNamedEventFilter."; 
		m_matchedEvents.add(new Integer(eventCode));
	}
	
	/**
	 * Remove an event code to the filter.
	 * @param eventCode eventCode to remoce. One of NamedEvent constants.
	 */
	public void removeEventCode(int eventCode) {
		m_matchedEvents.remove(new Integer(eventCode));
	}

	/**
	 * Add a source class to the filter
	 * @param source Class of the sources to match.
	 */
	public void addSource(Class source) {
		assert (Named.class.isAssignableFrom(source)) :
			"The source class filter must use a subclass of Named";
		m_matchedSources.add(source);
	}

	/**
	 * Remove a source class to the filter
	 * @param source Class of the sources to don't match anymore.
	 */
	public void removeSource(Class source) {
		m_matchedSources.remove(source);
	}
	
	// METHOD - private
	
	/** Validate the rules */
	private void validate() {
		// Avoid negative event code
		Iterator it = m_matchedEvents.iterator();
		while (it.hasNext()) {
			Integer code = (Integer)it.next();
			assert (code.intValue() >= 0) :
				"Do not use negative event code. To use the ALL_EVENTS constant"+
				" use CommonNamedEventFilter."; 
		}
		// Verify validity of classes
		Iterator it2 = m_matchedSources.iterator();
		while (it2.hasNext()) {
			Class source = (Class)it2.next();
			assert (Named.class.isAssignableFrom(source)) :
				"The source class filter must use a subclass of Named";
		}
	}
}
