/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 26 janv. 2005
 */
package com.simpledata.bc.components.worksheet;

import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;

/**
 * empty WorkSheet Container just to avoid having null terminals 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: DummyWorkSheetContainer.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
public class DummyWorkSheetContainer extends Named implements
		WorkSheetContainer {
	WorkSheet workSheet;
	private static int counter = 0;
	
	private static final Logger m_log = Logger.getLogger(DummyWorkSheetContainer.class);
	
	public DummyWorkSheetContainer(Tarification t) {
		super("DUMMY",  t,"DUMMYWORKSHEETCONTAINER", "NOID"+counter++);
		
	}

	public boolean setWorkSheet(WorkSheet ws, String key) {
		workSheet = ws;
		return true;
	}

	public WorkSheet getWorkSheetAt(String key) {
		return workSheet;
	}

	public String getWorkSheetKey(WorkSheet ws) {
		return "";
	}


	public ArrayList getChildWorkSheets() {
		ArrayList res = new ArrayList();
		res.add(workSheet);
		return res;
	}

	public Tarif getTarif() {
		return null;
	}

	public Class[] getAcceptedNewWorkSheets(String key) {
		return null;
	}

	/**
	 * */
	public boolean acceptsNewWorkSheet(Class c, String key) {
		return true;
	}

	/**
	 * @see com.simpledata.bc.datamodel.calculus.ComModifier#getComTitle()
	 */
	public String getComTitle() {return "DUMMY";}

	/* *
	 * @see com.simpledata.bc.datamodel.calculus.
	 * ComModifier#startComCalc(ComCalculus, Money, Set)
	 */
	public void startComCalc(ComCalculus cc, Money value, Set depedantModifiers) 
	{}
	
	/** 
	 * <B>Interface ComModifier</B> get the reduction<BR>
	 * Always return null;
	 **/
	public ReducOrFixed getReductionOrFixed() {
        return null;
     }

	public DummyWorkSheetContainer() {
		// Dummy constructor
	}
	
}
