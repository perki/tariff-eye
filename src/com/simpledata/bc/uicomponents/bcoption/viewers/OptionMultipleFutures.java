/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionMultipleFutures.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption.viewers;

import java.util.ArrayList;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.OptionFuture;
import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.worksheet.dispatcher.FuturesRoot0;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable;
import com.simpledata.bc.uicomponents.bcoption.tools.BCOptionJTable.TableObject;

/**
 * An option viewer for Futures. <BR>
 * they are presented in a Table
 */
public class OptionMultipleFutures extends OptionMultipleAbstract {
	
	/**
	 * @param ws
	 */
	public OptionMultipleFutures(FuturesRoot0 ws) {
		super(ws);
		jTable.setBooleanIconText(true,Resources.futureOpen,
				Lang.translate("on opening"));
		jTable.setBooleanIconText(false,Resources.futureClose,
				Lang.translate("on closeing"));
	}
	
//	---------- from abstract ---------------------//
    /**
     * @see OptionMultipleAbstract#createSlice()
     */
    public void createSlice() {
        OptionManager.createOption(
                getRootWorkSheet(), OptionFuture.class);
    }

    /**
     * @see OptionMultipleAbstract#getOptionAt(int)
     */
    protected BCOption getOptionAt(int row) {
    	return (BCOption) getValueAt(row, 0);
    }

    /**
     * @see BCOptionJTable.Interface#getMoney(int, int)
     */
    public Money getMoney(int row, int column) {
        assert true : "do not use me";
        return null;
    }

    /**
     * @see BCOptionJTable.Interface#getTOAt(int, int)
     */
    public TableObject getTOAt(int row, int column) {
        if (getRowCount() <= row)
			return null;
		if (getColumnClass(column) == Integer.class)
			return new FOInteger(((OptionFuture) getOptions().get(row)));
		if (getColumnClass(column) == Boolean.class)
			return new FODirection(((OptionFuture) getOptions().get(row)));
		return null;
    }

    /**
     * @see BCOptionJTable.Interface#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    private static final String[] names = {"On","Quantity"};
    /**
     * @see BCOptionJTable.Interface#getColumnName(int)
     */
    public String getColumnName(int c) {
		if (c <0 || c >= names.length)
			return "?? " + c;
		return Lang.translate(names[c]);
    }

    private static final int[] colsWidth = {130 , 60};
	/**
	 * min width is also used for weigthing
	 */
	public int getColumnMinWidth(int c) {
		if (c <0 || c >= colsWidth.length) return 0;
		return colsWidth[c];
	}

	private static final Class[] columnClasses = {Boolean.class, Integer.class};
	/** column classes **/
	public Class getColumnClass(int c) {
		if (c <0 || c >= columnClasses.length)
			return Object.class;
		return columnClasses[c];
	}
	/**
	 * get my Options vector
	 */
	private final ArrayList getOptions() {
		return getRootWorkSheet().getOptions(OptionFuture.class);
	}
	
	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return getOptions().size();
	}

    /**
     * @see BCOptionJTable.Interface#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        if (getRowCount() <= row)
			return "out of bound";
		OptionFuture oma = ((OptionFuture) getOptions().get(row));
		return oma;
    }

    /**
     * @see BCOptionJTable.Interface#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int col) {
        return true;
    }

	//	-------------- class for booleans ------------------//
	class FODirection implements TableObject {
	    OptionFuture oma;
		public FODirection(OptionFuture oma) {
			this.oma = oma;
		}
		public Object getValue() {
			return new Boolean(oma.onOpening());
		}
		public void setValue(Object o) {
			if (! (o instanceof Boolean)) return;
			oma.setOnOpening(
					((Boolean) o).booleanValue());
			oma.fireDataChange();
		}
	}

	//-------------- class for integer ------------------//
	class FOInteger implements TableObject {
	    OptionFuture oma;
		public FOInteger(OptionFuture oma) {
			this.oma = oma;
		}
		public Object getValue() {
			return new Integer(oma.numberOfContracts());
		}
		public void setValue(Object o) {
			if (! (o instanceof Integer)) return;
			oma.setNumberOfContracts(
					((Integer) o).intValue());
			oma.fireDataChange();
		}
	}
	
}
