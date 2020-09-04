/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: CompactRoot.java,v 1.2 2007/04/02 17:04:25 perki Exp $ 
 * @author Simpledata SARL, 2004, all rights reserved. 
 */

package com.simpledata.bc.uicomponents.compact; 

import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.tools.Lang;

/** 
 * A dummy node that is used as Root node, but it is never displayed.
 **/
public class CompactRoot extends CompactBCNodeSingle {

	protected CompactRoot(BCNode root, CNInterface expl) {
		super(null, root, expl);
	}
	public boolean contentsHasValue(Object o) {
	    return false;
	}
	public Object[] contentsGet() {
		return new Object[0];
	}
	public String displayTreeString() {
		return "["+Lang.translate("Portfolio")+"]";
	}

	// Visitor
	
	/**
	 * Visit a node of type CompactRoot.
	 * @param v Visitor instance to call back into. 
	 */
	public void visit( CompactTreeVisitor v ) {
		v.caseCompactRoot( this ); 
	}
}