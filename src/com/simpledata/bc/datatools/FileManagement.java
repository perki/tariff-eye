/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: FileManagement.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.datatools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Desktop;
import com.simpledata.bc.Params;
import com.simpledata.bc.Resources;
import com.simpledata.bc.SoftInfos;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.FileChooser;
import com.simpledata.bc.uicomponents.conception.CreatorGold;
import com.simpledata.bc.uicomponents.conception.CreatorLight;
import com.simpledata.bc.uicomponents.simulation.Simulator;
import com.simpledata.bc.uicomponents.tools.TarificationPropertiesPanel;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.filetools.FileUtilities;
import com.simpledata.filetools.Secu;
import com.simpledata.filetools.SecuSelf;
import com.simpledata.filetools.encoders.SelfDC_GZIP;
import com.simpledata.filetools.encoders.SelfDT_Serializable;

/**
 * Class with all tools including GUI for file Management
 */
public class FileManagement {
	// #### CONSTANTS ########################################################
	
	/** Differents load / save methods */
	private static final int TYPE_ANY= -1;
	private static final int TYPE_OBJECT_COMPRESS= 0;
	private static final int TYPE_XML_COMPRESS = 1;
	
	/** Default load / save method */
	private static final int TYPE_PREFERED_METHOD = TYPE_XML_COMPRESS;
	
	/** Map between local constants and Secu saving method constants */
	private static final int[] methodSave = { Secu.METHOD_OBJECT_COMPRESS_DES,
											  Secu.METHOD_XML_COMPRESS_DES};
	
	/** Map between save methods and file name extensions */
	static final String[] tarificationFileExt = {"tye","tye"};
	
	/** Map between save methods and descriptions */ 
	static final String[] tarificationTitle = { "Tarification Quick Save",  
									 "Tarification Exchange Format"};
	
	
	/** 
	 * default icon dimensions for the icon on the filesystem 
	 * maybe directly changed 
	 */
	public static Dimension defaultIconDimension = new Dimension(32,32);
	
	// #### CONSTANTS - public type of load / save operation #################
	
	// - Load -
	
	/** New tarification operation */
	public final static String CREATOR_NEW = "Creator:new";
	/** Open a tarification */
	public final static String CREATOR_OPEN = "Creator:open";
	/** New tarification using creator live */
	public final static String CREATOR_GOLD_NEW = "Creator:Gold:new";
	/** Open a tarification using creator live */
	public final static String CREATOR_GOLD_OPEN = "Creator:Gold:open";
	/** Start a new simulation */
	public final static String SIMULATOR_NEW = "Simulator:new";
	/** Open an existing portfolio */
	public final static String SIMULATOR_OPEN = "Simulator:open";
	
	// - save -
	
	/** Pusblish a tarification */
	public final static String CREATOR_PUBLISH = "Creator:Publish";
	/** Save a tarification */ 
	public final static String CREATOR_SAVE = "Creator:save";
	/** Save a porfolio */
	public final static String SIMULATOR_SAVE = "Simulator:save";

	// #### FIELDS ###########################################################
	
	/** Logger */
	private static final Logger m_log = Logger.getLogger(FileManagement.class);
	
	/** static instance to avoid double file chooser spawn */
	private static FileChooser uniqueChooser = null;
	

	// #### METHODS - public load / save #####################################
	
	/**
	 * Save the tarification, without poping a browser if possible.
	 * @param t The tarification to save
	 * @param savingType One of the constants. It describes where the save
	 * operation was called from. 
	 * @return true iif the save succeded.
	 */
	public static boolean save(Tarification t, String savingType) {
		if (savingType.equals(CREATOR_SAVE))
			return saveTarification(t);
		else if (savingType.equals(SIMULATOR_SAVE))
			return savePortofolio(t);
		else {
			m_log.error ("Called save with unknown saving type");
			return false; 
		}
	}
	
	/**
	 * Open a File browser to save a Tarification
	 * @param t The tarification object to save
	 * @param operationType one of the constants. 
	 * It describe from where the save operation was called. 
	 * @return true iif the save succeded.
	 */
	public static boolean saveAs(Tarification t, String operationType) {
		// update header
		TarificationHeader header = t.getHeader();
		if (operationType.equals(CREATOR_SAVE))
			header.setDataType(TarificationHeader.TYPE_TARIFICATION_MODIFIED);
		else if (operationType.equals(SIMULATOR_SAVE))
			header.setDataType(TarificationHeader.TYPE_PORTFOLIO);
		else if (operationType.equals(CREATOR_PUBLISH)) 
			header.setDataType(TarificationHeader.TYPE_TARIFICATION_ORIGINAL);
		else {
			m_log.warn("Unknow operation type for SaveAs...");
			return false;
		}
		return saveDialog(t, operationType);
	}
	
