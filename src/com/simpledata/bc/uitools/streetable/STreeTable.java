/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 7 avr. 2004
 *
 * $Id: STreeTable.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc.uitools.streetable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.uitools.SButton;
import com.simpledata.bc.uitools.TableTools;
import com.simpledata.uitools.stree.DefaultSTreeNode;
import com.simpledata.uitools.stree.STree;


/**
 * This class provides a mean to display a STree with a JTable synchronized
 */
public class STreeTable extends JSplitPane {
    private static final Logger m_log = Logger.getLogger( STreeTable.class );
    // Definitions
    public static final int DEFAULT_ROW_HEIGHT = 40;
    
    // Variables declaration - do not modify
    private JLabel bottomCornerLabel;
    private JPanel buttonsPanel;
    private JScrollBar scrollBar;
    private JPanel scrollPanel;
    private SButton showHideButton;
    private JSplitPane split2;
    private JLabel tableBottomLabel;
    private JPanel tablePanel;
    private JScrollPane tableScrollPane;
    private JLabel topCornerLabel;
    private JPanel treePanel;
    private JScrollPane treeScrollPane;
    private JPanel treeSubPanel;
    // End of variables declaration
    
    private JTree tree;
    private JPanel treeTitlePanel;
    private JTable table;
    private Component externalComponent;
        
    private STreeTableHeaderCellRenderer headRenderer;
    //private UpdateDivider mainDividerThread;
    //private UpdateDivider split2DividerThread;
    
    /** the Stree table model **/
    private STreeTableModel tableModel;
    
    private SplitSizerThread sizerThread;
    
    protected static Color selectionBackground;
    protected static Color selectionForeground;
    
    /**
     * CONSTRUCTOR 
     * NOTA : the tree nodes must implement the interface STreeTableNodeInterface
     * @param tree 
     * @param treeTitlePanel panel containing buttons to influence tree <br> can be null
     * @param nbColumns the desired number of columns for the table
     * @param columnNames the names of the columns (can be null)
     * @param columnSizes the respective sizes of the columns (can be null)
     * @param externalComp the component to be inserted on the right of the TreeTable (can be null)
     * @param rowHeight desired height for tree and table columns
     */
    public STreeTable
    (JTree tree, JPanel treeTitlePanel,int nbColumns, 
            ArrayList columnNames, ArrayList columnSizes, 
            Component externalComp, int rowHeight) {
        super();
        
        // Setting default selection colors if needed
        if (STreeTable.selectionBackground == null) {
            STreeTable.selectionBackground = 
                UIManager.getColor("Table.selectionBackground");
        }
        if (STreeTable.selectionForeground == null) {
            STreeTable.selectionForeground = 
                UIManager.getColor("Table.selectionForeground");
        }
        
        this.tree = tree;
        this.tree.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        
        this.treeTitlePanel = treeTitlePanel;
        
        if (this.treeTitlePanel != null) {
            this.treeTitlePanel.setBorder(new EmptyBorder(new Insets(0,0,0,0)));            
        } else {
            this.treeTitlePanel = new JPanel();
        }
        
        
        this.table = new JTable();        
        
        tableModel = new STreeTableModel(this, nbColumns, columnNames);
        this.table.setModel(tableModel);
        this.table.setDefaultRenderer(
                Object.class,new STreeTableRenderer(tree));
        
        // Setting rowHeight for tree and table        
        if (rowHeight > 0) {
            this.tree.setRowHeight(rowHeight);
            this.table.setRowHeight(rowHeight);
        } else {
            int maxHeight = 
                Math.max(this.table.getRowHeight(), this.tree.getRowHeight());
            // Defaulting to 18 in case of problem
            if (maxHeight < 1) maxHeight = STreeTable.DEFAULT_ROW_HEIGHT;
            
            this.tree.setRowHeight(maxHeight);
            this.table.setRowHeight(maxHeight);            
        }
        
        // add listeners on the JTree
        this.tree.addTreeExpansionListener(
                new STreeTableTreeExpansionListener(tableModel));
        
        this.tree.addTreeSelectionListener(
                new STreeTableTreeSelectionListener(tableModel));
        
        // add listeners on the JTable
        this.table.getSelectionModel().addListSelectionListener(
                new STreeTableListSelectionListener(this,this.tree,this.table));
        
        this.externalComponent = externalComp;
        
        initComponents();
        
        this.renderColumnsWidth(columnSizes);
        
        // Setting correct thickness for buttonPanel and table headers
        int panelHeight = 0;
        if (this.treeTitlePanel != null) {
            panelHeight = this.treeTitlePanel.getPreferredSize().height;
        }
        
        int rendererHeight = 
            this.table.getTableHeader().getPreferredSize().height;
        
        int maxHeaderHeight = Math.max(panelHeight+2, rendererHeight);
        
        Dimension d;
        
        d = new Dimension(UIManager.getInt("ScrollBar.width"), maxHeaderHeight);
        topCornerLabel.setMinimumSize(d);
        topCornerLabel.setPreferredSize(d);
        topCornerLabel.setMaximumSize(d);
        
        headRenderer = new STreeTableHeaderCellRenderer(maxHeaderHeight);
        this.renderHeaders();
        
        //Determine which height has to be used for table title and buttonPanel.
        headRenderer.setMaxHeight(maxHeaderHeight);
        d = this.buttonsPanel.getPreferredSize();
        d.setSize(d.width, maxHeaderHeight);
        this.buttonsPanel.setPreferredSize(d);
        
        this.placeDividers();
    }
     
