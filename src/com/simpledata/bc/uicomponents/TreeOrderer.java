/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 21 juin 2004
 * $Id: TreeOrderer.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.uitools.*;
import com.simpledata.uitools.stree.*;
import com.simpledata.util.CollectionsToolKit;

/**
 * This class represents a small JInternalFrame
 * allowing to modify the ordering for trees 
 * within a given tarification
 */
public class TreeOrderer extends JInternalFrame {

    private JLabel explainLabel;
    private JScrollPane jScrollPane1;
    private SButton okButton;
    
    private STree tree;
    private TreeOrdererNode rootNode;
    private Tarification owner;
    
    private boolean canCheckTrees;
    protected boolean onlyVisual;
    
    /**
     * @param canCheck can set Visible true/false a tree
     * @param onlyVisual modifications happens only on Visual tree (SIMULATION)
     */
    public TreeOrderer(Tarification t, boolean canCheck,boolean onlyVisual) {
        super("Trees orderer", true, true, false, true);
        owner = t;

        this.onlyVisual = onlyVisual;
        canCheckTrees = canCheck;
        generateTree();
        initComponents();
//        Rectangle rec = (Rectangle)BC.getParameter(Params.PARAM_TREE_ORDERER_BOUNDS);
//        this.setSize(rec.getSize());        
    }
    
    /**
     * Construct a little tree containing all subTrees
     */
    private void generateTree() {
        rootNode = new TreeOrdererNode(this, null);
        
        // Create and attach all childrens...
        BCTree[] trees = owner.getMyTrees();
        for (int i = 0; i< trees.length ; i++) {
            TreeOrdererNode ton = new TreeOrdererNode(this, trees[i]);
            rootNode.addChild(ton);
        }
        
        this.tree = new STree(rootNode, canCheckTrees);
        this.tree.setRootVisible(false);
    }
    
    private void initComponents() {
        explainLabel = new JLabel();
        jScrollPane1 = new JScrollPane();
        okButton = new SButton();

        this.setDefaultCloseOperation(HIDE_ON_CLOSE);

        explainLabel.setText("<html>" +
        		"<h2>Tree ordering tool</h2>" +
        		"Drag the trees to the desired position<br>" +
        		"The first tree cannot be moved and must stay first" +
        		"</html>");
        getContentPane().add(explainLabel, BorderLayout.NORTH);

        // Adding the tree to the ScrollPane
        jScrollPane1.setViewportView(this.tree);
        
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);

        okButton.setText("Done");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doDefaultCloseAction();
            }
        });
        
        getContentPane().add(okButton, BorderLayout.SOUTH);
    }

//    /**
//     * UNUSED METHODS
//     */
//    public void internalFrameOpened(InternalFrameEvent e) { }
//    public void internalFrameClosed(InternalFrameEvent e) { }
//    public void internalFrameIconified(InternalFrameEvent e) { }
//    public void internalFrameDeiconified(InternalFrameEvent e) { }
//    public void internalFrameActivated(InternalFrameEvent e) { }
//    public void internalFrameDeactivated(InternalFrameEvent e) { }
//    	
//    /**
//     * Operations that we want to do whenever the window is closed
//     */
//    public void internalFrameClosing(InternalFrameEvent e) { 
//        // We want to save the new order in the Tarification
//        
//    }
    
    protected Tarification getTarification() {
        return this.owner;
    }
    
}

/**
 * This is a small tree structure allowing trees ordering
 */
class TreeOrdererNode extends STreeNode {
    
    private BCTree modelRepresentation;
    private TreeOrdererNode parent;
    private ArrayList children;
    private TreeOrderer owner;
    
 
    public TreeOrdererNode(TreeOrderer to, BCTree bct) {
        this.modelRepresentation = bct;
        this.owner = to;
        children = new ArrayList();
    }
    
    public STreeNode getParent() {
        return parent;
    }
    
    protected void setParent(TreeOrdererNode ton) {
        parent = ton;
    }

	/**
	 * <B>STreeNode Interface</B><BR>
	 * return a Vector of STreeNode denoting the childrens of this node
	 */
	public final Vector/*<STreeNode>*/ getChildren() {
		return CollectionsToolKit.convertToVector(getChildrenAL());
	}
	
    public ArrayList getChildrenAL() {
        return children;
    }

    public STreeNode getChildAt(int index) {
        if ((0 <= index) && (index < children.size())) {
            return (STreeNode) children.get(index);
        }
        return null;
    }

    public int getChildCount() {
        return children.size();
    }

