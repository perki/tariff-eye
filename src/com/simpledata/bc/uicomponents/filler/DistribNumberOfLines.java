/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 *$Id: DistribNumberOfLines.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.SNumField;

import org.apache.log4j.Logger;

/**
 * This is a distribution method.<BR>
 * This method asks for the default number of lines per assets<BR>
 * Number of line must be at least 1
 */
public class DistribNumberOfLines implements DistributionMethod{
	private static final Logger m_log = Logger.getLogger( DistribNumberOfLines.class );
	
	int numOfLine;
	FillerNode owner;
	
	/**
	 * @param fn is the FillerNode that own this distribute Action.
	 * It is used by some Distribution method to find parent's Distributions
	 */
	public DistribNumberOfLines(FillerNode fn) {
		assert fn != null : "Cannot work on a null FillerNode";
		owner = fn;
		
		class Temp {
			int numberOfLines = 0;
			int numberWS = 0; 
		}
		
		
		
		final Temp counter = new Temp();
		
		// go look under and determine the average size of a line
		
		FillerVisitor fv = new FillerVisitor() {

			public void run(NodeInfo ni) {
				// run only on concerned childrens
				if (owner != ni && 
						! ni.getDistributionMethod(AssetsRoot0.class
								).methodForward())
					return ;
				
				if (ni.getChildren().length == 0) {
					Iterator/*<AssetsRoot0>*/ i
						= ni.getRootWorkSheets().iterator();
					
					while (i.hasNext()) {
						ArrayList/*<OptionMoneyAmount>*/ al =
							((AssetsRoot0) i.next()).getOptions(
									OptionMoneyAmount.class);
						
						// count only those with postions
						if (al.size() > 0) {
							counter.numberWS++;
							
							Iterator/*<OptionMoneyAmount>*/ it = al.iterator();
							while (it.hasNext()) {
								counter.numberOfLines += 
									((OptionMoneyAmount) it.next()
											).numberOfLines(null);
							}
						}
					}
				} else {
					ni.runOnChildren(this);
				}
			}
			
		};
		
		if (owner instanceof NodeInfo) {
			fv.run((NodeInfo) owner);
		}
		
		int calculatedNumOfLines = (counter.numberWS > 0) ?
			counter.numberOfLines / counter.numberWS : 1;
		
		setNumOfLine((calculatedNumOfLines > 0) ? calculatedNumOfLines : 1);
	}
	
	
	/**
	 * get the number of options the will be created from this node
	 */
	public int getCost(NodeInfo start) {
		class Cost {
			int value = 0;
		}
		
		final Cost cost = new Cost();
		
		FillerVisitor fv = new FillerVisitor() {
			public void run(NodeInfo ni) {
				// run only on concerned childrens
				if (owner != ni && 
						! ni.getDistributionMethod(AssetsRoot0.class
								).methodForward())
					return ;
				
				Money m = (Money) ni.getAmount().copy();
				if (m.getValueDouble() == 0) return;
				
				if (ni.getChildren().length == 0) {
					cost.value += ni.getRootWorkSheets().size();
				} else {
					ni.runOnChildren(this);
				}
				
			}
		};
		
		fv.run(start);
		
		return cost.value;
	}
	
	/**
	 * set the number of line per asset
	 * @param n must be sup or equals to 1
	 */
	public void setNumOfLine(int n) {
		if (n < 1) n = 1;
		numOfLine = n;
	}
	
	public boolean methodForward() {
		return false;
	}
	
	/** 
	 * return the FillerNode that owns this distribute action<BR>
	 * This is used by DistributMethod that need some informations
	 * about the parents
	 **/
	public FillerNode getOwner() {
		return owner;
	}
	
