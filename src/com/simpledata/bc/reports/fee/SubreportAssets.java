/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 25 ao�t 2004
 */
package com.simpledata.bc.reports.fee;

import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.base.Subreport;
import com.simpledata.bc.reports.common.SpecializedDataRow;
import com.simpledata.bc.reports.common.SpecializedSubreport;

/**
 * This class supports the data model for the SubreportAssets. This
 * subreport is generated for the detailed fee report.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: SubreportAssets.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
public class SubreportAssets extends SpecializedSubreport{
    // CONSTANTS - Jasper xml file
    
    /**
     * This constant determines the name of the template .jrxml to use
     * for the report.
     */ 
    public static final String REPORT_NAME = "AssetsSubreport";
    /**
     * This constant determines the name of the parent directory of the template.
     */ 
    public static final String REPORT_DIRECTORY = "fee";
    
    /**
     * The following constants contain the names of the different elements
     * of the template. They must be the same as in the .jrxml file
     */
    
    /** parameters */
    private static final String NUMBER_COLUMN_TITLE = "NumberColumnTitle";
    private static final String VALUE_COLUMN_TITLE  = "ValueColumnTitle";
    private static final String TOTAL_ASSETS_TEXT   = "TotalAssetsText";
    private static final String TOTAL_ASSETS_VALUE  = "TotalAssetsValue";
    
    /** fields */
    private static final String ASSETS_TITLE        = "AssetsTitle";
    private static final String ASSETS_VALUE        = "AssetsValue";
    private static final String ASSETS_NUMBER       = "AssetsNumber";
    
    // CONSTRUCTOR
    
    /**
     * Contructs a new instance of the subreport. It also sets some fields.
     * 
     * @param evtmng             Event manager for the whole report generate 
     * 							 process.
     * @param assetsColumnTitle  Name of the assets column (first one)
     * @param rateColumnTitle    Name of the rate column (second one)
     * @param feeColumnTitle     Name of the fee column (third one)
     */
    SubreportAssets (RenderEventManager evtmng, 
            		 String numberColumnTitle,
            		 String valueColumnTitle) {
        m_report = new Subreport(evtmng, REPORT_DIRECTORY, REPORT_NAME);
        
        // set parameters
        m_report.addAppValue(NUMBER_COLUMN_TITLE, numberColumnTitle);
        m_report.addAppValue(VALUE_COLUMN_TITLE, valueColumnTitle);
        
        // set table columns
        m_report.addJasperField(ASSETS_TITLE);
        m_report.addJasperField(ASSETS_NUMBER);
        m_report.addJasperField(ASSETS_VALUE);
        m_report.freezeDefinitions();
    }
    
    // METHODS - default visibility
    
    /**
     * Set the TotalAssetsValue parameter. Above the first column.
     * @param value  Value shown in the report.
     */
    void setTotalAssetsValue(String value) {
        m_report.addAppValue(TOTAL_ASSETS_VALUE, value);
    }
    
    void setTotalAssetsText(String value) {
    	m_report.addAppValue(TOTAL_ASSETS_TEXT, value);
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
     * This represents one row of data in the SubreportAssets
     * report. 
     * 
     * Internal: This could be a way of abstracting away 
     * all of the addData methods in the different Subreports. 
     */
    static final class DataRow extends SpecializedDataRow {
        private static final int ARRAYSIZE = 3;
    
        String assetsTitle;
        String assetsNumber;
        String assetsValue;
        
        DataRow() {
            // Easy constructor
        }
        
        public Object[] toObjectArray() {
            Object[] result = {
                    assetsTitle,
                    assetsNumber,
					assetsValue};
            
            return result; 
        }
    }
}


