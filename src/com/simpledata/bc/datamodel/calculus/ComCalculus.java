/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ComCalculus.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.calculus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.money.Money;


/** an object passed as id that contains a trace of the calculus **/
public class ComCalculus {
	private static final Logger m_log = Logger.getLogger( ComCalculus.class ); 
	
	/** in my context the list of up to date calculus **/
	private HashSet upTodate/*<ComModifier>*/;
	private int id;
	private ComCalc owner;

	protected ComCalculus(ComCalc cc, ComModifier initiator,int id) {
		
		upTodate = new HashSet/*<ComModifier>*/();
		this.id = id;
		this.owner = cc;
	}
	
	
	
	/**
	 * request the calculus of other entities
	 * @param cms is a vector of ComModifiers!!
	 */
	private void advertise(Collection/*<ComModifier>*/ cms) {
		for (Iterator/*<ComModifier>*/ i=cms.iterator();i.hasNext();)
			refresh((ComModifier) i.next());
	}
	
	public HashSet advertiseSet; 
	
	int depth = 0;
	
	
	/**
	 * init a calculus (makes it for real) if needed<BR>
	 * ABSOLUTELY NOT THREAD SAFE!!!!
	 * @param cm
	 */
	private void startComCalc(ComModifier cm) {
		assert cm != null;
		
		if (upTodate.contains(cm)) return; // prevent loops
		
		
		ComMemory cmem = owner.getComMemory(cm);
		
		// used to group advertising
		if (depth == 0) {
			assert advertiseSet == null;
			advertiseSet = new HashSet();
		}
		
		
		//m_log.debug(sDepth()+" OPEN "+cm+" "+cm.getClass());
		depth++;
		Money value = cmem.open(this);
		upTodate.add(cm);
		cm.startComCalc(this,value,advertiseSet); // make the calculus
		
		// apply the discount (if any)
		if (owner.withDiscounts() && cm.getReductionOrFixed() != null) {
    		cm.getReductionOrFixed().apply(value);
    	} 	
		
		owner.getComMemory(cm).close(this);
		
		depth--;
		//m_log.debug(sDepth()+" CLOSE "+cm+" "+cm.getClass());
		
		// advertise grouped ComModifier 
		if (depth == 0) {
			Collection c = (Collection) advertiseSet.clone();
			advertiseSet = null;
			advertise(c);
		}
	}

	/**
	 * refresh a calculus if neede
	 */
	public void refresh(ComModifier cm) {
		startComCalc(cm);
	}
	
	
	
	/** 
	 * get the Com for this modifier<BR>
	 * ABSOLUTELY NOT THREAD SAFE!!!!
	 * If unkown .. startCom.<BR>
	 * If known .. must be closed
	 **/
	public Money getCom(ComModifier cm) {
	        
		//m_log.debug(sDepth()+" REQ "+cm+" "+cm.getClass());
		ComMemory cmem = owner.getComMemory(cm);
		if (cmem.opened(this)) {
			m_log.error(
			"ComModifier :Loop detected ["+cm
			+"]["+cm.getComTitle() +"]["+cm.getClass()+"]" ,
			new Exception());
			return null;
		} 
		
		startComCalc(cm);
		return cmem.getValue();
	}
	
	public String toString() {
		return ""+id;
	}
	
	
}





/*
 * $Log: ComCalculus.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:45  perki
 * First commit on sourceforge
 *
 * Revision 1.18  2004/11/17 10:52:04  perki
 * Discount display preview step1
 *
 * Revision 1.17  2004/11/17 09:09:49  perki
 * first step of discount extraction
 *
 * Revision 1.16  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.15  2004/09/14 14:57:46  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/09/09 16:02:16  perki
 * Threaded Calculus
 *
 * Revision 1.13  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.12  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.11  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.10  2004/09/03 12:27:17  kaspar
 * ! Log.out -> log4j third part
 *
 * Revision 1.9  2004/09/03 12:24:06  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/09/03 12:22:28  kaspar
 * ! Log.out -> log4j second part
 *
 * Revision 1.7  2004/09/03 11:09:30  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/09/02 15:51:46  perki
 * Lot of change in calculus method
 *
 * Revision 1.5  2004/08/02 18:22:01  perki
 * Repartition viewer on simulator
 *
 * Revision 1.4  2004/07/16 18:39:20  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.2  2004/07/06 11:05:53  perki
 * calculus is now synchronized for forward options
 *
 * Revision 1.1  2004/04/13 21:30:41  perki
 * *** empty log message ***
 *
 */