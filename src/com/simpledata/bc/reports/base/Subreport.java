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
 * @version $Id: Subreport.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */

/**
 * Package containing all helper classes that form 
 * the abstract report/subreport tree. This tree
 * can directly be visited and produced as a report. 
 */
package com.simpledata.bc.reports.base;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;

import org.apache.log4j.Logger;


/**
 * Encapsulates all the data that is needed to 
 * graphically generate one subreport, that is 
 * part of a report. 
 * 
 * A single report can be constructed of an endless
 * hierarchy of such subreports, although a single
 * subreport cannot have two superreports currently. 
 * Note that this is connected to the problem of 
 * rewinding datasources and caching and could be 
 * solved. 
 * 
 */
public class Subreport implements SubreportTreeItem {
	
	private static final Logger m_log = Logger.getLogger( Subreport.class ); 
	
	/** Represents Jasper template to produce report with. */
	private Template m_template; 
	/** Fields that are passed to the report as Jasper parameters */
	private ReportFields m_fields; 
	/** Table that Jasper reports in the given template */
	private Table m_table; 
		
	// Interface that allows data access to package

	/**
	 * Return the data table for this Subreport. 
	 * @return data table. 
	 */
	public Table getTable() {
		return m_table;
	}
	
	/**
	 * Return the data table for this Subreport. 
	 * @return data table. 
	 */
	public AbstractTableModel getDatasource() {
		return new ReportTable( this );
	}
	
	/**
	 * Return the report template for this Subreport. This
	 * method proxies to Template. 
	 * @return template 
	 */
	public JasperReport getReport() {
		return m_template.getReport();
	}
	
	/**
	 * Returns the parameters for the Jasper report. 
	 * 
	 * @return Fields that must be passed to Jasper. 
	 */
	public Map getFields() {
		return m_fields.produceJasperFields();
	}
	
	/**
	 * Subreport constructor. 
	 * @param ctx Context for this report rendering. This is an instance
	 *            that will be the same for one whole report, not just 
	 *            different subreports. 
	 * @param report Name of Report, ie: 'tarification', subdir
	 *               of resources/reports/templates
	 * @param subReport Name of Subreport, ie: 'MainReport', also
	 *                  name of Jasper template in said subdirectory. 
	 */
	public Subreport( RenderEventManager ctx, String report, String subReport ) {
		m_template = new Template( report, subReport ); 
		m_table = new Table(); 
		m_fields = new ReportFields();
		
		ctx.notifyOfTemplateAddition( report, subReport );
	}
	
	// Proxy to Table --------------------------------------------
	
	/**
	 * Adding fields: Add Jasper Fields. See Table class 
	 * for more description. 
	 * @param name Name of field. 
	 */
	public void addJasperField( String name ) {
		m_table.addJasperField( name ); 
	}
	
	/**
	 * Adding fields: Add report fields. See Table class for 
	 * more description. 
	 * @param name Name of the field to add. 
	 */
	public void addReportField( String name ) {
		m_table.addReportField( name ); 
	}
	
	/**
	 * Freeze the fields that the table has. This MUST be called 
	 * before starting to add data. 
	 */
	public void freezeDefinitions() {
		m_table.freezeDefinitions(); 
	}
	
	/**
	 * Add one whole row of data to the report. The fields must 
	 * each have the correct type. Report fields will get 
	 * verified as to being instances of SubreportTreeItems. 
	 * @param data Data row to add. 
	 */
	public void addRow( Object[] data ) {
		m_table.addDataRow( data ); 
	}
	
	// Proxy to ReportFields ------------------------------------
	/**
	 * Add an application string to the collection. This method
	 * proxies to ReportFields. 
	 * @param name Name of Application string. 
	 * @param value Value of Application. 
	 */
	public void addAppValue( String name, Object value ) {
		m_fields.addAppValue( name, value );
	}
	
	/**
	 * Add a translation string to the collection. 
	 * Only the translation key must be given, since the 
	 * translation can be retrieved using the application 
	 * ways of doing so. This method proxies to ReportFields
	 * class. 
	 * @param name Name of string to translate. 
	 */
	public void addTranslatedString( String name ) {
		m_fields.addTranslatedString( name );
	}
	
	/**
	 * Render the report into a device independent 
	 * representation. 
	 *
	 * @return Printable report or null on failure.
	 */
	public JasperPrint renderReport() {
		JasperPrint print = null;
		try {
			print = JasperFillManager.fillReport(
				getReport(), 
				getFields(), 
				new JRTableModelDataSource( getDatasource() )
			);
		}
		catch (JRException e) {
			// empty, returning null default
			m_log.error( "renderReport caught exception", e );
		}
		
		return print;
	}

}

