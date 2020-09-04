/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: DistribSizeOfLines.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.FlowLayout;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.tools.MoneyEditor;
import com.simpledata.bc.uicomponents.tools.MoneyValueInputSlave;

import org.apache.log4j.Logger;

/**
 * A distribution method based on the Average size of line
 */
public class DistribSizeOfLines implements DistributionMethod {
	private static final Logger m_log = 
	    Logger.getLogger( DistribSizeOfLines.class );
	
	Money sizeOfLine;
	FillerNode owner;
	
	/** this is the default line size if it cannot be determined **/
	public static final double DEFAULT_LINE_VALUE = 100000;
	
	/** this is the minimum line size if calculated value is low **/
	public static final double MIN_CALCULATED_LINE_VALUE = 1000;
	
	/** this is the minimum acceptable line size **/
	public static final double MIN_LINE_VALUE = 1;
	
	/**
	 * @param fn is the FillerNode that own this distribute Action.
	 * It is used by some Distribution method to find parent's Distributions
	 */
	public DistribSizeOfLines(FillerNode fn) {
		assert fn != null : "Cannot work on a null FillerNode";
		owner = fn;
		
		
		class Temp {
			int numberOfLines = 0;
			double sumOfAmounts = 0; // in def currency
		}
		
		sizeOfLine = new Money(MIN_CALCULATED_LINE_VALUE);
		
		final Temp counter = new Temp();
		
		// go look under and determine the average size of a line
		
		FillerVisitor fv = new FillerVisitor() {

			public void run(NodeInfo ni) {
				//	run only on concerned childrens
				if (ni != owner && 
						! ni.getDistributionMethod(AssetsRoot0.class
								).methodForward())
					return ;
				
				if (ni.getChildren().length == 0) {
					Iterator/*<AssetsRoot0>*/ i
						= ni.getRootWorkSheets().iterator();
					
					while (i.hasNext()) {
						Iterator/*<OptionMoneyAmount>*/ i2 = 
						((AssetsRoot0) i.next()).getOptions(
								OptionMoneyAmount.class).iterator();
						OptionMoneyAmount oma;
						while (i2.hasNext()) {
							oma = (OptionMoneyAmount) i2.next();
							counter.numberOfLines += oma.numberOfLines(null);
							counter.sumOfAmounts += oma.moneyValueTotal(null
									).getValueDefCurDouble();
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
			
		double averageSize = DEFAULT_LINE_VALUE;
		if (counter.numberOfLines > 0) {
			averageSize = counter.sumOfAmounts / counter.numberOfLines;
		}
		if (averageSize < MIN_CALCULATED_LINE_VALUE) {
			averageSize = MIN_CALCULATED_LINE_VALUE;
		}
		
		setLineSize(averageSize);
	}
	
	
	
	/**
	 * set the line size in default Currency value
	 * @param d must be sup or equals to MIN_LINE_VALUE
	 */
	public void setLineSize(double d) {
		if (d < MIN_LINE_VALUE) d = MIN_LINE_VALUE;
		sizeOfLine.setValue(new Money(d));
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
	 * Distribute this amount of money on this WorkSheet
	 */
	public void distribute( Money m, Dispatcher tws, DistributionMonitor dm ) {		
		if (dm != null) dm.distributionMonitorStep();
		
		
		
		if (! (tws instanceof AssetsRoot0)) {
			m_log.error( "Got a Worplace:"+tws );
			return;
		}
		AssetsRoot0 ws = (AssetsRoot0) tws;
		
		int numOfLine = getNumOfLineToDistribute(m,sizeOfLine);
		
		m.operationFactor(1d/numOfLine);
		
		if (m.getValueDouble() == 0) return;
	
		OptionMoneyAmount bco;
		bco = (OptionMoneyAmount) 
			OptionManager.createOption(ws, 
				OptionMoneyAmount.class);
		bco.setMoneyValue((Money) m.copy()); 
		bco.setNumberOfLines(numOfLine);
	}
	
	/**
	 * Gets Number of line to distribute for this amount of 
	 * money and this sizeOfLine<BR>
	 */
	public static int getNumOfLineToDistribute(Money m,Money sizeOfLine) {
		double averageNumOfLine = 1;
		
		double mValue = m.getValueDouble(sizeOfLine.getCurrency());
		double solValue = sizeOfLine.getValueDouble();
		
		if (sizeOfLine.getValueDefCurDouble() > 1)
			averageNumOfLine =  mValue / solValue;
			
		
		if (averageNumOfLine <= 0) averageNumOfLine = 1;
		
		// calculate the closest number of line to achieve this
		int numOfLineMin = (int) Math.ceil(averageNumOfLine);
		int numOfLineMax = (int) Math.floor(averageNumOfLine);
		
		// find the closest line size
		double minDiff = Math.abs(solValue - (mValue / numOfLineMin));
		double maxDiff = Math.abs(solValue - (mValue / numOfLineMax));
		
		return (minDiff > maxDiff) ? numOfLineMax : numOfLineMin;
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
				if (ni.getChildren().length == 0) {
					
					int numOfWS = ni.getRootWorkSheets().size();
					
					if (numOfWS == 0) return;
					
					Money m = (Money) ni.getAmount().copy();
					if (m.getValueDouble() == 0) return;
					
					cost.value += numOfWS;
				} else {
					ni.runOnChildren(this);
				}
				
			}
		};
		
		fv.run(start);
		
		return cost.value;
	}
	

	/** the text that will be shown on the UI.. will be translated **/
	private static final String TOPIC_STR = 
		"Preffered size of a line:";
	
	JPanel ui;
	/**
	 * @see com.simpledata.bc.uicomponents.filler.DistributionMethod#getUI()
	 */
	public JPanel getUI() {
		if (ui != null) return ui;
		ui = new JPanel(new FlowLayout(FlowLayout.LEADING));
		MoneyValueInputSlave me = new MoneyValueInputSlave(sizeOfLine) {
			Money previousValue;
			public void stopEdit() {
				if (sizeOfLine.getValueDouble() < MIN_LINE_VALUE) {
					sizeOfLine.setValue(MIN_LINE_VALUE);
					refresh();
				}
				if (! previousValue.xequals(sizeOfLine)) {
					owner.redistributeRepartition(AssetsRoot0.class);
				}
			}

			public void startEdit() {
				previousValue = (Money) sizeOfLine.copy();
			}
		};
		
		ui.add(
		new JLabel(Lang.translate(TOPIC_STR)));
		
		ui.add(me);
		return ui;
	}
	
	/**
	 * Refresh the UI
	 */
	public void refreshUI() {};

	/**
	 * @see DistributionMethod#getSummary()
	 */
	public String getSummary() {
		return Lang.translate(TOPIC_STR)+MoneyEditor.MoneyToSTring(sizeOfLine);
	}
}

/*
 * $Log: DistribSizeOfLines.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.14  2004/10/11 10:19:16  perki
 * Percentage on Transactions
 *
 * Revision 1.13  2004/09/29 14:45:54  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.11  2004/09/09 14:12:06  jvaucher
 * - Calculus for DispatcherBounds
 * - OptionCommissionAmountUnder... not finished
 *
 * Revision 1.10  2004/09/04 18:12:31  kaspar
 * ! Log.out -> log4j
 *   Only the proper logger init is missing now.
 *
 * Revision 1.9  2004/09/02 13:26:54  kaspar
 * ! Hacking the Progressbar together. This is not a permanent fix.
 *
 * Revision 1.8  2004/08/27 11:24:53  kaspar
 * ! Moved all inner classes out of FillerData, this creates a more
 *   lisible design
 *
 * Revision 1.7  2004/08/27 10:02:09  kaspar
 * ! Refactor: Put DistributionMonitor in its own file
 *
 * Revision 1.6  2004/08/04 06:13:04  perki
 * Filler speeded up
 *
 * Revision 1.5  2004/08/04 06:03:12  perki
 * OptionMoneyAmount now have a number of lines
 *
 * Revision 1.4  2004/08/02 14:55:43  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/08/02 14:17:11  perki
 * Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 * Revision 1.2  2004/08/02 08:32:36  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/07/31 11:06:55  perki
 * Still have problems with the progressbar
 *
 */