/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

/**
 * Package containing all classes that generate a report. 
 * It relies heavily on the toolbox in base. 
 */
package com.simpledata.bc.reports.tarification;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import com.simpledata.bc.reports.scriptlets.EmptyScriptlet;


/**
 * The TableOfContents scriptlet constructs a Table of contents 
 * from group occurrence in toplevel template MasterReport. 
 *
 * This table of contents is then output inside the subreport that
 * is located at the very end of the MasterReport.
 *  
 * This class produces and fills up a data source that is used for 
 * output of this subreport: The datasource must first be produced 
 * since Jasper wants to know about it, but only then it can be filled
 * while building the rest of the report. 
 * 
 * This scriptlet sets the variable 'TOCDatasource' in the report
 * that uses it to the contained data source. 
 * 
 * This new version uses the title/content design introduced by the TocManager
 * utility. But it's not strongly linked to this data structure. It uses the
 * MasterReport dataSource to retrieve title names.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: TableOfContentsScriptlet.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class TableOfContentsScriptlet extends EmptyScriptlet
{
	// TODO Refactor this class. Correct the numbering
	
	/** Parameter that contains Jasper page number */
	private final static String PAGE_NUMBER = "PAGE_NUMBER";
	/** Constant for the level field in MasterReport template */
	private final static String FIELD_LEVEL = "Level";
	/** Constant for the title field in MasterTemplate. */
	private final static String FIELD_TITLE = "Title1";
	/** Constant for the subTitle field in MasterTemplate. */
	private final static String FIELD_SUBTITLE = "Title2";
	/** Constant for the subSubTitle field in MasterTemplate. */
	private final static String FIELD_SUBSUBTITLE = "Title3";
	/** Constant for the SubreportShow field in the MasterTemplate */
	private final static String FIELD_SUBREPORTSHOW = "SubreportShow";
	
	/** Datasource internally */
	private TOCDataSource m_datasource;
	
	private String m_lastTitle;
	private String m_lastSubTitle;
	private String m_lastSubSubTitle;
	private int m_lastPage;
	private int m_lastSubPage;
	private int m_lastSubSubPage;
	
		
	/**
	 * Constructs a TableOfContentsScriptlet.
	 */
	public TableOfContentsScriptlet( ) {
		super();
		m_datasource = new TOCDataSource();
	}
	
	/**
	 * For testing purposes, return data source. 
	 */
	TOCDataSource getInternalDatasource() {
		return m_datasource;
	}
	
	/**
	 * Returns current jasper page number. 
	 * @return page number
	 */
	int getPageNumber() throws JRScriptletException {
		Object o = this.getVariableValue( PAGE_NUMBER );
		
		if (o == null) return 1;        // means that its not initted yet, so first page
		return ((Integer) o).intValue();
	}
	
	/**
	 * Returns the data source that is the basis for the
	 * toc subreport. Note that this data source is not
	 * filled unless the report has been totally constructed.
	 * @return The Datasource for displaying the TOC. 
	 */
	public JRRewindableDataSource getDataSource() {
		assert m_datasource != null : 
			"Datasource is never null";
		
		return new JRTableModelDataSource( m_datasource );
	}
	
	/**
	 * This is called before any group level is entered. 
	 */
	public void beforeDetailEval() throws JRScriptletException {
		boolean isTitle = !((Boolean)this.getFieldValue( FIELD_SUBREPORTSHOW )).booleanValue();
		
		if (isTitle) {
			int page = getPageNumber();
			String title = ( (String) this.getFieldValue( FIELD_TITLE ) );
			String subTitle = ( (String) this.getFieldValue( FIELD_SUBTITLE ) );
			String subSubTitle = ( (String) this.getFieldValue( FIELD_SUBSUBTITLE));
			int level = ((Integer)this.getFieldValue(FIELD_LEVEL)).intValue();
			
			if ( m_lastTitle==null ||(! m_lastTitle.equals( title )) ) {
				// Level 1 toc
				if (title != null)
					m_datasource.addData(title, page, 1);
			}
			if ( m_lastSubTitle==null ||(! m_lastSubTitle.equals( subTitle )) ) {
				if (level >= 2)
					m_datasource.addData(subTitle, page, 2);
			}
			
			if (m_lastSubSubTitle==null||(!m_lastSubSubTitle.equals( subSubTitle))){
				if (level >= 3)
					m_datasource.addData(subSubTitle, page, 3);
			}
			
			m_lastTitle = title;
			m_lastSubTitle = subTitle;
			m_lastSubSubTitle = subSubTitle;	
		}
	}
	
	/**
	 * This is the subclass that acts as data source to jasper.
   * The Jasper data source for the TOC contains title, subtitle
	 * and subsubtitle texts. For each of these titles the datasource
	 * contains a page number. 
	 *
	 * It implements an AbstractTableModel interface for usage with
	 * the java table model data source. 
	 */
	static class TOCDataSource extends AbstractTableModel {
		private static final String[] m_headers = {
			"Section", "Page", "Level" };
		
		private final ArrayList /*<Object[]>*/ m_data;
		
		TOCDataSource() {
			m_data = new ArrayList();
		}
		
		void addData(String section, int page, int level) {
			Object[] row = {section, new Integer(page), new Integer(level)};
			m_data.add(row);
		}
		
		// #### METHODS - Implement AbstractTableModel ########################
		
		/**
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		public int getColumnCount() {
			return m_headers.length;
		}

		/**
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount() {
			return m_data.size();
		}

		/**
		 * Part of the AbstractTableModel interface: Returns the 
		 * name of the columnIndex'th column. 
		 */
		public String getColumnName(int columnIndex)
		{
			return m_headers[columnIndex];
		}
		
		/** 
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object[] row = (Object[])m_data.get(rowIndex);
			return row[columnIndex];
		}
	}
	
	
}
