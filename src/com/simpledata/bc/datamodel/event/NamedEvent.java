/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: NamedEvent.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.event;

import com.simpledata.bc.datamodel.Named;

/**
 * Event for Named object.
 */
public class NamedEvent {
	// CONSTANTS - Event codes
	
	/** All EVENTS **/
	public final static int ALL_EVENTS = -1;
	
	/** NAMED events starts at 100 **/
	public final static int TITLE_MODIFIED = 101;
	public final static int DESCRIPTION_MODIFIED = 102;
	
	/** TARIF EVENTS starts at 300 **/
	public final static int TARIF_MAPPING_MODIFIED = 302;
	
	/** WORKSHEET EVENTS starts at 400 **/
	/** has been dropped **/
	public final static int WORKSHEET_DROPPED = 401;
	/** worksheet hierarchy has been changed **/
	public final static int WORKSHEET_HIERARCHY_CHANGED = 402;
	public final static int WORKSHEET_OPTION_ADDED = 403;
	public final static int WORKSHEET_OPTION_REMOVED = 404;
	/** some of the data in a worksheet has been modified **/
	public final static int WORKSHEET_DATA_MODIFIED = 405;
	/** 
	 * some of the option data in a worksheet has been modified .
	 * The userObject of this event point to this option
	 * **/
	public final static int WORKSHEET_OPTION_DATA_CHANGED = 406;
	/** the Reduc or Fixed attached to this worksheet has been modified<BR>
	 * Added / Removed  (Value changes fires WORKSHEET_DATA_MODIFIED)**/
	public final static int WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE = 407;
	
	/** EVENTS ON OPTIONS starts at 500**/
	public final static int OPTION_DATA_CHANGED = 501;
	public final static int TARIF_OPTION_LINK_ADDED = 530;
	public final static int TARIF_OPTION_LINK_DROPED = 531;
	

	
	/** COMISSION CALCULATION starts at 600**/
	
	/** 
	 * A major change on the Calculation data occured
	 */
	public final static int COM_VALUE_CHANGED_TARIFICATION = 601;
	
	
	/** EVENTS ON BCTree starts at 700**/
	public final static int BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED = 701;
	
	/** EVENTS ON CompactTree (initialized by tarification)
	 *  starts at 800**/
	public final static int COMPACT_TREE_STRUCTURE_CHANGED = 801;
	
	
	private int eventCode = -1;
	
	private final Named m_source;
	
	/** an object that can be passed allong with an event **/
	private Object userObject;
	
	/**
	 * create a new Event for this Tarif
	 * @modifier  
	 */
	public NamedEvent(Named named,int eventCode) {
		assert (eventCode != ALL_EVENTS) : 
			"A NamedEvent cannot symbolize all events.";
		this.eventCode = eventCode;
		this.m_source = named;
	}

	/**
	 * create a new Event for this Tarif
	 * @modifier  
	 */
	public NamedEvent(Named named,int eventCode,Object userObject) {
		assert (eventCode != ALL_EVENTS) : 
			"A NamedEvent cannot symbolize all events.";
		this.m_source = named;
		this.eventCode = eventCode;
		this.userObject = userObject;
	}

	/**
	 * @return the event Type
	 */
	public int getEventCode() {
		return eventCode;
	}

	/**
	 * same as (Named) getSource();
	 * @return the Named o on which the event occured
	 */
	public Named getSource() {
		return m_source;
	}

	/**
	 * @return Returns the userObject.
	 */
	public Object getUserObject() {
		return userObject;
	}
	
	/**
	 * 
	 *
	 */
	public String toString() {
		return "NamedEvent ["+eventName()+"]  "+
		" source["+getSource()+"]  object:["+userObject+"]";
	}
	
