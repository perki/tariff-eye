/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 8 sept. 2004
 * $Id: CreatorGold.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import com.simpledata.bc.*;
import com.simpledata.bc.actions.ActionClearSimulation;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datatools.AutosaveTask;
import com.simpledata.bc.merging.MergingMenu;
import com.simpledata.bc.reports.ReportToolbox;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.filler.FillerViewer;
import com.simpledata.bc.uitools.streetable.STreeTable;

/**
 * 
 */
public class CreatorGold extends Creator {

    //public static final String PARAM_DESCRIPTION_MODIFIER = "Gold";
    
    
    
    /** Graphical components */
    private STreeTable treeTable;

	/** The Filler Viewer Controler Panel .. it's shown when needed **/
	private FillerViewer fillerViewer;
    
	// it is ok if never read.. references is kept just to prevent gc
	private MyCurrencyChangeListener myCurrencyListener;
    
    /**
     * DO NOT USE... 
     * @param t
     */
    protected CreatorGold(Tarification t) {
        super(t);
        // autosave
        boolean doAutosave = ((Boolean)BC.getParameter(Params.KEY_AUTOSAVE_ENABLE,
                Boolean.class)).booleanValue();
        m_autosaveTask = new AutosaveTask(t, AutosaveTask.LIVE_ENVT);
	    if (doAutosave) {
        	int period = ((Integer)BC.getParameter(Params.KEY_AUTOSAVE_PERIOD,
        	        Integer.class)).intValue();
        	BC.bc.m_timer.schedule(m_autosaveTask, new Date(), period * 60000);     
        }
    }

    /**
	 * Launch UI displaying a tarification for edition
	 * @param t tarification to be edited
	 */ 
    public static Creator openTarification(Tarification t) {
        Creator result=Creator.openTarification(t, 
                Params.KEY_CREATOR_GOLD_DESCRIPTION_MODIFIER);
        return result;
    }
    
    /**
     * @see Creator#performExtraEventTreatment(NamedEvent)
     */
    protected void performExtraEventTreatment(NamedEvent e) {
        if (NamedEvent.COM_VALUE_CHANGED_TARIFICATION == e.getEventCode()) {
            if (treeTable != null)
                treeTable.getTable().repaint();
        }
    }

    protected void performExtraCompactTreeSelectionEventTreatment(CompactNode node) {
        // tell the FillerViewr to change
        fillerViewer.showCompactNodeInfo(node);        
    }
    
    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.conception.Creator#buildLeftComponent()
     */
    protected void buildComplexComponents() {
		/** **/
		ArrayList colNames = new ArrayList();
		colNames.add("Currency");
		
		ArrayList colSizes = new ArrayList();
		colSizes.add(new Integer(90));
		treeTable = new STreeTable(compactExplorer().getSTree(), 
			compactTreeButtonPanel(), 1, colNames, colSizes, null, -1);
	

		//Add a listener to change the Table column name on currency change
		myCurrencyListener = new MyCurrencyChangeListener(treeTable);
		
		
		treeTable.setHighLightColors(Color.LIGHT_GRAY, Color.BLACK);
		
        fillerViewer = 
			new FillerViewer(compactExplorer(),tarifViewer());
    }

//    protected void buildSpecialComponents() {
//
//    }
    
    /**
     * @return true if you want the tarif viewer to show root options
     */
    public boolean souldTarifViewerShowsRootOption() {
        return true;
    }
    
    protected Component getLeftComponent() {
        return treeTable;
    }

    protected Component getCentralComponent() {
        return fillerViewer;
    }
    
    protected void updateLeftComponentProportions() {
        double[] treeSizes = (double[]) BC.getParameter(
                 Params.KEY_CREATOR_GOLD_COMPACT_TREE_TABLE_POS,
                 double[].class);
        if (treeSizes.length != 3)
            treeSizes = (double[])BC.forceDefaultParam(
                    Params.KEY_CREATOR_GOLD_COMPACT_TREE_TABLE_POS);
    }


