/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: TarificationExplorerTree.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/

package com.simpledata.bc.uicomponents;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.TreeCellEditor;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcnode.BCNodeEditor;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.uitools.stree.STree;

/**
 * handles an image of BCTree for UI manipulation
 */
public class TarificationExplorerTree extends STree {

	/***************************************************************************
	 * A map containing for each BCNode it's corresponding
	 * TarificationExplorerNode
	 **************************************************************************/
	HashMap tenMap= null;

	/** the BCTree it maps * */
	protected BCTree tree;

	/** my cell Editor * */
	protected TreeCellEditor cellEditor;

	/** the actually selected tarif * */
	private Tarif selectedTarif;

	/** keep the mainpulable state **/
	private boolean manipulable;

	/** keep the canRemapTarif state **/
	private boolean canRemapTarif;
	
	private ExtraPopup extraPopup;
	
	/** 
	 * Constructor 
	 * @param manipulable set to true, if you need renaming, moving etcc...
	 * @param canRemapTarif set to true if tarif can be remaped from this tree
	 * @param tetep to add actions to the JPopupMenu (can be set to null)
	 * * */
	public TarificationExplorerTree(
		BCTree tree,
		boolean manipulable,
		boolean canRemapTarif,
		TarificationExplorerTree.ExtraPopup tetep) {
			
		super();
		super.setCheckVisible(false); // (empty) without checks
		tenMap= new HashMap();
		this.tree= tree;
		//this.editorListener = new EditorListener(this);
		//super.addMouseListener(this.editorListener);
		this.cellEditor= new MyCellEditor();
		super.changeRoot(getRootTEN()); // with
		super.setEditable(true);
		super.setCellEditor(cellEditor);
		this.selectedTarif= null;
		this.manipulable= manipulable;
		this.canRemapTarif= canRemapTarif;
		this.extraPopup = tetep;
	}

	/** get for a BCNode it's corresponding TarificationExplorerNode * */
	public TarificationExplorerNode getTEN(BCNode bcnode) {
		// Is this node in the tree?
		if (!tree.containsNode(bcnode))
			return null;

		// Is it another StreeNode ?
		if (!tenMap.containsKey(bcnode)) {
			tenMap.put(bcnode, new TarificationExplorerNode(this, bcnode));
		}
		return (TarificationExplorerNode) tenMap.get(bcnode);
	}

	/** get root TarificationExplorerNode * */
	public TarificationExplorerNode getRootTEN() {
		return getTEN(tree.getRoot());
	}
	
	/** get the BCTRee mapped * */
	public BCTree getTree() {
		return tree;
	}

	/***************************************************************************
	 * (tool) convert a ArrayList of BCNode to a ArrayList of
	 * TarificationExplorerNode
	 **************************************************************************/
	public ArrayList convertToTEN(ArrayList bcnodes) {
		ArrayList res= new ArrayList();
		for (int i= 0; i < bcnodes.size(); i++)
			res.add(getTEN((BCNode) bcnodes.get(i)));
		return res;
	}

	/**
	 * @return true if this tree is manipulable
	 */
	public boolean isManipulable() {
		return manipulable;
	}

	/**
	 * @return true if the nodes can remap tarifs
	 */
	public boolean canRemapTarif() {
		return canRemapTarif;
	}
	
	/**
	 * @return the extra popup to display (can be null)
	 */
	public ExtraPopup getExtraPopup() {
		return extraPopup;
	}

	/**
	 * set the actually selected Tarif
	 */
	public void setSelectedTarif(Tarif t) {
		if (this.selectedTarif != t) {
			this.selectedTarif= t;
			if (t == null) {
				super.setCheckVisible(false); // without checks
			} else {
				super.setCheckVisible(true); // with checks

				// open checked nodes
				ArrayList v= t.getMyMapping(tree);
				Iterator e= v.iterator();
				BCNode temp = null;
				while (e.hasNext()) {
					temp = (BCNode) e.next();
					if (! temp.isRoot())
						super.expandNode((getTEN(temp)).getParent());
				}

			}
			//		refresh the stree
			super.fireRefresh();
		}
	}

	/**
	 * get the actually selected Tarif
	 */
	public Tarif getSelectedTarif() {
		return this.selectedTarif;
	}

	/**
	 * Open an editor for node ten
	 * 
	 * @param ten
	 */
	public void launchNodeEdition(final TarificationExplorerNode ten) {
		if (!isManipulable())
			return;

		Rectangle rec= this.getNodeBounds(ten);
		//Point delta = new Point(0,0);
		Point delta= rec.getLocation();
		//ModalJPanel mjp =
		ModalJPanel.createSimpleModalJInternalFrame(
			BCNodeEditor.getPropertiesEditor(ten.getBCNode()),
			this,
			delta,
			true,
			Resources.iconEdit,
			Resources.modalBgColor);

	}
	
	/** extends this interface if you need to add an extra popup**/
	public interface ExtraPopup {
		/** modify the popup launched over this TEN **/
		public void modifyMe(JPopupMenu jpm,TarificationExplorerNode ten);
	}
}

class MyCellEditor extends AbstractCellEditor implements TreeCellEditor {

