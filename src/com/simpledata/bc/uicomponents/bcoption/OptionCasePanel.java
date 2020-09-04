/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 11 juin 2004
 * $Id: OptionCasePanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.simpledata.bc.*;
import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.OptionCase;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.*;

/**
 * This is the standard panel to encapsulate an option
 * with multiple choices but only one answer
 */
public class OptionCasePanel extends OptionDefaultPanel {

    private JPanel container;
    
    private OptionCaseEditablePanel editablePanel;
    private OptionCaseNotEditablePanel notEditablePanel;
    
    /**
     * CONSTRUCTOR
     * @param option
     */
	public OptionCasePanel(OptionDefaultPanel.EditStates editStateControler, OptionCase option) {
        super(editStateControler, option);
        setLayout(new BorderLayout());
        setDim(this, DEF_WIDTH, 30);
        		
		container = new JPanel();
		container.setLayout(new BorderLayout());
		this.add(container, BorderLayout.CENTER);
		
        refresh();
    }
    
    public void refresh() {
    	boolean isEditable = 
    		super.getEditState() == OptionDefaultPanel.EditStates.FULL;
        if (isEditable) {
            // We are in the creation configuration
            if (this.editablePanel == null) {
                // We create it and destroy the other one
                this.notEditablePanel = null;
                this.editablePanel = new OptionCaseEditablePanel(this);
                this.container.removeAll();
                this.container.add(this.editablePanel, BorderLayout.CENTER);
            }
            
            this.editablePanel.refresh();
        } else {
            // We are in the simulation configuration
            if (this.notEditablePanel == null) {
                // We create it and destroy the other one
                this.editablePanel = null;
                this.notEditablePanel = new OptionCaseNotEditablePanel(this);
                this.container.removeAll();
                this.container.add(this.notEditablePanel, BorderLayout.CENTER);
            }
            
            this.notEditablePanel.refresh();
        }
        
        setStatus(true);
        
        repaint();
    }
    
}

/**
 * An abstract panel for OptionCasesSubPanel 
 * Optimized for code reuse...
 */
abstract class OptionCaseAbstractPanel extends JPanel implements ActionListener {
    
    protected boolean refreshing = false;
    protected OptionCasePanel owner;
    
    public OptionCaseAbstractPanel(OptionCasePanel owner) {
        this.owner = owner;
        refreshing = false;
    }
    
    protected abstract void internalRefresh();
    
    public void refresh() {
        refreshing = true;
        internalRefresh();
        refreshing = false;
    }
    
    protected abstract JComboBox getCombo();
    
    public void actionPerformed(ActionEvent e) {
        // Avoid this treatment while combo is being recreated
        if (refreshing) return;
        
        OptionCase oc = (OptionCase)this.owner.getOption();
        String oldKey = oc.getSelectedKey();
        CaseListComponent clc = (CaseListComponent)getCombo().getSelectedItem();
        
        if (clc != null) {
            String newKey = clc.getKey();
            if (oldKey != newKey) {
                oc.setSelectedCase(newKey);
            }
        }    
        
    }
}


/**
 * This small component will be used to fill in the
 * JComboBoxes
 */
class CaseListComponent {
    
    private String key;
    private OptionCase owner;
    
    public CaseListComponent(OptionCase owner, String key) {
        this.owner = owner;
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getObject() {
        return this.owner.getCase(key);
    }
    
    public String toString() {
        return getObject();
    }
    
}


/**
 * The panel that would represent an OptionCase in creation mode
 */
class OptionCaseEditablePanel extends OptionCaseAbstractPanel {

    private static final String DELETE_CASE_MESSAGE = 
        "OptionCasePanel:confirmCaseDeletion";
    
    /**
     * @param owner
     */
    public OptionCaseEditablePanel(OptionCasePanel owner) {
        super(owner);
        initComponents();
    }

