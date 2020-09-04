/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 25 f�vr. 2004
 * $Id: TreeIconManager.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.uicomponents;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.datamodel.BCTree;
import com.simpledata.bc.uitools.ImageTools;

/**
 * Manager for tree icons
 * Tells which icon should be displayed for each BCTree type
 */
public class TreeIconManager {
    
    private static final Logger m_log = Logger.getLogger( TreeIconManager.class );
	
	
	//public final static String BCNODE_BASE = "base";
	//public final static String BCNODE_LOCALISATION = "loc";
	//public final static String BCNODE_USER = "user";
	
	//public final static String CNODE_SHADOW = "shadow";
	public final static String CNODE_EXTERNAL_REF = "ext_ref";
	public final static String CNODE_TARIF = "tarif";

	private static ImageIcon stdLeaf;
	private static ImageIcon stdNotLeaf;
	private static ImageIcon stdNotLeafExpanded;
	
	private static ArrayList knownTreeTypes;
	
	private static HashMap stdExpandedIcons;
	private static HashMap stdNotExpandedIcons;
	private static HashMap stdLeafIcons;
	
	private static boolean oldStyle = true;
	
	private static HashMap treeTypesColors;
	static {
		/////// STATIC INIT BLOCK
		knownTreeTypes = new ArrayList();
		knownTreeTypes.add(BCTree.TYPE_BASE);
		knownTreeTypes.add(BCTree.TYPE_FUNDS);
		knownTreeTypes.add(BCTree.TYPE_INDUSTRY);
		knownTreeTypes.add(BCTree.TYPE_LOCALISATION);
		knownTreeTypes.add(BCTree.TYPE_MARKET);
		knownTreeTypes.add(BCTree.TYPE_OBLIGATIONS);
		knownTreeTypes.add(BCTree.TYPE_USERS);
		knownTreeTypes.add(BCTree.TYPE_INDEXES);
		
		stdExpandedIcons = new HashMap();
		stdNotExpandedIcons = new HashMap();
		stdLeafIcons = new HashMap();
		
		
		stdLeaf = Resources.stdTarificationLeafIcon;
		stdNotLeaf = Resources.stdTarificationNotLeafIcon;
		stdNotLeafExpanded = Resources.stdTarificationNotLeafIcon;
		
		// initializing colors
//		Color[] colors = new Color[7];
//		colors[0] = new Color(1f,0f,0f,0.2f);       // red
//		colors[1] = new Color(0f,1f,0f,0.2f);       // green
//		colors[2] = new Color(0f,0f,1f,0.2f);       // blue
//		colors[3] = new Color(1f,1f,0f,0.2f);       // yellow
//		colors[4] = new Color(1.0f,0.5f,0.3f,0.2f); // orange
//		colors[5] = new Color(0.3f,0.3f,0.3f,0.2f); // gray
//		colors[6] = new Color(1.0f,1.0f,1.0f,0.2f); // white
			
		Color[] colors = new Color[8];
		colors[0] = new Color(1f,0f,0f,0.3f);       // red
		colors[1] = new Color(0.2f,1f,0f,0.3f);     // green
		colors[2] = new Color(0f,0f,1f,0.3f);       // blue
		colors[3] = new Color(1f,1f,0f,0.3f);       // yellow
		colors[4] = new Color(1.0f,0.0f,1f,0.3f);   // orange
		colors[5] = new Color(0.5f,0.5f,0.5f,0.3f); // gray
		colors[6] = new Color(1.0f,0.5f,0.5f,0.3f); // pink
		colors[7] = new Color(0.0f,1.0f,1.0f,0.3f); // flashy blue
		
		treeTypesColors = new HashMap();
		
		treeTypesColors.put(BCTree.TYPE_BASE, colors[0]);
		treeTypesColors.put(BCTree.TYPE_LOCALISATION , colors[1]);
		treeTypesColors.put(BCTree.TYPE_USERS , colors[2]);
		treeTypesColors.put(BCTree.TYPE_FUNDS , colors[3]);
		treeTypesColors.put(BCTree.TYPE_INDUSTRY , colors[5]);
		treeTypesColors.put(BCTree.TYPE_MARKET , colors[4]);
		treeTypesColors.put(BCTree.TYPE_OBLIGATIONS, colors[7]);
		treeTypesColors.put(BCTree.TYPE_INDEXES, colors[6]);
		
		Iterator iter = treeTypesColors.keySet().iterator();
		while (iter.hasNext()) {
			String typ = (String)iter.next();
			//Color col = (Color)treeTypesColors.get(typ);

			ImageIcon exp = colorizeIconFromType(stdNotLeafExpanded,typ);
			ImageIcon notExp = colorizeIconFromType(stdNotLeaf,typ);
			ImageIcon leaf = colorizeIconFromType(stdLeaf,typ);
			stdExpandedIcons.put(typ, exp);
			stdNotExpandedIcons.put(typ, notExp);
			stdLeafIcons.put(typ, leaf);
		}
	}
	
	/**
	 * Constructor
	 */
	private TreeIconManager() {
	}
	
	/** colorize an icon with the color corresponding to a Tree **/
	public static ImageIcon colorizeIconFromType(ImageIcon source,BCNode node){
		return colorizeIconFromType(source,node.getTree().getType());
	}
	
	/** colorize an icon with the color corresponding to a Tree **/
	public static ImageIcon colorizeIconFromType(ImageIcon source,String type){
		Color col = (Color)treeTypesColors.get(type);
		if (col == null) return source;
		return ImageTools.getIconSquareChar(
					source, 
					col,12, new Color(0f,0f,0f,0.0f),' ');
		
	}
	
