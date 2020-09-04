/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: SButton.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import javax.swing.*;

/**
 * The SButton class provides an abstraction layer between 
 * the Swing Button class and the BC application. It basically
 * behaves like a JButton. 
 */
public class SButton extends SButtonAbstract {
    
	public SButton() {
		super();
	}

	public SButton(String text) {
		super(text);
	}

	public SButton(Action a) {
		super(a);
	}

	public SButton(Icon icon) {
		super(icon);
	}

	public SButton(String text, Icon icon) {
		super(text, icon);
	}

	protected void init() {};
	
}

/**
 *  $Log: SButton.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:41  perki
 *  First commit on sourceforge
 *
 */