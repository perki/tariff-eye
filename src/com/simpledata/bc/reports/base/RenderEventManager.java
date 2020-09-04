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


import java.util.LinkedList;
import java.util.ListIterator;

/**
 * RenderEventManager holds all listeners for render 
 * events. He is charged of notifying them of any render
 * events. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: RenderEventManager.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
public class RenderEventManager {
	
	private LinkedList/*<RenderEventListener>*/ m_listeners;
	
	/**
	 * Constructs a RenderEventManager for holding all 
	 * listeners. 
	 */
	public RenderEventManager() {
		m_listeners = new LinkedList(); 
	}
	
	/**
	 * Add a listener to the manager. 
	 * 
	 * @param listener Listener to add. 
	 */
	public synchronized void addListener( RenderEventListener listener ) {
		m_listeners.add( listener ); 
	}
	
	/**
	 * Remove a listener from the manager. 
	 * 
	 * @param listener Listener to add. 
	 */
	public synchronized void removeListener( RenderEventListener listener ) {
		m_listeners.remove( listener );
	}
	
	/** 
	 * Notifies the context of a template being added to the
	 * report. 
	 *
	 * @param report Name of report.
	 * @param subreport Name of template. 
	 */
	public synchronized void notifyOfTemplateAddition( String report, String subreport ) {
		ListIterator it = m_listeners.listIterator();
		while ( it.hasNext() ) {
			RenderEventListener rel = (RenderEventListener) it.next(); 
			
			rel.notifyOfTemplateAddition( report, subreport ); 
		}
	}
	
}