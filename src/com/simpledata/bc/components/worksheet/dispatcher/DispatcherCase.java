/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 15 juin 2004
 * $Id: DispatcherCase.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.components.worksheet.dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionCase;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Money;


/**
 * This dispatcher is used to have a multiple choice with a unique answer
 * i.e : type de gestion : aggressive, conservatrice, mod�r�e
 */
public class DispatcherCase extends DispatcherAbstract 
	implements WorkSheet.WithExtraNotice {
	private static final Logger m_log = Logger.getLogger( DispatcherCase.class ); 

    /** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "DispatcherCase";    
    
	private HashMap subWorkSheets;
	
	private Class[] acceptedOptionClass;
	
	/**
	* constructor.. should not be called by itself. 
	* use WorkSheet#createWorkSheet(Dispatcher d,Class c)
	*/
	public DispatcherCase(WorkSheetContainer parent, String title, String id, String key) {
		super(parent, title, id, key);
	}
	
	public OptionCase getOptionCase() {
	    OptionCase oc = null;
	    ArrayList v = getOptions(OptionCase.class);
	    if (v != null) {
	        if (v.size() == 1) {
	            oc = (OptionCase)v.get(0);
	        }
	    }
	    return oc;
	}
	
	private HashMap getWorkSheetsMap() {
	    if (subWorkSheets == null) {
            subWorkSheets = new HashMap();
        }
	    return subWorkSheets;
	}
	
//	public HashMap getWorkSheetsMapCopy() {
//	    return (HashMap)getWorkSheetsMap().clone();
//	}
	
    public boolean isValid() {
		if (getOptionCase() != null) {
		    // There is only one option
		    return true;
		}
        return false;
    }

    public void initializeData() {
        // We do not attach any option by default
        //new OptionCase(this, "New case");
    }

    public Class[] getAcceptedNewOptions() {
        if (this.acceptedOptionClass == null) {
            this.acceptedOptionClass = new Class[1];
            this.acceptedOptionClass[0] = OptionCase.class;
        }
        
        if (this.getOptions(OptionCase.class).size() == 0) {
            return this.acceptedOptionClass;
		}
        return new Class[0];
    }

    public Class[] getAcceptedRemoteOptions() {
        return getAcceptedNewOptions();
    }

    protected boolean _canRemoveOption(BCOption bco) {
        return false;
    }

    protected void privateComCalc(ComCalculus cc,Money value) {
        OptionCase option = getOptionCase();
        
        if (option == null) return;
        
        String key = option.getSelectedKey();
        
        WorkSheet ws = getWorkSheetAt(key);
        if (ws == null) return;
       
        
        value.setValue(cc.getCom(ws));
    }

    protected WorkSheet _copy(WorkSheetContainer parent, String key) {
        if (!isValid()) return null;
        
   		DispatcherCase copy = (DispatcherCase) 
			WorkSheetManager.createWorkSheet(parent,DispatcherCase.class,key);
   		//TODO faire marcher �a!!
   		BCOption opt = getOptionCase();
    		copy.addOption(opt);
   		HashMap hm = getWorkSheetsMap();
   		for (Iterator i = hm.keySet().iterator() ; i.hasNext();) {
   		    String skey = (String)(i.next());
   		    WorkSheet ws = (WorkSheet)(hm.get(skey));
   		    if (ws != null) 
   		        ws.copy(copy, skey);
   		}
		return copy;
    }

    public int getAcceptedReducType() {
        return ReducOrFixed.ACCEPT_REDUC_FULL;
    }

    public boolean setWorkSheet(WorkSheet ws, String key) {
        //if (ws == null) return false;
        OptionCase oc = getOptionCase();
        if (oc != null) {
            String caseTitle = getOptionCase().getCase(key);
            if (caseTitle != null) { 
            
                getWorkSheetsMap().put(key, ws);
            
                ws.setTitle(caseTitle);
            
                fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
                return true;
            }
        }
        return false;
    }

    public WorkSheet getWorkSheetAt(String key) {
        if ((key == null) || (key.equals(""))) {
            return null;
        }
        WorkSheet ws = null;

        HashMap hm = getWorkSheetsMap();
        if (hm.containsKey(key)) {
            ws = (WorkSheet)(hm.get(key));
        } 
//        else {
//            ws = WorkSheetManager.createWorkSheet(this, EmptyWorkSheet.class, key);
//            fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
//        }
//        
        return ws;
    }

    /**
     * Method used by the graphic version of the dispatcher to
     * determine the workSheet that has to be displayed at index
     * in the tabbedPane 
     * @param index
     * @return
     */
    public WorkSheet getWorkSheetAtIndex(int index) {
        WorkSheet ws = null;
        OptionCase option = getOptionCase();
        if (option != null) {
            String key = option.getKeyForPos(index);
            if (key != null) {
                ws = getWorkSheetAt(key);
            }
        }
        return ws;
    }
    
    /**
     * Method used by the graphic version to determine the index of
     * a given WorkSheet...
     * @param ws
     * @return
     */
    public int getIndexForWorkSheet(WorkSheet ws) {
        int res = -1;
        // We must retrieve the key for this given WorkSheet
        String key = getKeyForWorkSheet(ws);
        if (key != null) {
            res = getOptionCase().getPosForKey(key);
        }
        return res;
    }
    
    private String getKeyForWorkSheet(WorkSheet ws) {
        String key = null;
        HashMap hm = getWorkSheetsMap();
        for (Iterator i = hm.keySet().iterator(); i.hasNext();) {
            String keyTemp = (String)i.next();
            WorkSheet wsTemp = (WorkSheet)hm.get(keyTemp);
            if (wsTemp == ws) {
                key = keyTemp;
                break;
            }
        }
        
        return key;
    }
    
    public String getWorkSheetKey(WorkSheet ws) {
        HashMap hm = getWorkSheetsMap();
        
        for (Iterator i = hm.keySet().iterator() ; i.hasNext();) {
   		    String skey = (String)(i.next());
   		    WorkSheet ws2 = (WorkSheet)(hm.get(skey));
   		    if (ws2 == ws) {
   		        return skey;
   		    }
        }
        return null;
    }
    
    public ArrayList getChildWorkSheets() {
        HashMap hm = getWorkSheetsMap();
        
        ArrayList v = new ArrayList();
        ArrayList pos = new ArrayList();
        OptionCase oc = getOptionCase();
        if (oc != null) {
            for (Iterator i = hm.keySet().iterator(); i.hasNext(); ) {
                // We must reorder them after 
                String key = (String)i.next();
                
                int ind = oc.getPosForKey(key);
                if (ind >= 0) {
                    pos.add(new Integer(ind));
                    v.add(hm.get(key));
                } else {
                    m_log.error( "WorkSheetMap has a workSheet which is not " +
                                "referred in the Option : key : "+key );
//                    m_log.debug( "removing it..." );
//                    i.remove();
                }
            }
            
        } else {
            return new ArrayList();
        }
        
        int n = pos.size();
        ArrayList res = new ArrayList();
        if (n > 0) {
            Object[] objs = new Object[n];
            for (int i=0;i<pos.size();i++) {
                int index = ((Integer)pos.get(i)).intValue();
                objs[index] = v.get(i);
            }
            
            for (int j =0;j<objs.length;j++) {
                res.add(objs[j]);
            }
        }
        return res;
    }

    public void optionChanged(int eventType, String key) {
        //m_log.warn("OPTION CHANGED EVENT eventType : "+eventType+", key : "+key);
        WorkSheet ws;
        switch(eventType) {
        		case OptionCase.CASE_ADDED:
        		    //m_log.warn( "A case was added for key : "+key );
        			ws = WorkSheetManager.createWorkSheet(this, EmptyWorkSheet.class, key);
//        			setWorkSheet(ws, key);
//        			fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
        		    break;
        		case OptionCase.CASE_MODIFIED:
        		    //m_log.warn( "The case at key : "+key+" has been modified" );
        			ws = (WorkSheet)(getWorkSheetsMap().get(key));
        			String t = getOptionCase().getCase(key);
        			ws.setTitle(t);
        			fireNamedEvent(NamedEvent.WORKSHEET_DATA_MODIFIED);
        		    break;
        		case OptionCase.CASE_REMOVED:
        		    //m_log.warn( "The case at key : "+key+" has been removed" );
        			getWorkSheetsMap().remove(key);
        			fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
        		    break;
        		default:
        		    m_log.warn( "Received a wrong eventType : "+eventType );
        }
    }

    /**
     * This method is called whenever an option is added to the dispatcher
     */
    public void noticeOptionListModified(boolean added, BCOption bco) {
        // TODO finish & complete event management
        // An option has been added or removed... tatatata
        if (added) {
            getWorkSheetsMap().clear();
            
            // We analyze the number of cases...
            OptionCase option = getOptionCase();
            
            // Don't no if it is recommended to use the one passed in parameters
            //OptionCase option = (OptionCase)bco;
            
            if (option != null) {
                // We hope very highly that this is the case
                for (int i=0 ; i < option.getNumberOfCases(); i++) {
                    String key = option.getKeyForPos(i);
                    // We create the workSheets by getting them... tricky
                    getWorkSheetAt(key);
                }
            }

        
        } else {
            m_log.warn( "The option has been removed ... shouldn't it be forbidden ?" );
        }
        fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
    }
    
	/// -------------------------- XML -------------------------- ///
	
	public DispatcherCase() {
		// empty
	}
	
	public HashMap getSubWorkSheets() {
			return subWorkSheets;
	}
	
	public void setSubWorkSheets(HashMap hm) {
			this.subWorkSheets = hm;
	}
		
	// Visitor implementation
	/**
	 * Visit this node with the aid of a Visitor. 
	 * @param v Visitor to callback to. 
	 */
	public void visit(TarifTreeVisitor v) {
		v.caseDispatcherCase( this ); 
	}
 
}


