/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 15 juil. 2004
 * $Id: PreferencePanel.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.uicomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.SButton;

/**
 * This class defines a Preference Panel used in the main app
 * Could be standardized to migrate to SDL... 
 * 
 */
public class PreferencePanel extends JInternalFrame {

    
    /** Panel dimension */
    // For panel size see Desktop.java
    //private final Dimension prefPanelSize = new Dimension(300,500);
    
    private LinkedList/*<JPanel>*/ m_panels;
    
    public PreferencePanel() {
        // Change the 4th parameter for maximizable
        // Change the 5th for iconifiable
        super(Lang.translate("Preferences"), true, true, false, true);
        
        // Select all the preferences subpanels 
        m_panels = new LinkedList();
        m_panels.add(new GeneralPreferencePanel());
        m_panels.add(new LoadAndSavePreferencePanel());
        m_panels.add(new NetworkPreferencePanel());
        if (BC.isSimple())
        	m_panels.add(new DebugPreferencePanel());
        
        // Build the preference panel     
        buildMe();
    }
    
    // Variables declaration - do not modify
    private JLabel buttonsEmptyLabel;
    private JPanel buttonsPanel;
    private JTabbedPane contentPanel;
    private JLabel descriptionLabel;
    private SButton doneButton;
    private JLabel imageLabel;
    private JPanel textPanel;
    private JLabel titleLabel;
    private JPanel titlePanel;
    private JPanel titlePanelEncapsulator;
    // End of variables declaration
    
    /**
     * Override super dispose
     * to add personnal closing code
     */
    public void dispose() {
        // Ensure that focus is lost by any panel that could have it
        this.titleLabel.grabFocus();
        super.dispose();
    }
    
    /**
     * Initialize all graphical components
     */
    private void buildMe() {
    	setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GridBagConstraints gridBagConstraints;

        // Initalize objects
        titlePanelEncapsulator = new JPanel();
        titlePanel = new JPanel();
        imageLabel = new JLabel();
        textPanel = new JPanel();
        titleLabel = new JLabel();
        descriptionLabel = new JLabel();
        contentPanel = new JTabbedPane();
        buttonsPanel = new JPanel();
        buttonsEmptyLabel = new JLabel();
        doneButton = new SButton();

        // 1. TITLE PANEL
        
        titlePanelEncapsulator.setLayout(new GridBagLayout());
        titlePanelEncapsulator.setBackground(new Color(255, 255, 255));
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setOpaque(false);
        imageLabel.setIcon(Resources.preferencePanelIcon);
        imageLabel.setIconTextGap(10);
        titlePanel.add(imageLabel, BorderLayout.WEST);

        textPanel.setLayout(new GridBagLayout());

        textPanel.setOpaque(false);
        titleLabel.setFont(new Font("Dialog", 1, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        titleLabel.setIconTextGap(10);
        titleLabel.setMaximumSize(new Dimension(81, 5));
        titleLabel.setMinimumSize(new Dimension(45, 5));
        titleLabel.setPreferredSize(new Dimension(45, 5));
        titleLabel.setText(Lang.translate("Preferences"));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 7, 0, 7);
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        textPanel.add(titleLabel, gridBagConstraints);

        	
        descriptionLabel.setFont(new Font("Lucida Grande", 0, 10));
        descriptionLabel.setText(Lang.translate("Preference Panel description1"));
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(3, 7, 0, 7);
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        textPanel.add(descriptionLabel, gridBagConstraints);

        titlePanel.add(textPanel, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        titlePanelEncapsulator.add(titlePanel, gridBagConstraints);

        getContentPane().add(titlePanelEncapsulator, BorderLayout.NORTH);

        // 2 Content panel - tabbed pane for the differents preferences panels
        
        for (int i = 0; i<m_panels.size(); i++ ) {
            JPanel jp = (JPanel)m_panels.get(i);
            contentPanel.add(jp);
        }
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        buttonsPanel.add(buttonsEmptyLabel, gridBagConstraints);

        doneButton.setFont(new Font("Lucida Grande", 0, 12));
        doneButton.setText(Lang.translate("Done"));
        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });

        buttonsPanel.add(doneButton, new GridBagConstraints());

        
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        pack();
    }

    
    /**
     * Called whenever the done button is pressed
     * @param evt
     */
    private void doneButtonActionPerformed(ActionEvent evt) {
        this.dispose();
    }
    
}



/*
 * $Log: PreferencePanel.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:37  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/11/29 13:56:50  jvaucher
 * Proxy bug fixed. Notice that unresolvable host yields to the use of
 * a direct connection.
 *
 * Revision 1.7  2004/11/26 10:06:00  jvaucher
 * Begining of TariffEyeInfo feature
 *
 * Revision 1.6  2004/09/23 11:00:48  jvaucher
 * Improved filechooser rendering
 *
 * Revision 1.5  2004/09/21 17:07:03  jvaucher
 * Implemented load and save preferences
 * Need perhaps (certainly) to test the case where one refered folder is deleted
 *
 * Revision 1.4  2004/09/13 15:27:32  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/09/04 18:12:31  kaspar
 * ! Log.out -> log4j
 *   Only the proper logger init is missing now.
 *
 * Revision 1.2  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/07/15 17:44:38  carlito
 * *** empty log message ***
 *
 */