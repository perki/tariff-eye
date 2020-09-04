/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: WorkSheet.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/
package com.simpledata.bc.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.Tarif.TarifTransferRoot;
import com.simpledata.bc.datamodel.calculus.ComCalculus;
import com.simpledata.bc.datamodel.calculus.ComModifier;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.util.CollectionsToolKit;

/**
* WorkSheet (Feuille de calcul in French).
* used for interfacing purpose.. directly use a specialized 
* Dispatcher or WorkPlace
*/
public abstract class WorkSheet extends Named implements Copiable, 
	ComModifier {
	private static final Logger m_log = Logger.getLogger( WorkSheet.class ); 
		
	/** Class type for Named @see Named**/
	public final static String CLASS_TYPE= "WORKSHEET";

	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "UNKOWN";
	
	
	/** contains (or not) the reduction or fixed price to this Tarification**/
	public ReducOrFixed xReductionOrFixed;

	/**
	* constructor.. should not be called by itself.
	*/
	protected WorkSheet(
		WorkSheetContainer parent,
		String title,
		String id,
		String key) {
		super(CLASS_TYPE, (Named) parent, title, id); // call Named
		initializeData();
		setParent( parent,key);
	}
	
	/**
	* set parent (used when copy) or insert<BR>
	* could be overwriten but do not forget to call super.setContainer();<BR>
	* remove the final then...
	*/
	public  final void setParent(WorkSheetContainer wsc,String key) { 
		super.setContainer((Named) wsc);
		if (wsc != null)
			wsc.setWorkSheet(this, key);
		fireNamedEvent(NamedEvent.WORKSHEET_HIERARCHY_CHANGED);
	}
	
	/** return true if this WorkSheet is valid **/
	public abstract boolean isValid();
	
	/** return true if this WorkSheet and the conatined WorksSheet are valid **/
	public final boolean isValidAndChildrensToo() {
		if (! isValid()) return false;
		if (isDispatcher()) {
			Iterator/*<WorkSheet>*/ i = 
				((Dispatcher) this).getChildWorkSheets().iterator();
			WorkSheet temp ;
			while (i.hasNext()) {
				temp = (WorkSheet) i.next();
				if (temp == null) {
					m_log.warn("How comes one of my children is null");
				} else {
					if (! temp.isValidAndChildrensToo()) 
						return false;
				}
			}
		}
		return true;
	}
	
	
	/** create initial options here (if any) **/
	public abstract void initializeData();
	
	/**
	* get the Tarif that contains this WorkSheet.
	*/
	public final Tarif getTarif() {
		if (getWscontainer() == null) return null;
		return getWscontainer().getTarif();
	}
	

	

	
	
	/**
	 * get the list of options applicable for calculus:<BR>
	 * Those are the options from this WorkSheet and the sons Tarifs<BR>
	 * @param type if null then get all options
	 */
	public final ArrayList getOptionsApplicable(Class type) {
		ArrayList/*<BCOption>*/ result = getOptions(type);// get my own options
		
		//	look for sons
		Tarif daddy = getTarif();
		if (daddy == null) {
		    m_log.error("I am not attached to a tariff");
		    return result;
		}
		
		// Only for Tarif that may receive info from others
		if (daddy instanceof TarifTransferRoot) {
			Iterator/*<Tarif>*/ i =
				daddy.getWorkSheetThatTransferOptionsToMe().iterator();
			
			while (i.hasNext()) {
				// add the options to those ones
				CollectionsToolKit.addToCollection(result,
						((WorkSheet) i.next()).getOptionsApplicable(type));
			}
		}
		
		return result;
	}
	
	
	/**
	 * get the list of options attached to this WorkSheet (if any)<BR>
	 * <B>DO NOT USE FOR CALCULUS!!!!</B>: use getApplicableOptions();
	 * @see #getOptionsApplicable();
	 */
	public final ArrayList getOptions() {
		return getTarification().optionsLinks.getLeftOf(this);
	}

	/**
	 * get the list of options of this class type attached to this WorkSheet 
	 * (if any)<BR>
	 * @param type if null result is the same than getOptions()
	 */
	public final ArrayList getOptions(Class type) {
		if (type == null) return getOptions();
		
		Iterator e= getOptions().iterator();
		ArrayList res= new ArrayList();
		BCOption temp= null;
		while (e.hasNext()) {
			temp= (BCOption) e.next();
			if (type.isInstance(temp))
				res.add(temp);
		}
		return res;
	}

	/**
	 * return the list of Options type that can be created at this place
	 */
	public abstract Class[] getAcceptedNewOptions();
	
	/**
	 * return the list of Options type that can acceded remotely
	 */
	public abstract Class[] getAcceptedRemoteOptions();
	
	/** 
	 * check if this option can be removed<BR>
	 * This method is called directly by WorkSheet<BR>
	 * tests for "null" and if this option really exists have already
	 * been made by WorkSheet.canRemoveOption()
	 * **/
	protected abstract boolean _canRemoveOption(BCOption bco);
	
	/**
	 * return true if this option can be removed from this workSheet
	 */
	public final boolean canRemoveOption(BCOption bco) {
		if (isFloating()) {return false;
		}
		if (bco == null) {
			return false;
		}
		if (! xTarification.optionsLinks.pairExists(bco,this)) {
			return false;
		}
		if (_canRemoveOption(bco)) return true;
		
		return false;
	}

	
	

	
	
	/**
	 * add an option to this WorkSheet
	 */
	public final boolean addOption(BCOption bco) {
		if (isFloating()) {
		    m_log.warn("Attached option to floating WS: ["+this+"] : "+bco);
		}
		xTarification.optionsLinks.put(bco,this);
		
		// forward changed to sons that may need it
		if (this instanceof WithExtraNotice)
			((WithExtraNotice) this).noticeOptionListModified(true,bco);
		
		fireNamedEvent(NamedEvent.WORKSHEET_OPTION_ADDED);
		optionDataChanged(bco,null);
		return true;
	}
	
	
	
	/**
	 * remove an option from this worksheet
	 * @return true if succeded
	 */
	public final boolean removeOption(BCOption bco) {
		if (! canRemoveOption(bco)) {
			return false;
		}
		xTarification.optionsLinks.remove(bco, this);
		
		//		 forward changed to sons that may need it
		if (this instanceof WithExtraNotice)
			((WithExtraNotice) this).noticeOptionListModified(false,bco);
		
		fireNamedEvent(NamedEvent.WORKSHEET_OPTION_REMOVED);
		optionDataChanged(bco,null);
		return true;
	}
	
	/**
	 * A used option value changed.<BR>
	 * For now we don't use the passed BCOption .. but it may
	 * be useful to save some calculus.<BR>
	 * id can be null then it is a recalculate
	 * @param bco can be null
	 * @param cc can be null
	 */
	public final void optionDataChanged( BCOption bco, ComCalculus cc ) {
		if (isFloating()) return;
		
		if (bco != null) {
			fireNamedEvent(
				new NamedEvent(
					this,
					NamedEvent.WORKSHEET_OPTION_DATA_CHANGED,
					bco
				)
			);
		}
		
		if (cc == null) {
			getTarification().comCalc().start(this);
		} else {
		    cc.refresh(this);
		}
	}
	
	/**
	 * Com for this workSheet advertises other
	 */
	public final void startComCalc(ComCalculus cc,Money value,
			Set/*<ComModifier>*/ dependantModifiers) {
		if (isFloating()) {
			m_log.warn( "Calculus on a floating object" );
			return;
		}
		
	
        if (isValid()) {
        	privateComCalc(cc,value);	// launch the calculus
        	
        }	
        
		 //advertise my Parent (can be a Tarif or a Dispatcher)
        dependantModifiers.add(getWscontainer());
	}

	
	
	/**
	 * start a com calculation.<BR>
	 * NEVER call it by itself!! use startComCalc(ComCalculus cc)
	 * @see #startComCalc(ComCalculus cc)
	 * @param calc will never be null and always freshly created.
	 */
	protected abstract void privateComCalc(ComCalculus cc,Money Value);
	
	
	/** 
	 * Return the title to display for this commission modifier  <BR>
	 * Normally shoudl output Named.getTitle
	 **/
	public final String getComTitle() {
		return getTarif().getTitle()+":"+getTitle();
	}
	


	/**
	 * return true if this WorkSheet is a Dispatcher
	 */
	public final boolean isDispatcher() {
		return this instanceof Dispatcher;
	}

	/**
	 * get the WorkSheetContainer that contains this WorkSheet
	 */
	public final WorkSheetContainer getWscontainer() {
		return (WorkSheetContainer) getContainer();
	}

	/**
	 * get the Key the WorkSheetContainer uses to reference me
	 */
	public String getContainerKey() {
		if (getWscontainer() == null) return "";
		return getWscontainer().getWorkSheetKey(this);
	}

	
	

	/**
	 * delete this workSheet
	 */
	public final void drop() {
		if (getWscontainer() != null) {
			if (WorkSheetManager
				.createWorkSheet(
					getWscontainer(),
					EmptyWorkSheet.class,
					getWscontainer().getWorkSheetKey(this))
				== null) {
				
			}
		}
		
		setContainer(null);
		 
		// remove all used options references
		xTarification.optionsLinks.remove(null, this);
		xTarification.checkOptionTarifDependecies();
		fireNamedEvent(NamedEvent.WORKSHEET_DROPPED);
	}
	
	/** copy , And attach this dispatcher to a DummyWorkSheet<BR> 
	 * u may prefer using copy(WorkSheetContainer parent,String key)**/
	public final Copiable copy() {
		WorkSheet ws = 
			copy(WorkSheetManager.createDummyWorkSheet(getTarification()),"");
		return ws;
	}
	
	/** 
	 * copy used to get a copy of this WorkSHeet.
	 * The copy must look like the original (if possible).
	 * @param parent , the parent on which to attach the copy (can be null)
	 * @param key the key used by the parent to reference the position of this 
	 * copy
	 **/
	public final WorkSheet copy(WorkSheetContainer parent,String key) {
	    
	    WorkSheet ws = _copy(parent,key);
	    // take care of copping the description and title
	    if (ws == null) {
	        m_log.error("Failed on copy"+parent+" ["+key+"] ",new Exception());
	        return null;
	    }
	    ws.setTitle(getTitle());
		ws.setDescription(getDescription());
	    return ws;
	}
	
	/** 
	 * copy used to get a copy of this WorkSHeet.
	 * The copy must look like the original (if possible).<BR>
	 * will be called by copy(WorkSheetContainer parent,String key); ..
	 * which take care of setting the name and description
	 * @param parent , the parent on which to attach the copy (can be null)
	 * @param key the key used by the parent to reference the position of this 
	 * copy
	 **/
	protected abstract WorkSheet _copy(WorkSheetContainer parent,String key);
	
	
	
	/**
	 * return true if this WorkSheet is "floating" means not attached to the
	 * tarification
	 */
	public final boolean isFloating() {
		return WorkSheetManager.isFloating(this);
	}
	
	
	/** 
	 * get the Reduction or fixed price linked to this WorkSheet<BR>
	 * @return null if none
	 **/
	public ReducOrFixed getReductionOrFixed() {
	    if (! isValid()) return null;
		return xReductionOrFixed;
	}
	
	/** 
	 * delete the currently used ReductionOrFixed
	 **/
	public void dropReductionOrFixed() {
		xReductionOrFixed = null;
		fireNamedEvent(NamedEvent.WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE);
		optionDataChanged(null,null);
	}
	
	/** 
	 * create a reduction or fixed entry.<BR>
	 * has no effect if one already exists
	 **/
	public void createReductionOrFixed() {
		if (getAcceptedReducType() == ReducOrFixed.ACCEPT_REDUC_NO) return;
		if (getReductionOrFixed() != null) return;
		xReductionOrFixed = new ReducOrFixed(this);
		fireNamedEvent(NamedEvent.WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE);
		optionDataChanged(null,null);
	}
	
	/** 
	 * return the type of discount this worksheet accept <BR>
	 * @return one of ReducOrFixed.
	 * **/
	public abstract int getAcceptedReducType();
	
	/** 
	 * fire a Reduction Or Fixed Data Change (values)
	 **/
	public void fireReductionOrFixedDataChange() {
		if (getReductionOrFixed() == null) return;
		fireNamedEvent(NamedEvent.WORKSHEET_DATA_MODIFIED);
		optionDataChanged(null,null);
	}
	
	/**
	 * An interface for WorkSheets that need extra notification on state 
	 * changes
	 */
	public interface WithExtraNotice {
		/** 
		 * an option has been added / removed .. this is just a notice<BR>
		 * some WorkSheet may find usefull to be advertised of this<BR>
		 * <B> THIS IS MADE TO BE OVERWRITEN </B><BR>
		 * @param added true if option added, false if removed
		 * @param bco the option
		 */
		public void noticeOptionListModified(boolean added,BCOption bco);
	}
	
	
	/**
	 * An interface for WorkSheets that are cleareable<BR> 
	 * This means than any WorkSheet implementing this Interface must 
	 * remove all the option they uses. (this is mainly for 
	 * RootWorkSheet that can have many user options)
	 */
	public interface Cleareable {
		/** 
		 * remove all user option
		 */
		public void clear();
	}
	
	
	//---------------------- VISITORS ----------------//
	
	/** A visitor that may be used to visit all childrens of this WorkSheets **/
	public interface Visitor {
	    /** @return false if visit can be stoped **/
	    public boolean worksheetVisited(WorkSheet ws);
	}
	
	/** 
	 * Run this visitor on all my childrens.<BR>
	 * Logic, is : First me, then childrens
	 */
	public final void runOnChildren(Visitor v) {
	    _runOnChildren(v);
	}
	
	/** 
	 * helper for runOnChildren(Visitor v);
	 */
	private boolean _runOnChildren(Visitor v) {
	    if (! v.worksheetVisited(this)) return false;
	    if (this instanceof Dispatcher) {
	        for (Iterator i=(
	                        (Dispatcher) this).getChildWorkSheets().iterator();
	                i.hasNext();) {
	            WorkSheet ws = (WorkSheet) i.next();
	            if (ws == null) {
	                m_log.error("Found a null worksheet will iterating " +
	                		"over dispatcher : "+this.getClass());
	            } else {
	                if (! ws._runOnChildren(v)) {
	                    return false;
	                }
	            }
	        }
	    }
	    return true;
	}

	
	//----------------------- XML -----------------//
	/** XML **/
	protected WorkSheet() {
		
	}

	/** XML **/
	public ReducOrFixed getXReductionOrFixed() {
		return xReductionOrFixed;
	}
	/** XML **/
	public void setXReductionOrFixed(ReducOrFixed reductionOrFixed) {
		xReductionOrFixed = reductionOrFixed;
	}
	
	
}
/**
* $Log: WorkSheet.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:36  perki
* First commit on sourceforge
*
* Revision 1.77  2004/11/17 09:09:49  perki
* first step of discount extraction
*
* Revision 1.76  2004/11/16 10:36:51  perki
* Corrig� bug #11
*
* Revision 1.75  2004/11/15 18:41:24  perki
* Introduction to inserts
*
* Revision 1.74  2004/11/12 15:21:54  jvaucher
* - Fixed # 9: Copy of the description field
*
* Revision 1.73  2004/10/31 13:23:02  perki
* Coorected HUGE BUG in calculus with transfer options
*
* Revision 1.72  2004/10/19 16:57:18  carlito
* dispatcher case upgraded
some catchs in CompactTarifLinkNode
*
* Revision 1.71  2004/10/14 16:39:08  perki
* *** empty log message ***
*
* Revision 1.70  2004/10/11 17:48:08  perki
* Bobby
*
* Revision 1.69  2004/09/23 14:45:48  perki
* bouhouhou
*
* Revision 1.68  2004/09/16 17:26:37  perki
* *** empty log message ***
*
* Revision 1.67  2004/09/09 16:02:16  perki
* Threaded Calculus
*
* Revision 1.66  2004/09/09 12:43:08  perki
* Cleaning
*
* Revision 1.65  2004/09/09 12:14:11  perki
* Cleaning WorkSheet
*
* Revision 1.64  2004/09/08 19:28:55  perki
* Reaprtition now follows Transfer Options
*
* Revision 1.63  2004/09/08 16:35:14  perki
* New Calculus System
*
* Revision 1.62  2004/09/03 14:30:02  perki
* *** empty log message ***
*
* Revision 1.61  2004/09/03 13:25:34  kaspar
* ! Log.out -> log4j part four
*
* Revision 1.60  2004/09/02 16:18:54  perki
* Lot of change in calculus method
*
* Revision 1.59  2004/09/02 15:51:46  perki
* Lot of change in calculus method
*
* Revision 1.58  2004/08/24 12:57:16  kaspar
* ! Documentation spelling fixed
* + Added some documentation, trying to clarify
* ! Changed invalid line endings
*
* Revision 1.57  2004/08/17 11:46:00  kaspar
* ! Decoupled visitor architecture from datamodel. No illegal
*   dependencies left, hopefully
*
* Revision 1.56  2004/08/05 00:23:44  carlito
* DispatcherCase bugs corrected and aspect improved
*
* Revision 1.55  2004/08/02 10:08:43  perki
* introducing distribution for transactions
*
* Revision 1.54  2004/07/31 16:45:56  perki
* Pairing step1
*
* Revision 1.53  2004/07/31 11:06:55  perki
* Still have problems with the progressbar
*
* Revision 1.52  2004/07/26 17:39:36  perki
* Filler is now home
*
* Revision 1.51  2004/07/19 09:36:54  kaspar
* * Added Visitor for visiting the whole Tarif structure called
*   TarifTreeVisitor
* * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
* * removed superfluous hsqldb.jar
*
* Revision 1.50  2004/07/08 14:59:00  perki
* Vectors to ArrayList
*
* Revision 1.49  2004/07/06 11:05:53  perki
* calculus is now synchronized for forward options
*
* Revision 1.48  2004/07/04 14:54:53  perki
* *** empty log message ***
*
* Revision 1.47  2004/06/22 17:11:29  perki
* CompactNode now build from datamodel and added a notice interface to WorkSheet
*
* Revision 1.46  2004/05/31 17:08:05  perki
* *** empty log message ***
*
* Revision 1.45  2004/05/23 12:16:22  perki
* new dicos
*
* Revision 1.44  2004/05/23 10:40:06  perki
* *** empty log message ***
*
* Revision 1.43  2004/05/21 13:19:50  perki
* new states
*
* Revision 1.42  2004/05/20 17:05:30  perki
* One step ahead
*
* Revision 1.41  2004/05/20 10:36:15  perki
* *** empty log message ***
*
* Revision 1.40  2004/05/20 09:39:43  perki
* *** empty log message ***
*
* Revision 1.39  2004/05/19 16:39:58  perki
* *** empty log message ***
*
* Revision 1.38  2004/05/18 15:41:45  perki
* Better icons management
*
* Revision 1.37  2004/05/18 15:11:25  perki
* Better icons management
*
* Revision 1.36  2004/05/18 13:49:46  perki
* Better copy / paste
*
* Revision 1.35  2004/05/11 15:53:00  perki
* more calculus
*
* Revision 1.34  2004/05/10 19:00:51  perki
* Better amount option viewer
*
* Revision 1.33  2004/05/05 12:38:13  perki
* Plus FixedFee panel
*
* Revision 1.32  2004/04/12 17:34:52  perki
* *** empty log message ***
*
* Revision 1.31  2004/04/12 12:33:09  perki
* Calculus
*
* Revision 1.30  2004/04/12 12:30:28  perki
* Calculus
*
* Revision 1.29  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.28  2004/03/23 19:45:18  perki
* New Calculus Model
*
* Revision 1.27  2004/03/23 18:02:18  perki
* New WorkSHeet Panel model
*
* Revision 1.26  2004/03/18 18:08:59  perki
* barbapapa
*
* Revision 1.25  2004/03/18 15:43:33  perki
* new option model
*
* Revision 1.24  2004/03/04 14:32:07  perki
* copy goes to hollywood
*
* Revision 1.23  2004/03/04 11:12:23  perki
* copiable
*
* Revision 1.22  2004/03/03 14:42:11  perki
* Un petit bateau
*
* Revision 1.21  2004/02/26 13:24:34  perki
* new componenents
*
* Revision 1.20  2004/02/26 08:55:03  perki
* *** empty log message ***
*
* Revision 1.19  2004/02/25 17:36:54  perki
* *** empty log message ***
*
* Revision 1.18  2004/02/23 18:46:04  perki
* *** empty log message ***
*
* Revision 1.17  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.16  2004/02/20 03:14:06  perki
* appris un truc
*
* Revision 1.15  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.14  2004/02/19 19:47:34  perki
* The dream is coming true
*
* Revision 1.13  2004/02/17 11:39:21  perki
* zobi la mouche n'a pas de bouche
*
* Revision 1.12  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.11  2004/02/16 11:17:10  perki
* new event model
*
* Revision 1.10  2004/02/06 10:04:22  perki
* Lots of cleaning
*
* Revision 1.9  2004/02/05 15:11:39  perki
* Zigouuuuuuuuuuuuuu
*
* Revision 1.8  2004/02/05 11:07:28  perki
* Transactions are welcome aboard
*
* Revision 1.7  2004/01/20 08:58:36  perki
* Zorglub vaincra............
*
* Revision 1.6  2004/01/19 17:00:42  perki
* *** empty log message ***
*
* Revision 1.5  2004/01/19 10:07:50  perki
* Yehahh
*
* Revision 1.4  2004/01/18 18:43:41  perki
* *** empty log message ***
*
* Revision 1.3  2004/01/18 15:21:18  perki
* named and jdoc debugging
*
* Revision 1.2  2003/12/17 17:57:13  perki
* *** empty log message ***
*
* Revision 1.1  2003/12/16 17:10:42  perki
* *** empty log message ***
*
*/
