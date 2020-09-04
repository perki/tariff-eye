/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: DiscountMenu.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.simulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uitools.streetable.STreeTable;

/**
 * A menu to choose if the Discounts should be displayed and how
 */
public class DiscountMenu extends JMenu implements ActionListener {
    private static final Logger m_log = Logger.getLogger( DiscountMenu.class );
    private If i;
    private ButtonGroup group;
    private static final String HIDE = "hide";
    private static final String SHOW_DISC = "show_disc";
    private static final String SHOW_UNDIC = "show_undisc";
    
    public DiscountMenu(final Tarification t,final Simulator s) {
        super(Lang.translate("Discount"));
        setIcon(Resources.reductionButton);
        
        i = new If () {
            public Tarification t() { return t;}
            public CompactExplorer ce() { return s.simExplorer();}
            public STreeTable stt() { return s.treeTable();}};
        
        group = new ButtonGroup();
        myAdd("Hide discount informations",
                ! t.comCalc().getWithoutDiscounts(),HIDE);
        myAdd("Show discounts",
                t.comCalc().getWithoutDiscounts(),SHOW_DISC);
        myAdd("Show un-discounted fees",
                t.comCalc().getWithoutDiscounts(),SHOW_UNDIC);
        
    }
    
    public interface If {
        public CompactExplorer ce();
        public STreeTable stt();
        public Tarification t();
    }
    
    private void myAdd(String title,boolean isSelected,String action) {
        JRadioButtonMenuItem jrb=
            new JRadioButtonMenuItem(Lang.translate(title));
        jrb.setSelected(isSelected);
        group.add(jrb);
        add(jrb);
        
        jrb.setActionCommand(action);
        jrb.addActionListener(this);
    }
    
    
    public void actionPerformed(ActionEvent e) {
        boolean showColumn = false;
        String columnName = "";
        if (e.getActionCommand().equals(SHOW_DISC)) {
            i.ce().setDiscountOrUndisc(true);
            showColumn = true;
            columnName = "Discount";
        }
        if (e.getActionCommand().equals(SHOW_UNDIC)) {
            i.ce().setDiscountOrUndisc(false);
            showColumn = true;
            columnName = "Un-Discounted";
        }
        
        
        //	change ComCalc State
        i.t().comCalc().setWithoutDiscounts(showColumn);
        
        // refresh the calculus
        if (showColumn) {i.ce().refreshCalculus();}        
        
        int colCount = i.stt().getTable().getColumnCount();
       if (showColumn && (colCount < 2)) { // add column
            i.stt().addColumn();
        }
       
       if (showColumn) {
           i.stt().setColumnName(1,Lang.translate(columnName));
       }
       
        if ((! showColumn) && (colCount > 1)) { // del column
            i.stt().removeColumn(1);
        }
        i.stt().getTable().repaint();
    }
    
}