	public static ImageIcon 
			getBCNodeIcon(BCNode bcn, boolean isLeaf, boolean isExpanded) {
		if (bcn != null) {
			// add a User Tag if this is A Node created by a user
			ImageIcon i = getIcon(bcn.getTree().getType(), isLeaf, isExpanded);
			if (! bcn.isRoot() && bcn.isUserInstance()) {
				i = ImageTools.
				drawIconOnIcon(i,Resources.stdTagUser,new Point(0,0));
			}
			
			return i;
		} 
		//m_log.error("Asked for BCNode Icon form null BCNode");
		return getSTDIcon(isLeaf, isExpanded);
	}
	
	
	/**
	 * Returns the correct icon corresponding to the context
	 * determined by the user node itself (ie leaf or not etc...)
	 * @param typ : typ of node we which to represent<br>
	 * @param isLeaf
	 * @param isExpanded
	 * @return desired icon
	 */
	public static ImageIcon getIcon(String typ, 
			boolean isLeaf, 
			boolean isExpanded) {
		ImageIcon icon = null;

		// TYPES MANAGEMENT
		if (typ.equals(BCTree.TYPE_BASE)) {
			icon = Resources.stdTreeBaseIcon;
		}
		if (typ.equals(BCTree.TYPE_LOCALISATION)) {
			icon = Resources.stdTreeLocIcon;
		} 
		if (typ.equals(BCTree.TYPE_USERS)) {
			icon = Resources.stdTreeUserIcon;
		}
		
		if (typ.equals(CNODE_TARIF)) {
			icon = Resources.stdTarifIcon;
		}
		
		// Applying old style if desired
		if (oldStyle) {
			if (knownTreeTypes.contains(typ)) {
				icon = getSTDIcon(typ, isLeaf, isExpanded);
			}
		}

		// NULL MANAGEMENT
		if (icon == null) {
			m_log.warn("Icon required for unknown type : '"+typ+"'");
			icon = getSTDIcon(isLeaf, isExpanded);
		}
		return icon;
	}

	/**
	 * Internal method used to get standard icons for tree types
	 * <br>ie standard icons modified by a color
	 * @param typ
	 * @param isLeaf
	 * @param isExpanded
	 * @return
	 */
	private static ImageIcon getSTDIcon(String typ, 
			boolean isLeaf, 
			boolean isExpanded) {
		ImageIcon icon = null;
		if (!isLeaf) {
			if (isExpanded) {
				icon = (ImageIcon)stdExpandedIcons.get(typ);
			} else {
				icon = (ImageIcon)stdNotExpandedIcons.get(typ);
			}
		} else {
			icon = (ImageIcon)stdLeafIcons.get(typ);
		}
		
		if (icon == null) {
			// Management in case of null icon
			m_log.warn("No standard icon found for tree type : "+typ);
			icon = getSTDIcon(isLeaf, isExpanded);
		}
		
		return icon;
	}
	
	/**
	 * Returns standard icon not color modified<br>
	 * Use it when icon type is unknown
	 * @param isLeaf
	 * @param isExpanded
	 * @return
	 */
	public static ImageIcon getSTDIcon(boolean isLeaf, boolean isExpanded) {
		if (isLeaf) {
			return stdLeaf;
		}
		if (isExpanded) {
			return stdNotLeafExpanded;
		} 
		return stdNotLeaf;	
	}
	
	/**
	 * Apply tag more to the icon
	 * @param ico icon to modify
	 * @return 
	 */
	public static ImageIcon applyTagMore(ImageIcon ico) {
		return ImageTools.drawIconOnIcon(
				ico,Resources.stdTagMore,new Point(7,0));
	}

	public static ImageIcon applyTagInterest(ImageIcon ico) {
	    return ImageTools.drawIconOnIcon(ico, Resources.stdTagInterest, new Point(7,0));
	}
	
	/**
	 * get a icon showing a Tarif contained in a BCNode
	 */
	public static ImageIcon getIconBCNodeContainingTarif(BCNode node) {
	    ImageIcon icon = node == null? getIcon(CNODE_TARIF,true,false) :
	        colorizeIconFromType(getIcon(CNODE_TARIF,true,false), node);
	    icon = applyTagInterest(icon);
		return icon;
	}
	
}
/*
 * $Log: TreeIconManager.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:37  perki
 * First commit on sourceforge
 *
 * Revision 1.18  2004/10/14 16:39:08  perki
 * *** empty log message ***
 *
 * Revision 1.17  2004/09/29 12:40:19  perki
 * Localization tarifs
 *
 * Revision 1.16  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.15  2004/09/13 15:27:32  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/09/10 16:51:05  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/09/08 16:35:14  perki
 * New Calculus System
 *
 * Revision 1.12  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 * Revision 1.11  2004/07/22 15:12:34  carlito
 * lots of cleaning
 *
 * Revision 1.10  2004/07/08 15:49:22  perki
 * User node visibles on trees
 *
 * Revision 1.9  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.8  2004/07/08 09:43:20  perki
 * *** empty log message ***
 *
 * Revision 1.7  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.5  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.4  2004/03/13 17:44:47  perki
 * Ah ah ah aha ah ah aAAAAAAAAAAAAAA
 *
 * Revision 1.3  2004/03/02 00:32:54  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/02/26 14:36:12  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/02/25 19:01:33  carlito
 * *** empty log message ***
 *
 */