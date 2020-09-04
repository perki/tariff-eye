/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: OptionsLinkedToTarifTK.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.bcoption.tools;

import java.util.Iterator;
import java.util.ArrayList;

import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.util.CollectionsToolKit;

/**
 * A ToolKit for options Linked To Tarifs
 */
public class OptionsLinkedToTarifTK {
	
	/**
	 * get the Tarifs linked to Options.
	 * @return a ArrayList of Tarifs
	 * @param o the option
	 * @param tarifClass you can specifiy a class of Tarif. Set to null to get them all
	 */
	public static ArrayList getLinkedTarifs(BCOption o, Class tarifClass) {
		
		ArrayList tarifs = o.getTarification().getOptionTarifsDependenciesFor(o);
		// check if those are only [TarifAssets]
		
		if (tarifClass == null) return tarifs;
		
		ArrayList result = new ArrayList();
		Iterator e = tarifs.iterator();
		Tarif t = null;
		while (e.hasNext()) {
				t = (Tarif) e.next();
				if (tarifClass.isInstance(t))
					result.add(t);
		}
		return result;
	}

	/**
	 * add a tarif to this Option
	 * @return true if succeded.. you may want to check with isAcceptingTarif()
	 */
	public static boolean addLinkToTarif(Tarif t, LinkToTarifs linkedOption) {
		if (! linkedOption.isAcceptingTarif(t)) return false;
		
		assert linkedOption instanceof BCOption : 
			"The OptionsLinkedToTarif must be used with BCOption's";
		BCOption linkedOpt = (BCOption)linkedOption;
		// remove links to tarifs under this one:
		// the tarifs that have the same mapping (or more specialized)
		
		ArrayList/*<Tarif>*/ tarifsMapped 
			= linkedOpt.getTarification().getCompactTree().getSearcher().getAllSonsOf(t);
		
		Tarif tempTarif = null;
		for (Iterator/*<Tarif>*/ i= linkedOption.getLinkedTarifs().iterator(); i.hasNext();) {
			tempTarif = (Tarif) i.next();
			
			// if added Tarif is in this mapping remove this tempTarif
			if (tarifsMapped.contains(tempTarif))
				linkedOption.removeLinkToTarif(tempTarif);
			 
		}
		
		t.addLinkOption(linkedOpt);
		linkedOpt.fireNamedEvent(NamedEvent.TARIF_OPTION_LINK_ADDED);
		linkedOpt.fireDataChange();
		return true;
	}

	/**
	 * remove a tarif from this Option
	 */
	public static void removeLinkToTarif(Tarif t, LinkToTarifs linkedOption) {
		assert linkedOption instanceof BCOption : 
			"The OptionsLinkedToTarif must be used with BCOption's";
		BCOption linkedOpt = (BCOption)linkedOption;
		
		t.removeLinkOption(linkedOpt);
		linkedOpt.fireNamedEvent(NamedEvent.TARIF_OPTION_LINK_DROPED);
		linkedOpt.fireDataChange();
	}
	
	/**
	 * get the list of accepted Tarifs Class
	 */
	public static ArrayList getAcceptedTarifs(LinkToTarifs linkedOption) {
		assert linkedOption instanceof BCOption : 
			"The OptionsLinkedToTarif must be used with BCOption's";
		BCOption linkedOpt = (BCOption)linkedOption;
		
		// from all Tarifs
		ArrayList/*<Tarif>*/ res = linkedOption.getAcceptabletarifs();
	

		// remove all actually linked Tarifs
		res.removeAll(getTarifsUnderLinked(linkedOption));
		
		// remove also options that have registered this 
		// option and their parents (to prevent loops)
		Tarif t;
		for (Iterator/*<WorkSheet>*/ i=
		    linkedOpt.getWorkSheets().iterator();i.hasNext();)
		{
			t = ((WorkSheet)i.next()).getTarif();
			// remove tarif using options
			res.remove(t);
			
			//remove their parents
			res.removeAll(
					linkedOpt.getTarification().getCompactTree().getSearcher().
					getAllParentsOf(t));
		}
		
		
		return res;
	}
	
	public static ArrayList getTarifsUnderLinked(LinkToTarifs linkedOption) {
		assert linkedOption instanceof BCOption : 
			"The OptionsLinkedToTarif must be used with BCOption's";
		BCOption linkedOpt = (BCOption)linkedOption;
		
		ArrayList/*<Tarif>*/ linked = linkedOption.getLinkedTarifs();
		ArrayList/*<Tarif>*/ result = new ArrayList(linked);
		
		for (Iterator/*<Tarif>*/ e = linked.iterator(); e.hasNext();) {
			CollectionsToolKit.addToCollection(result,
				linkedOpt.getTarification().getCompactTree().getSearcher().getAllSonsOf(
						(Tarif) e.next()
				)
			);
		}
		return result;
	}
}


/**
 *  $Log: OptionsLinkedToTarifTK.java,v $
 *  Revision 1.2  2007/04/02 17:04:27  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:42  perki
 *  First commit on sourceforge
 *
 *  Revision 1.5  2004/09/29 12:40:19  perki
 *  Localization tarifs
 *
 *  Revision 1.4  2004/09/09 14:12:06  jvaucher
 *  - Calculus for DispatcherBounds
 *  - OptionCommissionAmountUnder... not finished
 *
 *  Revision 1.3  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.2  2004/03/02 00:32:54  carlito
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/02/26 13:27:36  perki
 *  Mais la terre est carree
 *
 */