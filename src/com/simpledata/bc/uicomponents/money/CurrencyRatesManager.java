/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 26 mai 2004
 * $Id: CurrencyRatesManager.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.uicomponents.money;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.EmptyBorder;

import com.simpledata.bc.*;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.uicomponents.tools.CurrencyChooserCombo;
import com.simpledata.bc.uitools.*;

import org.apache.log4j.Logger;

/**
 * This class is a small interface to edit rates between
 * defined currencies
 */
public class CurrencyRatesManager extends JInternalFrame {

    private static final Logger m_log = 
        Logger.getLogger(CurrencyRatesManager.class);
 
	private boolean isBuilding;
	
	private CurrencyPanel[] currencyPanels;
	
    private JLabel blankLabel;
    private JLabel comboLabel;
    private JPanel controlPanel;
    //private JScrollPane currenciesScroll;
    private JListWithPanels currenciesPanel;
    private CurrencyChooserCombo currencyChooserCombo;
    private JLabel explainLabel;
    private SButton finishButton;
    private JPanel referencePanel;
    private JLabel titleLabel;
	
    public CurrencyRatesManager() {
    		super("Currencies Manager", true, true, true, true);
    		
    		this.setPreferredSize(
    		        ((Rectangle)BC.getParameter(
    		                Params.KEY_CURRENCY_MANAGER_BOUNDS,
    		                Rectangle.class)).getSize()
    		);
    		
    		this.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    		
    		this.setFrameIcon(Resources.iconCurrencyManager);
    		
    		// informing the combo that it should not do anything
    		this.isBuilding = true;
    	
    		initComponents();
    		
    		Currency[] curs = Currency.getCurrencies();
    		currencyPanels = new CurrencyPanel[curs.length];
    		
    		for (int i=0; i<curs.length; i++) {
    			currencyPanels[i] = new CurrencyPanel(curs[i]);
    			this.currenciesPanel.addPanel(currencyPanels[i]);
    		}

    		// Now we want the combo to listen to events
    		this.isBuilding = false;

    		currencyChooserCombo.setSelectedCurrency(
    		        Currency.defaultCurrencyRef());

    		pack();
    		

    		
    		refresh();
    		
    		
    }
    
    private void finishButtonActionPerformed(ActionEvent evt) {
        this.doDefaultCloseAction();
    }

   
	
	/**
	 * Recalculate all rates
	 */
	private void refresh() {
	    if (this.isBuilding) return;
		// Refresh all CurrencyPanels
		for (int i=0;i<currencyPanels.length;i++) {
			currencyPanels[i].refresh();
		}
		
	}
	
	
	private Currency currencyChooserComboValue;
	private CurrencyChooserCombo currencyChooserCombo() {
	    if (currencyChooserCombo != null) return currencyChooserCombo;
	    final CurrencyRatesManager tthis = this;
	    currencyChooserComboValue = Currency.getDefaultCurrency();
	    currencyChooserCombo = 
	        new CurrencyChooserCombo(currencyChooserComboValue) {
	        
                protected void valueChanged() {
                		// We now statically set the correct reference currency    		
                		BC.getCurrencyManager().setDefaultCurrency(
                		        getSelectedCurrency());
                		tthis.refresh();
       
                }};
	    
	    return currencyChooserCombo;
	}
	

	private void initComponents() {
        GridBagConstraints gridBagConstraints;

        referencePanel = new JPanel();
        titleLabel = new JLabel();
        explainLabel = new JLabel();
        comboLabel = new JLabel();
        
        blankLabel = new JLabel();
        //currenciesScroll = new JScrollPane();
        currenciesPanel = new JListWithPanels();
        controlPanel = new JPanel();
        finishButton = new SButton();

        setName("Currency Manager");
        
        referencePanel.setLayout(new GridBagLayout());
        referencePanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        
        titleLabel.setText("Title");
        BC.langManager.register(titleLabel,"Rates management");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        referencePanel.add(titleLabel, gridBagConstraints);

        explainLabel.setText("Explanation");
        BC.langManager.register(explainLabel,"CurrencyRatesManager explanations");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 4, 4, 4);
        gridBagConstraints.weightx = 1.0;
        referencePanel.add(explainLabel, gridBagConstraints);

