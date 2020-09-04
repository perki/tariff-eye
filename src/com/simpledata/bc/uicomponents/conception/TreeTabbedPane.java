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
 * $Id: TreeTabbedPane.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.*;
import com.simpledata.bc.uitools.*;
import com.simpledata.bc.uitools.ComboTabbedPane;

/**
 * This is the small panel containing a JTabbedPane of
 * all trees used as dimensions in the creator
 */
public class TreeTabbedPane extends JPanel {
    
    /** Logger */
    private static Logger m_log = Logger.getLogger( TreeTabbedPane.class );
    
    private final static int STD_WIDTH = 150;
    private final static int STD_WIDTH_MIN = 75;
    
    /** Memory for opened TarificationExplorerTrees */
    private ArrayList/*<STree>*/ strees;
    
    private WeakReference/*<Tarif>*/ selectedTarifReference;
    
    private Creator owner;
    
    /** Graphical components */
    private ComboTabbedPane tabbedPane;
    private TarificationExplorerTree.ExtraPopup extraPopup;
    
    private JLabel blankLabel;
    private SButtonIcon treesCollapseButton;
    private SButtonIcon treesExpandButton;
    
    /**
     * Constructor
     * @param strees the STrees that will be displayed in each Pane
     */
    public TreeTabbedPane(Creator owner, Dimension minSize, Dimension prefSize, Dimension maxSize) {
        if (owner != null) {
            strees = new ArrayList/*<STree>*/();
            extraPopup = new ExtraPopup(owner);
        } else {
            m_log.fatal("owner must not be null");
        }
        
        this.owner = owner;
        
        // Size the component
        setComponentSizes(minSize, prefSize, maxSize);
        
        // Init shared graphical components
        initGraphicalComponents();
        
        // Create content
        buildUI();
        
        // Fill tabs
        fillTabbedPane(owner.getTarification());
        
    }
    
    /**
     * Set correct sizes for the component
     */
    private void setComponentSizes(Dimension minSize, Dimension prefSize, Dimension maxSize) {
        Dimension minS = minSize;
        Dimension prefS = prefSize;

        if (prefSize == null) {
            prefS = new Dimension(STD_WIDTH, Creator.STD_COMPONENT_HEIGHT);
        } 

        if (minSize == null) {
            minS = new Dimension(STD_WIDTH_MIN, Creator.STD_COMPONENT_HEIGHT_MIN);
        } 
        
        prefS.width = Math.max(minS.width, prefS.width);
        prefS.height = Math.max(minS.height, prefS.height);
        setMinimumSize(minS);
        
        if (maxSize != null) {
            prefS.width = Math.min(maxSize.width, prefS.width);
            prefS.height = Math.min(maxSize.height, prefS.height);
            setMaximumSize(maxSize);
        }
        setPreferredSize(prefS);
    }
    
    private void initGraphicalComponents() {
        blankLabel = new JLabel();
        
        treesExpandButton= new SButtonIcon(Resources.iconExpand);
        treesCollapseButton= new SButtonIcon(Resources.iconCollapse);
        tabbedPane= new ComboTabbedPane();
    }
    
