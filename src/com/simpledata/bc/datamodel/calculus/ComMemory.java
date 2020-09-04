/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ComMemory.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.calculus;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.money.Money;

/** small handler for informations of ComModifiers**/
class ComMemory {
    private static final Logger m_log = Logger.getLogger( ComMemory.class ); 
    
    
	private ComModifier cm;
	private Money value;
	private Money previousValue;
	private HashSet/*<ChangeListener>*/ changeListeners;
	private ComCalculus cc;
	
	
	public ComMemory(ComModifier cm) {
		this.cm = cm;
		value = new Money(0d);
		previousValue = new Money(0d);
		
	}
	
	//------------ deals with open / close -------------//
	
	public synchronized Money open(ComCalculus c) {
	    while (cc != null) {
	        try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	    }
	    cc = c;
	    getValue().setValue(0d);
	    return getValue();
	}
	
	
	public synchronized boolean opened(ComCalculus c) {
	    return cc == c;
	}
	
	public synchronized void close(ComCalculus c) {
	    if (! opened(c)) {
	        m_log.error("How can ["+c+"] close me.. I'm opened by:"+cc);
	        return ;
	    }
	    cc = null;
	    fireCommissionChanged(c);
	}
	
	
	//----------------- values ----------------//

	public Money getValue() {
		return value; 
	}
	

	/** fire change to listeners if needed **/
	public void fireCommissionChanged(ComCalculus cc) {
		if (changeListeners == null) return;
		if (previousValue.xequals(value)) return;
		previousValue.setValue(value);
		for (Iterator i = changeListeners.iterator();i.hasNext();) {
			((ComCalc.ComissionListener) i.next()).comissionChanged(cc,cm);
		}
	}
	
	/** add a listener to this ComModifier **/
	public void addCommissionChangeListener(ComCalc.ComissionListener cl) {
		if (changeListeners == null) 
			changeListeners = new HashSet/*<ChangeListener>*/();
		changeListeners.add(cl);
	}

}