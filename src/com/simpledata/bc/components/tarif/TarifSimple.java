/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: TarifSimple.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.tarif;

import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;

/**
 * A simple Tarif that accepts everything as WorkSheet
 */
public class TarifSimple extends Tarif 
	implements Tarif.TarifTransferRoot {
	/** the tarif type of this class **/
	public final static String TARIF_TYPE = "simple";
	
	public TarifSimple(
		Tarification tarification,
		String title) {
		super(tarification, title, TARIF_TYPE);
		WorkSheetManager.createWorkSheet(this,EmptyWorkSheet.class,"");
	}
	
	/** 
	 * @see com.simpledata.bc.datamodel.Tarif#isSpecialized()
	 */
	public boolean isSpecialized() {
		return true;
	}

	/**
	 * @see com.simpledata.bc.datamodel.WorkSheetContainer#getAcceptedNewWorkSheets(java.lang.String)
	 */
	public Class[] getAcceptedNewWorkSheets(String key) {
		return WorkSheetManager.defaultsWorksheets();
	}
	
	
	//---------- XML ---------------//
	public TarifSimple() {}

}


/**
 *  $Log: TarifSimple.java,v $
 *  Revision 1.2  2007/04/02 17:04:27  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:43  perki
 *  First commit on sourceforge
 *
 *  Revision 1.7  2004/11/15 18:41:24  perki
 *  Introduction to inserts
 *
 *  Revision 1.6  2004/10/11 17:48:08  perki
 *  Bobby
 *
 *  Revision 1.5  2004/09/09 12:43:07  perki
 *  Cleaning
 *
 *  Revision 1.4  2004/09/08 19:28:55  perki
 *  Reaprtition now follows Transfer Options
 *
 *  Revision 1.3  2004/05/22 08:39:35  perki
 *  Lot of cleaning
 *
 *  Revision 1.2  2004/03/17 14:28:53  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/03/04 18:44:54  perki
 *  *** empty log message ***
 *
 */