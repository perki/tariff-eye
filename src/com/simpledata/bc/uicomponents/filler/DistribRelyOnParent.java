/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: DistribRelyOnParent.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.money.Money;

/**
 * This method just forward its value to the first available parent.
 * This won't work on node that are root.
 */
public class DistribRelyOnParent 
	implements DistributionMethod , DistributionMethod.Typeable{
	private FillerNode owner;
	
	private Class myType;
	
	public DistribRelyOnParent(FillerNode fn) {
		owner = fn;
	}
	
	public void setType(Class type) {
		myType = type;
	}
	
	public boolean methodForward() {
		return true;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.filler.DistributionMethod#getOwner()
	 */
	public FillerNode getOwner() {
		return owner;
	}

	/**
	 * @see DistributionMethod
	 * 	#distribute(Money, AssetsRoot0, DistributionMonitor)
	 */
	public void distribute(Money m, Dispatcher ws, DistributionMonitor dm) {
		assert myType != null;
		
		assert owner.getParent() != null : 
			"I cannot forward If I'm attached to a root node";
		
		owner.getParent().getDistributionMethod(
				myType
		).distribute(m, ws,dm);
	}

	//------------------------ UI ------------------------//
	
	JPanel ui ;
	JLabel infos;
	/**
	 * the panel with the informations
	 */
	public JPanel getUI() {
		if (ui != null) return ui;
		
		ui = new JPanel(new FlowLayout(FlowLayout.LEADING));
		refresh();
		ui.add(infos);
		
		
		
		return ui;
	}
	
	/**
	 * Refresh the UI
	 */
	public void refreshUI() {
	    refresh();
	};
	

	/** adapt the text information **/
	public void refresh() {
		if (infos == null) infos = new JLabel();
		infos.setText(getSummary());
	}
	
	
	/**
	 * Return Parent Summary
	 */
	public String getSummary() {
		assert owner.getParent() != null : 
			"I cannot forward If I'm attached to a root node";
		assert myType != null;
		return owner.getParent().getDistributionMethod(
				myType).getSummary();
	}
	
	
	/**
	 * get the number of options the will be created from this node
	 */
	public int getCost(NodeInfo start) {
		assert owner.getParent() != null : 
			"I cannot forward If I'm attached to a root node";
		assert myType != null;
		return owner.getParent().getDistributionMethod(
				myType
		).getCost(start);
	}
}
/*
 * $Log: DistribRelyOnParent.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/10/11 10:19:16  perki
 * Percentage on Transactions
 *
 * Revision 1.7  2004/08/27 11:24:53  kaspar
 * ! Moved all inner classes out of FillerData, this creates a more
 *   lisible design
 *
 * Revision 1.6  2004/08/27 10:02:09  kaspar
 * ! Refactor: Put DistributionMonitor in its own file
 *
 * Revision 1.5  2004/08/24 14:12:29  kaspar
 * ! Commentary correction
 * ! Line endings changed.
 *
 * Revision 1.4  2004/08/02 14:17:11  perki
 * Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 * Revision 1.3  2004/08/02 08:32:36  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/07/31 11:06:55  perki
 * Still have problems with the progressbar
 *
 * Revision 1.1  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 */
