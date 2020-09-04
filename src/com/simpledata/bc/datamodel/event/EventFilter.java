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

/**
 * Intercae for the NamedEvent filters.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: EventFilter.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public interface EventFilter {
	
	/**
	 * Checks if the event match the rule of the filter.
	 * @param ne Event to check.
	 * @return true iif the event match the rule.
	 */
	public boolean match(NamedEvent ne);
	
}
