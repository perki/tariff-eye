/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarificationPublishingPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;



import java.awt.*;
import java.awt.event.*;
import java.util.Date;

import javax.swing.*;

import com.simpledata.bc.*;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.*;

/**
 * This class can either pop the properties panel or the publish dialog.
 */
public abstract class TarificationPublishingPanel extends JPanel{
	
	final TarificationPropertiesPanel m_tpp;
	
    private TarificationPublishingPanel(final Tarification t) {
 
        panelBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonClose = new SButton();
        buttonPublish = new SButton();
        
        
        setLayout(new java.awt.BorderLayout());

        m_tpp = new TarificationPropertiesPanel(t);
        add(m_tpp, java.awt.BorderLayout.CENTER);

        buttonClose.setText(Lang.translate("Done"));
        buttonClose.setPreferredSize(new Dimension(120, 25));
        
        buttonClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				close();
			}});
        
        panelBottom.add(buttonClose);
        
        add(panelBottom, java.awt.BorderLayout.SOUTH);
    }
	
	/**
	 * popup the properties as a ModalJPanel
	 */
	public static void showProperties(Tarification t, Component origin, Point delta) {
		//		 create a JInternalFrame
		final JInternalFrame jif= new JInternalFrame(
				Lang.translate("Tarification properties"), true, true);
		
		final TarificationPublishingPanel tpp 
		= new TarificationPublishingPanel(t) {

			public void close() {
				this.m_tpp.m_panelInfo.editionStopped();
				jif.dispose();
			}};
		
		//	add the component to the JInternalFrame
		jif.getContentPane().add(tpp);
		jif.setFrameIcon(Resources.iconSettings);
		
		ModalJPanel.warpJInternalFrame(jif,origin,delta,Resources.modalBgColor);
	}
	
	
	/**
	 * Save the save dialog for the publishing.
	 */
	public static void showPublishDialog(Tarification tarification) {
		tarification.getHeader().setDataType(
				TarificationHeader.TYPE_TARIFICATION_ORIGINAL);
		tarification.getHeader().setPublishingDate(new Date());
		FileManagement.saveAs(tarification, FileManagement.CREATOR_PUBLISH);
	}
	

	/**
	 * called when cancel or close is called
	 */
	public abstract void close();
	
    
    
    // Variables declaration - do not modify
    private SButton buttonClose;
    private SButton buttonPublish;
    private JPanel panelBottom;
    // End of variables declaration
}

/*
 * $Log: TarificationPublishingPanel.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.12  2004/11/22 16:30:10  jvaucher
 * Ticket # 28: Added the properties menu item for the creator, in which you can set the properties of the tarification.
 * I Also added the propoerties panel into the Publish dialog. Perhaps it's
 * too big, in this case remove it, like before.
 *
 * Revision 1.11  2004/11/19 06:46:37  perki
 * better image handeling
 *
 * Revision 1.10  2004/11/16 15:17:55  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.9  2004/10/04 09:38:29  carlito
 * Panel now allows to tag the tarification in isSimple mode to indicate that it is viewable in demo mode
 *
 * Revision 1.8  2004/10/04 08:45:55  carlito
 * publish with demo question phase1
 *
 * Revision 1.7  2004/09/22 15:46:11  jvaucher
 * Implemented cleaver load/save system
 *
 * Revision 1.6  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.5  2004/07/22 15:12:34  carlito
 * lots of cleaning
 *
 * Revision 1.4  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/06/21 06:56:23  perki
 * Loading panel ok
 *
 * Revision 1.2  2004/06/20 16:09:03  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/06/18 18:26:19  perki
 * *** empty log message ***
 *
 */