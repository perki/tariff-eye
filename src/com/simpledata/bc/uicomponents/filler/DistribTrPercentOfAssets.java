/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: DistribTrPercentOfAssets.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.filler.DistribTrSizeOfLines.CalculationResult;
import com.simpledata.bc.uicomponents.tools.MoneyEditor;
import com.simpledata.bc.uitools.PerBaseTenEditor;
import com.simpledata.bc.uitools.SNumField;

/**
 * A distribution method based on a percentage of assets to determine
 * the size of a line of a transaction
 */
public class DistribTrPercentOfAssets implements DistributionMethod {
    private static final Logger m_log = 
	    Logger.getLogger( DistribTrPercentOfAssets.class ); 

    FillerNode owner;
    
    private double value;
    private int divider;
    
    /**
     * @param fn is the FillerNode that own this distribute Action.
     * It is used by some Distribution method to find parent's Distributions
     */
    public DistribTrPercentOfAssets(FillerNode fn) {
        assert fn != null : "Cannot work on a null FillerNode";
        owner = fn;
        

		CalculationResult counter = 
		    DistribTrSizeOfLines.calculateSizeOfLineFromData(owner);
		
		double amount = owner.getAmount().getValueDefCurDouble() * 
						owner.rolloutGetApplicable();
		
		double avg = counter.getSumOfAmounts().getValueDefCurDouble();
		
		if (counter.getNumberOfLines() <= 0 || 
		        amount <= 0 ||
		        avg <= 0) {
		    value = 5;
		    divider = 100;
		    m_log.warn("  "+value+" "+divider);
		    return;
		}
		
		avg = avg / counter.getNumberOfLines();
		
		value = avg * 100 / (amount);
		divider = 100;
		
		if (value < 1 ) {
		    value = value / 10;
		    divider = 1000;
		}
    }
    
    public boolean methodForward() { return false; }

    /**
     * @see com.simpledata.bc.uicomponents.filler.DistributionMethod#getOwner()
     */
    public FillerNode getOwner() { return owner; }

    /**
     * 
     */
    public void distribute(Money m,Dispatcher workSheet,DistributionMonitor dm){
       DistribTrSizeOfLines.
       	distributeLineOfSize(m,workSheet,dm,getSizeOfALine());
    }

    
    /**
     * @see DistributionMethod#getSummary()
     */
    public String getSummary() {
        String percent = SNumField.formatNumber(value*divider/100,1,false);
        return Lang.translate("Size of line is %0%% of total assets :",
                percent)+
                "["+owner.getTitle()+"]"
                
                +" ("+MoneyEditor.MoneyToSTring(getSizeOfALine())+")";
    }

    /**
     * @see DistributionMethod#getCost(NodeInfo)
     */
    public int getCost(NodeInfo start) {
        return DistribTrSizeOfLines.getCost(start,owner);
    }
    
    /**-------------------------- UI --------------------------**/
    
  
    
    private void redistrib() {
        refresh();
        owner.redistributeRepartition(TransactionsRoot0.class);
    }
    
	JPanel ui;
	JLabel infoLabel;
	/**
	 * @see com.simpledata.bc.uicomponents.filler.DistributionMethod#getUI()
	 */
	public JPanel getUI() {
		if (ui != null)  {
		    refresh();
		    return ui;
		}
		ui = new JPanel(new FlowLayout(FlowLayout.LEADING));
		PerBaseTenEditor tEditor = new PerBaseTenEditor() {

            protected void changeDivider(int div) {
                if (div == 0) div = 1;
                if (div == divider) return;
                divider = div;
                redistrib();
            }

            protected void changeDoubleValue(double v) {
               if (value == v ) return;
               value = v;
               redistrib();
            }

            protected void startEditing() { }
         };
            
        tEditor.setDivider(divider);
        tEditor.setDouble(value);
		
		ui.add(
				new JLabel(
				        Lang.translate("Size of line is a percentage of assets:"
				                )));
		ui.add(tEditor);
		
		infoLabel = new JLabel();
		ui.add(infoLabel);
	
		refresh();
		return ui;
	}
	
	/**
	 * Refresh the UI
	 */
	public void refreshUI() {
	    refresh();
	};
	
	private void refresh() {
	    if (infoLabel == null) return;
	    infoLabel.setText("("+MoneyEditor.MoneyToSTring(getSizeOfALine())+")");
	}

	
	/**
	 * public void refresh
	 */
	private Money getSizeOfALine() {
	    Money result = (Money) owner.getAmount().copy();
	    result.operationFactor(owner.rolloutGetApplicable()*value/divider);
	    return result;
	}
	
}