	private TarificationExplorerNode currentEditedNode= null;
	private int clickCountToStart= 2;
	private JLabel editLabel;

	public MyCellEditor() {
		editLabel= new JLabel();
		editLabel.setForeground(new Color(153, 0, 0));
	}

	/**
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
	 */
	public Component getTreeCellEditorComponent(
		JTree tree,
		Object value,
		boolean isSelected,
		boolean expanded,
		boolean leaf,
		int row) {
		if (value != null) {
			TarificationExplorerNode ten= (TarificationExplorerNode) value;
			//this.owner.launchNodeEdition(ten);

			Rectangle rec= ten.getTETree().getNodeBounds(ten);
			//Point delta = new Point(0,0);
			Point delta= rec.getLocation();
			ModalJPanel mjp=
				ModalJPanel.createSimpleModalJInternalFrame(
					BCNodeEditor.getPropertiesEditor(ten.getBCNode()),
					ten.getTETree(),
					delta,
					true,
					Resources.iconEdit,
					Resources.modalBgColor);

			mjp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//Log.out("Window has been closed");
					//ten.getTETree().fireTreeStructureChanged(ten);
					fireEditingStopped();
				}
			});
		}
		editLabel.setText(Lang.translate("Edition in progress"));
		return editLabel;
	}

	public void fireEditingStopped() {
		this.currentEditedNode= null;
		super.fireEditingStopped();
	}

	/**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return this.currentEditedNode;
	}

	/**
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			return ((MouseEvent) anEvent).getClickCount() >= clickCountToStart;
		}
		return true;
	}
	

}

/*
 * $Log: TarificationExplorerTree.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:37  perki
 * First commit on sourceforge
 *
 * Revision 1.38  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.37  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.36  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.35  2004/03/18 18:51:52  perki
 * barbapapa
 *
 * Revision 1.34  2004/03/18 09:02:29  perki
 * *** empty log message ***
 *
 * Revision 1.33  2004/03/08 09:56:36  perki
 * houba houba hop
 *
 * Revision 1.32  2004/03/06 14:24:50  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.31  2004/03/06 11:49:22  perki
 * *** empty log message ***
 *
 * Revision 1.30  2004/03/04 17:32:55  carlito
 * *** empty log message ***
 *
 * Revision 1.29  2004/03/04 17:31:23  carlito
 * *** empty log message ***
 *
 * Revision 1.28  2004/03/03 11:48:18  carlito
 * *** empty log message ***
 *
 * Revision 1.27  2004/03/02 00:32:54  carlito
 * *** empty log message ***
 *
 * Revision 1.26  2004/02/24 13:33:48  carlito
 * *** empty log message ***
 *
 * Revision 1.25  2004/02/24 10:35:19  perki
 * *** empty log message ***
 *
 * Revision 1.24  2004/02/23 18:34:48  carlito
 * *** empty log message ***
 * Revision 1.23 2004/02/23 13:07:45
 * carlito *** empty log message ***
 * 
 * Revision 1.22 2004/02/23 12:42:40 carlito *** empty log message ***
 * 
 * Revision 1.21 2004/02/14 21:53:26 carlito *** empty log message ***
 * 
 * Revision 1.20 2004/02/06 15:07:44 perki New nodes
 * 
 * Revision 1.19 2004/02/05 09:58:11 perki Transactions are welcome aboard
 * 
 * Revision 1.18 2004/02/04 17:38:04 perki cleaning
 * 
 * Revision 1.17 2004/02/04 15:42:16 perki cleaning
 * 
 * Revision 1.16 2004/02/03 11:31:17 perki totally new double sided map
 * 
 * Revision 1.15 2004/02/02 18:19:15 perki yupeee3
 * 
 * Revision 1.14 2004/02/02 16:32:06 perki yupeee
 * 
 * Revision 1.13 2004/02/02 11:21:05 perki *** empty log message ***
 * 
 * Revision 1.12 2004/02/02 07:00:50 perki sevral code cleaning
 * 
 * Revision 1.11 2004/02/01 17:15:12 perki good day number 2.. lots of class
 * loading improvement
 * 
 * Revision 1.10 2004/02/01 11:13:51 perki nice job
 * 
 * Revision 1.9 2004/01/31 18:21:30 perki Wonderfull Day
 * 
 * Revision 1.8 2004/01/31 15:47:45 perki 16 heure 50
 * 
 * Revision 1.7 2004/01/31 15:46:50 perki 16 heure 49
 * 
 * Revision 1.6 2004/01/31 10:28:56 perki BCNode manipulation ok-- c'est de
 * la bombe
 * 
 * Revision 1.5 2004/01/23 17:30:59 perki *** empty log message ***
 * 
 * Revision 1.4 2004/01/23 14:08:56 perki *** empty log message ***
 * 
 * Revision 1.3 2004/01/20 11:05:23 perki Et la comete disparue dans l'espace
 * infini.. Fin
 * 
 * Revision 1.2 2004/01/10 08:11:44 perki UI addons and Look And Feel
 * 
 * Revision 1.1 2004/01/09 16:46:10 perki UI linking to DataModel Step 1
 *  
 */
