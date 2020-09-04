/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: FillerData.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.tarif.TarifAssets;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.compact.CompactNodeMayHavePair;
import com.simpledata.bc.uicomponents.compact.CompactTreeItem;
/**
 * This his linked to a CompactTree it contains all the datas about
 * the repartition of assets and transactions.<BR>
 * <B>Repartition</B> is the representation in percent of Assets.<BR>
 * <B>IT IS INCOMPATIBLE FOR A COMPACT TREE WITH SHOW OTHER NOT ENABLED
 * </B>
 */
public class FillerData {
		
	/** log4j logger */
	private static Logger m_log = Logger.getLogger( FillerData.class ); 
	
	/** the root node of this tree **/
	private CompactNode rootNode;
	
	/** caching the percentages of assets for each node **/
	private HashMap/*<CompactNode,NodeInfo>*/ cache;
	
	/** 
	 * If this boolean is set to true then all listener are going depth
	 * This is used when there are many changes to do ...
	 */
	private boolean deaf;
	
	/** 
	 * contains the list of node I'm interested in <BR>
	 * Those nodes are the ones that contains in their childrens at least
	 * one TarifAssets
	 */
	private ArrayList/*<CompactNode>*/ interestingNodes;
		

	
	/** 
	 * Adds the created NodeInfo class as being the information
	 * for the given CompactNode. This stops FillerData from creating
	 * another instance of NodeInfo when the CompactNode will 
	 * be touched again. 
	 * 
	 * This also remembers the NodeInfo instance as being the info
	 * for the pair of cn when there is such a pair. 
	 */
	void addToCache( CompactNode cn, NodeInfo info ) {
		cache.put( cn, info );
		
		// add pair node in the cache (if any)
		if (cn instanceof CompactNodeMayHavePair) {
			CompactNodeMayHavePair pairable = (CompactNodeMayHavePair) cn;
			CompactTreeItem pair = pairable.contextGetpair();	
			if ( pair != null ) {
				cache.put( pair, info ) ;
			}
		}
	}
	
	/**
	 * @return true if I'm deaf to events. 
	 */
	boolean amIDeaf() {
		return deaf;
	}
	
	/**
	 * @param state New state of deaf value
	 * @return old value of deaf hint.  
	 */
	boolean setDeaf( boolean state ) {
		boolean old = deaf; 
		deaf = state; 
		return old; 
	}
	
	/** 
	 * construct a FillerData on a CompactTree<BR>
	 * NOTE: it cannot be used before reset(CompactNode treeRoot) 
	 * has been called
	 * **/
	public FillerData() {
		deaf = false;
		// nothing to do here
	}
	
	/**
	 * reset all the datas in the FillerData handler<BR>
	 * Uses this when the structure of the CompactTree has changed
	 * @param treeRoot is the RootNode of this tree
	 */
	public void reset(CompactNode treeRoot) {
		assert treeRoot != null : 
			"Cannot attach a Filler on a null root node";
		
		// we are only interested by paths that contains TarifAssets
		// this first step determine which are the node we are interested in
		ArrayList/*<CompactNode>*/ nodeWithTarifAssets
			= treeRoot.contentsGetNodesWithClass(TarifAssets.class);
		
		// reset interstingNodes list
		interestingNodes = new ArrayList/*<CompactNode>*/();
		
		// Root node is always interesting
		interestingNodes.add(treeRoot);
		
		// add the path of those node to interesting node list
		Iterator/*<CompactNode>*/ i = nodeWithTarifAssets.iterator();
		CompactNode cn;
		while (i.hasNext()) {
			cn = (CompactNode) i.next();
			// add cn and cn parents (if not already in list)
			while (! interestingNodes.contains(cn) && cn != null) {
				interestingNodes.add(cn);
				cn = (CompactNode) cn.getParent();
			}
		}
		
		this.rootNode = treeRoot;
	
		
		cache = new HashMap/*<CompactNode,NodeInfo>*/();
		
		// construct the whole cache structure
		getNodeInfo(rootNode);
		
		// advertise my ui monitor
		if (uiMonitor != null) uiMonitor.fillerDataReseted();
	}
	
	
	
	
	
