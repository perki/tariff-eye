/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 *
 * $Id: STreeNode.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.uitools.stree;

import javax.swing.*;

/**
* Defines an abstract tree structure used by STree (@see STree)
*
* Methods which must be redefined 
* 1.  Vector                  getChildren()
* 2.  int                     getChildCount()
* 3.  STreeNode               getChildAt(int index)
* 4.  int                     getChildIndex(STreeNode child)
* 5.  boolean                 addChildAt(STreeNode child, int index)
* 6.  STreeNode               getParent()
* 7.  boolean                 remove(STreeNode child)
* 8.  boolean                 move(STreeNode destination, int position)
* 9.  boolean                 order(int newPosition)
* 10. boolean                 acceptDrag()    // not used at the moment
* 11. boolean                 acceptDrop(STreeNode droppedNode)
* 12. String                  toString() // name displayed in the tree
* 13. boolean                 isCheckable()
* 14. int                     getCheckState()
* 15. void                    check()
* 16. boolean                 equals(STreeNode node)
*
* Methods which should be implemented for graphical tuning
* 1.  ImageIcon               getOpenedIcon()
* 2.  ImageIcon               getClosedIcon()
* 3.  ImageIcon               getLeafIcon()
* 4.  ImageIcon               getIcon() // if implemented, will override 1,2 and 3
* 5.  ImageIcon               getCheckIcon()
*
*/
public abstract class STreeNode {
	
	public final static int NOT_CHECKED = 0;
	public final static int PARTIALLY_CHECKED = 1;
	public final static int FULLY_CHECKED = 2;

	/**
	* Returns an Enumeration of the node's children
	*/
	//public abstract Enumeration children();
	/*
	public Enumeration children() {
		return this.getChildren().elements();
	}
	*/
	
	/**
	* Returns node's parent
	* WARNING : must absolutely return null if
	* node is root
	*/	
	public abstract STreeNode getParent();
	
	/**
	* Returns a Vector of node's children
	*/	
	//public abstract Vector getChildren();
	
	/**
	* Returns child at specified index
	* if no child or index out of bounds, return null
	*/
	public abstract STreeNode getChildAt(int index) ;
	
	/**
	* Returns the number of children attached to this node
	*/	
    public abstract int getChildCount() ;
	
	/**
	* Returns the index of child within the node's children
	*/	
	public int getChildIndex(STreeNode child) {
		return child.getIndex();
	}
	
	/**
	* The node returns its index within its parent children
	* if it has parent else returns -1
	**/
	public  abstract int getIndex();
	
	/**
	* Adds a child at a specified index
	*/	
	public abstract boolean addChildAt(STreeNode child, int index);
	
	/**
	* Adds child at the end of children list
	*/	
    public boolean addChild(STreeNode child) {
		return (this.addChildAt(child, this.getChildCount()));
	}
	
	/**
	* Return true if the node is a leaf
	*/	
    public boolean isLeaf() {
		if (this.getChildCount() == 0) {
			return true;
		} 	
		return false;
	}
	
	/**
	* Returns depth of desired node
	* 0 for root
	*/
	public int getDepth() {
		int res = 0;
		STreeNode dad = this.getParent();
		while (dad != null) {
			res++;
			dad = dad.getParent();
		}
		return res;
	}
	
	/**
	* Returns true if this node is parent
	* of "node"
	*/	
	public boolean isAncestorOf(STreeNode node) {
		boolean res = false;
		STreeNode currentNode = node.getParent();
		while (currentNode != null) {
			if (this.equals(currentNode)) {
				res = true;
				break;
			}
			currentNode = currentNode.getParent();
		}
		return res;
	}
	
	/**
	* Removes child from children list
	*/
    public abstract boolean remove(STreeNode node);
	
	/**
	* Move the node to newPosition within its
	* parent children
	* return true in case of success
	*/
	public abstract boolean order(int newPosition);
	
	/**
	* Detach this node from its parent
	* and attach it to node : destination at index : position
	* return true in case of success
	*/
	public abstract boolean move(STreeNode destination, int position);
	
	/**************************************************************************/
	/******************* DRAG n DROP MANAGEMENT              ******************/
	/**************************************************************************/
	
	/**
	* Determines if the node is allowed to be dragged
	**/
	public abstract boolean acceptDrag();
	
