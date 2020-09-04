/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

/** 
 * Package that contains all Workplaces. Workplace s are a unit 
 * of computation and Dispatcher s are a way of finding the correct
 * applicable unit of computation. 
 */
/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.components.worksheet.workplace;

import com.simpledata.bc.components.TarifTreeItem;
import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.datamodel.WorkPlace;
import com.simpledata.bc.datamodel.WorkSheetContainer;

/**
 * Abstract base class for all workplaces. This makes the link 
 * with the abstract part of the datamodel, and at the same 
 * time introduces the concept of visitable nodes, etc. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: WorkPlaceAbstract.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public abstract class WorkPlaceAbstract extends WorkPlace implements TarifTreeItem {
	
	/**
	 * Constructor.
	 * Note: Should not be called by itself. use WorkSheet#createWorkSheet(Dispatcher d,Class c)
	 */
	public WorkPlaceAbstract( 
		WorkSheetContainer parent,
	  String title,
		String id, 
	  String key
	) {
		super(parent,title,id, key); 
	}
	
	//--------- XML ----//
	/** 
	 * XML only constructor. 
	 */
	public WorkPlaceAbstract() {
		// empty
	}
	
	/**
	 * Passed the visitor, will visit the node with the right basic
	 * case method. Any given node Foo in the tree must call back 
	 * the visitor using the method caseFoo. 
	 */
	public abstract void visit(TarifTreeVisitor v);
}