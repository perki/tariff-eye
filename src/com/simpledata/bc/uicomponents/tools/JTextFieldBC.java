/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: JTextFieldBC.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

/**
 * A default class for JTextField.
 * change its color while editing<BR>
 * <BR>
 * TODO Find a better way to get rid of Edition Detection problem
 * <BR>
 * <B>Known BUG!</B> 
 * This does not work if the component does not have focus. This is why a
 * trick with a Document Listener has been added to alter this bug.<BR>
 * Testing can be done, by starting the edition of a SNumField in a JTable by
 * pressing a key.. then the snumfield is in edition position but has no focus.
 */
public abstract class JTextFieldBC 
	extends JTextField implements JTextComponentBC {
	
	private ZZuFieldListener zz;

	protected JTextFieldBC() {
		this(false);
	}
	
	protected JTextFieldBC(boolean selectAllOnStart) {
		addDefaultListeners(this,true,selectAllOnStart);
	}
	
	/** add default listeners to a JTextComponent **/
	public static void addDefaultListeners(JTextComponentBC jtcbc,
			boolean loseFocusOnEnter,boolean selectAllOnStart) {
		ZZuFieldListener z = new ZZuFieldListener(jtcbc,loseFocusOnEnter,selectAllOnStart);
		
		jtcbc.getJTextComponent().addKeyListener(z);
		jtcbc.getJTextComponent().addFocusListener(z);
		
		jtcbc.getJTextComponent().getDocument().addDocumentListener(z);
		
		if (jtcbc instanceof JTextFieldBC)
			((JTextFieldBC) jtcbc).zz = z;
	
	}
	
	/** catch document changed to add the appropriate listeners **/
	public final void setDocument(Document doc) {
		// get previous document listeners and add ZZuFieldListener if found
		super.setDocument(doc);
		getDocument().addDocumentListener(zz);
	}
	
	
	public final JTextComponent getJTextComponent() {
		return this;
	}
	
}

class ZZuFieldListener extends KeyAdapter implements  FocusListener , DocumentListener {
	boolean loseFocusOnEnter;
	boolean selectAllOnStart;
	
	JTextComponentBC jtcbc;
	JTextComponent jtc;
	
	boolean hasFocus;
	
	boolean isEditing;
	
	private static final Logger m_log = Logger.getLogger(ZZuFieldListener.class);
	
	public ZZuFieldListener(final JTextComponentBC jtcbc, 
			boolean loseFocusOnEnter,boolean selectAllOnStart) {
		
		this.selectAllOnStart = selectAllOnStart;
		this.jtcbc = jtcbc;
		this.loseFocusOnEnter = loseFocusOnEnter;
		this.isEditing = false;
		jtc = jtcbc.getJTextComponent();
	}
	
	/**
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		if (selectAllOnStart)
			jtc.selectAll();
		}

	/**
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		stopEditing();
	}
	

	private void stopEditing() {
		if (isEditing) {
			
			isEditing = false;
			jtc.setForeground(JTextComponentBC.NORMAL_C);
			jtcbc.stopEditing();
			if (jtc.hasFocus())
				jtc.transferFocus();
		}
	}
	
	private void startEditing() {
		if (! isEditing) {
			
			isEditing = true;
			jtc.setForeground(JTextComponentBC.EDITING_C);
			//if (selectAllOnStart)
			//	jtc.selectAll();
			jtcbc.startEditing();
		}
	}
	
	//------------ key Adapter --------------//
	private boolean enterPressed= false;
	public void keyPressed(KeyEvent evt) {
		enterPressed = (evt.getKeyCode() == KeyEvent.VK_ENTER);
		
		
	}

	public void keyTyped(KeyEvent evt) {
		if (loseFocusOnEnter && enterPressed) {
			stopEditing();
		}
	}
	
	public void keyReleased(KeyEvent evt) {
		//catch eventual ENTER key typed
		
		if (!loseFocusOnEnter || !enterPressed)
			startEditing();
		
		if (evt.getKeyCode() == KeyEvent.VK_F2) {
			JTextFieldBC source = (JTextFieldBC) evt.getSource();
			source.select(0,0); // unselect
			source.setCaretPosition(source.getText().length());
		}
	}
	

	//________ Document Listener implementations -----------//
	
	public void changedUpdate(DocumentEvent e) {
		focusAndEdit();
	}

	public void insertUpdate(DocumentEvent e) {
		focusAndEdit();
	}

	public void removeUpdate(DocumentEvent e) {
		focusAndEdit();
	}
	
	private void focusAndEdit() {
		if (! jtc.hasFocus()) {
			jtc.grabFocus();
		}
	}
	
}

/**
 *  $Log: JTextFieldBC.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.11  2004/10/14 13:45:07  jvaucher
 *  - Ticket #84: Corrected into ZZuListener.
 *
 *  Revision 1.10  2004/09/10 16:29:48  jvaucher
 *  Allows negative percentage for discount
 *
 *  Revision 1.9  2004/09/10 13:03:09  jvaucher
 *  SNumField stronger and stronger
 *
 *  Revision 1.8  2004/09/09 14:12:06  jvaucher
 *  - Calculus for DispatcherBounds
 *  - OptionCommissionAmountUnder... not finished
 *
 *  Revision 1.7  2004/09/07 16:21:03  jvaucher
 *  - Implemented the DispatcherBounds to resolve the feature request #24
 *  The calculus on this dispatcher is not yet implemented
 *  - Review the feature of auto select at startup for th SNumField
 *
 *  Revision 1.6  2004/09/03 11:31:48  jvaucher
 *  - Fixed ticket #2. coma problem in SNumField
 *  - Fixed ticket #32, single digit input in SNumField
 *
 *  Revision 1.5  2004/09/02 16:05:51  jvaucher
 *  - Ticket #1 (JTextField behaviour) resolved
 *  - Deadlock at loading problem resolved
 *  - New kilo and million feature for the SNumField
 *
 *  Revision 1.4  2004/06/28 10:38:48  perki
 *  Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 *  Revision 1.3  2004/05/20 09:39:43  perki
 *  *** empty log message ***
 *
 *  Revision 1.2  2004/03/22 18:59:02  perki
 *  step 1
 *
 *  Revision 1.1  2004/03/12 14:07:51  perki
 *  Vaseline machine
 *
 */