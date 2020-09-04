/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: Special.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/
package com.simpledata.bc.tools;

import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.simpledata.bc.SoftInfos;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.BCTree;
import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datatools.SimpleXMLParser;

/**
* contains special tools that have been moved from BC.java to keep a clear code
*/
public class Special {
	
	/**
	* Retreive the tarification ID in this (XML STRING) @see com.simpledata.bc.datamodel.Named
	* @return null if none
	*/
	public static String getTarificationIDFromXML(String xmlString) {
		String res[] = 
			SimpleXMLParser.getAttrValue
			(xmlString,Tarification.CLASS_TYPE,Named.TAG_NID);
		if (res.length == 0) return null;
		return res[0];
	}
	
	/**
	* Retreive an instance from an ID (XML STRING) @see com.simpledata.bc.datamodel.Named
	* @param tarifi Tarification which to look for. 
	*/
	public static Named getInstanceFromXMLID
	(Tarification tarifi, String xmlString) {
		Document doc = SimpleXMLParser.getDocument(xmlString);
		Element el0 = doc.getDocumentElement();
		// el should be a Tarification TAG
		if (! el0.getTagName().equals(Tarification.CLASS_TYPE)) return null;
		if (! el0.getAttribute(Named.TAG_NID).equals(tarifi.getNID())) 
			return null;
		
		Node n1 = el0.getFirstChild();
		
		// ------- Tarif -----
		if (n1.getNodeName().equals(Tarif.CLASS_TYPE)) { 
			String nid = 
				n1.getAttributes().getNamedItem(Named.TAG_NID).getNodeValue();
			if (nid == "") return null;
			return tarifi.getInstanceForNID(nid);
			
			//	----- WorkSheets ID translation to XML should go here
			
		}
		
		// ----- BCOption
		if (n1.getNodeName().equals(BCOption.CLASS_TYPE)) { 
			String nid = 
				n1.getAttributes().getNamedItem(Named.TAG_NID).getNodeValue();
			if (nid == "") return null;
			return tarifi.getInstanceForNID(nid);
		}
		
		
		// ------- BCTree -----
		if (n1.getNodeName().equals(BCTree.CLASS_TYPE)) { 
			String nid = 
				n1.getAttributes().getNamedItem(Named.TAG_NID).getNodeValue();
			if (nid == "") return null;
			BCTree bct = (BCTree) tarifi.getInstanceForNID(nid);
			if (bct == null) return null;
			
			// ---- BCTree can contains BCNodes
			if (! n1.hasChildNodes()) return bct;
			
			Node n2 = n1.getFirstChild();
			// -------- BCNode
			if (n2.getNodeName().equals(BCNode.CLASS_TYPE)) { 
				String nid2 = 
				n2.getAttributes().getNamedItem(Named.TAG_NID).getNodeValue();
				if (nid == "") return null;
				return tarifi.getInstanceForNID(nid2);
			}
			
			return null;
		}
		
		
		
		
		
		return null;
	}
	
	
	
	
	/**
	* Create an id for a tarification. 
	* %License code of BC%+%YEAR/MONTH/DAY:HOUR:MINUTE:SECONDS%
	*/
	public static String generateTarificationId() {
		Calendar rightNow = Calendar.getInstance();
		StringBuffer res = new StringBuffer(SoftInfos.id());
		res.append("+").append(rightNow.get(Calendar.YEAR)).append("/");
		res.append(rightNow.get(Calendar.MONTH)+1).append("/").
			append(rightNow.get(Calendar.DAY_OF_MONTH)).append("/");
		res.append(rightNow.get(Calendar.HOUR_OF_DAY)).append("/").
			append(rightNow.get(Calendar.MINUTE));
		res.append("/").append(rightNow.get(Calendar.SECOND));
		return res.toString();
	}
}
/** $Log: Special.java,v $
/** Revision 1.2  2007/04/02 17:04:25  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:39  perki
/** First commit on sourceforge
/**
/** Revision 1.16  2004/08/23 08:44:11  kaspar
/** ! Normalized line endings
/** ! License code for another month in BCLoockAndFeel
/**
/** Revision 1.15  2004/06/16 19:04:26  perki
/** *** empty log message ***
/**
/** Revision 1.14  2004/05/22 08:39:35  perki
/** Lot of cleaning
/**
/** Revision 1.13  2004/04/09 07:16:51  perki
/** Lot of cleaning
/**
/** Revision 1.12  2004/03/17 14:28:53  perki
/** *** empty log message ***
/**
/** Revision 1.11  2004/03/06 14:24:50  perki
/** Tirelipapon sur le chiwawa
/**
/** Revision 1.10  2004/02/22 18:09:20  perki
/** good night
/**
/** Revision 1.9  2004/02/19 23:57:25  perki
/** now 1Gig of ram
/**
/** Revision 1.8  2004/02/04 15:42:16  perki
/** cleaning
/**
* Revision 1.7  2004/02/02 07:00:50  perki
* sevral code cleaning
*
* Revision 1.6  2004/01/29 13:40:40  perki
* *** empty log message ***
*
* Revision 1.5  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.4  2004/01/18 15:21:18  perki
* named and jdoc debugging
*
* Revision 1.3  2004/01/18 14:23:52  perki
* Naming about to be finished
*
* Revision 1.2  2004/01/17 17:21:16  perki
* Naming + et +
*
* Revision 1.1  2004/01/06 08:11:23  perki
* *** empty log message ***
*
*/
