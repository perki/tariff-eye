/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Mergeator.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.tarif.TarifAssets;
import com.simpledata.bc.components.tarif.TarifFutures;
import com.simpledata.bc.components.tarif.TarifTransactions;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.Copiable;
import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datatools.FileManagement;


/**
 * A class that should know how to merge a Tarification on another one<BR>
 * It used to "load data from a simulation to a Tarif"
 */
public class Mergeator {
	private static final Logger m_log = Logger.getLogger( Mergeator.class ); 
	
	/**
	 * Merge from file on this Tarification.<BR>
	 * Open a file browser ...
	 * @param origin a Swing component that will serve as Origin for window
	 * to display
	 */
	public static void 
		mergeFromFileOn(final Tarification t,final Component origin) {
		
		Tarification mergeMe = FileManagement.openTarification(
				TarificationHeader.tagsForTypeAnyPortfolio,
				BC.bc.getMajorComponent(), FileManagement.SIMULATOR_OPEN);
		MergingMonitorUI mmui = new MergingMonitorUI(t,mergeMe,origin);
		Mergeator.fillTarificationDwithS(t,mergeMe,mmui);
	}

	
	/**
	 * fill Tarification dest with values from Tarification source
	 */
	public static void testFillTarificationDwithS
		(Tarification dest, Tarification source) {
		fillTarificationDwithS(dest,source,new TestFillMonitor());
	}
	
	/**
	 * fill Tarification dest with values from Tarification source
	 * @param fm is the monitor for this fill process
	 */
	public static void fillTarificationDwithS
					(Tarification dest, Tarification source, FillMonitor fm) {
		
		
		// list of Tarifs where options can be transfered
		Class rootTarifs[] = new Class[] 
				{TarifAssets.class, 
		        TarifTransactions.class, 
		        TarifFutures.class};
		
		
		
		
		// get All Tarifs from source that have to be mapped
		ArrayList/*<Tarif>*/ sourceTarifs
		 	=	source.getTarifsListOfClass(rootTarifs);
		
		// remove from this list TarifsWith No Transferable Options
		ArrayList temp;
		for (Iterator/*<Tarif>*/ i=sourceTarifs.iterator(); i.hasNext();) {
			temp = getTransferableOption((Tarif) i.next());
			if (temp == null || temp.size() == 0) {
				i.remove();
			}
		}
		
		
		// advertise monitor that mapping is started
		fm.init((Tarif[]) sourceTarifs.toArray(new Tarif[0]));
		
		ArrayList/*<Proposition>*/ propositions 
			= new ArrayList/*<Proposition>*/();
		
		Tarif t;
		for (Iterator/*<Tarif>*/ i=sourceTarifs.iterator();i.hasNext();) {
			t = (Tarif)i.next();
			// advertise monitor we are starting to process a Tarif
			fm.startProcessOf(t);
			
			// get the best mapping possible for this Tarif in dest
			ArrayList/*<DLG>*/ bestMapping = getBestMapping(dest,t);
			
			// get a TarifMatch object (propositions of mapping)
			TarifMatch[] proposition = getBestTarifFor(t,bestMapping);
			
			// fill up the Array List 
			propositions.add(new Proposition(t,proposition));
			
		}
		
		// ask the monitor for the Tarif to apply
		fm.chooseMapping(propositions);
	
	}
	
	
	/**
	 * get the list of Option that COULD be transfered from this Tarif<BR>
	 * @return BCOptions (They are all instanceof Copiable.TransferableOption)
	 */
	private static ArrayList/*<BCOption>*/ getTransferableOption(Tarif t) {
		if (t == null || t.getWorkSheet() == null) {
			return null;
		}
		//		 go thru the list of options of the source	
		return t.getWorkSheet().getOptions(Copiable.TransferableOption.class);
	}
	
