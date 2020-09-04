/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: RateBySliceValue.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
 
/**
 * Tools for the Workplace classes. 
 */
package com.simpledata.bc.components.worksheet.workplace.tools;

import java.io.Serializable;

import com.simpledata.bc.datamodel.Copiable;
import com.simpledata.bc.datamodel.money.BCNumber;

/**
 * Contains rate or fixed values that are stored in a RateBySlice
 * container. 
 */
/**
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: RateBySliceValue.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
/**
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: RateBySliceValue.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class RateBySliceValue implements Serializable,Copiable {
	public Double xRate;
	public BCNumber xFixedMin;

	/** Empty (all herited)**/
	public RateBySliceValue() {}
		
	/** construct a rate container **/
	public RateBySliceValue(Double rate) {
		this.xRate= rate;
	}

	public boolean isRate() {
		return (xRate != null);
	}
	
	public boolean isFixed() {
		return (xFixedMin != null);
	}
	
	
	/**
	 * @see com.simpledata.bc.datamodel.Copiable#copy()
	 */
	public Copiable copy() {
		RateBySliceValue copy = new RateBySliceValue();
		if (xFixedMin != null)
			copy.xFixedMin = (BCNumber) xFixedMin.copy();
		if (xRate != null)
			copy.xRate = new Double(xRate.doubleValue());
		return copy;
	}
	
	public String toString() {
		return "RBSV("+xRate+","+xFixedMin+")";
	}

	/**
	 * Return the applicated rate for this slice. In range
	 * of 0 to 1.
	 * @return Rate using a double precision float.
	 */
	public double getRate() {
		return xRate.doubleValue()/100.0;
	}
	
	/**
	 * Return the minimal applicable fee for this slice
	 * @return minimal fee, using a double precision float.
	 */
	public double getFixedMin() {
		return xFixedMin.get(); 
	}
	//---------------- XML ------------------//
	
	
	/**XML*/
	public BCNumber getXFixedMin() {
		return xFixedMin;
	}

	/**XML*/
	public Double getXRate() {
		return xRate;
	}

	/**XML*/
	public void setXFixedMin(BCNumber number) {
		xFixedMin= number;
	}
	
	/**XML*/
	public void setXRate(Double double1) {
		xRate= double1;
	}

}

/**
 *  $Log: RateBySliceValue.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:40  perki
 *  First commit on sourceforge
 *
 *  Revision 1.10  2004/08/26 15:51:33  jvaucher
 *  - Partially implemented assets details in the fee report. Still a fragile version.
 *  - Also minor changes in the RateBySliceValue class. Added non-xml accessors.
 *
 *  Revision 1.9  2004/07/26 20:36:09  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: RateBySliceValue.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.8  2004/05/22 08:39:35  perki
 *  Lot of cleaning
 *
 *  Revision 1.7  2004/03/16 16:30:11  perki
 *  *** empty log message ***
 *
 *  Revision 1.6  2004/03/16 14:09:31  perki
 *  Big Numbers are welcome aboard
 *
 *  Revision 1.5  2004/03/04 16:38:05  perki
 *  copy goes to hollywood
 *
 *  Revision 1.4  2004/03/04 14:32:07  perki
 *  copy goes to hollywood
 *
 *  Revision 1.3  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.2  2004/03/03 20:36:48  perki
 *  bonne nuit les petits
 *
 *  Revision 1.1  2004/03/03 18:19:50  perki
 *  ziuiasdhgasjk
 *
 */