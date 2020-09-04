/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* Deals with look and feels
*/

package com.simpledata.bc.uitools;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import org.apache.log4j.Logger;

import com.incors.plaf.alloy.AlloyLookAndFeel;
import com.incors.plaf.alloy.AlloyTheme;
import com.incors.plaf.alloy.DefaultAlloyTheme;
import com.incors.plaf.alloy.themes.acid.AcidTheme;
import com.incors.plaf.alloy.themes.bedouin.BedouinTheme;
import com.incors.plaf.alloy.themes.glass.GlassTheme;
import com.simpledata.bc.BC;
import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.filetools.ImageFileChooser;

/**
* Look And Feel Manager for BC
* !!!
* 
* Note: in order to use the alloy look and fell you need
* a license code, you may request a trial or by one from Incors Gmbh
*/
public class BCLookAndFeel {
    private static final Logger m_log = Logger.getLogger(BCLookAndFeel.class);
	
	static boolean FORCE_LAF_ON_MAC=false;
	static boolean ENABLE_CLEAR_LOOK= true;

	static final int NONE= -1;
	static final int PLASTIC = 0; // for jgoodies
	static final int MAC_NATIVE = 1;
	static final int WINDOWS =2 ; //only sdk 1.4.2
	static final int GTK =3 ; //only sdk 1.4.2
	static final int ALLOY= 4;
	
	static int THEME= ALLOY;
	
	/** used for theme switching **/
	private static AlloyLookAndFeel alloyLAF;
	
	private static String alloyLicenseCode = null;

	/**
	* set a LookAndFeel
	*/
	public static void setLAF() {
		if ((THEME == ALLOY) && (alloyLicenseCode == null)) {
			THEME = WINDOWS;
		}
		// if apple designed JVM then no LAF
		if (!FORCE_LAF_ON_MAC) {
			if (System.getProperty("mrj.version") != null) {
				// This is a MAC
				// change the icon size of file viewers
				THEME = MAC_NATIVE;
				m_log.debug("Detected mac native theme possibility..."); 
			}
		}
		
		try {

		    switch (THEME) {
		    case GTK :
		        UIManager.setLookAndFeel(
		                "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
		        );
		        m_log.debug("Loaded GTK theme..."); 
		        break;
		    case WINDOWS :
		        UIManager.setLookAndFeel(
		                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
		        );
		        m_log.debug("Loaded Windows theme..."); 
		        break;
		    case PLASTIC :
		        
		        //Was used for the Looks LOOK AND FEEL
//		        UIManager.put(
//		                Options.USE_SYSTEM_FONTS_APP_KEY,
//		                Boolean.TRUE);
//		        Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
//		        Options.setDefaultIconSize(new Dimension(16, 16));
//		        Options.setPopupDropShadowEnabled(true);
//		        
//		        UIManager.put("jgoodies.popupDropShadowEnabled", Boolean.TRUE);
//		        Options.setUseNarrowButtons(true);
//		        //PlasticLookAndFeel.setMyCurrentTheme(new DesertBlue());
//		        
//		        UIManager.setLookAndFeel(new ExtWindowsLookAndFeel());
		        
		        break;
		    case MAC_NATIVE :
		        ImageFileChooser.defaultIconDimension = new Dimension(16,16);
		        FileManagement.defaultIconDimension = new Dimension(16,16);
		        m_log.debug("Loaded mac native theme..."); 
		        break;
		    case ALLOY :
		        AlloyLookAndFeel.setProperty("alloy.licenseCode", 
		        alloyLicenseCode);
		        alloyLAF = new AlloyLookAndFeel();
		        UIManager.setLookAndFeel(alloyLAF);
		        setAlloyTheme(BC.getParameterStr("ALLOY_LAF"));
		        
		        JFrame.setDefaultLookAndFeelDecorated(true);
		        JDialog.setDefaultLookAndFeelDecorated(true);
		        m_log.debug("Loaded Alloy theme..."); 
		        break;
		    }
			
			// was used with the looks LAF
//			if (ENABLE_CLEAR_LOOK)
//				ClearLookManager.setMode(ClearLookMode.DEBUG);
			
		} catch (UnsupportedLookAndFeelException e) {
			m_log.fatal(
				"BCLAF: Problems while setting the Look and Feel : ",
				e);
		}catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		

	}
	
	
	/** Add a theme chooser to this Menu 
	 * . HAS NO EFFECT
	 * **/
	public static void addThemeMenu(JMenu jm) {
	    // return
	}
	
	private static final String[] alloyThemes 
	= {"Alloy","Glass","Acid","Bedouin"};
	private static void setAlloyTheme(String name) {
		if (alloyLAF == null) return;
		int j = 0;
		for (int i = 0; i < alloyThemes.length; i++) {
		if (alloyThemes[i].equals(name))
		    j = i;
		}
		AlloyTheme temp = null;
		
		switch (j) {
		case 1: 
		temp = new GlassTheme();
		break;
		case 2: 
		temp = new AcidTheme();
		break;
		case 3: 
		temp = new BedouinTheme();
		break;
		default: 
		temp = new DefaultAlloyTheme();
		break;
		}
		
		alloyLAF.setTheme(temp,true);
		BC.setParameter("ALLOY_LAF",name);
	}
		
