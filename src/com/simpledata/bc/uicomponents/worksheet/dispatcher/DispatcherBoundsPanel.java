/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 7 sept. 2004
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherBounds;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.tools.MoneyEditor;
import com.simpledata.bc.uicomponents.tools.MoneyValueInput;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;

/**
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: DispatcherBoundsPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
public class DispatcherBoundsPanel extends WorkSheetPanel {
	
	// CONSTANTS - Locale
	private final static String UPPER = "Upper bound:";
	private final static String LOWER = "Lower bound:";
	
	// CONSTANTS - Look'n'feel
	private final static int LABEL_WIDTH = 30;
	
	// FIELDS
	/** Logger */
	private final static Logger m_log = Logger.getLogger(DispatcherBoundsPanel.class);
	/** Main panel for the UI */
	private final JPanel m_dbPanel;
	/** 
	 * South part of the main panel. It contains a representation of the
	 * child WorkSheet
	 */
	private final JPanel m_wsPanel;
	
	private final JPanel m_upperBoundPanel;
	private final JLabel m_infinity;
	
	/** Text field for the bounds */
	private final MoneyEditor m_lowerBoundField;
	private final MoneyValueInput m_upperBoundField;
	
	/** Check box for infinity */
	private final JCheckBox m_infinityCheckBox;
	
	/** Modelized representation of the Dispatcher */
	final DispatcherBounds m_db;
	
	/**
	 * overwrite this to set a default Class Icon
	 */
	public static ImageIcon defaultClassTreeIcon = Resources.wsDispatcherBounds;
	
	// CONSTRUCTOR 
	
	/**
	 * Construct the graphical representation of the given DispatcherBounds
	 * @param db DispatcherBounds object. Logical representation.
	 * @param tv The tarif viewer.
	 */
	public DispatcherBoundsPanel(DispatcherBounds db, TarifViewer tv) {
		super(db, tv);
		m_db = db;
		m_dbPanel = new JPanel();
		m_wsPanel = new JPanel();
		m_upperBoundPanel = new JPanel();
		m_infinity = new JLabel(Resources.infinityIcon);
		
		// Bounds fields
		final DispatcherBoundsPanel sthis = this;
		m_lowerBoundField = new MoneyEditor(m_db.getLowerBound()){
			public void stopEdit() {
				Money lower = m_db.getLowerBound();
				Money upper = m_db.getUpperBound();
				upper.setCurrency(lower.getCurrency());
				if (upper.getValueDouble() < lower.getValueDouble())
					upper.setValue(lower);
				m_db.optionDataChanged(null,null);
				sthis.refresh();
			}
			public void startEdit() {
				// nothing to do 
			}
		};
		m_upperBoundField = new MoneyValueInput(m_db.getUpperBound()){
			public void editionStopped() {
				Money lower = m_db.getLowerBound();
				Money upper = m_db.getUpperBound();
				if (upper.getValueDouble() < lower.getValueDouble())
					lower.setValue(upper);
				m_db.optionDataChanged(null,null);
				sthis.refresh();
			}
			
			public void editionStarted() {
				// nothing to do
			}
		};
		m_infinityCheckBox = new JCheckBox();
		initUIComponents();
	}

	/** Initializes the components of the panel */
	private void initUIComponents() {
		// switchable upper bound panel
		m_upperBoundPanel.setLayout(new GridLayout(1,1));
		Dimension d = new Dimension(150,23);
		
		// --- Parameter panel
		// (0;0) lower bound label
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
		c.anchor = GridBagConstraints.WEST;
		JPanel inner = new JPanel(new GridBagLayout());
		JLabel lowerBoundLabel = new JLabel(Lang.translate(LOWER));
		inner.add(lowerBoundLabel, c);
		// (1;0) lower bound field
		c.gridx = 1;
		inner.add(m_lowerBoundField, c);
		// (0;1) upper bound label
		JLabel upperBoundLabel = new JLabel(Lang.translate(UPPER));
		c.weightx = 0;
		c.gridy = 1; 
		c.gridx = 0;
		inner.add(upperBoundLabel, c);
		// (1;1) upper bound switchable panel
		c.gridx = 1;
		inner.add(m_upperBoundPanel, c);
		// (2;1) check box
		boolean initialState = m_db.getIsUpperBounded();
		m_infinityCheckBox.setSelected(initialState); // infinity iff not bounded
		infinityActionPerformed(initialState); // disable field if necessary
		m_infinityCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JCheckBox source = (JCheckBox)e.getSource();
				boolean newState = source.isSelected();
				infinityActionPerformed(newState);
			}});
		c.gridx = 2;
		inner.add(m_infinityCheckBox, c);
		// (3;1) check box label
		JLabel checkBoxLabel = new JLabel(Lang.translate("Use upper bound"));
		c.gridx = 3;
		c.weightx = 1.0;
		inner.add(checkBoxLabel, c);
		// ---- end panel
		// is editable ?
		if (getDisplayController().getEditWorkPlaceState() == WSIf.EDIT_STATE_NONE) {
			m_lowerBoundField.setEditable(false);
			m_upperBoundField.setEditable(false);
			m_infinityCheckBox.setEnabled(false);
		}
		// WorkSheet panel
		m_wsPanel.setLayout(new BorderLayout());
		m_wsPanel.add(
				getDisplayController().getWorkSheetPanel(m_db.getWorkSheet()).getPanel(),
				BorderLayout.CENTER
		); 
		
		// Main panel
		m_dbPanel.setLayout(new BorderLayout());
		m_dbPanel.setSize(WorkSheetPanel.defaultContentDim);
		m_dbPanel.add(inner, BorderLayout.NORTH);
		m_dbPanel.add(m_wsPanel, BorderLayout.CENTER);
		
		// Paint
		refresh();
	}
	
	void infinityActionPerformed(boolean state) {
		m_db.setIsUpperBounded(state); 
		m_upperBoundPanel.removeAll();
		if (!state) 
			m_upperBoundPanel.add(m_infinity); // bound editable iff bounded
		else
			m_upperBoundPanel.add(m_upperBoundField);
		m_db.optionDataChanged(null, null);
		m_upperBoundPanel.repaint();
		m_upperBoundPanel.revalidate();
	}
	
	/**
	 * Return the panel representing the dispatcher
	 * @return the panel.
	 */
	public JPanel getContents() {
		return m_dbPanel;
	}

	/**
	 * There's no option panel in this dispatcher.
	 * @return null
	 */
	public JPanel getOptionPanel() {
		// There's no option panel for this dispatcher
		return null;
	}

	/**
	 * Save nothing.
	 */
	public void save() {
		 // nothing to save
	}

	/**
	 * @return the icon for this Dispatcher
	 */
	protected ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}

	/**
	 * Refresh this panel
	 */
	public void refresh() {
		m_wsPanel.removeAll();
		m_wsPanel.add(
				getDisplayController().getWorkSheetPanel(m_db.getWorkSheet()).getPanel(),
				BorderLayout.CENTER); 
		m_lowerBoundField.refresh();
		m_upperBoundField.refresh();
		
	}

}
