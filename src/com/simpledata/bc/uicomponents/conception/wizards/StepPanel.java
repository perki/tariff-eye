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
 * $Id :$
 */
package com.simpledata.bc.uicomponents.conception.wizards;

import javax.swing.JComponent;

/**
 * An abstract prototype for steps used in NewTarifWizard
 */
public abstract class StepPanel {
	
	protected NewTarifWizard owner = null;
	
	/**
	 * Links this step to a specific wizard, in 
	 * order to allow mutual interaction
	 * @param ntw
	 */
	protected StepPanel(NewTarifWizard ntw) {
		this.owner = ntw;
	}
	
	
	/**
	 * @return the wizard to which this step is linked
	 */
	protected NewTarifWizard owner() {
		return this.owner;
	}
	
	/**
	 * Action taken when cancel is pressed
	 */
	public abstract void doCancel();
	
	/**
	 * Action taken when back is pressed
	 */
	public abstract void doBack();
	
	/**
	 * Action taken when next is pressed
	 */
	public abstract void doNext();
	
	/**
	 * Action taken when finish is pressed
	 */
	public abstract void doFinish();
	
	/**
	 * @return the component to be displayed in the wizard
	 */
	public abstract JComponent getDisplay();
	
	/**
	 * @return the title for this step
	 */
	public abstract String getStepTitle();
	
	/**
	 * @return the description for this step
	 */
	public abstract String getStepDescription();

	/**
	 * Called when the step is passed through
	 */
	public abstract void refreshState();

	/**
	 * This method is called by the wizard once the step has
	 * been initialized to set the state of navigation buttons
	 * <br>Example of use :<br>
	 * this.owner().setBack(true);<br>
	 * this.owner().setNext(false);<br>
	 * this.owner().setCancel(true);<br>
	 * this.owner().setFinish(false);<br>
	 */
	public abstract void doButtonEnabling();
	
}

/* 
 * $Log :$
 */