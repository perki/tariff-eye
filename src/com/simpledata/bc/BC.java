/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc;


import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.TString;
import com.simpledata.bc.merging.MergingMemory;
import com.simpledata.bc.tools.BCCurrencyManager;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.BCLookAndFeel;
import com.simpledata.bc.uitools.InternalFrameDescriptor;
import com.simpledata.bc.uitools.Splash;
import com.simpledata.bc.webcontrol.ControlServer;
import com.simpledata.filetools.CopyFile;
import com.simpledata.filetools.Secu;
import com.simpledata.filetools.SimpleException;
import com.simpledata.sdl.log.Log4jInitializer;
  
/**
 * This is the main Bank Comparator class. It launches a splash
 * screen and then the desktop behind it. 
 *
 * @version $Id: BC.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public class BC {
	//public final static String VERSION= "1.0";
	public final static String COPYRIGHT=
		"Copyright Simple Data & BearBull, 2003";

	public final static long splashTime= 4000; // in miliseconds
		
	public final static Logger m_log = Logger.getLogger( BC.class );

		/** Global instance of BC, accessible by all. */
	public static BC bc;  
	// XXX refactor: must this be public ? 
	public static Lang langManager; 

	
	/** Timer to schedule tasks */
	public final Timer m_timer;
	
	/** Randomizer */
	public final Random m_randomizer; // XXX never used. Can be removed
	
	public static Params params;
	private static ParametersSaver parameterSaver;
	private static BCCurrencyManager currencyManager;
	
	/** actual desktop **/
	private Desktop desktop1;

	private ControlServer controlServer; 
	
	/** splash screen **/
	private Splash splashScreen;
	
	/**
	 * Starting point
	 */
	public static void main(String[] args) {
	    Log4jInitializer.doInit(Resources.log4jpropsPath());
		//Log4jInitializer.doInit();
	    if (args.length == 1) {
	        SoftInfos.VERSION = args[0];
	    }
	    
		new BC();
	    //STree.main(args);
	}

	public BC() {
	    m_log.debug("Starting Tariff Eye:"+SoftInfos.VERSION);
	    BC.bc= this;
	    
	    m_timer = new Timer();
	    m_randomizer = new Random();
	    Secu.setPreferredMethod(Secu.METHOD_XML_COMPRESS_DES);
	    
		// Splash screen
		splashScreen  = new Splash();
		setMajorComponent(splashScreen);
		try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    splashScreen.toFront();   
                }});
        } catch (InterruptedException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InvocationTargetException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
		
        splashScreen.setStep("Initializing Logger");
		
		
		// Start the Parameter Saver
		parameterSaver = new ParametersSaver();
		parameterSaver.start();
		
		// Load specific ressources such as Icons
		splashScreen.setStep("Loading ressources");
		Resources.loadResources();
		
		
		
		
		splashScreen.setStep("Starting control server");
		getControlServer();
		
		splashScreen.setStep("Initialization of workspace");
		// Init parameters of work space
		initWorkingSpace();
		splashScreen.setInfo(getParameterStr("companyName"));

		// Set the proxy params
		boolean proxyState = 
			((Boolean)getParameter(Params.KEY_USE_PROXY)).booleanValue();
		setProxyEnable(proxyState);
		
		currencyManager = new BCCurrencyManager();
		
		// set Look And Feel
		BCLookAndFeel.setLAF();

		//		Languages management
		BC.langManager= new Lang();
		langManager.setLang(getParameterStr("lang"));
		TString.setLang(langManager.getLang());
		
		
		splashScreen.setStep("Creating Desktop");
		try {
            //Schedule a job for the event-dispatching thread:
            //creating and showing this application's GUI.
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            	public void run() {
            		desktop1= Desktop.create();
            		desktop1.setVisible(false);
            		
            	}
            });
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
		
		
		// Launch splash waiter thread
		SplashWaiterThread swt = 
		    new SplashWaiterThread(splashScreen, splashTime);
		swt.start();
		
		try {
		SwingUtilities.invokeAndWait(new Runnable() {

			public void run() {
				desktop1.setVisible(true);
			}});
		} catch (Exception e) {
			m_log.error("Problem when showing desktop.",e);
		}
		setMajorComponent(desktop1);
		splashScreen.toFront();
		splashScreen.setStep("");
		while (splashScreen.isVisible()) {
		    try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				m_log.error( "BC:awakened prematurely", e );
			}
		}
		
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(
			new HtmlBrowserLauncher( desktop1 )
			
		);
		
		// check for crashed session, recover autosave files.
		desktop1.checkCrash();
		
		// launch the auto-opener
		Launcher.startFileToOpenSpooler();
	}

	
	public static void setProxyEnable(boolean enable) {
		Properties prop = System.getProperties();
		String proxyHost = getParameterStr(Params.KEY_PROXY_HOST);
		String proxyPort = getParameterStr(Params.KEY_PROXY_PORT);
		prop.put("proxySet", (enable)?"true":"false");
		prop.put("http.proxyHost", (enable)?proxyHost:"");
		prop.put("http.proxyPort", (enable)?proxyPort:"");
	}
	
	/**
	 * alert the user<BR>
	 * Normally will popup a small window and the appropriate icon
	 * depending on the choosen type<BR>
	 */
	public void alertUser(String message,String title) {
	    JFrame jf = getMajorComponent();
	    boolean close = false;
	    if (jf == null) {
	        jf = new JFrame();
	        jf.pack();
	        jf.setVisible(true);
	        close = true;
	    }
	    
		JOptionPane.showMessageDialog(jf,
	    message,title,JOptionPane.WARNING_MESSAGE);
		
		if (close) {
		    jf.dispose();
		}
		
	}
	
	
	private JFrame majorComponent;
	/**
	* set the major componenet
	*/
	private void setMajorComponent(JFrame jf) {
		majorComponent = jf;
	}
	
	/**
	* get the major (normally desktop component may be the SplashScreen)
	*/
	public JFrame getMajorComponent() {
		return majorComponent;
	}
	
	/**
	 * get all the BC.TarificationModifiers actually on.<BR>
	 * Those TarificationModifiers are : Tarification Creator, Simulators
	 */
	public TarificationModifiers[] getAllTarificationModifiers() {
		Iterator i = desktop1.getDesktopPane().getRegisteredFrames().iterator();
		ArrayList/*<TarificationModifiers>*/ result 
				= new ArrayList/*<TarificationModifiers>*/();
		
		Object o ;
		while (i.hasNext()) {
			o = i.next();
			if (o instanceof TarificationModifiers)
				result.add(o);
		}
		return (TarificationModifiers[]) 
				result.toArray(new TarificationModifiers[0]);
	}
	
	/**
	 * an interface to Tag all components that contains tarifications<BR>
	 * (used by getAllTarificationModifiers())
	 */
	public interface TarificationModifiers {
		
		/** return the Tarifications at work **/
		public List tarifModifierGetTarifications();
		
		/** return the title to identify me **/
		public String tarifModifierGetTitle();
		
	}
	
	/**
	* Popup Component (mainly Internal Frame) in actual desktop
	*/
	public void popupJIFrame(JInternalFrame jif) {
		popupJIFrame(jif, null);
	}

	/**
	 * Popup JInternalFrame at specified location<br>
	 * Then select this frame
	 */
	public void popupJIFrame(JInternalFrame jif, int posX, int posY) {
		InternalFrameDescriptor ifd = new InternalFrameDescriptor();
		ifd.setInitialBounds(new Rectangle(
			posX, posY,
			jif.getWidth(), jif.getHeight()
		));
		
		popupJIFrame(jif, ifd);
	}

	/**
	 * Popup a JInternalFrame using a behavior descriptor
	 * @param jif
	 * @param ifd behavior descriptor (size, positioning etc...)
	 */
	public void popupJIFrame(JInternalFrame jif, InternalFrameDescriptor ifd) {
		if (jif == null) {
			m_log.error( "Call " +
				"to popupJIFrame(JInternalFrame jif, InternalFrameDescriptor ifd) " +
				"with a null JInternalFrame");
			return;
		}
		jif.setVisible(false);
		InternalFrameVisibilityThread thread = 
			new InternalFrameVisibilityThread(jif, desktop1.getDesktopPane());
		thread.start();
		desktop1.getDesktopPane().add(jif, ifd);
		//desktop1.getDesktopPane().add(jif);
		jif.setVisible(true);	
		desktop1.getDesktopPane().setSelectedFrame(jif);	    
	}
	
	class InternalFrameVisibilityThread extends Thread {
		private JInternalFrame jif;
		private JDesktopPane desktop;
		
		public InternalFrameVisibilityThread(JInternalFrame j, JDesktopPane des) {
			this.desktop = des;
			this.jif = j;
		}
		
		public void run() {
    		
			try {
				while(!jif.isVisible()) {
					sleep(100);
				}                    
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Dimension d = desktop.getSize();
			desktop.setSize(d);
    }
		
	}
	

	// ---------------------------------------------------------

	/**
	* Save everything an exit
	*/
	public static void exit() {
		commitChanges();
		System.exit(0);
	}

	/**
	* handle errors
	*/
//	public static void myError(String s) {
//		m_log.error( s );
//	}

	// ------------------------------------------------------------
	// --                   PARAMETERS MANAGEMENT             -----
	// ------------------------------------------------------------

	/**
	 * A Thread that saves the parameters when needed
	 */
	class ParametersSaver extends Thread {
		private boolean something;
		private long TIME_WAIT_IN_MILLIS= 20000;
		
		public ParametersSaver() {
			super("Parameter Saver");
		}
		
		public void run() {
			while (true) {
				try {
					sleep(TIME_WAIT_IN_MILLIS);
				} catch (InterruptedException e) {
					m_log.warn( "Param saver thread prob", e );
				}
				if (something) {
					synchronized (this) {
						saveNow();
					}
				}
			}
			
		}

		public synchronized void save() {
				if (something) return;
				something = true;
		}

		public synchronized void saveNow() {
		    try {
		        File p = new File(Resources.parametersPath());
		        Secu.save( null, params, p, null);
		        something = false;
		        //copy bc parameter to a bakup file
		        File bak = new File(Resources.parametersBackupPath());
		        CopyFile.to(p,bak);
		        
		    } catch (SimpleException e) {
				m_log.error( "Error saving parameters", e );
			}
			m_log.info( "Saved Tarif Eye parameters." );
		}
	}

	/**
	* set a parameter
	*/
	public static synchronized void setParameter(String key, Object param) {
	    if (key == null) {
	        m_log.warn("Called with a null key:"+param);
	    }
	    if (param != null) {
			params.put(key, param);
			//m_log.debug( "saved " + key + " = " + param );
	    } else {
	        params.remove(key);
	        //m_log.warn( "removed " + key );
	    }
		BC.parameterSaver.save();
	}


	/** 
	 * SAME as getParameter(key, Object.class)
	 * @param key
	 * @return
	 */
	public static synchronized Object getParameter(String key) {
		return params.obtain(key, Object.class);
	}
	
	/**
	* get a parameter
	*/
	public static synchronized Object getParameter(String key, Class c) {
		return params.obtain(key, c);
	}

	/**
	 * Gets a parameter as a String.
	 * @return The parameter as a String or null if no such parameter
   *         value exists. 
	 */
	public static synchronized String getParameterStr(String key) {
		return (String) getParameter(key, String.class);
	}
	
	/** When a bogus entry is encountered and we want to roll back to default */
	public static synchronized Object forceDefaultParam(String key) {
	    return params.forceDefault(key);
	}
	
	/**
	 * This method forces the parameters to be saved now
	 * Call it when you are closing the app (for example)
	 */
	public static synchronized void commitChanges() {
	    MergingMemory.save();
	    BC.parameterSaver.saveNow();
	}
	
	/*****INIT WORKING SPACE (directories files .... ) ******/

	/**
	* Initzialize the software, load parameters and license
	*/
	public void initWorkingSpace() {
		 (new File(Resources.dataPath())).mkdirs();

		// load parameters
		loadParameters();


	}

	/**
	* load parameters
	*/
	public static void loadParameters() {
		// LOADING stored settings
		File fSave= new File(Resources.parametersPath());
		if ((fSave.exists())) {
			m_log.info( "Loading saved parameters..." );
			try {
				params= (Params) Secu.getData(fSave, null);
				return;
			} catch (SimpleException e) {
				m_log.error( "BC:Error loadin parameters ", e );
			}
		}
		
		// try to retreive them from backup file
		fSave= new File(Resources.parametersBackupPath());
		if (fSave.exists()) {
		    try {
		        params= (Params) Secu.getData(fSave, null);
		        m_log.error( "BC:Error parameters retrieved from backup");
		        return;
		    } catch (SimpleException e1) {
		        m_log.error( "BC:Error loadin backup parameters ", e1 );
		    }
		}
		
		m_log.error("The Parameters file does not exist... creating it");
		params= new Params();
		//setDefaultParameters();
		
	}



	
	/**
	 * return the controlServer (web server)
	 */
	public ControlServer getControlServer() {
	    //	      Start the control server
	    if (controlServer == null)
			controlServer = new ControlServer(Resources.htdocsPath());
			
		return controlServer;
	}
	
	/**
	* redirect setParameters from license to my paremeters
	*/
	public void setOwnerParameter(String key, Object o) {
		setParameter(key,o);
	}

	/**
	 * Get the actual Currency Manager
	 */
	public static BCCurrencyManager getCurrencyManager() {
		return currencyManager;
	}
	
	/**
	 * return true if we are in a extend view of options and settings.
	 * This mode correspond to "Is SImpleData's staff using the software"
	 */
	public static boolean isSimple() {
	    Boolean b = (Boolean) getParameter(Params.KEY_IS_SIPMLE, Boolean.class);
	    if (b == null) {
	        setIsSimple(false) ; 
	        return false;
	    }
	    return b.booleanValue();
	}
	
	/** change the isSimple parameter
	 * @see #isSimple()
	 */
	public static void setIsSimple(boolean b) {
	    setParameter(Params.KEY_IS_SIPMLE,new Boolean(b));
	}
	
}



