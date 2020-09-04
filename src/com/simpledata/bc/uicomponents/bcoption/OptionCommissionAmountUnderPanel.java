/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 9 sept. 2004
 */
package com.simpledata.bc.uicomponents.bcoption;

import com.simpledata.bc.components.bcoption.OptionCommissionAmountUnder;


/**
 * This class provides the user interface for the OptionCommissionUnderAmount.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: OptionCommissionAmountUnderPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
public class OptionCommissionAmountUnderPanel 
	extends OptionLinkedToTarifsAbstract {
    
    public OptionCommissionAmountUnderPanel(EditStates editStateController, 
            OptionCommissionAmountUnder option) {
        super(editStateController, option);
    }
	
}
