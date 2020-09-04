/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.components.tarif.TarifAssets;
import com.simpledata.bc.components.tarif.TarifTransactions;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.calculus.ComCalc;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.money.TransactionValue;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.tools.ActionEventHandler;
import com.simpledata.bc.uicomponents.tools.MoneyEditor;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SNumField;

/** 
 * NodeInfo stores all information that is associated to 
 * a CompactNode node used in distributing the money in a 
 * top down manner. It is like a memento attached to the 
 * CompactNodes, storing what amount went to what node. 
 *
 * FIXME: This comment should be way longer. 
 * 
 * @version $Id: NodeInfo.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 * @author SimpleData SARL, 2004, all rights reserved. 
 */
class NodeInfo implements NamedEventListener, FillerNode {
	private static Logger m_log = Logger.getLogger( NodeInfo.class );
	/** the value under which we can consider a double is zero **/
	private static final double EPSILON = 0.0001d;
	/** the default rollout value if it cannot be calculated **/
	private static final double DEFAULT_ROLLOUT = 0d;
	
	/** the CompactNode I'm attached to*/
	private CompactNode cn;
	/** 
	 * The instance of FillerData that created this class.
	 * Acts as a 'Controller' in some sense. 
	 */
	private FillerData m_controller;  
	/** the amount of money under this Node **/
	private Money amountUnder;
	/** the percentage this money amount represent **/
	private double mpercentage;
	
	/** 
	 * the rollout associated with this node<BR>
	 * This is the volume of transaction that will be made<BR>
	 * = -1 if this node rely on it's parent for the roolout;
	 */
	private double rollout;
	
	/** my Parent Node Info (null if root) **/
	private NodeInfo parent;
	/** my children Node Info (never null) **/
	private ArrayList/*<NodeInfo>*/ children;
	/** the distribution Method for Assets associated with this NodeInfo **/
	private DistributionMethod distributionMethodA;
	/** the distribution Method for Transactions **/
	private DistributionMethod distributionMethodT;
	/** this is set to true if this node is dependent from others **/
	private boolean locked;
	/** the Object that monitor my progress when distributing **/
	private DistributionMonitor distributionMonitor;
	
	/** construct a Node info based on this CompactNode **/
	public NodeInfo( FillerData data, CompactNode cn) {
		assert cn != null : "Cannot handle null cn";
		assert cn.getChildrenAL() != null : 
			"this class is stupid:"+cn.getClass();
		
		m_controller = data; 
		
		setDistributionMonitor(null); // reset the distribution Monitor
		
		if (cn.getParent() != null) { // only ask for non null parents
			// my parent must be in the cache
			parent = m_controller.getNodeInfo((CompactNode)cn.getParent());
			//_setPercentage(0d);
			locked = false; // by default I'm not locked
		} else {
			
			locked = true; // no parent then I'm locked
		}
		//_setPercentage(1d);// If y've no parent percentage is 100%
		mpercentage = -1d;
		
		// init
		this.cn = cn;
		
		children = new ArrayList/*<NodeInfo>*/();
		amountUnder = new Money(0d);
		rollout = -1d;
		
		// add this instance as authority on cn and its pair. 
		m_controller.addToCache( cn, this );
		
		Iterator/*<CompactNode>*/i
			=	m_controller.getChildrenInteresting(cn).iterator();
		int counter = 0;
		NodeInfo child;
		while (i.hasNext()) {
			child = m_controller.getNodeInfo((CompactNode) i.next());
			children.add(child);
			// lock the first child by default
			if (counter == 0) child.lock(true);
			counter++;
		}
		
		//once all my children have been created I can calculate my amount
		calculateMoneyUnder();
		
		// be sure to set childs percentage
		revalidateChildsPercentage();
		
		
		//and my rollout value
		
		// TODO maybe update the rollout value
		//rolloutRecalculate();
		
		
		//I cannot have children And Tarifs!!
		//This may Happend if showOther of the compactTree is set to
		//false.. then we need some more coding for now thru an error
		
		if (! cn.getExplorer().showOthers()) {
			m_log.fatal(
				"This version does not support compact Tree"+
				" with show other Off node:["+cn+"]"
			);
		}
		
		//I now attach listener to possible tarifs I'm attached too
		//If it's content changes then I've to recalculate my amount
		// and tell my parents to do so
		Iterator/*<WorkSheet>*/ j = getRootWorkSheets().iterator();
		while (j.hasNext()) {
			((WorkSheet) j.next()).addNamedEventListener(
					this,-1,null);
		}
	}
	
