/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactBCGroupNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;

import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;
import com.simpledata.util.CollectionsToolKit;

/**
 * Node that group several BCNodes into one to create with one tarif at end<BR>
 * It's used when a tarif has sevral representation in the tree
 */
class CompactBCGroupNode 
	extends CompactBCNode {
	
	/** the bcnodes I compress **/
	private ArrayList/*<BCNode>*/ bcnodes; 
	
	/** a small tree corresponding to what I compress **/
	private SmallNode smallRootNode;
	
	public CompactBCGroupNode
		(CompactBCNode p, CNInterface expl,
				ArrayList/*<BCNode>*/ bcnodes,
				CompactTreeTarifRef t,
				SmallNode context) {
		super(p,expl);
		tarifsRefs.add(t); // add the tarif reference to my daddy
		this.bcnodes = bcnodes;
		smallRootNode = context;
	}


	/**
	 * <B>return only the first BCNode</B>
	 * @see com.simpledata.bc.uicomponents.compact.
	 * CompactBCNode#contentsGetBCnode()
	 */
	protected BCNode contentsGetBCnode() {
		if (bcnodes.size() > 0) return (BCNode) bcnodes.get(0);
		return null;
	}
	
	/**
	 * @return the List of BCnode contained
	 */
	public ArrayList/*<BCNode>*/ contentsGetBCnodes() {
		return bcnodes;
	}


	/**
	 * @see com.simpledata.bc.uicomponents.compact.
	 * CompactBCNode#addChildren(com.simpledata.bc.datamodel.BCNode)
	 */
	public CompactBCNode addChildren(BCNode n) {
		if (! bcnodes.contains(n)) bcnodes.add(n);
		return null;
	}


	/**
	 * <B>DO NOT USE!!!</B> Those nodes does not accept childrens
	 * @see com.simpledata.bc.uicomponents.compact.
	 * CompactBCNode#addChildren(CompactBCNode)
	 */
	protected void addChildren(CompactBCNode cnode) {
		assert false; 
	}


	/**
	 * @see CompactNode#displayTreeString()
	 */
	public String displayTreeString() {
		String result = new String();
		for (Iterator/*<BCNodes>*/ i = bcnodes.iterator(); i.hasNext();) {
			result += i.next().toString()+"/";
		}
		
		return result;
	}

	/**
	 * return an HTML formated Text wich expose the tree structure<BR>
	 * Tags "&lt;HTML>&lt;/HTML> need to bee added to use this result.
	 */
	public String displayTreeStringHTML() {
		final String blank = "                                   ";
		final StringBuffer result = new StringBuffer("<PRE>");
		smallRootNode.visitMeAndAllChildrens(new SmallNode.SmallVisitor() {
			public void visit(SmallNode sn,int level) {
				if (sn.node != null)
					result.append(blank.substring(0,level*3));
					result.append(sn.node.getTitle()).append("\n");
			}},0);
		
		result.append("</PRE>");
		return result.toString();
	}
	

	/**
	 * @see CompactNode#contentsHasValue(java.lang.Object)
	 */
	public boolean contentsHasValue(Object o) {
		if (bcnodes.contains(o)) return true;
		if (explorer.showTarifs() && uiComponentsCreated) return false;
		return contentsHasTarif(o);
	}


	/**
	 * @see CompactNode#contentsGet()
	 * @return Tarif and BCNode
	 */
	public Object[] contentsGet() {
		ArrayList result = ((ArrayList) bcnodes.clone());
		
		if (tarifsRefs != null) {
		    result.addAll(tarifsRefs);
			Iterator/*<CompactTreeTarifRef>*/ e = tarifsRefs.iterator();
			while (e.hasNext()) {
				result.add(((CompactTreeTarifRef) e.next()).getTarif());
			}
		}
		
		return result.toArray();
	}


	/**
	 * @see com.simpledata.bc.uicomponents.compact.
	 * CompactNode#visit(CompactTreeVisitor)
	 */
	public void visit(CompactTreeVisitor v) {
		v.caseCompactBCGroupNode( this ); 
	}
	
	
	//--------------- production --------------------//
	/**
	 * create the groups when tarifs are in multiple positions
	 */
	public static void prodCreateCompactBCGroupNodes(CompactNode rootNode) {
		if (rootNode == null) return;
		// for each Tarif
		Iterator/*<Tarif>*/ i=
			rootNode.getTarification().getAllTarifs().iterator();
		
		Tarif t;
		while (i.hasNext()) {
			t = (Tarif) i.next();
			// get all nodes that contains this Tarif
			ArrayList/*<CompactNode>*/ ls=rootNode.contentsGetNodesWithValue(t);
			
			if (ls.size() < 2) {
				continue;
			}
			
			/** will keep a small tree structure with all the node I was on **/
			SmallNode smallRootNode = new SmallNode(null,null);
			
			/** will contain the parent path of the future node **/
			ArrayList/*<CompactNode>*/ parents = null;
			/** will contain the sum of all the paths this node is replacing **/
			ArrayList/*<CompactNode>*/ allNodesInPath =
				new ArrayList/*<CompactNode>*/();
			
			
			CompactNode tempCN;
			CompactTarifsContainerNode tempCTCN;
			ArrayList/*<CompactNode>*/ tempTree;
			
			for (Iterator/*<CompactNode>*/ i2= ls.iterator(); i2.hasNext();) {
				tempCN = (CompactNode) i2.next();
				assert (tempCN instanceof CompactTarifsContainerNode):
					"How comes ["+tempCN+"] contains a Tarif!!";
				
				tempTree = tempCN.treeGetPath();
				
				// construct smallNodes
				smallRootNode.add(tempTree);
				
				CollectionsToolKit.addToCollection(allNodesInPath,tempTree);
				if (parents == null) {
					parents = (ArrayList) tempTree.clone();
				} else {
					parents.retainAll(tempTree);
				}

				tempCTCN = (CompactTarifsContainerNode) tempCN;
				// remove the Tarif from this node
				tempCTCN.contentsRemoveTarif(t);
				
				
			}
			
			
			// Remove from AllNode in Path the parent list 
			// after this allNodesInPath will contains all the nodes 
			// the future node will contains
			allNodesInPath.removeAll(parents);
			
			
			
		
			
			
			//	Get all the BCnodes I'm replacing //
			ArrayList/*<BCNode>*/ bcnodes = new ArrayList/*<BCNode>*/();
			
			Iterator/*<CompactNode>*/ i4= allNodesInPath.iterator(); 
			while (i4.hasNext()) {
				tempCN = (CompactNode) i4.next();
				
				// add BCnodes contained in this node to my list
				Object[] contents = tempCN.contentsGet();
				for (int i3 = 0; i3 < contents.length; i3++) {
					if (contents[i3] instanceof BCNode) {
						if (! bcnodes.contains(contents[i3]))
							bcnodes.add(contents[i3]);
					}
				}
			}
			// reverse BCNode order
			Collections.reverse(bcnodes);
			
			
			
			// future parent
			assert parents.size() > 0 : "I should at least find the root node";
			assert parents.get(0) instanceof CompactBCNode : 
				"How comes I could have a parent witch is not a BCNode";
			// create a new CompactTarif Ref
			CompactTreeTarifRef cttr = new CompactTreeTarifRef(t,1);
			
			// in parents we now find the treePath of the node to Group
			CompactBCNode parent = (CompactBCNode) parents.get(0);
			
			
			CompactBCGroupNode cbgn 
				= new CompactBCGroupNode(parent,
						rootNode.explorer,
						bcnodes,
						cttr,
						smallRootNode.getFirstIntersting());
			
			// add this to my parent
			parent.childrens.add(0,cbgn);
			
			
			// try to clean all node I've been unlinked from
			for (Iterator/*<CompactNode>*/ i2= ls.iterator(); i2.hasNext();) {
				tempCTCN = (CompactTarifsContainerNode) i2.next();
				
				CompactTarifsContainerNode tempParent;
				// if tempCN has no Tarif attached and no children remove it
				while (tempCTCN.getParent() != null &&
						tempCTCN.getParent() instanceof 
						CompactTarifsContainerNode &&
						tempCTCN.getChildrenAL().size() == 0 && 
						tempCTCN.tarifsRefs.size() == 0) {
					// remove me from my parent
					tempParent=
						(CompactTarifsContainerNode)tempCTCN.getParent();
					tempParent.childrens.remove(tempCTCN);
					tempCTCN = tempParent;
				}
			}
		}
		
	}
}