    protected void saveComponentsProportions() {
        double[] treeSizes = treeTable.getSizes();
        BC.setParameter(Params.KEY_CREATOR_GOLD_COMPACT_TREE_TABLE_POS, treeSizes);
    }
 
    protected String getParameterDescriptionModifier() {
        return Params.KEY_CREATOR_GOLD_DESCRIPTION_MODIFIER;
    }
    
	
	private JMenu menuData;
	/** get a JMenu for the data (clear options) **/
	private JMenu menuData() {
	    if (menuData != null) return menuData;
	    menuData = new JMenu();
	    BC.langManager.register(menuData,"Data");
	    
	    menuData.add(ActionClearSimulation.getMenuItem(getTarification()));
	    
	    return menuData;
	}
	
    
    protected void buildMenuBarPlus(JMenuBar menuBar) {
    	menuBar.add(new MergingMenu(getTarification(),this));
		
        menuBar.add(menuData());
    };
    
    // Creator special menu items - disabled
   /*
    protected JMenu buildFileMenu() {
    	JMenu fileMenu = super.buildFileMenu();
    	int pos = fileMenu.getMenuComponentCount(); // XXX hack. see super
    	
    	JMenuItem mFileFeeSummary = new JMenuItem();
    	BC.langManager.register(mFileFeeSummary, "Fee report summary...");
    	mFileFeeSummary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFeeSummaryPerformed();
			}
		});
		fileMenu.add(mFileFeeSummary, pos - 2);
		
		JMenuItem mFileDetailedFee = new JMenuItem();
		BC.langManager.register(mFileDetailedFee, "Fee detailed report...");
		mFileDetailedFee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mDetailedFeePerformed();
			}
		});
		fileMenu.add(mFileDetailedFee, pos - 1);
		
		return fileMenu;
    }
    */
    void mFeeSummaryPerformed() {
    	ReportToolbox.displayFeeReport(this, compExplorer, false);
    }
    
    void mDetailedFeePerformed() {
    	ReportToolbox.displayFeeReport(this, compExplorer, true);
    }

    /**
     * A change listener on currencies
     */
    class MyCurrencyChangeListener implements ChangeListener {
        STreeTable st;
        public MyCurrencyChangeListener(STreeTable stable) {
            st = stable;
            BC.getCurrencyManager().addWeakCurrencyChangeListener(this);
            stateChanged(null);
        }
        
        public void stateChanged(ChangeEvent e) {
            st.setColumnName(0,Lang.translate("Fees")+": "+
                    BC.getCurrencyManager().defaultCurrency().currencyCode());
        }
    }

}


/*
 * $Log: CreatorGold.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.16  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.15  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.14  2004/11/09 13:55:43  jvaucher
 * - Ticket # 40 : Autosave, added user parameters
 *
 * Revision 1.13  2004/11/09 12:48:26  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/11/08 16:42:35  jvaucher
 * - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 * Revision 1.11  2004/10/16 11:41:26  jvaucher
 * - Minor changes (Help, reports, UI)
 *
 * Revision 1.10  2004/10/11 07:49:00  perki
 * Links in Filler
 *
 * Revision 1.9  2004/09/28 09:41:51  perki
 * Clear action added to CreatorGold
 *
 * Revision 1.8  2004/09/27 08:48:50  jvaucher
 * Fee report. Rendering improved
 *
 * Revision 1.7  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.6  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.5  2004/09/15 11:04:22  jvaucher
 * Added a textual message for the event code (method eventName())
 * Added the modification dialog box. But some changes are still not observable. See tickets for details
 *
 * Revision 1.4  2004/09/14 12:07:26  carlito
 * commit de protection
 *
 * Revision 1.3  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.2  2004/09/09 18:38:46  perki
 * Rate by slice on amount are welcome aboard
 *
 * Revision 1.1  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 */