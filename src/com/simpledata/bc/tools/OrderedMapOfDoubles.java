/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/** 
 * $Id: OrderedMapOfDoubles.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.tools;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;

import com.simpledata.bc.datamodel.Copiable; 

/**
 * A map that contains objects indexed by doubles
 * The map is order depending on the value of those doubles<BR>
 * note : accepts only values > 0 (not even 0) as keys
 */
public class OrderedMapOfDoubles implements Serializable, Copiable {
	ArrayList xContents ;
	
	/**
	 * 
	 * @param zeroValue the default value for 0
	 */
	public OrderedMapOfDoubles (Copiable zeroValue) {
		getContents().add(new OrderedMapOfDoublesObject(this,0d,zeroValue));
	}
	
	public int size() {
		return getContents().size();
	}
	
	private ArrayList getContents() {
		if (xContents == null) xContents = new ArrayList();
		return xContents;
	}
	
	/**
	 * Add a value for this key, If key exists overwrite existsing value
	 */
	public void put(double key, Copiable value) {
		assert key > 0; 
		
		//	known key
		OrderedMapOfDoublesObject o0 = getOMODObjectAt(key); 
		if (o0 != null) {
			o0.xValue = value;
			return;
		}
		
		
		// unkown key get insert position
		int insertAt = 0;
		double keyTemp = 0;
		for (int i = 0; i < getContents().size() ; i++) {
			keyTemp = (getOMODObjectAtIndex(i)).xKey;
			if (key > keyTemp) {
				insertAt = i+1;
			}
		}
		getContents().add(insertAt,new OrderedMapOfDoublesObject(this,key,value));
	}
	
	/**
	 * Remove the pair at this index
	 */
	public void removeAtIndex(int index) {
		assert index > 0; 
		getContents().remove(index);
	}
	
	/**
	 * Remove this key
	 */
	public void remove(double key) {
		assert key > 0; 
		
		double keyTemp = 0;
		for (int i = 0; i < getContents().size() ; i++) {
			keyTemp = (getOMODObjectAtIndex(i)).xKey;
			if (key == keyTemp) {
				removeAtIndex(i);
				break;
			}
		}
	}
	
	/**
	 * get the maximum key
	 */
	public double getMaxKey() {
		if (getContents().size() <= 0) return 0;
		OrderedMapOfDoublesObject o = getOMODObjectAtIndex(getContents().size() - 1 );
		return o.xKey;
	}
	
	/**
	 * get the object where key == object.key
	 */
	private OrderedMapOfDoublesObject getOMODObjectAt(double key) {
		for (int i = 0; i < getContents().size() ; i++) {
			OrderedMapOfDoublesObject o = getOMODObjectAtIndex(i);
			if (key == o.xKey) return o;
		}
		return null;
	}
	/**
	 * get the object where key == object.key
	 */
	public Object getObjectAt(double key) {
		return getOMODObjectAt(key).xValue;
	}

	/**
	 * get the last object where key >= object.key
	 */
	private OrderedMapOfDoublesObject getOMODObjectLastLower(double key) {
		for (int i = getContents().size(); i > 0  ; i--) {
			OrderedMapOfDoublesObject o = getOMODObjectAtIndex(i-1);
			if (key >= o.xKey) {
				return o;
			}
		}
		return null;
	}
	
	/**
	 * get the last object where key >= object.key
	 */
	public Object getValueLastLower(double key) {
		OrderedMapOfDoublesObject o = getOMODObjectLastLower(key);
		if (o == null) return null;
		return o.xValue;
	}

	/**
	 * get the first object where key <= object.key
	 */
	private OrderedMapOfDoublesObject getOMODObjectFirstGreater(double key) {
		for (int i = 0; i < getContents().size() ; i++) {
			OrderedMapOfDoublesObject o = getOMODObjectAtIndex(i);
			if (key <= o.xKey) {
				return o;
			}
		}
		return null;
	}
	
	/**
	 * get the first object where key <= object.key
	 */
	public Object getValueFirstGreater(double key) {
		OrderedMapOfDoublesObject o = getOMODObjectFirstGreater(key);
		if (o == null) return null;
		return o.xValue;
	}
	
