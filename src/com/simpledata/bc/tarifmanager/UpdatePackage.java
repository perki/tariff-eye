/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: UpdatePackage.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.tarifmanager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An update package contains an array list of TarificationReferences<BR>
 * An a runFirst element to patch the software
 */
public class UpdatePackage implements Serializable {
    private ArrayList/*<TarificationReferences>*/ refs;
    private RunFirst runFirst;
    
    public UpdatePackage(ArrayList/*<TarificationReferences>*/ refs,RunFirst rf)
    {
        this.refs = refs;
        this.runFirst = rf;
    }
    
    /** return the tarification references **/
    public ArrayList/*<TarificationReferences>*/ refs() {
        return refs;
    }
    
    /** return the runFirst object **/
    public RunFirst runFirst() {
        return runFirst;
    }

    /*----------------- XML ---------------------*/
    
    /** XML **/
    public UpdatePackage() {
    }
    /** XML **/
    public ArrayList getXRefs() {
        return refs;
    }
    /** XML **/
    public void setXRefs(ArrayList refs) {
        this.refs = refs;
    }
    /** XML **/
    public RunFirst getXRunFirst() {
        return runFirst;
    }
    /** XML **/
    public void setXRunFirst(RunFirst runFirst) {
        this.runFirst = runFirst;
    }
}