	/**
	 * Provide a user readable event name.
	 * @return a String representing the event name.
	 */
	public String eventName() {
		switch(eventCode) {
		case TITLE_MODIFIED: return "TITLE_MODIFIED";
		case DESCRIPTION_MODIFIED: return "DESCRIPTION_MODIFIED";
		case TARIF_MAPPING_MODIFIED: return "TARIF_MAPPING_MODIFIED";
		case WORKSHEET_DROPPED: return "WORKSHEET_DROPPED";
		case WORKSHEET_HIERARCHY_CHANGED: return "WORKSHEET_HIERARCHY_CHANGED";
		case WORKSHEET_OPTION_ADDED: return "WORKSHEET_OPTION_ADDED";
		case WORKSHEET_OPTION_REMOVED: return "WORKSHEET_OPTION_REMOVED";
		case WORKSHEET_DATA_MODIFIED: return "WORKSHEET_DATA_MODIFIED";
		case WORKSHEET_OPTION_DATA_CHANGED: return "WORKSHEET_OPTION_DATA_CHANGED";
		case WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE: return "WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE";
		case OPTION_DATA_CHANGED: return "OPTION_DATA_CHANGED";
		case TARIF_OPTION_LINK_ADDED: return "TARIF_OPTION_LINK_ADDED";
		case TARIF_OPTION_LINK_DROPED: return "TARIF_OPTION_LINK_DROPED";
		case COM_VALUE_CHANGED_TARIFICATION: return "COM_VALUE_CHANGED_TARIFICATION";
		case BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED: return "BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED";
		case COMPACT_TREE_STRUCTURE_CHANGED: return "COMPACT_TREE_STRUCTURE_CHANGED";
		default: return "Unknow event";
		}
	}
}


/**
 *  $Log: NamedEvent.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:36  perki
 *  First commit on sourceforge
 *
 *  Revision 1.21  2004/11/15 11:21:24  jvaucher
 *  Ticket # 4: Added the discount information into tarification report.
 *
 *  Revision 1.20  2004/11/12 14:28:39  jvaucher
 *  New NamedEvent framework. New bugs ?
 *
 *  Revision 1.19  2004/09/22 06:47:05  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.18  2004/09/15 11:04:22  jvaucher
 *  Added a textual message for the event code (method eventName())
 *  Added the modification dialog box. But some changes are still not observable. See tickets for details
 *
 *  Revision 1.17  2004/09/08 16:35:14  perki
 *  New Calculus System
 *
 *  Revision 1.16  2004/08/25 14:20:50  kaspar
 *  + Logging facility for events. Not that it helps...
 *
 *  Revision 1.15  2004/06/22 08:59:05  perki
 *  Added CompactTree for CompactNode management and first sync with CompactExplorer
 *
 *  Revision 1.14  2004/06/21 16:27:31  perki
 *  added compact tree node and visibility / reorder for bctree
 *
 *  Revision 1.13  2004/05/21 13:19:50  perki
 *  new states
 *
 *  Revision 1.12  2004/05/20 17:05:30  perki
 *  One step ahead
 *
 *  Revision 1.11  2004/05/18 15:41:45  perki
 *  Better icons management
 *
 *  Revision 1.10  2004/05/14 14:20:19  perki
 *  *** empty log message ***
 *
 *  Revision 1.9  2004/05/10 19:00:51  perki
 *  Better amount option viewer
 *
 *  Revision 1.8  2004/04/12 17:34:52  perki
 *  *** empty log message ***
 *
 *  Revision 1.7  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.6  2004/03/18 18:08:59  perki
 *  barbapapa
 *
 *  Revision 1.5  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.4  2004/03/02 16:01:55  carlito
 *  *** empty log message ***
 *
 *  Revision 1.3  2004/02/24 13:33:48  carlito
 *  *** empty log message ***
 *
 *  Revision 1.2  2004/02/24 09:48:38  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/02/23 18:46:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.2  2004/02/16 10:56:41  perki
 *  new event model
 *
 *  Revision 1.1  2004/02/06 17:58:41  perki
 *  Events
 *
 */