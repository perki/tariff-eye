/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: MoneyValueInputSlave.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;

/**
 * A MoneyValue Input Synchronized on BC default Currency
 */
public abstract class MoneyValueInputSlave 
	extends JPanel {
	MoneyValueInput mvi ;
	JLabel currency;
	Money money;
	
	ChangeListener cl ;
	
	public MoneyValueInputSlave(Money m) {
		super(new FlowLayout(FlowLayout.LEFT,0,0));
		money = m;
	
		mvi = new MoneyValueInput(money) {
			
			public void editionStopped() {
				stopEdit();
				
			}

			public void editionStarted() {
				startEdit();
				
			}};
		currency = new JLabel("XXXX");
		currency.setPreferredSize(new Dimension(30,MoneyValueInput.DEF_H));
		
		
		add(mvi);
		add(currency);
		
		cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				refresh();
			}};
		BC.getCurrencyManager().addWeakCurrencyChangeListener(cl);
		refresh();
	}
	
	public void refresh() {
		currency.setText(Currency.getDefaultCurrency().toString());
		money.changeCurrency(Currency.getDefaultCurrency());
		mvi.refresh();
	}
	
	public void setEditable(boolean b) {
		mvi.setEditable(b);
	}
	
	public abstract void stopEdit();

	public abstract void startEdit();
	
}

/*
 * $Log: MoneyValueInputSlave.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.1  2004/08/02 14:55:43  perki
 * *** empty log message ***
 *
 */