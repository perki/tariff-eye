/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: FeeBySliceCell.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.workplace.tools;

import java.awt.Color;
import java.awt.Component;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.workplace.tools.FeeBySlice;
import com.simpledata.bc.components.worksheet.workplace.tools.FeeBySliceValue;
import com.simpledata.bc.tools.OrderedMapOfDoublesObject;
import com.simpledata.bc.uitools.SNumField;

/**
 * Cells for Fixed By Slice Table
 */
public abstract class FeeBySliceCell extends DataBySliceCell {

    static final int TYPE_KEY= 0;
	static final int TYPE_FIXED= 1;
	
	
	protected FeeBySlice fbs;
	
    /**
     * @param rbsp
     * @param omodo
     * @param type
     */
    protected FeeBySliceCell(FeeBySlicePanel rbsp, OrderedMapOfDoublesObject omodo, int type) {
        super(rbsp, omodo, type);
        this.fbs = rbsp.getFbs();
    }
	
	/** tools that return the FeeBySlice value at this line **/
	protected final FeeBySliceValue getFBSV() {
	    return (FeeBySliceValue) omodo.getValue();
	}
	
	
	/** money format String **/
	protected String moneyFormat(double d) {
		return SNumField.formatNumber(d,2,true);
	}
	
	/** tool to create Cell corresponding to the types **/
	public static FeeBySliceCell create(
			FeeBySlicePanel rbsp,
			OrderedMapOfDoublesObject omodo,
			int type) {
			switch (type) { 
				case TYPE_KEY :
					return new FKeyCell(rbsp, omodo);
				case TYPE_FIXED :
					return new FFixedCell(rbsp, omodo);
			}
			return null;
		}
  

}


class FKeyCell extends FeeBySliceCell {
	private SNumField jtf;

	FKeyCell(FeeBySlicePanel rbsp, OrderedMapOfDoublesObject omodo) {
		super(rbsp, omodo, TYPE_KEY);
	}
	
	/** return true if this cell is editable **/
	public boolean isEditable() {
		if (omodo.getKey() == 0d) return false;
		return true;
	}
	
	/**
	 * return an editor for this data
	 *
	 */
	public Component _getEditor() {
		jtf =  getMySNumFieldEditor(toString(),true);
		return jtf;
	}

	/**
	 * stop the edition of this cell
	 *
	 */
	public void _stopCellEditing() {
		if (jtf == null) return;
		Integer d= jtf.getInteger();
		if (d != null)
			omodo.setMyKey(d.intValue());
		jtf= null;
	}

	/**
	 * return the result as a String
	 */
	public String toString() {
		return ""+((int) omodo.getKey());
	}
}


class FFixedCell extends FeeBySliceCell {
    private static final Logger m_log = 
        Logger.getLogger( FFixedCell.class );
    
	private SNumField jtf;

	FFixedCell(FeeBySlicePanel rbsp, OrderedMapOfDoublesObject omodo) {
		super(rbsp, omodo, TYPE_FIXED);
	}

	/** return true if this cell is editable **/
	public boolean isEditable() {
		return true;
	}
	
	/**
	 * return an editor for this data
	 *
	 */
	public Component _getEditor() {
		jtf = getMySNumFieldEditor(moneyFormat(getFBSV().getValue()));
		return jtf;
	}

	/** stop cell editing **/
	public void _stopCellEditing() {
	    if (jtf == null) return;
		Double d = jtf.getDouble();
		getFBSV().setValue(d.doubleValue());
		jtf = null;
	}
	
	/** return the ForeGround Color **/
	public Color getForegroundColor() {
		return super.getForegroundColor();
	}

	/**
	 * return the result as a String
	 */
	public String toString() {
		return moneyFormat(getFBSV().getValue());
	}
}