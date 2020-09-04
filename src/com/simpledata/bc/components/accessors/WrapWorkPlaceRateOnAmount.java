/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 30 ao�t 2004
 */
package com.simpledata.bc.components.accessors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.simpledata.bc.components.bcoption.OptionMoneyAmountUnder;
import com.simpledata.bc.components.bcoption.OptionPerBaseTen;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateOnAmount;

/**
 * This class implements a wrapper to provide easy-to-use accessors
 * for the WorkPlaceRateOnAmount workplace.
 *  
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: WrapWorkPlaceRateOnAmount.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
public class WrapWorkPlaceRateOnAmount {
    // FIELDS
    private WorkPlaceRateOnAmount m_workplace;
    
    // CONSTRUCTOR   
    /**
     * Construct a new wrapper for the given WorkPlaceRateOnAmount
     * @param wp the workplace.
     */
    public WrapWorkPlaceRateOnAmount(WorkPlaceRateOnAmount wp) {
        m_workplace = wp;
    }
    
    // METHODS 
    /**
     * This method looks up for a OptionPerBaseTen and return the value
     * it contains, using a double precision float in range from 0 to 1.<BR>
     * If the workplace doesn't contain any OptionPerBaseTen the result is 0.
     * @return Applied percent in the workplace. In range from 0 to 1.
     */
    public double getRate() {
        double result = 0.0;
        ArrayList options = m_workplace.getOptions(OptionPerBaseTen.class);
        if (! options.isEmpty()) {
            OptionPerBaseTen pbt = (OptionPerBaseTen) options.get(0);
            result = pbt.getDoubleValue();
        }
        return result;
    }
    
    
    /**
     * This methods return a list of TarifAssets linked to the workplace.
     * @return TarifAssets linked to the workplace.
     */
    public List /*TarifAssets*/ getLinkedTarifs() {
        LinkedList result = new LinkedList();
        ArrayList options = m_workplace.getOptions(OptionMoneyAmountUnder.class);
        for (int i=0; i<options.size(); i++) {
            OptionMoneyAmountUnder omau = (OptionMoneyAmountUnder)options.get(i);
            ArrayList /*TarifAssets*/ tarifs = omau.getLinkedTarifs();
            result.addAll(tarifs);
        }
        return result;
    }
}


/** $Log: WrapWorkPlaceRateOnAmount.java,v $
/** Revision 1.2  2007/04/02 17:04:28  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:42  perki
/** First commit on sourceforge
/**
/** Revision 1.1  2004/08/31 15:36:16  jvaucher
/** - Fee Detailled Report continued
/** - Added accessors
/** - Automatic reports template recompilation
/**
 */