        comboLabel.setText("Select reference currency");
        BC.langManager.register(comboLabel,"Select reference currency");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 4, 4, 4);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        referencePanel.add(comboLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(0, 4, 4, 4);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        
       
        
        referencePanel.add(currencyChooserCombo(), gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        referencePanel.add(blankLabel, gridBagConstraints);

        getContentPane().add(referencePanel, BorderLayout.NORTH);

        currenciesPanel.setBorder(new EtchedBorder());
        getContentPane().add(currenciesPanel, BorderLayout.CENTER);

        controlPanel.setLayout(new BorderLayout());

        finishButton.setText("finish");
        finishButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                finishButtonActionPerformed(evt);
            }
        });
        controlPanel.add(finishButton, BorderLayout.EAST);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);

    }

}

class CurrencyPanel extends JPanel {
	
	private static final Logger m_log = Logger.getLogger( CurrencyPanel.class );
	
	private Currency cur;
	
	private JLabel title;
	private JLabel blank;
	private CurrencyManagerValueField value;
	
	public CurrencyPanel(Currency c) {
		super();
		this.cur = c;
		if (this.cur != null) {
			initComponents();
			refresh();
		} else {
			m_log.warn(
			     "Tried to instanciate a CurrencyPanel from a null Currency" );
		}
	}
	
	private void initComponents() {
	    setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		setLayout(new BorderLayout());
		
		title = new JLabel(this.cur.toString());
		
		blank = new JLabel("");
		
		
		this.add(title, BorderLayout.WEST);
		this.add(blank, BorderLayout.CENTER);
		
		//double d = this.cur.getValue();
		
		value = new CurrencyManagerValueField(this.cur);
		
		this.add(value, BorderLayout.EAST);
	}
	
	public void refresh() {
		if (this.cur != null) {
			this.value.refresh();
		}
	}
	
}

class CurrencyManagerValueField extends SNumField {
    private static final Logger m_log = 
        Logger.getLogger( CurrencyManagerValueField.class );
	private Currency cur;
	
	public CurrencyManagerValueField(Currency cur) {
	    setDigitAfterComa(5);
		this.cur = cur;
		Dimension d = new Dimension(170, 20);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		refresh();
	}
	
	public void stopEditing() {
	    Double dd = this.getDouble();
	    if (dd != null) {
	        double d = dd.doubleValue();
	        if (d > 0) {
	            // Must be a strictly positive double
	            BC.getCurrencyManager().setValue(
	                    Currency.defaultCurrencyRef(), this.cur, d);
	        } }
	   refresh();
	}

	public void startEditing() {
		// Nothing to do
	}
	
	public void refresh() {
		if (cur.xequals(Currency.defaultCurrencyRef())) {
			setEditable(false);
		} else {
			setEditable(true);
		}
		setDouble(cur.getValue());
	}
	
}
/*
 * $Log: CurrencyRatesManager.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.16  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.15  2004/10/07 08:21:08  perki
 * Keyring ok
 *
 * Revision 1.14  2004/10/05 08:05:46  carlito
 * *** empty log message ***
 *
 * Revision 1.13  2004/09/24 17:57:42  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.11  2004/09/22 09:17:36  perki
 * *** empty log message ***
 *
 * Revision 1.10  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.9  2004/09/04 18:12:32  kaspar
 * ! Log.out -> log4j
 *   Only the proper logger init is missing now.
 *
 * Revision 1.8  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.6  2004/07/06 17:31:25  carlito
 * Desktop manager enhanced
SButton with border on macs
desktop size persistent
 *
 * Revision 1.5  2004/05/31 17:12:47  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/31 16:14:45  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/05/28 11:11:08  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/27 15:01:52  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 */