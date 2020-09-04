/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 20 sept. 2004
 */
package com.simpledata.bc.uicomponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.tools.JTextFieldBC;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.bc.uitools.SNumField;

/**
 * This panel is a part of the preferences panel. It conains the user
 * preferences for the place where to save and load tarification files.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: LoadAndSavePreferencePanel.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
class LoadAndSavePreferencePanel extends JPanel {
	// TODO When the directory is deleted
	// TODO Lang en and Look and Feel of the file chooser
	// TODO Tune the panel size
	
	// #### CONSTANTS ########################################################
	
	private final static String PANEL_NAME = "Load and save preferences";
	
    /** Locale */
    private final static String CREATE_DIR = 
    	"The selected directory does not exist. Would you create it ?";
	
	// #### FIELDS ###########################################################
    
    private final JTextFieldBC m_saveDirectoryField;
    private final SNumField m_autosavePeriodField;
    private final JLabel m_periodLabel;
    
    // #### CONSTRUCTOR ######################################################
	
    LoadAndSavePreferencePanel() {
		super();
		m_saveDirectoryField = new JTextFieldBC(false) {
			public void startEditing() {
				
			}

			public void stopEditing() {
				String newValue = getText();
				if (validNewDir(new File(newValue))) {
					BC.setParameter(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH, newValue);
				} else {
					setText(BC.getParameterStr(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH));
				}
			}
		};
		m_autosavePeriodField = new SNumField("",true, true, true) {
			public void startEditing() {
				// nothing to do
			}

			public void stopEditing() {
				// record the new value
				Integer value = this.getInteger();
				// bounded 1 <= value <= 1000
				if (value.intValue() == 0) {
					value = new Integer(1);
					setText("1");
				}
				if (value.intValue() > 1000) {
					value = new Integer(1000);
					setText("1000");
				}
				BC.setParameter(Params.KEY_AUTOSAVE_PERIOD, value);
				correctPeriodLabel();
			}
		};
		m_periodLabel = new JLabel();
		buildMe();
	}
	
	// #### METHODS ##########################################################
	
    private void buildMe() {
    	// Look and feel
    	Dimension buttonSize = new Dimension(80,25);
    	Dimension firstColumnWidth = new Dimension(150,30);
    	this.setSize(530,200);
    	this.setName(Lang.translate(PANEL_NAME));
    	
    	// -- init
    	TitledBorder border = new TitledBorder(Lang.translate(PANEL_NAME));
    	this.setBorder(border);
    	this.setLayout(new GridBagLayout());
    	GridBagConstraints c = new GridBagConstraints();
    	c.insets = new Insets(2,2,2,2);
    	c.ipadx = 0;
    	c.ipady = 0;
    	c.anchor = GridBagConstraints.WEST;
    	// (0;0) Tarification library label.
    	JLabel lpLabel = new JLabel(Lang.translate("Tarification library: "));
    	String lpPath = BC.getParameterStr(Params.KEY_TARIFICATION_LIBRARY_PARTH);
    	JTextField lpField = new JTextField(lpPath);
    	lpField.setEditable(false);
    	lpField.setEnabled(false);
    	lpField.setBackground(Resources.disabledFieldBg);
    	this.add(lpLabel, c);
    	// (1;0) Tarification library field.
    	c.gridx = 1;
    	c.weightx = 1.0;
    	c.gridwidth = 2;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	this.add(lpField, c);
    	// (0;1) Saved tarification folder label
    	c.gridx = 0;
    	c.gridy = 1;
    	c.weightx = 0.0;
    	c.gridwidth = 1;
    	c.fill = GridBagConstraints.NONE;
    	// Default folder for saved tarifications
    	JLabel stLabel = new JLabel(Lang.translate("Saved tarification folder: "));
    	String stPath = BC.getParameterStr(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH);
    	File dir = new File(stPath);
    	if (!dir.exists()) {
    		stPath = Resources.findMyDocumentFolder();
    		BC.setParameter(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH, stPath);
    	}
    	m_saveDirectoryField.setText(stPath);
    	JButton browseButton = new JButton(Lang.translate("Browse..."));
    	browseButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				browseActionPerformed();
			}});

    	this.add(stLabel, c);
    	// (1;1) Save directory field
    	c.gridx = 1;
    	c.weightx = 1.0;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	this.add(m_saveDirectoryField, c);
    	// (2;1) Browse button
    	c.gridx = 2;
    	c.fill = GridBagConstraints.NONE;
    	c.gridwidth = GridBagConstraints.REMAINDER;
    	c.weightx = 0.0;
    	this.add(browseButton, c);
    	
    	// (0;2) Save location preference
    	c.gridy = 2;
    	c.gridx = 0;
    	c.weightx = 0.0;
    	c.gridwidth = GridBagConstraints.RELATIVE;
    	// Save location preference (radio buttons)
    	JLabel locPrefLabel = new JLabel (Lang.translate("Load location preference:"));
    	ButtonGroup selection = new ButtonGroup();
    	JRadioButton locButtonLastUsed = 
    		new JRadioButton(Lang.translate("Look for files in the last used directory"));
    	locButtonLastUsed.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				BC.setParameter(Params.KEY_OPEN_FOLDER_PREF, new Integer(Params.PREF_LAST_FOLDER));
				
			}});
    	JRadioButton locButtonDefDir =
    		new JRadioButton(Lang.translate("Look for files in default directory for saved tarifications"));
    	locButtonDefDir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				BC.setParameter(Params.KEY_OPEN_FOLDER_PREF, new Integer(Params.PREF_DEFAULT_FOLDER));
				
			}});
    	selection.add(locButtonLastUsed);
    	selection.add(locButtonDefDir);
    	int currentPref = ((Integer)BC.getParameter(Params.KEY_OPEN_FOLDER_PREF,
    	        Integer.class)).intValue();
    	switch (currentPref) {
    	case Params.PREF_LAST_FOLDER:
    		locButtonLastUsed.setSelected(true);
    		break;
    	case Params.PREF_DEFAULT_FOLDER:
    		locButtonDefDir.setSelected(true);
    	}
    	c.gridwidth = GridBagConstraints.REMAINDER;
    	this.add(locPrefLabel, c);
    	// (0;3) Radio button 1
    	c.insets = new Insets(0,2,0,2);
    	c.gridy = 3;
    	c.weightx = 1.0;
    	this.add(locButtonLastUsed, c);
    	// (0;4) Radio button 2
    	c.gridy = 4;
    	c.anchor = GridBagConstraints.NORTHWEST;
    	this.add(locButtonDefDir, c);
    	// (0;5) Autosave checkbox
    	JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	JCheckBox asCheckBox = new JCheckBox(Lang.translate("Autosave:"));
    	Boolean enabled = (Boolean)BC.getParameter(Params.KEY_AUTOSAVE_ENABLE,
    	        Boolean.class);
    	asCheckBox.setSelected(enabled.booleanValue());
    	asCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox)e.getSource();
		    	boolean enabled = source.isSelected();
				asCheckBoxActionPerformed(enabled);
			}});
    	asCheckBoxActionPerformed(enabled.booleanValue());
    	inner.add(asCheckBox);
    	// (1;5) Period Field
    	String period =
    		String.valueOf(BC.getParameter(Params.KEY_AUTOSAVE_PERIOD, Object.class));
    	m_autosavePeriodField.setText(period);
    	m_autosavePeriodField.setMinimumSize(new Dimension(60,22));
    	m_autosavePeriodField.setPreferredSize(new Dimension(60,22));
    	inner.add(m_autosavePeriodField);
    	// (2;5) Perdiod label
    	correctPeriodLabel();
    	m_periodLabel.setMinimumSize(new Dimension(60,22));
    	inner.add(m_periodLabel);
    	c.gridy = 5;
    	c.gridwidth = GridBagConstraints.REMAINDER;
    	c.anchor = GridBagConstraints.NORTHWEST;
    	c.weighty = 1.0;
    	this.add(inner, c);
    }
    
    void browseActionPerformed() {
    	File oldDir = new File(m_saveDirectoryField.getText());
    	JFileChooser jfc = new JFileChooser(oldDir);
    	jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	switch(jfc.showDialog(BC.bc.getMajorComponent(), Lang.translate("Select"))) {
    	case JFileChooser.APPROVE_OPTION :
    		File newDir= jfc.getSelectedFile();
    		if (validNewDir(newDir)) {
    			String newParamValue = newDir.toString();
    			BC.setParameter(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH, newParamValue);
    			m_saveDirectoryField.setText(newParamValue);
    		}
    	}
    }
    
    void asCheckBoxActionPerformed(boolean newState) {
    	BC.setParameter(Params.KEY_AUTOSAVE_ENABLE, new Boolean(newState));
    	m_autosavePeriodField.setEnabled(newState);
    }
    
    boolean validNewDir(File newDir) {
    	if (!newDir.exists()) {
    		if (ModalDialogBox.confirm(this, Lang.translate(CREATE_DIR)) == ModalDialogBox.ANS_OK)
    			if (newDir.mkdir())
    				return true;
    	} else
    		return true;
    	return false;
    }
    
    void correctPeriodLabel() {
    	Integer period = m_autosavePeriodField.getInteger();
    	boolean plural = (period.intValue() > 1);
    	m_periodLabel.setText(Lang.translate((plural)?"Minutes":"Minute"));
    }
}
