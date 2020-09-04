/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 29 nov. 2004
 */
package com.simpledata.bc.uicomponents;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;

/**
 * This panel allow to browse the BC parameters in Simple mode.
 * No lang support.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: DebugPreferencePanel.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
class DebugPreferencePanel extends JPanel {

	/** Panel name */
	private final static String PANEL_NAME = "Debug";
	/** Table model displaying the parameters */
	private final DefaultTableModel m_paramTable;
	/** Refresh button */
	private final JButton m_refreshButton;
	
	/** Constructor */
	DebugPreferencePanel() {
		String[] columnName = {"Key","Value"};
		m_paramTable = new DefaultTableModel(columnName, 0);
		m_refreshButton = new JButton("Refresh");
		buildMe();
	}
	
	/** Build the panel */
	private void buildMe() {
		Border border = new TitledBorder(PANEL_NAME);
		this.setBorder(border);
		this.setName(PANEL_NAME);
		
		JScrollPane jsp = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JTable table = new JTable(m_paramTable);
		jsp.setViewportView(table);
		this.setLayout(new BorderLayout());
		this.add(jsp, BorderLayout.CENTER);
		
		m_refreshButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				retreiveParams();
			}});
		
		// set content
		retreiveParams();
		
		this.add(m_refreshButton, BorderLayout.SOUTH);
	}
	
	/** Set the content of the table with all BC parameters */
	private void retreiveParams() {
		Params params = BC.params;
		
		// clear table
		for (int i = m_paramTable.getRowCount(); i>=1; i--)
			m_paramTable.removeRow(i-1);
		// new content
		Enumeration enume = params.keys();
		while (enume.hasMoreElements()) {
			String key = (String)enume.nextElement();
			Object o = params.get(key);
			String value = o.toString();
			if (o instanceof String[]) {
			    String[] s = (String[]) o;
			    value = "";
			    for (int i = 0; i < s.length; i++) {
			        value += " ["+s[i]+"] ";
			    }
			}
			
			Object[] row = {key, value};
			m_paramTable.addRow(row);
		}
	}
}
