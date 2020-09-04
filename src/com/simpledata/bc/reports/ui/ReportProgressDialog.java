/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * User interface helper classes for the reporting 
 * module. 
 */
package com.simpledata.bc.reports.ui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.InternalFrameDescriptor;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SButton;



/**
 * The ReportProgressDialog has essentially two visible
 * states: 
 * <ul>
 *  <li> A first one that tells the user to wait for 
 *      Reporting to initialize (modal) </li>
 *  <li> A second one that that has n phases that 
 *       can be completed. </li>
 * </ul>
 * 
 * If the second stage is completed (all phases of second
 * stage), all dialogs disappear. 
 */
public class ReportProgressDialog {
	
	/** STAGE_ constants for keeping internal state */
	private final static int STAGE_INIT = 0; 
	/** STAGE_ constants for keeping internal state */
	private final static int STAGE_WAITINGFORINITIALIZE = 1; 
	/** STAGE_ constants for keeping internal state */
	private final static int STAGE_PHASESPROGRESS = 2; 
	/** STAGE_ constants for keeping internal state */
	private final static int STAGE_USELESS = 3; 
	
	// contains the class state in form of a constant
	// from the STAGE_* family
	private int m_stage; 
	
	// contains the modal dialog that is displayed on
	// transition from state STAGE_WAITINGFORINITIALIZE to 
	// state STAGE_PHASESPROGRESS
	private ModalJPanel m_modal;
		
	private JLabel m_stage1Label;
		
	private JInternalFrame m_progress; 
		
	private JLabel m_stage2Label;
	private JProgressBar m_stage2Progress; 
	
	private int m_stage2TotalPhases; 
	private int m_stage2CurrentPhase;
	
	private ModalJPanel m_error; 
	
	/**
	 * Constructs a progress dialog. 
	 */
	public ReportProgressDialog() {
		m_stage = STAGE_INIT;
	}
	
	/**
	 * Initializes first stage that consists of a modal 
	 * dialog that displays the message passed as parameter. 
	 *
	 * @param origin Frame that the reporting has been launched
	 *               from. 
	 * @param msg Message to display. Will be translated 
	 *            internally. 
	 */
	public void displayInitializing( JInternalFrame origin, String msg ) {
		assert m_stage == STAGE_INIT : 
			"Stage of progress dialog should be STAGE_INIT"; 
		
		JInternalFrame frame          = new JInternalFrame();
		Container comp                = frame.getContentPane();
		GridBagLayout layout          = new GridBagLayout(); 
		GridBagConstraints constraint = new GridBagConstraints(); 
		
		constraint.insets = new Insets( 2, 5, 2, 5 ); 
		constraint.gridx = 0; 
		constraint.gridy = 0;
		
		m_stage1Label                 = new JLabel( Lang.translate( msg ) );
		Point delta                   = new Point( 40, 40 );
		m_modal = 
			ModalJPanel.warpJInternalFrame( 
					frame, origin, delta,Resources.modalBgColor );
		
		comp.setLayout( layout );
		comp.add( m_stage1Label, constraint );
		frame.pack();
		frame.show();
		
		m_stage = STAGE_WAITINGFORINITIALIZE;
	}
	
	/**
	 * Initializes the second stage of report creation. 
	 * The message passed as parameter will be translated and shown
	 * on top of a progress bar. 
	 * 
	 * @param message Message to display. 
	 * @param phases How many distinct events will there be ? 
	 */
	public void displayPhases( String message, int phases ) {
		assert m_stage == STAGE_WAITINGFORINITIALIZE : 
			"Must show modal waiting for dialog before entering here"; 
		
		// close first dialog
		m_modal.close(); 
		m_modal = null; 
		m_stage1Label = null;
		
		// open second dialog
		
		if ( phases > 1 ) {
			InternalFrameDescriptor ifd = new InternalFrameDescriptor();
			ifd.setInitialBounds(new Rectangle(200,100));
			ifd.setCenterOnOpen(true);
				
			m_progress = new JInternalFrame(
				Lang.translate( "Report Generation Progress" ), 
				false,         // resizeable
				false,         // closeable
				false,         // maximizeable
				false          // iconifiable
			);
			
			Container cont = m_progress.getContentPane();
			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints constraint = new GridBagConstraints();
			m_stage2Label = new JLabel( Lang.translate( message ) );
			m_stage2Progress = new JProgressBar( ); 
			
			cont.setLayout( layout );
			
			m_stage2Progress.setValue(0);
			m_stage2Progress.setMaximum( phases );
			m_stage2Progress.setStringPainted(true);
			
			// general setup of constraint
			constraint.insets = new Insets( 2, 5, 2, 5 ); 
			constraint.fill = GridBagConstraints.BOTH; 
			constraint.weightx = 1.0;
			constraint.weighty = 1.0;
			
			constraint.gridx = 0; 
			constraint.gridy = 0; 
			cont.add(m_stage2Label, constraint);
			
			constraint.gridx = 0; 
			constraint.gridy = 1; 
			cont.add( m_stage2Progress, constraint );
	
			BC.bc.popupJIFrame( m_progress, ifd );
		}
		
		m_stage2TotalPhases = phases; 
		m_stage2CurrentPhase = 0;

		// remember new stage
		m_stage = STAGE_PHASESPROGRESS; 
	}
	
