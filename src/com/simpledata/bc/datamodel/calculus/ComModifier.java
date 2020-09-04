/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ComModifier.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.calculus;

import java.util.Set;

import com.simpledata.bc.datamodel.money.Money;



/**
 * An Interface for all commission modifiers
 */
public interface ComModifier {
	
	/** 
	 * return the title to display for this commission modifier  <BR>
	 * Normally shoudl output Named.getTitle
	 **/
	public String getComTitle();
	
	/** 
	 * start a comission calculation.<BR>
	 * This method should advertise any dependencie of the change
	 * @param cc the ComCaluator the will handle this task
	 * @param value an initialized Money value, that should be modified to
	 * refelct the comission taken at this point
	 * @param depedantModifiers the list of ComModifiers that may be interested
	 * by a change in comission value at this point
	 **/
	public void startComCalc(ComCalculus cc,Money value, 
			Set/*<ComModifiers>*/ depedantModifiers);

	/** 
	 * return the reduction to apply on this ComCalc
	 **/
	public ReducOrFixed getReductionOrFixed();
}

/*
 * $Log: ComModifier.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:45  perki
 * First commit on sourceforge
 *
 * Revision 1.6  2004/11/17 09:09:49  perki
 * first step of discount extraction
 *
 * Revision 1.5  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.4  2004/09/02 15:51:46  perki
 * Lot of change in calculus method
 *
 * Revision 1.3  2004/05/05 08:26:54  perki
 * cleaning
 *
 * Revision 1.2  2004/04/12 17:34:52  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/04/12 12:33:09  perki
 * Calculus
 *
 */