	/**
	 * This save quickly a file. It uses the object save method,
	 * and show no progress bar.
	 * @param t the tarification object to save.
	 * @param autoSaveFile the file to overwrite.
	 */
	static void bgrObjectSave(Tarification t, File autoSaveFile) {
		
        TarificationHeader header = t.updateAndGetHeader();
        header.setIdLicense(SoftInfos.id());
        header.setLicensedCompanyName(
        		BC.getParameterStr("companyName"));
        
        SecuSelf ss = new SecuSelf(
        		new SelfDT_Serializable(header),
				new SelfDT_Serializable(t));
        ss.insertDataEncoder(new SelfDC_GZIP());
        
        
        FileOutputStream fos;
		try {
			fos = new FileOutputStream(autoSaveFile);
			ss.commit(fos);
			//m_log.debug("Autosave : "+autoSaveFile);
		} catch (Exception e) {
			m_log.error("Failed autosave : ",e);
		} 
	}
	
	/**
	 * This method is called within the desktop (mainly) 
	 * to open in a determined context (i.e. creator, creator-gold, simulator)
	 * a new or an existing tarification... It loads a tarification and open
	 * it in the specified window.
	 * @param rootFrame the frame the file browser will be attached to
	 * @param openingType the type of app and tarification that has to be loaded
	 */
	public static void promptFileAndStartApp(final JFrame rootFrame,
									    final String openingType) {
		String[] typesOfContent = TarificationHeader.tagsForTypeAny;
		String creatorApp = "creator";
		String simulatorApp = "simulator";
		String creatorGoldApp = "creatorGold";
		String app = creatorApp;
		
		File fileForAutoLoad = null;
		
		if (openingType == CREATOR_NEW) {
			app = creatorApp;
			fileForAutoLoad = Resources.getBlankTariffLocation();
		} else  if (openingType == CREATOR_OPEN) {
			app = creatorApp;
			typesOfContent = TarificationHeader.tagsForTypeAnyTarifications;
		} else if (openingType == CREATOR_GOLD_NEW) {
			app = creatorGoldApp;
			fileForAutoLoad = Resources.getBlankTariffLocation();
		} else if (openingType == CREATOR_GOLD_OPEN) {
			app = creatorGoldApp;
			typesOfContent = TarificationHeader.tagsForTypeAny;
		} else if (openingType == SIMULATOR_NEW) {
			app = simulatorApp;
			typesOfContent = TarificationHeader.tagsForTypeAnyTarifications;
		} else if (openingType == SIMULATOR_OPEN) {
			app = simulatorApp;
			typesOfContent = TarificationHeader.tagsForTypeAnyPortfolio;
		} else {
			m_log.error( "Got a wrong openingType parameter : '"+openingType+"'" );
		}
		Tarification t = null;
		if (fileForAutoLoad != null) {
			t = loadTarification(fileForAutoLoad, rootFrame);
			
			if (t != null) {
				// To avoid save over blank tariff
				// We consider autoload only for new tariff
				// which could be inaccurate if we had a menu 
				// remembering old opened files
				t.getHeader().setLoadingLocation(null);
				
				// check that this file is really the clean one
				if (! t.getHeader().isOpenableInDemo()) {
					t = null;
					ModalDialogBox.alert(
							rootFrame        
							,"Empty tariff is not valid");
				}
				
			} 
		} else {
			// We have to make the user choose the file he wants to load
			t=openTarification(typesOfContent, rootFrame, openingType);
		}
		if (t== null) return;
		
		if (app == creatorApp) {
			// TODO modify Tarification Creator to allow
			// two different modes
			CreatorLight.openTarification(t);
		} else if (app == creatorGoldApp) {
			// TODO modify Tarification Creator to allow
			// two different modes
			CreatorGold.openTarification(t);
		} else {
			// We assume it is a simulation...
			Simulator.openSimulation(t);
		}	       
	}
	
	private static final String[] toolsTitles = new String[] {
	        "dummy",
	        Desktop.MENU_TITLE_SIMULATION,
	        Desktop.MENU_TITLE_CREATION,
	        Desktop.MENU_TITLE_CREATION_LIVE
	};
	
