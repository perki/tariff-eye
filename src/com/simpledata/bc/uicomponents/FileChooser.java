/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 21 sept. 2004
 */
package com.simpledata.bc.uicomponents;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.filetools.SimpleFileBrowser;
import com.simpledata.filetools.SimpleFileView;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * This is a UI utility interfacing for load and save operation. It uses
 * the SimpleFileBrowser of the sdl. Regarding its interface.
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: FileChooser.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class FileChooser extends JPanel {

	// #### CONSTANTS #########################################################
	
	/** Locale */
	private final static String OPT_USERS_DIR =
		"Use a user's tarification file";
	private final static String OPT_LIBRARY =
		"Use a tarification from the Tarification library";
	private final static String TARIFICATION_LIBRARY = "Tarification Library";
	
	/** 
	 * This constants represents the return value of a show method
	 * when the user hits the approve button. It binds the same value as
	 * the swing's JFileChooser does to preserve the complience.
	 * Nevertheless, the programmer should use this one to avoid useless link
	 */
	public final static int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	
	/**
	 * Return value when the user hit the cancel button.
	 * see APPROVE_OPTION
	 */
	public final static int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
	
	/** Represents a Save DialogType */
	public final static int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;
	/** Represents an Open DialogType */
	public final static int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;
	/** Represents a Custom DialogType */
	public final static int CUSTOM_DIALOG = JFileChooser.CUSTOM_DIALOG;
	
	/** Value of the field since there's no answer */
	protected final static int NO_ANSWER = -1;
	
	// #### FIELDS ############################################################
	/** Chooser part of the UI */
	private final SimpleFileBrowser m_chooser;
	
	/** Library option panel's radio buttons */
	private final JRadioButton m_locUserDir;
	private final JRadioButton m_locLibrary;
	
	/** Logger */
	private final static Logger m_log = Logger.getLogger(FileChooser.class);
	
	/** PopUp object instanciated once of each call of showDialog */
	PopUp m_popup;
	
	/** Contains the old shown directory when the user switch to the library */
	private File m_userDir;
	
	/** Flag. Does the user select the tarification from the library */
	private boolean m_fileFromLibrary;
	
	/** Options shown flags */
	private final boolean m_showLibraryOption;
	
	private final JPanel[] m_moreOptions;
	
	// #### CONSTRUCTOR #######################################################
	
	/**
	 * Construct a new FileChooser.
	 * @param currentDirectory The first directory to be shown to the user.
	 * @param showLibraryOption If set to true the panel with the library view
	 * option is added to the chooser.
	 */
	public FileChooser(File currentDirectory, boolean showLibraryOption) {
		this(currentDirectory, showLibraryOption, new JPanel[0]);
	}
	
	/**
	 * Construct a new FileChooser.
	 * @param currentDirectory The first directory to be shown to the user.
	 * @param showLibraryOption If set to true the panel with the library view
	 * option is added to the chooser.
	 * @param moreOptions once can add more panel on the windows bottom.
	 */
	public FileChooser(File currentDirectory, 
					   boolean showLibraryOption,
					   JPanel[] moreOptions) {
		super();
		m_popup = null;
		m_fileFromLibrary = false;
		m_moreOptions = moreOptions;
		m_locUserDir = new JRadioButton();
		m_locLibrary = new JRadioButton();
		m_showLibraryOption = showLibraryOption;
		m_userDir = currentDirectory;
		m_chooser = new SimpleFileBrowser(currentDirectory);
		buildMe();
	}
	
	// #### METHODS - Ui components init ######################################
	
	/** Build the chooser */
	private void buildMe() {
		JPanel options = buildOptionsPanel();
		
		m_chooser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				synchronized (m_popup) {
					if (command.equals("CancelSelection")) {
						m_popup.m_answer = CANCEL_OPTION;
					} else if (command.equals("ApproveSelection")) {
						m_popup.m_answer = APPROVE_OPTION;
					}
					m_popup.notify();
				}
			}});
			
		this.setLayout(new BorderLayout());
		this.add(m_chooser, BorderLayout.CENTER);
		this.add(options, BorderLayout.SOUTH);
	}
	
	/** Build all options panels and return a container of them. */
	private JPanel buildOptionsPanel() {
		LinkedList /*<JPanel>*/ options = new LinkedList();
		
		// Process all options panel, depending the associate flag
		if (m_showLibraryOption) 
			options.addLast(buildOptionLibrary());
		for (int i=0; i<m_moreOptions.length; i++) {
			options.addLast(m_moreOptions[i]);
		}
		// Construct the parent panel
		int optionsCount = options.size();
		JPanel result = new JPanel(new GridLayout(optionsCount,1));
		for (int i = 0; i<optionsCount; i++)
			result.add((Component)options.get(i));	
		
		return result;
	}
	
	/** Build the Library View option panel */
	private JPanel buildOptionLibrary() {
		JPanel result = new JPanel(new GridLayout(2,1));
		Border border = BorderFactory.createLineBorder(new Color(0.2f, 0.2f, 0.2f));
		border = BorderFactory.createTitledBorder(border, Lang.translate(TARIFICATION_LIBRARY));
		result.setBorder(border);
		ButtonGroup selection = new ButtonGroup();
    	
		m_locUserDir.setText(Lang.translate(OPT_USERS_DIR));
		m_locUserDir.setSelected(true);
		m_locUserDir.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				switchToUserDir();
			}});
    	
    	m_locLibrary.setText(Lang.translate(OPT_LIBRARY));
    	m_locLibrary.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				switchToLibrary();
			}});
    
    	selection.add(m_locUserDir);
    	selection.add(m_locLibrary);
    	result.add(m_locUserDir);
    	result.add(m_locLibrary);
    	
    	return result;
	}
	
	/**
	 * Tells the chooser to switch to the library view. The current shown 
	 * directory become the tarification library regarding the user's 
	 * parameters. You can call this method even if the Library View option
	 * panel is not visible for the user. 
	 */
	public void switchToLibrary() {
		m_userDir = m_chooser.getCurrentDirectory();
		m_log.debug("User.dir prop :"+System.getProperty("user.dir"));
		File libraryDir = new File(System.getProperty("user.dir"),
				BC.getParameterStr(Params.KEY_TARIFICATION_LIBRARY_PARTH));
		if (! libraryDir.exists()) {
			m_log.warn ("The library folder doesn't exist. Creating it");
			libraryDir.mkdir();
		}
		// TODO perhaps kill the buttons on the top
		m_chooser.setCurrentDirectory(libraryDir);
		m_fileFromLibrary = true;
		m_locLibrary.setSelected(true);
	}
	
	/**
	 * Tells the chooser to switch to a normal view for the user.
	 */
	public void switchToUserDir() {
		m_chooser.setCurrentDirectory(m_userDir);
		m_fileFromLibrary = false;
		m_locUserDir.setSelected(true);
	}
	
	/**
	 * Returns true if the user browses his file using the from library view.
	 * It never ensures that the selected file is an original publication,
	 * but it is used for the browser to not remember the tarification directory
	 * as a normal user directory. If you want to know if your file is a
	 * publication, rather use the header of the file.
	 * @return true if the user browse his file using the library view.
	 */
	public boolean fileFromLibrary() {
		return m_fileFromLibrary;
	}
	
	// #### METHODS - File chooser wrappers ###################################
	
	/**
	 * SimpleFileBrowser 's method wrapper.
	 * @see com.simpledata.filetools.SimpleFileBrowser
	 */
	public void addFileView(SimpleFileView sfv, int level) {
		m_chooser.addFileView(sfv, level);
	}
	
	/**
	 * SimpleFileBrowser 's method wrapper.
	 * @see com.simpledata.filetools.SimpleFileBrowser
	 */
	public File getSelectedFile() {
		return m_chooser.getSelectedFile();
	}
	
	/**
	 * SimpleFileBrowser 's method wrapper.
	 * @see com.simpledata.filetools.SimpleFileBrowser
	 */
	public File[] getSelectedFiles() {
		return m_chooser.getSelectedFiles();
	}
	
	/**
	 * SimpleFileBrowser 's method wrapper.
	 * @see com.simpledata.filetools.SimpleFileBrowser
	 */
	public SimpleFileView getSelectedFileView() {
		return m_chooser.getSelectedFileView();
	}
	
	// #### METHODS - Show file chooser #######################################
	
	/**
	 * This is a blocking method. It shows the user a FileChooser, using
	 * the ModalJFrame utility. The text of the approve button is 'open'
	 * @param parent Parent frame.
	 * @return The code of the user action. I.e. APPROVE_OPTION or CANCEL_OPTION.
	 */
	public int showOpenDialog(Component parent) {
	    return showDialog(parent, Lang.translate("Open"), OPEN_DIALOG);
	}
	
	/**
	 * This is a blocking method. It shows the user a FileChooser, using
	 * the ModalJFrame utility. The text of the approve button is 'save'
	 * @param parent Parent frame.
	 * @return The code of the user action. I.e. APPROVE_OPTION or CANCEL_OPTION.
	 */
	public int showSaveDialog(Component parent) {
	    return showDialog(parent, Lang.translate("Save"), SAVE_DIALOG);
	}
	
	/**
	 * This is a blocking method. It shows the user a FileChooser, using
	 * the ModalJFrame utility. 
	 * @param parent Parent frame.
	 * @param approveButtonText Text shown into the approve button.
	 * @param dialogType one of SAVE_DIALOG, LOAD_DIALOG, CUSTOM_DIALOG. Defines
	 * which elements are on the window.
	 * @return The code of the user action. I.e. APPROVE_OPTION or CANCEL_OPTION.
	 */
	public int showDialog(Component parent, String approveButtonText, int dialogType) {
		m_chooser.setDialogType(dialogType);
		m_chooser.setApproveButtonText(approveButtonText);
		
		m_popup = new PopUp(this, parent);
		
		if (! SwingUtilities.isEventDispatchThread()) 
			try {
				SwingUtilities.invokeAndWait(m_popup);
			} catch (InvocationTargetException e) {
				m_log.error("Cannot create file chooser popup. ",e);
			} catch (InterruptedException e) {
				m_log.error("Cannot create file chooser popup. ",e);
			} 
		else
			m_popup.run();
		
		int result = m_popup.m_answer;
		m_popup = null;
		return result; 
	}
	
	// #### INNER CLASS #######################################################
	
	/**
	 * This class implements the runnable method that shows the chooser
	 * and block until the user choose something. A new instance should be
	 * used for each call of showDialog method.
	 */
	private static class PopUp implements Runnable {
		/** Logger */
		private static final Logger m_log = Logger.getLogger(PopUp.class);
		
		private final JPanel m_panel;
		private final Component m_parent;
		/** The selected button */
		protected int m_answer; 
		
		protected PopUp(JPanel popup, Component parent) {
			m_panel = popup;
			m_parent = parent;
			m_answer = NO_ANSWER;
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			/*// Don't work, sad.
			int x = (m_parent.getWidth() - m_panel.getWidth()) / 2;
			int y = (m_parent.getHeight() - m_panel.getHeight()) / 2;
			m_log.debug("parent wdth  : "+m_parent.getWidth());
			m_log.debug("panel wdth   : "+m_panel.getWidth());
			m_log.debug("parent height: "+m_parent.getHeight());
			m_log.debug("panel height : "+m_panel.getHeight());
			*/
			int x = 200; int y = 100; // TODO compute
			
			final Point delta = new Point(x,y);
			
			final ModalJPanel popUp = ModalJPanel.createSimpleModalJInternalFrame
			(m_panel,m_parent,delta,false,null,Resources.modalBgColor);
			
			try {
				Worker.post(new Task() {
					public Object run() throws Exception {
						waitForAnswer();
						popUp.close();
						return null;
					}});
			} catch (Exception e) {
				m_log.error("Pop up got an exception.",e);
			}
		}
		
		protected synchronized void waitForAnswer() throws InterruptedException{
			while (m_answer == NO_ANSWER)
				wait();
		}
		
	}
}
