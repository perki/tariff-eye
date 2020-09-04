/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: WorkPlaceRateBySliceOnAmountPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateBySliceOnAmount;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionsViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.RateBySlicePanel;

/**
 * Panel for WorkPlaceRateBySliceOnAmount
 */
public class WorkPlaceRateBySliceOnAmountPanel 
	extends WorkSheetPanel 
	implements RateBySlicePanel.RBSPListener {
    /** my Icon **/
	public static ImageIcon defaultClassTreeIcon 
		= Resources.wsWorkPlaceTrRateBySlice;
   
	
	private final WorkPlaceRateBySliceOnAmount wprbsoa;
	
	
    public WorkPlaceRateBySliceOnAmountPanel(
            WorkPlaceRateBySliceOnAmount ws, TarifViewer tv) {
        super(ws, tv);
       
        wprbsoa = ws;
        initialize();
    }
    
    
	
	/** state memory tells if we are in edit mode or not **/
	private int stateMemory;
	/**
	 * This method initializes this
	 */
	private void initialize() {
		getContents().setLayout(new BorderLayout());  // Generated
		getContents().add(getRbsp(),BorderLayout.CENTER);
		
		stateMemory = -1;
		refresh();
	}
    
    private RateBySlicePanel rbsp;
    /**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private RateBySlicePanel getRbsp() {
		if(rbsp == null) {
			rbsp = new RateBySlicePanel(wprbsoa.getRbs(),this);
			rbsp.setMinimumSize(new Dimension(100,50));
			rbsp.setPreferredSize(new Dimension(100,50)); 
		}
		return rbsp;
	}
    

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {
		
		if (stateMemory == getDisplayController().getEditWorkPlaceState()) 
		    return;
		stateMemory = getDisplayController().getEditWorkPlaceState();
		
		switch (stateMemory) {
			case WSIf.EDIT_STATE_NONE:
				getRbsp().setEditable(false);
				break;
			default:
				getRbsp().setEditable(true);
				break;
			
		}
		
	}
	
  
   private JPanel jp;
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		if (jp == null) jp = new JPanel();
		return jp;
	}

    OptionsViewer ov;
    /**
     * @see WorkSheetPanel#getOptionPanel()
     */
    public JPanel getOptionPanel() {
        if (ov == null) {
            ov = getStandardOptionViewer();
        }
        return ov;
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
     */
    public void save() {
        // TODO Auto-generated method stub

    }

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}

  
	/**
	 * interface to RateBySlicePanel.RBSPListener <BR>
	 * called when data is modified on the panel
	 */
	public void rbsDataChanged() {
		wprbsoa.optionDataChanged(null,null);
	}

}
