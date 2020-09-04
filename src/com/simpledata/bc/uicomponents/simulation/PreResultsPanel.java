/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 mars 2004
 * $Id: PreResultsPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.simulation;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.WorkSheet;

/**
 * This class is meant to display local results corresponding to
 * the currently seen Tarif (ie workSheet) in the SimuTarifViewer
 */
public class PreResultsPanel extends JPanel {
	
	private WorkSheet currentWorkSheet;
	
	// Graphical objects
	private JScrollPane textScroll;
	// CTD3 replace textArea by a more customizable text component (ie HTML area)
	private JTextArea textArea;
	// End of graphical objects
	
	public PreResultsPanel() {
		super();
		
		this.setLayout(new BorderLayout());
		TitledBorder tb = new TitledBorder("Tarif Description");
		BC.langManager.register(tb, "WorkSheet Description");
		this.setBorder(tb);
		
		this.textScroll = new JScrollPane();
		this.textArea = new JTextArea();
		this.textScroll.setViewportView(this.textArea);
		
		this.add(this.textScroll, BorderLayout.CENTER);

		Dimension d = new Dimension(550, 100);
		setPreferredSize(d);
	}
	
	public void setWorkSheet(WorkSheet ws) {
		if (ws == this.currentWorkSheet) {
			return;
		}
		this.currentWorkSheet = ws;
		
		this.textArea.setText("");
		
		String wsTitle = "";
		
		if (ws != null) {
			//wsTitle = "WorkSheet : "+ws.getTitle();
			wsTitle = ws.getDescription();
		}
		
		String res = wsTitle + "\n";
		this.textArea.setText(res);
	}
	
}
/*
 * $Log: PreResultsPanel.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.5  2004/05/31 16:14:45  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/03/22 14:32:30  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/03/18 09:30:15  carlito
 * *** empty log message ***
 *
 */