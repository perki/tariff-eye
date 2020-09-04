/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 15 juin 2004
 * $Id: OptionCase.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import java.util.Iterator;

import com.simpledata.bc.components.worksheet.dispatcher.DispatcherCase;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.tools.OrderedMapOfStrings;

import org.apache.log4j.Logger;


/**
 * This options represents a multiple choice option with
 * unique selection...
 * TODO maybe have TStrings instead of simple Strings
 */
public class OptionCase extends BCOption {

    /** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Case";
		
	public final static Logger m_log = Logger.getLogger( OptionCase.class );
    
	private OrderedMapOfStrings xCasesMap;
	
	private String xSelectedKey;
	
	
	/** EVENT TYPES USED by fireDataChange(int eventType, int involvedIndex) */
    public final static int CASE_MODIFIED = 0;
    public final static int CASE_ADDED = CASE_MODIFIED+1;
    public final static int CASE_REMOVED = CASE_ADDED+1;
	

    
    /**
     * CONSTRUCTOR
     * @param ws parent WorSheet
     * @param title
     */
    public OptionCase(WorkSheet ws, String title) {
        super(ws, title);
    }
	
    private synchronized OrderedMapOfStrings getCasesMap() {
        if (xCasesMap == null) {
            xCasesMap = new OrderedMapOfStrings();
            
//            // We initialize some cases :
//            // We do not use add case to avoid event firing
//            xCasesMap.put("Case 1", getNextKey());
//            xCasesMap.put("Case 2", getNextKey());
//            xCasesMap.put("Case 3", getNextKey());
//            
//            String firstKey = xCasesMap.getKeyForPos(0);
//            setSelectedCase(firstKey);
            
        }
        return xCasesMap;
    }
    
    	public OrderedMapOfStrings getCases() {
    	    return (OrderedMapOfStrings)(getCasesMap().clone());
    	}

    /**
	 * Generates the next key to be used for WorkSheet
	 * indexing
	 * @return
	 */
	public String getNextKey() {
	    String keyGen = Integer.toString(0);
	    
        // Get the maximum actual key...
	    OrderedMapOfStrings hm = getCasesMap();
	    int max = -1;
	    for (Iterator i = hm.getKeys().iterator(); i.hasNext();) {
	        String key = (String)i.next();
	        int myInt = -1;
	        try {
	            myInt = Integer.parseInt(key);
	        } catch (NumberFormatException nfe) {
	            m_log.error( nfe );
	        }
	        if (myInt > max) {
	            max = myInt;
	        }	        
	    }
	    
	    if (max >= 0) {
	        max++;
	        keyGen = Integer.toString(max);
	    }
	    
	    return keyGen;
	}
    
    
    protected int getStatusPrivate() {
        // TODO make it correct
        return BCOption.STATE_OK;
    }
    
    /**
     * Return the number of cases
     */
    public int getNumberOfCases() {
        return getCasesMap().size();
    }
    
    public String getKeyForPos(int index) {
        return getCasesMap().getKeyForPos(index);
    }
 
    public int getPosForKey(String key) {
        return getCasesMap().getKeyPos(key);
    }
    
    
    /**
     * Set the selected case
     * @param firstKey the key of the case we want to select
     */
    public boolean setSelectedCase(String key) {
        boolean res = false;
        OrderedMapOfStrings oms = getCasesMap();
        
        int tot = oms.size();
        if (tot > 0) {
            // We have elements
            if (oms.containsKey(key)) {
                if (xSelectedKey != key) {
                    xSelectedKey = key;
                    res = true;
                    fireDataChange();
                }
            } else {
                // There is a problem with the given key
                // We correct selecting the first case
                setSelectedCase(getCasesMap().getKeyForPos(0));
            }
        } else {
            // There are no elements
            xSelectedKey = null;
        }
        return res;
    }
    
    /**
     * Return the key of the currently selected case
     */
    public String getSelectedKey() {
        return xSelectedKey;
    }
    
