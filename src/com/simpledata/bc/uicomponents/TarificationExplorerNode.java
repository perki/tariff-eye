/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: TarificationExplorerNode.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/

package com.simpledata.bc.uicomponents;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.CheckBox;
import com.simpledata.uitools.stree.STreeNode;
import com.simpledata.util.CollectionsToolKit;

import org.apache.log4j.Logger;

/**
* Class that Maps BCNode to DefaulSTreeNode
*/
public class TarificationExplorerNode extends STreeNode {
	
	private static final Logger m_log = Logger.getLogger( TarificationExplorerNode.class );

	/** the node I refer To **/
	protected BCNode me;

	/** the TarificationExplorerTree I belong to**/
	protected TarificationExplorerTree tet;

	/**
	* construct a new BCNode reflecting  bcnode
	*/
	public TarificationExplorerNode(
		TarificationExplorerTree tet,
		BCNode bcnode) {
		this.me= bcnode;
		this.tet= tet;

	}

	/** return the BCNode object of this TarificationExplorerNode **/
	public BCNode getBCNode() {
		return me;
	}
	

	/**
	 * <B>STreeNode Interface</B><BR>
	 * return a Vector of STreeNode denoting the childrens of this node
	 */
	public final Vector/*<STreeNode>*/ getChildren() {
		return CollectionsToolKit.convertToVector(getChildrenAL());
	}

	/**
	* return the childrens vector of TarificationExplorerNode
	*/
	public ArrayList getChildrenAL() {
		ArrayList res= tet.convertToTEN(me.getChildrens());
		/** Uncomment to see tarifs in Tree
			ArrayList tarifs = me.getTarifMapping();
		for (int i = 0; i < tarifs.size(); i++) {
			res.add(new TarificationExplorerTarifNode(tet,this,(Tarif) tarifs.get(i)));
		}
		*/
		return res;
	}

	/**
	* return the num of childrens
	*/
	public int getChildCount() {
		// note: counting is done on BCNode (no need to convert)
		return this.getChildrenAL().size();
	}

	/**
	* The node returns its index within its parent children
	* if it has parent else returns -1
	**/
	public int getIndex() {
		return me.getPosition();
	}

	/**
	* get the Childnode at this position
	*/
	public STreeNode getChildAt(int index) {
		STreeNode res= null;
		ArrayList c= this.getChildrenAL();
		if (c != null) {
			int s= c.size();
			if ((0 <= index) && (index < s)) {
				res= (STreeNode) c.get(index);
			}
		}
		return res;
	}

	/** 
	* move this node to another location. Childrens remains attached to it.
	* @param index specify the position in the childrens ordering list of the new parent. -1 to add at the end
	*/
	public boolean addChildAt(STreeNode child, int index) {
		if (!tet.isManipulable())
			return false;
		return ((TarificationExplorerNode) child).getBCNode().setParent(
			me,
			index);
	}

	/**
	* return the parent of this node.
	*/
	public STreeNode getParent() {
		if (me.isRoot())
			return null;
		return tet.getTEN(me.getParent());
	}

	/**
	* drop a node
	*/
	public boolean remove(STreeNode node) {
		if (!tet.isManipulable())
			return false;
		/** !! alert if this does return false **/
		// recursive drop
		return ((TarificationExplorerNode) node).getBCNode().drop(true);
	}

	/**
	* move a node
	*/
	public boolean move(STreeNode dest, int index) {
		if (!tet.isManipulable())
			return false;
		return ((TarificationExplorerNode) dest).addChildAt(this, index);
	}

	/**
	* position of this node in its parent children list
	* @param newPosition (-1) or great number to be at the end
	*/
	public boolean order(int newPosition) {
		if (!tet.isManipulable())
			return false;
		me.setPositionTo(newPosition);
		return true;
	}

	/**
	 * get the STree attached to this node
	 */
	public TarificationExplorerTree getTETree() {
		return tet;
	}

	/**************************************************************************/
	/******************* DRAG n DROP MANAGEMENT              ******************/
	/**************************************************************************/

	/**
	* Determines if the node can be dragged
	**/
	public boolean acceptDrag() {
		return tet.isManipulable();
	}

