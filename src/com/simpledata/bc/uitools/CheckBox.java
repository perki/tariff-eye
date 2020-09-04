/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: CheckBox.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.Point;

import javax.swing.ImageIcon;

import com.simpledata.bc.Resources;

/**
 * 
 */
public class CheckBox {
	public final static int CHECKABLE = 0;
	public final static int CHECKABLE_NOT = 1;
	public final static int CHECKSTATE_CHECKED_NOT = 0;
	public final static int CHECKSTATE_CHECKED = 1;
	public final static int CHECKSTATE_PARTIALLY = 2;
	public final static int CHECKSTATE_INDUCED = 3;
	
	public final static int TAG_NONE = 0;
	public final static int TAG_WARNING = 1;
	
	private static ImageIcon[][] icons = new ImageIcon[2][4];

	private static ImageIcon[] boxes = new ImageIcon[2];
	private static ImageIcon[] checks = new ImageIcon[4];
	
	static {
		boxes[CHECKABLE] = Resources.checkBoxCheckable;
		boxes[CHECKABLE_NOT] = Resources.checkBoxCheckableNot;
		checks[CHECKSTATE_CHECKED_NOT] = Resources.checkBoxStateCheckedNot;
		checks[CHECKSTATE_CHECKED] = Resources.checkBoxStateChecked;
		checks[CHECKSTATE_PARTIALLY] = Resources.checkBoxStatePartially;
		checks[CHECKSTATE_INDUCED] = Resources.checkBoxStateInduced;
	}


	/**
	 * @param tag one of: CheckBox.TAG_XXX
	 * @param checkable one of: CheckBox.CHECKABLE, CheckBox.CHECKABLE_NOT
	 * @param checkState one of:  CheckBox.CHECKSTATE_XXX;
	 */
	public static ImageIcon get(int tag,int checkable,int checkState) {
		if (checkable < 0 || checkState < 0) return null;
		if (checkable >= icons.length) return null;
		if (checkState >= icons[0].length) return null;
		
		if (icons[checkable][checkState] == null) {
			icons[checkable][checkState] =
			ImageTools.drawIconOnIcon(boxes[checkable],
									  checks[checkState],
									  new Point(0,0));
		}
		
		switch (tag) {
			case TAG_WARNING:
			return ImageTools.drawIconOnIcon(icons[checkable][checkState],
				Resources.stdTagWarning,new Point(0,0));
		}
			 
		return icons[checkable][checkState];
	}


}


/**
 *  $Log: CheckBox.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:41  perki
 *  First commit on sourceforge
 *
 *  Revision 1.4  2004/07/08 12:02:32  kaspar
 *  * Documentation changes, Added some debug code into
 *    the main view of the creator
 *
 *  Revision 1.3  2004/05/22 08:39:36  perki
 *  Lot of cleaning
 *
 *  Revision 1.2  2004/03/18 09:02:29  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/03/08 08:46:29  perki
 *  houba houba hop
 *
 */