    protected void internalRefresh() {
        // Reconstruct the combo and reselect correct index
        
        OptionCase oc = (OptionCase)(this.owner.getOption());
        
        int noc = oc.getNumberOfCases();
        
        casesCombo.removeAllItems();
        
        
        if (noc == 0) {
            // We deactivate remove and update buttons
            casesCombo.setEnabled(false);
            deleteCaseButton.setEnabled(false);
            editCaseButton.setEnabled(false);
        } else  {
            
            CaseListComponent selectedObject = null;
            String selectedKey = oc.getSelectedKey();
            for (int i=0; i<noc ; i++) {
                String key = oc.getKeyForPos(i);
                CaseListComponent clc = new CaseListComponent(oc,key);
                casesCombo.addItem(clc);
                if (selectedKey.equals(key)) {
                    selectedObject = clc;
                }
            }
            
            editCaseButton.setEnabled(true);
            if (noc == 1) {           
                // We prohibit more remove
                casesCombo.setEnabled(false);
                deleteCaseButton.setEnabled(false);
            } else {
                casesCombo.setEnabled(true);
                deleteCaseButton.setEnabled(true);
            }
            
            if (selectedObject != null)
                casesCombo.setSelectedItem(selectedObject);
        }
    }

    /**
     * @param caseKey
     * @param text
     */
    protected void saveCase(String caseKey, String text) {
        if (!text.equals("")) {
            OptionCase oc = (OptionCase)this.owner.getOption();
            if (caseKey == null) {
                oc.addCase(text);
            } else {
                oc.modifyCase(caseKey ,text);
            }
        }
    }
    
    private void initComponents() {
        
        setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(2,1,2,1);
        constraints.anchor = GridBagConstraints.WEST;
        
        add(owner.getStatus(), constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(2,1,2,2);
        
        add(owner.getTitleTextField(), constraints);
        
        
        allCasesPanel = new JPanel();
        casesLabel = new JLabel();
        casesCombo = new JComboBox();
        editCaseButton = new SButtonIcon();
        deleteCaseButton = new SButtonIcon();
        fillLabel3 = new JLabel();
        newCaseButton = new SButtonIcon();
        
        /////////////////////////////////////////////////
        ///// FILLING CASE CONTAINER          BEGIN
        /////////////////////////////////////////////////
        
        allCasesPanel.setLayout(new GridBagLayout());


        casesLabel.setLabelFor(casesCombo);
        casesLabel.setText(":");
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 3, 2, 3);
        constraints.anchor = GridBagConstraints.WEST;
        allCasesPanel.add(casesLabel, constraints);

        casesCombo.setMinimumSize(new Dimension(100, 27));
        casesCombo.setOpaque(false);

        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 0, 2, 3);
        constraints.anchor = GridBagConstraints.WEST;
        allCasesPanel.add(casesCombo, constraints);