	/**
	* Determines if the node would accept the droppedNode as child 
	*
	**/
	public boolean acceptDrop(STreeNode droppedNode) {
		if (!tet.isManipulable())
			return false;
		if (droppedNode == null) {
			return false;
		}
		return ((TarificationExplorerNode) droppedNode)
			.getBCNode()
			.canBeMovedTo(
			me);
	}

	/**************************************************************************/
	/******************* CHECKABLE tree MANAGEMENT           ******************/
	/**************************************************************************/

	/**
	* If false is returned then the chekbox will be disabled
	*
	**/
	public boolean isCheckable() {
		if (!tet.canRemapTarif())
			return false;
		Tarif tarif= tet.getSelectedTarif();
		if (tarif == null)
			return false;
		if (isChecked()) { // unamp
			return (tarif.canBeRemovedFrom(me, false, false));
		}
		return (tarif.canBeMappedTo(me, false, false));
	}

	/**
	* Returns the status of the node
	* NOT_CHECKED      : not checked if leaf
	*                  : no child checked if not leaf
	* PARTIALY_CHECKED : the node is not a leaf and some of his children
	*                  : are not checked
	* FULLY_CHECKED   : leaf checked, or non leaf with all children checked
	**/
	public int getCheckState() { //return dummy
		return STreeNode.NOT_CHECKED;
	}

	/**
	* Check or uncheck this node and all his subnodes
	**/
	public void check() {
		if (!tet.canRemapTarif())
			return;

		Tarif tarif= tet.getSelectedTarif();
		if (tarif == null)
			return;

		if (isChecked()) { // unamp
			tarif.unMapFrom(me, true);
		} else { // map
			tarif.mapTo(me, true);
		}
		refresh();

	}

	/**
	 * refresh me (visual)
	 */
	public void refresh() {
		tet.fireRefresh();
	}

	/**
	 * Get if a Tarif maps this node
	 * @return true if isChecked
	 */
	public boolean isChecked() {
		if (tet.getSelectedTarif() == null)
			return false;
		return tet.getSelectedTarif().isMapping(me);
	}

	/**
	 * return true if one of my ancestor is checked
	 */
	public boolean isAncestorChecked() {
		if (isChecked()) {
			return true;
		}
		if (me.isRoot())
			return false;
		return tet.getTEN(me.getParent()).isAncestorChecked();
	}

	/**
	 * get the icon representing the check state
	 */
	public ImageIcon getCheckIcon() {
		Tarif tarif= tet.getSelectedTarif();
		int tag= CheckBox.TAG_NONE;

		int checkableCode=
			isCheckable() ? CheckBox.CHECKABLE : CheckBox.CHECKABLE_NOT;

		if (tarif == null)
			return CheckBox.get(
				tag,
				checkableCode,
				CheckBox.CHECKSTATE_CHECKED_NOT);

		if (isChecked()) { //checked
			tag=
				tarif.canBeRemovedFrom(me, true, false)
					? CheckBox.TAG_NONE
					: CheckBox.TAG_WARNING;
			return CheckBox.get(
				tag,
				checkableCode,
				CheckBox.CHECKSTATE_CHECKED);
		}
		
		
		
		//	not checked .. verify that if check the future state willbe stable
		 if (checkableCode == CheckBox.CHECKABLE)
			 tag = tarif.canBeMappedTo(me, true, false)
							 ? CheckBox.TAG_NONE
							 : CheckBox.TAG_WARNING;
							 
		if (isAncestorChecked())
					return CheckBox.get(tag,checkableCode, CheckBox.CHECKSTATE_INDUCED);
		
		if (me.getTree().getTarifMappingRecursively(me).contains(tarif)) {
			return CheckBox.get(tag, checkableCode, CheckBox.CHECKSTATE_PARTIALLY);
		}

		
		
		return CheckBox.get(tag,checkableCode, CheckBox.CHECKSTATE_CHECKED_NOT);
	}

	/**
	* Returns the icon to be displayed if the node
	* is not a leaf and is expanded
	*/
	public ImageIcon getOpenedIcon() {
		return TreeIconManager.getBCNodeIcon(this.me, false, true);
	}