	/**
	 * Returns the correct Border according to the actual 
	 * Look and Feel
	 */
	public static Border getSButtonBorder() {
	    Border result = null;
	    
	    switch(THEME) {
	    		case MAC_NATIVE :
	    		    result = new SoftBevelBorder(BevelBorder.RAISED);
	    		    break;
	    		default:
	    		    result = new EmptyBorder(new Insets(0,0,0,0));
	    }
	    
	    return result;
	}
	
}

/* $Log: BCLookAndFeel.java,v $
/* Revision 1.2  2007/04/02 17:04:26  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:41  perki
/* First commit on sourceforge
/*
/* Revision 1.43  2004/10/19 08:35:34  carlito
/* dicts corrected, BCLook&Feel messages changed
/*
/* Revision 1.42  2004/10/18 16:48:10  carlito
/* JComboBox bug corrected
/*
/* Revision 1.41  2004/10/18 10:34:53  carlito
/* editor pane without border
/*
/* Revision 1.40  2004/10/15 08:05:28  perki
/* *** empty log message ***
/*
/* Revision 1.39  2004/10/14 13:53:28  perki
/* *** empty log message ***
/*
/* Revision 1.38  2004/10/14 11:16:33  perki
/* Ehanced security in demo mode
/*
/* Revision 1.37  2004/09/29 12:40:19  perki
/* Localization tarifs
/*
/* Revision 1.36  2004/09/23 07:30:55  perki
/* *** empty log message ***
/*
/* Revision 1.35  2004/09/23 06:27:56  perki
/* LOt of cleaning with the Logger
/*
/* Revision 1.34  2004/09/14 12:07:26  carlito
/* commit de protection
/*
/* Revision 1.33  2004/09/10 16:51:05  perki
/* *** empty log message ***
/*
/* Revision 1.32  2004/09/09 18:53:50  perki
/* purchased Alloy
/*
/* Revision 1.31  2004/09/06 07:16:26  carlito
/* *** empty log message ***
/*
/* Revision 1.30  2004/08/25 07:35:37  kaspar
/* - Removed napkin LAF
/*
/* Revision 1.29  2004/08/23 08:44:11  kaspar
/* ! Normalized line endings
/* ! License code for another month in BCLoockAndFeel
/*
/* Revision 1.28  2004/08/23 07:40:02  jvaucher
/* Added the fee reports. Some changes should be done:
/* - Review the template FeeReport.jrxml. The length of the fields is
/* to small in some cases.
/* - Maybe some node should be reported.
/* - Should use a new common class for the numbering of the section
/*
/* Revision 1.27  2004/08/18 13:12:50  kaspar
/* + Added napkin look and feel. Its purpose is to convey an
/*   unfinished feeling to anyone looking at the gui, therefore
/*   making him believe that its not finished.
/* ! Bugfix: DispatcherSimple must be subclass of DispatcherAbstract,
/*   forget to change that yesterday.
/*
/* Revision 1.26  2004/08/02 10:44:51  carlito
/* *** empty log message ***
/*
/* Revision 1.25  2004/07/30 15:41:57  carlito
/* *** empty log message ***
/*
/* Revision 1.24  2004/07/19 07:56:50  perki
/* Alloy LAF
/*
/* Revision 1.23  2004/07/06 17:31:25  carlito
/* Desktop manager enhancedSButton with border on macsdesktop size persistent
/*
/* Revision 1.22  2004/06/28 16:47:54  perki
/* icons for tarif in simu
/*
/* Revision 1.21  2004/06/28 13:22:37  perki
/* icons are 16x16 for macs
/*
/* Revision 1.20  2004/06/16 19:04:26  perki
/* *** empty log message ***
/*
/* Revision 1.19  2004/06/16 07:49:28  perki
/* *** empty log message ***
/*
/* Revision 1.18  2004/05/10 19:00:51  perki
/* Better amount option viewer
/*
/* Revision 1.17  2004/05/07 15:50:06  perki
/* *** empty log message ***
/*
/* Revision 1.16  2004/05/05 16:52:29  carlito
/* *** empty log message ***
/*
/* Revision 1.15  2004/04/09 07:16:52  perki
/* Lot of cleaning
/*
/* Revision 1.14  2004/03/24 13:11:14  perki
/* Better Tarif Viewer no more null except
/*
/* Revision 1.13  2004/03/23 12:14:17  carlito
/* *** empty log message ***
/*
/* Revision 1.12  2004/03/23 10:59:10  perki
/* gdksagdfjs kdf
/*
/* Revision 1.11  2004/03/22 22:53:54  perki
/* gdksagdfjs kdf
/*
/* Revision 1.10  2004/03/22 21:40:19  perki
/* dodo
/*
* Revision 1.9  2004/03/17 15:53:42  carlito
* *** empty log message ***
*
* Revision 1.8  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.7  2004/01/22 15:40:51  perki
* Bouarf
*
* Revision 1.6  2004/01/22 13:03:32  perki
* *** empty log message ***
*
* Revision 1.5  2004/01/14 13:56:48  perki
* *** empty log message ***
*
* Revision 1.4  2004/01/13 16:13:22  perki
* *** empty log message ***
*
* Revision 1.3  2004/01/13 12:35:33  perki
* doc debug
*
* Revision 1.2  2004/01/12 11:27:12  perki
* *** empty log message ***
*
* Revision 1.1  2004/01/10 08:11:44  perki
* UI addons and Look And Feel
*
*
*/