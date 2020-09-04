/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: Named.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/
package com.simpledata.bc.datamodel;

import java.io.Serializable;
import java.util.*;

import com.simpledata.bc.datamodel.event.*;

import org.apache.log4j.Logger;

/**
* Named
* All Classes Of the DataModel must extend this class
*
*/
public class Named implements Serializable {
	/** RealClass type **/
	public final static String CLASS_TYPE= "UNKOWN";

	/** ID TAG FOR XML GENERATION **/
	public final static String TAG_NID= "ID";

	/** TITLE TAG FOR XML GENERATION **/
	public final static String TAG_TITLE= "TITLE";
		
	/** Log4j logger */
	private static Logger m_log = Logger.getLogger( Named.class );
	
	// FIELDS - Events
	/** Event Manager */
	private static final NamedEventManager m_eventManager = 
		new NamedEventManager();
	
	/** Silent mode */
	public static boolean silent = false;

	// FIELDS 
	
	/** Tarification this Named object belongs To **/
	protected Tarification xTarification;

	/** Container for this Object **/
	protected Named xContainer;

	/** Title of this object **/
	protected TString xTitle;

	/** Description of this object **/
	protected TString xDescription;

	/** Id of this object- Unique in Simple's World **/
	protected String xNID;

	/** Object Type **/
	protected String xClassType;


	public Named(String classType, Named container, String title) {
		this(classType,container,title,null);
	}
	/**
	* @param container Container of this Object
	* @param title Title of this object
	* @param nID unique id in this container namespace
	*/
	public Named(String classType, Named container, String title, String nID) {
		this.xContainer= container;
		if (container != null) {
			this.xTarification= container.getTarification();
		} else { // this should be a Tarification
			if (this instanceof Tarification) {
				this.xTarification= (Tarification) this;
			} else {
				m_log.error("Container should not be null"+
				        "(outside of a Tarification).");
				return;
			}
		}
		this.xTitle= new TString(this, title);
		this.xDescription= new TString(this, "");
		
		this.xClassType= classType;

		if (nID == null || nID.equals(""))
			nID= xTarification.getNextUniqueId();
		
		xNID = nID;	
		
		xTarification.newNamed(this,false);
	}
	
	/** change the id of this object -- use with great CARE**/
	public  final void changeNID(String nNID) {
		if (xTarification == null) {
			m_log.error("setNID("+nNID+") has been called on a object with a null Tarifcation");
			return;
		}
		if ( nNID == null) {
			m_log.error("cannot set a null nNID");
			return;
		}
		Named temp = xTarification.getInstanceForNID(nNID);
		if (temp == null) {
			this.xNID = nNID;
			xTarification.newNamed(this,true);
		} else {
			m_log.error("Cannot change id of ["+xNID+"] to ["
			        +nNID+"] .. id already taken by["+temp+"]",new Exception());
		}
	}
	
	/**
	 * @return true if this Named Object has been created a user
	 * false is created by SimpleDta
	 */
	public final boolean isUserInstance() {
		return ! getNID().startsWith(Tarification.publishingCreationTag);
	}

	/**
	* return the Tarification this object belongs to
	*/
	public  final Tarification getTarification() {
		return xTarification;
	}

	/**
	* return the container of this Instance
	*/
	public  final Named getContainer() {
		return xContainer;
	}

	/**
	* return a unique id in the container context
	**/
	public  final String getNID() {
		return xNID;
	}

	/**
	* return a unique id in the container context in XML format
	**/
	public  final String getSmallNID() {
		return _getStartNID() + "/>";
	}

	/**
	* return the fully qualified name of this object
	* <TN ID="%id%" TITLE=""><CONTAINER ID="%id%" TITLE=""><OBJECT ID="%id"/></CONTAINER></TN>
	**/
	public  final String getFullNID() {
		// if no container stop
		if (xContainer == null)
			return getSmallNID();
		// else warp me 
		return xContainer._getTraversalNID(getSmallNID());
	}

	/**
	* return start of the Id string (need to be closed)
	*/
	private String _getStartNID() {
		return "<"
			+ xClassType
			+ " "
			+ TAG_NID
			+ "=\""
			+ getNID()
			+ "\" "
			+ TAG_TITLE
			+ "=\""
			+ getTitle()
			+ "\"";
	}

	/**
	* return the passed id warped in this object
	*/
	private String _getTraversalNID(String s) {
		String myAddOn= _getStartNID() + ">" + s + "</" + xClassType + ">";
		// if Container = null (ends)
		if (xContainer == null)
			return myAddOn;
		return xContainer._getTraversalNID(myAddOn);
	}

	/** 
	* return the title of this object
	**/
	public  final String getTitle() {
		return xTitle.toString();
	}

	/** 
	* set the Title of this Object
	**/
	public  final void setTitle(String title) {
		this.xTitle.setActualTranslation(title);
		this.fireNamedEvent(NamedEvent.TITLE_MODIFIED);
	}

