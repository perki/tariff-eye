/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: TarificationCreator.java,v 1.2 2007/04/02 17:04:22 perki Exp $ 
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.actions.ActionDropTarif;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.datamodel.pair.Pairable;
import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.bc.merging.MergingMenu;
import com.simpledata.bc.reports.ReportToolbox;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.*;
import com.simpledata.bc.uicomponents.compact.*;
import com.simpledata.bc.uicomponents.conception.wizards.*;
import com.simpledata.bc.uicomponents.filler.FillerViewer;
import com.simpledata.bc.uicomponents.tools.*;
import com.simpledata.bc.uitools.*;
import com.simpledata.bc.uitools.streetable.STreeTable;
import com.simpledata.uitools.stree.STree;


/**
 * THIS CLASS IS DEAD
 * it stays around a little while the newer interfaces are being tested
 * for rollback reasons
 * 
 * This class defines the main frame for the tarification creation tool. 
 * It is the controller that creates all components.
 * 
 */
public class TarificationCreator
	extends JInternalFrame
	implements NamedEventListener, BC.TarificationModifiers {
//	extends Creator {

	private static Logger m_log = Logger.getLogger( TarificationCreator.class );
    
	private final static String PARAM_CREATOR = "Creator:Bounds";
	private final static String PARAM_TARIF_LIST = "Creator:TarifList:Bounds";
	private final static String PARAM_TREE_ORDERER="Creator:TreeOrderer:Bounds";
	
	//double[5] 3 first entries-> streeTable, 2 others main and secondary splits
	private final static String PARAM_DIVIDER_POS = "Creator:DividerPositions";
	
	/**
	 * Static WeakHashMap that does contains actually opened Tarifications
	 * Tarification serves as keys, and TarificationCreator as values.
	 */
	private static WeakHashMap openedTarification;
	
	/**
	 * Counter for opened tarificationTools
	 */
	private static Integer tarificationCount;
	
	private Tarification tarification;
	private CompactExplorer compExplorer;
	private ArrayList strees= null;
	
	// SECTION FOR TarificationExplorer
	/** 
	 * Strees are actualy opened TarificationExplorerTree, 
	 * keys are the BCTree relative to them 
	 */
	protected HashMap tetrees= null;
	/** TarifViewer*/
	public TarifViewer tarifViewer= null;
	/** The actually selected Tarif ... set to null if none **/
	private Tarif selectedTarif= null;
	// UI 
	private TarifList tl= null;
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JPanel workSheetsPanel;
	
	
	private JPanel centralPanel;
	//private JTree compactTree;
	private JPanel compactTreeActionButtonsPanel;
	private SButtonIcon compactTreeCollapseButton;
	private SButtonIcon compactTreeExpandButton;

	//private SButton treeConstructButton;

	private JCheckBox compactTreeAutoShrinkCheck;
	private JPanel compactTreePanel;
	
	private JSplitPane hSplit1;
	private JSplitPane hSplit2;
	//private JTree jTree1;
	private JMenu mFile;
	private JMenuItem mFileExit;
	//private JMenuItem mFileNew;
	private JMenuItem mFileOpen;
	private JMenuItem mFileSave;
	private JMenuItem mFileSaveAs;
	private JMenuItem mFilePublish;
	private JMenuItem mFileTarifSummary; 
	private JMenu mOptions;
	private JMenuItem mOptionsOrderTrees;
	private JMenuBar menuBar;

	private SButtonIcon deleteTarifButton;
	private SButtonIcon newTarifButton;
	private SButtonIcon tarifPairButton;

	private JPanel tarifActionsPanel;
	//private JPanel tarifDescriptionPanel;
	private JPanel treesActionButtonsPanel;
	private SButtonIcon treesCollapseButton;
	private SButtonIcon treesExpandButton;
	private JPanel treesPanel;
	private ComboTabbedPane treesTabbedPane;
	//private JTree wsTree;
	// End of variables declaration//GEN-END:variables
	
	private TarificationExplorerTree.ExtraPopup extraPopup;
	
	// it is ok if never read.. references is kept just to prevent gc
	private CurrencyChangeListener myCurrencyListener;
	
	private STreeTable compactTreeTable;
	
	private TreeOrderer treeOrderer;
	
	/** The Filler Viewer Controler Panel .. it's shown when needed **/
	protected FillerViewer fillerViewer;
    
    /**
     * Use openTarification(Tarification t) to open a Tarification
     * @see #openTarification(Tarification t)
     */
    protected TarificationCreator(Tarification t) {
        // JInternalFrame(String title,  boolean resizable,  boolean closable,  boolean maximizable,  boolean�iconifiable)

        super(Lang.translate("Tarification creation tool"), 
                true, true, true, true);

        
        this.tarification= t;
        
        
       // t.setReadyForCalcul(false);
        
        
        
        this.setFrameIcon(Resources.iconTools);
        
        if (t == null) {
					// Throw an exception into the log and silently fail. 
        	        // Is this the best way of reacting ? 
					m_log.fatal(
					"Tried to create a Tarification Creator from null Tarification");
					this.dispose();
        }
        
        this.tarification.addNamedEventListener(this);
        
        this.extraPopup= new ExtraPopup();
        
        //  Compact Explorer
		compExplorer= new CompactExplorer(this.tarification);
        
        initStdComponents();
        initCplxComponents();
        
        // set selection to null
        setCurrentWorkSheet(null);
        
        
        new OnShowDo(compExplorer.getSTree()) {
            public void done() {
                compExplorer.expandTree();
            }
        };
        
    }
		
	// FIXME: COMPONENT_SHOWN in ComponentListener achieves the
	// same result as this class without the thread. (ksc)
	// Normally one does not subclass thread, but creates a Runnable...
	abstract class OnShowDo extends Thread {
			
		Component c;
		
		public OnShowDo(Component c) {
			super( "OnShowDo" ); 
			this.c = c;
			start();
		}
		
		public void run()  {
			while (! c.isShowing()) {
				try {
					sleep(50);
				} catch (InterruptedException e) {
					m_log.warn( e );
				}
			}
			done();
		}
		public abstract void done();
	}
	
	/**
	 * Launch UI displaying a tarification for edition
	 * @param t tarification to be edited
	 * @return the TarifCreator  
	 */
	public static TarificationCreator openTarification(Tarification t) {
		
		
		if (openedTarification == null)
			openedTarification = new WeakHashMap();

		TarificationCreator tc= null;

		if (openedTarification.containsKey(t)) {
			tc= (TarificationCreator) openedTarification.get(t);
		}

		if (tc == null) {
			tc= new TarificationCreator(t);
			openedTarification.put(t, tc);
			tc.setTitle(tc.getTitle()+" "+getNextTarificationCount());
		}
		
		InternalFrameDescriptor ifd = new InternalFrameDescriptor(PARAM_CREATOR);
		ifd.setInitialBounds(new Rectangle(
		        100,1,
		        tc.getWidth(), tc.getHeight()
		));
		BC.bc.popupJIFrame(tc, ifd);
		
		return tc;
	}
	
	 
    private Tarif selectedTarif() {
        return selectedTarif;
    }
	
	/**
	 * @return the next tool number
	 * i.e the number that will be displayed in window name
	 */
	private static int getNextTarificationCount() {
		if (tarificationCount == null) {
			tarificationCount = new Integer(0);
		} else {
			int nextCount = tarificationCount.intValue() + 1;
			tarificationCount = new Integer(nextCount);
		}
		return tarificationCount.intValue();
		//return openedTarification.size();
	}
	
	private void initCplxComponents() {
		tetrees= new HashMap();
		strees= new ArrayList();

		// Trees
		BCTree[] trees= this.tarification.getMyTrees();

		for (int i= 0; i < trees.length; i++) {
			TarificationExplorerTree tet=
				new TarificationExplorerTree(trees[i], true, true, extraPopup);

			tetrees.put(trees[i], tet);

			// Ajout des STrees dans le tabbedPane correspondant
			JScrollPane jsc= new JScrollPane();
			jsc.setBorder(new LineBorder(null,0));
			jsc.setViewportView(tet);
			ImageIcon ico= tet.getRootTEN().getLeafIcon();
			treesTabbedPane.addTab(
				Lang.translate(trees[i].getTitle()),
				ico,
				jsc);
			strees.add(tet);
		}

		

		// tarif Viewer (must be created before wsTree)
		// TODO reactivate in case of rollback
		//tarifViewer= new TarifViewerConception();
		tarifViewer.setTarif(null);
		tarifViewer.refresh();
		workSheetsPanel.add(tarifViewer, BorderLayout.CENTER);

		
		

		//		 Reload old parameter
		Boolean b= (Boolean) BC.getParameter("autoShrinkView", Boolean.class);
		if (b == null) {
			b= new Boolean(true);
			BC.setParameter("autoShrinkView", b);
		}
		compactTreeAutoShrinkCheck.setSelected(b.booleanValue());
		compExplorer.setAutoShrinkView(b.booleanValue());

		STree st= compExplorer.getSTree();

		st.addTreeSelectionListener(new MyCompactSelectionListener(this));

		st.setBorder(new EtchedBorder());
		JScrollPane jsc= new JScrollPane();
		jsc.setViewportView(st);
		
		// TODO remettre en place et enlever le bloc suivant 
		// une fois le DEVEL termin�
		//compactTreePanel.add(jsc, java.awt.BorderLayout.CENTER);

		/** **/
		ArrayList colNames = new ArrayList();
		colNames.add("Currency");
		
		ArrayList colSizes = new ArrayList();
		colSizes.add(new Integer(90));
		this.compactTreeTable = new STreeTable(this.compExplorer.getSTree(), 
			compactTreeActionButtonsPanel, 1, colNames, colSizes, null, -1);
	

		//Add a listener to change the Table column name on currency change
		myCurrencyListener = new CurrencyChangeListener(compactTreeTable);
		
		
		this.compactTreeTable.setHighLightColors(Color.LIGHT_GRAY, Color.BLACK);
		
		final double[] proportions = (double[])BC.getParameter(PARAM_DIVIDER_POS, 
		        double[].class);
		if (proportions != null) {
		    
		    /////////////////////////////////////////////////////
		    /////////////    METHOD 2    ////////////////////////
		    /////////////////////////////////////////////////////
		    		    
		    final Runnable r0 = new Runnable() {
                public void run() {
                    hSplit1.setDividerLocation(proportions[3]);
                }
		    };

		    final Runnable r1 = new Runnable() {
		        
		        public void run() {
		            hSplit2.setDividerLocation(proportions[4]);
		        }
		        
		    };
		    
		    final Runnable r2 = new Runnable() {
		        
		        public void run() {
		            compactTreeTable.setSizes(proportions[0], proportions[1], proportions[2]);
		        }
		        
		    };    
		    
		    Timer tim = new Timer(100, new ActionListener() {
		        
		        private int state = 0;
		        
		        public void actionPerformed(ActionEvent e) {
		            if (state == 0) {
		                // Launch first runnable
		                SwingUtilities.invokeLater(r0);
		            }
		            if (state == 1) {
		                // Launch second runnable
		                SwingUtilities.invokeLater(r1);
		            }
		            if (state == 2) {
		                // Launch third runnable
		                SwingUtilities.invokeLater(r2);
		            }
		            if (state > 2) {
		                // Stop the timer
		                Timer source = (Timer)e.getSource();
	                    source.stop();
		            }
		            state++;
		        }
		        
		    });
		    tim.start();
		    
		} else {
		    this.compactTreeTable.setSizes(0.66, 0.33, 0.00);
		}
		compactTreePanel.add(
			this.compactTreeTable, 
			java.awt.BorderLayout.CENTER
		);

		/** **/

		// Adding auto show tarifs capability
		compactTreeAutoShrinkCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compactTreeAutoShowCheckActionPerformed();
			}
		});

	}
	/**
	 * get the TarificationExplorerTree relative to this BCNode
	 */
	public TarificationExplorerTree getTETree(BCNode node) {
		if (node == null)
			return null;
		return (TarificationExplorerTree) tetrees.get(node.getTree());
	}

	/**
	 * get the actually selected Tarif
	 */
	public Tarif getSelectedTarif() {
		return selectedTarif;
	}
	
	/** clean the UI **/
	private void cleanUI() {
		this.setCurrentWorkSheet(null);
		if (tl != null)
		    this.tl.refreshList();
		this.compExplorer.refreshStructure();
	}

	/**
	 * select Tarif in all strees and show workSheet in TarifViewer
	 */
	public void setCurrentWorkSheet(WorkSheet ws) {
		tarifViewer.setWorkSheet(ws);
		
		Tarif t = null;
		if (ws != null)
			t = ws.getTarif();
		
		if (t == selectedTarif) return;
		
		if (t == null) {
			this.deleteTarifButton.setEnabled(false);
			tarifPairButton.setEnabled(false);
		} else {
	
			tarifPairButton.setEnabled((t instanceof Pairable));
			this.deleteTarifButton.setEnabled(true);
		}

		// First remove TarificationCreator from old tarif listeners list
		// if it exists
		if (this.selectedTarif != null) {
			this.selectedTarif.removeNamedEventListener(this);
		}

		this.selectedTarif= t;

		// Attach TarificationCreator as listener for the selected tarif
		if (this.selectedTarif != null) {
			this.selectedTarif.addNamedEventListener(this);
		}

		// foreach stree
		Iterator i= tetrees.values().iterator();
		while (i.hasNext()) {
			((TarificationExplorerTree) i.next()).setSelectedTarif(t);
		}

		// Show tarif on Compact Explorer
		this.compExplorer.expandNodesWithObject(t);
	}
	
	public void showTree(BCTree tree) {
		Iterator e = strees.iterator();
		for (int i = 0; e.hasNext(); i++) {
			if (tree == ((TarificationExplorerTree) e.next()).getTree())
			treesTabbedPane.setSelectedIndex(i);
		}
	}

	public TarifViewer getTarifViewer() {
		return this.tarifViewer;
	}

	public CompactExplorer getCompactExplorer() {
		return this.compExplorer;
	}


	private void initStdComponents() { //GEN-BEGIN:initComponents
		hSplit1= new JSplitPane();
		treesPanel= new JPanel();
		treesActionButtonsPanel= new JPanel();
		treesExpandButton= new SButtonIcon(Resources.iconExpand);
		treesCollapseButton= new SButtonIcon(Resources.iconCollapse);
		//treesTabbedPane= new ComboTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);3
		treesTabbedPane= new ComboTabbedPane();

		hSplit2= new JSplitPane();
		hSplit2.setOneTouchExpandable(true);
		centralPanel= new JPanel();
		
		
		fillerViewer = 
			new FillerViewer(compExplorer,centralPanel);
		tarifActionsPanel= new JPanel();
		newTarifButton= new SButtonIcon(Resources.iconNew);
		deleteTarifButton= ActionDropTarif.createButton(this,
		        new ActionDropTarif.Interface(){
                    public Tarif actionDropTarif_getTarifToDrop() {
                        return selectedTarif();
                    }
                    public void actionDropTarif_tarifDroped(Tarif t) {
                       cleanUI();
                    }}
		
		);
		
		workSheetsPanel= new JPanel(new BorderLayout());
		compactTreePanel= new JPanel();
		compactTreeActionButtonsPanel= new JPanel();
		compactTreeExpandButton= new SButtonIcon(Resources.iconExpand);
		compactTreeCollapseButton= new SButtonIcon(Resources.iconCollapse);
		
		compactTreeAutoShrinkCheck= new JCheckBox();
		menuBar= new JMenuBar();
		mFile= new JMenu();
		//mFileNew= new JMenuItem();
		mFileOpen= new JMenuItem();
		mFileSave= new JMenuItem();
		mFileSaveAs= new JMenuItem();
		mFileExit= new JMenuItem();
		mFilePublish = new JMenuItem();
		mFileTarifSummary = new JMenuItem();
		
		mOptions = new JMenu();
		mOptionsOrderTrees = new JMenuItem();


		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setName(Lang.translate("Tarification Creation Tool"));

		hSplit1.setBorder(null);
		treesPanel.setLayout(new java.awt.BorderLayout());

		treesPanel.setBorder(new EtchedBorder());
		treesPanel.setMinimumSize(new java.awt.Dimension(150, 700));
		treesPanel.setPreferredSize(new java.awt.Dimension(250, 700));
		treesActionButtonsPanel.setLayout(
			new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 1, 1));

		treesActionButtonsPanel.setMaximumSize(new java.awt.Dimension(250, 29));
		treesActionButtonsPanel.setMinimumSize(new java.awt.Dimension(150, 29));
		treesActionButtonsPanel.setPreferredSize(
			new java.awt.Dimension(250, 29));
		//treesExpandButton.setFont(new java.awt.Font("Dialog", 0, 11));
		//treesExpandButton.setText("expand");
		BC.langManager.registerTooltip(treesExpandButton, "Expand");

		// DEBUG

		treesExpandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				treesExpandButtonActionPerformed();
			}
		});

		treesActionButtonsPanel.add(treesExpandButton);

		//treesCollapseButton.setFont(new java.awt.Font("Dialog", 0, 11));
		//treesCollapseButton.setText("collapse");
		BC.langManager.registerTooltip(treesCollapseButton, "Collapse");
		treesCollapseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				treesCollapseButtonActionPerformed();
			}
		});

		treesActionButtonsPanel.add(treesCollapseButton);

		treesPanel.add(treesActionButtonsPanel, java.awt.BorderLayout.NORTH);

		treesPanel.add(treesTabbedPane, java.awt.BorderLayout.CENTER);

		//hSplit1.setLeftComponent(treesPanel);

		hSplit2.setBorder(null);
		centralPanel.setLayout(new java.awt.BorderLayout());

		tarifActionsPanel.setLayout(
			new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 1, 1));

		
		
		tarifPairButton = new SButtonIcon(
				Resources.stdTagPaired
		);
		BC.langManager.registerTooltip(
				tarifPairButton,
				"Access to pairing properties of this Tarif");
		tarifPairButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				tarifPairButtonActionPerformed(tarifPairButton);
			}
		});
		tarifActionsPanel.add(tarifPairButton);
		
		
		
		
		//newTarifButton.setFont(new java.awt.Font("Dialog", 0, 11));
		BC.langManager.registerTooltip(newTarifButton, "Create new tarif");
		newTarifButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newTarifButtonActionPerformed();
			}
		});

		tarifActionsPanel.add(newTarifButton);

		
		tarifActionsPanel.add(deleteTarifButton);
		
		
		

		centralPanel.add(tarifActionsPanel, BorderLayout.NORTH);

		

		workSheetsPanel.setMinimumSize(new java.awt.Dimension(270, 10));
		workSheetsPanel.setPreferredSize(new java.awt.Dimension(470, 10));
		centralPanel.add(workSheetsPanel, BorderLayout.CENTER);

		hSplit2.setLeftComponent(fillerViewer);

		compactTreePanel.setLayout(new java.awt.BorderLayout());

		compactTreeActionButtonsPanel.setBorder(new EtchedBorder());
		compactTreeActionButtonsPanel.setMaximumSize(
			new java.awt.Dimension(250, 29));
		compactTreeActionButtonsPanel.setMinimumSize(
			new java.awt.Dimension(150, 29));
		compactTreeActionButtonsPanel.setPreferredSize(
			new java.awt.Dimension(250, 29));

		compactTreeActionButtonsPanel.setLayout(new java.awt.GridBagLayout());

		BC.langManager.registerTooltip(compactTreeExpandButton, "Expand");
		compactTreeExpandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compactTreeExpandButtonActionPerformed();
			}
		});
		GridBagConstraints gridBagConstraints;
		gridBagConstraints= new java.awt.GridBagConstraints();
		gridBagConstraints.insets= new java.awt.Insets(2, 1, 2, 0);
		gridBagConstraints.anchor= java.awt.GridBagConstraints.WEST;
		compactTreeActionButtonsPanel.add(
			compactTreeExpandButton,
			gridBagConstraints);

		BC.langManager.registerTooltip(compactTreeCollapseButton, "Collapse");
		compactTreeCollapseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compactTreeCollapseButtonActionPerformed();
			}
		});
		gridBagConstraints= new java.awt.GridBagConstraints();
		gridBagConstraints.insets= new java.awt.Insets(2, 1, 2, 0);
		gridBagConstraints.anchor= java.awt.GridBagConstraints.WEST;
		compactTreeActionButtonsPanel.add(
			compactTreeCollapseButton,
			gridBagConstraints);
		
		gridBagConstraints= new java.awt.GridBagConstraints();
		gridBagConstraints.fill= java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx= 1.0;
		compactTreeActionButtonsPanel.add(new JLabel(), gridBagConstraints);

		BC.langManager.register(compactTreeAutoShrinkCheck, "Auto Shrink View");
		gridBagConstraints= new java.awt.GridBagConstraints();
		gridBagConstraints.insets= new java.awt.Insets(2, 3, 2, 6);
		gridBagConstraints.anchor= java.awt.GridBagConstraints.EAST;
		compactTreeActionButtonsPanel.add(
			compactTreeAutoShrinkCheck,
			gridBagConstraints);

		compactTreePanel.add(compactTreeActionButtonsPanel, BorderLayout.NORTH);

		//hSplit2.setRightComponent(compactTreePanel);
		hSplit2.setRightComponent(treesPanel);
		hSplit1.setLeftComponent(compactTreePanel);
		
		
		hSplit1.setRightComponent(hSplit2);
		
		hSplit1.setResizeWeight(0.33);
		hSplit2.setResizeWeight(0.66);

		getContentPane().add(hSplit1, java.awt.BorderLayout.CENTER);

		//mFile.setText("File");
		BC.langManager.register(mFile,"File");
		
		if (BC.isSimple()) {
			JMenuItem jmi = new JMenuItem("Debug Print Tarification");
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					m_log.info("XXX\n"+getTarification().toString());
				}
			});
			mFile.add(jmi);

			//mFileOpen.setText("Open Tarif List");
			BC.langManager.register(mFileOpen, "Open Tarif List");
			mFileOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mFileOpenActionPerformed();
				}
			});
	
			mFile.add(mFileOpen);
		}

		//mFileExit.setText("Quit");
		BC.langManager.register(mFileExit,"Close");
		mFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFileExitActionPerformed();
			}
		});

		mFile.add(mFileExit);
		
		//mFile.add(new JSeparator());
		
		//mFileSave.setText("Save");
		BC.langManager.register(mFileSave, "Save");
		mFileSave.setMnemonic(KeyEvent.VK_S);
		mFileSave.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.META_MASK));
		mFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFileSaveActionPerformed();
			}
		});

		mFile.add(mFileSave);

		//mFileSaveAs.setText("Save As...");
		BC.langManager.register(mFileSaveAs, "Save As");
		mFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFileSaveAsActionPerformed();
			}
		});

		mFile.add(mFileSaveAs);
		
		//mFile.add(new JSeparator());
		
		//		mFileSaveAs.setText("Save As...");
		BC.langManager.register(mFilePublish, "Publish");
		mFilePublish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFilePublishActionPerformed();
			}
		});

		mFile.add(mFilePublish);
		
		BC.langManager.register(mFileTarifSummary, "Tarification Summary...");
		mFileTarifSummary.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doTarifSummary();
			}
		} );

		mFile.add(mFileTarifSummary);
		
		mFile.add(new JSeparator());
		
		mFile.add(getCreationTagMenu());

		menuBar.add(mFile);
		
		BC.langManager.register(mOptions, "Options");
		
		BC.langManager.register(mOptionsOrderTrees, "Order Trees");
		mOptionsOrderTrees.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
				mOptionsOrderTreesActionPerformed();
			}
		});
		
		mOptions.add(mOptionsOrderTrees);
		
		menuBar.add(mOptions);
		
		// add setDepth
		menuBar.add(getMaxDepthMenu());
		
		
		menuBar.add(new MergingMenu(getTarification(),this));

		setJMenuBar(menuBar);

		pack();
	} //GEN-END:initComponents
	
	/**
	 * Show a Tarification report to the user. 
	 */
	private void doTarifSummary() {
		ReportToolbox.displayTarificationReport( this, compExplorer );
	}
	
	/**
	 * Show a Fee report to the user
	 */
	private void doFeeSummary() {
	    ReportToolbox.displayFeeReport(this, compExplorer, true);
	}

	//------------------ CREATION TAG ----------------------//
	private JMenuItem creationTagMenu;
	
	private JMenuItem getCreationTagMenu() {
		if (creationTagMenu != null) return creationTagMenu;
		
		final Tarification tf = getTarification();
		
		creationTagMenu = new JMenuItem(Lang.translate("Creation tag"));
		
		creationTagMenu.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JPanel jp = new JPanel(new BorderLayout());
				
				jp.add(new JLabel("Simpledatacode : "
						+Tarification.publishingCreationTag),
						BorderLayout.NORTH);
				
				JTextFieldBC jtf = new JTextFieldBC() {

					public void stopEditing() {
						tf.changeCreationTag(getText());
					}

					public void startEditing() {
						
						
					}};
				jtf.setText(tf.getCreationTag());
				
				jp.add(jtf,BorderLayout.CENTER);
				
				ModalJPanel.createSimpleModalJInternalFrame(jp,
								centralPanel,new Point(0,0),true,
								Resources.iconEdit,Resources.modalBgColor);
				
			}});
		return creationTagMenu;
	}
	
	
	//------------------ DEPTH MENU ------------------------//
	
	private JMenu maxDepthMenu;
	/**
	 * TODO REMOVE WHEN BETTER SOLUTION FOUND
	 * get an option panel to change the depth of View
	 */
	JMenu getMaxDepthMenu() {
		if (maxDepthMenu != null) return maxDepthMenu;
	
		maxDepthMenu= new JMenu(Lang.translate("Depth Of View"));
	
		// value for the JcomboBox
		int[] vals= { 1, 2, 3, 4, 5, 10 };
	
		for (int i= 0; i < vals.length; i++) {
			final int j = vals[i];
			final JMenuItem jmi = new JMenuItem(""+vals[i]);
			if (jmi.getText().equals(""+TarifViewer.getMaxDepth())) {
				jmi.setIcon(Resources.greenBall);
			 }
			
			maxDepthMenu.add(jmi);
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (tarifViewer == null) return;
					 
					tarifViewer.setMaxDepth(j);
					
					//	set the selected element in the depth selection Menu
					Component[] cs = getMaxDepthMenu().getMenuComponents();
					 for (int k = 0; k < cs.length; k++) {
						JMenuItem jmit = (JMenuItem) cs[k];
						jmit.setIcon(Resources.pixel);
						 if (jmit.getText().equals(""+TarifViewer.getMaxDepth())) {
							jmit.setIcon(Resources.greenBall);
						 }
					 }
				}
			});
		}

		return maxDepthMenu;
	}

	private void tarifPairButtonActionPerformed(Component origin) {
		if (selectedTarif != null && (selectedTarif instanceof Pairable)) {
			
			PairingDialog.showPairingDialog(
					(Pairable) selectedTarif,origin,
					new PairingDialog.Controler(){

						public void jumpTo(Tarif t) {
							setCurrentWorkSheet(t.getWorkSheet());
						}});
		}
	}

	public void newTarifButtonActionPerformed() {
		startTarifCreationWizard(null, null, null, null);
	}

	/** 
	 * launch a Tarif Creation process 
	 * All parameters can be null
	 * **/
	protected void startTarifCreationWizard(
		BCNode localisationInBaseTree,
		String tarifType,
		String title,
		String description) {
		NewTarifWizard myWiz= new NewTarifWizard();

		// create steps
		ArrayList mySteps= new ArrayList();
		TarifMappingSelectionStep tmss=
			new TarifMappingSelectionStep(myWiz, this.tarification);
		mySteps.add(tmss);

		myWiz.setUserObject("node", localisationInBaseTree);
		myWiz.setUserObject("title", title);
		myWiz.setUserObject("description", description);
		myWiz.setUserObject("type", tarifType);

		myWiz.setStepPanels(mySteps);

		//	try to go in steps
		myWiz.goAsNextAsPossible();

		//BC.bc.popupJIFrame(myWiz, 10, 10);
		myWiz.setPreferredSize(new Dimension(450, 400));

		ModalJPanel.warpJInternalFrame(
			myWiz,
			this,
			new Point(50, 50),Resources.modalBgColor);
	}

	void mFileExitActionPerformed() {
		// if you want to add special exit code, insert it in dispose() method
		this.dispose();
	}

	void mFileSaveAsActionPerformed() {
		tarification.getHeader().setDataType(
				TarificationHeader.TYPE_TARIFICATION_MODIFIED);
		FileManagement.saveAs(
			tarification,
			FileManagement.CREATOR_SAVE);
	}

	/**
	 * Tries to quicksave the tarification
	 * if not saved allready will open the file chooser...
	 */
	void mFileSaveActionPerformed() {
	    FileManagement.save(this.tarification, FileManagement.CREATOR_SAVE);
	}
	
	void mFilePublishActionPerformed() {
		TarificationPublishingPanel.showProperties(
				getTarification(),this,new Point(0,0));
	}

	void mFileOpenActionPerformed() {
	    if (tl == null) {
	        tl= new TarifList(this.tarification, this);
	        Point p = this.compactTreeTable.getLocation();
	        InternalFrameDescriptor ifd = new InternalFrameDescriptor(PARAM_TARIF_LIST);
			ifd.setInitialBounds(new Rectangle(
			        p.x,p.y,
			        350, 350
			));
	        BC.bc.popupJIFrame(tl, ifd);
	    }
	    tl.refreshList();
	    tl.show();
	    
	}

	void mOptionsOrderTreesActionPerformed() {
	    // Open the TreeOrderer
	    if (treeOrderer == null) {
	        treeOrderer = new TreeOrderer(this.tarification, true,false);
	        InternalFrameDescriptor ifd = new InternalFrameDescriptor(PARAM_TREE_ORDERER);
			ifd.setInitialBounds(new Rectangle(
			        10,10,
			        treeOrderer.getWidth(), treeOrderer.getHeight()
			));
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
    
    void compactTreeCollapseButtonActionPerformed() {
        this.compExplorer.getSTree().collapseAll();
    }
    
    void compactTreeExpandButtonActionPerformed() {
        this.compExplorer.getSTree().expandAll();
    }
    
    void treesCollapseButtonActionPerformed() {
        // Identify the tree to which we want to refer
        TarificationExplorerTree tet=
            (TarificationExplorerTree) this.strees.get(
                    this.treesTabbedPane.getSelectedIndex());
        tet.collapseAll();
    }
    
    public void treesExpandButtonActionPerformed() {
        // Identify the tree to which we want to refer
        TarificationExplorerTree tet=
            (TarificationExplorerTree) this.strees.get(
                    this.treesTabbedPane.getSelectedIndex());
        tet.expandAll();
    }
    
    /** Set the autoshrink property for CompactExplorer */
    public void compactTreeAutoShowCheckActionPerformed() {
        Boolean b= new Boolean(compactTreeAutoShrinkCheck.isSelected());
        BC.setParameter("autoShrinkView", b);
        // Take actions for Compact Explorer to work properly...
        this.compExplorer.setAutoShrinkView(b.booleanValue());
    }
    
    
    /**
     * The event handler (global on tarification)
     */
	public void eventOccured(NamedEvent e) {
		switch (e.getEventCode()) {
			case NamedEvent.COM_VALUE_CHANGED_TARIFICATION :
				if (compactTreeTable != null)
					compactTreeTable.getTable().repaint();
			break;
			case NamedEvent.WORKSHEET_DROPPED :
				WorkSheetContainer parent = 
					((WorkSheet) e.getSource()).getWscontainer();
				if (parent instanceof WorkSheet) {
					tarifViewer.refresh();
				} 
				if (parent instanceof Tarif) {
					tarifViewer.setTarif((Tarif) parent);
				}
				break;
			case NamedEvent.WORKSHEET_HIERARCHY_CHANGED :
				if ( tarifViewer != null ) 
					tarifViewer.refresh();
				else 
					m_log.error( "tarifViewer is null, but should be refreshed !" );
				break;
			case NamedEvent.TITLE_MODIFIED :
				if (e.getSource() == this.selectedTarif) {
					// currently showned tarif changed name
					// We update every node to which this tarif is attached
					ArrayList pos=
						this.compExplorer.getNodesWithObjects(this.selectedTarif);
					for (int i= 0; i < pos.size(); i++) {
						CompactNode stn= (CompactNode) pos.get(i);
						this.compExplorer.fireTreeNodesChanged(stn);
					}
				}
				break;
			default :
				break;
		}
	}
    
    /**
     * get the in-use tarification
     */
    public Tarification getTarification() {
        return tarification;
    }
    
    /**
     * Overriding super dispose to add some cleaning on close
     */
    public void dispose() {
        // Closing the tree orderer
        if (treeOrderer != null) {
            treeOrderer.dispose();
            treeOrderer = null;
        }
        
        // Closing the tarif list
        if (tl != null) {
            this.tl.dispose();
            this.tl = null;
        }
        
        // Remove tarification listener
        if (this.tarification != null) {
            this.tarification.removeNamedEventListener(this);
        }
        // unregister this viewer
        openedTarification.remove(tarification);
        tarification= null;
        
        // Saving divider positions..
        double[] savedParam = new double[5];
        double[] treeSizes = this.compactTreeTable.getSizes();
        for (int i=0; i<3; i++) {
            savedParam[i] = treeSizes[i];
        }
        double split1W = this.hSplit1.getWidth();
        if (split1W > 0) {
            savedParam[3] = (this.hSplit1.getDividerLocation())/split1W;
        } else {
            savedParam[3] = 0;
        }
        double split2W = this.hSplit2.getWidth();
        if (split2W > 0) {
            savedParam[4] = (this.hSplit2.getDividerLocation())/split2W;
        } else {
            savedParam[4] = 0;
        }
        BC.setParameter(PARAM_DIVIDER_POS, savedParam);
        
        super.dispose();
    }
    
    class ExtraPopup implements TarificationExplorerTree.ExtraPopup {
        
        /**
         * @see com.simpledata.bc.uicomponents.TarificationExplorerTree.ExtraPopup#modifyMe(javax.swing.JPopupMenu, com.simpledata.bc.uicomponents.TarificationExplorerNode)
         */
        public void modifyMe(JPopupMenu jpm, TarificationExplorerNode ten) {
            BCNode bcnode= ten.getBCNode();
            
            if (bcnode.getTree() == bcnode.getTarification().getTreeBase()) {
                ArrayList tarifs= bcnode.getAcceptedTarifTypes();
                
                if (tarifs.size() > 0) {
                    jpm.addSeparator();
                    
                    if (tarifs.size() == 1) {
                        String tarifT= tarifs.get(0).toString();
                        String title=
                            Lang.translate("Create Tarif")+" "
                            + Lang.translate(tarifT);
                        addItem(jpm, bcnode, tarifT, title);
                        
                    } else {
                        JMenu jm= new JMenu(Lang.translate("Create Tarif"));
                        jm.setIcon(Resources.iconNew);
                        
                        // if there is a choice in tarifs
                        Iterator e= tarifs.iterator();
                        while (e.hasNext()) {
                            String tarifT= e.next().toString();
                            String title= Lang.translate(tarifT);
                            addItem(jm, bcnode, tarifT, title);
                        }
                        jpm.add(jm);
                        
                    }
                }
            }
        }
        
        private void addItem(
                Container c,
                final BCNode bcnode,
                final String tarifType,
                String title) {
            JMenuItem jmi= new JMenuItem(title);
            jmi.setIcon(Resources.iconNew);
            jmi.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    startTarifCreationWizard(bcnode, tarifType, null, null);
                }
            });
            
            c.add(jmi);
        }
        
    }

	/**
	 * @see com.simpledata.bc.BC.TarificationModifiers#tarifModifierGetTarifications()
	 */
	public List/*<Tarification>*/ tarifModifierGetTarifications() {
		ArrayList l = new ArrayList();
		l.add(tarification);
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
 * Simple tarif Viewer JLIst
 */
class TarifList extends JInternalFrame implements NamedEventListener {
	
		private static final Logger m_log = Logger.getLogger( TarifList.class ); 
    
    protected TarificationCreator te;
    private JList jList1;
    private JScrollPane jScrollPane1;
    
    private Tarification t;
    
    private Tarif oldTarif= null;
    
    /** Creates new form TarifList */
    public TarifList(Tarification tarification, final TarificationCreator te) {
        super("Tarifs:" + tarification.getTitle(), true, true, true, true);
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        this.te= te;
        
        this.t= tarification;
        
        jScrollPane1= new JScrollPane();
        jList1= new JList();
        
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                jList1ValueChanged();
            }
        });
        
        tarification.addNamedEventListener
        (this,NamedEvent.TARIF_MAPPING_MODIFIED,null);
        
        jScrollPane1.setViewportView(jList1);
        
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        refreshList();
        
        pack();
        
        pack();
        setVisible(true);
    }
    
    public void eventOccured(NamedEvent ne) {
        refreshList();
    }
    
    protected void refreshList() {
        ArrayList v = (ArrayList) t.getAllTarifs().clone();
        v.add(new String("null"));
        jList1.setListData(v.toArray());
        jList1.revalidate();
        jList1.repaint();
    }
    
    void jList1ValueChanged() {
        Tarif tarif= null;
        try {
            tarif= (Tarif) jList1.getSelectedValue();
        } catch (Exception e) {
            m_log.error( "Clicked on null" ); 
        }
        if (oldTarif != null) {
            if (oldTarif.equals(tarif))
                return;
        }
        
        oldTarif= tarif;
        
        if (tarif == null) {
            te.setCurrentWorkSheet(null);
        } else {
            te.setCurrentWorkSheet(tarif.getWorkSheet());
        }
        
    }
}

