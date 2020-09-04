/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.uitools.stree;


import java.util.ArrayList;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

/**
* DefaultSTreeNode is a simple arborescent data structure<br>
* for STree (@see STree) 
*/
public class DefaultSTreeNode extends STreeNode {
	
	private static final Logger m_log = Logger.getLogger( DefaultSTreeNode.class );
	
	private STreeNode parent;
//	private Vector children;
	private ArrayList children;
	private String nodeTitle = "";
	// Gives a functionality similar to DefaultMutableTreeNode
	private Object userObject = null;
	private boolean checkStatus = false;

	
	/**
	* CONSTRUCTOR
	* new Node with title name
	*/	
	public DefaultSTreeNode(String name) {
		this();
		this.nodeTitle = name;
	}
	
	/**
	* CONSTRUCTOR
	* new node without name
	**/
	public DefaultSTreeNode() {
		this.parent = null;
		//this.children = new Vector();
		this.children = new ArrayList();
	}
	
	/**
	* Returns a Vector of node's children
	*/	
	public ArrayList getChildren() {
		return this.children;
	}
	
	/**
	* Returns child at specified index
	* if no child return null
	*/
	public STreeNode getChildAt(int index) {
		STreeNode res = null;
		if ((-1 < index) && (index < this.getChildCount())) {
			// Index is within range
			res = (STreeNode)this.children.get(index);
		}
		return res;
	}
	
	/**
	* Returns the number of children attached to this node
	*/	
    public int getChildCount() {
		return this.children.size();
	}
	
	/**
	* Returns the index of this within its parent's children
	*/	
	public int getIndex() {
		DefaultSTreeNode dad = (DefaultSTreeNode)this.getParent();
		if (dad == null) {
			return 0;
		} 
		return dad.getChildren().indexOf(this);
	}
	
	/**
	* Adds a child at a specified index
	*/	
	public boolean addChildAt(STreeNode child, int index) {
		// Check if index is correct
		if ( (index < 0) || (index > this.children.size()) ) {
			return false;
		}
		this.children.add(index, child);
		((DefaultSTreeNode)child).setParent(this);
		return true;
	}
	
	/**
	* Internal use only
	* sets the parent of the node
	*/	
	protected void setParent(DefaultSTreeNode newParent) {
		this.parent = newParent;
	}
	
	/**
	* Returns node's parent
	*/	
	public STreeNode getParent() {
		return this.parent;
	}

	/**
	* Removes child from children list
	*/
    public boolean remove(STreeNode node) {
		this.children.remove(node);
		DefaultSTreeNode dstn = (DefaultSTreeNode)node;
		dstn.setParent(null);
		return true;
	}
	
	/**
	* Move the node to newPosition within its
	* parent children
	* return true in case of success
	*/
	public boolean order(int newPosition) {
		boolean success = false;
		if (this.getParent() != null) {
		    success = this.move(this.getParent(), newPosition);
		}
		return success;
	}

	/**
	* Detach this node from its parent
	* and attach it to node : destination at index : position
	* return true in case of success
	*/
	public boolean move(STreeNode destination, int position) {
		boolean success = false;
		STreeNode oldParent = this.getParent();
		int newPos = position;
		if (oldParent.equals(destination)) {
			// Ordering situation
			int oldPos =  this.getIndex();
			if (newPos == oldPos) {
				return false;
			}
			if (newPos > oldPos) {
				// We must insert a left shift to compensate
				// node removal
				newPos--;
			}
			oldParent.remove(this);
			oldParent.addChildAt(this, newPos);
			success = true;
		} else {
			// Moving situation
			oldParent.remove(this);
			destination.addChildAt(this, newPos);
			success = true;
		}
		return success;
	}
	
	
	/**
	* Set a user object attached to this node 
	*/
	public void setUserObject(Object newUserObject) {
		this.userObject = newUserObject;
	}
	
	/**
	* Get the user object attached to this node
	*/
	public Object getUserObject() {
		return this.userObject;
	}
	
	
	/**************************************************************************/
	/******************* DRAG n DROP MANAGEMENT              ******************/
	/**************************************************************************/
	
	/**
	* Determines if the node is allowed to be dragged
	**/
	public boolean acceptDrag() {
		boolean res = true;
		return res;
	}
	
	/**
	* Determines if the node would accept the droppedNode as child 
	*
	**/
	public boolean acceptDrop(STreeNode droppedNode) {
		boolean res = true;
		// For the default node we consider a simple rule :
		// always accept drop while droppedNode is not an ancestor
		if ( droppedNode.isAncestorOf(this) ) {
			res = false;
		}
		return res;
	}
	
	
	/**************************************************************************/
	/******************* CHECKABLE tree MANAGEMENT           ******************/
	/**************************************************************************/

	/**
	* If false is returned then the chekbox will be disabled
	*
	**/
	public boolean isCheckable() {
		return true;
	}
	
