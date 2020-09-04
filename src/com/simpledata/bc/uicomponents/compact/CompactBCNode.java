/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactBCNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;
import com.simpledata.bc.uicomponents.TreeIconManager;
import com.simpledata.bc.uitools.ImageTools;

/**
 * Node that contains a BCNode<BR>
 * Take care of production
 */
public abstract class CompactBCNode 
	extends CompactTarifsContainerNode 
	implements CompactNodeMayHavePair {
		
	private static final Logger m_log = Logger.getLogger( CompactBCNode.class ); 

	public CompactBCNode(CompactBCNode p, CNInterface expl) {
		super(p, expl);
	}
	
	//-------------- context -----------------------//

	/** cache for my context .. context is the localisation in multiple trees*/
	private ArrayList/*<BCNode>*/ contextBCnodes;
	/**
	 * get the context of this CompactNode<BR>
	 * It's the list of all BCNode mapped to come up to this node
	 */
	private final ArrayList/*<BCNode>*/ contextGetBCnodes() {
		if (contextBCnodes != null) return contextBCnodes;
		contextBCnodes = new ArrayList();
		contextBCnodes.addAll(contentsGetBCnodes());
		
		// keep a list of my trees
		ArrayList/*<BCTree>*/ trees = new ArrayList/*<BCTree>*/();
		BCNode temp;
		for(Iterator/*<BCNode>*/ i=contentsGetBCnodes().iterator();i.hasNext();)
		{
			temp = (BCNode) i.next();
			if (! trees.contains(temp.getTree())) trees.add(temp.getTree()) ;
		}
		
		if (parent != null && (parent instanceof CompactBCNode)) {
			ArrayList/*<BCNode>*/ pcontext = 
				((CompactBCNode) parent).contextGetBCnodes();
			
			for(Iterator/*<BCNode>*/ i = pcontext.iterator();i.hasNext();){
				temp = (BCNode) i.next();
				if (! trees.contains(temp.getTree()))
					if (! contextBCnodes.contains(temp))
						contextBCnodes.add(temp);
			}
			
		}
		
		return contextBCnodes;
	}
	
	/**
	 * Get the Node that matches this context
	 * @param shadow if true return the shadow node that map this context.
	 * @return the Node that matches this context or "null" if none
	 */
	private final CompactTreeItem contextGetCompact(
			ArrayList/*<BCNode>*/ context,boolean shadow)
	{
		
		
		if (contextGetBCnodes().containsAll(context) && 
				context.containsAll(contextGetBCnodes())) {
			
			if (! shadow) return this;
			
			// look for my shadow node (if any)
			Iterator/*<CompactNode>*/ i = getChildrenAL().iterator();
			CompactTreeItem cn;
			while (i.hasNext()) {
				cn = (CompactTreeItem) i.next();
				if (cn instanceof CompactShadowNode)
					return cn;
			}
			
			return null;
		} 
		
		CompactTreeItem result = null;
		
		// look into childrens
		Iterator/*<CompactNode>*/ i = getChildrenAL().iterator();
		CompactTreeItem cn;
		while (result == null && i.hasNext()) {
			cn = (CompactTreeItem) i.next();
			if (cn instanceof CompactBCNode) 
				result = ((CompactBCNode) cn).contextGetCompact(context,shadow);
		}
		
		return result;
	}
	
	
	/**
	 * get the paired node of this one<BR>
	 */
	protected  final CompactTreeItem _contextGetPair(boolean shadow) {
		ArrayList/*<BCnode>*/ pairContext = 
			explorer.getTarification().getPairManager().getPairPosition(
					contextGetBCnodes());
		
		
		if (pairContext == null) return null;
		
		CompactTreeItem root = getRoot();

		assert root instanceof CompactBCNode : 
			"My root node is not a CompactBCNode "+root;
		
		return ((CompactBCNode) root).contextGetCompact(pairContext,shadow);
	}
	
	/**
	 * <B>Interface CompactBCNode.MayHavePair</B>
	 * get the paired node of this one<BR>
	 */
	public final CompactTreeItem contextGetpair() {
		return _contextGetPair(false);
	}
	

	
	
	//---------------- contents ------------------------//
	
	/**
	 * @return the List of BCnode contained
	 */
	abstract protected ArrayList/*<BCNode>*/ contentsGetBCnodes();
	
	/**
	 * @return the BCnode (or first BCnode contained)
	 */
	abstract protected BCNode contentsGetBCnode();
	
	

	//--------------------- USED FOR TREE CREATION ---------------------//

	/** 
	 * createTarifs Nodes and init the UI components should be
	 * called at first getChildrenALs.. will call getChildrenALs on childrens too
	 **/
	protected void prodCreateUIComponents() {

		// call createUIComponenents() on childrens too
		Iterator e0= getChildrenAL().iterator();
		while (e0.hasNext()) {
			((CompactNode) e0.next()).getChildrenAL();
		}

		// mode with other nodes
		if (explorer.showOthers()) {
			if (getChildCount() > 0) { // not a leaf
				// remove from me Tarifs that are specialized
				ArrayList/*<CompactTreeTarifRef>*/ specializedTarifs 
					= new ArrayList/*<CompactTreeTarifRef>*/();
				ArrayList/*<CompactTreeTarifRef>*/ myNewTarifs
				    = new ArrayList/*<CompactTreeTarifRef>*/();
				Iterator/*<CompactTreeTarifRef>*/ e= tarifsRefs.iterator();
				while (e.hasNext()) {
					CompactTreeTarifRef t= (CompactTreeTarifRef)e.next();
					if (t.getTarif().isSpecialized()) {
						specializedTarifs.add(t);
					} else {
						myNewTarifs.add(t);
					}

				}
				// override my tarifs
				tarifsRefs= myNewTarifs;

				//create a shadow node with the specialized Tarifs
				if (specializedTarifs.size() > 0)
					childrens.add(
						new CompactShadowNode(
							this,
							specializedTarifs,
							explorer));
			}
		}

		super.prodCreateTarifsComponents();

	}

	//-------------------- Tree creation tools -----------------------//


	public abstract CompactBCNode addChildren(BCNode n);

	protected abstract void addChildren(CompactBCNode cnode);

	/**
	 * Do not use directly
	 * Will take care of positioning
	 */
	protected final void _addChildren(CompactBCNode cnode) {
		Iterator/*<CompactBCNode>*/ e = childrens.iterator();
		for (int i = 0; e.hasNext(); i ++) {
			if (((CompactBCNode) e.next()).contentsGetBCnode().getPosition()
				> cnode.contentsGetBCnode().getPosition()) {
				childrens.add(i, cnode);
				return;
			}
		}
		childrens.add(cnode);
	}

	/**
	 * merge Childrens
	 */
	protected final void mergeChildrensWith(CompactBCNode cnode) {
		Iterator i= cnode.childrens.iterator();
		while (i.hasNext()) {
			addChildren((CompactBCNode) i.next());
		}
	}

	/** return the children with this value **/
	protected final CompactBCNode getChildrenALWithValue(Object v) {
		CompactBCNode result= null;
		Iterator/*<CompactBCNode>*/ e = childrens.iterator();
		CompactBCNode cbcnode;
		for (int i = 0; e.hasNext(); i ++) {
			cbcnode = (CompactBCNode) e.next();
			if (cbcnode.contentsHasValue(v)) {
				// normally I shoudlnever have two nodes 
				// that mathches this value
				if (result == null) {
					result= cbcnode;
				} else {
					m_log.error(
						"I got two matching nodes. Dunno how to "
					);
				}
			}

		}
		return result;
	}

	/**
	 * return my self merged with this CNode
	 */
	public void mergeWith(CompactBCNode cnode) {
		if (cnode.contentsHasValue(contentsGetBCnode())) { // no me
			addChildren(cnode);
			return;
		}
		mergeChildrensWith(cnode);
	}
	
	/*	*******************************************************
	------------- STREENODE IMPLEMENTATION ----------------
	*******************************************************/
	// -----------Override abstract---------------//
	public final ImageIcon getMyOpenedIcon() {
		return 
		        TreeIconManager.getBCNodeIcon(
		                contentsGetBCnode(), false, true);
	}
	
	public final ImageIcon getMyClosedIcon() {
		return  
		        TreeIconManager.getBCNodeIcon(
		                contentsGetBCnode(), false, false);
	}
	
	public final ImageIcon getMyLeafIcon() {
		return  CompactBCNode.
		getLeafIcon(explorer,tarifsRefs.size() > 0,contentsGetBCnode());
	}
	
	/** get the leaf icon .. modified if needed by a Tarif icon **/
	protected static ImageIcon getLeafIcon
			(CNInterface explorer,boolean hasTarif, BCNode node) {
		// display a different icon (Tarif) when leaf, has Tarif
		// and not showing Tarifs
		if (! explorer.showTarifs() && hasTarif) {
			return TreeIconManager.getIconBCNodeContainingTarif(node);
		}
		
		return TreeIconManager.getBCNodeIcon(node, true, false);
	}
	
	/**
	 * when you need additional tags<BR>
	 * example.. if the tag erro should be displayed: <BR>
	 * b[TAG_ERROR] = true;
	 */
	protected void getAdditonalTags(boolean[] b) {
	    Tarif t = null;
	    for (Iterator i=tarifsRefs.iterator(); i.hasNext();) {
	       t = ((CompactTreeTarifRef) i.next()).getTarif();
	       if (t.hasReduction()) b[TAG_REDUC] = true;
	       if (! t.isValid()) b[TAG_ERROR] = true;
	    }
	    
	};
}

/*
 * $Log: CompactBCNode.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.6  2004/10/11 17:48:08  perki
 * Bobby
 *
 * Revision 1.5  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.4  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.3  2004/08/05 11:44:11  perki
 * Paired compact Tree
 *
 * Revision 1.2  2004/08/04 16:40:08  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/07/30 05:58:15  perki
 * Slpitted CompactNode.java in sevral files
 *
 */