/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.reports.tarification;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.reports.base.SubreportTreeItem;
import com.simpledata.bc.reports.common.ReportRenderContext;
import com.simpledata.bc.reports.common.ReportSection;
import com.simpledata.bc.reports.common.SectionNumber;
import com.simpledata.bc.reports.common.TocManager;
import com.simpledata.bc.uicomponents.compact.CompactNode;



/**
 * This class uses the TocManager object, to construct the MasterReport.
 * It will take all items (tiltles and sections) embeeded into the toc
 * and create the corresponding TarificationReport.
 * <BR>
 *  Note that this class previously did also the work of the TocManager.
 * 
 * @version $Id: CompactNodeLinearizer.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved
 */
class CompactNodeLinearizer {
	/**
	 * <code>REPORTNAME</code> is the name of the report that is
	 * generated here. 
	 */
	public static final String REPORTNAME = "tarification";
	
	/**
	 * From this tree depth on the titles are not 
	 * real titles that get indented, but fake ones
	 * that live on the same level as the upper level 
	 * title, but change their name. 
	 */
	private static final int REALTITLELIMIT = 3; 
	/** Space limit on the m_sectionNumber array. */
	private static final int MAXSECTIONS = 20; 
	/** Seprarator between numbering and title */
	private final static String SEPARATORSTRING = ": ";
	
	/** Logger */
	private static final Logger m_log = Logger.getLogger(CompactNodeLinearizer.class);
	/** Subreport that results. */
	private SubreportMasterReport m_result; 
		
	/** Render context */
	private ReportRenderContext m_context; 
	/** Current indentation level of the report */
	private int m_currentLevel;
	/** A row of the MasterReoport */
	private final SubreportMasterReport.DataRow m_row;		
		
	/**
	 * Construct a linearizer. 
	 * 
	 * @param context Context that this render action will take place in. 
	 */
	CompactNodeLinearizer( ReportRenderContext context ) {
		m_context = context; 
		
		m_result = new SubreportMasterReport( m_context.getEventManager(), REPORTNAME ); 
		m_row  = m_result.produceDataRow();
		
		m_currentLevel = 0; 
	}
	
	/**
	 * This method constructs the MasterReport using the TocManager. <BR>
	 * The TOC of the report must have been generated, i.e. the TocManager
	 * object embeeded into the RenderContext object have to not be null.
	 * @param reportTitle Title of the report
	 */
	void generate(Tarification tarification) {
		TocManager tm = m_context.getTocManager();
		LinkedList /*<ReportSection>*/ sections = tm.getSections();
		
		// construct the MasterReport
		m_result.setMasterTitle(tarification.getTitle());
		m_result.setSubTitle(tarification.getDescription());
		TarificationHeader th = tarification.getHeader();
		File thFile = th.myLoadingLocation();
		if (thFile != null) 
			m_result.setFileName(thFile.getName());
		else
			m_result.setFileName("");
		Date thDate = th.getPublishingDate();
		if (thDate != null) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
			m_result.setPublishDate(df.format(thDate));
		} else
			m_result.setPublishDate("");
		// Tarification Icon
   		BufferedImage bi = new BufferedImage(32,32,Image.SCALE_DEFAULT);
   		Graphics2D g2d = (Graphics2D) bi.getGraphics();
   		g2d.setColor(Color.WHITE);
   		g2d.fillRect(0,0,32,32);
   		g2d.drawImage(th.getIcon().getImage(),null,null);
   		m_result.setTarificationIcon(bi);
   		// To be continued
		for (int i=0; i<sections.size(); i++) {
			ReportSection rs = (ReportSection)sections.get(i);
			CompactNode node = rs.node;
			
			if (rs.isTariff) {
				// Tariff
				TarifReportConstructor reportConstructor = new TarifReportConstructor( m_context );
				Tarif t = node.getFirstTarif();
				assert t != null : 
					"A node refered as tariff should contain a tariff.";
				SubreportTreeItem subreport = reportConstructor.consumeTarif(t);
				addContent(subreport, t.getTitle(), t.getDescription()); 
			} else {
				// Title
				SectionNumber sn = rs.number;
				int level = sn.level();
				String name = sn.toString()+SEPARATORSTRING+rs.title;
				addTitle(name, level); 
			}
		}
	}
	
	/** Add a title item */
	private void addTitle(String name, int level) {
		assert level > 0 : "level cannot be equal to zero";
		m_currentLevel = level;
		if (level < REALTITLELIMIT) {
			m_row.titles[level-1] = name;
		} else {
			m_row.titles[REALTITLELIMIT-1] = produceIndent(level)+name;
			m_row.subreport = null;
			m_row.tariffTitle = "";
			m_row.tariffDescription = "";
			m_row.level = REALTITLELIMIT;
			m_result.addData(m_row);
		}
	}
	
	/** Add content item */
	private void addContent(SubreportTreeItem subreport, String title, String description) {
		if (m_currentLevel < REALTITLELIMIT) {
			// Flush title first
			m_row.level = m_currentLevel;
			m_row.subreport = null;
			m_row.tariffTitle = "";
			m_row.tariffDescription = "";
			m_result.addData(m_row);
		}
		m_row.subreport = subreport;
		m_row.tariffTitle = title;
		m_row.tariffDescription = description;
		m_result.addData(m_row);
	}
	
	/** produce indentation space for the given level */
	private String produceIndent(int level) {
		if (level > 3) {
			StringBuffer result = new StringBuffer();
			for (int i = 3; i<level; i++) {
				result.append("   ");
			}
			return result.toString();
			// NEVER REACHED
		}
		return "";
	}
	
	/**
	 * Retrieve the resulting report that has been built 
	 * up by one or more calls to <code>generate</code>. 
	 * @return Report that represents consumed data. 
	 */
	SubreportTreeItem getSubreport() {
		return m_result.getReport(); 
	}
	
	// Inner Classes ----------------------------------------
	
	/**
	 * Small class that holds a name and a title. 
	 * The subreport that should be show here is also 
	 * element of the structure. 
	 */
	class Title {
		/** Title of section */
		public String title; 
		/** Description of section */
		public String description; 
		
		/**
		 * Constructs a Title structure. 
		 * 
		 * @param title Title of section
		 * @param description Description of section
		 */
		public Title( String title, String description) {
			this.title = title; 
			this.description = description;
		}
	}
}