	/**
	 * get the value for this double.
	 * return null if no value found;
	 */
	public Object get(double key) {
		for (int i = 0; i < getContents().size() ; i++) {
			OrderedMapOfDoublesObject o = getOMODObjectAtIndex(i);
			if (o.xKey == key) return o.xValue;
			if (o.xKey > key) return null;
		}
		return null;
	}
	
	/**
	 * get the Objects in a list
	 */
	public Object[] getValues() {
		Object[] res = new Object[getContents().size()];
		for (int i = 0; i < getContents().size(); i++) {
			res[i] = (getOMODObjectAtIndex(i)).xValue;
		}
		return res;
	}
	
	/**
	 * get the Key at the specified index
	 */
	public double getKeyAtIndex(int index) {
		return (getOMODObjectAtIndex(index)).xKey;
	}
	
	/**
	 * get the Value at the specified index
	 */
	public Object getValueAtIndex(int index) {
		return (getOMODObjectAtIndex(index)).xValue;
	}
	
	/**
	 * get the Value at the specified index
	 */
	public OrderedMapOfDoublesObject getOMODObjectAtIndex(int index) {
		return (OrderedMapOfDoublesObject) getContents().get(index);
	}
	
	
	/**
	 * get the Keys in a list
	 */
	public double[] getKeys() {
		double[] res = new double[getContents().size()];
		for (int i = 0; i < getContents().size(); i++) {
			res[i] = (getOMODObjectAtIndex(i)).xKey;
		}
		return res;
	}
	
	/**
	 * get the index of this Object
	 */
	public int getOMODOIndex(OrderedMapOfDoublesObject omodo) {
		return getContents().indexOf(omodo);
	}
	
	/**
	 * toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
			sb.append("Order Map Of Double \n");
			for (int i = 0; i < getContents().size(); i++) {
				OrderedMapOfDoublesObject o = getOMODObjectAtIndex(i);
				sb.append(o.xKey+":\t").append(o.xValue).append("\n");
			}
		return sb.toString();
	}
	
	/** copy (like clone) **/
	public Copiable copy() {
		OrderedMapOfDoubles copy = new OrderedMapOfDoubles();
		ArrayList contentsCopy = new ArrayList();
		Iterator e = getContents().iterator();
		while (e.hasNext()) {
			contentsCopy.add(((OrderedMapOfDoublesObject) e.next()).copy(copy));
		}
		copy.setXContents(contentsCopy);
		return copy;
	}
	
	
	//----------------------- XML ------------------//
	/** XML **/
	public OrderedMapOfDoubles() {}
	
	/**
	 * XML
	 */
	public ArrayList getXContents() {
		return xContents;
	}

	/**
	 * XML
	 */
	public void setXContents(ArrayList vector) {
		xContents= vector;
	}

}

/**
 * $Log: OrderedMapOfDoubles.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.18  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.17  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.16  2004/05/11 15:53:00  perki
 * more calculus
 *
 * Revision 1.15  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.14  2004/03/04 16:38:05  perki
 * copy goes to hollywood
 *
 * Revision 1.13  2004/03/04 14:32:07  perki
 * copy goes to hollywood
 *
 * Revision 1.12  2004/03/04 11:12:23  perki
 * copiable
 *
 * Revision 1.11  2004/03/03 20:36:48  perki
 * bonne nuit les petits
 *
 * Revision 1.10  2004/03/03 18:19:22  perki
 * ziuiasdhgasjk
 *
 * Revision 1.9  2004/02/22 10:43:57  perki
 * File loading and saving
 *
 * Revision 1.8  2004/02/19 15:40:25  perki
 * Tango Bravo
 *
 * Revision 1.7  2004/02/19 13:16:07  perki
 * Tango Bravo
 *
 * Revision 1.6  2004/02/19 11:23:21  perki
 * Zoulou
 *
 * Revision 1.5  2004/02/18 16:59:29  perki
 * turlututu
 *
 * Revision 1.4  2004/02/06 07:44:55  perki
 * lot of cleaning in UIs
 *
 * Revision 1.3  2004/02/05 18:35:59  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/02/05 15:11:39  perki
 * Zigouuuuuuuuuuuuuu
 *
 * Revision 1.1  2004/02/05 11:07:28  perki
 * Transactions are welcome aboard
 *
 */