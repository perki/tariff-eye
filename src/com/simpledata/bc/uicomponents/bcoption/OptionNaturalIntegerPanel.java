/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionNaturalIntegerPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import com.simpledata.bc.components.bcoption.OptionNaturalInteger;
import com.simpledata.bc.uitools.SNumField;

/**
* UI for OptionNaturalInteger
* @see com.simpledata.bc.components.bcoption.OptionNaturalInteger
 */
public class OptionNaturalIntegerPanel extends OptionDefaultPanel {

	/** Width of the Value Field **/
	public static int VALUE_W = 150;

	private SNumField valueField;

	private OptionNaturalInteger optionNA;

	/**
	 * @param option
	 */
	public OptionNaturalIntegerPanel(OptionDefaultPanel.EditStates editStateController,
	        OptionNaturalInteger option) {
		super(editStateController,option);
		optionNA= option;
		this.add(getStatus(), null);
		this.add(getTitleTextField(), null);
		this.add(getEqualLabel(), null);
		this.add(getValueField(), null);
		refresh();
	}

	/**
	 * This method initializes valueField
	 * 
	 * @return JTextField
	 */
	private SNumField getValueField() {
		if (valueField == null) {

			valueField= new SNumField(optionNA.getValue(),true) {
				public void stopEditing() {
					Integer res = getInteger();
					if (res != null) {
						optionNA.setIntValue(res.intValue());
					}
				}

				public void startEditing() {
					setStatus(false);
				}
			};

			setDim(valueField,VALUE_W, DEF_COMPONENT_H);
		}
		return valueField;
	}
	
	/**
	 * Load current Data
	 */
	public void refresh() {
		valueField.setText(optionNA.getValue());
		setStatus(true);
	}
}
/** 
 * $Log: OptionNaturalIntegerPanel.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.10  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.9  2004/05/22 17:30:20  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.7  2004/03/18 15:43:33  perki
 * new option model
 *
 * Revision 1.6  2004/03/12 14:06:10  perki
 * Vaseline machine
 *
 * Revision 1.5  2004/02/22 10:43:57  perki
 * File loading and saving
 *
 * Revision 1.4  2004/02/06 08:05:41  perki
 * lot of cleaning in UIs
 *
 * Revision 1.3  2004/02/06 07:44:55  perki
 * lot of cleaning in UIs
 *
 * Revision 1.2  2004/02/05 15:11:39  perki
 * Zigouuuuuuuuuuuuuu
 *
 * Revision 1.1  2004/02/05 07:46:54  perki
 * Transactions are welcome aboard
 *
 */