/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Package that contains all the different panels for displaying
 * any given Workplace. 
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import java.awt.BorderLayout;

import javax.swing.*;

import com.simpledata.bc.*;
import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFixedFee;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.tools.MoneyEditor;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;




/**
 * Panel for WorkPlaceFixedFee
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: WorkPlaceFixedFeePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class WorkPlaceFixedFeePanel extends WorkSheetPanel {
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon 
		= Resources.wsWorkPlaceFixedFee;
			
	//WorkPlaceFixedFee wpff;
	
	private JPanel jp;
	
	
	public WorkPlaceFixedFeePanel(WorkPlaceFixedFee wpff, TarifViewer tv) {
		super(wpff, tv);
		//this.wpff = wpff;
		initialize();
	}

	/**
	 * Get associated option
	 */
	private WorkPlaceFixedFee getWorkPlaceFixedFee() {
	    WorkSheet ws = this.getWorkSheet();
	    return (WorkPlaceFixedFee)(ws);
	}
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
	    jp = new JPanel();
	    jp.setLayout(new BorderLayout(0,0));
	    JLabel jb = new JLabel("Fixed amount that will be charged : ");
	    BC.langManager.register(jb, "Fixed Amount Description");
	    jp.add(jb, BorderLayout.NORTH);
	    jp.add(getMoneyEditor(), BorderLayout.CENTER); 	    
	}
	
	private MoneyEditor moneyEditor;
	public MoneyEditor getMoneyEditor() {
		if (moneyEditor == null) {
			moneyEditor = new MoneyEditor(getWorkPlaceFixedFee().getMyFee()) {
				public void stopEdit() {
					// need to fire the changes to the workplace
				    getWorkPlaceFixedFee().optionDataChanged(null,null);
				}
				public void startEdit() {
					// nothing to do
				}
			};
		}
		return moneyEditor;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
	 */
	public void save() {
		// auto

	}

	

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {
		switch (getDisplayController().getEditWorkPlaceState()) {
			case WSIf.EDIT_STATE_NONE:
				getMoneyEditor().setEditable(false);
				break;
			default:
				getMoneyEditor().setEditable(true);
				break;
			
		}
	}
	
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	
	/**
	 */
	public JPanel getOptionPanel() {
		return null;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		return jp;
	}
}

/*
 * $Log: WorkPlaceFixedFeePanel.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.7  2004/09/14 10:21:41  perki
 * Futures step2
 *
 * Revision 1.6  2004/09/07 13:35:03  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/08/02 10:17:43  carlito
 * new fixed fee panel
 *
 * Revision 1.4  2004/07/26 20:36:10  kaspar
 * + trRateBySlice subreport that shows for all
 *   RateBySlice Workplaces. First Workplace subreport.
 * + Code comments in a lot of classes. Beautifying, moving
 *   of $Id: WorkPlaceFixedFeePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
 * + Long promised caching of reports, plus some rudimentary
 *   progress tracking.
 *
 * Revision 1.3  2004/05/31 07:19:47  perki
 * Enable and disable
 *
 * Revision 1.2  2004/05/06 07:06:25  perki
 * WorkSheetPanel has now two new methods
 *
 * Revision 1.1  2004/05/05 12:38:51  perki
 * Plus FixedFee panel
 *
 */