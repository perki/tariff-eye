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
 * $Id: NewTarifWizard.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception.wizards;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.*;

import com.simpledata.bc.uitools.SButton;

import org.apache.log4j.Logger;

/**
 * This class defines a wizard for creating new Tarifs in TarificationCreator
 */
public class NewTarifWizard extends JInternalFrame {
	
	private static final Logger m_log = Logger.getLogger( NewTarifWizard.class );

	private ArrayList stepPanels = null;
	private StepPanel currentStepPanel = null;
	private HashMap userObjects = null;
	
	private boolean debug = false;
	
	// Variables declaration - do not modify
	private SButton back;
	private JLabel blankLabel1;
	private SButton cancel;
	private JPanel controlsPanel;
	private SButton next;
	private SButton finish;
	private JScrollPane stepContentScroll;
	private JLabel stepDescription;
	private JLabel stepTitle;
	private JPanel stepTitlePanel;
	// End of variables declaration

	/** Creates new form NewTarifWizard */
	public NewTarifWizard() {
		// String title, boolean�resizable,  boolean�closable,  boolean�maximizable,  boolean�iconifiable
		super("Tarif creation wizard", true, false,  true,  false);
		this.userObjects = new HashMap();
		
		initStdComponents();
		
		this.setSize(400,400);
		
	}
	
	public void setStepPanels(ArrayList stepPanelsList) {
		boolean pb = false;
		if (stepPanelsList != null) {
			if (stepPanelsList.size() > 0) {
				this.stepPanels = stepPanelsList;
				// We set the first step
				this.setStep((StepPanel)stepPanelsList.get(0));
			} else {
				pb = true;
			}
		} else {
			pb = true;
		}
		if (pb) { 
			m_log.error( "NewTarifWizard was given a null or empty vector of StepPanels" );
			stepPanels = new ArrayList();
		}
	}
	
	public void setDebug(boolean b) {
		this.debug = b;
	}
	
	public void debugOut(String s) {
		if (debug) m_log.debug(s);
	}
	
	/** This method is called from within the constructor to
	 * initialize the frame.
	 */
	private void initStdComponents() {
		GridBagConstraints gridBagConstraints;

		stepTitlePanel = new JPanel();
		stepTitle = new JLabel();
		stepDescription = new JLabel();
		stepContentScroll = new JScrollPane();
		controlsPanel = new JPanel();
		cancel = new SButton();
		blankLabel1 = new JLabel();
		back = new SButton();
		next = new SButton();
		finish = new SButton();

		stepTitlePanel.setLayout(new GridBagLayout());

		stepTitle.setFont(new Font("Arial Black", 0, 14));
		stepTitle.setText("1. First step");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(5, 6, 0, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		stepTitlePanel.add(stepTitle, gridBagConstraints);

		stepDescription.setFont(new Font("Lucida Grande", 0, 10));
		stepDescription.setText("Short description for step 1");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 6, 3, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		stepTitlePanel.add(stepDescription, gridBagConstraints);

		getContentPane().add(stepTitlePanel, BorderLayout.NORTH);

		getContentPane().add(stepContentScroll, BorderLayout.CENTER);

		controlsPanel.setLayout(new GridBagLayout());

		cancel.setText("cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		controlsPanel.add(cancel, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(5, 0, 5, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		controlsPanel.add(blankLabel1, gridBagConstraints);

		back.setText("back");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				backActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 25, 5, 0);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		controlsPanel.add(back, gridBagConstraints);

		next.setText("next");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				nextActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 25);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		controlsPanel.add(next, gridBagConstraints);

		finish.setText("finish");
		finish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				okActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		controlsPanel.add(finish, gridBagConstraints);

		getContentPane().add(controlsPanel, BorderLayout.SOUTH);

		pack();
	}

	public void okActionPerformed() {
		// We have a finish
		if (this.currentStepPanel != null) {
			this.currentStepPanel.doFinish();
		}
		this.flushUserObjects();
		this.dispose();
	}

