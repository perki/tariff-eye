/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ActionEventHandler.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple object that will handle events Listener and fire event.<BR>
 * 
 * To use it forward fire events and add Listeners.<BR>
 * 
 * This class handles normal events Handlers and Weak Events 
 */
public class ActionEventHandler {
	
	/** the ArrayList for normal events */
	private ArrayList actionListeners;
	
	/** the HashMap for weak events **/
	private WeakHashMap weakActionListeners;

	public ActionEventHandler() {
		actionListeners = new ArrayList();
		weakActionListeners = new WeakHashMap();
	}
	
	/** Add an action listener for event change **/
	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}
	
	/** Add a weak action listener for event change **/
	public void addWeakActionListener(ActionListener listener) {
		weakActionListeners.put(listener,new String("dummy"));
	}

	/**
	 * Fire an ActionEvent to all listeners
	 * @param paramString the string that will be retrieavable on 
	 * ActionEvent.paramString()
	 */
	public void fireActionEvent(String paramString) {
		ActionEvent e
			= new ActionEvent(this, ActionEvent.ACTION_PERFORMED,paramString);
	
		//do it for normal listeners
		synchronized (actionListeners) {
			Iterator en= actionListeners.iterator();
			while (en.hasNext()) {
				((ActionListener) en.next()).actionPerformed(e);
			}
		}
		
		// do it for weak listeners
		synchronized (weakActionListeners) {
			Iterator/*<Map.Entry>*/ en
					= weakActionListeners.entrySet().iterator();
			while (en.hasNext()) {
				((ActionListener)
						((Map.Entry)en.next()).getKey()
						).actionPerformed(e);
			}
		}
	}
	
}
/**
* $Log: ActionEventHandler.java,v $
* Revision 1.2  2007/04/02 17:04:24  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.1  2004/07/26 17:39:37  perki
* Filler is now home
*
*/