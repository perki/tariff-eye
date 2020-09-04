/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 8 nov. 2004
 */
package com.simpledata.bc.datatools;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarification;

/**
 * This class is a TimerTask that save a tarification file. It uses a name
 * autosave_XX_yyyyy.tmp, where XX represents the environmenent where the
 * tarification was shown, and yyyyy is an unique number. When the program
 * starts Desktop.checkCrash() method looks up for these file and reopen
 * it if the user wants to.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: AutosaveTask.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
public class AutosaveTask extends TimerTask {
	// CONSTANTS - autosave files name
	
	
	/** 
	 * Autosave file prefix. <br>
	 * All autasave file begins with this string. It allows Desktop.checkCrash()
	 * method to retrieve them
	 */
	public static final String AUTOSAVE_PREFIX = "autosave_";
	
	/** Name for autosaved file of simulator */
	public static final String SIM_PREFIX      = AUTOSAVE_PREFIX+"sim_";
	/** Name for autosaved file of Creator */
	public static final String CREATOR_PREFIX  = AUTOSAVE_PREFIX+"cr_";
	/** Name for autosaved file of CreatorLive */
	public static final String LIVE_PREFIX     = AUTOSAVE_PREFIX+"live_";
	
	// CONSTANTS - Environment parameter
	
	/** Use this constant to generate autosave files for the simulator */
	public static final int SIMULATOR_ENVT = 0;
	/** Use this constant to generate autosave files for the creator */
	public static final int CREATOR_ENVT = 1;
	/** Use this constant to generate autosave files for creator live */
	public static final int LIVE_ENVT = 2;
	
	/** Logger */
	private final static Logger m_log = Logger.getLogger(AutosaveTask.class);
	
	/** Temporary file where we do save */
	private File m_autoSaveFile;
	
	/** Name of the temp file */
	private final String m_fileNamePrefix;
	
	/** Corresponding Tarification */
	private final Tarification m_tarification;
	
	/** Is the agent running */
	private boolean m_running;

	/**
	 * Construct a new task. If you want it to run, call start().
	 * @param t Tarification object to save.
	 * @param environment One of the above environment constants.
	 */
	public AutosaveTask(Tarification t, int environment) {
		assert t != null : "You cannot create an autosave task for a null tarification";
		m_tarification = t;
		// Name of the file
		switch (environment) {
		case SIMULATOR_ENVT:
			m_fileNamePrefix = SIM_PREFIX;
			break;
		case CREATOR_ENVT:
			m_fileNamePrefix = CREATOR_PREFIX;
			break;
		case LIVE_ENVT:
			m_fileNamePrefix = LIVE_PREFIX;
			break;
		default:
			m_fileNamePrefix = "";
			assert false : "Unknow environment for autosave.";
		}
		m_running = false;
		m_log.debug("Registred new autosave agent for tarification "+t);
	}
	
	/**
	 * Called by the timer. It saves the tarification in the autosave file,
	 * using object form.
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		if (m_running) {
			try {
				File tmp = File.createTempFile("tariffeye",null);
				FileManagement.bgrObjectSave(m_tarification, tmp);
				m_autoSaveFile.delete();
				tmp.renameTo(m_autoSaveFile);
			} catch (IOException e) {
				m_log.warn("Unable to perform secure autosave...",e);
			}
		}
	}
	
	/**
	 * Deletes the autosave file, and kill the task.
	 * @see java.util.TimerTask#cancel()
	 */
	public boolean cancel() {
		// delete the temporary file
		if (m_autoSaveFile != null)
			m_autoSaveFile.delete();
		return super.cancel();
	}
	
	/**
	 * Turn the autosaver on. Creates the temp file. Called when the user
	 * modify its work.
	 */
	public void start() {
		if (!m_running) {
			File autoSaveDir = new File(Resources.dataPath());
			//Create the file
			try {
				m_autoSaveFile = File.createTempFile(m_fileNamePrefix,".tmp",autoSaveDir);
			} catch (IOException e) {
				m_log.warn("Unable to create temp file for autosave in directory "+
						autoSaveDir, e);
				m_autoSaveFile = null;
				cancel();
			}
		    m_log.debug("Autosave started. File is "+m_autoSaveFile);
		    m_running = true;
		    run();
		}
	}

	/**
	 * Turn the autosaver off and delete the autosave file. Called when
	 * the user saves his work.
	 */
	public void stop() {
	    if (m_running) {
	        m_log.debug("Autosave stopped.");
	        m_running = false;
	        m_autoSaveFile.delete();
	    }
	}
}
