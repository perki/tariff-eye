/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: JTextFieldNamedTitle.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;

/** 
 * a class directly listening for Named Title changes 
 * **/
public abstract class JTextFieldNamedTitle
	extends JTextFieldBC
	implements NamedEventListener {
	Named n;
	boolean editable;
	
	private static final Logger m_log = Logger.getLogger(JTextFieldNamedTitle.class);

	protected JTextFieldNamedTitle() {
		setEditableMode(true);
		setEditable(false);
	}

	/**
	 * Create a new JTextField to edit named titles.
	 * @param editable set to false if this JTextField cannot be edited.
	 */
	public JTextFieldNamedTitle(Named n,boolean editable) {
		this();
		setEditableMode(editable);
		setNamedObject(n);
	}

	public void setNamedObject(Named o) {
		if (n == o)
			return;
		n= o;
		refresh(); // load the text

		if (n == null) {
			setEditable(false);
			return;
		}
		setEditable(true);

		n.addNamedEventListener(this,NamedEvent.TITLE_MODIFIED,n.getClass());
	}
	
	/** set the default editable mode **/
	public void setEditableMode(boolean b) {
		this.editable = b;
		setEditable(editable);
	}
	
	/** override JTextField setEditable **/
	public void setEditable(boolean b) {
		if (! b) { 
			super.setEditable(false);
			return ;
		}
		super.setEditable(editable);
	}

	/**
	 * @see com.simpledata.bc.datamodel.event.NamedEventListener#eventOccured(com.simpledata.bc.datamodel.event.NamedEvent)
	 */
	public void eventOccured(NamedEvent e) {
		if (e.getSource() == n)
			refresh();
	}

	/**
	 * reread the title
	 */
	public void refresh() {
		setText(n == null ? "" : n.getTitle() );
		setEditable(n != null);
	}

	/**
	 * save the changes
	 */
	final public void stopEditing() {
		String s= getText().trim();
		if (s.equals("")) { //rollback
			refresh();
		} else { // save
			if (n != null)
				n.setTitle(this.getText());
		}
	
		editionStopped();
	}

	/**
	 * @see com.simpledata.bc.uicomponents.tools.JTextFieldBC#startEditing()
	 */
	final public void startEditing() {

		editionStarted();
	}

	/** called when edition stopped **/
	public abstract void editionStopped();

	/** called when edition start **/
	public abstract void editionStarted();
}
/**
 *  $Log: JTextFieldNamedTitle.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.8  2004/11/12 14:28:39  jvaucher
 *  New NamedEvent framework. New bugs ?
 *
 *  Revision 1.7  2004/10/14 13:45:07  jvaucher
 *  - Ticket #84: Corrected into ZZuListener.
 *
 *  Revision 1.6  2004/05/22 08:39:36  perki
 *  Lot of cleaning
 *
 *  Revision 1.5  2004/05/06 07:06:25  perki
 *  WorkSheetPanel has now two new methods
 *
 *  Revision 1.4  2004/03/24 13:11:14  perki
 *  Better Tarif Viewer no more null except
 *
 *  Revision 1.3  2004/03/22 18:59:02  perki
 *  step 1
 *
 *  Revision 1.2  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.1  2004/03/12 14:07:51  perki
 *  Vaseline machine
 *
 */