class SmallNode {
	BCNode node;
	SmallNode parent;
	ArrayList/*<SmallNode>*/ childrens;
	
	public SmallNode(BCNode bcn,SmallNode myParent) {
		node = bcn; parent = myParent; 
		childrens = new ArrayList/*<SmallNode>*/();
	}
	
	/** 
	 * construct a Tree from this TreePath .. 
	 * 
	 * @param treePath treePath.get(end) is the rootNode
	 */
	public void add(ArrayList/*<CompactNode>*/ treePath) {
		Stack/*<CompactNode>*/ s = new Stack();
		s.addAll(treePath);
		_add(s);
	}
	
	private void _add(Stack/*<CompactNode>*/ s) {
		if (s.size() == 0) return;
		CompactTreeItem cn = (CompactTreeItem) s.pop();
		if (! (cn instanceof CompactBCNode)) return;
		getChildrensWith(((CompactBCNode) cn).contentsGetBCnode())._add(s);		
	}
	
	
	/**
	 * get the first node under me with more than one chidlren
	 */
	public SmallNode getFirstIntersting() {
		if (childrens.size() == 1) {
			return ((SmallNode) childrens.get(0)).getFirstIntersting();
		}
		return this;
	}
	
	private SmallNode getChildrensWith(BCNode bcn) {
		SmallNode temp;
		for (Iterator/*<SmallNode>*/ i = childrens.iterator();i.hasNext();) {
			temp = (SmallNode) i.next();
			if (temp.node == bcn) return temp;
		}
		temp = new SmallNode(bcn,this);
		childrens.add(temp);
		return temp;
	}
	
	public void visitMeAndAllChildrens(SmallVisitor sv,int level) {
		sv.visit(this,level);
		level++;
		for (Iterator/*<SmallNode>*/ i = childrens.iterator();i.hasNext();) {
			((SmallNode) i.next()).visitMeAndAllChildrens(sv,level);
		}
		level--;
	}
	
	
	interface SmallVisitor {
		public void visit(SmallNode sn,int level);
	}
}

/*
 * $Log: CompactBCGroupNode.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.11  2004/10/04 15:30:54  perki
 * *** empty log message ***
 *
 * Revision 1.10  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.9  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.8  2004/08/05 11:44:11  perki
 * Paired compact Tree
 *
 * Revision 1.7  2004/08/04 16:40:08  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/07/30 15:48:40  perki
 * bugs for kaspar
 *
 * Revision 1.5  2004/07/30 15:38:19  perki
 * some changes
 *
 * Revision 1.4  2004/07/30 11:28:39  perki
 * Better tooltips
 *
 * Revision 1.3  2004/07/30 09:09:26  perki
 * Grouping ok
 *
 * Revision 1.2  2004/07/30 07:07:23  perki
 * Moving Compact Tree from uicomponents to uicomponents.compact
 *
 * Revision 1.1  2004/07/30 05:58:15  perki
 * Slpitted CompactNode.java in sevral files
 *
 */