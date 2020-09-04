/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionPerBaseTenPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.bcoption.OptionPerBaseTen;
import com.simpledata.bc.uitools.PerBaseTenEditor;

/**
 * UI for OptionPerBaseTen
 * @see com.simpledata.bc.components.bcoption.OptionPerBaseTen
 */
public class OptionPerBaseTenPanel extends OptionDefaultPanel {
	
	private static final Logger m_log = 
	    Logger.getLogger( OptionPerBaseTenPanel.class ); 

	
	public OptionPerBaseTen optionPBT;

	/**
	 * @param option
	 */
	public OptionPerBaseTenPanel(
	        OptionDefaultPanel.EditStates editStateController,
	        OptionPerBaseTen option) {
		super(editStateController,option);
		optionPBT= option;

		this.add(getStatus(), null);
		this.add(getTitleTextField(), null);
		this.add(getEqualLabel(), null);
		this.add(editor(), null);
		refresh();
	}

	private PerBaseTenEditor editor;
	public PerBaseTenEditor editor() {
	    if (editor == null) {
	        editor = new PerBaseTenEditor() {
	            
	            public void changeDivider(int f) {
	                optionPBT.setDivider(f);
                }
	            
                public void changeDoubleValue(double v) {
				  optionPBT.setDoubleValue(v / optionPBT.getDivider());
				  setStatus(true);
                }

                public void startEditing() {
                    setStatus(false);
                }
	            
	        };
	        
	    }
	    return editor;
	}
	

	/**
	 * Load current Data
	 */
	public void refresh() {
		double value = optionPBT.getDoubleValue() * optionPBT.getDivider();
		editor.setDouble(value);
		editor.setDivider(optionPBT.getDivider());
		
		setStatus(true);
	}
	
	
}

/**
 *  $Log: OptionPerBaseTenPanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.11  2004/10/11 10:19:16  perki
 *  Percentage on Transactions
 *
 *  Revision 1.10  2004/09/10 16:29:48  jvaucher
 *  Allows negative percentage for discount
 *
 *  Revision 1.9  2004/09/10 13:03:09  jvaucher
 *  SNumField stronger and stronger
 *
 *  Revision 1.8  2004/09/03 13:25:34  kaspar
 *  ! Log.out -> log4j part four
 *
 *  Revision 1.7  2004/08/17 13:51:49  kaspar
 *  ! #26: crash on RateOnAmount fixed. Cause: assert in the wrong place,
 *    mixup between object init and event callbacks (as usual)
 *
 *  Revision 1.6  2004/07/08 14:59:00  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.5  2004/05/22 17:30:20  carlito
 *  *** empty log message ***
 *
 *  Revision 1.4  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.3  2004/03/18 15:43:33  perki
 *  new option model
 *
 *  Revision 1.2  2004/03/12 14:06:10  perki
 *  Vaseline machine
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */