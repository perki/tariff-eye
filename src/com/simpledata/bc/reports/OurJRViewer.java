/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 nov. 2004
 */
package com.simpledata.bc.reports;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JRViewer;

import org.apache.log4j.Logger;

/**
 * This class override the on the shelf JRViewer from Jasper reports. For
 * a file type purpose. This one permits only to save using pdf file type.
 * <BR>This needs iText jar files.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: OurJRViewer.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
public class OurJRViewer extends JRViewer {
	
	private static final Logger m_log = Logger.getLogger(OurJRViewer.class);
	
	private final JasperPrint jasperPrint;
	
	/**
	 * Constructor. Set a new listener for the save button.
	 * @param jrPrint report
	 * @throws JRException
	 */
	public OurJRViewer(JasperPrint jrPrint) throws JRException {
		super(jrPrint);
		jasperPrint = jrPrint;
		
		btnSave.removeActionListener(btnSave.getActionListeners()[0]);
		// hack hack hack
		
		btnSave.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				btnSaveActionPerformed();
			}});
	}
	
	private void btnSaveActionPerformed() {
		// Code from super
		
		JFileChooser fileChooser = new JFileChooser();
		
		JRSaveContributor pdfSaveContrib = null;
		try {
			Class pdfSaveContribClass = JRClassLoader.loadClassForName("net.sf.jasperreports.view.save.JRPdfSaveContributor");
			pdfSaveContrib = (JRSaveContributor)pdfSaveContribClass.newInstance();
			fileChooser.addChoosableFileFilter(pdfSaveContrib);
		} catch (Exception e) {}
		fileChooser.setFileFilter(pdfSaveContrib);
		
		int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION) {
			FileFilter fileFilter = fileChooser.getFileFilter();
			File file = fileChooser.getSelectedFile();
			String lowerCaseFileName = file.getName().toLowerCase();
			
			try {
				if (fileFilter instanceof JRSaveContributor) {
					((JRSaveContributor)fileFilter).save(jasperPrint, file);
				} else {
					if ( lowerCaseFileName.endsWith(".pdf") 
								&& pdfSaveContrib != null )	{
						pdfSaveContrib.save(jasperPrint, file);
					} else {
						if (!file.getName().endsWith(".pdf")) {
							file = new File(file.getAbsolutePath() + ".pdf");
						}
						pdfSaveContrib.save(jasperPrint, file);
					}
				}
			}
			catch (JRException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("net/sf/jasperreports/view/viewer").getString("error.saving"));
			}
		}
	}
}
