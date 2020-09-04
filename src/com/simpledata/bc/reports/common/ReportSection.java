/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 13 oct. 2004
 */
package com.simpledata.bc.reports.common;

import com.simpledata.bc.uicomponents.compact.CompactNode;

/**
 * This class reporesent a section of a report. It contains its SectionNumber,
 * its title, the node it describes and a flag which indicates if the node is
 * a tariff of not.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: ReportSection.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public class ReportSection {
	// #### FIELDS ############################################################
	
	/** SectionNumber of the section */
	public SectionNumber number;
	/** Title of the sectin */
	public String        title;
	/** The node of the compact tree explorer coresponding to this report section */
	public final CompactNode   node;
	/** Contains this node a tariff to report */
	public boolean		 isTariff; // is only title otherwise 
	
	// #### CONSTRUCTOR #######################################################
	
	/**
	 * Construct a new instance for the given node.
	 * @param node the given node.
	 */
	ReportSection(CompactNode node) {
		this.number = null;
		title       = "";
		this.node	= node;
		isTariff    = false;
	}
	
	/**
	 * String representation.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String stNumber = number == null ? "null" : number.toString();
		return stNumber+": "+title+" [isTariff ? "+isTariff+"]";
	}
}
