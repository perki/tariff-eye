/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TableTools.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.UIManager;

/**
 * A class that contains static tools fot table.<BR>
 * Mainly for rendering purposes
 */
public class TableTools {
	
	
	/**
	 * return the colors that should be applied to rows<BR>
	 * Create two color based on a variation of the background 
	 * and foreground colors<BR>
	 * The color passed as argument will be darken by the paramter s
	 * @param initBackgd the color to be modified
	 * @param s the strength of the modification (10 is light)
	 */
	public static Color[] getColors (Color initBackgd,int s) {
		Color[] result = new Color[2];
		result[0] = initBackgd;

		int r = initBackgd.getRed() > s ? initBackgd.getRed() - s : 0;
		int g = initBackgd.getGreen() > s ? initBackgd.getGreen() - s : 0;
		int b = initBackgd.getBlue() > s ? initBackgd.getBlue() - s : 0;

		result[1] = new Color(r,g,b);
		
		return result;
	}
	
	/**
	 * Set the color of a row<BR>
	 * Background/foreground<BR>
	 * To be called in getTableCellRendererComponent()<BR>
	 * @param jTable the jTable that contains this component
	 * @param co the component to change
	 * @param isSelected
	 * @param hasFocus
	 * @param row
	 */
	public static void setRowColors (
			JTable jTable,
			Component co, 
			boolean isSelected, 
			boolean hasFocus, 
			int row) {
		if (hasFocus) {
			// do not care
		}
		if (!isSelected) {
			Color c = jTable.getBackground();
			Color f = co.getForeground();
			if (f == null) {
				f = UIManager.getColor("Table.textForeground");
				co.setForeground(f);
			}
			
			co.setBackground(getColors(c,10)[row % 2]);
		} else {
			co.setBackground(UIManager
					.getColor("Table.selectionBackground"));
			co.setForeground(UIManager
					.getColor("Table.selectionForeground"));
		}
	}
	
}

/*
 * $Log: TableTools.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.3  2004/07/16 18:39:20  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.1  2004/05/12 10:11:12  perki
 * *** empty log message ***
 *
 */