/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: BCControler.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.webcontrol;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.Resources;
import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.bc.help.Helper;
import com.simpledata.bc.tarifmanager.SubscriptionToolBox;
import com.simpledata.sdl.os.BrowserOpener;

/**
 * This is an object that have all the method used for the scripted web pages
 * It is able to control BC and many other things<BR><BR>
 * 
 * <B>ANY CHANGE TO THIS FILE SHOULD BE REFLECTED IN :
 * resources/htdocs/BCControlerDefinition.jsp !!!!!</B>
 */
public class BCControler {
	
	private static final Logger m_log = Logger.getLogger( BCControler.class ); 
	
	
	/** all the packages that need to be imported **/
	public static final String[] imports = new String[] {
	        "com.simpledata.bc.BC"
	};
	
	private PrintStream pout;
	private HashMap parameters;
	private ControlServer daddy;
	
	public BCControler(ControlServer daddy) {
		this.daddy = daddy;
	}
	
	public void setOut(PrintStream out) {
		pout = out;
	}
	
	public void setParams(HashMap params) {
		parameters = params;
	}
	
	//---------------- INCLUDE OF FILES ------------//
	
	/** like an include in php .. this is done in wwwroot context**/
	public void parse(String filenameRelativeToWWWroot) {
		File f = daddy.getFileInPath(filenameRelativeToWWWroot);
		try {
			FileInputStream file = new FileInputStream(f);
			daddy.evalScript(ControlServer.parseFile(file),pout,parameters);
		} catch (FileNotFoundException e) {
			m_log.error(e);
			return;
		}
	}
	
	//---------------- PARAMETERS ------------------//
	
	/** 
	 * get the parameter corresponding to this key : "" if none <BR>
	 * use paramsExists(String key) to test
	 * **/
	public String params(String key) { 
		if (! paramsExists(key)) return "";
		return parameters.get(key).toString();
	}
	
	/** 
	 * return true if this parameter is known
	 **/
	public boolean paramsExists(String key) { 
		return parameters.containsKey(key); 
	}
	
	//	---------------- OUTPUT ------------------//
	
	/** shortcut to print **/
	public void p(Object o) { print(o); }
	
	/** print this object (toString()) to the web page **/
	public void print(Object o) { pout.print(o); }
	
	// ------------------ HELPER -----------------//
	
	/**
	 * shortcut to random()
	 */
	public void r() {
		random();
	}
	
	/** 
	 * echoes a random String : &random=Xdkakd <BR>
	 * to prevent caching
	 */
	public  void random() {
		p("&random="+Math.random());
	}
	
	// ------------------ ACTION -----------------//
	
	//public final String APP_CREATOR = "creator";
	//public final String APP_SIMULATOR = "simulator";
	
	public final String ACT = ControlServer.BASE_URL;
	public final String ACTD = ControlServer.BASE_URL_DIRECT;
	
	/** waiting for parameter : <CREATOR or SIMULATOR><OPEN or NEW> **/
	public final String DIRECT_menu_file_open = "menu_file_open";
	public final String DIRECT_showHelp = "showHelp";
	/** waiting for parameter : open_url=URL **/
	public final String DIRECT_open_url_in_browser = "open_url_in_browser";
	
	public final String CREATOR_NEW = "apptype=creator:new";
	public final String CREATOR_OPEN = "apptype=creator:open";
	
	public final String SIMULATOR_NEW = "apptype=simulator:new";
	public final String SIMULATOR_OPEN = "apptype=simulator:open";
	
	/**open the tarifcation browser **/
	public void menu_file_open(final String applicationType) {
	    String loadParam = FileManagement.CREATOR_NEW;
	    if (CREATOR_NEW.equals(applicationType)) {
	        loadParam = FileManagement.CREATOR_NEW;
	    } else if (CREATOR_OPEN.equals(applicationType)) {
	        loadParam = FileManagement.CREATOR_OPEN;
	    } else if (SIMULATOR_NEW.equals(applicationType)) {
	        loadParam = FileManagement.SIMULATOR_NEW;
	    } else if (SIMULATOR_OPEN.equals(applicationType)) {
	        loadParam = FileManagement.SIMULATOR_OPEN;
	    } else {
	        m_log.error( "Wrong applicationType : '"+applicationType+"'" );
	    }
	    
	    FileManagement.promptFileAndStartApp(
	            BC.bc.getMajorComponent(), loadParam);
	}
	
	/** Launch helper if asked */
	public void showHelp() {
	    Helper.showHelp();
	}
	
