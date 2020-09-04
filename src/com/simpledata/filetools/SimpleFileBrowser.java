/*
* $Id: SimpleFileBrowser.java,v 1.1 2006/12/03 12:48:38 perki Exp $
*/
package com.simpledata.filetools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;

/**
* FileChooser That display an Info panel 
* <PRE>
* EXAMPLE
* import com.simpledata.filetools.*;
* import java.io.*;
* import javax.swing.*;
*
* SimpleFileBrowser fc = new SimpleFileBrowser();
* fc.addDefaultFileView(null,"*.*",null,0); // Default File Filter Any File
* String[] filter = {"titi","toto"};
* fc.addDefaultFileView(o,"TITI AND TOTO Files",null,1);
* SimpleFileView test = new Test() // Test is a class that implements SimpleFileView
* fc.addFileView(test,2);
* int returnVal = fc.showOpenDialog(frame);
*  // use fc.showSaveDialog(frame); for a Save Dialog
* //Process the results.
* if (returnVal == JFileChooser.APPROVE_OPTION) {
* 		File file = fc.getSelectedFile();
*		System.out.println("Selected file: " + file.getName());
*	} else {
*		System.out.println("Cancel "); 
* }
* </PRE>
*/
public class SimpleFileBrowser extends JFileChooser {
	private static final Logger m_log = Logger.getLogger( SimpleFileBrowser.class );
	
	private Vector svfs = null; // contains all SimpleFileView
	private Vector levels = new Vector(); // contains positions
	
	/** dimension of the info Panel **/
	public static final int INFO_PANEL_WIDTH = 250;
	public static final int INFO_PANEL_HEIGHT = 210;
	public static final Dimension infoPanelDimension = 
		new Dimension(INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT);
	
	/**
	* DEFAULT ICON FOR RECOGNIZED FILE TYPE /resources/images/appIcon1616.png
	*/
	public static final ImageIcon DEFAULT_ICON 
	= Resources.getIcon("images","appIcon1616.png");
	
	
	/**
	* Constructs a JFileChooser pointing to the user's default directory.
	*/
	public SimpleFileBrowser() { super(); init(); }
	/**
	* Constructs a JFileChooser using the given path.
	*/
	public SimpleFileBrowser(File currentDirectory) { 
		super(currentDirectory); 
		init(); 
		if (super.getCurrentDirectory() != null) {
			if (super.getCurrentDirectory().isFile()) {
				setSelectedFile(super.getCurrentDirectory());
			}
		}
	}
	
	/**
	* Constructs a JFileChooser using the given path.
	*/
	public SimpleFileBrowser(String currentDirectoryPath) { 
		this (new File( currentDirectoryPath));
	}
	
	/* init */
	private void init() {
		svfs = new Vector();
		setAcceptAllFileFilterUsed(false);
		
		//Add the preview pane.
		setAccessory(new FileInfoPanel(this));
	}
	
	
	
	
	/**
	* Utility return the SimpleFileView that matches this File
	*/
	SimpleFileView getMatchingSFV(File f) {
		return getMatchingSFV(FileUtilities.getExtension(f));
	}
	
	/**
	* Utility return the SimpleFileView that matches this Extension
	*/
	private SimpleFileView getMatchingSFV(String extension) {
		if (svfs == null) {
			return null;
		}
		
		Enumeration en = svfs.elements();
		SimpleFileView sfv = null;
		while (en.hasMoreElements()) {
			sfv = (SimpleFileView) en.nextElement(); 
			if (extensionMatches(sfv,extension)) {
				return sfv;
			}
		}

		return null;
	}
	
