/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.uitools.stree;


import java.awt.datatransfer.*;
import javax.swing.tree.TreePath;




/**
* This represents a TreePath (a node in a JTree) that can be transferred between a drag source and a drop target.
*/
public class CTransferableTreePath implements Transferable
{
	// The type of DnD object being dragged...
	public static final DataFlavor TREEPATH_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, "TreePath");

	private TreePath		_path;

	private DataFlavor[]	_flavors = 
							{
								TREEPATH_FLAVOR
							};
	
	/**
	* Constructs a transferrable tree path object for the specified path.
	*/
	public CTransferableTreePath(TreePath path)
	{
		_path = path;
	}
	
	// Transferable interface methods...
	public DataFlavor[] getTransferDataFlavors()
	{
		return _flavors;
	}
	
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return java.util.Arrays.asList(_flavors).contains(flavor);
	}
	
	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
	{
		if (flavor.isMimeTypeEqual(TREEPATH_FLAVOR.getMimeType())) // DataFlavor.javaJVMLocalObjectMimeType))
			return _path;
		throw new UnsupportedFlavorException(flavor);	
	}

	
}
	
