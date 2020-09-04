/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: JTextAreaBC.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

/**
 * A default class for JTextArea.
 * change its color while editing
 */
public abstract class JTextAreaBC 
			extends JTextArea implements JTextComponentBC {

	protected JTextAreaBC() {
		JTextFieldBC.addDefaultListeners(this,false,false);
	}


	/**
	 * @see com.simpledata.bc.uicomponents.tools.JTextComponentBC#getJTextComponent()
	 */
	public JTextComponent getJTextComponent() {
		return this;
	}

}


/**
 *  $Log: JTextAreaBC.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.4  2004/09/07 16:21:03  jvaucher
 *  - Implemented the DispatcherBounds to resolve the feature request #24
 *  The calculus on this dispatcher is not yet implemented
 *  - Review the feature of auto select at startup for th SNumField
 *
 *  Revision 1.3  2004/09/03 11:31:48  jvaucher
 *  - Fixed ticket #2. coma problem in SNumField
 *  - Fixed ticket #32, single digit input in SNumField
 *
 *  Revision 1.2  2004/06/21 06:56:23  perki
 *  Loading panel ok
 *
 *  Revision 1.1  2004/03/22 19:32:45  perki
 *  step 1
 *
 */