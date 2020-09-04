/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 22 juil. 2004
 * $Id: SButtonAbstract.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import javax.swing.*;
import javax.swing.JButton;

/**
 * Defines a superclass for SButton and SButtonIcon
 * Ensuring that an init method is called after any new instanciation
 * WARNING : Avoid creating constructors for inheriting classes
 */
public abstract class SButtonAbstract extends JButton {

	/**
	 * 
	 */
	public SButtonAbstract() {
		super();
		init();
	}

	/**
	 * @param text
	 */
	public SButtonAbstract(String text) {
		super(text);
		init();
	}

	/**
	 * @param a
	 */
	public SButtonAbstract(Action a) {
		super(a);
		init();
	}

	/**
	 * @param icon
	 */
	public SButtonAbstract(Icon icon) {
		super(icon);
		init();
	}

	/**
	 * @param text
	 * @param icon
	 */
	public SButtonAbstract(String text, Icon icon) {
		super(text, icon);
		init();
	}

	protected abstract void init();
	
}


/*
 * $Log: SButtonAbstract.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.1  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 */