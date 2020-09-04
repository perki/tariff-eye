/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 13 juil. 2004
 * $Id: SButtonIcon.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * 
 */
public class SButtonIcon extends SButtonAbstract {
    
	public SButtonIcon() {
		super();
	}

	public SButtonIcon(String text) {
		super();
	}

	public SButtonIcon(Action a) {
		super(a);
	}

	public SButtonIcon(Icon icon) {
		super(icon);
	}

	public SButtonIcon(String text, Icon icon) {
		super(icon);
	}

	/**
	 * Sets the button icon
	 */
	public void setIcon(Icon icon) {
		super.setIcon(icon);
		sizeIt();
	}

	/**
	 * Anihilate the text ;)
	 */
	public void setText(String s) {
	    
	}
	
	/** change the size depending on the icon **/
	private void sizeIt() {
		if (getIcon() == null) return;
		Dimension d = new Dimension(getIcon().getIconWidth()+4,
					getIcon().getIconHeight()+4);
		setPreferredSize(d);
		setMinimumSize(d);
		setSize(d);
	}

	/** initialize the button 
	 * Method called just after the button has been initialized
	 */
	protected void init() {
		setNullBorder();
		
		sizeIt();
		
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent evt) {
				if (isEnabled()) {
					setBorder(new EtchedBorder(EtchedBorder.LOWERED));
					setBorderPainted(true);
					sizeIt();
				}
			}
			public void mouseExited(MouseEvent evt) {
				setNullBorder();
				sizeIt();
			}
		});
	}
	
	
	// set null borders
	private void setNullBorder() {
		setBorder(BCLookAndFeel.getSButtonBorder());
		setBorderPainted(true);
		setFocusPainted(false);
	}
    
}


/*
 * $Log: SButtonIcon.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.2  2004/10/15 06:39:00  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.1  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 */