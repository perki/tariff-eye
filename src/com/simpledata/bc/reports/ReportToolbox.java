/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports;


import java.awt.Rectangle;

import javax.swing.JInternalFrame;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.reports.common.Report;
import com.simpledata.bc.reports.common.ReportFactory;
import com.simpledata.bc.reports.fee.FeeReportFactory;
import com.simpledata.bc.reports.tarification.TarificationReportFactory;
import com.simpledata.bc.reports.templates.TemplateEventListener;
import com.simpledata.bc.reports.templates.TemplateFactory;
import com.simpledata.bc.reports.ui.ReportProgressDialog;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uitools.InternalFrameDescriptor;
import com.simpledata.bc.uitools.ModalDialogBox;



/**
 * The report toolbox provides some simple static methods
 * to launch the creation of a report and the display of 
 * a JRViewer frame internally in the Desktop. 
 *
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: ReportToolbox.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
public class ReportToolbox {
	private static final Logger m_log = Logger.getLogger( ReportToolbox.class ); 
	
	// static methods -------------------------------------
	
	/**
	 * Launches a new thread to construct and show the tarification report.
	 * 
	 * @param origin Frame that originated request. 
	 */
	public static void displayTarificationReport( JInternalFrame origin, CompactExplorer exp ){
		Tarification tarification = exp.getTarification();
		if (! tarification.isValid()) {
			String msg = "<HTML>"+Lang.translate("Cannot show the tarification "+
			"report for an invalid tarification. Correct errors first.")+"</HTML>";
			ModalDialogBox.alert(BC.bc.getMajorComponent(),msg);
		} else {
			TarificationReportFactory factory = new TarificationReportFactory();
			Thread t = new Thread( new TarificationReportController( origin, exp, factory ), "Report Toolbox" );
			t.start();
		}
	}

	/**
	 * Launches a new thread to construct and show the fee report.
	 * 
	 * @param origin   Frame origin
	 * @param exp      CompactExplorer, where all datamodels are.
	 * @param details  A detailed report is produced if set to true. It means that the
	 *                 details of the different accounts and transaction are shown
	 *                 in a subreport for each tarifs.
	 */
	public static void displayFeeReport (JInternalFrame origin, CompactExplorer exp, boolean details) {
	    FeeReportFactory factory = new FeeReportFactory(details);
		Thread t = new Thread( new TarificationReportController( origin, exp, factory ), "Report Toolbox" );
		t.start();
	}
	
	// Instance methods ----------------------------------------------
	
	/**
	 * Constructs a ReportToolbox. This is not public since
	 * the class should not have any instances. 
	 */
	private ReportToolbox() {
		// easy constructor
	}
	
	
}

/**
 * The TarificationReportController handles the whole report creation 
 * process in its run method. It specializes on the generation of the
 * Tarification report. 
 */
class TarificationReportController implements Runnable {
	private static final Logger m_log = Logger.getLogger( TarificationReportController.class ); 
	
	private CompactExplorer m_explorer;
	private JInternalFrame m_origin; 
	private ReportFactory m_factory;
		
	private static final String TEXT_INITIALIZING = "Preparing Report calculation..."; 
	private static final String TEXT_CALCULATING = "Generating report...";
	private static final String MSG_COULDNOT = 
		"I am sorry but your report cannot be produced. I enountered errors. "
		+"\nSee the error log for details and/or contact your Administrator about it.";
	
	/**
	 * Constructs a controller for generation of a report on 
	 * exp. 
	 *
	 * @param origin Internal frame that is currently active and 
	 *               that has originated the request. 
	 * @param exp Compact Tree to render report for. 
	 * @param factory An object realising the ReportFactoryInterface, i.e it cans
	 *                produce reports object.
	 */
	TarificationReportController( JInternalFrame origin, CompactExplorer exp, ReportFactory factory ) {
		m_explorer = exp;
		m_origin = origin;
		m_factory = factory;
	}
	
	/**
	 * Launches the creation of a report on the given CompactExplorer. 
	 */
	public void run() {
		ReportProgressDialog ui = new ReportProgressDialog();
		CompilationEventListener cel = new CompilationEventListener( ui );
		
		ui.displayInitializing( m_origin, TEXT_INITIALIZING );
		Report report = m_factory.produceReport( m_explorer );
		if ( report == null || report.report == null ) {
			ui.displayError( m_origin, 
				MSG_COULDNOT );
			return;
		}
		
		ui.displayPhases( TEXT_CALCULATING, report.templatesToCompile + 1 );
		TemplateFactory.addEventListener( cel );
		
		try {
		
			if ( report != null && report.report != null ) {
				JasperPrint print = report.report.renderReport();
				
				ui.displayPhaseComplete( TEXT_CALCULATING );
				ui.cleanup();
				
				if ( print == null ) {
					ui.displayError( m_origin, MSG_COULDNOT );
					return;
				}
				
				assert print != null : 
					"Report production must be successful.";
				
				com.simpledata.bc.reports.tools.Tools.moveTableOfContents( print );
			
				JRViewer view = null; 
				try {
					view = new OurJRViewer(print); 
				}
				catch (JRException e) {
					m_log.error(e); 
				}
				
				InternalFrameDescriptor ifd = new InternalFrameDescriptor();
				ifd.setCenterOnOpen(true);
				ifd.setInitialBounds(new Rectangle(600,400));
				
				JInternalFrame frame = new JInternalFrame(
					"Report Print Preview", // TODO display title of frame
					true,          // resizeable
					true,          // closeable
					true,          // maximizeable
					true           // iconifiable
				);
				frame.getContentPane().add( view );
				frame.setFrameIcon(Resources.iconReporting);
				BC.bc.popupJIFrame( frame, ifd );
			}
		
		} // try
		finally {
			// be sure to remove the listener. 
			TemplateFactory.removeEventListener( cel );
		}
	}
	
	// Inner classes ----------------------------------------------------
	
	/**
	 * Inner class that handles display of changing 
	 * states while compilation and generation of report. 
	 */
	static class CompilationEventListener implements TemplateEventListener {
		private ReportProgressDialog m_ui;
			
		CompilationEventListener( ReportProgressDialog ui ) {
			m_ui = ui;
		}
		
		public void notifyBeforeTemplateCompilation( String report, String subreport ) {
		}
		
		public void notifyAfterTemplateCompilation( String report, String subreport ) {
			m_ui.displayPhaseComplete( TEXT_CALCULATING );
		}
	}
}