/*
 * $Log: DispatcherCase.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:36  perki
 * First commit on sourceforge
 *
 * Revision 1.23  2004/11/17 18:19:12  carlito
 * Design reviewed
 *
 * Revision 1.22  2004/11/17 17:45:30  perki
 * *** empty log message ***
 *
 * Revision 1.21  2004/11/17 17:19:05  perki
 * *** empty log message ***
 *
 * Revision 1.20  2004/11/17 15:26:22  carlito
 * New design for WorkSheetPanel, WorkSheetPanelBorder and DispatcherCasePanel advanced
 *
 * Revision 1.19  2004/11/16 10:36:51  perki
 * Corrig� bug #11
 *
 * Revision 1.18  2004/11/15 18:41:24  perki
 * Introduction to inserts
 *
 * Revision 1.17  2004/11/15 14:57:03  carlito
 * Copy uncommented
 *
 * Revision 1.16  2004/10/04 08:33:08  perki
 * Added Demo prop
 *
 * Revision 1.15  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.14  2004/09/09 12:43:07  perki
 * Cleaning
 *
 * Revision 1.13  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.12  2004/09/07 13:27:27  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/09/03 11:47:53  kaspar
 * ! Log.out -> log4j first half
 *
 * Revision 1.10  2004/08/17 11:45:59  kaspar
 * ! Decoupled visitor architecture from datamodel. No illegal
 *   dependencies left, hopefully
 *
 * Revision 1.9  2004/08/05 00:23:44  carlito
 * DispatcherCase bugs corrected and aspect improved
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
 * Revision 1.6  2004/07/04 14:54:53  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/07/01 14:45:14  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/06/28 12:48:49  carlito
 * Dispatcher case++
 *
 * Revision 1.2  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.1  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 */