    /**
     * Place dividers and set their resize weight correctly
     */
    private void placeDividers() {
        if (this.externalComponent == null) {
            this.setResizeWeight(1.0);
            this.split2.setResizeWeight(1.0);
            this.split2.setDividerSize(0);
            this.split2.setDividerLocation(this.split2.getWidth()-40);
            this.setDividerLocation(1.0);
        } else {
            this.setResizeWeight(0.5);
            this.split2.setResizeWeight(0.0);
            this.split2.setDividerSize(3);
        }
    }
    
    /**
     * Initialize graphical components and construct general Layout
     */
    private void initComponents() {
        treePanel = new JPanel();
        treeSubPanel = new JPanel();
        buttonsPanel = new JPanel();
        showHideButton = new SButton();
        treeScrollPane = new JScrollPane();
        split2 = new JSplitPane();
        tablePanel = new JPanel();
        tableScrollPane = new JScrollPane();
        tableBottomLabel = new JLabel();
        scrollPanel = new JPanel();
        topCornerLabel = new JLabel();
        scrollBar = new JScrollBar();
        bottomCornerLabel = new JLabel();
        
        Dimension d;
        
        
        int scrollWidth = UIManager.getInt("ScrollBar.width");
        
        this.setDividerSize(3);
        
        //*  TREE PANEL *//
        treePanel.setLayout(new BorderLayout());
        treePanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
                
        //* TREE SUB PANEL *//
        treeSubPanel.setLayout(new BorderLayout());
        treeSubPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        
        //* BUTTONS PANEL *//
        buttonsPanel.setLayout(new GridBagLayout());
        
        buttonsPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(1, 1, 1, 1);
        gridBagConstraints.weightx = 1.0;
        buttonsPanel.add(this.treeTitlePanel, gridBagConstraints);

        showHideButton.setIcon(Resources.showHide);
        showHideButton.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        showHideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				showHideActionPerformed();
			}
		});
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(1, 1, 1, 1);
        buttonsPanel.add(showHideButton, gridBagConstraints);
        //* -END- BUTTONS PANEL *//
        
        treeSubPanel.add(buttonsPanel, BorderLayout.NORTH);
        
        	//*  TREE SCROLL PANE *//
        treeScrollPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        treeScrollPane.setHorizontalScrollBarPolicy(
        		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        
        // Attaching scrollBar to tree
        treeScrollPane.setVerticalScrollBar(scrollBar);
        
        treeScrollPane.setViewportView(this.tree);
        //*  -END- TREE SCROLL PANE *//
        
        treeSubPanel.add(treeScrollPane, BorderLayout.CENTER);        
        	//*  -END- TREE SUB PANEL *//
        
        treePanel.add(treeSubPanel, BorderLayout.CENTER);
        
        //*  SCROLL PANEL  *//
        scrollPanel.setLayout(new BorderLayout());
        
        scrollPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        d = new Dimension(scrollWidth, scrollWidth);
        topCornerLabel.setMinimumSize(d);
        topCornerLabel.setPreferredSize(d);
        topCornerLabel.setMaximumSize(d);
        scrollPanel.add(topCornerLabel, BorderLayout.NORTH);
        
        scrollPanel.add(scrollBar, BorderLayout.CENTER);
        
        d = new Dimension(scrollWidth, scrollWidth);
        bottomCornerLabel.setMinimumSize(d);
        bottomCornerLabel.setPreferredSize(d);
        bottomCornerLabel.setMaximumSize(d);
        scrollPanel.add(bottomCornerLabel, BorderLayout.SOUTH);
        	//*  -END- SCROLL PANEL  *//
        
        treePanel.add(scrollPanel, BorderLayout.WEST);
        
        this.setLeftComponent(treePanel);
        
        split2.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        split2.setDividerSize(1);
        tablePanel.setLayout(new BorderLayout());
        
        tablePanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        tableScrollPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        tableScrollPane.setVerticalScrollBarPolicy(
        		ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        this.table.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
        JTableHeader jth = table.getTableHeader();
        jth.setReorderingAllowed(false);
        tableScrollPane.setViewportView(this.table);
        
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        d = new Dimension(1, scrollWidth);
        tableBottomLabel.setMinimumSize(d);
        tableBottomLabel.setPreferredSize(d);
        tableBottomLabel.setMaximumSize(d);
        tablePanel.add(tableBottomLabel, BorderLayout.SOUTH);
        
        split2.setLeftComponent(tablePanel);
        
        split2.setRightComponent(this.externalComponent);
        
        this.setRightComponent(split2);
 
        // Synchronizing scroll for tree and table
        scrollBar.addAdjustmentListener(new STreeTableAdjustmentListener(this));        
    }

    /**
     * Verify that table and tree scrollPane are synchronized
     * @param treeSource if true the tree will be taken as source else it would be the table
     */
    protected void verifyScrollsPositions(boolean treeSource) {
        JScrollPane scroll1;
        JScrollPane scroll2;
        if (treeSource) {
            scroll1 = treeScrollPane;
            scroll2 = tableScrollPane;
        } else {
            scroll1 = tableScrollPane;
            scroll2 = treeScrollPane;
        }
        Point p1 = scroll1.getViewport().getViewPosition();
        Point p2 = scroll2.getViewport().getViewPosition();
        if (p1.getY() == p2.getY()) return;
        p2.setLocation(p2.getX(),p1.getY());
        scroll2.getViewport().setViewPosition(p2);
        scroll2.getViewport().repaint();
    }

    /** change a column name **/
    public void setColumnName(int columnIndex,String name) {
    	tableModel.setColumName(columnIndex,name);
    	renderHeaders();
    }
    
    /** remove a column **/
    public void removeColumn(int columnIndex) {
        if (columnIndex < 0 || 
                columnIndex >= table.getColumnModel().getColumnCount() ) 
        {
            m_log.error("Cannot remove an unkown column",new Exception());
            return;
        }
        table.getColumnModel().removeColumn(
                table.getColumnModel().getColumn(columnIndex));
    }
    
    /** remove a column **/
    public void addColumn() {
        table.getColumnModel().addColumn(
                new TableColumn(0));
        renderHeaders();
    }
    
    private void renderHeaders() {
        for (int i=0; i< this.table.getColumnCount(); i++) {
            // Finally, set the icon header renderer on the second column
            	this.table.getColumnModel().getColumn(i).setHeaderRenderer(
            			this.headRenderer);
        }
      
    }
    
    private void renderColumnsWidth(ArrayList widths) {
        if ((widths == null) || (widths.size() == 0)) return;
        int nbSizes = widths.size();
        int nbCol = this.table.getModel().getColumnCount();
        if (nbSizes < nbCol) {
            for (int i=nbSizes;i<nbCol;i++) {
                widths.add(i, new Integer(0));
            }
        }
        nbSizes = nbCol;
        
        // Calculating total size
        int sum = 0;
        for (int i=0; i<nbSizes;i++) {
            sum += ((Integer)widths.get(i)).intValue();
        }
        
        // Setting table width
        if (sum > 0) {            
            this.split2.setDividerLocation(sum);
        }
        
        // Setting columns width
        TableColumnModel model = this.table.getColumnModel();
        TableColumn column = null;
        for (int i=0;i<nbCol;i++) {
            // We set the prefered width for each column
            column = model.getColumn(i);
            column.setPreferredWidth( ((Integer)widths.get(i)).intValue() );
        }
    }
    
    //private int oldDivider2Pos = 100;
    //private int oldDivider1Pos = 100;
    
    private int oldTreeWidth = 0;
    private int oldTableWidth = 0;
    private int oldExternalCompWidth = 0;
    
    private int oldDivSize = 0;
    
    private boolean tableIsHidden = false;
    
    // The distance under which we consider that a divider is too close to
    // the border of the splitPane
    private static final int MIN_DIV_SPACING = 20; 
    
    /**
     * This method is called whenever the hide button is pressed
     */
    private void showHideActionPerformed() {
        if ((oldTreeWidth == 0) && 
                (oldTableWidth == 0) && 
                (oldExternalCompWidth == 0)) {
            //Log.out("first call : init of tableIsHidden prop");
            // This is the first call of this function
            // We verify table showing state
            oldTableWidth = this.split2.getLeftComponent().getWidth();
            if (oldTableWidth > 0) {
                //Log.out("false");
                tableIsHidden = false;
            } else {
                //Log.out("true");
                tableIsHidden = true;
            }
        }
        
        if (!tableIsHidden) {
            // Lets store every usefull parameters
            oldTreeWidth = this.getLeftComponent().getWidth();
            oldTableWidth = this.split2.getLeftComponent().getWidth();
            if (this.externalComponent != null) {
                oldExternalCompWidth = this.split2.getRightComponent().getWidth();
            }
            
            //Log.out("Parameters stored, oldTreeWidth : "+oldTreeWidth+", oldTableWidth : "+oldTableWidth+", oldExt : "+oldExternalCompWidth);
            
        }
        
        // We distinguish two different behaviour
        // With and without external component
        if (this.externalComponent == null) {
            // Behaviour without external component
            if (!tableIsHidden) {
                // We have to hide the table                
                oldDivSize = this.getDividerSize();
                this.setDividerSize(0);
                
                this.setDividerLocation(1.0d);
                
                tableIsHidden = true;
            } else {
                // We have to show the table
                int totalWidth = this.getWidth();                
                if (oldTableWidth < (totalWidth - MIN_DIV_SPACING)) {
                    // We are sufficiently far from edge
                    // We reset to exact old size
                    int newPos = totalWidth - oldTableWidth;
                    this.setDividerLocation(newPos);
                } else {
                    // Too close from edge, lets use proportional placing instead...
                    int oldTotal = oldTreeWidth + oldTableWidth;
                    if (oldTotal > 0) {
                        double newProp = ((double)oldTreeWidth) / ((double)oldTotal);
                        this.setDividerLocation(newProp);
                    } else {
                        this.setDividerLocation(0.5d);
                    }
                }
                this.setDividerSize(oldDivSize);
                tableIsHidden = false;
            }
        } else {           
            // Behaviour with existing external component
            if (!tableIsHidden) {
                // We have to hide the table
                
                oldDivSize = this.split2.getDividerSize();
                this.split2.setDividerSize(0);
                
                this.split2.setDividerLocation(0.0d);
                
                tableIsHidden = true;
            } else {
                // We have to show the table
                // We must keep size of tree and old size of table
                
                // We just verify that oldTableWidth fits in split2 width
                int totalWidth = this.split2.getWidth();
                int newTreeWidth = this.getLeftComponent().getWidth();
                if (oldTableWidth < (totalWidth - MIN_DIV_SPACING)) {
                    
                    if (oldTableWidth < MIN_DIV_SPACING) {
                        oldTableWidth = MIN_DIV_SPACING;
                        newTreeWidth = newTreeWidth - oldTableWidth;
                    }
                    
                    // We are sufficiently far from edge
                    // We reset to exact old size
                    this.setSizes((double)newTreeWidth, 
                            (double)oldTableWidth, 
                            (double)(totalWidth - oldTableWidth));
                    m_log.debug("far enough"+newTreeWidth+
                            " "+oldTableWidth+" "+totalWidth);
                } else {
                    // Too close from edge, lets use proportional placing instead...
                    m_log.debug("too close");
                    // Old props between table and component
                    double oldProp;
                    int oldTotal = oldTableWidth + oldExternalCompWidth;
                    if (oldTotal > 0) {
                        oldProp = ((double)oldTableWidth) / ((double)oldTotal);
                    } else {
                        oldProp = 0.5;
                    }
                    
                    this.setSizes((double)newTreeWidth, 
                            oldProp*((double)totalWidth), 
                            (1.0d - oldProp)*((double)totalWidth));
                    
                }
                this.split2.setDividerSize(oldDivSize);
                tableIsHidden = false;
                
            }
            
            
        }
        
        
        
        
        //        if (this.externalComponent != null) {
        //            int oldPos = this.split2.getDividerLocation();
        //            if (oldPos > 0) {
        //                // We must memorize
        //                this.oldDivider2Pos = oldPos;
        //                this.split2.setDividerLocation(0);
        //                this.split2.setDividerSize(0);
        //            } else {
        //                // We have to restore
        //            		int h = this.split2.getWidth();
        //            		int h10 = Math.max((int)(h*0.005), 5);
        //            		if ((oldDivider2Pos < h10) || (oldDivider2Pos > (h - h10))) {
        //            			this.split2.setDividerLocation(0.5);
        //            		} else {
        //            			this.split2.setDividerLocation(this.oldDivider2Pos);
        //            		}
        //                this.split2.setDividerSize(2);
        //            }
        //        } else {
        //        		// New method
        //        		// Lets get all sizes first
        //        		/*
        //        		 * HERE IS THE COMPONENT REPARTITION
        //        		 * 			mainSplit
        //        		 *  |--------------|------------------------------------|
        //        		 *        tree     |------------|-----------------------|
        //        		 * 						table       externalComponent      
        //        		 */
        //        		
        //        	
        //            if (this.split2DividerThread == null) {
        //                this.split2DividerThread = new UpdateDivider(this.split2);
        //            }
        //            // We have no right component
        //            int oldPos = this.getDividerLocation();
        //            //int extremePos = this.getWidth() - this.scrollBar.getPreferredSize().width;
        //            int extremePos = this.getWidth();
        //            
        //            if (oldPos < extremePos) {
        //                // We memorize
        //                this.oldDivider1Pos = oldPos;
        //                this.setDividerLocation(extremePos);
        //                this.setDividerSize(0);
        //            } else {
        //                // We must restore
        //                this.setDividerLocation(this.oldDivider1Pos);
        //                this.setDividerSize(2);
        //                // We then restore split 2
        //                //this.split2.setDividerLocation();
        //                this.split2DividerThread.setPos(extremePos - this.oldDivider1Pos);
        //                javax.swing.SwingUtilities.invokeLater(this.split2DividerThread); 
        //            }
        //        }
        
        
        
    }
    
    
    /**
     * Set colors for highlighted cells
     * @param bg background color
     * @param fg foreground color
     */
    public void setHighLightColors(Color bg, Color fg) {
        STreeTable.selectionBackground = bg;
        STreeTable.selectionForeground = fg;
    }
    
    /**
     * Set proportional sizes for every component
     * @param tree proportional size of the tree 
     * @param table proportional size of the table
     * @param component proportional size of the component
     */
    public void setSizes(double tree, double table, double component) {
        this.sizerThread = new SplitSizerThread(this, 0.5, 0.5);
    		
    		double total;
    		
    		if (this.externalComponent == null) {
    			// We just consider the ratio between tree and table
    			total = tree + table;
    			if ( (total <= 0) || (tree < 0) || (table < 0) ) {
    				m_log.error( "invalid parameters passed to setSize" );
    				return;
    			}
    			sizerThread.reset(tree/total ,1);
    		} else {
    			double subtotal = table + component;
    			total = tree + subtotal;
    			
    			if ( (total <= 0) || (subtotal <= 0) || (tree < 0) || (table < 0) || (component < 0)) {
    				m_log.error( "invalid parameters passed to setSize" );
    				return;
    			}
    			double n1 = ((double)tree) / total;
    			double n2 = ((double)table) / subtotal;
    			//Log.out("SizerThread reseted with sizes => n1 : "+n1+", n2 : "+n2);
    			sizerThread.reset(n1, n2);
    		}
    		sizerThread.start();
    }
    
    public double[] getSizes() {
        double[] res = new double[3];
        double totalWidth = getWidth();
        double firstPos = 0;
        double secondPos = 0;
        double thirdPos = 0;
        
        if (totalWidth > 0) {
            firstPos = (this.getDividerLocation()/totalWidth);
            if (this.externalComponent != null) {
                //double secondSplitWidth = this.split2.getWidth();
                secondPos= 
                    (this.split2.getLeftComponent().getWidth()/totalWidth);
                thirdPos = 1 - secondPos - firstPos;
            } else {
                secondPos = 1 - firstPos;
            }
        }
//        double firstDivRelPos = this.getDividerLocation();
//        double secondSplitWidth = this.getRightComponent().getWidth();
        res[0] = firstPos;
        res[1] = secondPos;
        res[2] = thirdPos;
        return res;
    }
    
    // -------------------------- //
    // ------  GETTERS ---------- //
    // -------------------------- //
    
    /**
     * Returns the JTree used in the STreeTable
     */
    public JTree getTree() {
        return this.tree;
    }
    
    /**
     * Returns the table used in the STreeTable
     */
    public JTable getTable() {
        return this.table;
    }
    
    /**
     * Returns the splitPane between table and component
     */
    protected JSplitPane getSecondarySplit() {
		return this.split2;
    }
    
    // -------------------------- //
    // ----  END GETTERS -------- //
    // -------------------------- //
    
    
    
    /*
     * TODO Remove when testing complete
     */
    public static void main(String[] args) {
        Resources.loadResources();
        
        JFrame jf = new JFrame();
        
        jf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                System.exit(0);
            }
        });
               
        // Tree generation
        DefaultSTreeNode stRoot = (DefaultSTreeNode)(new STree()).getRoot();
        MyNode root = STreeTable.pumpDefaultSTreeNode(stRoot);
        STree tree = new STree(root);
                
        // externalComp generation
        JLabel jLabel1;
        JPanel myComponent;
        myComponent = new JPanel();
        jLabel1 = new JLabel();
        myComponent.setLayout(new BorderLayout());        
        myComponent.setMinimumSize(new Dimension(300, 100));
        myComponent.setPreferredSize(new Dimension(300, 100));
        jLabel1.setText("supplementary Component");
        myComponent.add(jLabel1, BorderLayout.CENTER);
        
        ArrayList colNames = new ArrayList();
        colNames.add("First column");
        colNames.add("Second one");
        
        ArrayList colSizes = new ArrayList();
        colSizes.add(new Integer(80));
        colSizes.add(new Integer(160));
        
        // With external Component;
        STreeTable stt = 
            new STreeTable(tree,null,2, colNames, colSizes, myComponent, -1);
        
        // Without external Component
        //STreeTable stt =
        // new STreeTable(tree, null ,2, colNames, colSizes, null, -1);
        
        jf.getContentPane().add(stt);
        jf.pack();
        jf.setVisible(true);
        
        stt.setSizes(0.5,0.5,0.5);
    }
 
    /*
     * TODO Remove when testing complete
     */
    private static MyNode pumpDefaultSTreeNode(DefaultSTreeNode dstn) {
        String title = dstn.toString();
        MyNode mn = new MyNode(title);
        for (int i=0; i<dstn.getChildCount();i++) {
            DefaultSTreeNode child = (DefaultSTreeNode)dstn.getChildAt(i);
            mn.addChild(pumpDefaultSTreeNode(child));
        }
        return mn;
    }
        
}

