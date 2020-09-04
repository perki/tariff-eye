/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: CompactShadowNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
 
package com.simpledata.bc.uicomponents.compact; 

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import com.simpledata.bc.datamodel.compact.CompactTreeTarifRef;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TreeIconManager;
import com.simpledata.util.CollectionsToolKit;


/**
 * Shadowe node that shows OTHERS<BR>
 * Used for clarity in thr display.. show a fake node for nodes with
 * Tarifs that are not leafs.
 */
public class CompactShadowNode extends CompactTarifsContainerNode 
	implements CompactNodeMayHavePair {
	
	CompactShadowNode(CompactBCNode parent, 
			ArrayList/*<CompactTreeTarifRef>*/ tarifs, 
			CNInterface expl) {
		super(parent, tarifs, expl);
	}
	
	
	/**
	 * <B>Interface CompactNodeMayHavePair</B>
	 * get the paired node of this one<BR>
	 */
	public final CompactTreeItem contextGetpair() {
		return ((CompactBCNode) parent)._contextGetPair(true);
	}
	
	
	/** return true if this Node contains this Tarif **/
	public boolean contentsHasValue(Object o) {
		if (tarifsRefs == null) return false;
		
		if (explorer.showTarifs() && uiComponentsCreated) return false;
		return contentsHasTarif(o);
	}

	/** take care contains Tarif and BCNode **/
	public Object[] contentsGet() {
		ArrayList result =CollectionsToolKit.getArrayList(parent.contentsGet());
		if (explorer.showTarifs() && uiComponentsCreated) {
			
		} else {
			
			if (tarifsRefs != null) {
				Iterator/*<CompactTreeTarifRef>*/ e = tarifsRefs.iterator();
				while (e.hasNext()) {
					result.add(((CompactTreeTarifRef) e.next()).getTarif());
				}
			}
		}
		return result.toArray();
	}

	// ----------- getIcon -----------------------//
	public ImageIcon getMyOpenedIcon() {
		return getMyLeafIcon();
	}

	public ImageIcon getMyClosedIcon() {
		return getMyLeafIcon();
	}

	public ImageIcon getMyLeafIcon() {
		
		if (getParent() instanceof CompactBCNodeSingle) {
		return CompactBCNode.getLeafIcon(explorer,tarifsRefs.size() > 0,null);
		}
		return TreeIconManager.getSTDIcon(true,true);
	}

	

	// ----------- Default -----------------------//

	public String displayTreeString() {
		return Lang.translate("others");
	}

	/**
	 * @see CompactTarifsContainerNode#createUIComponenents()
	 */
	void prodCreateUIComponents() {
		prodCreateTarifsComponents();
	}

	// Visitor
	
	/**
	 * Visit a node of type CompactRoot.
	 * @param v Visitor instance to call back into. 
	 */
	public void visit( CompactTreeVisitor v ) {
		v.caseCompactShadowNode( this ); 
	}
}

