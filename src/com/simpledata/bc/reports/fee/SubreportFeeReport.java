/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.reports.fee;

import java.awt.Image;
import java.util.LinkedList;

import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.ReportRenderContext;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;
import com.simpledata.bc.tools.Lang;

/**
 * This class implements methods to create a Fee Report with the Jasper
 * utilities. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportFeeReport.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
class SubreportFeeReport extends SpecializedSubreport {
    // CONSTANTS
    
    /**
     * This constant determines the name of the template .jrxml to use
     * for the report.
     */ 
    public static final String REPORT_NAME = "FeeReport";
    /**
     * This constant determines the name of the parent directory of the template.
     */ 
    public static final String REPORT_DIRECTORY = "fee";
    
    /**
     * The following constants contain the names of the different fields
     * of the template. They must be the same as the .jrxml file
     */
    private static final String REPORT_TITLE       = "ReportTitle";
    private static final String REPORT_FILE_NAME   = "ReportFileName";
    private static final String LEVEL1_TITLE       = "Level1Title";
    private static final String LEVEL1_ASSETS      = "Level1Assets";
    private static final String LEVEL1_VALUE       = "Level1Value";
    private static final String LEVEL1_TOTAL_TEXT  = "Level1TotalText";
    private static final String LEVEL1_TOTAL_VALUE = "Level1TotalValue";
    private static final String LEVEL2_TITLE       = "Level2Title";
    private static final String LEVEL2_ASSETS      = "Level2Assets";
    private static final String LEVEL2_TOTAL_TEXT  = "Level2TotalText";
    private static final String LEVEL2_TOTAL_VALUE = "Level2TotalValue";
    private static final String LEVEL2_VALUE       = "Level2Value";
    private static final String LEVEL3_TITLE       = "Level3Title";
    private static final String LEVEL3_ASSETS      = "Level3Assets";
    private static final String LEVEL3_VALUE       = "Level3Value";
    private static final String GRAND_TOTAL_TEXT   = "GrandTotalText";
    private static final String GRAND_TOTAL_VALUE  = "GrandTotalValue";
    private static final String LEVELS  		   = "Levels";
    private static final String ASSETS_SUBREPORT   = "AssetsSubreport";
    private static final String TARIFICATION_IMAGE = "TarificationImage";
    
    /**
     * English texts used on the report	
     */
    private static final String TXT_TOTAL = "Total";
    
    /**
     * Miscellaneous look'n'feel preferences
     */
    private static final String SECTION_NB_SEPARATOR = ".";
    
    // FIELDS
    /** Dynamic list of Interger. It contains the current section number */
    private LinkedList /* Integer */ m_counter;
    /** Current row of the report */
    private DataRow m_lastRow;
    /** Render context */
    private ReportRenderContext m_ctx;
    
    // CONSTRUCTOR
    /**
     * Create a new Fee Report object for Jasper rendering.
     *  
     * @param ctx    RenderEventManager. Handle the listeners that counts the 
     * 				 number of templates to be compilated. This single object is the
     *               same in all the subreport objects of the report.
     * @param title  Title of the report. It apears at the first line.
     */
    SubreportFeeReport(ReportRenderContext ctx, String title) {
        m_ctx = ctx;
        m_counter = new LinkedList();
        m_counter.addFirst(new Integer(0));
        m_lastRow = produceDataRow();
        
        m_report = new Subreport (ctx.getEventManager(), REPORT_DIRECTORY, REPORT_NAME);
        
        m_report.addAppValue(REPORT_TITLE, title);
        m_report.addTranslatedString(GRAND_TOTAL_TEXT);
        
        m_report.addJasperField(LEVEL1_TITLE);
        m_report.addJasperField(LEVEL2_TITLE);
        m_report.addJasperField(LEVEL3_TITLE);
        m_report.addJasperField(LEVEL1_VALUE);
        m_report.addJasperField(LEVEL2_VALUE);
        m_report.addJasperField(LEVEL3_VALUE);
        m_report.addJasperField(LEVEL1_ASSETS);
        m_report.addJasperField(LEVEL2_ASSETS);
        m_report.addJasperField(LEVEL3_ASSETS);
        m_report.addJasperField(LEVEL2_TOTAL_TEXT);
        m_report.addJasperField(LEVEL2_TOTAL_VALUE);
        m_report.addJasperField(LEVEL1_TOTAL_TEXT);
        m_report.addJasperField(LEVEL1_TOTAL_VALUE);
        m_report.addJasperField(LEVELS);
        m_report.addReportField(ASSETS_SUBREPORT);
        m_report.freezeDefinitions();
    }
    
    // METHODS - private
    /** Returns the current indentation level */
    private int currentLevel() {
        return m_counter.size();
    }
    
    /** Returns a string representation of the current section number */
    private String produceSectionNumber() {
        return produceSectionNumber(m_counter.size());
    }
    
    /** 
     * Returns a string representation of the current section number 
     * 
     * @param depth number of section levels that should be printed.
     * @return The string representation.
     */
    private String produceSectionNumber(int depth) {
        Object[] numbers = m_counter.toArray();
        assert depth <= numbers.length : "Cannot produce a so big section number.";
        StringBuffer result = new StringBuffer();
        for (int i=0; i<depth-1; i++) {
            result.append(String.valueOf(((Integer)numbers[i]).intValue()));
            result.append(SECTION_NB_SEPARATOR);
        }
        result.append(String.valueOf(((Integer)numbers[depth-1]).intValue()));
        return result.toString();
    }
    
    /** 
     * Add space at the beginning of a String, depend of the current indentation
     * level.
     * 
     * @param text Original text
     * @return Indentated text
     */ 
    private String spaceIndent(String text) {
        StringBuffer buf = new StringBuffer();
        for (int i = 3; i<currentLevel(); i++) 
            buf.append("  ");
        buf.append(text);
        return buf.toString();
    }
    
    /**
     * Increment current section number
     */
    private void increment() {
        int newSectionNumber = ((Integer)m_counter.getLast()).intValue()+1;
        m_counter.removeLast();
        m_counter.addLast(new Integer(newSectionNumber));
    }
    
    // METHODS - default visibility
    
    /**
     * Increase the indentation level of the report.
     *
     */
    void indent() {
        m_counter.addLast(new Integer(0));
    }
    
    /**
     * Decrease the indentation level of the report.
     *
     */
    void undent() {
        assert currentLevel() > 1 : "Miminum level reached. Unable to undent.";
        m_counter.removeLast();
    }
    
    /**
     * Add a new section in the report. A section is shown in the report as a line 
     * without any value. It also set the value 'total' shown at the
     * last line of the section.<BR>
     * The section must contain associated values or a subsection to be shown in the report. 
     * Consecutive calls to this method without any call of newValue or indent 
     * will replace the old section.
     * 
     * @param title Text shown in the report.
     * @param assets Little summary of the assets for the section.
     * @param total Value shown at the end of the section.
     */
    void newSection (String title, String assets, String total) {
        increment();
        switch (currentLevel()) {
        case 1:
            m_lastRow.level1Title = produceSectionNumber()+" "+title;
            m_lastRow.level1TotalText = Lang.translate(TXT_TOTAL)+" : "+
            produceSectionNumber()+" "+title;
            m_lastRow.level1TotalValue = total; 
            m_lastRow.level1Value = "";
            m_lastRow.level1Assets = "";
            break;
        case 2:
            m_lastRow.level2Title = produceSectionNumber()+" "+title;
            m_lastRow.level2TotalText = Lang.translate(TXT_TOTAL)+" : "+
            produceSectionNumber()+" "+title;
            m_lastRow.level2Assets = assets;
            m_lastRow.level2TotalValue = total; 
            break;
        default:
            m_lastRow.level3Title = spaceIndent(produceSectionNumber()+" "+title);
        	m_lastRow.level3Assets = assets;
        	m_lastRow.level3Value = "";
        	m_lastRow.assetsSubreport = null;
        	m_lastRow.levels = 3;
        	addData (m_lastRow);
        }
    }
    
    /**
     * Add a new fee line to the report, with a description and three values. 
     * @param title       Description of the investment place.
     * @param assets      Little summary of the assets for the value.
     * @param value       Value of the fee.
     * @param subreport   Subreport which contains 
     *                    detailed structure of the assets or the transactions. 
     */
    void newValue (String title, 
    			   String assets,
            	   String value, 
            	   SubreportAssets subreport) {
        
        // increment section counter
       increment();
      
       // produce data
       m_lastRow.assetsSubreport = (subreport == null) ? null : subreport.getReport();       
       switch (currentLevel()) {
       	case 1:
       		m_lastRow.level1Title = produceSectionNumber()+" "+title;
       		m_lastRow.level1Assets = "";
       		m_lastRow.level1Value = value;
       		m_lastRow.levels = 1;
       		m_lastRow.level1TotalValue = value;
       		m_lastRow.level1TotalText = Lang.translate(TXT_TOTAL)+" : "+
            							produceSectionNumber()+" "+title;
       		m_lastRow.levels = 1;
       		break;
        case 2:
            m_lastRow.level2Title = produceSectionNumber()+" "+title;
            m_lastRow.level2Assets = assets;
            m_lastRow.level2Value = value;
            m_lastRow.levels = 2;
            break;
        default:
        	m_lastRow.level2Value = "";
        	m_lastRow.level3Title = spaceIndent(produceSectionNumber()+" "+title);
            m_lastRow.level3Assets = assets;
        	m_lastRow.level3Value = value;
            m_lastRow.levels = 3;
       }
       addData(m_lastRow);    
    }
    
    /**
     * Set the Grand Total value, shown at the last page of the report
     * @param total The Grand Total value.
     */
    void setGrandTotal (String total) {
        m_report.addAppValue(GRAND_TOTAL_VALUE, total);
    }
    
    /**
     * Set the value of the ReportFileName field. Above the title.
     * @param fileName
     */
    void setReportFileName (String fileName) {
    	m_report.addAppValue(REPORT_FILE_NAME, fileName);
    }
    
    /**
     * Set the tarification image of the report
     * @param tarificationImage the image to put.
     */
    void setTarificationImage (Image tarificationImage) {
    	m_report.addAppValue(TARIFICATION_IMAGE, tarificationImage);
    }
    
    /**
	 * Produce an empty row instance. SubreportMasterReport is 
	 * a producer of DataRows. 
	 * 
	 * @return An empty DataRow.
	 */
    DataRow produceDataRow() {
        return new DataRow();
    }
    
    /**
	 * This represents one row of data in the SubreportFeeReport. 
	 * 
	 * Internal: This could be a way of abstracting away 
	 * all of the addData methods in the different Subreports. 
	 */
    static final class DataRow extends SpecializedDataRow {
        private static final int ARRAYSIZE = 15;
        
        String level1Title;
        String level2Title;
        String level3Title;
        String level1Value;
        String level2Value;
        String level3Value;
        String level1Assets;
        String level2Assets;
        String level3Assets;
        String level2TotalText;
        String level2TotalValue;
        String level1TotalText;
        String level1TotalValue;
        int levels = 0;
        SubreportTreeItem assetsSubreport;
        
        DataRow() {
            // easy constructor
        }
        
        public Object[] toObjectArray() {
            Object[] result = {
                     level1Title,
                     level2Title,
                     level3Title,
					 level1Value,
                     level2Value,
                     level3Value,
					 level1Assets,
					 level2Assets,
					 level3Assets,
                     level2TotalText,
                     level2TotalValue,
                     level1TotalText,
                     level1TotalValue,
                     new Integer(levels), 
                     assetsSubreport
            };
            return result;
        }
        
        
    }
}