    /**
     * Return the index of the currently selected case
     * @return
     */
    public int getSelectedIndex() {
        String key = getSelectedKey();
        if (key == null) {
            return -1;
        }
        return getCasesMap().getKeyPos(key);
    }
    
    /**
     * Add a new case 
     * @param title
     */
    public void addCase(String title) {
        String key = getNextKey();
        getCasesMap().put(title, key);
        setSelectedCase(key);
        informAttachedWorkSheets(CASE_ADDED, key);
    }
    
    /**
     * Remove case identified by key
     * @param key
     */
    public boolean removeCase(String key) {
        if (key == null) return false;
        boolean res = false;
        OrderedMapOfStrings oms = getCasesMap();
        if (oms.containsKey(key)) {
            oms.remove(key);
            informAttachedWorkSheets(CASE_REMOVED, key);
            //fireDataChange();
            if (key.equals(getSelectedKey())) {
                // Ensure another selection
                setSelectedCase("-1");
            }
            res = true;
        }
        return res;
    }
    
    /**
     * Returns the title of the case identified by key
     * @param key
     * @return
     */
    public String getCase(String key) {
        return getCasesMap().get(key);
    }
    
    /**
     * Return a couple key-caseTitle representing fully the case at index
     * @param index
     * @return
     */
    public String[] getFullCase(int index) {
        String[] res = new String[2];
        OrderedMapOfStrings oms = getCasesMap();
        String key = oms.getKeyForPos(index);
        if (key != null) {
            res[0] = key;
            res[1] = oms.get(key);
        }
        return res;
    }
    
    /**
     * Modify title for a specified case
     * @param key key of the case
     * @param s new string to apply
     * @return
     */
    public boolean modifyCase(String key, String newTitle) {
        boolean res = false;
        OrderedMapOfStrings oms = getCasesMap();
        if (oms.containsKey(key)) {
            oms.put(newTitle, key);
            informAttachedWorkSheets(CASE_MODIFIED, key);
            //fireDataChange();
            res = true;
        }
        return res;
    }
    
    
    /**
     * This is an internal event throwing to allow dispatchers which
     * are connected to this option to have 
     * @param eventType
     * @param involvedIndex
     */
    public void informAttachedWorkSheets(int eventType, 
            String caseKey) {
        // Inform all related WorkSheets that the option has changed
        // Actions have to be taken accordingly
        for (Iterator i = getWorkSheets().iterator(); i.hasNext(); ) {
            WorkSheet ws = (WorkSheet)i.next();
            if (ws instanceof DispatcherCase) {
                DispatcherCase dc = (DispatcherCase)ws;
                dc.optionChanged(eventType, caseKey);
            }
        }
        
    }
    
    
    /// ----------------------- XML -------------------- ///
    
    public OptionCase() {}
    
    public OrderedMapOfStrings getXCasesMap() {
        return xCasesMap;
    }
    
    public void setXCasesMap(OrderedMapOfStrings oms) {
        xCasesMap = oms;
    }
    
    /**
     * @return Returns the xSelectedKey.
     */
    public String getXSelectedKey() {
        return xSelectedKey;
    }
    /**
     * @param selectedKey The xSelectedKey to set.
     */
    public void setXSelectedKey(String selectedKey) {
        xSelectedKey = selectedKey;
    }
}

/*
 * $Log: OptionCase.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.9  2004/11/17 18:19:12  carlito
 * Design reviewed
 *
 * Revision 1.8  2004/11/17 17:17:04  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/09/09 12:43:07  perki
 * Cleaning
 *
 * Revision 1.6  2004/09/03 11:47:53  kaspar
 * ! Log.out -> log4j first half
 *
 * Revision 1.5  2004/07/01 14:45:14  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/06/28 12:48:49  carlito
 * Dispatcher case++
 *
 * Revision 1.2  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.1  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 */