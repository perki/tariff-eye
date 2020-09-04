/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 4 oct. 2004
 * $Id: About.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.tools.Lang;

/**
 * A little about window for the application...
 */
public class About extends JDialog {

    /** Logger */
    private static Logger m_log = Logger.getLogger( About.class );
    
    private boolean xHeld = false;
    private boolean kHeld = false;
    private boolean oHeld = false;
    private boolean iHeld = false;
    private boolean sHeld = false;
    
    private boolean consoleOpenActionPerformed = false;
    private boolean setIsSimpleActionPerformed = false;

    private Desktop ownerDesktop;
        
    public About(Desktop desktop) {
        super(desktop, Lang.translate("About"),false);
        
        ownerDesktop = desktop;
        
        buildUI();
        
        attachListeners();
        
        showUI();
    }
    
    private void buildUI() {
        
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        
        JLabel imageAbout = new AboutImageWithText();
        
        getContentPane().add(imageAbout, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        JButton closeAbout = new JButton();
        closeAbout.setText(Lang.translate("Close"));
        closeAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }            
        });
        
        bottomPanel.add(closeAbout, gbc);
        
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

    }
    
    private void attachListeners() {
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
    }
    
    private void showUI() {

        pack();
        ToolKit.centerAonB(this, ownerDesktop);
        show();
        requestFocus();
        
    }
    
    private void exit() {
        this.dispose();
    }
    
    private void formKeyPressed(KeyEvent evt) {
		String keyString = (new Character(evt.getKeyChar())).toString();
		//m_log.debug("Key received : "+keyString);
		if (keyString.equals("x")) {
			xHeld = true;
		}
		if (keyString.equals("k")) {
			kHeld = true;
		}
		if (keyString.equals("o")) {
			oHeld = true;
		}
		if (keyString.equals("i")) {
			iHeld = true;
		}
		if (keyString.equals("s")) {
			sHeld = true;
		}
		
		// Console opening 
		if (xHeld && kHeld && oHeld) {
		    if (!consoleOpenActionPerformed) {
		        ownerDesktop.launchConsole();
		        consoleOpenActionPerformed = true;
		        exit();
		    }
		}
		
		// setIsSimple management
		if (xHeld && sHeld && iHeld) {
			if (!setIsSimpleActionPerformed) {
			    BC.setIsSimple(!BC.isSimple());
			    setIsSimpleActionPerformed = true;
			    exit();
			}
		}
		
    }

    private void formKeyReleased(KeyEvent evt) {
		String keyString = (new Character(evt.getKeyChar())).toString();
		boolean cancelConsole = false;
		boolean cancelSetSimple = false;
		if (keyString.equals("x")) {
			xHeld = false;
			cancelConsole = true;
			cancelSetSimple = true;
		}
		if (keyString.equals("s")) {
			sHeld = false;
			cancelSetSimple = true;
		}
		if (keyString.equals("i")) {
			iHeld = false;
			cancelSetSimple = true;
		}
		if (keyString.equals("k")) {
			kHeld = false;
			cancelConsole = true;
		}
		if (keyString.equals("o")) {
			oHeld = false;
			cancelConsole = true;			
		}
		
		if (cancelConsole) {
			consoleOpenActionPerformed = false;
		}
		if (cancelSetSimple) {
		    setIsSimpleActionPerformed = false;
		}

    }
    

    /**
     * This class creates an image with correct text on it
     */
	class AboutImageWithText extends JLabel {
		
		private Image imageAbout = null;

		
		public AboutImageWithText() {
			super();
			ImageIcon img = new ImageIcon(Resources.splashImagePath());
			imageAbout = img.getImage();
		
			Dimension d = new Dimension(img.getIconWidth(), img.getIconHeight());
			this.setMinimumSize(d);
			this.setPreferredSize(d);
			
		}
		
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(imageAbout, 0, 0,null);
			g.setColor(new Color(204, 0, 0));
			Font f = new Font("Dialog", 1, 14);
			f.deriveFont(Font.BOLD);
			g.setFont(f);
			g.drawString(Lang.translate("Version:")+SoftInfos.softVersion(), 20, 25);
		
		}
	}
    
}


/*
 * $Log: About.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.3  2004/10/05 05:40:52  carlito
 * About fully functionnal, added a little toolkit for windows in uitools
 *
 * Revision 1.2  2004/10/04 16:57:02  carlito
 * About finished
 *
 * Revision 1.1  2004/10/04 15:49:18  carlito
 * *** empty log message ***
 *
 */