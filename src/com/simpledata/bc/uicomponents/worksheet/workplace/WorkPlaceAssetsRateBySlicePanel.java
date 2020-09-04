/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: WorkPlaceAssetsRateBySlicePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
 
/**
 * Package that contains all the different panels for displaying
 * any given Workplace. 
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;



import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceAssetsRateBySlice;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.RateBySlicePanel;
/**
 * UI for WorkPlaceAssetsRateBySlice
 * @see RateBySlicePanel;
 */
public class WorkPlaceAssetsRateBySlicePanel
	extends WorkSheetPanel
	implements AbstractBySlicePanel.RBSPListener 
{
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon 
		= Resources.wsWorkPlaceTrRateBySlice;
	
	WorkPlaceAssetsRateBySlice wpasrbs;
	private RateBySlicePanel rbsp = null;
	
	private JPanel topPanel = null;
	private JLabel appliesOnLabel = null;
	private JComboBox modeChooser = null;
	
	
	public WorkPlaceAssetsRateBySlicePanel(WorkPlaceAssetsRateBySlice wpasrbs,
	        TarifViewer tv) {
		super(wpasrbs, tv);
		this.wpasrbs = wpasrbs;
	
		initialize();
	}
	
	/** state memory tells if we are in edit mode or not **/
	private int stateMemory;
	/**
	 * This method initializes this
	 */
	private void initialize() {
		getContents().setLayout(new BorderLayout());  // Generated
		getContents().add(getTopPanel(), BorderLayout.NORTH);  // Generated
		getContents().add(getRbsp(),BorderLayout.CENTER);
		
		stateMemory = -1;
		refresh();
	}
	
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private RateBySlicePanel getRbsp() {
		if(rbsp == null) {
			rbsp = new RateBySlicePanel(wpasrbs.getRbs(),this);
			rbsp.setMinimumSize(new Dimension(100,50));
			rbsp.setPreferredSize(new Dimension(100,50)); 
		}
		return rbsp;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
	 */
	public void save() {
		// auto saving

	}

	/**
	 * @see WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
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
				getModeChooser().setEnabled(false);
				break;
			default:
				getRbsp().setEditable(true);
				getModeChooser().setEnabled(true);
				break;
			
		}
		
	}

	/**
	 * This method initializes topPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getTopPanel() {
		if(topPanel == null) {
			topPanel = new javax.swing.JPanel();  // Generated
			java.awt.FlowLayout layFlowLayout1 = new java.awt.FlowLayout();  // Generated
			layFlowLayout1.setAlignment(java.awt.FlowLayout.LEFT);  // Generated
			topPanel.setLayout(layFlowLayout1);  // Generated
			topPanel.add(getAppliesOnLabel(), null);  // Generated
			topPanel.add(getModeChooser(), null);  // Generated
		}
		return topPanel;
	}
	/**
	 * This method initializes appliesOnLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getAppliesOnLabel() {
		if(appliesOnLabel == null) {
			appliesOnLabel = new javax.swing.JLabel();  // Generated
			appliesOnLabel.setText(Lang.translate("Rate applies:"));  // Generated
		}
		return appliesOnLabel;
	}
	/**
	 * This method initializes modeChooser
	 * 
	 * @return javax.swing.JComboBox
	 */
	private javax.swing.JComboBox getModeChooser() {
		if(modeChooser == null) {
			String[] texts = new String[] {
				Lang.translate("Per amount"),
				Lang.translate("On sum of amounts")
			};
			modeChooser = new javax.swing.JComboBox(texts); 
			int selectIndex = wpasrbs.getPerAmount().booleanValue() ? 0 : 1;
			modeChooser.setSelectedIndex(selectIndex);
			modeChooser.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					wpasrbs.setPerAmount(new Boolean(modeChooser.getSelectedIndex() == 0));
					wpasrbs.optionDataChanged(null,null); // fire data changed
				}
			});
		}
		return modeChooser;
	}
	

	/**
	 */
	public JPanel getOptionPanel() {
		return null;
	}
	
	private JPanel jp;
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		if (jp == null) jp = new JPanel();
		return jp;
	}


	/**
	 * interface to RateBySlicePanel.RBSPListener <BR>
	 * called when data is modified on the panel
	 */
	public void rbsDataChanged() {
		wpasrbs.optionDataChanged(null,null);
	}
	
} 

