/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 8 sept. 2004
 */
package com.simpledata.bc.components.bcoption;

import java.util.ArrayList;

import com.simpledata.bc.components.bcoption.tools.LinkToTarifs;
import com.simpledata.bc.components.bcoption.tools.OptionsLinkedToTarifTK;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.money.Money;

/**
 * This option compute a sum of commission taken on different tarifs.
 * It can be used with the WorkPlace RateOnAmount
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: OptionCommissionAmountUnder.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
public class OptionCommissionAmountUnder 
	extends AbstractOptionMoneyAmount 
	implements LinkToTarifs{
	
	
	// reports.
	
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE = "Amount Under Commission";
	
	/**
	 * Constructor of the option.
	 * @param workSheet Parent worksheet
	 * @param title     Title of this WorkPlace
	 */
	public OptionCommissionAmountUnder(WorkSheet workSheet,String title) {
		super(workSheet,title);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.BCOption#getStatusPrivate()
	 */
	protected int getStatusPrivate() {
		return BCOption.STATE_OK;
	}

	/** We accept all tarifs */
	public ArrayList getAcceptabletarifs() {
		Tarification t = getTarification();
		return t.getAllTarifs();
	}

	/**
	 * @return Is this option accepting this tarif ?
	 */
	public boolean isAcceptingTarif(Tarif t) {
		return (getAcceptedTarifs().contains(t));
	}

	/**
	 * @return The list of tarifs, the option can yet accept. I.e all of 
	 * cceptable tarif, without all the already linked tarifs.
	 */
	public ArrayList getAcceptedTarifs() {
		return OptionsLinkedToTarifTK.getAcceptedTarifs(this);
	}

	/**
	 * Register the given tarif, to be linked with this option.
	 * @param t The tarif to link.
	 * @return true if the operation succeed.
	 */
	public boolean addLinkToTarif(Tarif t) {
		return OptionsLinkedToTarifTK.addLinkToTarif(t, this);
	}

	/**
	 * Remove the given tarif (and all its sons) to the list of linked tarif.
	 * @param t The tarif to remove.
	 */
	public void removeLinkToTarif(Tarif t) {
		OptionsLinkedToTarifTK.removeLinkToTarif(t, this);
	}

	/**
	 * @return The list of all linked tarifs (not the sons).
	 */
	public ArrayList getLinkedTarifs() {
		return OptionsLinkedToTarifTK.getLinkedTarifs(this, null);
	}

	/**
	 * @return all tarifs we have to use to compute the value of the
	 * option.
	 */
	public ArrayList getUsedTarifs() {
		return OptionsLinkedToTarifTK.getTarifsUnderLinked(this);
	}

	/**
	 * @return 1. Irrevelent method.
	 */
	public int numberOfLines(ComCalculus cc) {
		return 1;
	}

	/**
	 * Compute the sum of the commission in all linked tarifs. I.e the
	 * value of the option
	 * @return Money object representing the resulting sum.
	 */
	public Money moneyValue(ComCalculus cc) {
		Money result = new Money(0.0);
		ArrayList tarifs = OptionsLinkedToTarifTK.getTarifsUnderLinked(this);
		for (int i=0; i< tarifs.size(); i++) {
			Tarif t = (Tarif)tarifs.get(i);
			result.operation(cc.getCom(t),1);
		}
		return result;
	}
	
	
	// XML - don't use
	
	/**
	 * XML - don't use
	 */
	public OptionCommissionAmountUnder() {
		// easy constructor
	}
	
	
}
