/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: NamedEventListener.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.event;

/**
 * Interface for the Listeners
 */
public interface NamedEventListener {

	/**
	 * Executed by the NamedEvent thread, when the event occurs.
	 * @param e The event object.
	 */
	public void eventOccured(NamedEvent e);

}


/**
 *  $Log: NamedEventListener.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:36  perki
 *  First commit on sourceforge
 *
 *  Revision 1.3  2004/11/12 14:28:39  jvaucher
 *  New NamedEvent framework. New bugs ?
 *
 *  Revision 1.2  2004/08/24 12:57:16  kaspar
 *  ! Documentation spelling fixed
 *  + Added some documentation, trying to clarify
 *  ! Changed invalid line endings
 *
 *  Revision 1.1  2004/02/23 18:46:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/02/06 17:58:41  perki
 *  Events
 *
 */