	/**
	* Returns the icon to be displayed if the node
	* is not a leaf and is not expanded
	*/
	public ImageIcon getClosedIcon() {
		return TreeIconManager.getBCNodeIcon(this.me, false, false);
	}

	/**
	* Returns the icon to be displayed if the node
	* is a leaf
	*/
	public ImageIcon getLeafIcon() {
		return TreeIconManager.getBCNodeIcon(this.me, true, false);
	}

	/**
	* Returns nodeTitle
	*/
	public String toString() {
		return me.getTitle();
	}

	/**
	* returns true if this method is equal
	*/
	public boolean equals(STreeNode dstNode) {
		return (dstNode == tet.getTEN(me));
	}

	//-------------------- ACTIONS ---------------------//

	/** actions keys (commands) called by Popup **/
	String[] actionKeys= { "create", "drop", "droprecursive", "settings" };

	/**
	 * @see com.simpledata.uitools.stree.STreeNode#getPopupMenu()
	 */
	public JPopupMenu getPopupMenu() {
		if ((!tet.isManipulable()) && tet.getExtraPopup() == null)
			return null;

		JPopupMenu res= new JPopupMenu();

		if (tet.isManipulable()) { // if is manipulable
			String[] actionNames= new String[actionKeys.length];
			boolean[] active= new boolean[actionKeys.length];
			ImageIcon[] icons= new ImageIcon[actionKeys.length];

			// create
			actionNames[0]= Lang.translate(actionKeys[0]);
			active[0]= me.getBoolProperty(BCNode.PROP_BOOL_EXTENDABLE);
			icons[0]= Resources.iconPlus;

			// drop & drop recursively
			String message[] = new String[2];
			message[0] = me.canBeDroped(false);
			message[1] = me.canBeDroped(true);
			for (int i= 1; i < 3; i++) {
				if (message[i-1] == null) {
					actionNames[i]= Lang.translate(actionKeys[i]);
					active[i]= true;
				} else {
					actionNames[i]=
						Lang.translate(actionKeys[i]) + " (" + 
						Lang.translate(message[i-1]) + ")";
					active[i]= false;
				}
				icons[i]= Resources.iconDelete;
			}

			// rename
			actionNames[3]= Lang.translate(actionKeys[3]);
			active[3]= true;
			icons[3]= Resources.iconSettings;

			// fill the Menu
			final TarificationExplorerNode ten= this;
			for (int i= 0; i < actionKeys.length; i++) {
				final String key= actionKeys[i];
				JMenuItem jmi= new JMenuItem(actionNames[i], icons[i]);
				jmi.setEnabled(active[i]);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(
						java.awt.event.ActionEvent evt) {
						ten.doAction(key);
					}
				});
				res.add(jmi);
			}
		}

		// call extra popup
		if (tet.getExtraPopup() != null)
			tet.getExtraPopup().modifyMe(res, this);

		return res;
	}

	/* 
	 * @see com.simpledata.uitools.stree.STreeNode#doAction(java.lang.String)
	 */
	public void doAction(String arg0) {

		// create
		if (arg0.equals(actionKeys[0])) {
			// create a new Node
			BCNode newNode= me.getTree().createNode("", me);
			if (newNode == null) {
				m_log.error( "cannot create node on " + me.getFullNID() );
				return;
			}
			// set the appropriate Title
			newNode.setTitle(Lang.translate("New"));
			STreeNode dad= getParent();
			if (dad != null) {
				tet.fireTreeStructureChanged(dad);
			} else {
				tet.fireTreeStructureChanged(this);
			}

			// start editing on this node
			TarificationExplorerNode ten= tet.getTEN(newNode);
			if (newNode == null) {
				m_log.error( "got a null node  " + newNode.getFullNID() );
				return;
			}
			tet.startEditingAtPath(tet.getPath(ten));
			//tet.expandNode(ten);
			//tet.fireRefresh();
			//tet.launchNodeEdition(ten);
			return;
		}
		//	drop
		if (arg0.equals(actionKeys[1])) {
			me.drop(false);
			tet.fireTreeStructureChanged(getParent());
			return;
		}
		//	drop recursive
		if (arg0.equals(actionKeys[2])) {
			me.drop(true);
			tet.fireTreeStructureChanged(getParent());
			return;
		}
		//	rename
		if (arg0.equals(actionKeys[3])) {
			tet.startEditingAtPath(tet.getPath(this));
			return;
		}
	}

	//--------------- Edition 
	/**
	 * get if this node is Editable (the title)
	 */
	public boolean isEditable() {
		return true;
	}

	/**
	 * change the Title of this BCnode
	*/
	public boolean setTitle(String title) {
		title= title.trim();
		if (title.length() == 0)
			return false;
		me.setTitle(title);
		return true;
	}
}

