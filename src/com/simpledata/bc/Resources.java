/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * CopyRight Simple Data 2004
 * $Id: Resources.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 * 
 */

/// Basic package of bank comparator. 
package com.simpledata.bc;

import java.awt.Color;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import com.simpledata.win32.Win32EnvironementVariable;

/**
 * The resources class is a toolbox providing access to 
 * global variables. These variables include default 
 * locations for most files that are important to the
 * running of Tarif Eye. 
 * Another job of Resources is to load and keep the instances
 * of all icons and images used during the lifetime of the
 * application. 
 */
public class Resources {
	private static final Logger m_log = Logger.getLogger( Resources.class ); 
	
	/// s holds the platform path separator. 
	private static String s= File.separator;
	/// System path separator. 
	public final static String PATHSEPARATOR = File.separator;  // TODO: replace s with this
	

	/**
	 * Return the path to resources base. 
	 */
	public static String resourcePath() {
		return "resources";
	}
	public static String dictionariesPath() {
		return resourcePath() + s + "dicts" + s;
	}
	public static String imagesPath() {
		return resourcePath() + s + "images" + s;
	}
	public static String appIconPath() {
		return imagesPath() + s + "appIcon.png";
	}
	public static String splashImagePath() {
		return imagesPath() + s + "Splash.png";
	}
	public static String splashSmallImagePath() {
		return imagesPath() + s + "SplashSmall.png";
	}
	
	public static String htdocsPath() {
		return resourcePath() + s + "htdocs"+s;
	}
	public static String log4jpropsPath() {
		return resourcePath() + s + "log4j.xml"; 
	}
	
	/**
	 * Return the path to the report templates. 
	 */
	public static String reportsPath() {
		// s contains the path separator for the platform. 
		return resourcePath() + s + "reports" + s + "templates" + s; 
	}

	public static String dataPath() {
		return ".bcdata";
	}
	public static String fileToOpenSpoolPath() {
		return dataPath() + s + "spool"+s;
	}
	public static String parametersPath() {
		return dataPath() + s + "bc";
	}
	public static String parametersBackupPath() {
		return parametersPath() +".bak";
	}
	public static String mergingMemoryPath() {
		return dataPath() + s + "mm";
	}
	
	/** the default license if none found **/
	public static String licenseDefaultPath() {
		return resourcePath() + s + "license.default";
	}
	
	// Reports paths
	
	/** SimpleData logo path */
	//public static final String simpleDataLogoPath = 
	//	imagesPath() + "sd_logo_1024_256.png";
	
	/** SimpleData ghost logo path */
	//public static final String simpleDataGhostLogoPath = 
	//	imagesPath() + "sd_logo_ghost_1024_256.png";
	
	/** TariffEye ghost logo path */
	public static final String tariffEyeGhostLogoPath = 
		imagesPath() + "te_logo_ghost_1024_256.png";
	
	/** Check box path */
	public static final String checkBoxPath = 
		imagesPath() + "checkBox" + s + "checked.gif";
	
	/** 
     * the file that contains the user data.<BR>
     * see the variable I4J_LICENCE_FILE in ant's build.xml,
     * or LICENCE_FILE user var in install4j configuration
     */
	public static String licenseJar() {
		return ".install4j"+s+"user.jar";
	}
	
	/**
	 * Return the path relative to program start location
	 * that contains build-in tarification data files.  
	 */
	public static String defaultTarificationLibraryPath() {
		return "tarifications";
	}

	/**
	 * Returns the location of the blank tariff
	 * TODO change this location before release
	 * TODO eventually hide this file in the common file browser
	 */
	public static File getBlankTariffLocation() {
	    return new File(resourcePath()+
	            File.separator+"blank");
	}
	
	// FONT
	/** the size of a small font **/
	public static float fontSmall() {
	    return fontNorm() - 2;
	}
	/** the size of a big font **/
	public static float fontBig() {
	    return fontNorm() + 2;
	}
	private static float normFont = -1;
	/** the size of a normal font **/
	public static float fontNorm() {
	   if (normFont > 0 ) return normFont;
	   // extrat font size froma label
	   normFont = (new JLabel()).getFont().getSize2D();
	   return normFont;
	};
	
