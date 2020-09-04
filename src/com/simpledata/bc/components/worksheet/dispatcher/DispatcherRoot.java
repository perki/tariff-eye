/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: DispatcherRoot.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.components.worksheet.dispatcher;

import java.util.Iterator;

import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.util.CollectionsToolKit;

/**
 * This class is a tagging class for all Root Classes<BR>
 * And it contains some tools used only by those root classes<BR>
 * 
 * 
 * A ROOT DISPATCHER MUST ACCEPT FORWARDS (TRANSFERABLE OPTIONS) FROM
 * ANOTHER SON TARIF 
 * 
 */
public abstract class DispatcherRoot 
	extends DispatcherSimple implements WorkSheet.Cleareable {
	
	/**
	 * @param parent
	 * @param title
	 * @param id
	 */
	public DispatcherRoot(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(parent, title, id,key);
	}
	
	/**
	 * <B>implements WSContainer's and override Dispatcher's<B>
	 * call super's and add WorkPlaceTransferOption<BR>
	 */
	public final Class[] getAcceptedNewWorkSheets(String key) {
		return (Class[]) CollectionsToolKit.addToArray(
				super.getAcceptedNewWorkSheets(key), 
				new Class[]{WorkPlaceTransferOptions.class}
				);
	}
	

	/**
	 * return the proportion represented by the options contained in this
	 * WorkSheet over myself
	 * @param dr must be of the exact same class type that this dispatcher
	 * @return valueOf(dr) / valueOf(me)
	 */
	public abstract double proportionOf(DispatcherRoot dr);
	
	/**
	 * <B>INTERFACE<B> WorkSheet.Cleareable
	 * unregister all user options
	 */
	public final void clear() {
		Iterator/*<BCOption>*/ i = getOptions().iterator();
		while (i.hasNext())
			removeOption((BCOption) i.next());
	}
	
	//------------- XML ---------------//
	/** XML */
	public DispatcherRoot() { }
	
}
/**
 * $Log: DispatcherRoot.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:36  perki
 * First commit on sourceforge
 *
 * Revision 1.7  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.6  2004/09/09 12:14:11  perki
 * Cleaning WorkSheet
 *
 * Revision 1.5  2004/09/08 19:28:55  perki
 * Reaprtition now follows Transfer Options
 *
 * Revision 1.4  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 * Revision 1.3  2004/07/19 09:36:53  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.2  2004/07/07 13:37:10  carlito
 * imports organized
 *
 * Revision 1.1  2004/07/04 10:58:37  perki
 * *** empty log message ***
 *
 */