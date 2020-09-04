/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: MoneyEditor.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.*;

import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.uitools.SNumField;

/**
 * A simple Panel that contains a MoneyValueInput and a CurrencyChooser
 */
public abstract class MoneyEditor extends JPanel {
	
	public static int DEF_W = 0;
	public static int DEF_H = 20;
	
	static {
		DEF_W = MoneyValueInput.DEF_W + CurrencyChooserCombo.DEF_W;
	}

	private MoneyValueInput mvi;
	private CurrencyChooserCombo ccc;

	/**
	 * set the dimensions of this component
	 * id one of the value is <= 0 then the default value is taken
	 */
	public void setDim(int height,int widthMoney,int widthCurrency) {
		Dimension d1 = 
			new Dimension(widthMoney <= 0 ? MoneyValueInput.DEF_W : widthMoney
					,height <= 0 ? DEF_H : height);
		Dimension d2 = 
			new Dimension(
					widthCurrency <= 0 ? 
							CurrencyChooserCombo.DEF_W : widthCurrency
					,height <= 0 ? DEF_H :height);
		_setDim(mvi,d1);
		_setDim(ccc,d2);
	}
	
	private void _setDim(JComponent c,Dimension d) {
		c.setSize(d);
		c.setPreferredSize(d);
		c.setMinimumSize(d);
	}
	
	public MoneyEditor(Money money) {
		this(money,null);
	}
	
	public MoneyEditor(Money money,String text) {
		super(new FlowLayout(FlowLayout.LEFT,0,0));
		
		mvi = new MoneyValueInput(money) {
			public void editionStopped() {
				stopEdit();
			}

			public void editionStarted() {
				startEdit();
			}
		};
		ccc = new CurrencyChooserCombo(money) {
			protected void valueChanged() {
				startEdit();
				stopEdit();
			}
		};
		setEditable(true);
		if (text != null)
			add(new JLabel(text));
		add(mvi);
		add(ccc);
		
	}
	
	boolean isEditable;
	public boolean isEditable() {
		return isEditable;
	}
	
	public void setEditable(boolean b) {
		if (isEditable == b) return;
		isEditable = b;
		if (mvi != null)
		mvi.setEditable(isEditable);
		if (ccc != null)
		ccc.setEnabled(isEditable);
	}
	
	/** reload data from model **/
	public void refresh() {
		mvi.refresh();
		ccc.refresh();
	}
	
	/** called when edition stopped **/
	public abstract void stopEdit();

	/** called when edition start **/
	public abstract void startEdit();
	/**
	 * @return
	 */
	public CurrencyChooserCombo getCcc() {
		return ccc;
	}

	/**
	 * @return
	 */
	public MoneyValueInput getMvi() {
		return mvi;
	}

	/**
	 * Tool that return a formated string of this money value
	 */
	public static String MoneyToSTring(Money m) {
		return SNumField.formatNumber(m.getValueDouble(),2,true)
		+" "+m.getCurrency();
	}
	
}


/**
 *  $Log: MoneyEditor.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.7  2004/09/24 17:31:24  perki
 *  New Currency is now handeled
 *
 *  Revision 1.6  2004/07/31 11:06:55  perki
 *  Still have problems with the progressbar
 *
 *  Revision 1.5  2004/07/26 17:39:37  perki
 *  Filler is now home
 *
 *  Revision 1.4  2004/05/31 07:19:47  perki
 *  Enable and disable
 *
 *  Revision 1.3  2004/05/20 17:05:30  perki
 *  One step ahead
 *
 *  Revision 1.2  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.1  2004/03/12 14:07:51  perki
 *  Vaseline machine
 *
 */