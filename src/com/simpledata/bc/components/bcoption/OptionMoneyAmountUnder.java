/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionMoneyAmountUnder.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import java.util.ArrayList;
import java.util.Iterator;

import com.simpledata.bc.components.bcoption.tools.LinkToTarifs;
import com.simpledata.bc.components.bcoption.tools.OptionsLinkedToTarifTK;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.money.Money;

/**
 * Is an option that gets an amount under an Asset Tarif Type
 * It behave likes an OptionMoneyAmount
 */
public class OptionMoneyAmountUnder 
	extends AbstractOptionMoneyAmount implements LinkToTarifs {
	
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE = "Sum of amounts within context";
	
	
	/**
	* Constructor.. 
	*/
	public OptionMoneyAmountUnder (WorkSheet workSheet,String title) {
		super(workSheet,title);
	}
	
	/**
	 * Option Money Amount Under always return 1 
	 */
	public int numberOfLines(ComCalculus cc) {
		return 1;
	}
	
	/**
	 * @see com.simpledata.bc.components.bcoption.
	 * AbstractOptionMoneyAmount#moneyValue()
	 */
	public Money moneyValue(ComCalculus cc) {
		Money result = new Money(0d);
		Iterator e = getSummedTarifs().iterator();
		while (e.hasNext()) {
			result.operation(((OMMUAcceptedTarif)e.next()).getSumOfAmount(),1);
		}
		return result;
	}
	
	/**
	 * get the Tarifs linked to this Option
	 * @return a vector of TarifAssets
	 */
	public ArrayList getLinkedTarifs() {
		return OptionsLinkedToTarifTK.getLinkedTarifs(
		        this,OMMUAcceptedTarif.class);
	}
	
	
	
	
	public ArrayList getUsedTarifs() {
		return getSummedTarifs();
	}
	
	/**
	 * get the list of Tarifs that will be summed.
	 * @return a ArrayList containing the Linked tarifs and their subTarifs
	 */
	private ArrayList/*<Tarif>*/ getSummedTarifs() {
		return OptionsLinkedToTarifTK.getTarifsUnderLinked(this);
	}
	
	
	
	/**
	 * return true if this Option accepts to be mapped to this Tarif
	 */
	public boolean isAcceptingTarif(Tarif t) {
		return (getAcceptedTarifs().contains(t));
	}
	
	/**
	 * return the list of Tarifs that in a way could be accepted.
	 * This should return the full range of Tarifs to show. 
	 */
	public ArrayList getAcceptabletarifs() {
		return getTarification().getTarifsListOfClass(
				new Class[] {OMMUAcceptedTarif.class});
	}
	
	/* (non-Javadoc)
	 * @see com.simpledata.bc.components.bcoption.tools.LinkToTarifs#getAcceptedTarifs()
	 */
	public ArrayList getAcceptedTarifs() {
		return OptionsLinkedToTarifTK.getAcceptedTarifs(this);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.components.bcoption.tools.LinkToTarifs#addLinkToTarif(com.simpledata.bc.datamodel.Tarif)
	 */
	public boolean addLinkToTarif(Tarif t) {
		return OptionsLinkedToTarifTK.addLinkToTarif(t, this);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.components.bcoption.tools.LinkToTarifs#removeLinkToTarif(com.simpledata.bc.datamodel.Tarif)
	 */
	public void removeLinkToTarif(Tarif t) {
		OptionsLinkedToTarifTK.removeLinkToTarif(t, this);
	}

	
	protected int getStatusPrivate() { return STATE_OK; }
	
	
	/** 
	 * all tarifs that may be used by OptionMoneyAmountUnder must implements
	 * this interface<BR>
	 * (also used by OptionMoneyAmountSum)
	 */
	public interface OMMUAcceptedTarif {
	    /**
		 * get the Amount of money contained in this node
		 */
		public Money getSumOfAmount();
	}
	
	//------------------- XML ------------------//
	/** XML CONSTRUCTOR **/
	public OptionMoneyAmountUnder() {}

	
}


/**
 *  $Log: OptionMoneyAmountUnder.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.18  2004/10/12 08:12:35  perki
 *  *** empty log message ***
 *
 *  Revision 1.17  2004/10/12 08:07:51  perki
 *  Sum Of Amount 2
 *
 *  Revision 1.16  2004/09/29 12:40:19  perki
 *  Localization tarifs
 *
 *  Revision 1.15  2004/09/09 13:45:18  jvaucher
 *  Corrected method signatures for com calculus complience
 *
 *  Revision 1.14  2004/09/09 13:41:33  perki
 *  Added context to MoneyAmount
 *
 *  Revision 1.13  2004/08/04 06:03:11  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.12  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.11  2004/07/07 13:37:10  carlito
 *  imports organized
 *
 *  Revision 1.10  2004/07/07 05:55:13  perki
 *  No more loops in Amount Under links
 *
 *  Revision 1.9  2004/06/28 10:38:47  perki
 *  Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 *  Revision 1.8  2004/05/20 09:39:43  perki
 *  *** empty log message ***
 *
 *  Revision 1.7  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.6  2004/03/18 18:08:59  perki
 *  barbapapa
 *
 *  Revision 1.5  2004/03/18 15:43:32  perki
 *  new option model
 *
 *  Revision 1.4  2004/03/04 18:44:23  perki
 *  *** empty log message ***
 *
 *  Revision 1.3  2004/03/03 10:17:23  perki
 *  Un petit bateau
 *
 *  Revision 1.2  2004/03/02 14:42:47  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */