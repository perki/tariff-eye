/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.uicomponents.worksheet.workplace.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.workplace.tools.RateBySlice;
import com.simpledata.bc.components.worksheet.workplace.tools.RateBySliceValue;
import com.simpledata.bc.datamodel.money.BCNumber;
import com.simpledata.bc.tools.OrderedMapOfDoublesObject;
import com.simpledata.bc.uitools.SNumField;

/**
 * Cells for RateBySlicePanel
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: RateBySliceCell.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public abstract class RateBySliceCell extends DataBySliceCell {
    
    static final int TYPE_KEY= 0;
	static final int TYPE_MARGINAL_RATE= 1;
	static final int TYPE_EFFECTIVE_RATE= 2;
	static final int TYPE_FIXED= 3;
    
    
    protected RateBySlice rbs;

	/**
	 * 
	 * @param omodo the Object to Translate
	 * @param type if one of TYPE_XXXX
	 */
	protected RateBySliceCell(
	    RateBySlicePanel rbsp,
		OrderedMapOfDoublesObject omodo,
		int type) {
	    super(rbsp,omodo,type);
		this.rbs= rbsp.getMyRbs();
	}

	public static RateBySliceCell create(
		RateBySlicePanel rbsp,
		OrderedMapOfDoublesObject omodo,
		int type) {
		switch (type) {
			case TYPE_KEY :
				return new KeyCell(rbsp, omodo);
			case TYPE_FIXED :
				return new FixedCell(rbsp, omodo);
			case TYPE_MARGINAL_RATE :
				return new MarginalRateCell(rbsp, omodo);
			case TYPE_EFFECTIVE_RATE :
				return new EffectiveRateCell(rbsp, omodo);
		}
		return null;
	}

	/** get the RateBySliceValue in this omod **/
	protected RateBySliceValue getRBSV() {
		return getRBSV(omodo);
	}

	/** get the RateBySliceValue in this omod **/
	protected RateBySliceValue getRBSV(OrderedMapOfDoublesObject o) {
		return (RateBySliceValue) o.getValue();
	}
	

	/**
	 * return the result as a String
	 */
	public abstract String toString();
	
	
	/** money format String **/
	protected String moneyFormat(double d) {
		return SNumField.formatNumber(d,2,true);
	}
	
}

class FixedCell extends RateBySliceCell {
	private SNumField jtf;
	
	 private static final Logger m_log 
		= Logger.getLogger( FixedCell.class );
	
	FixedCell(RateBySlicePanel rbsp, OrderedMapOfDoublesObject omodo) {
		super(rbsp, omodo, TYPE_FIXED);
	}

	/** get the Marginal Rate that applies here **/
	private Double getApplicableMinimum() {
		if (getRBSV() == null) {
			m_log.error("getRBSV cannot be null omod="+omodo);
			return null;
		} 
		if (getRBSV().isFixed()) {
			return new Double(getRBSV().xFixedMin.get());
		}
		for (OrderedMapOfDoublesObject o= omodo.getPrevious();
			o != null;
			o= o.getPrevious()) {
			if (getRBSV(o).isFixed())
				return new Double(getRBSV(o).xFixedMin.get());
		}
		return null;
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
		if (getRBSV().xFixedMin != null) {
			jtf = getMySNumFieldEditor(moneyFormat(getRBSV().xFixedMin.get()));
		} else {
			jtf = getMySNumFieldEditor();
		}
		return jtf;
	}

	/** stop cell editing **/
	public void _stopCellEditing() {
		if (jtf == null)
			return;
		Double d= jtf.getDouble();
		if (d != null) {
			getRBSV().xFixedMin = new BCNumber(d.doubleValue());
		} else {
			getRBSV().xFixedMin = null;
		}
		jtf= null;
	}
	
	/** return the ForeGround Color **/
	public Color getForegroundColor() {
		if (getRBSV() == null) {
		    m_log.error("getRBSV cannot be null omod="+omodo);
			return super.getForegroundColor();
		} 
		if (! getRBSV().isFixed()) return  COLOR_HERITED;
		return super.getForegroundColor();
	}

	/**
	 * return the result as a String
	 */
	public String toString() {
		Double m= getApplicableMinimum();
		if (m == null) return ".....";
		return moneyFormat(m.doubleValue());
	}
}

class EffectiveRateCell extends RateCell {

    private static final Logger m_log 
	= Logger.getLogger( EffectiveRateCell.class );
    
	EffectiveRateCell(RateBySlicePanel rbsp, OrderedMapOfDoublesObject omodo) {
		super(rbsp, omodo,TYPE_EFFECTIVE_RATE);
		
	}
	
	/** return one of TYPE_*_RATE **/
	protected int getRateType() {
		return TYPE_EFFECTIVE_RATE;
	}
	
	/** return true if this cell is editable **/
	public boolean isEditable() {
		return ! rbs.getIsMarginalRate();
	}

	/** get the previous non null Rate **/
	protected final Double getPreviousRate() {
		Point p = getPosition();
		if (p.y < 1)
			return null;
		Object o = rbsp.getValueAtX(p.y -1,p.x);
		return ((RateCell) o).getApplicableRate();
	}

