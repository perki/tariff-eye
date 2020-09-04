/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Interface for tree visits.
 * @version $Id: TarifTreeVisitor.java,v 1.2 2007/04/02 17:04:31 perki Exp $
 * @author Simpledata 2004, all rights reserved. 
 */

/// Package for datamodel base interfaces and classes. 
package com.simpledata.bc.components;  

import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherBounds;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherCase;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSequencer;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSimple;
import com.simpledata.bc.components.worksheet.dispatcher.FuturesRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceAssetsRateBySlice;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFixedFee;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFutFeeBySlice;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateBySliceOnAmount;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateOnAmount;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceSimple;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTrRateBySlice;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions;


/**
 * Visitor for the data tree. 
 * @see TarifTreeItem
 */
public interface TarifTreeVisitor { 
	
	// all workplaces
	public void caseWorkPlaceWithOnlyOptions( WorkPlaceWithOnlyOptions node );
	public void caseWorkPlaceTransferOptions( WorkPlaceTransferOptions node ); 
	public void caseWorkPlaceTrRateBySlice( WorkPlaceTrRateBySlice node );
	public void caseWorkPlaceSimple( WorkPlaceSimple node );
	public void caseWorkPlaceAssetsRateBySlice( WorkPlaceAssetsRateBySlice node ); 
	public void caseWorkPlaceRateOnAmount( WorkPlaceRateOnAmount node ); 
	public void caseWorkPlaceFixedFee( WorkPlaceFixedFee node );
	public void caseEmptyWorkSheet( EmptyWorkSheet node ); 
	public void caseWorkPlaceRateBySliceOnAmount( 
	        WorkPlaceRateBySliceOnAmount node ); 
	public void caseWorkPlaceFutFeeBySlice(WorkPlaceFutFeeBySlice node);
	
	// all dispatchers
	public void caseDispatcherSimple( DispatcherSimple node );
	public void caseDispatcherBounds( DispatcherBounds node );
	public void caseDispatcherSequencer( DispatcherSequencer node );
	public void caseDispatcherIf( DispatcherIf node );
	public void caseDispatcherCase( DispatcherCase node );
	public void caseAssetsRoot0( AssetsRoot0 node );
	public void caseTransactionsRoot0( TransactionsRoot0 node ); 
	public void caseFuturesRoot0( FuturesRoot0 node ); 
}