	/**
	 * change my percentage with no calculus
	 */
	private void _setPercentage(double d) {
		_setPreviewPercentage(d);
		if (d != mpercentage) {
			mpercentage = d;
			fireActionEvent();
		}
	}
	
	/**
	 * get the value of my percentage
	 */
	private double _getPercentage() {
			return mpercentage;
	}
	
	/**
	 * get the Distribution Method associated with this FillerNode
	 */
	public DistributionMethod getDistributionMethod(Class type) {
		
		
		// check if the actual distribution method is valid
		if (type == AssetsRoot0.class) {
			if (distributionMethodA == null 
				|| (! DistributionManager.fillerNodeAccepts(type,
						this,distributionMethodA.getClass())))
				DistributionManager.setPreferedDistribMethod(type,this);
			return distributionMethodA;
		}
		
		//	check if the actual distribution method is valid
		if (type == TransactionsRoot0.class) {
			if (distributionMethodT == null 
				|| (! DistributionManager.fillerNodeAccepts(type,
						this,distributionMethodT.getClass())))
				DistributionManager.setPreferedDistribMethod(type,this);
			return distributionMethodT;
		}
		
		return null;
	}
	
	/**
	 * set the Distribution Method associtated with this FillerNode
	 */
	public void setDistributionMethod(Class type,DistributionMethod dm) {
		if (DistributionManager.fillerNodeAccepts(type,this,dm.getClass())){
			if (AssetsRoot0.class == type)
					distributionMethodA = dm;
			if (TransactionsRoot0.class == type)
				distributionMethodT = dm;	
		}
		
	}
	
	/**
	 * get the compactNode associated with this NodeInfo
	 */
	public CompactNode getCompatcNode() {
	    return cn;
	}
	
	
	/** 
	 * TarifAssets may be paired (almost everytime) to TarifTransactions,
	 * sometimes, TariffAssets are more specialized than transactions, then
	 * assets may be invested but no transactions will be related to those 
	 * positions. 
	 * @return the percentage of assets at this node that will not be 
	 * distributed
	 **/
	public double getPercentOfNotDistributedToTr() {
	    
	    if (getPercentage() <= 0.001) return 0; 
	    
	    class Temp implements FillerVisitor {
	        double n = 0d;
	        TarifAssets t;
	        
	        double cumul = 1d / getPercentage();
            public void run(NodeInfo ni) {
                double oldCumul = cumul;
                cumul = cumul * ni.getPercentage();
                
                
                if (cumul >= 0.001) {
                    
                    int c = 0;
                    int nots = 0;
                    for (Iterator i = ni.getTarifs().iterator(); 
                    i.hasNext(); c++) {
                        t = ((TarifAssets) i.next());
                        if (t.pairedGet() == null) {
                            nots++;
                        }
                    }
                    if (c > 0) n += cumul * nots / c;
                    
                    
                    ni.runOnChildren(this);
                    
                }
                cumul = oldCumul; 
                return;
               
                
            }};   
	    
	    Temp fv = new Temp();
        fv.run(this);
        
        return fv.n;
	}
	
	/** 
	 * Get the Tarifs I'm attached to.
	 * @return only TarifAssets 
	 **/
	public ArrayList/*<TarifAssets>*/ getTarifs() {
		ArrayList/*<TarifAssets>*/ result=new ArrayList/*<TarifAssets>*/();
		Object[] o = cn.contentsGet();
		for (int j = 0; j < o.length; j++) {
			if (o[j] instanceof TarifAssets) {
				result.add(o[j]);
			}
		}
		return result;
	}
	