	/** get the Marginal Rate that applies here **/
	protected Double getApplicableRate() {
		if (getRBSV() == null) {
			m_log.error("getRBSV cannot be null omod="+omodo);
			return null;
		} 
		
		Double prevRate = getPreviousEffectiveRate();
		if (prevRate == null) prevRate = new Double(0d);
		Double rate = getRBSV().isRate() ? getRBSV().xRate : prevRate;
		if (rbs.getIsMarginalRate()) {
			double kp = getNextKey();
			
			if (kp <= 0) return null;
			
			double k =  omodo.getKey();
			double m = rate.doubleValue();
			double um = prevRate.doubleValue();
			
			rate = new Double(m + k * ( um - m) / kp);
		}
		return rate;
	}
}

class MarginalRateCell extends RateCell {
	
    private static final Logger m_log 
	= Logger.getLogger( MarginalRateCell.class );
    
	MarginalRateCell(RateBySlicePanel rbsp, OrderedMapOfDoublesObject omodo) {
		super(rbsp, omodo,TYPE_MARGINAL_RATE);
	}

	/** return one of TYPE_*_RATE **/
	protected int getRateType() {
		return TYPE_MARGINAL_RATE;
	}
	
	
	/** get the Marginal Rate that applies here **/
	protected Double getApplicableRate() {
		if (getRBSV() == null) {
		    m_log.error("getRBSV cannot be null omod="+omodo);
			return null;
		} 
		Double pm = getPreviousEffectiveRate();
		
		Double rate = getRBSV().isRate() ? getRBSV().xRate : pm;
		if (! rbs.getIsMarginalRate()) {
			
			if (pm != null) {
				double kp = getNextKey();
				double k =  omodo.getKey();
				if ((kp - k) <= 0) return null;
				
				double u = rate.doubleValue();
				
				double um = pm.doubleValue();
				
				rate = new Double((kp * u - k * um) /( kp - k ));
			}
	
		}
		return rate ;
	}
	
	
	
	/** return true if this cell is editable **/
	public boolean isEditable() {
		return rbs.getIsMarginalRate();
	}
}

/** a class for all RateCells **/
abstract class RateCell extends RateBySliceCell {
    private static final Logger m_log 
	= Logger.getLogger( RateCell.class );
    
	private SNumField jtf;

	protected RateCell(RateBySlicePanel rbsp, 
			OrderedMapOfDoublesObject omodo,int type) {
		super(rbsp, omodo, type);
	}
	
	/** return the rate applicable to this row **/
	protected abstract Double getApplicableRate();
	
	/** return one of TYPE_*_RATE **/
	protected abstract int getRateType();
	
	/** get the previous non null Rate **/
	protected final Double getPreviousEffectiveRate() {
		Point p = getPosition();
		int x = rbsp.getColumnIndex(getRateType());
		if (p.y < 1)
			return null;
		Object o = rbsp.getValueAtX(p.y -1,x);
		return ((RateCell) o).getApplicableRate();
	}
	
	
	/** 
	 * get the previous non null Rate 
	  * @return -1 if null
	 * **/
	protected final double getNextKey() {
		OrderedMapOfDoublesObject o = omodo.getNext();
		if (o == null) return -1;
		return o.getKey();
	}
	

	
	
	/**
	 * return an editor for this data
	 *
	 */
	public final Component _getEditor() {
		if (getRBSV().xRate != null) {
			jtf = getMySNumFieldEditor(formatMe(getRBSV().xRate.doubleValue()));
		} else {
			jtf = getMySNumFieldEditor();
		}
		return jtf;
	}

	/**
	 * stop the edition of this cell
	 *
	 */
	public final void _stopCellEditing() {
		if (jtf == null)
			return;
		Double d= jtf.getDouble();
		getRBSV().xRate = d;
		jtf= null;
	}
	
	/**
	 * return the result as a String
	 */
	public final String toString() {
		Double d= getApplicableRate();
		if (d == null)
			return "....";
		return formatMe(d.doubleValue())+ " %";
	}
	
	/**
	 * format a double to what i want
	 */
	private final String formatMe(double d) {
		return SNumField.formatNumber(d+"",3,true) ;
	}
	
	/** return the ForeGround Color **/
	public final Color getForegroundColor() {
		if (getRBSV() == null) {
		    m_log.error("getRBSV cannot be null omod="+omodo);
			return super.getForegroundColor();
		} 
		if (! isEditable()) return COLOR_CALCULATED;
		if (! getRBSV().isRate()) return COLOR_HERITED;
		return super.getForegroundColor();
	}

}

class KeyCell extends RateBySliceCell {
	private SNumField jtf;

	KeyCell(RateBySlicePanel rbsp, OrderedMapOfDoublesObject omodo) {
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
		jtf =  getMySNumFieldEditor(toString());
		return jtf;
	}

	/**
	 * stop the edition of this cell
	 *
	 */
	public void _stopCellEditing() {
		if (jtf == null)
			return;
		Double d= jtf.getDouble();
		if (d != null)
			omodo.setMyKey(d.doubleValue());
		jtf= null;
	}

	/**
	 * return the result as a String
	 */
	public String toString() {
		return moneyFormat(omodo.getKey());
	}

}