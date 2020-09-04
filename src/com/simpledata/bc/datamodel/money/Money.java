/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Money.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel.money;

import java.io.Serializable;

import com.simpledata.bc.datamodel.Copiable;

import org.apache.log4j.Logger;

/**
 * Has a money value.. handles conversion and such
 */
public class Money implements Serializable,Copiable, Comparable{
	private static final Logger m_log = Logger.getLogger( Money.class ); 
	private BCNumber xValue;
	private Currency xCurrency;
	
	/**
	 * Create a new Money value in the default Currency
	 */
	public Money( double d ) {
		this( d, null );
	}

	/** 
	 * Compares this Money to the given Money object. Return -1 iff the given
	 * Money object is less valuable (according the rate of the both currencies)
	 * than this. 0 if they have the same value and 1 iff the given object is 
	 * more valuable.
	 * @param o Object this is compared with.
	 * @return -1, 0, 1 according the value of o.
	 * @throws ClassCastException if o is not an instance of Money.
	 */
	public int compareTo(Object o) {
		Money mo = (Money) o;
		
		Double thisValue = new Double(this.getValueDefCurDouble());
		Double compared  = new Double(mo.getValueDefCurDouble());
		return thisValue.compareTo(compared);
	}
	
	/**
	 * Create a new Money value
	 */
	public Money(double d, Currency c) {
		xValue = new BCNumber( d );
		xCurrency = c == null ? Currency.getDefaultCurrency() : c ;
	}

	
	/**
	 * equals.. return true id doubleValue = doubleValue 
	 * and currency = currency
	 */
	public boolean xequals(Money m) {
		if (this == m) return true;
		if (xCurrency == null ) return false;
		if (! xCurrency.xequals(m.getCurrency())) return false;
		if (getValueDouble() != m.getValueDouble()) return false;
		return true;
	}
	
	/**
	 * Opertion<BR>
	 * Will make the folowing operation on this Money<BR>
	 * this + factor * m <BR>
	 * Example : m1 = m1 + m2  : m1.operation(m2,1)<BR>
	 * Example : m1 = m1 - m2  : m1.operation(m2,-1)<BR>
	 */
	public void operation(Money m,double factor) {
		check();
		if (m == null) {
			m_log.error( "Warning! Money:operation m == null" );
			return ;
		}
		xValue.set(xValue.get() + ( factor * m.getValueDouble(getCurrency())));
	}
	
	/**
	 * Factor<BR>
	 * Change the money Value by multiplicationg it's actual value by the
	 * factor passed
	 */
	public void operationFactor(double factor) {
		check();
		xValue.set(xValue.get() * factor);
	}

	/**
	 * Divides the current amount by the given value. 
	 * @param dividend Dividend. 
	 */
	public Money divide( double dividend ) {
		return new Money( 
			getValueDouble() / dividend, 
			getCurrency()
		); 
	}
	
	/**
	 * @return
	 */
	public Currency getCurrency() {
		return xCurrency;
	}

	/**
	 * @return the actually used value
	 */
	public BCNumber getValue() {
		check();
		return xValue;
	}
	
	/**
	 * @return
	 */
	public double getValueDouble() {
		check();
		return xValue.get();
	}
	
	/**
	 * get the value as a String
	 */
	public String getValueStr() {
		check();
		return xValue.toString();
	}

	/**
	 * get the value of this Money in the specified Currency
	 */
	public double getValueDouble(Currency c) {
		check();
		return c.getValue(xCurrency) * xValue.get();
	}
	
	/**
	 * get the value of this Money in the default Currency
	 */
	public double getValueDefCurDouble() {
		check();
		return getValueDouble(Currency.getDefaultCurrency());
	}
	
	/**
	 * get the value of this Money in the default Currency
	 */
	public String getValueDefCurString() {
		check();
		return BCNumber.toString(getValueDefCurDouble());
	}

	/**
	 *Set the currency without changing the value<BR>
	 *@see #changeCurrency(Currency currency)
	 */
	public void setCurrency(Currency currency) {
		this.xCurrency= currency;
	}
	