	/**
	 * Retrive data from an autosave file, and deletes it after.
	 * 
	 * @param file autosave file.
	 * @param rootFrame component.
	 * @param tool 0:unkown, 1:simulation, 2:creation, 3:creationLive
	 */
	public static void 
		openExternal(File file, final JFrame rootFrame,int tool) {
	    if (file.isDirectory() || (! file.exists())) return;
	    
	    // check if I can open this file
	    TarificationFileView tfv = 
		  new TarificationFileView(TYPE_ANY,TarificationHeader.tagsForTypeAny);
	    
	    
	    TarificationHeader tarifHeader = tfv.getAcceptAndHeader(file,false);
	    
	    if (tarifHeader == null) {
	        m_log.warn("User tried to open a forbiden file"+file);
	        BC.bc.alertUser(
	        Lang.translate("This file is not valid or you do not have the " +
	        		"credentials to open it"),"");
	        return;
	    }
	    
	    
	    
		Tarification result = null;
		try {
			Secu.Monitor monitor = 
				LoadingDialog.getJInternalFrame(rootFrame);
			result = IOLib.load(file,monitor);
		} catch (Exception e) {
			m_log.warn("The file "+file+" cannot be retrieved. Giving up.",e);
			return;
		} 
		if (result == null) {
		    m_log.warn("The file "+file+" cannot be retrieved. Giving up.");
		    return;
		}
		result.setReadyForCalcul();
		
		
		//******** Choose an application to open it
		
		boolean isPortfolio=
		    TarificationHeader.TYPE_PORTFOLIO.equals(tarifHeader.getDataType());
		
		// create the list of authorized openeners
		ArrayList/*<Integers>*/ tools = new ArrayList/*<Integer>*/();
		switch (tool) {
			case -1:
			    tools.add(new Integer(1));
			 	if (! isPortfolio)
			 	    if (SoftInfos.canGoCreation()) tools.add(new Integer(2));
			    if (SoftInfos.canGoCreationGold()) tools.add(new Integer(3));
			break;
			case 3:
			    if (SoftInfos.canGoCreationGold()) {
			        tools.add(new Integer(3));
			        break;
			    } 
			case 2:
			    if (SoftInfos.canGoCreation()) {
			        tools.add(new Integer(2));
			        break;
			    } 
			case 1:
			    tools.add(new Integer(1));
			break;
		}
		
		if (tools.size() == 0) {
		    m_log.warn("Cann find a suitable tool to open "+file);
		    return;
		}
		
		// if there is more than one choice ask the user for the tool to open
		int choosenTool = ((Integer) tools.get(0)).intValue();
		if (tools.size() > 1) {
		    
		    String m = "<HTML><B>"
		        +Lang.translate("Choose a tool to open:")+"</B><BR>"+
		    	"<CENTER>"+tarifHeader.getTitle()+"<BR>"+
		    	file.getName()
		    	+"</CENTER></HTML>";
		    
		    String[] options = new String[tools.size()+1];
		    options[options.length-1] = Lang.translate("Cancel");
		    int j = 0;
		    for ( Iterator i = tools.iterator(); i.hasNext();j++) {
		        String t = toolsTitles[((Integer) i.next()).intValue()];
		        options[j] = Lang.translate(t); 
		    }
		    
		    int choice = ModalDialogBox.custom(rootFrame,m,options,
		            tarifHeader.getIcon());
		    
		    if (choice >= 0 && choice < tools.size()) {
		        choosenTool = ((Integer) tools.get(choice)).intValue();
		    } else {
		        return;
		    }
		    
		}
		
		switch (choosenTool) {
		case 1:
		    Simulator.openSimulation(result).needSave();
		    break;
		case 2:
		    CreatorLight.openTarification(result).needSave();
		    break;
		case 3:
		    CreatorGold.openTarification(result).needSave();
		    break;
		}
	}
	
	
	
	/**
	 * Prompt the user with a chooser to select a tarification file.
	 * Load the file and returen the Tarification
	 * @param rootFrame the frame to go on top of
	 * @param typesOfContent one of TarificationHeader.tagsForXXXX
	 * else will open Simulator
	 * @return Tarification, null if it fails.
	 */
	public static Tarification openTarification (String[] typesOfContent,
	        									  final JFrame rootFrame,
	        									  String openingType) {
		if (uniqueChooser != null) {
		    if (uniqueChooser.isShowing()) {
		        // A file chooser is currently showing... we return
		        return null;
		    } 
		}
	    
		// Select the right default path for the open file dialog
		File openPath = retrieveUserPreferredPath(openingType);
	
		//FileChooser fc;
		if (openingType.equals(SIMULATOR_OPEN)) {
			//fc = new FileChooser(openPath, false);
		    uniqueChooser = new FileChooser(openPath, false);
		} else {
		    uniqueChooser = new FileChooser(openPath, true);
		}
		
		if (openingType.equals(SIMULATOR_NEW)) {
			// Temporary behaviour. It opens the library first
			uniqueChooser.switchToLibrary();
		}
		
		TarificationFileView tfv = 
		    new TarificationFileView(TYPE_ANY,typesOfContent);
		
		if (TarificationHeader.tagsForTypeAnyPortfolio.equals(typesOfContent)) {
		    tfv.setFilterTitle(Lang.translate("Portofolio"));
		} else if (TarificationHeader.tagsForTypeAnyTarifications.equals(
		            typesOfContent)) {
		    tfv.setFilterTitle(Lang.translate("Tarification"));
		}
		uniqueChooser.addFileView(tfv, 0);
		
		int returnVal= uniqueChooser.showOpenDialog(rootFrame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file= uniqueChooser.getSelectedFile().getAbsoluteFile();
			m_log.info( "Selected file: " + file.getName() );
			if (file != null && !uniqueChooser.fileFromLibrary()) {
			    saveUserPath(openingType, file.getParentFile());
			}
			if (file.isDirectory()) {
			    m_log.warn( "user tried to open a directory" );
			    return null;
			} 
			if (! tfv.accept(file)) {
			    m_log.warn( "user tried to open a forbiden file" );
			    return null;
			} 
			 
			if (! tfv.canDecrypt(file)) {
			    ModalDialogBox.alert(rootFrame,
			            Lang.translate("You do not have the credentials to open this file"));
			    return null;
			} 
			return loadTarification(file, rootFrame);
			
		} 
		m_log.info( "File open Canceled " );
		return null;
	}
	
