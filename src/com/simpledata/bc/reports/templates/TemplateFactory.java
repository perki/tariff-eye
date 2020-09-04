/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Reporting: Template handling
 */
package com.simpledata.bc.reports.templates; 

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;

/**
 * The template factory provides access to the jasper
 * class instances of the various template files. 
 *
 * This class implements the Observer-Observeable pattern
 * wherein it takes the place of the Observeable. The 
 * TemplateEventListener class is the way the observer gets
 * notified of changements. 
 *
 * @version $Id: TemplateFactory.java,v 1.2 2007/04/02 17:04:26 perki Exp $ 
 * @author Simpledata 2004 all rights reserved. 
 */
public class TemplateFactory {
	private static final Logger m_log = Logger.getLogger( TemplateFactory.class ); 
	
	private boolean m_switchingStates; // true while notification of listeners
	private List/*<TemplateEventListener>*/ m_listeners;
	
	private Map/*<String, JasperReport>*/ m_memoizeReports;
	private static TemplateFactory m_factory; 
	
	/**
	 * This constructor is private since the class is singleton. Access
	 * trough getJasperTemplate. 
	 */
	private TemplateFactory() {
		m_memoizeReports = new HashMap();
		m_listeners = new LinkedList();
		
		m_switchingStates = false; 
	}
	
	/**
	 * Retrieves and compiles the report if it is not already in memory,
	 * or if changes performed on the source. If the system can't compile
	 * (e.g. we miss the source file, the java compiler isn't aviable) we
	 * try to use an old compiled report. If it fails, an exception is raised.<BR>
	 * Note that if the report is already in the memory but the source file has
	 * changed, the program do not recompile the source file. 
	 * @return Report or null if it could not be found.
	 */
	private JasperReport compileReport( String reportName, String templateName ) {
		String file = getFileName( reportName, templateName );
		
		if ( m_memoizeReports.containsKey( file ) ) {
		  
		    JasperReport r = (JasperReport) m_memoizeReports.get( file );
		    assert r != null : 
		        "Stored report cannot be stored as null";
		    
		    return r;
		    /* NOT REACHED */
		   
		}
		
		JasperReport report = null; 
		
		notifyBeforeTemplateCompilation( reportName, templateName );
		// try to load an already compiled report: 
		String compiled = file + TemplateConstants.REPORT_COMPIL_EXT;
		String source   = file + TemplateConstants.REPORT_DESIGN_EXT;
		File compiledFile = new File(compiled);
		File sourceFile   = new File(source);
		
		
		if (compiledFile.exists() && 
		        sourceFile.exists() &&
		        compiledFile.lastModified() >= sourceFile.lastModified()) {
		    // current compiled file is up-to-date
		    try {
		        report = (JasperReport)JRLoader.loadObject(compiledFile);
		    }
		    catch (Exception e) {
		        // This is a warning that should stay in the application
		        m_log.warn( "Compiled report "+templateName+" not found, expected path ["+file+TemplateConstants.REPORT_COMPIL_EXT+"]",e ); 
		    }
		} else {
		    // current compiled file is not up-to-date
		    // or one of source or compiled file is missing
		    JasperReport deprecatedTemplate = null;
		    if (compiledFile.exists()) {
		        // in case we can't compile the file
		        // a old one would be better than nothing
		        try {
		            deprecatedTemplate = (JasperReport)JRLoader.loadObject(compiledFile);
		        }
		        catch (Exception e) {
		            // This is a warning that should stay in the application
		            m_log.warn( "Compiled report "+templateName+" not found, expected path ["+file+TemplateConstants.REPORT_COMPIL_EXT+"]" );
		        }
		    }
		    // we must try compile
		    if (sourceFile.exists()) {
		        try {
		            JasperCompileManager.compileReportToFile( source, compiled );
		            report = (JasperReport)JRLoader.loadObject(compiledFile);
		        }
		        catch (JRException e) {
		            m_log.error( e ); 
		        }
		    }
		    // if error on report load
		    if (report == null) {
		        // try the old one
		        report = deprecatedTemplate;
		    }
		}
		
		notifyAfterTemplateCompilation( reportName, templateName );
		
		assert report != null : 
			"Report is nonempty and instance of JasperReport.";
		
		m_memoizeReports.put( file, report );
		
		return report;
	}
	
