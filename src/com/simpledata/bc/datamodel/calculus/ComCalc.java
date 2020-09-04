/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ComCalc.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.calculus;

import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Money;

/**
 * A class that does calculus or reporting
 */
public class ComCalc {
	private static final Logger m_log = Logger.getLogger( ComCalc.class ); 
	
	/** calculus id **/
	int calculus;
	
	/** an hashmap that hold the Commission of each ComModifiers **/
	private WeakHashMap commissions;
	
	/** A set of ComModifier kept in memory to be flushed As the same set of calculus **/
	private HashSet/*<ComModifier>*/ groupedCalculus;
	
	/** A boolean tha keeps in memory if Calculus should be grouped **/
	private boolean grouped;
	
	protected Tarification tarification;
	
	/** a ComCalc without discount is associated with any discount **/
	private ComCalc withoutReduc;
	
	/** if true calculus are also made without discount **/
	private boolean woDiscounts;
	
	public ComCalc(Tarification t) {
		this(t,true);
	}
	
	
	private ComCalc(Tarification t,boolean withDiscount) {
	    commissions = new WeakHashMap();
		calculus = 0;
		grouped = false;
		groupedCalculus = new HashSet();
		tarification = t;
		woDiscounts = false;
		if (withDiscount) {
		    withoutReduc = new ComCalc(t,false);
		}
	}
	
	//---------------- WITH(OUT) DISCOUNTS ------//
	
	/** set if calculus should ALSO be made without discount **/
	public void setWithoutDiscounts(boolean woDiscounts) {
	   this.woDiscounts = woDiscounts;
	}
	
	/** return true if calculus are made woDiscounts **/
	public boolean getWithoutDiscounts() {
	  return woDiscounts;
	}
	
	/** return true if this ComCalc forward to a withoutDiscount ComCal **/
	private boolean forwardToWoDiscounts() {
	    return (getWithoutDiscounts() && withoutReduc != null);
	}
	
	/** return true if this ComCalc takes discounts in acccount **/
	protected boolean withDiscounts() {
	    return withoutReduc != null;
	}
	
	//--------------- GROUPING ------------------//
	
	/** start a grouped Calculus **/
	public void groupedStart() {
	    if (forwardToWoDiscounts()) withoutReduc.groupedStart();
	    
		m_log.debug( "******************* START " );
		if (grouped) { m_log.error( "Already in a grouped calculus" ); }
		grouped = true;
	}
	
	/** flush all the ComModifier in the grouped Calc **/
	public synchronized void groupedStop() {
	    if (forwardToWoDiscounts()) withoutReduc.groupedStop();
	    
		m_log.debug( "******************* STOP " );
		if (! grouped) { m_log.error( "Was not in a grouped calculus" ); }
		grouped = false;
		ComModifier cm = new ComModifier() {
			public String getComTitle() {
				return "GROUP";
			}
			
			public void startComCalc(ComCalculus cc,Money value,
					Set/*<ComModifier>*/ toAdvertise) { 
				toAdvertise.addAll(groupedCalculus);
				groupedCalculus.clear();
			}

            public ReducOrFixed getReductionOrFixed() {
               return null;
            }};
		
		_start(cm);
	}
	
	//--------------- CALCULUS ------------------//
	
	/**  
	 * start a calculation and return a Calculus Dimension
	 **/
	public synchronized void start(ComModifier initiator) {
	    if (forwardToWoDiscounts()) withoutReduc.start(initiator);
	    
	    
		if (! tarification.readyForCalculusAndUse) return ;
		
		if (grouped) {
			groupedCalculus.add(initiator);
			return; 
		}

		
		_start(initiator);
	}
	
	/**  
	 * start a calculation and return a Calculus Dimension
	 **/
	private synchronized void _start(final ComModifier initiator) {
	    final ComCalculus cc = new ComCalculus(this,initiator,calculus++);
        m_log.debug( cc+ " ** STARTED : "+initiator );
        cc.refresh(initiator);
        m_log.debug( cc+ " ** DONE : ");
        tarification.fireNamedEvent(
				NamedEvent.COM_VALUE_CHANGED_TARIFICATION
		);
	}
	
	
	
	//--- should be displaced --------//
	
	
	/** 
	 * Get the comission of an object<BR>
	 * Take care.. you are manipulating the ACTUAL reference of this Money
	 */
	public Money getCom(ComModifier cm) {
		return getComMemory(cm).getValue();
	}
	
	/** 
	 * Get the comission of an object without reduc 
	 */
	public Money getComWithoutDiscount(ComModifier cm) {
	    if (forwardToWoDiscounts()) 
	        {
	        return withoutReduc.getCom(cm);
	        }
		return new Money(0d);
	}
	
	
	/**
	 * Get the ComMemory associated with this ComModifier
	 */
	protected ComMemory getComMemory(ComModifier cm) {
		ComMemory result = (ComMemory) commissions.get(cm);
		if (result == null) {
			result = new ComMemory(cm);
			commissions.put(cm,result);
		}
		return result;
	}
	
	/** add a CommissionListener to this ComissionModifier **/
	public void 
		addCommissionListener(ComModifier cm, ComCalc.ComissionListener cl) {
	    // always froward listeners in case woDiscount is activated
	    if (withDiscounts()) {
		    withoutReduc.addCommissionListener(cm,cl);
		}
		getComMemory(cm).addCommissionChangeListener(cl);
	}
	
	/** use this interface to listen for a proper ComModifier **/
	public interface ComissionListener {
		public void comissionChanged(ComCalculus cc,ComModifier cm);
	}
}








/*
 * $Log: ComCalc.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:45  perki
 * First commit on sourceforge
 *
 * Revision 1.19  2004/11/17 12:04:40  perki
 * Discounts Step 2
 *
 * Revision 1.18  2004/11/17 10:52:04  perki
 * Discount display preview step1
 *
 * Revision 1.17  2004/11/17 09:09:49  perki
 * first step of discount extraction
 *
 * Revision 1.16  2004/11/09 15:56:04  perki
 * *** empty log message ***
 *
 * Revision 1.15  2004/11/09 12:48:26  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/09/16 11:49:45  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/09/14 14:57:46  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/09/09 16:24:15  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/09/09 16:02:16  perki
 * Threaded Calculus
 *
 * Revision 1.10  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.9  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.8  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.7  2004/09/03 12:22:28  kaspar
 * ! Log.out -> log4j second part
 *
 * Revision 1.6  2004/09/03 11:09:30  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/09/02 15:51:46  perki
 * Lot of change in calculus method
 *
 * Revision 1.4  2004/07/16 19:02:36  perki
 * About ready with merging
 *
 * Revision 1.3  2004/07/16 18:39:20  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/04/12 17:34:52  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/04/12 12:33:09  perki
 * Calculus
 *
 */