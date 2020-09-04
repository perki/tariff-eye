/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Pairable.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.datamodel.pair;

import com.simpledata.bc.datamodel.Tarif;

/**
 * Tarif that are pairable must implements this interface<BR>
 * It provide different access to the pairing properties
 */
public interface Pairable {
	
	
	/** An undefined error occurred wath your log for details **/
	public static final int CAN_BE_UNDIFINED = 0;
	
	/** A paired can be created **/
	public static final int CAN_BE_OK_CREATE = 1;
	
	/** A paired can be attached **/
	public static final int CAN_BE_OK_ATTACHED = 2;
	
	/** A paired cannnot be created, I'm already paired **/
	public static final int CAN_BE_NOK_ALREADY_PAIRED = -1;
	
	/** A paired cannnot be created, no pair position found **/
	public static final int CAN_BE_NOK_NO_PAIR_POSITION = -2;
	
	/** A paired cannnot be created, proposition is not valid **/
	public static final int CAN_BE_NOK_PROPOSITION_NOT_VALID = -3;
	
	
	
	

	/** @return the Paired Tarif to this one, null if not paired <BR>
	 **/ 
	public Tarif pairedGet();
	
	
	/** @return one of CAN_BE_PAIRED_* **/
	public int pairedCanBe();
	
	/** 
	 * create a default pair for this Tarif <BR>
	 * Or Pair with the value given by pairedGetProposition()<BR>
	 * HAS NO EFFECT IF ALREADY PAIRED OR Tarif.isValid() == false;
	 **/
	public void pairedCreate();
	
	/**
	 * If there is a Pairable node at the desired position we cannot
	 * create a new Tarif, we will link to the tarif returned.<BR>
	 * Take care !! it can happens that two Tarif could be paired then 
	 * the first one found will be returned...
	 * But i will not be paired if Tarif.isValid() == false;
	 */
	public Tarif pairedGetProposition();
	
	/**
	 * break the actual pairing
	 */
	public void pairedBreak();
}

/*
 * $Log: Pairable.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.3  2004/10/12 10:21:35  perki
 * detecting when repartition is not total in transactions
 *
 * Revision 1.2  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.1  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 */