/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 31 mai 2004
 * $Id: CurrencyManager.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.money;

/**
 * Interface for a currency manager
 * i.e. set and get default currency
 * define codes and conversion values for currencies
 * 
 * WARNING : INSTANCIATE RULE
 * You must attach the currencyManager before any Currency instanciation
 * example :
 * public MyCurrencyManager() {
 * 	Currency.setCurrencyManager(this);
 * 	[...] 
 * }
 */

public interface CurrencyManager {

	/**
	 * 
	 * @return
	 */
	//public String[] getSymbols();
	
	/**
	 * @return the Currency to work on
	 */
	public Currency defaultCurrency();
	
	/**
	 * set the default Currency
	 */
	public void setDefaultCurrency(Currency c);
	
	/**
	 * Add a currency with the folowing value.
	 * 1 currencyCode = value reference
	 */
	public void 
		addCurrency(String currencyCode,Currency reference,double value);
	
	
	/**
	 * set a currency value versus another<BR>
	 * for example setCurrency(USD,EUR,0.9);<BR>
	 * USD change will not change<BR>
	 * EUR rate will change to makes 1$ -> 0.9�
	 */
	public void setValue(Currency fixed,Currency toChange,double value);
		
	/**
	 * get the value of a currency from a reference currency<BR>
	 * @param reference the reference Currency
	 */
	public double getValue(Currency reference,Currency c);

	/**
	 * get the list of currencies
	 */
	public Currency[] getCurrencies();
	
	/**
	 * check if this a known currency code
	 * @param cCode a curency code
	 */
	public boolean currencyCodeExists (String cCode);
	
}


/*
 * $Log: CurrencyManager.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:36  perki
 * First commit on sourceforge
 *
 * Revision 1.6  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.5  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.4  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.3  2004/05/31 17:04:47  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/31 16:56:44  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/31 16:12:25  carlito
 * *** empty log message ***
 *
 */