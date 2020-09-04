/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.reports.fee;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeItem;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.reports.base.RenderEventManager;
import com.simpledata.bc.reports.common.Report;
import com.simpledata.bc.reports.common.ReportCountListener;
import com.simpledata.bc.reports.common.ReportFactory;
import com.simpledata.bc.reports.common.ReportRenderContext;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.compact.CompactTarifLinkNode;
import com.simpledata.bc.uicomponents.filler.FillerData;
import com.simpledata.bc.uicomponents.filler.FillerNode;
import com.simpledata.bc.uitools.SNumField;

/**
 * This class constructs a fee report. Using a compact tree.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: FeeReportFactory.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
public class FeeReportFactory implements ReportFactory {
    //  CONSTANTS
    
    /** Title of the reports window */
    private static final String DETAIL_REPORTNAME = "Fee detailed report"; 
    private static final String SUMMARY_REPORTNAME = "Fee report summary";
    private static final String OF = "of";
    private static final String FROM_TARIFICATION = "Regarding tarif from %0.";
    
    // FIELDS
    private SubreportFeeReport m_feeReport;
    private final ReportRenderContext m_context;
    private final ReportCountListener m_counter;
    private static final Logger m_log = Logger.getLogger( FeeReportFactory.class );
    private FillerData m_filler;
    /** Report mode */
    private final boolean m_details;
    
    // CONSTRUCTOR
    /**
     * Main constructor.
     * @param details If this flag is set to true, details are shown in the fee
     * report.
     */
    public FeeReportFactory(boolean details) {
    	m_details = details;
   		m_feeReport = null;
   		m_filler = null;
       m_context = new ReportRenderContext();
       m_counter = new ReportCountListener();
       RenderEventManager evtmgr = m_context.getEventManager();
       
       assert evtmgr != null : 
			"Event manager cannot be constructed null";
		
       evtmgr.addListener( m_counter );
   }
   
   // METHODS - default visibility
   
   /**
    * This method produces the report.
    * 
    * @param exp   The compact tree the report represents.
    * @return      A fee report. It contains a single subreport using the
    *              SubreportFeeReport class.  
    */
    public Report produceReport (CompactExplorer exp) {
    	m_context.computeToc(exp);
   		m_filler = exp.getFillerData();
   		// find names
   		Tarification t = exp.getTarification();
   		TarificationHeader th = t.getHeader();
   		File file = th.myLoadingLocation();
   		String fileName;
   		if (file != null)
   			fileName = file.getName();
   		else
   			fileName = Lang.translate("Untitled");
   		String tarificationName = th.getTitle();
   		// produce title & subtitle
   		StringBuffer title = new StringBuffer();
   		title.append((m_details) ? Lang.translate(DETAIL_REPORTNAME) 
   				                : Lang.translate(SUMMARY_REPORTNAME));
   		title.append(" : ");
   		title.append(fileName);
   		
   		String subTitle = Lang.translate(FROM_TARIFICATION, tarificationName);
   		m_feeReport = new SubreportFeeReport(m_context, title.toString());
   		m_feeReport.setReportFileName(subTitle);
   		// set report image
   		BufferedImage bi = new BufferedImage(32,32,Image.SCALE_DEFAULT);
   		Graphics2D g2d = (Graphics2D) bi.getGraphics();
   		g2d.setColor(Color.WHITE);
   		g2d.fillRect(0,0,32,32);
   		g2d.drawImage(th.getIcon().getImage(),null,null);
   		m_feeReport.setTarificationImage(bi);
   		// produce report
   		consume (exp.getTreeRoot());
   		
   		m_log.debug("Event listener count "+m_counter.getReportsCount()+" templates.");
   		return new Report( m_feeReport.getReport(), m_counter.getReportsCount());
   }
   
   // METHODS - linearize
   
   	/**
   	 * Use this method to generate the report contents.
   	 * 
   	 * @param node   Root of the compact tree.
   	 */
   	private void consume(CompactNode node) {
   	    String grandTotal = Currency.getDefaultCurrency() +" "+ node.getValueAt(0);
   	    ArrayList children = node.getChildrenAL(); 
		m_feeReport.setGrandTotal(grandTotal);
		// get all child nodes of this node
		Iterator it = children.iterator();
		
		if ( it.hasNext() ) {
			visitChildren(it);
		} 
   	}
   	
   	private String formatMoney(Money m) {
    	String numPart = SNumField.formatNumber(m.getValueDouble(), 2, true);
    	return m.getCurrency() +" "+ numPart;
    }
   	
   	/** Produce the string with the assets distribution for the given node */
   	private String produceAssetsDistribution(CompactNode node) {
   		FillerNode fn = m_filler.getFillerNode(node);
	        StringBuffer assets = new StringBuffer(); 
		if (fn != null) {
			assets.append(" "+formatMoney(fn.getAmount()));
			if (fn.getParent() != null) {
				assets.append(" ("+SNumField.formatNumber(fn.getPercentage()*100,1,true));
				assets.append(" % ");
				assets.append(Lang.translate(OF));
				assets.append(" "+node.getParent()+")");
			}
		}
   		return assets.toString();
   	}
   	
   	/** Produce a line of report for the given node */
   	private void produceReportLine(CompactNode node) {
   		// compute values shown in a report line
   		String title = node.toString();
   		String assets = produceAssetsDistribution(node);
   		String currency = Currency.getDefaultCurrency().toString();
   		String value = currency + " " +node.getValueAt(0).toString() ;
   		m_log.debug("Found a node "+title+", value = "+value);
   		
   		// produce line
   		if (node instanceof CompactTarifLinkNode) {
   			// This node is only a graphic reference
   			// for another tarif. We won't print it, but 
   			// we'll memorize a reference.
   			m_log.debug("...is CompactTarifLinkNode");
   		} else {
   			if (node.isLeaf()) {
   				m_log.debug("...is leaf");
   				Tarif  t = node.getFirstTarif();
   				
   				// get worksheet
   				TarifTreeItem tti = null;
   				if (t != null) {
   					tti = (TarifTreeItem)t.getWorkSheet();
   				}
   				if (tti != null) {
   					// Do not produce any line in the following cases:
   					if (tti instanceof WorkPlaceWithOnlyOptions)
   						return;
   					// Produce the line
   					if (m_details) {
   						// detailed version
   						FeeConstructor visitor = new FeeConstructor(m_context);
   						tti.visit(visitor);
   						m_feeReport.newValue(title.toString(), 
   								assets.toString(),
								value, 
								visitor.m_subreport);
   					} else { // non detailed version
   						m_feeReport.newValue(title.toString(), assets.toString(), value, null); 
   					}
   				}
   			} else { // it's a node with children
   				m_feeReport.newSection(title.toString(), assets.toString(), value); 
   				m_feeReport.indent();
   				ArrayList children = node.getChildrenAL();
   				visitChildren(children.iterator());
   				m_feeReport.undent();
   			}
   		}
   	}
   	
   	/** 
   	 * Recursive method used to construct the report. For each node of the tree
   	 * a new section is generated in the report.
   	 * The children are subsections. <BR>
   	 * If m_details constant is set to true. Subreport are generated for each
   	 * place with a detailed view of the assets and the fees taken on them.
   	 */
   	private void visitChildren(Iterator it) {
   	    while (it.hasNext()) {
   	        CompactNode node = (CompactNode) it.next();
   	        produceReportLine(node);
   	    }
   	}
}