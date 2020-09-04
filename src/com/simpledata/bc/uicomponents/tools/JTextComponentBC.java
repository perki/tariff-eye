/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: JTextComponentBC.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Color;

import javax.swing.text.JTextComponent;

/**
 * 
 */
public interface JTextComponentBC {
	public static Color EDITING_C= new Color(153, 0, 0);
	public static Color NORMAL_C= Color.BLACK;
	
	/** this will be call when the value needs to be saved **/
	public void stopEditing();

	/** this will be call when the edition starts **/
	public void startEditing();
	
	/** return the JTextComponent they represent (normaly themselves) **/
	public JTextComponent getJTextComponent();
}


/**
 *  $Log: JTextComponentBC.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.1  2004/03/22 19:32:45  perki
 *  step 1
 *
 */