	/** 
	 * Gets the Root WorkSheets I'm attached to
	 * @return only AssetsRoot0 rootWorkSheets of TarifAssets
	 * **/
	public ArrayList/*<AssetsRoot0>*/ getRootWorkSheets() {
		ArrayList/*<AssetsRoot0>*/ result=new ArrayList/*<AssetsRoot0>*/();
		AssetsRoot0 temp ;
		for (Iterator/*<TarifAssets>*/ i=getTarifs().iterator();
			i.hasNext();) {
			temp = ((TarifAssets) i.next()).getRootWorkSheet();
			if (temp == null) {
				m_log.error( " has a null root WS" );
			} else {
				result.add(temp);
			}
		}
		return result;
	}

	
	/** recalculate my Percentage and tell my childs to do so **/
	private void revalidateChildsPercentage() {
		// first of all tell my children to calculate
		runOnChildren(
			new FillerVisitor() {
				public void run( NodeInfo ni ) {
					ni.calculatePercentage();
				}
			}  // new FillerVisitor
		);  // runOnChildren
	}
	
	
	/** 
	 * Recalculates MoneyUnder value.
	 * 
	 * In case the value changed It will take care of advertising 
	 * Parent to recalculate their amount and children to calculate
	 * their percentages
	 * 
	 **/
	private void calculateMoneyUnder () {
		// remember my last value
		double lastValue = amountUnder.getValueDefCurDouble();
		
		// reset my value
		amountUnder.setValue(0d);
		
		runOnChildren(new FillerVisitor() {
			public void run(NodeInfo ni) {
				amountUnder.operation(ni.getAmountUnder(),1f);
			}
		});
		
		
		// If my CompactNode is attached to a Tarif add it's value
		Iterator/*<AssetsRoot0>*/ i = getRootWorkSheets().iterator();
		while (i.hasNext()) {
			amountUnder.operation(
					((AssetsRoot0) i.next()).getSumOfAmountLocal(),1f);
		}
		
		// If my value has changed I must tell my parents to do so
		if (lastValue != amountUnder.getValueDefCurDouble()) {
			if (parent != null)
				parent.calculateMoneyUnder();
			// and to tell my children to change their percentage
			revalidateChildsPercentage();
			fireActionEvent();
		} 
	}
	
	/** recalculate my percentage **/
	private void calculatePercentage() {
		// If y've no father the it's 100%
		if (parent == null ) {
			_setPercentage(1d);
			return;
		}
		
	
		
		
		// get amount under daddy
		// get brother count
		double daddy_value = parent.getAmountUnder().getValueDefCurDouble();
		int brothers = parent.getChildren().length;
	
		
		// if daddy value is 0 then I keep my value unless I've no
		// percentage set yet.. then i take a default value
		if (daddy_value < EPSILON) {
			if (_getPercentage() < 0) { // only at initialisation
				assert brothers > 0;
				_setPercentage(1d / brothers);
			}
		} else {
			_setPercentage(
					getAmountUnder().getValueDefCurDouble()/daddy_value);
		}
	}
	
	
	/** get the amount under this node **/
	private Money getAmountUnder() {
		return amountUnder;
	}
	
	
	
	
	/**
	 * <B>INTERFACE</B>com.simpledata.bc.datamodel.event.NamedEvent<BR>
	 * called when an event occures on my WorkSheets
	 */
	public void eventOccured(NamedEvent e) {
		// if I'm deaf do nothing
		if ( m_controller.amIDeaf() ) {
			return;
		}
		
		int ec = e.getEventCode();
		if (ec != NamedEvent.WORKSHEET_OPTION_DATA_CHANGED &&
				ec != NamedEvent.WORKSHEET_OPTION_REMOVED &&
				ec != NamedEvent.WORKSHEET_OPTION_ADDED)
			return ;
				
		// check that this NamedEvent concerns a MoneyAmount
		if (e.getUserObject() != null 
				&& e.getUserObject() instanceof OptionMoneyAmount) {
			// I've to recalculate my self
			calculateMoneyUnder();
			
		}
	}
	
	
	//************* Interface FillerNode **************//
	
	/** return the percentage at this node **/
	public double getPercentage() {
		return _getPercentage();
	}
	
	/** 
	 * return the Amount at this node 
	 **/
	public Money getAmount() {
		return (Money) amountUnder.copy();
	}
	
	/** 
	 * commit the value of preview Percentage
	 **/
	public void commit() {
		assert parent != null : "Cant change a percentage with parent null";
		
		// XXX this method of handling context is not thread safe.
		boolean oldDepth = m_controller.setDeaf( true );
		
		parent.runOnChildren(new FillerVisitor(){
			public void run(NodeInfo ni) {
				ni._setPercentage(ni.previewPercentage);
			}});
		
		// tell my parent to redistribute percentages
		parent.redistribute();
		
		m_controller.setDeaf( oldDepth );
	}
	
	
	
	
	
