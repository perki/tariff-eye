/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Package that contains all sorts of helper classes
 * to manipulate the jasper library. 
 */
package com.simpledata.bc.reports.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.reports.templates.TemplateConstants;


/**
 * Tools contains various static tool routines that
 * do things that use only Jasper functions to 
 * get their job done. 
 * 
 * @version $Id: Tools.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public class Tools {
	
	/** Logger */
	private static final Logger m_log = Logger.getLogger(Tools.class);
	
		/**
	 * Move table of contents delimited by 'Table of Contents begin here' 
	 * to the beginning of report. 
	 * @param jasperPrint Report to change
	 */
	public static void moveTableOfContents(JasperPrint jasperPrint)
	{
		if (jasperPrint != null) {
			List pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0) {
				String key = "Table of Contents begin here";
				JRPrintPage page = null;
				Collection elements = null;
				Iterator it = null;
				JRPrintElement element = null;
				int i = pages.size() - 1;
				boolean isFound = false;
				while(i >= 0 && !isFound) {
					page = (JRPrintPage)pages.get(i);
					elements = page.getElements();

					if (elements != null && elements.size() > 0) {
						it = elements.iterator();
						
						while(it.hasNext() && !isFound) {
							element = (JRPrintElement)it.next();
							
							if (element instanceof JRPrintText) {
								if (key.equals(
								        ((JRPrintText)element).getText())) {
									isFound = true;
									break;
								}
							}
						}
					}
					
					i--;
				}
				
				if (isFound)
				{
					for(int j = i + 1; j < pages.size(); j++)
					{
						jasperPrint.addPage(j -i-1,jasperPrint.removePage(j));
					}
				}
			}
		}
		
	}
	
	
	/** Recompile all jasper reports */
	public static void forceTemplatesCompile() {
		int found = 0, compiled = 0, errors = 0, replaced = 0;
		m_log.info ("### Force templates recompile started ###");
		File reportsHome = new File(Resources.reportsPath());
		m_log.info("# Use REPORT_HOME = "+reportsHome.getPath());
		File[] reportsDirs = reportsHome.listFiles(new FileFilter(){

			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}});
		m_log.info("# Found "+reportsDirs.length+" reports directories: "+reportsDirs);
		for (int i = 0; i<reportsDirs.length; i++) {
			File[] reportsToCompile = reportsDirs[i].listFiles(new FilenameFilter(){

				public boolean accept(File dir, String name) {
					return name.endsWith(TemplateConstants.REPORT_DESIGN_EXT);
				}});
			for (int j = 0; j<reportsToCompile.length; j++) {
				found++;
				File source = reportsToCompile[j];
				String destName = source.getName().replaceAll(
						TemplateConstants.REPORT_DESIGN_EXT, 
						TemplateConstants.REPORT_COMPIL_EXT);
				File destination = new File(reportsDirs[i], destName);
				m_log.info ("# Start compilation: "+source.getName()+
						" -> "+ destination.getName());
				if (destination.exists()) {
					m_log.info("# Warning: Overridding file !");
					replaced++;
				}
				try {
					JasperCompileManager.compileReportToFile( source.getAbsolutePath(), 
						destination.getAbsolutePath() );
					m_log.info("# Compilation done.");
					compiled++;
				} catch (JRException e) {
					m_log.error ("# Unable to compile "+source.getName());
					errors++;
				}
			}
		}
		m_log.info("### Force templates recompile done ######");
		m_log.info("### Found "+found+" templates ("+replaced+") replaced.");
		m_log.info("###     "+compiled+" succesfully compiled.");
		m_log.info("###     "+errors  +" failed.");
		
	}
	

}