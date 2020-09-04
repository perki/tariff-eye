/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: BCNode.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/
package com.simpledata.bc.datamodel;

import java.util.*;

import com.simpledata.bc.components.tarif.TarifManager;

import org.apache.log4j.Logger;

/**
* BCNode
* Tree describing and modeling element
*/
public class BCNode extends Named {
	private static final Logger m_log = Logger.getLogger( BCNode.class ); 
	
	/** RealClass type **/
	public final static String CLASS_TYPE = "NODE";
	
	/** references the tree this node belong to **/
	protected BCTree tree;

	
	/** 
	* HashMap containing properties <BR>
	* values of constraints can be acceded using setProperty or getProperty
	**/
	protected HashMap properties ;
	
	//-------------------------------------------------------------------------//
	
	// properties (keys to constrainst)
	/** key for boolean property : is this node movable ?**/
	public static String PROP_BOOL_MOVEABLE = "prop_bool_movable";
	
	/** key for boolean property : does this node accept NEW childrens ?**/
	public static String PROP_BOOL_EXTENDABLE = "prop_bool_extendable";
	
	/** key for boolean property : can this node be droped (removed) ?**/
	public static String PROP_BOOL_DROPABLE = "prop_bool_dropable";
	
	/** 
	* if this property is not null, then this node will keep 
	* this specified parent as it's ancestor.<BR>
	* Note: has no effect if PROP_NODE_MOVEABLE is set to false
	**/
	public static String PROP_NODE_TO_KEEP_AS_ANCESTOR = "prop_ancestor";
	
	/**
	* key to get the list of accepted tarifs types for this node
	*/
	public static String PROP_ACCEPTED_TARIFS_TYPE = "prop_accepted_tarifs";
	
	// Init
	/** 
	* Constructor -- should never be called by itself. use : BCTree.createNode()
	* @see BCTree#createNode(String translationKey)
	* @param tree , the tree it references to
	**/
	protected BCNode( BCTree tree,String title) {
		super(CLASS_TYPE,tree,title);
		this.tree = tree;
		//tarifs = new ArrayList();
		properties = new HashMap();
	}
	
	public String toString() {
		return getTitle();
	}
	
	//--------- Data Mapping ----------------//
	/**
	* get the Tarifs Mapped to this node.
	*/
	public ArrayList getTarifMapping() {
		return tree.getTarifMapping(this);
	}
	
	
	
	//-------- Tree tools -------------------//
	/**
	* @return true if its the rootNode of this Tree
	*/
	public boolean isRoot() {
		return tree.isRoot(this);
	}
	
	/**
	* @return true if this node is an ancestor of this children or if 
	* children is the node itself
	*/
	public boolean isAncestorOf(BCNode children) {
		return tree.isAncestorOf(this,children);
	}
	
	/**
	* @return true if this node is the direct parent of this node
	*/
	public boolean isParentOf(BCNode children) {
		return tree.isParentOf(this,children);
	}
	
	/**
	* return the childrens vector of this node
	*/
	public ArrayList getChildrens() {
		return tree.getChildrens(this);
	}
	
	/**
	* return the parent of this node. (return null if unkown node or 
	* has no parent)
	* note: rootNode will return null.
	*/
	public BCNode getParent() {
		return tree.getParent(this);
	}
	
	/**
	* return the BCTree this node is attached to
	*/
	public BCTree getTree() {
		return tree;
	}
	
	// properties and constraints
	
	/**
	* set a specific property to a node<BR>
	* use PROP_* keys<BR>
	* for booleans use: setBoolProperty
	*/
	public void setProperty(Object key, Object value) {
		properties.put(key,value);
	}
	
	/**
	* set a boolean value to a property<BR>
	* use PROP_BOOL_* keys<BR>
	*/
	public void setBoolProperty(Object key, boolean value) { 
		properties.put(key,new Boolean(value));
	}
	
	/**
	* get the value of a specific property<BR>
	* use PROP_* keys<BR>
	* for booleans use: getBoolProperty
	*/
	public Object getProperty(Object key) {
		return properties.get(key);
	}
	