	/** 
	 * Sets the money amount under this node. 
	 */
	public void setMoneyAmount(final Money m) {
		groupCalculusStart();
		final NodeInfo tthis = this;
		
		//~ Thread th = new Thread()  {
		//~ public void run() {
		//~ if ( ! m_controller.acquireCalculationThread() )
		//~ return;
		
		ModalJPanel mjp = new ModalJPanel(
				new JLabel(""),
				BC.bc.getMajorComponent(),
				null
		);
		
		boolean oldDeaf = m_controller.setDeaf( true );
		
		Money mm = m;
		
		boolean force = false;
		// if money == null then it is a redistribution
		if (m == null) {
			mm = amountUnder;
			force = true;
		}
		
		
		distributionMonitor.distributionMonitorStart(
				getDistributionMethod(AssetsRoot0.class
				).getCost(tthis)+
				getDistributionMethod(TransactionsRoot0.class
				).getCost(tthis)
		);
		
		_setMoneyAmount(mm,force,distributionMonitor);
		
		
		// go tell the leaf to recalculate
		FillerVisitor recalculateAll = new FillerVisitor(){
			public void run(NodeInfo ni) {
				if (ni.children.size() == 0) {
					ni.calculateMoneyUnder();
				} else {
					ni.runOnChildren(this);
				}
			}
		};
		
		recalculateAll.run(tthis);
		
		
		// redistribute transactions
		_redistributeRepartitionTransactions(distributionMonitor);
		
		m_controller.setDeaf( oldDeaf );
		
		distributionMonitor.distributionMonitorDone();
		
		mjp.close();
		
		//~ m_controller.releaseCalculationThread();
		//~ }
		//~ };
		//~ th.start();
		groupCalculusStop();
		
		// tell my parent to refresh
		if (parent != null)
			parent.fireActionEvent();
		
		
	}
	
	/** redistribute the money depending on percentages **/
	public void redistribute() {
		setMoneyAmount(null);
	}
	
	/** 
	 * Redistributes the money on leaf because the repartition changed. 
	 */
	public void redistributeRepartition(Class type) {
		if (type == AssetsRoot0.class) 
			redistributeRepartitionAssets();
		
		if (type == TransactionsRoot0.class) 
			redistributeRepartitionTransactions();
	}

	/** utility to start a grouped calculus **/
	private void groupCalculusStart() {
		ComCalc cc = cn.getExplorer().getTarification().comCalc();
		if (cc != null) cc.groupedStart();
	}
	
	/** utility to stop a grouped calculus (will flush down all calculus **/
	private void groupCalculusStop() {
		ComCalc cc = cn.getExplorer().getTarification().comCalc();
		if (cc != null) cc.groupedStop();
	}
	
	
	/**
	 * Redistributes the money on leaf because the repartition changed. 
	 * This method handles only nodes of type <code>Transactions</code>. 
	 */
	private void redistributeRepartitionTransactions() {
		final NodeInfo tthis = this;
		groupCalculusStart();
		
		
		
		boolean oldDepth = m_controller.setDeaf( true );
		
		ModalJPanel mjp = new ModalJPanel(
				new JLabel(""),
				BC.bc.getMajorComponent(),
				null
		);
		
		
		distributionMonitor.distributionMonitorStart(
				getDistributionMethod(TransactionsRoot0.class).
				getCost(tthis));
		
		
		_redistributeRepartitionTransactions(distributionMonitor);
		
		mjp.close();
		
		m_controller.setDeaf( oldDepth );
		
		fireActionEvent();
		distributionMonitor.distributionMonitorDone();
		
		//~ m_controller.releaseCalculationThread();
		//~ }
		//~ };
		//~ th.start();
		groupCalculusStop();
	}
	
	/**
	 * Redistributes the money on leaf because the repartition changed. 
	 * This method handles only nodes of type <code>Transactions</code>. 
	 */
	private void _redistributeRepartitionTransactions
		(final DistributionMonitor dm) {
		
		final NodeInfo tthis = this;
		FillerVisitor fv = new TransactionValueVisitor() {
			void gotWorkSheet(NodeInfo ni,TransactionsRoot0 tr,int n) {
				DistributionMethod distm = 
					ni.getDistributionMethod(TransactionsRoot0.class);
				
				if (distm instanceof DistribRelyOnParent) {
					((DistribRelyOnParent) distm).refresh();
				} else {
					if (tthis != ni) return;
				}
				
				
				// Money to distribute is :
				Money toDist = ni.getAmount();
				toDist.operationFactor(rolloutGetApplicable()/n);
				
			
				// clear this Transaction
				tr.clear();
				
				distm.distribute(toDist,tr,dm);
				
			}
		};
		
		fv.run(this);
		
	}
	
