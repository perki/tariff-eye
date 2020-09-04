/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 16 f�vr. 2004
 *
 * $Id: TarifNameAndTypeSelectionStep.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception.wizards;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.conception.Creator;

/**
 * StepPanel  of the second step of tarif creation
 */
public class TarifNameAndTypeSelectionStep extends StepPanel {
    private static final Logger m_log = 
        Logger.getLogger( TarifNameAndTypeSelectionStep.class );
    
	private Creator tarificationCreator = null;
	//private Tarification tarification = null;
	private BCNode currentNode = null;
	
	private String title = null;
	private String description = null;
	private Object type = null;
	
	// Variables declaration - do not modify
	private JLabel blankLabel;
	private JPanel container;
	private JTextArea descriptionField;
	private JLabel descriptionLabel;
	private JScrollPane descriptionScroll;
	private JTextField titleField;
	private JLabel titleLabel;
	private JComboBox typeCombo;
	private JLabel typeLabel;
	// End of variables declaration
	
	
	public TarifNameAndTypeSelectionStep(NewTarifWizard ntw, Creator c) {
		super(ntw);

		this.tarificationCreator = c;
		
		initComponents();
	}
	
	private void initComponents() {
		GridBagConstraints gridBagConstraints;

		container = new JPanel();
		titleLabel = new JLabel();
		titleField = new JTextField();
		descriptionLabel = new JLabel();
		descriptionScroll = new JScrollPane();
		descriptionField = new JTextArea();
		typeLabel = new JLabel();
		typeCombo = new JComboBox();
		blankLabel = new JLabel();

		container.setLayout(new GridBagLayout());

		titleLabel.setText("Name");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(7, 7, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		container.add(titleLabel, gridBagConstraints);

		titleField.setMinimumSize(new Dimension(250, 22));
		titleField.setPreferredSize(new Dimension(250, 22));
		titleField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				titleFieldFocusLost();
			}
		});
		
		// add enter listener
		/*
		Action saveTitle = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				titleFieldFocusLost();
			}
		};
		titleField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"saveMe");
		titleField.getActionMap().put("saveMe",saveTitle);
		*/
		titleField.addKeyListener(new myKeyAdapter(this));
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(7, 7, 0, 7);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		container.add(titleField, gridBagConstraints);

		descriptionLabel.setText("Description");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(7, 7, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		container.add(descriptionLabel, gridBagConstraints);

		descriptionScroll.setMinimumSize(new Dimension(250, 100));
		descriptionScroll.setPreferredSize(new Dimension(250, 100));
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		
		descriptionField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent evt) {
				descriptionFieldFocusLost();
			}
		});
		// add enter listener
		Action saveDesc = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				descriptionFieldFocusLost();
			}
		};
		descriptionField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),"saveMe");
		descriptionField.getActionMap().put("saveMe",saveDesc);
		
		descriptionScroll.setViewportView(descriptionField);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(7, 7, 0, 7);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		container.add(descriptionScroll, gridBagConstraints);

		typeLabel.setText("Type");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(7, 7, 7, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		container.add(typeLabel, gridBagConstraints);

		//typeCombo.setLightWeightPopupEnabled(false);
		typeCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				typeComboActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(7, 7, 7, 7);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		container.add(typeCombo, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		container.add(blankLabel, gridBagConstraints);

	}

	public void typeComboActionPerformed() {
		int i = this.typeCombo.getSelectedIndex();
		if (i > 0) {
			// A type is selected 
			this.type = ((TarifTypeRepresenter)this.typeCombo.getItemAt(i)).getType();
		} else {
			this.type = null;
		}
		this.checkForFinish();
	}
	
	/** set the title of this tarif **/
	protected void setTitle(String title) {
		this.title = title;
		this.checkForFinish();
	}
	
	public void titleFieldFocusLost() {
		setTitle(titleField.getText().trim());
	}
	
	/** set the Description of this tarif **/
	private void setDescription(String description) {
		this.description = description;
	}
	
	public void descriptionFieldFocusLost() {
		setDescription(this.descriptionField.getText().trim());
	}
	
	public void checkForFinish() {
		boolean finish = true;
		if (this.title == null) {
			finish = false;
		} else if (this.title.equals("")) {
			finish = false;
		} 
		
		if (this.type == null) {
			finish = false;
		}
		
		this.owner().setFinish(finish);
	}
	
	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doCancel()
	 */
	public void doCancel() {
		// Nothing saved, nothing to do
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doBack()
	 */
	public void doBack() {
		this.owner().setUserObject("title",this.title);
		this.owner().setUserObject("description", this.description);
		this.owner().setUserObject("type",this.type);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doNext()
	 */
	public void doNext() {
		// Impossible to reach, last step
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doFinish()
	 */
	public void doFinish() {
		// Create a tarif on tarification with specified parameters
		Tarif t = this.currentNode.getTarification().createTarif(this.title, (String)this.type);
		t.mapTo(this.currentNode,true);
		t.setDescription(this.description);
		if (t != null)
		this.tarificationCreator.setCurrentWorkSheet(t.getWorkSheet());
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#getDisplay()
	 */
	public JComponent getDisplay() {
		return this.container;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#getStepTitle()
	 */
	public String getStepTitle() {
		return new String("STEP 2. Name, Description and type choosing");
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#getStepDescription()
	 */
	public String getStepDescription() {
		return new String("Please enter a name and a description. Once the type choosen, press Finish");
	}

	/**
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#refreshState()
	 */
	public void refreshState() {
		this.currentNode = (BCNode)this.owner().getUserObject("node");
		if (this.currentNode == null) {
			m_log.error(
			        "Step2 was accessed with a null TarificationExplorerNode");
		}
		
		this.title = (String) this.owner().getUserObject("title");
		this.description = (String) this.owner().getUserObject("description");
		
		this.titleField.setText(this.title);
		this.descriptionField.setText(this.description);
		
		this.fillCombo();
		
		String oldType = (String)(this.owner().getUserObject("type"));

		
		this.setComboSelection(oldType);
		
		this.checkForFinish();
		
		this.titleField.grabFocus();
	}

	private void fillCombo() {
		this.typeCombo.removeAllItems();
		this.typeCombo.addItem("---- select a tarif type ----");
		if (this.currentNode != null) {
			ArrayList types = this.currentNode.getAcceptedTarifTypes();
			if (types != null) {
				for (int i=0;i < types.size(); i++) {
					this.typeCombo.addItem(new TarifTypeRepresenter(types.get(i)));
				}
			}
		}
		this.typeCombo.setEnabled(true);
		
		int nbItems = this.typeCombo.getItemCount();
		if (nbItems == 1)
			this.typeCombo.setSelectedIndex(0);
		if (nbItems == 2)
			this.typeCombo.setSelectedIndex(1);
		if (nbItems < 3) { // no selection possible
			this.typeCombo.setEnabled(false);
		}
		
	}
	
	private void setComboSelection(String typeName) {
		if (typeName == null) return;
		if (this.typeCombo.isEnabled()) {
			if (currentNode.getAcceptedTarifTypes().contains(typeName)) {
				this.typeCombo.setSelectedItem(new TarifTypeRepresenter(typeName));
			} else {
				this.typeCombo.setSelectedIndex(0);
			}
			this.typeCombo.transferFocus();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.conception.wizards.StepPanel#doButtonEnabling()
	 */
	public void doButtonEnabling() {
		this.owner().setCancel(true);
		this.owner().setBack(true);
		this.owner().setNext(false);
		this.owner().setFinish(false);
		
		this.checkForFinish();
	}

	class myKeyAdapter extends KeyAdapter {
		
		public TarifNameAndTypeSelectionStep panel;
		private boolean enterPressed = false;
		private boolean keyTyped = false;
		
		public myKeyAdapter(TarifNameAndTypeSelectionStep tnatss) {
			this.panel = tnatss;
		}

		public void keyPressed(KeyEvent evt) {
			// catch eventual ENTER key typed
			if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
				this.enterPressed = true;					
			} else {
				this.enterPressed = false;
			}
		}
		
		public void keyTyped(KeyEvent evt) {
			keyTyped = true;
		}
		
		public void keyReleased(KeyEvent evt) {
			JTextField origin = (JTextField)evt.getSource();
			if (!enterPressed) {
				if (keyTyped) {					
					String t = origin.getText().trim();
					panel.setTitle(t);
					panel.checkForFinish();
				}
			} else {
				origin.transferFocus();
			}
			keyTyped = false;
		}
		
	}
	
}


class TarifTypeRepresenter {
	
	private Object tarif_type = null;
	
	public TarifTypeRepresenter(Object type) {
		this.tarif_type = type;
	}
	
	public String toString() {
		return Lang.translate("TARIF TYPE:"+this.tarif_type);
	}
	
	public Object getType() {
		return this.tarif_type;
	}
	
	/** test the equality lazily on the toString() value **/
	public boolean equals(Object o) {
		if (o== null) return false;
		
		String str = o.toString();
		if (TarifTypeRepresenter.class.isInstance(o)) 
			str = ((TarifTypeRepresenter) o).tarif_type.toString();
			
		return str.equals(tarif_type.toString());
	}
}

/*
 * $Log: TarifNameAndTypeSelectionStep.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.18  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.17  2004/09/09 18:38:46  perki
 * Rate by slice on amount are welcome aboard
 *
 * Revision 1.16  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 * Revision 1.15  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.14  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.13  2004/03/24 13:11:14  perki
 * Better Tarif Viewer no more null except
 *
 * Revision 1.12  2004/03/18 09:02:29  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/03/08 16:40:34  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/03/08 14:22:22  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/03/08 11:01:18  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/03/06 15:25:09  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.6  2004/03/06 14:24:50  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.5  2004/03/03 11:48:18  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/02/17 11:39:24  carlito
 * *** empty log message ***
 *
 */
