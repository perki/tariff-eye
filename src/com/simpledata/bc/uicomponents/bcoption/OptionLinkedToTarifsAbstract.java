/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionLinkedToTarifsAbstract.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption;

import java.awt.Point;
import java.util.Iterator;

import javax.swing.JLabel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.tools.LinkToTarifs;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.tools.LinkToTarifsPanel;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SButtonIcon;

/**
 * Abstract class for options linked to tarifs
 */
public abstract class OptionLinkedToTarifsAbstract extends OptionDefaultPanel {
    	/** width of the SUM SIGN **/
    	public static int SUM_W= 30;
    	/** width of the TARIF NAME LABEL **/
    	public static int TARIF_NAME_W = 100;
    	
    	/** width of the TarifCHooser */
    	public static int TARIF_CHOOSER_W= 250;
    	/** height of the TarifCHooser */
    	public static int TARIF_CHOOSER_H= 600;
    	
    	
    	private LinkToTarifs optionMAU;

    	private JLabel tarifName= null;
    	private SButtonIcon changeTarifButton= null;
    	
    	/**
    	 * @param option is a LinkToTarifs
    	 */
    	protected OptionLinkedToTarifsAbstract(
    	        OptionDefaultPanel.EditStates editStateController,
    	        BCOption option) {
    		super(editStateController,option);
    		
    		assert option instanceof LinkToTarifs;
    		
    		optionMAU = (LinkToTarifs) option;
    		initialize();
    	}

    	/**
    	 * This method initializes this
    	 * 
    	 */
    	private final void initialize() {
    		this.add(getStatus(), null);
    		this.add(getTitleTextField(), null); 
    		this.add(getEqualLabel(), null); 
    		this.add(getSumLabel(), null); 
    		this.add(getTarifName(), null); 
    		this.add(getChangeTarifButton(), null); 
    		refresh();
    	}

    	

    	/**
    	 * Save current Data
    	 */
    	public final void save() {
    		refresh(); // refresh with current values
    	}

    	/**
    	 * Show a Tarif Chooser
    	 *
    	 */
    	public final void showTarifChooser() {
    		LinkToTarifsPanel lttp= new LinkToTarifsPanel(optionMAU);
    		setDim(lttp,TARIF_CHOOSER_W, TARIF_CHOOSER_H);
    		lttp.setEditable(getEditState() == EditStates.FULL);
    		 ModalJPanel.createSimpleModalJInternalFrame(
    			lttp,
    			changeTarifButton,
    			new Point(20, -50),
    			true,
    			Resources.iconSelect,Resources.modalBgColor);
    	}

    	/**
    	 * Load current Data
    	 */
    	public final void refresh() {
    		
    		String temp= optionMAU.getLinkedTarifs().size() 
    			+ " " + Lang.translate("Tarif");
    		Iterator e = optionMAU.getLinkedTarifs().iterator();
    		while (e.hasNext()) {
    			temp = temp + ":"+e.next();
    		}
    		
    		
    		tarifName.setText(temp);
    		setStatus(true);
    	}
    	
    	private JLabel sumLabel;
    	/**
    	 * This method initializes sumLabel
    	 * 
    	 * @return javax.swing.JLabel
    	 */
    	private JLabel getSumLabel() {
    		if (sumLabel == null) {

    			sumLabel= new javax.swing.JLabel(); 
    			sumLabel.setIcon(Resources.iconSum);
    			setDim(sumLabel,SUM_W, DEF_COMPONENT_H);
    		}
    		return sumLabel;
    	}
    	
    	/**
    	 * This method initializes tarifName
    	 * 
    	 * @return javax.swing.JLabel
    	 */
    	private final JLabel getTarifName() {
    		if (tarifName == null) {
    			tarifName= new JLabel(); 
    			tarifName.setText("the tarifName");
    			setDim(tarifName,TARIF_NAME_W, DEF_COMPONENT_H);
    		}
    		return tarifName;
    	}
    	
    	/**
    	 * This method initializes changeTarifButton
    	 * 
    	 * @return SButton
    	 */
    	private final SButtonIcon getChangeTarifButton() {
    		if (changeTarifButton == null) {
    			changeTarifButton= new SButtonIcon(); 
    			changeTarifButton.setIcon(Resources.iconEdit);
    			changeTarifButton.setPreferredSize(
    			        new java.awt.Dimension(DEF_ICON_W, DEF_COMPONENT_H));
    			changeTarifButton.setToolTipText(Lang.translate("change tarif"));
    			changeTarifButton
    				.addActionListener(new java.awt.event.ActionListener() {
    				public void actionPerformed(java.awt.event.ActionEvent e) {
    					showTarifChooser();
    				}
    			});
    		}
    		return changeTarifButton;
    	}
   
    
}
