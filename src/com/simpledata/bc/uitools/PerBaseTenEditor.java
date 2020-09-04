/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: PerBaseTenEditor.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.simpledata.bc.components.bcoption.OptionPerBaseTen;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;

/**
 * A simple Component with a Snumfield and a COmboBoy to edit percentages
 */
public abstract class PerBaseTenEditor extends JPanel {

	/** the width of the Value Field **/
	private static int VALUEFIELD_W= 60;

	/** the width of the Base Chooser **/
	private static int BASECHOOSER_W= 50;

	/** default height **/
	private static int DEF_H= 20;
	
	
	public SNumField valueField;

	public JComboBox baseChooser;

	
	public PerBaseTenEditor() {
	    setLayout(new FlowLayout(FlowLayout.LEADING,0,0));
	    add(getValueField());
	    add(getBaseChooser());
	}
	
	/** value **/
	public final void setDouble(double value) {
	    getValueField().setDouble(value);
	}
	
	/** one of 1,10,100 **/
	public final void setDivider(int div) {
	    baseChooser.setSelectedItem(new MyBase(div));
	}
	
	protected abstract void changeDivider(int div);
	protected abstract void changeDoubleValue(double value);
	protected abstract void startEditing();
	
	/**
	 * setEditable()
	 */
	public void setEditable(boolean b) {
	    getValueField().setEditable(b);
	    getBaseChooser().setEnabled(b);
	}
	
	/**
	 * This method initializes valueField
	 * 
	 * @return JTextField
	 */
	private final SNumField getValueField() {
		if (valueField == null) {
		    final PerBaseTenEditor tthis = this;
			valueField= new SNumField("", false, true, false) {
				public void stopEditing() {
				    changeDoubleValue(getDouble().doubleValue());
				}
				public void startEditing() {
				    tthis.startEditing();
				}
			};
			valueField.setDigitAfterComa(2);
			OptionDefaultPanel.setDim(valueField, VALUEFIELD_W, DEF_H);

		}
		return valueField;
	}
	
	/**
	 * This method initializes baseChooser
	 * 
	 * @return javax.swing.JComboBox
	 */
	private final JComboBox getBaseChooser() {
		if (baseChooser == null) {
			ArrayList v= new ArrayList();
			for (int i= 0;
				i < OptionPerBaseTen.ACCEPTED_DIVIDERS.length;
				i++) {
				v.add(new MyBase(OptionPerBaseTen.ACCEPTED_DIVIDERS[i]));
			}
			baseChooser= new JComboBox(v.toArray()); // Generated
			OptionDefaultPanel.setDim(baseChooser, BASECHOOSER_W, DEF_H);

			baseChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    changeDivider(
				            ((MyBase) baseChooser.getSelectedItem()).value);
					
				}
			});
		}
		return baseChooser;
	}
    
	

}



class MyBase {
	public int value;

	public MyBase(int i) {
		value= i;
	}

	public String toString() {
		switch (value) {
			case 1 :
				return "";
			case 100 :
				return "�/.";
			case 1000 :
				return "�/..";
		}
		return "" + value;
	}

	public boolean equals(Object o) {
		if (o instanceof MyBase)
			return (((MyBase) o).value == value);
		return false;
	}
}