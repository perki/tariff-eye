/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: FillerViewer.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.DefaultPieDataset;
import org.jfree.data.PieDataset;
import org.jfree.util.Rotation;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.filler.FillerData.UIMonitor;
import com.simpledata.bc.uicomponents.tools.MoneyValueInputSlave;
import com.simpledata.bc.uitools.JListWithPanels;
import com.simpledata.bc.uitools.SButtonIcon;
import com.simpledata.bc.uitools.SNumField;

import foxtrot.Job;
import foxtrot.Worker;


/**
 * This Panel has the capabilities to show informations about the repartion
 * of the assets on a node. 
 */
public class FillerViewer extends JPanel implements ActionListener, UIMonitor {
	
	private static final Logger m_log = Logger.getLogger( FillerViewer.class );
	
	
	//------------------------- ICONS -------------------------//
	
	/** the locked Icon **/
	private static ImageIcon iconLocked = Resources.iconLockOpened;
	
	/** the un locked Icon **/
	private static ImageIcon iconUnLocked = Resources.iconLockClosed;
	
	
	//------------------------- VARS --------------------------//
	
	/** the panel in the center **/
	private JPanel centerPanel;
	
	/** the panel with extra Parameters for repartition **/
	private JPanel repartitionPanel;
	
	/** the button to show / close the reparition panel */
	private SButtonIcon showRepartionButton;
	/** the PLUS icon for showRepartionButton */
	private static final ImageIcon showRepartionIconPlus = Resources.arrowDown;
	/** the MINUS icon for showRepartionButton */
	private static final ImageIcon showRepartionIconMinus = Resources.arrowUp;
	
	/** my content Panel **/
	private JPanel contents;
	
	/** conatins the Amount the Turnover etc.... **/
	private JPanel headPanel;
	
	/** the head Container / use to show or hide the head**/
	private JPanel headContainer;
	
	
	/** the panel to switch with **/
	private JPanel switchPanel;

	/** my data filler **/
	private FillerData fillerData;
	
	/** display the title of  the node I'm working on**/
	private JLabel nodeTitle;
	
	/** display the parent of  the node I'm working on**/
	private JLabel parentNodeTitle;
	
	/** A money Editor for the node **/
	private MoneyValueInputSlave moneyEditor;
	
	/** An editor for the rollout value **/
	private SNumField rolloutEditor;
	
	/** An button to choose if the rollout value should relies on parent **/
	private SButtonIcon rolloutButton;
	
	/** A memory for the money value of the node at work **/
	private Money moneyValue;
	
	/** The node at work **/
	private FillerNode fn;
	
	/** shows the list of childrens I can manipulate **/
	private JListWithPanels childPanel;
	
	/** the progress bar that shows calculus in progress **/
	private ProgressMonitor progressMonitor;
	
	/** The Pie that shows the datas **/
	private ChartPanel pieChart;
	
	/** The data contained by the pieChart 
	 * Necessary since I found no way to return to it from the chart nor the 
	 * chart panel
	 */
	private FillerPieDataSet dataSet;
	
	//-------- Dimensions
	private  final static Dimension DIM_TITLE = new Dimension(200,14);
	private  final static Dimension DIM_PARENT_TITLE = new Dimension(70,20);
	private  final static Dimension DIM_SLIDERS = new Dimension(200,20);
	private  final static Dimension DIM_PERCENT = new Dimension(50,20);
	private  final static Dimension DIM_NONREPART = new Dimension(100,10);
	
	private  final static Dimension DIM_ROLLOUT_EDIT 
	= new Dimension(50,20);
	
	private  final static Dimension DIM_SHOW_BUTTON 
					= new Dimension(16,16);
	
	private final static Dimension DIM_CHART_PANEL = new Dimension(400,200);
	
	/**	 Slider minimum increment **/
	private static final double SLIDER_TICK = 0.5;
	
	/** 
	 * memory to knwow if the reaprtion panel is shown or not */
	boolean showingRepartionPanel = true; // default value is (! this one)
	
	private CompactExplorer compactExplorer;
	
	/** 
	 * contains the link action on the label that display the parent's node
	 * name
	 */
	LinkFollower upInTreeLink ;
	
