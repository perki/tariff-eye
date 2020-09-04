/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: BCOptionJTable.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption.tools;

import java.awt.Component;
import java.awt.ComponentOrientation;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.uicomponents.tools.CurrencyChooserCombo;
import com.simpledata.bc.uicomponents.tools.MoneyValueInput;
import com.simpledata.bc.uitools.SNumField;
import com.simpledata.bc.uitools.TableTools;

/**
 * is a Jtable that can contains various 
 * type of data (from BCoption standards)
 */
public class BCOptionJTable extends JTable {
	Interface ift;
	JTable jTable;
	
	private static ImageIcon DEFAULT_BOOLEAN_TRUE_ICON 
	= Resources.checkBoxStateChecked;
	private static ImageIcon DEFAULT_BOOLEAN_FALSE_ICON 
		= Resources.checkBoxStateCheckedNot;


	
	public BCOptionJTable(BCOptionJTable.Interface ift) {
		super();
		jTable = this;
		this.ift = ift;
		setColumnModel(new MaColumnModel());
		
		setModel(new MAModel());
		
		setDefaultEditor(Boolean.class, new MABooleanEditor());
		setDefaultEditor(Money.class, new MAMoneyEditor());
		setDefaultEditor(Currency.class, new MACurrencyEditor());
		setDefaultEditor(Integer.class, new MAIntegerEditor());
		setDefaultRenderer(Boolean.class, new MaBooleanRenderer());
		setDefaultRenderer(Money.class, new MaMoneyRenderer());
		setDefaultRenderer(Currency.class, new MaCurrenyRenderer());
		setDefaultRenderer(Integer.class, new MaIntegerRenderer());
	}
	
	
	
	
	class MaMoneyRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			String str = SNumField.formatNumber(ift.getMoney(row,col)
						.getValueDouble(), 2, true);
			
