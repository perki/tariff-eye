/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 23 nov. 2004
 */
package com.simpledata.bc.uicomponents;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.tools.JTextFieldBC;
import com.simpledata.bc.uitools.SNumField;

/**
 * This is the preference panel where once saves the network preferences.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: NetworkPreferencePanel.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
class NetworkPreferencePanel extends JPanel {
	// TODO use a terminology for TariffEye infos.
	
	// ### CONSTANTS #########################################################
	/** Name of the panel, shown in the tabs */
	private final static String PANEL_NAME = "Update / Network";
	
	// ### FIELDS ############################################################
	/** Logger */
	private final static Logger m_log = Logger.getLogger(NetworkPreferencePanel.class);
	/** Ui components */
	private final JCheckBox m_teInfoCB;
	private final JCheckBox m_useProxyCB;
	private final JTextFieldBC m_proxyServerField;
	private final SNumField m_proxyPortField;
	private final JLabel m_serverLabel;
	private final JLabel m_portLabel;
	
	// ### CONSTRUCTOR #######################################################
	/** Usual constructor. Creates panel and inits components */
	NetworkPreferencePanel() {
		super();
		
		m_serverLabel = new JLabel();
		m_portLabel = new JLabel();
		m_teInfoCB = new JCheckBox();
		m_useProxyCB = new JCheckBox();
		m_proxyServerField = new JTextFieldBC(){
			public void stopEditing() {
				proxyServerFieldEdited();
			}

			public void startEditing() {
				// NOP
			}};
		m_proxyPortField = new SNumField("",true,true,true){

			public void stopEditing() {
				proxyPortFieldEdited();
			}

			public void startEditing() {
				// NOP
			}};
		
		buildMe();
	}
	
	/** Build the graphical components */
	private void buildMe() {
		// components settings
		m_teInfoCB.setText(Lang.translate(
				"Enable Tariff Eye info"));
		boolean teInfoState = 
			((Boolean)BC.getParameter(Params.KEY_TARIFF_EYE_INFO)).booleanValue();
		m_teInfoCB.setSelected(teInfoState);
		m_teInfoCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				teInfoCBActionPerformed();
			}});
		
		m_useProxyCB.setText(Lang.translate(
				"Connect to internet using a http proxy"));
		boolean proxyState = 
			((Boolean)BC.getParameter(Params.KEY_USE_PROXY)).booleanValue();
		m_useProxyCB.setSelected(proxyState);
		m_useProxyCB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				useProxyCBActionPerformed();
			}});
		toggleProxyFields(proxyState);
		
		String proxyServer = BC.getParameterStr(Params.KEY_PROXY_HOST);
		m_proxyServerField.setText(proxyServer);
		
		String proxyPort = BC.getParameterStr(Params.KEY_PROXY_PORT);
		m_proxyPortField.setText(proxyPort);
		m_proxyPortField.setPreferredSize(new Dimension(60,20));
		
		// Labels
		m_serverLabel.setText(Lang.translate("Server:")+" http://");
		m_portLabel.setText(Lang.translate("Port:"));
		
		// Panel init
		setName(Lang.translate(PANEL_NAME));
		TitledBorder border = new TitledBorder(Lang.translate(
				"Upgrade / Network preferences"));
		setBorder(border);
		setLayout(new GridBagLayout());
		
		// Components layout (4,4)
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0,5,0,5);
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 4;
		add(m_teInfoCB, c);
		c.gridy = 1;
		add(m_useProxyCB, c);
		c.gridy = 2;
		c.gridwidth = 1;
		add(m_serverLabel, c);
		c.gridx = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(m_proxyServerField, c);
		c.gridx = 2;
		c.weightx = 0.0;
		add(m_portLabel, c);
		c.gridx = 3;
		add(m_proxyPortField, c);
		c.weighty = 1.0;
		c.gridx = 0; 
		c.gridy = 3; 
		c.gridwidth = 4;
		Dimension d = new Dimension (50,50); // Dontcare value
		add(new Box.Filler(d,d,d), c);
		
	}
	
	/** Listen to proxy server field modifications */
	private void proxyServerFieldEdited() {
		String value = m_proxyServerField.getText();
		Properties prop = System.getProperties();
		try {
			String secureValue = 
				(value.startsWith("http://")?value:"http://"+value);
			URL newUrl = new URL(secureValue); // check
			BC.setParameter(Params.KEY_PROXY_HOST, value);
			prop.put("http.proxyHost", value);
			m_log.debug("New URL succeded");
		} catch (MalformedURLException e) {
			// rollback
			String old = BC.getParameterStr(Params.KEY_PROXY_HOST);
			m_proxyServerField.setText(old);
			m_log.debug("New URL failed. Rollback");
		}
	}
	
	/** Listen to proxy port field modifications */
	private void proxyPortFieldEdited() {
		Integer newPort = m_proxyPortField.getInteger();
		Properties prop = System.getProperties();
		if (newPort.intValue() < 0 || newPort.intValue() > 65535) {
			// Invalid range, roll back
			String old = BC.getParameterStr(Params.KEY_PROXY_PORT);
			m_proxyPortField.setText(old);
		} else {
			// Validated, save param
			BC.setParameter(Params.KEY_PROXY_PORT, newPort.toString());
			prop.put("http.proxyPort", newPort.toString());
		}
	}
	
	/** Listen to CheckBox soft Update **/
	private void teInfoCBActionPerformed() {
		boolean newState = m_teInfoCB.isSelected();
		BC.setParameter(Params.KEY_TARIFF_EYE_INFO, new Boolean(newState));
	}
		
	/** Listen to CheckBox Use proxy */
	private void useProxyCBActionPerformed() {
		boolean newState = m_useProxyCB.isSelected();
		BC.setParameter(Params.KEY_USE_PROXY, new Boolean(newState));
		BC.setProxyEnable(newState);
		toggleProxyFields(newState);
	}
	
	/** Update the enable of the proxy params fields */
	private void toggleProxyFields(boolean proxyState) {
		m_proxyServerField.setEnabled(proxyState);
		m_proxyPortField.setEnabled(proxyState);
		m_portLabel.setEnabled(proxyState);
		m_serverLabel.setEnabled(proxyState);
	}
}
