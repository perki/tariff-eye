/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: DoubleSideMap.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/

package com.simpledata.bc.tools;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
* This is a two column table<BR>
* contains pair of objects. A pair will appear only once.<BR>
* Use it to represent Networks or Trees<BR>
* Note: this object is NOT synchronized!<BR>
* 
* <BR>
* TODO Makes it shrink when many removes occures, for now this map can only grow
* TODO Optimise this with real Hash mapping
*/
public class DoubleSideMap implements java.io.Serializable {
	/** Logger */
	private static final Logger m_log = Logger.getLogger(DoubleSideMap.class);
	
	
	/** when resizing up .. the amount of free space to keep **/
	private static final int BUFFER_SIZE = 10;

	
	/** data container **/
	private Object[][] data;
	
	/**
	* Constructor
	* @param dummy whatever you want, this is just to fool the XMLencoder
	*/
	public DoubleSideMap(boolean dummy) {
		data = new Object[BUFFER_SIZE][2];
	}

//	
//	/**
//	 * return true if this object exists on the left column
//	 */
//	public boolean hasLeftObject(Object o) {
//		return hasObject(o,0);
//	}
//	
//	/**
//	 * return true if this object exists on the left column
//	 */
//	public boolean hasRightObject(Object o) {
//		return hasObject(o,1);
//	}
//	
//	/** helper for hasXXXObject **/
//	private boolean hasObject(Object o,int side) {
//	    for (int i = 0; i < data.length; i++)
//	        if (data[i][side] == o) return true;
//		return false;
//	}
	
	/**
	 * Get if a pair exists
	 * @return true if a pair of object exists
	 */
	public boolean pairExists(Object leftO, Object rightO) {
	    if (needConvertion) convert();// TODO Remove when conversion done
		return (pairIndex(leftO, rightO) >= 0);
	}

	/**
	 * Get if a pair index
	 * @return thr position of this par -1 if does not exists
	 */
	private int pairIndex(Object leftO, Object rightO) {
	    for (int i = 0; i < data.length; i++) 
	        if (data[i][0] == leftO && data[i][1] == rightO)
	            return i;
	    
		return -1;
	}

	
	/**
	* add this pair to the table. If this pair exists then 
	* it will not be added
	*/
	public synchronized void put(Object leftO, Object rightO) {
	    if (needConvertion) convert();// TODO Remove when conversion done
		assert leftO != null && rightO != null : 
		    	"Avoid that one of the par is null";
		
		if (pairExists(leftO, rightO)) return;
		
		// get first free slot
		int position = pairIndex(null,null);
		
		if (position < 0) { // need more space
		    m_log.debug("Resizing UP");
		    Object[][] newData = new Object[data.length+BUFFER_SIZE][2];
		    for (int i = 0; i < data.length; i++) newData[i] = data[i];
		    data = newData;
		    position = pairIndex(null,null);
		}
		
		data[position][0] = leftO;
		data[position][1] = rightO;
	}

	/**
	* remove this specific pair of object
	* @param leftO if null then any pair with rightO in the right column will be removed.
	* @param rightO if null then any pair with rightO in the right column will be removed.
	*/
	public synchronized void remove(Object leftO, Object rightO) {
	    if (needConvertion) convert();// TODO Remove when conversion done
	    if (leftO == null && rightO == null) return;
	    
	    
		if (leftO != null && rightO != null) {
			int pos= pairIndex(leftO, rightO);
			data[pos][0] = null; data[pos][1] = null;
			return;
		} 
		
		int side = (leftO == null) ? 1 : 0;
		Object o = (leftO == null) ? rightO : leftO;
		for (int i = 0; i < data.length ; i++)
		    if (data[i][side] == o) {
		        data[i][0] = null; data[i][1] = null;
		    }
	}

	/**
	* get all the objects pairing with leftO where leftO is in the left column
	*/
	public ArrayList getRightOf(Object leftO) {
		return  getDataPairing(leftO,0);
	}
	
	/**
	* get all the objects pairing with rightO where rightO is in the right column
	*/
	public ArrayList getLeftOf(Object rightO) {
	    return  getDataPairing(rightO,1);
	}
	
	private ArrayList getDataPairing(Object o, int side) {
	    if (needConvertion) convert();// TODO Remove when conversion done
	    int oside = (side == 0) ? 1 : 0;
	    ArrayList res= new ArrayList();
		for (int i= 0; i < data.length; i++) {
			if (data[i][side] == o)
				res.add(data[i][oside]);
		}
		return res;
	}

	

	/**
	* get all objects that appear on the leftColoumns
	*/
	public ArrayList getLeftObjects() {
		return _getSideObjects(0);
	}

	/**
	* get all objects that appear on the right Column
	*/
	public ArrayList getRightObjects() {
		return _getSideObjects(1);
	}

	private ArrayList _getSideObjects(int side) {
	    if (needConvertion) convert();// TODO Remove when conversion done
		ArrayList v= new ArrayList();
		for (int i= 0; i < data.length; i++) {
			if (data[i][side] != null && !v.contains(data[i][side]))
				v.add(data[i][side]);
		}
		return v;
	}

