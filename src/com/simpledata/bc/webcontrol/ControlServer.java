/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ControlServer.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.webcontrol;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

import com.simpledata.bc.BC;
import com.simpledata.filetools.SimpleException;
import com.simpledata.filetools.TextFileUtils;


/**
 * A web server that controls BC
 */
public class ControlServer {
	
	private static final Logger m_log = Logger.getLogger( ControlServer.class );
	
	private Interpreter interp;
	private BCControler bob;
	private String wwwhome;
	
	private static final String TAG_START = "<%";
	private static final String TAG_END = "%>";
	private static final String BSHTML_EXT = "jsp";
	
	private static final int ACTION_EVAL = 0;
	private static final int ACTION_IGNORE = 1;
	private static final int ACTION_ECHO = 2;

	public static final String BASE_URL = "http://tariffeye.local/";
	/** 
	 * when a url starts with this the remainting data will be passed to
	 * BCControler.handleDirect(String command);
	 */
	public static final String BASE_URL_DIRECT = BASE_URL+"direct/";
	
	public ControlServer(String wwwhome) {
		this.wwwhome = wwwhome;
		interp = new Interpreter();
		bob = new BCControler(this);
		try {
			interp.eval("import com.simpledata.bc.webcontrol.*;");
			for (int i = 0; i < BCControler.imports.length; i++) {
			    interp.eval("import "+BCControler.imports[i]+";");
			}
			
			
			interp.set("bob",bob);
			interp.eval("importObject(bob);");
		} catch (EvalError e1) {
			m_log.error( "failed init ", e1 );
		}
		
	}
	
	
	/** handle direct requests commands **/
	public void handleDirect(URL direct) {
	   // check that url starts with direct path and remove it 
	  
	   if (! direct.toString().startsWith(ControlServer.BASE_URL_DIRECT)) {
           m_log.error("Invalid head URL:"+direct,new Exception());
           return;
       }
	   
	   String str = direct.toString().substring(
	           ControlServer.BASE_URL_DIRECT.length());
	   
	   // extract the parameters
	   //	 get the parameters from the request
       HashMap parameters = new HashMap();
       
       String cmd = str;
       try {
           cmd = parseParameters(str,null,parameters);
       } catch (UnsupportedEncodingException e) { m_log.error("",e);
       } catch (MalformedURLException e) {  m_log.error("",e); }
      
       bob.handleDirect(cmd,parameters);
       
	}
	
	/** return a file in the wwwhome path **/
	protected File getFileInPath(String path) {
	    String test[] = new String[]{
	            wwwhome + "/", 
	            wwwhome + "/"+BC.langManager.getLang()+"dir/"};
	    
	    File f = null;
	    for (int i = 0; i < test.length ; i++) {
	    
		    f = new File( test[i]+path);
	
		    if (f.isDirectory()) { 
	            // if directory, implicitly add 'index.html
	            f = new File(f.toString()+"/index.html");
	        }
		    if (f.exists()) {
		        return f;
		    }
	    }
		return f;
	}
	

	
	/**
	 * get the parameters from a URL encoded string (& = ..)
	 * <BR>FILL parameters with those params
	 * @return a cleaned version of the request (without params) or http://
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	private String 
			parseParameters(String req,String encoding,HashMap parameters) 
	throws UnsupportedEncodingException, MalformedURLException {
	   
	    
	    //	  look if there is parameters
	    int qIndex = req.indexOf('?');
	    
	    if (encoding == null) encoding = "cp1252";
	    
	    if (qIndex > 0) {
	        
	        String[] splits = req.substring(qIndex+1).split("&");
	        String[] subSplits = null;
	        String value = null;
	        for (int i = 0; i < splits.length ; i++ ) {
	            subSplits = splits[i].split("=");
	            value = "";
	            if (subSplits.length > 1 ) 
	                value = URLDecoder.decode(subSplits[1],encoding);
	                    
	                parameters.put(subSplits[0],value);
	        }
	        
	        
	        req = req.substring(0,qIndex);
	    }
	    
	    //	  look if I need to remove an "http://*/"
	    if (req.startsWith("http://")) {
	        URL u = new URL(req);
	        req = u.getPath();
	    }
	    
