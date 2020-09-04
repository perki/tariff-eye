/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: PairManagerAssetsTransactions.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.tarif;

import java.io.Serializable;
import java.util.*;

import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.pair.PairManager;
import com.simpledata.bc.datamodel.pair.Pairable;

import org.apache.log4j.Logger;

/**
 * This class take care of all pairing operation<BR>
 * All the code could have been added to Tarification, 
 * It's just kept here to lighten up a little Tarification.java
 */
public class PairManagerAssetsTransactions 
	implements PairManager, Serializable {
	private final static Logger m_log = Logger.getLogger( PairManagerAssetsTransactions.class ); 
		
	private Tarification tarification;
	
	/** static name of the transaction node Named.getNid **/
	public final static String NID_TRANSACTION = "00BASE_TRANSACTIONS";
	
	/** static name of the assets node Named.getNid **/
	public final static String NID_ASSETS = "00BASE_ASSETS";
	
	
	/** contains all the pairing<BR>
	 * key : TarifAssets<BR>
	 * value : TarifTransactions
	 */
	private HashMap/*<TarifAssets>,<TarifTransactions>*/ pairingA;
	
	/** contains all the pairing<BR>
	 * key : TarifTransactions<BR>
	 * value : TarifAssets
	 */
	private HashMap/*<TarifAssets>,<TarifTransactions>*/ pairingT;
	
	public PairManagerAssetsTransactions(Tarification t) {
		this.tarification = t;
		pairingA = new HashMap/*<TarifAssets>,<TarifTransactions>*/();
		pairingT = new HashMap/*<TarifTransactions>,<TarifAssets>*/();
	}
	
	/** 
	 * @return the TarifTransactions paired with this TarifAssets
	 *	, null if not paired */
	public TarifTransactions get(TarifAssets ta) {
		return (TarifTransactions) pairingA.get(ta);
	}
	
	/** 
	 * @return the TarifAssets paired with this TarifTransactions
	 *	, null if not paired */
	public TarifAssets get(TarifTransactions tt) {
		return (TarifAssets) pairingT.get(tt);
	}
	
	/**
	 * break the pairing of this TarifAssets
	 */
	public void breakP(TarifAssets ta) {
		TarifTransactions tt = get(ta);
		if (tt == null) return; 
		pairingT.remove(tt);
		pairingA.remove(ta);
	}
	
	/**
	 * break the pairing of this TarifAssets
	 */
	public void breakP(TarifTransactions tt) {
		TarifAssets ta = get(tt);
		if (ta == null) return; 
		pairingT.remove(tt);
		pairingA.remove(ta);
	}
	
	/**
	 * return one of Pairable.CAN_BE_*
	 */
	public int canBePaired(TarifTransactions tt) {
		return _create(tt,true);
	}
	
	/**
	 * return one of Pairable.CAN_BE_*
	 */
	public int canBePaired(TarifAssets ta) {
		return _create(ta,true);
	}
	
	
	/**
	 * create the pairing for this tarif (if possible)
	 */
	public void create(TarifTransactions tt) {
		_create(tt,false);
	}
	
	/**
	 * helper for create and canBeCreated<BR>
	 * This create method does not check for a Valid Mapping
	 */
	public int _create(TarifTransactions tt,boolean test) {
		assert tt != null;
		if (getPairTreeNode(tt) == null)
			return Pairable.CAN_BE_NOK_NO_PAIR_POSITION;
		if (get(tt) != null) return Pairable.CAN_BE_NOK_ALREADY_PAIRED;
		TarifAssets ta = getProposition(tt);
		if (ta != null ) {
			if (! ta.isValid()) 
				return Pairable.CAN_BE_NOK_PROPOSITION_NOT_VALID;
			if (test) return Pairable.CAN_BE_OK_ATTACHED;
		} else {
			if (test) return Pairable.CAN_BE_OK_CREATE;
			ta = (TarifAssets)
				createTarifFrom(tt,getPairPosition(tt),TarifAssets.TARIF_TYPE);
			if (ta == null) return Pairable.CAN_BE_UNDIFINED;
		}
		create(ta,tt);
		return Pairable.CAN_BE_UNDIFINED; // not used
	}
	
	
	/**
	 * create the pairing for this tarif (if possible)
	 */
	public  void create(TarifAssets ta) {
		_create(ta,false);
	}
	
	/**
	 * create the pairing for this tarif (if possible)<BR>
	 */
	public int _create(TarifAssets ta,boolean test) {
		assert ta != null;
		if (getPairTreeNode(ta) == null)
			return Pairable.CAN_BE_NOK_NO_PAIR_POSITION;
		if (get(ta) != null) return Pairable.CAN_BE_NOK_ALREADY_PAIRED;
		TarifTransactions tt = getProposition(ta);
		if (tt != null ) {
			if (! tt.isValid()) 
				return Pairable.CAN_BE_NOK_PROPOSITION_NOT_VALID;
			if (test) return Pairable.CAN_BE_OK_ATTACHED;
		} else {
			if (test) return Pairable.CAN_BE_OK_CREATE;
			tt = (TarifTransactions)
			createTarifFrom(ta,getPairPosition(ta),
					TarifTransactions.TARIF_TYPE);
			if (tt == null) return Pairable.CAN_BE_UNDIFINED;
		}
		create(ta,tt);
		return Pairable.CAN_BE_UNDIFINED; // not used
	}
	
	
	/** 
	 * helper for both create method.. create the desired Tarif
	 * @param future the position for the newly created tarif
	 */
	private Tarif createTarifFrom(
			Tarif t,ArrayList/*<BCNode>*/ future,String type) {
		assert tarification != null;
		assert t != null;
		assert future != null;
		// create new blank TarifAssets with a forward WorkPlace
		Tarif partner = tarification.createTarif("P:"+t.getTitle(),
				type);
		if (partner == null) {
			m_log.error( "Failed creating a Tarif" );
			return null;
		}
		
		//	set the position first of all map to the base tree
		
		BCNode n;
		for (Iterator/*<BCNode>*/ i = future.iterator();i.hasNext();) {
			n = (BCNode) i.next();
			if (n.getTree() == tarification.getTreeBase()) {
				partner.mapTo(n,true);
				i.remove();
			}
		}
		
		//	set the position
		for (Iterator/*<BCNode>*/ i = future.iterator();i.hasNext();)
			partner.mapTo((BCNode) i.next(),true);
		
		WorkSheetManager.createWorkSheet(
				(Dispatcher)
				partner.getWorkSheet(),WorkPlaceTransferOptions.class,"");
		return partner;
	}
	
	
	
	/**
	 * physically link both tarif
	 */
	private void create(TarifAssets ta, TarifTransactions tt) {
		pairingA.put(ta,tt);
		pairingT.put(tt,ta);
	}
	
	
	/** 
	 * get a proposition of pairing for this node 
	 * <BR>
	 * return null if none found
	 * **/
	public TarifAssets getProposition(TarifTransactions tt) {
		if (getPairTreeNode(tt) == null) return null;
		return (TarifAssets) 
		getProposition(tt,TarifAssets.class,getPairPosition(tt));
	}
	
	/** 
	 * get a proposition of pairing for this node 
	 * <BR>
	 * return null if none found
	 * **/
	public TarifTransactions getProposition(TarifAssets ta) {
		if (getPairTreeNode(ta) == null) return null;
		return (TarifTransactions) 
		getProposition(ta,TarifTransactions.class,getPairPosition(ta));
	}
	
	
	/** get proposition helper **/
	public Tarif getProposition
	(Tarif t,Class partnerClass, ArrayList/*<BCNode>*/ pairPosition) {
		Iterator/*<Tarif>*/ i = 
			tarification.getTarifsAt(pairPosition).iterator();
		Tarif temp;
		int taMappingSize = t.getMyMapping().size();
		while (i.hasNext()) {
			temp = (Tarif) i.next();
			if (partnerClass.isInstance(temp)) {
				if (temp.getMyMapping().size() == taMappingSize)
					return temp;
			}
		}
		return null;
	}
	
	
	//****************************************//
	//******** positions tools ***************//
	//****************************************//
	

	/** get the assets base node **/
	private BCNode getPairTreeNode(TarifTransactions tt) {
		return getPairTreeNode(tt,NID_TRANSACTION,NID_ASSETS);
	}
	
	
	/** get the transaction base node **/
	private BCNode getPairTreeNode(TarifAssets ta) {
		return getPairTreeNode(ta,NID_ASSETS,NID_TRANSACTION);
	}
	
	/** get the pair position for this Tarif **/
	private ArrayList/*<BCNode>*/ getPairPosition(TarifAssets ta) {
		return getPairPosition(ta,getMyNode(ta),getPairTreeNode(ta));
		
	}
	
	/** get the pair position for this Tarif **/
	private ArrayList/*<BCNode>*/ getPairPosition(TarifTransactions tt) {
		return getPairPosition(tt,getMyNode(tt),getPairTreeNode(tt));
	}
	
	/** get the pair position for this Tarif **/
	private ArrayList/*<BCNode>*/ 
		getPairPosition(Tarif t,BCNode toRemove,BCNode toAdd) {
		ArrayList/*<BCNode>*/ map = t.getMyMapping();
		assert map.contains(toRemove) : t+" is not placed under"+toRemove;
		
		map.remove(toRemove);
		map.add(toAdd);
		return map;
	}
	
	
	/** 
	 * get the pair position for this List of BCnode
	 * return null if none
	 */
	public ArrayList/*<BCNode>*/ getPairPosition(ArrayList/*<BCnode>*/ mapping){
		// search for one of the my two root node
		ArrayList/*<BCNode>*/ result = (ArrayList) mapping.clone();
		
		
		BCNode node;
		BCNode toAdd =null;
		BCNode toRemove =null;
		for (Iterator/*<BCNode>*/ i= result.iterator(); i.hasNext();) {
			node = (BCNode) i.next();
			if (node.getNID().startsWith(NID_ASSETS)) {
				assert toAdd == null;
				toRemove = node;
				toAdd = getPartnerNode(node,NID_TRANSACTION,NID_ASSETS);
			} else if (node.getNID().startsWith(NID_TRANSACTION)) {
				assert toAdd == null;
				toRemove = node;
				toAdd = getPartnerNode(node,NID_ASSETS,NID_TRANSACTION);
			}
		}
		
		if (toAdd == null) return null;
		result.add(toAdd);
		result.remove(toRemove);
		
		return result;
	}
	

	
	/**
	 * get the Node on base tree where this tarif is linked
	 */
	private BCNode getMyNode(Tarif t) {
		ArrayList/*<BCNode>*/ map = t.getMyMapping(tarification.getTreeBase());
		if (map.size() != 1) {
			m_log.error( "How come ["+t+"] has an invalid mapping!!" );
			return null;
		}
		return (BCNode) map.get(0);
	}
	
	/** a small chache for partner lurking **/
	private transient HashMap partnerCache;
	
	
	/** 
	 * get the pair base tree node for this tarif 
	 * @return null if none found
	 * **/
	private BCNode getPairTreeNode(Tarif t,String myRootId,String partnerRootId)
	{
		// look in the map If I already know him
		if (partnerCache == null) partnerCache = new HashMap();
		Object o = partnerCache.get(t);
		if (o == t) { 
			return null;
		} else if (o != null) {
			return (BCNode) o;
		}
		
	
		BCNode myNode = getMyNode(t);
		
		//look in the map If I already know him
		 o = partnerCache.get(myNode);
		if (o == myNode) { 
			return null;
		} else if (o != null) {
			return (BCNode) o;
		}
		
		BCNode partner = getPartnerNode(myNode,partnerRootId,myRootId);
		
		if (partner == null) {
			// put identities in the hashmap to inidcate no match has been 
			// found for this Tarif and for the Node
			partnerCache.put(t,t);
		} else {
			partnerCache.put(t,partner);
		}
		
		return partner;
	}
	
	/**
	 * get the partner node of this one<BR>
	 * @return null if no pairing found
	 */
	private BCNode getPartnerNode
		(BCNode myNode,String partnerRootId, String myRootId) {
		if (partnerCache == null) partnerCache = new HashMap();
		// look in the map If I already know him
		Object o = partnerCache.get(myNode);
		if (o == myNode) { 
			return null;
		} else if (o != null) {
			return (BCNode) o;
		} 
		
		BCNode partner = null;
		// check this node has a valid id
		if (myNode.getNID().startsWith(myRootId)) {
			
			//	remove from myNode id the header of myRoot id
			String stamp = myNode.getNID().substring(myRootId.length());
			
			//		 I'm looking for 
			String partnerID = partnerRootId+stamp;
			
			partner = getNodeWithId(
					tarification.getTreeBase().getRoot(),partnerID);
		
		}
		

		if (partner == null) {
			// put identities in the hashmap to inidcate no match has been 
			// found for this Tarif and for the Node
			partnerCache.put(myNode,myNode);
		} else {
			partnerCache.put(myNode,partner);
		}
		
		
		return partner;
	}
	
	/**
	 * search the base Tree for a partner Id
	 */
	public BCNode getNodeWithId(BCNode parent, String s) {
		if (parent.getNID().equals(s)) return parent;
		Iterator/*<BCNode>*/ i = parent.getChildrens().iterator();
		BCNode b = null;
		while (i.hasNext() && b == null) {
			b = getNodeWithId((BCNode) i.next(),s);
		}
		return b;
	}
	
	
	//------------------ XML -------------------//
	/**
	 * XML
	 */
	public HashMap getPairingA() {
		return pairingA;
	}
	/**
	 * XML
	 */
	public void setPairingA(HashMap pairingA) {
		this.pairingA = pairingA;
	}
	/**
	 * XML
	 */
	public HashMap getPairingT() {
		return pairingT;
	}
	/**
	 * XML
	 */
	public void setPairingT(HashMap pairingT) {
		this.pairingT = pairingT;
	}
	/**
	 * XML
	 */
	public Tarification getTarification() {
		return tarification;
	}
	/**
	 * XML
	 */
	public void setTarification(Tarification tarification) {
		this.tarification = tarification;
	}
}

/*
 * $Log: PairManagerAssetsTransactions.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:43  perki
 * First commit on sourceforge
 *
 * Revision 1.6  2004/09/03 11:47:53  kaspar
 * ! Log.out -> log4j first half
 *
 * Revision 1.5  2004/08/05 11:44:11  perki
 * Paired compact Tree
 *
 * Revision 1.4  2004/08/04 16:40:08  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.2  2004/07/31 17:43:30  perki
 * Pairing ok ... it was a hard time
 *
 * Revision 1.1  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 */