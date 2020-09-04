/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionMultipleAbstract.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */

package com.simpledata.bc.uicomponents.bcoption.viewers;


import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable;
import com.simpledata.bc.uitools.SButtonIcon;

import org.apache.log4j.Logger;


/**
 * An option viewer for Transactions. <BR>
 * they are presented in a Table
 */
public abstract class OptionMultipleAbstract extends JPanel 
									implements NamedEventListener,
									BCOptionJTable.Interface {
	private static final Logger m_log = Logger.getLogger( OptionMultipleAbstract.class ); 
										
	private WorkSheet rootWorkSheet;
	private SButtonIcon plusButton;
	private SButtonIcon deleteButton;
	private JPanel jPanel;
	private JScrollPane jScrollPane;
	protected BCOptionJTable jTable;
	
	protected OptionMultipleAbstract(WorkSheet ws) {
		this.rootWorkSheet = ws;
		initialize();
		ws.addNamedEventListener(this);
		refreshOptionListeners();
	}
	

	/** create slice * */
	public abstract void createSlice();
	
	
	/** 
	 * the value of the following object has changed, 
	 * please commit it
	 * **/
	//protected abstract void commitDataChange(int row, int column,Object value); 
	
	/** return the BCOption at this row in the table **/
	protected abstract BCOption getOptionAt(int row);
	
	
	private BCOptionJTable getJTable() {
		if (jTable == null) {
			jTable = new BCOptionJTable(this);
			jTable.setRowSelectionAllowed(true);
			jTable.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					jTableMouseEvent(evt);
				}
				public void mouseReleased(MouseEvent evt) {
					jTableMouseEvent(evt);
				}
			});
		}
		return jTable;
	}
	//----------------------------------------------------------//
	

	protected final void fireTableDataChanged() {
		getJTable().fireTableDataChanged();
		getJTable().repaint();
	}
	
	protected final  void repaintTable() {
		getJTable().repaint();
	}
	
	protected final WorkSheet getRootWorkSheet() {
		return rootWorkSheet;
	}
	
	
	
	/** delete Selected Slice * */
	public final void deleteSlice() {
		int row = getJTable().getSelectedRow();
		if (row < 0)
			return; // no delete
		
		BCOption o = getOptionAt(row);
		m_log.info( "remove at: ["+row+"] o ["+o+"]" );
		rootWorkSheet.removeOption(o);
	}
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new java.awt.BorderLayout());
		this.add(getJPanel(), java.awt.BorderLayout.NORTH);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		// TODO dinamycally adapat this
		this.setPreferredSize(new Dimension(OptionDefaultPanel.DEF_WIDTH,
				OptionDefaultPanel.DEF_HEIGHT * 4));
	}
	
	
	
	
	/**
	 * Event on the Jtable (open popup ??)
	 */
	protected final void jTableMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			final int row = getJTable().getSelectedRow();
			JPopupMenu jp = new JPopupMenu();
			if (row > -1) {
				JMenuItem jmi = new JMenuItem(deleteButton.getToolTipText(),
						deleteButton.getIcon());
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						deleteSlice();
					}
				});
				jp.add(jmi);
			}
			JMenuItem jmi = new JMenuItem(plusButton.getToolTipText(),
					plusButton.getIcon());
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createSlice();
				}
			});
			jp.add(jmi);
			jp.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected final JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new javax.swing.JPanel();
			java.awt.FlowLayout layFlowLayout1 = new java.awt.FlowLayout();
			layFlowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
			jPanel.setLayout(layFlowLayout1);
			jPanel.add(getPlusButton(), null);
			jPanel.add(getDeleteButton(), null);
		}
		return jPanel;
	}
	
	
	private final JButton getPlusButton() {
		if (plusButton == null) {
			plusButton = new SButtonIcon(Resources.iconPlus);
			plusButton.setPreferredSize(new Dimension(20, 20));
			plusButton.setToolTipText(Lang.translate("Add"));
			plusButton.setBorderPainted(false);
			plusButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createSlice();
				}
			});
		}
		return plusButton;
	}
	
	private final JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton = new SButtonIcon(Resources.iconDelete);
			deleteButton.setPreferredSize(new Dimension(20, 20));
			deleteButton
					.setToolTipText(Lang.translate("Delete selected"));
			deleteButton.setBorderPainted(false);
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteSlice();
				}
			});
		}
		return deleteButton;
	}
	
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}
	
	//------------------ TABLE STUFF --------------//
	

	
	
	/**
	 * register a listner for all options
	 */
	private void refreshOptionListeners() {
		int max = getRowCount();
		for (int i = 0; i < max; i++) {
			getOptionAt(i).addNamedEventListener(this);
		}
	}
	
	
	/**
	 * Catch events for worksheet and option changes
	 */
	public final void eventOccured(NamedEvent e) {
		
		if (getJTable() != null) {
			
			if (e.getSource() == getRootWorkSheet()) 
			if (e.getEventCode() == NamedEvent.WORKSHEET_OPTION_ADDED
					|| e.getEventCode() == NamedEvent.WORKSHEET_OPTION_REMOVED) {
				fireTableDataChanged();
				refreshOptionListeners();
			}
			
			if (e.getSource() instanceof BCOption) {
				repaintTable();
			}
		}
	}
}

/*
 * $Log: OptionMultipleAbstract.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.11  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.10  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.9  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.8  2004/05/18 17:22:06  perki
 * Better icons management
 *
 * Revision 1.7  2004/05/18 17:04:26  perki
 * Better icons management
 *
 * Revision 1.6  2004/05/18 15:51:26  perki
 * Better icons management
 *
 * Revision 1.5  2004/05/18 10:48:38  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/18 10:10:27  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/05/14 16:00:41  perki
 * Nice option table
 *
 * Revision 1.2  2004/05/14 15:27:02  perki
 * Nice option table
 *
 * Revision 1.1  2004/05/14 14:22:16  perki
 * *** empty log message ***
 *
 */