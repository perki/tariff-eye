/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarifManager.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.components.tarif;

import java.util.HashMap;
import java.util.ArrayList;

import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datatools.ComponentManager;

/**
 * Manager for all tarifs.
 */
public class TarifManager {
	
	/** contains the linking from the type to the classname **/
	private static HashMap tarifClassNames;
	static {
		tarifClassNames = new HashMap();
		tarifClassNames.put(TarifSimple.TARIF_TYPE,TarifSimple.class);
		tarifClassNames.put(TarifAssets.TARIF_TYPE,TarifAssets.class);
		tarifClassNames.put(TarifTransactions.TARIF_TYPE,TarifTransactions.class);
		tarifClassNames.put(TarifRoot.TARIF_TYPE,TarifRoot.class);
		tarifClassNames.put(TarifFutures.TARIF_TYPE,TarifFutures.class);
		tarifClassNames.put(TarifLocalization.TARIF_TYPE,
		        TarifLocalization.class);
	}
	
	/**
	* Check if a TarifType does exists
	* @return true is it does
	*/
	public static boolean checkTarifExists(String tarifType) {
		return tarifClassNames.containsKey(tarifType);
	}

	/**
	* Get the list of defined tarifs Types
	*/
	public static ArrayList getTarifTypes() {
		// clone it to be sure noone is going to modify it
		return new ArrayList(tarifClassNames.keySet());
	}
	
	/**
	 * createTarif
	 * should only be called by Tarification
	 * @see com.simpledata.bc.datamodel.Tarification#createTarif(String, String)
	 */
	public static Tarif 
		createTarif(Tarification t,String title,String tarifType) {
		
		 // create parameters Array
		 Object[] initArgs = new Object[2];
		 initArgs[0] = t;
		 initArgs[1] = title;

		 // create parameters class type to get the right constructor
		 Class[]paramsType = ComponentManager.getClassArray(initArgs);

		 return (Tarif) ComponentManager.getInstanceOf(
		 	(Class) tarifClassNames.get(tarifType),paramsType,initArgs);
		}
}
/** $Log: TarifManager.java,v $
/** Revision 1.2  2007/04/02 17:04:27  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:43  perki
/** First commit on sourceforge
/**
/** Revision 1.9  2004/09/29 12:40:19  perki
/** Localization tarifs
/**
/** Revision 1.8  2004/09/10 14:48:50  perki
/** Welcome Futures......
/**
/** Revision 1.7  2004/07/08 14:58:59  perki
/** Vectors to ArrayList
/**
/** Revision 1.6  2004/03/17 14:28:53  perki
/** *** empty log message ***
/**
/** Revision 1.5  2004/03/04 18:44:23  perki
/** *** empty log message ***
/**
/** Revision 1.4  2004/02/05 07:45:52  perki
/** *** empty log message ***
/**
/** Revision 1.3  2004/02/04 15:42:16  perki
/** cleaning
/**
 * Revision 1.2  2004/02/01 18:27:51  perki
 * dimmanche soir
 *
 * Revision 1.1  2004/02/01 17:15:12  perki
 * good day number 2.. lots of class loading improvement
 *
 */