	/** 
	 * construct a FillerViewer
	 * @param dataSource the Data I must trust in ;)
	 * @param switchWith the Panel I should switch with, can be null
	 * **/
	public FillerViewer(CompactExplorer comExplorer,JPanel switchWith) {
		assert comExplorer.getFillerData() != null : 
		    	"What could I do of a null fillerData?";
		
		compactExplorer = comExplorer;
		
		switchPanel = switchWith;
		
		// init datas
		fillerData = comExplorer.getFillerData();
		fillerData.setUIMonitor(this);
		fn = null;
		moneyValue = new Money(0d);
		
		setLayout(new BorderLayout());
		
		
		actualPanelIsRepartition = true;
		
		// init components
		parentNodeTitle = new JLabel();
		//parentNodeTitle.setMaximumSize(DIM_PARENT_TITLE);
		_setDim(parentNodeTitle,DIM_PARENT_TITLE);
		upInTreeLink = 
		    new LinkFollower(parentNodeTitle,compactExplorer.getSTree());
		
		nodeTitle = new JLabel();
		childPanel = new JListWithPanels();
		childPanel.setSelectionVisible(false);
		repartitionPanel = new JPanel();
		repartitionPanel.setLayout(
				new BoxLayout(repartitionPanel,BoxLayout.Y_AXIS));
		
		moneyEditor = 
			new MoneyValueInputSlave(moneyValue) {
			public void stopEdit() {
				moneyValueChanged();
			}

			public void startEdit() {}
		};
		
		
		rolloutEditor = new SNumField() {
			public void stopEditing() {
				rolloutValueChanged();
			}
			public void startEditing() {}
		};
		rolloutEditor.setTruncateAfterMax(true);
		rolloutEditor.setDigitAfterComa(0);
		rolloutEditor.setDouble(0d);
		
		
		
		rolloutButton = new SButtonIcon(Resources.iconLink);
		rolloutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rolloutButtonPressed();
			}
		});
		
		
		
		progressMonitor = new ProgressMonitor();
		
		// pie Chart
		setPieChart(null);
		
		
		// button to open close the repartition panel
		showRepartionButton = new SButtonIcon(showRepartionIconMinus);
		showRepartionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRepartitionPanel(! showingRepartionPanel);
			}
		});
		showRepartitionPanel(! showingRepartionPanel); // reset
		
		// Layout contents 
		
		// HEADER
		headPanel = new JPanel(new BorderLayout());
		JPanel headNorth = new JPanel(new BorderLayout());
		JPanel headNorthCenter = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel headNorthSouth = new JPanel(new FlowLayout(FlowLayout.LEADING));
		nodeTitle.setSize(DIM_TITLE);
		nodeTitle.setMinimumSize(DIM_TITLE);
		
		headNorthCenter.add(parentNodeTitle);
		headNorthCenter.add(nodeTitle);
		headNorthCenter.add(moneyEditor);
		
		
		JLabel rolloutText = new JLabel();
		BC.langManager.register(rolloutText,"Rollout:");
		rolloutEditor.setPreferredSize(DIM_ROLLOUT_EDIT);
		rolloutEditor.setHorizontalAlignment(SwingConstants.RIGHT);
		JLabel dummy = new JLabel("");
		//parentNodeTitle.setMaximumSize(DIM_PARENT_TITLE);
		showRepartionButton.setPreferredSize(DIM_SHOW_BUTTON);
		headNorthSouth.add(showRepartionButton);
		Dimension d = new Dimension(
		        DIM_PARENT_TITLE.width - DIM_SHOW_BUTTON.width,
		        DIM_PARENT_TITLE.height - DIM_SHOW_BUTTON.height);
		_setDim(dummy,d);
		headNorthSouth.add(dummy);
		headNorthSouth.add(rolloutText);
		headNorthSouth.add(rolloutButton);
		headNorthSouth.add(rolloutEditor);
		headNorthSouth.add(new JLabel("%"));
		headNorthSouth.add(new JLabel("<HTML><I>     "+
		        Lang.translate("the turnover is not re-calculated " +
		        		"on transactions change")
		        +"</I></HTML>"
		));
		
		
		
		
		headNorth.add(headNorthCenter,BorderLayout.CENTER);
		headNorth.add(headNorthSouth,BorderLayout.SOUTH);
		
		progressMonitor.setPreferredSize(new Dimension(200,10));
		
		headPanel.add(headNorth,BorderLayout.NORTH);
		headPanel.add(progressMonitor,BorderLayout.CENTER);
		headPanel.add(repartitionPanel,BorderLayout.SOUTH);
		
		
		
		// Contents
		
		contents = new JPanel(new BorderLayout());
		
		contents.add(childPanel,BorderLayout.CENTER);
		
		pieChart.setPreferredSize(DIM_CHART_PANEL);
		
		//Setting the pieChart to non opaque to avoid ugly gray background
		pieChart.setOpaque(false);
		//pieChart.setBackground(Color.RED);

		
		JPanel pieContainer = new JPanel();
		pieContainer.setMinimumSize(DIM_CHART_PANEL);
		
		JPanel pieContainer2 = new JPanel(new BorderLayout());
		pieContainer2.setMinimumSize(DIM_CHART_PANEL);
		pieContainer2.setMaximumSize(DIM_CHART_PANEL);
		pieContainer2.setPreferredSize(DIM_CHART_PANEL);
		
		pieContainer2.add(pieChart, BorderLayout.CENTER);
		
		pieContainer.add(pieContainer2);
		
		
		contents.add(pieContainer, BorderLayout.SOUTH);
		
		centerPanel = new JPanel(new BorderLayout());
		
		centerPanel.add(contents,BorderLayout.CENTER);
		
		headContainer = new JPanel(new BorderLayout());
		add(headContainer,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		refresh();
	}
	
	final String showStr = Lang.translate("Show extra parameters");
	final String hideStr = Lang.translate("Hide extra parameters");
	
	/**
	 * Show / hide the repartition panel<BR>
	 */
	public void showRepartitionPanel(boolean show) {
		if (showingRepartionPanel == show) return;
		showingRepartionPanel = show;
		showRepartionButton.setToolTipText(
				showingRepartionPanel ? hideStr : showStr);
		showRepartionButton.setIcon(
				showingRepartionPanel ? 
						showRepartionIconMinus : showRepartionIconPlus);
		refreshRepartionPanel();
		
	}
	
	
	
	private boolean actualPanelIsRepartition;
	/**
	 * change the JPanel in the contents<BR>
	 * if (b == true) show the repartion view else show the tarif data
	 */
	public void showContents(boolean b) {
		if (actualPanelIsRepartition == b) return;
		actualPanelIsRepartition = b;
		if (switchPanel == null) return;
		centerPanel.removeAll();
		centerPanel.add(b ? contents : switchPanel);
		centerPanel.revalidate();
		centerPanel.repaint();
	}
	
	
	/**
	 * the money value changed take this in account
	 */
	private void moneyValueChanged() {
		if (fn != null) {
			fn.setMoneyAmount((Money) moneyValue.copy());
			refresh();
		} 
	}
	
	/** the rollout value changed **/
	private void rolloutValueChanged() {
		if (fn != null) {
			fn.rolloutSet(rolloutEditor.getDouble().doubleValue()/100);
		}
	}
	
	/** the rollout button has been pressed **/
	private void rolloutButtonPressed() {
		if (fn == null) return;
		if (fn.rolloutReliesOnParent()) {
			fn.rolloutSet(fn.rolloutGetApplicable());
			return;
		}
		fn.rolloutSet(-1);
	}
	
	/**
	 * advertised of a Compact Node selection
	 */
	public void showCompactNodeInfo(CompactNode cn) {
	    FillerNode fnt = fillerData.getFillerNode(cn);
		
		showContents((! cn.isLeaf()) && (fnt != null));
		
		
		
		if (fnt == fn) return;
		fn = fnt;
		
		// reset Data
		childPanel.removeAll();
		
		//FillerPieDataSet fpds = new FillerPieDataSet();
		dataSet = new FillerPieDataSet();
		
		
		
		// refresh ui
		refresh();
		
		if (fn != null) {
			
			fn.setDistributionMonitor(progressMonitor);
			
			// add childrens to my list
			FillerNode[] childs = fn.getChildren();
			ChildPanel cp ;
			for (int i = 0; i < childs.length; i++) {
				cp = new ChildPanel(childs[i]);
				cp.sliderIsEnabled(! childs[i].getLockState());
				
				childPanel.addPanel(cp);
				
				// fillPie
				//fpds.add(childs[i]);
				dataSet.add(childs[i]);
			}
			
			// add listeners
			fn.addWeakActionListener(this);
		}
		
		// chart
		String chartTitle = fn == null ? "" : fn.getTitle();
		JFreeChart chart = cn == null ? null : 
			ChartFactory.createPieChart3D(chartTitle,dataSet,true,false,false);
		setChartProperties(chart);
		setPieChart(chart);
	}
	
	/**
	 * Parametrize a JFreeChart (must be a PieChart3D) 
	 * Change transparency, start angle, etc...
	 * @param jfc the chart that has to be tuned
	 */
	private void setChartProperties(JFreeChart jfc) {
	    	    
	    jfc.setBackgroundPaint(null);
	    //Legend leg = jfc.getLegend();
	    
		final PiePlot3D plot = (PiePlot3D)jfc.getPlot();
		plot.setStartAngle(200);
		plot.setDirection(Rotation.CLOCKWISE);
		plot.setForegroundAlpha(0.65f);
		plot.setBackgroundPaint(Color.WHITE);
		plot.setBackgroundAlpha(0.3f);
		plot.setNoDataMessage(Lang.translate("No data to display"));
		plot.setDepthFactor(0.15);
		plot.setLabelGenerator( new FillerPieDataSet.LabelGenerator() );

		//plot.setInteriorGap(0.3);
		//plot.setDataAreaRatio(20);
		//plot.setCircular(false);
	}
	
	/**
	 * This method is called on panel Change to change the pieCHart
	 * @param jc
	 */
	private void setPieChart(JFreeChart jc) {
		if (jc == null) {
			jc = ChartFactory.createPieChart3D(
					"",new FillerPieDataSet(),true,false,false);
			setChartProperties(jc);
		}

		if (pieChart == null) {
			pieChart = new ChartPanel(jc); 
		} else {
			pieChart.setChart(jc);
		}
	}

	
	private boolean lastWasNull = true;
	/** update all the data **/
	public void refresh() {
		if (fn == null) {
		    
		    showContents(false);
			nodeTitle.setText("");
			parentNodeTitle.setText("");
			moneyEditor.setEditable(false);
			moneyValue.setValue(0d);
			moneyEditor.refresh();
			rolloutEditor.setDouble(0d);
			rolloutEditor.setEditable(false);
			rolloutButton.setIcon(Resources.iconLink);
			rolloutButton.setEnabled(false);
			
			if (! lastWasNull) {
			    headContainer.removeAll();
			    headContainer.revalidate();
			    headContainer.repaint();
			}
			
		} else {
		    
		    if (lastWasNull) {
			    headContainer.removeAll();
			    headContainer.add(headPanel,BorderLayout.CENTER);
			    headContainer.revalidate();
			    headContainer.repaint();
			}
		    
			
		    rolloutButton.setEnabled(true);
			
			if (fn.rolloutReliesOnParent()) {
			    rolloutButton.setIcon(Resources.iconLink);
				rolloutButton.setToolTipText(
						Lang.translate("Specify the rollout value")
				);
				rolloutEditor.setToolTipText(
						Lang.translate("The rollout value depend on parent's")
						);
			} else {
			    rolloutButton.setIcon(Resources.iconUnlink);
				rolloutButton.setToolTipText(
					Lang.translate("Make the rollout value depend on parent")
				);
				rolloutEditor.setToolTipText("");
			}
			
			
			
		    if (fn.getParent() == null) {
		        rolloutButton.setEnabled(false);
		        rolloutButton.setIcon(Resources.iconLink);
				rolloutButton.setToolTipText("");
				rolloutEditor.setToolTipText("");
				parentNodeTitle.setText("");
				upInTreeLink.refresh(null);
		    } else {
		        String t = fn.getParent().getTitle();
		        if (t.equals("")) t = "..";
		        parentNodeTitle.setText(t);
		        if (fn.getParent() instanceof NodeInfo) {
		            upInTreeLink.refresh(
		                    ((NodeInfo) fn.getParent()).getCompatcNode());
		        }
		        
		    }
			
			rolloutEditor.setEditable(! fn.rolloutReliesOnParent());
			moneyEditor.setEditable(true);
			
			// update my Money Value
			moneyValue.setValue(fn.getAmount());
			moneyEditor.refresh();
			nodeTitle.setText("-"+fn.getTitle());
			rolloutEditor.setDouble(fn.rolloutGetApplicable()*100);
		
		}
		// forward to repartion panel
		refreshRepartionPanel();
		lastWasNull = (fn == null);
	}
	
	
	
	/** keeps in memory the distribution to apply **/
	DistributionMethod dmA;DistributionMethod dmT;
	/** refresh the repartition panel **/
	private void refreshRepartionPanel() {
		showRepartionButton.setEnabled(fn != null);
		if (fn == null || ! showingRepartionPanel) {
			repartitionPanel.removeAll();
			repartitionPanel.revalidate();
			dmA = null; dmT = null;
			return;
		}
		
		
		DistributionMethod dmA2 = fn.getDistributionMethod(AssetsRoot0.class);
		DistributionMethod dmT2 = fn.getDistributionMethod(
				TransactionsRoot0.class);
		dmA2.refreshUI();
		dmT2.refreshUI();
		if (dmA2 == dmA && dmT2 == dmT) return;
		
		repartitionPanel.removeAll();
		dmA = dmA2; dmT = dmT2;
		
		
		repartitionPanel.add(
				DistributionManager.getComboBox(AssetsRoot0.class,fn));
		repartitionPanel.add(dmA.getUI());
		repartitionPanel.add(
				DistributionManager.getComboBox(TransactionsRoot0.class,fn));
		repartitionPanel.add(dmT.getUI());
		
		repartitionPanel.revalidate();
	}
	
	
	/**
	 * Called when a modification happens on the FillerNode
	 * @see java.awt.event.ActionListener
	 * #actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
	    m_log.debug("--> *** Action catched");
		refresh();
	}
	
	/** 
	 * util
	 * */
	private void _setDim(JComponent c,Dimension d) {
		c.setSize(d);
		c.setPreferredSize(d);
		c.setMinimumSize(d);
	}
	
	
	////////////////////// INNER CLASSES /////////////////////////
	
	/**
	 * Components that goes into the Childrens list Panel
	 */
	class ChildPanel extends JPanel {
		private MoneyValueInputSlave me;
		private JSlider js ;
		private JLabel percent;
		private JLabel nonReparti;
		private Money mv;
		private FillerNode childFN;
		private SButtonIcon lockMe;
		
		/** listener for value changes **/
		private ActionListener alValue;
		
		/** listener for preview changes **/
		private ActionListener alPreview;
		
		/** listener for lock changes **/
		private ActionListener alLock;
		
		/**
		 * prevent loops in percentage Change<BR>
		 * When we change the value of the JSilderBar it throw an event
		 * and somethimes we want to call setValue from the event handler
		 * */
		private boolean lockPercentageChange;
		
		/** knows if the last percentahe Change was a drag event **/
		private boolean lastPercentageChangeWasADrag;
		
	
		
		/**
		 * 
		 * @param childFN the FillerNode to monitor
		 */
		public ChildPanel(final FillerNode childFN) {
			this.childFN = childFN;
			
		
			
			
			
			lockPercentageChange = false;
			lastPercentageChangeWasADrag = false;
			
			
		
			
			percent = new JLabel("",JLabel.RIGHT);
			_setDim(percent,DIM_PERCENT);
			
			lockMe = new SButtonIcon();
			lockMe.setSize(new Dimension(16,16));
			
			
			// Slider
			
			js = new JSlider(0,(int) (100/SLIDER_TICK));
			
			js.addChangeListener(new ChangeListener(){
			    
			    //private int count = 0;
			    
				public void stateChanged(ChangeEvent arg0) {
					percentageChange(js.getValue()/(100/SLIDER_TICK));
				}});

			
			// Money
			mv = childFN.getAmount();
			me = new MoneyValueInputSlave(mv) {
				public void stopEdit() {
					childFN.setMoneyAmount(mv);
				}
				public void startEdit() {} // do not care
			};
			
			
			
			
			// LockMeButton
			lockMe.addActionListener(new ActionListener() {
			    
			    //private int count = 0;
			    
				public void actionPerformed(ActionEvent arg0) {
					childFN.lock(! childFN.getLockState());
					//refreshExploded();
				}});
			
			
			// layout
			//_setDim(title,DIM_TITLE);
			_setDim(js,DIM_SLIDERS);
			
		

			setLayout(new FlowLayout(FlowLayout.LEADING,0,0));
			
			
			
			add(lockMe);
			
			
			// Label / Slider Panel
			JPanel namedSlider = new JPanel(new BorderLayout(0,0));
			JLabel title = new JLabel(childFN.getTitle());
			_setDim(title,DIM_TITLE);
			title.setFont(title.getFont().deriveFont(
			        12f
			));
			
			// attach a link follower 
			if (childFN instanceof NodeInfo) { 
			    new LinkFollower(title,compactExplorer.getSTree(),
			            ((NodeInfo) childFN).getCompatcNode());
			}
			
			
			namedSlider.add(title,BorderLayout.NORTH);
			namedSlider.add(js,BorderLayout.CENTER);
			
			add(namedSlider);
			
			
			//panel with MoneyEditor and not repartit amount
			JPanel merep = new JPanel(new BorderLayout(0,0));
			
			nonReparti = new JLabel(" ");
			nonReparti.setFont(nonReparti.getFont().deriveFont(10f));
			_setDim(nonReparti,DIM_NONREPART);
			
			
			merep.add(me,BorderLayout.CENTER);
			merep.add(nonReparti,BorderLayout.SOUTH);
			
			//bottomPanel.add(js);
			add(percent);
			add(merep);
			
			//add(topPanel);
			//add(bottomPanel);
			
			// update the data
			refresh();
			
			// ----------- listners -----------------//
			
			//-- value change
			alValue = new ActionListener() {
			    
			    //private int count = 0;
			    
				public void actionPerformed(ActionEvent e) {
					refresh();
				}
			};
			childFN.addWeakActionListener(alValue);
			
			//-- previewPercentage change
			alPreview = new ActionListener() {
			    
			    //private int count = 0;
			    
				public void actionPerformed(ActionEvent e) {
				    if (fn == null || mv == null) return;
				    
					double newValue = childFN.getPreviewPercentage();
					
					// JSlider
					if (! lockPercentageChange) {
						lockPercentageChange = true;
						setJSValue(newValue);
						lockPercentageChange = false;
					}
					
					
					//	change the text of my money value
					mv.setValue(fn.getAmount()); 
					mv.operationFactor(newValue);
					me.refresh();
					
					//	change the text of my percentage
					percentText(newValue); 
					
				}
			};
			childFN.addPreviewWeakActionListener(alPreview);
			
			//-- locker state change
			alLock = new ActionListener() {
			    
			    //private int count = 0;
			    
				public void actionPerformed(ActionEvent e) {
					refresh();
				}
			};
			childFN.addLockWeakActionListener(alLock);
			
		}
		

		/**
		 * change the editing property of the slider
		 */
		public void sliderIsEnabled(boolean b) {
			js.setEnabled(b);
		}
		
		
		
		/**
		 * percentage changes ... called when sliddebar moves
		 * @param v is a value between 0 and 1
		 */
		private void percentageChange(double v) {
			js.setExtent(
			(int) ((1-childFN.getMaximumPreviewPercentage())*100/SLIDER_TICK));
			if (lockPercentageChange) return; // already in percentageChange
			
			if (childFN.getLockState()) return; //do not care
			
			
			
			
			lockPercentageChange = true; // lock percentageChange
			
			
			// only If'm being adjusting
			if (js.getValueIsAdjusting()) {
				childFN.setPreviewPercentage(v);
			}
			
			// if this is the slider has been released commit the changes
			if ((! js.getValueIsAdjusting()) 
					&& lastPercentageChangeWasADrag) {
				childFN.commit();
				refresh();
			}
			
			
			// realease Percentage Change
			lockPercentageChange = false;
			
			// remember the last event call
			lastPercentageChangeWasADrag = js.getValueIsAdjusting();
			
			
		}
		
		/** 
		 * set the JSlider value
		 * @param d is a value between 0 and 1
		 */
		private void setJSValue(final double d) {
			js.setValue((int) (d*100/SLIDER_TICK));
		}
		
		/** called when values need to be refreshed */
		private void refresh() {
			lockMe.setIcon(childFN.getLockState() ?  iconUnLocked : iconLocked);
			lockMe.setEnabled(childFN.canBeLocked(! childFN.getLockState()));
			
			sliderIsEnabled(! childFN.getLockState());
			setJSValue(childFN.getPercentage());
			percentText(childFN.getPercentage());
			mv.setValue(childFN.getAmount());
			me.refresh();
			
	
			// set the color depending 
			double sum = childFN.getPercentOfNotDistributedToTr();
			if (sum > 0.001) {
			    nonReparti.setIcon(Resources.stdTagWarning);
			    nonReparti.setText(SNumField.formatNumber(100*sum,1,true)+"%");
			    nonReparti.setToolTipText("<HTML>"+
			            Lang.translate("The percentage is the proportion of<BR>" 
			                    +
			            		"assets at this positions that do not<BR>" +
			            		"participate in transaction distribution.")
			            		+"</HTML>");
			} else {
			    nonReparti.setIcon(null);
			    nonReparti.setText(" ");
			    nonReparti.setToolTipText("");
			}
			
					
		}
		
		/** update percentage text **/
		private void percentText(double d) {
			percent.setText(
					SNumField.formatNumber(d*100,1,true)
					+"%");
		}
		
	}


    /**
     * @see com.simpledata.bc.uicomponents.filler.FillerData.UIMonitor#fillerDataReseted()
     */
    public void fillerDataReseted() {
       fn = null;
       refresh();
    }
}