	// #### METHODS - private #################################################
	
	/** Save the tarificaiton as a not original tarification */
	private static boolean saveTarification(Tarification t) {
		boolean success;
		// verify the validity of t
		if (t == null) return false;
		
		TarificationHeader header = t.getHeader();
		String dataType = header.getDataType();
		if (dataType.equals(TarificationHeader.TYPE_TARIFICATION_MODIFIED)) {
			// try to recover the location of the file to override it
			File location = t.getHeader().myLoadingLocation();
			if (location == null) 
				// Ask the user to create a new file
				success = saveDialog(t, CREATOR_SAVE);
			else {
				// override the old file
				success = generateTyeFile(t,
						location, 
						TYPE_PREFERED_METHOD, 
						BC.bc.getMajorComponent());
			}
		} else 
		    if (dataType.equals(TarificationHeader.TYPE_TARIFICATION_ORIGINAL)){
			// we cannot override a original tarificatoin
			header.setDataType(TarificationHeader.TYPE_TARIFICATION_MODIFIED);
			success = saveDialog(t, CREATOR_SAVE); // perhaps it's gold, but don't care
		} else {
			// trying to write a tarification with a portofolio
			m_log.error("Trying to override a portofolio with a tarification");
			success = false;
		}
		return success;
	} 
	
	/** Save the tarification as a portfolio */
	private static boolean savePortofolio(Tarification t) {
		boolean success;
		// verify t
		if (t == null) return false;
		
		TarificationHeader header = t.getHeader();
		String dataType = header.getDataType();
		if (dataType.equals(TarificationHeader.TYPE_PORTFOLIO)) {
			// retrieve old file location
			File location = header.myLoadingLocation();
			if (location == null) {
				// ask a location for the file
				success = saveDialog(t, SIMULATOR_SAVE);
			} else
				success = generateTyeFile(t,
					location, 
					TYPE_PREFERED_METHOD, 
					BC.bc.getMajorComponent());
		} else {
			header.setDataType(TarificationHeader.TYPE_PORTFOLIO);
			success = saveDialog(t, SIMULATOR_SAVE);
		}
		return success;
	}
	
	/** Asks the user where he wants to save his file. And save it */
	private static boolean saveDialog(Tarification t, String operationType) {
		boolean success;
		JFrame rootFrame = BC.bc.getMajorComponent();
		File userDir = retrieveUserPreferredPath(operationType);
		FileChooser fc;
		String buttonName = Lang.translate("Save"); // default
		if (operationType.equals(CREATOR_PUBLISH)) {
			JPanel propPanel = new TarificationPropertiesPanel(t);
			JPanel[] options = {propPanel};
			fc = new FileChooser(userDir, false, options);
			fc.switchToLibrary();
			buttonName = Lang.translate("Publish");
		} else {
			fc = new FileChooser(userDir, false);
		}
		fc.addFileView(new TarificationFileView(TYPE_OBJECT_COMPRESS,null), 0);
		fc.addFileView(new TarificationFileView(TYPE_XML_COMPRESS,null), 0);
		//fc.addFileView(new TarificationFileView(TYPE_ARMORED,null), 0);
		// manage the good button name
		int returnVal= fc.showDialog(rootFrame, buttonName, FileChooser.SAVE_DIALOG);
		if (returnVal == FileChooser.APPROVE_OPTION) {
			// get the filter to apply	
			TarificationFileView tfv=
				(TarificationFileView) fc.getSelectedFileView();
			
			assert tfv.getType() >= 0;
			
			int choosenMethod = tfv.getType();
			
			File file=
				FileUtilities.forceExtension(
						fc.getSelectedFile(),
						tarificationFileExt[choosenMethod]);
			m_log.info( "Selected file: " + file.getName() );
			saveUserPath(operationType,file.getParentFile());
			if (file != null) {
				success = 
					generateTyeFile(t, file, choosenMethod, rootFrame);
				TarificationHeader header = t.getHeader();
				if (! operationType.equals(CREATOR_PUBLISH)) {
					//set the location of the current t
					header.setLoadingLocation(file);
				}
				m_log.debug("DataType after save: "+header.getDataType());
			} else {
				m_log.info( "Cancel " );
				success = false;
			}
		} else {
			m_log.info( "Cancel " ); // operation canceled
			success = false;
		}
		return success;
	}
	/**
	 * Generate the tarification file. Overriding the file if it already exists. 
	 */
	private static boolean generateTyeFile(Tarification t, 
	        File file, int choosenMethod, JFrame rootFrame) {
		boolean success;
	    if (file != null) {
	        Secu.Monitor monitor = 
				LoadingDialog.getJInternalFrame(rootFrame);
	       
	        int method = methodSave[choosenMethod];
	        
	        TarificationHeader header = t.updateAndGetHeader();
	        header.setModificationDate(new Date());
	        header.setIdLicense(SoftInfos.id());
	        header.setLicensedCompanyName(
	                BC.getParameterStr("companyName")); 
	        
	        success = IOLib.saveSecure(header,t,
	                file,method,monitor);
	    } else {
	    	m_log.error ("Cannot generate tye file for null tarification.");
			success = false;
	    }
	    return success;
	}
	