	/**
	 * copy all the option from this Tarif to this other one
	 */
	protected static void copyOptionFromTo(Tarif dest,Tarif source) {
		ArrayList/*<BCOption>*/ sourceOptions =getTransferableOption(source);
		if (dest == null || dest.getWorkSheet() == null) {
			m_log.warn( 
				"Tarif ["+source+"] has not been mapped, dest or" +
				" dest.worksheet is null"
			);
			return;
		}
		
		
		if (sourceOptions == null ) {
			m_log.warn( "Tarif source is null  or source.worksheet is null" );
			return;
		}
		
		// in fact we only copy the options of the first WorkSheet
		WorkSheet destWS = dest.getWorkSheet();
		
		// get the list of Acceptable options class for dest
		Class[] acceptableOptions = destWS.getAcceptedNewOptions();
		
		// go thru the list of options of the source	
		
		
		
		
		// note all those options are instanceof Copiable.TransferableOption
		Iterator/*<BCOption>*/ i= sourceOptions.iterator();
		BCOption sourceOpt;
		BCOption destOpt;
		while (i.hasNext()) {
			sourceOpt = (BCOption) i.next();
			//	check that the destination accept this class of option
			boolean ok = false;
			for (int j = 0; !ok && j < acceptableOptions.length; j++) 
				if (acceptableOptions[j].isAssignableFrom(sourceOpt.getClass()))
					ok = true;
			
				
			// check that this option contents can be copied
			if (ok && sourceOpt instanceof Copiable.TransferableOption) {
				Copiable.TransferableOption sourceOptCT = 
					(Copiable.TransferableOption) sourceOpt;
				
				if (sourceOptCT.canCopyValuesInto(sourceOpt.getClass())) {
					destOpt = 
						OptionManager.createOption(destWS,sourceOpt.getClass());
					sourceOptCT.copyValuesInto(destOpt);
				} else {
					 m_log.warn( "cannot copy ["+sourceOpt+"]" );
				}
			} else {
				 m_log.warn(
					"cannot find an acceptable type for ["
					+sourceOpt+"]"
				);
			}
		}
		
	}
	

	
	/**
	 * Get the ordered list of tarifs that matches the best<BR>
	 * Based on reasearch on BCnodes only
	 */
	private static TarifMatch[] 
				getBestTarifFor(Tarif src,ArrayList/*<DLG>*/ destMapping){
		HashMap/*<Tarif,TarifMatch>*/ result 
			= new HashMap/*<Tarif,TarifMatch>*/();
		
		//		 for each node
		DLG tempDLG4Children;
		DLG tempDLG4Parent;
		for (Iterator/*<DLG>*/ i=destMapping.iterator(); i.hasNext();) {
			tempDLG4Parent = (DLG) i.next();
			
			
			// geChildren will go one step up by itself just downgrade it
			tempDLG4Children = tempDLG4Parent.getCopy();
			tempDLG4Children.d--;
			tempDLG4Children.g--;
			
			// search into childrens
			getNodeDistanceRecurChildren(src,
					tempDLG4Children.destination,result,tempDLG4Children);
			
			// search into parents
			getNodeDistanceRecurParentOf(src,
					tempDLG4Parent.destination,result,tempDLG4Parent);
		}
				
		// fill up an ArrayList with the result
		// and find the largest DLGGrading
		ArrayList/*<TarifMatch>*/ resultA = new ArrayList/*<TarifMatch>*/();
		TarifMatch tm;
		float maxDLGGrade = 0;
		for (Iterator/*Map.Entry*/ i=result.entrySet().iterator(); i.hasNext();)
		{
			tm = (TarifMatch) ((Map.Entry) i.next()).getValue();
			resultA.add(tm);
			if (tm.getDLGGrading() > maxDLGGrade) 
				maxDLGGrade = tm.getDLGGrading();
		}
		
		// advertise all TarifMatch of the maxgrade
		for (Iterator/*TarifMatch*/ i=resultA.iterator(); i.hasNext();)
		{
			((TarifMatch) i.next()).setMaxDLGGrading(maxDLGGrade);
		}
		

		// order the results
		Collections.sort(resultA);
		
		
		return (TarifMatch[]) resultA.toArray(new TarifMatch[0]);
	}
	

	/**
	 * This will return an array of (unique) BCNode that matches the best
	 * this tarif in this tarification. <BR>
	 * BCNodes are bundleded in a DLG 
	 */
	private static ArrayList/*<DLG>*/
			getBestMapping(Tarification destTn,Tarif sourceTf) {
		ArrayList destMap = new ArrayList/*<DLG>*/();

		DLG tempD ; //destination
		BCNode tempS ;
		
		// loop on all BCNode matching sourceTf
		Iterator/*<BCNode>*/ i = sourceTf.getMyMapping().iterator();
		while (i.hasNext()) {
			tempS = (BCNode) i.next();
			// get the BCNode that match the best tempS on dest tarification
			tempD = getFirstMatchingParent(destTn, tempS);
			if (tempD == null) {
				m_log.warn(
					"Cannot match :"+tempS
				);
			} else {
				// check if this node is known
				DLG temp;
				boolean found = false;
				Iterator/*<DLG>*/ it=destMap.iterator();
				while (! found && it.hasNext()){
					temp = (DLG) it.next();
					if (temp.destination == tempD.destination) {
						found = true;
						temp.d = temp.d > tempD.d ? temp.d : tempD.d;
					}
				}
				if (! found) destMap.add(tempD);
			}
		}
		return destMap;
	}
	
