/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 4 oct. 2004
 */
package com.simpledata.bc.reports.common;

import java.util.LinkedList;

/**
 * This type represent a SectionNumber in the reports (e.g 2.3.2 or 1.3).
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SectionNumber.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public class SectionNumber {
	// #### CONSTANTS - Tunning ###############################################
	/** First section number */
	private static final Integer FIRST = new Integer(1);
	/** Separator section / subsection */
	private static final String SEPARATOR = ".";
	
	// #### FIELDS ############################################################
	
	private LinkedList m_number;
	
	// #### CONSTRUCTOR #######################################################
	
	/** Private since we provide other way to get the section numbers */
	private SectionNumber() {
		m_number = new LinkedList();
	}
		
	// #### METHODS ###########################################################
	
	/**
	 * @return The first SectionNumber. An anchor for the numbering.
	 */
	public static SectionNumber beggining() {
		SectionNumber result = new SectionNumber();
		result.m_number.addLast(FIRST);
		return result;
	}
	
	/**
	 * @return A new SectionNumber representing the section folowwing this one.
	 * (I.e. 27.10 -> 27.11)
	 */
	public SectionNumber nextSection() {
		SectionNumber result = (SectionNumber)this.clone();
		LinkedList numbers = result.m_number;
		int sect = ((Integer)numbers.removeLast()).intValue();
		sect++;
		numbers.addLast(new Integer(sect));
		return result;
	}
	
	/**
	 * @return A new SectionNumber representing the first subsection of the
	 * current one. (I.e. 6.11 -> 6.11.1)
	 */
	public SectionNumber enterSubsection() {
		SectionNumber result = (SectionNumber)this.clone();
		LinkedList number = result.m_number;
		number.addLast(FIRST);
		return result;
	}
	
	/**
	 * @return A new SectoinNumber representing the parent section of this
	 * subsection. (I.e. 3.5.4 -> 3.5)<BR>
	 * If the level is less than 2, this method yields to an exception.
	 */
	public SectionNumber exitSubsection() {
		assert this.level() > 1 : "Current section level is 1"; 
		
		SectionNumber result = (SectionNumber)this.clone();
		LinkedList number = result.m_number;
		number.removeLast();
		return result;
	}
	
	/**
	 * @return The level of this section. (I.e 2.3 -> 2 ; 5.4.3.2.1 -> 5)
	 */
	public int level() {
		return m_number.size();
	}
	
	// #### METHODS - Genreal Object utilities ################################
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		SectionNumber result = new SectionNumber();
		result.m_number = (LinkedList)this.m_number.clone();
		return result;
	}

	/**
	 * String representation. Use the separator defined in the constants.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i<m_number.size()-1; i++) {
			result.append(m_number.get(i));
			result.append(SEPARATOR);
		}
		result.append(m_number.get(m_number.size()-1));
		return result.toString();
	}
}
