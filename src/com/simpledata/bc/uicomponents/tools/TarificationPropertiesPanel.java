/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 22 nov. 2004
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.SButton;
import com.simpledata.filetools.ImageFileChooser;
import com.simpledata.uitools.ImageUtils;

/**
 * This panel contains information about the tarification. 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: TarificationPropertiesPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
public class TarificationPropertiesPanel extends JPanel {
	
	/** Locale */
	private final static String DEMO_CHECK_TEXT = 
        "TarificationPublishingPanel:isDemoTarificationQuestion";
	
	// ##### FIELDS - UI Components ###########################################
	
	final NamedTitleDescriptionEditor m_panelInfo;
	private final JLabel m_icon;
	private final JCheckBox m_visibilityCb;
	private final Tarification m_tarification;
	private final SButton m_changeIconButton;

    // ##### CONSTRUCTOR ######################################################
	
	/**
	 * Construct a new tarification properties panel
	 * @param t The tarification once sets the properties for.
	 */
	public TarificationPropertiesPanel(Tarification t) {
		super();
		
		// fileds init
		m_tarification = t;
		
		m_icon = new JLabel();
		m_changeIconButton = new SButton();
		m_visibilityCb = new JCheckBox();
		m_panelInfo = new NamedTitleDescriptionEditor(t){
			
			public void editionStopped() {
				// NOP	
			}

			public void editionStarted() {
				// NOP
			}};
		buildMe();
	}
	
	/** Build the panel */
	private void buildMe() {
		// panel init 
		setBorder(new TitledBorder(Lang.translate("Tarification properties")));
		setLayout(new GridBagLayout());
		
		// component init
		m_icon.setPreferredSize(new Dimension(32,32));
		m_icon.setIcon(m_tarification.getHeader().getIcon());
		
		m_changeIconButton.setText(Lang.translate("Change Icon"));
		m_changeIconButton.setPreferredSize(new Dimension(120,25));
		m_changeIconButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				changeIconPerformed();
			}});
		m_visibilityCb.setText(Lang.translate(DEMO_CHECK_TEXT));
		m_visibilityCb.setSelected(m_tarification.getHeader().isOpenableInDemo());
        
		m_visibilityCb.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                changeDemoTag();
            }
            
        });
		
		// Labels
		JLabel iconLabel  = new JLabel(Lang.translate("Icon")+":");
		JLabel visibilityLabel = new JLabel(Lang.translate("Visibility")+":");
		Dimension d = new Dimension(75, 20);
		iconLabel.setPreferredSize(d);
		visibilityLabel.setPreferredSize(d);

		
		// Add components to a 3 x 3 (or 2) grid
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0,5,0,5);
		c.gridwidth = 3;
		c.weightx = 0.0;
		add(m_panelInfo, c);
		
		c.gridy = 1;
		c.gridwidth = 1;
		add(iconLabel, c);
		c.gridx = 1;
		
		add(m_icon, c);
		c.gridx = 2;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.EAST;
		add(m_changeIconButton,c);
		c.weightx = 0.0;
		
		if (BC.isSimple()) {
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridy = 2;
			c.gridx = 0;
			c.weighty = 1.0;
			add(visibilityLabel,c);
			c.gridx = 1;
			c.gridwidth = 2;
			add(m_visibilityCb, c);
		} else {
			c.gridy = 2;
			c.gridx = 0;
			c.weighty = 1.0;
			c.gridwidth = 3;
			c.fill = GridBagConstraints.BOTH;
			add(new JPanel(),c); // fill free space
		}
		
	}
	
	/**
	 * Change the demo tag of the tarification
	 * accordingly to the state of the demoQuestionCheck state
	 */
	private void changeDemoTag() {
	    if (m_visibilityCb == null) return;
	    boolean newState = m_visibilityCb.isSelected();
	    m_tarification.getHeader().isOpenableInDemo(newState);
	}
	
	/** Change the icon of the tarification */
	private void changeIconPerformed() {
		Image i = ImageFileChooser.chooseImage(null,
				(JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this));
		if (i == null) return;
		m_tarification.getHeader().changeIcon(
				new ImageIcon(ImageUtils.fitInBox(i,32,32,true))
				);
		m_icon.setIcon(m_tarification.getHeader().getIcon());
	}
}