	/**
	 * Try to load a tarification directly from a determined file.
	 * @param file
	 * @param rootFrame
	 * @return tarification if found else null
	 */
	private static Tarification 
		loadTarification(File file, final JFrame rootFrame) {
		if (file != null) {
			try {
				Secu.Monitor monitor = 
					LoadingDialog.getJInternalFrame(rootFrame);
				
				// remove when file are converted
				//Currency.INIT_AT_XML_CREATION = true;
				Tarification t = IOLib.load(file,monitor);
				
				// remove when file are converted
				//Currency.INIT_AT_XML_CREATION = false;
				
				if (t != null) {
					
					// set the file to the header
					t.getHeader().setLoadingLocation(file);
					m_log.debug("DataType after load: "+t.getHeader().getDataType());
					t.setReadyForCalcul();
					return t;
					
				}
			} catch (ClassCastException e) {
				m_log.error( "failed loading data", e );
			} catch (Exception e) {
				m_log.error( "failed loading data", e );
			} 
		}
		return null;	    
	}
	
	// #### METHODS - private utils ###########################################
	
	/** Compute the save directory, using the user's preferences */
	private static File defaultSaveDir() {
		File saveDir = new File(
		        BC.getParameterStr(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH));
		if (! saveDir.exists()) {
			m_log.warn("Current user dir has been moved or deleted. Use user home");
			String userHome = Resources.findMyDocumentFolder();
			BC.setParameter(Params.KEY_DEFAULT_SAVED_TARIFICATION_PATH, userHome);
			saveDir = new File(userHome);
		}
		if (! saveDir.exists()) {
			File[] fsRoot = File.listRoots();
			if (fsRoot.length > 0) {
				m_log.warn("The system user.home directory doesn't exist. Using a FS root");
				saveDir = fsRoot[0];
			} else
				m_log.error("The system havn't any root file system. Rebuy a computer.");
		}
		return saveDir;
	}
	/**
	 * Save the last path used
	 */
	private static void saveUserPath(String operationType, File dir) {
		if (! dir.exists())
			return;
		if (operationType.equals(CREATOR_SAVE) ||
			operationType.equals(CREATOR_OPEN) ||
			operationType.equals(CREATOR_GOLD_OPEN) ||
			operationType.equals(SIMULATOR_NEW))
			BC.setParameter(Params.KEY_LAST_TARIFICATION_FOLDER, dir.getAbsolutePath());
		else if (operationType.equals(SIMULATOR_OPEN) ||
				 operationType.equals(SIMULATOR_SAVE))
			BC.setParameter(Params.KEY_LAST_SIMULATION_FOLDER, dir.getAbsolutePath());
	}

	/**
	 * Return the best path for tarification saving and loading
	 */
	private static File retrieveUserPreferredPath(String operationType) {
		File result = null;
	    int userPref = 
	        ((Integer)BC.getParameter(Params.KEY_OPEN_FOLDER_PREF,
	                Integer.class)).intValue();
	    if (userPref == Params.PREF_LAST_FOLDER) {
	    	// retrieve the last used folder for this kind of operation
	    	if (operationType.equals(SIMULATOR_OPEN) ||
	    		operationType.equals(SIMULATOR_SAVE)) {
	    		String path = 
	    		    BC.getParameterStr(Params.KEY_LAST_SIMULATION_FOLDER);
	    		if (path != null)
	    			result = new File(path);
	    	} else if (operationType.equals(CREATOR_GOLD_OPEN) ||
	    			 operationType.equals(CREATOR_OPEN) ||
					 operationType.equals(CREATOR_SAVE) ||
					 operationType.equals(SIMULATOR_NEW)) {
	    		String path = 
	    		    BC.getParameterStr(Params.KEY_LAST_TARIFICATION_FOLDER);
	    		if (path != null)
	    			result = new File(path);
	    	}
	    }
	    if (result != null)
	    	if (!result.exists())
	    		result = null;
	    if (result == null) // otherwise use default folder
	    	result = defaultSaveDir();
	    return result;
	}

}



