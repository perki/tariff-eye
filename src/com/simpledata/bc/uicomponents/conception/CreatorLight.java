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
 * $Id: CreatorLight.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.util.Date;

import javax.swing.*;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datatools.AutosaveTask;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.uitools.stree.STree;


/**
 * This is the light version for Tarification Creation ...
 */
public class CreatorLight extends Creator {

    //public static final String PARAM_DESCRIPTION_MODIFIER = "Light";

    private JPanel compactTreePanel;
    private JPanel centralPanel;
    
    /**
     * DO NOT USE
     */
    protected CreatorLight(Tarification t) {
        super(t);
        //autosave
        boolean doAutosave = ((Boolean)BC.getParameter(Params.KEY_AUTOSAVE_ENABLE,
                Boolean.class)).booleanValue();
        m_autosaveTask = new AutosaveTask(t, AutosaveTask.CREATOR_ENVT);
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
        return Creator.openTarification(t, 
                Params.KEY_CREATOR_LIGHT_DESCRIPTION_MODIFIER);
    }
    
    /** nothing to do */
    protected void performExtraEventTreatment(NamedEvent e) { }

    protected void performExtraCompactTreeSelectionEventTreatment(CompactNode node) { }
    
    protected void buildComplexComponents() {
        compactTreePanel = new JPanel(new BorderLayout());
        
        compactTreePanel.add(compactTreeButtonPanel(), BorderLayout.NORTH);
        
        STree st= compactExplorer().getSTree();
        JScrollPane jsc= new JScrollPane();
        jsc.setViewportView(st);
        
        compactTreePanel.add(jsc, BorderLayout.CENTER);
        
        centralPanel= new JPanel(new BorderLayout());
        
        // TODO fine tune sizes
        //       centralPanel.setMinimumSize(new Dimension(270, 10));
        //       centralPanel.setPreferredSize(new java.awt.Dimension(470, 10));
        
        centralPanel.add(tarifViewer(), BorderLayout.CENTER);
    }
    
    protected Component getLeftComponent() {
        return compactTreePanel;
    }

    protected Component getCentralComponent() {
        return centralPanel;
    }
    
    protected void updateLeftComponentProportions() { }

    protected void saveComponentsProportions() { }

    protected String getParameterDescriptionModifier() {
        return Params.KEY_CREATOR_LIGHT_DESCRIPTION_MODIFIER;
    }
    
    /**
     * @return true if you want the tarif viewer to show root options
     */
    public boolean souldTarifViewerShowsRootOption() {
        return false;
    }

}


/*
 * $Log: CreatorLight.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.5  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.4  2004/11/09 13:55:43  jvaucher
 * - Ticket # 40 : Autosave, added user parameters
 *
 * Revision 1.3  2004/11/08 16:42:35  jvaucher
 * - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 * Revision 1.2  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.1  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 */