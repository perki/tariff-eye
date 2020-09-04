/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 mai 2004
 * $Id: DispatcherIfPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import java.util.ArrayList;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.OptionBoolean;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionsViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.dispatcher.tools.TabbedDispatcherAbstract;


/**
 * This class is the graphical representation of a DispatcherIf
 * i.e. simple choice
 */
public class DispatcherIfPanel extends TabbedDispatcherAbstract {
    
    //private static Logger m_log = Logger.getLogger( DispatcherIfPanel.class );
    
    //private DispatcherIf dispatcherIf;
    
    //private TarifViewer tarifViewer;
    
    private OptionsViewer ov;
    
    /** my Icon **/
	public static ImageIcon defaultClassTreeIcon = Resources.wsDispatcherIf;
    
    /**
     * @param d
     * @param tv
     */
    public DispatcherIfPanel(DispatcherIf d, TarifViewer tv) {
        super(d, tv);
        //this.dispatcherIf = d;
        //this.tarifViewer = tv;
        
        ov = getStandardOptionViewer();
        
        initEventListener();
    }

    public int getTabCount() {
        // There are always 2 tabs
        return 2;
    }

    public WorkSheetPanel getWorkSheetPanelAt(int index) {
        if (getDispatcherIf() == null) {
            return null;
        }
        
        if (index == 0) {
            //WorkSheet ws = getDispatcherIf().getWorkSheetAt(DispatcherIf.YES_WORKSHEET);
            return getDisplayController().getWorkSheetPanel(getDispatcherIf().getWorkSheetAt(DispatcherIf.YES_WORKSHEET));
        }
        if (index == 1) {
            return getDisplayController().getWorkSheetPanel(getDispatcherIf().getWorkSheetAt(DispatcherIf.NO_WORKSHEET));
        }
        // Wrong index
        return null;
    }

    public int getWorkSheetIndex(WorkSheet ws) {
        if (ws == getDispatcherIf().getWorkSheetAt(DispatcherIf.YES_WORKSHEET)) {
            return 0;
        }  
        if (ws == getDispatcherIf().getWorkSheetAt(DispatcherIf.NO_WORKSHEET)) {
            return 1;
        }
        return -1;
    }

    /**
     * Get the associated DispatcherIf
     */
    private DispatcherIf getDispatcherIf() {
        WorkSheet ws = this.getWorkSheet();
        return (DispatcherIf)(ws);
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getOptionPanel()
     */
    public JPanel getOptionPanel() {
        return ov;
    }

    public void save() {
        // Sert � rien pour l'instant
    }

    public ImageIcon getTreeIcon() {
        return Resources.wsDispatcherIf;
    }

	/**
	 * get the WorkSHeetPanel Title .. can be overwriten
	 */
	public String getTitleOf(int index) {
	    String content = super.getTitleOf(index);
	    
	    if (getOptionStateControler().getEditOptionState() 
	    		== OptionDefaultPanel.EditStates.FULL) {
	        if (index == 0) {
	            content = "true :" + content;
	        }
	        if (index == 1) {
	            content = "false :" + content;
	        }
	    }
	    
	    return content;
	}
	
	/** When a new tab has been selected we want to select the according option */
	public void indexHasBeenSelected(int index) {
	    if ((index < 0) || (index > 1)) return;
	    boolean state = false;
	    if (index == 0) {
	        state = true;
	    }
	    getDispatcherIf().getOptionBoolean().setState(state);
	}
	
	/** ini my Event listner **/
	private void initEventListener() {
		if (getWorkSheet() == null) return;
		myEventListener = new MyEventListener(this);
		getWorkSheet().addNamedEventListener(
				myEventListener,NamedEvent.WORKSHEET_OPTION_DATA_CHANGED,null);
	}
	
	MyEventListener myEventListener;
	class MyEventListener  implements NamedEventListener {
	    
	    private DispatcherIfPanel owner;
	    
	    public MyEventListener(DispatcherIfPanel dip) {
	        this.owner = dip;
	    }
	    
		public void eventOccured(NamedEvent e) {
			if ( e.getSource() != getWorkSheet()) return;
			if ( ! (e.getUserObject() instanceof OptionBoolean)) return;
			OptionBoolean ob = (OptionBoolean) e.getUserObject();
			ArrayList opts = this.owner.getWorkSheet().getOptions(OptionBoolean.class);
			if (opts.size() > 0) {
				OptionBoolean myOb = (OptionBoolean)opts.get(0);
				if (myOb == ob) {
					// We have caught an event from our own option
					// We select the tabs accordingly
					if (ob.getState()) {
					    this.owner.setSelectedIndex(0);
						//getTabbedPane().setSelectedIndex(0);
					} else {
					    this.owner.setSelectedIndex(1);
						//getTabbedPane().setSelectedIndex(1);
					}
					
				}
			}
			
		}
	}
	
}

/*
 * $Log: DispatcherIfPanel.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.12  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.11  2004/09/07 13:35:03  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.9  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/05/22 17:58:19  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/22 17:49:17  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/22 17:30:20  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/22 08:39:36  perki
 * Lot of cleaning
 *
 * Revision 1.3  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.2  2004/05/21 12:15:12  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/18 19:11:52  carlito
 * *** empty log message ***
 *
 */