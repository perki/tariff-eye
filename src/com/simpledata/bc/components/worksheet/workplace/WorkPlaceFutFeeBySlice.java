/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: WorkPlaceFutFeeBySlice.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.components.worksheet.workplace;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeItem;
import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionFuture;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.FuturesRoot0;
import com.simpledata.bc.components.worksheet.workplace.tools.FeeBySlice;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;

/**
 * Slices for Futures...<BR>
 * Depending on the number of contracts.. choose the fee to apply.
 */
public class WorkPlaceFutFeeBySlice extends WorkPlaceAbstract {
    private static final Logger m_log 
	= Logger.getLogger( WorkPlaceFutFeeBySlice.class ); 

	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "Futures fee by slice";

	public static final int APPLY_ON_OPENING = 2;
	public static final int APPLY_ON_CLOSEING = 1;
	public static final int APPLY_ON_OPENING_AND_CLOSEING = 0;
	
	private static final int APPLY_ON_DEFAULT = 
	    APPLY_ON_OPENING_AND_CLOSEING;
	
	/** one of APPLY_ON_* **/
	private int applyOn;
	
	/** my Fee By slice container **/
	private FeeBySlice fbs;
	
	public WorkPlaceFutFeeBySlice(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id, key);
	}
	
	//------------- content access --------------------------//
	
	
	/**
	 * get the FeeBySlice associated with this WorkPlace
	 * <BR> Note: also used by XML
	 */
	public FeeBySlice getFbs() {
	  
	    return fbs;
	}
	
	/**
	 * change the type of future this workplace should be applied<BR>
	 * fires a NamedEvent.WORKSHEET_DATA_MODIFIED;
	 *
	 * Note: This method takes part in loading from XML.
	 * @param applyOnType one of APPLY_ON_*
	 */
	public void setApplyOn(int applyOnType) {
		if (applyOn == applyOnType) return ;
		applyOn = applyOnType;
		fireNamedEvent(NamedEvent.WORKSHEET_DATA_MODIFIED);
		optionDataChanged(null,null);
	}
	
	/**
	 * get the type of futures this workplace will use<BR>
	 * Note: also used by XML
	 * @return on of APPLY_ON_*
	 */
	public int getApplyOn() {
		return applyOn;
	}
	
	/**
	 * get the FuturesRoot0 tarif I'm working on
	 */
	public FuturesRoot0 getFuturesRoot0() {
		if (getTarif() != null) {
			WorkSheet ws= getTarif().getWorkSheet();
			//	check if I'm attached to a valid tarif
			if (ws != null && (FuturesRoot0.class.isInstance(ws))) {
				return (FuturesRoot0) ws;
			}
			m_log.fatal( "My Tarif does not hold an valid root WS" );
			
		} 
		m_log.warn( "Not on a Tarif" );
		return null;
	}
	
	
	/**
	 * return true if this WorkPlace applies for this Future
	 */
	private boolean appliesOn(OptionFuture of) {
		switch (applyOn) {
			case APPLY_ON_OPENING_AND_CLOSEING:
				return true;
			case  APPLY_ON_OPENING:
				return of.onOpening();
			case  APPLY_ON_CLOSEING:
				return ! of.onOpening();
		}
		return false;
	}
	
	//-------------- extends WorkPlaceAbstract --------------//
	
	 /**
     * @see WorkSheet#initializeData()
     */
    public void initializeData() {
        fbs = new FeeBySlice(null);
    	applyOn = APPLY_ON_DEFAULT;
    }
    
    /**
     * @see WorkSheet#getAcceptedReducType()
     */
    public int getAcceptedReducType() {
        return ReducOrFixed.ACCEPT_REDUC_ONLY_RATE;
    }
	

    /**
     * @see WorkSheet#privateComCalc(ComCalculus, Money)
     */
    protected void privateComCalc(ComCalculus cc, Money value) {
        OptionFuture temp= null;
		
		if (getFuturesRoot0() != null) {
			
			Iterator e= getFuturesRoot0().getOptionsApplicable(
					OptionFuture.class).iterator();
			
			int numberOfContracts = 0;
			
			while (e.hasNext()) {
				temp= ((OptionFuture) e.next());
				if (temp != null) {
					if (appliesOn(temp)) {
						numberOfContracts += temp.numberOfContracts();
					}
				}
			}
			
			Money localCom = getFbs().getCom(
			        numberOfContracts);
			
			
			
			value.operation(localCom,numberOfContracts);
			
//			m_log.debug("val:"+value+"  min:"+fbs.getMinimum()
//			        +" "+value.compareTo(fbs.getMinimum()));
			
			// check for minimum if at least 1 contract
			if (numberOfContracts > 0) {
			    if (value.compareTo(fbs.getMinimum()) < 0) {
			        value.setValue(fbs.getMinimum());
			    }
			}
		}
		
	  
		    
    }
    

    /**
     * @see WorkSheet#isValid()
     */
    public boolean isValid() {return true;}

    
    /**
     * @see TarifTreeItem#visit(TarifTreeVisitor)
     */
    public void visit(TarifTreeVisitor v) {
       v.caseWorkPlaceFutFeeBySlice(this);
    }

   

    /**
     * @see WorkSheet#getAcceptedNewOptions()
     */
    public Class[] getAcceptedNewOptions() {
        //      Accept no new options
		return new Class[0];
    }

    /**
     * @see WorkSheet#getAcceptedRemoteOptions()
     */
    public Class[] getAcceptedRemoteOptions() {
        //      none
		return 	getAcceptedNewOptions();
    }

    /**
     * @see WorkSheet#_canRemoveOption(BCOption)
     */
    protected boolean _canRemoveOption(BCOption bco) {
        return true;
    }


    /**
     * @see WorkSheet#copy(WorkSheetContainer, String)
     */
    protected WorkSheet _copy(WorkSheetContainer parent, String key) {
        WorkPlaceFutFeeBySlice ws = 
            (WorkPlaceFutFeeBySlice) WorkSheetManager.createWorkSheet(
                    parent,
                    this.getClass(),
                    key);
        ws.setFbs((FeeBySlice) fbs.copy());
        ws.setApplyOn(getApplyOn());
        return ws;
    }

    //-------------- XML -------------//
    /**
     * XML
     */
    public WorkPlaceFutFeeBySlice() {
        //      TODO XXX remove when ficles are translated
//      if (Currency.INIT_AT_XML_CREATION) initializeData();     
    }
    
    

    /**
     * XML
     */
    public void setFbs(FeeBySlice fbs) {
        this.fbs = fbs;
    }
}
