/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 13 sept. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFutFeeBySlice;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.FeeBySlicePanel;
import com.simpledata.bc.uitools.ImageTools;

/**
 * @author SD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkPlaceFutFeeBySlicePanel extends WorkSheetPanel 
	implements AbstractBySlicePanel.RBSPListener  {
    
    /** my Icon **/
    public static ImageIcon defaultClassTreeIcon 
    = Resources.wsWorkPlaceTrRateBySlice;
    
    
    WorkPlaceFutFeeBySlice wpffbs;
    private FeeBySlicePanel rbsp = null;
    
    private JPanel jp;
  
    private JComboBox applyChooser = null;
    
    
    public WorkPlaceFutFeeBySlicePanel(WorkPlaceFutFeeBySlice wptrbs, 
            TarifViewer tv) {
        super(wptrbs, tv);
        this.wpffbs = wptrbs;
        
       initialize();
    }
    
    /** state memory tells if we are in edit mode or not **/
	private int stateMemory;
    /**
	 * This method initializes this
	 */
	private void initialize() {
		
		jp = new JPanel();
		jp.setLayout(new BorderLayout(0,0));
		jp.add(getApplyChooser(), BorderLayout.NORTH);
        jp.add(getFbsp(), BorderLayout.CENTER); 
        
        stateMemory = -1;
        refresh();
			
	}
	
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private FeeBySlicePanel getFbsp() {
		if(rbsp == null) {
			rbsp = new FeeBySlicePanel(wpffbs.getFbs(),this);
			rbsp.setMinimumSize(new Dimension(0,50));
			rbsp.setPreferredSize(new Dimension(100,50)); 
		}
		return rbsp;
	}
	
	
	public ImageIcon getTreeIcon() {
	    return ImageTools.drawIconOnIcon(
				defaultClassTreeIcon,
				getIconFor(wpffbs.getApplyOn()),
				new Point(0,0)
				);
	}
    
    
    /**
     * @see WorkSheetPanel#getContents()
     */
    public JPanel getContents() {
        return jp;
    }
    
    /**
     * @see WorkSheetPanel#getOptionPanel()
     */
    public JPanel getOptionPanel() {
        return null;
    }
    
    /**
     * @see WorkSheetPanel#save()
     */
    public void save() {
        wpffbs.setApplyOn(
    			((FApplyOption) applyChooser.getSelectedItem()).applyType
    		);
    }
    
 
    
    /**
     * @see WorkSheetPanel#refresh()
     */
    public void refresh() {
        if (stateMemory == getDisplayController().getEditWorkPlaceState()) 
            return;
        stateMemory = getDisplayController().getEditWorkPlaceState();
        
        switch (stateMemory) {
	        case WSIf.EDIT_STATE_NONE:
	            getFbsp().setEditable(false);
	        getApplyChooser().setEnabled(false);
	        break;
	        default:
	            getFbsp().setEditable(true);
	        getApplyChooser().setEnabled(true);
	        break;
        }
        
    }
 
	/**
	 * return the applies on Combo
	 */
	private JComboBox getApplyChooser() {
		if (applyChooser == null) {
			FApplyOption[] aos = new FApplyOption[3];
			aos[0] = new FApplyOption(
			        WorkPlaceFutFeeBySlice.APPLY_ON_OPENING_AND_CLOSEING);
			aos[1] = new FApplyOption(
			        WorkPlaceFutFeeBySlice.APPLY_ON_OPENING);
			aos[2] = new FApplyOption(
			        WorkPlaceFutFeeBySlice.APPLY_ON_CLOSEING);
			
			applyChooser = new JComboBox(aos);
			applyChooser.setRenderer(new FCBR());
			applyChooser.setSelectedItem(
			        new FApplyOption(wpffbs.getApplyOn()));
			
			applyChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					save();
				}});
			
			
		}
		return applyChooser;
	}

    /**
     * @see AbstractBySlicePanel.RBSPListener#rbsDataChanged()
     */
    public void rbsDataChanged() {
        wpffbs.optionDataChanged(null,null);
    }
    
    
    //  class CBR extends BasicComboBoxRenderer {
	class FCBR implements ListCellRenderer {
	    
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus) {
	        
	        FApplyOption ao = (FApplyOption) value;
	        
	        JPanel jp = (ao).getComponent();
	        
	        if (isSelected) {
	            jp.setBackground(list.getSelectionBackground());
	            jp.setForeground(list.getSelectionForeground());
	        } else {
	            jp.setBackground(list.getBackground());
	            jp.setForeground(list.getForeground());
	        }
	        return jp;
	    }
	    
	    
	}
    
    
    /** A class that encapsulates the Drop Down select listbox behaviour. 
	 */
	class FApplyOption {
		public int applyType;
		JPanel jp ;
		
		public JPanel getComponent() {
			return jp;
		}
		
		public FApplyOption(int applyType) {
			this.applyType = applyType;
			String txt = Lang.translate("Unkown type");
			switch (applyType) {
				case WorkPlaceFutFeeBySlice.APPLY_ON_OPENING_AND_CLOSEING:
					txt =  Lang.translate("Applies on all futures");
				break;
				case WorkPlaceFutFeeBySlice.APPLY_ON_OPENING:
					txt =  Lang.translate("Applies on opening");
				break;
				case WorkPlaceFutFeeBySlice.APPLY_ON_CLOSEING:
					txt =  Lang.translate("Applies on closeing");
				break;
			}
			
			
			final JLabel jl = new JLabel(txt, 
			        getIconFor(applyType), SwingConstants.LEFT);
			jp = new JPanel(new BorderLayout(0,0)) {
			    	
			    public void setBackground(Color col) {
			        super.setBackground(col);
			        if (jl != null)
			            jl.setBackground(col);
			    }
			    
			    public void setForeground(Color col) {
			        super.setForeground(col);
			        if (jl != null)
			            jl.setForeground(col);
			    }
			    
			};
			jp.add(jl,BorderLayout.CENTER);
			
		}
		
		public boolean equals(Object o) {
			if (! (o instanceof FApplyOption)) return false;
			return ((FApplyOption) o).applyType == applyType;
		}
	}
	
	/**
	 * get the type icon
	 */
	public static ImageIcon getIconFor(int applyType) {
		switch (applyType) {
			case WorkPlaceFutFeeBySlice.APPLY_ON_OPENING_AND_CLOSEING:
				return Resources.futureOpenClose;
			case WorkPlaceFutFeeBySlice.APPLY_ON_OPENING:
				return Resources.futureOpen;
			case WorkPlaceFutFeeBySlice.APPLY_ON_CLOSEING:
				return Resources.futureClose;
		}
		return null;
	}
}