/**
 * A container for all Info needed by the PieChart
 */
class FillerPieDataSet extends DefaultPieDataset {
	private static final Logger m_log=Logger.getLogger(FillerPieDataSet.class);
	
	ArrayList childs;
	
	public FillerPieDataSet() {
		childs = new ArrayList();
	}
	
	public void add(FillerNode fno) {
		FillerNodePieChart pcn 
		= new FillerNodePieChart(this,fno,childs.size());
		childs.add(pcn);
	}
	
	public FillerNodePieChart getChildAt(int index) {
	    FillerNodePieChart res = null;
	    if ((0 <= index) && (index < childs.size())) {
	        res = (FillerNodePieChart)childs.get(index);
	    }
	    return res;
	}
	
	
	
	
	// Inner classes -------------------------------------------------
	
	// container for FillerNode into the PieChar
	static class FillerNodePieChart implements Comparable , ActionListener {
		FillerNode originalNode;
		FillerPieDataSet owner;
		int index ;
		
		public FillerNodePieChart( 
			FillerPieDataSet fpds, 
			FillerNode fno, 
			int index 
		) {
			owner = fpds;
			originalNode = fno;
			this.index = index;
			
			// add a listener;
			originalNode.addWeakActionListener(this);
			originalNode.addPreviewWeakActionListener(this);
			
			// tell the FillerNode to display me
			actionPerformed(null);
		}
		