	    return req;
	}
	
	
	
	/** return the content of the document described by this URL **/
	public String getDocument(URL page)throws java.io.IOException {
	    
	    String req = page.toString();
	    // get the parameters from the request
        HashMap parameters = new HashMap();
     
            req = parseParameters(req,null,parameters);
     
        
        File f = getFileInPath(req);
        //File f = new File(path);
	   
	    
	    final String  zContentType = guessContentType(f.toString()) ;
	    m_log.warn("=== "+page+" "+zContentType);
	    
	   
	    if (isBshScripted(f.toString())) {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PrintStream ps = new PrintStream(baos);
	        evalScript(parseFile(new FileInputStream(f)),ps,parameters);
	        return baos.toString();
	    } 
	        try {
                return TextFileUtils.getString(f);
            } catch (SimpleException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        return "<HTML>Failed "+page+" </HTML>";
	}
	
	
	/** return true if this file is a bsh script **/
	private static boolean isBshScripted(String path) 
	{
		return path.endsWith("."+BSHTML_EXT);
	}
	
	/** return the content type of a file **/
	private static String guessContentType(String path)
	{
		if (isBshScripted(path)) 
			return "text/html";
		if (path.endsWith(".html") || path.endsWith(".htm")) 
			return "text/html";
		else if (path.endsWith(".txt") || path.endsWith(".java")) 
			return "text/plain";
		else if (path.endsWith(".gif")) 
			return "image/gif";
		else if (path.endsWith(".class"))
			return "application/octet-stream";
		else if (path.endsWith(".jpg") || path.endsWith(".jpeg"))
			return "image/jpeg";
		else 	
			return "text/plain";
	}
	
	/** clean text for output **/
	private static void addPrint(StringBuffer script,String text) {
		// clean text : add \\ for \
		text = text.replaceAll("\\\\","\\\\\\\\").replaceAll("\"","\\\\\"");
		script.append("p(\""+text+"\");");
	}
	
	/** read a file look for <% %> and prepare a bsh script from it **/
	static StringBuffer parseFile(InputStream ins){
		
		// read file into a Buffer
		StringBuffer buff = new StringBuffer();
		
		// create a script from this file
		StringBuffer script = new StringBuffer();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(ins));
			while (in.ready())  {
				buff.append(in.readLine());
			}
		} catch (IOException e) { m_log.error(e); }
		
		int pointer = 0;
		int at = -1;
		char tagID ; // will contain the tag identification character
		
		int action = ACTION_EVAL;
		
		// search for <% %> tags
		
		at = buff.indexOf(TAG_START,pointer);
		while (at > -1) {
			addPrint(script,buff.substring(pointer,at));
			pointer = at + TAG_START.length();
			
			action = ACTION_EVAL;
			// read the next character to see what action should be taken
			if ((buff.length() + 1) > pointer) {
				tagID = buff.charAt(pointer);
				if (tagID == '@') { // ignore content
					action = ACTION_IGNORE;
					pointer++;
				}
				if (tagID == '=') { // output content
					action = ACTION_ECHO;
					pointer ++;
				}
			}
			
			at = -1;
			at = buff.indexOf(TAG_END,pointer);
			if (at < 0) {
				m_log.warn( "Cannot find closing TAG" );
			} else {
				// pass the code to execute to the interpreter
				switch (action) {
					case ACTION_EVAL:
						script.append(buff.substring(pointer,at));
						script.append(";");
					break;
					case ACTION_ECHO:
						script.append("p(new StringBuffer().append(")
						.append(buff.substring(pointer,at)).append("));");
					break;
					case ACTION_IGNORE:
					break;
				}
				
				
				pointer = at + TAG_END.length();
			}
			at = buff.indexOf(TAG_START,pointer);
		}
			
		// flush the remaining text
		addPrint(script,buff.substring(pointer));
		return script;
	}
		
	public void evalScript(StringBuffer script,
			PrintStream pout,HashMap params) {
		try {
			bob.setOut(pout);
			bob.setParams(params);
			
			interp.eval(script.toString());
		} catch (EvalError e1) {
			bob.print("<HR><H3>Error</H3>CODE:<BR><PRE>"+
					script.toString().replaceAll("<","&lt;")+
					"</PRE><HR>ERROR:<BR><PRE>");
			e1.printStackTrace(pout);
			bob.print("</PRE>");
			m_log.error( "Failed while executing code",e1 );
		}
			
	
	}

	/**
	 * @return Returns the interpeter.
	 */
	public Interpreter getInterp() {
		return interp;
	}
}

/*
* $Log: ControlServer.java,v $
* Revision 1.2  2007/04/02 17:04:27  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:42  perki
* First commit on sourceforge
*
* Revision 1.16  2004/11/26 11:15:18  perki
* *** empty log message ***
*
* Revision 1.15  2004/09/27 13:19:10  perki
* register Dialog OK
*
* Revision 1.14  2004/09/23 16:29:03  perki
* Pfuuuu--- got the web interfac to work
*
* Revision 1.13  2004/09/23 14:45:48  perki
* bouhouhou
*
* Revision 1.12  2004/09/23 08:21:26  perki
* removed all the code relative to the WebServer
*
* Revision 1.11  2004/09/22 14:59:03  perki
* Web server off
*
* Revision 1.10  2004/09/06 13:23:53  kaspar
* + InstanceFinder debug tool
*
* Revision 1.9  2004/08/25 13:07:43  kaspar
* ! Added names to threads.
*
* Revision 1.8  2004/08/02 07:59:53  kaspar
* ! CVS merge
*
* Revision 1.7  2004/06/07 15:51:54  perki
* aiye aye baby
*
* Revision 1.6  2004/06/07 14:32:43  perki
* *** empty log message ***
*
* Revision 1.5  2004/06/06 17:28:09  perki
* *** empty log message ***
*
* Revision 1.4  2004/06/06 15:48:07  perki
* *** empty log message ***
*
* Revision 1.3  2004/06/06 15:44:34  perki
* added controler
*
* Revision 1.2  2004/06/06 14:03:38  perki
* *** empty log message ***
*
* Revision 1.1  2004/06/05 15:39:25  perki
* *** empty log message ***
*
*/