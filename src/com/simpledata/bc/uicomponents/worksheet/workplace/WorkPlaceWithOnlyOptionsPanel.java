/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: WorkPlaceWithOnlyOptionsPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
 
/**
 * Package that contains all the different panels for displaying
 * any given Workplace. 
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;

/**
 * WorkPlaceWithOnlyOptions UI
 * @see com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions
 */
public class WorkPlaceWithOnlyOptionsPanel extends WorkSheetPanel {
	
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon 
		= Resources.wsWorkPlaceWithOnlyOptions;
	
	public WorkPlaceWithOnlyOptionsPanel(WorkPlaceWithOnlyOptions wpwoo, 
			TarifViewer tv) {
		super(wpwoo, tv);
	}
	
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
	 */
	public void save() {

	}
	
	/**
	 *
	 */
	public JMenu getJMenu() {
		return null;
	}

	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {
		// nothing to do
		
	}

	/**
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
		return null;
	}

}  
/* $Log: WorkPlaceWithOnlyOptionsPanel.java,v $
/* Revision 1.2  2007/04/02 17:04:26  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:41  perki
/* First commit on sourceforge
/*
/* Revision 1.14  2004/07/26 20:36:10  kaspar
/* + trRateBySlice subreport that shows for all
/*   RateBySlice Workplaces. First Workplace subreport.
/* + Code comments in a lot of classes. Beautifying, moving
/*   of $Id: WorkPlaceWithOnlyOptionsPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
/* + Long promised caching of reports, plus some rudimentary
/*   progress tracking.
/*
/* Revision 1.13  2004/05/06 07:06:25  perki
/* WorkSheetPanel has now two new methods
/*
/* Revision 1.12  2004/04/09 07:16:52  perki
/* Lot of cleaning
/*
/* Revision 1.11  2004/03/23 13:39:19  perki
/* New WorkSHeet Panel model
/*
 * Revision 1.10  2004/02/19 23:57:26  perki
 * now 1Gig of ram
 *
 * Revision 1.9  2004/02/19 16:21:25  perki
 * Tango Bravo
 *
 * Revision 1.8  2004/02/17 15:55:03  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.7  2004/02/17 08:54:07  perki
 * zibouw
 *
 * Revision 1.6  2004/02/16 18:59:15  perki
 * bouarf
 *
 * Revision 1.5  2004/02/16 13:07:53  perki
 * new event model
 *
 * Revision 1.4  2004/02/06 10:04:22  perki
 * Lots of cleaning
 *
 * Revision 1.3  2004/02/06 07:44:55  perki
 * lot of cleaning in UIs
 *
 * Revision 1.2  2004/02/02 07:00:50  perki
 * sevral code cleaning
 *
 * Revision 1.1  2004/02/01 18:28:54  perki
 * dimmanche soir
 *
 */