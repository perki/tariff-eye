/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: DataBySliceCell.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.workplace.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import com.simpledata.bc.components.worksheet.workplace.tools.DataBySlice;
import com.simpledata.bc.tools.OrderedMapOfDoublesObject;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel.DBSTableEditor;
import com.simpledata.bc.uitools.SNumField;

/**
 *Default code for Cells
 */
public abstract  class DataBySliceCell {
   
	
	static Color COLOR_NOT_EDITABLE = Color.LIGHT_GRAY;
	static Color COLOR_HERITED = Color.BLUE;
	static Color COLOR_CALCULATED = Color.LIGHT_GRAY;

	protected OrderedMapOfDoublesObject omodo= null;
	int type= 0;

	private DBSTableEditor ed= null;
	protected DataBySlice dbs;
	protected AbstractBySlicePanel rbsp;
    
	/**
	 * 
	 * @param omodo the Object to Translate
	 * @param type if one of TYPE_XXXX
	 */
	protected DataBySliceCell(
	    AbstractBySlicePanel rbsp,
		OrderedMapOfDoublesObject omodo,
		int type) {
	    this.omodo = omodo;
	    this.rbsp = rbsp;
	    this.dbs = rbsp.getDbs();
	    this.type = type;
	}
	
	
	
	/** return true if this cell is editable **/
	public abstract boolean isEditable();
	
	
	/** donnot override unless u know what you are doing **/
	public final Component getEditor(DBSTableEditor ed) {
		this.ed= ed;
		return _getEditor();
	}
	/**
	 * return an editor for this data
	 */
	public abstract Component _getEditor();
	
	
	/**
	 * stop the edition of this cell
	 *
	 */
	public final void stopCellEditing() {
		_stopCellEditing();
		if (this.ed == null) return;
		DBSTableEditor med = ed;
		ed= null;
		med.stopCellEditing();

	}
	
	/**
	 * return the position of this Cell in the table
	 */
	public final Point getPosition() {
		return rbsp.getPositionOf(this);
	}
	
	
	/** get a MyTextField FOR TEXT EDITING **/
	protected final SNumField getMySNumFieldEditor(String s,boolean isInteger) {
	    final SNumField jtf= new SNumField(s, isInteger,false) {
			public void stopEditing() {
				stopCellEditing();
			}

			public void startEditing() {
				//NOTHING TO DO
			} 
		};
		jtf.setBorder(null);
		return jtf;
	}

	/** get a MyTextField FOR TEXT EDITING **/
	protected final SNumField getMySNumFieldEditor(String s) {
		return getMySNumFieldEditor(s,false);
	}
	
	protected final SNumField getMySNumFieldEditor() {
		return getMySNumFieldEditor("",false);
	}
	
	/**
	 * @return
	 */
	public final OrderedMapOfDoublesObject getOmodo() {
	    return omodo;
	}
	

	/** return the ForeGround Color , can be overidden**/
	public Color getForegroundColor() {
		if (! isEditable()) return COLOR_NOT_EDITABLE;
		return null;
	}
	

	/** return the type of this cell <BR>
	 * one of TYPE_*
	 * **/
	public final int getType() {
		return type;
	}
	
	/**
	 * stop the edition of this cell
	 *
	 */
	public abstract void _stopCellEditing();

	
}