/*
 * $Log: BC.java,v $
 * Revision 1.2  2007/04/02 17:04:28  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.114  2006/04/05 16:14:30  simple
 * New License Key for his.ch
 * and update of building
 *
 * Revision 1.113  2004/12/04 12:54:54  perki
 * Corrected problem with autosave
 *
 * Revision 1.112  2004/11/29 13:56:50  jvaucher
 * Proxy bug fixed. Notice that unresolvable host yields to the use of
 * a direct connection.
 *
 * Revision 1.111  2004/11/29 13:34:45  perki
 * *** empty log message ***
 *
 * Revision 1.110  2004/11/29 09:09:09  jvaucher
 * - Bug with Proxies
 * - Fixed swing problem at startup
 *
 * Revision 1.109  2004/11/23 17:42:32  perki
 * *** empty log message ***
 *
 * Revision 1.108  2004/11/23 17:42:02  perki
 * *** empty log message ***
 *
 * Revision 1.107  2004/11/20 15:28:27  perki
 * *** empty log message ***
 *
 * Revision 1.106  2004/11/20 15:11:17  perki
 * *** empty log message ***
 *
 * Revision 1.105  2004/11/19 18:02:20  perki
 * Introducing file associations
 *
 * Revision 1.104  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.103  2004/11/16 17:22:11  perki
 * Merging now remembers of last picks
 *
 * Revision 1.102  2004/11/16 07:08:17  perki
 * Now license is loaded from the jar file directly
 *
 * Revision 1.101  2004/11/08 16:42:35  jvaucher
 * - Ticket # 40: Autosave. At work. Some tunning is still necessary
 *
 * Revision 1.100  2004/11/02 16:28:02  perki
 * Log4J in now in sdl
 *
 * Revision 1.99  2004/10/20 08:19:39  perki
 * *** empty log message ***
 *
 * Revision 1.98  2004/10/18 16:48:10  carlito
 * JComboBox bug corrected
 *
 * Revision 1.97  2004/10/18 10:34:53  carlito
 * editor pane without border
 *
 * Revision 1.96  2004/10/17 16:22:59  perki
 * *** empty log message ***
 *
 * Revision 1.95  2004/10/17 09:45:54  perki
 * New save system
 *
 * Revision 1.94  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.93  2004/10/12 17:49:09  carlito
 * Simulator split problems solved...
 * description pb solved
 *
 * Revision 1.92  2004/10/08 06:59:08  perki
 * Minor changes in the ant / install4j process
 *
 * Revision 1.91  2004/10/07 08:21:08  perki
 * Keyring ok
 *
 * Revision 1.90  2004/10/05 10:28:43  kaspar
 * ! Reenabled loading of license, should work now.
 *
 * Revision 1.89  2004/10/04 15:35:24  perki
 * *** empty log message ***
 *
 * Revision 1.88  2004/10/04 15:33:51  perki
 * *** empty log message ***
 *
 * Revision 1.87  2004/10/04 15:30:54  perki
 * *** empty log message ***
 *
 * Revision 1.86  2004/10/04 08:47:09  perki
 * Moved soft info medthods to a new class
 *
 * Revision 1.85  2004/09/30 15:25:18  perki
 * Better Startp process
 *
 * Revision 1.84  2004/09/29 14:45:54  perki
 * *** empty log message ***
 *
 * Revision 1.83  2004/09/29 12:40:19  perki
 * Localization tarifs
 *
 * Revision 1.82  2004/09/29 12:22:25  kaspar
 * + Keyring loading works now fine.
 * + Added keyring constants into Params
 * + Moved keyring to private part of license.
 *
 * Revision 1.81  2004/09/29 09:47:14  perki
 * New Splash system
 *
 * Revision 1.80  2004/09/29 06:54:25  perki
 * *** empty log message ***
 *
 * Revision 1.79  2004/09/28 15:22:18  perki
 * Pfiuuuu
 *
 * Revision 1.78  2004/09/27 13:19:10  perki
 * register Dialog OK
 *
 * Revision 1.77  2004/09/27 08:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.76  2004/09/25 13:32:30  perki
 * *** empty log message ***
 *
 * Revision 1.75  2004/09/25 11:47:54  perki
 * Added a way to find My Documents Folder
 *
 * Revision 1.74  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.73  2004/09/24 13:56:22  kaspar
 * + Access keyring_initial and keyring at startup
 *
 * Revision 1.72  2004/09/24 10:08:28  perki
 * *** empty log message ***
 *
 * Revision 1.71  2004/09/24 00:09:08  kaspar
 * + Added 'Armored File Format' which is for now just encoding
 *   using XStream (and containing some minor hickups).
 * + Added Jars that implement XStream library.
 * ! New SDL
 * ! Bugfix in MiniBrowser.java; must have been debug code that was
 *   only partially added to CVS
 *
 * Revision 1.70  2004/09/23 16:29:03  perki
 * Pfuuuu--- got the web interfac to work
 *
 * Revision 1.69  2004/09/23 14:45:47  perki
 * bouhouhou
 *
 * Revision 1.68  2004/09/23 08:21:26  perki
 * removed all the code relative to the WebServer
 *
 * Revision 1.67  2004/09/22 14:59:03  perki
 * Web server off
 *
 * Revision 1.66  2004/09/22 06:47:04  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.65  2004/09/21 17:07:03  jvaucher
 * Implemented load and save preferences
 * Need perhaps (certainly) to test the case where one refered folder is deleted
 *
 * Revision 1.64  2004/09/14 14:46:29  perki
 * *** empty log message ***
 *
 * Revision 1.63  2004/09/04 18:26:45  kaspar
 * + resource/log4j.properties controls the Log4j subsystem
 *   configuration.
 *
 * Revision 1.62  2004/09/03 11:47:52  kaspar
 * ! Log.out -> log4j first half
 *
 * Revision 1.61  2004/09/01 09:03:19  perki
 * *** empty log message ***
 *
 * Revision 1.60  2004/08/25 13:07:43  kaspar
 * ! Added names to threads.
 *
 * Revision 1.58  2004/08/25 07:39:19  kaspar
 * ! Some cleaning up in BC, moving classes outside of
 *   main class, deleting old commented out code.
 * + Log4j for Log.out rebuild
 *
 * Revision 1.57  2004/08/17 09:08:14  kaspar
 * ! Nonrelevant XXX removed
 *
 * Revision 1.56  2004/07/31 16:45:56  perki
 * Pairing step1
 *
 * Revision 1.55  2004/07/22 15:12:34  carlito
 * lots of cleaning
 *
 * Revision 1.54  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.53  2004/07/19 17:39:08  perki
 * *** empty log message ***
 *
 * Revision 1.52  2004/07/19 12:25:02  perki
 * Merging finished?
 *
 * Revision 1.51  2004/07/19 09:36:53  kaspar
 * * Added Visitor for visiting the whole Tarif structure called
 *   TarifTreeVisitor
 * * Added Visitor for visiting UI side CompactTree called CompactTreeVisitor
 * * removed superfluous hsqldb.jar
 *
 * Revision 1.50  2004/07/09 19:10:24  carlito
 * *** empty log message ***
 *
 * Revision 1.49  2004/07/08 14:58:59  perki
 * Vectors to ArrayList
 *
 * Revision 1.48  2004/07/07 13:43:56  carlito
 * *** empty log message ***
 *
 * Revision 1.47  2004/07/06 17:31:25  carlito
 * Desktop manager enhanced
SButton with border on macs
desktop size persistent
 *
 * Revision 1.46  2004/06/30 17:33:25  carlito
 * MiniBrowser replaced by Kaspar Browser.
launchable from desktop
 *
 * Revision 1.45  2004/06/30 08:59:18  carlito
 * web improvment and dispatcher case debugging
 *
 * Revision 1.44  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.43  2004/06/06 17:28:09  perki
 * *** empty log message ***
 *
 * Revision 1.42  2004/05/31 17:55:46  carlito
 * *** empty log message ***
 *
 * Revision 1.41  2004/05/31 17:13:04  carlito
 * *** empty log message ***
 *
 * Revision 1.40  2004/05/31 16:56:13  carlito
 * *** empty log message ***
 *
 * Revision 1.39  2004/05/31 16:22:37  carlito
 * *** empty log message ***
 *
 * Revision 1.38  2004/05/31 15:59:06  perki
 * *** empty log message ***
 *
 * Revision 1.37  2004/05/31 15:02:59  perki
 * *** empty log message ***
 *
 * Revision 1.36  2004/05/31 12:40:22  perki
 * *** empty log message ***
 *
 * Revision 1.35  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.34  2004/05/20 06:11:17  perki
 * id tagging
 *
 * Revision 1.33  2004/05/12 13:38:06  perki
 * Log is clever
 *
 * Revision 1.32  2004/04/09 07:16:51  perki
 * Lot of cleaning
 *
 * Revision 1.31  2004/03/24 13:11:14  perki
 * Better Tarif Viewer no more null except
 *
 * Revision 1.30  2004/03/22 22:53:54  perki
 * gdksagdfjs kdf
 *
 * Revision 1.29  2004/03/17 15:53:42  carlito
 * *** empty log message ***
 *
 * Revision 1.28  2004/03/17 14:28:53  perki
 * *** empty log message ***
 *
 * Revision 1.27  2004/03/17 10:54:45  perki
 * Thread for params
 *
 * Revision 1.26  2004/03/15 15:46:56  carlito
 * *** empty log message ***
 *
 * Revision 1.25  2004/03/08 06:59:29  perki
 * et hop une bouteille de rhum
 *
 * Revision 1.24  2004/03/06 11:49:21  perki
 * *** empty log message ***
 *
 * Revision 1.23  2004/03/03 20:36:48  perki
 * bonne nuit les petits
 *
 * Revision 1.22  2004/02/22 16:59:28  perki
 * *** empty log message ***
 *
 * Revision 1.21  2004/02/22 15:57:25  perki
 * Xstream sucks
 *
 * Revision 1.20  2004/02/22 10:43:56  perki
 * File loading and saving
 *
 * Revision 1.19  2004/02/06 14:50:29  carlito
 * paouatche
 *
 * Revision 1.18  2004/02/04 18:10:05  carlito
 * zorglub
 *
 * Revision 1.17  2004/02/04 17:46:44  carlito
 * added Icons to resources
 * Revision 1.16  2004/02/04 15:42:16  perki
 * cleaning
 *
 * Revision 1.15  2004/02/02 11:21:05  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/01/31 18:21:30  perki
 * Wonderfull Day
 *
 * Revision 1.13  2004/01/31 15:46:49  perki
 * 16 heure 49
 *
 * Revision 1.12  2004/01/29 12:17:35  perki
 * Import cleaning
 *
 * Revision 1.11  2004/01/22 18:03:46  perki
 * *** empty log message ***
 *
 * Revision 1.10  2004/01/22 15:40:51  perki
 * Bouarf
 *
 * Revision 1.9  2004/01/22 13:03:31  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/17 14:27:54  perki
 * Better (Best?) Named implementation
 *
 * Revision 1.7  2004/01/17 08:00:35  perki
 * better beans comliance but not done yet
 *
 * Revision 1.6  2004/01/10 13:25:32  perki
 * Xml saving and more
 *
 * Revision 1.5  2004/01/10 08:11:44  perki
 * UI addons and Look And Feel
 *
 * Revision 1.4  2003/12/08 17:32:58  perki
 * bean shell console
 *
 * Revision 1.3  2003/12/05 17:43:12  perki
 * Desktop en plus
 *
 * Revision 1.2  2003/12/05 16:41:45  perki
 * *** empty log message ***
 *
 * Revision 1.1  2003/12/05 15:53:39  perki
 * start
 */
