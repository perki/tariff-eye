/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: JTextAreaNamedDescription.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;

/**
 * A TextArea that handles himself the edition of a Description
 */
public abstract class JTextAreaNamedDescription extends JTextAreaBC implements NamedEventListener {
	Named n;

	protected JTextAreaNamedDescription() {
		setEditable(false);
	}

	public  JTextAreaNamedDescription(Named n) {
		this();
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
		setText(n == null ? "" : n.getDescription() );
		setEditable(n != null);
	}

	/**
	 * save the changes
	 */
	final public void stopEditing() {
		String s= getText().trim();
//		if (s.equals("")) { //rollback
//			refresh();
//		} else { // save
//			if (n != null)
//				n.setDescription(this.getText());
//		}
		if (n != null) {
		    if (s.equals("")) {
		        n.setDescription(null);
		    } else {
		        n.setDescription(this.getText());
		    }
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
 *  $Log: JTextAreaNamedDescription.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.5  2004/11/12 14:28:39  jvaucher
 *  New NamedEvent framework. New bugs ?
 *
 *  Revision 1.4  2004/09/22 15:39:55  carlito
 *  Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 *  Revision 1.3  2004/05/22 08:39:36  perki
 *  Lot of cleaning
 *
 *  Revision 1.2  2004/03/24 13:11:14  perki
 *  Better Tarif Viewer no more null except
 *
 *  Revision 1.1  2004/03/22 19:32:45  perki
 *  step 1
 *
 */