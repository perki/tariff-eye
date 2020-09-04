/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 5 oct. 2004
 * $Id: ToolKit.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.*;

/**
 * Some usefull tools for window positioning
 */
public class ToolKit {
    
	private ToolKit() {
		
	}
	
	public static void centerOnScreen(Component c) {
		centerOnScreen(c,0,0);
	}
	
	public static void centerOnScreen(Component c, int xOffset, int yOffset) {
		int x,y,xMax,yMax;
		Rectangle screen = c.getGraphicsConfiguration().getBounds();
		Rectangle rc = c.getBounds();
		xMax = screen.width - rc.width;
		yMax = screen.height - rc.height;
		
		x = (int)screen.getCenterX() - (int)rc.getCenterX() + xOffset;
		y = (int)screen.getCenterY() - (int)rc.getCenterY() + yOffset;

		if ((x < 0) ||  (x > xMax)) {
			x = 0;
		}
		
		if ((y < 0) || (y > yMax)) {
			y = 0;
		}
		
		c.setLocation(x,y);
	}
	
	public static void centerAonB(Component a, Component b) {
		centerAonB(a, b, 0, 0);
	}
	
	public static void centerAonB(Component a, Component b, int xOffset, int yOffset) {
		
		int x,y,xMax,yMax;
		Rectangle screen = a.getGraphicsConfiguration().getBounds();
		Rectangle ra = a.getBounds();
		Rectangle rb = b.getBounds();
		xMax = screen.width - ra.width;
		yMax = screen.height - ra.height;
		
		if (b.isShowing()) {
			// center on app
			x = (int)rb.getCenterX() - (int)ra.getCenterX() + xOffset;
			y = (int)rb.getCenterY() - (int)ra.getCenterY() + yOffset;
		} else {
			// center on screen
			x = (int)screen.getCenterX() - (int)ra.getCenterX() + xOffset;
			y = (int)screen.getCenterY() - (int)ra.getCenterY() + yOffset;
		}		
		
		if ((x < 0) ||  (x > xMax)) {
			x = 0;
		}
		
		if ((y < 0) || (y > yMax)) {
			y = 0;
		}
		
		a.setLocation(x,y);
	}
	
}


/*
 * $Log: ToolKit.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.1  2004/10/05 05:40:52  carlito
 * About fully functionnal, added a little toolkit for windows in uitools
 *
 */