/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarifFutures.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.tarif;

import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.FuturesRoot0;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFutFeeBySlice;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;

/**
 * Tarif for futures
 */
public class TarifFutures extends Tarif {
    
    /** the tarif type of this class **/
	public final static String TARIF_TYPE= "futures";
    
	
	/**
	 */
	public TarifFutures(
		Tarification tarification,
		String title) {
		super(tarification, title, TARIF_TYPE);
		
		//		 attach the default worksheet to this tarif
		WorkSheetManager.createWorkSheet(this, FuturesRoot0.class, "");
	}
	
	
    /**
     * @see com.simpledata.bc.datamodel.Tarif#isSpecialized()
     */
    public boolean isSpecialized() {
        return true;
    }

    /**
     * @see WorkSheetContainer#getAcceptedNewWorkSheets(String)
     */
    public Class[] getAcceptedNewWorkSheets(String key) {
        //      if no workSheet has been set
		if (super.getWorkSheet() == null) {
			// accepts only AssetsRoot0
			return new Class[] { FuturesRoot0.class };
		}

		// return default WorkSheets plus WorkPlaceTrRateBySlice.class
		return WorkSheetManager.sumClassArray(
			WorkSheetManager.defaultsWorksheets(),
			new Class[] { WorkPlaceFutFeeBySlice.class });
    }
    
	//	------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public TarifFutures() {}

}