	/**
	 * Redistributes the money on leaf because the repartition changed. 
	 * This method handles only nodes of type <code>Assets</code>. 
	 */
	private void redistributeRepartitionAssets() {
		
		final NodeInfo tthis = this;
		//~ Thread th = new Thread()  {			
		//~ public void run() {
		//~ if ( ! m_controller.acquireCalculationThread() )
		//~ return;
		
		
		// tell ComCalc to group the following calculus
		groupCalculusStart(); // will be commited by groupCalculusStop()
		
		boolean deaf = m_controller.setDeaf( true );
		
		ModalJPanel mjp = new ModalJPanel(
				new JLabel(""),
				BC.bc.getMajorComponent(),
				null
		);
		
		
		int cost = 
			getDistributionMethod(AssetsRoot0.class).getCost(tthis)+  
			getDistributionMethod(TransactionsRoot0.class).
			getCost(tthis);
		
		distributionMonitor.distributionMonitorStart(cost);
		
		
		//	go tell the slaves leaf to recalculate
		FillerVisitor advertiseToSlaves = new FillerVisitor(){
			public void run(NodeInfo ni) {
				DistributionMethod distm = 
					ni.getDistributionMethod(AssetsRoot0.class);
				
				if (distm instanceof DistribRelyOnParent) {
					((DistribRelyOnParent) distm).refresh();
				} else {
					if (tthis != ni) return;
				}
				
				if (ni.children.size() == 0) {
					ni._setMoneyAmount(ni.amountUnder,true,
							distributionMonitor);
				} else {
					ni.runOnChildren(this);
				}
			}	
		};
		advertiseToSlaves.run(tthis);
		
		
		// distribute transactions
		_redistributeRepartitionTransactions(distributionMonitor);
		
		mjp.close();
		
		m_controller.setDeaf( deaf );
		
		fireActionEvent();
		distributionMonitor.distributionMonitorDone();
		
		//~ m_controller.releaseCalculationThread();
		//~ }			
		//~ }; // anonymous thread 
		
		//~ th.start();
		groupCalculusStop(); // commit the grouped calculus
	}
	
	/** 
	 * Sets the money amount under this node.
	 */
	private void _setMoneyAmount(final Money m,boolean force,
			final DistributionMonitor dm) {
	    
	    
		if ((! force ) &&
			m.getValueDefCurDouble() == amountUnder.getValueDefCurDouble())
			return; // nothing to do
		
		
		
		if (children.size() == 0) {
			// if I've no childs then I should have workSheets
			setMoneyAmountOnWS(m,dm);
			
		} else {
			// depending on the percentage give part of money to each one
			// of my children
			runOnChildren(new FillerVisitor(){
				public void run(NodeInfo ni) {
					Money mm 
					= new Money(m.getValueDouble() * ni._getPercentage(),
							m.getCurrency());
					ni._setMoneyAmount(mm,false,dm);
				}});
		}
		
	}
	
	/** 
	 * Set the money amount on my workSheet 
	 */
	private void setMoneyAmountOnWS( Money m, DistributionMonitor monitor ) {
		assert m != null : "Cannot work on null amount";
		
		
		
		ArrayList/*<WorkSheet>*/ wss = getRootWorkSheets();
		if (wss.size() == 0) return;
		// get the Amount Per WorkSheet
		Money amountPerWS = m.divide( wss.size() );
		
		// add an option with this amount for each WorkSheet
		AssetsRoot0 ws ;
		for (Iterator i = wss.iterator();i.hasNext();) {
		    ws = (AssetsRoot0) i.next();
		    
		    
		    ws.clear();
		    
		    // do not distribute if amount == 0
		    if (amountPerWS.getValueDouble() > 0) {
		        DistributionMethod method = 
		            getDistributionMethod( AssetsRoot0.class );
		        assert method != null : "I need a valid distribution method"; 
		        
		        method.distribute( 
		                (Money) amountPerWS.copy(), 
		                ws,
		                monitor
		        );
		    }
		    
		}
	}
	
	/** get the title of this node **/
	public String getTitle() {
		return cn.toString();
	}
	
	/** return the children of this node **/
	public FillerNode[] getChildren() {
		return (FillerNode[]) children.toArray(ARRAY_CLASS);
	}
	
	/** return the parent of this node (null if root) **/
	public FillerNode getParent() {
		return  parent;
	}
	
	//************* Visitor handlers **************//
	/**
	 * Vistor handler.. It will call run on vistor for each of it's children
	 */
	public void runOnChildren(FillerVisitor v) {
		assert v != null;
		for (Iterator/*<NodeInfo>*/ i=children.iterator();i.hasNext();){
			v.run((NodeInfo)i.next());
		}
	}
	
