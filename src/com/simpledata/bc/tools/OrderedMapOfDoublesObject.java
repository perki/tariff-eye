/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OrderedMapOfDoublesObject.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.tools;

import java.io.Serializable;

import com.simpledata.bc.datamodel.Copiable;

/**
 * Container for data used by OrderedMapOfDoubles
 * @see OrderedMapOfDoubles
 */
public class OrderedMapOfDoublesObject implements Serializable {
	public double xKey;
	public Copiable xValue;
	public OrderedMapOfDoubles xOmod;
	
	public OrderedMapOfDoublesObject(
		OrderedMapOfDoubles omod,double key, Copiable value) {
		this.xKey = key;
		this.xValue = value;
		this.xOmod = omod;	
	}

	
	//-------------- getter an setters ---------------//
	
	/** 
	 * return previous Object in the Ordered Map Of double 
	 * @return null if first one
	 * **/
	public OrderedMapOfDoublesObject getPrevious() {
		int i = xOmod.getOMODOIndex(this);
		if (i < 1) return null;
		return xOmod.getOMODObjectAtIndex(i-1);
	}
	
	/** 
	 * return previous Object in the Ordered Map Of double 
	 * @return null if first one
	 * **/
	public OrderedMapOfDoublesObject getNext() {
		int i = xOmod.getOMODOIndex(this);
		if (i >= (xOmod.size() - 1)) return null;
		return xOmod.getOMODObjectAtIndex(i+1);
	}
	
	public double getKey() {
		return xKey;
	}


	public Object getValue() {
		return xValue;
	}


	public void setMyKey(double d) {
		assert d > 0; 

		if (d != xKey) { // reorder the list
			if (xOmod != null) {
				xOmod.remove(xKey);
				xOmod.remove(d);
				xKey = d;
				xOmod.put(xKey,xValue);
			}
		}
	}


	public void setValue(Copiable object) {
		xValue = object;
	}
	
	/** copy (like clone) **/
	public OrderedMapOfDoublesObject copy(OrderedMapOfDoubles copyOwner) {
		OrderedMapOfDoublesObject copy = new OrderedMapOfDoublesObject();
		copy.setKey(xKey);
		copy.setValue(xValue.copy());
		copy.setXOmod(copyOwner);
		return copy;
	}

	public String toString() {
		return "OMDO key:"+xKey+" value:"+xValue+" OMOD:"+xOmod;
	}

	//----------------- XML ------------------//
	/** XML CONSTRUCTOR DO NOT USE **/
	public OrderedMapOfDoublesObject() {}

	
	/**
	 * XML
	 */
	public OrderedMapOfDoubles getXOmod() {
		return xOmod;
	}

	/**
	 * XML
	 */
	public void setXOmod(OrderedMapOfDoubles doubles) {
		xOmod= doubles;
	}

	/** XML */
	public void setKey(double d) {
		xKey= d;
	}

	/** XML */
	public double getXKey() {
		return xKey;
	}

	/** XML */
	public Copiable getXValue() {
		return xValue;
	}

	/** XML */
	public void setXKey(double d) {
		xKey= d;
	}

	/** XML */
	public void setXValue(Copiable copiable) {
		xValue= copiable;
	}

}


/**
 *  $Log: OrderedMapOfDoublesObject.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.11  2004/09/03 13:25:34  kaspar
 *  ! Log.out -> log4j part four
 *
 *  Revision 1.10  2004/05/13 14:09:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.9  2004/03/04 16:38:05  perki
 *  copy goes to hollywood
 *
 *  Revision 1.8  2004/03/04 14:32:07  perki
 *  copy goes to hollywood
 *
 *  Revision 1.7  2004/03/04 11:12:23  perki
 *  copiable
 *
 *  Revision 1.6  2004/03/03 20:36:48  perki
 *  bonne nuit les petits
 *
 *  Revision 1.5  2004/03/03 18:19:22  perki
 *  ziuiasdhgasjk
 *
 *  Revision 1.4  2004/02/22 10:43:57  perki
 *  File loading and saving
 *
 *  Revision 1.3  2004/02/19 15:40:25  perki
 *  Tango Bravo
 *
 *  Revision 1.2  2004/02/19 13:16:07  perki
 *  Tango Bravo
 *
 *  Revision 1.1  2004/02/19 11:23:21  perki
 *  Zoulou
 *
 */