/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: AbstractBySlicePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.workplace.tools;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.tools.DataBySlice;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.tools.OrderedMapOfDoubles;
import com.simpledata.bc.tools.OrderedMapOfDoublesObject;
import com.simpledata.bc.uitools.SButtonIcon;
import com.simpledata.bc.uitools.TableTools;

/**
 * An abstract containe for all By Slice Calculus
 */
public abstract class AbstractBySlicePanel extends JPanel { 
   
	
	private SButtonIcon plusButton;
	private SButtonIcon deleteButton;
	
	
	/** the type chooser (marginal /effective)**/
	JComboBox typeChooser ;

	DataBySlice dbs;

	private JScrollPane jScrollPane;
	JTable jTable;
	DBSTableModel rtbsptModel;
	public DBSTableEditor rtbsptEditor;
	RBSPListener rbspl;

	
	/**
	 * This is the default constructor
	 * @param rbspl an object implementing RateBySlicePanel.RBSLPListener (can be null)
	 */
	public AbstractBySlicePanel( DataBySlice rbs, RBSPListener rbspl ) {
		super();
		this.dbs= rbs;
		
		// avoid rbspl being null
		if (rbspl == null)
			rbspl = new AbstractBySlicePanel.RBSPListener() {
				public void rbsDataChanged() {}
			};
		this.rbspl = rbspl;
		
	}

	/**
	 * An interface that should be implemented if you wnt to listen 
	 * To data changes
	 */
	public interface RBSPListener {
		/** called when a data is changed "graphically" on the RBS **/
		public void rbsDataChanged() ;
	}

	/** delete actually Selected Slice **/
	public final void deleteSlice() {
		int row= jTable.getSelectedRow();
		if (row < 1)
			return; // no delete
		dbs.getOmod().removeAtIndex(row);
		rtbsptModel.fireTableDataChanged();
		rbspl.rbsDataChanged(); // advertise listener
	}

	/** create an empty slice **/
	public final void createSlice() {
		OrderedMapOfDoubles omod= dbs.getOmod();
		int row= jTable.getSelectedRow() + 1;
		if ((row < 1) || (row > omod.size()))
			row= omod.size();

		// smartly invent a value for the new slice
		double key= omod.getMaxKey() + 1;
		if (omod.size() > 1) {
			if (row == omod.size()) { //	at the end
				key=
					2 * omod.getKeyAtIndex(row - 1)
						- omod.getKeyAtIndex(row - 2);
			} else { // between two rows
				key=
			(omod.getKeyAtIndex(row) + omod.getKeyAtIndex(row - 1)) / 2;
			}
		}
		
		dbs.createLineAt(key);
		
		rtbsptModel.fireTableDataChanged();
		rbspl.rbsDataChanged(); // advertise listener
		load();
	}
	
	/** refresh data **/
	public abstract void load();

	/**
	 * Event on the Jtable (open popup ??)
	 */
	final void jTableMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			final int row= jTable.getSelectedRow();