	// Colors
	/** the color used to shade the background when popuping modal panels **/
	public static Color modalBgColor = new Color(0.8f,0.8f,0.8f,0.4f);
	/** The color used by the UI object to show a field isn't editable */
	public static Color disabledFieldBg = new Color(0.9f,0.9f,0.9f);
	
	// app icon
	public static ImageIcon appIcon128128;
	// app icon
	public static ImageIcon appIcon1616;
	
	// lonely pixel
	public static ImageIcon pixel;
	
	// ImageIcons used for tarification display
	public static ImageIcon stdTarificationLeafIcon= null;
	public static ImageIcon stdTarificationNotLeafIcon= null;

	// ImageIcons used for different tree nodes display
	public static ImageIcon stdTreeBaseIcon= null;
	public static ImageIcon stdTreeLocIcon= null;
	public static ImageIcon stdTreeUserIcon= null;
	public static ImageIcon stdTarifIcon= null;
	public static ImageIcon stdTarifReference = null;
	

	// Tags
	public static ImageIcon stdTagWarning;
	public static ImageIcon stdTagError;
	public static ImageIcon stdTagMore;
	public static ImageIcon stdTagTarifed;
	public static ImageIcon stdTagInterest;
	public static ImageIcon stdTagUser;
	public static ImageIcon stdTagPaired;
	public static ImageIcon stdTagNotPaired;
	
	// ImageIcons for workSheet Trees display
	public static ImageIcon stdIconDispatcher;
	public static ImageIcon stdIconWorkplace;
	public static ImageIcon wsMagnifier;
	
	// WorkSheets
	public static ImageIcon wsDefaultWorkSheet;
	public static ImageIcon wsDefaultWorkPlace;
	public static ImageIcon wsDefaultDispatcher;
	public static ImageIcon wsDispatcherCase;
	public static ImageIcon wsDispatcherIf;
	public static ImageIcon wsDispatcherSequence;
	public static ImageIcon wsDispatcherSimple;
	public static ImageIcon wsDispatcherBounds;
	public static ImageIcon wsEmptyWorkSheet;
	public static ImageIcon wsRootDispatcher;
	public static ImageIcon wsWorkPlace;
	public static ImageIcon wsWorkPlaceWithOptions;
	public static ImageIcon wsWorkPlaceWithOnlyOptions;
	public static ImageIcon wsWorkPlaceTrRateBySlice;
	public static ImageIcon wsWorkPlaceRateOnAmount;
	public static ImageIcon wsWorkPlaceFixedFee;

	// ImageIcons for DispatcherSequencer
	public static ImageIcon moveUP;
	public static ImageIcon moveDown;
	public static ImageIcon moveLeft;
	public static ImageIcon moveRight;
	
	// ImageIcon for DispatcherBound
	public static ImageIcon infinityIcon;
	
	// ImageIcon for splitPane
	public static ImageIcon splitHorizontal;
	public static ImageIcon showHide;

	// ImageIcons for options
	public static ImageIcon greenBall;
	public static ImageIcon redBall;
	
	// arrows
	public static ImageIcon arrowBoth;
	public static ImageIcon arrowLeft;
	public static ImageIcon arrowRight;
	
	// futures
	public static ImageIcon futureOpenClose;
	public static ImageIcon futureOpen;
	public static ImageIcon futureClose;
	
	// discount
	public static ImageIcon reductionTag;
	public static ImageIcon reductionButton;
	
	// textual signs
	public static ImageIcon signInfinite;
	
