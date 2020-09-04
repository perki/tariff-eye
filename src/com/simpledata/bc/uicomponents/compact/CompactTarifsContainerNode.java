/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: CompactTarifsContainerNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.calculus.*;
import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Money;

/**
 * Super class, for all class that may contains a tarif or a list of tarifs.<BR>
 * Has containers for tarifs, childrens and remember tarif occurencies
 */
abstract class CompactTarifsContainerNode extends CompactTarifManagerNode {
	private static final Logger m_log = 
		Logger.getLogger( CompactTarifsContainerNode.class ); 
	
	
    ArrayList/*<CompactTreeTarifRef>*/ tarifsRefs;
	ArrayList/*<CompactBCNode>*/ childrens;
	
	//	trigger to know if Tarifs has been created
	protected boolean uiComponentsCreated;

	/**
	 * Constructor used from CompactShadowNode<BR>
	 * with no tarif references
	 */
	protected CompactTarifsContainerNode(
			CompactBCNode parent,
			CNInterface expl) {
		this(parent,new ArrayList/*<CompactTreeTarifRef>*/(),expl);
	}

	/**
	 * Constructor used from CompactBCNode<BR>
	 * with tarifs references
	 */
	protected CompactTarifsContainerNode(
		CompactBCNode parent,
		ArrayList/*<CompactTreeTarifRef>*/ tarifs,
		CNInterface expl) {
		super(parent, expl);
		this.tarifsRefs= tarifs;
		childrens= new ArrayList/*<CompactBCNode>*/();
		uiComponentsCreated= false;
	}

	/** get the first contained Tarif **/
	public final WorkSheet contentsGetWorkSheet() {
		if (explorer.showTarifs()) {
			return null;
		}
		if (tarifsRefs.size() > 0) {
			return 
			((CompactTreeTarifRef) tarifsRefs.get(0)).getTarif().getWorkSheet();
		}
		return null;
	}
	
	/** return true if this Node contains this Tarif **/
	public final boolean contentsHasTarif(Object o) {
		if (tarifsRefs == null) return false;
		
		Iterator/*<CompactTreeTarifRef>*/ e = tarifsRefs.iterator();
		while (e.hasNext()) {
			if (((CompactTreeTarifRef) e.next()).getTarif() == o)
				return true;
		}
		return false;
	}
	
	/** 
	 * remove a Tarif from my list of Tarif
	 * This is only possible if Ui has been created
	 *  **/
	protected final void contentsRemoveTarif(Tarif t) {
		if (tarifsRefs == null) return ;
		assert (!uiComponentsCreated) : "Cannot remove Tarif when UI is on";
		
		Iterator/*<CompactTreeTarifRef>*/ e = tarifsRefs.iterator();
		while (e.hasNext()) {
			if (((CompactTreeTarifRef) e.next()).getTarif() == t)
				e.remove();
		}
	}
	
	
	
	/**
	 * Add a Tarif to this Node
	 */
	public final void addTarif(CompactTreeTarifRef cttr) {
		tarifsRefs.add(cttr);
	}
	

	/** return the vector of childrens of this node */
	public final ArrayList/*<CompactNode>*/ getChildrenAL() {
		return childrens;
	}

	/** 
	 * launch the creation of UI components 
	 * <B> TODO This should be removed</B>
	 * **/
	abstract void prodCreateUIComponents();


	
	/**
	 * used for creation add or not Tarif components
	 */
	protected final void prodCreateTarifsComponents() {
		if ((!explorer.showTarifs()) && !(this instanceof CompactRoot)) {
			addTarifListeners();
			return;
		}

		Iterator/*<CompactTreeTarifRef>*/ e= tarifsRefs.iterator();
		while (e.hasNext()) {
			CompactTreeTarifRef cttr 
			= ((CompactTreeTarifRef) e.next());
			//	mode with nodes for tarifs
			//if (explorer.showTarifs()) {
				childrens.add(0, new CompactTarifNode(this, cttr, explorer));
			//}

			//	add links to Tarifs
			if (explorer.showTarifsRefrences()) {
				ArrayList v= cttr.getTarif().getLinkedOptions();
				for (int i= 0; i < v.size(); i++) {
					childrens.add(
						0,
						new CompactTarifLinkNode(
							this,
							(BCOption) v.get(i),
							explorer));
				}
			}
		}
		uiComponentsCreated = true;
	}
	 
	/**
	 * Add the necessary listeners to listen for tarif COM value change
	 */
	protected final void addTarifListeners() {
		if (getTarification() == null) return;
		Iterator/*<CompactTreeTarifRef>*/ e = tarifsRefs.iterator();
		Tarif temp;
		while (e.hasNext()) {
			temp = ((CompactTreeTarifRef) e.next()).getTarif();
			getTarification().comCalc().addCommissionListener(temp,this);
		}
	}
	
	/**
	 * got a change in the commission of my sons
	 */
	public final void _startComCalc(ComCalculus cc,Money value) {
		// for each children
		for (int i = 0; i < childrens.size() ; i++) {
			if (childrens.get(i) instanceof ComModifier ) {
				
				boolean done = false;
				if (childrens.get(i) instanceof CompactTarifNode) {
					CompactTarifNode ctn = (CompactTarifNode) childrens.get(i);
					// now BCGroupNode prevent multiple representation 
					// of a Tarif in the Tree
					// so getOccurences() should always return 1 and this code 
					// is inactive
					if (ctn.cttr.getOccurences() > 1) {
						m_log.warn("I should never go into this code");
						return;
//						
//						double factor = 1d / ctn.cttr.getOccurences();
//						value.operation(cc.getCom(ctn),factor);
//						
//						done = true;
					} 
					
				} 
				if (! done) {
					value.operation(cc.getCom((ComModifier)childrens.get(i)),1);
				}
			} 
		}
		
		// for each tarif (if ! show Tarifs)
		// divide by the number of occurencies
		if (!explorer.showTarifs()) {
			Iterator/*<CompactTreeTarifRef>*/ e= tarifsRefs.iterator();
			while (e.hasNext()) {
				CompactTreeTarifRef cttr = 
					((CompactTreeTarifRef) e.next());
				
				// if occurencies > 1
				// now BCGroupNode prevent multiple representation of a 
				// Tarif in the Tree
				// so getOccurences() should always return 1 
				if (cttr.getOccurences() > 0) {
					double factor = 1d / cttr.getOccurences();
					value.operation(cc.getCom(cttr.getTarif()),factor);
				} 
				
			}
		}
	}
	
	
	
}

/*
 * $Log: CompactTarifsContainerNode.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.9  2004/11/09 15:56:04  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/09/14 13:22:30  perki
 * *** empty log message ***
 *
 * Revision 1.7  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.6  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.5  2004/09/03 14:30:02  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/09/02 15:51:46  perki
 * Lot of change in calculus method
 *
 * Revision 1.3  2004/07/30 11:28:39  perki
 * Better tooltips
 *
 * Revision 1.2  2004/07/30 09:09:26  perki
 * Grouping ok
 *
 * Revision 1.1  2004/07/30 05:58:15  perki
 * Slpitted CompactNode.java in sevral files
 *
 */