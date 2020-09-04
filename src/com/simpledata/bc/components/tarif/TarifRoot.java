/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarifRoot.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 * $Log: TarifRoot.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:43  perki
 * First commit on sourceforge
 *
 * Revision 1.10  2004/11/15 18:41:24  perki
 * Introduction to inserts
 *
 * Revision 1.9  2004/09/09 12:43:07  perki
 * Cleaning
 *
 * Revision 1.8  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.7  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.6  2004/03/17 14:28:53  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/02/22 10:43:56  perki
 * File loading and saving
 *
 * Revision 1.4  2004/02/17 13:36:24  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.3  2004/02/06 10:04:22  perki
 * Lots of cleaning
 *
 * Revision 1.2  2004/02/02 07:00:50  perki
 * sevral code cleaning
 *
 * Revision 1.1  2004/02/01 18:28:54  perki
 * dimmanche soir
 *
 */
package com.simpledata.bc.components.tarif;

import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;

/**
 * Root Tarif that does nothing else that handeling options
 */
public class TarifRoot extends Tarif {
	/** the tarif type of this class **/
	public final static String TARIF_TYPE = "root";
	
	/**
	 */
	public TarifRoot(
		Tarification tarification,
		String title) {
		super(tarification, title, TARIF_TYPE);
		WorkSheetManager.createWorkSheet(this,WorkPlaceWithOnlyOptions.class,"");
	}
	
	/* (non-Javadoc) 
	 * @see com.simpledata.bc.datamodel.Dispatcher#getAcceptedNewWorkSheets(java.lang.String)
	 */
	public Class[] getAcceptedNewWorkSheets(String key) {
		// if no workSheet has been set
		if (super.getWorkSheet() == null) {
			// accepts only AssetsRoot0
			return new Class[] {WorkPlaceWithOnlyOptions.class};
		}
		return new Class[0];
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.Tarif#isSpecialized()
	 */
	public boolean isSpecialized() {
		return false;
	}
	

	
	//	------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public TarifRoot() {}
}