	// UI icons
	public static ImageIcon iconNew;
	public static ImageIcon iconSave;
	public static ImageIcon iconEdit;
	public static ImageIcon iconTrashEmpty;
	public static ImageIcon iconTrashFull;
	public static ImageIcon iconCopy;
	public static ImageIcon iconPaste;
	public static ImageIcon iconSum;
	public static ImageIcon iconExpand;
	public static ImageIcon iconCollapse;
	public static ImageIcon iconDelete;
	public static ImageIcon iconLoading;
	public static ImageIcon iconOpenFile;
	public static ImageIcon iconView;
	public static ImageIcon iconLaunch;
	public static ImageIcon iconPlus;
	public static ImageIcon iconMinus;
	public static ImageIcon iconOK;
	public static ImageIcon iconSettings;
	public static ImageIcon iconSelect;
	public static ImageIcon iconLockClosed;
	public static ImageIcon iconLockOpened;
	public static ImageIcon iconMagicWand;
	public static ImageIcon iconLink;
	public static ImageIcon iconUnlink;
	public static ImageIcon arrowUp;
	public static ImageIcon arrowDown;
	public static ImageIcon iconTransferOptions;
	public static ImageIcon iconSimulation;
	public static ImageIcon iconReporting;
	public static ImageIcon iconHelp;
	public static ImageIcon iconHome;

	/// checkBoxIcons
	public static ImageIcon checkBoxCheckable;
	/// checkBoxIcons
	public static ImageIcon checkBoxCheckableNot;
	/// checkBoxIcons
	public static ImageIcon checkBoxStateChecked;
	/// checkBoxIcons
	public static ImageIcon checkBoxStateInduced;
	/// checkBoxIcons
	public static ImageIcon checkBoxStatePartially;
	/// checkBoxIcons
	public static ImageIcon checkBoxStateCheckedNot;
	
	/// icons for ComboTabbedPane
	public static ImageIcon tabbedPaneCheckedSelected;
	/// icons for ComboTabbedPane
	public static ImageIcon tabbedPaneCheckedNotSelected;
	
	// Icons for applications
	/// Icon for TarificationCreator
	public static ImageIcon iconTools;
	/// Icon for console
	public static ImageIcon iconConsole;
	/// Icon for currency manager
	public static ImageIcon iconCurrencyManager;
	/// Icon for standard Windows
	public static ImageIcon iconStdWindow;
	
	
	/// Icons for tarifications
	public static ImageIcon tarificationDefaultIcon3232;

	
	
	
	// BIG icons 
	/// icon for preference panel
	public static ImageIcon preferencePanelIcon;
	
	//// Images
	// import
	public static ImageIcon importArrow;
	
