/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.uitools;

import java.awt.datatransfer.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
/**
* Object used to constuct a Tree for CheckNodeTree<BR>
* @see CheckNodeTree
*/
public class CheckNode extends DefaultMutableTreeNode implements Serializable,  Transferable {
	protected int selectionMode;
	protected boolean isSelected;
	protected boolean isChecked;
	protected Icon icon;
	
	final public static DataFlavor INFO_FLAVOR =
	new DataFlavor(CheckNode.class, "Checkal Information");
	
	static DataFlavor flavors[] = {INFO_FLAVOR };
	
	/**
	* set allowsChildren to true if this node is a folder
	*/
	public CheckNode(Object userObject,  boolean allowsChildren) {
		this(userObject,allowsChildren,null,false);
	}
	/**
	* set allowsChildren to true if this node is a folder
	*/
	public CheckNode(Object userObject,  boolean allowsChildren, Icon icon) {
		this(userObject,allowsChildren,icon, false);
	}
	/**
	* set allowsChildren to true if this node is a folder
	*/
	public CheckNode(Object userObject, boolean allowsChildren ,  boolean isSelected) {
		this(userObject,allowsChildren,null, isSelected);
	}
	
	/**
	* set allowsChildren to true if this node is a folder
	*/
	public CheckNode(Object userObject, boolean allowsChildren , Icon icon, boolean isSelected) {
		super(userObject, allowsChildren);
		setSelected(isSelected);
		setChecked(isSelected);
		setSelectionMode(CheckNodeTree.SINGLE_SELECTION);
		setIcon(icon);
	}
	
	
	
	/**
	* return true is this node is Checked. Has no effect on SINGLE_SELECTION mode
	*/
	public boolean isChecked() {
		return this.isChecked;
	}
	
	/**
	* set the icon of this node. Has no effect on folders.
	*/
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	/**
	* do not use this method. Used by CheckNodeTree.
	*/
	public void setSelectionMode(int mode) {
		selectionMode = mode;
	}
	
	/**
	* Select this Node
	*/
	public void setSelected(boolean isSelected) {
		setSelected(isSelected,true);
	}
	
	/**
	* check this Node. Has no effect on SINGLE_SELECTION mode
	*/
	public void setChecked(boolean checked) {
		this.isChecked = checked;
	}
	
	/**
	* Select this Node
	*/
	public void setSelected(boolean isSelected, boolean doICheckIt) {
		if (this.isSelected && isSelected && doICheckIt) {
			
			isChecked = ! isChecked;
			propagateCheck(isChecked);
			
			// check or unckeck folders automatically
			autoSelectMyParent ();
		}
		this.isSelected = isSelected;
	}
	
	/**
	* Propagate selection in tree
	*/
	public void propagateCheck(boolean isChecked) {
		setChecked(isChecked);
		if ((selectionMode == CheckNodeTree.MULTIPLE_PROPAGATE) && (children != null)) {
			Enumeration en = children.elements();
			while (en.hasMoreElements()) {
				CheckNode node = (CheckNode)en.nextElement();
				node.propagateCheck(isChecked);
			}	
		}
	}
	
	public void autoSelectMyParent () {
		if ((selectionMode == CheckNodeTree.MULTIPLE_PROPAGATE) && (parent != null)) {
			Enumeration en = parent.children();
			boolean parentChecked= true;
			while (en.hasMoreElements()) {
				if (! ((CheckNode)en.nextElement()).isChecked()) 
					parentChecked = false;
			}
			((CheckNode) parent).setChecked(parentChecked);
			((CheckNode) parent).autoSelectMyParent();
		}
	}
	
	/**
	* Return true if this object is actually selected
	*/
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	* Return the appropriate icon for this object
	*/
	public Icon getIcon (boolean expanded) {
		if (isObject()) {
			if (icon != null) 
				return icon;
			return UIManager.getIcon("Tree.leafIcon");
		} 
		if (expanded) {
			return UIManager.getIcon("Tree.openIcon");
		} 
		return UIManager.getIcon("Tree.closedIcon");
	}
	
	/**
	* Return true if this Node contains an object (i.e. is not a folder)
	*/
	public boolean isObject() {
		return (! getAllowsChildren());
	}
	
	
	
	// --------- Transferable --------------
	
	public boolean isDataFlavorSupported(DataFlavor df) {
		return df.equals(INFO_FLAVOR);
	}
	
	/** implements Transferable interface */
	public Object getTransferData(DataFlavor df)
	throws UnsupportedFlavorException, IOException {
		if (df.equals(INFO_FLAVOR)) {
			return this;
		}
		throw new UnsupportedFlavorException(df);
	}
	
	/** implements Transferable interface */
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}
	
	public void add(CheckNode cn) {
		super.add(cn);
		cn.setSelectionMode(selectionMode);
	}
}