    /**
     * Create and set the layout for all graphical components
     */
    private void buildUI() {
        setLayout(new BorderLayout());
        
        add(buildActionButtonsPanel(), BorderLayout.NORTH);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    
    private JPanel buildActionButtonsPanel() {
        JPanel treesActionButtonsPanel;
        
        
        treesActionButtonsPanel= new JPanel();
        
        setLayout(new BorderLayout());
        
        setBorder(new EtchedBorder());
        
        treesActionButtonsPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(0,5,0,5);
        constraints.anchor = GridBagConstraints.EAST;
        
        treesActionButtonsPanel.add(owner.newTarifControlPanel(), constraints);
        
        constraints.gridx++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        constraints.insets = new Insets(0,0,0,0);
        
        treesActionButtonsPanel.add(blankLabel, constraints);
        
        
        
        BC.langManager.registerTooltip(treesExpandButton, "Expand");
        
        constraints.gridx++;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.insets = new Insets(2,1,2,1);
        
        treesActionButtonsPanel.add(treesExpandButton, constraints);
        
        
        BC.langManager.registerTooltip(treesCollapseButton, "Collapse");
        
        constraints.gridx++;
        constraints.insets = new Insets(2,0,2,1);
        
        treesActionButtonsPanel.add(treesCollapseButton, constraints);
        
        // Add listeners
        treesExpandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                treesExpandButtonActionPerformed();
            }
        });
        
        treesCollapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                treesCollapseButtonActionPerformed();
            }
        });
        
        
        return treesActionButtonsPanel;
    }

    /** 
     * Filling the tabbed pane with the correct representations of BCTrees according
     * to the Tarification model parameter
     */
    private void fillTabbedPane(Tarification model) {
        
        // Filling in the tabbedPane with the TarificationExplorerTrees
		// Trees
		BCTree[] trees= model.getMyTrees();

		if (trees.length == 0) {
		    treesCollapseButton.setEnabled(false);
		    treesExpandButton.setEnabled(false);
		}
		
		for (int i= 0; i < trees.length; i++) {
			TarificationExplorerTree tet=
				new TarificationExplorerTree(trees[i], true, true, extraPopup);

			// Ajout des STrees dans le tabbedPane correspondant
			JScrollPane jsc= new JScrollPane();
			jsc.setBorder(new LineBorder(null,0));
			jsc.setViewportView(tet);
			ImageIcon ico= tet.getRootTEN().getLeafIcon();
			tabbedPane.addTab(
				Lang.translate(trees[i].getTitle()),
				ico,
				jsc);
			strees.add(tet);
		}
        
    }
    
    /**
     * Set the desired height for Buttons panel
     * Attention : won't go below its own minimal height
     * @param height the new height
     */
    protected void setTreesButtonPanelHeight(int height) {
        blankLabel.setPreferredSize(new Dimension(3,height));
    }
    
    
	/** Show the correct tree in the treeTabbedPane */
	protected void showTree(BCTree tree) {
	    Iterator e = strees.iterator();
		for (int i = 0; e.hasNext(); i++) {
			if (tree == ((TarificationExplorerTree) e.next()).getTree())
			tabbedPane.setSelectedIndex(i);
		}
	}
    
	/**
	 * Update the display according to the new selected WorkSheet
	 * In this case we are only interested by tarifs...
	 */
    protected void setCurrentWorkSheet(WorkSheet ws) {
        Tarif t = null;
        
        Tarif selectedTarif = null;
        if (selectedTarifReference != null) {
            selectedTarif = (Tarif)selectedTarifReference.get();
        }
        
        if (ws != null) {
			t = ws.getTarif();
        }
			
		/* Avoid redisplay in case the tarif is allready selected */
		if (t == selectedTarif) return;
		
		selectedTarifReference = new WeakReference/*<Tarif>*/(t);
		
		// foreach TarificationExplorerTree select the correct Tarif...
		Iterator i= strees.iterator();
		while (i.hasNext()) {
			((TarificationExplorerTree) i.next()).setSelectedTarif(t);
		}
    }
    
    /**
     * Collapse the actual tree shown
     */
    void treesCollapseButtonActionPerformed() {
        // Identify the tree to which we want to refer
        TarificationExplorerTree tet=
            (TarificationExplorerTree) this.strees.get(
                    tabbedPane.getSelectedIndex());
        tet.collapseAll();
    }

    /**
     * Expand the actual tree shown
     */
    public void treesExpandButtonActionPerformed() {
        // Identify the tree to which we want to refer
        TarificationExplorerTree tet=
            (TarificationExplorerTree) this.strees.get(
                    tabbedPane.getSelectedIndex());
        tet.expandAll();
    }
    
    /** The concrete class of TarificationExplorerTree.ExtraPopup <br>
     * Used to display a popup over a specific node of our TarificationExplorerTrees
     */
    class ExtraPopup implements TarificationExplorerTree.ExtraPopup {
        
        private Creator owner;
        
        public ExtraPopup(Creator owner) {
            super();
            this.owner = owner;
        }
        
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
                            Lang.translate("Create Tarif")
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
                BCNode bcnode,
                String tarifType,
                String title) {
            JMenuItem jmi= new JMenuItem(title);
            jmi.setIcon(Resources.iconNew);
            jmi.addActionListener(new MyActionListener(owner, bcnode, tarifType));
            
            c.add(jmi);
        }
        
        /**
         * Small Action Listener
         */
        class MyActionListener implements ActionListener {

            private Creator owner;
            private BCNode node;
            private String tarifType;
            
            public MyActionListener(Creator owner, BCNode bcn, String tarifType) {
                this.owner = owner;
                this.node = bcn;
                this.tarifType = tarifType;
            }
            
            public void actionPerformed(ActionEvent e) {
                owner.startTarifCreationWizard(node, tarifType, null, null);
            }
        }
        
    }
    
}


/*
 * $Log: TreeTabbedPane.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.2  2004/09/28 09:41:51  perki
 * Clear action added to CreatorGold
 *
 * Revision 1.1  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 */