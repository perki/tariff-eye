/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: NullWorkSheet.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.components.worksheet.workplace;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;


/**
 * An empty Workplace, with nothing inside
 */
public class NullWorkSheet extends WorkPlaceAbstract {
    
    /** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
    public final static String WORKSHEET_TITLE = "";
    
    /**
     * @param parent
     * @param title
     * @param id
     * @param key
     */
    public NullWorkSheet(
            WorkSheetContainer parent,
            String title,
            String id,
            String key) {
        super(parent, title, id, key);
    }
    

    /**
     * @see WorkPlaceAbstract#visit(TarifTreeVisitor)
     */
    public void visit(TarifTreeVisitor v) {
       // nothing to visit
    }

    /**
     * @see com.simpledata.bc.datamodel.WorkSheet#isValid()
     */
    public boolean isValid() {
       return true;
    }

    /**
     * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
     */
    public void initializeData() {} 

    /**
     * @see WorkSheet#getAcceptedNewOptions()
     */
    public Class[] getAcceptedNewOptions() {
        return new Class[0];
    }

    /**
     * @see WorkSheet#getAcceptedRemoteOptions()
     */
    public Class[] getAcceptedRemoteOptions() {
        return new Class[0];
    }

    /**
     * @see WorkSheet#_canRemoveOption(BCOption)
     */
    protected boolean _canRemoveOption(BCOption bco) {
        return false;
    }

    /**
     * @see WorkSheet#privateComCalc(ComCalculus,Money)
     */
    protected void privateComCalc(ComCalculus cc, Money Value) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see WorkSheet#copy(WorkSheetContainer, String)
     */
    protected WorkSheet _copy(WorkSheetContainer parent, String key) {
        return WorkSheetManager.createWorkSheet(parent,this.getClass(),key);
    }

    /**
     * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedReducType()
     */
    public int getAcceptedReducType() {
        return ReducOrFixed.ACCEPT_REDUC_NO;
    }
    
    
    /** --------------------- XML ------------------ **/
    public NullWorkSheet() {}

}
