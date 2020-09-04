/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: Tarif.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/
package com.simpledata.bc.datamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.dispatcher.DispatcherRoot;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.pair.Pairable;
import com.simpledata.util.CollectionsToolKit;

/**
* Tarif
* Container for a tarif, from this point defines, all spatial and logical 
* informations about a tarif
*/
public abstract class Tarif extends Named 
	implements WorkSheetContainer {
	private static final Logger m_log = Logger.getLogger( Tarif.class ); 
	
	/** RealClass type @see Named **/
	public final static String CLASS_TYPE= "TARIF";

	// WHEN YOU ADD A TARIF TYPE UPDATE TarifManager#tarifClassNames
	/** the tarif type of this class **/
	public final static String TARIF_TYPE= "UNKOWN";

	/** type **/
	protected String xType= "UNKOWN";

	/** Root WorkSheet, Initial WorkSheet **/
	protected WorkSheet xWorkSheet= null;

	//---------- Initialization ---------------//
	/**
	* Constructor. Should not be called by itself.. 
	* use Tarification.createTarif
	* @see Tarification#createTarif(String, String)
	*/
	protected Tarif(Tarification tarification, String title, String type) {
		super(CLASS_TYPE, tarification, title);
		this.xType= type;

	}
	
	//--------- Options -----------//
	/**
	 * link option to Tarif (Dependencies)
	 */
	public final void addLinkOption(BCOption bco) {
		getTarification().optionTarifDependecies.put(bco,this);
	}
	
	/**
	 * remove a tarif from this Option
	 */
	public final void removeLinkOption(BCOption bco) {
		getTarification().optionTarifDependecies.remove(bco,this);
	}
	
	
	
	
	//--------- Contextual ---------//
	
	/**
	 * get the direct sons (Tarifs) of this tarif in a contextual matter
	 * @see com.simpledata.bc.datamodel.compact.
	 * CompactTreeSearch#getDirectSonsOf(Tarif t);
	 */
	public final ArrayList/*<Tarif>*/ getDirectSons() {
		return getTarification().getCompactTree().getSearcher().
		getDirectSonsOf(this);
	}
	
	/**
	 * Gets the parent of this Tarif in a contextual manner.
	 * @return List of Tarif. 
	 * @see com.simpledata.bc.datamodel.compact.
	 * CompactTreeSearch#getDirectParentsOf(Tarif t);
	 */
	public final ArrayList/*<Tarif>*/ getDirectParents() {
		return getTarification().getCompactTree().getSearcher().
			getDirectParentsOf( this );
	}
	
	
	/** 
	 * Tagging interface made, All tarifs that implements this interface
	 * may accept options from "sons" tariffs
	 */
	public interface TarifTransferRoot { }
	
	/**
	 * Get WorkSheet that transfer options to me<BR>
	 * Tarif with a DispatcherRoot as first Tarif and a TransferOption as
	 * second WorkSheet<BR>
	 * This method only works for Tarifs that implements 
	 * TarifTransferRoot interface
	 */
	public final ArrayList/*<DispatcherRoot>*/ 
	getWorkSheetThatTransferOptionsToMe() {
		ArrayList/*<DispatcherRoot>*/  result 
		= new ArrayList/*<DispatcherRoot>*/();
		
		if (! (this instanceof TarifTransferRoot)) {
		    m_log.error("I do not accept option transfers to me "+this,new Exception());
		    return result;
		}
		
		
		Iterator/*<Tarif>*/ i = getDirectSons().iterator();
		WorkSheet ws ;
		WorkSheet ws2 ;
		Tarif son;
		while (i.hasNext()) {
		    son = ((Tarif) i.next());
		    //m_log.debug(this+" son "+son);
			ws = son.getWorkSheet();
			if (ws != null && ws instanceof DispatcherRoot) {
				// look if the worplace of this dispatcher is a TransferOption
				ws2 = ((DispatcherRoot) ws).getWorkSheet(); 
				if (ws2 instanceof WorkPlaceTransferOptions) {
					if (ws2.isValid()) {
						if (! result.contains(ws)) result.add(ws);
					}
				}
			} 
		}
		
		
		return result;
	}

	//--------- WorkSheet ---------//
	/**
	* return the root WorkSheet of this Tarif
	*/
	public final WorkSheet getWorkSheet() {
		return xWorkSheet;
	}


	
	/**
	* return contained WorkSheets
	*/
	public ArrayList getChildWorkSheets() {
		ArrayList v= new ArrayList();
		v.add(xWorkSheet);
		return v;
	}

	/**
	 * get if this Tarif accept this WorkSheet for this key<BR>
	 */
	public boolean acceptsNewWorkSheet(Class c, String key) {
		Class[] cs= getAcceptedNewWorkSheets("");
		if (cs == null)
			return true;
		for (int i= 0; i < cs.length; i++) {
			if (cs[i] == c)
				return true;
		}
		return false;
	}

	/**
	* set the root workSheet of this Tarif<BR>
	* implements WorkSheetContainer @see WorkSheetContainer
	* @param ws te WorkSheet to attach to this Tarif
	* @param key as no use for Tarifs, present for compliance with interface
	*/
	public boolean setWorkSheet(WorkSheet ws, String key) {
		// check if this ws can be added
		if (!acceptsNewWorkSheet(ws.getClass(), key))
			return false;

		xWorkSheet= ws;
		return true;
	}

	/**
	* implements WorkSheetContainer @see WorkSheetContainer
	*/
	public Tarif getTarif() {
		return this;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.WorkSheetContainer#getWorkSheetKey(com.simpledata.bc.datamodel.WorkSheet)
	 */
	public String getWorkSheetKey(WorkSheet ws) {
		return "";
	}

	public WorkSheet getWorkSheetAt(String key) {
		return xWorkSheet;
	}

	/** 
	 * return true if this Tarif contains a WorkSheet that should be displayed
	 * in a "Other" node instead of the mapped node (ask Charles if don't get it)
	 * If the absolute mapped node (hehe) has typical option then return true
	 * (ie : un tarif de type avoir est specialis� 
	 * 	--> le noeud auquel il est attach�
	 * pr�sente une option "montant")
	 */
	public abstract boolean isSpecialized();
	
	
	//-------- Calculus Handeling -------------//
	
	/** 
	 * <B>Interface ComModifier</B> get the reduction<BR>
	 * Always return null;
	 **/
	public ReducOrFixed getReductionOrFixed() {
        return null;
     }
	
	/** <B>Interface ComModifier</B> start calculus at this point **/
	public final void startComCalc(ComCalculus cc,
			Money value,
			Set/*<ComModifier>*/ toAdvertise) {
		
		
		// get value from my root WorkSheet
		if (getWorkSheet() != null) {
			value.setValue(cc.getCom(getWorkSheet()));
		}
		
		// remove value from all workSheets that transfer value to me
		if (this instanceof TarifTransferRoot) {
			Iterator/*<WorkSheet>*/ i = 
				getWorkSheetThatTransferOptionsToMe().iterator();
			WorkSheet ws;
			while (i.hasNext()) {
				ws = (WorkSheet) i.next();
				value.operation(cc.getCom(ws),-1);
			}
		}
		// advertise options links to my Tarif
		Iterator e = getLinkedOptions().iterator();
		while (e.hasNext()) {
			toAdvertise.addAll(((BCOption) e.next()).getWorkSheets());
		}
	}
	

	
	/** <B>Interface ComModifier</B> start calculus at this point **/
	public final String getComTitle() {
		return getTitle();
	}
	


	//-------- Local data Handeling -----------//

	/**
	* return the Type of this tarif @see TypesAndConstraints
	*/
	public String getType() {
		return xType;
	}

	//-------- Mapping Handeling -----------//

	/**
	 * return a ArrayList showing the future possible spatialization
	 * of this tarif if it gets mapped to this node. taking care of 
	 * removing it from its parents
	 */
	public ArrayList futureMappingTo(BCNode node) {
		//	v is the vector representing the actual mapping
		ArrayList v= getMyMapping();

		if (v.contains(node))
			return (ArrayList) v.clone(); //already mapped

		// The tree we are wotking on
		BCTree bctree= node.getTree();

		// result is the vector we manipulate	
		ArrayList result= new ArrayList();

		//	on base tree Tarif can be mapped only once
		if (bctree == getTarification().treeBase) {
		    result.addAll(v);
		    for (Iterator i = result.iterator(); i.hasNext(); ){
		        if (((BCNode) i.next()).getTree() == getTarification().treeBase)
		            i.remove();
		    }
			result.add(node);
			return result;
		}

		//	search this vector and remove parents of node
		for (int i= 0; i < v.size(); i++) {
			BCNode n= (BCNode) v.get(i);
			if (!node.isAncestorOf(n))
				if (!n.isAncestorOf(node)) {
					result.remove(n); //to avoid double entries
					result.add(n);
				}
		}

		// if an ancestor is checked then it's an unckeck of me and check all
		// childrens of this ancestor
		ArrayList ancestors= bctree.getAncestorsOf(node);
		ancestors.remove(node);
		CollectionsToolKit.collectionsInclusion(ancestors, v);
		// ancestors now represents only ancestors of node mapping this
		if (ancestors.size() > 0) { // an ancestor is checked
			// check all known childrens of this ancestor
			for (int i= 0; i < ancestors.size(); i++) {
				ArrayList childrens=
					bctree.getChildrensRecursively((BCNode) ancestors.get(i));
				for (int j= 0; j < childrens.size(); j++) {
					// add only childrens of my ancestor not mine
					if (!node.isAncestorOf((BCNode) childrens.get(j)))
						result.add(childrens.get(j));
				}
			}
		} else { // no ancestors are checked
			result.add(node);
		}

		// remove all ancestors of this node
		ArrayList temp= bctree.getAncestorsOf(node);
		temp.remove(node);
		for (int i= 0; i < temp.size(); i++) {
			result.remove(temp.get(i));
		}

		// compress the tree 
		// (ie check parents that have all their childrens checked)
		// but do not check root nodes
		// do not do this on node that have only one children
		boolean inspect= true;
		while (inspect) {
			inspect= false;
			ArrayList nodesToInspect= new ArrayList();
			// contains all parents of result
			BCNode tempNode;
			for (Iterator/*<BCNode>*/ i = result.iterator(); i.hasNext(); ) {
				tempNode = (BCNode) i.next();
				
				if (!nodesToInspect.contains(tempNode.getParent()))
					nodesToInspect.add(tempNode.getParent());
			}
		
			
			for (Iterator/*<BCNode>*/ i = nodesToInspect.iterator()
					; i.hasNext(); ) {
				tempNode = (BCNode) i.next();
				if (tempNode != null) {
					ArrayList v2= tempNode.getChildrens();
					if ((v2.size() > 1) && (result.containsAll(v2))) {
						result.removeAll(v2);
						if (! result.contains(tempNode))
							result.add(tempNode);
						inspect= true;
					}
				}
			}
		}

		// uncheck all nodes that have their parents checked
		ArrayList temp2= (ArrayList) result.clone();
		for (int i= 0; i < temp2.size(); i++) {
			BCNode bcTemp= (BCNode) temp2.get(i);
			if (result.contains(bcTemp.getParent())) {
				result.remove(bcTemp);
				result.removeAll(bctree.getChildrensRecursively(bcTemp));
			}
		}
		
		// be sure there is at least one mapping in base tree
		beSureitBelongsTobaseTree(result);
		

		return result;

	}
	
	/**
	 * check that this mapping contains at least one node in the base tree
	 * if not, add the root node to this mapping
	 */
	private void beSureitBelongsTobaseTree(ArrayList mapping) {
		boolean ok = false;
		Iterator i = mapping.iterator();
		while (i.hasNext()) {
			if (((BCNode)i.next()).getTree()==getTarification().getTreeBase()) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			mapping.add(getTarification().getTreeBase().getRoot());
		}
	}

	/**
	* check if this tarif can be unMapped to this node
	* @param userAlert true if you want alert message to users
	* @param checkFutureMapping set to true if you want to check if a node will have the same position than another.
	*/
	public boolean canBeRemovedFrom(
		BCNode node,
		boolean checkFutureMapping,
		boolean userAlert) {
		if (node == null) {
			return false;
		}
		
		// cannot remove node from Base Tree
		if (node.getTree() == getTarification().getTreeBase())
			return false;
		
		// get the future mapping
		ArrayList future= ((ArrayList) getMyMapping().clone());

		if (!future.contains(node))
			return false;

		if (!checkFutureMapping)
			return true;

		// remove the node from the future vector
		future.remove(node);
		
		// be sure there is at least one mapping in base tree
		beSureitBelongsTobaseTree(future);

		//	existing nodes....
		if (!isMappingCorrect(future, null)) {
			if (userAlert)
				m_log.info( "A node is already mapping this position" );
			return false;
		}

		return true;
	}

	/**
	 * check if this tarif can be mapped to this node
	 * @param checkFutureMapping set to true if you want to check if a node will have the same position than another.
	 * @param userAlert true if you want canBeMappedTo to generate user alerts
	 */
	public boolean canBeMappedTo(
		BCNode node,
		boolean checkFutureMapping,
		boolean userAlert) {
		
		// if node is a root node of a tree then NO
		// (only allowed for root of base tree)
		if (node.isRoot() && 
				(node.getTree() != getTarification().getTreeBase())) 
			return false;
			
		// get the future mapping of this node and check with 

		// check if this node accept this kind of tarif
		if (!node.acceptThisTarifType(getType())) {
			if (userAlert)
				m_log.info( "This node does not accept this type of tarif" );
			return false;
		}

		if (!checkFutureMapping)
			return true;

		//		existing nodes....
		if (!isMappingCorrect(futureMappingTo(node), null)) {
			if (userAlert)
				m_log.info( "A node is already mapping this position" );
			return false;
		}

		return true;
	}
	
	
	/** return true if one workSheet has a reduction **/
	public boolean hasReduction() {
		// if mapping is not valid return false
		if (! isMappingCorrect(getMyMapping(), null)) return false;
		
		// look if all my workplace are valid
		if (getWorkSheet() == null) return false;
		
		class Temp { boolean b; }
		final Temp t = new Temp();
		t.b = false;
		
		WorkSheet.Visitor wv = new WorkSheet.Visitor() {
            public boolean worksheetVisited(WorkSheet ws) {
                if (ws.getReductionOrFixed() != null) {
                    t.b = true;
                    return false;
                }
                return true;
            }
		};
		
		getWorkSheet().runOnChildren(wv);
		
		return t.b;
	}
	

	/** return true if this Tarif is valid **/
	public boolean isValid() {
		// if mapping is not valid return false
		if (! isMappingCorrect(getMyMapping(), null)) return false;
		
		// look if all my workplace are valid
		if (getWorkSheet() == null) return false;
		
		if (! getWorkSheet().isValidAndChildrensToo()) return false;
		
		return true;
	}

	/**
	 * get if mapping is correct on this tree with those tarifs
	 * @param bcnodes a vector containaing the position to check (IN ALL TREES!!!)
	 * @param tarifsToCompare the tarifs we want to verify. CAN BE NULL IF CHECK APPLIES ON ALL KNOWN TARIFS
	 */
	private boolean isMappingCorrect(ArrayList bcnodes, ArrayList tarifsToCompare) {
		// if tarifsToCompare this does means all that matches me
		if (tarifsToCompare == null)
			tarifsToCompare= getTarification().getTarifsAt(bcnodes);

		// TODO maybe only use Trees of this set of Tarif
		BCTree[] trees= getTarification().getMyTrees();

		// keep only tarifs that have exaclty the same mapping than me
		Iterator e= ((ArrayList) tarifsToCompare.clone()).iterator();
		Tarif t= null;
		while (e.hasNext()) {
			t= (Tarif) e.next();

			boolean keep= (t != this);
			for (int i= 0; i < trees.length && keep; i++) {
				keep=
					!AContainsNOneOfB(getNodesBelongingToTree(trees[i],
						bcnodes),
						t.getMyMapping(trees[i]));
			}

			if (!keep) {
				tarifsToCompare.remove(t);
			}
		}

		// if there still Tarif then bahhh 
		return (tarifsToCompare.size() == 0);
	}

	public static boolean AContainsNOneOfB(Collection a, Collection b) {
		if (a.size() + b.size() == 0)
			return false;
		Iterator e= b.iterator();
		while (e.hasNext())
			if (a.contains(e.next()))
				return false;

		return true;
	}

	/**
	* map this tarif to this node. Will be done only if NO other nodes map 
	* this new position
	* @return true if done
	* @param force set to true if you want to map this node even if there is 
	* another Tarif matching this position. 
	*/
	public boolean mapTo(BCNode node, boolean force) {
		return mapTo(node,force,true);
	}
	
	/**
	 * same as mapTo plus forward to paired
	 * @param forwardToPaired if true, this mapping will be forwarded to pair
	 * if needed
	 * @see #mapTo(BCNode node, boolean force)
	 */
	private boolean mapTo(BCNode node, boolean force,boolean forwardToPaired) {
		if (getMyMapping().contains(node)) return true; // already mapped
		if (!canBeMappedTo(node, !force, true)) {
			m_log.debug( "not mapped" );
			return false;
		}
		
		applyMapping(futureMappingTo(node));
		
		
		// if I'm paired
		// if this node is on base tree then we just break the link
		if (forwardToPaired && (this instanceof Pairable)) {
			Tarif pair = ((Pairable) this).pairedGet();
			if (pair != null) {
				if (getTarification().getTreeBase() == node.getTree()) {
					((Pairable) this).pairedBreak();
				} else {
					// apply this move to my Pair (if any)
					// and tell him not to forward this to me
					pair.mapTo(node,true,false); 
					
				}
			}
		}
		fireNamedEvent(NamedEvent.TARIF_MAPPING_MODIFIED);
		return true;
	}
	
	
	/** 
	 * apply the passed mapping to me
	 */
	private void applyMapping(ArrayList/*<BCnode>*/ mapping) {
		ArrayList toRemove=
			CollectionsToolKit.getInAnotInB(getMyMapping(), mapping);
		ArrayList toAdd=
			CollectionsToolKit.getInAnotInB(mapping, getMyMapping());

		// remove olds
		for (int i= 0; i < toRemove.size(); i++) {
			xTarification.tarifsNodesMap.remove(this, toRemove.get(i));
		}

		//add news
		for (int i= 0; i < toAdd.size(); i++) {
			xTarification.tarifsNodesMap.put(this, toAdd.get(i));
		}
	}
	

	/**
	* unMap this tarif from this node.  Will be done only if NO other nodes
	*  map this position
	* @parm force unMap node even if matching the position of another Tarif
	* @return true if done
	*/
	public boolean unMapFrom(BCNode node, boolean force) {
		return unMapFrom(node,force,true);
	}
	/**
	 * same as unMapFrom plus forward to paired
	 * @param forwardToPair if true, this mapping will be forwarded to pair
	 * if needed
	 * @see #unMapFrom(BCNode node, boolean force);
	 */
	public boolean unMapFrom(BCNode node, boolean force,boolean forwardToPair) {
		if (!canBeRemovedFrom(node, !force, true)) return false;
		ArrayList/*<BCNode>*/ future = getMyMapping();
		future.remove(node);
		beSureitBelongsTobaseTree(future);
		applyMapping(future);
		fireNamedEvent(NamedEvent.TARIF_MAPPING_MODIFIED);
		
		// if I'm paired
		// if this node is on base tree then we just break the link
		if (forwardToPair && (this instanceof Pairable)) {
			Tarif pair = ((Pairable) this).pairedGet();
			if (pair != null) {
				if (getTarification().getTreeBase() == node.getTree()) {
					((Pairable) this).pairedBreak();
				} else {
					// apply this move to my Pair (if any)
					// and tell him not to forward this to me
					pair.unMapFrom(node,true,false); 
					
				}
			}
		}
		
		
		return true;
	}

	

	/**
	 * refresh the mapping of a tarif (occurs for node moving)
	 */
	public void refreshMap(BCNode node) {
		if (getMyMapping().contains(node)) {
			unMapFrom(node, true);
			mapTo(node, true);
		}
	}

	/**
	* return a ArrayList containing all the nodes this Tarif is mapped to 
	*/
	public ArrayList getMyMapping(BCTree tree) {
		return getNodesBelongingToTree(tree, getMyMapping());
	}
	
	/**
	* return a ArrayList containing all the trees I belong to 
	*/
	public ArrayList getMyTrees() {
		ArrayList result = new ArrayList();
		Iterator e = getMyMapping().iterator();
		Object temp = null;
		while (e.hasNext()) {
			temp = ((BCNode) e.next()).getTree();
			if (! result.contains(temp)) {
				result.add(temp);	
			}
		}
		return result;
	}

	/** 
	 *tool that extract from a vector of BCNodes the ones that belongs to
	 *the given BCTree
	 **/
	private static ArrayList getNodesBelongingToTree(
		BCTree tree,
		ArrayList bcnodes) {
		ArrayList result= new ArrayList();
		for (int i= 0; i < bcnodes.size(); i++) {
			if (((BCNode) bcnodes.get(i)).getTree() == tree)
				result.add(bcnodes.get(i));
		}
		return result;
	}

	/**
	* return a ArrayList containing all the nodes this Tarif is mapped to
	* this vector does not represent the data stored an can be manipulated
	*/
	public ArrayList getMyMapping() {
		return xTarification.tarifsNodesMap.getRightOf(this);
	}

	/**
	 * return true if this tarif maps this node
	 */
	public boolean isMapping(BCNode node) {
		return xTarification.tarifsNodesMap.pairExists(this, node);
	}
	
	
	

	/**
	* remove true if a tarif can be dropped<BR>
	* You may want to check getLinkedOptions() that would cause
	* a false if this Tarif is linked to options
	*/
	public boolean canBeDroppped() {
		return (getLinkedOptions().size() == 0);
	}

	/**
	 * get the options that are using this Tarif
	 */
	public ArrayList getLinkedOptions() {
		return xTarification.optionTarifDependecies.getLeftOf(this);
	}

	/**
	* remove a Tarif<BR>
	* will take care of unlinking references to this Tarif<BR>
	* you may want to use canBeDroppped() to check if 
	* this tarif can be dropped
	* @return true if canBeDroppped()
	*/
	public boolean drop() {
		if (!canBeDroppped()) {
		    m_log.warn("cannot drop "+this+" they are linked options");
			return false;
		}
		
		xTarification.tarifsNodesMap.remove(this, null);
		// if paired.. break the pairing
		if (this instanceof Pairable) {
			((Pairable) this).pairedBreak();
		}
		
		if (xWorkSheet != null) {
		    xWorkSheet.setContainer(null);
		    xWorkSheet.drop();
			xWorkSheet = null;
		}
	
		
		//advertise listeners
		fireNamedEvent(NamedEvent.TARIF_MAPPING_MODIFIED);
		return true;
	}

	//------------------- debuging interface ---------------//
	/**
	* present data contained in this Tarif
	*/
	public String toString() {
		return getTitle();
	}

	/** 
	* present data contained in this Tarif
	*/
	public String toStringFull() {
		StringBuffer sb= new StringBuffer();
		sb.append("#######TARIF###########\n").append(getFullNID());
		// --- add Node mapping ---//
		sb.append("Mapped to :\n");
		ArrayList v= xTarification.tarifsNodesMap.getRightOf(this);
		Iterator e= v.iterator();
		while (e.hasNext()) {
			sb.append("   ").append(
				((BCNode) e.next()).getFullNID()).append(
				"\n");
		}
		return sb.toString();
	}

	//-------------------- XML -------------------//
	/** XML **/
	public Tarif() {}

	/**
	 * XML
	 */
	public String getXType() {
		return xType;
	}

	/**
	 * XML
	 */
	public void setXType(String string) {
		xType= string;
	}

	/**
	 * XML
	 */
	public void setXWorkSheet(WorkSheet sheet) {
		xWorkSheet= sheet;
	}

	/**
	 * XML
	 */
	public WorkSheet getXWorkSheet() {
		return xWorkSheet;
	}

}
/** $Log: Tarif.java,v $
/** Revision 1.2  2007/04/02 17:04:23  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:36  perki
/** First commit on sourceforge
/**
/** Revision 1.96  2004/11/17 17:44:04  perki
/** corrected bug #3
/**
/** Revision 1.95  2004/11/17 09:09:49  perki
/** first step of discount extraction
/**
/** Revision 1.94  2004/11/11 12:38:02  perki
/** New distribution system for the Filler
/**
/** Revision 1.93  2004/11/08 17:29:10  perki
/** *** empty log message ***
/**
/** Revision 1.92  2004/10/31 13:23:02  perki
/** Coorected HUGE BUG in calculus with transfer options
/**
/** Revision 1.91  2004/10/14 16:39:08  perki
/** *** empty log message ***
/**
/** Revision 1.90  2004/10/11 17:48:08  perki
/** Bobby
/**
/** Revision 1.89  2004/09/29 12:40:19  perki
/** Localization tarifs
/**
/** Revision 1.88  2004/09/16 17:26:37  perki
/** *** empty log message ***
/**
/** Revision 1.87  2004/09/09 12:43:08  perki
/** Cleaning
/**
/** Revision 1.86  2004/09/08 19:28:55  perki
/** Reaprtition now follows Transfer Options
/**
/** Revision 1.85  2004/09/08 16:35:14  perki
/** New Calculus System
/**
/** Revision 1.84  2004/09/03 12:22:28  kaspar
/** ! Log.out -> log4j second part
/**
/** Revision 1.83  2004/09/03 11:09:30  perki
/** *** empty log message ***
/**
/** Revision 1.82  2004/08/24 12:57:16  kaspar
/** ! Documentation spelling fixed
/** + Added some documentation, trying to clarify
/** ! Changed invalid line endings
/**
/** Revision 1.81  2004/08/01 15:48:43  perki
/** Pairing forward moves
/**
/** Revision 1.80  2004/07/31 17:43:30  perki
/** Pairing ok ... it was a hard time
/**
/** Revision 1.79  2004/07/31 16:45:56  perki
/** Pairing step1
/**
/** Revision 1.78  2004/07/19 09:36:54  kaspar
/** * Added Visitor for visiting the whole Tarif structure called
/**   TarifTreeVisitor
/** * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
/** * removed superfluous hsqldb.jar
/**
/** Revision 1.77  2004/07/15 17:49:56  perki
/** grading better
/**
/** Revision 1.76  2004/07/08 14:59:00  perki
/** Vectors to ArrayList
/**
/** Revision 1.75  2004/07/06 14:55:30  perki
/** Better node mapping (no more root problems)
/**
/** Revision 1.74  2004/07/05 07:24:03  perki
/** parent / sons forward is now working
/**
/** Revision 1.73  2004/07/04 10:57:45  perki
/** *** empty log message ***
/**
/** Revision 1.72  2004/07/02 09:37:31  perki
/** *** empty log message ***
/**
/** Revision 1.71  2004/06/30 08:59:18  carlito
/** web improvment and dispatcher case debugging
/**
/** Revision 1.70  2004/06/28 10:38:47  perki
/** Finished sons detection for Tarif, and half corrected bug for edition in STable
/**
/** Revision 1.69  2004/06/25 10:09:49  perki
/** added first step for first sons detection
/**
/** Revision 1.68  2004/04/12 12:33:09  perki
/** Calculus
/**
/** Revision 1.67  2004/04/12 12:30:28  perki
/** Calculus
/**
/** Revision 1.66  2004/04/09 07:16:51  perki
/** Lot of cleaning
/**
/** Revision 1.65  2004/03/23 19:45:18  perki
/** New Calculus Model
/**
/** Revision 1.64  2004/03/18 16:26:54  perki
/** new option model
/**
/** Revision 1.63  2004/03/18 12:25:30  perki
/** yeah
/**
/** Revision 1.62  2004/03/18 09:02:29  perki
/** *** empty log message ***
/**
/** Revision 1.61  2004/03/17 14:56:53  perki
/** *** empty log message ***
/**
/** Revision 1.60  2004/03/17 14:28:53  perki
/** *** empty log message ***
/**
/** Revision 1.59  2004/03/16 14:09:31  perki
/** Big Numbers are welcome aboard
/**
/** Revision 1.58  2004/03/13 18:12:16  perki
/** Ah ah ah aha ah ah aAAAAAAAAAAAAAA
/**
/** Revision 1.57  2004/03/08 09:56:36  perki
/** houba houba hop
/**
/** Revision 1.56  2004/03/08 08:46:03  perki
/** houba houba hop
/**
/** Revision 1.55  2004/03/06 14:24:50  perki
/** Tirelipapon sur le chiwawa
/**
/** Revision 1.54  2004/03/04 18:44:23  perki
/** *** empty log message ***
/**
/** Revision 1.53  2004/03/03 10:17:23  perki
/** Un petit bateau
/**
/** Revision 1.52  2004/02/26 13:24:34  perki
/** new componenents
/**
/** Revision 1.51  2004/02/25 17:36:54  perki
/** *** empty log message ***
/**
/** Revision 1.50  2004/02/25 16:34:05  perki
/** *** empty log message ***
/**
/** Revision 1.49  2004/02/25 15:16:03  perki
/** *** empty log message ***
/**
/** Revision 1.48  2004/02/24 10:35:19  perki
/** *** empty log message ***
/**
/** Revision 1.47  2004/02/24 09:48:38  perki
/** *** empty log message ***
/**
/** Revision 1.46  2004/02/23 18:46:04  perki
/** *** empty log message ***
/**
/** Revision 1.45  2004/02/22 18:09:20  perki
/** good night
/**
/** Revision 1.44  2004/02/22 10:43:57  perki
/** File loading and saving
/**
/** Revision 1.43  2004/02/20 00:07:40  perki
/** now 1Gig of ram de la balle de balle
/**
/** Revision 1.42  2004/02/19 23:57:25  perki
/** now 1Gig of ram
/**
/** Revision 1.41  2004/02/19 19:47:34  perki
/** The dream is coming true
/**
/** Revision 1.40  2004/02/17 13:36:24  perki
/** zobi la mouche n'a pas de bouche
/**
/** Revision 1.39  2004/02/17 11:39:21  perki
/** zobi la mouche n'a pas de bouche
/**
/** Revision 1.38  2004/02/17 10:45:03  carlito
/** new Tarif Wizard and WorkSheetTree adapted to WorkSheetPanel
/**
/** Revision 1.37  2004/02/16 10:56:41  perki
/** new event model
/**
/** Revision 1.36  2004/02/14 21:53:26  carlito
/** *** empty log message ***
/**
/** Revision 1.35  2004/02/06 19:02:45  perki
/** *** empty log message ***
/**
/** Revision 1.34  2004/02/06 18:07:13  perki
/** *** empty log message ***
/**
/** Revision 1.33  2004/02/06 17:58:41  perki
/** Events
/**
/** Revision 1.32  2004/02/06 15:07:44  perki
/** New nodes
/**
/** Revision 1.31  2004/02/06 10:04:22  perki
/** Lots of cleaning
/**
/** Revision 1.30  2004/02/05 15:11:39  perki
/** Zigouuuuuuuuuuuuuu
/**
/** Revision 1.29  2004/02/04 19:04:19  perki
/** *** empty log message ***
/**
/** Revision 1.28  2004/02/04 17:38:04  perki
/** cleaning
/**
* Revision 1.27  2004/02/04 11:11:35  perki
* *** empty log message ***
*
* Revision 1.26  2004/02/03 11:31:17  perki
* totally new double sided map
*
* Revision 1.25  2004/02/02 16:32:06  perki
* yupeee
*
* Revision 1.24  2004/02/02 11:21:05  perki
* *** empty log message ***
*
* Revision 1.23  2004/02/02 07:00:50  perki
* sevral code cleaning
*
* Revision 1.22  2004/02/01 18:27:51  perki
* dimmanche soir
*
* Revision 1.21  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.20  2004/01/30 15:18:12  perki
* *** empty log message ***
*
* Revision 1.19  2004/01/28 15:31:48  perki
* Il neige plus
*
*/