        editCaseButton.setIcon(Resources.iconEdit);
        editCaseButton.setToolTipText(Lang.translate("Edit"));
        editCaseButton.setOpaque(false);
        editCaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                editCaseButtonActionPerformed(evt);
            }
        });

        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 0, 2, 3);
        constraints.anchor = GridBagConstraints.WEST;
        allCasesPanel.add(editCaseButton, constraints);

        deleteCaseButton.setIcon(Resources.iconDelete);
        deleteCaseButton.setToolTipText(Lang.translate("Delete"));
        deleteCaseButton.setOpaque(false);
        deleteCaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteCaseButtonActionPerformed(evt);
            }
        });

        constraints = new GridBagConstraints();
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 0, 2, 16);
        constraints.anchor = GridBagConstraints.WEST;
        allCasesPanel.add(deleteCaseButton, constraints);

        constraints = new GridBagConstraints();
        constraints.gridx = 5;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 5, 2, 5);
        constraints.anchor = GridBagConstraints.WEST;
        allCasesPanel.add(fillLabel3, constraints);

        newCaseButton.setIcon(Resources.iconNew);
        newCaseButton.setToolTipText(Lang.translate("New"));
        newCaseButton.setOpaque(false);
        newCaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                newCaseButtonActionPerformed(evt);
            }
        });

        constraints = new GridBagConstraints();
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.insets = new Insets(2, 0, 2, 3);
        constraints.anchor = GridBagConstraints.WEST;
        allCasesPanel.add(newCaseButton, constraints);
        
        /////////////////////////////////////////////////
        ///// FILLING CASE CONTAINER          END
        /////////////////////////////////////////////////
        
        
        constraints = new GridBagConstraints();
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        add(allCasesPanel, constraints);
                
        // Start to listen to comboEvents...
        casesCombo.addActionListener(this);
        
    }
    
    private void newCaseButtonActionPerformed(ActionEvent evt) {
        ModalJPanel.warpJInternalFrame(
                new CaseEditorPanel(this, Lang.translate("New case"), null), 
                newCaseButton, new Point(-100,-25), Resources.modalBgColor);
    }
    
    private void deleteCaseButtonActionPerformed(ActionEvent evt) {
        if (ModalDialogBox.confirm(BC.bc.getMajorComponent(), 
                Lang.translate(DELETE_CASE_MESSAGE))
                ==	ModalDialogBox.ANS_OK) {
            int nbItems = casesCombo.getItemCount();
            if (nbItems > 0) {
                CaseListComponent clc = (CaseListComponent)casesCombo.getSelectedItem();
                OptionCase oc = (OptionCase)this.owner.getOption();
                oc.removeCase(clc.getKey());
            }
        }
    }

    private void editCaseButtonActionPerformed(ActionEvent evt) {
        CaseListComponent clc = (CaseListComponent)casesCombo.getSelectedItem();
        
        if (clc != null) {
            ModalJPanel.warpJInternalFrame(
                    new CaseEditorPanel(this, clc.toString(), clc.getKey()), 
                    newCaseButton, new Point(-100,-25), Resources.modalBgColor);
        }
    }
    
    protected JComboBox getCombo() {
        return casesCombo;
    }
    
    // Variables declaration - do not modify

    private JPanel allCasesPanel;
    private JComboBox casesCombo;
    private JLabel casesLabel;
    private SButtonIcon deleteCaseButton;
    private SButtonIcon editCaseButton;
    private JLabel fillLabel3;
    private SButtonIcon newCaseButton;
    // End of variables declaration
    
}


/**
 * Small panel allowing edition or creation of a single case
 */
class CaseEditorPanel extends JInternalFrame {
    
    private OptionCaseEditablePanel owner;
    private String caseKey;
    
    /**
     * Creates a new CaseEditorPanel attached to its OptionCaseEditablePanel
     * @param parent OptionCasePanel
     * @param title the text we want in the textField
     * @param key the key for the workSheet (null for a new case...)
     */
    public CaseEditorPanel(OptionCaseEditablePanel parent, String title, String key) {
        super(Lang.translate("Case editor"), true, false, false, false);
        
        this.owner = parent;
        this.caseKey = key;
        
        initComponents();
        
        caseTitleField.setText(title);
    }
    
