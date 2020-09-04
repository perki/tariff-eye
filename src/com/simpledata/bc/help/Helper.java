/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 27 sept. 2004
 */
package com.simpledata.bc.help;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.JHelpContentViewer;
import javax.help.JHelpNavigator;
import javax.help.NavigatorView;
import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.InternalFrameDescriptor;

/**
 * This class contains tool for the helper. Current version is a very simple one.
 * It just allows to show a window with the help system. None of the contextual
 * purpose are implemented here.
 * The help system use JavaHelp 2.0. Package jhall.jar is necessary.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: Helper.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
public class Helper {
	/** Singleton instance */
	private static final Helper instance;
	
	static {
		// Singleton instance
		instance = new Helper();
	}

	/** Location of the .hs file */
	private static final String HELPER_LOCATION =
		"resources/help/tariffeye.hs"; // TODO perhaps use the resources class
	/** Home page of the helper. One of ID defined into map.jhm */
	private static final String HOME_ID = "manual_toc";
	
	/** Some JavaHelp objects */
	private HelpSet m_helpSet;
	private final HelpBroker m_helpBroker;
	/** A Light Logger Yield Our Brains, Aim to Save Energy */
	private static final Logger m_log = Logger.getLogger(Helper.class);
	
	/** Construct the singleton instance */
	private Helper() {
		File hsFile = new File(HELPER_LOCATION);
		try {
			URL hsUrl = hsFile.toURL();
			m_helpSet = new HelpSet(null, hsUrl);
		} catch (Exception e) {
			m_log.error("Exception while constructing the HelpSet", e);
		} 
		m_helpBroker = m_helpSet.createHelpBroker();
	}
	
	/**
	 * @return an action listener which show the help system. When it's fired
	 * The default viewer of JavaHelp is shown in a new Frame. For TariffEye,
	 * rather use the showHelp() method. 
	 */
	public static ActionListener actionListener() {
		return new CSH.DisplayHelpFromSource(instance.m_helpBroker);
	}
	
	/**
	 * Show an internal frame, which contains the TariffEye help system.
	 */
	public static void showHelp() {
		// help panel
		JSplitPane helpPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		// content panel
		HelpSet hs = instance.m_helpSet;
		JHelpContentViewer jhcv = new JHelpContentViewer(hs);
		jhcv.setCurrentID(HOME_ID);
		helpPanel.add(jhcv, JSplitPane.RIGHT);
		
		// view: TOC
		NavigatorView nv = hs.getNavigatorView("TOC");
		JHelpNavigator xnav = (JHelpNavigator) nv.createNavigator(jhcv.getModel());
		helpPanel.add(xnav, JSplitPane.LEFT);
		
		// show the frame
		InternalFrameDescriptor ifd = new InternalFrameDescriptor();
		ifd.setCenterOnOpen(true);
		ifd.setInitialBounds(new Rectangle(1000,500));
		
		JInternalFrame frame = new JInternalFrame(
			Lang.translate("TariffEye Help"), 
			true,          // resizeable
			true,          // closeable
			true,          // maximizeable
			true           // iconifiable
		);
		frame.getContentPane().add(helpPanel);
		frame.setFrameIcon(Resources.iconHelp);
		BC.bc.popupJIFrame( frame, ifd );
	}
	
}
