/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: OptionFuture.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.components.bcoption;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.Copiable;
import com.simpledata.bc.datamodel.WorkSheet;

/**
 * @author SD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OptionFuture extends BCOption 
	implements Copiable.TransferableOption  {
    
    private static final Logger m_log = 
        	Logger.getLogger( OptionFuture.class );
	
	
	/** Unique Key to identify this Option Type **/
	public final static String OPTION_TITLE= "Future";
    
	/** true, if this future is on opening, false if on closing **/
	private boolean xonOpening;
    
	/** number of contracts **/
	private int xnumberOfContracts;
	
	/**
	 */
	public OptionFuture(WorkSheet workSheet, String title) {
		super(workSheet, title);
	}

	//-------- access to values ------------//
	
	/** get the number of contracts **/
	public int numberOfContracts() { 
	    return (xnumberOfContracts < 1) ? 1 : xnumberOfContracts;
	}
	
	
	/** set the number of contracts **/
	public void setNumberOfContracts(int n) {
	    if (n < 1 || n == xnumberOfContracts) return;
	    xnumberOfContracts = n;
	    fireDataChange();
	}
	
	/** return true if is on openening,false if on closing **/
	public boolean onOpening() { return xonOpening ; }
	
	/** set the value of onOpening **/
	public void setOnOpening(boolean b) {
	    if (b == xonOpening) return;
	    xonOpening = b;
	    fireDataChange();
	}
	
	
	
    /**
     * @see BCOption#getStatusPrivate()
     */
    protected int getStatusPrivate() { return STATE_OK; }
    
    //	---------- IMPLEMENTS Copiable.TransferableOption --------------//

    /**
     *  <B>IMPLEMENTS Copiable.TransferableOption</B><BR>
     * @see TransferableOption#canCopyValuesInto(java.lang.Class)
     */
    public boolean canCopyValuesInto(Class destination) {
        return (destination.equals(getClass()));
    }

    /**
     * @see TransferableOption#copyValuesInto(BCOption)
     */
    public void copyValuesInto(BCOption destination) {
        if (! canCopyValuesInto(destination.getClass())) {
			m_log.error( "cannot copy value of ["+destination.getClass()+
					"] into a ["+getClass()+"]" );
		}
        OptionFuture of = (OptionFuture) destination;
        of.setTitle(new String(getTitle()));
        of.setNumberOfContracts(numberOfContracts());
        of.setOnOpening(onOpening());
    }
    
    //--------- XML ------------//
    /** XML **/
	public OptionFuture() {}
	

	/** XML **/
    public int getXnumberOfContracts() {
        return xnumberOfContracts;
    }
    /** XML **/
    public void setXnumberOfContracts(int xnumberOfContracts) {
        this.xnumberOfContracts = xnumberOfContracts;
    }
    /** XML **/
    public boolean getXonOpening() {
        return xonOpening;
    }
    /** XML **/
    public void setXonOpening(boolean xonOpening) {
        this.xonOpening = xonOpening;
    }
}
