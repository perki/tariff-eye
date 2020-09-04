/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: CompactTarifLinkNode.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
 
package com.simpledata.bc.uicomponents.compact; 

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;

/**
 * Node that shows up references from a Tarif to a WorkSheet.
 *
 * XXX: Is this used at all ? If not, take it out of the repository. 
 *
 */
public class CompactTarifLinkNode extends CompactNode implements NamedEventListener {
	BCOption option;
	WorkSheet ws; // can be null if sevral workSheets
	ArrayList children;

	/**
	 * Constructor. 
	 * @param parent   Parent node that should contain this node. 
	 * @param o        initial options
	 * @param expl     Creator of tree, controller in this operation.
	 */
	CompactTarifLinkNode(
		CompactTarifsContainerNode parent,
		BCOption o,
		CNInterface expl
	) {
			
		super(parent, expl);
		option= o;
		ws= null;
		children= new ArrayList();

		// check if mutliWorkseet or no...
		ArrayList v= option.getWorkSheets();
		children= new ArrayList();
		for (int i= 0; i < v.size(); i++) {
			children.add(
				new CompactWorkSheetNode(this, (WorkSheet) v.get(i), expl));
		}
		
		if (v.size() == 1) {
			ws= (WorkSheet) v.get(0);

			// add a listener for tarif name change
			// will also catch WorkSheets events
			ws.getTarif().addNamedEventListener(
				this,
				NamedEvent.TITLE_MODIFIED,
				null);
			//	add a listener for worksheet drop event
			ws.getTarif().addNamedEventListener(
					this,
					NamedEvent.WORKSHEET_DROPPED,
					null);
					
			return;
		}

		// add a listener for my name change
		option.addNamedEventListener(this, NamedEvent.TITLE_MODIFIED, null);

	}

	/** return the value contained in this node **/
	public boolean contentsHasValue(Object o) {
		return (option == o);
	}

	public Object[] contentsGet() {
		return new BCOption[] { option };
	}

	// ----------- getIcon -----------------------//
	public ImageIcon getMyLeafIcon() {
		return Resources.stdTarifReference;
	}

	protected ImageIcon getMyOpenedIcon() {
		return Resources.stdTarifReference;
	}

	protected ImageIcon getMyClosedIcon() {
		return Resources.stdTarifReference;
	}

	// ----------- Default -----------------------//

	public ArrayList getChildrenAL() {
	    assert children != null :
	        "Children must not be null";
	    
		return children;
	}

	public String displayTreeString() {
		String add= "";
		if (ws != null)
			add= " - " + ws.getTarif().getTitle();
		return option.getTitle() + add;
	}

	public WorkSheet contentsGetWorkSheet() {
		return ws;
	}

	/**
	 * @see com.simpledata.bc.datamodel.event.NamedEventListener#eventOccured(com.simpledata.bc.datamodel.event.NamedEvent)
	 */
	public void eventOccured(NamedEvent e) {
		if (e.getEventCode() == NamedEvent.WORKSHEET_DROPPED
			&& e.getSource() == ws) {
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
		v.caseCompactTarifLinkNode( this ); 
	}

}

