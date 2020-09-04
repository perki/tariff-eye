/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactTarifManagerNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

import java.util.Set;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.calculus.ComCalc;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ComModifier;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.uitools.SNumField;

/**
 * Abstract class for all tarif containers<BR>
 * <B>This class takes in charge all the comission calulation overhead</B><BR>
 * Register event listeners / dispatch calculation 
 */
abstract class CompactTarifManagerNode extends CompactNode 
	implements ComModifier , ComCalc.ComissionListener {
	private static final Logger m_log = 
		Logger.getLogger( CompactTarifManagerNode.class ); 
	
	
	protected CompactTarifManagerNode(CompactNode parent, CNInterface expl) {
		super(parent, expl);
	}
	
	
	/** Return the value to be displayed in the table (for calulations) **/
	public  final Object displayTableValue(int columnIndex) {
		Money m = new Money(0d);
		if (columnIndex == 0) {
		    m = getTarification().comCalc().getCom(this);
		}
		if (columnIndex == 1) {
		    m.operation(
		            getTarification().comCalc().getComWithoutDiscount(this),1
		    );
		    if (explorer.discountOrUndisc()) {
		        
		        m.operation(getTarification().comCalc().getCom(this),-1);
		    } 
		}
		
		assert m != null : "Comission must be valid."; 
		
		return SNumField.formatNumber(m.getValueDefCurDouble(),2,true);
	}
	
	
	/** <B>Interface ComCalc.CommissionListener</B> gets registered events **/
	public void comissionChanged(ComCalculus cc,ComModifier cm) {
		cc.refresh(this);
	}

	
	
	//	------------ Comission calculations -----------//
	
	/**
	 * <B>Interface ComModifier</B> return the title to 
	 */
	public final String getComTitle() {
		return getClass()+":"+toString();
	}

	
	/** 
	 * <B>Interface ComModifier</B> 
	 * start a comission calculation.<BR>
	 * This method should advertise any dependencie of the change
	 * @param cc the ComCaluator the will handle this task
	 * @param value an initialized Money value, that should be modified to
	 * refelct the comission taken at this point
	 * @param depedantModifiers the list of ComModifiers that may be interested
	 * by a change in comission value at this point
	 **/
	public void startComCalc(ComCalculus cc,Money value, 
			Set/*<ComModifiers>*/ depedantModifiers) {
		
		_startComCalc(cc,value);
		// tell my parent I changed
		
		if (getParent() instanceof ComModifier) {
			depedantModifiers.add(getParent());
		}
	}
	
	/** 
	 * <B>Interface ComModifier</B> get the reduction<BR>
	 * Always return null;
	 **/
	public ReducOrFixed getReductionOrFixed() {
        return null;
     }
	
	/**
	 * forwarding calculus after overhead treatment made by 
	 * CompactTarifManagerNode.startComCalc(ComCalculus cc)
	 */
	protected abstract void _startComCalc(ComCalculus cc,Money value);
}

/*
 * $Log: CompactTarifManagerNode.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.10  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.9  2004/11/17 10:52:03  perki
 * Discount display preview step1
 *
 * Revision 1.8  2004/11/17 09:09:49  perki
 * first step of discount extraction
 *
 * Revision 1.7  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.6  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.5  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.4  2004/09/03 11:47:53  kaspar
 * ! Log.out -> log4j first half
 *
 * Revision 1.3  2004/09/02 16:18:54  perki
 * Lot of change in calculus method
 *
 * Revision 1.2  2004/08/02 15:45:48  perki
 * Repartition viewer on simulator
 *
 * Revision 1.1  2004/07/30 05:58:15  perki
 * Slpitted CompactNode.java in sevral files
 *
 */