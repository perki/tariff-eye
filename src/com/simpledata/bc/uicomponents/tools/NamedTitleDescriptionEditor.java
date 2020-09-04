/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: NamedTitleDescriptionEditor.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SButtonIcon;


/**
 * A component to edit the title and description of a Named object
 */
public abstract class NamedTitleDescriptionEditor extends JPanel {
	private final static Logger m_log = Logger.getLogger(NamedTitleDescriptionEditor.class);
    
    public final static int PREF_LABEL_WIDTH = 400;
        
	private JTextAreaNamedDescription descriptionField;
	private JLabel descriptionLabel;
	private JTextFieldNamedTitle titleField;
	private JLabel titleLabel;
	private SButtonIcon okButton;
	private JScrollPane descriptionScroll;
	
	private JPanel jp;
	
	public NamedTitleDescriptionEditor(Named n) {
		jp = this;
		
		descriptionField = new JTextAreaNamedDescription(n) {
			public void editionStopped() {
				_editionStopped();
			}
			public void editionStarted() {
				_editionStarted();
			}};
			
		 titleField = new JTextFieldNamedTitle(n, true) {
		 	public void editionStopped() {
				_editionStopped();
			}
			public void editionStarted() {
				_editionStarted();
			}};
			
		descriptionLabel = new JLabel(Lang.translate("Description"));
		titleLabel = new JLabel(Lang.translate("Title"));
		
		okButton= new SButtonIcon(Resources.iconSave);
		descriptionScroll = new JScrollPane();
		
		initUI();
	}
	
	/** 
	 * get a JInternal Frame for this named object 
	 * see ModalJPanel for parameters informations
	 * @see ModalJPanel
	 **/
	public static ModalJPanel getModalJInternalFrame 
	(Named n, Component origin, Point delta,boolean closable) {
		NamedTitleDescriptionEditor nde = new NamedTitleDescriptionEditor(n) {
			public void editionStopped() {}
			public void editionStarted() {}
		};
		
		ModalJPanel mjp=
			ModalJPanel.createSimpleModalJInternalFrame(
				nde,
				origin,
				delta,
				closable,
				Resources.iconEdit,Resources.modalBgColor);
		return mjp;
	}
	
	private void initUI() {
		GridBagConstraints gridBagConstraints;
		
		jp.setLayout(new GridBagLayout());

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.insets= new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		jp.add(titleLabel, gridBagConstraints);

		titleField.setMinimumSize(new Dimension(PREF_LABEL_WIDTH, 22));
		titleField.setPreferredSize(new Dimension(PREF_LABEL_WIDTH, 22));


		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.fill= GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets= new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx= 1.0;
		jp.add(titleField, gridBagConstraints);

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 1;
		gridBagConstraints.insets= new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		jp.add(descriptionLabel, gridBagConstraints);

		descriptionScroll.setMinimumSize(new Dimension(PREF_LABEL_WIDTH, 66));
		descriptionScroll.setPreferredSize(new Dimension(PREF_LABEL_WIDTH, 66));
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);

		descriptionScroll.setViewportView(descriptionField);

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 1;
		gridBagConstraints.gridy= 1;
		gridBagConstraints.fill= GridBagConstraints.BOTH;
		gridBagConstraints.insets= new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor= GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx= 1.0;
		gridBagConstraints.weighty= 1.0;
		jp.add(descriptionScroll, gridBagConstraints);

	
		okButton.setFocusPainted(false);
		okButton.setEnabled(false);
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 2;
		gridBagConstraints.gridwidth= 2;
		gridBagConstraints.insets= new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor= GridBagConstraints.NORTHEAST;
		jp.add(okButton, gridBagConstraints);
		
	}
	

	public void _editionStopped() {
		okButton.setEnabled(false);
	}
	
	public void _editionStarted() {
		okButton.setEnabled(true);
	}
	
	public abstract void editionStopped();
	public abstract void editionStarted();
	
}



/*
 * $Log: NamedTitleDescriptionEditor.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.7  2004/10/14 13:45:07  jvaucher
 * - Ticket #84: Corrected into ZZuListener.
 *
 * Revision 1.6  2004/10/12 17:49:10  carlito
 * Simulator split problems solved...
 * description pb solved
 *
 * Revision 1.5  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.4  2004/07/22 15:12:34  carlito
 * lots of cleaning
 *
 * Revision 1.3  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/20 06:11:17  perki
 * id tagging
 *
 * Revision 1.1  2004/05/14 07:52:53  perki
 * baby dispatcher is going nicer
 *
 */