/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: WorkPlaceSimple.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */

/** 
 * Package that contains all Workplaces. Workplace s are a unit 
 * of computation and Dispatcher s are a way of finding the correct
 * applicable unit of computation. 
 */
package com.simpledata.bc.components.worksheet.workplace;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;

/**
* Simpliest Workplace Ever. For debugging and devloppement purpose
*/
public class WorkPlaceSimple extends WorkPlaceAbstract {
	private static final Logger m_log = Logger.getLogger( WorkPlaceSimple.class ); 
	
	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "SimpleWorkPlace";

	private String myScript= null;

	/**
	* constructor.. should not be called by itself. 
	* use WorkSheet.createWorkSheet(Dispatcher d,Class c)
	*/
	public WorkPlaceSimple(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {

		super(parent, title, id, key);
		myScript= "// modify the variable \"(Money) result\"";
	}
	
	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_FULL;
	}

	Interpreter interp;
	
	/**
	 * Calculate the commission taken at this point
	 */
	public  synchronized void privateComCalc(ComCalculus cc,Money value) {
		if (interp == null) interp = new Interpreter();
		Money result = new Money(0d);
		
		StringBuffer sb= new StringBuffer();
		sb.append("//   START WorkPlaceSimple :").append(getNID());
		sb.append("\n").append(myScript).append("\n");
		
		try {
			interp.set("result",result);
			interp.eval(sb.toString());
		} catch (EvalError e) {
			m_log.error( "failed calculs", e );
		}
		value.setValue(result);
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedNewOptions()
	 */
	public Class[] getAcceptedNewOptions() {
		return OptionManager.defaultsOptions();
	}
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#getAcceptedRemoteOptions()
	 */
	public Class[] getAcceptedRemoteOptions() {
		// all
		return 	getAcceptedNewOptions();
	}
	public boolean isValid() { return true ; }
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#_canRemoveOption(BCOption bco)
	 */
	public boolean _canRemoveOption(BCOption bco) { return true; }
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#initializeData()
	 */
	public void initializeData() { }	
	
	/**
	 * @see com.simpledata.bc.datamodel.WorkSheet#copy(com.simpledata.bc.datamodel.WorkSheetContainer, java.lang.String)
	 */
	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
		WorkPlaceSimple copy = 
			(WorkPlaceSimple) WorkSheetManager.createWorkSheet(parent,this.getClass(),key);
		copy.setMyScript(getMyScript().toString());
		return copy;
	}
	
	//	------------- XML ---------------//
	  /** XML CONSTRUCTOR **/
	  public WorkPlaceSimple() {}
	
	/**
	 * @return Returns the myScript.
	 */
	public String getMyScript() {
		return myScript;
	}
	/**
	 * @param myScript The myScript to set.
	 */
	public void setMyScript(String myScript) {
		this.myScript = myScript;
	}
	
	// Visitor
	public void visit(TarifTreeVisitor v) {
		v.caseWorkPlaceSimple(this); 
	}
}
/* $Log: WorkPlaceSimple.java,v $
/* Revision 1.2  2007/04/02 17:04:25  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:39  perki
/* First commit on sourceforge
/*
/* Revision 1.32  2004/11/16 10:36:51  perki
/* Corrig� bug #11
/*
/* Revision 1.31  2004/09/23 14:45:48  perki
/* bouhouhou
/*
/* Revision 1.30  2004/09/08 16:35:14  perki
/* New Calculus System
/*
/* Revision 1.29  2004/09/03 11:47:53  kaspar
/* ! Log.out -> log4j first half
/*
/* Revision 1.28  2004/08/17 11:45:59  kaspar
/* ! Decoupled visitor architecture from datamodel. No illegal
/*   dependencies left, hopefully
/*
/* Revision 1.27  2004/07/26 20:36:09  kaspar
/* + trRateBySlice subreport that shows for all
/*   RateBySlice Workplaces. First Workplace subreport.
/* + Code comments in a lot of classes. Beautifying, moving
/*   of $Id: WorkPlaceSimple.java,v 1.2 2007/04/02 17:04:25 perki Exp $ tag.
/* + Long promised caching of reports, plus some rudimentary
/*   progress tracking.
/*
/* Revision 1.26  2004/07/19 09:36:54  kaspar
/* * Added Visitor for visiting the whole Tarif structure called
/*   TarifTreeVisitor
/* * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
/* * removed superfluous hsqldb.jar
/*
/* Revision 1.25  2004/05/23 12:16:22  perki
/* new dicos
/*
/* Revision 1.24  2004/05/21 13:19:50  perki
/* new states
/*
/* Revision 1.23  2004/05/20 10:36:15  perki
/* *** empty log message ***
/*
/* Revision 1.22  2004/05/18 15:11:25  perki
/* Better icons management
/*
/* Revision 1.21  2004/04/12 17:34:52  perki
/* *** empty log message ***
/*
/* Revision 1.20  2004/04/12 12:33:09  perki
/* Calculus
/*
/* Revision 1.19  2004/04/12 12:30:28  perki
/* Calculus
/*
/* Revision 1.18  2004/04/09 07:16:51  perki
/* Lot of cleaning
/*
/* Revision 1.17  2004/03/23 19:45:18  perki
/* New Calculus Model
/*
/* Revision 1.16  2004/03/18 15:43:33  perki
/* new option model
/*
/* Revision 1.15  2004/03/04 14:32:07  perki
/* copy goes to hollywood
/*
/* Revision 1.14  2004/03/04 11:12:23  perki
/* copiable
/*
/* Revision 1.13  2004/03/02 14:42:48  perki
/* breizh cola. le cola du phare ouest
/*
/* Revision 1.12  2004/02/26 13:24:34  perki
/* new componenents
/*
/* Revision 1.11  2004/02/26 08:55:03  perki
/* *** empty log message ***
/*
/* Revision 1.10  2004/02/22 10:43:56  perki
/* File loading and saving
/*
/* Revision 1.9  2004/02/19 23:57:25  perki
/* now 1Gig of ram
/*
* Revision 1.8  2004/02/19 19:47:34  perki
* The dream is coming true
*
* Revision 1.7  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.6  2004/02/06 10:04:22  perki
* Lots of cleaning
*
* Revision 1.5  2004/02/02 07:00:50  perki
* sevral code cleaning
*
* Revision 1.4  2004/01/20 12:35:27  perki
* Prosper YOupla boum. c'est le roi du pain d'epice
*
* Revision 1.3  2004/01/20 10:29:58  perki
* The Dispatcher Force be with you my son
*
* Revision 1.2  2004/01/19 17:04:29  perki
* pfiuuu
*
* Revision 1.1  2004/01/19 17:02:05  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/19 09:45:34  perki
* WorkPlace creation done
*
* Revision 1.1  2004/01/18 18:43:41  perki
* *** empty log message ***
*
*/