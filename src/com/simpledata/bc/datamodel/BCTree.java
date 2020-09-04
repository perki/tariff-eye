/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: BCTree.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/
package com.simpledata.bc.datamodel;

import java.util.*;

import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.util.CollectionsToolKit;

import org.apache.log4j.Logger;

/**
* BCTree
* Mapping object for BCNodes.
*/
public class BCTree extends Named {
	private static final Logger m_log = Logger.getLogger( BCTree.class ); 
		
	/** Class type for Named @see Named**/
	public final static String CLASS_TYPE= "BCTREE";

	/** definition of the base tree name **/
	public final static String TYPE_BASE = "base";

	/** redefinition of types **/
	public final static String TYPE_LOCALISATION= "loc";
	public final static String TYPE_FUNDS= "funds";
	public final static String TYPE_MARKET= "market";
	public final static String TYPE_INDUSTRY= "indus";
	public final static String TYPE_OBLIGATIONS= "obligations";
	public final static String TYPE_USERS= "user";
	public final static String TYPE_INDEXES= "indexes";


	/** 
	 * TYPES OF KNOWN TREES 
	 * !!! only trees in this selectin will be created automagicaly
	 **/
	public final static HashMap TREE_TITLES;
	static {
		TREE_TITLES= new HashMap();
		TREE_TITLES.put(TYPE_BASE,"Base Tree");
		TREE_TITLES.put(TYPE_LOCALISATION, "Localisation-Tree");
		TREE_TITLES.put(TYPE_FUNDS, "Type of Funds");
		TREE_TITLES.put(TYPE_MARKET, "Market");
		TREE_TITLES.put(TYPE_INDUSTRY, "Industry");
		TREE_TITLES.put(TYPE_INDEXES, "Indexes");
		TREE_TITLES.put(TYPE_OBLIGATIONS, "Obligations");
		TREE_TITLES.put(TYPE_USERS, "User-Tree");
	}

	/** type of node .. see TYPE_UNDEF, TYPE_BASE .... ***/
	protected String type= "undef";

	/** root node of this tree **/
	protected BCNode root;


	/** hashtable containing the relation Parent <--> Children**/
	protected HashMap parentsOf;

	/** 
	* hashtable containing the relation Childrens <--> Parents<BR>
	* IS A REVERSED INDEX OF parentOf
	**/
	protected HashMap childrensOf;

	
	/** 
	 * this boolean denotes the visibility of the this BCTree<BR>
	 * Can be interpreted as "is used"
	 */
	protected boolean xVisible;

	// ---------------------- initialisation ----------------------------//
	/**
	* constructor.. should not be called by itself. use Tarification.createTree()
	*/
	public BCTree(
		Tarification tarification,
		String type,
		String title,
		String id) {
		super(CLASS_TYPE, tarification, title, id);
		this.type= type;
		parentsOf= new HashMap();
		childrensOf= new HashMap();
		// add root node
		root= new BCNode(this, this.getTitle());
		xVisible = true;
	}

	//----------------------- typing ------------------------//

	public String getType() {
		return type;
	}

	//----------------------- node creation -----------------------------//
	/**
	* return a newly created node, attached to root with automatic id generation
	*/
	public synchronized BCNode createNode(String translationKey) {
		return createNode(translationKey, root);
	}

	/**
	* return a newly created node, attached to the specified parent node
	*/
	public synchronized BCNode createNode(
		String translationKey,
		BCNode parent) {

		BCNode temp= new BCNode(this, translationKey);


		// put node in tree
		add(parent, temp, -1);
		return temp;
	}

	/**
	 * Simulate dropNode()
	 * @return null if done or a message if cannot be done
	 * @see #dropNode(BCNode node, boolean recursive)
	 */
	public String canBeDropedNode(BCNode node, boolean recursive) {
		return _dropNode(node, recursive, true);
	}

	/**
	* drop a node, removing all references to it.
	* @param recursive if set to true all belonging childrens are also removed else uplink childrens to the actual parent. If this node has no parent (unlinked.. then recurse)
	* @return ATTENTION!! if a node that is programmed to be removed has a Tarif mapped to it! DROP WILL FAILED RETURNING FALSE.
	*/
	public boolean dropNode(BCNode node, boolean recursive) {
		return (_dropNode(node, recursive, false) == null);
	}

