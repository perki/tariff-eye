/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 10 sept. 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.FuturesRoot0;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionMultipleAmounts;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionMultipleFutures;

/**
 * @author SD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FuturesRoot0Panel extends DispatcherSimplePanel {
    
    /**
	 * overwrite this to set a default Class Icon
	 */
	public static ImageIcon defaultClassTreeIcon
		 = Resources.wsRootDispatcher;
    
    /**
     * @param ds
     * @param tv
     */
    public FuturesRoot0Panel(FuturesRoot0 fr, TarifViewer tv) {
        super(fr, tv);
    }
    
    /**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	private OptionMultipleFutures omf;
	/**
	 */
	public JPanel getOptionPanel() {
	    if (! getDisplayController().showRootOptions()) return null;
	    
		if (omf == null) 
			omf = new OptionMultipleFutures((FuturesRoot0) getWorkSheet());
		return omf;
	}

}