	/**
	* Utility return true if this Extension Matches this SimpleFileView
	*/
	public static boolean extensionMatches(SimpleFileView sfv,String extension) {
		String[] exts = sfv.getExtensions();
		for (int i = 0; i < exts.length; i++) {
			if (exts[i] == null) return true;
			if (extension != null) {
				if (exts[i].equals(extension)) return true;
			}
		}
		return false;
	}
			
	
	/**
	* Add a SimpleFileView<BR>
	* Use addFileView() for a better integration.<BR>
	* @param extensions is a list of extensions for this Simple, 
	* can null for any file
	* @param icon is an icon for those extensions, can be null.
	* @param description for this type of file
	* @param level is the weight of this file view. If mutiple filters 
	* matches a File the higher level will be selected.
	*/
	public void addDefaultFileView(String[] extensions,String description, 
			ImageIcon icon, int level) {
		if (extensions == null) {
			extensions = new String[1];
			extensions[0] = null;
		}
		SimpleFileView sfv 
			= new ReallySimpleFileView(extensions,description,icon);
		addFileView(sfv,level);
	}
	
	
	/**
	* Add a SimpleFileView
	* @see SimpleFileView
	* @param level is the weight of this file view. 
	* If mutiple filters matches a File the higher level will be selected.
	*/
	public void addFileView(SimpleFileView sfv, int level) {
		
		int pos = 0;
		if (levels.size() > 0) {
			int z = 0;
			for (pos = 0; pos < levels.size(); pos++) {
				z = ((Integer) levels.get(pos)).intValue();
				if (level >= z) {
					break;
				}
			}
		}
		m_log.info( sfv.getDescription()+" at "+pos );
		svfs.add(pos,sfv);
		levels.add(pos,new Integer(level));
		
		addChoosableFileFilter(new SimpleFilter(sfv));
	}
	
	/**
	* Test code
	*/
	public static void main(String[] args) {
		
		//Create and set up the window.
		JFrame frame = new JFrame("SimpleFileBrowser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);
		
		SimpleFileBrowser fc = new SimpleFileBrowser();
		String[] o = {"toto"};
		String[] i = {"titi","toto"};
		fc.addDefaultFileView(null,"*.*",null,0);
		fc.addDefaultFileView(o,"toto",null,1);
		fc.addDefaultFileView(i,"titi",null,0);
		fc.addFileView(FilePackager.getSimpleFileView(),2);
		int returnVal = fc.showSaveDialog(frame);
		
		//Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			m_log.info( "Attaching file: " + file.getName() );
		} else {
			m_log.debug( "Cancel " ); 
		}
		
		fc.setSelectedFile(null);
	}
	
	/** 
	* Popup a message -- proposition : continue or leave 
	* return 0 - OK ; 1 - CANCEL
	**/
	public static int popupVerify(Frame f, String message) {
		return JOptionPane.showOptionDialog(f, message,null,
		JOptionPane.YES_NO_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		null,
		null);
	}
	
	/** internal method
	* @see SimpleFileView#getTypeDescription(File f)
	*/
	public String getTypeDescription(File f) {
		SimpleFileView sfv = getMatchingSFV(f);
		
		if (sfv != null) {
			return sfv.getTypeDescription(f);
		}
		return super.getTypeDescription(f);
	}
	
	/** internal method
	* @see SimpleFileView#getIcon(File f)
	*/
	public Icon getIcon(File f) {
		Icon icon = null;
		if (f != null) {
			SimpleFileView sfv = getMatchingSFV(f);
			if (sfv != null) {
				icon = sfv.getIcon(f);
			}
		}
		if (icon != null) {
			return icon;
		}
		return super.getIcon(f);
	}
	
	/* SimpleFilter.java  */
	class SimpleFilter extends FileFilter {
		SimpleFileView sfv;
		
		public SimpleFilter (SimpleFileView sfv) {
			this.sfv = sfv;
		}
		
		public boolean accept(File f) {
			if (sfv instanceof SimpleFileView.Extended) 
				return ((SimpleFileView.Extended)sfv).accept(f);
			
			if (f.isDirectory()) {
				return true;
			}
			String extension = FileUtilities.getExtension(f);
			return SimpleFileBrowser.extensionMatches(sfv,extension);
		}
		
		//The description of this filter
		public String getDescription() {
			return sfv.getDescription();
		}
	}
	
	/** return the selected File View **/
	public SimpleFileView getSelectedFileView() {
		SimpleFilter sf = (SimpleFilter) getFileFilter();
		if (sf == null) return null;
		return sf.sfv;
	}
	
	
	
