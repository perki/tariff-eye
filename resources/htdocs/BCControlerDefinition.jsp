<!-- /** imported fields shoudl be updated to BCControler.imports **/ -->
<%@ page import="com.simpledata.bc.*" %>
<%!
	
	
	
	/** 
	 * THIS FILE IS NEVER USED IT SERVERS AS AT FRONTEND FOR
	 * lomboz eclipse plugin : http://www.objectlearn.com/
	 *
	 *
	 * This file should reflect all the functions accessible in
	 * BCControler
	 */

	// ---------------- BEANSHELL COMMANDS --------//
	
	/**  
	* Read filename into the interpreter and evaluate it in the 
	* current namespace. Like the Bourne Shell "." command. This 
	* command acts exactly like the eval() command but reads from a 
	* file or URL source.
	**/
	public void source(String filename) {};
	public void source(java.net.URL url) {};
	
	
	// ---------------- Accesible static objects ------//
	
	
	// ---------------- PARSE AN EXTERNAL FILE ------//
	
	/** like an include in php .. this is done in wwwroot context**/
	public void parse(String filenameRelativeToWWWroot) {};
	
	// --------------- PARAMS COMMANDS ------------//

	/** 
	 * get the parameter corresponding to this key : "" if none <BR>
	 * use paramsExists(String key) to test
	 * **/
	public String params(String key) { return "" ; };
	
	/** 
	 * return true if this parameter is known
	 **/
	boolean paramsExists(String key) { return false;}
	
	//	---------------- OUTPUT ------------------//
	
	/** shortcut to print **/
	public void p(Object o) {}
	
	/** print this object (toString()) to the web page **/
	public void print(Object o) {}
	
	// ------------------ HELPER -----------------//
	
	/**
	 * shortcut to random()
	 */
	public void r() {}
	
	/** 
	 * echoes a random String : &random=Xdkakd <BR>
	 * to prevent caching
	 */
	public void random() {}
	
	// ------------------ ACTION -----------------//
	
	public final String CREATOR_NEW = "";
	public final String CREATOR_OPEN = "";
	
	public final String SIMULATOR_NEW = "";
	public final String SIMULATOR_OPEN = "";
	
	public final String ACT = "";
	public final String ACTD = "";
	
	/** waiting for parameter : app_type=<CREATOR or SIMULATOR><OPEN or NEW> **/
	public final String DIRECT_menu_file_open = "";
	public final String DIRECT_showHelp = "";
	/** waiting for parameter : open_url=URL **/
	public final String DIRECT_open_url_in_browser = "";
	
	/**open the tarifcation browser **/
	public void menu_file_open(final String applicationType) {}
	
	/** Show helper */
	public void showHelp() {}
	
	/** open an url in an external browser **/
	public void open_url_in_browser(String url) {}
	
	//--------- BASE HREF --------------------//
	/** return the base HREF of this site **/
	public String  base_href() { return ""; }
	
	//--------- URL GENERATION ---------------//
	/** 
	 * get TariffEye's UPDATE and INFO URL
	 * get INFO URL 
	 * **/
	public String getInfoURL() { return ""; };
	
	//--------- COLORS AND UI INTEGRATION ------//
	/** return the bgcolor of a background **/
	public String color_bg() { return ""; }
	
	/** 
	 * return a color String such as #FF00FF 
	 * return "" if color is null
	 * **/
	public String colorToString(java.awt.Color c) { return ""; }
	
	//---------- BC PARAMETERS --------- //
	
	/** 
	 * Wrapper to BC.getParameter
	 * @param key key
	 * @return value
	 */
	public Object getBCParameter(String key) {return null;}
	
	/**
	 * @return true if the user had choosen TariffEyeInfo in his preferences
	 */
	public boolean useTariffEyeInfo() {return false;}
	
	//---------- Subscription ---------- //
	/**
	 * Check the subscription expiration date
	 * @return 0 if the subscription go through a while, 1 if the subscription
	 * expires in less than a month, 2 if the subscription has expired.
	 */
	public int warningSubscription() {return 0;}
	//---------------- DEBUG ----------//
	public void debug_print_params() {}
	
%>
<!--
 $Id: BCControlerDefinition.jsp,v 1.1 2006/12/03 12:48:43 perki Exp $
 $Log: BCControlerDefinition.jsp,v $
 Revision 1.1  2006/12/03 12:48:43  perki
 First commit on sourceforge

 Revision 1.17  2004/12/01 09:33:25  jvaucher
 Subscription delay information

 Revision 1.16  2004/11/30 14:43:49  jvaucher
 - Emergency release

 Revision 1.15  2004/11/29 10:07:49  perki
 *** empty log message ***

 Revision 1.14  2004/11/26 11:37:13  perki
 *** empty log message ***

 Revision 1.13  2004/11/26 11:15:18  perki
 *** empty log message ***

 Revision 1.12  2004/10/19 17:10:42  carlito
 assistant modified

 Revision 1.11  2004/09/28 08:55:21  plegris
 Minor changes

 Revision 1.10  2004/09/27 13:19:10  plegris
 register Dialog OK

 Revision 1.9  2004/09/23 16:29:03  plegris
 Pfuuuu--- got the web interfac to work

 Revision 1.8  2004/09/23 06:27:55  plegris
 LOt of cleaning with the Logger

 Revision 1.7  2004/07/19 20:00:33  carlito
 *** empty log message ***

 Revision 1.6  2004/06/28 19:25:42  carlito
 *** empty log message ***

 Revision 1.5  2004/06/16 09:58:27  plegris
 *** empty log message ***

 Revision 1.4  2004/06/07 15:51:54  plegris
 aiye aye baby

 Revision 1.3  2004/06/07 15:12:18  plegris
 *** empty log message ***

 -->