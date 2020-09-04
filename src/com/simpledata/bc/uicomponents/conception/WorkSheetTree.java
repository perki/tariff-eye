/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 14 févr. 2004
 * $Id: WorkSheetTree.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.uitools.stree.STree;

import org.apache.log4j.Logger;


/**
 * This class defines a simple WorkSheet explorer tree
 */
public class WorkSheetTree
	extends JScrollPane
	implements TreeSelectionListener {
		
	private static final Logger m_log = Logger.getLogger( WorkSheetTree.class ); 

	private TarifViewer tv;
	private STree wTree;
	private JPanel blankPanel;

	
	private WSTreeCellEditor editor= null;

	public WorkSheetTree(TarifViewer tv) {
		super();
		this.tv= tv;
		this.editor= new WSTreeCellEditor(this);
		initBlankPanel();

		// create the tree
		wTree= new STree(null, false);
		this.wTree.addTreeSelectionListener(this);
		this.wTree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.wTree.setCellEditor(this.editor);
		this.wTree.setEditable(true);
		this.setViewportView(this.wTree);

	}

	/** update the tree **/
	public void refresh() {
		wTree.changeRoot(getGraphicalNode(tv.getRootWorkSheetPanel()));
		wTree.fireTreeStructureChanged(wTree.getRoot());
		wTree.expandAll();
		repaint();
	}
	
	


	// Simple TreeSelectionListener for WorkSheetExplorer
	WorkSheetNode lastSelected= null;
	
	/**
	 */
	public void valueChanged(TreeSelectionEvent event) {
		if (event == null) {
			lastSelected= null;
			return;
		}
		TreePath tp= event.getPath();

		// Removing selection
		removeSelection();

		if (tp != null) {
			WorkSheetNode cn= (WorkSheetNode) tp.getLastPathComponent();
			if (cn == null) {
				this.lastSelected= cn;
				return;
			}
			if (cn.equals(lastSelected))
				return;

			lastSelected= cn;
			WorkSheetPanel wsp= cn.getWorkSheetPanel();

			if (wsp != null) {
				tv.setWorkSheetPanel(wsp);
			} else {
				m_log.error(
					"Node : '" + cn + "' had a null WorkSheetPanel");
			}
		}
	}

	private WorkSheetNode getGraphicalNode(WorkSheetPanel wsp) {
		WorkSheetNode res= null;
		if (wsp != null) {
			res= new WorkSheetNode(wsp);

			WorkSheetPanel[] wsps= wsp.getChildrenWorkSheetPanels();
			for (int i= 0; i < wsps.length; i++) {
				WorkSheetPanel currentWsp= wsps[i];
				if (currentWsp != null) {
					res.addChild(this.getGraphicalNode(currentWsp));
				}
			}
		}
		return res;
	}

	
  /**
   * Inits the member blankPanel to a JPanel and sets 
   * its display defaults. 
   */
  private void initBlankPanel() {
		blankPanel= new JPanel();
		blankPanel.setBackground(Color.WHITE);
	}

	public STree getSTree() {
		return this.wTree;
	}

	public void removeSelection() {
		TreePath tp= this.wTree.getSelectionPath();
		if (tp != null) {
			this.wTree.removeSelectionPath(tp);
		}
	}
}

class WSTreeCellEditor extends AbstractCellEditor implements TreeCellEditor {

	private WorkSheetTree owner= null;
	private WorkSheetNode currentEditedNode= null;
	private int clickCountToStart= 2;
	private JLabel editLabel;


	public WSTreeCellEditor(WorkSheetTree wst) {
		this.owner= wst;
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
			WorkSheetNode wsn= (WorkSheetNode) value;

			this.currentEditedNode= wsn;
			//this.owner.launchNodeEdition(ten);

			Rectangle rec= owner.getSTree().getNodeBounds(wsn);
			//Point delta = new Point(0,0);
			Point delta= rec.getLocation();

			ModalJPanel mjp= 
			this.currentEditedNode.getWorkSheetPanel().
				getNamedTitleDescriptionEditor(this.owner.getSTree(),
				delta);
				
					

			mjp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			});

		}
		editLabel.setText(Lang.translate("Edition in progress"));
		return editLabel;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return this.currentEditedNode;
	}

	public void fireEditingStopped() {
		super.fireEditingStopped();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		this.currentEditedNode= null;

		fireEditingCanceled();
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		if (this.currentEditedNode == null)
			return false;
		this.currentEditedNode= null;

		fireEditingStopped();
		return true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			MouseEvent me= (MouseEvent) anEvent;
			WorkSheetNode wsn=
				(WorkSheetNode)
					((STree) anEvent.getSource()).getClosestStreeNode(
					me.getPoint());
			if (wsn != null
				&& wsn.getWorkSheetPanel().getWorkSheet() == null) {
				return false;
			}
			return me.getClickCount() >= clickCountToStart;
		}
		return false;
	}
}


/*
 * $Log: WorkSheetTree.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.25  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.24  2004/07/08 12:02:32  kaspar
 * * Documentation changes, Added some debug code into
 *   the main view of the creator
 *
 * Revision 1.23  2004/05/14 14:20:19  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/05/14 07:52:53  perki
 * baby dispatcher is going nicer
 *
 * Revision 1.21  2004/05/05 10:44:59  perki
 * tarif viewer is better now
 *
 * Revision 1.20  2004/04/13 21:30:14  perki
 * *** empty log message ***
 *
 * Revision 1.19  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.18  2004/03/24 14:33:46  perki
 * Better Tarif Viewer no more null except
 *
 * Revision 1.17  2004/03/24 13:11:14  perki
 * Better Tarif Viewer no more null except
 *
 * Revision 1.16  2004/03/22 19:32:45  perki
 * step 1
 *
 * Revision 1.15  2004/03/22 18:59:02  perki
 * step 1
 *
 * Revision 1.14  2004/03/22 16:40:47  perki
 * step 1
 *
 * Revision 1.13  2004/03/08 09:02:20  perki
 * houba houba hop
 *
 * Revision 1.12  2004/03/06 11:49:22  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/03/03 15:04:18  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/03/03 11:39:49  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/03/03 10:47:47  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/03/02 16:47:38  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/02/25 17:36:54  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/02/24 13:33:48  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/02/18 16:57:29  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/02/17 18:03:17  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/02/17 11:39:24  carlito
 * *** empty log message ***
 *
 */