	/**
	* drop or simulate drop on a node, removing all references to it.
	* @return null if done or a message if cannot be done
	*/
	private String _dropNode(
		BCNode node,
		boolean recursive,
		boolean simulate) {
		// first of all simulate if it's possible
		if (!simulate) {
			String res = _dropNode(node,recursive,true);
			if (res != null) return res;
		}
		
		
		BCNode parent= getParent(node);
		if (parent == null)
			recursive= true;

		// check if nodes allow tobe dropped
		if (! node.getBoolProperty(BCNode.PROP_BOOL_DROPABLE)) {
			return "Node protected";
		}
		
		// check if I can do this
		if (getTarifMapping(node).size() > 0) {
			return "Cannot remove:node is linked to a Tarif";
		}
		if (recursive) {
			if (getTarifMappingRecursively(node).size() > 0) {
				return "Cannot remove:node has childrens";
			}
		}

	

		// now go for childrens
		ArrayList childs= (ArrayList)getChildrens(node).clone();
		String msg = null;
		for (int i= 0; i < childs.size(); i++) {
			
			if (!recursive) {
				// if ! MOVABLE.. not possible
				if (! canBeMovedTo((BCNode) childs.get(i), parent))
					return "One of the children : is not moveable";
				if (!simulate) {
					//attach them do my parent
					add(parent, (BCNode) childs.get(i), -1);
				} 
			} else {
				//drop them
				msg = _dropNode((BCNode) childs.get(i), true, simulate);
				if (msg != null) {
					return "One of the children :"+msg;
				}
			}
		}

		// do it if not simulating
		if (!simulate) {
			unlink(node); // node is unlinked from parent
		}
		
		return null;
	}

	//----------------------- data mapping ------------------------------//
	/**
	* get the Tarifs Mapped to this node .
	* @return THE mapping vector .. should never been modified without care!
	*/
	public ArrayList/*<Tarif>*/ getTarifMapping(BCNode node) {
		return xTarification.tarifsNodesMap.getLeftOf(node);
	}

	
	
	
	/**
	* get the Tarifs Mapped to this node and recursively into childrens.
	*/
	public ArrayList getTarifMappingRecursively(BCNode node) {
		ArrayList/*<Tarif>*/ result= new ArrayList/*<Tarif>*/();
		CollectionsToolKit.addToCollection(result, getTarifMapping(node));

		Iterator/*<BCNode>*/ e = getChildrensRecursively(node).iterator();
		while (e.hasNext()) {
			CollectionsToolKit.addToCollection(
				result,
				((BCNode) e.next()).getTarifMapping());
		}
		return result;
	}

	//----------------------- tree tools -------------------------------//

	/**
	* Can a node be attached to this futureParent node <=> (! node.isAncestorOf(futureParent))  
	* @return true if this source can be attached to this parent
	*/
	public boolean canBeMovedTo(BCNode node, BCNode futureParent) {
		// if node is root -> false
		if (isRoot(node)) 
			return false;
		

		// if node has no parent the -> true (first placement)
		if (getParent(node) == null)
			return true;
			

		// check if node is moveable
		if (!node.getBoolProperty(BCNode.PROP_BOOL_MOVEABLE)) 
			return false;
		
		// check if parent accepts NEW childrens
		if (!futureParent.getBoolProperty(BCNode.PROP_BOOL_EXTENDABLE)) 
			return false;
		
		// if this node is a parent of the future parent return false
		if (isAncestorOf(node, futureParent)) 
			return false;
		
		// check if this node can be removed from a specific ancestor
		try {
			BCNode temp=
				(BCNode) node.getProperty(BCNode.PROP_NODE_TO_KEEP_AS_ANCESTOR);
			if (temp != null) {
				if (temp != futureParent) {
					if (!isAncestorOf(temp, futureParent))
						return false;
				}
			}
		} catch (ClassCastException e) {
			m_log.error( "BCTRee: canBeMovedTo", e );
		}

		// pfiu!! all tests passed
		return true;
	}

	/**
	* Ancestor or not?
	* @return true if a node is an ancestor of a specified children
	*/
	public boolean isAncestorOf(BCNode ancestor, BCNode children) {
		return getAncestorsOf(children).contains(ancestor);
	}

	/**
	* @return true if a node is a direct children of this parent
	*/
	public boolean isParentOf(BCNode parent, BCNode children) {
		BCNode testParent= getParent(children);
		if (testParent == null)
			return false;
		return (testParent == parent);
	}

	/**
	* @return true if its the rootNode of this Tree
	*/
	public boolean isRoot(BCNode node) {
		return (root == node);
	}

	/**
	* @return the rootNode of this tree
	*/
	public BCNode getRoot() {
		return root;
	}

	//----------------------- tree manipulation -------------------------//

