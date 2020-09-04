/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: MergingMonitorUISourceTree.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;


import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.compact.CompactTreeItem;
import com.simpledata.bc.uitools.SButtonIcon;
import com.simpledata.uitools.stree.STree;
import com.simpledata.util.CollectionsToolKit;

/**
 * The left Tree of MergingMonitorUI that shows up the source nodes
 */
public class MergingMonitorUISourceTree
	extends JPanel
	implements CompactNode.CNInterface {
	private CompactNode myRoot;

	public STree stree;
	
	private Tarif selectedTarif;
	
	private final Tarification tarification;

	/**
	 * Constructor 
	 */
	public MergingMonitorUISourceTree(Tarification t) {
	    tarification = t;
		// create the Compact Explorer
		stree= new STree(null);
		//		optimisation
		stree.setLargeModel(true);
		stree.setRowHeight(17);

		stree.setCheckVisible(false);
		

		setLayout(new BorderLayout(0, 0));

		SButtonIcon expand= new SButtonIcon(Resources.iconExpand);
		expand.setPreferredSize(new Dimension(20, 20));
		expand.setToolTipText(Lang.translate("Expand"));
		expand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stree.expandAll();
			}
		});

		SButtonIcon collapse= new SButtonIcon(Resources.iconCollapse);
		collapse.setPreferredSize(new Dimension(20, 20));
		collapse.setToolTipText(Lang.translate("Collapse"));
		collapse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stree.collapseAll();
			}
		});

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(expand);
		buttons.add(collapse);
		buttons.add(
		        new JLabel(
		                tarification.getTitle(),
		                tarification.getHeader().getIcon(),JLabel.LEFT));
		
		add(buttons, BorderLayout.NORTH);

		add(new JScrollPane(stree), BorderLayout.CENTER);

		stree.expandAll();
		
	}
	
	/** return the Tarif from this TreePath **/
	public static Tarif getTarifFromTreePath(TreePath tp) {
		if (tp == null) return null;
        if (tp.getLastPathComponent() != null) {
            CompactNode cn= (CompactNode) tp.getLastPathComponent();
            WorkSheet ws = cn.contentsGetWorkSheet();
            if (ws != null) {
                return ws.getTarif();
            }
        }
		return null;
	}
	
	/** forward addTreeSlectionListener to my Stree **/
	public void addTreeSelectionListener(TreeSelectionListener tsl) {
		stree.addTreeSelectionListener(tsl);
	}
	
	/** change the tarifs in the tree **/
	public void setTarifs(Tarif[] t) {
		
		if (t == null || t.length == 0) {
			setTarifs((ArrayList) null);
			return;
		}
		setTarifs(CollectionsToolKit.getArrayList(t));
	}
	
	/** change the tarifs in the tree **/
	public void setTarifs(ArrayList/*<Tarif>*/ tarifs) {
		
		if (tarifs == null || tarifs.size() == 0) {
			myRoot = null;
			//tarification = null;
			
		} else {
			//tarification = ((Tarif)tarifs.get(0)).getTarification();
			
			myRoot=
				CompactNode.prodGetTreeForTarifs
						(tarifs,tarification.getMyTrees(),this);
		}
		
		
		stree.changeRoot(myRoot);
		setSelectedTarif(null);
	}

	/**
	 * new TarifMatch Selection
	 */
	public void setSelectedTarif(Tarif tm) {
		selectedTarif = tm;
		if (tm != null && myRoot != null) {
			// get node containing this Tarif
			Iterator/*<CompactNode>*/ i = 
				myRoot.contentsGetNodesWithValue(tm).iterator();
			CompactNode cn;
			while (i.hasNext()) {
				cn = (CompactNode) i.next();
				stree.selectNode(cn);
				stree.scrollPathToVisible(stree.getPath(cn));
			}
		}
		stree.repaint();
	}
	
	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#isCompactNodeHighLighted()
	 */
	public boolean isCompactNodeHighLighted(CompactNode cn) {
		if (selectedTarif == null) return false;
		return (cn.contentsHasValue(selectedTarif));
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#getCompactNodeCheckState()
	 */
	public int getCompactNodeCheckState(CompactNode cn) {
		return CHECKED_NOT;
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#getCompactNodeCheckIcon(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public ImageIcon getCompactNodeCheckIcon(CompactNode cn) {
		return null;
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#isCompactNodeCheckable(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public boolean isCompactNodeCheckable(CompactNode cn) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.CompactExplorerInterface#showTarifs()
	 */
	public boolean showTarifs() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.CompactExplorerInterface#showOthers()
	 */
	public boolean showOthers() {
		return true;
	}
	
	/** return the tarification we are working on */
	public Tarification getTarification() {
		return tarification; 
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.compact.CompactNode.CNInterface#showTarifsRefrences()
	 */
	public boolean showTarifsRefrences() {
		return false;
	}
	/**
	 * @see com.simpledata.bc.uicomponents.compact.CompactNode.CNInterface#showRootNodes()
	 */
	public boolean showRootNodes() {
		return false;
	}

	/**
	 * @see com.simpledata.bc.uicomponents.CompactExplorerInterface#checkCompactNode(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public void checkCompactNode(CompactNode cn) {
		
	}
	
	/** return true if should create new nodes when tarif has sevral mapping */
	public boolean createVirtualNode() {return false; }

	public void setExpanded(CompactTreeItem cn, boolean state) {}
	
	/**
	 * @see com.simpledata.bc.uicomponents.compact.CompactNode.CNInterface#fireTreeNodesChanged(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public void fireTreeNodesChanged(CompactNode cn) {
		
	}
	
	/** 
	 * get the ToolTip for the CompactNode<BR>
	 *  @return null if none
	 */
	public String getToolTip(CompactNode cn) {return null;}
	
	/**
	 * tell the tree to refresh it's structure
	 */
	public void refreshStructure() {}
	
	/** 
	 * When calculating discount info column (for the StreeTable)<BR>
	 * return true to display the discount, false to display the 
	 * undiscounted column
	 */
	public boolean discountOrUndisc() {
	    // DO NOT CARE
	    return false;
	}
	
}

/*
 * $Log: MergingMonitorUISourceTree.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.10  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.9  2004/09/28 17:19:59  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.7  2004/07/30 07:07:23  perki
 * Moving Compact Tree from uicomponents to uicomponents.compact
 *
 * Revision 1.6  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 * Revision 1.5  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.4  2004/07/19 12:25:03  perki
 * Merging finished?
 *
 * Revision 1.3  2004/07/12 17:34:31  perki
 * Mid commiting for new matching system
 *
 * Revision 1.2  2004/07/09 20:53:31  perki
 * Merging UI step 1.5
 *
 * Revision 1.1  2004/07/09 20:25:02  perki
 * Merging UI step 1
 *
 */