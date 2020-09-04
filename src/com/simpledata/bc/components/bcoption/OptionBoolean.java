/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 mai 2004
 * $Id: OptionBoolean.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import com.simpledata.bc.datamodel.*;


/**
 * This class defines an option representing a simple choice
 * i.e. Is the person Swiss : true or false
 */
public class OptionBoolean extends BCOption {

    /** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Boolean";
	
	private boolean xState;
	
    /**
     * CONSTRUCTOR
     * @param ws parent WorSheet
     * @param title
     */
    public OptionBoolean(WorkSheet ws, String title) {
        super(ws, title);
        this.xState = true;
    }
    
    public boolean getState() {
        return this.xState;
    }
    
    public void setState(boolean b) {
        if (this.xState != b) {
            this.xState = b;
            fireDataChange();
        }
    }
    
    protected int getStatusPrivate() { return STATE_OK; }

    /// ---------- XML ------ ///
    
    /**
     * XML
     */
    public OptionBoolean() {}
    
    /**
     * XML
     */
    public boolean getXState() {
        return this.xState;
    }

    /**
     * XML 
     */
    public void setXState(boolean b) {
        this.xState = b;
    }
    
}

/*
 * $Log: OptionBoolean.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.3  2004/05/21 12:15:12  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/20 09:39:43  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/18 19:11:52  carlito
 * *** empty log message ***
 *
 */