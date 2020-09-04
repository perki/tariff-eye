/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 6 sept. 2004
 * $Id: Creator.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.datatools.*;
import com.simpledata.bc.reports.ReportToolbox;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.*;
import com.simpledata.bc.uicomponents.compact.*;
import com.simpledata.bc.uicomponents.conception.wizards.*;
import com.simpledata.bc.uicomponents.tools.*;
import com.simpledata.bc.uitools.*;
import com.simpledata.uitools.stree.STree;

/**
 * This is the abstract class for all tarification creators tools
 */
public abstract class Creator extends JInternalFrame implements
							NamedEventListener, BC.TarificationModifiers {
    
    /** Logger */
    private static Logger m_log = Logger.getLogger( Creator.class );
    
    /** Fields used externaly */
    protected final static int STD_COMPONENT_HEIGHT_MIN = 300;
    protected final static int STD_COMPONENT_HEIGHT = 500;
    

    //private final static String PARAM_CREATOR_WIZ = "Creator:NewTarifWizard:Bounds:";
    
    /** Locale */
    private static final String SAVE_CHANGES_MESSAGE = "Creator:saveChangesMessage";
    private static final String FRAME_TITLE = "Creator:frameTitle";
    private static final String ERROR_FRAME_TITLE = "Creator:errorFrameTitle";
    
    
    /**
     * Static WeakHashMap that does contains actually opened Tarifications
     * Tarification serves as keys, and TarificationCreator as values.
     */
    private static WeakHashMap openedTarification;
    
    /**
     * Counter for opened tarificationTools
     */
    private static Integer tarificationCount;
    
    private final int tarificationCountTag;
    
    private Tarification tarification;

    private WeakReference/*<Tarif>*/ selectedTarifReference;
    
    /** Graphical components */
    protected CompactExplorer compExplorer;
    private TarifViewerConception tarifViewer;
    private TreeTabbedPane treeTabbedPane;
    private CompactTreeButtonPanel compactTreeButtonPanel;
    private TariffList tarifList;
    private TreeOrderer treeOrderer;
    
    private JPanel newTarifControlPanel;

	
    //private JMenuBar menuBar;
    //private JMenu maxDepthMenu;
    //private JPanel centralPanel;
    private JSplitPane hSplit1;
    private JSplitPane hSplit2;
    
    /** AutosaveTask */
    AutosaveTask m_autosaveTask;
    /** Needs the work to be saved */
    boolean needSave = false;
    
	/**
	 * Used by subInstance to open a window
	 */
	protected static Creator openTarification(Tarification t, 
	        String parameterDescriptor) {

	    // tell t to synchronize his visualTreeOrder to the system one
	    t.synchronizeTrees();
	    
	    if (openedTarification == null)
			openedTarification = new WeakHashMap();

		Creator c = null;

		if (openedTarification.containsKey(t)) {
			c = (Creator) openedTarification.get(t);
		}

		if (c  == null) {
		    if (Params.KEY_CREATOR_LIGHT_DESCRIPTION_MODIFIER.equals( 
		        parameterDescriptor)) {
		        // We want a light
		        c = new CreatorLight(t);
		    } else {
		        // We want a gold
		        parameterDescriptor = Params.KEY_CREATOR_GOLD_DESCRIPTION_MODIFIER;
		        c = new CreatorGold(t);
		    }

		    openedTarification.put(t, c);
			
		}

		String paramKey = Params.KEY_CREATOR_BOUNDS + parameterDescriptor;
		
		InternalFrameDescriptor ifd = new InternalFrameDescriptor(paramKey);
//		ifd.setInitialBounds(new Rectangle(
//		        100,1,
//		        c.getWidth(), c.getHeight()
//		));
		
		// DEFO
		//ifd.setInitialBounds((Rectangle)Params.getDefaultValue(paramKey));
		
		BC.bc.popupJIFrame(c, ifd);
		
		c.updateFrameTitle();
		
		c.needSave = false; // avoid to handle load changes
		m_log.debug ("Changes reset");
		return c;
		
	}

	/** Update the frame title */
	private void updateFrameTitle() {
		if (getTarification() == null) {
			setTitle(Lang.translate(ERROR_FRAME_TITLE));
		} else {
			TarificationHeader th = getTarification().getHeader();
			StringBuffer frameTitle = 
				new StringBuffer(Lang.translate(FRAME_TITLE)+": ");
			File loadingLocation = th.myLoadingLocation();
			if (loadingLocation != null) {
				frameTitle.append(loadingLocation.getName());
			} else {
				frameTitle.append(
						Lang.translate("Untitled")+" "+tarificationCountTag);
			}
			if (needSave) 
				frameTitle.append("*");
			// Upgrading frame title
			setTitle(frameTitle.toString());
			
			setFrameIcon(getTarification().getHeader().getIcon());
		}
		
	}
	
	/**
	 * @return the next tool number
	 * i.e the number that will be displayed in window name
	 */
	private static int getNextTarificationCount() {
		if (tarificationCount == null) {
			tarificationCount = new Integer(1);
		} else {
			int nextCount = tarificationCount.intValue() + 1;
			tarificationCount = new Integer(nextCount);
		}
		return tarificationCount.intValue();
		//return openedTarification.size();
	}
    
    
    /**
     * Use openTarification(Tarification t) to open a Tarification
     * @see #openTarification(Tarification t)
     */
    protected Creator(Tarification t) {
        super(Lang.translate("Tarification creation tool"), 
                true, true, true, true);
        
        tarification= t;
        
        needSave = false;
        
        selectedTarifReference = new WeakReference/*<Tarif>*/(null);
        
        tarificationCountTag = getNextTarificationCount();
        
        this.setFrameIcon(Resources.iconTools);
        
        if (t == null) {
            // Throw an exception into the log and silently fail. 
            // Is this the best way of reacting ? 
            m_log.fatal(
            "Tried to create a Tarification Creator from null Tarification");
            
            // TODO add a neat alert here ....
            
            this.dispose();
        }
        
        this.tarification.addNamedEventListener(this);

        buildComponents();
        
        buildComplexComponents();
        
        buildUI();
        
        //buildSpecialComponents();
        
        buildMenuBar();
        
        pack();
        
        updateProportions();
        
//        initStdComponents();
//        initCplxComponents();
        
        // set selection to null
        setCurrentWorkSheet(null);
        
        expandTreeWhenShowing();        
        
        
        // Modifs listener
        
		t.addNamedEventListener(new NamedEventListener(){

			public void eventOccured(NamedEvent e) {
				if (e.getEventCode() != NamedEvent.COM_VALUE_CHANGED_TARIFICATION)
					needSave();
			}
			
		});
		
    }
    
    /**
     * This is where we build all components that are going to be used in the
     * instances of this class 
     * such as CompactTree, etc...
     */
    private void buildComponents() {
        
        compExplorer = new CompactExplorer(this.tarification);
        compactTreeButtonPanel = new CompactTreeButtonPanel(this);
        
        STree st= compExplorer.getSTree();
		st.setBorder(new EtchedBorder());

		st.addTreeSelectionListener(new CompactSelectionListener(this));
        
		buildNewTarifControlPanel();
		//buildCurrentTarifControlPanel();
		
		tarifViewer= new TarifViewerConception(this);
		tarifViewer.setTarif(null);
		tarifViewer.refresh();        
        
        // TODO fine tune sizes
        treeTabbedPane = new TreeTabbedPane(this, null, null, null);
        
        
        
    }
    

    
    /** Launch the graphical build of left component */
    protected abstract void buildComplexComponents();
    
    /** We build the control panel for new tarifs
     * For now it will just contain a button for launching new tarif wizard */
    private void buildNewTarifControlPanel() {
        newTarifControlPanel = new JPanel(new BorderLayout());
        
        SButtonIcon newTarifButton = new SButtonIcon(Resources.iconNew);
        
        BC.langManager.registerTooltip(newTarifButton, "Create new tarif");
		newTarifButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				newTarifButtonActionPerformed();
			}
		});

		newTarifControlPanel.add(newTarifButton, BorderLayout.CENTER);
    }
    
    private void buildUI() {
        hSplit1= new JSplitPane();
        hSplit2= new JSplitPane();
        hSplit2.setOneTouchExpandable(true);
 
        
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setName(Lang.translate("Tarification Creation Tool"));

		hSplit1.setBorder(null);
        
		hSplit1.setLeftComponent(getLeftComponent());
		
		hSplit2.setBorder(null);
		
		hSplit2.setLeftComponent(getCentralComponent());
		
		hSplit2.setRightComponent(treeTabbedPane);

		hSplit1.setRightComponent(hSplit2);
		
		hSplit1.setResizeWeight(0.333);
		hSplit2.setResizeWeight(0.5);

		getContentPane().add(hSplit1, java.awt.BorderLayout.CENTER);		
    }
    
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(buildFileMenu());
        
        menuBar.add(buildReportsMenu());

        menuBar.add(buildOptionsMenu());
        
        menuBar.add(new MaxDepthMenu(this));
        
		buildMenuBarPlus(menuBar);
		        
		setJMenuBar(menuBar);
    }
    
    /** override this method to augment the building of the MenuBar **/
    protected void buildMenuBarPlus(JMenuBar menuBar) {};
    
    protected JMenu buildFileMenu() {
        JMenu mFile = new JMenu();
        
        //private JMenuItem mFileNew;
        JMenuItem mFileOpen = new JMenuItem();
        JMenuItem mFileSave = new JMenuItem();
        JMenuItem mFileSaveAs = new JMenuItem();
        JMenuItem mFilePublish = new JMenuItem();
        JMenuItem mFileProperties = new JMenuItem();
        JMenuItem creationTagMenu = new JMenuItem();
        JMenuItem mFileExit = new JMenuItem();
        
		BC.langManager.register(mFile,"File");
		
		if (BC.isSimple()) {
			JMenuItem jmi = new JMenuItem("Debug Print Tarification");
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					m_log.info("XXX\n"+getTarification().toString());
				}
			});
			mFile.add(jmi);
			
	
			BC.langManager.register(mFileOpen, "Open Tarif List");
			mFileOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					mFileOpenActionPerformed();
				}
			});
			mFile.add(mFileOpen);
		}



		BC.langManager.register(mFileExit,"Close");
		mFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFileExitActionPerformed();
			}
		});
		mFile.add(mFileExit);
		
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

		BC.langManager.register(mFileSaveAs, "Save As");
		mFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFileSaveAsActionPerformed();
			}
		});
		mFile.add(mFileSaveAs);
		
		BC.langManager.register(mFilePublish, "Publish...");
		mFilePublish.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				mFilePublishActionPerformed();
			}
		});
		mFile.add(mFilePublish);
		
		BC.langManager.register(mFileProperties, "Tarification properties...");
		mFileProperties.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				mFilePropertiesActionPerformed();
			}});
		mFile.add(mFileProperties);
		
		// Devel options
		if (BC.isSimple()) {
			mFile.add(new JSeparator());
			
			JMenuItem jmi = new JMenuItem("Debug Print Tarification");
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					m_log.info("XXX\n"+getTarification().toString());
				}
			});
			mFile.add(jmi);
			
		    BC.langManager.register(creationTagMenu,"Creation tag");
		    creationTagMenu.addActionListener(new ActionListener(){
		        
		        public void actionPerformed(ActionEvent e) {
		            JPanel jp = new JPanel(new BorderLayout());
		            
		            jp.add(new JLabel("Simpledatacode : "
		                    +Tarification.publishingCreationTag),
		                    BorderLayout.NORTH);
		            
		            JTextFieldBC jtf = new JTextFieldBC() {
		                
		                public void stopEditing() {
		                    tarification.changeCreationTag(getText());
		                }
		                
		                public void startEditing() {
		                    
		                    
		                }};
		                jtf.setText(tarification.getCreationTag());
		                
		                jp.add(jtf,BorderLayout.CENTER);
		                
		                ModalJPanel.createSimpleModalJInternalFrame(jp,
		                        getCentralComponent(),new Point(0,0),true,
		                        Resources.iconEdit,Resources.modalBgColor);
		                
		        }});
		    mFile.add(creationTagMenu);
		}
		
		
		return mFile;
    }
    
    private JMenu buildReportsMenu() {
    	JMenu result = new JMenu();
    	BC.langManager.register(result, "Reports");
    	
    	JMenuItem mReportsTarifSummary = new JMenuItem(); 
        
    	BC.langManager.register(mReportsTarifSummary, "Tarification Summary...");
		mReportsTarifSummary.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doTarifSummary();
			}
		} );
		result.add(mReportsTarifSummary);
    	
    	return result;
    }
    
    private JMenu buildOptionsMenu() {
        JMenu mOptions = new JMenu();
        JMenuItem mOptionsOrderTrees = new JMenuItem();
        
		BC.langManager.register(mOptions, "Options");
		
		BC.langManager.register(mOptionsOrderTrees, "Order Trees");
		mOptionsOrderTrees.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent evt) {
				mOptionsOrderTreesActionPerformed();
			}
		});
		
		mOptions.add(mOptionsOrderTrees);
        
        return mOptions;
    }
    
    /** 
     * Waiting for the compactExplorer tree to show, to perform an expand on it
     */
    private void expandTreeWhenShowing() {
        ActionListener treeVisibilityListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (compExplorer.getSTree().isShowing()) {
                    // Launch expand on the tree
                    compExplorer.expandTree();
                    
                    Timer tim = (Timer)e.getSource();
                    tim.stop();
                }
            }  
        };
        
        Timer tim = new Timer(50, treeVisibilityListener);
        tim.start();
    }
    

    
    /**
     * *****************************************************************************
     * *****************************************************************************
     * 					GETTERS AND SETTERS
     * *****************************************************************************
     * *****************************************************************************
     */
    
    /**
     * Get current tarification
     * @return the current tarification
     */
    public Tarification getTarification() {
        return tarification;
    }
    
    public CompactExplorer compactExplorer() {
        return compExplorer;
    }
    
    public TarifViewer tarifViewer() {
        return tarifViewer;
    }
    
    
    