	/**
	* Determines if the node would accept the droppedNode as child 
	*
	**/
	public abstract boolean acceptDrop(STreeNode droppedNode);
	
	/**************************************************************************/
	/******************* CHECKABLE tree MANAGEMENT           ******************/
	/**************************************************************************/

	/**
	* If false is returned then the chekbox will be disabled
	*
	**/
	public abstract boolean isCheckable();
	
	/**
	* Returns the status of the node
	* <PRE>
	* NOT_CHECKED      : not checked if leaf
	*                  : no child checked if not leaf
	* PARTIALY_CHECKED : the node is not a leaf and some of his children
	*                  : are not checked
	* FULLY__CHECKED   : leaf checked, or non leaf with all children checked
	*</PRE>
	**/
	public abstract int getCheckState();
	
	/**
	* Check or uncheck this node and all his subnodes
	* returns true in case of success
	**/
	public abstract void check() ;
	
	/**
	 * @return the icon corresponding to the checkStatus of the node
	 */
	public ImageIcon getCheckIcon() {
		return null;
	}
	
	/**
	* Returns the icon to be displayed if the node
	* is not a leaf and is expanded
	*/
	public ImageIcon getOpenedIcon() {
		return null;
	}
	
	/**
	* Returns the icon to be displayed if the node
	* is not a leaf and is not expanded
	*/
	public ImageIcon getClosedIcon() {
		return null;
	}
	
	/**
	* Returns the icon to be displayed if the node
	* is a leaf
	*/
	public ImageIcon getLeafIcon() {
		return null;
	}
	
	/**
	* Returns the correct icon corresponding to the context
	* determined by the user node itself (ie leaf or not etc...)
	*/
	public ImageIcon getIcon() {
		return null;
	}
	
	/**
	* Returns stNodeTitle
	*/
	public abstract String toString();
	
	/**
	* Test equality with another node
	*/
	public abstract boolean equals(STreeNode dstn);
	
	// JPopupMenu managmement
	
	/**
	 * This method is called by STree when a right click is done upon the node<br>
	 * It can redefine an entire JPopupMenu with ActionListeners etc, in case of 
	 * highly customization requirement.<br>
	 * Or it can make a call to a static method in STree : <br>
	 * getPopup(STreeNode source, String[] actionNames, String[] actionKeys)<br>
	 * actionNames : the displayed names of each JMenus<br>
	 * actionKeys  : the key strings used for the call of doAction in -source- STreeNode<br>
	 * 
	 * <br>If you don't need JPopupMenu on your nodes, just return null
	 * <br><br>For optimization purposes you should store a permanent JPopupMenu
	 * in your implementation of STreeNode to avoid instanciations on every clicks
	 * @return JPopupMenu to be displayed in tree
	 */
	public abstract JPopupMenu getPopupMenu();
	
	/**
	 * This method is called by static method getPopup in STree<br>
	 * Tells the node that action "key" has been invoked upon him within
	 * a JPopupMenu (generated in getPopupMenu)<br>
	 * <b>Important</b> : Remember to add this line for non recognized Strings :<br>
	 * <code>
	 * Log.catchException("Key "+key+" not found in doAction");
	 * </code>
	 * <br><br>
	 * If you are not using JPopupMenus or if you do but do not use 
	 * static call to method getPopup in STree, just return
	 * @param key action string corresponding to desired action
	 */
	public abstract void doAction(String key);
	
	//Editing Management
	/**
	 * Return true if this STreeNode implements all methods of editon
	 */
	public boolean isEditable() {
		return true;
	}
	

	// HighLight Management
	/**
	 * Tells the renderer that it is highlighted
	 * @return true if highlighted
	 */
	public boolean isHighlighted() {
		return false;
	}
	
	// Tooltip Managment
	
	public String getToolTipText() {
		String s = "ToolTip for node "+this.toString();
		return s;
	}
	
}

/* 
 * $Log: STreeNode.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.14  2004/09/14 10:17:07  carlito
 * FileBrowser updated for macs
 *
 * Revision 1.13  2004/07/12 09:38:27  carlito
 * STree passed to ArrayList
 *
 * Revision 1.12  2004/07/08 12:03:20  kaspar
 *  * Documentation changes
 *
 */