			Component co = super.getTableCellRendererComponent(table, str,
					isSelected, hasFocus, row, col);
			TableTools.setRowColors(jTable, co, isSelected, hasFocus, row);
			co.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			return co;
		}
	}
	
	class MaIntegerRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			TableObject to = ift.getTOAt(row,col);
			String str = to == null ? "" : 
				SNumField.formatNumber(""+to.getValue(),0,true);
			Component co = super.getTableCellRendererComponent(table, str,
					isSelected, hasFocus, row, col);
			TableTools.setRowColors(jTable, co, isSelected, hasFocus, row);
			co.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			return co;
		}
	}
	
	class MaCurrenyRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			String str = ift.getMoney(row,col).getCurrency()
						.currencyCode();
			Component co = super.getTableCellRendererComponent(table, str,
					isSelected, hasFocus, row, col);
			TableTools.setRowColors(jTable, co, isSelected, hasFocus, row);
			co.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			return co;
		}
	}
	
	
	
	
	
	//--------------- Boolean Properties --------------------//
	private ImageIcon[] booleanIcon;
	private String[] booleanText;
	
	private void initBooleanIconText() {
		if (booleanIcon == null) {
			booleanIcon = new ImageIcon[2];
			booleanIcon[0] = DEFAULT_BOOLEAN_FALSE_ICON;
			booleanIcon[1] = DEFAULT_BOOLEAN_TRUE_ICON;
		}
		if (booleanText == null) 
			booleanText = new String[] {"",""};
		
	}
	
	/** change the icon for boolean values **/
	public void setBooleanIconText(boolean b,ImageIcon newIcon,String text) {
		if (booleanIcon == null) initBooleanIconText();
		if (booleanText == null) initBooleanIconText();
		booleanIcon[b ? 1 : 0] = newIcon;
		booleanText[b ? 1 : 0] = text;
	}
	
	private ImageIcon getBooleanIcon(boolean b) {
		if (booleanIcon == null) initBooleanIconText();
		return booleanIcon[b ? 1 : 0];
	}
	private String getBooleanText(boolean b) {
		if (booleanText == null) initBooleanIconText();
		return booleanText[b ? 1 : 0];
	}
	
	class MaBooleanRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int col) {
			TableObject to = ift.getTOAt(row,col);
			if (to == null || ! ( to.getValue() instanceof Boolean)) {
				return new JLabel("?????");
			}
			boolean b = ((Boolean) to.getValue()).booleanValue();
			JLabel co = new JLabel(
					getBooleanText( b),
					getBooleanIcon( b),SwingConstants.LEFT);
			co.setOpaque(true);
			co.setHorizontalTextPosition(SwingConstants.LEADING);
			TableTools.setRowColors(jTable, co, isSelected, hasFocus, row);
			co.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			return co;
		}
	}
	
	/**
	 * The cell editor, for boolean
	 * please uses 
	 */
	class MABooleanEditor extends DefaultCellEditor {
		JTable table;
		public MABooleanEditor() {
			super(new JTextField());
		}
		public Component getTableCellEditorComponent(JTable table,
				final Object value, boolean isSelected, int row, int column) {
			this.table = table;
			final TableObject to = ift.getTOAt(row,column);
			if (to == null) return null;
			if (! (to.getValue() instanceof Boolean)) return null; 
			to.setValue(
					new Boolean(! ((Boolean) to.getValue()).booleanValue()));
			stopCellEditing();
			return null;
		}
		
		public boolean stopCellEditing() {
			fireEditingStopped();
			if (table != null)
				table.repaint();
			return true;
		}
	}
	
	
	/**
	 * The cell editor
	 *  
	 */
	class MAMoneyEditor extends DefaultCellEditor {
		public MAMoneyEditor() {
			super(new JTextField());
		}
		public Component getTableCellEditorComponent(JTable table,
				final Object value, boolean isSelected, int row, int column) {
			JTextField jtf = 
				new MoneyValueInput(ift.getMoney(row,column), false) {
				public void editionStopped() {
					((BCOption) value).fireDataChange();
					stopCellEditing();
				}
				public void editionStarted() {
					
				}
			};
			jtf.setBorder(new EmptyBorder(0, 0, 0, 0));
			
			return jtf;
		}
		public boolean stopCellEditing() {
			fireEditingStopped();
			return true;
		}
	}
	
	/**
	 * The cell editor
	 *  
	 */
	class MACurrencyEditor extends DefaultCellEditor {
		JTable table;
		public MACurrencyEditor() {
			super(new JComboBox());
		}
		public Component getTableCellEditorComponent(JTable table,
				final Object value, boolean isSelected, int row, int column) {
			this.table = table;
			JComboBox jcb = new CurrencyChooserCombo(ift.getMoney(row,column)) {
				protected void valueChanged() {
					((BCOption) value).fireDataChange();
					stopCellEditing();
				}
			};
			jcb.setBorder(new EmptyBorder(0, 0, 0, 0));
			return jcb;
		}
		public boolean stopCellEditing() {
			fireEditingStopped();
			if (table != null)
				table.repaint();
			return true;
		}
	}
	
	/**
	 * The cell editor, for int
	 * please uses 
	 */
	class MAIntegerEditor extends DefaultCellEditor {
		JTable table;
		public MAIntegerEditor() {
			super(new JTextField());
		}
		public Component getTableCellEditorComponent(JTable table,
				final Object value, boolean isSelected, int row, int column) {
			this.table = table;
			final TableObject to = ift.getTOAt(row,column);
			if (to == null) return null;
			final SNumField jcb = new SNumField(""+to.getValue(),false){
				
				public void stopEditing() {
					to.setValue(getInteger());
					stopCellEditing();
				}

				public void startEditing() {
					
				}};
			jcb.setBorder(new EmptyBorder(0, 0, 0, 0));
			return jcb;
		}
		public boolean stopCellEditing() {
			fireEditingStopped();
			if (table != null)
				table.repaint();
			return true;
		}
	}
	
	public void fireTableDataChanged() {
		((MAModel) getModel()).fireTableDataChanged();
	}
	
	/**
	 * an interface for Other objects than Money
	 */
	public interface TableObject {
		public Object getValue();
		public void setValue(Object o);
	}
	
	
	public interface Interface {
		/** 
		 * return the money value contained at this column
		 **/ 
		public Money getMoney(int row,int column);
		/** 
		 * return the integer value contained at this column
		 **/ 
		public TableObject getTOAt(int row,int column);
		
		public int getColumnCount();
		
		/** return the column name * */
		public String getColumnName(int column);
		
		/**
		 * min width is also used for weigthing
		 */
		public int getColumnMinWidth(int c);
		
		/**
		 * @return known class type : Money.class, Currency.class....
		 */
		public Class getColumnClass(int c);
		
		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount();
		
		/**
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int row, int col);
		
		/**
		 * return true if this cell is editable
		 */
		public boolean isCellEditable(int row, int col);
	}
	
	/**
	 * The column Model
	 */
	class MaColumnModel extends DefaultTableColumnModel {
		
		//TODO use it or remove it
		private int getWidth(int col) {
			int max = jTable.getWidth();
			int min = ift.getColumnMinWidth(col);
			if (max <= min) return min;
			
			// get all other column width
			int base = 1;
			for (int i = 0; i < ift.getColumnCount(); i++) {
				base += ift.getColumnMinWidth(i);
			}
			if (base >= max) return min;
			
			return max * min / base;
		}
		
		public TableColumn getColumn(int columnIndex) {
			TableColumn tc = super.getColumn(columnIndex);
			
			getWidth(columnIndex); // just to prevent eclipse from weening
			//tc.setMinWidth(ift.getColumnMinWidth(columnIndex));
			//tc.setWidth(getWidth(columnIndex));
			return tc;
		}
	}
	
	class MAModel extends AbstractTableModel {
		public int getColumnCount() { return ift.getColumnCount(); }
		public String getColumnName(int c) { return ift.getColumnName(c); }
		public Class getColumnClass(int c) { return ift.getColumnClass(c);}
		public int getRowCount() { return ift.getRowCount(); }
		public Object getValueAt(int r, int c) {return ift.getValueAt(r,c);}
		public boolean isCellEditable(int r, int c) {
			return ift.isCellEditable(r,c);}
	}
}