    public int getIndex() {
        if (getParent() != null) {
            ArrayList v = ((TreeOrdererNode)getParent()).getChildrenAL();
            return v.indexOf(this);
        } 
        return -1;
    }

    public boolean addChildAt(STreeNode child, int index) {
        int exist = this.getChildIndex(child);
        if (exist < 0) {
            // this node is not allready attached
            
            // We check index validity
            if ((0 <= index) && (index <= children.size())) {
                children.add( index, child);
                ((TreeOrdererNode) child).setParent(this);
                return true;
            }
        } 
        return false;
    }

    public boolean remove(STreeNode node) {
        if (children.contains(node)) {
            ((TreeOrdererNode) node).setParent(null);
            return this.children.remove(node);
        }
        return false;
    }

    public boolean order(int newPosition) {
		boolean success = false;
		if (getParent() != null) {
		    success = this.move(this.getParent(), newPosition);
		}
		return success;
    }

    public boolean move(STreeNode destination, int position) {
		boolean success = false;
		STreeNode oldParent = this.getParent();
		if ((oldParent == null)) { 
		    return false;
		}
		
		int oldPos = this.getIndex();
		
		if ((position < 0) || (position >= getParent().getChildCount())) {
		    // We are trying to place a tree
		    // outside the range
		    return false;
		}
		
		int newPos = position;
		if (oldParent.equals(destination)) {
			// Ordering situation			
			this.owner.getTarification().treesReorder(
					this.modelRepresentation, position,owner.onlyVisual);
			
			if (newPos > oldPos) {
				// We must insert a left shift to compensate
				// node removal
				newPos--;
			}
			oldParent.remove(this);
			oldParent.addChildAt(this, newPos);
			success = true;
		} else {
			// Moving situation (prohibited)
			success = false;
		}
		return success;
    }

    public boolean acceptDrag() {
        if ((getParent() != null)) {
            return true;
        }
        return false;
    }

    public boolean acceptDrop(STreeNode droppedNode) {
        if (!this.equals(droppedNode)) {
            if (getParent() == null) {
                // Only root accepts drops
                return true;
            }
        }
        return false;
    }

    public boolean isCheckable() {
        // if root or first node return false
    	
       if ((getParent() == null)) {
            return false;
        }
        return this.modelRepresentation.canChangeVisibility();
    }

    public int getCheckState() {
        if (this.modelRepresentation != null) {            
            boolean b = this.modelRepresentation.isVisible();
            if (!b) {
                return STreeNode.NOT_CHECKED;
            }
        }
        return STreeNode.FULLY_CHECKED;
    }

    public void check() {
        if (! isCheckable()) return;
        boolean b = this.modelRepresentation.isVisible();
        this.modelRepresentation.setVisible(!b);
    }

    public String toString() {
        if (modelRepresentation == null) {
            // root node
            return "";
        }
        return modelRepresentation.getTitle();
    }

    public boolean equals(STreeNode dstn) {
        return (this == dstn);
    }

    public JPopupMenu getPopupMenu() {
        // We do not need JPopupMenu on this tree
        	return null;
    }

    public void doAction(String key) { }
    
	public ImageIcon getIcon() {
		if (modelRepresentation != null) {
		    return TreeIconManager.getIcon(modelRepresentation.getType(), true, true);
		    
		}
		return null;
	}
	
	public ImageIcon getCheckIcon() {
		//Tarif tarif= tet.getSelectedTarif();
		int tag= CheckBox.TAG_NONE;
		int checkableCode=
			isCheckable() ? CheckBox.CHECKABLE : CheckBox.CHECKABLE_NOT;
		int checkState =
		    (getCheckState() == STreeNode.FULLY_CHECKED) ? CheckBox.CHECKSTATE_CHECKED : CheckBox.CHECKSTATE_CHECKED_NOT; 
		
		return CheckBox.get(tag,checkableCode, checkState);
	}
    
}

/*
 * $Log: TreeOrderer.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:37  perki
 * First commit on sourceforge
 *
 * Revision 1.9  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.8  2004/11/10 17:35:46  perki
 * Tree ordering is now correct
 *
 * Revision 1.7  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.5  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/06/28 13:13:52  carlito
 * Tree Orderer verbose for debug on windows...
 *
 * Revision 1.3  2004/06/25 08:30:55  perki
 * oordering in tree modified
 *
 * Revision 1.2  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.1  2004/06/22 11:07:31  carlito
 * Tree orderer v0.1
 *
 */