    private void initComponents() {
        
        caseTitleLabel = new javax.swing.JLabel();
        caseTitleField = new javax.swing.JTextField();
        fillLabel4 = new javax.swing.JLabel();
        editCaseInnerPanel = new javax.swing.JPanel();
        fillLabel7 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        
        getContentPane().setLayout(new GridBagLayout());
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        caseTitleLabel.setLabelFor(caseTitleField);
        caseTitleLabel.setText(Lang.translate("Case Title"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        getContentPane().add(caseTitleLabel, gridBagConstraints);

        caseTitleField.setMinimumSize(new java.awt.Dimension(150, 22));
        caseTitleField.setPreferredSize(new java.awt.Dimension(200, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        gridBagConstraints.weightx = 0.5;
        getContentPane().add(caseTitleField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        getContentPane().add(fillLabel4, gridBagConstraints);

        editCaseInnerPanel.setLayout(new java.awt.GridBagLayout());

        editCaseInnerPanel.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        editCaseInnerPanel.add(fillLabel7, gridBagConstraints);

        saveButton.setText(Lang.translate("ok"));
        saveButton.setOpaque(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 7);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        editCaseInnerPanel.add(saveButton, gridBagConstraints);

        cancelButton.setText(Lang.translate("cancel"));
        cancelButton.setOpaque(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        editCaseInnerPanel.add(cancelButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(editCaseInnerPanel, gridBagConstraints);
    }
    
    private void saveButtonActionPerformed() {
        this.owner.saveCase(caseKey, caseTitleField.getText());
        dispose();
    }

    private void cancelButtonActionPerformed() {
        dispose();
    }

    
    // Variables declaration - do not modify
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField caseTitleField;
    private javax.swing.JLabel caseTitleLabel;
    private javax.swing.JPanel editCaseInnerPanel;
    private javax.swing.JLabel fillLabel4;
    private javax.swing.JLabel fillLabel7;
    private javax.swing.JButton saveButton;
    // End of variables declaration

}


/**
 * The panel that would represent an OptionCase in simulation mode
 */
class OptionCaseNotEditablePanel extends OptionCaseAbstractPanel {

    /**
     * @param owner
     */
    public OptionCaseNotEditablePanel(OptionCasePanel owner) {
        super(owner);
        initComponents();
    }

    protected void internalRefresh() {
        OptionCase oc = (OptionCase)this.owner.getOption();
        
        OptionTitleNonEditableLabel.setText(oc.getTitle()+" :");
        
        CaseListComponent selectedObject = null;
        String selectedKey = oc.getSelectedKey();
        
        int noc = oc.getNumberOfCases();
        caseComboNotEditable.removeAllItems();
        for (int i=0; i<noc ; i++) {
            String key = oc.getKeyForPos(i);
            CaseListComponent clc = new CaseListComponent(oc,key);
            caseComboNotEditable.addItem(clc);
            if (selectedKey.equals(key)) {
                selectedObject = clc;
            }
        }
        
        if (selectedObject != null)
            caseComboNotEditable.setSelectedItem(selectedObject);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        OptionTitleNonEditableLabel = new javax.swing.JLabel();
        caseComboNotEditable = new javax.swing.JComboBox();

        setLayout(new java.awt.GridBagLayout());

        OptionTitleNonEditableLabel.setText("Question :");
        OptionTitleNonEditableLabel.setFont(
                OptionTitleNonEditableLabel.getFont().deriveFont(Font.BOLD));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        add(OptionTitleNonEditableLabel, gridBagConstraints);

        caseComboNotEditable.setMinimumSize(new java.awt.Dimension(100, 27));
        caseComboNotEditable.setOpaque(false);

        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        
        add(caseComboNotEditable, gridBagConstraints);
        
        // Register as listener of the comboBox
        caseComboNotEditable.addActionListener(this);
    }
    
    
    protected JComboBox getCombo() {
        return caseComboNotEditable;
    }
    
    // Graphical Variables declaration
    private javax.swing.JLabel OptionTitleNonEditableLabel;
    private javax.swing.JComboBox caseComboNotEditable;
    // End of variables declaration
    
}


/*
 * $Log: OptionCasePanel.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.13  2004/11/17 18:28:09  carlito
 * arggg
 *
 * Revision 1.12  2004/11/17 15:26:22  carlito
 * New design for WorkSheetPanel, WorkSheetPanelBorder and DispatcherCasePanel advanced
 *
 * Revision 1.11  2004/11/15 11:33:57  carlito
 * bug w case solved
 *
 * Revision 1.10  2004/11/09 18:29:26  carlito
 * Dispatcher case upgraded according to issue 43
 *
 * Revision 1.9  2004/10/06 17:27:39  carlito
 * Help continues... an alert has been added to DispatcherCase to allow secured deletion
 *
 * Revision 1.8  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 * Revision 1.7  2004/08/05 00:23:44  carlito
 * DispatcherCase bugs corrected and aspect improved
 *
 * Revision 1.6  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.4  2004/06/30 08:59:18  carlito
 * web improvment and dispatcher case debugging
 *
 * Revision 1.3  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/06/28 12:48:49  carlito
 * Dispatcher case++
 *
 * Revision 1.1  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 */