	/**
	* get the boolean value of a specific property<BR>
	* use PROP_BOOL_* keys<BR>
	* <B>!!!!Note!!!!.. if key does not exists or not boolean.. 
	* it will return TRUE<BR>
	* So take care of having all your default value set to true</B>
	*/
	public boolean getBoolProperty(Object key) {
		Object o = properties.get(key);
		if (o == null) return true;
		if (! Boolean.class.isInstance(o)) return true; 
		Boolean b = (Boolean) o;
		return b.booleanValue();
	}
	
	/**
	* tells if the acceptedTarifTypes are kept here or rely on it's parent
	* @param value set true if it relys on parent
	* @see #isRelingOnParentForTarifTypes()
	*/
	public void setAcceptedTarifTypesReliesOnParent(boolean value) {
		if (value) {
			setProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE,null);
		} else {
			// fill with actual parents setting
			setProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE,
				getAcceptedTarifTypes().clone()
			);
		}
	}
	
	/**
	* tells if the acceptedTarifTypes are kept here or rely on it's parent
	*/
	public boolean isRelingOnParentForTarifTypes() {
		return (getProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE) == null);
	}
	
	/**
	* add an accepted tarifs types  accepted by this Node.
	* @see TypesAndConstraints
	*/
	public void addAcceptedTarifType(Object tarifType) {
		if (isRelingOnParentForTarifTypes()) {
			m_log.error( "BCNode:"+getFullNID()+" cannot add tarif"+
			             "when relying on parent" );
			return;
		}
		ArrayList v = (ArrayList) getProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE);
		v.remove(tarifType);
		v.add(tarifType);
	}
	
	/**
	* remove an accepted tarifs types  accepted by this Node.
	* @see TypesAndConstraints
	*/
	public void removeAcceptedTarifType(Object tarifType) {
		if (isRelingOnParentForTarifTypes()) {
			m_log.error( "BCNode:"+getFullNID()+" cannot remove tarif"+
			             "when relying on parent");
			return;
		}
		ArrayList v = (ArrayList) getProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE);
		v.remove(tarifType);
	}
	
	/**
	* get the tarifs type lists accepted by this Node.<BR>
	* the passed object is THE actual references to the Types.<BR>
	* The ArrayList is made of <B>String</B>
	*/
	public ArrayList getAcceptedTarifTypes() {
		try {
			ArrayList v = (ArrayList) getProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE);
			if (v == null) { // not localy handeled
				if (isRoot()) { // fill with all known Tarifs
					v = TarifManager.getTarifTypes();
					setProperty(BCNode.PROP_ACCEPTED_TARIFS_TYPE,v);
				} else {
					return getParent().getAcceptedTarifTypes();
				}
			}
			return v;
		} catch (Exception e) {
			m_log.error( "BCNode: getAcceptedTarifTypes[]", e );
		}
		return null;
	}
	
	/**
	* Check if this TarifType is accepted by this Node
	*/
	public boolean acceptThisTarifType(Object tarifType) {
		return getAcceptedTarifTypes().contains(tarifType);
	}
	
	//-------- Tree Manipulation ------------//
	
	/**
	 * simulate a drop on this node.
	 * @return null if OK or a message if it cannot be dropped
	 */
	public String canBeDroped(boolean recursive) {
			return tree.canBeDropedNode(this,recursive);
		}
	
	/**
	* drop this node (remove)
	* @param recursive if set to true all its childrens are attached to 
	* its parent
	* @return ATTENTION!! if a node that is programmed to be removed has 
	* a Tarif mapped to it! 
	* DROP WILL FAIL RETURNING FALSE.
	*/
	public boolean drop(boolean recursive) {
		return tree.dropNode(this,recursive);
	}
	
	/** 
	* move this node to another location. Childrens remains attached to it.
	* @param position specify the position in the childrens ordering list 
	* of the new parent. -1 to add at the end
	* @return true if succeded
	*/
	public boolean setParent(BCNode parent, int position) {
		return tree.add(parent,this,position);
	}
	
	/**
	* @return true if this node can me moved to have the following node has 
	* parent.
	*/
	public boolean canBeMovedTo(BCNode futureParent) {
		return tree.canBeMovedTo(this,futureParent);
	}

	//------------------------- Childrens Ordering tools ----------------//
	
	/**
	* return the position of this node in it's parent's children list
	* (-1) if has no parent
	*/
	public int getPosition() {
		return tree.getPositionOf(this);
	}
	
	/**
	* position of this node in its parent children list
	* @param position (-1) or great number to be at the end
	*/
	public void setPositionTo(int position) {
		tree.setPositionOfTo(this,position);
	}
	
	//------------------ XML -----------------//
	/** XML CONSTRUCTOR **/
	public BCNode() {}
	
	/**
	 *XML
	 */
	public HashMap getProperties() {
		return properties;
	}

	/**
	 *XML
	 */
	public void setProperties(HashMap map) {
		properties= map;
	}

	/**
	 *XML
	 */
	public void setTree(BCTree tree) {
		this.tree= tree;
	}

}
/*
* $Log: BCNode.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:36  perki
* First commit on sourceforge
*
* Revision 1.38  2004/11/23 14:06:35  perki
* updated tariffs
*
* Revision 1.37  2004/09/03 11:47:53  kaspar
* ! Log.out -> log4j first half
*
* Revision 1.36  2004/07/22 15:12:35  carlito
* lots of cleaning
*
* Revision 1.35  2004/07/19 13:54:46  kaspar
* - refactoring: Moving Compact* nodes into public view for
*   access in reporting
* - Removed useless reporting classes
* - Adding partly finished Linearizer Test
* - Accomodated for changements in how to do things
*
* Revision 1.34  2004/07/12 17:34:31  perki
* Mid commiting for new matching system
*
* Revision 1.33  2004/07/08 14:58:59  perki
* Vectors to ArrayList
*
* Revision 1.32  2004/07/02 09:37:31  perki
* *** empty log message ***
*
* Revision 1.31  2004/06/28 16:47:54  perki
* icons for tarif in simu
*
* Revision 1.30  2004/06/28 13:22:37  perki
* icons are 16x16 for macs
*
* Revision 1.29  2004/06/25 10:09:49  perki
* added first step for first sons detection
*
* Revision 1.28  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.27  2004/03/17 14:28:53  perki
* *** empty log message ***
*
* Revision 1.26  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.25  2004/02/17 09:40:06  perki
* zibouw
*
* Revision 1.24  2004/02/04 12:53:30  carlito
* rajouter un import...
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
* Revision 1.19  2004/01/29 13:40:40  perki
* *** empty log message ***
*
* Revision 1.18  2004/01/22 15:40:51  perki
* Bouarf
*
* Revision 1.17  2004/01/22 13:03:31  perki
* *** empty log message ***
*
* Revision 1.16  2004/01/18 15:21:18  perki
* named and jdoc debugging
*
* Revision 1.15  2004/01/18 14:23:52  perki
* Naming about to be finished
*
* Revision 1.14  2004/01/17 14:27:54  perki
* Better (Best?) Named implementation
*
* Revision 1.13  2004/01/07 15:00:11  perki
* type handling for Tarifs
*
* Revision 1.12  2004/01/06 17:33:07  perki
* better constraints handling for TNode
*
* Revision 1.11  2004/01/06 11:03:52  perki
* properties for TNodes
*
* Revision 1.10  2004/01/05 16:11:44  perki
* *** empty log message ***
*
* Revision 1.9  2003/12/17 17:57:13  perki
* *** empty log message ***
*
* Revision 1.8  2003/12/16 12:52:50  perki
* Type and constraints + improvements on naming
*
* Revision 1.7  2003/12/15 16:45:11  perki
* *** empty log message ***
*
* Revision 1.6  2003/12/15 11:17:13  perki
* better parent/ancestor handling
*
* Revision 1.5  2003/12/12 16:22:44  perki
* week-end
*
* Revision 1.4  2003/12/12 12:12:31  perki
* Sevral debuging
*
* Revision 1.3  2003/12/11 18:33:50  perki
* *** empty log message ***
*
* Revision 1.2  2003/12/11 16:38:47  perki
* + Tarif
*
* Revision 1.1  2003/12/10 16:38:40  perki
* *** empty log message ***
*
*/