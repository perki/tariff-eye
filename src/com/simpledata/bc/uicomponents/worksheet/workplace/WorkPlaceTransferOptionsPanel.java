/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/** 
 * @version $Id: WorkPlaceTransferOptionsPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
 
/**
 * Package that contains all the different panels for displaying
 * any given Workplace. 
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import java.util.Iterator;
import java.util.ArrayList;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;

/**
 * UI for WorkPlaceTransferOptions
 */
public class WorkPlaceTransferOptionsPanel 
			extends WorkSheetPanel 
			implements NamedEventListener {
	
	WorkPlaceTransferOptions wptso;
	
	/** panel **/
	JPanel panel;
	/** the output to the user **/
	JLabel infoLabel;
	
	/** the text area with my informations **/
	
	
	public WorkPlaceTransferOptionsPanel
					(WorkPlaceTransferOptions ws, TarifViewer tv) {
		super(ws, tv);
		wptso = ws;
		
		// add an event listener for tarifMapping changes
		ws.getTarification().addNamedEventListener(
				this,NamedEvent.TARIF_MAPPING_MODIFIED,null);
	}

	/**
	 * @see WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		if (panel != null) return panel;
		panel = new JPanel();
		infoLabel = new JLabel();
		panel.add(infoLabel);
		refresh();
		return panel;
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
		// nada
	}

	/**
	 * @see WorkSheetPanel#getTreeIcon()
	 */
	protected ImageIcon getTreeIcon() {
		// TODO create an icon 
		return Resources.wsWorkPlace;
	}

	/**
	 * change the text on the infoLabel
	 */
	public void refresh() {
		StringBuffer sb = new StringBuffer("<HTML>");
		// get the parent tarifs
		ArrayList/*<Tarif>*/ tarifs = wptso.getParentTarif();
		
		// tell if this worksheet is valid or not
		if (! wptso.isValid()) {
			sb.append("<B>").
			append(Lang.translate("INVALID")).append("</B><BR>");
			
			if (tarifs.size() == 0) {
				sb.append(Lang.translate("No parent tarif"));
			} else if (tarifs.size() > 1){
				sb.append(Lang.translate("More than one parent tarif found"));
			}
			sb.append("<BR>");
		} else {
			sb.append("<B>").
			append(Lang.translate("Values are reported to tarif(s):"))
			.append("</B><BR>");
		}
		
		// display the list of detected parents
		Iterator/*<Tarif>*/ i = tarifs.iterator();
		while (i.hasNext()) {
			sb.append(" - ").append((i.next())).append("<BR>");
		}
		
		sb.append("</HTML>");
		infoLabel.setText(sb.toString());
	}

	/**
	 * refresh on tarif mapping changes
	 */
	public void eventOccured(NamedEvent e) {
		refresh();
	}
}
/*
* $Log: WorkPlaceTransferOptionsPanel.java,v $
* Revision 1.2  2007/04/02 17:04:26  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:41  perki
* First commit on sourceforge
*
* Revision 1.7  2004/11/12 14:28:39  jvaucher
* New NamedEvent framework. New bugs ?
*
* Revision 1.6  2004/10/11 17:48:08  perki
* Bobby
*
* Revision 1.5  2004/07/26 20:36:10  kaspar
* + trRateBySlice subreport that shows for all
*   RateBySlice Workplaces. First Workplace subreport.
* + Code comments in a lot of classes. Beautifying, moving
*   of $Id: WorkPlaceTransferOptionsPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
* + Long promised caching of reports, plus some rudimentary
*   progress tracking.
*
* Revision 1.4  2004/07/08 14:59:00  perki
* Vectors to ArrayList
*
* Revision 1.3  2004/07/04 14:54:53  perki
* *** empty log message ***
*
* Revision 1.2  2004/07/04 11:23:39  perki
* temp unstable state
*
* Revision 1.1  2004/07/04 10:58:37  perki
* *** empty log message ***
*
*/