/*
 * TODO Remove when testing complete
 */
class MyNode extends DefaultSTreeNode implements STreeTableNode {
    
    public MyNode(String title) {
        super(title);
    }
    
    public Object getValueAt(int columnIndex) {
        return new String(this.toString()+" "+columnIndex);
    }

    public boolean isHighLighted(int columnIndex) {
        return false;
    }
    
}

/**
 * This thread is used to restore dividerPosition after a show - hide operation
 */
class UpdateDivider implements Runnable {
    
    private JSplitPane pane;
    private int position;
    private double relPosition;
    
    public UpdateDivider(JSplitPane owner) {
        this.pane = owner;
    }
    
    public void setPos(int pos) {
        this.position = pos;
        this.relPosition = 0;
    }
    
    public void setPos(double pos) {
        this.relPosition = pos;
        this.position = 0;
    }
    
    public void run() {
        if (this.relPosition != 0) {
            this.pane.setDividerLocation(this.relPosition);
            return;
        }
        this.pane.setDividerLocation(this.position);
    }        
}

/**
 * Thread used to enable correct divider repositioning
 */
class SplitSizerThread extends Thread {
	private static final Logger m_log =Logger.getLogger(SplitSizerThread.class);
	
	public final static int WAIT_FOR_SHOW = 0;
	public final static int WAIT_FOR_DIVIDER_1 = WAIT_FOR_SHOW + 1;
	public final static int FINISHED = WAIT_FOR_DIVIDER_1 + 1;

