/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: FillerNode.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import com.simpledata.bc.datamodel.money.Money;

/**
 * An interface for nodes containing informations about repartition
 */
public interface FillerNode {
	
	public static FillerNode[] ARRAY_CLASS = new FillerNode[0];
	
	/** 
	 * lock this node
	 * @see #getLockState()
	 */
	public void lock(boolean b);
	
	
	/** 
	 * get the lockstate of a node<BR>
	 * If a node is locked it cannot be move and it will be dependant 
	 * of other nodes
	 */
	public boolean getLockState();
	

	/** 
	 * return true if this state can be achived by this Node
	 * @see #getLockState()
	 */
	public boolean canBeLocked(boolean b);
	
	/** return the percentage at this node 
	 * @return a value between 0 and 1
	 * **/
	public double getPercentage();
	
	/** 
	 * return the Amount at this node 
	
	 **/
	public Money getAmount();
	
	/** 
	 * commit the changes that has set done with setPreviewPercentage
	 * **/
	public void commit();
	
	
	/** set the rollout value under this node, -1 for forward to parent **/
	public void rolloutSet(double d);
	
	/** get the rollout value aplicable at this node **/
	public double rolloutGetApplicable();
	
	/** return true if this node relies on parent for it rollout **/
	public boolean rolloutReliesOnParent();
	
	/** set the money mount under this node **/
	public void setMoneyAmount(Money m);
	
	/** get the title of this node **/
	public String getTitle();
	
	/** return the childrens of this node **/
	public FillerNode[]/*<FillerNode*>*/ getChildren();
	
	/** return the parent of this node 
	 * @return null if root 
	 **/
	public FillerNode getParent();
	
	/** 
	 * redristibute the money under this node (refresh())
	 * (needed when percentages change)
	 */
	public void redistribute();
	
	/** 
	 * get the Root WorkSheets I'm attached too<BR>
	 * @return only AssetsRoot0 rootWorkSheets of TarifAssets
	 * **/
	public ArrayList/*<AssetsRoot0>*/ getRootWorkSheets();
	
	/** 
	 * redristibute the money under this node (on leafs)
	 * (needed when distibution change)
	 */
	public void redistributeRepartition(Class type);
	
	
	
	/**
	 * get the Distribution Method associtated with this FillerNode
	 * for this class type
	 * @param type is one of AssetsRoot0 or TransactionRoot0
	 */
	public DistributionMethod getDistributionMethod(Class type);
	
	/**
	 * set the Distribution Method associtated with this FillerNode
	 */
	public void setDistributionMethod(Class type,DistributionMethod dm);
	
	
	/**
	 * set the preview percentage, return the true future value..
	 * for example if a value cannot be reached it will return the maximum
	 * possible value
	 * @see #getMaximumPreviewPercentage()
	 */
	public double setPreviewPercentage(double d);
	
	/**
	 * get the preview percentage<BR>
	 * will return the last value returned by setPreviewPercentage
	 */
	public double getPreviewPercentage();
	
	/**
	 * get the maximum preview pecentage that can be set
	 */
	public double getMaximumPreviewPercentage();
	
	
	// ************ events
	/** 
	 * Add an action Listener to this Node.. it should
	 * be fired when modification on the data occures
	 **/
	public void addWeakActionListener(ActionListener al);
	
	/** 
	 * Add an action Listener it will be advertised of any preview change
	 * on this node.
	 **/
	public void addPreviewWeakActionListener(ActionListener al);
	
	/** 
	 * Add an action Listener it will be advertised of any lock change
	 **/
	public void addLockWeakActionListener(ActionListener al);
	
	
	/** 
	 * Set the monitor for distribution
	 * @param md set to null to unset 
	 **/
	public void setDistributionMonitor(DistributionMonitor md);
	
	
	/** 
	 * TarifAssets may be paired (almost everytime) to TarifTransactions,
	 * sometimes, TariffAssets are more specialized than transactions, then
	 * assets may be invested but no transactions will be related to those 
	 * positions. 
	 * @return the sum of Assets that do not participate in transaction 
	 * distributions
	 **/
	public double getPercentOfNotDistributedToTr();
	
}
/*
 * $Log: FillerNode.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.17  2004/11/11 12:38:02  perki
 * New distribution system for the Filler
 *
 * Revision 1.16  2004/11/08 15:32:05  perki
 * I went haunting the repartition bug #37
 *
 * Revision 1.15  2004/10/12 17:15:21  perki
 * Filler now display which nodes do not participate in transaction distribution
 *
 * Revision 1.14  2004/10/12 10:21:35  perki
 * detecting when repartition is not total in transactions
 *
 * Revision 1.13  2004/08/27 10:02:09  kaspar
 * ! Refactor: Put DistributionMonitor in its own file
 *
 * Revision 1.12  2004/08/02 14:17:11  perki
 * Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 * Revision 1.11  2004/08/02 10:08:43  perki
 * introducing distribution for transactions
 *
 * Revision 1.10  2004/08/02 09:40:43  kaspar
 * ! Display of pie pieces is now done with the same percentage
 *   rounding that is done up in the panel
 *
 * Revision 1.9  2004/08/02 08:32:36  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/08/01 18:00:59  perki
 * *** empty log message ***
 *
 * Revision 1.7  2004/07/31 11:06:55  perki
 * Still have problems with the progressbar
 *
 * Revision 1.6  2004/07/30 17:52:46  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/07/30 15:38:19  perki
 * some changes
 *
 * Revision 1.4  2004/07/29 11:38:13  perki
 * Sliders should be ok now
 *
 * Revision 1.3  2004/07/29 10:42:20  perki
 * Sliders should be ok now
 *
 * Revision 1.2  2004/07/27 16:56:31  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/07/26 17:39:37  perki
 * Filler is now home
 *
*/