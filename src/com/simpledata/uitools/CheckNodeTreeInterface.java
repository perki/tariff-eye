/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.uitools;

/**
* Any onject who uses a CheckNodeTree must implements the following methods:
*/
public interface CheckNodeTreeInterface {
	/** A node has been selected */
	public void checkNodeSelected (CheckNode cn);

	/** A directory has been created, return true if OK, false if not */
	public void checkNodeMenuEvent (String button, CheckNode cn);
	
	/** A Node has been moved, return true if OK, false if not */
	public boolean checkNodeMoved (CheckNode source, CheckNode destination);
}
