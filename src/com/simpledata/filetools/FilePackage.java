/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Log: FilePackage.java,v $
* Revision 1.2  2007/04/02 17:04:25  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.6  2004/01/29 16:34:51  perki
* Sevral cleaning
*
* Revision 1.5  2004/01/29 13:03:04  carlito
* warnings and imports corrected ...
*
* Revision 1.4  2003/10/10 11:26:30  perki
* test
*
* $Id: FilePackage.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/
package com.simpledata.filetools;

import java.io.File;
import java.util.*;
/**
* Used to create a Package
* @see FilePackager form examples;
*/
public class FilePackage extends Hashtable {
	private String title = "";
	private Vector fileList = null;
	private File root = null;
	
	/**
	* set the Title of this FilePackage
	*/
	public void setTitle(String t) {
		title = t;
	}
	/**
	* get the Title of this FilePackage
	*/
	public String getTitle() {
		return title;
	}
	
	
	/**
	* clean fileList (erase all references to files) 
	*/
	public void clean() {
		fileList.clear();
	}
	
	/**
	* display infos about this FilePackage
	*/
	public String toString() {
		String res = "";
		res += "Title:"+title+"\n";
		Enumeration en = keys();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			res += key+":"+get(key)+"\n";
		}
		res += "**** "+fileList.size()+" files ******\n";
		en = fileList.elements();
		while (en.hasMoreElements()) {
			res += ""+en.nextElement()+"\n";
		}
		return res;
	}
	
	
	
	/**
	* return the name this file will have in package
	*/
	public String getName(File f) {
		// return null if not in fileList
		if (! fileList.contains(f)) return null;
		
		String filePath = f.getAbsolutePath();
		String rootPath = root.getAbsolutePath();	
		if (! filePath.startsWith(rootPath)) {
			
			// rootPath = one of the System root
			File[] roots = File.listRoots();
			for (int i = 0; i < roots.length; i++) {
				rootPath = roots[i].getAbsolutePath();
				if (! filePath.startsWith(rootPath)) {
					break;
				}
				rootPath = "";
			}
		}
		String name = filePath.substring(rootPath.length());
		return name;
	}
	
	/**
	* Create a FilePackage with the specified root Directory.<BR>
	* could be created with new FilePackage(new File("")) to use user directory
	*/
	public FilePackage(File root) {
		super();
		fileList = new Vector();
		this.root = root;
	}
	
	/**
	* return the root directory of this files
	*/
	public File getRoot() {
		return root;
	}
	
	/**
	* return the list of files in this Package
	*/
	public File[] getFileList () {
		File[] fl = new File[fileList.size()];
		Enumeration en = fileList.elements();
		for (int i= 0; en.hasMoreElements(); i++) {
			fl[i] = (File) en.nextElement();
		}
		return fl;
	}
	
	/**
	* insert a File (no directory)
	*/
	public void insert(File f) {
		if ((! fileList.contains(f)) && (! f.isDirectory())) {
			fileList.add(f);
		}
	}
	
	/**
	* Insert a File, or a Directory and all it's contents into this package
	*/
	public void insertRecursive(File f) {
		File[] fl = new File[1];
		fl[0] = f;
		insertRecursive(fl);
	}
	/**
	* Insert Files, or Directories and all their contents into this package
	*/
	public void insertRecursive(File[] fl) {
		for (int j = 0; j < fl.length ; j++) {
			if (! fileList.contains(fl[j])) {
				if (fl[j].isDirectory()) {
					File[] fld = fl[j].listFiles();
					for (int i = 0; i < fld.length ; i++) {
						insertRecursive(fld[i]);
					}
					return ;
				} 
				insert(fl[j]);	
			}
		}
	}
	
	
	/**
	* remove a File (no directory)
	*/
	public void remove(File f) {
		if (fileList.contains(f)) {
			fileList.remove(f);
		}
	}
	
	/**
	* Remove a File, or a all contents of a Directory from this package
	*/
	public void removeRecursive(File f) {
		File[] fl = new File[1];
		fl[0] = f;
		removeRecursive(fl);
	}
	
	/**
	* Remove Files, or Directories and all their contents from this package
	*/
	public void removeRecursive(File[] fl) {
		for (int j = 0; j < fl.length ; j++) {
			if (fileList.contains(fl[j])) {
				if (fl[j].isDirectory()) {
					File[] fld = fl[j].listFiles();
					for (int i = 0; i < fld.length ; i++) {
						removeRecursive(fld[i]);
					}
					return ;
				} 
				remove(fl[j]);	
			}
		}
	}
	
}
