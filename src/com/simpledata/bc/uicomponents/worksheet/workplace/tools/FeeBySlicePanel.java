/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 13 sept. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.simpledata.bc.uicomponents.worksheet.workplace.tools;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.simpledata.bc.components.worksheet.workplace.tools.FeeBySlice;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.tools.OrderedMapOfDoublesObject;
import com.simpledata.bc.uicomponents.tools.CurrencyChooserCombo;
import com.simpledata.bc.uicomponents.tools.MoneyValueInput;

/**
 * @author SD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FeeBySlicePanel extends AbstractBySlicePanel {
     
    private JLabel currencyLabel;
	private CurrencyChooserCombo currencyChooser;
	private MoneyValueInput mvi;
    
    public final FeeBySlice fbs;
    
    /** columns **/
	int column[] = {
		FeeBySliceCell.TYPE_KEY,
		FeeBySliceCell.TYPE_FIXED
	};
    
    /** return the current FeeBySlice **/
	public final FeeBySlice getFbs() {
	    return fbs;
	}
	
	/**
	 * this method initialze the minimum value
	 */
	private MoneyValueInput moneyValueInput() {
	    if (mvi == null) {
	        final FeeBySlicePanel tthis = this;
	        mvi = new MoneyValueInput(fbs.getMinimum()){

                public void editionStopped() {
                    tthis.save();
                }

                public void editionStarted() {}};
	    }
	    return mvi;
	}
	
	/**
	 * This method initializes currencyLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel currencyLabel() {
		if (currencyLabel == null) {
			currencyLabel= new javax.swing.JLabel();
			currencyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			currencyLabel.setText(Lang.translate("Currency"));
			currencyLabel.setPreferredSize(new java.awt.Dimension(90, 20));
		}
		return currencyLabel;
	}
	
	/**
	 * This method initializes currencyChooser
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox currencyChooser() {
		if (currencyChooser == null) {
		    
		    final FeeBySlicePanel tthis = this;
		    
			currencyChooser=
				new CurrencyChooserCombo(fbs.getMinimum()) {

                            protected void valueChanged() {
                                tthis.save();
                            }};
		}
		return currencyChooser;
	}
	
	/** save the data **/
	public void save() {
	    //fbs.setCurrency(currencyChooser.getSelectedCurrency());
	    if (jTable != null) jTable.revalidate();
		rbspl.rbsDataChanged(); // advertise listener
	}
	
    /**
     * @param rbs
     * @param rbspl
     */
    public FeeBySlicePanel(FeeBySlice fbs, RBSPListener rbspl) {
        super(fbs, rbspl);
		this.fbs = fbs;
		initialize(); 
    }
    
    
    /** return the columns type **/
	public int[] getColumnsTypes() {
	    return column;
	}
	
	
	/** return the column name **/
	public String getTableColumnName(int c) {
	    switch (getColumnType(c)) {
	    	case FeeBySliceCell.TYPE_KEY:
	    	    return Lang.translate("From");
	    	case FeeBySliceCell.TYPE_FIXED:
			    return Lang.translate("Fee");
	    }
	    return "??";
	}
	
    
    /**
     * @see com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel#load()
     */
    public void load() {
        currencyChooser.setSelectedCurrency(
                fbs.getMinimum().getCurrency());

    }
    
    JPanel jPanel;
    /**
     * @see com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel#getJPanel()
     */
    protected JPanel getJPanel() {
        if (jPanel == null) {
			jPanel= new javax.swing.JPanel();
			java.awt.FlowLayout layFlowLayout1= new java.awt.FlowLayout();
			layFlowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			jPanel.setLayout(layFlowLayout1);
			jPanel.add(currencyLabel(), null);
			jPanel.add(currencyChooser(), null);
			jPanel.add(new JLabel(Lang.translate("Minimum Fee:")),null);
			jPanel.add(moneyValueInput(),null);
			jPanel.add(getPlusButton(), null);
			jPanel.add(getDeleteButton(), null);
			
		}
		return jPanel;
    }



    /**
     * @see com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel#createRBSC(com.simpledata.bc.tools.OrderedMapOfDoublesObject, int)
     */
    protected DataBySliceCell createRBSC(OrderedMapOfDoublesObject omodo,
            int type) {
        return FeeBySliceCell.create(this,omodo,type);
    }

 

    /**
     * @see com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel#_setEditable(boolean)
     */
    protected void _setEditable(boolean b) {
        currencyChooser().setEnabled(b);
    }

}
