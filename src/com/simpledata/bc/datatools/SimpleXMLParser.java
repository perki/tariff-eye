/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: SimpleXMLParser.java,v 1.2 2007/04/02 17:04:27 perki Exp $
*/


package com.simpledata.bc.datatools;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.apache.log4j.Logger;

/**
* Tools for XML Parsing
*/
public class SimpleXMLParser
{
	private static final Logger m_log = Logger.getLogger( SimpleXMLParser.class ); 
		
	public static Document getDocument(String xmlString)  {
		Document doc = null;
		try {
		// Step0 convert the string to a stream
		ByteArrayInputStream xmlData = new ByteArrayInputStream(xmlString.getBytes());
		
		// Step 1: create a DocumentBuilderFactory
		DocumentBuilderFactory dbf =
		DocumentBuilderFactory.newInstance();
		
		// Step 2: create a DocumentBuilder
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		// Step 3: parse the input file to get a Document object
		doc = db.parse(xmlData);
		} catch (Exception e ) {
			m_log.error( "SimpleXMLParser: getDocuement()", e );
		}
		return doc;
	}    
	
	/**
	* return the attribute 
	*/
	public static String[] getAttrValue(String xml,String element,String arg)  {
		
		Document document = getDocument(xml);
		NodeList els = document.getElementsByTagName(element);
		
		String[] res = new String[els.getLength()];
		
		for (int i = 0; i < res.length; i++) {
			res[i] = els.item(i).getAttributes().getNamedItem(arg).getNodeValue();
		}
		
		return res;
	}
	
	
}
/*
* $Log: SimpleXMLParser.java,v $
* Revision 1.2  2007/04/02 17:04:27  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:42  perki
* First commit on sourceforge
*
* Revision 1.7  2004/09/03 13:25:34  kaspar
* ! Log.out -> log4j part four
*
* Revision 1.6  2004/06/16 19:04:26  perki
* *** empty log message ***
*
* Revision 1.5  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.4  2004/01/22 15:40:51  perki
* Bouarf
*
* Revision 1.3  2004/01/22 13:03:32  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/18 14:23:52  perki
* Naming about to be finished
*
* Revision 1.1  2004/01/17 17:21:16  perki
* Naming + et +
*
*/