/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 23 mars 2004
 * $Id: WorkSheetPanelBorder.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.tools.Lang;

/**
 * Defines a border for WorkSheetPanelContainers
 */
public class WorkSheetPanelBorder extends JPanel implements NamedEventListener {
    
    private final static Logger m_log = Logger.getLogger(WorkSheetPanelBorder.class);
    
    private WorkSheetPanel owner;
    
    // locales
    private final static String NULL_WS_TITLE = 
        "WorkSheetPanelBorder:NullWorkSheetTitle";
    
    // Graphical objects declaration
    private JPanel titlePanel;
    private JLabel titleLabel;
    private JPanel actionPanelContainer;
    // End of graphical objects declaration
    
    public WorkSheetPanelBorder(WorkSheetPanel wsp) {
        super();
        this.owner = wsp;
        
        initComponents();

        if (wsp != null) {
            WorkSheet ws = wsp.getWorkSheet();
            if (ws != null) {
                ws.addNamedEventListener(this, -1, ws.getClass());
            }
        }
        
        this.titleLabel.addMouseListener(new BorderMouseListener(owner));
    }
    
    private static Color getBackgroundColor() {
        return UIManager.getColor("TextField.selectionBackground");
    }
    
    private static Color getForegroundColor() {
        return UIManager.getColor("TextField.selectionForeground");
    }
    
    private void initComponents() {
        this.setOpaque(true);
        this.setBackground(getBackgroundColor());
        
        this.setBorder(new LineBorder(getForegroundColor(), 1));
        
        GridBagConstraints gridBagConstraints;
        
        titlePanel = new JPanel();
        titleLabel = new JLabel();
        actionPanelContainer = new JPanel(new BorderLayout());
        this.setLayout(new GridBagLayout());
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(new JLabel(), gridBagConstraints);
        
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.setOpaque(false);
        titleLabel.setOpaque(false);
        titleLabel.setForeground(getForegroundColor());
        
        if (this.owner == null) {
            titleLabel.setText(
                    Lang.translate("WorkSheetPanelBorder:NullWorkSheetPanel"));
        } else {
            titleLabel.setIcon(this.owner.getIcon());
            WorkSheet ws = this.owner.getWorkSheet();
            if (ws != null) {
                setTitle(ws.getTitle());
                //titleLabel.setText(ws.getTitle());
            } else {
                //titleLabel.setText("null WorkSheet");
                setTitle(Lang.translate(NULL_WS_TITLE));
            }
        }
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        titlePanel.add(titleLabel, gridBagConstraints);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(2, 0, 2, 0);
        this.add(titlePanel, gridBagConstraints);
        
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(new JLabel(), gridBagConstraints);
        
        
        actionPanelContainer.setOpaque(false);
        actionPanelContainer.setForeground(getForegroundColor());
        
        if (this.owner != null) {
            JPanel jp = this.owner.getActionPanel();
            if (jp != null) {                   
                jp.setOpaque(false);
                jp.setForeground(getForegroundColor());
                this.actionPanelContainer.add(jp, BorderLayout.CENTER);
            }
        }
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 0, 0);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(actionPanelContainer, gridBagConstraints);
        
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        this.add(new JLabel(), gridBagConstraints);
        
    }
    
    
    private void setTitle(String s) {
        int len1 = -1;
        int len2 = -1;
        Font f = titleLabel.getFont().deriveFont(
                BorderMouseListener.NOT_OVER_STYLE);
        FontMetrics fm = titleLabel.getFontMetrics(f);
        len1 = fm.stringWidth(s);
        f = f.deriveFont(BorderMouseListener.OVER_STYLE);
        fm = titleLabel.getFontMetrics(f);
        len2 = fm.stringWidth(s);
        
        //m_log.warn("found following sizes len1 : "+len1+", len2 : "+len2);
        
        // TODO minimumSize does not work
        // we should import the preferredHeight of this kind of label from somewhere
        // instead of just setting it to 18
        // 16 is for icon width... 
        this.titleLabel.setPreferredSize(
                new Dimension(Math.max(len1, len2) + 16 +
                        titleLabel.getIconTextGap(), 18));
        
        this.titleLabel.setText(s);
    }
    
    /**
     * @see com.simpledata.bc.datamodel.event.NamedEventListener#eventOccured(com.simpledata.bc.datamodel.event.NamedEvent)
     */
    public void eventOccured(NamedEvent e) {
        if (e.getSource() == this.owner.getWorkSheet()) {
            if (e.getEventCode() == NamedEvent.TITLE_MODIFIED) {
                setTitle(this.owner.getWorkSheet().getTitle());
            }
            
            if (e.getEventCode() == NamedEvent.WORKSHEET_DATA_MODIFIED ||
                    e.getEventCode() == NamedEvent.WORKSHEET_OPTION_ADDED ||
                    e.getEventCode() == NamedEvent.WORKSHEET_OPTION_REMOVED ||
                    e.getEventCode() == 
                        NamedEvent.WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE)
                this.titleLabel.setIcon(this.owner.getIcon());
            
//            // Insert or not the correct action panel if any
        
            
        }
    }
    
}