//    public CompactTreeButtonPanel getCompactTreeButtonPanel() {
//        return compactTreeButtonPanel;
//    }
    
    /** set currently selected tarif */
    private void selectedTarif(Tarif t) {
        selectedTarifReference = new WeakReference/*<Tarif>*/(t);
    }
    
    /** get currently selected tarif */
    protected Tarif selectedTarif() {
        return (Tarif)selectedTarifReference.get();
    }

//    /** Return the center component*/
//    protected JPanel centralPanel() {
//        return centralPanel;
//    }
    
    /**
     * Returns the button panel used by the compact tree
     */
    protected JPanel compactTreeButtonPanel() {
        return compactTreeButtonPanel;
    }
    
    /** Returns the new Tarif ControlPanel */
    protected JPanel newTarifControlPanel() {
        return newTarifControlPanel;
    }
    
//    /** Returns the currently edited tarif ControlPanel */
//    protected JPanel currentTarifControlPanel() {
//        return currentTarifControlPanel;
//    }
    
	/**
	 * select Tarif in all strees and show workSheet in TarifViewer
	 */
	public void setCurrentWorkSheet(WorkSheet ws) {
		tarifViewer.setWorkSheet(ws);
		
		Tarif t = null;
		if (ws != null)
			t = ws.getTarif();
		
		if (t == selectedTarif()) return;

		// Events attach and detach management
		
		// First remove TarificationCreator from old tarif listeners list
		// if it exists
		if (selectedTarif() != null) {
			selectedTarif().removeNamedEventListener(this);
		}

		selectedTarif(t);
		
		tarifViewer.updateButtonsStatus();

		// Attach TarificationCreator as listener for the selected tarif
		if (selectedTarif() != null) {
			selectedTarif().addNamedEventListener(this);
		}

		treeTabbedPane.setCurrentWorkSheet(ws);

		// Show tarif on Compact Explorer
		this.compExplorer.expandNodesWithObject(t);
	}
    
	/** Modifs occurs. Enable autosave and dispose confirm dialog */
	public void needSave() {
        needSave = true;
        m_autosaveTask.start();
        updateFrameTitle();
    }
    
	/** Save occurs. Disable autosave and dispose confirm dialog */
    private void hasBeenSaved() {
    	needSave = false;
    	m_autosaveTask.stop();
    	updateFrameTitle();
    }
    
    /**
     * Called whenever an event is received by the creator
     */
    public void eventOccured(NamedEvent e) {
    	switch (e.getEventCode()) {
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
            if ( tarifViewer != null ) {
            	tarifViewer.refresh();
                
            } else 
                m_log.error( "tarifViewer is null, but should be refreshed !" );
        break;
        case NamedEvent.TITLE_MODIFIED :
        		
            	Tarif atWorkT = tarifViewer.getTarifAtWork();
        		
        if (e.getSource() == atWorkT) {
            // currently showned tarif changed name
            // We update every node to which this tarif is attached
            ArrayList pos=
                this.compExplorer.getNodesWithObjects(atWorkT);
            for (int i= 0; i < pos.size(); i++) {
                CompactNode stn= (CompactNode) pos.get(i);
                this.compExplorer.fireTreeNodesChanged(stn);
            }
        }
        break;
        default :
            break;
        }
        
        performExtraEventTreatment(e);
    }
    
    /** 
     * This enables the Creator to save params for subInstances in different
     * locations (i.e. proportions and bounds can be different for the light and the 
     * gold version)
     * @return for example 'LIGHT' or 'GOLD' ;)
     */
    protected abstract String getParameterDescriptionModifier();
    
    /**
     * If you should perform extra treatment over some of your elements
     * @param e
     */
    protected abstract void performExtraEventTreatment(NamedEvent e);
    
    /** 
     * 
     * @return
     */
    protected abstract void 
    		performExtraCompactTreeSelectionEventTreatment(CompactNode node);
    
    /**
     * Returns the left component
     */
    protected abstract Component getLeftComponent();

    /**
     * Returns the central component
     */    
    protected abstract Component getCentralComponent();
    
    /** 
     * Tells the leftComponent to update its proportions
     * i.e the CompactTreeTable sets sizes beetween its components..
     */
    protected abstract void updateLeftComponentProportions();
    
    /**
     * This is where left component proportions should be saved
     */
    protected abstract void saveComponentsProportions();
    
    /**
     * @return true if you want the tarif viewer to show root options
     */
    public abstract boolean souldTarifViewerShowsRootOption();
        
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
		TarifNameAndTypeSelectionStep tnatss=
			new TarifNameAndTypeSelectionStep(myWiz, this);
		mySteps.add(tnatss);

		myWiz.setUserObject("node", localisationInBaseTree);
		myWiz.setUserObject("title", title);
		myWiz.setUserObject("description", description);
		myWiz.setUserObject("type", tarifType);

		myWiz.setStepPanels(mySteps);

		//	try to go in steps
		myWiz.goAsNextAsPossible();

		//BC.bc.popupJIFrame(myWiz, 10, 10);
		myWiz.setPreferredSize(new Dimension(450, 400));

