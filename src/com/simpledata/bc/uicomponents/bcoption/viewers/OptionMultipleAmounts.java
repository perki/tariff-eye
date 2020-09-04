/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionMultipleAmounts.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption.viewers;
import java.util.ArrayList;

import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable.TableObject;
/**
 * An option viewer for Amounts. <BR>
 * they are presented in a Table
 */
public class OptionMultipleAmounts extends OptionMultipleAbstract {
	
	public OptionMultipleAmounts(AssetsRoot0 ws) {
		super(ws);
	}
	//---------- from abstract ---------------------//
	/** create slice * */
	public void createSlice() {
		OptionManager.createOption(getRootWorkSheet(), OptionMoneyAmount.class);
	}
	
	protected BCOption getOptionAt(int row) {
		return (BCOption) getValueAt(row, 0);
	}
	
	//------------------ TABLE STUFF --------------//
	
	
	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}
	
	/** return the column name * */
	public String getColumnName(int c) {
		String[] names = {"Nb", "Amount" , "Currency"};
		if (c <0 || c >= names.length)
			return "?? " + c;
		return Lang.translate(names[c]);
	}
	
	/**
	 * min width is also used for weigthing
	 */
	public int getColumnMinWidth(int c) {
		int[] cols = {60, 130 , 60};
		if (c <0 || c >= cols.length) return 0;
		return cols[c];
	}
	
	public Class getColumnClass(int c) {
		Class[] classes = {Integer.class, Money.class, Currency.class};
		if (c <0 || c >= classes.length) return Object.class;
		return classes[c];
	}
	
	/**
	* return the money value at this position
	*/
	public Money getMoney(int row, int col) {
		return ((OptionMoneyAmount) getOptionAt(row)).moneyValue(null);
	}
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return getOptions().size();
	}
	
	/**
	 * get my Options vector
	 */
	private final ArrayList getOptions() {
		return getRootWorkSheet().getOptions(OptionMoneyAmount.class);
	}
	
	/**
	 * 
	 */
	public Object getValueAt(int row, int col) {
		if (getRowCount() <= row)
			return "out of bound";
		OptionMoneyAmount oma = ((OptionMoneyAmount) getOptions().get(row));
		oma.addNamedEventListener(this, -1, oma.getClass());
		return oma;
	}
	/**
	 * return true if this cell is editable
	 */
	public boolean isCellEditable(int row, int col) {
		return true;
	}
	/**
	 * @see com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable.Interface#getTOAt(int, int)
	 */
	public TableObject getTOAt(int row, int column) {
		if (getColumnClass(column) == Integer.class)
			return new TOInteger(((OptionMoneyAmount) getOptions().get(row)));
		return null;
	}
	
	//	-------------- class for integer ------------------//
	class TOInteger implements TableObject {
		OptionMoneyAmount oma;
		public TOInteger(OptionMoneyAmount oma) {
			this.oma = oma;
		}
		public Object getValue() {
			return new Integer(oma.numberOfLines(null));
		}
		public void setValue(Object o) {
			if (! (o instanceof Integer)) return;
			oma.setNumberOfLines(
					((Integer) o).intValue());
		}
	}
}
/*
 * $Log: OptionMultipleAmounts.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.17  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.16  2004/09/09 14:12:06  jvaucher
 * - Calculus for DispatcherBounds
 * - OptionCommissionAmountUnder... not finished
 *
 * Revision 1.15  2004/08/04 06:03:12  perki
 * OptionMoneyAmount now have a number of lines
 *
 * Revision 1.14  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.13  2004/05/18 17:04:26  perki
 * Better icons management
 *
 * Revision 1.12  2004/05/18 10:10:27  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/14 16:03:44  perki
 * Nice option table
 *
 * Revision 1.9  2004/05/14 15:27:02  perki
 * Nice option table
 * Revision 1.8 2004/05/14 14:20:19 perki
 * *** empty log message ***
 * 
 * Revision 1.7 2004/05/12 17:13:54 perki zob
 * 
 * Revision 1.6 2004/05/11 15:53:00 perki more calculus Revision 1.5
 * 2004/05/10 19:00:51 perki Better amount option viewer
 * 
 * Revision 1.4 2004/05/07 17:22:37 perki installer ok
 * 
 * Revision 1.3 2004/05/07 15:50:06 perki *** empty log message ***
 * 
 * Revision 1.2 2004/05/06 08:38:01 perki OptionViewer add ons
 * 
 * Revision 1.1 2004/05/06 07:27:32 perki OptionViewer moved
 *  
 */