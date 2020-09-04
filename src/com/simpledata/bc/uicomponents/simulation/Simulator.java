/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 24 fevr. 2004
 * $Id: Simulator.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.simulation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.Resources;
import com.simpledata.bc.actions.ActionClearSimulation;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.datatools.AutosaveTask;
import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.bc.merging.MergingMenu;
import com.simpledata.bc.reports.ReportToolbox;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.TreeOrderer;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.compact.CompactTreeItem;
import com.simpledata.bc.uicomponents.filler.FillerViewer;
import com.simpledata.bc.uitools.InternalFrameDescriptor;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.bc.uitools.SButtonIcon;
import com.simpledata.bc.uitools.streetable.STreeTable;

/**
 * A GUI to simulate over a simulation tarification made of 1 or n standard tarifications
 */
public class Simulator 
	extends JInternalFrame 
	implements NamedEventListener,  BC.TarificationModifiers {
	
    private static final Logger m_log = Logger.getLogger( Simulator.class );

    /** size static gimmies */
    private final static int MIN_W = 500;
    private final static int MIN_H = 300;
    private final static int PREF_W = 900;
    private final static int PREF_H = 600;
    
    /** Locale */
    private static final String SAVE_CHANGES_MESSAGE = "Simulator:saveChangesMessage";
    private static final String LAUNCH_ERROR_MESSAGE = "Simulator:launchErrorMessage";
    private static final String FRAME_TITLE = "Simulator:frameTitle";
    private static final String ERROR_FRAME_TITLE = "Simulator:errorFrameTitle";
    
    /** Autosave */
    //private static final int AUTOSAVE_PERIOD = 60000; // 1min
    
	// Std Graphical objects declaration 
	private SButtonIcon collapseTreeButton;
	private JPanel controlButtonsPanel;
	private JLabel controlButtonsPanelEmptyLabel1;
	private SButtonIcon expandTreeButton;
	private SButtonIcon launchSimulationButton;
	private JMenu mFile;
	private JMenuItem mFileQuit;
	private JMenuItem mFileSave;
	private JMenuItem mFileSaveAs;
	private JMenu mReports;
	private JMenuItem mReportsFeeReportSummary;
	private JMenuItem mReportsFeeDetailedReport;
	private JMenuItem mReportsTariffingReport;
	private JMenuItem mOptionTarifications;
	private JMenuItem mOptionOrderTrees;
	private JMenu mOptions;
	private JMenuBar menuBar;
	private JPanel treeControlButtonPanel;
	private SButtonIcon viewTarificationButton;
	
	private JSplitPane hSplit;
	// End of Std Graphical objects declaration
	
	 /** The Filler Viewer Controler Panel .. it's shown when needed **/
    protected FillerViewer fillerViewer;
	
	/**
	 * Static WeakHashMap that does contains actually opened Tarifications (simulations)
	 * Tarification serves as keys, and Simulator as values.
	 */
	private static WeakHashMap openedSimulation;
	
	/** Auto save task scheduled every AUTOSAVE_PERIOD milliseconds */ 
	private final AutosaveTask m_autosaveTask;
	
	/** counts the number of opened simulations */
	private static Integer simulationCount;
	
	/** This will contain the number which has been attributed to this sim */
	private int simulationCountTag = -1;
	
	private Tarification simulationTarification;
	
	private CompactExplorer simExplorer;
	private TarifViewer tarifViewer;
	
	private STreeTable treeTable;
	
	// Orderer for trees : JInternalFrame shown at will
	private TreeOrderer treeOrderer;
	
	private CurrencyChangeListener currencyChangeListener;
	
	// Modifications
	boolean m_needSave;
	
	/**
	 * Constructor
	 * DO NOT CALL DIRECTLY --- CALL openSimulation 
	 * @param t the tarification that has to be simulated (must not be null)
	 */
	protected Simulator(Tarification t) {
		super("Simulation tool", true, true, true, true);
		this.simulationTarification = t;
		this.simulationCountTag = getNextSimulationCount();
		
		if (this.simulationTarification != null) {
		    
		    buildUI();
		    
			// TODO Refine event management
			this.simulationTarification.addNamedEventListener(this,
					NamedEvent.ALL_EVENTS,null);
			
			
		} else {
		    m_log.error( "Tried to open a simulation from a null " +
			"simulation tarification" );
		    buildErrorUI();
		    
		}

		m_needSave = false;
		// autosave
        boolean doAutosave = ((Boolean)BC.getParameter(Params.KEY_AUTOSAVE_ENABLE,
                Boolean.class)).booleanValue();
        m_autosaveTask = new AutosaveTask(t, AutosaveTask.SIMULATOR_ENVT);
        if (doAutosave) {
        	int period = ((Integer)BC.getParameter(Params.KEY_AUTOSAVE_PERIOD,
        	        Integer.class)).intValue();
        	BC.bc.m_timer.schedule(m_autosaveTask, new Date(), period * 60000);     
        }
	}
	
	/**
	 * This is the method that should be called to create a new instance of
	 * simulator and show it on screen
	 * @param t Tarification to simulate
	 * @return a new Simulator
	 */
	public static Simulator openSimulation(Tarification t) {
		if (openedSimulation == null) openedSimulation = new WeakHashMap();
		
		Simulator sim = null;
		
		if (openedSimulation.containsKey(t)) {
			sim = (Simulator) openedSimulation.get(t);
		}
		
		
		if (sim == null) {
			sim = new Simulator(t);
			if (sim.getTarification() != null) {
				openedSimulation.put(t,sim);
			} else {
				sim = null;
				return null;
			}
		}
			
		sim.updateSimulationTitle();
		
		InternalFrameDescriptor ifd = new InternalFrameDescriptor(
		        Params.KEY_SIMULATOR_BOUNDS);
		ifd.setInitialBounds(new Rectangle(
		        10,10,
		        sim.getWidth(), sim.getHeight()
		));
		BC.bc.popupJIFrame(sim, ifd);
		
		sim.m_needSave = false; // avoid startupChanges
		return sim;
	}
	
	/**
	 * Get the next simulation identifier...
	 * @return static simulationCount
	 */
	private static int getNextSimulationCount() {
	    if (simulationCount == null) {
	        simulationCount = new Integer(1);
	    } else {
	        int nextCount = simulationCount.intValue()+1;
	        simulationCount = new Integer(nextCount);
	    }
	    return simulationCount.intValue();
	}
	
	
	public Tarification getTarification() {
		return this.simulationTarification;
	}
	
	protected STreeTable treeTable() {
	    return this.treeTable;
	}
	protected CompactExplorer simExplorer() {
	    return simExplorer;
	}
	

	private void buildUI() {
		hSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		hSplit.setResizeWeight(0.5);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//setName(Lang.translate("Simulation tool"));
		setFrameIcon(Resources.iconSimulation);
		
		getContentPane().setLayout(new BorderLayout());
		
		setMinimumSize(new Dimension(MIN_W, MIN_H));
		setPreferredSize(new Dimension(PREF_W, PREF_H));
		
		// TODO reactivate this if you want a toolBar
		//getContentPane().add(buildToolBar(), BorderLayout.NORTH);

		getContentPane().add(hSplit, BorderLayout.CENTER);

		buildMenuBar();

		buildTreeControlPanel();
		
		buildComplexComponents();
		
		hSplit.setLeftComponent(this.treeTable);
		hSplit.setRightComponent(this.fillerViewer);
		
		pack();
		
		// Differ resize on show
		Timer tim = new Timer(50, new ResizeLauncher(this)); 
		tim.start();
		
//		 hide the second column of the table
		treeTable.removeColumn(1);
	}
	
	/**
	 * Action Listener meant to wait for the simulator to show
	 * before launching resizes....
	 */
	class ResizeLauncher implements ActionListener {
	    private Simulator owner;
	    
	    public ResizeLauncher(Simulator sim) {
	        owner = sim;
	    }

       public void actionPerformed(ActionEvent event) {
           if (owner.isShowing()) {
               // Launch the resize
               owner.restoreSizes();
               Timer source = (Timer)event.getSource();
               source.stop();
           }
       }
	}

	private void buildTreeControlPanel() {
		GridBagConstraints gridBagConstraints;

		treeControlButtonPanel = new JPanel();
		expandTreeButton = new SButtonIcon(Resources.iconExpand);
		collapseTreeButton = new SButtonIcon(Resources.iconCollapse);
		
		treeControlButtonPanel.setLayout(new GridBagLayout());

		BC.langManager.registerTooltip(expandTreeButton,"Expand");
		expandTreeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				expandTreeButtonActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		treeControlButtonPanel.add(expandTreeButton, gridBagConstraints);

		BC.langManager.registerTooltip(collapseTreeButton,"Collapse");
		collapseTreeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				collapseTreeButtonActionPerformed();
			}
		});

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
		treeControlButtonPanel.add(collapseTreeButton, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		treeControlButtonPanel.add(new JLabel(), 
		        					  gridBagConstraints);
	}
	
	/**
	 * This construct a toolBar that can be added 
	 * at the top of the simulator
	 */
	private JPanel buildToolBar() {
	    if (controlButtonsPanel == null) {
	        
	        GridBagConstraints gridBagConstraints;
	        
	        controlButtonsPanel = new JPanel();
	        controlButtonsPanelEmptyLabel1 = new JLabel();
	        viewTarificationButton = new SButtonIcon(Resources.iconView);
	        launchSimulationButton = new SButtonIcon(Resources.iconLaunch);
	        
	        controlButtonsPanel.setLayout(new GridBagLayout());
	        
	        gridBagConstraints = new GridBagConstraints();
	        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
	        gridBagConstraints.weightx = 1.0;
	        controlButtonsPanel.add(controlButtonsPanelEmptyLabel1, gridBagConstraints);
	        
	        BC.langManager.registerTooltip(viewTarificationButton,"View tarification content");
	        viewTarificationButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	                viewTarificationButtonActionPerformed();
	            }
	        });
	        
	        gridBagConstraints = new GridBagConstraints();
	        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
	        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
	        
	        controlButtonsPanel.add(viewTarificationButton, gridBagConstraints);
	        
	        BC.langManager.registerTooltip(launchSimulationButton,"Launch simulation");
	        launchSimulationButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	                launchSimulationButtonActionPerformed();
	            }
	        });
	        
	        gridBagConstraints = new GridBagConstraints();
	        gridBagConstraints.insets = new Insets(3, 3, 3, 3);
	        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
	        
	        controlButtonsPanel.add(launchSimulationButton, gridBagConstraints);
	    }
		
		return controlButtonsPanel;
	}
	
	/**
	 * Builds menu bar
	 */
	private void buildMenuBar() {
		menuBar = new JMenuBar();
		mFile = new JMenu();
		mFileQuit = new JMenuItem();
		mFileSave = new JMenuItem();
		mFileSaveAs = new JMenuItem();
		mReports = new JMenu();
		mReportsFeeReportSummary = new JMenuItem();
		mReportsFeeDetailedReport = new JMenuItem();
		mReportsTariffingReport = new JMenuItem();
		mOptions = new JMenu();
		mOptionTarifications = new JMenuItem();
		mOptionOrderTrees = new JMenuItem();

		
		BC.langManager.register(mFile, "File");
		
			BC.langManager.register(mFileQuit, "Close");
			mFileQuit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mFileQuitActionPerformed();
				}
			});

			mFile.add(mFileQuit);
			
			BC.langManager.register(mFileSave, "Save");
			mFileSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mFileSaveActionPerformed();
				}
			});

			mFile.add(mFileSave);

			BC.langManager.register(mFileSaveAs, "Save As");
			mFileSaveAs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mFileSaveAsActionPerformed();
				}
			});
			mFile.add(mFileSaveAs);
			
			menuBar.add(mFile);

			// REPORTS MENU --------------------------------
			BC.langManager.register(mReports, "Reports");
			
			BC.langManager.register(mReportsFeeReportSummary, "Fee report summary...");
			mReportsFeeReportSummary.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mReportsSummaryPerformed();
				}
			});
			mReports.add(mReportsFeeReportSummary);
			
			BC.langManager.register(mReportsFeeDetailedReport, "Fee detailed report...");
			mReportsFeeDetailedReport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mReportsDetailedPerformed();
				}
			});
			mReports.add(mReportsFeeDetailedReport);
			
			BC.langManager.register(mReportsTariffingReport, "Tariffing report...");
			mReportsTariffingReport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mReportsTariffingPerformed();
				}
			});
			mReports.add(mReportsTariffingReport);
			
			menuBar.add(mReports);
			// -----------------------------------------------
			
			//mOptions.setText("Options");
			BC.langManager.register(mOptions, "Options");
			//mOptionTarifications.setText("Tarifications");
			BC.langManager.register(mOptionTarifications, "Tarifications");
			mOptionTarifications.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mOptionTarificationsActionPerformed();
				}
			});

			//mOptions.add(mOptionTarifications);
			
			BC.langManager.register(mOptionOrderTrees, "Order Trees");
			mOptionOrderTrees.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
				    mOptionOrderTreesActionPerformed();
				}
			});

			mOptions.add(mOptionOrderTrees);

			menuBar.add(mOptions);


			menuBar.add(new MergingMenu(getTarification(),this));
			menuBar.add(menuData());
			menuBar.add(new DiscountMenu(getTarification(),this));

			setJMenuBar(menuBar);
	}
	
	private JMenu menuData;
	/** get a JMenu for the data (clear options) **/
	private JMenu menuData() {
	    if (menuData != null) return menuData;
	    menuData = new JMenu();
	    BC.langManager.register(menuData,"Data");
	    
	    menuData.add(ActionClearSimulation.getMenuItem(getTarification()));
	    
	    return menuData;
	}
	
	private void buildComplexComponents() {
		this.simExplorer = 
			new CompactExplorer(this.simulationTarification, false, true, true);

			// Adding a listener to the tree
			this.simExplorer.getSTree().addTreeSelectionListener(
					new CompactTreeListener(this));
			
			this.tarifViewer = new SimuTarifViewer();
			
			this.tarifViewer.setWorkSheet(null);
			
			fillerViewer = 
				new FillerViewer(simExplorer,tarifViewer);
			
			this.treeTable = new STreeTable(this.simExplorer.getSTree(), 
					this.treeControlButtonPanel, 2, null, null, null, -1);
			
			
			
			currencyChangeListener = new CurrencyChangeListener(treeTable);
	}
	
	private void restoreSizes() {
		// Setting old parameters for STreeTable
		double[] proportions = (double[])BC.getParameter(
		        Params.KEY_SIMULATOR_DIVIDERS_POS,
		        double[].class);
		
		if (proportions.length != 3)
		    proportions = (double[])BC.forceDefaultParam(
		            Params.KEY_SIMULATOR_DIVIDERS_POS);
		
		double treeProportion = proportions[0];
		double tableProportion = proportions[1];
		double hSPlitDividerLocation = proportions[2];
		
		hSplit.setDividerLocation(hSPlitDividerLocation);
		
		TableResizer runResize = new TableResizer(this, treeProportion , tableProportion);
		SwingUtilities.invokeLater(runResize);
	}
	
	/**
	 * Get the sizes to be saved for correct component disposition at 
	 * next launch
	 */
	private double[] getSizes() {
	    double[] propsToSave = new double[3];
	    double[] treeTableSizes = treeTable.getSizes();
	    propsToSave[0] = treeTableSizes[0];
	    propsToSave[1] = treeTableSizes[1];
	    double loc = (double)hSplit.getDividerLocation();
	    double w = (double)hSplit.getWidth();
	    if (w!=0) {
	        propsToSave[2] = loc/w;
	    } else {
	        propsToSave[2] = 0.4;
	    }
	    return propsToSave;
	}
	
	/** This class is a runnable to differ treeTable resize */
	class TableResizer implements Runnable {

	    private Simulator owner;
	    private double tree;
	    private double table;
	    
	    public TableResizer(Simulator dad, double treeProp, double tableProp) {
	        owner = dad;
	        tree = treeProp;
	        table = tableProp;
	    }
	    
	    public void run() {
	        owner.treeTable().setSizes(tree,table,0);
	    }
	}
	
	/** Call it whenever simulation title (or icon) has changed */
	private void updateSimulationTitle() {
	    if (getTarification() == null) {
	        setTitle(Lang.translate(ERROR_FRAME_TITLE));
	    } else {
	    	TarificationHeader th = getTarification().getHeader();
	    	StringBuffer frameTitle = 
	    		new StringBuffer(Lang.translate(FRAME_TITLE)+": ");
	    	File loadingLocation = th.myLoadingLocation();
	    	frameTitle.append(getTarification().getTitle()+": ");
	    	if (loadingLocation != null && 
	    			th.getDataType().equals(TarificationHeader.TYPE_PORTFOLIO)) {
	    		frameTitle.append(loadingLocation.getName());
	    	} else {
	    		frameTitle.append(
	    				Lang.translate("Untitled")+" "+simulationCountTag);
	    	}
	    	if (m_needSave)
	    		frameTitle.append("*");
			// Upgrading frame title
			setTitle(frameTitle.toString());
			
			setFrameIcon(getTarification().getHeader().getIcon());
	    }
	}
	
	
	private boolean problemLauching = false;
	/** This method builds an error panel displayed in case of launch error */
	private void buildErrorUI() {
	    
	    JPanel mainPanel = new JPanel(new GridBagLayout());
	    Dimension d = new Dimension(300,300);
	    
	    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    getContentPane().setLayout(new BorderLayout());
	    
	    setMinimumSize(d);
	    setPreferredSize(d);
	    
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(5,5,5,5);
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.gridx = 0;
	    gbc.gridy = 0;
	    gbc.weighty = 1.0;
	    gbc.fill = GridBagConstraints.VERTICAL;
	    
	    JLabel errorText = new JLabel();
	    errorText.setAlignmentX(JButton.CENTER_ALIGNMENT);
	    errorText.setAlignmentY(JButton.CENTER_ALIGNMENT);
	    errorText.setText(Lang.translate(LAUNCH_ERROR_MESSAGE));
	    
	    mainPanel.add(errorText, gbc);
	    
	    gbc = new GridBagConstraints();
	    gbc.insets = new Insets(5,0,5,5);
	    gbc.anchor = GridBagConstraints.CENTER;
	    gbc.gridx = 0;
	    gbc.gridy = 1;
	    
	    JButton closeWindowButton = new JButton();
	    closeWindowButton.setText(Lang.translate("Close"));
	    closeWindowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
	    });
	    
	    mainPanel.add(closeWindowButton, gbc);

	    getContentPane().add(mainPanel, BorderLayout.CENTER);
	    
	    problemLauching = true;
	    
	    pack();
	    
	}
	
	private void launchSimulationButtonActionPerformed() {
//        InternalFrameDescriptor ifd = new InternalFrameDescriptor();
//        ifd.setCenterOnOpen(true);
//        ifd.setInitialBounds(new Rectangle(350,350));
//        
//        PieChartTest pct = new PieChartTest();
//        BC.bc.popupJIFrame(pct, ifd);
	}

	private void mOptionTarificationsActionPerformed() {
		// Add your handling code here:
	}

	private void mOptionOrderTreesActionPerformed() {
	    // Open the TreeOrderer
	    if (treeOrderer == null) {
	        treeOrderer = new TreeOrderer(this.simulationTarification, false,true);
	        InternalFrameDescriptor ifd = new InternalFrameDescriptor(
	                Params.KEY_TREE_ORDERER_BOUNDS);
//	        ifd.setInitialBounds(
//	                (Rectangle)Params.getDefaultValue(
//	                        Params.PARAM_TREE_ORDERER_BOUNDS
//	                )
//	         );
	        
	        BC.bc.popupJIFrame(treeOrderer, ifd);
	    } else {
	        treeOrderer.show();
	        treeOrderer.toFront();
	        try {
                treeOrderer.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
	    }
	}
	
	private void mReportsSummaryPerformed() {
		ReportToolbox.displayFeeReport(this, simExplorer, false);
	}
	
	private void mReportsDetailedPerformed() {
		ReportToolbox.displayFeeReport(this, simExplorer, true);
	}

	private void mReportsTariffingPerformed() {
		ReportToolbox.displayTarificationReport(this, simExplorer);
	}

	private void viewTarificationButtonActionPerformed() {

	}

	private void collapseTreeButtonActionPerformed() {
		this.simExplorer.getSTree().collapseAll();
	}

	private void expandTreeButtonActionPerformed() {
		this.simExplorer.getSTree().expandAll();
	}

	private void mFileQuitActionPerformed() {
		// Add any extra closing code in dispose() method
		//TODO as a (do you want to save popup)
		this.dispose();
	}	
	
	/**
	 * Tries to quickSave the tarification
	 * If it cannot it will launch a file chooser....
	 */
	private void mFileSaveActionPerformed() {
		m_log.debug("Saving...");
	    if (FileManagement.save(getTarification(), FileManagement.SIMULATOR_SAVE)) {
	    	hasBeenSaved();
	    	m_log.debug("Saving done.");
	    }
	}

	private void mFileSaveAsActionPerformed() {
	    // change the type to a portfolio
		getTarification().getHeader().setDataType(
				TarificationHeader.TYPE_PORTFOLIO);
		if (FileManagement.saveAs(getTarification(), FileManagement.SIMULATOR_SAVE))
			hasBeenSaved();
	}
	
	/**
	 * Overriding super dispose to add some extra close operations
	 */
	public void dispose() {
	    
	    if (problemLauching) {
	        super.dispose();
	        return;
	    }
	    
		// if modification succeded. Ask if once saves.
	    if (m_needSave) {
	        String[] params = new String[1];
	        params[0] = simulationTarification.getTitle();
	        String res = Lang.translate(SAVE_CHANGES_MESSAGE, params);
	        
	        if (ModalDialogBox.questionUncancelable(BC.bc.getMajorComponent(), res)
	                ==	ModalDialogBox.ANS_YES)
	            FileManagement.save(getTarification(), FileManagement.SIMULATOR_SAVE);
	    }
	    // Add extra closing code here
	    if (treeOrderer != null) {
	        treeOrderer.dispose();
	        treeOrderer = null;
	    }
	    
	    // Saving divider positions
	    BC.setParameter(Params.KEY_SIMULATOR_DIVIDERS_POS, getSizes());
	    
	    // kill the autosave task
	    m_autosaveTask.cancel();
	    super.dispose();
	}
	
	/**
	 * Call this method when the file has been modified. It enables the
	 * autosave task, and set the m_needsave flag to true
	 */
	public void needSave() {
		m_needSave = true;
		m_autosaveTask.start();
		updateSimulationTitle();
	}
	
	/** Called whenever the file is saved */
	private void hasBeenSaved() {
		m_needSave = false;
		m_autosaveTask.stop();
		updateSimulationTitle();
	}
	
	/**
	 * Event management
	 */
	public void eventOccured(NamedEvent e) {
		switch (e.getEventCode()) {
		case NamedEvent.COM_VALUE_CHANGED_TARIFICATION :
			treeTable.getTable().repaint();
		break;
		default :
			needSave();
		break;
		}
	}
	
	protected void setCurrentWorkSheet(WorkSheet ws) {
		// TODO dispatch to other elements
		String ret = "null";
		if (ws != null) {
			ret = ws.getTitle();
		}
		
		this.tarifViewer.setWorkSheet(ws);
	}
	
	/**
	 * @see com.simpledata.bc.BC.TarificationModifiers#tarifModifierGetTarifications()
	 */
	public List/*<Tarification>*/ tarifModifierGetTarifications() {
		ArrayList l = new ArrayList();
		l.add(getTarification());
		return l;
	}

	/**
	 * @see com.simpledata.bc.BC.TarificationModifiers#tarifModifierGetTitle()
	 */
	public String tarifModifierGetTitle() {
		return getTitle();
	}
	
}

