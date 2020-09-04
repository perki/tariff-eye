/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactTree.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.compact;

import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;

import org.apache.log4j.Logger;

/**
 * Handler for a compacted (effective) version of this
 * Tarification.<BR>
 * The Tree structure and content is contained is CompactTreeNode
 * but the class handles all the refreshing and events needed to keep
 * this Tree up to date and synchronized with the datamodel.<BR>
 * The compact tree created only relies on Visible BCTrees.
 */
public class CompactTree  implements NamedEventListener {
	
	private static final Logger m_log = Logger.getLogger( CompactTree.class ); 
	
	/** the root node of this compact Tree **/
	private CompactTreeNode rootNode;
	
	/** the tarification attached to this tree **/
	private Tarification tarification;
	
	/** 
	 * this value is  trigger that remember if the CompacTree 
	 * needs to be refresehed. And it will be only if getRoot()
	 * is called
	 */
	private boolean needRefresh;
	
	/**
	 * construct a CompactTree only on Visible Trees
	 */
	private boolean onlyShowVisibleTrees;
	
	/**Tool to make searches will be created only if needed**/
	private CompactTreeSearch searcher;
	
	
	/**
	 * event to fire on change (-1) if none
	 */
	private int eventToFire;
	
	/** 
	 * construct a compact tree and add necessry listeners 
	 * @param fireEvent the event code you want to fire when changes 
	 * occures -1 if none. (see NamedEvent for codes)
	 * **/
	public CompactTree(Tarification t,
					   boolean showOnlyVisibles,
					   int fireEvent) {
		needRefresh = false;
		tarification = t;
		onlyShowVisibleTrees = showOnlyVisibles;
		eventToFire = fireEvent;
		
		// add the listeners
		tarification.addNamedEventListener(this);
		
	}

	
	/** get the tool that makes searches possible on this tree **/
	public synchronized CompactTreeSearch getSearcher() {
		if (searcher == null) searcher = new CompactTreeSearch(this);
		return searcher;
	}
	
	/** get the root node (from which the tree can be itterated) */
	public synchronized CompactTreeNode getRoot() {
		if (rootNode == null || needRefresh) refreshCompactTree();
		return rootNode;
	}
	

	/** refresh (reconstruct) the compactTree **/
	private synchronized void refreshCompactTree() {
		rootNode = 
			CompactTreeNode.getTreeForTarifs(
					tarification.getAllTarifs(),
					onlyShowVisibleTrees ? 
							tarification.getMyTreesVisible() : 
								tarification.getMyTrees());
		needRefresh = false;
		m_log.info( "refreshed "+needRefresh );
	}
	
	
	/**
	 * deals with registered events
	 */
	public synchronized void eventOccured(NamedEvent e) {
		switch (e.getEventCode()) {
			case NamedEvent.TARIF_MAPPING_MODIFIED:
			case NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED:
				// forward events only if need refresh was false;
				if (! needRefresh) {
					needRefresh = true;
					if (eventToFire > 0) {
						tarification._fireCompactTreeStructureChange(
							eventToFire	
						);
					}
				}
			break;
		}
	}
	
	
	public String toString() {
		return getRoot().toString();
	}
}