	// Actual precision in pixel for the isNear method
	private final static int PRECISION = 5;
	
	// Wait time between each loop of the state machine in ms
	private final static int WAIT_TIME_MS = 50;
	
	//Maximum number of loop before we consider that we have reached an infinite 
	// loop (i.e for example when the precision is too small and we never reach
	// the expected position)
	private final static int MAX_LOOP_COUNT = 100;
	
	// Contains the actual state
	private int state;
	
	private JSplitPane mainSplit;
	private JSplitPane secondarySplit;
	
	private double firstDividerRelPos;
	private double secondaryDividerRelPos;
	
	// Intermediate variables
	private int expectedMainDivPos;
	private int expectedSecondaryDivPos;

	// Loop counter to avoid eternal loops
	int loopCount;

	/**
	 * CONSTRUCTOR
	 * @param stt the owner
	 * @param firstDividerRelPos relative position for the first 
	 * divider (0 <= double <= 1)
	 * @param secondaryDividerRelPos relative position for the second 
	 * position (0 <= double <= 1)
	 */
	public SplitSizerThread(STreeTable stt, 
							double firstDividerRelPos, 
							double secondaryDividerRelPos) {
		this.mainSplit = stt;
		reset(firstDividerRelPos, secondaryDividerRelPos);
		loopCount = 0;
		if (stt != null) {
			this.secondarySplit = stt.getSecondarySplit();
		}
	}

	
	/**
	 * Resets the class to new positioning values...
	 * @param firstDividerRelPos relative position for the 
	 * first divider (0 <= double <= 1)
	 * @param secondaryDividerRelPos relative position for the 
	 * second position (0 <= double <= 1)
	 */
	public void reset(double firstDividerRelPos, double secondaryDividerRelPos) {

		this.firstDividerRelPos = firstDividerRelPos;
		this.secondaryDividerRelPos = secondaryDividerRelPos;
		
		this.state = WAIT_FOR_SHOW;
		
		//////////////////////
		// FAIL PROOF TESTS //
		//////////////////////
		if (this.mainSplit == null) {
			m_log.fatal( "Malformed class, mainSplit null, exiting..." );
			this.state = FINISHED;
		}
		
		if ((firstDividerRelPos < 0) || (firstDividerRelPos > 1)) {
			m_log.warn( "First divider position is malformed, exiting, value : "
			        +firstDividerRelPos );
			this.state = FINISHED;
		}
		
		if ((secondaryDividerRelPos < 0) || (secondaryDividerRelPos > 1)) {
			m_log.warn("Second divider position is malformed, exiting, value : "
			        +secondaryDividerRelPos );
			this.state = FINISHED;
		}
		//////////////////////////
		// FAIL PROOF TESTS END //
		//////////////////////////
		
		this.expectedMainDivPos = -1;
		this.expectedSecondaryDivPos = -1;

	}

//	/**
//	 * Returns the string description for a defined state
//	 * @param state
//	 * @return
//	 */
//	private String stateDescription(int state) {
//		String res = "";
//		switch(state) {
//			case WAIT_FOR_SHOW:
//				res = "WAIT_FOR_SHOW";
//				break;
//			case WAIT_FOR_DIVIDER_1:
//				res = "WAIT_FOR_DIVIDER_1";
//				break;
//			case FINISHED:
//				res = "FINISHED";
//				break;
//			default:
//				res = "unknown state";
//		}
//		return res;
//	}
	
