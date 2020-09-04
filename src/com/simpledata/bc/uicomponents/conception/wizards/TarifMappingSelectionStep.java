/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 16 f�vr. 2004
 *
 * $Id: TarifMappingSelectionStep.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception.wizards;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.uicomponents.*;

/**
 * Defines the step panel for choosing new tarif's mapping
 */
public class TarifMappingSelectionStep extends StepPanel implements TreeSelectionListener {
	
	private Tarification tarification = null;
	private TarificationExplorerTree tet = null;
	private BCNode currentNode = null;

	public TarifMappingSelectionStep(NewTarifWizard ntw, Tarification t) {
		super(ntw);
		this.tarification = t;
		initComponents();
	}
	
	private void initComponents() {
		this.tet = new TarificationExplorerTree(this.tarification.getTreeBase(),false,false,null);
		this.tet.addTreeSelectionListener(this);
		this.tet.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doCancel()
	 */
	public void doCancel() {
		// Nothing saved --> nothing to do
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doBack()
	 */
	public void doBack() {
		// Should not be accessed in first step
	}

	/**
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doNext()
	 */
	public void doNext() {	
		this.owner().setUserObject("node", this.currentNode);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doFinish()
	 */
	public void doFinish() {
		// Not accessible from here
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#getDisplay()
	 */
	public JComponent getDisplay() {
		return this.tet;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#getStepTitle()
	 */
	public String getStepTitle() {
		return new String("STEP 1. Select tarif position");
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#getStepDescription()
	 */
	public String getStepDescription() {
		return new String("Select the node you wish the tarif to be linked on, and press Next");
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doButtonEnabling()
	 */
	public void doButtonEnabling() {
		this.owner().setCancel(true);
		this.owner().setBack(false);
		if (this.currentNode == null) { 
			this.owner().setNext(false);
		} else {
			this.owner().setNext(true);
		}
		this.owner().setFinish(false);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#refreshState()
	 */
	public void refreshState() {
		this.currentNode = (BCNode) this.owner().getUserObject("node");
		if (this.currentNode != null) {
			TarificationExplorerNode tn = tet.getTEN(this.currentNode);
			if (tn != null)
				this.tet.selectNode(tn);
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent event) {
		TreePath tp = event.getNewLeadSelectionPath();
		if (tp != null) {
			// We have an active selection
			setCurrentNode((TarificationExplorerNode)tp.getLastPathComponent());
		} else {
			setCurrentNode(null);
		}
		
	}

	/** change the currently selected node **/
	private void setCurrentNode(TarificationExplorerNode ten) {
		if (ten != null) {
		this.currentNode = ten.getBCNode();
		} else {
			this.currentNode = null;
		}
		this.owner().setNext(this.currentNode != null);
	}

	/**
	 * should be called from the outside to simulate a select
	 */
	/*
	public void outSelectBCNode(BCNode node) {
		tet.selectNode(tet.getTEN(node));
	}
	*/

}

/*
 * $Log: TarifMappingSelectionStep.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/03/08 16:43:48  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/03/08 14:22:22  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/03/08 11:11:36  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/03/06 15:22:41  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.4  2004/03/06 14:24:50  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.3  2004/02/22 18:09:20  perki
 * good night
 *
 * Revision 1.2  2004/02/17 18:03:17  carlito
 * *** empty log message ***
 *
 */