	/** 
	* return the title of this object
	**/
	public  final String getDescription() {
		return xDescription.toString();
	}

	/** 
	* set the Title of this Object
	**/
	public  final void setDescription(String description) {
		this.xDescription.setActualTranslation(description);
		this.fireNamedEvent(NamedEvent.DESCRIPTION_MODIFIED);
	}

	/**
	 * set the Container of this Object.
	 */
	public  final void setContainer(Named container) {
		this.xContainer= container;
	}

	/** 
	* default toString() : getSmallNID();
	**/
	public String toString() {
		return getSmallNID();
	}

	// ------------------ Events -------------------------//
	
	/**
	 * Add an event listener to the object matching all events from this
	 * object and the nested ones.
	 * @param nel The event listener to call when the event occurs.
	 */
	public final void addNamedEventListener(NamedEventListener nel) {
		addNamedEventListener(nel, NamedEvent.ALL_EVENTS, null);
	}
	
	/**
	 * Add an event listener to the object filtering a single event code.
	 * @param nel The event listener to call when the event occurs.
	 * @param eventType Code of the event to match. One of NamedEvent constants.
	 */
	public final void addNamedEventListener(NamedEventListener nel,
									  int eventType) {
		addNamedEventListener(nel, eventType, null);
	}
	
	/**
	 * Add an event listener filtering a specific source class.
	 * @param nel The event listener to call when the event occurs.
	 * @param sourceType The class object of the source the listener filters.
	 */
	public final void addNamedEventListener(NamedEventListener nel,
								      Class sourceType) {
		addNamedEventListener(nel, NamedEvent.ALL_EVENTS, sourceType);
	}
	
	/**
	 * Add an event listener filtering the event code and the source class.
	 * @param nel The event listener to call when the event occurs.
	 * @param eventType The class object of the source the listener filters.
	 * @param sourceType Code of the event to match. One of NamedEvent constants.
	 */
	public final void addNamedEventListener(NamedEventListener nel,
									  int eventType,
									  Class sourceType) {
		if (sourceType != null) {
			assert (Named.class.isAssignableFrom(sourceType)) :
				"c must be a subclass of Named";
		}
		
		CommonNamedEventFilter filter = 
			new CommonNamedEventFilter(eventType, sourceType);
		addNamedEventListener(nel, filter);
	}
	
	/** 
	 * Adds an EventListener. Using the rules of a user defined filter.
	 * @param nel the class object of the source the listener filters.
	 * @param filter object implementing the match method.
	 */
	public final void addNamedEventListener( NamedEventListener nel,
										   EventFilter filter) {
		m_eventManager.addListener(filter, nel, this);
	}

	/** 
	 * Removes an Event Listener. 
	 * @param nel The listener to remove.
	 */
	public final void removeNamedEventListener(NamedEventListener nel) {
		m_eventManager.removeListener(nel, this);
	}
	
	/**
	 * Fires an Event. 
	 * @param eventCode one of NamedEvent.*;
	 */
	public final void fireNamedEvent(int eventCode) {
		// TODO This method should have protected visibility
		// and only named object should fire their changes, but
		// it has to be corrected in a lot of place...
		if (!silent) 
			propagateNamedEvent(new NamedEvent(this, eventCode));
	}

	protected final void fireNamedEvent(NamedEvent ne) {
		propagateNamedEvent(ne);
	}
	
	/**
	 * Schedule the Event into the eventQueue. And pass the event to the
	 * container.
	 */
	private final void propagateNamedEvent(NamedEvent ne) {
	    if (!silent) {
	    	m_eventManager.fireEvent(this,ne);
	    }
	    Named container = getContainer();
    	if (container != null)
    		container.propagateNamedEvent(ne);
	}

	// ------------------ XML ENCODING -------------------//

	/** XML ENCODING CONSTRUCTOR DO NOT USE **/
	public Named() {}

	/**
	 * XML ENCODING
	 */
	public String getXClassType() {
		return xClassType;
	}

	/**
	 * XML ENCODING
	 */
	public TString getXDescription() {
		return xDescription;
	}

	/**
	 * XML ENCODING
	 */
	public TString getXTitle() {
		return xTitle;
	}

	/**
	 * XML ENCODING
	 */
	public void setXClassType(String string) {
		xClassType= string;
	}

	/**
	 * XML ENCODING
	 */
	public void setXContainer(Named named) {
		xContainer= named;
	}

	

	/**
	 *  XML ENCODING
	 */
	public void setXDescription(TString string) {
		xDescription= string;
	}

	/**
	 * XML ENCODING
	 */
	public void setXTitle(TString string) {
		xTitle= string;
	}
	
	
	/**
	 * XML
	 */
	public void setXNID(String nID) {
		this.xNID= nID;
		rewriteID();
	
	}
	
