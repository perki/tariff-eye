/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionSimplePanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import com.simpledata.bc.components.bcoption.OptionSimple;
import com.simpledata.bc.uicomponents.tools.JTextFieldBC;

/**
 *UI for OptionSimple
 *@see com.simpledata.bc.components.bcoption.OptionSimple
 */
public class OptionSimplePanel extends OptionDefaultPanel {

	/** the width of the Value Field **/
	public static int VALUEFIELD_W = 100;

	private JTextFieldBC valueField = null;
	
	private OptionSimple optionSimple;

	/**
	 * @param option

	 */
	public OptionSimplePanel(OptionDefaultPanel.EditStates editStateController,
	        OptionSimple option) {
		super(editStateController,option);
		optionSimple = option;
		
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
	private JTextFieldBC getValueField() {
		if (valueField == null) {
			
			valueField = new JTextFieldBC() {
				public void stopEditing() {
					mySave();	
				}

				public void startEditing() {
					setStatus(false);
				}
			};
			setDim(valueField,VALUEFIELD_W, DEF_COMPONENT_H);
		}
		return valueField;
	}
	/**
	 * Save current Data
	 */
	public void mySave() {
		optionSimple.setValue(valueField.getText());
	}
	
	/**
	 * Load current Data
	 */
	public void refresh() {
		valueField.setText(optionSimple.getValue());
		setStatus(true);
	}
} 
/** $Log: OptionSimplePanel.java,v $
/** Revision 1.2  2007/04/02 17:04:24  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:38  perki
/** First commit on sourceforge
/**
/** Revision 1.14  2004/05/22 17:30:20  carlito
/** *** empty log message ***
/**
/** Revision 1.13  2004/04/09 07:16:51  perki
/** Lot of cleaning
/**
/** Revision 1.12  2004/03/18 15:43:33  perki
/** new option model
/**
/** Revision 1.11  2004/03/12 14:06:10  perki
/** Vaseline machine
/**
/** Revision 1.10  2004/02/06 08:05:41  perki
/** lot of cleaning in UIs
/**
/** Revision 1.9  2004/02/06 07:44:55  perki
/** lot of cleaning in UIs
/**
/** Revision 1.8  2004/02/05 07:45:52  perki
/** *** empty log message ***
/**
 * Revision 1.7  2004/02/04 11:11:35  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/01/30 15:18:12  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/29 14:53:27  perki
 * Eclipse Rocks
 *
 */