			JPopupMenu jp= new JPopupMenu();
			if (row > 0) {
				JMenuItem jmi=
					new JMenuItem(
						deleteButton.getToolTipText(),
						deleteButton.getIcon());
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						deleteSlice();
					}
				});
				jp.add(jmi);
			}
			JMenuItem jmi=
				new JMenuItem(
					plusButton.getToolTipText(),
					plusButton.getIcon());
			jmi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createSlice();
				}
			});
			jp.add(jmi);
			jp.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	/**
	 * This method initializes this
	 */
	public final void initialize() {
		this.setLayout(new java.awt.BorderLayout());
		this.add(getJPanel(), java.awt.BorderLayout.NORTH);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);

		//this.setSize(300, 200);
	}

	

	
	/**
	 *Get the header panel
	 */
	protected abstract JPanel getJPanel();


	protected final JButton getPlusButton() {
		if (plusButton == null) {
			plusButton= new SButtonIcon(Resources.iconPlus);
			plusButton.setPreferredSize(new Dimension(20, 20));
			plusButton.setToolTipText(Lang.translate("Add a slice"));
			plusButton.setBorderPainted(false);
			plusButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createSlice();
				}
			});
		}
		return plusButton;
	}

	protected final JButton getDeleteButton() {
		if (deleteButton == null) {
			deleteButton= new SButtonIcon(Resources.iconDelete);
			deleteButton.setPreferredSize(new Dimension(20, 20));
			deleteButton.setToolTipText(
				Lang.translate("Delete selected slice"));
			deleteButton.setBorderPainted(false);
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteSlice();
				}
			});
		}
		return deleteButton;
	}

	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			rtbsptModel= new DBSTableModel();
			jTable= new JTable(rtbsptModel);
			jTable.setDefaultRenderer(Object.class, new RTBSPTRenderer());
			rtbsptEditor =  new DBSTableEditor();
			jTable.setDefaultEditor(Object.class,rtbsptEditor);

			jTable.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent evt) {
					jTableMouseEvent(evt);
				}
				public void mouseReleased(MouseEvent evt) {
					jTableMouseEvent(evt);
				}
			});
		}
		return jTable;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane= new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/** 
	 * return the list of columns 
	 **/
	protected abstract int[] getColumnsTypes();
	
	/** return the colum type **/
	public final int getColumnType(int col) {
	    assert col < getColumnsTypes().length && col >= 0 : 
			"Accessed column must be in range of column array.";
	    return getColumnsTypes()[col];
	}
	
	/** 
	 * get the column index <BR>
	 * this is the reverse of getColumnType(int col)
	 * **/
	public final int getColumnIndex(int type) {
	    int column[] = getColumnsTypes();
		for (int i = 0; i < column.length; i++) {
			if (column[i] == type) return i;
		}
		return -1;
	}
	

	/**
	 * get the Position of this Cell
	 */
	protected final Point getPositionOf(DataBySliceCell cell) {
		int x = getColumnIndex(cell.getType());
		int y = dbs.getOmod().getOMODOIndex(cell.getOmodo());
		return new Point(x,y);
	}
	
	/** 
	 * Create a new DataBySliceCell 
	 */
	protected abstract DataBySliceCell 
		createRBSC(OrderedMapOfDoublesObject omodo, int type);
	
	/** This is an array of HashMaps. The array will eventually 
	 *contain as many elements as there are columns in the table
	* and the individual hashmaps contain OrderedMapOfDoublesObject
	* entries.
	*/
	HashMap/*<DataBySliceCell>*/[] cellMemory;
	
	/**
	 * Return the number of columns in the table element of this
	 * panel. 
	 */
	public final int getTableColumnCount() {
		return getColumnsTypes().length;
	}
	
	/**
	 * Return the number of rows in the table element of this
	 * panel. 
	 */
	public final int getTableRowCount() {
		return dbs.getOmod().size();
	}
	
	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public final Object getValueAtX(int rowIndex, int columnIndex) {
		if (cellMemory == null)
			cellMemory= new HashMap[4];

		int t= getColumnType(columnIndex);
		if (cellMemory[t] == null)
			cellMemory[t]= new HashMap();
		
		// assure that the object we have just retrieved
		// is in the cellMemory table
		OrderedMapOfDoublesObject o=
			dbs.getOmod().getOMODObjectAtIndex(rowIndex);
		if (!cellMemory[t].containsKey(o))
			cellMemory[t].put(o, createRBSC(o, t));

		// return the cell memory entry
		return cellMemory[t].get(o);
	}
	
	/** return the column name (title) **/
	public abstract String getTableColumnName(int column);
	
	
	private static final Logger m_log = 
        Logger.getLogger( AbstractBySlicePanel.class );
	
	/**
	 * The Table Model
	 */
	class DBSTableModel extends AbstractTableModel {
	    
	    
	    public String getColumnName(int c) {
			return getTableColumnName(c);
		}
	    
		public int getColumnCount() {
			return getTableColumnCount();
		}

		public int getRowCount() {
			return getTableRowCount();
		}

		public Object getValueAt( int rowIndex, int columnIndex ) {
			return getValueAtX( rowIndex, columnIndex );
		}

		/**
		 * get the RateBySliceCell at this position in the table
		 */
		public DataBySliceCell getRBSCAt(int rowIndex,int columnIndex) {
			return (DataBySliceCell) getValueAt(rowIndex,columnIndex);
		}

		/**
		 * return true if this cell is editable
		 */
		public boolean isCellEditable(int row, int col) {
			return (editable && getRBSCAt(row,col).isEditable());
		}
	}

	/**
	 * The cell editor
	 *
	 */
	class DBSTableEditor extends DefaultCellEditor {

	
		public DBSTableEditor() {
			super(new JTextField());
		}

		public DataBySliceCell actuallyEditing= null;

		public Component getTableCellEditorComponent(
			JTable table,
			Object value,
			boolean isSelected,
			int row,
			int column) {
			actuallyEditing= (DataBySliceCell) value;
			jTable.repaint();
			return actuallyEditing.getEditor(this);
		}

		public boolean stopCellEditing() {
			if (actuallyEditing == null)
				return true;
			actuallyEditing.stopCellEditing();
			actuallyEditing= null;

			rtbsptModel.fireTableDataChanged();
			fireEditingStopped();
			rbspl.rbsDataChanged(); // advertise listener
			return true;
		}

		/**
		 * @see javax.swing.CellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue() {
			if (actuallyEditing == null)
				return null;
			return actuallyEditing.getOmodo();
		}

	}

	/**
	 * The Table Renderer
	 * (makes lines of different colors)
	 */
	class RTBSPTRenderer extends DefaultTableCellRenderer {
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

			DataBySliceCell rbsc= null;
			if (value != null) {
				rbsc= (DataBySliceCell) value;
				
			}

			boolean temp = ! (!isSelected || 
					(rtbsptEditor.actuallyEditing != null));
			
			TableTools.setRowColors(table,co,temp,hasFocus,row);
			
			Color f= null;
			if (rbsc != null)
				f= rbsc.getForegroundColor();

			if (f == null)
				f= UIManager.getColor("Table.textForeground");
			co.setForeground(f);
			
			co.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			return co;
		}
	}
	
	private boolean editable;
	
	/**
	 * change the editable state
	 */
	public final void setEditable(boolean b) {
		editable = b;
		getPlusButton().setEnabled(editable);
		getPlusButton().setVisible(editable);
		getDeleteButton().setEnabled(editable);
		getDeleteButton().setVisible(editable);
		_setEditable(editable);
	}
	
	/** change the editable properties of my components **/
	protected abstract void _setEditable(boolean b);
	
	
	/**
	 * @return Returns the rbs.
	 */
	public final DataBySlice getDbs() {
		return dbs;
	}	
	
	
}