//		String paramKey = PARAM_CREATOR_WIZ + getParameterDescriptionModifier();
//		
//		InternalFrameDescriptor ifd = new InternalFrameDescriptor(paramKey);
//		ifd.setInitialBounds(new Rectangle(
//		        50,50,
//		        450, 500
//		));
//		BC.bc.popupJIFrame(myWiz, ifd);
		
		ModalJPanel.warpJInternalFrame(
			myWiz,
			this,
			new Point(50, 50),Resources.modalBgColor);
    }
    
    /** Show the correct tree in the treeTabbedPane */
    protected void showTree(BCTree tree) {
        if (treeTabbedPane != null) {
            treeTabbedPane.showTree(tree);
        }
    }
    
    /**
     * Update the dividers positions according to a stored value...
     */
    private void updateProportions() {
        
        String paramKey = Params.KEY_CREATOR_DIVIDER_POS + 
        		getParameterDescriptionModifier();
        
        double[] savedProp = (double[])BC.getParameter(paramKey, double[].class );
        
        if (savedProp.length != 2) {
            savedProp = (double[])BC.forceDefaultParam(paramKey);
//        if (savedProp == null) {
//            savedProp = new double[2];
//            savedProp[0] = 0.28;
//            savedProp[1] = 0.61;
//        }
        }
        
        final double[] proportions = savedProp;
        final Runnable r0 = new Runnable() {
            public void run() {
                hSplit1.setDividerLocation(proportions[0]);
            }
        };
        
        final Runnable r1 = new Runnable() {
            
            public void run() {
                hSplit2.setDividerLocation(proportions[1]);
            }
            
        };
        
        final Runnable r2 = new Runnable() {
            
            public void run() {
                updateLeftComponentProportions();
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
    }
    
    
    
    /**
     * ******************************************************************
     * ******************************************************************
     * ACTIONS PERFORMED ************************************************
     * ******************************************************************
     * ******************************************************************
     */
    private void mFileExitActionPerformed() {
		// if you want to add special exit code, insert it in dispose() method
		this.dispose();
	}

	private void mFileSaveAsActionPerformed() {
		tarification.getHeader().setDataType(
				TarificationHeader.TYPE_TARIFICATION_MODIFIED);
		if (FileManagement.saveAs(tarification, FileManagement.CREATOR_SAVE))
			hasBeenSaved();
	}

	/**
	 * Tries to quicksave the tarification
	 * if not saved allready will open the file chooser...
	 */
	private void mFileSaveActionPerformed() {
	    if (FileManagement.save(this.tarification, FileManagement.CREATOR_SAVE))
	    	hasBeenSaved();
	}
	
	private void mFilePublishActionPerformed() {
		TarificationPublishingPanel.showPublishDialog(getTarification());
	}
	
	private void mFilePropertiesActionPerformed() {
		TarificationPublishingPanel.showProperties(
				getTarification(),this,new Point(0,0));
	}

	private void mFileOpenActionPerformed() {
	    if (tarifList == null) {
	        tarifList= new TariffList(tarification, this);
	        Point p = this.getLeftComponent().getLocation();
	        InternalFrameDescriptor ifd = new InternalFrameDescriptor(
	                Params.KEY_TARIF_LIST_BOUNDS);
	        Rectangle rec = (Rectangle)BC.getParameter(Params.KEY_TARIF_LIST_BOUNDS,
	                Rectangle.class);
	        rec.setLocation(p.x, p.y);
			ifd.setInitialBounds(rec);
	        BC.bc.popupJIFrame(tarifList, ifd);
	    }
	    tarifList.refreshList();
	    tarifList.show();
	}

	private void mOptionsOrderTreesActionPerformed() {
	    // Open the TreeOrderer
	    if (treeOrderer == null) {
	        treeOrderer = new TreeOrderer(this.tarification, true,false);
	        InternalFrameDescriptor ifd = new InternalFrameDescriptor(
	                Params.KEY_TREE_ORDERER_BOUNDS);
//			ifd.setInitialBounds(
//			        (Rectangle)BC.forceDefaultParam(Params.PARAM_TREE_ORDERER_BOUNDS));
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
    
	/** 
	 * Starts new tarif creation wizard
	 */
	private void newTarifButtonActionPerformed() {
		startTarifCreationWizard(null, null, null, null);
	}
	

	
	/** clean the UI after a tarif delete **/
	protected void cleanUI() {
		this.setCurrentWorkSheet(null);
		if (tarifList != null)
		    tarifList.refreshList();
		this.compExplorer.refreshStructure();
	}
	
	/**
	 * Show a Tarification report to the user. 
	 */
	void doTarifSummary() {
		ReportToolbox.displayTarificationReport( this, compExplorer );
	}
    
    /**
     * Overriding super dispose to add some cleaning on close
     */
    public void dispose() {
        // is this tarification modified ?
        if (needSave) {
            String[] params = new String[1];
            params[0] = tarification.getTitle();
            String res = Lang.translate(SAVE_CHANGES_MESSAGE, params);
            
            if (ModalDialogBox.questionUncancelable(BC.bc.getMajorComponent(), res)
                    ==	ModalDialogBox.ANS_YES)
                FileManagement.save(this.tarification, FileManagement.CREATOR_SAVE);
        }
    	// Ends the autosave task
        m_autosaveTask.cancel();
        
        // Closing the tree orderer
        if (treeOrderer != null) {
            treeOrderer.dispose();
            treeOrderer = null;
        }
        
        // Closing the tarif list
        if (tarifList != null) {
            tarifList.dispose();
            tarifList = null;
        }
        
        // Remove tarification listener
        if (getTarification() != null) {
            getTarification().removeNamedEventListener(this);
        }
        // unregister this viewer
        openedTarification.remove(tarification);
        tarification = null;
        
        // Saving divider positions..
        
        String paramKey = Params.KEY_CREATOR_DIVIDER_POS + 
        		getParameterDescriptionModifier();
        
        BC.setParameter(paramKey, mainSplitsProportions());
        
        // Order the subInstance to save components proportions
        
        saveComponentsProportions();
        
        super.dispose();
    }
	
    /** Returns the proportions for hSplpit1 ans hSplit2 */
    private double[] mainSplitsProportions() {
        double[] res = new double[2];

        double split1W = hSplit1.getWidth();
        if (split1W > 0) {
            res[0] = (hSplit1.getDividerLocation())/split1W;
        } else {
            res[0] = 0;
        }
        double split2W = hSplit2.getWidth();
        if (split2W > 0) {
            res[1] = (hSplit2.getDividerLocation())/split2W;
        } else {
            res[1] = 0;
        }
        
        return res;
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
 * This is the menu used to modify depth of view within the creator
 */
class MaxDepthMenu extends JMenu {

    private Creator owner;
    
    //  value for the JcomboBox
    private final static int[] vals= { 1, 2, 3, 4, 5, 10 };

    public MaxDepthMenu(Creator c) {
        super();
        owner = c;
        BC.langManager.register(this, "Depth Of View");
        
        addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e) {
                buildChildMenus();
            }

            public void menuDeselected(MenuEvent e) {
                removeAll();
            }

            public void menuCanceled(MenuEvent e) { }
            
        });
    }
    
    private void buildChildMenus() {
        int maxDepth = TarifViewer.getMaxDepth().intValue();
        
        for (int i= 0; i < vals.length; i++) {
            final int depth = vals[i];
            final JCheckBoxMenuItem jcbm = new JCheckBoxMenuItem(depth+"");
            if (depth == maxDepth) {
                jcbm.setSelected(true);
            } else {
                jcbm.setSelected(false);
                jcbm.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (owner == null) return;                        
                        owner.tarifViewer().setMaxDepth(depth);
                    }
                });
            }
            this.add(jcbm);
            
        }
    }
    
}