	/* FileInfoPanel.java is  used by SimpleFileBrowser.java. */
	class FileInfoPanel extends JPanel implements PropertyChangeListener {
		private File file = null;
		private SimpleFileBrowser owner = null;
		private JScrollPane jsp = null;
		public FileInfoPanel(SimpleFileBrowser fc) {
			owner = fc;
			owner.addPropertyChangeListener(this);
			init();
		}
	
		private void init() {
			Dimension normal = new Dimension(INFO_PANEL_WIDTH, INFO_PANEL_HEIGHT);
			this.setMinimumSize(normal);
			this.setPreferredSize(normal);
			this.setMaximumSize(normal);
			
			setLayout(new BorderLayout());
			jsp = new JScrollPane();
			jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			JPanel jp = new JPanel();
			jsp.setViewportView(jp);
			
			add(jsp, BorderLayout.CENTER);
		}
		
		public void refresh() {
			
			JPanel jp = null; 
			if (file != null) {
				SimpleFileView sfv = owner.getMatchingSFV(file);
				if (sfv != null) {
					jp = sfv.getPanel(file);
				}
			} else {
				jp = new JPanel();
				jp.add(new JLabel(""));
			}
			
			
			jsp.setViewportView(jp);
			
		}
		
		public void propertyChange(PropertyChangeEvent e) {
			boolean update = false;
			String prop = e.getPropertyName();
			
			//If the directory changed, don't show an image.
			if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
				file = null;
				update = true;
				
				//If a file became selected, find out which one.
			} else 
				if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
				file = (File) e.getNewValue();
				update = true;
			}
			
			//Update the preview accordingly.
			if (update) {
				if (isShowing()) {
					refresh();
				}
			}
		}
		
	}
}

class ReallySimpleFileView implements SimpleFileView {
	private String exts[];
	private String desc;
	private ImageIcon icon;
	
	public ReallySimpleFileView
	(String[] extensions,String description, ImageIcon icon) {
		this.exts = extensions;
		this.desc = description;
		this.icon = icon;
	}
	
	public ImageIcon getIcon(File f) {
		return icon;
	}
	
	/**
	* return a list of extensions, if one of them 
	* is null then ANY file will be accepted
	*/
	public String[] getExtensions() {
		return exts;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String getTypeDescription(File f) {
		return desc;
	}
	
	public JPanel getPanel(File f) {
		JPanel jp = new JPanel();
		jp.add(new JLabel(desc));
		return jp;
	}
}
/*
* $Log: SimpleFileBrowser.java,v $
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.20  2004/10/18 17:12:49  carlito
* Size reduced for mac UI
*
* Revision 1.19  2004/09/23 10:53:20  jvaucher
* Some modifications on the SimpleBrowser rendering
*
* Revision 1.18  2004/09/14 10:17:07  carlito
* FileBrowser updated for macs
*
* Revision 1.17  2004/09/04 18:10:15  kaspar
* ! Log.out -> log4j, last part
*
* Revision 1.16  2004/06/28 19:12:45  carlito
* FileBrowser info panel width incremented by 30
*
* Revision 1.15  2004/06/21 06:58:34  perki
* Loading panel ok
*
* Revision 1.14  2004/06/18 18:25:23  perki
* *** empty log message ***
*
* Revision 1.13  2004/06/16 07:50:58  perki
* *** empty log message ***
*
* Revision 1.12  2004/05/27 14:42:11  perki
* *** empty log message ***
*
* Revision 1.11  2004/05/12 13:37:54  perki
* Log is clever
*
* Revision 1.10  2004/04/09 07:16:37  perki
* Lot of cleaning
*
* Revision 1.9  2004/03/08 17:57:29  perki
* *** empty log message ***
*
* Revision 1.8  2004/02/22 15:57:01  perki
* Xstream sucks
*
* Revision 1.7  2004/01/29 13:03:04  carlito
* warnings and imports corrected ...
*
* Revision 1.6  2004/01/13 11:06:58  perki
* lot of javadoc repairs
*
* Revision 1.5  2003/10/29 08:03:16  perki
* File Utilities
*
* Revision 1.4  2003/10/10 11:26:30  perki
* test
*/