abstract class LoadingDialog  implements Secu.Monitor {
	JPanel jpz = null;
	private HashMap monitorMap;
	

	public LoadingDialog() {
		jpz = new JPanel();
	}
	
	 /** 
     * if an error occures, this method is called with the reason
     * message as parameter, and an Error code which is one one of
     * ERROR_XXXX<BR>
     * When an error occure the loading must be finished
     * @param code one of Monitor.ERROR_XXXX
     * @param textual informations about the error
     */
    public final void error(int code,String message,Throwable e) {
        message = 
            "<HTML><B>"+
            Lang.translate("An error occured, operation cannot be completed.")
            +"</B><HR><SMALL>"+message+"</SMALL><HR>"
            +"<SMALL>Guru meditation:"+e+"</SMALL>"
            +"</HTML>";
        ModalDialogBox.alert(jpz,message);
        
        done();
    }
	
	/**
	 * @see Secu.Monitor#setMonitors(java.lang.String[])
	 */
	public void setMonitors(String[] monitors) {
		if (monitorMap != null) return;
		jpz.setLayout(new BoxLayout(jpz,BoxLayout.Y_AXIS));
		monitorMap = new HashMap();
		for (int i = 0 ; i < monitors.length; i++) {
			JPanel jp = new JPanel(new BorderLayout());
			JProgressBar jpb = new JProgressBar();
			jpb.setMinimumSize(new Dimension(20,200));
			
			JLabel jb = new JLabel(Lang.translate(monitors[i]));
			jb.setAlignmentX(Component.LEFT_ALIGNMENT);
			jp.add(jb,BorderLayout.NORTH);
			jp.add(jpb,BorderLayout.CENTER);
			jpz.add(jp);
			
			// add this jpb to the monitors
			monitorMap.put(monitors[i],jpb);
		}
		show();
	}
	
	/** called when needed to show up **/
	abstract void show();

	/**
	 * @see com.simpledata.filetools.Secu.Monitor#valueChange(String monitor, long value,long pos)
	 */
	public void valueChange(String monitor, long value,long pos) {
		if ((monitorMap == null) || (jpz == null)) {
			return;
			} 
		JProgressBar jpb = (JProgressBar) monitorMap.get(monitor);
		if (jpb == null) {
			return;
		} 
		int v = new Long(value).intValue();
		jpb.setIndeterminate(value < 0);
		jpb.setValue(v);
		jpz.repaint();
	}

	/**
	 * @see com.simpledata.filetools.Secu.Monitor#done()
	 */
	public abstract void done();
	
	
	//-------------------- CREATION ----------------------//
	/** create a modal JinternalFrame **/
	public static Secu.Monitor getJInternalFrame(final JFrame owner) {
		final JInternalFrame jif = new JInternalFrame("",true,false);
		jif.setFrameIcon(Resources.iconLoading);
		final ModalJPanel mjp =
			ModalJPanel.warpJInternalFrame(
					jif,owner,new Point(10,60),Resources.modalBgColor);
		jif.pack();
		jif.show();
		LoadingDialog me = new LoadingDialog() {
			void show() {
				jif.pack();
				jif.show();
			}
			public void done() {
				jif.dispose();
				mjp.close();
			}
		};
	
		jif.getContentPane().add(me.jpz,BorderLayout.CENTER);
		
		return me;
	}
	
	/** create a JDialog **/
	public static Secu.Monitor getJDialog(JFrame owner) {
		final JDialog jd = new JDialog(owner,false);
		LoadingDialog me = new LoadingDialog() {
			void show() {
				jd.pack();
				jd.show();
			}
			public void done() {
				jd.dispose();
			}
		};
		
		jd.getContentPane().add(me.jpz,BorderLayout.CENTER);
		return me;
	}
}

