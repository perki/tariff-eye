/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: SerializableWeakHashMap.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.tools;

import java.io.Serializable;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 *  A WeakHashMap that can be serialized
 */
public class SerializableWeakHashMap
	extends WeakHashMap
	implements Serializable {
	private void readObject(java.io.ObjectInputStream stream)
		throws java.io.IOException, java.lang.ClassNotFoundException {
		putAll((HashMap) stream.readObject());
	}

	private void writeObject(java.io.ObjectOutputStream stream)
		throws java.io.IOException {
		stream.writeObject(new HashMap(this));
	}
	public String toString() {
	    return "SerializableWeakHashMap size:"+size();
	}
}

/**
 *  $Log: SerializableWeakHashMap.java,v $
 *  Revision 1.2  2007/04/02 17:04:25  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:39  perki
 *  First commit on sourceforge
 *
 *  Revision 1.3  2004/09/16 17:26:38  perki
 *  *** empty log message ***
 *
 *  Revision 1.2  2004/03/17 14:28:53  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/03/06 11:50:06  perki
 *  Tirelipapon sur le chiwawa
 *
 */