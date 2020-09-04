/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.uicomponents.filler;

import javax.swing.JPanel;

import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.money.Money;

/**
 * This interface is implemented by all classes that knows how to
 * distribute an Amount of money into a WorkSheet
 * 
 * @version $Id: DistributionMethod.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public interface DistributionMethod {
	
	
	
	/**
	 * return true if this Method forward to it's parent<BR>
	 * Normaly only DistribRelyOnParent
	 * @see DistribRelyOnParent
	 */
	public boolean methodForward();
	
	/** 
	 * return the FillerNode that owns this distribute action<BR>
	 * This is used by DistributMethod that need some informations
	 * about the parents
	 **/
	public FillerNode getOwner() ;
	
	/** 
	 * distribute this amount of money on this WorkSheet<BR>
	 * For transactions, this amount of money represent the sum of money
	 * That should go in and out
	 * **/
	public void distribute(Money m,Dispatcher workSheet,
			DistributionMonitor dm);
	
	/**
	 * Get the User interface relative to this FillerNode
	 */
	public JPanel getUI();
	
	/**
	 * Refresh the UI
	 */
	public void refreshUI();
	
	/**
	 * Get a Summary HTMLized String describing this Method and it's value
	 * <BR>This will be used by DistribRelyOnParent
	 */
	public String getSummary();
	
	
	
	/**
	 * get the number of options the will be created from this node
	 */
	public int getCost(NodeInfo start);
	
	
	/** implement this interface if your method accept sevral types **/
	public interface Typeable {
		public void setType(Class type);
	}
	
}
/*
 * $Log: DistributionMethod.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.7  2004/10/11 10:19:16  perki
 * Percentage on Transactions
 *
 * Revision 1.6  2004/08/27 11:24:53  kaspar
 * ! Moved all inner classes out of FillerData, this creates a more
 *   lisible design
 *
 * Revision 1.5  2004/08/27 10:02:09  kaspar
 * ! Refactor: Put DistributionMonitor in its own file
 *
 * Revision 1.4  2004/08/02 14:17:11  perki
 * Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 * Revision 1.3  2004/08/02 08:32:36  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/07/31 11:06:55  perki
 * Still have problems with the progressbar
 *
 * Revision 1.1  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 */