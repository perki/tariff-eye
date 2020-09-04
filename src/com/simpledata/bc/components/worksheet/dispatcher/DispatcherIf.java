/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 mai 2004
 * $Id: DispatcherIf.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.components.worksheet.dispatcher;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionBoolean;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.money.Money;


/**
 * This class represents a standard simple choice
 * i.e. Are you swiss? : Yes or No
 */

public class DispatcherIf extends DispatcherAbstract {

    private final static Logger m_log = Logger.getLogger(DispatcherIf.class);
    
    /** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "DispatcherIf";
    
    private WorkSheet yesWorkSheet;
    private WorkSheet noWorkSheet;
    
    //private OptionBoolean option;
    
    private Class[] acceptedOptionClass;
	
	/**
	* constructor.. should not be called by itself. 
	* use WorkSheet#createWorkSheet(Dispatcher d,Class c)
	*/
	public DispatcherIf(WorkSheetContainer parent, String title, String id,	String key) {
		super(parent, title, id, key);
	}
    
    public void initializeData() {
        new OptionBoolean(this, "Option");
    }

    public Class[] getAcceptedNewOptions() {
        if (this.acceptedOptionClass == null) {
            this.acceptedOptionClass = new Class[1];
            this.acceptedOptionClass[0] = OptionBoolean.class;
        }
        
        if (this.getOptions(OptionBoolean.class).size() == 0) {
            return this.acceptedOptionClass;
		}
        return new Class[0];        
    }

    /** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public int getAcceptedReducType() {
		return ReducOrFixed.ACCEPT_REDUC_FULL;
	}
    
    public Class[] getAcceptedRemoteOptions() {
        return getAcceptedNewOptions();
    }

    public boolean _canRemoveOption(BCOption bco) {
        return true;
    }

    public OptionBoolean getOptionBoolean() {
        return (OptionBoolean)this.getOptions().get(0);
    }
    
	/**
	 * Calculates the fee taken in the chosen branch of this 
	 * dispatcher. 
	 */
	public void privateComCalc(ComCalculus cc,Money value) {

		OptionBoolean option = (OptionBoolean)this.getOptions().get(0);
		
		WorkSheet ws = getWorkSheetAt(
				option.getState() ? YES_WORKSHEET : NO_WORKSHEET);
		
		value.setValue(cc.getCom(ws));
	}

	protected WorkSheet _copy(WorkSheetContainer parent, String key) {
    		DispatcherIf copy = 
			(DispatcherIf) WorkSheetManager.createWorkSheet(
			        parent,DispatcherIf.class,key);
    		
    		this.yesWorkSheet.copy(copy, DispatcherIf.YES_WORKSHEET);
    		this.noWorkSheet.copy(copy, DispatcherIf.NO_WORKSHEET);
    		
		return copy;
    }

    public boolean setWorkSheet(WorkSheet ws, String key) {
        if (ws == null) return false;
        if (key.equals(DispatcherIf.YES_WORKSHEET)) {
            this.yesWorkSheet = ws;
            return true;
        }
        if (key.equals(DispatcherIf.NO_WORKSHEET)) {
            this.noWorkSheet = ws;
            return true;
        }
        return false;
    }

    public static String YES_WORKSHEET = "yes_worksheet";
    public static String NO_WORKSHEET = "no_worksheet";
    
    public WorkSheet getWorkSheetAt(String key) {
        if (key.equals(DispatcherIf.YES_WORKSHEET)) {
            if (this.yesWorkSheet == null) {
                WorkSheetManager.createWorkSheet(this, 
                        EmptyWorkSheet.class, DispatcherIf.YES_WORKSHEET);
            }
            return this.yesWorkSheet;
        }
        if (key.equals(DispatcherIf.NO_WORKSHEET)) {
            if (this.noWorkSheet == null) {
                WorkSheetManager.createWorkSheet(this, 
                        EmptyWorkSheet.class, DispatcherIf.NO_WORKSHEET);
            }
            return this.noWorkSheet;
        }
        // Wrong key
        return null;
    }

    public String getWorkSheetKey(WorkSheet ws) {
        String key = DispatcherIf.YES_WORKSHEET;
        if (ws == this.getWorkSheetAt(key)) {
            return key;
        }
        key = DispatcherIf.NO_WORKSHEET;
        if (ws == this.getWorkSheetAt(key)) {
            return key;
        }
        // Wrong workSheet
        return null;
    }
    
    public ArrayList getChildWorkSheets() {
        ArrayList res = new ArrayList();
        res.add(this.getWorkSheetAt(DispatcherIf.YES_WORKSHEET));
        res.add(this.getWorkSheetAt(DispatcherIf.NO_WORKSHEET));
        return res;
    }

	public boolean isValid() {
		if (this.getOptions(OptionBoolean.class).size() == 0) {
		    // There is no option
		    return false;
		}
		if ((this.getWorkSheetAt(DispatcherIf.YES_WORKSHEET) instanceof EmptyWorkSheet) 
		     && (this.getWorkSheetAt(DispatcherIf.NO_WORKSHEET) instanceof EmptyWorkSheet)) {
		    // Both workSheets are empty
		    return false;
		}
		return true;
	}
	
	/// ------ XML -------- ////
	
	/**
	 * XML
	 */
	public DispatcherIf() {
	    m_log.error("Created dispatcher if");    
	}    
	
	/**
	 * XML
	 */
	public WorkSheet getNoWorkSheet() {
			return noWorkSheet;
	}
	/**
	 * XML
	 */
	public void setNoWorkSheet(WorkSheet noWorkSheet) {
			this.noWorkSheet = noWorkSheet;
	}
	/**
	 * XML
	 */
	public WorkSheet getYesWorkSheet() {
			return yesWorkSheet;
	}
	/**
	 * XML
	 */
	public void setYesWorkSheet(WorkSheet yesWorkSheet) {
			this.yesWorkSheet = yesWorkSheet;
	}
	
	// Visitor implementation
	
	public void visit( TarifTreeVisitor v ) {
		v.caseDispatcherIf( this ); 
	}

}

/*
 * $Log: DispatcherIf.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:37  perki
 * First commit on sourceforge
 *
 * Revision 1.16  2004/11/16 10:36:51  perki
 * Corrig� bug #11
 *
 * Revision 1.15  2004/11/15 18:54:04  carlito
 * DispatcherIf removed... workSheet and option cleaning...
 *
 * Revision 1.14  2004/10/04 08:33:08  perki
 * Added Demo prop
 *
 * Revision 1.13  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.12  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.11  2004/09/07 13:27:27  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/08/24 12:56:14  kaspar
 * ! Corrected some identation
 *
 * Revision 1.9  2004/08/17 11:45:59  kaspar
 * ! Decoupled visitor architecture from datamodel. No illegal
 *   dependencies left, hopefully
 *
 * Revision 1.8  2004/07/19 09:36:53  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.7  2004/07/08 14:58:59  perki
 * Vectors to ArrayList
 *
 * Revision 1.6  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/28 17:02:21  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/23 12:16:22  perki
 * new dicos
 *
 * Revision 1.3  2004/05/21 13:19:49  perki
 * new states
 *
 * Revision 1.2  2004/05/20 10:36:15  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/18 19:11:52  carlito
 * *** empty log message ***
 *
 */