	/**
	 * Contains SplitSizerThread state machine
	 */
	public void run() {
	    //Log.out("run launched");
		// Resetting the loop counter
		loopCount = 0;
		
		// We loop on the state cycle until we finish it
		while (this.state != FINISHED) {
			loopCount++;
			//Log.out("current state : "+stateDescription(this.state));
			try {
				//Log.out(".");
				sleep(SplitSizerThread.WAIT_TIME_MS);
			} catch (InterruptedException e) {
				m_log.error( "Sleep problem in SplitSizerThread", e );
			}
			switch (state) {				
				case WAIT_FOR_SHOW:
					// We are waiting for the STreeTable to show 
					// (just to make sure)
					if ((this.mainSplit.isShowing())  || 
					        (loopCount > SplitSizerThread.MAX_LOOP_COUNT)) {
						// Calculate mainSplitSize
						int mainSplitWidth = this.mainSplit.getWidth();
						this.expectedMainDivPos = 
						    (int)(mainSplitWidth*this.firstDividerRelPos);
						
						this.mainSplit.setDividerLocation(
						        this.firstDividerRelPos);

						loopCount = 0;
						if (this.secondarySplit != null) {
							this.state = WAIT_FOR_DIVIDER_1;
						} else {
							this.state = FINISHED;
						}
					} 
					break;
				case WAIT_FOR_DIVIDER_1:
					// We are waiting for the first split to finish
					// its divider positioning

					int leftCompWidth = 
					    this.mainSplit.getLeftComponent().getWidth();
					
					if ((isNear(leftCompWidth, this.expectedMainDivPos) )  
					        || (loopCount > SplitSizerThread.MAX_LOOP_COUNT)) {
						
					    int secondarySplitWidth =this.secondarySplit.getWidth();
						
						expectedSecondaryDivPos = 
						    (int)(secondarySplitWidth*secondaryDividerRelPos);
						
						secondarySplit.setDividerLocation(
						        secondaryDividerRelPos);

						loopCount = 0;
						this.state = FINISHED;
					}						
					
					break;
				default:
					m_log.error(
						"STreeTable.SplitSizerThread:Unknown state : "+state);
			}
		}
		
		//Log.out("run terminated");
		
	}
	
