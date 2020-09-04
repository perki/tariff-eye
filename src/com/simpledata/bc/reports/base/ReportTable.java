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
 * @version $Id: ReportTable.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import javax.swing.table.AbstractTableModel;

import net.sf.jasperreports.engine.data.JRTableModelDataSource;


/**
 * The ReportTable class permits construction of a 
 * java table view of a Subreport. It implements
 * all the neccessary methods to serve as a table
 * datasource to Jasper. 
 *
 * AbstractTableModel is subclassed because it handles
 * some of the more nasty stuff with visitors fine, things
 * that we don't need in this context. 
 */
class ReportTable extends AbstractTableModel {
	
	private static final int SUBREPORTFIELDS = 4;
	
	private String[] m_columnNames; 
	private Object[][] m_data; 
	
	/**
	 * Construct table model (data, header) from a given
	 * Subreport Table. 
	 * @param table Data to extract
	 */
	void constructTable( Table table ) {
		TableFields header = table.getColumnHeaders();
		
		boolean subreport[] = new boolean[ header.size() ];
		
		int targetSize = 0; // size of resulting table
		int sourceSize = header.size(); // to avoid calling all the time
		for (int i=0; i<sourceSize; ++i) {
			// temporary store: is the index i a subreport field ? 
			subreport[i] = header.isSubreportField( i );
			
			if ( subreport[i] ) 
				targetSize += SUBREPORTFIELDS;
			else 
				targetSize += 1;
		}
		
		// now targetSize contains the width of the output table
		m_columnNames = new String[ targetSize ];
		
		// now build column header information.
		for (int i=0,j=0; i<sourceSize; ++i) {
			// i indexes into source, j into target.
			String base = header.getBaseName( i );
			if ( subreport[i] ) {
				m_columnNames[j + 0] = base + "Show";     // should this subreport be hidden ? 
				m_columnNames[j + 1] = base + "Report";   // JasperReport subreport
				m_columnNames[j + 2] = base + "Datasource"; // Data for the subreport
				m_columnNames[j + 3] = base + "Fields";   // Parameters for the subreport
				j += 4;
			}
			else {
				m_columnNames[j] = base;
				j += 1;
			}
		}
		
		m_data = new Object[ table.rows() ][];
		// i goes from 0 to the number of rows in the data
		for (int i=0; i<table.rows(); ++i) {
			// construct data row and fill it
			m_data[i] = new Object[ targetSize ];
			
			// iterate over source columns, filing target columns
			int t=0;
			// j goes from 0 to the number of rows in source data
			for (int j=0; j<sourceSize; ++j) {
				if ( subreport[j] ) {
					SubreportTreeItem sr = table.getReportCell( i, j );
					// should the subreport be visible ?
					if (sr != null) {
						m_data[i][ t + 0 ] = new Boolean( true );
						m_data[i][ t + 1 ] = sr.getReport();
						m_data[i][ t + 2 ] = new JRTableModelDataSource( sr.getDatasource() );  // recurse for table construction
						m_data[i][ t + 3 ] = sr.getFields();
					}
					else {
						m_data[i][ t ] = new Boolean( false );
					}
					t += 4;
				}
				else {
					Object o = table.getNormalCell( i, j );
					m_data[i][ t ] = o; 
					t += 1;
				}
			}  // iteration over all source columns
		}  // iteration over all source rows
	}  // constructTable
	
	/**
	 * Construct a report table from the data contained in
   * a given Subreport. 
	 * @param report Report to extract data from. 
	 */
	ReportTable( SubreportTreeItem report ) {
		constructTable( report.getTable() ); 
	}
	
	// AbstractTableModel implementation --------------------------
	
	/**
	 * Part of the AbstractTableModel interface: Returns the
	 * column count in this data source. 
	 */
	public int getColumnCount()
	{
		return m_columnNames.length;
	}


	/**
	 * Part of the AbstractTableModel interface: Returns the 
	 * name of the columnIndex'th column. 
	 */
	public String getColumnName(int columnIndex)
	{
		return m_columnNames[ columnIndex ];
	}


	/**
	 * Part of the AbstractTableModel interface: Returns the 
	 * number of rows that can be provided by this data source. 
	 */
	public int getRowCount()
	{
		return m_data.length;
	}


	/**
	 * Part of the AbstractTableModel interface: Return the 
	 * value of one table cell for insertion into the report. 
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		return m_data[rowIndex][columnIndex];
	}
}