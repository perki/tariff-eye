/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarifTransactions.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.tarif;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTrRateBySlice;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.pair.Pairable;

/**
 * Tarif for transactions
 */
public class TarifTransactions extends Tarif 
	implements Pairable, Tarif.TarifTransferRoot  {
	
	private static final Logger m_log = 
	    Logger.getLogger( TarifTransactions.class ); 

	/** the tarif type of this class **/
	public final static String TARIF_TYPE= "transactions";
	
	/**
	 */
	public TarifTransactions(
		Tarification tarification,
		String title) {
		super(tarification, title, TARIF_TYPE);
		
		//		 attach the default worksheet to this tarif
		WorkSheetManager.createWorkSheet(this, TransactionsRoot0.class, "");
	}
	
	/**
	 * @return Returns the rootWorkSheet.
	 */
	public TransactionsRoot0 getRootWorkSheet() {
		WorkSheet ws = getWorkSheet();
		if (ws == null || ! (ws instanceof TransactionsRoot0)) {
			m_log.error( "How come my worksheet is of type:"+ws );
			return null;
		}
		return (TransactionsRoot0) ws;
	}
	

	/**
	 * @see com.simpledata.bc.datamodel.Dispatcher#getAcceptedNewWorkSheets(java.lang.String)
	 */
	public Class[] getAcceptedNewWorkSheets(String key) {
		// if no workSheet has been set
		if (super.getWorkSheet() == null) {
			// accepts only AssetsRoot0
			return new Class[] { TransactionsRoot0.class };
		}

		// return default WorkSheets plus WorkPlaceTrRateBySlice.class
		return WorkSheetManager.sumClassArray(
			WorkSheetManager.defaultsWorksheets(),
			new Class[] { WorkPlaceTrRateBySlice.class });
	}

	/**
	 * @see com.simpledata.bc.datamodel.Tarif#isSpecialized()
	 */
	public boolean isSpecialized() {
		return true;
	}
	
	//	--------------------- Pairing 
	/**
	 * @see com.simpledata.bc.datamodel.pair.Pairable#pairedCanBe()
	 */
	public int pairedCanBe() {
		return  ((PairManagerAssetsTransactions) 
				getTarification().getPairManager()).canBePaired(this);
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.pair.Pairable#pairedGet()
	 */
	public Tarif pairedGet() {
		return ((PairManagerAssetsTransactions) 
				getTarification().getPairManager()).get(this);
	}

	/**
	 * @see com.simpledata.bc.datamodel.pair.Pairable#pairedCreate()
	 */
	public void pairedCreate() {
		((PairManagerAssetsTransactions) 
				getTarification().getPairManager()).create(this);
	}

	/**
	 * @see com.simpledata.bc.datamodel.pair.Pairable#pairedGetProposition()
	 */
	public Tarif pairedGetProposition() {
		return ((PairManagerAssetsTransactions) 
				getTarification().getPairManager()).getProposition(this);
	}

	/**
	 * @see com.simpledata.bc.datamodel.pair.Pairable#pairedBreak()
	 */
	public void pairedBreak() {
		((PairManagerAssetsTransactions) 
		getTarification().getPairManager()).breakP(this);
	}

	//	------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public TarifTransactions() {}
	
	
	

	
}
/* 
 * $Log: TarifTransactions.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:43  perki
 * First commit on sourceforge
 *
 * Revision 1.21  2004/10/11 17:48:08  perki
 * Bobby
 *
 * Revision 1.20  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.19  2004/09/09 12:43:07  perki
 * Cleaning
 *
 * Revision 1.18  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.17  2004/09/03 11:47:53  kaspar
 * ! Log.out -> log4j first half
 *
 * Revision 1.16  2004/08/01 18:00:59  perki
 * *** empty log message ***
 *
 * Revision 1.15  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.14  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 * Revision 1.13  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.12  2004/03/17 14:28:53  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/03/02 14:42:48  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.10  2004/02/22 10:43:56  perki
 * File loading and saving
 *
 * Revision 1.9  2004/02/19 23:57:25  perki
 * now 1Gig of ram
 *
 * Revision 1.8  2004/02/19 19:47:34  perki
 * The dream is coming true
 *
 * Revision 1.7  2004/02/18 16:59:29  perki
 * turlututu
 *
 * Revision 1.6  2004/02/18 13:37:51  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/02/17 13:36:24  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.4  2004/02/17 11:39:21  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.3  2004/02/06 10:04:22  perki
 * Lots of cleaning
 *
 * Revision 1.2  2004/02/05 11:07:28  perki
 * Transactions are welcome aboard
 *
 * Revision 1.1  2004/02/05 07:46:54  perki
 * Transactions are welcome aboard
 *
 */
