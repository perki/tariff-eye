/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: TarificationHeader.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
package com.simpledata.bc.datamodel;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.simpledata.bc.tools.Lang;
import com.simpledata.uitools.ImageUtils;


/**
 * This is the header object used to describe the Tarification files
 */
public class TarificationHeader implements Serializable {
	private static final Logger m_log 
			=Logger.getLogger(TarificationHeader.class ); 
	
	/** tag original tarification<BR>
	 * You can use tagsForTypeXXXXX for easer use
	 */
	public static final String TYPE_TARIFICATION_ORIGINAL 
				= "Tarification Original";
	/** tag modified tarification<BR>
	 * You can use tagsForTypeXXXXX for easer use
	 */
	public static final String TYPE_TARIFICATION_MODIFIED 
				= "Tarification Modified";
	/** tag portfolio<BR>
	 * You can use tagsForTypeXXXXX for easer use
	 */
	public static final String TYPE_PORTFOLIO
				= "Portfolio";
	
	
	//	 ------ tag utilities -----------//
	/** get the tags that represent any type of content (in fact null) **/
	public static final String[] tagsForTypeAny = null;
	
	/** get the tags that represent any type of tarifications **/
	public static final String[] tagsForTypeAnyTarifications = 
		new String[] {TYPE_TARIFICATION_MODIFIED , TYPE_TARIFICATION_ORIGINAL};
	
	/** get the tags that represent any type of portfolio **/
	public static final String[] tagsForTypeAnyPortfolio =
		new String[] {TYPE_PORTFOLIO};
	
	
	// ---------- vars ------------//
	
	
	private String dataType; // the type of this tarification
	
	private TString xTitle; // the title of this tarification
	private TString xDescription; // the title of this tarification
	
	/** name of the licensed customer **/
	private String licensedCompanyName; 
	
	/** nlicense id of the customer **/
	private String idLicense; 
	
	/** creation date **/
	private Date publishingDate; 
	
	/** last modification date **/
	private Date modificationDate; 
	
	private transient ImageIcon zIcon; // the icon of this tarification
	
	private String zIconData; // the bytes containingthe icon data 
	
	/** extra properties the may be saved in the header **/
	private HashMap extraProperties; 
	
	/**
	 * the location it comes from (modified at load)
	 */
	private transient File myLoadingLocation;  
	
	/**
	 * used by TarificationFileView to determine if this file can
	 * be loaded (means decrypted)
	 */
	public transient boolean canBeLoaded = true;  
	
	//-------- static fields ------------//
	
	private static TarificationHeader dummy; // a dummy viewer
	
	
	
	/** construct a Tarification header for this Tarification **/
	
	/** construct a Tarification header for a file that is not a Tarification */
	public static TarificationHeader getDummy() {
		if (dummy == null) {
			dummy = new TarificationHeader();
			dummy.xTitle = new TString("en","Not a valid file");
			dummy.zIcon = null;
			dummy.xDescription = new TString("en","");
			dummy.licensedCompanyName = "";
			dummy.dataType = "Unkown type";
			dummy.publishingDate = new Date();
			dummy.modificationDate = new Date();
			dummy.extraProperties = new HashMap();
		}
		return dummy;
	}
	
	/**
	 * return the title of this Tarification
	 */
	public String getTitle() {
		if(xTitle == null) return "No title set";
		return xTitle.toString();
	}
	
	/**
	 * return the description of this Tarification
	 */
	public String getDescription() {
		return xDescription.toString();
	}
	
	/**
	 * Note: also used by XML
	 * @return Returns the type of this tarification : one of TYPE_*.
	 */
	public String getDataType() {
		if (dataType == null) {
			//m_log.warn( "TYPE WAS NULL :"+getTitle() );
			dataType = TYPE_TARIFICATION_ORIGINAL;
		}
		return dataType;
	}
	
	/**
	 * Note: also used by XML
	 * @param type define the Type of this tarification : one of TYPE_*
	 */
	public void setDataType(String type) {
		assert type != null;
		this.dataType = type;
	}
	
	/**
	 * @return Returns the icon.
	 */
	public ImageIcon getIcon() {
		if (zIcon == null && zIconData != null) {
			zIcon = new ImageIcon(ImageUtils.base64toImage(zIconData));
		}
		return zIcon;
	}
	
	/**
	 * @param i The icon to set.
	 */
	public void changeIcon(ImageIcon i) {
		zIcon = i;
	
		// save this into a byte array for saving purposes
		if (zIcon == null) {
			zIconData = null;
			return;
		}
		
		zIconData = ImageUtils.imageToBase64(
		        ImageUtils.imageToBufferedImage(i.getImage()));
	}
	
	
	