/** $Log: TarificationExplorerNode.java,v $
/** Revision 1.2  2007/04/02 17:04:23  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:37  perki
/** First commit on sourceforge
/**
/** Revision 1.45  2004/09/04 18:12:31  kaspar
/** ! Log.out -> log4j
/**   Only the proper logger init is missing now.
/**
/** Revision 1.44  2004/07/08 14:59:00  perki
/** Vectors to ArrayList
/**
/** Revision 1.43  2004/06/28 13:22:37  perki
/** icons are 16x16 for macs
/**
/** Revision 1.42  2004/05/20 09:39:43  perki
/** *** empty log message ***
/**
/** Revision 1.41  2004/03/18 12:25:30  perki
/** yeah
/**
/** Revision 1.40  2004/03/18 09:02:29  perki
/** *** empty log message ***
/**
/** Revision 1.39  2004/03/08 08:46:03  perki
/** houba houba hop
/**
/** Revision 1.38  2004/03/06 14:24:50  perki
/** Tirelipapon sur le chiwawa
/**
/** Revision 1.37  2004/03/06 11:49:22  perki
/** *** empty log message ***
/**
/** Revision 1.36  2004/03/04 17:16:44  perki
/** copy goes to hollywood
/**
/** Revision 1.35  2004/03/02 00:32:54  carlito
/** *** empty log message ***
/**
/** Revision 1.34  2004/02/26 13:24:34  perki
/** new componenents
/**
/** Revision 1.33  2004/02/26 10:27:37  perki
/** TAC goes to hollywood
/**
/** Revision 1.32  2004/02/23 18:34:48  carlito
/** *** empty log message ***
/**
/** Revision 1.31  2004/02/18 13:37:51  carlito
/** *** empty log message ***
/**
/** Revision 1.30  2004/02/14 21:53:26  carlito
/** *** empty log message ***
/**
/** Revision 1.29  2004/02/06 15:07:44  perki
/** New nodes
/**
/** Revision 1.28  2004/02/06 10:04:22  perki
/** Lots of cleaning
/**
/** Revision 1.27  2004/02/05 18:58:37  carlito
/** fin de journee
/**
/** Revision 1.26  2004/02/04 18:10:05  carlito
/** zorglub
/**
/** Revision 1.25  2004/02/04 15:42:16  perki
/** cleaning
/**
* Revision 1.24  2004/02/04 12:53:30  carlito
* rajouter un import...
*
* Revision 1.23  2004/02/03 11:41:50  perki
* totally new double sided map
*
* Revision 1.22  2004/02/03 11:31:17  perki
* totally new double sided map
*
* Revision 1.21  2004/02/02 18:19:15  perki
* yupeee3
*
* Revision 1.20  2004/02/02 17:00:39  perki
* yupeee2
*
* Revision 1.19  2004/02/02 16:32:06  perki
* yupeee
*
* Revision 1.18  2004/02/02 11:21:05  perki
* *** empty log message ***
*
* Revision 1.17  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.16  2004/01/31 15:46:49  perki
* 16 heure 49
*
* Revision 1.15  2004/01/31 10:28:56  perki
* BCNode manipulation ok-- c'est de la bombe
*
* Revision 1.14  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.10  2004/01/23 10:23:34  perki
* Dans les annees 70 la couleur apparu
*
* Revision 1.9  2004/01/23 09:55:23  perki
* Welcome to the wordefull world of colors
*
* Revision 1.7  2004/01/21 13:42:30  perki
* Apfelgluck
*
* Revision 1.6  2004/01/20 11:05:23  perki
* Et la comete disparue dans l'espace infini.. Fin
*
*
*/