/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 * Created on 2 fevr. 2004
 * CopyRight Simple Data 2003 
 * 
 * $Id: CollectionsToolKit.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */

package com.simpledata.util;

import java.util.*;

/**
 * This class defines a serie of tools for collections and arrays manipulation
 */
public class CollectionsToolKit {
	
	/**
	* Utility 
	* convert a Collection into a Vector
	*/
	@SuppressWarnings("unchecked")
	public static Vector convertToVector(Collection a) {
		if (a == null) return null;
		return new Vector(a);
	}
	
	/**
	* Utility 
	* Get a New Vector representing the Object of a not present in b.
	*/
	public static Vector getVectorInAnotInB(Collection a,Collection b) {
		Vector v = new Vector();
		fillCollectionInAnotInB(v,a,b);
		return v;
	}
	
	/**
	* Utility 
	* Get a New ArrayList representing the Object of a not present in b.
	*/
	public static ArrayList getInAnotInB(Collection a,Collection b) {
		ArrayList v = new ArrayList();
		fillCollectionInAnotInB(v,a,b);
		return v;
	}
	
	/**
	 * utility for get in A not in B<BR>
	 */
	private static void 
	fillCollectionInAnotInB(Collection dest,Collection a,Collection b) {
		Object temp = null;
		for (Iterator i = a.iterator(); i.hasNext(); ) {
			temp = i.next();
			if (! b.contains(temp))
				dest.add(temp);
		}
	}
	
	/**
	 * Returns a Vector generated from given array
	 * @param array Array of objects that is meant to be transformed into a Vector
	 * @return
	 */
	public static Vector getVector(Object[] array) {
		Vector res = new Vector();
		fillCollection(res,array);
		return res;
	}
	
	/**
	 * Returns an ArrayList generated from given array
	 * @param array Array of objects that is meant to be transformed into a Vector
	 * @return
	 */
	public static ArrayList getArrayList(Object[] array) {
		ArrayList res = new ArrayList ();
		fillCollection(res,array);
		return res;
	}
	
	/**
	 * Fill a Collection with the given Array
	 * @param c the Collection
	 * @param array the array
	 */
	public static void fillCollection(Collection c, Object[] array) {
	    if (array == null) return;
		for (int i=0; i<array.length; i++) {
			c.add(array[i]);
		}
	}

	/**
	 * Utility 
	 * Add all elements from source to dest if the DOESNOT exist in dest
	 */
	public static void addToCollection(Collection dest,Collection source) {
		if (source == null) return;
		Iterator i = source.iterator();
		Object temp;
		while (i.hasNext()) {
			temp = i.next();
			if (! dest.contains(temp)) {
				dest.add(temp);
			}
		}
	}
	
	/**
	 * Utility 
	 * Add all elements from source to dest if the DOESNOT exist in dest
	 */
	public static void addToCollection(Collection dest,Object[] source) {
		if (source == null) return;
		for (int i = 0; i < source.length ; i++) {
			if (! dest.contains(source[i])) {
				dest.add(source[i]);
			}
		}
	}
	
	/**
	 * Utility,
	 * dest is modified, to represent the inclusion of dest and v.
	 */
	public static void collectionsInclusion(Collection dest,Collection v) {
		dest.retainAll(v);
	}
	
	/**
	 * Utility,
	 * Create a new Array of object in dest and v. 
	 * (only elements not in dest will be added)
	 */
	public static Object[] addToArray(Object[] dest,Object[] v) {
		Vector res= getVector(dest);
		addToCollection(res,v);
		return res.toArray(dest);
	}
	
}
/*
 * $Log: CollectionsToolKit.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.8  2004/09/29 13:33:32  perki
 * Bug -1 in Filler
 *
 * Revision 1.7  2004/07/08 14:53:59  perki
 * Vectors to ArrayList
 *
 * Revision 1.6  2004/07/05 07:23:21  perki
 * minor changes in collections
 *
 * Revision 1.5  2004/07/04 10:57:07  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/06/28 12:50:32  perki
 * Finished sons detection for Tarif, and half corrected bug for edition in STable
 *
 */
