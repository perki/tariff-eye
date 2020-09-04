/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarifMatch.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;

import java.util.ArrayList;
import java.util.Iterator;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.Tarif;

/**
 * A class that contains information on a tarif and the way it matches
 * another one<BR>
 * This class is comparable it means it can be ordered<BR><BR>
 * 
 * <P>
 * <B>TO DEBUG / TEST MATCHING</B><BR>
 * 	be in "Simple MODE" : BC.setIsSimpleTrue()<BR>
 * 	Then you will have ToolTipTexts explaining most af the calculus and 
 *  variables on MergingMonitor.. Over the title labels
 * </P>
 */
public class TarifMatch implements Comparable {
	//-------------- Grading parameters -----------------//
	/** 
	 * this number defines the weight of DLG grading ...<BR>
	 * The higher it is the lower it influence the grading.<BR>
	 * 1 will make the weight of DLG grading the more import
	 */
	final float WEIGHT_OF_DLG_GRADING = 3;
	
	/**
	 * this number define the cost of going from a parent to it's children
	 */
	final float WEIGTH_PARENT_TO_CHILDREN = 3;
	
	/**
	 * this number define the cost of going from a children to it's paent
	 */
	final float WEIGTH_CHILDREN_TO_PARENT = 2;
	
	/**
	 * this number define the extra cost of no matching on the same
	 * position in base tree
	 */
	final float WEIGTH_EXTRA_FOR_BASE_TREE_DIFF = 3;
	
	
	//-------------------  vars -------------------------//
	
	/** the Tarif I'm trying to match **/
	private Tarif sourceTarif;
	
	/** the maximum number of positions that could be matched **/
	private int maxMatchingPossible;
	
	/** the maximum DLGGrading found between me and the other TarifMatch
	 * for this source
	 */
	private float maxDLGGrading;
	
	/** the Tarif I'm linked too **/
	private Tarif tarif;
	
	/** ArrayList containing the different depth I've been found on **/
	private ArrayList/*<Mergeator.DLG>*/ dlgs;
	
	/** grade caching **/
	private float gradeCache;
	
	/** get the shortest distance at with it matches on base tree **/
	private int matchesOnBaseTree = 100;
	
	/** this List contains the UNMATCHED position **/
	private ArrayList/*<BCNode>*/ unMatchedNodes;
	
	/** 
	 * <B>!!! No need to call foundAt() it's called at construction 
	 * @param t the Tarif 
	 * @param dlg the informations about the place it was found
	 * @param position the BCNode it has been found at
	 */
	public TarifMatch(Tarif source,Tarif t,BCNode position,  Mergeator.DLG dlg){
		tarif = t;
		this.sourceTarif = source;
		this.maxMatchingPossible = sourceTarif.getMyMapping().size();
		this.maxDLGGrading = -1;
		gradeCache = 0;
		dlgs = new ArrayList/*<Mergeator.DLG>*/();
		matchesOnBaseTree = 100;
		unMatchedNodes = t.getMyMapping();
		foundAt(position, dlg);
	}
	
	/**
	 * set the Value of the maximum DLGGrading found between me and the other 
	 * TarifMatch for this source
	 */
	public void setMaxDLGGrading(float maxDLGGrading) {
		this.maxDLGGrading = maxDLGGrading;
		calculateGrade();
	}
	
	/**
	 * call this when you found a new occurence of this Tarif in your search
	 * @param position the BCNode it has been found at
	 */
	public void foundAt(BCNode position, Mergeator.DLG dlg) {
		if (position.getTree() == position.getTarification().getTreeBase()) {
		    if (dlg.d < matchesOnBaseTree)
			matchesOnBaseTree = dlg.d;
		}
		
		// remove this node for unMatched
		unMatchedNodes.remove(position);
		
		dlgs.add(dlg.getCopy());
		calculateGrade();
	}
	
	
	
	Boolean isKnownMem;
	/**
	 * @return true if this tarifMatch as already been selected one
	 */
	public boolean isKnown() {
	    if (isKnownMem == null)
	        isKnownMem = new Boolean(MergingMemory.exists(sourceTarif,tarif));
	    return isKnownMem.booleanValue();
	}
	
	
	/**
	 * get the garde associated with this MatchInfo.
	 */
	public float getGrade() {
		return gradeCache;
	}
	/**
	 * get the garde associated with this MatchInfo.
	 */
	private void calculateGrade() {
		assert maxMatchingPossible > 0 :
			"How can I match on something that is not defined in space";
		
		// if does not matches on base Tree : grade = -1;
		if (matchesOnBaseTree == 100) {
			gradeCache = -1;
			return ;
		}
		
		// if does not now the maxDLGGrading
		if (maxDLGGrading <= 0) {
			gradeCache = -1;
			return ;
		}

	
		
		// if all the nodes are matching we have a 100 base
		// if half of the nodes are matching we have a 50 percent base
		// the more we find it the best it matches
		float ranking = (dlgs.size() + 0f ) /(maxMatchingPossible); 
		
		// reduce the result by the times this grading is far in the base tree
		float  distanceInBase = 
		    1f / (matchesOnBaseTree* WEIGTH_EXTRA_FOR_BASE_TREE_DIFF + 1 );

		// if they are dimension of me I was not able to match then it goes down
		float lostDimenensions =  
		    (dlgs.size() + 0f) / (dlgs.size() + unMatchedNodes.size());

		float dlgWeigth = 
		(maxDLGGrading * WEIGHT_OF_DLG_GRADING- getDLGGrading()) 
			/ (maxDLGGrading  * WEIGHT_OF_DLG_GRADING);
		
		
		
		gradeCache = 100f 
			* ranking * distanceInBase * lostDimenensions * dlgWeigth;
		
		// debug
		if (BC.isSimple()) {
		toStringDebugCalculusInfos(
		        new String[] {"ranking","distanceInBase",
		                		"lostDimenensions","dlgWeigth"},
		        new float[] {ranking,distanceInBase,lostDimenensions,dlgWeigth}
		        );
		}
	}
	
