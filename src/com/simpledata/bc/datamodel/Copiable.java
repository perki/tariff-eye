/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: Copiable.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel;

/**
 * Like clone (but simple adaptation)
 */
public interface Copiable {
	
	/** return a copy of itslef (like clone) **/
	public Copiable copy();

	/**
	 * Interface for options that are copiable into another one<BR>
	 * BCoptions implementing this interface wil not return a new instance
	 * but fill an option with their value<BR>
	 * <B>This interface is used as TAGGING interface for merging 
	 * an option into another Tarif</B>
	 */
	public interface TransferableOption {
		
		/**
		 * @return true if this Copiable Option can be copied to this class
		 */
		public boolean canCopyValuesInto(Class destination);
		
		/** 
		 * copy all the data of this option into destination<BR>
		 * You can use canCopyValuesInto() to check if this is possible
		 **/
		public void copyValuesInto(BCOption destination);
	}
} 


/**
 *  $Log: Copiable.java,v $
 *  Revision 1.2  2007/04/02 17:04:23  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:36  perki
 *  First commit on sourceforge
 *
 *  Revision 1.2  2004/07/07 17:27:09  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/03/04 11:12:53  perki
 *  copiable
 *
 */