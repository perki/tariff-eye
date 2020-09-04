/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarificationFileView.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.datatools;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets; 
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.SoftInfos;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.ImageTools;
import com.simpledata.bc.uitools.Splash;
import com.simpledata.filetools.FileUtilities;
import com.simpledata.filetools.Secu;
import com.simpledata.filetools.SimpleException;
import com.simpledata.filetools.SimpleFileBrowser;
import com.simpledata.filetools.SimpleFileView;
import com.simpledata.filetools.encoders.SelfD;
import com.simpledata.uitools.ImageUtils;

/**
 * The FileFilter for tarifications
 *
 */
public class TarificationFileView implements SimpleFileView.Extended {
	private static final Logger m_log = Logger.getLogger( TarificationFileView.class ); 

	private int type= -1;
	
	/** caching object with the header of all files presented **/
	private HashMap cache;
	
	/** the type of content accepted by this filter**/
	private String[] typeOfContents;
	
	private String title = null;
		
	/**
	 * 
	 * @param type one of FileManagement.TYPE_*
	 * @param contentType some of TarificationHeader.TYPE_* (or null for any)
	 */
	public TarificationFileView(int type,String[] contentType) {
		this.type= type;
		typeOfContents = contentType;
		cache = new HashMap();
	}
	
	/** 
	 * get the head object of this File which should 
	 * represent a Tarification 
	 **/
	public TarificationHeader getHeader(File f) {
		if (f == null || ! f.exists()) return TarificationHeader.getDummy();
		
		if (cache.containsKey(f.toString())) 
			return (TarificationHeader) cache.get(f.toString());
		
		TarificationHeader th = TarificationHeader.getDummy();
		
		class bob implements ActionListener {
		    boolean canBeLoaded = true;
		    public void actionPerformed(ActionEvent e) {
                m_log.warn("Got error:"+e);
                canBeLoaded = false;
            }
		}
		
		bob al = new bob();
		
		try {

	        SelfD.DecodeFlow sd = Secu.getDefaultDF("SooSIMPL",al);
			Object o = Secu.getHeader(f,sd);
			
			if (o != null && o instanceof TarificationHeader) {
				th = (TarificationHeader) o;
				th.canBeLoaded = al.canBeLoaded;
			}
		} catch (SimpleException se) {
			m_log.error( "Not a Tarification file: "+f +" "+se);
		} catch (Exception e) {
			m_log.error( "Not a Tarification file: "+f+" "+e);
		}
		cache.put(f.toString(), th);
		return th;
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getIcon(java.io.File)
	 */
	public ImageIcon getIcon(File f) {
	    TarificationHeader th = getHeader(f);
		ImageIcon result = th.getIcon();
		if (result == null) return null;
		ImageIcon ii = new ImageIcon(ImageUtils.fitInBox(result.getImage(),
				FileManagement.defaultIconDimension.width,
				FileManagement.defaultIconDimension.height,false));
		
		if (! th.canBeLoaded) {
		    ii = ImageTools.drawIconOnIcon(
		            ii,Resources.iconLockClosed,new Point(0,0));
		}
		
		return ii;
	}
	
	/**
	 * check if this file can be decrypted
	 */
	public boolean canDecrypt(File file) {
	    TarificationHeader th = getHeader(file);
	    return th.canBeLoaded;
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getExtensions()
	 */
	public String[] getExtensions() {
		if (type < 0) {
			return FileManagement.tarificationFileExt;
		}
		return new String[] { FileManagement.tarificationFileExt[type] };
	}
	
	
	
	
	/**
	 * @return true if this FileView accept this file 
	 * (or if file is a directory)
	 */
	public boolean accept(File f) {

		if (f.isDirectory()) {
			return true;
		}
	    
	    return getAcceptAndHeader(f,true) != null;
	}
	
    /** 
     * return the type of this file or null if not accepted 
     * @return one of TarificationHeader
     * **/
	public TarificationHeader getAcceptAndHeader(File f,boolean checkExtension){
	    
	    if (checkExtension) {
		    String extension = FileUtilities.getExtension(f);
		    if (!SimpleFileBrowser.extensionMatches(this,extension)) {
		        return null;
		    }
	    }
	    
	    // look for accepted contents
	    
	    TarificationHeader th = getHeader(f);
	    if (th == null) return null;
	    String contentType = th.getDataType();
	    if (typeOfContents != null) {
	        for (int i = 0; i < typeOfContents.length ; i++) {
	            if (contentType.equals(typeOfContents[i])) {
	                // If in demo mode only display DEMO Tarifs
	                if (SoftInfos.isLocked())
	                    return th.isOpenableInDemo() ? th : null;
	                return th;
	            }
	        } 
	        return null;
	    }
	    
	    return th;
	}
	
	/**
	 * Sets the name that will appear in the filter combo of the file chooser
	 * i.e. "All files" or "Portofolio"
	 * @param s
	 */
	public void setFilterTitle(String s) {
	    this.title = s;
	}
	
	/**
	 * return the used type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getDescription()
	 */
	public String getDescription() {
	    if (this.title != null) {
	        return this.title;
	    }
		if (type < 0)
			return Lang.translate("Tarification");
		return Lang.translate(FileManagement.tarificationTitle[type]);
	}

	
	
	/**
	 * @see com.simpledata.filetools.SimpleFileView#getTypeDescription(java.io.File)
	 */
	public String getTypeDescription(File f) {
		return null;
	}
	
	
	/**
	 * Convert a String to HTML<BR>
	 * add &lt;HTML> and &lt;BR> tags
	 */
	private static String toHTML(String s) {
		return "<HTML>"+s.replaceAll("\n","<BR>")+"</HTML>";
	}
	
	/**
	 * Add a new titled JLabel
	 */
	private static JLabel produceJLabel(String ntitle,String contents) {
		JLabel jl = new JLabel(toHTML(contents));
		jl.setBorder(new TitledBorder(ntitle));
		return jl;
	}
	
	
	/**
	 * Get a Panel with all the information contained in a Tarificationheader
	 */
	public static JPanel getTarificationHeaderPanel(TarificationHeader th) {
	    JPanel jp = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		jp.setLayout(gbl);
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
	
		// Icon
		JLabel icon = new JLabel(th.getIcon(),JLabel.LEFT);
		icon.setSize(32,32);
		jp.add(icon,c);
		
		// DataType
		JLabel dataType = new JLabel(toHTML(Lang.translate(th.getDataType())));
		dataType.setHorizontalAlignment(JLabel.RIGHT);
		c.gridx = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		jp.add(dataType, c);
		
		// Title
		JLabel titleLabel =produceJLabel(Lang.translate("Title"),th.getTitle());
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 0;
		c.gridy = 1;
		jp.add(titleLabel, c);

		// Description
		JTextArea descriptionField = new JTextArea();
		descriptionField.setText(th.getDescription());
		descriptionField.setLineWrap(true);
		descriptionField.setWrapStyleWord(true);
		descriptionField.setEditable(false);
		descriptionField.setBorder(
				new TitledBorder(Lang.translate("Description")));
		c.gridy = 2;
		jp.add(descriptionField,c);
		
		// Publication date
		JLabel publicationDateLabel 
			= produceJLabel(Lang.translate("Publication date"),
				th.getPublishingDate().toString());
		c.gridy = 3;
		jp.add(publicationDateLabel, c);
		
		// Last Modified
		JLabel lastModLabel 
			= produceJLabel(Lang.translate("Last modification date"),
				th.getModificationDate().toString());
		c.gridy = 4;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		jp.add(lastModLabel, c);

		return jp;
	}
	
	/**
	 * @see com.simpledata.filetools.SimpleFileView#getPanel(java.io.File)
	 */
	public JPanel getPanel(File f) {
	    return getTarificationHeaderPanel(getHeader(f));
	}
	
		

}
