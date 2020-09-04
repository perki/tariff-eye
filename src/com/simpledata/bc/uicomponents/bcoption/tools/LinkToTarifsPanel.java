/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: LinkToTarifsPanel.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption.tools;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.tools.LinkToTarifs;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.compact.CompactTreeItem;
import com.simpledata.bc.uitools.SButtonIcon;
import com.simpledata.uitools.stree.STree;

/**
 * A UI for LinkToTarif Objects
 */
public class LinkToTarifsPanel
	extends JPanel
	implements CompactNode.CNInterface {
	private CompactNode myRoot;

	private LinkToTarifs ltt;

	public STree stree;
	
	
	private ArrayList tarifsChecked;
	private ArrayList tarifsCheckedPartially;
	private ArrayList tarifsAccepted ;
	
	private void load () {
		tarifsChecked = ltt.getLinkedTarifs();
		tarifsCheckedPartially = ltt.getUsedTarifs();
		tarifsAccepted = ltt.getAcceptedTarifs();
	}
	
	

	/**
	 * Constructor 
	 */
	public LinkToTarifsPanel(LinkToTarifs ltt) {
		this.ltt= ltt;
		
		load();
		
		// create the Compact Explorer
		stree= new STree();
		//		optimisation
		stree.setLargeModel(true);
		stree.setRowHeight(17);

		stree.setCheckVisible(true);

		myRoot=
			CompactNode.prodGetTreeForTarifs(
				ltt.getAcceptabletarifs(),
				ltt.getTarification().getMyTrees(),
				this);
		stree.changeRoot(myRoot);

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
		
		add(buttons, BorderLayout.NORTH);

		add(new JScrollPane(stree), BorderLayout.CENTER);

		stree.expandAll();
		
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#isCompactNodeHighLighted()
	 */
	public boolean isCompactNodeHighLighted(CompactNode cn) {
		return false;
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#getCompactNodeCheckState()
	 */
	public int getCompactNodeCheckState(CompactNode cn) {
		Tarif t= getTarif(cn);
		if (t == null)
			return CHECKED_NOT;

		if (tarifsChecked.contains(t))
			return CHECKED_FULLY;
			
		if (tarifsCheckedPartially.contains(t))
					return CHECKED_PARTIALLY;

		return CHECKED_NOT;
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#getCompactNodeCheckIcon(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public ImageIcon getCompactNodeCheckIcon(CompactNode cn) {
		if (getTarif(cn) == null)
			return Resources.pixel;
		return null;
	}

	/**
	 * see com.simpledata.bc.uicomponents.CNInterface#isCompactNodeCheckable(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public boolean isCompactNodeCheckable(CompactNode cn) {
		Tarif t= getTarif(cn);
		if (t == null)
			return false;

		if (tarifsChecked.contains(t))
			return true;
		return tarifsAccepted.contains(t);
	}

	/** get the tarif contained in this node or null **/
	private Tarif getTarif(CompactNode cn) {
		WorkSheet ws= cn.contentsGetWorkSheet();
		if (ws == null)
			return null;
		return ws.getTarif();
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
		return false;
	}
	
	/** return the tarification we are working on */
	public Tarification getTarification() {
			return ltt.getTarification(); 
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
	
	private boolean editable = true;
	/** self explicit **/
	public void setEditable(boolean b) {
	    editable = b;
	}

	/**
	 * @see com.simpledata.bc.uicomponents.CompactExplorerInterface#checkCompactNode(com.simpledata.bc.uicomponents.CompactNode)
	 */
	public void checkCompactNode(CompactNode cn) {
	    // do nothing if I'm not editable
	    if (! editable) return ;
	    
		Tarif t= getTarif(cn);
		if (t == null)
			return;
		if (tarifsChecked.contains(t)) {
			ltt.removeLinkToTarif(t);
		} else if (tarifsAccepted.contains(t)) {
			ltt.addLinkToTarif(t);
		}
		load();
		stree.repaint();
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

/**
 *  $Log: LinkToTarifsPanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:28  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:44  perki
 *  First commit on sourceforge
 *
 *  Revision 1.28  2004/11/17 15:14:46  perki
 *  Discount DISPLAY RC1
 *
 *  Revision 1.27  2004/10/15 06:38:59  perki
 *  Lot of cleaning in code (comments and todos
 *
 *  Revision 1.26  2004/08/17 12:09:27  kaspar
 *  ! Refactor: Using interface instead of class as reference type
 *    where possible
 *
 *  Revision 1.25  2004/07/30 15:48:40  perki
 *  bugs for kaspar
 *
 *  Revision 1.24  2004/07/30 05:50:01  perki
 *  Moved all CompactTree classes from uicompnents to uicomponents.compact
 *
 *  Revision 1.23  2004/07/26 17:39:36  perki
 *  Filler is now home
 *
 *  Revision 1.22  2004/07/22 15:12:34  carlito
 *  lots of cleaning
 *
 *  Revision 1.21  2004/07/08 14:59:00  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.20  2004/06/22 10:56:20  perki
 *  Lot of cleaning in CompactNode part1
 *
 *  Revision 1.19  2004/05/22 08:39:36  perki
 *  Lot of cleaning
 *
 *  Revision 1.18  2004/04/12 12:30:28  perki
 *  Calculus
 *
 *  Revision 1.17  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.16  2004/03/18 18:08:59  perki
 *  barbapapa
 *
 *  Revision 1.15  2004/03/18 16:26:54  perki
 *  new option model
 *
 *  Revision 1.14  2004/03/18 10:43:02  carlito
 *  *** empty log message ***
 *
 *  Revision 1.13  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.12  2004/03/15 10:43:15  perki
 *  *** empty log message ***
 *
 *  Revision 1.11  2004/03/13 17:44:47  perki
 *  Ah ah ah aha ah ah aAAAAAAAAAAAAAA
 *
 *  Revision 1.10  2004/03/08 09:02:20  perki
 *  houba houba hop
 *
 *  Revision 1.9  2004/03/06 15:22:41  perki
 *  Tirelipapon sur le chiwawa
 *
 *  Revision 1.8  2004/03/06 11:49:22  perki
 *  *** empty log message ***
 *
 *  Revision 1.7  2004/03/03 11:35:07  perki
 *  Un petit bateau
 *
 *  Revision 1.6  2004/03/03 10:17:23  perki
 *  Un petit bateau
 *
 *  Revision 1.5  2004/03/02 17:59:15  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.4  2004/03/02 17:01:41  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.3  2004/03/02 16:36:17  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.2  2004/03/02 16:28:27  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.1  2004/03/02 15:40:39  perki
 *  breizh cola. le cola du phare ouest
 *
 */