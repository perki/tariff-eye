/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: DispatcherSimplePanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
*/
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSimple;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
/**
* Dispatcher Simple UI
*/
public class DispatcherSimplePanel extends WorkSheetPanel {
	/**
	 * overwrite this to set a default Class Icon
	 */
	public static ImageIcon defaultClassTreeIcon
		 = Resources.wsDispatcherSimple;
		 
	private DispatcherSimple ds;
	
	
	private JPanel sonPanel ; 
	
	public DispatcherSimplePanel(DispatcherSimple ds,TarifViewer tv) {
		super(ds,tv);
		this.ds = ds;
		sonPanel = new JPanel();
		refresh();
	}
	
	/**
	* refresh the display
	*/
	public void refresh() {
		sonPanel.removeAll();
		sonPanel.setLayout(new BorderLayout());
		sonPanel.setSize(WorkSheetPanel.defaultContentDim);

		// fix a WorkSheetExplorer with the Panel inside
		sonPanel.add(
		        getDisplayController().getWorkSheetPanel(
		                ds.getWorkSheet()).getPanel(),
				BorderLayout.CENTER
		);
	}
	
	/**
	* implementation of @see WorkSheetPanel#save();
	*/
	public void save() {
		// nothing to save yet ?
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	
	/**
	 */
	public JPanel getOptionPanel() {
		return super.getStandardOptionViewer();
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		return sonPanel;
	}
}
/*
* $Log: DispatcherSimplePanel.java,v $
* Revision 1.2  2007/04/02 17:04:24  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.23  2004/11/11 10:57:44  perki
* Intro to new Dispachers design
*
* Revision 1.22  2004/09/07 13:35:03  carlito
* *** empty log message ***
*
* Revision 1.21  2004/05/22 18:33:22  perki
* *** empty log message ***
*
* Revision 1.20  2004/05/06 07:06:25  perki
* WorkSheetPanel has now two new methods
*
* Revision 1.19  2004/04/09 07:16:52  perki
* Lot of cleaning
*
* Revision 1.18  2004/03/23 13:39:19  perki
* New WorkSHeet Panel model
*
* Revision 1.17  2004/03/08 17:53:18  perki
* *** empty log message ***
*
* Revision 1.16  2004/02/20 05:45:05  perki
* appris un truc
*
* Revision 1.15  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.14  2004/02/19 16:21:25  perki
* Tango Bravo
*
* Revision 1.13  2004/02/17 15:55:02  perki
* zobi la mouche n'a pas de bouche
*
* Revision 1.12  2004/02/17 08:54:07  perki
* zibouw
*
* Revision 1.11  2004/02/17 08:50:22  perki
* zibouw
*
* Revision 1.10  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.9  2004/02/16 13:07:53  perki
* new event model
*
* Revision 1.8  2004/02/06 08:05:41  perki
* lot of cleaning in UIs
*
* Revision 1.7  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.6  2004/01/20 15:28:45  perki
* got milk?
*
* Revision 1.5  2004/01/20 14:06:53  perki
* Et au fond du noir, le noir le plus profond une limiere ..
*
* Revision 1.4  2004/01/20 12:35:27  perki
* Prosper YOupla boum. c'est le roi du pain d'epice
*
* Revision 1.3  2004/01/20 11:05:23  perki
* Et la comete disparue dans l'espace infini.. Fin
*
* Revision 1.2  2004/01/20 10:29:59  perki
* The Dispatcher Force be with you my son
*
* Revision 1.1  2004/01/19 19:22:20  perki
* Goldorak Go
*
*/