	/**
	* Returns the status of the node
	* NOT_CHECKED      : not checked if leaf
	*                  : no child checked if not leaf
	* PARTIALY_CHECKED : the node is not a leaf and some of his children
	*                  : are not checked
	* FULLY__CHECKED   : leaf checked, or non leaf with all children checked
	**/
	public int getCheckState() {
	    if (this.isLeaf()) {
	        if (this.checkStatus) {
	            return FULLY_CHECKED;
	        } 
	        return NOT_CHECKED;
	    } 
	    
	    int checkedChildren = 0;
	    int partiallyCheckedChildren = 0;
	    for (int i = 0; i < this.getChildCount(); i++) {
	        int state =  this.getChildAt(i).getCheckState();
	        if (state == FULLY_CHECKED) {
	            checkedChildren++;
	        }
	        if (state == PARTIALLY_CHECKED) {
	            partiallyCheckedChildren++;
	        }
	    }
	    if (checkedChildren == this.getChildCount()) {
	        return FULLY_CHECKED;
	    } else if ((checkedChildren + partiallyCheckedChildren) > 0) {		
	        return PARTIALLY_CHECKED;
	    } 
	    return NOT_CHECKED;
	}
	
	/**
	* Check or uncheck this node and all his subnodes
	* returns true in case of success
	**/
	public void check() {
		if (this.isLeaf()) {
			this.checkStatus = !this.checkStatus;
		} else {
			// Check all children
			int status = this.getCheckState();
			if ((status == NOT_CHECKED) || (status == PARTIALLY_CHECKED)) {
				// Check all children
				for (int i = 0; i < this.getChildCount() ; i++) {
					STreeNode child = this.getChildAt(i);
					if (!(child.getCheckState() == FULLY_CHECKED)) {
						child.check();
					}
				}
				this.checkStatus = true;
			} else {
				// Uncheck all
				for (int i = 0; i < this.getChildCount() ; i++) {
					STreeNode child = this.getChildAt(i);
					child.check(); // this will uncheck
				}
				this.checkStatus = false;
			}
		}
	}
	
	/**
	* Returns nodeTitle
	*/
	public String toString() {
		return this.nodeTitle;
	}
	
	/**
	* Test equality with another node
	*/
	public boolean equals(STreeNode dstn) {
		return (this == dstn);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getPopupMenu()
	 */
	public JPopupMenu getPopupMenu() {
		JPopupMenu jpm = STree.getPopup(this, new String[] {"Nouveau","Effacer"}, new String[] {"new","delete"});
		return jpm;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#doAction(java.lang.String)
	 */
	public void doAction(String key) {
		if (key.equals("delete")) {
			m_log.info( this+" was asked for a delete" );
		} else if (key.equals("new")) {
			m_log.info( this+" was asked for new child" );
		} else {
			m_log.info( "Key '"+key+"' is not recognized" );
		}
	}
	
}

/*
* $Log: DefaultSTreeNode.java,v $
* Revision 1.2  2007/04/02 17:04:27  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:42  perki
* First commit on sourceforge
*
* Revision 1.20  2004/09/14 10:17:07  carlito
* FileBrowser updated for macs
*
* Revision 1.19  2004/09/04 18:10:15  kaspar
* ! Log.out -> log4j, last part
*
* Revision 1.18  2004/07/12 17:18:34  carlito
* Some cleaning in trees
*
* Revision 1.17  2004/07/12 09:38:27  carlito
* STree passed to ArrayList
*
* Revision 1.16  2004/06/28 13:16:06  carlito
* *** empty log message ***
*
* Revision 1.15  2004/06/16 10:12:28  carlito
* *** empty log message ***
*
* Revision 1.14  2004/04/09 07:16:37  perki
* Lot of cleaning
*
* Revision 1.13  2004/02/02 16:51:58  carlito
* changed isParentOf to isAncestorOf
*
* Revision 1.12  2004/01/30 15:22:30  carlito
* Popups and checks functional
*
* Revision 1.11  2004/01/29 13:03:05  carlito
* warnings and imports corrected ...
*
* Revision 1.10  2004/01/23 14:01:41  carlito
* *** empty log message ***
*
* Revision 1.9  2004/01/23 13:55:43  carlito
* *** empty log message ***
*
* Revision 1.8  2004/01/21 12:57:49  carlito
* STreeNode abstract between STree and DefaultSTreeNode
*
* Revision 1.7  2004/01/21 10:54:48  perki
* *** empty log message ***
*
* Revision 1.6  2004/01/19 18:01:48  carlito
* Stree has better drop detection
* new test image for windows forbidden1_16x16.gif
*
* Revision 1.5  2004/01/17 06:04:30  carlito
* *** empty log message ***
*
* Revision 1.4  2004/01/13 20:16:51  carlito
* Difficile de creer une interface sans impliquer toute une srie de casts...
*
* Revision 1.3  2004/01/13 16:03:31  carlito
* *** empty log message ***
*
* Revision 1.2  2004/01/09 16:51:02  carlito
* *** empty log message ***
*
* Revision 1.1  2004/01/09 14:53:59  carlito
* *** empty log message ***
*
*
*/