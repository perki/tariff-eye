/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 7 sept. 2004
 */
package com.simpledata.bc.components.worksheet.dispatcher;

import java.util.Set;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;

/**
 * The dispatcher bounds provide a way to assign upper and lower bound
 * to a tarif sheet.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: DispatcherBounds.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class DispatcherBounds 
	extends DispatcherSimple implements Dispatcher.SupportInsertion {
	// FIELDS
	/** Logger */
	private final static Logger m_log = Logger.getLogger(DispatcherBounds.class);
	
	/** Title of the WorkSheet. It should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "DispatcherBounds";

	/** The lower bound money object */
	private Money m_lowerBound;
	/** The upper bound money object */
	private Money m_upperBound;
	/** Is up bounded ? */
	private boolean m_isUpperBounded;
	
	// CONSTRUCTOR
	/**
	 * Main constructor. We avoid to initialize objects in it because of the
	 * super construcors which call geters defined in the abstract superclasses.
	 * 
	 * @param parent  Parent worksheet of the dispatcher
	 * @param title   Title of the dispatcher
	 * @param id      ID number
	 * @param key     Key
	 */
	public DispatcherBounds(
			WorkSheetContainer parent,
			String title,
			String id,
			String key) {
		super(parent, title, id, key);
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
	 */
	public void initializeData() { 
	   m_lowerBound = new Money(0.0);
	   m_upperBound = new Money(0.0);
	   m_isUpperBounded = true;
	}
	
	/** 
	 * return the type of discount the dispatcher accept. I.e. all
	 * kind of reduction. <BR>
	 * @return ReducOrFixed.ACCEPT_REDUC_FULL 
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_FULL;
	}
	
	/**
	 * Calculate the commission taken at this point
	 */
	public final void privateComCalc(ComCalculus cc,Money value) {
		
		if (myWorkSheet != null) {
			value.operation(cc.getCom(myWorkSheet),1);
		}
		if (value.compareTo(m_upperBound) > 0 && m_isUpperBounded) 
			value.setValue(m_upperBound);
		if (value.compareTo(m_lowerBound) < 0) 
			value.setValue(m_lowerBound);
	}
	
	/**
	 * Returns the money object representing the lower bound of the
	 * tarif.
	 * @return Lower bound money object.
	 */
	public Money getLowerBound() {
		
		return m_lowerBound;
	}
	
	/**
	 * Returns the money object representing the upper bound of the
	 * tarif.
	 * @return Upper bound money object.
	 */
	public Money getUpperBound() {
	
		return m_upperBound;
	}
	
	/**
	 * @return true if the upper bound is activated. Return false otherwise.
	 */
	public boolean getIsUpperBounded() {
		return m_isUpperBounded;
	}

	/**
	 * Set a flag which indicates that the upper bound is activated.
	 * @param value true if the upper bound is activated (default)
	 */
	public void setIsUpperBounded(boolean value) {
		m_isUpperBounded = value;
	}
	
	// Visitor implementation
	
	/**
	 * call the right method in the given visitor.
	 * @param v visitor to apply. 
	 */
	public void visit(TarifTreeVisitor v) {
		v.caseDispatcherBounds( this ); 
	}
	
	 /**
     * @see Dispatcher.SupportInsertion#getInsertionKey()
     */
    public String getInsertionKey() {
        return "";
    }

	
	// XML STUFF - Don't use 
	
	/**
	 * XML
	 */
	public DispatcherBounds() {
	     
	}
	
	/**
	 * XML
	 * @param value
	 */
	public void setLowerBound(Money value) {
		m_lowerBound = value;
	}
	
	/**
	 * XML
	 * @param value
	 */
	public void setUpperBound(Money value) {
		m_upperBound = value;
	}

   
   
}
