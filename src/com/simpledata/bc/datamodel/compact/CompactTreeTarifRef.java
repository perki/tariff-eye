/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactTreeTarifRef.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.compact;

import com.simpledata.bc.datamodel.Tarif;

/**
 * This class is a container for a tarif and it's occurences in a tree
 */
public class CompactTreeTarifRef {
	private int occ;
	
	private Tarif warpped;
	
	

	/** 
	 * create a CompactTreeTarifRef containing tarifs and its containers
	 */
	public CompactTreeTarifRef(Tarif t,int occurences) {
		warpped = t;
		occ = occurences;
	}
	
	/**
	 * @return Returns the occurences of a tarif.
	 */
	public int getOccurences() {
		return occ;
	}
	
	/**
	 * @return Returns the tarif.
	 */
	public Tarif getTarif() {
		return warpped;
	}
	
	/** toString() facility **/
	public String toString() {
		return "["+occ+"] "+warpped.toString();
	}

}

/*
 * $Log: CompactTreeTarifRef.java,v $
 * Revision 1.2  2007/04/02 17:04:30  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:45  perki
 * First commit on sourceforge
 *
 * Revision 1.7  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.6  2004/07/06 11:05:53  perki
 * calculus is now synchronized for forward options
 *
 * Revision 1.5  2004/06/28 10:38:47  perki
 * Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 * Revision 1.4  2004/06/23 18:38:04  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.2  2004/06/23 12:06:41  perki
 * Cleaned CompactNode
 *
 * Revision 1.1  2004/06/22 17:13:04  perki
 * CompactNode now build from datamodel and added a notice interface to WorkSheet
 *
 */