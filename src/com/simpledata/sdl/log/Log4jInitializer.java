/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.sdl.log;

import java.io.ByteArrayInputStream;

import org.apache.log4j.Layout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Initializes Log4j subsystem from the log4j 
 * properties file which is located at Resources.log4jpropsPath(). 
 *
 * Static method does the initialization globally at first call, 
 * from then on the other calls are warned about and ignored. 
 * 
 * @version $Id: Log4jInitializer.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public class Log4jInitializer {
	
	private static final Logger m_log =Logger.getLogger(Log4jInitializer.class);
	private static boolean m_initialized = false; 
	
	/**
	 * Don't call. 
	 */
	private Log4jInitializer() {
	}
	
	private static synchronized boolean check() {
	    synchronized ( Log4jInitializer.class ) {
			if ( m_initialized ) {
				m_log.warn( "Log4j subsystem is already"+
				        "initialized, double init call.");
				return false;
			}
			// was not initialized, mark as initialized now, and quit synchro.
			m_initialized = true; 
		}
	    return true;
	}
	
	
	
	/** default init with the content of XML_INIT **/
	public static void doInit() {
	    doInitStr(CONFIG);
	}
	
	public static void doInitStr(String config) {
	    if (! check()) return;
	    ByteArrayInputStream bais = 
	        new ByteArrayInputStream(config.getBytes());
	    new DOMConfigurator().doConfigure(bais,  
	            LogManager.getLoggerRepository());
	}
	
	/** take a file as argument containing the xml for Log4j **/
	public static void doInit(String filename) {
	    if (! check()) return;
	    DOMConfigurator.configure(filename);
	}
	
	public static final String CONFIG_SUSH = "<?xml version= '1.0' encoding= 'UTF-8' ?> " +
	"<!DOCTYPE log4j:configuration SYSTEM 'log4j.dtd ' >  " +
	"<!-- $Id: Log4jInitializer.java,v 1.2 2007/04/02 17:04:30 perki Exp $ -->  " +
	"<log4j:configuration xmlns:log4j='http://jakarta.apache.org/log4j/'>  	" +
	"<appender name= 'DUMMY' class= 'org.apache.log4j.ConsoleAppender' >"+
	"		<param name='Target' value='System.err' />" +
	" 		<layout class= 'org.apache.log4j.PatternLayout' >" +
	"  			<param name= 'ConversionPattern' value= '%-5r %-5p [%t] %c{3}\t%m %l%n'/>" +
	" 		</layout >" +
	" 		<filter class= 'org.apache.log4j.varia.LevelRangeFilter' >" +
	" 			<param name= 'LevelMin' value= 'warn' />" +
	" 			<param name= 'LevelMax' value= 'fatal' />" +
	" 			<param name= 'AcceptOnMatch' value= 'true' />" +
	" 		</filter >" +
	"</appender>"+
	" 	 	<root >" +
	" 		<level value= 'debug' />" +
	" 		<appender-ref ref= 'DUMMY' />" +
	"</root>"+
	"  </log4j:configuration >";
	
	public static Layout getDefaultLayout() {
	    return new SimpleLayout();
	}
	
	public static final String CONFIG = "<?xml version= '1.0' encoding= 'UTF-8' ?> " +
			"<!DOCTYPE log4j:configuration SYSTEM 'log4j.dtd ' >  " +
			"<!-- $Id: Log4jInitializer.java,v 1.2 2007/04/02 17:04:30 perki Exp $ -->  " +
			"<log4j:configuration xmlns:log4j='http://jakarta.apache.org/log4j/'>  	" +
			"<appender name= 'APPLCATION' class= 'org.apache.log4j.RollingFileAppender' > " +
			"		<param name= 'File' value= 'application.log'/>" +
			" 		<param name= 'MaxFileSize' value= '100KB'/>" +
			" 		<param name= 'MaxBackupIndex' value= '1'/>" +
			" 		<layout class= 'org.apache.log4j.PatternLayout' >" +
			" 			<param name= 'ConversionPattern' value= '%-5p [%t] %c{3}\t%m %l%n'/>" +
			" 		</layout >" +
			" 		<filter class= 'org.apache.log4j.varia.LevelRangeFilter' >" +
			" 			<param name= 'LevelMin' value= 'error' />" +
			" 			<param name= 'LevelMax' value= 'fatal' />" +
			" 			<param name= 'AcceptOnMatch' value= 'true' />" +
			" 		</filter >" +
			" 	</appender >" +
			" 	 	<appender name= 'ECLPSERR' class= 'org.apache.log4j.ConsoleAppender' >" +
			" 		<param name='Target' value='System.err' />" +
			" 		<layout class= 'org.apache.log4j.PatternLayout' >" +
			"  			<param name= 'ConversionPattern' value= '%-5r %-5p [%t] %c{3}\t%m %l%n'/>" +
			" 		</layout >" +
			" 		<filter class= 'org.apache.log4j.varia.LevelRangeFilter' >" +
			" 			<param name= 'LevelMin' value= 'warn' />" +
			" 			<param name= 'LevelMax' value= 'fatal' />" +
			" 			<param name= 'AcceptOnMatch' value= 'true' />" +
			" 		</filter >" +
			" 	</appender >" +
			" 	 	<appender name= 'ECLPSOUT' class= 'org.apache.log4j.ConsoleAppender' >" +
			" 		<param name='Target' value='System.err' />" +
			" 		<layout class= 'org.apache.log4j.PatternLayout' >" +
			" 			<param name= 'ConversionPattern' value= '%-5r %-5p [%t] %c{3}\t%m %l%n'/>" +
			" 		</layout >" +
			" 		<filter class= 'org.apache.log4j.varia.LevelRangeFilter' >" +
			" 			<param name= 'LevelMin' value= 'debug' />" +
			" 			<param name= 'LevelMax' value= 'info' />" +
			" 			<param name= 'AcceptOnMatch' value= 'true' />" +
			" 		</filter >" +
			" 	</appender >" +
			" 	 	<root >" +
			" 		<level value= 'debug' />" +
			" 		<appender-ref ref= 'APPLCATION' />" +
			" 		<appender-ref ref= 'ECLPSERR' />" +
			" 		<appender-ref ref= 'ECLPSOUT' />" +
			" 	 	</root >" +
			"  </log4j:configuration >";
};