	/**
	 * Test if source integer is in a range defined by PRECISION 
	 * around target
	 * @param source
	 * @param target
	 * @return
	 */
	private boolean isNear(int source, int target) {
		return (((target-PRECISION) <= source)&&(source<=(target+PRECISION)));
	}
	
}

/**
 * The Table Model
 */
class STreeTableModel extends AbstractTableModel {
		private static final Logger m_log=
		    Logger.getLogger(STreeTableModel.class ); 
	
    private STreeTable owner;
    private int nbColumns;
    private ArrayList names;
    //private STreeTableHeaderCellRenderer headRenderer;

    /** construct a StreeTableModel over a JTree **/
    public STreeTableModel 
    (STreeTable owner,int nbColumns,ArrayList columnNames) {
        
        this.owner = owner;
        this.nbColumns = nbColumns;
        
        // Then we repair the names
        if (columnNames == null) {
            this.names = new ArrayList();
            for (int i=0; i<this.nbColumns; i++) {
                this.names.add(new String("c "+i));
            }
        } else {
            if (columnNames.size() >= this.nbColumns) {
                // no prob
                this.names=new ArrayList(columnNames.subList(0,this.nbColumns));
            } else {
                this.names = columnNames;
                for (int i=columnNames.size();i<this.nbColumns;i++) {
                    this.names.add(new String("c "+i));
                }
            }
        }
        
    }
    