/**
 *  $Log: FileManagement.java,v $
 *  Revision 1.2  2007/04/02 17:04:27  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:42  perki
 *  First commit on sourceforge
 *
 *  Revision 1.91  2006/04/05 16:14:30  simple
 *  New License Key for his.ch
 *  and update of building
 *
 *  Revision 1.90  2004/12/17 10:24:27  jvaucher
 *  Tarification cleaned.
 *  Secure autosave
 *  Disabled default TariffEyeInfo
 *
 *  Revision 1.89  2004/12/04 13:17:23  perki
 *  hacked discount bug .. anyway the whole system of display should be reviewed
 *
 *  Revision 1.88  2004/12/04 12:54:54  perki
 *  Corrected problem with autosave
 *
 *  Revision 1.87  2004/11/29 10:07:49  perki
 *  *** empty log message ***
 *
 *  Revision 1.86  2004/11/23 14:39:35  perki
 *  *** empty log message ***
 *
 *  Revision 1.85  2004/11/23 10:37:28  perki
 *  *** empty log message ***
 *
 *  Revision 1.84  2004/11/22 16:30:10  jvaucher
 *  Ticket # 28: Added the properties menu item for the creator, in which you can set the properties of the tarification.
 *  I Also added the propoerties panel into the Publish dialog. Perhaps it's
 *  too big, in this case remove it, like before.
 *
 *  Revision 1.83  2004/11/20 15:11:17  perki
 *  *** empty log message ***
 *
 *  Revision 1.82  2004/11/20 10:56:25  perki
 *  Launcher is now ok
 *
 *  Revision 1.81  2004/11/19 18:02:20  perki
 *  Introducing file associations
 *
 *  Revision 1.80  2004/11/17 12:04:40  perki
 *  Discounts Step 2
 *
 *  Revision 1.79  2004/11/16 18:30:51  carlito
 *  New parameter management ...
 *
 *  Revision 1.78  2004/11/16 15:17:55  jvaucher
 *  Refactor of load / save methods.
 *
 *  Revision 1.77  2004/11/09 12:48:26  perki
 *  *** empty log message ***
 *
 *  Revision 1.76  2004/11/08 16:42:35  jvaucher
 *  - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 *  Revision 1.75  2004/11/08 16:17:16  carlito
 *  Some comment cleaning
 *
 *  Revision 1.74  2004/11/08 16:16:24  carlito
 *  Modifications to handle issue#30 (double clic on asisstant launches two file opener)
 *
 *  Revision 1.73  2004/10/18 16:48:03  perki
 *  *** empty log message ***
 *
 *  Revision 1.72  2004/10/18 07:29:45  jvaucher
 *  Added frame icons. Fixed save method bug.
 *
 *  Revision 1.71  2004/10/18 06:49:53  perki
 *  *** empty log message ***
 *
 *  Revision 1.70  2004/10/17 09:45:54  perki
 *  New save system
 *
 *  Revision 1.69  2004/10/14 13:53:28  perki
 *  *** empty log message ***
 *
 *  Revision 1.68  2004/10/14 11:16:33  perki
 *  Ehanced security in demo mode
 *
 *  Revision 1.67  2004/10/13 16:01:14  perki
 *  *** empty log message ***
 *
 *  Revision 1.66  2004/10/04 17:02:03  perki
 *  New Licenses
 *
 *  Revision 1.65  2004/10/04 15:49:18  carlito
 *  *** empty log message ***
 *
 *  Revision 1.64  2004/10/04 10:10:30  jvaucher
 *  - Minor changes in FileManagement, allowing to choose the dialogType
 *  - Helper skeleton
 *  - Improved rendering of Tarification Report
 *  - Dispatcher bound can yet disable the upper bound
 *
 *  Revision 1.63  2004/10/04 08:47:09  perki
 *  Moved soft info medthods to a new class
 *
 *  Revision 1.62  2004/09/29 16:40:06  perki
 *  Fixef Futures
 *
 *  Revision 1.61  2004/09/29 06:54:25  perki
 *  *** empty log message ***
 *
 *  Revision 1.60  2004/09/28 17:22:58  perki
 *  *** empty log message ***
 *
 *  Revision 1.59  2004/09/28 15:22:18  perki
 *  Pfiuuuu
 *
 *  Revision 1.58  2004/09/28 10:23:08  perki
 *  *** empty log message ***
 *
 *  Revision 1.57  2004/09/28 08:51:31  jvaucher
 *  Changed the behaviour of new simulation opening
 *
 *  Revision 1.56  2004/09/28 08:47:59  jvaucher
 *  Changed the behaviour of new simulation opening
 *
 *  Revision 1.55  2004/09/25 11:47:54  perki
 *  Added a way to find My Documents Folder
 *
 *  Revision 1.54  2004/09/24 10:08:28  perki
 *  *** empty log message ***
 *
 *  Revision 1.53  2004/09/24 00:09:08  kaspar
 *  + Added 'Armored File Format' which is for now just encoding
 *    using XStream (and containing some minor hickups).
 *  + Added Jars that implement XStream library.
 *  ! New SDL
 *  ! Bugfix in MiniBrowser.java; must have been debug code that was
 *    only partially added to CVS
 *
 *  Revision 1.52  2004/09/23 14:45:48  perki
 *  bouhouhou
 *
 *  Revision 1.51  2004/09/23 11:00:48  jvaucher
 *  Improved filechooser rendering
 *
 *  Revision 1.50  2004/09/22 15:46:11  jvaucher
 *  Implemented cleaver load/save system
 *
 *  Revision 1.49  2004/09/22 09:05:56  jvaucher
 *  Fixed some problems with the load/save system
 *
 *  Revision 1.48  2004/09/22 08:23:40  perki
 *  *** empty log message ***
 *
 *  Revision 1.47  2004/09/22 06:47:05  perki
 *  A la recherche du bug de Currency
 *
 *  Revision 1.46  2004/09/21 17:07:03  jvaucher
 *  Implemented load and save preferences
 *  Need perhaps (certainly) to test the case where one refered folder is deleted
 *
 *  Revision 1.45  2004/09/13 15:27:31  carlito
 *  *** empty log message ***
 *
 *  Revision 1.44  2004/09/09 17:24:08  carlito
 *  Creator Gold and Light are there for their first breathe
 *
 *  Revision 1.43  2004/09/03 11:47:53  kaspar
 *  ! Log.out -> log4j first half
 *
 *  Revision 1.42  2004/08/25 13:07:43  kaspar
 *  ! Added names to threads.
 *
 *  Revision 1.41  2004/08/01 09:56:44  perki
 *  Background color is now centralized
 *
 *  Revision 1.40  2004/07/30 15:41:57  carlito
 *  *** empty log message ***
 *
 *  Revision 1.39  2004/07/30 15:18:45  carlito
 *  *** empty log message ***
 *
 *  Revision 1.37  2004/07/27 17:56:29  carlito
 *  *** empty log message ***
 *
 *  Revision 1.36  2004/07/27 16:56:31  perki
 *  *** empty log message ***
 *
 *  Revision 1.35  2004/07/27 09:52:23  carlito
 *  *** empty log message ***
 *
 *  Revision 1.34  2004/07/26 16:46:09  carlito
 *  *** empty log message ***
 *
 *  Revision 1.33  2004/07/22 15:12:34  carlito
 *  lots of cleaning
 *
 *  Revision 1.32  2004/07/20 18:30:36  carlito
 *  *** empty log message ***
 *
 *  Revision 1.31  2004/07/19 20:00:33  carlito
 *  *** empty log message ***
 *
 *  Revision 1.30  2004/07/19 17:39:08  perki
 *  *** empty log message ***
 *
 *  Revision 1.29  2004/07/19 17:35:12  carlito
 *  *** empty log message ***
 *
 *  Revision 1.28  2004/07/15 17:44:38  carlito
 *  *** empty log message ***
 *
 *  Revision 1.27  2004/07/09 20:25:02  perki
 *  Merging UI step 1
 *
 *  Revision 1.26  2004/07/08 15:49:22  perki
 *  User node visibles on trees
 *
 *  Revision 1.25  2004/07/08 14:59:00  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.24  2004/07/08 09:43:20  perki
 *  *** empty log message ***
 *
 *  Revision 1.23  2004/07/07 17:27:09  perki
 *  *** empty log message ***
 *
 *  Revision 1.22  2004/06/28 13:22:37  perki
 *  icons are 16x16 for macs
 *
 *  Revision 1.21  2004/06/21 14:45:06  perki
 *  Now BCTrees are stored into a vector
 *
 *  Revision 1.20  2004/06/21 06:56:23  perki
 *  Loading panel ok
 *
 *  Revision 1.19  2004/06/20 16:22:03  perki
 *  *** empty log message ***
 *
 *  Revision 1.18  2004/06/20 16:09:03  perki
 *  *** empty log message ***
 *
 *  Revision 1.17  2004/06/16 07:49:28  perki
 *  *** empty log message ***
 *
 *  Revision 1.16  2004/06/15 06:13:37  perki
 *  *** empty log message ***
 *
 *  Revision 1.15  2004/05/31 17:08:05  perki
 *  *** empty log message ***
 *
 *  Revision 1.14  2004/05/23 14:08:11  perki
 *  *** empty log message ***
 *
 *  Revision 1.13  2004/05/22 08:39:35  perki
 *  Lot of cleaning
 *
 *  Revision 1.12  2004/05/12 13:38:06  perki
 *  Log is clever
 *
 *  Revision 1.11  2004/04/09 07:16:51  perki
 *  Lot of cleaning
 *
 *  Revision 1.10  2004/03/22 14:32:30  carlito
 *  *** empty log message ***
 *
 *  Revision 1.9  2004/03/18 18:51:52  perki
 *  barbapapa
 *
 *  Revision 1.8  2004/03/17 10:54:45  perki
 *  Thread for params
 *
 *  Revision 1.7  2004/03/16 16:30:11  perki
 *  *** empty log message ***
 *
 *  Revision 1.6  2004/03/13 14:50:39  perki
 *  *** empty log message ***
 *
 *  Revision 1.5  2004/03/12 19:04:15  perki
 *  Monitoring file loading
 *
 *  Revision 1.4  2004/03/12 17:48:06  perki
 *  Monitoring file loading
 *
 *  Revision 1.3  2004/03/08 17:53:18  perki
 *  *** empty log message ***
 *
 *  Revision 1.2  2004/03/06 14:24:50  perki
 *  Tirelipapon sur le chiwawa
 *
 *  Revision 1.1  2004/02/22 10:44:25  perki
 *  File loading and saving
 *
 */