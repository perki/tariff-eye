/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: CompactExplorer.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.uicomponents.compact.CompactNode.CNInterface;
import com.simpledata.bc.uicomponents.filler.FillerData;
import com.simpledata.uitools.stree.STree;

/**
 * Compact Explorer Tree V2
 */
public class CompactExplorer 
	   implements CompactNode.CNInterface, NamedEventListener {
	
	// modes
	
	private CompactNode myRoot;
	private Tarification myTarification;
	private STree stree;
	boolean listenerDeaf= false;

	private boolean showTarifs;
	private boolean showOthers;
	private boolean autoShrinkView;
	
	private HashMap tarifPos;
	
	// For nice structure refresh
	// expanded objects memories
	private ArrayList expandedNodes;
	private ArrayList expandedValues;
	
	
	// A Data Filler memory for testing
	private FillerData fillerData;
	
	/** 
	 * Compact Explorer Tree V2 
	 */
	public CompactExplorer(Tarification t) {
		// Defaulting to creation mode
		this(t,true,true, true);
	}

	/**
	 * 
	 * @param showTarifs show Tarifs as nodes
	 * @param showOthers show some tarifs in "Others" nodes
	 * @param memorizeExpandedNodes simulation(false) or creation(true)
	 */
	public CompactExplorer(Tarification t,boolean showTarifs,
								boolean showOthers, 
								boolean memorizeExpandedNodes) {
		myTarification= t;
		this.showTarifs = showTarifs;
		this.showOthers = showOthers;
		tarifPos = new HashMap();
		
		expandedNodes = new ArrayList();
		expandedValues = new ArrayList();
		
		stree= new STree() {// enable deaf-ing (override fireing of events)
			protected void fireValueChanged(TreeSelectionEvent e) {
				if (listenerDeaf) { 
					return; // I'm deaf
				}
				super.fireValueChanged(e);
			}
		};
		// optimisation
		stree.setLargeModel(true);
		stree.setRowHeight(17);
		
		stree.setCheckVisible(false);
		
		stree.enableToolTips();
		
		this.myTarification.addNamedEventListener(this);
		
		if (memorizeExpandedNodes) {
			// We add an expansion listener to memorize expanded 
			// node for repeated refreshStructure
			stree.addTreeExpansionListener(
					new CompactTreeExpansionListener(this));
		}
		
		// initialize the Data Filler
		fillerData = new FillerData();
		
		refreshStructure();
	}
	
	
	/**
	 * Event management
	 * @param e
	 */
	public void eventOccured(NamedEvent e) {
		switch (e.getEventCode()) {
			case NamedEvent.TARIF_OPTION_LINK_ADDED :
			case NamedEvent.TARIF_OPTION_LINK_DROPED :
			case NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED :
				refreshStructure();
			break;
			
			case NamedEvent.WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE :
			    this.stree.repaint();
			break;
			
			case NamedEvent.TITLE_MODIFIED:
				if (e.getSource() instanceof BCNode) {
					// Should only receive event from BCNodes and 
					// of form title changed
					BCNode bcn = (BCNode)e.getSource();
					ArrayList v = this.getBCNodePos(bcn);
					for (int i=0;i<v.size();i++) {
						CompactNode cn = (CompactNode)v.get(i);
						this.stree.fireTreeNodesChanged(cn);
					}
				}
			break;
		}
	}
	
	/** return true if should show Tarifs */
	public boolean showTarifs() {return showTarifs; }
	
	/** return true if should show Tarifs */
	public boolean showOthers() {return showOthers; }
	
	/** return true if should create new nodes when tarif has sevral mapping */
	public boolean createVirtualNode() {return false; }
	
	/** return the tarification we are working on */
	public Tarification getTarification() {return myTarification; }
	
	
	/**
	 * @see com.simpledata.bc.uicomponents.compact.CompactNode.CNInterface#showTarifsRefrences()
	 */
	public boolean showTarifsRefrences() {
		return true;
	}
	
	/**
	 * @see CNInterface#showRootNodes()
	 */
	public boolean showRootNodes() {
		return false;
	}
	
	/**
	 * expand the tree
	 */
	public void expandTree() {
		stree.expandAll();
	}
	
	/**
	 * This methods reloads the structure from the datamodel when it has changed subsequently<br>
	 * Warning : rather costy operation
	 */
	public void refreshStructure() {
		tarifPos.clear();
		
		
		
		this.listenerDeaf = true;
		// Storing all expanded objects for future re-expand
		for (int i=0; i<this.expandedNodes.size(); i++) {
			CompactNode cn = (CompactNode)this.expandedNodes.get(i);
			Object[] ob = cn.contentsGet();
			if (ob.length > 0) {
				this.expandedValues.add(ob[0]);
			}
		}
		
		myRoot=
			CompactNode.prodGetTreeForTarifs(this);
		
		
		// tell the filler to refresh it's data too
		fillerData.reset(myRoot); 
		
		
		this.expandedNodes.clear();
		
		stree.changeRoot(myRoot);
		
		this.listenerDeaf = false;
		// We restore the correct expanded states
		doExpansion(this.myRoot);
		
		this.expandedValues.clear();
		
		
		//recalculate root node
		refreshCalculus();
		
	}
	
	/** recalculate evrything from the root node **/
	public void refreshCalculus() {
	    if (myRoot != null && myRoot instanceof CompactTarifManagerNode)
		    myTarification.comCalc().start(
		            (CompactTarifManagerNode) myRoot);
	}

	private void doExpansion(CompactNode cn) {
		if (cn == null) {
			return;
		}
		Object[] objs = cn.contentsGet();
		Object obj;
		if (objs.length > 0) {
			obj = objs[0];
			if (this.expandedValues.contains(obj)) {
				// We must expand this peculiar node
				this.getSTree().expandNode(cn);
			}
		}
		for (int i=0; i<cn.getChildCount(); i++) {
			doExpansion((CompactNode)(cn.getChildAt(i)));
		}
	}
	
	//------------------------- Stree manipulation -------------------//
	public STree getSTree() {
		return this.stree;
	}

	/**
	 * Return the root node of the Compact tree. 
	 * This can be used for traversal. 
	 */
	public CompactNode getTreeRoot() {
		return myRoot; 
	}
	//------------------------- Tarif selection ----------------------//
	
	/** return true if this node should be highlighted **/
	public boolean isCompactNodeHighLighted(CompactNode cn) {
		return cn.contentsHasValue(lastExpandedObject);
	}
	
	/** actually selected Object **/
	protected Object lastExpandedObject= null;

	public void setAutoShrinkView(boolean b) {
		this.autoShrinkView = b;
	}
	
	/**
	 * Expand the nodes with this object selected
	 * @param o object to be shown
	 */
	public void expandNodesWithObject(Object o) {
		lastExpandedObject= o;

		listenerDeaf= true; // shushh
		
		if (o != null) {
			//	unset actual selection ?
			this.stree.removeSelectionPath(this.stree.getSelectionPath());
			Iterator e= getNodesWithObjects(o).iterator();
			if (this.autoShrinkView) {
				// close tree
				this.stree.collapseAll();
			}	
			// expand node where this tarif exists
			while (e.hasNext())
				this.stree.expandNode((CompactNode) e.next());			
		}

		this.stree.fireRefresh();

		listenerDeaf= false; // speak
	}

	// ----------------- Indexes and Mapping

	/**
	 * Returns all CompactNodes mapping for BCNode bcn
	 * @param bcn 
	 * @return vector of CompactNodes where the bcn is represented, returns an empty ArrayList if not in tree
	 */
	public ArrayList getBCNodePos(BCNode bcn) {
		return myRoot.contentsGetNodesWithValue(bcn);
	}

	
	
	/** get the CompactNodes where this Object is located **/
	public ArrayList getNodesWithObjects(Object o) {
		return myRoot.contentsGetNodesWithValue(o);
	}

	/**
	 */
	public int getCompactNodeCheckState(CompactNode cn) {
		return CompactNode.CNInterface.CHECKED_NOT;
	}

	/**
	 */
	public boolean isCompactNodeCheckable(CompactNode cn) {
		return false;
	}

	/**
	 */
	public void checkCompactNode(CompactNode cn) { }

	/** 
	 * @see CompactNode.CNInterface#getCompactNodeCheckIcon(CompactNode)
	 */
	public ImageIcon getCompactNodeCheckIcon(CompactNode node) {
		return null;
	}


	/**
	 * CompactNode tells the explorer that their expanded state
	 */
	public void setExpanded(CompactTreeItem cn, boolean state) {
		if (state) {
			this.expandedNodes.add(cn);
			return;
		} 
		this.expandedNodes.remove(cn);
	}

	/**
	 * Fire a refresh event up to the STree
	 * @param cn Node that has changed. 
	 */
	public void fireTreeNodesChanged(CompactNode cn) {
		getSTree().fireTreeNodesChanged(cn);
	}
	
	/** 
	 * get the ToolTip for the CompactNode<BR>
	 *  @return null if none
	 */
	public String getToolTip(CompactNode cn) {
		String temp = 
			(fillerData == null) ? null : fillerData.getInfosStr(cn);
		
		if (temp == null) temp = "";
		
		if (cn instanceof CompactBCGroupNode) {
			if (! temp.equals("")) temp +="<HR>";
			temp += ((CompactBCGroupNode) cn).displayTreeStringHTML();
		}
		
		if (temp.equals("")) {
			return cn.displayTreeString();
		} 
		
		return "<HTML>"+temp+"</HTML>";
		
	}
	
	
	/**
	 * @return Returns the fillerData.
	 */
	public FillerData getFillerData() {
		return fillerData;
	}
	
	/** store the value of discountOrUndisc **/
	private boolean discountOrUndisc;
	
	/** 
	 * Change the value of discountOrUndisc()<BR>
	 * When calculating discount info column (for the StreeTable)<BR>
	 * set true to display the discount, false to display the 
	 * undiscounted column
	 */
	public void setDiscountOrUndisc(boolean discountOrUndisc) {
	   this.discountOrUndisc = discountOrUndisc;
	}
	
	/** 
	 * When calculating discount info column (for the StreeTable)<BR>
	 * return true to display the discount, false to display the 
	 * undiscounted column
	 */
	public boolean discountOrUndisc() {
	    return discountOrUndisc;
	}
}