	/**
	 * Get The DLG Grading total  of this node
	 */
	public float getDLGGrading() {
		Iterator/*<Mergeator.DLG>*/ e = dlgs.iterator();
		float result = 0f;
		Mergeator.DLG dlg ;	
		
		while (e.hasNext()) {
			dlg =  ((Mergeator.DLG)e.next());
			
			// depth is bad it adds points
			result +=  dlg.d * WEIGTH_PARENT_TO_CHILDREN 
			- dlg.l * (WEIGTH_PARENT_TO_CHILDREN  - WEIGTH_CHILDREN_TO_PARENT); 
		}
		return result;
	}
	
	/**
	 * @return -1 if the grade is BETTER, 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
	    assert o instanceof TarifMatch : 
	        "Cannot compare ["+this+"] with ["+o+"]"; 
	
		TarifMatch mi = (TarifMatch)o;
		
		boolean me = isKnown();
		boolean his = mi.isKnown();
		
		if (me != his) return me ? -1 : 1;
		
		return gradeCache == mi.gradeCache ? 
		        0 : gradeCache > mi.gradeCache ? -1 : 1;
		
	
	}
	
	/** 
	 * return the tarif in this MatchInfo
	 **/
	public Tarif getTarif() {
		return tarif;
	}
	
	/** 
	 * call this to fill this TarifMatch (Tarif) with the passed Tarif
	 */
	public void fillWithTarif(Proposition p) {
		// Paste the option value from one to another
		Mergeator.copyOptionFromTo(getTarif(),p.tarifSource);
		
		MergingMemory.add(p,getTarif());
	}
	
	
	public String toString() {
		return tarif.toString();
	}
	
	
	
	private String debugCalculusInfos = "";
	/**
	 * collect some calculus informations for debug output 
	 */
	private void toStringDebugCalculusInfos(String[] titles,float[] weigths) {
	    debugCalculusInfos = "<TABLE><TR><TD colspan=2><B>Calculus infos</B>";
	    for (int i = 0; i < titles.length ; i++) {
	        debugCalculusInfos += "<TR><TD>"+titles[i]+"<TD>"+weigths[i];
	    }
	    debugCalculusInfos += "</TABLE>";
	}
	
	/**
	 * debug used by tooltip<BR> 
	 * BC.setIsSimpleTrue(true) to see them
	 * **/
	public String toStringDebug() {
		String res = "<HTML><H2>apears only in isSimple Mode</H2>";
		
		//------------ dlg sum
		res += "<BR><B>DLGGRADING:</B>"+getDLGGrading()+"";
		res += "<BR><B>maxGrading:</B>"+maxDLGGrading+"<BR>";
		
		res += "<BR><B>maching size:</B>"+dlgs.size()+"";
		res += "<BR><B>remaining:</B>"+unMatchedNodes.size()+"";
		res += "<BR><B>maxMatching:</B>"+maxMatchingPossible+"<BR>";
		
		res += "<BR><B>matcheson base tree:</B>"+matchesOnBaseTree+"<BR>";
		//------------ dlgs
		Iterator/*<Mergeator.DLG>*/ e = dlgs.iterator();
		res += "<BR><B>DLGS</B>";
		Mergeator.DLG dlg ;	
		while (e.hasNext()) {
			dlg =  ((Mergeator.DLG)e.next());
			// depth is bad it adds points
			res += "<BR>"+dlg.destination.getTree()+" "
					+dlg.destination+" D:"+dlg.d+" L:"+dlg.l+" G:"+dlg.g; 
		}
		
		res += "<BR>"+debugCalculusInfos;
		
		return res+"</HTML>";
	}

	
}

/*
 * $Log: TarifMatch.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.10  2004/11/16 17:22:11  perki
 * Merging now remembers of last picks
 *
 * Revision 1.9  2004/10/08 14:56:04  perki
 * Better Matching / merging logic
 *
 * Revision 1.8  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.7  2004/07/16 18:39:20  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/07/15 17:49:56  perki
 * grading better
 *
 * Revision 1.5  2004/07/12 17:34:31  perki
 * Mid commiting for new matching system
 *
 * Revision 1.4  2004/07/09 20:25:02  perki
 * Merging UI step 1
 *
 * Revision 1.3  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.2  2004/07/07 17:27:09  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/27 14:41:56  perki
 * added merging state alpha
 *
 */