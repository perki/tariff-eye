/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactTreeSearch.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
package com.simpledata.bc.datamodel.compact;

import java.util.*;

import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.tools.DoubleSideMap;

/**
 * This class, is a tool for optimising CompactTree Searches.
 */
public class CompactTreeSearch {
	
	/** the compact tree I'am monitoring **/
	private CompactTree ct;
	
	/** the last known root node of this Compact Tree **/
	private CompactTreeNode ctRoot;
	
	
	/** the hashMap that contains the tarif -> CompactNode mapping **/
	private HashMap/*<Tarif,ArrayList<CompacTreeNode>>*/ tarifMapping;
	
	/** 
	 * the hashMap that contains the Tarif directSons mapping <BR>
	 * left: parents<BR>
	 * right:sons
	 **/
	private DoubleSideMap/*<Tarif,Tarif>*/ tarifSons;
	
	/** 
	 * a ArrayList that contains the Tarif for which TarifSons has beend 
	 * processed. This is just a "Marker" vector
	 */
	private ArrayList/*<Tarif>*/ computedDirectTarifSons;
	
	/** 
	 * a ArrayList that contains the Direct Tarif for which TarifParents 
	 * has beend processed This is just a "Marker" vector
	 */
	private ArrayList/*<Tarif>*/ computedDirectTarifParents;
	
	
	
	public CompactTreeSearch(CompactTree c) {
		ct = c;
	}
	
	/** 
	 * called when the CompactTreeNode has been changed and the 
	 * references need to be rebuilded
	 */
	public void reset() {
		ctRoot = ct.getRoot();
		tarifMapping = new HashMap/*<Tarif,ArrayList<CompacTreeNode>>*/();
		tarifSons = new DoubleSideMap/*<Tarif,Tarif>*/(true);
		computedDirectTarifSons = new ArrayList/*<Tarif>*/();
		computedDirectTarifParents = new ArrayList/*<Tarif>*/();
	}
	
	/** check if the CompactTreeNode need to be rebuilded .. do so if needed */
	public void checkReset() {
		if (ct.getRoot() != ctRoot) reset();
	}
	
	//----------------- MAPPING --------------------------------//
	
	/** get an ArrayList of CompactTreeNode containing this Tarif **/
	public ArrayList/*<CompactTreeNode>*/ getTarifMapping(Tarif t) {
		checkReset(); // clean if needed
		ArrayList/*<CompactTreeNode>*/ result = (ArrayList)tarifMapping.get(t);
		if (result != null) return result;
		
		// construct a new result
		result = new ArrayList/*<CompactTreeNode>*/();
		_getTarifMappingRecur(t,ctRoot,result);
		
		// store the result
		tarifMapping.put(t,result);
		
		return result;
	}
	
	/** recursive helper to construct a Tarif Mapping **/
	private void _getTarifMappingRecur(Tarif t,CompactTreeNode ctn,
			ArrayList result) {
		if (ctn.isMappingTarif(t)) { 
			result.add(ctn);
		}
		Iterator/*<CompactTreeNode>*/ i = ctn.getChildren().iterator();
		while (i.hasNext()) {
			_getTarifMappingRecur(t,(CompactTreeNode)i.next(),result);
		}
	}
	
	
	//------------------- PARENTS -------------------------------//
	
	
	/** 
	 * Gets parent(s) of a Tarif. A parent of a tarif is a Tarif that
	 * is applicable to the whole group of things of which the Tarif 
	 * t is applicable to only one.
	 * 
	 * Example: <code>Avoirs non specialis�es</code> is applicable to all the group
	 * <code>Avoirs</code>, but a given Tarif might only be applicable to  
	 * <code>Avoirs/Cash</code>. The Tarif below 
	 * <code>Avoirs non specialis�es</code> would
	 * then be the parent. 
	 * 
	 * Note: a tarif with more than one parent means those parents are
	 * in an unstable state.
	 * @return List of Tarif. 
	 */ 
	public ArrayList/*<Tarif>*/ getDirectParentsOf( Tarif t ) {
		checkReset(); // clean if needed
		
		// this is a known Tarif ?
		if ( computedDirectTarifParents.contains(t) ) {
			return tarifSons.getLeftOf(t);
		}
		
		// unknown: update it
		computedDirectTarifParents.add(t);
		
		ArrayList/*<Tarif>*/ result = _getParentOf( t, true );
		
		// fill the Double Sided Map 
		Iterator it = result.iterator();
		while (it.hasNext()) {
			tarifSons.put( it.next(),t );
		}
		
		return result;
	}
	
	
	/**
	 * tool for getDirectParent and getAllParents
	 */
	private ArrayList/*<Tarif>*/ _getParentOf(Tarif t,boolean onlyDirect) {
		ArrayList/*<Tarif>*/ result = new ArrayList/*<Tarif>*/();
		//		 get the mapping of this Tarif
		Iterator/*<CompactTreeNode>*/ i = getTarifMapping(t).iterator();
		while (i.hasNext()) {
			// look for first parent with Tarifs
			_getParentOfHelper(
					((CompactTreeNode) i.next()).getParent(),result,onlyDirect);
		}
		return result;
	}
	