	/** 
	 * distribute this amount of money on this WorkSheet<BR>
	 */
	public void distribute(Money m,Dispatcher tws,
			DistributionMonitor dm) {
		if (dm != null) dm.distributionMonitorStep();
				
		if (! (tws instanceof AssetsRoot0)) {
			m_log.error( "Got a Worplace:"+tws );
			return;
		}
		AssetsRoot0 ws = (AssetsRoot0) tws;
		
		m.operationFactor(1d/numOfLine);
		
		if (m.getValueDouble() == 0) return;
			
		OptionMoneyAmount bco;
		bco = (OptionMoneyAmount) 
			OptionManager.createOption(ws, 
				OptionMoneyAmount.class);
		bco.setMoneyValue((Money) m.copy()); 
		bco.setNumberOfLines(numOfLine);
	}

	/** the text that will be shown on the UI.. will be translated **/
	private static final String TOPIC_STR = 
		"Number of line per asset position:";
	
	JPanel ui;
	/**
	 * @see com.simpledata.bc.uicomponents.filler.DistributionMethod#getUI()
	 */
	public JPanel getUI() {
		if (ui != null) return ui;
		ui = new JPanel(new FlowLayout(FlowLayout.LEADING));
		SNumField sn = new SNumField(""+numOfLine,true) {
			public void stopEditing() {
				Integer i = getInteger();
				if (i == null || i.intValue() < 1 ) {
					i = new Integer(numOfLine);
					setDouble(i.doubleValue());
				}
				
				if (numOfLine != i.intValue()) {
					numOfLine = i.intValue();
					owner.redistributeRepartition(AssetsRoot0.class);
				}
			}

			public void startEditing() {}
		};
		
		ui.add(
		new JLabel(Lang.translate(TOPIC_STR)));
		
		sn.setPreferredSize(new Dimension(30,20));
		ui.add(sn);
		return ui;
	}
	
	/**
	 * Refresh the UI
	 */
	public void refreshUI() {};
	

	/**
	 * @see com.simpledata.bc.uicomponents.filler.DistributionMethod#getSummary()
	 */
	public String getSummary() {
		return Lang.translate(TOPIC_STR)+numOfLine;
	}
}
/*
 *$Log: DistribNumberOfLines.java,v $
 *Revision 1.2  2007/04/02 17:04:27  perki
 *changed copyright dates
 *
 *Revision 1.1  2006/12/03 12:48:42  perki
 *First commit on sourceforge
 *
 *Revision 1.14  2004/10/11 10:19:16  perki
 *Percentage on Transactions
 *
 *Revision 1.13  2004/09/29 14:45:54  perki
 **** empty log message ***
 *
 *Revision 1.12  2004/09/09 14:12:06  jvaucher
 *- Calculus for DispatcherBounds
 *- OptionCommissionAmountUnder... not finished
 *
 *Revision 1.11  2004/09/04 18:12:31  kaspar
 *! Log.out -> log4j
 *  Only the proper logger init is missing now.
 *
 *Revision 1.10  2004/09/02 13:26:54  kaspar
 *! Hacking the Progressbar together. This is not a permanent fix.
 *
 *Revision 1.9  2004/08/27 11:24:52  kaspar
 *! Moved all inner classes out of FillerData, this creates a more
 *  lisible design
 *
 *Revision 1.8  2004/08/27 10:02:09  kaspar
 *! Refactor: Put DistributionMonitor in its own file
 *
 *Revision 1.7  2004/08/04 06:13:04  perki
 *Filler speeded up
 *
 *Revision 1.6  2004/08/02 14:17:11  perki
 *Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 *Revision 1.5  2004/08/02 08:32:36  perki
 **** empty log message ***
 *
 *Revision 1.4  2004/07/31 11:06:55  perki
 *Still have problems with the progressbar
 *
 *Revision 1.3  2004/07/30 17:52:46  perki
 **** empty log message ***
 *
 *Revision 1.2  2004/07/27 17:54:05  perki
 **** empty log message ***
 *
 *Revision 1.1  2004/07/26 17:39:36  perki
 *Filler is now home
 *
 */