	/** open an url in an external browser **/
	public void open_url_in_browser(String url) {
	    BrowserOpener.init();
	    try {
	    	m_log.debug("opening URL:"+url);
            BrowserOpener.displayURL(url);
        } catch (IOException e) {
          m_log.error("Failed opening URL:"+url,e);
        }
	}
	
	
	/** handle direct commands requests **/
	protected void handleDirect(String cmd,HashMap params) {
	    if (cmd.equals(DIRECT_menu_file_open)) {
	        menu_file_open("apptype="+params.get("apptype"));
	        return ;
	    }
	    if (cmd.equals(DIRECT_showHelp)) {
	        showHelp();
	        return ;
	    }
	    if (cmd.equals(DIRECT_open_url_in_browser)) {
	        open_url_in_browser(""+params.get("open_url"));
	        return ;
	    }
	    m_log.error("Invalid direct request: "+cmd,new Exception());
	}
	
//	--------- BASE HREF --------------------//
	/** return the base HREF of this site **/
	public String  base_href() {
	    String res = "";
	    res = new File(Resources.htdocsPath()).toURI().toString();
	    //m_log.debug("*********"+res);
	    return "<BASE HREF=\""+res+"\">"; 
	}
	/** return the base HREF of this site **/
	public String  base_uri() {
	    String res = "";
	    res = new File(Resources.htdocsPath()).toURI().toString();
	    return res; 
	}
	
	//--------- URL GENERATION ---------------//
	/** 
	 * get TariffEye's UPDATE and INFO URL
	 * get INFO URL 
	 * **/
	public String getInfoURL() {
	   return (new TariffEyeQuery("INFO")).getURL();
	}
	
	//--------- COLORS AND UI INTEGRATION ------//
	/** return the bgcolor of a background **/
	public String color_bg() {
		return colorToString(UIManager.getColor("Panel.background"));
	}
	
	/** 
	 * return a color String such as #FF00FF 
	 * return "" if color is null
	 * **/
	public String colorToString(Color c) {
		if (c == null) return "";
		return "#"+Integer.toHexString( c.getRGB() & 0x00ffffff );
	}
	
	//---------- BC PARAMETERS --------- //
	
	/** 
	 * Wrapper to BC.getParameter
	 * @param key key
	 * @return value
	 */
	public Object getBCParameter(String key) {
		return BC.getParameter(key);
	}
	
	/**
	 * @return true if the user had choosen TariffEyeInfo in his preferences
	 */
	public boolean useTariffEyeInfo() {
		Boolean tyi = (Boolean)BC.getParameter(Params.KEY_TARIFF_EYE_INFO);
		return (tyi != null)?(tyi.booleanValue()):false;
	}
	
	//---------- Subscription ---------- //
	/**
	 * Check the subscription expiration date
	 * @return the number of days before expiration. -1 if the information is
	 * not aviable, -2 if the subscription expired
	 */
	public int warningSubscription() {
		if (SubscriptionToolBox.expiresTimeAviable()) {
			int days = SubscriptionToolBox.expiresTimeInDay();
			return (SubscriptionToolBox.hasExpired() ? -2 : days);
		}
		return -1;
		
	}
	//---------------- DEBUG ----------//
	public void debug_print_params() {
		Iterator it = parameters.keySet().iterator();
		String key = null;
		p("<TABLE BORDER=1><TR><TD COLSPAN=2>GET PARAMS</TD></TR>");
		while (it.hasNext()) {
			key = (String) it.next();
			p("<TR><TD>"+key+"</TD><TD>"+params(key)+"</TD></TR>");
		}	
		p("</TABLE>");
	}
	
}

/*
 * $Log: BCControler.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.26  2004/12/01 09:33:25  jvaucher
 * Subscription delay information
 *
 * Revision 1.25  2004/11/30 14:43:49  jvaucher
 * - Emergency release
 *
 * Revision 1.24  2004/11/30 12:36:20  perki
 * *** empty log message ***
 *
 * Revision 1.23  2004/11/26 16:03:42  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/11/26 13:48:35  perki
 * *** empty log message ***
 *
 * Revision 1.21  2004/11/26 11:37:13  perki
 * *** empty log message ***
 *
 * Revision 1.20  2004/11/26 11:15:18  perki
 * *** empty log message ***
 *
 * Revision 1.19  2004/11/16 15:17:55  jvaucher
 * Refactor of load / save methods.
 *
 * Revision 1.18  2004/10/19 17:10:42  carlito
 * assistant modified
 *
 * Revision 1.17  2004/09/28 08:55:22  perki
 * Minor changes
 *
 * Revision 1.15  2004/09/24 10:08:28  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/09/23 16:29:03  perki
 * Pfuuuu--- got the web interfac to work
 *
 * Revision 1.13  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.12  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/07/19 20:00:33  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/07/07 17:27:10  perki
 * *** empty log message ***
 *
 * Revision 1.9  2004/06/30 17:33:25  carlito
 * MiniBrowser replaced by Kaspar Browser.launchable from desktop
 *
 * Revision 1.8  2004/06/30 08:59:18  carlito
 * web improvment and dispatcher case debugging
 *
 * Revision 1.7  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/06/16 09:58:28  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/06/07 15:51:54  perki
 * aiye aye baby
 *
 * Revision 1.3  2004/06/07 14:32:43  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/06/06 17:28:09  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/06/06 15:44:34  perki
 * added controler
 *
 */