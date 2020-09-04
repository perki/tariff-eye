/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: CurrencyChooserCombo.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;

/**
 * A JcomboBox just made for currency choosing
 * NOTE: does not refresh when event occures on the datamodel
 */
public abstract class CurrencyChooserCombo extends JComboBox {
	private static final Logger m_log = 
	    Logger.getLogger( CurrencyChooserCombo.class ); 
	
	/** My default WIDTH **/
	/** My default WIDTH **/
	public static int DEF_W = 60;
	public static int DEF_H = MoneyEditor.DEF_H;
	
	/** the money Value I do monitor **/
	private Money moneyValue ; 
	
	/** the currency Value I do monitor **/
	
	/**
	 * 
	 * @param currencyList
	 */
	public CurrencyChooserCombo (Currency currency) {
		this(new Money(0d,currency)); 
	}
	
	/**
	 * 
	 * @param currencyList
	 */
	public CurrencyChooserCombo (Money moneyValue) {
		super(BC.getCurrencyManager().getCurrencies());

		this.moneyValue = moneyValue;
		
		refresh();
		
		//setPreferredSize(new Dimension(DEF_W,OptionDefaultPanel.DEF_HEIGHT));
		_setDim(new Dimension(DEF_W, DEF_H));
		addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					save(); 
				}
		});
		
		addPopupMenuListener(new PopupMenuListener() {
            
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
            	valueChanged();
            }
            
            public void popupMenuCanceled(PopupMenuEvent evt) {
            }

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
			}
        });
		
		// listen for currency changes
		BC.getCurrencyManager().addWeakCurrencyChangeListener(
		        new ChangeListener(){

                    public void stateChanged(ChangeEvent e) {
                        String code = e.getSource().toString();
                        if (getCurrencyIndex(code) < 0) { // new currency
                            removeAllItems();
                            
                            Currency[] c= 
                                BC.getCurrencyManager().getCurrencies();
                            for (int i = 0; i < c.length; i++)
                                addItem(c[i]);
                            
                            refresh();
                            
                        }
                    }});
        
	}
	
	/**
	 * get the selected Currency
	 */
	public Currency getSelectedCurrency() {
		return moneyValue.getCurrency();
	}
	
	/**
	 * set the selected Currency
	 */
	public void setSelectedCurrency(Currency c) {
		if (c.xequals(moneyValue.getCurrency())) return;
		moneyValue.setCurrency(c);
		refresh();
	}
	
	/**
	 * get the currency index, return -1 if not found
	 */
	private int getCurrencyIndex(String currencyCode) {
	    // set the selected Currency (based on it's code)
	    for (int i = getItemCount(); i > 0 ; i--) {
	        if (currencyCode.equals(getItemAt(i-1).toString())) {
	            return i-1;
	        }
	    }
	    return -1;
	}
	
	/**
	 * reload the value from the MoneyValue or the Currency and
	 * displays it.
	 */
	public void refresh() {
	    String previous = ""+moneyValue.getCurrency();
	    
	    int i = getCurrencyIndex(moneyValue.getCurrency().currencyCode());
	    
	    if (i > 0)
	          setSelectedIndex(i);
	   

		m_log.debug("#####previous: "+previous+" -> "+getSelectedItem());
	}
	
	/**
	 * save the selected data to the object I monitor
	 */
	public void save() {
		Currency c = (Currency) getSelectedItem();
		if (! c.xequals(moneyValue.getCurrency())) {
			moneyValue.setCurrency(c);
			valueChanged();
		}
	}
	
	/**
	 * Sets all sizes to dimension d
	 * @param d
	 */
	private void _setDim(Dimension d) {
		this.setSize(d);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
	}
	
	/**
	 * called when a value changed
	 */
	protected abstract void valueChanged();
}



/**
 *  $Log: CurrencyChooserCombo.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.9  2004/09/27 08:48:50  jvaucher
 *  Fee report. Rendering improved
 *
 *  Revision 1.8  2004/09/24 17:31:24  perki
 *  New Currency is now handeled
 *
 *  Revision 1.7  2004/09/22 06:47:05  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.6  2004/08/02 10:17:43  carlito
 *  new fixed fee panel
 *
 *  Revision 1.5  2004/05/21 13:19:50  perki
 *  new states
 *
 *  Revision 1.4  2004/05/20 17:05:30  perki
 *  One step ahead
 *
 *  Revision 1.3  2004/05/10 19:00:51  perki
 *  Better amount option viewer
 *
 *  Revision 1.2  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.1  2004/03/12 14:07:51  perki
 *  Vaseline machine
 *
 */