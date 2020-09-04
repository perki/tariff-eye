/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 21 sept. 2004
 */
package com.simpledata.bc.uicomponents;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.simpledata.bc.tools.Lang;

/**
 * Old DepthOfViewPanel. Refactored for logical purposes.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: GeneralPreferencePanel.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class GeneralPreferencePanel extends JPanel {
	private final static String PANEL_NAME = "General preferences";
	// Standard values for the comboBox
	private final static int[] vals = { 1, 2, 3, 4, 5, 10 };
	
	public GeneralPreferencePanel() {
		super();
		
		initComponents();
	}
	
	// Variables declaration - do not modify
	private JLabel comboLabel;
	private JComboBox depthCombo;
	private JLabel explanatoryLabel;
	// End of variables declaration
	
	private void initComponents() {
		TitledBorder border = new TitledBorder(Lang.translate(PANEL_NAME));
    	this.setBorder(border);
		this.setName(PANEL_NAME);
		java.awt.GridBagConstraints gridBagConstraints;
		
		explanatoryLabel = new JLabel();
		comboLabel = new JLabel();
		depthCombo = new JComboBox();
		
		setLayout(new java.awt.GridBagLayout());
		
		explanatoryLabel.setText(Lang.translate("Please select the depth of view :"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		add(explanatoryLabel, gridBagConstraints);
		
		comboLabel.setText(Lang.translate("Depth of View"));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		add(comboLabel, gridBagConstraints);
		
		int arraySize = vals.length;
		int oldDepth = TarifViewer.getMaxDepth().intValue();
		for (int i=0; i<arraySize; i++) {
			depthCombo.addItem(new Integer(vals[i]));
			if (vals[i] == oldDepth) {
				depthCombo.setSelectedIndex(i);
			}
		}
		
		depthCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				depthComboActionPerformed(evt);
			}
		});
		
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		add(depthCombo, gridBagConstraints);
		
		
	}
	
	protected void depthComboActionPerformed(ActionEvent evt) {
		int selIndex = ((Integer)depthCombo.getSelectedItem()).intValue();
		TarifViewer.setMaxDepthStatic(selIndex);
	}
	
}


