/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: NullWorkSheetPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.simpledata.bc.components.worksheet.workplace.NullWorkSheet;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;

/**
 * @author SD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NullWorkSheetPanel extends WorkSheetPanel {

    /**
     * @param ws
     * @param tv
     */
    public NullWorkSheetPanel(NullWorkSheet ws, TarifViewer tv) {
        super(ws, tv);
    }

    /**
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
     */
    public JPanel getContents() {
        return new JPanel();
    }

    /**
     * @see WorkSheetPanel#getOptionPanel()
     */
    public JPanel getOptionPanel() {
        return null;
    }

    /**
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
     */
    public void save() {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getTreeIcon()
     */
    protected ImageIcon getTreeIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /* *
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
     */
    public void refresh() {
        // TODO Auto-generated method stub

    }

}
