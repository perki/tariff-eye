/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: CompactTarifNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
 
package com.simpledata.bc.uicomponents.compact; 

import java.awt.Point;
import java.util.Iterator;

import javax.swing.ImageIcon;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.pair.Pairable;
import com.simpledata.bc.uicomponents.TreeIconManager;
import com.simpledata.bc.uitools.ImageTools;

/**
 * Node that contains a Tarif
 * (always leafs)
 */
public class CompactTarifNode 
	extends CompactTarifManagerNode {
	CompactTreeTarifRef cttr; 

	protected CompactTarifNode(
		CompactTarifsContainerNode parent,
		CompactTreeTarifRef tarifRef,
		CNInterface expl) {
		super(parent, expl);
		cttr= tarifRef;
		if (getTarification() == null) return;
		getTarification().comCalc().addCommissionListener(
				tarifRef.getTarif(),this
		);
	}
	
	
	/**
	 * got a change in the commission of my sons
	 * And copy the value of My Tarif into my Own comission
	 */
	public void _startComCalc(ComCalculus cc,Money value) {
		if (cttr != null && cttr.getTarif() != null) 
			value.setValue(cc.getCom(cttr.getTarif()));
	}
	

	/** 
	 * return true if this node contains this tarif
	 * @param o a Tarif instance
	 * */
	public boolean contentsHasValue(Object o) {
		return (cttr.getTarif() == o);
	}
	
	/**
	 * return an array of Tarif[]. 
	 */
	public Object[] contentsGet() {
		return new Tarif[] { cttr.getTarif() };
	}

	// ----------- getIcon -----------------------//
	public ImageIcon getMyLeafIcon() {
		ImageIcon i = TreeIconManager.getIcon(
				TreeIconManager.CNODE_TARIF,
				true,
				false);
		
		//	if Tarif is Pairable and not paired
		if (cttr.getTarif() instanceof Pairable) {
			if (((Pairable) cttr.getTarif()).pairedGet() == null) {
				i = ImageTools.drawIconOnIcon(
						i,
						Resources.stdTagNotPaired,
						new Point(0, 0));
			} else {
			    i = ImageTools.drawIconOnIcon(
						i,
						Resources.stdTagPaired,
						new Point(0, 0));
			}
		}
		
//		// if Tarif is not valid
//		if (!cttr.getTarif().isValid()) {
//			i = ImageTools.drawIconOnIcon(
//				i,
//				Resources.stdTagError,
//				new Point(0, 0));
//		}
//
//		// if Tarif has a reduction
//		if (cttr.getTarif().hasReduction()	) {
//			i = ImageTools.drawIconOnIcon(
//				i,
//				Resources.reductionTag,
//				new Point(0, 0));
//		}
//		
		
		
		return i;
	}
	
	/**
	 * when you need additional tags<BR>
	 * example.. if the tag erro should be displayed: <BR>
	 * b[TAG_ERROR] = true;
	 */
	protected void getAdditonalTags(boolean[] b) {
	       if (cttr.getTarif().hasReduction()) b[TAG_REDUC] = true;
	       if (! cttr.getTarif().isValid()) b[TAG_ERROR] = true;
	};

	protected ImageIcon getMyOpenedIcon() {
		return null;
	}

	protected ImageIcon getMyClosedIcon() {
		return null;
	}

	
	

	// ----------- Default -----------------------//

	public String displayTreeString() {
		return cttr.getTarif().getTitle();
	}

	public WorkSheet contentsGetWorkSheet() {
		return cttr.getTarif().getWorkSheet();
	}

	// Visitor
	
	/**
	 * Visit a node of type CompactRoot.
	 * @param v Visitor instance to call back into. 
	 */
	public void visit( CompactTreeVisitor v ) {
		v.caseCompactTarifNode( this ); 
	}
}

