/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionDoublePositivePanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import com.simpledata.bc.components.bcoption.OptionDoublePositive;
import com.simpledata.bc.uitools.SNumField;

/**
* UI for OptionDoublePositive
* @see com.simpledata.bc.components.bcoption.OptionDoublePositive
 */
public class OptionDoublePositivePanel extends OptionDefaultPanel {

    /** Width of the Value Field **/
	public static int VALUE_W = 150;

	private SNumField valueField= null;

	public OptionDoublePositive optionDP;

	/**
	 * @param option
	 */
	public OptionDoublePositivePanel(OptionDefaultPanel.EditStates editStateController, OptionDoublePositive option) {
		super(editStateController,option);
		optionDP= option;
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

			valueField= new SNumField(optionDP.getValue(),false) {
				public void stopEditing() {
					Double res = getDouble();
					if (res != null) {
						optionDP.setDoubleValue(res.doubleValue());
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
		valueField.setText(optionDP.getValue());
		setStatus(true);
	}
}

/**
 *  $Log: OptionDoublePositivePanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.5  2004/09/09 12:26:08  perki
 *  Cleaning
 *
 *  Revision 1.4  2004/05/22 17:30:20  carlito
 *  *** empty log message ***
 *
 *  Revision 1.3  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.2  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.1  2004/03/12 14:07:51  perki
 *  Vaseline machine
 *
 */