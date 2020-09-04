/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: TarifLocalization.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.tarif;

import com.simpledata.bc.components.bcoption.OptionMoneyAmountUnder;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.workplace.NullWorkSheet;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.money.Money;

/**
 * This is a dummy WorkSheet just used as a localisation mark<BR>
 * It does not perform any operation but servers to attach CommissionUnder or
 * Amount Under
 */
public class TarifLocalization 
	extends Tarif implements OptionMoneyAmountUnder.OMMUAcceptedTarif {
    
	/** the tarif type of this class **/
	public final static String TARIF_TYPE= "Localization";
    
	
	/**
	 *
	 */
	public TarifLocalization (
		Tarification tarification,
		String title) {
		super(tarification, title, TARIF_TYPE);
		

		// attach the default worksheet to this tarif
		WorkSheetManager.createWorkSheet(this,NullWorkSheet.class,"");
	}
	
    /**
     * @see com.simpledata.bc.datamodel.Tarif#isSpecialized()
     */
    public boolean isSpecialized() {
        return false;
    }

    /**
     * @see WorkSheetContainer#getAcceptedNewWorkSheets(java.lang.String)
     */
    public Class[] getAcceptedNewWorkSheets(String key) {
        return new Class[] {NullWorkSheet.class};
    }

    public static final Money value = new Money(0d);
    /**
     * <B>Interface OptionMoneyAmountUnder.AcceptedTarif</B>
     * Return a null money value
     * @see OptionMoneyAmountUnder.OMMUAcceptedTarif#getSumOfAmount()
     */
    public Money getSumOfAmount() {
        return value;
    }
    
    //-------------------------- xml --------------------------//
    /** XML **/
    public TarifLocalization() {}
}