	/**
	 * change the currency.. value is converted it's value in the new currency
	 * @see #setCurrency(Currency currency)
	 */
	public void changeCurrency(Currency currency) {
		if (this.xCurrency.xequals(currency)) return;
		setValue(getValueDouble(currency));
		this.xCurrency = currency;
	}
	
	/**
	 * change the value of this MoneyAmount, by taking the one passed by
	 * the one passed, but It keep it's currency
	 */
	public void setValue(Money m) {
		check();	
		setValue(m.getValueDouble(getCurrency()));
	}
	
	
	/**
	 * @param d
	 */
	public void setValue(double d) {
		check();
		xValue.set(d);
	}
	
	/**
	 * to String
	 */
	public String toString() {
		check();
		return ""+xValue.toString()+" "+xCurrency.currencyCode();
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.Copiable#copy()
	 */
	public Copiable copy() {
		check();
		Money copy = new Money(xValue.get(),(Currency) xCurrency.copy());
		return copy;
	}
	
	void check() {
		if (xValue == null) m_log.error( "xValue == null" );
		if (getCurrency() == null) m_log.error( "Currency == null" );
	}
	
	// --- XML --//
	/** XML **/
	public Money() {
		
	}


	
	/**XML*/
	public Currency getXCurrency() {
		return xCurrency;
	}

	/**XML*/
	public BCNumber getXValue() {
		return xValue;
	}

	/**XML*/
	public void setXCurrency(Currency currency) {
		xCurrency= currency;
	}

	/**XML*/
	public void setXValue(BCNumber number) {
		xValue= number;
	}


	
}
/* $Log: Money.java,v $
/* Revision 1.2  2007/04/02 17:04:23  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:36  perki
/* First commit on sourceforge
/*
/* Revision 1.24  2004/11/08 15:32:05  perki
/* I went haunting the repartition bug #37
/*
/* Revision 1.23  2004/09/23 14:45:48  perki
/* bouhouhou
/*
/* Revision 1.22  2004/09/22 06:47:05  perki
/* A la recherche du bug de Currency
/*
/* Revision 1.21  2004/09/09 11:51:46  jvaucher
/* Money implements comparable
/*
/* Revision 1.20  2004/09/08 16:35:14  perki
/* New Calculus System
/*
/* Revision 1.19  2004/09/03 12:22:28  kaspar
/* ! Log.out -> log4j second part
/*
/* Revision 1.18  2004/08/27 10:02:09  kaspar
/* ! Refactor: Put DistributionMonitor in its own file
/*
/* Revision 1.17  2004/07/26 17:39:36  perki
/* Filler is now home
/*
/* Revision 1.16  2004/06/28 16:47:54  perki
/* icons for tarif in simu
/*
/* Revision 1.15  2004/05/12 13:38:06  perki
/* Log is clever
/*
/* Revision 1.14  2004/05/05 09:02:57  perki
/* cleaning
/*
/* Revision 1.13  2004/04/12 12:30:28  perki
/* Calculus
/*
/* Revision 1.12  2004/03/23 19:45:18  perki
/* New Calculus Model
/*
/* Revision 1.11  2004/03/17 10:54:45  perki
/* Thread for params
/*
/* Revision 1.10  2004/03/16 16:30:11  perki
/* *** empty log message ***
/*
/* Revision 1.9  2004/03/16 14:09:31  perki
/* Big Numbers are welcome aboard
/*
 * Revision 1.8  2004/03/04 11:12:23  perki
 * copiable
 *
 * Revision 1.7  2004/03/03 20:36:48  perki
 * bonne nuit les petits
 *
 * Revision 1.6  2004/03/03 14:42:11  perki
 * Un petit bateau
 *
 * Revision 1.5  2004/02/26 08:55:03  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/02/22 15:57:25  perki
 * Xstream sucks
 *
 * Revision 1.3  2004/02/22 10:43:57  perki
 * File loading and saving
 *
 * Revision 1.2  2004/02/19 21:32:16  perki
 * now 1Gig of ram
 *
 * Revision 1.1  2004/02/04 11:13:16  perki
 * oh money boney
 *
 */