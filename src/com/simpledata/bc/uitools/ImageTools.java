/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: ImageTools.java,v 1.2 2007/04/02 17:04:26 perki Exp $
*/

package com.simpledata.bc.uitools;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.simpledata.uitools.ImageUtils;
/**
* Image Management and creation tools
*/
public class ImageTools {
    private static final Logger m_log = Logger.getLogger( ImageTools.class );
    
	
	
	/**
	* get an the passed icon with a square and a char draw<BR>
	* The drawn chars will be in "Arial-BOLD-12"
	* @param icon the source Icon
	* @param sColor the colorOfTheSquare
	* @param sWidth the width of the Square
	* @param cColor the colorOfThe Character
	* @param c the character itself
	*/
	public static ImageIcon 
		getIconSquareChar(ImageIcon icon, Color sColor, int sWidth,
											Color cColor, char c) {
		Image image = icon.getImage();
		
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		
		BufferedImage result = ImageUtils.getBufferedImage(w,h);
		
		Graphics2D g2 = (Graphics2D) result.getGraphics();
		g2.setColor(new Color(0, 0, 0, 0));
		g2.setPaintMode();
		g2.fillRect(0, 0, h, w);
		// draw Image
		g2.drawImage(image, 0, 0, null);
		
		//-------- Square
		g2.setColor(sColor);
		int l = sWidth;
		g2.fillRect((w-l)/2, (h-l)/2,l,l);
		
		//-------- Char
		g2.setColor(cColor);
		Font font = Font.decode("Arial-BOLD-12");
		g2.setFont(font);
		FontMetrics fm = g2.getFontMetrics();
		int x = (w - fm.charWidth(c)) / 2;
		int y = (h + fm.getHeight()-4) / 2;
		g2.drawString(""+c,x,y);
		g2.dispose();
		return new ImageIcon(result);
	}
	
	/**
	 * Draw an Icon tag on Icon dest
	 * @param location if null then 0,0 is used
	 */
	public static ImageIcon drawIconOnIcon
		(ImageIcon dest,ImageIcon tag,Point location) {
		if (location == null) location = new Point(0,0);
			if ((dest == null) || (tag == null)) {
			    m_log.error("One of my argument is nul dest:"+dest+
									" tag:"+tag,new Exception()); 
				return null;
			}
		Image image = dest.getImage();
		Image iTag = tag.getImage();
		
		if (iTag == null) {
		    m_log.debug("iTag image was null... awaiting exception...");
		}
		
		int w = image.getWidth(null);
		int h = image.getHeight(null);

		BufferedImage result = ImageUtils.getBufferedImage(w,h);

		Graphics2D g2 = (Graphics2D) result.getGraphics();
		g2.setPaintMode();
		
		g2.setColor(new Color(0, 0, 0, 0));

		g2.fillRect(0, 0, w, h);
		
		// draw Image dest
		g2.drawImage(image, 0, 0,null);
		
		// draw Image tag
		g2.drawImage(iTag, location.x, location.y, null);
		g2.dispose();
		return new ImageIcon(result);
	}
}
/* $Log: ImageTools.java,v $
/* Revision 1.2  2007/04/02 17:04:26  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:41  perki
/* First commit on sourceforge
/*
/* Revision 1.11  2004/11/22 18:44:39  carlito
/* Affichage des DispatcherCase modifications quasi-finale
Modification du WorkSheetPanelBorder pour qu'il n'ai plus la bougeotte avec le titre...
/*
/* Revision 1.10  2004/10/20 08:19:39  perki
/* *** empty log message ***
/*
/* Revision 1.9  2004/09/23 06:27:56  perki
/* LOt of cleaning with the Logger
/*
/* Revision 1.8  2004/06/18 18:25:39  perki
/* *** empty log message ***
/*
/* Revision 1.7  2004/06/16 07:49:28  perki
/* *** empty log message ***
/*
* Revision 1.6  2004/06/15 06:13:37  perki
* *** empty log message ***
*
* Revision 1.5  2004/05/22 17:20:46  perki
* Reducs are visibles
*
* Revision 1.4  2004/02/18 16:59:29  perki
* turlututu
*
* Revision 1.3  2004/02/18 11:00:57  perki
* *** empty log message ***
*
* Revision 1.2  2004/02/02 18:19:15  perki
* yupeee3
*
* Revision 1.1  2004/01/23 13:52:07  perki
* *** empty log message ***
*
*/