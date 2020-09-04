/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: AssetsRoot0Panel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionMultipleAmounts;

/**
 * UI for AssetsRoot0
 * @see AssetsRoot0
 */
public class AssetsRoot0Panel extends DispatcherSimplePanel {
	/**
	 * overwrite this to set a default Class Icon
	 */
	public static ImageIcon defaultClassTreeIcon
		 = Resources.wsRootDispatcher;

	/**
	 * @param ws
	 * @param tv
	 */
	public AssetsRoot0Panel(AssetsRoot0 ws, TarifViewer tv) {
		super(ws, tv);
	}

	

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	
	private OptionMultipleAmounts oma;
	
	/**
	 */
	public JPanel getOptionPanel() {
	    if (! getDisplayController().showRootOptions()) return null;
	    
		if (oma == null) 
			oma = new OptionMultipleAmounts((AssetsRoot0) getWorkSheet());
		return oma;
		//return super.getStandardOptionViewer();
	}

	
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"
/* $Log: AssetsRoot0Panel.java,v $
/* Revision 1.2  2007/04/02 17:04:24  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:38  perki
/* First commit on sourceforge
/*
/* Revision 1.16  2004/09/10 14:48:50  perki
/* Welcome Futures......
/*
/* Revision 1.15  2004/05/10 19:00:51  perki
/* Better amount option viewer
/*
/* Revision 1.14  2004/05/07 17:22:37  perki
/* installer ok
/*
/* Revision 1.13  2004/05/06 08:38:01  perki
/* OptionViewer add ons
/*
/* Revision 1.12  2004/03/08 17:53:18  perki
/* *** empty log message ***
/*
/* Revision 1.11  2004/02/19 23:57:25  perki
/* now 1Gig of ram
/*
/* Revision 1.10  2004/02/19 21:32:16  perki
/* now 1Gig of ram
/*
 * Revision 1.9  2004/02/19 16:21:25  perki
 * Tango Bravo
 *
 * Revision 1.8  2004/02/17 15:55:02  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.7  2004/02/17 08:54:07  perki
 * zibouw
 *
 * Revision 1.6  2004/02/17 08:50:22  perki
 * zibouw
 *
 * Revision 1.5  2004/02/16 18:59:15  perki
 * bouarf
 *
 * Revision 1.4  2004/02/16 13:07:53  perki
 * new event model
 *
 * Revision 1.3  2004/02/06 08:05:41  perki
 * lot of cleaning in UIs
 *
 * Revision 1.2  2004/02/05 11:07:28  perki
 * Transactions are welcome aboard
 *
 * Revision 1.1  2004/01/30 15:18:45  perki
 * *** empty log message ***
 *
 */