/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 mai 2004
 * $Id: OptionBooleanPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.ChangeListener;

import com.simpledata.bc.components.bcoption.OptionBoolean;


/**
 * 
 * 
 */
public class OptionBooleanPanel extends OptionDefaultPanel {

   
    private JPanel container;
    private OptionBooleanNotEditablePanel notEditablePanel;
    private OptionBooleanEditablePanel editablePanel;
    
    /**
     * CONSTRUCTOR
     * @param option
     */
	public OptionBooleanPanel(
	        OptionDefaultPanel.EditStates editStateControler, 
	        OptionBoolean option) {
        super(editStateControler, option);

        this.add(getStatus(), null);
		this.add(getTitleTextField(), null);
		
		container = new JPanel();
		container.setLayout(new BorderLayout());
		this.add(container, null);
		
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
                this.editablePanel = new OptionBooleanEditablePanel(this);
            }
            
            this.editablePanel.refresh();
            
            this.container.removeAll();
            this.container.add(this.editablePanel, BorderLayout.CENTER);
            
        } else {
            // We are in the simulation configuration
            if (this.notEditablePanel == null) {
                // We create it and destroy the other one
                this.editablePanel = null;
                this.notEditablePanel = new OptionBooleanNotEditablePanel(this);
            }
            
            this.notEditablePanel.refresh();
            
            this.container.removeAll();
            this.container.add(this.notEditablePanel, BorderLayout.CENTER);
            
        }
        
        setStatus(true);
        
        repaint();
    }

}

class OptionBooleanEditablePanel extends JPanel {
    
    private OptionBooleanPanel owner;
    private JCheckBox check;
    
    public OptionBooleanEditablePanel(OptionBooleanPanel obp) {
        super();
        this.owner = obp;
        this.setLayout(new BorderLayout());
        this.check = new JCheckBox("State");
        this.check.addChangeListener(new StateListener(this));
        this.add(this.check, BorderLayout.CENTER);
    }

    public void refresh() {
        OptionBoolean ob = (OptionBoolean)this.owner.getOption();
        
        boolean state = ob.getState();
        this.check.setSelected(state);
    }
    
    class StateListener implements ChangeListener {

        private OptionBooleanEditablePanel obep;
        
        public StateListener(OptionBooleanEditablePanel o) {
            this.obep = o;
        }
        
        public void stateChanged(ChangeEvent e) {
            JCheckBox jcb = (JCheckBox)e.getSource();
            boolean b = jcb.isSelected();
            this.obep.statusChanged(b);            
        }
        
    }
    
    protected void statusChanged(boolean b) {
        OptionBoolean ob = (OptionBoolean)this.owner.getOption();
        
        ob.setState(b);
        this.refresh();
    }
    
}

class OptionBooleanNotEditablePanel extends JPanel {
    
    private OptionBooleanPanel owner;

    // Graphical components
    private JCheckBox trueCheck;
    private JCheckBox falseCheck;
    
    public OptionBooleanNotEditablePanel(OptionBooleanPanel obp) {
        super();
        this.owner = obp;
        
        this.setPreferredSize(new Dimension(OptionDefaultPanel.DEF_WIDTH-OptionDefaultPanel.DEF_TITLE_W-OptionDefaultPanel.DEF_STATUS_W-30, OptionDefaultPanel.DEF_COMPONENT_H));
        
        initComponents();
    }
    
    private void initComponents() {
        this.trueCheck = new JCheckBox("true");
        this.trueCheck.addChangeListener(new StateListener(this, true));
        this.falseCheck = new JCheckBox("false");
        this.falseCheck.addChangeListener(new StateListener(this, false));
        
        this.setLayout(new BorderLayout());
        this.add(this.trueCheck, BorderLayout.WEST);
        this.add(this.falseCheck, BorderLayout.EAST);
        
    }
    
    public void refresh() {
        OptionBoolean ob = (OptionBoolean)this.owner.getOption();
        
        boolean state = ob.getState();
        
        this.trueCheck.setSelected(state);
        this.falseCheck.setSelected(!state);
    }
    
    class StateListener implements ChangeListener {

        private OptionBooleanNotEditablePanel obep;
        // true source is true check
        // false source is false check
        private boolean source;
        
        public StateListener(OptionBooleanNotEditablePanel o, boolean source) {
            this.obep = o;
            this.source = source;
        }
        
        public void stateChanged(ChangeEvent e) {
            JCheckBox jcb = (JCheckBox)e.getSource();
            boolean b = jcb.isSelected();
            this.obep.statusChanged(b,this.source);            
        }
        
    }
    
    protected void statusChanged(boolean b, boolean source) {
        OptionBoolean ob = (OptionBoolean)this.owner.getOption();
        if (source) {
            ob.setState(b);
        } else {
            ob.setState(!b);
        }
        this.refresh();
    }
    
}

/*
 * $Log: OptionBooleanPanel.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.7  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/22 17:58:19  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/22 17:30:20  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/22 08:39:35  perki
 * Lot of cleaning
 *
 * Revision 1.3  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.2  2004/05/21 12:15:12  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/18 19:11:52  carlito
 * *** empty log message ***
 *
 */