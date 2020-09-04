/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: FeeBySlice.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.components.worksheet.workplace.tools;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Copiable;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.OrderedMapOfDoubles;

/**
 * This utility can handle different fees based on a slices.
 */
public class FeeBySlice implements Serializable, Copiable , DataBySlice{
    private static final Logger m_log = Logger.getLogger(FeeBySlice.class );
	
    
	/** currency of the slices values and minimum value**/
	private Money minimum= null;
	
	/** the slices **/
	private OrderedMapOfDoubles omod/*<Double>*/= null;
   
	/** @param dummy whatever you want **/
	public FeeBySlice(String dummy) {
        omod= new OrderedMapOfDoubles(new FeeBySliceValue());
        minimum = new Money(0d);
    }

    /**
	 * get the minimum value (also hold the currency)
	 */
	public Money getMinimum() {
		return minimum;
	}
	/**
	 * change the currency<BR>
	 * Note: Also use by XML
	 */
	public void setMinimum(Money m) {
		this.minimum = m;
	}
   
	/**
	 * get the used OrderedMapOfDoubles
	 * <BR>Note: Also use by XML
	 */
	public OrderedMapOfDoubles getOmod() {
		return omod;
	}
	
	/**
	 * get the Comission taken by this RBS for this Money amount<BR>
	 * NOTE!! minimum is never applied.. and should be checked by
	 * owner..
	 */
	public Money getCom(double key) {
	    Money result = new Money(0d,minimum.getCurrency());
	    
	    
	    
	    Object o = omod.getValueLastLower(key);
	    //m_log.debug("key:"+key+" -> obj "+o);
	    
	    if (o == null || ! (o instanceof FeeBySliceValue)) {
			m_log.error( "Cannot find a Double" );
		} else {
		    result.setValue(((FeeBySliceValue) o).getValue());
		}
	    
	 
	    return result;
	}
    
    /**
     * @see Copiable#copy()
     */
    public Copiable copy() {
        FeeBySlice copy = new FeeBySlice();
    	copy.setOmod((OrderedMapOfDoubles) omod.copy());
		copy.setMinimum((Money) getMinimum().copy());
        return copy;
    }
    
    /** create a Line at this key position **/
    public void createLineAt(double key) {
        omod.put(key, new FeeBySliceValue());
    }
    
    //-------------- XML ----------//
    
    /** xml **/
    public FeeBySlice() {}
    
    /**
	 * XML
	 */
	public void setOmod(OrderedMapOfDoubles doubles) {
		omod= doubles;
	}
}