	/**
	* add a children to the specified node
	* if this children already has a parent, then it'is unlinked from the previous parent<BR>
	* can be used to move a node to another position in the tree
	* @param position specify the position in the childrens ordering list. -1 to add at the end
	* @return true if succeded
	*/
	public boolean add(BCNode node, BCNode children, int position) {
		if (!canBeMovedTo(children, node))
			return false;

		// first we remove the children from any other reference
		unlink(children);
		parentsOf.put(children, node);

		ArrayList v= getChildrens(node);
		if ((position < 0) || (position > v.size()))
			position= v.size();
		v.add(position,children);
		// if the children has mapped Tarifs
		// makes an add/remove of tarif to validate them
		Iterator e= ((ArrayList) children.getTarifMapping().clone()).iterator();
		while (e.hasNext()) {
			Tarif t= (Tarif) e.next();
			t.refreshMap(children);
		}

		return true;
	}

	/**
	* unlink this node from the tree. (remove)<BR>
	* USE _dropNode or dropNode !!!!! <BR>
	* <B>THERE IS NO CHECK PEFFORMED WITH UNLINK!!</B>
	*/
	private void unlink(BCNode node) {
		if (!parentsOf.containsKey(node))
			return;
		BCNode parent= getParent(node);
		if (parent == null)
			return;
		getChildrens(parent).remove(node);
	}

	/**
	* return the childrens vector of this node
	*/
	public ArrayList getChildrens(BCNode node) {
		if (!childrensOf.containsKey(node)) {
			childrensOf.put(node, new ArrayList());
		}
		return (ArrayList) childrensOf.get(node);
	}

	/**
	* return the childrens,childrens and childrens ... vector of this node
	*/
	public ArrayList getChildrensRecursively(BCNode node) {
		ArrayList result= new ArrayList();
		_getChildrensRecursively(node, result);
		return result;
	}

	/**
	* return the childrens,childrens and childrens ... vector of this node.<BR>
	* and store the result in the passed ArrayList
	*/
	private void _getChildrensRecursively(BCNode node, ArrayList result) {
		ArrayList childs= getChildrens(node);
		for (int i= 0; i < childs.size(); i++) {
			if (!result.contains(childs.get(i))) {
				result.add(childs.get(i));
				_getChildrensRecursively((BCNode) childs.get(i), result);
			}
		}
	}


	/**
	* return the parent of this node. (return null if unkown node or has no parent)
	* note: rootNode will return null.
	*/
	public BCNode getParent(BCNode node) {
		return (BCNode) parentsOf.get(node);
	}

	/**
	* get the Ancestor list of this node. This list does contain 
	* at the last position the node itself.
	* @return the Ancestor ArrayList of this node. 
	* ArrayList.get(0) is the root of this tree.
	*/
	public ArrayList getAncestorsOf(BCNode node) {
		ArrayList result= new ArrayList();
		if (node.tree != this)
			return result; // prevents looking for ancestors in other trees
		result.add(node);
		if (!isRoot(node)) {
			_addParentsToArrayList(node, result);
		}
		return result;
	}

	/**
	* helper for recursive search of parents, used by getAncestorOf()
	*/
	private void _addParentsToArrayList(BCNode node, ArrayList result) {
		BCNode parent= getParent(node);
		result.add(0, parent);
		if (isRoot(parent))
			return;
		_addParentsToArrayList(parent, result);
	}

	//-------------------- Ordering ------------------//

	/**
	* in the vector of children, move the specified Node up or down.
	* @param position (-1) or great number to be at the end
	*/
	public void setPositionOfTo(BCNode children, int position) {
		BCNode parent= getParent(children);
		if (parent == null)
			return;

		ArrayList childrens= getChildrens(parent);
		int pos= childrens.indexOf(children);
		if (pos < 0)
			return;

		int dest= childrens.size();
		if ((position >= 0) && (position <= childrens.size()))
			dest= position;
		if (dest > pos)
			dest--;
		childrens.add(dest, childrens.remove(pos));
	}

	/**
	* return the position of this node in it's parent's children list
	* (-1) if has no parent
	*/
	public int getPositionOf(BCNode node) {
		BCNode parent= getParent(node);
		if (parent == null)
			return -1;

		ArrayList childrens= getChildrens(parent);
		return childrens.indexOf(node);
	}
	


	/**
	 * @return true if this node in known 
	 */
	public boolean containsNode(BCNode bcnode) {
		if (isRoot(bcnode)) return true;
		return parentsOf.containsKey(bcnode);
	}
	
	//---------------- Visibility --------//
	/**
	 *return true if this tree is visible
	 */
	public boolean isVisible() {
		// if no trees are visible and I'm the base Tree then I'm visible
		if (getTarification().getTreeBase() == this &&
				xVisible == false &&
				getTarification().getMyTreesVisibleAndIgnore(this).length == 1)
			setVisibleForced(true);
	
		return xVisible;
	}
	
	/**
	 * return true if the visibility of this Tree can be changed
	 */
	public boolean canChangeVisibility() {
		// if this is the only tree visible then no
		return ! (isVisible() 
				&& getTarification().getMyTreesVisible().length == 1);
	}
	
