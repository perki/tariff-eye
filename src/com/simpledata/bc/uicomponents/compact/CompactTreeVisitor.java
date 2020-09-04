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
 * @version $Id: CompactTreeVisitor.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
 
/// User interface components. 
package com.simpledata.bc.uicomponents.compact; 


/**
 * Compact tree visitor interface. This is the interface 
 * that all classes that want to visit the CompactTree 
 * must implement. 
 * 
 */
public interface CompactTreeVisitor {
	
	public void caseCompactBCNodeSingle( CompactBCNodeSingle node ); 
	public void caseCompactRoot( CompactRoot node ); 
	public void caseCompactShadowNode( CompactShadowNode node ); 
	public void caseCompactTarifNode( CompactTarifNode node ); 
	public void caseCompactTarifLinkNode( CompactTarifLinkNode node ); 
	public void caseCompactWorkSheetNode( CompactWorkSheetNode node );
	public void caseCompactBCGroupNode(CompactBCGroupNode node); 
	
}