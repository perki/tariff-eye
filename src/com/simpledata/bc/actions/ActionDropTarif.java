/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ActionDropTarif.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.bc.uitools.SButtonIcon;

/**
 * This Action describe how a Tarif can be dropped
 */
public class ActionDropTarif {
    private static final String DELETE_CONFIRM = 
        "Warning. Current tarif will be permanently deleted.";
	
    
    private static final Logger m_log = 
        Logger.getLogger( ActionDropTarif.class ); 
    
    /**
     * get the Icon for this Action
     */
    public static ImageIcon getIcon() {
        return Resources.iconDelete;
    }
    
    /**
     * Create a Button that will drop a Tarif
     * @param owner is the UI component that uses this button (used for popups)
     */
    public static SButtonIcon createButton(Component owner,final Tarif t) {
        return createButton(owner,new Interface(){
            public Tarif actionDropTarif_getTarifToDrop() {
                return t;
            }

            public void actionDropTarif_tarifDroped(Tarif t) {
                // Nothing to do
            }});
    }
    
    /**
     * Create a Button that will drop a Tarif<BR>
     * Based on the tariff proposed by this inteface
     */
    public static SButtonIcon 
    createButton(final Component owner,final Interface i) {
        SButtonIcon sbi = new SButtonIcon(getIcon());
        sbi.setToolTipText("Delete Tarif");
        
        sbi.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                int answer = ModalDialogBox.confirm(owner, 
                        Lang.translate(DELETE_CONFIRM));
                if (answer == ModalDialogBox.ANS_OK) { 
                    // Delete current tarif from tarification
                    Tarif t = i.actionDropTarif_getTarifToDrop();
                    if (t != null) {
                        if (doAction(owner,t))
                            i.actionDropTarif_tarifDroped(t);
                    }
                }
            }
            
        
        });
        
        return sbi;
    }
    
    
    /**
     * Drop the passed Tarif<BR>
     * @param c is used to pass alerts to the user.. but it can be null
     * @return true if the action succeded
     */
    public static boolean doAction(Component c,Tarif t) {
        if (t.canBeDroppped()) {
		    t.drop();
		    return true;
	    } 
        if (c != null ) {
	        ModalDialogBox.alert(c,
	        Lang.translate("A tarif with references cannot be deleted")
	        );
	    } else {
	        m_log.warn("Cannot drop: tarif ["+t+"] it has linked options");
	    }
        
        return false;
    }
    
    /** inteface for container that may have tarifs to drop **/
    public interface Interface {
        /** return the tarif to drop ... may be null **/
        public Tarif actionDropTarif_getTarifToDrop();
        /** will be called when a Tarif has been dropped,
         * for information **/
        public void actionDropTarif_tarifDroped(Tarif t);
    }

}
