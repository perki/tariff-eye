/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionMultipleTransactions.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption.viewers;


import java.util.ArrayList;


import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.bcoption.OptionTransaction;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable.TableObject;

/**
 * An option viewer for Transactions. <BR>
 * they are presented in a Table
 */
public class OptionMultipleTransactions extends OptionMultipleAbstract {
	
	/**
	 * @param ws
	 */
	public OptionMultipleTransactions(TransactionsRoot0 ws) {
		super(ws);
		jTable.setBooleanIconText(true,Resources.arrowRight,
				Lang.translate("to bank"));
		jTable.setBooleanIconText(false,Resources.arrowLeft,
				Lang.translate("from bank"));
	}
	
	//		---------- from abstract ---------------------//
	/** create slice * */
	public void createSlice() {
		OptionManager.createOption(getRootWorkSheet(), OptionTransaction.class);
	}
	
	protected BCOption getOptionAt(int row) {
		return (BCOption) getValueAt(row, 0);
	}
	
	/**
	 * get my Options vector
	 */
	private final ArrayList getOptions() {
		return getRootWorkSheet().getOptions(OptionTransaction.class);
	}
	//------------------ TABLE STUFF --------------//
	
	
	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 4;
	}
	
    private static final String[] names = {"Direction","Nb", "Amount" , "Currency"};
	/** return the column name * */
	public String getColumnName(int c) {
		if (c <0 || c >= names.length)
			return "?? " + c;
		return Lang.translate(names[c]);
	}
	
	private static final int[] colsWidth = {130 , 60, 130,60};
	/**
	 * min width is also used for weigthing
	 */
	public int getColumnMinWidth(int c) {
		if (c <0 || c >= colsWidth.length) return 0;
		return colsWidth[c];
	}
	
	private static final Class[] columnClasses = {Boolean.class, Integer.class, 
			   Money.class, Currency.class};
	/** column classes **/
	public Class getColumnClass(int c) {
		
		if (c <0 || c >= columnClasses.length)
			return Object.class;
		return columnClasses[c];
	}
	
	/**
	 * return the money value at this position
	 */
	public Money getMoney(int row, int col) {
		return ((OptionTransaction) getOptionAt(row))
			.getTransactionValue().getMoneyValue();
	}
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return getOptions().size();
	}
	
	/**
	 * 
	 */
	public Object getValueAt(int row, int col) {
		if (getRowCount() <= row)
			return "out of bound";
		
		OptionTransaction oma = ((OptionTransaction) getOptions().get(row));
		
		return oma;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable.Interface#getTOAt(int, int)
	 */
	public TableObject getTOAt(int row, int column) {
		if (getRowCount() <= row)
			return null;
		if (getColumnClass(column) == Integer.class)
			return new TOInteger(((OptionTransaction) getOptions().get(row)));
		if (getColumnClass(column) == Boolean.class)
			return new TODirection(((OptionTransaction) getOptions().get(row)));
		return null;
	}
	
	/**
	 * return true if this cell is editable
	 */
	public boolean isCellEditable(int row, int col) {
		return true;
	}

	//	-------------- class for booleans ------------------//
	class TODirection implements TableObject {
		OptionTransaction oma;
		public TODirection(OptionTransaction oma) {
			this.oma = oma;
		}
		public Object getValue() {
			return new Boolean(oma.getTransactionValue().inGoingToBank());
		}
		public void setValue(Object o) {
			if (! (o instanceof Boolean)) return;
			oma.getTransactionValue().setInGoingToBank(
					((Boolean) o).booleanValue());
			oma.fireDataChange();
		}
	}

	//-------------- class for integer ------------------//
	class TOInteger implements TableObject {
		OptionTransaction oma;
		public TOInteger(OptionTransaction oma) {
			this.oma = oma;
		}
		public Object getValue() {
			return new Integer(oma.getTransactionValue().getAverageNumber());
		}
		public void setValue(Object o) {
			if (! (o instanceof Integer)) return;
			oma.getTransactionValue().setAverageNumber(
					((Integer) o).intValue());
			oma.fireDataChange();
		}
	}
	
}

/*
 * $Log: OptionMultipleTransactions.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/11/10 17:47:47  perki
 * Closed bug #50 : TransactionValues did not save the direction of their transactions
 *
 * Revision 1.7  2004/09/14 14:46:29  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/08/04 06:03:12  perki
 * OptionMoneyAmount now have a number of lines
 *
 * Revision 1.5  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.4  2004/05/18 17:04:26  perki
 * Better icons management
 *
 * Revision 1.3  2004/05/18 10:10:27  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/14 16:00:41  perki
 * Nice option table
 *
 * Revision 1.1  2004/05/14 14:20:19  perki
 * *** empty log message ***
 *
 */