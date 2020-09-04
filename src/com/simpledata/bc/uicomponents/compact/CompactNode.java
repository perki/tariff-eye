/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

import java.awt.Point;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.compact.CompactTreeNode;
import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;
import com.simpledata.bc.uitools.ImageTools;
import com.simpledata.bc.uitools.streetable.STreeTableNode;
import com.simpledata.uitools.stree.STreeNode;
import com.simpledata.util.CollectionsToolKit;

/**
 * Node that describes a graphical CompactTree.<BR>
 * CompactNode are managed by a CompactExplorer<BR>
 * <BR>
 * <B>TODO: Lot of cleaning has to be done especially in production that should
 * be done at a single point. </B>
 */
public abstract class CompactNode extends STreeNode 
	implements STreeTableNode, CompactTreeItem {
	protected CompactNode parent;

	/** set interface that contains parameters to display myself **/
	protected CNInterface explorer;

	/** constructor called by subclasses to set the actual explorer **/
	protected CompactNode(CompactNode parent, CNInterface expl) {
		explorer= expl;
		this.parent= parent;
	}
	
	/** get the Tarification we are working on **/
	protected final Tarification getTarification() {
		return explorer.getTarification();
	}
	
	//-------- ABSTRACT METHODS -----------------------//
	
	// --- display ----//
	/** @return the title of the nodes (toString() will be applied on it)**/ 
	public abstract Object displayTableValue(int columnIndex);
	
	/** @return the title of the nodes **/ 
	public abstract String displayTreeString();
	
	// --- contents ---//
	
	/** 
	 * return the workSheet at this node (when possible)
	 * (first workseet of tarif at this node)<BR>
	 * somethimes.. on invalid but possible configurations two tarifs
	 * can be present, then pick the first worksheet of the first tarif.
	 ***/
	public abstract WorkSheet contentsGetWorkSheet();

	/** 
	 * return true if this object contains this value<BR> 
	 * This trully depend on the node, but as V1.41 objects tested where
	 * BCNode/Tarif/BCOption/WorkSheet 
	 **/
	public abstract boolean contentsHasValue(Object o);

	/** 
	 * return the Objects contained in this node ..<BR>
	 * as V1.41 reported objected where:
	 *  BCNode/Tarif/BCOption/WorkSheet and event mixed of Tarif/BCNode
	 **/
	public abstract Object[] contentsGet();
	
	
	//--- abstract icons ---/
	/** The open when node is open */
	protected abstract ImageIcon getMyOpenedIcon();
	/** The open when node is closed */
	protected abstract ImageIcon getMyClosedIcon();
	/** The open when node is a leaf */
	protected abstract ImageIcon getMyLeafIcon();
	
	//-------------- Object manipulation -------------//
	
	/** 
	 * Utility<BR>
	 * return a list of CompactNodes that contains this value.
	 **/
	public final ArrayList contentsGetNodesWithValue(Object o) {
		ArrayList v= new ArrayList();
		_contentsGetNodesWithValue(o, v);
		return v;
	}

	/** internal tool for recusivity of contentsGetNodesWithValue **/
	private void _contentsGetNodesWithValue(Object o, ArrayList v) {
		if (contentsHasValue(o))
			v.add(this);
		if (getChildCount() == 0)
			return;
		Iterator e= getChildrenAL().iterator();
		while (e.hasNext()) {
			((CompactNode) e.next())._contentsGetNodesWithValue(o, v);
		}
	}
	
	/** 
	 * Utility<BR>
	 * return a list of CompactNodes that contains at least one object
	 * of this class type.
	 **/
	public final ArrayList contentsGetNodesWithClass(Class c) {
		ArrayList v= new ArrayList();
		_contentsGetNodesWithClass(c, v);
		return v;
	}
	
	/** 
	 * Utility<BR>
	 * return true if this node contains at least one object of this class type
	 * (that can be instanciated in this class)
	 **/
	public final boolean contentsHasClass(Class c) {
		Object[] o = contentsGet();
		for (int i = 0; i < o.length ; i++) {
			if (o[i].getClass().isAssignableFrom(c)) return true;
		}
		return false;
	}
	
	/** internal tool for recusivity of contentsGetNodesWithClass **/
	private final void _contentsGetNodesWithClass(Class c, ArrayList v) {
		if (contentsHasClass(c))
			v.add(this);
		if (getChildCount() == 0)
			return;
		Iterator e= getChildrenAL().iterator();
		while (e.hasNext()) {
			((CompactNode) e.next())._contentsGetNodesWithClass(c, v);
		}
	}
	
	/**
	 * Utility<BR>
	 * Lookup for the first Tarif element of the CompactNode components 
	 * @return first tarif in object list or null if none found
	 */
	public Tarif getFirstTarif() {
	    Object[] objs = this.contentsGet();
		for (int i=0; i<objs.length; ++i ) {
			if ( objs[i] instanceof Tarif ) 
				return (Tarif) objs[i];
		}
		
		return null;
	}

	
	
	//	------------------ interface Streetable ---------------//
	
	
	/** 
	 * <B>Interface StreeTable</B><BR>
	 * return the value to display in the table<BR>
	 * forwarded to displayTableValue();
	 **/
	public final Object getValueAt(int columnIndex) {
	    
		return displayTableValue(columnIndex);
	}
	
	/**  
	 * <B>Interface StreeTable</B> <BR>
	 * forwared to StreeNode.isHighlighted();
	 **/
	public final boolean isHighLighted(int columnIndex) {
		return isHighlighted();
	}
	
	//	-------- STREENODE INTERFACING ------------------//
	
	

	//---     stree node interface may be overwriten --//
	/**
	 * <B>STreeNode Interface</B><BR>
	 * return a tool tip to be displayed
	 */
	public final String getToolTipText() {
		return explorer.getToolTip(this);
	}
	
	
	
	/**
	 * <B>STreeNode Interface</B><BR>
	 * return a Vector of STreeNode denoting the childrens of this node
	 */
	public final Vector/*<STreeNode>*/ getChildren() {
		return CollectionsToolKit.convertToVector(getChildrenAL());
	}
	
	/**
	 * Return all children of this node as an array list. The list is empty
	 * if there are no children. 
	 * @return list of children of this node. 
	 */
	public ArrayList/*<CompactNode>*/ getChildrenAL() {
		return new ArrayList();
	}
	
	/**
	 * Return the root node of this Tree
	 */
	public final CompactTreeItem getRoot() {
		if (parent == null) return this;
		return parent.getRoot();
	}
	
	//	---     stree node interface implemented and final --//
	
	/** <B>STreeNode Interface</B><BR>
	 * ask the explorer if I should be highlighted **/
	public final boolean isHighlighted() {
		return explorer.isCompactNodeHighLighted(this);
	}
	
	/** <B>STreeNode Interface</B><BR>
	 * return the parent of this nod null if any **/
	public final STreeNode getParent() {
		return parent;
	}

	/** <B>STreeNode Interface</B><BR>
	 * get the position of this node in the children list **/
	public final int getIndex() {
		if (parent == null)
			return -1;
		return parent.getChildrenAL().indexOf(this);
	}

	/** <B>STreeNode Interface</B><BR>
	 * return true if (dstn == this) **/
	public final boolean equals(STreeNode dstn) {
		return this == dstn;
	}
	
	/** <B>STreeNode Interface</B><BR>
	 * return the Child at this index */
	public final STreeNode getChildAt(int index) {
		if (getChildrenAL() == null)
			return null;
		if (index < 0 || index >= getChildrenAL().size())
			return null;
		return (STreeNode) getChildrenAL().get(index);
	}
	
	/** <B>STreeNode Interface</B><BR>
	 * return the number of children under this node */
	public final int getChildCount() {
		if (getChildrenAL() == null)
			return 0;
		return getChildrenAL().size();
	}
	
	//	---     stree node interface forward to internals --//
	/** <B>STreeNode Interface</B><BR> Forward to tableDisplayString() */
	public final String toString() {
		return displayTreeString();
	}
	
	/** <B>STreeNode Interface</B><BR> Forward to getMyOpenedIcon() */
	public final ImageIcon getOpenedIcon() {
		return applyTag(getMyOpenedIcon());
	}
	/** <B>STreeNode Interface</B><BR> Forward to getMyClosedIcon() */
	public final ImageIcon getClosedIcon() {
		return applyTag(getMyClosedIcon());
	}
	/** <B>STreeNode Interface</B><BR> Forward to getMyLeafIcon() */
	public final ImageIcon getLeafIcon() {
		return applyTag(getMyLeafIcon());
	}

	
	//	---     stree node interface forward to explorer --//
	/** <B>STreeNode Interface</B><BR> 
	 * return true if this node should be displayed with a checkBox 
	 */
	public final boolean isCheckable() {
		return explorer.isCompactNodeCheckable(this);
	}
	/** <B>STreeNode Interface</B><BR> 
	 * return the checkState of a node
	 */
	public final int getCheckState() {
		return explorer.getCompactNodeCheckState(this);
	}
	/**
	 * <B>STreeNode Interface</B><BR> 
	 * @return the icon corresponding to the checkStatus of the node
	 */
	public final ImageIcon getCheckIcon() {
		return explorer.getCompactNodeCheckIcon(this);
	}
	
	/** <B>STreeNode Interface</B><BR> A check event occured**/
	public final void check() {
		explorer.checkCompactNode(this);
	}
	
	//	---     stree node interface not used --//
	
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final boolean addChildAt(STreeNode child, int index) {
		return false;
	}
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final boolean remove(STreeNode node) {
		return false;
	}
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final boolean order(int newPosition) {
		return false;
	}
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final boolean move(STreeNode destination, int position) {
		return false;
	}
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final boolean acceptDrag() {
		return false;
	}
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final boolean acceptDrop(STreeNode droppedNode) {
		return false;
	}

	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final JPopupMenu getPopupMenu() {
		return null;
	}
	/** <B>STreeNode Interface</B><BR> NOT USED */
	public final void doAction(String key) {}


	
	//---------- expanded / close manipulations ---//
	
	/** expanded state memory to avoid extra treatment */
	private boolean expandedState= false;

	/** set to true if the state of this node is expanded **/
	public final void expandSet(boolean state) {
		this.expandedState= state;
	}
	
	/** @return true if the state of this node is declared as expanded **/
	public final boolean expandGet() {
		return this.expandedState;
	}
	

	/**
	 * An interface that should be used to create a CompactNodeTree
	 *
	 */
	public interface CNInterface {
		public int CHECKED_NOT= STreeNode.NOT_CHECKED;
		public int CHECKED_PARTIALLY= STreeNode.PARTIALLY_CHECKED;
		public int CHECKED_FULLY= STreeNode.FULLY_CHECKED;

		/** return true if should show Tarifs */
		public boolean showTarifs();


		/**
		 * @return
		 */
		public Tarification getTarification();

		/**
		 * tell the tree to refresh it's structure
		 */
		public void refreshStructure();

		/**
		 * return true is should show Tarifs references
		 */
		public boolean showTarifsRefrences();

		/** return true if should show Tarifs */
		public boolean showOthers();

		/** return true if this node should be highlighted **/
		public boolean isCompactNodeHighLighted(CompactNode cn);

		/**
		* Returns the status of the node
		* <PRE>
		* CHECKED_NOT      : not checked if leaf
		*                  : no child checked if not leaf
		* CHECKED_PARTIALY : the node is not a leaf and some of his children
		*                  : are not checked
		* CHECKED_FULLY    : leaf checked, or non leaf with all children checked
		* </PRE>
		**/
		public int getCompactNodeCheckState(CompactNode cn);

		/** return true if this node is checkable **/
		public boolean isCompactNodeCheckable(CompactNode cn);

		/** return true is tarif appears on one time on tree **/
		public boolean createVirtualNode();

		/** return true if you want to show root nodes **/
		public boolean showRootNodes();

		/** An action of check has been detected on this node **/
		public void checkCompactNode(CompactNode cn);

		/**
		 * return the check icon to display 
		 */
		public ImageIcon getCompactNodeCheckIcon(CompactNode node);
		
		/** 
		 * a node expand/close event append.. 
		 * @param node the node that has been opened/closed
		 * @param state true if open
		 **/
		public void setExpanded(CompactTreeItem node, boolean state);

		/** tell that something changed on this node **/
		public void fireTreeNodesChanged(CompactNode cn);
		
		/** 
		 * get the ToolTip for the CompactNode<BR>
		 *  @return null if none
		 */
		public String getToolTip(CompactNode cn);
		
		
		/** 
		 * When calculating discount info column (for the StreeTable)<BR>
		 * return true to display the discount, false to display the 
		 * undiscounted column
		 */
		public boolean discountOrUndisc();
		
	}
	
	//################# Tree informations #######################//
	
	/**
	 * return the tree path of this node
	 * path.get(0) is the node itslef path.get(end) is the rootNode
	 */
	public ArrayList/*<CompactNode>*/ treeGetPath() {
		ArrayList/*<CompactNode>*/ result = new ArrayList/*<CompactNode>*/();
		_treeGetpath(result);
		return result;
	}
	
	/** recursive helper to treegetPath(ArrayList) **/
	private void _treeGetpath(ArrayList/*<CompactNode>*/ tp) {
		tp.add(this);
		if (getParent() != null) ((CompactNode) getParent())._treeGetpath(tp);
	}
	

	//################# PRODUCTION ##############################//

	
	/**
	 * Create a compact explorer Tree based on this list of Tarifs for
	 * this tree
	 */
	public static CompactNode prodGetTreeForTarifs(
			ArrayList/*<Tarif>*/ tarifs,
			BCTree[] trees,
			CNInterface expl) {
		
		return prodGetTreeFrom(
				CompactTreeNode.getTreeForTarifs(
				tarifs,
				trees),expl);
	}
	
	/**
	 * Create a compact explorer Tree based on the the datamodel's
	 * actual CompactTree
	 */
	public static CompactNode prodGetTreeForTarifs(CNInterface expl) {
		// retrieve the Root compact node
		return 
	prodGetTreeFrom(expl.getTarification().getCompactVisibleTreeRoot(), expl);
	}
	
	/** Create a Tree from a CompactTreeNode **/
	private static CompactNode 
		prodGetTreeFrom(CompactTreeNode ctRoot,CNInterface expl) {
		
		
		// Create a dummy Root that will contain the whole tree
		CompactBCNode cRoot= new CompactRoot(ctRoot.getBCNode(), expl);
		
		//launch tree creation
		prodCreateChildrens(expl,cRoot,ctRoot);
		
		
		// compress the tree
		CompactBCGroupNode.prodCreateCompactBCGroupNodes(cRoot);
		
		
		// create the UI
		cRoot.visitAllChildrens(new EmptyCompactTreeVisitor(){
			public void action(CompactTarifsContainerNode node) {
				node.prodCreateUIComponents();
			}
			public void caseCompactRoot(CompactRoot node) {action(node);}
			public void caseCompactBCNodeSingle(CompactBCNodeSingle node) {
				action(node);
			}
			public void caseCompactBCGroupNode(CompactBCGroupNode node) {
				action(node);
			}
			public void caseCompactShadowNode(CompactShadowNode node) {
				action(node);
			}
		
		},true);
		
		return cRoot;
	}
	
	
	/**
	 * recursively follow the the CompactTree
	 * @param c the CompactNode to work on
	 * @param ctn the "image" of this CompactNode in the CompactTreeNode
	 * @param expl the CNInterface that contains all the display settings
	 */
	private static void 
		prodCreateChildrens(CNInterface expl,
				CompactBCNode c,CompactTreeNode ctn) {
		
		//*** attach Tarifs contained in ctn to c
		Iterator/*<CompactTreeTarifRef>*/ e = ctn.getTarifsRefs().iterator();
		while (e.hasNext()) {
			c.addTarif((CompactTreeTarifRef) e.next());
		}
		
		//*** go thru childrens
		Iterator/*<CompactTreeNode>*/ e1 = ctn.getChildren().iterator();
		CompactTreeNode childTN = null;
		while (e1.hasNext()) {
			childTN = (CompactTreeNode) e1.next();
			
			// if we do not want to show root nodes
			if (!expl.showRootNodes() && 
					// if the consireder BCnode is a root
					childTN.getBCNode().isRoot() &&
					// this is only possible for nodes with no tarifs
					childTN.getTarifsRefs().size() == 0) {
				// Then we attach the childrens of this node to the current node
				prodCreateChildrens(expl,c,childTN);
			} else {
				// create a new Node
				CompactBCNode child = 
					new CompactBCNodeSingle(c, childTN.getBCNode(), c.explorer);
	
				c.addChildren(child);
				// continue construction recursively
				prodCreateChildrens(expl,child,childTN);
			}
		}
	}
	
	// CNInterface access
	/**
	 * return the CNinterface I'm using
	 */
	public final CNInterface getExplorer() {
		return explorer;
	}
	
	//TAGS----------------------------------------------------
	
	public static final int TAG_ERROR = 0;
	public static final int TAG_REDUC = 1;
	
	private static final ImageIcon[] TAGS = new ImageIcon[] { 
	        Resources.stdTagError,
	        Resources.reductionTag
	};
	
	private final ImageIcon applyTag(ImageIcon src) {
	    final boolean[] b = new boolean[] {false,false};
	    visitAllChildrens(new SimpleCompactTreeVisitor(){
            public void visitCompactNode(CompactNode cn) {
                cn.getAdditonalTags(b);
            }},true);
	    
	    for (int i = 0; i < TAGS.length; i++) {
	        if (b[i]) {
			src = ImageTools.drawIconOnIcon(
					src,
					TAGS[i],
					new Point(0, 0));
	        }
	    }
	    return src;
	    
	}
	
	/**
	 * when you need additional tags<BR>
	 * example.. if the tag erro should be displayed: <BR>
	 * b[TAG_ERROR] = true;
	 */
	protected void getAdditonalTags(boolean[] b) {}

	// Visitor implementation --------------------------------------------
	/**
	 * Implement this method to be able to visit the CompactTree using
	 * the Visitor pattern. If you are wondering how to do this, 
	 * inspire yourself in other concrete descendants of CompactNode. 
	 */
	public abstract void visit( CompactTreeVisitor v );
	
	
	/**
	 * The visitor passed will visit all the nodes that have this node in their
	 * path.<BR>
	 * @param before if true parent.visit(v) will be called 
	 * BEFORE childrens.visit(v);
	 */
	public final void visitAllChildrens(CompactTreeVisitor v,boolean before) {
		if (before) visit(v);
		for(Iterator/*<CompactNode>*/ i=getChildrenAL().iterator();i.hasNext();)
			((CompactNode) i.next()).visitAllChildrens(v,before);
		if (! before) visit(v);
	}
}












