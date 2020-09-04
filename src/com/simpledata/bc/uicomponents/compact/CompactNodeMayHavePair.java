/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactNodeMayHavePair.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;


/** interface for node that May have a pair node in the Compact Tree **/
public interface CompactNodeMayHavePair {
	/*
	 * get the paired node of this one<BR>
	 */
	public CompactTreeItem contextGetpair();
}


/*
 * $Log: CompactNodeMayHavePair.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.2  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.1  2004/08/05 11:44:11  perki
 * Paired compact Tree
 *
 */