	/**
	 * Load the resources that are held statically in this
	 * class. This is to avoid creation and loading multiple
	 * times. 
	 */
	public static void loadResources() {
		// AppIcon
		appIcon1616 = getIcon("","appIcon1616.png");
		appIcon128128 = getIcon("","appIcon128128.png");
		
		//	ImageIcons used for tarification display
		stdTarificationLeafIcon= getIcon("BCNodes", "folder_single.gif");
		stdTarificationNotLeafIcon= getIcon("BCNodes", "folder_many.gif");
		
		//	ImageIcons used for different tree nodes display
		stdTreeBaseIcon= getIcon("BCNodes", "base.gif");
		stdTreeLocIcon= getIcon("BCNodes", "loc.gif");
		stdTreeUserIcon= getIcon("BCNodes", "user.gif");
		stdTarifIcon= getIcon("BCNodes", "tarif.gif");
		stdTarifReference= getIcon("BCNodes", "reference.gif");
		
		// Tags
		stdTagWarning= getIcon("BCNodes",  "tagWarning.gif");
		stdTagError= getIcon("BCNodes","tagForbiden.gif");
		stdTagTarifed= getIcon("BCNodes", "tagTarifed.gif");
		stdTagMore= getIcon("BCNodes", "tagMore.gif");
		stdTagInterest = getIcon("BCNodes", "tagInterest.gif");
		stdTagUser = getIcon("BCNodes", "tagUser.gif");
		stdTagPaired = getIcon("BCNodes", "tagPaired.gif");
		stdTagNotPaired = getIcon("BCNodes", "tagNotPaired.gif");
		
		// ImageIcons for DispatcherSequencer
		stdIconDispatcher= getIcon("WSNodes", "dispatcher.gif");
		stdIconWorkplace= getIcon("WSNodes", "workplace.gif");
		wsMagnifier= getIcon("WSPanels", "Magnifier.gif");
		
		//	WorkSheets
		wsDefaultWorkSheet= getIcon("WSPanels", "DefaultWorkSheet.gif");
		wsDefaultDispatcher= getIcon("WSPanels", "DefaultDispatcher.gif");
		wsDefaultWorkPlace= getIcon("WSPanels", "DefaultWorkPlace.gif");
		wsDispatcherCase= getIcon("WSPanels", "DispatcherCase.gif");
		wsDispatcherIf= getIcon("WSPanels", "DispatcherIf.gif");
		wsDispatcherSequence= getIcon("WSPanels", "DispatcherSequence.gif");
		wsDispatcherSimple= getIcon("WSPanels", "DispatcherSimple.gif");
		wsDispatcherBounds = getIcon("WSPanels", "DispatcherBounds.gif");
		wsEmptyWorkSheet= getIcon("WSPanels", "EmptyWorkSheet.gif");
		wsRootDispatcher= getIcon("WSPanels", "RootDispatchers.gif");
		wsWorkPlace= getIcon("WSPanels", "WorkPlace.gif");
		wsWorkPlaceWithOptions= getIcon("WSPanels", "WorkPlaceWithOptions.gif");
		wsWorkPlaceFixedFee=
			getIcon("WSPanels", "WorkPlaceFixedFee.gif");
		wsWorkPlaceWithOnlyOptions=
			getIcon("WSPanels", "WorkPlaceWithOnlyOptions.gif");
		wsWorkPlaceTrRateBySlice=
			getIcon("WSPanels", "WorkPlaceTrRateBySlice.gif");
		wsWorkPlaceRateOnAmount=
			getIcon("WSPanels", "WorkPlaceRateOnAmount.gif");

		splitHorizontal = getIcon("balls","splitHoriz.gif");
		showHide = getIcon("icons", "showHide.gif");
		
		//		ImageIcons for options
		redBall= getIcon("balls", "redball.gif");
		greenBall= getIcon("balls", "greenball.gif");
		
		// arrows
		arrowBoth = getIcon("balls", "arrow_both.gif");
		arrowLeft = getIcon("balls", "arrow_left.gif");
		arrowRight = getIcon("balls", "arrow_right.gif");
		reductionTag = getIcon("balls", "reductionTag.gif");
		reductionButton = getIcon("balls", "reductionButton.gif");

		// textual signs
		signInfinite = getIcon("images","infinite.gif");
		
		// futures
		futureOpenClose = getIcon("icons", "onOpenAndClose.gif");
		futureOpen = getIcon("icons", "onOpen.gif");
		futureClose = getIcon("icons", "onClose.gif");
		
		//	UI icons
		iconSettings= getIcon("icons", "settings.gif");
		iconNew= getIcon("icons", "new.gif");
		iconSave= getIcon("icons", "save.gif");
		iconEdit= getIcon("icons", "edit.gif");
		iconCopy= getIcon("icons", "copy.gif");
		iconOK= getIcon("icons", "ok.gif");
		iconPaste= getIcon("icons", "paste.gif");
		iconSum= getIcon("icons", "sum.gif");
		pixel= getIcon("icons", "pixel.gif");
		iconExpand= getIcon("icons", "expand.gif");
		iconCollapse= getIcon("icons", "collapse.gif");
		iconDelete= getIcon("icons", "delete.gif");
		iconView= getIcon("icons", "view.gif");
		iconLoading = getIcon("icons", "loading.gif");
		iconOpenFile = getIcon("icons", "open_file.gif");
		iconLaunch= getIcon("icons", "run.gif");
		iconTrashEmpty = getIcon("icons","trash_empty.gif");
		iconTrashFull = getIcon("icons","trash_full.gif");
		moveDown= getIcon("icons", "moveDown.gif");
		moveUP= getIcon("icons", "moveUp.gif");
		moveLeft= getIcon("icons", "moveLeft.gif");
		moveRight= getIcon("icons", "moveRight.gif");
		iconPlus= getIcon("icons", "plus.gif");
		iconMinus= getIcon("icons", "minus.gif");
		iconSelect= getIcon("icons", "select.gif");
		iconLockClosed= getIcon("icons", "lock_closed.gif");
		iconLockOpened= getIcon("icons", "lock_opened.gif");
		iconMagicWand = getIcon("icons", "magic_wand.gif");
		iconLink = getIcon("icons", "link.gif");
		iconUnlink = getIcon("icons", "unlink.gif");
		arrowUp = getIcon("icons", "arrowUp.gif");
		arrowDown = getIcon("icons", "arrowDown.gif");
		iconTransferOptions = getIcon("icons", "transferOptions.gif");
		iconSimulation = getIcon("icons", "simulation.gif");
		iconReporting = getIcon("icons", "reporting.gif");
		iconHelp = getIcon("icons", "help.gif");
		iconHome = getIcon("icons", "home.png");
		
		// checkBoxIcons
		checkBoxCheckable= getIcon("checkBox", "checkable.gif");
		checkBoxCheckableNot= getIcon("checkBox", "checkableNot.gif");
		checkBoxStateChecked= getIcon("checkBox", "checked.gif");
		checkBoxStateCheckedNot= getIcon("checkBox", "checkedNot.gif");
		checkBoxStateInduced= getIcon("checkBox", "induced.gif");
		checkBoxStatePartially= getIcon("checkBox", "partially.gif");
		
		// Icons for applications
		// Icon for TarificationCreator
		iconTools = getIcon("icons", "tools.gif");
		// Icon for Console
		iconConsole = getIcon("icons", "console.gif");
		// Icons for Currency Manager
		iconCurrencyManager = getIcon("icons", "currency.gif");
		
		// Icon for standard windows
		iconStdWindow = getIcon("icons" , "stdWindow.gif");
		
		// icons for ComboTabbedPane
		tabbedPaneCheckedSelected = 
			getIcon("ComboTabbedPane", "checkedSelected.gif");
		tabbedPaneCheckedNotSelected = 
			getIcon("ComboTabbedPane", "checkedNotSelected.gif");
		
		// icons for tarifications
		tarificationDefaultIcon3232 = getIcon("banksLogos","default.png");
		
		// BIG icons 
		// icon for preferenc panel 75x58
		preferencePanelIcon = getIcon("icons","prefPanel.gif");
		
		//// Images
		// import
		importArrow = getIcon("images","importArrow.png");
		infinityIcon = getIcon("images","infinite.gif");
	}

