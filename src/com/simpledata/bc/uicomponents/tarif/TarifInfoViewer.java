/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: TarifInfoViewer.java,v 1.2 2007/04/02 17:04:31 perki Exp $
 */
package com.simpledata.bc.uicomponents.tarif;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import com.simpledata.bc.*;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.tools.*;
import com.simpledata.bc.uitools.SButtonIcon;

/**
 * Defines a panel displaying name and description for a certain Tarif
 */
public class TarifInfoViewer extends JPanel {
	
	private JPanel emptyPanel= null;
	private JLabel emptyTitle;
	private JLabel emptyBlankLabel1;
	private JLabel emptyBlankLabel2;

	//private JLabel title;
	private JLabel nameLabel;
	private JTextFieldNamedTitle nameField;
	private JLabel descriptionLabel;
	private JScrollPane textScroll;
	private JTextAreaNamedDescription descriptionField;
	
	SButtonIcon saveButton;
	
	private TarifViewer tv;
	
	public TarifInfoViewer(TarifViewer tv) {
		this.tv = tv;
		initComponents();
	}

	public void refresh() {
		nameField.setNamedObject(tv.getTarifAtWork());
		descriptionField.setNamedObject(tv.getTarifAtWork());
	}

	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		// Init of empty panel
		emptyPanel= new JPanel();
		emptyTitle= new JLabel();
		emptyBlankLabel1= new JLabel();
		emptyBlankLabel2= new JLabel();

		emptyTitle.setFont(new Font("Arial Black", 0, 14));
		emptyTitle.setForeground(new Color(153, 0, 0));
		//emptyTitle.setText("No tarif selected");
		BC.langManager.register(emptyTitle, "No tarif selected");
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 0;
		gridBagConstraints.insets= new Insets(5, 5, 0, 0);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		emptyPanel.add(emptyTitle, gridBagConstraints);

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 1;
		gridBagConstraints.gridy= 0;
		gridBagConstraints.fill= GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx= 1.0;
		emptyPanel.add(emptyBlankLabel1, gridBagConstraints);

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 1;
		gridBagConstraints.fill= GridBagConstraints.VERTICAL;
		gridBagConstraints.weighty= 1.0;
		emptyPanel.add(emptyBlankLabel2, gridBagConstraints);

		// Init of standard Panel

		//title= new JLabel();
		nameLabel= new JLabel();
		nameField= new JTextFieldNamedTitle(null,true) {
			public void editionStopped() {}
			public void editionStarted() {}
		};
		descriptionLabel= new JLabel();
		textScroll= new JScrollPane();
		descriptionField= new JTextAreaNamedDescription() {
			public void editionStopped() {saveButton.setEnabled(false);}
			public void editionStarted() {saveButton.setEnabled(true);}};


		setLayout(new GridBagLayout());

//		title.setFont(new Font("Arial Black", 0, 14));
//		title.setText("Tarif");
//		BC.langManager.register(title, "Tarif");
//		gridBagConstraints= new GridBagConstraints();
//		//gridBagConstraints.gridwidth = 2;
//		gridBagConstraints.insets= new Insets(5, 5, 8, 0);
//		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
//		add(title, gridBagConstraints);




		//nameLabel.setText("Name");
		BC.langManager.register(nameLabel, "Name");
		nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 0;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets= new Insets(3, 7, 0, 10);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		add(nameLabel, gridBagConstraints);
		
		//nameField.setText("jTextField1");
		nameField.setMinimumSize(new Dimension(200, 22));
		nameField.setPreferredSize(new Dimension(200, 22));
		//nameField.setEnabled(false);
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 1;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill= GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx= 0.8;
		gridBagConstraints.insets= new Insets(1, 5, 0, 10);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;

		add(nameField, gridBagConstraints);

		
		// Building a spacer 
		// This will make a proportional blank part of 1 fifth
		
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 1;
		gridBagConstraints.gridy= 1;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill= GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx= 0.2;
		
		add(new JLabel(), gridBagConstraints);
		
		//descriptionLabel.setText("Description");
		BC.langManager.register(descriptionLabel, "Description");
		descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(Font.BOLD));
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 2;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets= new Insets(3, 7, 0, 10);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		add(descriptionLabel, gridBagConstraints);

		
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
	
		textScroll.setMinimumSize(new Dimension(200, 44));
		textScroll.setPreferredSize(new Dimension(200, 44));
		textScroll.setViewportView(descriptionField);

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 3;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets= new Insets(1, 5, 5, 10);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		gridBagConstraints.fill= GridBagConstraints.BOTH;
		gridBagConstraints.weightx= 0.8;
		gridBagConstraints.weighty= 1.0;
		add(textScroll, gridBagConstraints);

		saveButton= new SButtonIcon(Resources.iconSave);
		saveButton.setEnabled(false);
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				save();
			}});
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 4;
		//gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets= new Insets(3, 5, 5, 10);
		gridBagConstraints.anchor= GridBagConstraints.SOUTHEAST;
		gridBagConstraints.gridwidth= 1;
		gridBagConstraints.weightx= 1.0;
		add(saveButton, gridBagConstraints);
	
	}

	void save() {
		descriptionField.stopEditing();
	}

}

/**
 *  $Log: TarifInfoViewer.java,v $
 *  Revision 1.2  2007/04/02 17:04:31  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:45  perki
 *  First commit on sourceforge
 *
 *  Revision 1.6  2004/10/12 17:49:10  carlito
 *  Simulator split problems solved...
 *  description pb solved
 *
 *  Revision 1.5  2004/08/05 00:23:44  carlito
 *  DispatcherCase bugs corrected and aspect improved
 *
 *  Revision 1.4  2004/07/22 15:12:35  carlito
 *  lots of cleaning
 *
 *  Revision 1.3  2004/05/06 07:06:25  perki
 *  WorkSheetPanel has now two new methods
 *
 *  Revision 1.2  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.1  2004/03/22 19:33:14  perki
 *  step 2
 *
 */