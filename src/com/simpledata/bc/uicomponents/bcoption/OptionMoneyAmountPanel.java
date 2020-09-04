/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionMoneyAmountPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $: OptionMoneyAmountPanel.java,v 1.3 2004/02/04 17:38:04 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import javax.swing.JLabel;

import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.uicomponents.tools.*;
import com.simpledata.bc.uitools.SNumField;

/**
 * UI for OptionMoneyAmount
 * @see com.simpledata.bc.components.bcoption.OptionMoneyAmount
 */
public class OptionMoneyAmountPanel extends OptionDefaultPanel {

	/** the width of the numberField **/
	public static int NUMBERFIELD_W = 30;
	
	
	public OptionMoneyAmount optionMA;

	private MoneyEditor moneyEditor= null;

	private SNumField numberField;
	
	private JLabel xLabel ;
	
	/**
	 * @param option
	 */
	public OptionMoneyAmountPanel(OptionDefaultPanel.EditStates editStateController,OptionMoneyAmount option) {
		super(editStateController,option);
		optionMA= option;

		this.add(getStatus(), null);
		this.add(getTitleTextField(), null);
		this.add(getEqualLabel(), null);
		this.add(getNumberField(), null);  
		this.add(getXLabel(), null); 
		this.add(getMoneyEditor(), null);

		refresh();
	}

	/**
	 * This method initializes valueField
	 * 
	 * @return JTextField
	 */
	private MoneyEditor getMoneyEditor() {
		if (moneyEditor == null) {
			Money moneyValue= optionMA.moneyValue(null);
			moneyEditor=
				new MoneyEditor(moneyValue) {
				public void stopEdit() {
					// need to fire the changes to the option
					optionMA.fireDataChange();
					setStatus(true);
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
	 * This method initializes numberField
	 * 
	 * @return javax.swing.JTextField
	 */
	private SNumField getNumberField() {
		if(numberField == null) {
			numberField = new SNumField("1",true) {
				public void stopEditing() {
					Integer i = getInteger();
					if (i != null) {
						optionMA.setNumberOfLines(i.intValue());
					}
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
	
	/**
	 * Load current Data
	 */
	public void refresh() {
		numberField.setText(""+optionMA.numberOfLines(null));
		getMoneyEditor().refresh();
		getTitleTextField().refresh();
		setStatus(true);
	}

}
/** $Log: OptionMoneyAmountPanel.java,v $
/** Revision 1.2  2007/04/02 17:04:24  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:38  perki
/** First commit on sourceforge
/**
/** Revision 1.16  2004/09/24 17:31:24  perki
/** New Currency is now handeled
/**
/** Revision 1.15  2004/09/09 14:12:06  jvaucher
/** - Calculus for DispatcherBounds
/** - OptionCommissionAmountUnder... not finished
/**
/** Revision 1.14  2004/08/04 06:03:12  perki
/** OptionMoneyAmount now have a number of lines
/**
/** Revision 1.13  2004/05/22 17:30:20  carlito
/** *** empty log message ***
/**
/** Revision 1.12  2004/04/09 07:16:51  perki
/** Lot of cleaning
/**
/** Revision 1.11  2004/03/18 15:43:33  perki
/** new option model
/**
/** Revision 1.10  2004/03/12 14:06:10  perki
/** Vaseline machine
/**
/** Revision 1.9  2004/03/08 11:01:18  perki
/** *** empty log message ***
/**
/** Revision 1.8  2004/03/04 19:31:28  carlito
/** *** empty log message ***
/**
/** Revision 1.7  2004/02/26 13:24:34  perki
/** new componenents
/**
/** Revision 1.6  2004/02/06 08:05:41  perki
/** lot of cleaning in UIs
/**
/** Revision 1.5  2004/02/06 07:44:55  perki
/** lot of cleaning in UIs
/**
/** Revision 1.4  2004/02/05 07:45:52  perki
/** *** empty log message ***
/**
 * Revision 1.3  2004/02/04 17:38:04  perki
 * cleaning
 *
 * Revision 1.2  2004/02/04 15:42:16  perki
 * cleaning
 *
 * Revision 1.1  2004/02/04 11:12:46  perki
 * Moneys   .. oh money
 *
 * Revision 1.6  2004/01/30 15:18:12  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/29 14:53:27  perki
 * Eclipse Rocks
 *
 */