/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: MoneyValueInput.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.Dimension;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.uitools.SNumField;

/**
 * A TextField to manipulate MoneyValues <BR>
 * NOTE: does not refresh when event occures on the datamodel
 */
public abstract class MoneyValueInput extends SNumField {
	/** Logger */
	private final static Logger m_log = Logger.getLogger(MoneyValueInput.class);
	
	/** My default WIDTH **/
	public static int DEF_W = 100;
	// XXX corrected to avoid startup deadlock
	public static int DEF_H = 20; // see MoneyEditor.DEF_H;
	
	/** the money Value I do monitor **/
	private Money moneyValue ; 
	

	public MoneyValueInput(Money money) {
		this(money, true);
	}
	
	public MoneyValueInput(Money money, boolean selectOnFocus) {
		super("", selectOnFocus);
		setDigitAfterComa(2);
		setTruncateAfterMax(true);
		moneyValue = money;
		_setDim(new Dimension(DEF_W, DEF_H));
		refresh(); // load the text
		this.selectAll(); // selecte it. Excel behaviour
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.tools.JTextFieldBC
	 */
	final public void stopEditing() {
		Double temp = super.getDouble();
		if (temp != null) {
			moneyValue.setValue(temp.doubleValue());
		}
		editionStopped();
		refresh();
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.tools.JTextFieldBC#startEditing()
	 */
	final public void startEditing() {
		editionStarted();
	}
	
	public void refresh() {
		setDouble(moneyValue.getValueDouble());
	}
	
	/**
	 * Sets all sizes to dimension d
	 * @param d
	 */
	private void _setDim(Dimension d) {
		this.setSize(d);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
	}
	
	/** called when edition stopped **/
	public abstract void editionStopped();
	
	/** called when edition start **/
	public abstract void editionStarted();

	
}

/**
 *  $Log: MoneyValueInput.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.9  2004/09/07 16:21:03  jvaucher
 *  - Implemented the DispatcherBounds to resolve the feature request #24
 *  The calculus on this dispatcher is not yet implemented
 *  - Review the feature of auto select at startup for th SNumField
 *
 *  Revision 1.8  2004/09/02 16:05:51  jvaucher
 *  - Ticket #1 (JTextField behaviour) resolved
 *  - Deadlock at loading problem resolved
 *  - New kilo and million feature for the SNumField
 *
 *  Revision 1.7  2004/08/02 14:55:43  perki
 *  *** empty log message ***
 *
 *  Revision 1.6  2004/08/02 10:17:43  carlito
 *  new fixed fee panel
 *
 *  Revision 1.5  2004/07/26 17:39:37  perki
 *  Filler is now home
 *
 *  Revision 1.4  2004/05/20 17:05:30  perki
 *  One step ahead
 *
 *  Revision 1.3  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.2  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.1  2004/03/12 14:07:51  perki
 *  Vaseline machine
 *
 */