/**
 * A mouse listener for the border
 */
class BorderMouseListener implements MouseListener {
    private static Logger m_log = Logger.getLogger( BorderMouseListener.class );
    private static JPopupMenu jPopupMenu;
    private WorkSheetPanel attWsp;
    
    public final static int OVER_STYLE = Font.BOLD;
    public final static int NOT_OVER_STYLE = Font.PLAIN;
    
    public BorderMouseListener(WorkSheetPanel wsp) {
        this.attWsp = wsp;
    }
    
    public void mouseClicked(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
        manageEvent(e, true);
    }
    
    public void mouseReleased(MouseEvent e) {
        if (!e.isConsumed()) {
            manageEvent(e, false);
        }
    }
    
    public void mouseEntered(MouseEvent e) {
        JLabel jb = (JLabel)e.getSource();
        Font f = jb.getFont().deriveFont(OVER_STYLE);
        jb.setFont(f);
    }
    
    public void mouseExited(MouseEvent e) {
        JLabel jb = (JLabel)e.getSource();
        Font f = jb.getFont().deriveFont(NOT_OVER_STYLE);
        jb.setFont(f);
    }
    
    private void manageEvent(MouseEvent e, boolean pressed) {
        if (e.isPopupTrigger() || pressed) {
            jPopupMenu= this.attWsp.getJPopupMenu();
            if (jPopupMenu != null) {
                jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            } 
        } 
    }
}

/* 
 * $Log: WorkSheetPanelBorder.java,v $
 * Revision 1.2  2007/04/02 17:04:28  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.24  2004/11/22 18:44:39  carlito
 * Affichage des DispatcherCase modifications quasi-finale
Modification du WorkSheetPanelBorder pour qu'il n'ai plus la bougeotte avec le titre...
 *
 * Revision 1.23  2004/11/19 09:59:57  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/11/17 18:28:09  carlito
 * arggg
 *
 * Revision 1.21  2004/11/17 15:26:22  carlito
 * New design for WorkSheetPanel, WorkSheetPanelBorder and DispatcherCasePanel advanced
 *
 * Revision 1.20  2004/11/15 09:11:03  perki
 * *** empty log message ***
 *
 * Revision 1.19  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.18  2004/11/11 10:57:44  perki
 * Intro to new Dispachers design
 *
 * Revision 1.17  2004/10/11 07:49:00  perki
 * Links in Filler
 *
 * Revision 1.16  2004/09/21 09:11:55  perki
 * small changes
 *
 * Revision 1.15  2004/09/08 16:35:15  perki
 * New Calculus System
 *
 * Revision 1.14  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.12  2004/05/18 17:04:26  perki
 * Better icons management
 *
 * Revision 1.11  2004/05/18 15:41:45  perki
 * Better icons management
 *
 * Revision 1.10  2004/05/18 15:11:25  perki
 * Better icons management
 *
 * Revision 1.9  2004/05/14 14:20:19  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/05/14 07:52:53  perki
 * baby dispatcher is going nicer
 *
 * Revision 1.7  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.6  2004/03/25 17:12:28  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/03/24 18:19:15  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/03/24 13:29:12  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/03/24 11:16:38  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/03/23 19:19:33  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/03/23 19:02:55  carlito
 * *** empty log message ***
 *
 */