	/** Note: also used by XML*/
	public Date getModificationDate() {
		return modificationDate;
	}
	/**
	 * Note: also used by XML
	 * @param modificationDate when it was saved
	 */
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	/** Note: also used by XML*/
	public Date getPublishingDate() {
		return publishingDate;
	}
	/**
	 * Note: also used by XML
	 * @param publishingDate when it was published (created)
	 */
	public void setPublishingDate(Date publishingDate) {
		this.publishingDate = publishingDate;
	}
	
	/**
	 * Note: also used by XML
	 * @return Returns the idLicense.
	 */
	public String getIdLicense() {
		return idLicense;
	}
	/**
	 * Note: also used by XML
	 * @param idLicense The idLicense to set.
	 */
	public void setIdLicense(String idLicense) {
		this.idLicense = idLicense;
	}
	
	/** Note: also use by XML */
	public String getLicensedCompanyName() {
		return licensedCompanyName;
	}
	/** Note: also use by  XML */
	public void setLicensedCompanyName(String customer) {
		licensedCompanyName = customer;
	}
	
	/**
	 * WARNING!! can return null
	 * @return Returns the location I was loaded from.
	 */
	public File myLoadingLocation() {
		return myLoadingLocation;
	}
	
	/**
	 * @param loadingLocation location I was loaded from.
	 */
	public void setLoadingLocation(File loadingLocation) {
		this.myLoadingLocation = loadingLocation;
	}
	
	/** properties tag for the extra prop. hashmap.. **/
	private final static String PROP_OPENABLE_IN_DEMO = "OPEN IN DEMO";
	/**
	 * return true if this Tarification can ben opened in the Demo version
	 */
	public boolean isOpenableInDemo() {
	    Object o = getXExtraProperties().get(PROP_OPENABLE_IN_DEMO);
	    if (o != null && o instanceof Boolean) {
	        return ((Boolean) o).booleanValue();
	    }
	    return false;
	}
	
	/**
	 * set if this Tarification can be opened in demo mode
	 */
	public void isOpenableInDemo(boolean b) {
	   getXExtraProperties().put(PROP_OPENABLE_IN_DEMO,new Boolean(b));
	}
	
	// ------------- XML -----------//
	
	/** XML */
	public TarificationHeader() {}
	
	/** XML */
	public HashMap getXExtraProperties() {
	    if (extraProperties == null) extraProperties = new HashMap();
	    return extraProperties;
	}
	
	/** XML */
	public void setXExtraProperties(HashMap h) {
	    extraProperties = h;
	}
	
	/** XML */
	public TString getXTitle() {
		return xTitle;
	}
	
	/** XML */
	public void setXTitle(TString title) {
		this.xTitle = title;
	}
	
	/**
	 *XML
	 */
	public TString getXDescription() {
		return xDescription;
	}
	/**
	 * XML
	 */
	public void setXDescription(TString description) {
		xDescription = description;
	}
	
	/**
	 * XML
	 */
	public void setBase64IconData(String iconData) {
		zIconData = iconData;
	}
	
	/**
	 * XML<BR>
	 * Also used to get image to publish on the web site
	 */
	public String getBase64IconData() {
		return zIconData ;
	}
	
	/**
	 * XML
	 */
	public void setZIconData(int[] iconData) {
	    m_log.warn("VERSIONNING ICON");
		changeIcon(new ImageIcon(ImageUtils.intsToImage(iconData)));
	}
	
}
/**
 * $Log: TarificationHeader.java,v $
 * Revision 1.2  2007/04/02 17:04:23  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:36  perki
 * First commit on sourceforge
 *
 * Revision 1.15  2004/11/19 06:46:37  perki
 * better image handeling
 *
 * Revision 1.14  2004/10/19 06:31:28  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/10/18 16:48:03  perki
 * *** empty log message ***
 *
 * Revision 1.12  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.11  2004/10/04 08:33:08  perki
 * Added Demo prop
 *
 * Revision 1.10  2004/09/16 17:26:37  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/09/03 12:22:28  kaspar
 * ! Log.out -> log4j second part
 *
 * Revision 1.7  2004/07/26 17:39:36  perki
 * Filler is now home
 *
 * Revision 1.6  2004/07/19 17:39:08  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/06/21 17:26:14  perki
 * added compact tree node
 *
 * Revision 1.4  2004/06/21 14:45:06  perki
 * Now BCTrees are stored into a vector
 *
 * Revision 1.3  2004/06/20 16:09:03  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/06/18 18:25:39  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/06/16 07:49:28  perki
 * *** empty log message ***
 *
 */
