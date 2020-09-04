/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 3 sept. 2004
 */
package com.simpledata.bc.uitools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;

import foxtrot.Task;
import foxtrot.Worker;

// TODO Add icon, impove look an feel, add default choices.

/**
 * This class provides a toolbox for the dialog box of tarif eye.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: ModalDialogBox.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class ModalDialogBox {
	// CONSTANTS
	private static final int DIALOG_BOX_HEIGHT = 120;
	private static final int DIALOG_BOX_WIDTH  = 420;
	
	private static final int BUTTON_WIDTH = 80;
	
	private static final String TXT_OK      = "Ok";
	private static final String TXT_CANCEL  = "Cancel";
	private static final String TXT_YES     = "Yes";
	private static final String TXT_NO      = "No";
	
	/**
	 * Maximum number of button the dialog box can display
	 */
	public static final int MAX_NB_BUTTONS = DIALOG_BOX_WIDTH / (BUTTON_WIDTH + 10);
	
	// CONSTANTS - Answer codes
	/** No answer */
	private static final int ANS_NULL      = -1;
	/** Answer: OK */
	public static final int ANS_OK        = 0;
	/** Answer: Cancel */
	public static final int ANS_CANCEL    = 1;
	/** Answer: Yes */
	public static final int ANS_YES       = 2;
	/** Answer: No */
	public static final int ANS_NO        = 3;
	
	// FIELDS
	/** Contains the answer code */
	int m_dialogAnswer;
	
	/** Modal internal frame */
	ModalJPanel m_modalJPanel;
	
	/** The dialog box panel */
	private JPanel m_panel;
	
	/** Origin frame */
	private final Component m_origin;
	
	/** Logger */
	private final static Logger m_log = Logger.getLogger(ModalDialogBox.class);
	
	// CONSTRUCTOR
	/**
	 * This construcor is for an internal purpose. Rather use the static
	 * methods.
	 * 
	 * @param origin   Parent container.
	 * @param message  The message displayed in the dialog box. Please format it
	 * in html to get neat display...
	 * @param buttons  Different asnwers of the dialog box. Ordered from left to right.
	 * @param icon     TEMPORARY DISABLED. Use null.
	 */
	private ModalDialogBox(Component origin, 
			   String message, 
			   String[] buttons,
			   Icon icon) {
		// init
		m_dialogAnswer = ANS_NULL;
		m_panel = new JPanel();
		m_origin = origin;
		m_modalJPanel = null;
		buildDialogPanel(message, buttons, icon);

		// run into correct thread
		DialogBoxPainter painter = new DialogBoxPainter();
		if (SwingUtilities.isEventDispatchThread()) {
			painter.run();
			try {
				Worker.post(new Task() {
					public Object run() {
						waitForAnswer();
						return null;
					}
				});
			} catch (Exception e) {
				m_log.error("Foxtrot raised an exception.", e);
			}
		} else {
			try {
				SwingUtilities.invokeAndWait(painter);
				waitForAnswer();
			} catch (Exception e) {
				m_log.error("Unable to run the dialogBox.",e);
			}
		}
	}
	
	/** build the panel */
	private void buildDialogPanel(String message, String[] buttons, Icon icon) {
		m_panel.setLayout(new BorderLayout());
		
		Dimension dim = new Dimension(DIALOG_BOX_WIDTH, DIALOG_BOX_HEIGHT);
		m_panel.setMinimumSize(dim);
		m_panel.setPreferredSize(dim);
		
		JPanel messageContainer = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.gridx = 0;
		if (icon != null) {
		    JLabel iconLabel = new JLabel(icon);
		    
		    messageContainer.add(iconLabel, gbc);
		    
		    gbc.gridx++;
		}
		
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		JLabel messageLabel = new JLabel(message);
		messageLabel.setVerticalAlignment(SwingConstants.TOP);
		messageContainer.add(messageLabel, gbc);
		
		m_panel.add(messageContainer, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		
		gbc.anchor = GridBagConstraints.CENTER;
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		
		buttonPanel.add(new JLabel(), gbc);
		
		gbc.insets = new Insets(2,5,2,5);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		
		Dimension buttonSize = new Dimension (BUTTON_WIDTH, 30);
		for (int i=0; i<buttons.length; i++) {
		    gbc.gridx++;
			JButton aButton = new JButton(buttons[i]);
			aButton.setMinimumSize(buttonSize);
			aButton.addMouseListener(new DialogMouseAdapter(i,this));
			buttonPanel.add(aButton, gbc);
		}
		
		m_panel.add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	/**
	 * Block the current thread, until the user hits a button.
	 */
	synchronized void waitForAnswer() {
		try {
			while (m_dialogAnswer == ANS_NULL)
				wait();
		} catch (InterruptedException e) {
			m_log.error("Dialog box interrupted.", e);
		}
	}
	
	/**
	 * Show an alert dialog box nested in the given container.<BR>
	 * It contains a customised message, and an OK button closing the dialog.
	 * @param origin  Parent container
	 * @param message Message shown to the user. (best in HTML)
	 */
	public static void alert(Component origin, String message) {
		String[] buttons = {Lang.translate(TXT_OK)};
		Icon icon = UIManager.getIcon("OptionPane.warningIcon");
		new ModalDialogBox (origin, message, buttons, icon);
	}
	
	/**
	 * Show an confirmation dialog box nested in the given container.<BR>
	 * It contains a customised message, an OK button and a CANCEL button.
	 * @param origin   Parent container.
	 * @param message  Message shown to the user. (best in HTML)
	 * @return Answer code of the chosen button.
	 */
	public static int confirm(Component origin, String message) {
		String[] buttons = {Lang.translate(TXT_OK), Lang.translate(TXT_CANCEL)};
		Icon icon = UIManager.getIcon("OptionPane.questionIcon");
		ModalDialogBox instance 
		= new ModalDialogBox (origin, message, buttons, icon);
		return instance.m_dialogAnswer;
	}
	
	/**
	 * Ask a yes/no question toward a dialog box nested in the given container.<BR>
	 * It contains a customised message, a YES button, a NO button and a CANCEL button.
	 * @param origin   Parent container.
	 * @param message  Message shown to the user.  (best in HTML)
	 * @return Answer code of the chosen button.
	 */
	public static int questionCancelable(Component origin, String message) {
		String[] buttons = {Lang.translate(TXT_CANCEL), Lang.translate(TXT_YES), Lang.translate(TXT_NO)};
		Icon icon = UIManager.getIcon("OptionPane.questionIcon");
		ModalDialogBox instance = new ModalDialogBox (origin, message, buttons, icon);
		// Answer mapping - for constants
		switch (instance.m_dialogAnswer) {
		case 0:
			return ANS_CANCEL;
		case 1:
			return ANS_YES;
		default: // can only be 2
			return ANS_NO;
		}
		// never reached...
	}
	
	/**
	 * Ask a yes/no question toward a dialog box nested in the given container.<BR>
	 * It contains a customised message, a YES button and a NO button.
	 * @param origin   Parent container.
	 * @param message  Message shown to the user.  (best in HTML)
	 * @return Answer code of the chosen button.
	 */
	public static int questionUncancelable(Component origin, String message) {
		String[] buttons = {Lang.translate(TXT_YES), Lang.translate(TXT_NO)};
		Icon icon = UIManager.getIcon("OptionPane.questionIcon");
		ModalDialogBox instance = new ModalDialogBox (origin, message, buttons, icon);
		// Answer mapping - for constants
		switch (instance.m_dialogAnswer) {
		case 0:
			return ANS_YES;
		default: // can only be 1
			return ANS_NO;
		}
		// never reached...
	}
	
	/**
	 * Show a customized dialog box nested in the given container.<BR>
	 * It contains a customised message, and a user defined set of buttons.
	 * @param origin   Parent container.
	 * @param message  Message shown to the user.  (best in HTML)
	 * @param buttons  Array of alternatives, they will be shown from left to right.
	 *                 The maximum number is known using the MAX_NB_BUTTONS static field.
	 *                 An asertion exception is raised if the array is too large.
	 * @param icon     The icon that will be displayed at the left of the panel
	 * @return Index of the chosen button in the <CODE>buttons</CODE> array.
	 */
	public static int custom(Component origin, String message, String[] buttons, Icon icon) {
		ModalDialogBox instance = new ModalDialogBox (origin, message, buttons, icon);
		return instance.m_dialogAnswer;
	}
	
	/** DialogBox Painter should be called only from Swing Thread*/
	private final class DialogBoxPainter implements Runnable {

		/** Paint the dialog Box */
		public void run() {
			//show modal dialog
			int originHeight = m_origin.getHeight();
			int originWidth = m_origin.getWidth();
			int centerXpos = (originWidth - DIALOG_BOX_WIDTH) / 2;
			int centerYpos = (originHeight - DIALOG_BOX_HEIGHT) / 2;
			
			m_modalJPanel = ModalJPanel.createSimpleModalJInternalFrame(
					m_panel, 
					m_origin, 
					new Point(centerXpos,centerYpos), 
					false, 
					null, 
					Resources.modalBgColor);
			
			// beep
			Toolkit.getDefaultToolkit().beep();
		}
		
	}
	
	/** Mouse listener */
	private static final class DialogMouseAdapter extends MouseAdapter {
		private final int m_eventId;
		private final ModalDialogBox m_mdb;
		
		DialogMouseAdapter(int eventId, ModalDialogBox mdb) {
			m_eventId = eventId;
			m_mdb = mdb;
		}
		
		/**
		 * Kill the dialog box and set the answer code.
		 */
		public void mouseClicked(MouseEvent e) {
			synchronized (m_mdb) {
				m_mdb.m_dialogAnswer = m_eventId;
				m_mdb.m_modalJPanel.close();
				m_mdb.notify();	
			}
		}
	}
}
