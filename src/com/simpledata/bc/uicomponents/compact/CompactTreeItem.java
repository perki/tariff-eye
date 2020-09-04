/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: CompactTreeItem.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
 
/// User interface components. 
package com.simpledata.bc.uicomponents.compact; 


/**
 * Tree element of the tree managed by CompactExplorer. 
 * This interface should contain all basic access functions
 * to the tree. 
 * XXX: Probably some other methods should be migrated into 
 * this interface. 
 */
public interface CompactTreeItem {
	
	/**
	 * Visitor entry point. 
	 */
	public void visit( CompactTreeVisitor v );
}