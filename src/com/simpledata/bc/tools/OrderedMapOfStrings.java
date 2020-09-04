/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 24 juin 2004
 * $Id: OrderedMapOfStrings.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */

package com.simpledata.bc.tools;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * This class acts almost like an HashMap
 * keeping a map of String keys and objects
 */
public class OrderedMapOfStrings implements Cloneable, Serializable {
    
    private ArrayList xKeys;
    private ArrayList xObjects;
    
    public OrderedMapOfStrings() {
        xKeys = new ArrayList();
        xObjects = new ArrayList();
    }
    
    public ArrayList getKeys() {
        return xKeys;
    }
    
    public String get(String key) {
        int index = xKeys.indexOf(key);
        if (index >= 0) {
            return (String)xObjects.get(index);
        }
        return null;
    }
    
    public void put(String object, String key) {
        // Retrieve index to see if it exists
        int index = xKeys.indexOf(key);
        if (index < 0) {
            // We create a new entry
            xKeys.add(key);
            xObjects.add(object);
        } else {
            // We replace the existing entry
        	xObjects.set(index,object);
        }
    }
    
    public void remove(String key) {
        int i = getKeyPos(key);
        if (i >= 0) {
            xKeys.remove(i);
            xObjects.remove(i);
        }
    }
    
    public boolean containsKey(String key) {
        return xKeys.contains(key);
    }
    
    public boolean containsObject(String object) {
        return xObjects.contains(object);
    }
    
    public int getKeyPos(String key) {
        return xKeys.indexOf(key);
    }
    
    public int getObjectPos(String object) {
        return xObjects.indexOf(object);
    }
    
    public String getKeyForPos(int index) {
        String res = null;
        // We verify index validity
        if ((0 <= index) && (index < xKeys.size())) {
            res = (String)xKeys.get(index);
        }
        return res;
    }
    
    public void clear() {
        xKeys.clear();
        xObjects.clear();
    }
    
    public int size() {
        return xKeys.size();
    }
    
    
    public Object clone() {
        OrderedMapOfStrings oms = new OrderedMapOfStrings();
        for (Iterator i = getKeys().iterator(); i.hasNext();) {
            // We fill the clone
            String key = (String)i.next();
            String object = get(key);
            oms.put(object,key);
        }
        return oms;
    }
    
    /* XML */
    
    /**
     * @return Returns the xKeys.
     */
    public ArrayList getXKeys() {
        return xKeys;
    }
    /**
     * @param keys The xKeys to set.
     */
    public void setXKeys(ArrayList keys) {
        xKeys = keys;
    }
    /**
     * @return Returns the xObjects.
     */
    public ArrayList getXObjects() {
        return xObjects;
    }
    /**
     * @param objects The xObjects to set.
     */
    public void setXObjects(ArrayList objects) {
        xObjects = objects;
    }
}



/*
 * $Log: OrderedMapOfStrings.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.3  2004/07/30 15:41:57  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.1  2004/06/28 12:48:49  carlito
 * Dispatcher case++
 *
 */