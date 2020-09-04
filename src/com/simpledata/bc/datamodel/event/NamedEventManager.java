/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 10 nov. 2004
 */
package com.simpledata.bc.datamodel.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Named;

/**
 * This class manages all the NamedEvent dispatching and filtering. There is
 * a singleton static instance of it in the Named class. It lauches a thread
 * to process events.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: NamedEventManager.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class NamedEventManager {
	
	/** Logger */
	private final static Logger m_log = Logger.getLogger(NamedEventManager.class);
	
	/** EventQueue Thread */
	private final EventQueueThread m_eventQueueThread;
	
	/** Event queue */
	final LinkedList /*<EventQueueListItem>*/ m_eventQueue;
	
	/** Listeners subscribe queue */
	final LinkedList /*<SubscribeListItem>*/ m_subscribeQueue;
	
	/**
	 * Initialize a NamedEventManager
	 */
	public NamedEventManager() {
		// init members objects
		m_eventQueue = new LinkedList();
		m_subscribeQueue = new LinkedList();
		
		// Launch EventQueue Thread
		m_eventQueueThread = new EventQueueThread("NamedEventQueue");
		m_eventQueueThread.start();
	}
	
	/**
	 * Fire an event. This calls all listeners' eventOccurs method, iif the 
	 * associated filter matches.
	 * @param who Caller of this method, all the listeners associated with
	 * this object will be called.
	 * @param ne The event.
	 */
	public void fireEvent(Named who, NamedEvent ne) {
		// TODO Reactivate the following assertion when the problem in 
		// compactNode is resolved.
		
		//assert Thread.currentThread() != m_eventQueueThread :
		//	"An event should not encours other events.";
		synchronized(m_eventQueue) {
			m_eventQueue.addLast(new EventQueueListItem(who,ne));
		}
		synchronized(m_eventQueueThread) {
			m_eventQueueThread.notify();
		}
	}
	
	/**
	 * Schedule a new listener to be added.
	 * @param filter The filter associated with the listener
	 * @param listener An object that implements the NamedEventListener
	 * interface. I.e. it has a eventOccured method to call, when the event
	 * happends.
	 * @param who Who the listener want to listen to.
	 */
	public void addListener(EventFilter filter, 
							NamedEventListener listener,
							Named who) {
		synchronized(m_subscribeQueue) {
			SubscribeListItem item = new SubscribeListItem(filter, listener, who);
			m_subscribeQueue.addLast(item);
		}
	}
	
	/**
	 * Schedule a listener to be removed from a Named object.
	 * @param listener The listener obejct to remove.
	 * @param who On which named object.
	 */
	public void removeListener(NamedEventListener listener, Named who) {
		synchronized (m_subscribeQueue) {
			SubscribeListItem item = new SubscribeListItem(null, listener, who);
			m_subscribeQueue.addLast(item);
		}
	}
	
	/**
	 * This class implements the Thread that process the events. Before
	 * processing an event, it updates the listener list. From the
	 * subscribeQueue.
	 */
	private class EventQueueThread extends Thread {
		
		/** Registred Listeners */
		private final WeakHashMap /*<Named,LinkedList<ListenerListItem>>*/ m_listeners;
		
		EventQueueThread(String name) {
			super(name);
			m_listeners = new WeakHashMap();
		}
		
		/** run */
		public void run() {
			try {
				do {
					while (! m_eventQueue.isEmpty()) {
						// update listeners list
						processSubscribeQueue(); 
						// process an event
						processNextEvent();
					}
					synchronized(this) {
						wait();
					}
				} while (true);
			} catch (InterruptedException e) {
				m_log.fatal("EventThread interrupted",e);
			}
		}
		
		/** Process the next event from the event Queue */
		private void processNextEvent() {
			// Get next event
			EventQueueListItem eventQueueItem;
			synchronized (m_eventQueue) {
				eventQueueItem = (EventQueueListItem) m_eventQueue.removeFirst();
			}
			// Get all listeners / filters for this source
			Named source = eventQueueItem.who;
			final NamedEvent event = eventQueueItem.ne;
			LinkedList listeners = (LinkedList) m_listeners.get(source);
			if (listeners != null) {
				Iterator it = listeners.iterator();
				while (it.hasNext()) {
					// Check if filter matches, and send event if it does
					ListenerListItem item = (ListenerListItem)it.next();
					EventFilter filter = item.filter;
					final NamedEventListener listener = item.listener;
					if (filter.match(event)) {
						SwingUtilities.invokeLater(new Runnable(){
							public void run() {
								listener.eventOccured(event);
							}});
					}
				}
			}
		}
		
		/** Process all subscribe / unsubscribe requests */
		private void processSubscribeQueue() {
			synchronized(m_subscribeQueue) {
				while (! m_subscribeQueue.isEmpty()) {
					SubscribeListItem item = 
						(SubscribeListItem) m_subscribeQueue.removeFirst();
					if (item.filter == null) { 
						// remove listener
						unsubscribe(item.listener, item.who);
					} else { 
						// add listener
						subscribe(item);
					}
				}
			}
		}
		
		/** Add a listener. If the listener already exists, replace the filter */
		private void subscribe(SubscribeListItem newListener) {
			LinkedList listeners = (LinkedList) m_listeners.get(newListener.who);
			if (listeners == null) {
				// First listener for that class
				listeners = new LinkedList();
				m_listeners.put(newListener.who, listeners);
			}
			boolean updated = false;
			for (int i=0; i<listeners.size(); i++) {
				ListenerListItem registredListener = 
					(ListenerListItem) listeners.get(i);
				if (registredListener.listener == newListener.listener) {
					// already registred, modify the filter
					registredListener.filter = newListener.filter;
					updated = true;
					break;
				}
			}
			if (!updated) {
				// new listener
				listeners.addLast(new ListenerListItem(newListener.filter,
													   newListener.listener));
			}
		}
		
		/** Remove a listener */
		private void unsubscribe(NamedEventListener listener, Named who) {
			LinkedList regListeners = (LinkedList) m_listeners.get(who);
			if (regListeners != null) {
				for (int i = 0; i<regListeners.size(); i++) {
					ListenerListItem lli = (ListenerListItem) regListeners.get(i);
					if (lli.listener == listener) 
						regListeners.remove(lli);
				}
			}
		}
	}
}

/**
 * Item of the HashMap m_listenerList.
 */
class ListenerListItem {
	/** Filter associated with the listener */
	EventFilter filter;
	/** Listener to call */
	final NamedEventListener listener;
	
	ListenerListItem(EventFilter filter, NamedEventListener listener) {
		this.filter = filter;
		this.listener = listener;
	}
}

/**
 * Item of the LinkedList m_subscribeQueue.
 */
class SubscribeListItem {
	/** Who once wants to listen to */
	final Named who;
	/** With what filter */
	final EventFilter filter;
	/** The listener object */
	final NamedEventListener listener;
	
	SubscribeListItem(EventFilter filter, 
							  NamedEventListener listener,
							  Named who) {
		this.filter = filter;
		this.listener = listener;
		this.who = who;
	}
}

/**
 * Item of the m_eventQueue LinkedList.
 */
class EventQueueListItem {
	/** Who fires the event */
	final Named who;
	/** The event */
	final NamedEvent ne;
	
	EventQueueListItem(Named who, NamedEvent ne) {
		this.who = who;
		this.ne  = ne;
	}
}