	/**
	 * Tells the progress bar (in STAGE_PHASESPROGRESS) to 
	 * show another phase as completed, updates the message text. 
	 *
	 * If the current phase is already equal to the number of
	 * phases, this method does nothing. Get your phase counts
	 * right, and you won't have that kind of trouble. 
	 */
	public void displayPhaseComplete( String message ) {
		assert m_stage == STAGE_PHASESPROGRESS : 
			"Must be in second stage"; 
		
		// swallow this error without warning.
		if ( m_stage2CurrentPhase >= m_stage2TotalPhases )
			return;
		
		if ( m_progress != null ) {
			m_stage2Label.setText( Lang.translate( message ) );
			
			m_stage2CurrentPhase += 1;
			m_stage2Progress.setValue( m_stage2CurrentPhase ); 
		}
	}
	
	/**
	 * Displays error message and waits for user to click 
	 * dismiss. This makes the user interface 'useless' - no 
	 * other displays can be made. 
	 *
	 * @param origin Frame that the reporting has been launched
	 *               from. 
	 * @param msg Error message to display. Will be translated 
	 *            internally. 
	 */
	public void displayError( JInternalFrame origin, String msg ) {
		// hide all dialogs whatsoever
		cleanup();
		
		JInternalFrame frame          = new JInternalFrame(
			Lang.translate( "Error while generating Report" ), 
			false,         // resizeable
			true,          // closeable
			false,         // maximizeable
			false          // iconifiable
		);
		Container comp                = frame.getContentPane();
		GridBagLayout layout          = new GridBagLayout(); 
		GridBagConstraints constraint = new GridBagConstraints(); 
		// split error message in multiple lines
		String[] lines = msg.split( "\n" );
		
		constraint.insets = new Insets( 2, 5, 2, 5 ); 
		constraint.gridx = 0; 
		constraint.gridy = 0;
		

		Point delta                   = new Point( 40, 40 );
		m_error = 
			ModalJPanel.warpJInternalFrame( frame, origin, delta, Resources.modalBgColor );
		
		comp.setLayout( layout );
		
		// add all lines of error msg to the dialog
		for (int i=0; i<lines.length; ++i) {
			comp.add( new JLabel( Lang.translate( lines[i] ) ), constraint );
			constraint.gridy += 1;
		}
		
		// TODO Make this button the default button of the error 
		// dialog
		SButton btn = new SButton( Lang.translate( "Dismiss" ) );
		btn.setDefaultCapable( true );
		comp.add( btn, constraint );
		
		// add dialog close functionality to the dialog
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_error.close();
			}
		} );
		
		
		frame.pack();
		frame.show();
		
		m_stage = STAGE_USELESS;
	}
	
	/**
	 * Tells the progress bar that we're all done. This kills 
	 * all ui components and makes this class instance useless. 
	 */
	public void cleanup() {
		// assume that this method can be called any time
		// in the graph, check all the pointers used. 
		if ( m_modal != null ) {
			m_modal.close();
			m_modal = null;
		}
		m_stage1Label = null; 
		m_stage2Label = null;
		m_stage2Progress = null;
		
		if ( m_progress != null ) {
			m_progress.hide();
			m_progress.dispose();
			m_progress = null;
		}
		
		if ( m_error != null ) {
			m_error.close();
			m_error = null;
		}
		
		m_stage = STAGE_USELESS;
	}
	
}