	/**
	* String representation of this DoubleSideMap
	*/
	public String toString() {
	    if (needConvertion) convert();// TODO Remove when conversion done
		StringBuffer sb= new StringBuffer();
		sb.append("Double sided Map\n");
		for (int i= 0; i < data.length; i++) {
			sb.append(i + ":").append(data[i][0]+"");
			sb.append("\t - ").append(data[i][1]+"");
			sb.append("\n");
		}
		return sb.toString();
	}

	// ------------------------ XML -------------------//
	
	/** XML */
	public DoubleSideMap() { }
	
	/** XML **/
	public Object[][] getXData() {
	    return data;
	}
	
	/** XML **/
	public void setXData(Object[][] d) {
	    data = d;
	}
	
	// ------------------- old XML ------------------------ //
	
	/** will be set to true if data need to be converted **/
	private boolean needConvertion = false;
	
	/** convert from version with ArrayList to OBject[][2] **/
	private void convert() {
	    if (! needConvertion) return;
	    m_log.warn("Convertion array map");
	    assert tempL != null && tempR != null;
	    assert tempL.size() == tempR.size();
	    data = new Object[BUFFER_SIZE][2];
	    needConvertion = false;
	    
	    Iterator iL = tempL.iterator();
	    Iterator iR = tempR.iterator();
	    for (int i = 0; iL.hasNext() && iR.hasNext(); i++) {
	        put(iL.next(),iR.next());
	    }
	    tempL = null;
	    tempR = null;
	    
	    m_log.warn("Convertion done map");
	    System.out.println(""+this);
	}
	
//	/**
//	 *  OLD XML HERE FOR FILE CONVERSION ONLY
//	 */
//	public void setXLeft(ArrayList vector) {
//	    needConvertion = true;
//	    m_log.warn("setXLeft");
//		//xLeft= vector;
//	}
//
//	/**
//	 * OLD XML HERE FOR FILE CONVERSION ONLY
//	 */
//	public void setXRight(ArrayList vector) {
//	    needConvertion = true;
//	    m_log.warn("setXRight");
//	}
	
	private transient ArrayList tempL;
	
	/**
	 * OLD XML HERE FOR FILE CONVERSION ONLY
	 */
	public ArrayList getXLeft() {
	    needConvertion = true;
	    m_log.warn("VERSIONNING: getXLeft");
	    if (tempL == null) tempL = new ArrayList();
	    return tempL;
	}
	
	private  transient ArrayList tempR;
	/**
	 * OLD XML HERE FOR FILE CONVERSION ONLY
	 */
	public ArrayList getXRight() {
	    needConvertion = true;
	    m_log.warn("VERSIONNING: getXRight");
	    if (tempR == null) tempR = new ArrayList();
	    return tempR;
	}

}
/* $Log: DoubleSideMap.java,v $
/* Revision 1.2  2007/04/02 17:04:25  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:39  perki
/* First commit on sourceforge
/*
/* Revision 1.18  2004/10/15 06:38:59  perki
/* Lot of cleaning in code (comments and todos
/*
/* Revision 1.17  2004/09/30 15:25:18  perki
/* Better Startp process
/*
/* Revision 1.16  2004/09/30 13:57:36  perki
/* DoubleSideMap is now much stringor.. abandoned ArrayLists for Objects
/*
/* Revision 1.15  2004/09/09 18:38:46  perki
/* Rate by slice on amount are welcome aboard
/*
/* Revision 1.14  2004/09/09 16:38:44  jvaucher
/* - Finished the OptionCommissionAmountUnder, used by RateOnAmount WorkPlace
/* - A bit of cleaning in the DoubleSideMap
/*
/* Revision 1.13  2004/07/08 14:59:00  perki
/* Vectors to ArrayList
/*
/* Revision 1.12  2004/06/28 10:38:47  perki
/* Finished sons detection for Tarif, and half corrected bug for edition in STable
/*
/* Revision 1.11  2004/03/18 18:08:59  perki
/* barbapapa
/*
/* Revision 1.10  2004/03/08 09:56:36  perki
/* houba houba hop
/*
/* Revision 1.9  2004/03/06 14:24:50  perki
/* Tirelipapon sur le chiwawa
/*
/* Revision 1.8  2004/02/22 18:09:20  perki
/* good night
/*
* Revision 1.7  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.6  2004/02/04 19:04:19  perki
* *** empty log message ***
*
* Revision 1.5  2004/02/03 11:41:50  perki
* totally new double sided map
*
* Revision 1.4  2004/02/03 11:31:17  perki
* totally new double sided map
*
* Revision 1.3  2004/01/18 14:23:52  perki
* Naming about to be finished
*
* Revision 1.2  2004/01/14 14:43:30  perki
* Double Side Maps and Options
*
* Revision 1.1  2004/01/14 13:56:48  perki
* *** empty log message ***
*
* 
*/