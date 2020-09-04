/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionFuturePanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.simpledata.bc.components.bcoption.OptionFuture;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.SNumField;

/**
 * @author SD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OptionFuturePanel extends OptionDefaultPanel {
    
    
    private OptionFuture of;
  
    
    /**
     * CONSTRUCTOR
     * @param option
     */
	public OptionFuturePanel(OptionDefaultPanel.EditStates editStateControler, 
	        OptionFuture option) {
        super(editStateControler, option);

        of = option;
        
        this.add(getStatus(), null);
        this.add(new JLabel(Lang.translate("Future :")));
		this.add(directionCombo(), null);
		this.add(getNumberField(), null);
		this.add(new JLabel(Lang.translate("contracts")));
		
        refresh();
    }    
	
	/** the width of the numberField **/
	public static int NUMBERFIELD_W = 30;
	private SNumField numberField;
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
					of.setNumberOfContracts(i.intValue());
					 refresh();
				}

				public void startEditing() {
					setStatus(false);
				}};
			
			setDim(numberField,NUMBERFIELD_W,DEF_COMPONENT_H);  
		}
		return numberField;
	}
	
	private JComboBox direction;
	/**
	 * Get a direction combo
	 */
	private JComboBox directionCombo() {
		if (direction != null) return direction;
		Direction[] d = new Direction[2];
		d[0] = new Direction(true);
		d[1] = new Direction(false);
		direction = new JComboBox(d);
		direction.setSelectedIndex(
				of.onOpening() ? 0 : 1);
		
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
	
	//	 a simple class for appliesOnType
	class Direction {
		boolean isOnOpening;
		
		public Direction(boolean b) {
			this.isOnOpening = b;
		}
		
		public String toString() {
			if (isOnOpening) {
				return Lang.translate("On opening");
			}
			return Lang.translate("On closeing");
		}
		
	}

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel#refresh()
     */
    public void refresh() {
    	numberField.setText(""+of.numberOfContracts());
		direction.setSelectedIndex(
				of.onOpening() ? 0 : 1);
		setStatus(true);
    }
    
    /**
	 * Save current Data
	 */
	public void save() {
	    Direction d = (Direction) directionCombo().getSelectedItem();
	    of.setOnOpening(d.isOnOpening);
	    refresh();
	}

}