/*
 * $Log: CompactNode.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.11  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.10  2004/11/17 10:52:03  perki
 * Discount display preview step1
 *
 * Revision 1.9  2004/11/10 17:35:46  perki
 * Tree ordering is now correct
 *
 * Revision 1.8  2004/10/11 17:48:08  perki
 * Bobby
 *
 * Revision 1.7  2004/08/23 07:40:02  jvaucher
 * Added the fee reports. Some changes should be done:
 * - Review the template FeeReport.jrxml. The length of the fields is
 * to small in some cases.
 * - Maybe some node should be reported.
 * - Should use a new common class for the numbering of the section
 *
 * Revision 1.6  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.5  2004/08/05 11:44:11  perki
 * Paired compact Tree
 *
 * Revision 1.4  2004/07/30 11:28:39  perki
 * Better tooltips
 *
 * Revision 1.3  2004/07/30 07:07:23  perki
 * Moving Compact Tree from uicomponents to uicomponents.compact
 *
 * Revision 1.2  2004/07/30 05:58:15  perki
 * Slpitted CompactNode.java in sevral files
 *
 * Revision 1.1  2004/07/30 05:50:01  perki
 * Moved all CompactTree classes from uicompnents to uicomponents.compact
 *
 * Revision 1.60  2004/07/30 05:35:14  perki
 * *** empty log message ***
 *
 * Revision 1.59  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 * Revision 1.58  2004/07/22 15:12:34  carlito
 * lots of cleaning
 *
 * Revision 1.57  2004/07/19 13:54:46  kaspar
 * - refactoring: Moving Compact* nodes into public view for
 *   access in reporting
 * - Removed useless reporting classes
 * - Adding partly finished Linearizer Test
 * - Accomodated for changements in how to do things
 *
 * Revision 1.56  2004/07/19 09:36:54  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.55  2004/07/15 09:45:42  kaspar
 * * Change of getChildrenAL: Documentation and change of
 *   semantics for no children case.
 *
 * Revision 1.54  2004/07/09 20:25:02  perki
 * Merging UI step 1
 *
 * Revision 1.53  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.52  2004/07/02 09:37:31  perki
 * *** empty log message ***
 *
 * Revision 1.51  2004/06/28 17:29:16  perki
 * *** empty log message ***
 *
 * Revision 1.50  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.49  2004/06/28 10:38:48  perki
 * Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 * Revision 1.48  2004/06/25 10:09:50  perki
 * added first step for first sons detection
 *
 * Revision 1.47  2004/06/23 18:38:04  perki
 * *** empty log message ***
 *
 * Revision 1.46  2004/06/23 12:06:41  perki
 * Cleaned CompactNode
 *
 * Revision 1.45  2004/06/23 07:16:42  perki
 * integrated CompactNode with CompactTreeTarifRef
 *
 * Revision 1.44  2004/06/22 17:11:29  perki
 * CompactNode now build from datamodel and added a notice interface to WorkSheet
 *
 * Revision 1.43  2004/06/22 11:22:39  perki
 * *** empty log message ***
 *
 * Revision 1.42  2004/06/22 10:56:20  perki
 * Lot of cleaning in CompactNode part1
 *
 * Revision 1.41  2004/05/31 17:08:05  perki
 * *** empty log message ***
 *
 * Revision 1.40  2004/05/31 14:00:37  perki
 * *** empty log message ***
 *
 * Revision 1.39  2004/05/31 12:40:22  perki
 * *** empty log message ***
 *
 * Revision 1.38  2004/04/12 17:34:52  perki
 * *** empty log message ***
 *
 * Revision 1.37  2004/04/12 12:33:09  perki
 * Calculus
 *
 * Revision 1.36  2004/04/12 12:30:28  perki
 * Calculus
 *
 * Revision 1.35  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.34  2004/03/18 18:51:52  perki
 * barbapapa
 *
 * Revision 1.33  2004/03/18 18:08:59  perki
 * barbapapa
 *
 * Revision 1.32  2004/03/18 16:26:54  perki
 * new option model
 *
 * Revision 1.31  2004/03/18 15:34:12  carlito
 * *** empty log message ***
 *
 * Revision 1.30  2004/03/18 10:43:02  carlito
 * *** empty log message ***
 *
 * Revision 1.29  2004/03/18 09:30:02  perki
 * *** empty log message ***
 *
 * Revision 1.28  2004/03/18 09:02:29  perki
 * *** empty log message ***
 *
 * Revision 1.27  2004/03/16 14:09:31  perki
 * Big Numbers are welcome aboard
 *
 * Revision 1.26  2004/03/15 10:43:15  perki
 * *** empty log message ***
 *
 * Revision 1.25  2004/03/13 17:44:47  perki
 * Ah ah ah aha ah ah aAAAAAAAAAAAAAA
 *
 * Revision 1.24  2004/03/06 15:22:41  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.23  2004/03/06 11:49:22  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/03/03 10:17:23  perki
 * Un petit bateau
 *
 * Revision 1.21  2004/03/02 17:59:15  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.20  2004/03/02 17:01:41  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.19  2004/03/02 16:28:27  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.18  2004/03/02 14:42:48  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.17  2004/03/02 00:32:54  carlito
 * *** empty log message ***
 *
 * Revision 1.16  2004/02/25 17:36:54  perki
 * *** empty log message ***
 *
 * Revision 1.15  2004/02/25 13:21:15  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/02/25 11:08:03  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/02/25 10:24:27  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/02/25 10:20:15  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/02/25 09:54:43  perki
 * *** empty log message ***
 *
 * Revision 1.10  2004/02/25 08:11:58  perki
 * nicer
 *
 */