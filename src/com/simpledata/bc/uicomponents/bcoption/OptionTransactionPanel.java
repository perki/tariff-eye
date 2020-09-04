/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionTransactionPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.simpledata.bc.components.bcoption.OptionTransaction;
import com.simpledata.bc.datamodel.money.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.tools.*;
import com.simpledata.bc.uitools.SNumField;

/**
 *UI for OptionTransaction
 *@see com.simpledata.bc.components.bcoption.OptionTransaction
 */
public class OptionTransactionPanel extends OptionDefaultPanel {

	/** the width of the numberField **/
	public static int NUMBERFIELD_W = 30;
	
	public OptionTransaction optionT;
	public TransactionValue transactionValue;

	private MoneyEditor moneyEditor;
	private SNumField numberField;
	private JLabel xLabel ;
	private JComboBox direction;
	
	/**
	 * @param option
	 */
	public OptionTransactionPanel(OptionDefaultPanel.EditStates editStateController,
	        OptionTransaction option) {
		super(editStateController,option);
		optionT = option;
		transactionValue = optionT.getTransactionValue();
		
		this.add(getStatus(), null); 
		//this.add(getTitleTextField(), null);
		this.add(getDirectionCombo(),null);
		//this.add(getEqualLabel(), null); 
		this.add(getNumberField(), null);  
		this.add(getXLabel(), null); 
		this.add(getMoneyEditor(), null);  
		
		refresh();
	}
	
	
	/**
	 * Save current Data
	 */
	public void save() {
		Direction d = (Direction) getDirectionCombo().getSelectedItem();
		if (transactionValue.inGoingToBank() != d.isOnGoingFromBank) {
			transactionValue.setInGoingToBank(d.isOnGoingFromBank);
			optionT.fireDataChange();
			refresh();
		}
		 // refresh with current values
	}
	
	/**
	 * Load current Data
	 */
	public void refresh() {
		numberField.setText(""+transactionValue.getAverageNumber());
		direction.setSelectedIndex(
				transactionValue.inGoingToBank() ? 0 : 1);
		getMoneyEditor().refresh();
		setStatus(true);
	}
	
	/**
	 * Get the money Editor (value + currency)
	 */
	private MoneyEditor getMoneyEditor() {
		if (moneyEditor == null) {
			Money moneyValue= transactionValue.getMoneyValue();
			moneyEditor=
				new MoneyEditor(moneyValue) {
				public void stopEdit() {
					setStatus(true);
					// need to call directly data change
					optionT.fireDataChange();
				}
				public void startEdit() {
					setStatus(false);
				}
			};
			setDim(
				moneyEditor,
				MoneyEditor.DEF_W,
				DEF_COMPONENT_H);
			setDim(moneyEditor.getCcc(),CurrencyChooserCombo.DEF_W,DEF_COMPONENT_H);
			setDim(moneyEditor.getMvi(),MoneyValueInput.DEF_W,DEF_COMPONENT_H);
		}
		return moneyEditor;
	}
	
	/**
	 * Get a direction combo
	 */
	private JComboBox getDirectionCombo() {
		if (direction != null) return direction;
		Direction[] d = new Direction[2];
		d[0] = new Direction(true);
		d[1] = new Direction(false);
		direction = new JComboBox(d);
		direction.setSelectedIndex(
				transactionValue.inGoingToBank() ? 0 : 1);
		
		direction.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				save();
			}});
		
		setDim(
				direction,
				150,
				DEF_COMPONENT_H);
		return direction;
	}
	
	// a simple class for appliesOnType
	class Direction {
		boolean isOnGoingFromBank;
		
		public Direction(boolean isOnGoingFromBank) {
			this.isOnGoingFromBank = isOnGoingFromBank;
		}
		
		public String toString() {
			if (isOnGoingFromBank) {
				return Lang.translate("going to bank");
			}
			return Lang.translate("coming from bank");
		}
		
	}
	
	
	/**
	 * This method initializes numberField
	 * 
	 * @return javax.swing.JTextField
	 */
	private SNumField getNumberField() {
		if(numberField == null) {
			numberField = new SNumField("0",true) {
				public void stopEditing() {
					Integer i = getInteger();
					if (i != null) {
						transactionValue.setAverageNumber(i.intValue());
					}
					// need to call directly data change
					optionT.fireDataChange();
				}

				public void startEditing() {
					setStatus(false);
				}};
			
			setDim(numberField,NUMBERFIELD_W,DEF_COMPONENT_H);  
		}
		return numberField;
	}
	
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private JLabel getXLabel() {
		if(xLabel == null) {
			xLabel = new JLabel("X"); 
			setDim(xLabel,DEF_ICON_W,DEF_COMPONENT_H);    
		}
		return xLabel;
	}
}

/**
 *  $Log: OptionTransactionPanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.13  2004/11/10 17:47:47  perki
 *  Closed bug #50 : TransactionValues did not save the direction of their transactions
 *
 *  Revision 1.12  2004/09/24 17:31:24  perki
 *  New Currency is now handeled
 *
 *  Revision 1.11  2004/08/04 06:03:12  perki
 *  OptionMoneyAmount now have a number of lines
 *
 *  Revision 1.10  2004/05/22 17:30:20  carlito
 *  *** empty log message ***
 *
 *  Revision 1.9  2004/05/14 11:48:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.8  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.7  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.6  2004/03/12 14:06:10  perki
 *  Vaseline machine
 *
 *  Revision 1.5  2004/02/19 21:32:16  perki
 *  now 1Gig of ram
 *
 *  Revision 1.4  2004/02/06 08:05:41  perki
 *  lot of cleaning in UIs
 *
 *  Revision 1.3  2004/02/06 07:44:55  perki
 *  lot of cleaning in UIs
 *
 *  Revision 1.2  2004/02/05 15:11:39  perki
 *  Zigouuuuuuuuuuuuuu
 *
 *  Revision 1.1  2004/02/05 11:08:29  perki
 *  Transactions are welcome aboard
 *
 */