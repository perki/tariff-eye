/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TransactionsRoot0Panel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionMultipleTransactions;

/**
 * UI for TransactionsRoot0
 * @see TransactionsRoot0
 */
public class TransactionsRoot0Panel extends DispatcherSimplePanel {
	/**
	 * overwrite this to set a default Class Icon
	 */
	public static ImageIcon defaultClassTreeIcon
		 = Resources.wsRootDispatcher;

	/**
	 * @param ws
	 * @param tv
	 */
	public TransactionsRoot0Panel(TransactionsRoot0 ws, TarifViewer tv) {
		super(ws, tv);
	}

	

	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	

	OptionMultipleTransactions omt;
	
	/**
	 */
	public JPanel getOptionPanel() {
	    if (! getDisplayController().showRootOptions()) return null;
	    
		if (omt == null) 
			omt = new OptionMultipleTransactions(
					(TransactionsRoot0) getWorkSheet());
		return omt;
	}
}  
/**
* $Log: TransactionsRoot0Panel.java,v $
* Revision 1.2  2007/04/02 17:04:24  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.14  2004/09/10 14:48:51  perki
* Welcome Futures......
*
* Revision 1.13  2004/08/04 06:03:12  perki
* OptionMoneyAmount now have a number of lines
*
* Revision 1.12  2004/05/18 10:10:27  perki
* *** empty log message ***
*
* Revision 1.11  2004/05/14 16:00:41  perki
* Nice option table
*
* Revision 1.10  2004/03/08 17:53:18  perki
* *** empty log message ***
*
* Revision 1.9  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.8  2004/02/19 16:21:25  perki
* Tango Bravo
*
* Revision 1.7  2004/02/17 15:55:02  perki
* zobi la mouche n'a pas de bouche
*
* Revision 1.6  2004/02/17 08:54:07  perki
* zibouw
*
* Revision 1.5  2004/02/17 08:50:22  perki
* zibouw
*
* Revision 1.4  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.3  2004/02/16 13:07:53  perki
* new event model
*
* Revision 1.2  2004/02/06 08:05:41  perki
* lot of cleaning in UIs
*
* Revision 1.1  2004/02/05 07:46:54  perki
* Transactions are welcome aboard
*
*/