/**
 * This is the panel containing all the buttons to interact with the 
 * CompactTree of the Creator
 */
class CompactTreeButtonPanel extends JPanel {

    private Creator owner;
    
    /** Shared graphical basic components */
	private JCheckBox compactTreeAutoShrinkCheck;
    
    public CompactTreeButtonPanel(Creator owner) {
        super();
        this.owner = owner;
        
        initComponents();
    }

    /**
     * Init Graphical components
     */
    private void initComponents() {
        SButtonIcon compactTreeCollapseButton = new SButtonIcon(Resources.iconCollapse);
        SButtonIcon compactTreeExpandButton = new SButtonIcon(Resources.iconExpand);
        compactTreeAutoShrinkCheck= new JCheckBox();
        
        setLayout(new GridBagLayout());
        
        setBorder(new EtchedBorder());
        
		BC.langManager.registerTooltip(compactTreeExpandButton, "Expand");
		compactTreeExpandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compactTreeExpandButtonActionPerformed();
			}
		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets= new Insets(2, 1, 2, 0);
		constraints.anchor= java.awt.GridBagConstraints.WEST;

		add(compactTreeExpandButton,constraints);

		BC.langManager.registerTooltip(compactTreeCollapseButton, "Collapse");
		compactTreeCollapseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compactTreeCollapseButtonActionPerformed();
			}
		});
		
		constraints.gridx++;
		
		add(compactTreeCollapseButton,constraints);
		
		constraints.gridx++;
		constraints.fill= GridBagConstraints.HORIZONTAL;
		constraints.weightx= 1.0;
		
		add(new JLabel(), constraints);

		BC.langManager.register(compactTreeAutoShrinkCheck, "Auto Shrink View");
		compactTreeAutoShrinkCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				compactTreeAutoShowCheckActionPerformed();
			}
		});
		
		constraints.gridx++;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.insets= new Insets(2, 3, 2, 6);
		constraints.anchor= GridBagConstraints.EAST;
		
		add(compactTreeAutoShrinkCheck,constraints);
    }
    
    /** Expand the CompactExplorer */
    private void compactTreeExpandButtonActionPerformed() {
        owner.compactExplorer().getSTree().expandAll();
    }
    
    /** Collapse the CompactExplorer */
    private void compactTreeCollapseButtonActionPerformed() {
        owner.compactExplorer().getSTree().collapseAll();
    }
    
    /** Set the autoshrink property for CompactExplorer */
    private void compactTreeAutoShowCheckActionPerformed() {
        Boolean b= new Boolean(compactTreeAutoShrinkCheck.isSelected());
        BC.setParameter("autoShrinkView", b);
        // Take actions for Compact Explorer to work properly...
        owner.compactExplorer().setAutoShrinkView(b.booleanValue());
    }
    

    
}