	/**
	 * change the visibility of this (use) of this BCTree<BR>
	 * fire a NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED
	 */
	public void setVisible(boolean visible) {
		if (! canChangeVisibility()) return;
		setVisibleForced(visible);
	}
	/**
	 * private access to force visible state with no check
	 */
	private void setVisibleForced(boolean visible) {
		if (xVisible == visible) return;
		this.xVisible = visible;
		
		// be sure at least One tree is checked
		
		
		fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
	}

	//---------------- XML ---------------//
	/** XML **/
	public BCTree() {}

	/**
	 * XML
	 */
	public HashMap getChildrensOf() {
		return childrensOf;
	}


	/**
	 * XML
	 */
	public HashMap getParentsOf() {
		return parentsOf;
	}

	/**
	 * XML
	 */
	public void setChildrensOf(HashMap map) {
		childrensOf= map;
	}

	/**
	 * XML
	 */
	public void setParentsOf(HashMap map) {
		parentsOf= map;
	}

	/**
	 * XML
	 */
	public void setRoot(BCNode node) {
		root= node;
	}

	/**
	 * XML
	 */
	public void setType(String string) {
		type= string;
	}

	/**
	 * XML
	 */
	public boolean isXVisible() {
		return xVisible;
	}
	/**
	 * XML
	 */
	public void setXVisible(boolean visible) {
		this.xVisible = visible;
	}
}


/* $Log: BCTree.java,v $
/* Revision 1.2  2007/04/02 17:04:23  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:36  perki
/* First commit on sourceforge
/*
/* Revision 1.49  2004/09/03 11:47:53  kaspar
/* ! Log.out -> log4j first half
/*
/* Revision 1.48  2004/07/30 15:38:19  perki
/* some changes
/*
/* Revision 1.47  2004/07/19 13:54:46  kaspar
/* - refactoring: Moving Compact* nodes into public view for
/*   access in reporting
/* - Removed useless reporting classes
/* - Adding partly finished Linearizer Test
/* - Accomodated for changements in how to do things
/*
/* Revision 1.46  2004/07/15 17:49:56  perki
/* grading better
/*
/* Revision 1.45  2004/07/12 17:34:31  perki
/* Mid commiting for new matching system
/*
/* Revision 1.44  2004/07/08 14:58:59  perki
/* Vectors to ArrayList
/*
/* Revision 1.43  2004/07/02 09:37:31  perki
/* *** empty log message ***
/*
/* Revision 1.42  2004/06/28 10:38:47  perki
/* Finished sons detection for Tarif, and half corrected bug for edition in STable
/*
/* Revision 1.41  2004/06/25 10:09:49  perki
/* added first step for first sons detection
/*
/* Revision 1.40  2004/06/25 08:30:55  perki
/* oordering in tree modified
/*
/* Revision 1.39  2004/06/23 18:38:04  perki
/* *** empty log message ***
/*
/* Revision 1.38  2004/06/22 17:11:29  perki
/* CompactNode now build from datamodel and added a notice interface to WorkSheet
/*
/* Revision 1.37  2004/06/22 08:59:05  perki
/* Added CompactTree for CompactNode management and first sync with CompactExplorer
/*
/* Revision 1.36  2004/06/21 16:27:31  perki
/* added compact tree node and visibility / reorder for bctree
/*
/* Revision 1.35  2004/05/20 09:39:43  perki
/* *** empty log message ***
/*
/* Revision 1.34  2004/04/09 07:16:51  perki
/* Lot of cleaning
/*
/* Revision 1.33  2004/03/17 14:28:53  perki
/* *** empty log message ***
/*
/* Revision 1.32  2004/03/08 08:46:02  perki
/* houba houba hop
/*
/* Revision 1.31  2004/03/06 14:24:50  perki
/* Tirelipapon sur le chiwawa
/*
/* Revision 1.30  2004/03/03 10:17:23  perki
/* Un petit bateau
/*
/* Revision 1.29  2004/02/26 10:27:37  perki
/* TAC goes to hollywood
/*
/* Revision 1.28  2004/02/23 18:46:04  perki
/* *** empty log message ***
/*
/* Revision 1.27  2004/02/22 15:57:25  perki
/* Xstream sucks
/*
* Revision 1.26  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.25  2004/02/06 15:07:44  perki
* New nodes
*
* Revision 1.24  2004/02/04 19:04:19  perki
* *** empty log message ***
*
* Revision 1.23  2004/02/02 16:32:06  perki
* yupeee
*
* Revision 1.22  2004/02/02 11:21:05  perki
* *** empty log message ***
*
* Revision 1.21  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.20  2004/01/31 10:28:56  perki
* BCNode manipulation ok-- c'est de la bombe
*
* Revision 1.18  2004/01/22 15:40:51  perki
* Bouarf
*
*/