	/** get parent recursive helper <BR>
	 * recursively look for Tarif in parent path 
	 * @param onlyDirect set to true if only Direct Parents are needed
	 * */
	private void _getParentOfHelper(CompactTreeNode ctn,
			ArrayList result,boolean onlyDirect) {
		if (ctn == null) return;
		
		ArrayList/*<CompactTreeTarifRef>*/ temp = ctn.getTarifsRefs();		
	
		Iterator/*<CompactTreeTarifRef>*/ i = temp.iterator(); 
		CompactTreeTarifRef ttemp ;
		while (i.hasNext()) {
			ttemp = (CompactTreeTarifRef) i.next();
			if (! result.contains(ttemp.getTarif())) {
				result.add(ttemp.getTarif());
			}
		}
		
		// forward to parent, if not only direct or no tarif found on path
		if ((! onlyDirect || temp.size() == 0) && ctn.getParent() != null) {
			_getParentOfHelper(ctn.getParent(),result,onlyDirect);
		}
		
	}
	
	
	/**
	 * Gets all the parents (ancestors of a Tarif)
	 * ordered in path.
	 * @return List of Tarif.
	 */
 	public ArrayList/*<Tarif>*/ getAllParentsOf(Tarif t) {
 		checkReset(); // clean if needed
 		return  _getParentOf(t,false);
 	}
	
	//------------------------ SONS --------------------------------//
	
	/** get direct sons of a Tarif **/
	public ArrayList/*<Tarif>*/ getDirectSonsOf(Tarif t) { 
	    
		checkReset(); // clean if needed
		
		// this is a known Tarif
		if (computedDirectTarifSons.contains(t)) {
			return tarifSons.getRightOf(t);
		}
		
		// unkown: update it
		computedDirectTarifSons.add(t);
		
		ArrayList/*<Tarif>*/ result = _getSons(t,true);
			
		// fill the Double Sided Map 
		Iterator/*<Tarif>*/ it = result.iterator();
		while (it.hasNext()) {
			tarifSons.put(t,it.next());
		}
		
		return result;
	}
	
	
	/** tools for getDirectSons and getAllSons
	 * @param onlyDirect if true return only first sons on a branch
	 * **/
	private ArrayList _getSons(Tarif t,boolean onlyDirect) {
		ArrayList/*<Tarif>*/ result = new ArrayList/*<Tarif>*/();
		
		Iterator/*<CompactTreeNode>*/ i = getTarifMapping(t).iterator();
		while (i.hasNext()) {
			_getDirectSonsHelper(result,(CompactTreeNode) i.next(),onlyDirect);
		}
		return result;
	}
	
	/** 
	 * recursive helper for getSons<BR>
	 * @param onlyDirect if true return only first sons on a branch
	 * **/
	private void 
	_getDirectSonsHelper(ArrayList/*<Tarif>*/ result,
			CompactTreeNode ctn, boolean onlyDirect) {
		
		Iterator/*<CompactTreeNode>*/ i = ctn.getChildren().iterator();
		ArrayList/*<CompactTreeTarifRef>*/ tarifsTemp;
		CompactTreeNode nodeTemp;
		while (i.hasNext()) {
			nodeTemp = (CompactTreeNode) i.next();
			tarifsTemp = nodeTemp.getTarifsRefs();
			
			//	Tarifs found
			Iterator/*<CompactTreeTarifRef>*/ it = tarifsTemp.iterator();
			CompactTreeTarifRef ttemp ;
			while (it.hasNext()) {
				ttemp = (CompactTreeTarifRef) it.next();
				if (! result.contains(ttemp.getTarif())) {
					result.add(ttemp.getTarif());
				}
			}
			
			
			// go for sons in branch if not onlyDirect or tarifs has been found
			if (! onlyDirect || tarifsTemp.size() == 0) {
				// Look into childrens
				_getDirectSonsHelper(result,nodeTemp,onlyDirect);
			}
		}
	}
	
	/** return all the sons of a tarif **/
	public ArrayList/*<Tarif>*/ getAllSonsOf(Tarif t) {
		checkReset(); // clean if needed
		return _getSons(t,false);
	}
}