/**
 * Simple tarif Viewer JLIst
 */
class TariffList extends JInternalFrame implements NamedEventListener {
	
	private static final Logger m_log = Logger.getLogger( TariffList.class ); 
    
    protected Creator te;
    private JList jList1;
    private JScrollPane jScrollPane1;
    
    private Tarification t;
    
    private Tarif oldTarif= null;
    
    /** Creates new form TarifList */
    public TariffList(Tarification tarification, final Creator te) {
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
class CompactSelectionListener implements TreeSelectionListener {
    
    // TODO maybe replace by a WeakReference
    CompactTreeItem lastSelected;
    Creator owner;
    
    public CompactSelectionListener(Creator tc) {
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
            
            owner.performExtraCompactTreeSelectionEventTreatment(cn);
            
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

/*
 * $Log: Creator.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.26  2004/11/22 16:30:10  jvaucher
 * Ticket # 28: Added the properties menu item for the creator, in which you can set the properties of the tarification.
 * I Also added the propoerties panel into the Publish dialog. Perhaps it's
 * too big, in this case remove it, like before.
 *
 * Revision 1.25  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.24  2004/11/16 16:20:53  jvaucher
 * Ticket # 53 : Bad call to needSave() into creator. Corrected in the listener. creator.java
 *
 * Revision 1.23  2004/11/16 15:17:55  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.22  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.21  2004/11/10 17:35:46  perki
 * Tree ordering is now correct
 *
 * Revision 1.20  2004/11/09 12:48:26  perki
 * *** empty log message ***
 *
 * Revision 1.19  2004/11/08 16:42:35  jvaucher
 * - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 * Revision 1.18  2004/10/16 11:41:26  jvaucher
 * - Minor changes (Help, reports, UI)
 *
 * Revision 1.17  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.16  2004/10/04 15:49:19  carlito
 * *** empty log message ***
 *
 * Revision 1.15  2004/10/04 13:28:44  perki
 * SoftInfo +
 *
 * Revision 1.14  2004/10/04 10:10:31  jvaucher
 * - Minor changes in FileManagement, allowing to choose the dialogType
 * - Helper skeleton
 * - Improved rendering of Tarification Report
 * - Dispatcher bound can yet disable the upper bound
 *
 * Revision 1.13  2004/10/01 10:02:16  carlito
 * Infinite sign added to resources
 *
 * Revision 1.12  2004/09/28 09:41:51  perki
 * Clear action added to CreatorGold
 *
 * Revision 1.11  2004/09/23 10:41:04  carlito
 * Tarifications icons used for the frames of Creator and Simulator
 *
 * Revision 1.10  2004/09/22 17:21:49  carlito
 * confirm dialogs with icons...
Texts updated...
 *
 * Revision 1.9  2004/09/22 15:46:11  jvaucher
 * Implemented cleaver load/save system
 *
 * Revision 1.8  2004/09/22 15:39:55  carlito
 * Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 * Revision 1.7  2004/09/16 09:55:50  jvaucher
 * Introduced the total volume calculation for the transactions rateBySlice workplace.
 *
 * Revision 1.6  2004/09/15 11:04:22  jvaucher
 * Added a textual message for the event code (method eventName())
 * Added the modification dialog box. But some changes are still not observable. See tickets for details
 *
 * Revision 1.5  2004/09/14 12:07:26  carlito
 * commit de protection
 *
 * Revision 1.4  2004/09/13 17:33:42  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/09/13 15:27:31  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.1  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 */