/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: MergingMenu.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;

import java.awt.Component;
import java.awt.event.*;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.*;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.tools.Lang;

/**
 * A JMenu with all the necessary tool to start a Merge
 */
public class MergingMenu extends JMenu {
	
	/** The tarificatio I will merge on */
	private Tarification tarification;
	
	/** The Component I'm working on */
	protected Component origin;
	
	
	private JMenu mImportFromOpen;
	
	/**
	 * @param t the tarification I will merge on (may be modified with 
	 * setTarification
	 * @param origin the graphical component relative to me 
	 */
	public MergingMenu(Tarification t, final Component origin) {
		super(Lang.translate("Import"));
		setTarification(t);
		this.origin = origin;
		// from file
		JMenuItem mImportFromFile = new JMenuItem(Lang.translate("From file"));
		mImportFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Mergeator.mergeFromFileOn(getTarification(),origin);
			}
		});
		add(mImportFromFile);
		
		mImportFromOpen 
			= new JMenu(Lang.translate("From documents at work"));
		
		add(mImportFromOpen);
		
		addMenuListener(new MenuListener() {
			public void menuCanceled (MenuEvent e) {}

			public void menuDeselected (MenuEvent e) {
				mImportFromOpen.removeAll();
			}

			public void menuSelected (MenuEvent e) {
				buildChildMenus();
			}
		});
		
		
	}
	
	private void buildChildMenus() {
		BC.TarificationModifiers[] tms = BC.bc.getAllTarificationModifiers();
		for (int i = 0; i < tms.length; i++) {
			Iterator j = tms[i].tarifModifierGetTarifications().iterator();
		
			while (j.hasNext()) {
				final Tarification t = (Tarification) j.next();
				
				// do not merge with my self
				if (t == tarification) break;
				
				//--------- add the Item ----------------//
				JMenuItem mImport = new JMenuItem();
				mImport.setIcon(t.getHeader().getIcon());
				mImport.setText(t.getHeader().getTitle()
							+" - "+tms[i].tarifModifierGetTitle());
				mImport.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						MergingMonitorUI mmui 
						= new MergingMonitorUI(tarification,t,origin);
						Mergeator.fillTarificationDwithS(tarification,t,mmui);
					}
				});
				mImportFromOpen.add(mImport);
			}
		}
		mImportFromOpen.validate();
	}
	


	/**
	 * @return Returns the tarification.
	 */
	public Tarification getTarification() {
		return tarification;
	}
	/**
	 * @param tarification The tarification to set.
	 */
	public void setTarification(Tarification tarification) {
		setEnabled(tarification != null);
		this.tarification = tarification;
	}
}
/*
 * $Log: MergingMenu.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.3  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.2  2004/07/20 16:19:46  perki
 * merging menus
 *
 * Revision 1.1  2004/07/19 17:39:08  perki
 * *** empty log message ***
 *
 */