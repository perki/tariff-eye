/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: CompactBCNodeSingle.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */

package com.simpledata.bc.uicomponents.compact; 

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;

/** 
 * Node that contains a BCNode
 * Single BCNode container
 */
public class CompactBCNodeSingle extends CompactBCNode {
	private static final Logger m_log = Logger.getLogger( CompactBCNodeSingle.class ); 
	
	/** never use it! use: hasValue() et getValue(); getBCnode() **/
	private BCNode zValue;

	public CompactBCNodeSingle(CompactBCNode p, BCNode v, CNInterface expl) {
		super(p, expl);
		zValue= v;
	}

	/** 
	 * return the value contained in this node <BR>
	 * If explorer.showTarifs() && uiComponentsCreated : return false;
	 * **/
	public boolean contentsHasValue(Object o) {
		if (zValue == o) return true;
		
		if (explorer.showTarifs() && uiComponentsCreated) return false;
		
		return contentsHasTarif(o);
	}

	/** return the bcnode contained in this node */
	public Object[] contentsGet() {
		ArrayList result = new ArrayList();
		result.add(zValue);
		
		// add tarifs if needed
		if (explorer.showTarifs() && uiComponentsCreated) {
			
		} else {
			
			if (tarifsRefs != null) {
				Iterator/*<CompactTreeTarifRef>*/ e = tarifsRefs.iterator();
				while (e.hasNext()) {
					result.add(((CompactTreeTarifRef) e.next()).getTarif());
				}
			}
		}
		return result.toArray();
	}
	
	/**
	 * @return the List of BCnode contained
	 */
	public ArrayList/*<BCNode>*/ contentsGetBCnodes() {
		ArrayList/*<BCNode>*/ temp = new ArrayList/*<BCNode>*/();
		temp.add(zValue);
		return temp;
	}
	
	/** return the bcnode contained in this node */
	public BCNode contentsGetBCnode() {
		return zValue;
	}

	/*	*******************************************************
		------------- STREENODE IMPLEMENTATION ----------------
		*******************************************************/
	

	//	----------- Default -----------------------//

	public String displayTreeString() {
		return contentsGetBCnode().toString();
	}

	public final CompactBCNode addChildren(BCNode n) {
		if (contentsHasValue(n))
			return this;
		CompactBCNode child= getChildrenALWithValue(n);
		if (child == null) {
			child= new CompactBCNodeSingle(this, n, explorer);
			_addChildren(child);
		}
		// should return (child .. but if Virtual Node different comportement)
		return child.addChildren(n);
	}

	/**
	 * Add this node as a children
	 */
	protected final void addChildren(CompactBCNode cnode) {

		CompactBCNode child= null;

		//	Check that there is just one solution
		Object contents[]= cnode.contentsGet();

		for (int i= 0; i < contents.length; i++) {
			CompactBCNode temp= getChildrenALWithValue(contents[i]);
			if (temp != null) {
				if (child == null) {
					child= temp;
				} else {
					m_log.error( "got several solutions" );
				}
			}
		}

		if (child == null) {
			_addChildren(cnode);
			return;
		}
		// already known value
		child.mergeChildrensWith(cnode);
	}
	
	// Visitor
	
	/**
	 * Visit a node of type CompactBCNode.
	 * @param v Visitor instance to call back into. 
	 */
	public void visit( CompactTreeVisitor v ) {
		v.caseCompactBCNodeSingle( this ); 
	}
}



