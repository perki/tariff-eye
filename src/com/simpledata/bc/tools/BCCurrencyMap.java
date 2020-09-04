/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 24 sept. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.simpledata.bc.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.simpledata.bc.datamodel.money.Currency;

/**
 * Map to be saved with currencies
 */
public class BCCurrencyMap extends HashMap {
    
    public BCCurrencyMap(String defaultCurrency) {
        put(defaultCurrency,1d);
    }
    
    public void put(String currencyCode,double value) {
        if (value == 0) {value = -1d;}
        super.put(currencyCode,new Double(value));
        myCurrencies = null; // invalidate the currency list
    }
    
    /** return -1 if this value is unkown **/
    public double getValue(String currencyCode) {
        Object o = super.get(currencyCode);
        if (o == null || ! (o instanceof Double)) return -1;
       return ((Double) o).doubleValue();
    }
    
    /** return the known currencies has an array **/
    public Currency[] getCurrencies() {
        if (myCurrencies == null) doCurrencies();
        return myCurrencies;
    }
    
    private transient Currency[] myCurrencies;
    /** refesh the currency list **/
    private synchronized void doCurrencies() {
        myCurrencies = new Currency[size()];
       
        Vector v = new Vector(keySet());
        Collections.sort(v);
        
        Iterator i = v.iterator();
        int j = 0;
        String key;
        while (i.hasNext()) {
            key = (String) i.next();
            myCurrencies[j] = new Currency(key);
            j++;
        }
    }
    
    //------------------- XML --------------------//
    /** XML ***/
    public BCCurrencyMap() {};
}