/**
 * Simple TreeSelectionListener for compactExplorer
 */
class CompactTreeListener implements TreeSelectionListener {
	
	private static final Logger m_log = Logger.getLogger( CompactTreeListener.class );

	CompactTreeItem lastSelected;
	Simulator owner;

	public CompactTreeListener(Simulator sim) {
		owner= sim;
	}

	/* (non-Javadoc)
	 * @see event.TreeSelectionListener#valueChanged(event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent event) {
		TreePath tp= event.getNewLeadSelectionPath();
		if (tp != null) {
			CompactNode cn= (CompactNode) tp.getLastPathComponent();
			if (cn == lastSelected)
				return;
			lastSelected= cn;
			
			 // tell the FillerViewr to change
            owner.fillerViewer.showCompactNodeInfo(cn);
			
			m_log.debug( "Asking workSheet for Node : "+cn );
			this.owner.setCurrentWorkSheet(cn.contentsGetWorkSheet());
		}

	}

}

class CurrencyChangeListener implements ChangeListener {
	STreeTable st;
	public CurrencyChangeListener(STreeTable stable) {
		st = stable;
		BC.getCurrencyManager().addWeakCurrencyChangeListener(this);
		stateChanged(null);
	}
	
	public void stateChanged(ChangeEvent e) {
		st.setColumnName(0,Lang.translate("Fees")+": "+
				BC.getCurrencyManager().defaultCurrency().currencyCode());
		// horrible hack ... TODO make this better
		st.removeColumn(1);
	}
	
}
/*
 * $Log: Simulator.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.76  2004/12/04 13:17:23  perki
 * hacked discount bug .. anyway the whole system of display should be reviewed
 *
 * Revision 1.75  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.74  2004/11/17 12:04:40  perki
 * Discounts Step 2
 *
 * Revision 1.73  2004/11/17 10:52:03  perki
 * Discount display preview step1
 *
 * Revision 1.72  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.71  2004/11/16 15:17:56  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.70  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.69  2004/11/10 17:35:46  perki
 * Tree ordering is now correct
 *
 * Revision 1.68  2004/11/09 13:55:43  jvaucher
 * - Ticket # 40 : Autosave, added user parameters
 *
 * Revision 1.67  2004/11/08 16:42:35  jvaucher
 * - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 * Revision 1.66  2004/11/05 16:32:32  perki
 * Now calculations are done at loading of simulations
 *
 * Revision 1.65  2004/10/16 11:41:26  jvaucher
 * - Minor changes (Help, reports, UI)
 *
 * Revision 1.64  2004/10/15 17:50:04  carlito
 * SLabel
 *
 * Revision 1.63  2004/10/15 06:39:00  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.62  2004/10/12 17:49:10  carlito
 * Simulator split problems solved...
 * description pb solved
 *
 * Revision 1.61  2004/10/11 07:49:00  perki
 * Links in Filler
 *
 * Revision 1.60  2004/09/28 08:55:22  perki
 * Minor changes
 *
 * Revision 1.59  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.58  2004/09/23 10:41:04  carlito
 * Tarifications icons used for the frames of Creator and Simulator
 *
 * Revision 1.57  2004/09/22 17:21:49  carlito
 * confirm dialogs with icons...
Texts updated...
 *
 * Revision 1.56  2004/09/22 15:46:11  jvaucher
 * Implemented cleaver load/save system
 *
 * Revision 1.55  2004/09/22 15:39:55  carlito
 * Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 * Revision 1.54  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.53  2004/09/16 09:55:50  jvaucher
 * Introduced the total volume calculation for the transactions rateBySlice workplace.
 *
 * Revision 1.52  2004/09/15 11:04:22  jvaucher
 * Added a textual message for the event code (method eventName())
 * Added the modification dialog box. But some changes are still not observable. See tickets for details
 *
 * Revision 1.51  2004/09/14 15:30:53  jvaucher
 * Minor locale changes
 *
 * Revision 1.50  2004/09/14 12:07:26  carlito
 * commit de protection
 *
 * Revision 1.49  2004/09/07 15:32:37  carlito
 * *** empty log message ***
 *
 * Revision 1.48  2004/09/04 18:12:32  kaspar
 * ! Log.out -> log4j
 *   Only the proper logger init is missing now.
 *
 * Revision 1.47  2004/09/01 16:22:58  jvaucher
 * - Fee reports. First stable version. But, unable to process worksheets
 * of the first level of the tree.
 *
 * Revision 1.46  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.45  2004/08/02 15:24:44  perki
 * Repartition viewer on simulator
 *
 * Revision 1.44  2004/07/30 05:50:01  perki
 * Moved all CompactTree classes from uicompnents to uicomponents.compact
 *
 * Revision 1.43  2004/07/29 18:29:24  carlito
 * *** empty log message ***
 *
 * Revision 1.42  2004/07/28 11:42:28  kaspar
 * ! PieChart test code commented out, since the
 *   corresponding package is not in the CVS.
 *
 * Revision 1.41  2004/07/27 17:56:29  carlito
 * *** empty log message ***
 *
 * Revision 1.40  2004/07/26 17:39:37  perki
 * Filler is now home
 *
 * Revision 1.39  2004/07/26 16:46:10  carlito
 * *** empty log message ***
 *
 * Revision 1.38  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.37  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.36  2004/07/20 16:19:46  perki
 * merging menus
 *
 * Revision 1.35  2004/07/19 20:00:33  carlito
 * *** empty log message ***
 *
 * Revision 1.34  2004/07/19 17:39:08  perki
 * *** empty log message ***
 *
 * Revision 1.33  2004/07/15 17:44:38  carlito
 * *** empty log message ***
 *
 * Revision 1.32  2004/07/09 19:10:25  carlito
 * *** empty log message ***
 *
 * Revision 1.31  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.30  2004/06/25 10:09:50  perki
 * added first step for first sons detection
 *
 * Revision 1.29  2004/06/25 08:30:55  perki
 * oordering in tree modified
 *
 * Revision 1.28  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.27  2004/06/22 11:22:40  perki
 * *** empty log message ***
 *
 * Revision 1.26  2004/06/22 11:06:50  carlito
 * Tree orderer v0.1
 *
 * Revision 1.25  2004/06/22 10:56:20  perki
 * Lot of cleaning in CompactNode part1
 *
 * Revision 1.24  2004/06/20 16:09:03  perki
 * *** empty log message ***
 *
 * Revision 1.23  2004/06/16 07:49:28  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/05/31 16:56:00  carlito
 * *** empty log message ***
 *
 * Revision 1.21  2004/05/31 14:00:37  perki
 * *** empty log message ***
 *
 * Revision 1.20  2004/05/31 12:40:22  perki
 * *** empty log message ***
 *
 * Revision 1.19  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.18  2004/05/19 16:39:58  perki
 * *** empty log message ***
 *
 * Revision 1.17  2004/05/18 19:09:47  carlito
 * *** empty log message ***
 *
 * Revision 1.16  2004/05/11 17:57:01  carlito
 * *** empty log message ***
 *
 * Revision 1.15  2004/05/11 13:40:35  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/05/10 17:43:54  carlito
 * *** empty log message ***
 *
 * Revision 1.13  2004/05/05 16:52:29  carlito
 * *** empty log message ***
 *
 * Revision 1.12  2004/04/12 09:41:28  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.10  2004/03/22 18:21:30  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/03/22 14:32:30  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/03/18 15:34:12  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/03/18 09:14:06  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/03/17 17:10:24  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/03/08 09:02:20  perki
 * houba houba hop
 *
 * Revision 1.4  2004/03/06 14:24:50  perki
 * Tirelipapon sur le chiwawa
 *
 * Revision 1.3  2004/03/06 11:49:22  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/03/03 18:45:52  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/03/03 16:52:07  carlito
 * *** empty log message ***
 *
 */
