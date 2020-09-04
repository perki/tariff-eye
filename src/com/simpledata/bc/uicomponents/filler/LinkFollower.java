/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: LinkFollower.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.simpledata.uitools.stree.STree;
import com.simpledata.uitools.stree.STreeNode;

/**
 * Makes Linkables Label to navigate into the Tree 
 */
public class LinkFollower extends MouseAdapter{
    private static final Logger m_log 
	= Logger.getLogger( LinkFollower.class );
    
    private STree stree;
    private STreeNode stn;
    private JLabel label;
    
    public LinkFollower(JLabel label,STree stree) {
        this.stree = stree;
        this.label = label;
        label.addMouseListener(this);
    }
    
    public LinkFollower(JLabel label,STree stree, STreeNode stn) {
        this(label,stree); 
        refresh(stn);
    }
    
    /**
     * change the STreeNode link
     */
    public void refresh(STreeNode stn) {
        this.stn = stn;
    }
    
    public void mouseClicked(MouseEvent e) {
        if (stn != null) stree.selectNode(stn);
	}
    
    public void mouseEntered(MouseEvent e) {
        if (stn != null)
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	public void mouseExited(MouseEvent e) {
		label.setFont(label.getFont().deriveFont(Font.PLAIN));
		label.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