/*
 * $Log: BCOptionJTable.java,v $
 * Revision 1.2  2007/04/02 17:04:28  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.18  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.17  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.16  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.15  2004/09/07 16:21:03  jvaucher
 * - Implemented the DispatcherBounds to resolve the feature request #24
 * The calculus on this dispatcher is not yet implemented
 * - Review the feature of auto select at startup for th SNumField
 *
 * Revision 1.14  2004/09/03 13:21:05  jvaucher
 * Fixed a SNumField edition problem
 *
 * Revision 1.13  2004/09/02 16:05:51  jvaucher
 * - Ticket #1 (JTextField behaviour) resolved
 * - Deadlock at loading problem resolved
 * - New kilo and million feature for the SNumField
 *
 * Revision 1.12  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/06/28 10:38:48  perki
 * Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 * Revision 1.10  2004/05/31 16:14:45  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/05/22 08:39:35  perki
 * Lot of cleaning
 *
 * Revision 1.8  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.7  2004/05/19 16:39:58  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/18 17:04:26  perki
 * Better icons management
 *
 * Revision 1.5  2004/05/18 15:51:26  perki
 * Better icons management
 *
 * Revision 1.4  2004/05/18 10:48:38  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/05/18 10:10:27  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/14 16:00:41  perki
 * Nice option table
 *
 * Revision 1.1  2004/05/14 15:27:02  perki
 * Nice option table
 *
 */