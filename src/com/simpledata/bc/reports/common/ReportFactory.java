/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.reports.common;

import com.simpledata.bc.uicomponents.compact.CompactExplorer;

/**
 * This is the common interface for all the report factories. The ReportToolbox
 * class implements methods which creates different kind of report using
 * those factories.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: ReportFactory.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public interface ReportFactory {
    
    /**
     * Produce a report using the given compact tree.
     * 
     * @param tree  The tree containing the data.
     * @return      A report.
     */
    public Report produceReport (CompactExplorer tree); 
}