    /** change a column name **/
    protected void setColumName(int column,String name) {
    	if (column < 0 || (column + 1) > names.size()) {
    		m_log.warn(
    				"Tried to set a column name on an outbound column");
    	}
    	names.add(column,name);
    	fireTableStructureChanged();
    	
    }
    
    /** return the column name **/
    public String getColumnName(int column) {
        return (String) this.names.get(column);
    }
    
    /**
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return this.nbColumns;
    }
    
    /**
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return this.owner.getTree().getRowCount();
    }
    
    /**
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        TreePath tp = this.owner.getTree().getPathForRow(rowIndex);
        if (tp == null) return null;
        Object o = tp.getLastPathComponent();
        if (o == null) return null;
        if (o instanceof STreeTableNode) {
            STreeTableNode sttn = (STreeTableNode)o;
            return sttn.getValueAt(columnIndex);
        }
        return null;
    }
    
    /**
     * return true if this cell is editable
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
}

/**
 * The Table Renderer
 * (makes lines of different colors)
 */
class STreeTableRenderer extends DefaultTableCellRenderer {
    JTree jtree;
    
    public STreeTableRenderer(JTree jtree) {
        this.jtree = jtree;
    }
        
    public Component getTableCellRendererComponent(
            JTable table,
            java.lang.Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        Component co=
            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column);
        
        
        isSelected = jtree.isPathSelected(jtree.getPathForRow(row));
            
        TableTools.setRowColors(table,co,isSelected,hasFocus,row);
                
        // Extract the corresponding node to evaluate if highlight is
        // needed
        TreePath tp = this.jtree.getPathForRow(row);
        if (tp != null) {
            Object node = tp.getLastPathComponent();
            if (node instanceof STreeTableNode) {
                if (((STreeTableNode)node).isHighLighted(column)) {
                    co.setBackground(STreeTable.selectionBackground);
                    co.setForeground(STreeTable.selectionForeground);
                }
                
            }
        }

        co.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return co;
    }
}