	/** 
	 * add ID -- used to fill  named references
	 * 
	 */ 
	private void rewriteID() {
		if (xTarification == null) return;
		if (xNID == null) return;
		xTarification.newNamed(this,false);
	}
	
	/**
	 * XML ENCODING
	 */
	public void setXTarification(Tarification tarification) {
		this.xTarification= tarification;
		rewriteID();
	
	}
	
	/**
	 * XML
	 */
	public Tarification getXTarification() {
		return xTarification;
	}

	/**
	 * XML
	 */
	public Named getXContainer() {
		return xContainer;
	}

	/**
	 * XML
	 */
	public String getXNID() {
		return xNID;
	}

}
/**
* $Log: Named.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:36  perki
* First commit on sourceforge
*
* Revision 1.47  2004/11/12 14:28:39  jvaucher
* New NamedEvent framework. New bugs ?
*
* Revision 1.46  2004/11/09 08:57:36  perki
* *** empty log message ***
*
* Revision 1.45  2004/11/08 18:16:17  perki
* *** empty log message ***
*
* Revision 1.44  2004/11/08 17:29:10  perki
* *** empty log message ***
*
* Revision 1.43  2004/11/08 17:19:44  perki
* New Named Event Handleing (Threaded) may be Buggy
*
* Revision 1.42  2004/10/15 06:38:59  perki
* Lot of cleaning in code (comments and todos
*
* Revision 1.41  2004/09/22 06:47:04  perki
* A la recherche du bug de Currency
*
* Revision 1.40  2004/09/16 17:26:37  perki
* *** empty log message ***
*
* Revision 1.39  2004/09/08 16:35:14  perki
* New Calculus System
*
* Revision 1.38  2004/09/03 12:22:28  kaspar
* ! Log.out -> log4j second part
*
* Revision 1.37  2004/09/02 15:51:46  perki
* Lot of change in calculus method
*
* Revision 1.36  2004/08/25 14:20:50  kaspar
* + Logging facility for events. Not that it helps...
*
* Revision 1.35  2004/07/31 16:45:56  perki
* Pairing step1
*
* Revision 1.34  2004/07/08 15:49:22  perki
* User node visibles on trees
*
* Revision 1.33  2004/07/08 14:58:59  perki
* Vectors to ArrayList
*
* Revision 1.32  2004/07/08 09:43:20  perki
* *** empty log message ***
*
* Revision 1.31  2004/06/21 17:26:14  perki
* added compact tree node
*
* Revision 1.30  2004/05/11 15:53:00  perki
* more calculus
*
* Revision 1.29  2004/05/10 19:00:51  perki
* Better amount option viewer
*
* Revision 1.28  2004/04/12 12:30:28  perki
* Calculus
*
* Revision 1.27  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.26  2004/03/18 16:26:54  perki
* new option model
*
* Revision 1.25  2004/03/18 15:43:33  perki
* new option model
*
* Revision 1.24  2004/03/17 14:28:53  perki
* *** empty log message ***
*
* Revision 1.23  2004/03/03 11:35:07  perki
* Un petit bateau
*
* Revision 1.22  2004/03/03 10:47:47  carlito
* *** empty log message ***
*
* Revision 1.21  2004/03/02 17:01:41  perki
* breizh cola. le cola du phare ouest
*
* Revision 1.20  2004/03/02 16:28:27  perki
* breizh cola. le cola du phare ouest
*
* Revision 1.19  2004/03/02 16:01:32  carlito
* *** empty log message ***
*
* Revision 1.18  2004/02/24 13:33:48  carlito
* *** empty log message ***
*
* Revision 1.17  2004/02/23 18:46:04  perki
* *** empty log message ***
*
* Revision 1.16  2004/02/22 18:09:20  perki
* good night
*
* Revision 1.15  2004/02/22 15:57:25  perki
* Xstream sucks
*
* Revision 1.14  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.13  2004/02/16 11:17:10  perki
* new event model
*
* Revision 1.12  2004/02/06 10:04:22  perki
* Lots of cleaning
*
* Revision 1.11  2004/02/04 19:04:19  perki
* *** empty log message ***
*
* Revision 1.10  2004/01/30 15:18:12  perki
* *** empty log message ***
*
* Revision 1.9  2004/01/29 14:50:54  perki
* *** empty log message ***
*
* Revision 1.8  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.7  2004/01/23 17:30:59  perki
* *** empty log message ***
*
* Revision 1.6  2004/01/19 17:00:42  perki
* *** empty log message ***
*
* Revision 1.5  2004/01/18 15:21:18  perki
* named and jdoc debugging
*
* Revision 1.4  2004/01/18 14:23:52  perki
* Naming about to be finished
*
* Revision 1.3  2004/01/17 17:21:16  perki
* Naming + et +
*
* Revision 1.2  2004/01/17 14:27:54  perki
* Better (Best?) Named implementation
*
* Revision 1.1  2003/12/10 16:38:40  perki
* *** empty log message ***
*
*/