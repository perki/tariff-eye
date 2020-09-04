/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: CompactWorkSheetNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
 
package com.simpledata.bc.uicomponents.compact; 

import javax.swing.ImageIcon;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;

/**
 * Node that references a WorkSheet in the compact tree. This node
 * is a leaf node mostly. 
 *
 * CompactTarifLinkNode contains one of these to reference to the 
 * tarif that it contains. 
 */
public class CompactWorkSheetNode extends CompactNode implements NamedEventListener {
	WorkSheet workSheet;

	CompactWorkSheetNode(CompactNode parent, WorkSheet ws, CNInterface expl) {
		super(parent, expl);
		workSheet= ws;

		//	add a listener for tarif name change
		// will also catch WorkSheet name modified ;)
		workSheet.getTarif().addNamedEventListener(
			this,
			NamedEvent.TITLE_MODIFIED,
			null);
			
		//	add a listener for worksheet drop event
		ws.getTarif().addNamedEventListener(
			this,
			NamedEvent.WORKSHEET_DROPPED,
			null);

	}

	/**
	 * Return whether there is a value in this node. 
	 * @return true if there is a value in this node. 
	 **/
	public boolean contentsHasValue(Object o) {
		return (workSheet == o);
	}

	public Object[] contentsGet() {
		return new WorkSheet[] { workSheet };
	}

	// ----------- getIcon -----------------------//
	public ImageIcon getIcon() {
		return Resources.wsDefaultWorkSheet;
	}

	// ----------- Default -----------------------//

	public String displayTreeString() {
		return workSheet.getTarif().getTitle() + " - " + workSheet.getTitle();
	}

	public WorkSheet contentsGetWorkSheet() {
		return workSheet;
	}

	// Icon management
	protected ImageIcon getMyOpenedIcon() {
		return null;
	}

	protected ImageIcon getMyClosedIcon() {
		return null;
	}

	protected ImageIcon getMyLeafIcon() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.event.NamedEventListener#eventOccured(com.simpledata.bc.datamodel.event.NamedEvent)
	 */
	public void eventOccured(NamedEvent e) {
		if (e.getEventCode() == NamedEvent.WORKSHEET_DROPPED
			&& e.getSource() == workSheet) {
			explorer.refreshStructure();
			return;
		}
		explorer.fireTreeNodesChanged(this);
	}
	
	/**
	 * Return the value to be display in the Table
	 */
	public Object displayTableValue(int columnIndex) {
		return "...";
	}
	// Visitor
	
	/**
	 * Visit a node of type CompactRoot.
	 * @param v Visitor instance to call back into. 
	 */
	public void visit( CompactTreeVisitor v ) {
		v.caseCompactWorkSheetNode( this ); 
	}
}



