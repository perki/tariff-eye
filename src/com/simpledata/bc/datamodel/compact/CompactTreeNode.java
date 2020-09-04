/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactTreeNode.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.compact;

import java.util.*;

import com.simpledata.bc.datamodel.*;

public class CompactTreeNode {
	/**the node I am mapping to*/
	private BCNode myNode; 
	/**the tarifs i do contain*/
	private ArrayList/*<CompactTreeTarifRef>*/ tarifRefs;
	/**my childrens*/
	private ArrayList/*<CompactTreeNode>*/ childrens;
	/**my parent (null if root node)*/
	private CompactTreeNode myParent; 
	

	/** 
	 * Construct a CompactTree node
	 * 
	 * @param bcNode the node it contains
	 * @param parent its parent, null if this is a root node
	 */
	private CompactTreeNode(CompactTreeNode parent,BCNode bcNode) {
		myNode = bcNode;
		myParent = parent;
		tarifRefs = new ArrayList/*<CompactTreeTarifRef>*/();
		childrens = new ArrayList/*<CompactTreeNode>*/();
	}
	
	/** @return a CompactTreeNode representing my parent, null if root **/
	public CompactTreeNode getParent() {
		return myParent;
	}
	
	/** return a ArrayList of CompactTreeNode representing my childrens **/
	public ArrayList/*<CompactTreeNode>*/ getChildren() {
		return childrens;
	}
	
	/** return a ArrayList of CompactTreeTarifRef this node is Mapping **/
	public ArrayList/*<CompactTreeTarifRef>*/ getTarifsRefs() {
		return tarifRefs;
	}
	
	/** return true if this CompactTreeNode contains this node **/
	public boolean isMappingNode(BCNode n) {
		return myNode == n;
	}
	
	/** return true if this CompactTreeNode contains this tarif **/
	public boolean isMappingTarif(Tarif t) {
		Iterator/*<CompactTreeTarifRef>*/ i = tarifRefs.iterator();
		while (i.hasNext()) {
			if (((CompactTreeTarifRef) i.next()).getTarif() == t) {
				return true;
			}
		}
		return false;
	}
	
	/** return the BCnode mapped **/
	public BCNode getBCNode() {
		return myNode;
	}
	
	//------------------- CONSTRUCTION ----------------------//
	
	/**
	 * Create the tree corresponding to this tarifs and those trees
	 */
	public static CompactTreeNode 
	getTreeForTarifs(ArrayList/*<Tarif>*/ tarifs, BCTree[] trees) {
		//		 if no trees then return a dummy node
		if (trees.length == 0) {
			return new CompactTreeNode(null,null);
		}
		CompactTreeNode root = new CompactTreeNode(null,trees[0].getRoot());
		
		ArrayList/*<CompactTreeNode>*/ leafs= new ArrayList/*<CompactTreeNode>*/();
		ArrayList/*<BCNode>*/ nodes= null; 
		ArrayList/*<CompactTreeNode>*/ futureLeafs
			= new ArrayList/*<CompactTreeNode>*/(); 
		ArrayList/*<CompactTreeNode>*/ ancestorsOfNode= null;

		// for each Tarif
		Tarif t= null;
		Iterator/*<Tarif>*/ e= tarifs.iterator();
		while (e.hasNext()) {
			t= (Tarif) e.next();
			leafs.clear();
			leafs.add(root);

			// for each tree
			for (int i= 0; i < trees.length; i++) {
				futureLeafs.clear();
				nodes= t.getMyMapping(trees[i]);

				//	for each node mapping
				Iterator/*<Tarif>*/ e1= nodes.iterator();
				while (e1.hasNext()) {
					ancestorsOfNode=
						trees[i].getAncestorsOf((BCNode) e1.next());
					
					//	for each leaf
					Iterator/*<CompactTreeNode>*/ e2= leafs.iterator();
					while (e2.hasNext()) {
						futureLeafs.add(
							((CompactTreeNode) e2.next()).addChildrens(
								ancestorsOfNode));
					} // end for each leaf
				} // end for each node mapping


				if (futureLeafs.size() > 0)
					leafs= (ArrayList) futureLeafs.clone();
			}

			// finaly add tarifs
			//CompactTreeTarifRef cttr = new CompactTreeTarifRef(t,leafs.size());
			CompactTreeTarifRef cttr = 
				new CompactTreeTarifRef(t,leafs.size());
			Iterator/*<CompactTreeNode>*/ e3 = leafs.iterator();
			while (e3.hasNext())
				((CompactTreeNode) e3.next()).addTarif(cttr);
		}
		return root;
	}
	
	
	/** 
	 * add a vector of BCNode 
	 * represented as a tree path
	 * @return the CNode created as leaf
	 **/
	private CompactTreeNode addChildrens(ArrayList/*<BCNode>*/ v) {
		CompactTreeNode last= this;
		for (int i= 0; i < v.size(); i++)
			last= last.addChildren((BCNode) v.get(i));
		return last;
	}
	
	private CompactTreeNode addChildren(BCNode n) {
		if (myNode == n) return this;
		CompactTreeNode child= getChildrenWithValue(n);
		if (child == null) {
			child = new CompactTreeNode(this, n);
			_addChildren(child);
		}
		// should return (child .. but if Virtual Node different comportement)
		return child.addChildren(n);
	}
	
	
	
	/**
	 * Do not use directly
	 * Will take care of positioning
	 */
	private void _addChildren(CompactTreeNode cnode) {
		for (int i= 0; i < childrens.size(); i++) {
			if (((CompactTreeNode) childrens.get(i)).getBCNode().getPosition()
					> cnode.getBCNode().getPosition()) {
				childrens.add(i, cnode);
				return;
			}
		}
		childrens.add(cnode);
	}
	
	
	/** return the children that has this value **/
	private CompactTreeNode getChildrenWithValue(BCNode n) {
		Iterator/*<CompactTreeNode>*/ e = getChildren().iterator();
		CompactTreeNode temp;
		while (e.hasNext()) {
			temp = (CompactTreeNode) e.next();
			if (temp.isMappingNode(n)) return temp;
		}
		return null;
	}
	
	/** add a Tarif to this node **/
	private void addTarif(CompactTreeTarifRef t) {
		if (! tarifRefs.contains(t)) tarifRefs.add(t);
	}
	
	
	//------------------- to String ----------------------//
	
	/** output this node and it's childrens as a Tree **/
	public String toString() {
		StringBuffer result = new StringBuffer("Started\n");
		toString(0,result);
		return result.toString();
	}
	
	private static String tabs 
	= new String("                                      ");
	
	/** tab **/
	private void addTab(int i,StringBuffer sb) {
		sb.append(tabs.substring(0,i));
	}
	
	/** helper for toString **/
	private void toString(int i,StringBuffer sb) {
		addTab(i,sb);
		sb.append("+").append(myNode.getTitle()).append("\n");
		i++;
		
		// tarifs
		Iterator/*<CompactTreeTarifRef>*/ e0 = getTarifsRefs().iterator();
		while (e0.hasNext()) {
			addTab(i,sb);
			sb.append("-").append(e0.next()).append("\n");
		}
		
		// nodes
		Iterator/*<CompactTarifNodes>*/ e1 = getChildren().iterator();
		while (e1.hasNext()) {
			((CompactTreeNode) e1.next()).toString(i,sb);
		}
		
	}
}