	public static ImageIcon getIcon(String subfolder, String imageName) {
		try {
			return new ImageIcon(imagesPath() + s + subfolder + s + imageName);
		} catch (Exception e) {
			m_log.error( "Problem while loading standard Icons", e );
		}
		return null;
	}
    public static String findMyDocumentFolder() {
    	// TODO verify stability of this solution
        // jette un oeil a sdl/com.simpledata.win32.Win32EnvironementVariable
        
    	String os = System.getProperty("os.name");
    	if (os.substring(0,3).toLowerCase().equals("win")) {
    		String res = 
    		    Win32EnvironementVariable.getCurrentUserPersonalFolderPath();
    		
    		if (res != null) {
    		    return res;
    		} 
    		m_log.debug("Cannot find My document on:"+
    		        System.getProperty("os.name"));
    		
    	} 
    	return System.getProperty("user.home");
    }

}

/*
 * $Log: Resources.java,v $
 * Revision 1.2  2007/04/02 17:04:28  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.82  2004/11/26 15:43:31  perki
 * *** empty log message ***
 *
 * Revision 1.81  2004/11/26 14:31:47  jvaucher
 * TariffEyeInfo, replaced by the TariffEyeQuery class for generalisation purpose
 *
 * Revision 1.80  2004/11/26 10:06:00  jvaucher
 * Begining of TariffEyeInfo feature
 *
 * Revision 1.79  2004/11/22 09:13:19  jvaucher
 * Changes on tarification reports: Added check box for options.
 *
 * Revision 1.78  2004/11/19 18:02:20  perki
 * Introducing file associations
 *
 * Revision 1.77  2004/11/18 13:33:39  jvaucher
 * Ticket # 55 : Added TariffEye logo into Fee reports
 *
 * Revision 1.76  2004/11/16 17:22:11  perki
 * Merging now remembers of last picks
 *
 * Revision 1.75  2004/11/16 07:08:17  perki
 * Now license is loaded from the jar file directly
 *
 * Revision 1.74  2004/11/08 13:45:30  carlito
 * New pairing nodes color policy
 *
 * Revision 1.73  2004/10/20 08:19:39  perki
 * *** empty log message ***
 *
 * Revision 1.72  2004/10/15 17:54:43  carlito
 * Added iconReporting and iconHelp
 *
 * Revision 1.71  2004/10/13 16:01:14  perki
 * *** empty log message ***
 *
 * Revision 1.70  2004/10/04 10:10:31  jvaucher
 * - Minor changes in FileManagement, allowing to choose the dialogType
 * - Helper skeleton
 * - Improved rendering of Tarification Report
 * - Dispatcher bound can yet disable the upper bound
 *
 * Revision 1.69  2004/10/01 10:02:16  carlito
 * Infinite sign added to resources
 *
 * Revision 1.68  2004/09/28 15:22:18  perki
 * Pfiuuuu
 *
 * Revision 1.67  2004/09/25 11:47:54  perki
 * Added a way to find My Documents Folder
 *
 * Revision 1.66  2004/09/23 08:21:26  perki
 * removed all the code relative to the WebServer
 *
 * Revision 1.65  2004/09/22 15:39:55  carlito
 * Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 * Revision 1.64  2004/09/21 17:07:03  jvaucher
 * Implemented load and save preferences
 * Need perhaps (certainly) to test the case where one refered folder is deleted
 *
 * Revision 1.63  2004/09/14 15:52:58  carlito
 * *** empty log message ***
 *
 * Revision 1.62  2004/09/14 14:46:29  perki
 * *** empty log message ***
 *
 * Revision 1.61  2004/09/14 13:06:32  perki
 * *** empty log message ***
 *
 * Revision 1.60  2004/09/14 12:07:26  carlito
 * commit de protection
 *
 * Revision 1.59  2004/09/13 15:27:31  carlito
 * *** empty log message ***
 *
 * Revision 1.58  2004/09/07 16:21:03  jvaucher
 * - Implemented the DispatcherBounds to resolve the feature request #24
 * The calculus on this dispatcher is not yet implemented
 * - Review the feature of auto select at startup for th SNumField
 *
 * Revision 1.57  2004/09/07 09:59:23  kaspar
 * ! Log4j now uses DOMConfigurator for better configurability.
 *   View the resources/log4j.xml file for runtime config changes.
 *
 * Revision 1.56  2004/09/04 18:26:45  kaspar
 * + resource/log4j.properties controls the Log4j subsystem
 *   configuration.
 *
 * Revision 1.55  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.54  2004/08/02 10:44:51  carlito
 * *** empty log message ***
 *
 * Revision 1.53  2004/08/01 18:00:59  perki
 * *** empty log message ***
 *
 * Revision 1.52  2004/08/01 12:23:08  perki
 * Better show/hide extra parameter
 *
 * Revision 1.51  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.50  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 * Revision 1.49  2004/07/30 11:47:52  carlito
 * *** empty log message ***
 *
 * Revision 1.48  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.47  2004/07/19 12:25:02  perki
 * Merging finished?
 *
 * Revision 1.46  2004/07/16 09:25:31  kaspar
 * * Loading of JasperReports from Design: TemplateFactory
 * * Added path to resources: reportsPath
 *
 * Revision 1.45  2004/07/15 17:44:38  carlito
 * *** empty log message ***
 *
 * Revision 1.44  2004/07/08 15:49:21  perki
 * User node visibles on trees
 *
 * Revision 1.43  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.42  2004/06/18 18:25:39  perki
 * *** empty log message ***
 *
 * Revision 1.41  2004/06/06 17:28:09  perki
 * *** empty log message ***
 *
 * Revision 1.40  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.39  2004/05/23 14:08:11  perki
 * *** empty log message ***
 *
 * Revision 1.38  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.37  2004/05/22 17:20:46  perki
 * Reducs are visibles
 *
 * Revision 1.36  2004/05/14 11:48:27  perki
 * *** empty log message ***
 *
 * Revision 1.35  2004/05/14 08:46:18  perki
 * *** empty log message ***
 *
 * Revision 1.34  2004/05/05 12:38:13  perki
 * Plus FixedFee panel
 *
 * Revision 1.33  2004/04/12 15:32:31  carlito
 * *** empty log message ***
 *
 * Revision 1.32  2004/03/22 21:40:19  perki
 * dodo
 *
 * Revision 1.31  2004/03/18 09:02:29  perki
 * *** empty log message ***
 *
 * Revision 1.30  2004/03/17 11:24:14  carlito
 * *** empty log message ***
 *
 * Revision 1.29  2004/03/16 14:09:31  perki
 * Big Numbers are welcome aboard
 *
 * Revision 1.28  2004/03/15 15:46:56  carlito
 * *** empty log message ***
 *
 * Revision 1.27  2004/03/12 02:52:51  carlito
 * *** empty log message ***
 *
 * Revision 1.26  2004/03/08 15:40:48  perki
 * *** empty log message ***
 *
 * Revision 1.25  2004/03/08 09:02:20  perki
 * houba houba hop
 *
 * Revision 1.24  2004/03/08 08:46:02  perki
 * houba houba hop
 *
 * Revision 1.23  2004/03/04 14:32:07  perki
 * copy goes to hollywood
 *
 * Revision 1.22  2004/03/03 16:52:07  carlito
 * *** empty log message ***
 *
 * Revision 1.21  2004/03/03 15:04:18  carlito
 * *** empty log message ***
 *
 * Revision 1.20  2004/03/02 17:59:15  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.19  2004/03/02 14:42:47  perki
 * breizh cola. le cola du phare ouest
 *
 * Revision 1.18  2004/03/02 00:32:54  carlito
 * *** empty log message ***
 *
 * Revision 1.17  2004/02/26 13:29:01  perki
 * Mais la terre est carree
 *
 * Revision 1.16  2004/02/26 13:24:34  perki
 * new componenents
 *
 * Revision 1.15  2004/02/22 10:43:56  perki
 * File loading and saving
 *
 * Revision 1.14  2004/02/20 05:45:05  perki
 * appris un truc
 *
 * Revision 1.13  2004/02/19 16:21:25  perki
 * Tango Bravo
 *
 * Revision 1.12  2004/02/18 16:59:29  perki
 * turlututu
 *
 * Revision 1.11  2004/02/18 13:37:51  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/02/18 11:00:56  perki
 * *** empty log message ***
 *
 * Revision 1.9  2004/02/17 15:55:02  perki
 * zobi la mouche n'a pas de bouche
 *
 * Revision 1.8  2004/02/16 13:07:53  perki
 * new event model
 *
 * Revision 1.7  2004/02/14 21:53:26  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/02/06 07:44:55  perki
 * lot of cleaning in UIs
 *
 * Revision 1.5  2004/02/05 07:45:52  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/02/04 18:10:05  carlito
 * zorglub
 *
 * Revision 1.3  2004/02/04 17:46:44  carlito
 * added Icons to resources
 *
 * Revision 1.2  2003/12/05 16:41:45  perki
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 15:53:39  perki
 * start
 *
 */