/**
 * Simple TreeSelectionListener for compactExplorer
 */
class MyCompactSelectionListener implements TreeSelectionListener {
    
    CompactTreeItem lastSelected;
    TarificationCreator owner;
    
    public MyCompactSelectionListener(TarificationCreator tc) {
        owner= tc;
    }
    
    /**
     * EVENTS from the tree
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
            
            WorkSheet ws = cn.contentsGetWorkSheet();
            if (ws != null) {
                this.owner.setCurrentWorkSheet(ws);
            } else {
                // maybe it's a CompactBCNode .. if it is then show up the tree
                Object[] values = cn.contentsGet();
                for (int i = 0; i < values.length; i++) {
                    if (values[i] instanceof BCNode) {
                        owner.showTree(((BCNode) values[i]).getTree());
                        break;	
                    }
                    
                }
            }
            
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
    }
    
}

/*
 * $Log: TarificationCreator.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.161  2004/11/22 16:30:10  jvaucher
 * Ticket # 28: Added the properties menu item for the creator, in which you can set the properties of the tarification.
 * I Also added the propoerties panel into the Publish dialog. Perhaps it's
 * too big, in this case remove it, like before.
 *
 * Revision 1.160  2004/11/17 18:09:22  perki
 * corrected bug #27
 *
 * Revision 1.159  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.158  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.157  2004/11/16 15:17:55  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.156  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.155  2004/11/10 17:35:46  perki
 * Tree ordering is now correct
 *
 * Revision 1.154  2004/11/08 13:45:29  carlito
 * New pairing nodes color policy
 *
 * Revision 1.153  2004/10/20 08:19:39  perki
 * *** empty log message ***
 *
 * Revision 1.152  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.151  2004/10/11 07:49:00  perki
 * Links in Filler
 *
 * Revision 1.150  2004/10/04 13:28:44  perki
 * SoftInfo +
 *
 * Revision 1.149  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.148  2004/09/22 15:46:11  jvaucher
 * Implemented cleaver load/save system
 *
 * Revision 1.147  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.146  2004/09/14 15:30:53  jvaucher
 * Minor locale changes
 *
 * Revision 1.145  2004/09/09 17:27:44  carlito
 * *** empty log message ***
 *
 * Revision 1.144  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 * Revision 1.143  2004/09/09 12:43:08  perki
 * Cleaning
 *
 * Revision 1.142  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.141  2004/09/07 15:50:35  carlito
 * *** empty log message ***
 *
 * Revision 1.140  2004/09/07 10:24:44  perki
 * *** empty log message ***
 *
 * Revision 1.139  2004/09/06 13:23:53  kaspar
 * + InstanceFinder debug tool
 *
 * Revision 1.138  2004/09/06 10:06:09  jvaucher
 * Added the ModalDialogBox toolkit, using a very simple reformed look and feel.
 *
 * Revision 1.137  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.136  2004/09/01 16:22:58  jvaucher
 * - Fee reports. First stable version. But, unable to process worksheets
 * of the first level of the tree.
 *
 * Revision 1.135  2004/08/26 12:10:40  kaspar
 * ! Transformed NullPointerException into ERROR, see trouble ticket
 *   #30
 *
 * Revision 1.134  2004/08/25 13:07:43  kaspar
 * ! Added names to threads.
 *
 * Revision 1.133  2004/08/23 07:40:02  jvaucher
 * Added the fee reports. Some changes should be done:
 * - Review the template FeeReport.jrxml. The length of the fields is
 * to small in some cases.
 * - Maybe some node should be reported.
 * - Should use a new common class for the numbering of the section
 *
 * Revision 1.132  2004/08/17 12:09:27  kaspar
 * ! Refactor: Using interface instead of class as reference type
 *   where possible
 *
 * Revision 1.131  2004/08/17 09:06:23  kaspar
 * ! Changed comment: After testing, the behaviour in case of
 *   error is good and can be accepted
 *
 * Revision 1.130  2004/08/02 15:45:48  perki
 * Repartition viewer on simulator
 *
 * Revision 1.129  2004/08/02 15:24:44  perki
 * Repartition viewer on simulator
 *
 * Revision 1.128  2004/08/02 14:17:11  perki
 * Repartitions on Transactions Youhoucvs commit -m cvs commit -m
 *
 * Revision 1.127  2004/08/02 10:44:51  carlito
 * *** empty log message ***
 *
 * Revision 1.126  2004/08/02 07:59:52  kaspar
 * ! CVS merge
 *
 * Revision 1.125  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.124  2004/07/31 16:45:57  perki
 * Pairing step1
 *
 * Revision 1.123  2004/07/30 15:38:19  perki
 * some changes
 *
 * Revision 1.122  2004/07/30 14:23:56  kaspar
 * + Added RenderContext class that is notified each
 *   time a Template is loaded.
 * + Added TemplateCountRenderContext class for using
 *   this feature for progress bar display.
 * + Added TemplateEventListener architecture to get
 *   notified before and after template compilation.
 * + Added progress bar display.
 *
 * Revision 1.121  2004/07/30 05:50:01  perki
 * Moved all CompactTree classes from uicompnents to uicomponents.compact
 *
 * Revision 1.120  2004/07/29 10:42:20  perki
 * Sliders should be ok now
 *
 * Revision 1.119  2004/07/28 14:52:20  kaspar
 * + Added page number below report
 * + Added a page number default of 1, because that is
 *   how jasper works
 * ! Moved the debug code in the ReportToolbox who is the
 *   permanent home for that code.
 *
 * Revision 1.118  2004/07/28 11:42:28  kaspar
 * ! PieChart test code commented out, since the
 *   corresponding package is not in the CVS.
 *
 * Revision 1.117  2004/07/27 17:56:29  carlito
 * *** empty log message ***
 *
 * Revision 1.116  2004/07/26 20:36:10  kaspar
 * + trRateBySlice subreport that shows for all
 *   RateBySlice Workplaces. First Workplace subreport.
 * + Code comments in a lot of classes. Beautifying, moving
 *   of $Id: TarificationCreator.java,v 1.2 2007/04/02 17:04:22 perki Exp $ tag.
 * + Long promised caching of reports, plus some rudimentary
 *   progress tracking.
 *
 * Revision 1.115  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 * Revision 1.114  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.113  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.112  2004/07/22 13:43:27  kaspar
 * ! Bugfixing. Now the code runs, but never seems to get called.
 *   Dispatchers should be visible now, but they are not.
 *
 * Revision 1.111  2004/07/21 16:50:43  kaspar
 * ! fixed index bug in TarificationReportFactory
 * ! fixed report template to follow conventions
 * ! fixed access of things
 *
 * Revision 1.110  2004/07/21 15:44:44  kaspar
 * + Added production of TableModel from a
 *   subreport hierarchy
 *
 * Revision 1.109  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.108  2004/07/20 16:30:18  perki
 * merging menus
 *
 * Revision 1.106  2004/07/19 20:00:33  carlito
 * *** empty log message ***
 *
 * Revision 1.105  2004/07/19 17:39:08  perki
 * *** empty log message ***
 *
 * Revision 1.104  2004/07/19 16:39:09  kaspar
 * - Good class design for reporting. This is not finished,
 *   it will need another days work. But I think this structure
 *   can stay around.
 *
 * Revision 1.103  2004/07/19 09:36:54  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.102  2004/07/16 12:11:08  kaspar
 * * Added descriptions all over the place
 * * title/description now handled by Title
 * * launches a non modal window with the report
 *
 * Revision 1.101  2004/07/15 12:36:45  kaspar
 * * Added linearized data source from prototype
 * * Added libraries: junit, jasper, itext with licences
 * * Added a copy of the LGPL
 *
 * Revision 1.100  2004/07/15 12:00:54  kaspar
 * * Report Generation Prototype included into HEAD
 * * Generates a .dot file for debug output.
 *
 * Revision 1.99  2004/07/12 17:34:31  perki
 * Mid commiting for new matching system
 *
 * Revision 1.98  2004/07/09 20:25:03  perki
 * Merging UI step 1
 *
 * Revision 1.97  2004/07/09 19:10:25  carlito
 * *** empty log message ***
 *
 * Revision 1.96  2004/07/08 15:49:22  perki
 * User node visibles on trees
 *
 * Revision 1.95  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.94  2004/07/08 12:02:32  kaspar
 * * Documentation changes, Added some debug code into
 *   the main view of the creator
 *
 * Revision 1.93  2004/07/08 09:43:20  perki
 * *** empty log message ***
 *
 * Revision 1.92  2004/07/07 17:27:09  perki
 * *** empty log message ***
 *
 * Revision 1.91  2004/07/07 13:44:18  carlito
 * *** empty log message ***
 *
 * Revision 1.90  2004/07/06 18:02:27  carlito
 * tarif list enhanced
 *
 * Revision 1.89  2004/07/05 06:25:46  perki
 * *** empty log message ***
 *
 * Revision 1.88  2004/07/04 14:54:53  perki
 * *** empty log message ***
 *
 * Revision 1.87  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.86  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.85  2004/06/23 18:38:04  perki
 * *** empty log message ***
 *
 * Revision 1.84  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.83  2004/06/22 11:22:40  perki
 * *** empty log message ***
 *
 * Revision 1.82  2004/06/22 10:56:20  perki
 * Lot of cleaning in CompactNode part1
 *
 * Revision 1.81  2004/06/22 08:59:05  perki
 * Added CompactTree for CompactNode management and first sync with CompactExplorer
 *
 * Revision 1.80  2004/06/21 14:45:06  perki
 * Now BCTrees are stored into a vector
 *
 * Revision 1.79  2004/06/20 16:09:03  perki
 * *** empty log message ***
 *
 * Revision 1.78  2004/06/18 18:25:39  perki
 * *** empty log message ***
 *
 * Revision 1.77  2004/06/16 09:58:28  perki
 * *** empty log message ***
 *
 * Revision 1.76  2004/06/16 07:49:28  perki
 * *** empty log message ***
 *
 * Revision 1.75  2004/05/23 14:08:11  perki
 * *** empty log message ***
 *
 * Revision 1.74  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.73  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.72  2004/05/20 17:05:30  perki
 * One step ahead
 *
 * Revision 1.71  2004/05/20 06:11:17  perki
 * id tagging
 *
 *
 */