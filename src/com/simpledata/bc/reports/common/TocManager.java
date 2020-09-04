/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 4 oct. 2004
 */
package com.simpledata.bc.reports.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.tarif.TarifLocalization;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.uicomponents.compact.CompactExplorer;
import com.simpledata.bc.uicomponents.compact.CompactNode;
import com.simpledata.bc.uicomponents.compact.CompactTarifLinkNode;
import com.simpledata.bc.uicomponents.compact.CompactTarifNode;

/**
 * This class is a utility class. It contains the structure the reports will
 * have. It also can retrieves links.<BR>
 * The structure is composed by a sequence of report item. I.e a title or
 * some content.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: TocManager.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
public class TocManager {
	/** Nice logger */
	private final static Logger m_log = Logger.getLogger(TocManager.class);
	
	/** Links */
	private final HashMap /*<CompactNode -> SectionNumber>*/ m_references;
	/** Sections */
	private final LinkedList /*<ReportSection>*/ m_sections;	
	/** Current new section number */
	private SectionNumber m_currentSectionNumber;
	
	/**
	 * Construct a new TocManager for the given CompactTreeExplorer. It yields
	 * to the immediate generation of the toc.
	 * @param exp The CompactExplorer to represent.
	 */
	public TocManager(CompactExplorer exp) {
		// members initialization
		m_references = new HashMap();
		m_sections = new LinkedList();
		m_currentSectionNumber = SectionNumber.beggining();
		
		// immediate computation
		generateToc(exp.getTreeRoot());
	}
	
	// #### METHODS - toc generation algorithm ################################
	
	/**
	 * Generates sections and subsections for the given Vector of nodes. See
	 * method comments for further infos about the policy.
	 * @param sections a vector of node.
	 */
	private void processSections(Vector sections) {
		Iterator it = sections.iterator();
		while (it.hasNext()) {
			CompactNode node = (CompactNode) it.next();
			Tarif tarif = node.getFirstTarif();
			
			if (! (node instanceof CompactTarifLinkNode)) { // do not report a link
				if (tarif == null || (tarif instanceof TarifLocalization &&
									  ! (node instanceof CompactTarifNode))) {
					// we deal with a node with childrens, so we report a title
					addTitle(node);
					// process node children
					m_currentSectionNumber = m_currentSectionNumber.enterSubsection();
					processSections(node.getChildren());
					m_currentSectionNumber = m_currentSectionNumber.exitSubsection();
					m_currentSectionNumber = m_currentSectionNumber.nextSection();
				} else {
					if (! (tarif instanceof TarifLocalization))
						// we have a node with an interessant tarif	
						if (node instanceof CompactTarifNode) { 
							// this node contains only the tariff
							addContent(node);
						} else {
							// this node contains the tariff and the title
							addTitle(node);
							addContent(node);
							m_currentSectionNumber = m_currentSectionNumber.nextSection();
						}
				}
			}
		}
	}
	
	/**
	 * Append section content to the report structure. And refers link
	 * @param node The corresponding node.
	 */
	private void addContent(CompactNode node) {
		// add a link
		if (! m_sections.isEmpty()) {
			ReportSection previous = (ReportSection) m_sections.getLast();
			SectionNumber link = previous.number;
			if (link != null) {
				Tarif t = node.getFirstTarif();
				assert t != null :
					"A node refered as containing tariff must contains a tariff.";
				m_references.put (t.getNID(), link);
			}
		}
		// add the section content
		ReportSection content = new ReportSection(node);
		content.isTariff = true;
		m_sections.addLast(content);
	}
	
	/**
	 * Append a title to the report structure. And refers link
	 * @param node The correspoding node.
	 */
	private void addTitle(CompactNode node) {
		ReportSection title = new ReportSection(node);
		title.title = node.toString();
		title.number = m_currentSectionNumber;
		m_sections.addLast(title);
		m_references.put(node, m_currentSectionNumber);
	}
	
	/**
	 * Main method. 
	 * @param root Root node of the CompactExplorer
	 */
	private void generateToc(CompactNode root) {
		Vector sections = root.getChildren();
		m_log.debug("Processing Toc generation...");
		processSections(sections);
		m_log.debug("Done. Results:");
		for (int i = 0; i<m_sections.size(); i++)
			m_log.debug(m_sections.get(i).toString());		
	}
	
	// #### METHODS - provided by TocManager ##################################
	
	/**
	 * Retrieve the section number of a given node.
	 * @param node node to retrive
	 * @return SectionNumber object. null iff the node doesn't belong to
	 * the report structure.
	 */
	public SectionNumber sectionNumber(CompactNode node) {
		return (SectionNumber)m_references.get(node); 
	}
	
	/**
	 * Retrieve the section number of a given tariff.
	 * @param t tarif to retrive
	 * @return SectionNumber object. null iff the tariff doesn't belong to
	 * the report structure.
	 */
	public SectionNumber sectionNumber(Tarif t) {
		return (SectionNumber)m_references.get(t.getNID()); 
	}
	
	/**
	 * @return The whole structure of the report. Using a LinkedList of 
	 * ReportSection objects. 
	 */
	public LinkedList /*<ReportSection>*/ getSections() {
		return m_sections;
	}
}

