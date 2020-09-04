/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Proposition.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;

import com.simpledata.bc.datamodel.Tarif;

/**
 * A handler for tarif matching proposition.. contains
 * The tarif Source and a List of TarifMatch Possible
 */
public class Proposition {
	public Tarif tarifSource;
	public TarifMatch[] matches;
	
	protected Proposition(Tarif src,TarifMatch[] tMatches) {
		tarifSource = src;
		matches = tMatches;
	}
}

/*
 * $Log: Proposition.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.1  2004/07/09 20:25:02  perki
 * Merging UI step 1
 *
 */