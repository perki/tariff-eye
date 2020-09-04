/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 */
package com.simpledata.uitools;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * A utility that shows all properties accessible by
 * UIManger, and their value
 */
public class UIBrowser extends JFrame {
	
	public String[][] tableData;
	public Class[] classes;
	
	public static void main(String a[]) {
		go();
	}
	
	/**
	 * open the UIBrowser
	 *
	 */
	public static void go() {
		new UIBrowser();
	}
	
	public UIBrowser() {
		super("UIBrowser");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		classes = new Class[] 
			{null, Color.class, Font.class, Icon.class};
		
		
		getUIInfos(null);
		
		
		
		TableModel dataModel = new AbstractTableModel() {
	          public int getColumnCount() {return 2; }
	          public int getRowCount() { 
	          	return (tableData == null) ? 0 : tableData.length;	
	          }
	          public Object getValueAt(int row, int col) { 
	          	return tableData[row][col]; 
	          }
	          public boolean isCellEditable(int r, int c) {
	          	return true;
	          }
	      };
	      final JTable table = new JTable(dataModel);
	      JScrollPane scrollpane = new JScrollPane(table);
	      
	      getContentPane().add(scrollpane,BorderLayout.CENTER);
	      
	      final JComboBox jc = new JComboBox(classes);
	      jc.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getUIInfos((Class) jc.getSelectedItem());
				
				table.revalidate();
				table.repaint();
			}});
	      getContentPane().add(jc,BorderLayout.NORTH);
	      
	      setSize(600,600);
	      setVisible(true);
	      show();
	}
	
	
	public void getUIInfos(Class c) {
		UIDefaults uiDefaults = UIManager.getDefaults();
		
		
		Enumeration en = uiDefaults.keys();
		
		
		Vector v = new Vector();
		HashMap corres = new HashMap();
		Object k = "";
		while ( en.hasMoreElements()) {
			k = en.nextElement();
			Object val = uiDefaults.get(k);
			if (c == null || 
					(val != null && c.isAssignableFrom(val.getClass()))) {
				v.add(k.toString());
				corres.put(k.toString(),k);
			}
		
		}
		
		
		
		// sort elements and save to tableData
		Collections.sort(v);
		en = v.elements();
		
		tableData = new String[v.size()][2];
		for (int i = 0; en.hasMoreElements(); i++)
		{
		    Object key = en.nextElement();
		    Object val = uiDefaults.get(corres.get(key));
		    tableData[i][0] = key.toString();
		    tableData[i][1] = (val != null) ? val.toString() : "(null)";
		} 
		
	}
	
}

/*
 * $Log: UIBrowser.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.4  2004/10/18 17:15:15  carlito
 * Stree enabling drag over root node when show root handle false
 *
 * Revision 1.3  2004/06/15 06:14:06  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/06/08 17:17:50  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/06/08 17:01:33  perki
 * *** empty log message ***
 *
 */