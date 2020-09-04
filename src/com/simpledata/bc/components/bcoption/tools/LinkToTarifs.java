/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: LinkToTarifs.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.bcoption.tools;

import java.util.ArrayList;

import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;

/**
 * Interface that any object linked to Tarif should implements
 */
public interface LinkToTarifs {
	/**
	 * return the list of Tarifs that in a way could be accepted.
	 * This shoudl return the full range of Tarifs to show. 
	 */
	public ArrayList getAcceptabletarifs();
	
	/**
	 * return true if this Option accepts to be mapped to this Tarif
	 */
	public boolean isAcceptingTarif(Tarif t);
	
	/**
	 * get the list of accepted Tarifs Class
	 */
	public ArrayList getAcceptedTarifs();
	
	/**
	 * add a tarif to this Option<BR>
	 * <B>NOTE!!!</B>  fireNamedEvent(NamedEvent.TARIF_OPTION_LINK_ADDED);
	 * @return true if succeded.. you may want to check with isAcceptingTarif()
	 */
	public boolean addLinkToTarif(Tarif t);
	
	/**
	 * remove a tarif from this Option<BR>
	 * <B>NOTE!!!</B>  fireNamedEvent(NamedEvent.TARIF_OPTION_LINK_DROPED);
	 */
	public void removeLinkToTarif(Tarif t);
	
	/**
	 * get the Tarifs linked to this Option
	 * @return a vector of TarifAssets
	 */
	public ArrayList getLinkedTarifs();
	
	/**
	 * get the Tarifs that are used. (used for partial checking)
	 * @return a vector of TarifAssets
	 */
	public ArrayList getUsedTarifs();
	
	/**
	 * get the Tarification relative to this Object
	 */
	public Tarification getTarification();


}


/**
 *  $Log: LinkToTarifs.java,v $
 *  Revision 1.2  2007/04/02 17:04:27  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:42  perki
 *  First commit on sourceforge
 *
 *  Revision 1.5  2004/09/09 14:12:06  jvaucher
 *  - Calculus for DispatcherBounds
 *  - OptionCommissionAmountUnder... not finished
 *
 *  Revision 1.4  2004/07/08 14:58:59  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.3  2004/03/18 18:08:59  perki
 *  barbapapa
 *
 *  Revision 1.2  2004/03/03 10:17:23  perki
 *  Un petit bateau
 *
 *  Revision 1.1  2004/03/02 15:40:39  perki
 *  breizh cola. le cola du phare ouest
 *
 */