	//*************** Events **************//

	private ActionEventHandler eventHandler;

	/** Add an action listener for event change **/
	public void addWeakActionListener(ActionListener listener) {
		if (eventHandler == null) eventHandler = new ActionEventHandler();
		eventHandler.addWeakActionListener(listener);
	}

	private void fireActionEvent() {
		if ( m_controller.amIDeaf() ) {
		    return;
		}
		if (eventHandler == null) {
		    return;
		}
		eventHandler.fireActionEvent("");
	}
	
	
	//*************** For display purposes *****************//
	public String toString() {
		return "<TR><TD>"+cn.toString()+"</TD><TD>"+
		SNumField.formatNumber(getPercentage()*100,1,true)
		+"% </TD><TD ALIGN=RIGHT>"+
		SNumField.formatNumber(amountUnder.getValueDouble(),2,true)
		+"</TD><TD>"+amountUnder.getCurrency()+"</TR>";
	}
	
	public String toStringTitle() {
		return "<B>"+cn.toString()+"</B>  "+
		MoneyEditor.MoneyToSTring(amountUnder)+" "+
		Lang.translate("Rollout:")+
		SNumField.formatNumber(rolloutGetApplicable()*100,0,true)+"%";
	}

	
	//---------------------- Previews -----------------------//
	
	/** a percentage that is kept for preview only **/
	double previewPercentage;
	
	/** get the maximum value for the preview percentage **/
	public double getMaximumPreviewPercentage() {
		assert parent != null : "Cant change a percentage with parent null";
		if (locked) return 1d;
		
		//			 get the max value possible for me
		PairedSearch max = _getMaximumPreviewPercentage();
		
		return max.value;
	}
	
