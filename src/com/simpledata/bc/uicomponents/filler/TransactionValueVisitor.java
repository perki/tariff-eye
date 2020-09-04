/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TransactionValueVisitor.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.util.Iterator;

import com.simpledata.bc.components.bcoption.OptionTransaction;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.datamodel.money.TransactionValue;

/**
 * A tool class that contains a Vistor to go throught 
 * Transaction Values of a NodeInfo
 */
public abstract class TransactionValueVisitor 
	implements FillerVisitor {
	
	private boolean line = true;
	
	public final void run(NodeInfo ni) {
		 line = true;
		if (ni.getChildren().length == 0) {
			TransactionsRoot0 tr; 
			int totalNumOfWs = ni.getRootWorkSheetPaired().size();
			for (Iterator/*<TransactionsRoot0>*/ i =
				ni.getRootWorkSheetPaired().iterator();i.hasNext();) 
			{
				tr = (TransactionsRoot0) i.next();
				gotWorkSheet(ni,tr,totalNumOfWs);
				if (line) {
					for (
						Iterator/*<OptionTransaction>*/ j = (tr).getOptions(
							OptionTransaction.class
						).iterator();
						j.hasNext();
						// empty
					) {
						
						gotLine(ni,((OptionTransaction) 
								j.next()).getTransactionValue());
						
					}
				}
			}
		
			
		} else {
			ni.runOnChildren(this);
		}
	}
	
	/** augment this if you want to receive line events<BR>
	 * do not forget to return true with needLine **/
	void gotLine(NodeInfo ni,TransactionValue tv) {
		 line = false; // just ask one for lines
	}
	
		
	/** 
	 * augment this if you want to receive workSHeet events 
	 * @param totalNumOfWs is the total number of worksheets on this node 
	 * **/
	void gotWorkSheet(NodeInfo ni,TransactionsRoot0 tr,int totalNumOfWs) {
		
	}
		
		
}

/*
 * $Log: TransactionValueVisitor.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.5  2004/08/27 11:24:53  kaspar
 * ! Moved all inner classes out of FillerData, this creates a more
 *   lisible design
 *
 * Revision 1.4  2004/08/24 14:12:29  kaspar
 * ! Commentary correction
 * ! Line endings changed.
 *
 * Revision 1.2  2004/08/02 14:17:11  perki
 * Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 * Revision 1.1  2004/08/02 09:27:34  perki
 * *** empty log message ***
 *
 */