	public void nextActionPerformed() {
		// We have a next
		if (this.currentStepPanel != null) {
			int newStepIndex = this.stepPanels.indexOf(this.currentStepPanel) + 1;
			if (newStepIndex < this.stepPanels.size()) {
				this.currentStepPanel.doNext();
				this.setStep((StepPanel)this.stepPanels.get(newStepIndex));
			} else {
				m_log.error( "Tried to do next on the last step" );
			}
		} else {
			m_log.error( "We are doing next with a currentStepPanel null" );
		}
	}

	public void backActionPerformed() {
		// We have a back
		if (this.currentStepPanel != null) {
			int newStepIndex = this.stepPanels.indexOf(this.currentStepPanel) - 1;
			if (newStepIndex >= 0) {
				this.currentStepPanel.doBack();
				this.setStep((StepPanel)this.stepPanels.get(newStepIndex));
			} else {
				m_log.error( "Tried to do back on the first step" );
			}
		} else {
			m_log.error( "We are doing back with a currentStepPanel null" );
		}
	}

	public void cancelActionPerformed() {
		if (this.currentStepPanel != null) {
			this.currentStepPanel.doCancel();
		}
		this.flushUserObjects();
		this.dispose();
	}
	
	/** 
	 * called from outside to simulate a doNextAction <BR>
	 * can be called safely will go next only if current panel is finished
	 * and if the currentpanel == the passed StepPanel
	 * **/
	public boolean tryNext(StepPanel sp) {
		if (this.currentStepPanel == sp) {
			if (this.next.isEnabled()) {
				nextActionPerformed();
				return true;
			}
		}
		return false;
	}
	
	public void goAsNextAsPossible() {
		int counter = 0;
		while ((counter < stepPanels.size()) && (this.tryNext((StepPanel)this.stepPanels.get(counter)))) {
			counter++;
		}
	}
	
	public void setNext(boolean b) {
		this.next.setEnabled(b);
	}
	

	public void setBack(boolean b) {
		this.back.setEnabled(b);
	}
	
	public void setCancel(boolean b) {
		this.cancel.setEnabled(b);
	}
	
	public void setFinish(boolean b) {
		this.finish.setEnabled(b);
	}
	
	public void  setTitle(String newTitle) {
		this.stepTitle.setText(newTitle);
	}
	
	public void setDescription(String newDesc) {
		this.stepDescription.setText(newDesc);
	}
	
	private void setStep(StepPanel sp) {
		this.currentStepPanel = sp;
		sp.refreshState();
		this.setTitle(this.currentStepPanel.getStepTitle());
		this.setDescription(this.currentStepPanel.getStepDescription());
		this.stepContentScroll.setViewportView(this.currentStepPanel.getDisplay());
		this.currentStepPanel.doButtonEnabling();
		
		// In case of absurd next or back setting
		this.correctBackAndNext();
	}
	
	private void correctBackAndNext() {
		int i = this.stepPanels.indexOf(this.currentStepPanel);
		if (i == 0) {
			// We cannot go back
			this.setBack(false);
		}
		if (i == (this.stepPanels.size() - 1)) {
			// We cannot go further
			this.setNext(false);
		}
	}
	
	public void setUserObject(String key, Object obj) {
		this.userObjects.put(key,obj);	
	}
	
	public Object getUserObject(String key) {
		Object res = null;
		if (this.userObjects.containsKey(key)) {
			res = this.userObjects.get(key);
		} else {
			this.debugOut("The key : "+key+" does not exist");
		}
		if (res == null) {
			this.debugOut("User Object for key : "+key+" was null");
		}
		return res;
	}
	
	private void flushUserObjects() {
		this.userObjects.clear();
	}
}

/*
 * $Log: NewTarifWizard.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.14  2004/09/04 18:12:31  kaspar
 * ! Log.out -> log4j
 *   Only the proper logger init is missing now.
 *
 * Revision 1.13  2004/07/26 16:46:10  carlito
 * *** empty log message ***
 *
 * Revision 1.12  2004/07/09 19:10:25  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.10  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.9  2004/03/08 16:43:48  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/03/08 16:40:34  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/03/08 14:22:22  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/03/06 15:22:41  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.5  2004/03/06 14:24:50  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.4  2004/03/03 11:48:18  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/02/17 11:39:24  carlito
 * *** empty log message ***
 *
 */