/**
 * Renderer for the table header
 * enabling it to have a thickness corresponding to the
 * thickness of the treeTitlePanel
 */
class STreeTableHeaderCellRenderer extends DefaultTableCellRenderer {

    private int maxHeight;
    
    public STreeTableHeaderCellRenderer(int maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    public void setMaxHeight(int max) {
        this.maxHeight = max;
    }
    
    public int getMaxHeight() {
        return this.maxHeight;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // Inherit the colors and font from the header component
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }
        
        if (value instanceof JLabel) {
            JLabel jb = (JLabel)value;
            setIcon(jb.getIcon());
            setText(jb.getText());
        } else if (value instanceof Icon) {
            // Value is an Icon
            setIcon((Icon)value);
            setText("");
        } else {
            // Value is text
            setText((value == null) ? "" : value.toString());
            setIcon(null);
        }
        Border border = UIManager.getBorder("TableHeader.cellBorder");
        setBorder(border);
        
        // TODO analyze border thickness to compensate 
        Dimension d = this.getPreferredSize();
        d = new Dimension((int) d.getWidth(), this.maxHeight);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setSize(d);
        setHorizontalAlignment(SwingConstants.CENTER);
        
        return this;
    }
}

/**
 * Listener for tree expansions to enable table update
 */
class STreeTableTreeExpansionListener implements TreeExpansionListener {

    private AbstractTableModel tableModel;
    
    public STreeTableTreeExpansionListener(AbstractTableModel model) {
        this.tableModel = model;
    }
    
    public void treeExpanded(TreeExpansionEvent event) {
        tableModel.fireTableDataChanged();
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        tableModel.fireTableDataChanged();
    }
    
}

/**
 * Listener for selections on the tree
 */
class STreeTableTreeSelectionListener implements TreeSelectionListener {
    
    private AbstractTableModel tableModel;
    
    public STreeTableTreeSelectionListener(AbstractTableModel model) {
        this.tableModel = model;
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        tableModel.fireTableDataChanged();
    }
}
   
/**
 * Listeners for selection on the table
 */
class STreeTableListSelectionListener implements ListSelectionListener {
    
    private MyAction myAction;
    private JTree tree;
    private JTable table;
    
    public STreeTableListSelectionListener
    (STreeTable owner, JTree tree, JTable table) {
        
        this.tree = tree;
        this.table = table;

        myAction = new MyAction(owner);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        int row = this.table.getSelectedRow();
        if (row > -1)
            if (this.tree.getMinSelectionRow() != row) {
                this.tree.setSelectionRow(this.table.getSelectedRow());
                // We then adapt the scrollPanes 
                // position in case of table auto scroll
                javax.swing.SwingUtilities.invokeLater(myAction);                
            }
    }
    
    class MyAction implements Runnable {
        
        private STreeTable owner;
        
        public MyAction(STreeTable owner) {
            this.owner = owner;
        }
        
        public void run() {
            this.owner.verifyScrollsPositions(false);
        }        
    }
    
}

/**
 * Adjustement listener enabling tree and table scrolling
 * to be synchronized
 */
class STreeTableAdjustmentListener implements AdjustmentListener {

    private STreeTable owner;
    
    public STreeTableAdjustmentListener(STreeTable owner) {
        this.owner = owner;
    }
    
    public void adjustmentValueChanged(AdjustmentEvent e) {
        this.owner.verifyScrollsPositions(true);
    }
}


/*
 * $Log: STreeTable.java,v $
 * Revision 1.2  2007/04/02 17:04:28  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.27  2005/01/24 11:49:17  jvaucher
 * Issue#67/#68: OptionCase re-use problem
 * Issue#69: Tariff Delete bug.
 * Issue#66: Description dans les dispatcher sur le TarificationReport
 *
 * Revision 1.26  2004/11/17 15:14:46  perki
 * Discount DISPLAY RC1
 *
 * Revision 1.25  2004/09/28 17:19:59  perki
 * *** empty log message ***
 *
 * Revision 1.24  2004/09/07 10:13:43  kaspar
 * ! Replacing Log.out
 *
 * Revision 1.23  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.22  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.21  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.20  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.19  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 * Revision 1.18  2004/05/28 11:11:08  carlito
 * *** empty log message ***
 *
 * Revision 1.17  2004/05/22 08:39:36  perki
 * Lot of cleaning
 *
 * Revision 1.16  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.15  2004/05/11 17:57:01  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/05/11 15:53:00  perki
 * more calculus
 *
 * Revision 1.13  2004/05/10 17:43:54  carlito
 * *** empty log message ***
 *
 * Revision 1.12  2004/05/05 16:52:29  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/05 08:26:54  perki
 * cleaning
 *
 * Revision 1.10  2004/04/12 16:31:11  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/04/12 16:10:27  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/04/12 15:37:41  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/04/12 15:32:31  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/04/12 14:02:18  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/04/12 13:49:59  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/04/12 13:47:04  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/04/12 12:56:18  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/04/12 09:41:28  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/04/09 18:01:01  carlito
 * *** empty log message ***
 *
 */