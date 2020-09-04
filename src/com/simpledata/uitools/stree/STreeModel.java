/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.uitools.stree;

import java.util.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * This class represent a TreeModel based on 
 * STreeNode structure
 * @see STreeNode
 */
public class STreeModel implements TreeModel {
	
	// Variable used for the implementation of TreeModel
	private Vector modelListeners = new Vector();
	private STreeNode root;
	
	public STreeModel(STreeNode tree) {
		this.root = tree;
	}
	
	
	
	/****************************************************************************/
	/***********        Implementation of TreeModel interface           *********/
	/****************************************************************************/
	/**
	* Adds a listener for the TreeModelEvent posted after the tree changes.
	*/
	public void addTreeModelListener( TreeModelListener listener ) {
		if ( listener != null && !modelListeners.contains( listener ) ) {
			modelListeners.addElement( listener );
		}
	}
	
	/**
	* Removes a listener previously added with addTreeModelListener.
	*/
	public void removeTreeModelListener( TreeModelListener listener ) {
		if ( listener != null ) {
			modelListeners.removeElement( listener );
		}
	}
	
	/**
	* Indicate a change in nodes content
	*/
	public void fireTreeNodesChanged( TreeModelEvent e ) {
		Enumeration listeners = modelListeners.elements();
		while ( listeners.hasMoreElements() ) {
			TreeModelListener listener = (TreeModelListener)listeners.nextElement();
			listener.treeNodesChanged( e );
		}
	}
	
	/**
	* Indicate that nodes have been inserted
	*/
	public void fireTreeNodesInserted( TreeModelEvent e ) {
		Enumeration listeners = modelListeners.elements();
		while ( listeners.hasMoreElements() ) {
			TreeModelListener listener = (TreeModelListener)listeners.nextElement();
			listener.treeNodesInserted( e );
		}
	}
	
	/**
	* Indicate that nodes have been deleted
	*/
	public void fireTreeNodesRemoved( TreeModelEvent e ) {
		Enumeration listeners = modelListeners.elements();
		while ( listeners.hasMoreElements() ) {
			TreeModelListener listener = (TreeModelListener)listeners.nextElement();
			listener.treeNodesRemoved( e );
		}
	}
	
	/**
	* Indicate a change in the tree structure
	*/
	public void fireTreeStructureChanged( TreeModelEvent e ) {
		Enumeration listeners = modelListeners.elements();
		while ( listeners.hasMoreElements() ) {
			TreeModelListener listener = (TreeModelListener)listeners.nextElement();
			listener.treeStructureChanged( e );
		}
	}
	
	
	
	/**
	* Returns the child of parent at index in the parent's child array.
	*/
	public Object getChild(Object parent, int index) {
		return ((STreeNode)parent).getChildAt(index);
	}

	
	/**
	* Returns the number of children of parent.
	*/
	public int getChildCount(Object parent) {
		return ((STreeNode)parent).getChildCount();
	}
	
	/**
	* Returns the index of child in parent.
	*/
	public int getIndexOfChild(Object parent, Object child) {
		return ((STreeNode)parent).getChildIndex((STreeNode)child);
	}

	/**
	* Returns the root of the tree.
	*/
	public Object getRoot() {
		return this.root;
	}

	/**
	* Returns true if node is a leaf.
	*/
	public boolean isLeaf(Object node) {
		STreeNode dstn = (STreeNode)node;
		return dstn.isLeaf();
		/*
		int i = dstn.getChildCount();
		if (i > 0) {
			return false;
		} else {
			return true;
		}
		*/
	}

	/**
	* Messaged when the user has altered the value for the item identified by path to newValue.
	*/
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Enter here code to apply in case of tree direct editing
	}

}

/**
 * $Log: STreeModel.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/07/12 17:18:34  carlito
 * Some cleaning in trees
 *
 * Revision 1.7  2004/07/08 12:03:20  kaspar
 *  * Documentation changes
 *
 * Revision 1.6  2004/01/21 10:54:48  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/17 06:04:30  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/13 16:08:20  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/01/13 16:03:31  carlito
 * *** empty log message ***
 *
 *
 */