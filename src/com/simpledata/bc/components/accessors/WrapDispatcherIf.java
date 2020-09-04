/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 27 ao�t 2004
 */
package com.simpledata.bc.components.accessors;

import java.util.ArrayList;

import com.simpledata.bc.components.TarifTreeItem;
import com.simpledata.bc.components.bcoption.OptionBoolean;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.datamodel.WorkSheet;

/**
 * This class provides accessors for the data contained in a DispatcherIf.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: WrapDispatcherIf.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
public class WrapDispatcherIf {
    // FIELD
    private DispatcherIf m_dispatcher;
    
    // CONSTRUCTOR
    
    /**
     * Creates a new accessor utility for the given DispatcherIf
     * @param dispatcher A DispatcherIf 
     */
    public WrapDispatcherIf(DispatcherIf dispatcher) {
       m_dispatcher = dispatcher;
    }
    
    // METHODS
    
    /**
     * This method return the current selected state of the DispatcherIf.
     * @return Current selected state.
     */
    public boolean getCurrentState() {
        ArrayList options = m_dispatcher.getOptions(OptionBoolean.class);
        assert options.size() > 0 : "Dispatcher must contain a boolean option";
        OptionBoolean ob = (OptionBoolean) options.get(0);
        return ob.getState();
    }
    
    /**
     * This method return the WorkSheet linked to the current state
     * of the dispatcher.
     * @return either the 'Yes' WorkSheet or the 'No' WorkSheet.
     */
    public WorkSheet getAppliedWorkSheet() {
        if (getCurrentState()) 
            return m_dispatcher.getYesWorkSheet();
      
        return m_dispatcher.getNoWorkSheet();
    }
    
    /**
     * Same as getAppliedWorkSheet, but it returns a TarifTreeItem
     * object. This method raise an assertion exception if the WorkSheet
     * cannot be casted.
     * @return either the 'Yes' TarifTreeItem or the 'No' TarifTreeItem.
     */
    public TarifTreeItem getAppliedTarifTreeItem() {
        WorkSheet ws = getAppliedWorkSheet();
        
        assert ws instanceof TarifTreeItem : "The applicable worksheet should be a TarifTreeItem object";
        return (TarifTreeItem)ws;
    }
}
