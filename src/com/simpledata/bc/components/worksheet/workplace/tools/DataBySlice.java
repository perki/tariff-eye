/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: DataBySlice.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.components.worksheet.workplace.tools;

import com.simpledata.bc.tools.OrderedMapOfDoubles;

/**
 * Interface for all data that represent a slice context
 */
public interface DataBySlice {
    /** create a Line at this key position **/
    public void createLineAt(double key);
    
    /** get OrderedMapOfDoubles **/
    public OrderedMapOfDoubles getOmod();
}
