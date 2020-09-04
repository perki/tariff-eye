/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: WorkPlaceRateBySliceOnAmount.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.components.worksheet.workplace;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.AbstractOptionMoneyAmount;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.tools.RateBySlice;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;

/**
 *An abstract for all RateBySlice on a Single Amount Option
 */
public  class WorkPlaceRateBySliceOnAmount 
	extends WorkPlaceAbstract {
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "Rate By Slice On Amount";
	
	private RateBySlice rbs;

	
	/**
	* constructor.. should not be called by itself. 
	* use WorkSheet.createWorkSheet(Dispatcher d,Class c)
	*/
	public WorkPlaceRateBySliceOnAmount(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id, key);
		
	}

    /**
     * Valid only if it has one option
     */
    public final boolean isValid() {
        return (getOptions().size() == 1);
    }

    /**
     * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
     */
    public final void initializeData() {
        rbs= new RateBySlice(true);
    }

    /**
     * if not valid require one of my Option
     */
    public final Class[] getAcceptedNewOptions() {
        if (isValid()) return new Class[0];
        return AbstractOptionMoneyAmount.defaultOptions();
    }

    /**
     * @see WorkSheet#getAcceptedRemoteOptions()
     */
    public final Class[] getAcceptedRemoteOptions() {
        return getAcceptedNewOptions();
    }

    /**
     * @see WorkSheet#_canRemoveOption(BCOption)
     */
    protected final boolean _canRemoveOption(BCOption bco) {
        return true;
    }

    /**
     * @seeWorkSheet#privateComCalc(ComCalculus,Money)
     */
    protected void privateComCalc(ComCalculus cc, Money value) {
        if (! isValid()) return;
        AbstractOptionMoneyAmount o = 
            (AbstractOptionMoneyAmount) getOptions().get(0);
        value.operation(
                getRbs().getCom(o.moneyValue(cc)),
                o.numberOfLines(cc));
    }

    /**
     * @see WorkSheet#copy(WorkSheetContainer, String)
     */
    protected WorkSheet _copy(WorkSheetContainer parent, String key) {
        WorkPlaceRateBySliceOnAmount ws=
			(WorkPlaceRateBySliceOnAmount) WorkSheetManager.createWorkSheet(
				parent,
				this.getClass(),
				key);
		ws.setRbs((RateBySlice) rbs.copy());
		return ws;
    }

    /**
     * @see WorkSheet#getAcceptedReducType()
     */
    public int getAcceptedReducType() {
        return ReducOrFixed.ACCEPT_REDUC_FULL;
    }
    
    /**
	 * get the used rate By Slice<BR>
	 * note: also use by XML
	 */
	public RateBySlice getRbs() {
		
		return rbs;
	}
	
	 /**
     * @see WorkPlaceAbstract#visit(TarifTreeVisitor)
     */
    public void visit(TarifTreeVisitor v) {
        v.caseWorkPlaceRateBySliceOnAmount(this);
    }
	
	//------------------ XML -----------------//
    
    /** XML **/
    public WorkPlaceRateBySliceOnAmount() {
        // TODO XXX remove when ficles are translated
//      if (Currency.INIT_AT_XML_CREATION) initializeData();       
    }
    
	/** XML **/
	public void setRbs(RateBySlice rbs) {
	    this.rbs = rbs;
	}

   

}