	/**
	 * get the first BCNode of Tarification destT that is found in the
	 * "parent�e" of source.<BR>
	 * The BCNode comes bundled in a DLG denoting it's distance.
	 */
	private static DLG getFirstMatchingParent(Tarification destT,BCNode n) {
		ArrayList/*<BCNode>*/ ancestors = n.getTree().getAncestorsOf(n);
		Named temp;
		int distance = 0; // distance counter
		for (int i = (ancestors.size() -1 ) ; i > -1 ; i--) {
			// try to find a BCNode in n Ancestors (based on NID search)
			temp = destT.getInstanceForNID(((BCNode)ancestors.get(i)).getNID());
			if (temp != null && temp instanceof BCNode) {
				return new DLG((BCNode) temp,distance,0,0);
			}
			distance++;
		}
		
		return null;
	}
	
	
	/**
	 * Interface for fillTarificationDwithS<BR>
	 * This interface is used for user input and feedback on process
	 */
	public interface FillMonitor {
		
		/** 
		 * this does initialize the process giving the list of Tarif that
		 * will be mapped
		 */
		public void init(Tarif[] listOfTarifToProcess);
		
		/**
		 * this is called each time a Tarif is processed<BR>
		 * <B>This does close the previously processed Tarif</B>
		 * You can use this information to display some status information
		 */
		public void startProcessOf(Tarif tarifSource);
		
		/**
		 * this is called when all the research has been done<BR>
		 * Then you have to call one of the proposition fillWithTarif(t);
		 * @param  proposition is of type ArrayList&lt;Proposition>
		 * @see TarifMatch#fillWithTarif(Tarif t)
		 */
		public void chooseMapping(ArrayList/*<Proposition>*/ proposition);
		
	}
	
	//--------------- Matching Tarif research ------------------------//
	
	/**
	 * DistanceLevelGeneration<BR>
	 * Contains informations on a TarifMatching ..<BR>
	 * It first of all contains the references BCNode in the destination tree
	 * <BR> 
	 * it contains the Distance, the Generation and the Level<BR>
	 * The level is the generation of the first common ancestor, the level  
	 * is set to 0 for sons<BR>
	 * 0,0,0 is for the best matching node.<BR>
	 * <PRE>
	 * examples :
	 * direct parent : 1,-1,-1
	 * parent parent : 2,-2,-2
	 * brother       : 2,-1,0
	 * oncle         : 3,-2,-1
	 * cousin		 : 4,-2,0 (oncle's childs)
	 * direct childs : 1,0,1
	 * grand children: 2,0,2
	 * </PRE>
	 */
	public static class DLG {
		BCNode destination;
		int d , l ,g ;
		DLG(BCNode destination,int d, int l, int g) {
			this.d = d; this.l = l; this.g = g;
			this.destination = destination;
		}
		
		/** return a copy of this DLG. like a clone() but no cast needed **/
		public DLG getCopy() {
			return new DLG(destination,d,l,g);
		}
	}
	

	/**
	 * from a point in BCTree, recursively go into PARENTS in a Tree to 
	 * calculate distances
	 */
	private static void 
	getNodeDistanceRecurParentOf(Tarif src,BCNode son,HashMap result,DLG dlg2) {
		// copy dlg because we modify it
		DLG dlg = dlg2.getCopy();
		
		if (son.isRoot()) return; // done
		BCNode node = son.getParent();
		dlg.d++;
		dlg.l--;
		dlg.g--;
		
		//	commit nodes here
		checkForTarifHere(src,result,node,dlg);
		
		// go recursively into childrens but not into passed node
		BCNode temp;
		for (Iterator/*<BCNode>*/ i=node.getChildrens().iterator();i.hasNext();)
		{
			temp = (BCNode) i.next();
			if (temp != son) getNodeDistanceRecurChildren(src,temp,result,dlg);
		}
		
		getNodeDistanceRecurParentOf(src,node,result,dlg);
	}
	