	/** 
	 * return the interesting childs of this CompactNode
	 */
	ArrayList/*<CompactNode>*/ getChildrenInteresting(
			CompactNode cn
	) {
		assert cn != null : "How a null node can have a child!!";
		assert interestingNodes.contains(cn) : "I don't care about node:"+cn;
		
		ArrayList/*<CompactNode>*/ result = 
			(ArrayList/*<CompactNode>*/) cn.getChildrenAL().clone();
		
		result.retainAll(interestingNodes);
		
		return result;
	}
	
	
	/** 
	 * get the FillerNode relative to this CompactNode
	 * @return null if there is no information about this node
	 */
	public FillerNode getFillerNode(CompactTreeItem cn) {
		assert cn != null : "Cannot retreive a null CompactNode";
		return (NodeInfo) cache.get(cn);
	}
	
	/** get the NodeInfo relative to this CompactNode **/
	NodeInfo getNodeInfo(CompactNode cn) {
		assert cn != null : 
			"Cannot retreive a null CompactNode";
		assert interestingNodes.contains(cn) : 
			"I don't care about node:["+cn+"]";
		
		NodeInfo result = (NodeInfo) cache.get(cn);
		if (result == null)  {
			result = new NodeInfo( this, cn );
			// node Infos will add themselves in the cache at creation
		}
		return result;
	}
	
	//-----------------Interface for the UI----------------------//
	
	/** Set the object that monitor me **/
	public void setUIMonitor(UIMonitor uiMonit) {
	    if (uiMonitor != null) {
	        m_log.error("Cannot change of monitor",new Exception());
	        return;
	    }
	    uiMonitor = uiMonit;
	}
	
	/** The Object that monitor me (if any) **/
	private UIMonitor uiMonitor;
	public interface UIMonitor {
	    public void fillerDataReseted();
	}
	
	
	//***************** DEBUG *************************//
	/** 
	 * Gives all info relative to a Node as an HTML-formatted String 
	 * Tags "&lt;HTML>&lt;/HTML> need to bee added to use this result.
	 * @return null for uninteresting nodes
	 */
	public String getInfosStr(CompactNode cn) {
		if ( cn == null ) return null; 
		if (! interestingNodes.contains(cn)) return null;
		
		NodeInfo ni = getNodeInfo(cn);
		
		StringBuffer sb = new StringBuffer("");
		sb.append(ni.toStringTitle()).append("<BR><TABLE>");
		
		ni.runOnChildren( new DebugInfoGenerator( sb ) );
		
		sb.append("</TABLE>");
		return sb.toString();
	}
	
	/**
	 * Acquire a calculation right. This means that a thread can 
	 * be launched doing non-thread safe operations on this instance
	 * in another thread than swings UI thread. 
	 * 
	 * @return true if the right has been granted. 
	 */
	public boolean acquireCalculationThread() {
//		try {
//			m_semCalculation.acquire();
//		}
//		catch ( InterruptedException e ) {
//			m_log.error( "Thread got interrupted." );
//			return false; 
//		}
		
		return true; 
	}
	
	/**
	 * Release a previously acquired calculation right. Other threads
	 * that may be waiting in line to start their computation may be 
	 * released now. 
	 */
	public void releaseCalculationThread() {
//		m_semCalculation.release();
	}
	
	// Inner classes ------------------------------------------------
	
	class DebugInfoGenerator implements FillerVisitor {
		StringBuffer m_buffer; 
		
		DebugInfoGenerator( StringBuffer buffer ) {
			m_buffer = buffer;
		}
		
		public void run(NodeInfo ni) {
			// XXX What info on nodes ? 
			m_buffer.append("<tr><td>");
			m_buffer.append( ni.toStringTitle() );
			m_buffer.append("</td></tr>");
		}
	}
	
}

/** A simple double container **/
class PairedSearch {
	/** The double value */
	public double value;
	public int pairCounter;
	public PairedSearch(double d) {
		value = d;
		pairCounter = 0;
	}
}

