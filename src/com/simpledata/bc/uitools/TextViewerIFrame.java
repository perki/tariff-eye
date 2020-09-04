/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: TextViewerIFrame.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

/**
 * A simple text viewer mainly for debugging purposes
 */
public class TextViewerIFrame extends JInternalFrame {

	private javax.swing.JPanel jContentPane= null;

	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTextPane textArea = null;
	private javax.swing.JButton jButton = null;
	/**
	 * 
	 */
	public TextViewerIFrame() {
		super();
		initialize();
	}

	/**
	 * @param title
	 */
	public TextViewerIFrame(String title,String text) {
		this();
		setTitle(title);
		textArea.setText(text);
	}



	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setSize(346, 270);
		this.setContentPane(getJContentPane());
		this.setClosable(true);  // Generated
		this.setResizable(true);  // Generated
		try {
			this.setSelected(false);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}  // Generated
		
	}
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane= new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);  // Generated
			jContentPane.add(getJButton(), java.awt.BorderLayout.NORTH);  // Generated
		}
		return jContentPane;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();  // Generated
			jScrollPane.setViewportView(getTextArea());  // Generated
		}
		return jScrollPane;
	}
	/**
	 * This method initializes textArea
	 * 
	 * @return javax.swing.JTextPane
	 */
	private javax.swing.JTextPane getTextArea() {
		if(textArea == null) {
			textArea = new javax.swing.JTextPane();  // Generated
		}
		return textArea;
	}
	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getJButton() {
		if(jButton == null) {
			jButton = new javax.swing.JButton();  // Generated
			jButton.setText("Evaluate Script in Console");  // Generated
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					evalScript(); 
				}

				
			});
		}
		return jButton;
	}

	/**
	 * 
	 */
	protected void evalScript() {
		Console.evaluate(textArea.getText());
		
	}
}  //  @jve:visual-info  decl-index=0 visual-constraint="10,10"


/**
 *  $Log: TextViewerIFrame.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:40  perki
 *  First commit on sourceforge
 *
 *  Revision 1.2  2004/04/09 07:16:52  perki
 *  Lot of cleaning
 *
 *  Revision 1.1  2004/02/19 20:20:11  perki
 *  nicer
 *
 */