	/**
	 * from a point in BCTree, recursively go int CHILDRENS in a Tree to 
	 * calculate distances
	 */
	private static void 
	getNodeDistanceRecurChildren(Tarif src,BCNode node,HashMap result,DLG dlg2) {
		// copy dlg because we modify it
		DLG dlg = dlg2.getCopy();
	
		dlg.d++;
		// nothing for dlg.l
		dlg.g++;
		
		
		// commit nodes here
		checkForTarifHere(src,result,node,dlg);
		
		for (Iterator/*<BCNode>*/ i=node.getChildrens().iterator();i.hasNext();)
		{
			getNodeDistanceRecurChildren(src,(BCNode) i.next(),result,dlg);
		}
	}
	
	/**
	 * A Tarif has been found 
	 */
	private static void 
		checkForTarifHere(Tarif source,HashMap map,BCNode node,DLG dlg) {
		Iterator/*<Tarif>*/ i = node.getTarifMapping().iterator();
		Tarif t;
		while (i.hasNext()) {
			t = (Tarif) i.next();
			if (map.containsKey(t)) { // no need for more check already known 
				((TarifMatch) map.get(t)).foundAt(node,dlg);
			} else {
				if (canBeMapped(source,t)) {
					map.put(t,new TarifMatch(source,t,node,dlg));
				}
			}
			
		}
	}
	
	
	/**
	 * Check if a Tariff can be mapped over another one<BR>
	 * simple check (source.getClass() == dest.getClass());
	 * @return true if this is possible to map s over d
	 */
	private static boolean canBeMapped(Tarif source,Tarif dest) {
		return (source.getClass() == dest.getClass());
	}
	
}

/** to be removed **/
class TestFillMonitor implements Mergeator.FillMonitor {

	private static final Logger m_log = Logger.getLogger( TestFillMonitor.class ); 
	
	/**
	 * @see com.simpledata.bc.merging.Mergeator.
	 * FillMonitor#init(com.simpledata.bc.datamodel.Tarif[])
	 */
	public void init(Tarif[] listOfTarifToProcess) {
		m_log.info(
			"Started processing with: "+
			listOfTarifToProcess.length+" tarfis"
		);
	}
	
	/**
	 * @see com.simpledata.bc.merging.Mergeator.
	 * FillMonitor#startProcessOf(com.simpledata.bc.datamodel.Tarif)
	 */
	public void startProcessOf(Tarif tarifSource) {
		m_log.info( "**** processing"+tarifSource );
	}
	
	/**
	 * @see com.simpledata.bc.merging.Mergeator.
	 * FillMonitor#chooseMapping(ArrayList)
	 */
	public void chooseMapping(ArrayList/*<Proposition>*/ propositions) {
		Proposition prop;
		for (Iterator/*<Proposition>*/ j=propositions.iterator(); j.hasNext();){
			prop = (Proposition) j.next();
			m_log.debug( "confirm:" );
			for (int i = 0; i < prop.matches.length ; i++) {
				m_log.debug( "   "+prop.matches[i] );
			}
	
			if (prop.matches.length > 0) {
				prop.matches[0].fillWithTarif(prop);
			}
		}
	}
	
}

/*
 * $Log: Mergeator.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.16  2004/11/16 17:22:11  perki
 * Merging now remembers of last picks
 *
 * Revision 1.15  2004/11/16 15:17:55  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.14  2004/09/23 11:00:48  jvaucher
 * Improved filechooser rendering
 *
 * Revision 1.13  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.12  2004/09/14 14:46:29  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.10  2004/07/19 17:39:08  perki
 * *** empty log message ***
 *
 * Revision 1.9  2004/07/16 18:39:20  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/07/15 17:49:56  perki
 * grading better
 *
 * Revision 1.7  2004/07/15 07:49:57  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/07/12 17:34:31  perki
 * Mid commiting for new matching system
 *
 * Revision 1.5  2004/07/09 20:25:02  perki
 * Merging UI step 1
 *
 * Revision 1.4  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.3  2004/07/07 17:27:09  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/28 12:42:33  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/27 14:41:56  perki
 * added merging state alpha
 *
 */