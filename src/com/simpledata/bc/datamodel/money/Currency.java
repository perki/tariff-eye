/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Currency.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */

package com.simpledata.bc.datamodel.money;

import java.io.Serializable;

import com.simpledata.bc.datamodel.Copiable;

import org.apache.log4j.Logger;

/**
 * Handles the list of currencies and their values
 * 
 * WARNING : INSTANCIATE RULE
 * see CurrencyManager.java
 *
 * TODO Currencies could only be obtained from CurrencyManager, 
 * which would eliminate all checks for that manager here. This 
 * change cannot be made without changing load&save first, since 
 * XML loader depends on the class as it is now. 
 */
public class Currency implements Serializable,Copiable {
	
	transient private static final Logger m_log 
			= Logger.getLogger( Currency.class ); 
	
	transient private static CurrencyManager currencyManager;
	
	private String currencyCode = null;
	
	/**
	 * construct a new currency of this code.
	 * You can get currencies code with currenciesCodeArrayList
	 * @param currencyCode is the object that choose in wich currency to work on
	 * if null then the default currency will be used
	 */
	public Currency(String currencyCode) {
		if (currencyManager == null) {
			return;
		} 
		if (currencyCode == null) {
		    currencyCode = defaultCurrencyRef().currencyCode();
		} else if (!currencyManager.currencyCodeExists(currencyCode)) {
			return;
		}

		this.currencyCode = currencyCode+"";
	}
	

	
	/**
	 * Set the currency manager
	 */
	public static void setCurrencyManager(CurrencyManager cm) {
		currencyManager = cm;
	}
	
	
	/**
	 * get a new instance of default Currency
	 */
	public static Currency getDefaultCurrency() {
		Currency ref = defaultCurrencyRef();
		if (ref == null) return null;
		return defaultCurrencyRef().same();
	}
	
	/**
	 * get the default Currency reference.
	 * @see #getDefaultCurrency()
	 */
	public static Currency defaultCurrencyRef() {
		if (currencyManager == null) {
			m_log.error( "getDefaultCurrencyRef() called while " +
			             "currencyManager is not set");
			return null;
		}
		return currencyManager.defaultCurrency();
	}
	
	/**
	 * get the value of a currency from a reference currency<BR>
	 * @param reference the reference Currency
	 */
	public static double getValue(Currency reference,Currency c) {
		// get banana value	
		if (currencyManager == null) {
			m_log.error( "getValue(Currency reference,Currency c) called" +
			             " with no currencyManager set");
			return 0;
		}
		return currencyManager.getValue(reference, c);
	}
	
	/**
	 * get the list of currencies
	 */
	public static Currency[] getCurrencies() {
		if (currencyManager == null) {
			m_log.error( "getCurrencies() called with no currencyManager set" );
			return null;
		}
		return currencyManager.getCurrencies();
	}
	
	
	//-------------- NON STATIC METHODS ---------------//
	
	/**
	 * check if this a known currency code
	 * @param cCode a curency code
	 */
	public static boolean currencyCodeExists (String cCode) {
		if (currencyManager == null) {
			m_log.error( "currencyCodeExists() called " +
			             "while currencyManager is not set");
			return false;
		}
		return currencyManager.currencyCodeExists(cCode);
	}
	
	
	
	/**
	 * get the value of a currency from a reference currency<BR>
	 * @param reference the reference Currency
	 */
	public double getValue(Currency reference) {
		return Currency.getValue(reference, this);
	}
	
	/**
	 * get the value of a currency from the default Currency<BR>
	 * Ex: an amount X in USD can be converted in def 
	 * currency with X*USD.getValue();
	 */
	public double getValue() {
		return getValue(Currency.defaultCurrencyRef());
	}
	
	/**
	 * getCurrencyCode
	 */
	public String currencyCode() {
	    if (currencyCode == null) {
	        currencyCode = Currency.defaultCurrencyRef().currencyCode()+"";
	        m_log.warn("Init");
	    }
		return currencyCode;
	}
	

	/**
	 * toString
	 */
	public String toString() {
		return ""+currencyCode;
	}
	
	
	/**
	 * equals, based on the currency code
	 */
	public boolean xequals(Object o) {
		if (! (o instanceof Currency) || o == null) return false;
		return ((Currency)o).getCurrencyCode().equals(getCurrencyCode());
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.Copiable#copy()
	 * @see #same()
	 */
	public Copiable copy() {
		return same();
	}
	
	/**
	 * return a currency like this one ..
	 * equivalent to new Currency(currencyCode);
	 */
	public Currency same() {
		return new Currency(currencyCode);
	}
	
	//-------------- XML ------------------//

	
	/** XML  -- never use*/
	public Currency() { 
	    //if (INIT_AT_XML_CREATION) currencyCode();
	}

	/** XML  -- never use*/
	public void setCurrencyCode(String string) {
		currencyCode= string;
	}
	/** XML  -- never use*/
	public String getCurrencyCode() {
		return currencyCode;
	}

}


/*
 * $Log: Currency.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:36  perki
 * First commit on sourceforge
 *
 * Revision 1.30  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.29  2004/09/29 16:40:06  perki
 * Fixef Futures
 *
 * Revision 1.28  2004/09/29 12:40:19  perki
 * Localization tarifs
 *
 * Revision 1.27  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.26  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.25  2004/09/22 15:39:55  carlito
 * Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 * Revision 1.24  2004/09/22 09:44:54  perki
 * repaired Currency bug
 *
 * Revision 1.23  2004/09/22 09:17:36  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/09/22 08:23:40  perki
 * *** empty log message ***
 *
 * Revision 1.21  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.20  2004/09/03 12:22:28  kaspar
 * ! Log.out -> log4j second part
 *
 * Revision 1.19  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.18  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.17  2004/05/31 17:12:30  carlito
 * *** empty log message ***
 *
 * Revision 1.16  2004/05/31 17:04:47  carlito
 * *** empty log message ***
 *
 * Revision 1.15  2004/05/31 16:56:44  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/05/31 16:12:14  carlito
 * *** empty log message ***
 *
 * Revision 1.13  2004/05/31 12:40:22  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/05/27 15:01:52  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.9  2004/05/20 17:05:30  perki
 * One step ahead
 *
 * Revision 1.8  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.7  2004/03/04 11:12:23  perki
 * copiable
 *
 * Revision 1.6  2004/02/22 15:57:25  perki
 * Xstream sucks
 *
 * Revision 1.5  2004/02/22 10:43:57  perki
 * File loading and saving
 *
 * Revision 1.4  2004/02/19 21:32:16  perki
 * now 1Gig of ram
 *
 * Revision 1.3  2004/02/05 18:35:59  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/02/05 15:11:39  perki
 * Zigouuuuuuuuuuuuuu
 *
 * Revision 1.1  2004/02/04 11:13:16  perki
 * oh money boney
 *
 */