		public int compareTo(Object o) {
			if (! (o instanceof FillerNodePieChart)) {
				int i = ((FillerNodePieChart) o).index;
				if (i == index) return 0;
				return (i > index) ? -1 : 1;
			}
			return 1;
		}
		
		public String toString() {
			return originalNode.getTitle();
		}
		
		/**
		 * @see java.awt.event.ActionListener
		 * #actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int v = (int) (originalNode.getPreviewPercentage() * 1000);
			owner.setValue(this, v  / 10d);
		}
		
		/**
		 * Returns true if the slide bar representing the fillerNode
		 * is actually locked
		 * @return
		 */
		public boolean isLocked() {
		    return this.originalNode.getLockState();
		}
		
	
		/**
		 * Return the title that should be displayed inside the chart. 
		 * 
		 * @return Title for display in chart. 
		 */
		public String getChartTitle() {
			double percent = originalNode.getPreviewPercentage();
			String title = originalNode.getTitle();
			
			return 
				title 
				+ " " 
			  + SNumField.formatNumber(percent*100,1,true)
				+"%";
		}
	}
	
	
	/**
	 * Label generator class that handles generation of labels
	 * in pie chart. 
	 */
	static class LabelGenerator implements PieSectionLabelGenerator {
		// PieSectionLabelGenerator ----------------------------------------------
	
