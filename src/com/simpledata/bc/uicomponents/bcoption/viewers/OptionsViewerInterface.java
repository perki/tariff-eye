/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 2 ao�t 2004
 * $Id: OptionsViewerInterface.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcoption.viewers;

import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.WorkSheet;

/**
 * Interface used to standardize OptionsViewer behaviour
 */
public interface OptionsViewerInterface {
    
	/**
	 * the workSheet which this option viewer refers to
	 */
    public WorkSheet getWorkSheet();
    
    /**
     * Tell the option viewer to create a new option of class c
     * @param c
     */
    public void createOption(Class c);
    
    /**
     * Tell the option viewer add an existing option
     * @param otpion
     */
    public void addRemoteOption(BCOption otpion);
    
}


/*
 * $Log: OptionsViewerInterface.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.1  2004/08/05 00:23:44  carlito
 * DispatcherCase bugs corrected and aspect improved
 *
 */