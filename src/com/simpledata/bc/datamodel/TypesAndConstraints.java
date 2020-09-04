/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
*$Id: TypesAndConstraints.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/

package com.simpledata.bc.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

import java.awt.Color;
import java.io.Serializable;

/**
* <PRE>
* Handles typage and relation between Tarifs and Trees.
* This object is design to handles a better serialization of objects and types.
* 
* NOTE: order in which Trees are entered does count!!
*
* </PRE>
*/
public class TypesAndConstraints implements Serializable {
	
	
	// end of types declarations
	
	private String baseTreeCode;
	private ArrayList treesCodes;
	private HashMap treesTitles;
	private HashMap treesColors;
	
	public static Color[] colors;
	private int treeColorCounter; // counter for tree color variations
	
	
	
	/**
	*
	*/
	public TypesAndConstraints () {
		// colors
		colors = new Color[5];
		colors[0] = new Color(1f,0f,0f,0.2f); // red
		colors[1] = new Color(0f,1f,0f,0.2f); // green
		colors[2] = new Color(0f,0f,1f,0.2f); // blue
		colors[3] = new Color(1f,1f,0f,0.2f); // yellow
		colors[4] = new Color(1.0f,0.5f,0.3f,0.2f); // orange
		treeColorCounter = 0;
		treesColors = new HashMap();
		
		treesCodes = new ArrayList();
		treesTitles = new HashMap();
	}
	
	
	//--------------------- TREES ---------------------------//
	
	/** return the tree code of the base Tree **/
	public String getBaseTreeCode() {
		return baseTreeCode;
	}
	
	
	/** 
	* add a Tree definition
	* @param treeCode the access code of this tree
	**/
	public void addTree(String treeCode,String treeTitle) {
		if (treesCodes.contains(treeCode)) return;
		treesCodes.add(treeCode);
		treesTitles.put(treeCode,treeTitle);
		treesColors.put(treeCode,colors[treeColorCounter]);
		treeColorCounter++;
		if (treeColorCounter >= colors.length) treeColorCounter = 0;
	}
	
	
	/**
	* get the color corresponding to this tree
	*/
	public Color getTreeColor(String treeCode) {
		return (Color) treesColors.get(treeCode);
	}
	
	/** return the title of this tree **/
	public String getTreeTitle(String treeCode) {
		return (String) treesTitles.get(treeCode);
	}
	
	/** return a list of all the tree codes **/
	public String[] getTreesCodes() {
		return (String[]) treesCodes.toArray(new String[0]);
	}
	
	// --------------------------- XML --------------------------//

	
	

	/**
	 * XML
	 */
	public int getTreeColorCounter() {
		return treeColorCounter;
	}

	/**
	 * XML
	 */
	public HashMap getTreesColors() {
		return treesColors;
	}

	/**
	 * XML
	 */
	public HashMap getTreesTitles() {
		return treesTitles;
	}

	/**
	 * XML
	 */
	public void setBaseTreeCode(String string) {
		baseTreeCode= string;
	}


	/**
	 * XML
	 */
	public void setTreeColorCounter(int i) {
		treeColorCounter= i;
	}

	/**
	 * XML
	 */
	public void setTreesCodes(ArrayList vector) {
		treesCodes= vector;
	}

	/**
	 * XML
	 */
	public void setTreesColors(HashMap map) {
		treesColors= map;
	}

	/**
	 * XML
	 */
	public void setTreesTitles(HashMap map) {
		treesTitles= map;
	}

}
/*$Log: TypesAndConstraints.java,v $
/*Revision 1.2  2007/04/02 17:04:23  perki
/*changed copyright dates
/*
/*Revision 1.1  2006/12/03 12:48:36  perki
/*First commit on sourceforge
/*
/*Revision 1.14  2004/07/08 14:59:00  perki
/*Vectors to ArrayList
/*
/*Revision 1.13  2004/04/09 07:16:51  perki
/*Lot of cleaning
/*
/*Revision 1.12  2004/02/26 10:27:37  perki
/*TAC goes to hollywood
/*
/*Revision 1.11  2004/02/22 15:57:25  perki
/*Xstream sucks
/*
/*Revision 1.10  2004/02/22 10:43:57  perki
/*File loading and saving
/*
*Revision 1.9  2004/02/01 17:15:12  perki
*good day number 2.. lots of class loading improvement
*
*Revision 1.8  2004/01/30 15:18:12  perki
**** empty log message ***
*
*Revision 1.7  2004/01/23 10:23:34  perki
*Dans les annees 70 la couleur apparu
*
*Revision 1.6  2004/01/18 15:21:18  perki
*named and jdoc debugging
*
*Revision 1.5  2004/01/06 17:46:40  perki
*better constraints handling for TNode
*
*Revision 1.4  2004/01/06 17:33:08  perki
*better constraints handling for TNode
*
*Revision 1.3  2004/01/05 16:11:44  perki
**** empty log message ***
*
*Revision 1.2  2003/12/17 17:57:13  perki
**** empty log message ***
*
*Revision 1.1  2003/12/16 12:52:50  perki
*Type and constraints + improvements on naming
*
*/