		/**
		 * Generates a label for a pie section.
		 * 
		 * @param dataset  the dataset (<code>null</code> not permitted). Dataset
		 *                 will be equal to the this pointer, since this is the dataset. 
		 * @param key  the section key (<code>null</code> not permitted). Section key is 
		 *             aequivalent with instances of the local class FillerNodePieChart down
		 *             below. 
		 * 
		 * @return the label (possibly <code>null</code>).
		 */
		public String generateSectionLabel(final PieDataset dataset, final Comparable key) {
			assert key instanceof FillerNodePieChart : 
				"key must be instance of FillerNodePieChart"; 
			
			if ( key instanceof FillerNodePieChart ) {
				FillerNodePieChart s = (FillerNodePieChart) key; 
				return s.getChartTitle();
			}
			
			return "??"; 
			
		}
	}  // LabelGenerator class
}

/**
 * A JProgressBar that monitor the progress of redistribution
 *
 */
class ProgressMonitor extends JProgressBar 
	implements DistributionMonitor {
		
	private static final Logger m_log = Logger.getLogger(ProgressMonitor.class);
	
	public ProgressMonitor() {
		super(0,100);
	
	}
	
	int step;
	int length;
	public void distributionMonitorStart(int length) {
		this.length = length;
		setMaximum(length);
		setValue(0);
		step = 0;
		
	}

	public void distributionMonitorStep() {
		Worker.post( new Job() {
			public Object run() {
				step++;
				setValue(step);
				
				return null;
			}
		} );
	}

	public void distributionMonitorDone() {
		setValue(length);
	}
	
	
	

	
}

