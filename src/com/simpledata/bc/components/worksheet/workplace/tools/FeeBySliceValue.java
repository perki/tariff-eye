/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: FeeBySliceValue.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.components.worksheet.workplace.tools;

import java.io.Serializable;

import com.simpledata.bc.datamodel.Copiable;

/**
 * A container for FeeBySlice double values
 */
public class FeeBySliceValue implements Copiable, Serializable {
    private double value;

    /**
     * @see com.simpledata.bc.datamodel.Copiable#copy()
     */
    public Copiable copy() {
        FeeBySliceValue copy = new FeeBySliceValue();
        copy.setValue(value);
        return copy;
    }
    
    /**
     * @return Returns the value.
     */
    public double getValue() {
        return value;
    }
    /**
     * @param value The value to set.
     */
    public void setValue(double value) {
        this.value = value;
    }
    // ------- XML --------//
    public FeeBySliceValue() {
        value = 0d;
    }
    
}
