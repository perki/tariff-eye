/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionMoneyAmount.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.money.Money;

import org.apache.log4j.Logger;

/**
 * An option that contains a Money value
 */
public class OptionMoneyAmount extends AbstractOptionMoneyAmount 
	implements Copiable.TransferableOption {
	private static final Logger m_log =
	    Logger.getLogger(OptionMoneyAmount.class );
		
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Money Amount";

	/** my value **/
	private Money xMoneyValue;
	
	/** the number of line of thisMoney Amount **/
	private int xNumOfLines;

	/**
	 */
	public OptionMoneyAmount(WorkSheet workSheet, String title) {
		super(workSheet, title);
	}

	/**
	 * This is the number of Lines contained in this money value
	 */
	public int numberOfLines(ComCalculus cc) {
		return (xNumOfLines < 1) ? 1 : xNumOfLines;
	}
	
	/**
	 * Set the number of lines 
	 */
	public void setNumberOfLines(int i) {

		i = (i < 1) ? 1 : i ;
	
		if (i != xNumOfLines) {
			xNumOfLines = i;
			fireDataChange();
		}
	}
	
	/**
	 * get the money value in this Option
	 */
	public Money moneyValue(ComCalculus cc) {
		if (xMoneyValue == null) xMoneyValue = new Money(0d);
		return xMoneyValue;
	}

	/**
	 * @see com.simpledata.bc.datamodel.BCOption#setValue(java.lang.String)
	 */
	public void setMoneyValue(Money value) {
		if (xMoneyValue == null || ! xMoneyValue.xequals(value)) {
			xMoneyValue = value;
			fireDataChange();
		}
	}
	
	protected int getStatusPrivate() { return STATE_OK; }
	
	
//	---------- IMPLEMENTS Copiable.TransferableOption --------------//
	
	/**
	 * <B>IMPLEMENTS Copiable.TransferableOption</B><BR>
	 * @see TransferableOption#canCopyValuesInto(Class destination)
	 */
	public boolean canCopyValuesInto(Class destination) {
		return (destination.equals(OptionMoneyAmount.class));
	}

	/**
	 * <B>IMPLEMENTS Copiable.TransferableOption</B><BR>
	 * @see com.simpledata.bc.datamodel.Copiable.
	 * TransferableOption#copyValuesInto(com.simpledata.bc.datamodel.BCOption)
	 */
	public void copyValuesInto(BCOption destination) {
		if (! canCopyValuesInto(destination.getClass())) {
			m_log.error( "cannot copy value of ["+destination.getClass()+
					"] into a ["+getClass()+"]" );
		}
		OptionMoneyAmount ot = (OptionMoneyAmount) destination;
		ot.setTitle(new String(getTitle()));
		ot.setDescription(new String(getDescription()));
		ot.setMoneyValue((Money) moneyValue(null).copy());
		ot.setNumberOfLines(numberOfLines(null));
	}
	
	
	//------------ XML -------//
	/** XML **/
	public OptionMoneyAmount() {}

	/** XML **/
	public Money getXMoneyValue() {
		return xMoneyValue;
	}

	/** XML **/
	public void setXMoneyValue(Money money) {
		xMoneyValue= money;
	}

	/** XML **/
	public int getXNumOfLines() {
		return numberOfLines(null);
	}
	
	/** XML **/
	public void setXNumOfLines(int numOfLines) {
		xNumOfLines = numOfLines;
	}
}

/* $Log: OptionMoneyAmount.java,v $
/* Revision 1.2  2007/04/02 17:04:24  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:38  perki
/* First commit on sourceforge
/*
/* Revision 1.22  2004/09/29 12:40:19  perki
/* Localization tarifs
/*
/* Revision 1.21  2004/09/22 06:47:04  perki
/* A la recherche du bug de Currency
/*
/* Revision 1.20  2004/09/10 14:48:50  perki
/* Welcome Futures......
/*
/* Revision 1.19  2004/09/09 13:41:33  perki
/* Added context to MoneyAmount
/*
/* Revision 1.18  2004/09/03 11:47:53  kaspar
/* ! Log.out -> log4j first half
/*
/* Revision 1.17  2004/08/04 06:03:11  perki
/* OptionMoneyAmount now have a number of lines
/*
/* Revision 1.16  2004/08/02 15:49:15  kaspar
/* + Added rate on amount with amount fixed, even though we should
/*   remove that.
/* ! Small change to dispatcher template, bold, underline.
/* + toString to OptionMoneyAmount
/*
/* Revision 1.15  2004/07/26 17:39:36  perki
/* Filler is now home
/*
/* Revision 1.14  2004/07/07 17:27:09  perki
/* *** empty log message ***
/*
/* Revision 1.13  2004/05/31 16:15:18  carlito
/* *** empty log message ***
/*
/* Revision 1.12  2004/05/21 13:19:49  perki
/* new states
/*
/* Revision 1.11  2004/05/20 09:39:43  perki
/* *** empty log message ***
/*
/* Revision 1.10  2004/05/12 13:38:06  perki
/* Log is clever
/*
/* Revision 1.9  2004/04/12 12:30:28  perki
/* Calculus
/*
/* Revision 1.8  2004/03/18 15:43:32  perki
/* new option model
/*
/* Revision 1.7  2004/02/26 08:55:03  perki
/* *** empty log message ***
/*
/* Revision 1.6  2004/02/22 10:43:56  perki
/* File loading and saving
/*
/* Revision 1.5  2004/02/19 23:57:25  perki
/* now 1Gig of ram
/*
 * Revision 1.4  2004/02/19 21:32:16  perki
 * now 1Gig of ram
 *
 * Revision 1.3  2004/02/19 19:47:34  perki
 * The dream is coming true
 *
 * Revision 1.2  2004/02/05 09:58:11  perki
 * Transactions are welcome aboard
 *
 * Revision 1.1  2004/02/04 11:12:46  perki
 * Moneys   .. oh money
 *
 */