/*
*$Log: FillerData.java,v $
*Revision 1.2  2007/04/02 17:04:27  perki
*changed copyright dates
*
*Revision 1.1  2006/12/03 12:48:42  perki
*First commit on sourceforge
*
*Revision 1.41  2004/10/14 16:39:08  perki
**** empty log message ***
*
*Revision 1.40  2004/09/22 06:47:05  perki
*A la recherche du bug de Currency
*
*Revision 1.39  2004/09/09 12:43:08  perki
*Cleaning
*
*Revision 1.38  2004/09/03 12:24:06  perki
**** empty log message ***
*
*Revision 1.37  2004/09/02 15:51:46  perki
*Lot of change in calculus method
*
*Revision 1.36  2004/09/01 15:13:02  kaspar
*! Cache add was adding this instead of info..
*
*Revision 1.35  2004/08/27 11:24:53  kaspar
*! Moved all inner classes out of FillerData, this creates a more
*  lisible design
*
*Revision 1.33  2004/08/26 11:42:38  kaspar
*! Fixed the concurrent calculation bug, discovered thanks to
*  log4j
*+ added Semaphore, that implements a subset of java 1.5 Semaphore
*  class.
*
*Revision 1.32  2004/08/24 13:51:35  kaspar
*! Spell errors in documentation
*
*Revision 1.31  2004/08/24 12:57:16  kaspar
*! Documentation spelling fixed
*+ Added some documentation, trying to clarify
*! Changed invalid line endings
*
*Revision 1.30  2004/08/17 12:09:27  kaspar
*! Refactor: Using interface instead of class as reference type
*  where possible
*
*Revision 1.29  2004/08/05 13:03:54  perki
*debugging + pairing in compact tree
*
*Revision 1.28  2004/08/05 11:44:11  perki
*Paired compact Tree
*
*Revision 1.27  2004/08/04 16:40:08  perki
**** empty log message ***
*
*Revision 1.26  2004/08/04 06:03:12  perki
*OptionMoneyAmount now have a number of lines
*
*Revision 1.25  2004/08/02 15:24:44  perki
*Repartition viewer on simulator
*
*Revision 1.24  2004/08/02 14:17:11  perki
*Repartitions on Transactions Youhoucvs commit -m cvs commit -m
*
*Revision 1.23  2004/08/02 10:08:43  perki
*introducing distribution for transactions
*
*Revision 1.22  2004/08/02 09:40:43  kaspar
*! Display of pie pieces is now done with the same percentage
*  rounding that is done up in the panel
*
*Revision 1.21  2004/08/02 08:32:36  perki
**** empty log message ***
*
*Revision 1.20  2004/08/01 18:00:59  perki
**** empty log message ***
*
*Revision 1.19  2004/08/01 14:15:26  perki
*introducing rollout
*
*Revision 1.18  2004/08/01 12:23:08  perki
*Better show/hide extra parameter
*
*Revision 1.17  2004/07/31 12:01:00  perki
*Still have problems with the progressbar
*
*Revision 1.16  2004/07/31 11:06:55  perki
*Still have problems with the progressbar
*
*Revision 1.15  2004/07/30 17:52:46  perki
**** empty log message ***
*
*Revision 1.14  2004/07/30 15:38:19  perki
*some changes
*
*Revision 1.13  2004/07/30 11:28:39  perki
*Better tooltips
*
*Revision 1.12  2004/07/30 07:07:23  perki
*Moving Compact Tree from uicomponents to uicomponents.compact
*
*Revision 1.11  2004/07/30 05:50:01  perki
*Moved all CompactTree classes from uicompnents to uicomponents.compact
*
*Revision 1.10  2004/07/29 18:29:24  carlito
**** empty log message ***
*
*Revision 1.9  2004/07/29 13:41:24  carlito
**** empty log message ***
*
*Revision 1.8  2004/07/29 13:40:05  carlito
**** empty log message ***
*
*Revision 1.7  2004/07/29 11:38:13  perki
*Sliders should be ok now
*
*Revision 1.6  2004/07/29 10:42:20  perki
*Sliders should be ok now
*
*Revision 1.5  2004/07/27 17:54:05  perki
**** empty log message ***
*
*Revision 1.4  2004/07/27 16:56:31  perki
**** empty log message ***
*
*Revision 1.3  2004/07/27 15:33:18  perki
*minus bug in fillerdata
*
*Revision 1.2  2004/07/27 14:23:33  carlito
*Vistor renamed to FillerVisitor				      ^
*
*Revision 1.1  2004/07/26 17:39:36  perki
*Filler is now home
*
*/