/*
* $Log: FillerViewer.java,v $
* Revision 1.2  2007/04/02 17:04:27  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:42  perki
* First commit on sourceforge
*
* Revision 1.50  2004/11/23 10:37:28  perki
* *** empty log message ***
*
* Revision 1.49  2004/11/17 16:37:56  perki
* *** empty log message ***
*
* Revision 1.48  2004/11/17 16:19:37  perki
* FillerHeader nows hide by himself
*
* Revision 1.47  2004/11/16 17:22:11  perki
* Merging now remembers of last picks
*
* Revision 1.46  2004/11/11 12:38:02  perki
* New distribution system for the Filler
*
* Revision 1.45  2004/11/08 18:16:18  perki
* *** empty log message ***
*
* Revision 1.44  2004/10/15 06:38:59  perki
* Lot of cleaning in code (comments and todos
*
* Revision 1.43  2004/10/14 16:39:08  perki
* *** empty log message ***
*
* Revision 1.42  2004/10/12 17:15:21  perki
* Filler now display which nodes do not participate in transaction distribution
*
* Revision 1.41  2004/10/12 12:32:26  perki
* *** empty log message ***
*
* Revision 1.40  2004/10/12 10:21:36  perki
* detecting when repartition is not total in transactions
*
* Revision 1.39  2004/10/11 11:55:06  perki
* Ok done
*
* Revision 1.38  2004/10/11 10:19:16  perki
* Percentage on Transactions
*
* Revision 1.37  2004/10/11 07:49:00  perki
* Links in Filler
*
* Revision 1.36  2004/10/08 14:56:04  perki
* Better Matching / merging logic
*
* Revision 1.35  2004/09/22 15:39:55  carlito
* Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
*
* Revision 1.34  2004/09/22 06:47:05  perki
* A la recherche du bug de Currency
*
* Revision 1.33  2004/09/21 09:11:55  perki
* small changes
*
* Revision 1.32  2004/09/16 17:26:38  perki
* *** empty log message ***
*
* Revision 1.31  2004/09/14 13:06:32  perki
* *** empty log message ***
*
* Revision 1.30  2004/09/09 12:43:08  perki
* Cleaning
*
* Revision 1.29  2004/09/04 18:12:31  kaspar
* ! Log.out -> log4j
*   Only the proper logger init is missing now.
*
* Revision 1.28  2004/09/03 11:09:30  perki
* *** empty log message ***
*
* Revision 1.27  2004/09/02 15:51:46  perki
* Lot of change in calculus method
*
* Revision 1.26  2004/09/02 13:26:54  kaspar
* ! Hacking the Progressbar together. This is not a permanent fix.
*
* Revision 1.25  2004/08/27 10:02:09  kaspar
* ! Refactor: Put DistributionMonitor in its own file
*
* Revision 1.24  2004/08/02 14:55:43  perki
* *** empty log message ***
*
* Revision 1.23  2004/08/02 10:44:51  carlito
* *** empty log message ***
*
* Revision 1.22  2004/08/02 10:41:13  perki
* *** empty log message ***
*
* Revision 1.21  2004/08/02 10:08:43  perki
* introducing distribution for transactions
*
* Revision 1.20  2004/08/02 09:40:43  kaspar
* ! Display of pie pieces is now done with the same percentage
*   rounding that is done up in the panel
*
* Revision 1.19  2004/08/02 08:32:36  perki
* *** empty log message ***
*
* Revision 1.18  2004/08/01 18:00:59  perki
* *** empty log message ***
*
* Revision 1.17  2004/08/01 12:23:08  perki
* Better show/hide extra parameter
*
* Revision 1.16  2004/07/31 12:01:00  perki
* Still have problems with the progressbar
*
* Revision 1.15  2004/07/31 11:06:55  perki
* Still have problems with the progressbar
*
* Revision 1.14  2004/07/30 15:59:36  carlito
* *** empty log message ***
*
* Revision 1.13  2004/07/30 15:41:57  carlito
* *** empty log message ***
*
* Revision 1.12  2004/07/30 15:38:19  perki
* some changes
*
* Revision 1.11  2004/07/30 13:33:42  carlito
* *** empty log message ***
*
* Revision 1.10  2004/07/30 11:47:52  carlito
* *** empty log message ***
*
* Revision 1.9  2004/07/30 05:50:01  perki
* Moved all CompactTree classes from uicompnents to uicomponents.compact
*
* Revision 1.8  2004/07/29 18:29:24  carlito
* *** empty log message ***
*
* Revision 1.7  2004/07/29 11:38:13  perki
* Sliders should be ok now
*
* Revision 1.6  2004/07/29 10:42:20  perki
* Sliders should be ok now
*
* Revision 1.5  2004/07/27 17:56:29  carlito
* *** empty log message ***
*
* Revision 1.4  2004/07/27 17:54:05  perki
* *** empty log message ***
*
* Revision 1.3  2004/07/27 16:56:31  perki
* *** empty log message ***
*
* Revision 1.2  2004/07/27 15:14:06  carlito
* *** empty log message ***
*
* Revision 1.1  2004/07/26 17:39:37  perki
* Filler is now home
*
*/