	private PairedSearch _getMaximumPreviewPercentage() {
		
		//			 get the max value possible for me
		final PairedSearch max = new PairedSearch(_getPercentage());
		final NodeInfo tthis = this;
		parent.runOnChildren(new FillerVisitor() {
			public void run(NodeInfo ni) {
			    // add unlocked nodes values
				if (! ni.getLockState() && (tthis != ni)) { 
					max.value += ni._getPercentage();
					max.pairCounter++;
				}
			}
		});
		return max;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.filler.FillerNode
	 * #setPreviewPercentage(double)
	 */
	public double setPreviewPercentage(double d) {
		assert (d >= 0 && d <= 1) : "Invalid percentage "+d;
		assert parent != null : "Cant change a percentage with parent null";
		assert locked == false : "Cannot move a locked node:"+getTitle();
		
		// nothing to do here
		if (d == previewPercentage) return previewPercentage; 
		
		// get the max value possible for me
		final PairedSearch max = _getMaximumPreviewPercentage();
		
		// check that d does not exceed the max value
		d = (d > max.value) ? max.value : d;
		
		
		// calculate the delta of this preview
		double delta = getPreviewPercentage() - d;
		
		
		assert max.pairCounter > 0:
			"How comes I cannot find any locked node";
		
		final NodeInfo tthis = this;
			// if d is positive then I add proport. the delta to each node
		if (delta > EPSILON) {
			final double plus = delta / max.pairCounter;
			parent.runOnChildren(new FillerVisitor() {
				public void run(NodeInfo ni) {
					if ((! ni.getLockState()) && ni != tthis )
					ni._setPreviewPercentage(
							ni.previewPercentage + plus
					);
				}
			});
		} else if (delta < (-1 * EPSILON)) {
			//if delta is negative then I remove what I can on each node
			// turn delta into a positive value (easier for calculus)
			delta = -1 * delta;
			
			
			// this PairedSearch contains the number of node
			// that can have some value removed and the 
			// minimum value I can remove from them
			final PairedSearch rep = new PairedSearch(1d);
			FillerVisitor fv = new FillerVisitor() {
				public void run(NodeInfo ni) {
					// add all locked nodes values with some data to consume
					if (
					(! ni.getLockState()) && ni.previewPercentage > 0 
					&& ni != tthis ) { 
						rep.pairCounter++;
						
						if (rep.value > ni.previewPercentage) {
							rep.value = ni.previewPercentage;
							
						}
					}
				}
			};
			
			
			// loop and remove what we can on each node until delta is empty 
			//(we use an about empty value to prevent very long loops)
			while (delta > EPSILON ) {
				// reset rep
				rep.value = 1d; rep.pairCounter = 0;
				parent.runOnChildren(fv);
				
				// the maximum possible to remove on each node
				double minus = rep.value;
				
				if (rep.pairCounter > 0 ) {
					// do not remove more than I can
					double minusMax = delta / rep.pairCounter;
					
					minus = minus > minusMax ? minusMax : minus;
					
					// update the delta from what I will remove
					delta = delta - (minus * rep.pairCounter);
					
					final double myMinus = minus;
					// set the new percentages on the node
					parent.runOnChildren(new FillerVisitor(){
						public void run(NodeInfo ni) {
							if ((! ni.getLockState()) 
									&& ni.previewPercentage > 0 && ni != tthis){ 
								ni._setPreviewPercentage(
										ni.previewPercentage - myMinus
								);
							}
						}});
				}  else {
				    m_log.error("Loop");
				    d = d -delta;
				    delta = 0;
				}
			}
			
			
		}
		
		//	set my preview percentage
		_setPreviewPercentage(d);
		return previewPercentage;
	}
	
	
	/**
	 * Changes the preview percentage of a node with no calculus.<BR>
	 * Fires an event if previewPercentage changes.
	 * @param d Percentage to set, withhin 0.0, 1.0, gets normalized
	 *          to these bounds. 
	 */
	private void _setPreviewPercentage(double d) {
		if ( d == previewPercentage ) return; // nothing to do
		if ( d < 0.0 ) {d = 0.0; m_log.warn("d was < 0 :"+d);} 
		if ( d > 1.0 ) {d = 1.0; m_log.warn("d was > 1 :"+d);} 
		
		previewPercentage = d;
		if (eventHandlerPrv != null) {
			eventHandlerPrv.fireActionEvent("");
		}
	}

	/**
	 * @see com.simpledata.bc.uicomponents.filler.FillerNode
	 * #getPreviewPercentage()
	 */
	public double getPreviewPercentage() {
		return previewPercentage;
	}
	
	//------------------- PAIRING HANDELING ------------//
	/** get the paired all the paired WorkSheets **/
	public ArrayList/*<TransactionsRoot0>*/ getRootWorkSheetPaired() {
		ArrayList/*<TransactionsRoot0>*/ result = 
			new ArrayList/*<TransactionsRoot0>*/();
		TransactionsRoot0 temp ;
		TarifTransactions tt;
		for (Iterator/*<TarifAssets>*/ i=getTarifs().iterator();
			i.hasNext();) {
			
			tt = (TarifTransactions) ((TarifAssets) i.next()).pairedGet();
			
			if (tt != null) {
				temp = tt.getRootWorkSheet();
				if (temp != null) result.add(temp);
			}
		}
		return result;
	}
	
	//------------------- ROLLOUT HANDLING -------------//
	
	
	
	/**
	 * initialize the rollout value<BR>
	 * Go look into children nodes the actual rollout values<BR>
	 * This method has no effect If my value is -1 and
	 * if I'm not the root node. 
	 */
	public void rolloutRecalculate() {
		if (rollout < 0 && parent != null) return;
		
		if (amountUnder.getValueDefCurDouble() <= EPSILON ) {
			rolloutSet(DEFAULT_ROLLOUT,false);
			return;
		}
		
		class Counter {
			double in = 0;
			double out = 0;
		}
		
		final Counter counter = new Counter();
		
		FillerVisitor fv = new TransactionValueVisitor() {
			void gotLine(NodeInfo ni, TransactionValue tv) {
				double d = tv.getMoneyValue(
				).getValueDefCurDouble()*tv.getAverageNumber();
				if (tv.inGoingToBank()) {
					counter.in += d;
				} else {
					counter.out += d;
				}
			}
		};
		
		
		fv.run(this); // launch the visitor
		
		
		
		rolloutSet(
				(counter.in + counter.out) / 
				( 2 * amountUnder.getValueDefCurDouble()),false);
		
	}
	
	
	/**
	 * @return the rollOutValue on this node (-1 if relies on parent)
	 */
	public double rolloutGet() {
		if (rollout < 0 && parent == null) {
			rolloutSet(DEFAULT_ROLLOUT);
		}
		return rollout;
	}
	
	/** return true if this node relies on parent for it rollout **/
	public boolean rolloutReliesOnParent() {
		return (rolloutGet() < 0);
	}
	
	/**
	 * @return the applicable rollout value on this node.
	 */
	public double rolloutGetApplicable() {
		if (rolloutGet() < 0 && parent != null) 
			return parent.rolloutGetApplicable();
		return rolloutGet();
	}
	
	
	/**
	 * change the rollout value of this node.
	 * -1 for relies on parent
	 * <BR> THIS WILL CAUSE A REDISTRIBUTION OF TRANSACTIONS
	 */
	public void rolloutSet(double value) {
	    rolloutSet(value,true);
	}
	
	/**
	 * change the rollout value of this node.
	 * -1 for relies on parent
	 * @param redistribute set to true if this will imply a recalculation
	 */
	private void rolloutSet(double value,boolean redistribute) {
		if (value < 0 && parent == null) {
			value = DEFAULT_ROLLOUT;
		}
		
		
		if (rollout == value) return;
		rollout = value;
		fireActionEvent();
		
	
		// redistribute on Transactions
		if (redistribute)
			redistributeRepartitionTransactions();
	}
	
	
	//------------------- EVENTS -----------------------//
	
	/** an action Event Handler for preview change **/
	private ActionEventHandler eventHandlerPrv;
	/**
	 * @see com.simpledata.bc.uicomponents.filler.FillerNode
	 * #addPreviewWeakActionListener(java.awt.event.ActionListener)
	 */
	public void addPreviewWeakActionListener(ActionListener listener) {
		
		if (eventHandlerPrv == null) 
				eventHandlerPrv=new ActionEventHandler();
		eventHandlerPrv.addWeakActionListener(listener);
	}

	//----------------------- LOCKS ----------------------//
	
	
	
	/**
	 * lock this node (make it depend from other nodes)<BR>
	 * Take care some nodes cannot be locked.. only trust
	 * getLockState()
	 */
	public void lock(boolean b) {
		_lock(b,false);
	}
	private void _lock(boolean b,boolean shush) {
		if (b == locked) return; // nothing to do
		
		if (! canBeLocked(b)) b = !b;
		
		// advertise changes 
		if (b != locked) {
			locked = b;
			if (! shush) {
				_advertiseLock();
			}
		}
		
	}
	private void _advertiseLock() {
		FillerVisitor fv = new FillerVisitor() {
			
			public void run(NodeInfo ni) {
				if (ni.eventHandlerLock != null)
					ni.eventHandlerLock.fireActionEvent("");
			}
		};
		

		if (parent == null) {
			fv.run(this);
		} else {
			parent.runOnChildren(fv);
		}
	}

	
	/** 
	 * return true if this state can be achived by this Node
	 * @see #getLockState()
	 */
	public boolean canBeLocked(boolean b) {
	    if (! b) { // unlock this node
	        //If I'm the only child of my parent or I've no parent
			// I must be locked all the time
		    if (getParent()==null || getParent().getChildren().length<2) {
				return false;
			}

			return true;
		} 
		
		// lock this node
		
		if (getParent()==null || getParent().getChildren().length<3) {
			return false;
		}
		
		// We must keep at least two unlocked nodes 
		final PairedSearch max = new PairedSearch(0d);
		parent.runOnChildren(new FillerVisitor() {
			public void run(NodeInfo ni) {
				if (! ni.getLockState()) { // add all locked nodes values
					max.pairCounter++;
				}
			}
		});
		if (max.pairCounter < 3) 
			return false;
		
		return true;
	}

	/**
	 * @see com.simpledata.bc.uicomponents.filler.FillerNode#getLockState()
	 */
	public boolean getLockState() {
	    // if I'm alone then I'm locked
	    if (getParent() == null || getParent().getChildren().length < 2)
	        locked = true;
		return locked;
	}
	
	/** an action Event Handler for lock change **/
	private ActionEventHandler eventHandlerLock;
	
	/**
	 * @see com.simpledata.bc.uicomponents.filler.FillerNode
	 * #addLockWeakActionListener(java.awt.event.ActionListener)
	 */
	public void addLockWeakActionListener(ActionListener listener) {
		if (eventHandlerLock == null) 
			eventHandlerLock=new ActionEventHandler();
		eventHandlerLock.addWeakActionListener(listener);
	}
	
	
	
	/** 
	 * Set the monitor for distribution
	 * @param dm set to null to unset 
	 **/
	public void setDistributionMonitor(DistributionMonitor dm) {
		if (dm == null) {
			dm = new DistributionMonitor() {
				public void distributionMonitorStart(int length) {}
				public void distributionMonitorStep() {}
				public void distributionMonitorDone() {}};
		}
		
		distributionMonitor = dm;
	}
	
}
	
	