/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SimpleCompactTreeVisitor.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.uicomponents.compact;

/**
 * A Visitor that overides all visit method to one single
 */
public abstract class SimpleCompactTreeVisitor implements CompactTreeVisitor {
    
    public abstract void visitCompactNode(CompactNode cn);
    
    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactBCNodeSingle(com.simpledata.bc.uicomponents.compact.CompactBCNodeSingle)
     */
    public final void caseCompactBCNodeSingle(CompactBCNodeSingle node) {
       visitCompactNode(node);
        
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactRoot(com.simpledata.bc.uicomponents.compact.CompactRoot)
     */
    public final void caseCompactRoot(CompactRoot node) {
       visitCompactNode(node);
        
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactShadowNode(com.simpledata.bc.uicomponents.compact.CompactShadowNode)
     */
    public final void caseCompactShadowNode(CompactShadowNode node) {
       visitCompactNode(node);
        
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactTarifNode(com.simpledata.bc.uicomponents.compact.CompactTarifNode)
     */
    public final void caseCompactTarifNode(CompactTarifNode node) {
       visitCompactNode(node);
        
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactTarifLinkNode(com.simpledata.bc.uicomponents.compact.CompactTarifLinkNode)
     */
    public final void caseCompactTarifLinkNode(CompactTarifLinkNode node) {
       visitCompactNode(node);
        
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactWorkSheetNode(com.simpledata.bc.uicomponents.compact.CompactWorkSheetNode)
     */
    public final void caseCompactWorkSheetNode(CompactWorkSheetNode node) {
       visitCompactNode(node);
        
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.compact.CompactTreeVisitor#caseCompactBCGroupNode(com.simpledata.bc.uicomponents.compact.CompactBCGroupNode)
     */
    public final void caseCompactBCGroupNode(CompactBCGroupNode node) {
       visitCompactNode(node);
        
    }

}
