/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: IOLib.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */

package com.simpledata.bc.datatools;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.filetools.Secu;
import com.simpledata.filetools.SimpleException;

import foxtrot.Task;
import foxtrot.Worker;
/**
 * This class is a library for low level load / save operation.
 */
class IOLib {
	
	// #### FIELDS ############################################################
	
	/** Logger */
	private static final Logger m_log = Logger.getLogger( IOLib.class ); 
	
	// #### STATIC METHODS ####################################################
	
	/**
	 * Save data, using a temporary file to avoid destroying the old file if
	 * the save fails. It also use a monitor (can be null) for displaying the
	 * progress of the save process. This method can only be called by a AWT
	 * event. If you want another thread to save, use the unsecure way.
	 * @param head Header of the file.
	 * @param data Data of the file.
	 * @param f the file to write.
	 * @param method The method to use for saving 
	 * One of com.simpledata.filetools.Secu.METHOD_xxx constants
	 * @param monitor a Secu.Monitor displaying the state of the saving process. 
	 * @return true if the file succeded.
	 */
	static synchronized boolean saveSecure(final Object head,
			final Object data, final File f, final int method,
			final Secu.Monitor monitor) {
		assert SwingUtilities.isEventDispatchThread() :
			"The saveSecure method can be used only by the AWT EventDispatcher"+
			" thread.";
		
		try {
			Worker.post(new Task(){
				public Object run() throws Exception {
					save(head, data, f, method, monitor, true);
					return null;
				}});
		} catch (Exception e) {
			m_log.error("Save failed.",e);
			return false;
		}
		return true;
	}
	
	/**
	 * Save data, writing direct into the file. Without chowing the progress
	 * @param head Header of the file.
	 * @param data Data of the file.
	 * @param f the file to write.
	 * @param method The method to use for saving 
	 * One of com.simpledata.filetools.Secu.METHOD_xxx constants 
	 * @return true if the file succeded.
	 */
	static synchronized boolean saveUnsecure(final Object head,
			final Object data, final File f, final int method) {
		try {
			save(head, data, f, method, null, false);
		} catch (SimpleException e) {
			m_log.error( "Unsecure save failed.", e );
			return false;
		}
		return true;
	}
	
	/**
	 * Load a tarification from file, using a monitor to display the progress.
	 * You have to call this method from a AWT event thread.
	 * 
	 * @param f the file to load.
	 * @param monitor is a Secu.Monitor implementation
	 * @return the loaded tarification, null if the load fails.
	 */
	static synchronized Tarification load(final File f, 
												 final Secu.Monitor monitor) {
		assert SwingUtilities.isEventDispatchThread() :
			"The load method can be used only by the AWT EventDispatcher"+
			" thread.";
		Object result = null;
		try {
			result = Worker.post (new Task(){		
				public Object run() throws Exception {
					return IOLib.loadObject(f, monitor);
				}});
		} catch (Exception e) {
			m_log.debug("Cannot load. Got exception: ",e);
		}
		return (Tarification) result;
	}
	
	// #### METHODS private ###################################################
	
	/** Save */
	static private void save(Object head, Object data, File f,
			int method, Secu.Monitor monitor, boolean transactionMode)
			throws SimpleException {
		if (transactionMode)
			saveWithTransaction(head, data, f, method, monitor);
		else {
			Object res = saveWithoutTransaction(head, data, f, method, monitor);
			if (res != null)
				throw (SimpleException) res;
		}
	}
	
	/** Save the file using a temporary file. Return true if save successed */
	static private void saveWithTransaction(Object head, Object data, File f,
			int method, Secu.Monitor monitor) throws SimpleException {
		File tempFile;
		boolean success = true;
		try {
			// create temp file
			tempFile = File.createTempFile("tye",null);
			m_log.debug("Using temporary file: "+tempFile);
			// save data into temp file
			
			Object res=
			    saveWithoutTransaction(head,data,tempFile,method,monitor);
			success = (res == null);
			if (!success) {
				throw (SimpleException) res;
			}
			// replace the real file with the temporary one
			if (f.exists()) {
				success &= f.delete();
			}
			if (success && ! tempFile.renameTo(f)) {
				m_log.warn("Unable to move temporary file to destination file");
				ModalDialogBox.alert(BC.bc.getMajorComponent(),"<HTML>"+
						Lang.translate("An error occured during the replacement"
						        +
								" of the file. Check directory permissions or "+
						"try save as.")+"</HTML>");
				success = false;
			}
			// handle failure 
			
		} catch (IOException e) {
			m_log.error("Unable to produce temporary file.",e);
			success = false;
		} 
		if (! success)
			throw (new SimpleException(SimpleException.IOException,
			"Unable to perform secure saving."));
	}
	
	/** Main save method, using bsh script */
	private static SimpleException saveWithoutTransaction(Object head, Object data, 
			File f, int method, Secu.Monitor monitor)  {
		
		try {
			Secu.save(head, data, f, (String) null, method, monitor);
		} catch (SimpleException e) {
			return e;
		}
		
		return null;
	}
	
	/** load the content of a file. Return null if it fails */
	private static Object loadObject(File f, Secu.Monitor monitor) 
						  throws SimpleException {
		return Secu.getData(f, null,monitor);
	}
}
/*
 * $Log: IOLib.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.15  2004/11/29 10:07:49  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/11/16 15:17:55  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.13  2004/11/08 16:42:35  jvaucher
 * - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 * Revision 1.12  2004/11/05 10:06:00  jvaucher
 * - Ticket #34 : Ensure non destructive save
 *
 * Revision 1.11  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.10  2004/08/25 13:07:43  kaspar
 * ! Added names to threads.
 *
 * Revision 1.9  2004/05/27 14:41:56  perki
 * added merging state alpha
 *
 * Revision 1.8  2004/05/22 08:39:35  perki
 * Lot of cleaning
 *
 * Revision 1.7  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.6  2004/03/17 10:54:45  perki
 * Thread for params
 *
 * Revision 1.5  2004/03/13 14:50:39  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/03/12 17:48:06  perki
 * Monitoring file loading
 *
 * Revision 1.3  2004/03/08 17:53:18  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/02/02 11:21:05  perki
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 15:53:39  perki
 * start
 *
 */