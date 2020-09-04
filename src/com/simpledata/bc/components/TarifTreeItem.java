/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Tarif tree interface. 
 * @version $Id: TarifTreeItem.java,v 1.2 2007/04/02 17:04:31 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
 
/// Package for datamodel base interfaces and classes. 
package com.simpledata.bc.components;  

/**
 * Interface implemented by each data item in a 
 * tree. This interface provides basic access to the 
 * tree under iteration.  
 * 
 * Access is provided using the visitor pattern. 
 */
public interface TarifTreeItem {
	/**
	 * Passed the visitor, will visit the node with the right basic
	 * case method. Any given node Foo in the tree must call back 
	 * the visitor using the method caseFoo. 
	 */
	public void visit(TarifTreeVisitor v);
}

