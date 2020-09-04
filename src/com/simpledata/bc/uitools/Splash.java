/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: Splash.java,v 1.2 2007/04/02 17:04:26 perki Exp $
*/

package com.simpledata.bc.uitools;
/*
* Splash.java
*
*
*/
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.simpledata.bc.Resources;

/**
*
* The starting Splash Window 
*/
@SuppressWarnings("serial")
public class Splash extends JFrame {
	private SplashDialog sd ;
	
	public Splash() {
		super("Tariff Eye");
		setIconImage( (new ImageIcon(Resources.appIconPath())).getImage());
		sd = new SplashDialog(this);
		Rectangle rec = getGraphicsConfiguration().getBounds();
		Rectangle rec2 = getBounds();
		int w = (int)rec.getCenterX();
		int h = (int)rec.getCenterY();
		w = w - (int)rec2.getCenterX();
		h = h - (int)rec2.getCenterY();
		setLocation(w,h);
		show();
		toBack();
		
           
	}
	
	
	
	
	/** 
	 * change the step message 
	 * @param s UN-transalated Message;
	 * **/
	public void setStep(String s) {
	    step().setText(s);
	}
	
	/** change the Info String **/
	public void setInfo(String to) {
	    infos().setText(to);
	}
	
	
	public void byebye() {
		sd.dispose();
		dispose();
	}
	
	private JLabel step;
	private JLabel step() {
	    if (step == null) step = new JLabel("..."); 
	    return step;
	}

	private JTextField infos;
	private JTextField infos() {
	    if (infos == null) infos = new JTextField(); 
	    return infos;
	}
	
	@SuppressWarnings("serial")
	class SplashDialog extends JDialog {
		// Variables declaration  
		
		// End of variables declaration
		
		/** Creates new form Splash */
		
		public SplashDialog( Frame son) {
			super(son, false);
			
			this.setUndecorated(true);
			GraphicsConfiguration gc = GraphicsEnvironment.
			getLocalGraphicsEnvironment().
			getDefaultScreenDevice().
			getDefaultConfiguration();
			JLabel image = 
			    new JLabel(new ImageIcon(Resources.splashImagePath()));
			infos().setEditable(false);
			infos().setFont(new java.awt.Font("Dialog", 0, 24));
			infos().setHorizontalAlignment(SwingConstants.CENTER);
			javax.swing.border.TitledBorder tb 
			= new javax.swing.border.TitledBorder(null, 
			        "Informations:", 
			        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, 
			        javax.swing.border.TitledBorder.DEFAULT_POSITION, 
			        new java.awt.Font("Dialog", 1, 12));
			tb.setTitle("Informations:");
			infos.setBorder(tb);
			getContentPane().add(infos(), java.awt.BorderLayout.CENTER);
			getContentPane().add(step(), java.awt.BorderLayout.SOUTH);
			getContentPane().add(image, java.awt.BorderLayout.NORTH);
			pack();
			Rectangle screenRect = gc.getBounds();
			setLocation(screenRect.x + screenRect.width/2 - getSize().width/2,
			screenRect.y + screenRect.height/2 - getSize().height/2);
			show();
			toFront();
		}
		
	}
}
/**
* $Log: Splash.java,v $
* Revision 1.2  2007/04/02 17:04:26  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:41  perki
* First commit on sourceforge
*
* Revision 1.17  2004/10/04 08:47:09  perki
* Moved soft info medthods to a new class
*
* Revision 1.16  2004/09/29 12:40:19  perki
* Localization tarifs
*
* Revision 1.15  2004/09/29 09:47:14  perki
* New Splash system
*
* Revision 1.14  2004/09/28 10:23:08  perki
* *** empty log message ***
*
* Revision 1.13  2004/09/28 09:41:51  perki
* Clear action added to CreatorGold
*
* Revision 1.12  2004/09/28 08:55:22  perki
* Minor changes
*
* Revision 1.11  2004/09/27 13:30:14  perki
* register Dialog OK
*
* Revision 1.10  2004/09/27 13:19:10  perki
* register Dialog OK
*
* Revision 1.9  2004/09/27 08:40:06  perki
* *** empty log message ***
*
* Revision 1.8  2004/09/25 13:32:30  perki
* *** empty log message ***
*
* Revision 1.7  2004/05/22 08:39:36  perki
* Lot of cleaning
*
* Revision 1.6  2004/04/09 07:16:52  perki
* Lot of cleaning
*
* Revision 1.5  2004/03/06 14:24:50  perki
* Tirelipapon sur le chiwawa
*
* Revision 1.4  2004/03/06 11:49:22  perki
* *** empty log message ***
*
* Revision 1.3  2004/02/04 15:42:17  perki
* cleaning
*
* Revision 1.2  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.1  2003/12/05 15:53:39  perki
* start
*
*
*/