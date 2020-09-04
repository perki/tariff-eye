/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ImageFileChooser.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools;

import java.awt.*;
import java.io.File;

import javax.swing.*;

import com.simpledata.uitools.ImageUtils;

/**
 * A FileChooser for images, with preview and icons
 */
public class ImageFileChooser {

	/** 
	 * default icon dimensions for the icon on the filesystem 
	 * maybe directly changed 
	 */
	public static Dimension defaultIconDimension = new Dimension(32,32);

	public static void main (String args[]) {
		JFrame jf = new JFrame();
		jf.show();
		chooseImage(null,jf);
		jf.dispose();
	}
	
	/** list of images extensions: "jpeg","jpg","gif","tif","tiff","png" */
	public final static String[] imagesExtensions 
		= new String[] { "jpeg","jpg","gif","tif","tiff","png"};
	
	/**
	 * Open a File chooser that will return a File denoting an image
	 * @return the choosen file or null if none has been choosen
	 */
	public static File chooseFile(File initialDirectory,Frame parent) {
		SimpleFileBrowser fc = new SimpleFileBrowser(initialDirectory);
		fc.addFileView(new ImageFileView(),0);
		
		int returnVal = fc.showOpenDialog(parent);
		
		File result = null;
		//Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			result = fc.getSelectedFile();
		} 
		return result;
	}
	
	/**
	 * Open a File chooser that will return an Image
	 * @return the choosen image or null if none has been choosen
	 */
	public static Image chooseImage(File initialDirectory,Frame parent) {
		File f = chooseFile(initialDirectory,parent);
		if (f == null) return null;
		return ImageUtils.getImage(f.toString());
	}
	
	
	
}

class ImageFileView implements SimpleFileView {
	JPanel jp;
	JLabel jl;
	public ImageFileView () {
		jp = new JPanel(new FlowLayout(0,0,FlowLayout.CENTER));
		jl = new JLabel();
		jp.add(jl);
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getIcon(java.io.File)
	 */
	public ImageIcon getIcon(File f) {
		return getImage(f,
				ImageFileChooser.defaultIconDimension.width,
				ImageFileChooser.defaultIconDimension.height);
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getExtensions()
	 */
	public String[] getExtensions() {
		return ImageFileChooser.imagesExtensions;
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getDescription()
	 */
	public String getDescription() {
		return "Images";
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getTypeDescription(java.io.File)
	 */
	public String getTypeDescription(File f) {
		return "Image";
	}

	/**
	 * @see com.simpledata.filetools.SimpleFileView#getPanel(java.io.File)
	 */
	public JPanel getPanel(File f) {
		jl.setIcon(getImage(f,SimpleFileBrowser.INFO_PANEL_WIDTH-5,
						SimpleFileBrowser.INFO_PANEL_HEIGHT-5));
		return jp;
	}
	
	
	
	public static ImageIcon getImage(File f,int x,int y) {
		Image img = null;
		
			img = ImageUtils.getImage(f.toString());
		
		if (img == null) return null;
		
		
		return new ImageIcon(ImageUtils.fitInBox(img,x,y,false));

	}
	
}

/*
 * $Log: ImageFileChooser.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.5  2004/09/23 10:53:20  jvaucher
 * Some modifications on the SimpleBrowser rendering
 *
 * Revision 1.4  2004/07/04 10:57:07  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/06/28 13:22:44  perki
 * icons are 16x16 for macs
 *
 * Revision 1.2  2004/06/20 16:09:39  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/06/18 18:27:06  perki
 * *** empty log message ***
 *
 */