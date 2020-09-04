/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: PairManager.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.datamodel.pair;

import java.util.ArrayList;

/**
 * An object who knows how to handle pairing
 */
public interface PairManager {
	
	/** 
	 * get the pair position for this List of BCnode
	 * return null if none
	 */
	public ArrayList/*<BCNode>*/ getPairPosition(ArrayList/*<BCnode>*/ mapping);
	
}

/*
 * $Log: PairManager.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.2  2004/08/04 16:40:08  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 */