class CompactTreeExpansionListener implements TreeExpansionListener {

	private CompactNode.CNInterface explorer;
	
	public CompactTreeExpansionListener(CompactNode.CNInterface owner) {
		super();
		this.explorer = owner;
	}
	
	public void treeExpanded(TreeExpansionEvent event) {
		treatEvent(event, true);
	}

	public void treeCollapsed(TreeExpansionEvent event) {
		treatEvent(event, false);
	}
	
	private void treatEvent(TreeExpansionEvent event, boolean expandedState) {
		TreePath tp = event.getPath();
		CompactNode cn =  null;
		if (tp != null) {
			cn = (CompactNode)(tp.getLastPathComponent());
		}
		if (cn != null) {
			if (cn.expandGet() != expandedState) {
				this.explorer.setExpanded(cn, expandedState);
				cn.expandSet(expandedState);
			}
		}
	}
	
	
	
}

/**
 *  $Log: CompactExplorer.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.14  2004/12/04 13:17:23  perki
 *  hacked discount bug .. anyway the whole system of display should be reviewed
 *
 *  Revision 1.13  2004/11/17 18:29:02  perki
 *  corrected bug #48
 *  dicounts where not visible on trees
 *
 *  Revision 1.12  2004/11/17 15:14:46  perki
 *  Discount DISPLAY RC1
 *
 *  Revision 1.11  2004/11/17 12:04:40  perki
 *  Discounts Step 2
 *
 *  Revision 1.10  2004/11/12 14:28:39  jvaucher
 *  New NamedEvent framework. New bugs ?
 *
 *  Revision 1.9  2004/11/05 16:32:32  perki
 *  Now calculations are done at loading of simulations
 *
 *  Revision 1.8  2004/10/15 06:38:59  perki
 *  Lot of cleaning in code (comments and todos
 *
 *  Revision 1.7  2004/10/14 16:39:08  perki
 *  *** empty log message ***
 *
 *  Revision 1.6  2004/09/22 06:47:05  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.5  2004/08/17 12:09:27  kaspar
 *  ! Refactor: Using interface instead of class as reference type
 *    where possible
 *
 *  Revision 1.4  2004/07/30 15:38:19  perki
 *  some changes
 *
 *  Revision 1.3  2004/07/30 11:28:39  perki
 *  Better tooltips
 *
 *  Revision 1.2  2004/07/30 05:58:15  perki
 *  Slpitted CompactNode.java in sevral files
 *
 *  Revision 1.1  2004/07/30 05:50:01  perki
 *  Moved all CompactTree classes from uicompnents to uicomponents.compact
 *
 *  Revision 1.56  2004/07/26 17:39:36  perki
 *  Filler is now home
 *
 *  Revision 1.55  2004/07/19 13:54:46  kaspar
 *  - refactoring: Moving Compact* nodes into public view for
 *    access in reporting
 *  - Removed useless reporting classes
 *  - Adding partly finished Linearizer Test
 *  - Accomodated for changements in how to do things
 *
 *  Revision 1.54  2004/07/15 12:00:54  kaspar
 *  * Report Generation Prototype included into HEAD
 *  * Generates a .dot file for debug output.
 *
 *  Revision 1.53  2004/07/08 14:59:00  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.51  2004/06/25 08:30:55  perki
 *  oordering in tree modified
 *
 *  Revision 1.50  2004/06/23 18:38:04  perki
 *  *** empty log message ***
 *
 *  Revision 1.49  2004/06/22 17:11:29  perki
 *  CompactNode now build from datamodel and added a notice interface to WorkSheet
 *
 *  Revision 1.48  2004/06/22 11:22:39  perki
 *  *** empty log message ***
 *
 *  Revision 1.47  2004/06/22 11:06:50  carlito
 *  Tree orderer v0.1
 *
 *  Revision 1.46  2004/06/22 10:56:20  perki
 *  Lot of cleaning in CompactNode part1
 *
 *  Revision 1.45  2004/06/22 08:59:05  perki
 *  Added CompactTree for CompactNode management and first sync with CompactExplorer
 *
 *  Revision 1.44  2004/05/31 12:40:22  perki
 *  *** empty log message ***
 *
 *  Revision 1.43  2004/04/12 17:34:52  perki
 *  *** empty log message ***
 *
 *  Revision 1.42  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.41  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.40  2004/03/24 14:33:46  perki
 *  Better Tarif Viewer no more null except
 *
 *  Revision 1.39  2004/03/18 18:08:59  perki
 *  barbapapa
 *
 *  Revision 1.38  2004/03/18 16:26:54  perki
 *  new option model
 *
 *  Revision 1.37  2004/03/18 15:34:11  carlito
 *  *** empty log message ***
 *
 *  Revision 1.36  2004/03/18 10:43:02  carlito
 *  *** empty log message ***
 *
 *  Revision 1.35  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.34  2004/03/15 15:46:56  carlito
 *  *** empty log message ***
 *
 *  Revision 1.33  2004/03/15 10:43:15  perki
 *  *** empty log message ***
 *
 *  Revision 1.32  2004/03/13 17:44:47  perki
 *  Ah ah ah aha ah ah aAAAAAAAAAAAAAA
 *
 *  Revision 1.31  2004/03/06 15:22:41  perki
 *  Tirelipapon sur le chiwawa
 *
 *  Revision 1.30  2004/03/03 10:47:47  carlito
 *  *** empty log message ***
 *
 *  Revision 1.29  2004/03/02 17:59:15  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.28  2004/03/02 16:28:27  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.27  2004/03/02 15:39:08  perki
 *  *** empty log message ***
 *
 *  Revision 1.26  2004/03/02 14:42:48  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.25  2004/03/02 00:32:54  carlito
 *  *** empty log message ***
 *
 *  Revision 1.24  2004/02/25 19:01:33  carlito
 *  *** empty log message ***
 *
 *  Revision 1.23  2004/02/25 15:34:26  perki
 *  *** empty log message ***
 *
 *  Revision 1.22  2004/02/25 15:15:22  perki
 *  *** empty log message ***
 *
 *  Revision 1.21  2004/02/25 13:21:14  perki
 *  *** empty log message ***
 *
 *  Revision 1.20  2004/02/25 11:08:02  perki
 *  *** empty log message ***
 *
 *  Revision 1.19  2004/02/25 09:54:43  perki
 *  *** empty log message ***
 *
 *  Revision 1.18  2004/02/25 08:11:58  perki
 *  nicer
 *
 *  Revision 1.1  2004/02/24 18:22:54  perki
 *  nicer
 *
*/