	/**
	 * Return the file name for the report design being given 
	 * the report and the template name. 
	 * 
	 * @param reportName Name of report that should be loaded. 
	 * @param templateName Template that should be loaded.
	 * @return String that is the full path of the template to be
	 *         loaded.  
	 */
	private String getFileName(String reportName, String templateName) {
		return Resources.reportsPath() + Resources.PATHSEPARATOR 
			+ reportName + Resources.PATHSEPARATOR + templateName; 
	}
	
	
	/**
	 * Return true if the template is immediately available. 
	 * 
	 * @param reportName Name of the report to find files in. 
	 * @param templateName Name of subreport template. 
	 * @return true if the template does not have to be compiled. 
	 */
	private boolean isAvailable( String reportName, String templateName ) {
		String file = getFileName( reportName, templateName );
		return m_memoizeReports.containsKey( file );
	}
	
	// Listener handling -----------------------------------------------------
	
	/**
	 * Add an event listener. 
	 * 
	 * @param l Listener to register at the factory. 
	 */
	private void addListener( TemplateEventListener l ) {
		synchronized ( m_listeners ) {
			assert ! m_switchingStates : 
				"Cannot modify listener list while notifying them."; 
			
			m_listeners.add( l );
		}
	}
	
	/**
	 * Remove an event listener. 
	 * 
	 * @param l Listener to deregister at the factory. 
	 */
	private void removeListener( TemplateEventListener l ) {
		synchronized ( m_listeners ) {
			assert ! m_switchingStates : 
				"Cannot modify listener list while notifying them."; 
			
			m_listeners.remove( l );
		}
	}
	
	/**
	 * Pass the event to all listeners. 
	 */
	private void notifyBeforeTemplateCompilation( String report, String subreport ) {
		synchronized ( m_listeners ) {
			m_switchingStates = true; 
			
			ListIterator it = m_listeners.listIterator();
			
			while ( it.hasNext() ) {
				TemplateEventListener l = (TemplateEventListener) it.next(); 
				
				l.notifyBeforeTemplateCompilation( report, subreport ); 
			}
			
			m_switchingStates = false; 
		}
	}
	
	/**
	 * Call all listeners with the after template event. 
	 *  
	 * @param report Report that was compiled. 
	 * @param subreport Subreport that was compiled. 
	 */
	private void notifyAfterTemplateCompilation( String report, String subreport ) {
		synchronized ( m_listeners ) {
			m_switchingStates = true; 
			
			ListIterator it = m_listeners.listIterator();
			
			while ( it.hasNext() ) {
				TemplateEventListener l = (TemplateEventListener) it.next(); 
				
				l.notifyAfterTemplateCompilation( report, subreport ); 
			}
			
			m_switchingStates = false; 
		}
	}
	
	// Static singleton interface ------------------------------------
	
	/**
	 * Retrieve singleton instance of TemplateFactory. 
	 * 
	 * @return singleton instance of TemplateFactory. 
	 */
	private static TemplateFactory instance() {
		if ( m_factory == null ) {
			m_factory = new TemplateFactory();
		}
		
		assert m_factory != null : 
			"Should have been able to create a factory.";
		
		return m_factory; 
	}
	
	/**
	 * Given a report and a template name, retrieves the 
	 * compiled template for that name. 
	 * 
	 * @param reportName Name of the report to find files in. 
	 * @param templateName Name of subreport template. 
	 * @return report or null if compilation failed. 
	 */
	public static JasperReport getJasperTemplate(String reportName, String templateName) {
		TemplateFactory singleton = instance(); 
	
		return singleton.compileReport( reportName, templateName );
	}
	
	/**
	 * Ask if a template is immediately available or whether
	 * it must be compiled. 
	 * 
	 * @param reportName Name of the report to find files in. 
	 * @param templateName Name of subreport template. 
	 * @return true if the template is immediately available. 
	 */
	public static boolean isTemplateAvailable( String reportName, String templateName ) {
		TemplateFactory singleton = instance(); 
		
		return singleton.isAvailable( reportName, templateName );
	}

	/**
	 * Add a template event listener to the template factory 
	 * instance. The listener MUST be removed by calling the
	 * #removeEventListener method.
	 * 
	 * @param l Listener to register at the factory. 
	 */
	public static void addEventListener( TemplateEventListener l ) {
		TemplateFactory s = instance(); 
		
		s.addListener( l ); 
	}
	
	/**
	 * Remove a template listener from the factory. 
	 * 
	 * @param l Listener to deregister at the factory. 
	 */
	public static void removeEventListener( TemplateEventListener l ) {
		TemplateFactory s = instance(); 
		
		s.removeListener( l ); 
	}
	
	/**
	 * Utils method for devel. Remove all cached templates, modified ones will 
	 * then be recompiled. 
	 */
	public static void cleanTemplatesCache() {
		TemplateFactory s = instance();
		s.m_memoizeReports.clear();
	}
}