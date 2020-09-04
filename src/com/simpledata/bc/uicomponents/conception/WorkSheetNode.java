/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 14 f�vr. 2004
 * $Id: WorkSheetNode.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.uitools.stree.STreeNode;
import com.simpledata.util.CollectionsToolKit;

/**
 * Tree nodes used in WorkSheetTree
 * @see WorkSheetTree
 */
public class WorkSheetNode extends STreeNode {

	private WorkSheetPanel wsp = null;
	
	private WorkSheetNode parent = null;
	private ArrayList children = null;

	/**
	 * Constructor
	 * @param wsp WorkSheetPanel this node is linked to
	 */
	public WorkSheetNode(WorkSheetPanel wsp) {
		this.wsp = wsp;
		this.children = new ArrayList();
	}

	/**
	 * @see com.simpledata.uitools.stree.STreeNode#getParent()
	 */
	public STreeNode getParent() {
		return this.parent;
	}

	protected void setParent(WorkSheetNode dad) {
		this.parent = dad;
	}
	

	/**
	 * <B>STreeNode Interface</B><BR>
	 * return a Vector of STreeNode denoting the childrens of this node
	 */
	public final Vector/*<STreeNode>*/ getChildren() {
		return CollectionsToolKit.convertToVector(getChildrenAL());
	}
	
	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getChildrenAL()
	 */
	public ArrayList getChildrenAL() {
		return this.children;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getChildAt(int)
	 */
	public STreeNode getChildAt(int index) {
		STreeNode stn = null;
		if ((index >= 0) && (index < this.getChildCount())) {
			// We are in correct range
			stn = (STreeNode)this.getChildrenAL().get(index);
		}
		return stn;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getChildCount()
	 */
	public int getChildCount() {
		return this.getChildrenAL().size();
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getIndex()
	 */
	public int getIndex() {
		int res = -1;
		WorkSheetNode dad = (WorkSheetNode)this.getParent();
		if (dad != null) {
			res = dad.getIndexOf(this);
		}
		return res;
	}
	
	protected int getIndexOf(WorkSheetNode wsn) {
		return this.getChildrenAL().indexOf(wsn);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#addChildAt(com.simpledata.uitools.stree.STreeNode, int)
	 */
	public boolean addChildAt(STreeNode stn, int pos) {
		boolean res = false;
		if ((pos >= 0) && (pos <= this.getChildCount())) { 
			this.getChildrenAL().add( pos, stn);
			((WorkSheetNode)stn).setParent(this);
			res = true;
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#remove(com.simpledata.uitools.stree.STreeNode)
	 */
	public boolean remove(STreeNode stn) {
		return this.getChildrenAL().remove(stn);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#order(int)
	 */
	public boolean order(int newPos) {
		// Not implemented yet
		return false;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#move(com.simpledata.uitools.stree.STreeNode, int)
	 */
	public boolean move(STreeNode stn, int newPos) {
		// Not implemented yet
		return false;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#acceptDrag()
	 */
	public boolean acceptDrag() {
		// Not implemented yet
		return false;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#acceptDrop(com.simpledata.uitools.stree.STreeNode)
	 */
	public boolean acceptDrop(STreeNode arg0) {
		// Not implemented yet
		return false;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#isCheckable()
	 */
	public boolean isCheckable() {
		// Not implemented yet
		return false;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getCheckState()
	 */
	public int getCheckState() {
		// Not implemented yet
		return STreeNode.NOT_CHECKED;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#check()
	 */
	public void check() {
		// Not implemented yet
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	    if (this.wsp != null) {
	        return this.wsp.toString();
	    } 
	    return new String("PB : No WorSheetPanel attached to this node");
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#equals(com.simpledata.uitools.stree.STreeNode)
	 */
	public boolean equals(STreeNode stn) {
		return this.equals((WorkSheetNode)stn);
	}
	
	public boolean equals(WorkSheetNode wsn) {
		return (this == wsn);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#getPopupMenu()
	 */
	public JPopupMenu getPopupMenu() {
		JPopupMenu res = null;
		if (this.wsp != null) {
			res = this.wsp.getJPopupMenu();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.uitools.stree.STreeNode#doAction(java.lang.String)
	 */
	public void doAction(String arg0) {
		// Won't be implemented
	}
	
	public ImageIcon getIcon() {
		ImageIcon res = null;
		if (this.wsp != null) {
			res = wsp.getIcon();
		}
		return res;
	}
	
	/**
	 * Returns the icon to be displayed if the node
	 * is not a leaf and is expanded
	 */
	public ImageIcon getOpenedIcon() {
		return this.getIcon();
	}
	
	/**
	 * Returns the icon to be displayed if the node
	 * is not a leaf and is not expanded
	 */
	public ImageIcon getClosedIcon() {
		return this.getIcon();
	}
	
	/**
	 * Returns the icon to be displayed if the node
	 * is a leaf
	 */
	public ImageIcon getLeafIcon() {
		return this.getIcon();
	}
	
	/**
	 * Tells the tree if highlighted or not
	 */
	public boolean isHighlighted() {
		return wsp.isHighLighted();
	}
	
	/**
	 * @return the WorkSheet which this node is linked to
	 */
	public WorkSheetPanel getWorkSheetPanel() {
		return wsp;
	}
	
}
/*
 * $Log: WorkSheetNode.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.12  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.11  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.10  2004/05/22 18:33:22  perki
 * *** empty log message ***
 *
 * Revision 1.9  2004/05/18 15:11:25  perki
 * Better icons management
 *
 * Revision 1.8  2004/05/05 10:44:59  perki
 * tarif viewer is better now
 *
 * Revision 1.7  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.6  2004/02/18 16:57:29  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/02/18 11:00:57  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/02/17 18:03:17  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/02/17 11:39:24  carlito
 * *** empty log message ***
 *
 */
