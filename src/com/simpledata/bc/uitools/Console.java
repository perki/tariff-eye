/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/* CopyRight Simple Data 2003 
 * 
 * $Id: Console.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */ 

package com.simpledata.bc.uitools;

import java.awt.Rectangle;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import bsh.ClassPathException;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.classpath.ClassManagerImpl;
import bsh.util.JConsole;
import bsh.util.NameCompletionTable;

import com.simpledata.bc.*;
import com.simpledata.bc.Resources;

/**
* This ui element is supposed to be added at bottom of apps.
* See constant defined to used method setStatus.
* Do not instantiate, but use getStatusDisplay.
*/

public class Console extends JInternalFrame {
    private static final Logger m_log = Logger.getLogger( Console.class );
	
    
	JTextArea logPane = new JTextArea();
	JConsole jconsole = null;
	Interpreter interp = null;
	
	/**
	* Initialisation script will be passed to any console
	*/
	private final static String initScript = 	
		"import com.simpledata.bc.*;"+
		"import bsh.util.*;"+
		"String getBshPrompt() { return \"bc % \"; };"+
		"rootNamespace = this.namespace;"+
		"public void ldebug() { " +
		"setNameSpace(rootNamespace) ; source(\"src/bsh/debug.bsh\");};"+
		"public void z() { " +
		"setNameSpace(rootNamespace) ; source(\"src/bsh/z.bsh\"); };";
	
	public static Console me=null;
    public final static int STATUS_OK = 1;
	public final static int STATUS_WAIT = 2; 
    public final static int STATUS_NOTOK = 3;
    public final static int STATUS_ERROR = 4;

    public static boolean debugB;
    
   	JScrollPane scroller;
	
	/**
     * Constructur shoudln't be called.
     */
	private Console(boolean debug) {
		super("Console", true, true, true, true);
		setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		this.setFrameIcon(Resources.iconConsole);
		Rectangle defSize = (Rectangle)BC.getParameter(
		        Params.KEY_CONSOLE_BOUNDS,
		        Rectangle.class);
		setSize(defSize.getSize());
		//setSize(500,400);
		setLocation(defSize.getLocation());
		//setLocation(3,3);
        Console.debugB=debug;
        me = this;
        
		//----------- Layout
		//this.getContentPane().setLayout(new BorderLayout());
		JSplitPane jSplitPane1 = new JSplitPane();
		jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		//----------- Logpane
		
        logPane.setEditable(false);
        scroller = new JScrollPane();
        JViewport vp = scroller.getViewport();
        vp.add(this.logPane);
        vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		
		jSplitPane1.setLeftComponent(scroller);
		
		jSplitPane1.setDividerLocation(0.5);

		//----------- Console
		
		jconsole = new JConsole();
		interp = new Interpreter( jconsole );
		
		
		try {
			interp.eval(initScript);
		} catch (bsh.EvalError e) {
			m_log.error("Console: error interpreting init Script",e);
		}
		new Thread( interp ).start();
		
		// provide name completion for console

        // utility to hold the names
        NameCompletionTable nct = new NameCompletionTable();

        // add names from the namespace (e.g. vars)
        nct.add( interp.getNameSpace() );
		
        // add names from the claspath
        try {
            ClassManagerImpl bcm = (ClassManagerImpl) interp.getNameSpace().getClassManager();
            if ( bcm != null ) {
                nct.add( bcm.getClassPath() );
            }
        } catch ( ClassPathException e ) {
            m_log.error("Console: classpath exception in name compl:",e);
        }

        jconsole.setNameCompletion( nct );

        // end setup name completion

		
       
	   
	   jSplitPane1.setRightComponent(jconsole);
	   jSplitPane1.setDividerLocation(250);
	   getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);
	}
	
   	/**
     * Return a JPanel that will display status
     */
    static public JInternalFrame getDisplay() {
        getDisplay(false);
        me.setVisible(true);
        return me;
    }

    /**
     * Return a JPanel that will display status
     * @param debug if set to true: messags will be sent to standard output
     */
    static public JInternalFrame getDisplay(boolean debug) {
        if(me==null) {
            me = new Console(debug);
        }
        if (!me.isShowing()) {
        	me.show();
        }
		me.jconsole.requestFocus();
        return me;
    }

   
	
	/** evaluate some script in the console **/
	public static void evaluate(String script) {
		if(me == null) {
		    m_log.error("cannot evaluate console is down");
			return;
		}
		try {
			me.interp.eval(script);
		} catch (EvalError e) {
		    m_log.error("eval error",e);
		}
	}

	public static void addLog(String msg) {
		if(me != null) {
			me.logPane.append(msg+"\n");
			
			SwingUtilities.invokeLater(new Runnable() {		
				public void run() {
					me.scroller.getVerticalScrollBar().setValue(me.scroller.getVerticalScrollBar().getMaximum());
				}
			});
			
		} else {
			System.out.println(msg);
		}
	}
	
	public static void focusOnText() {
		me.jconsole.grabFocus();
	}
	
	public void dispose() {
		super.dispose();
		me = null;
	}

}

/* 
 * $Log: Console.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.28  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.27  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.26  2004/09/22 09:05:56  jvaucher
 * Fixed some problems with the load/save system
 *
 * Revision 1.25  2004/07/09 19:10:25  carlito
 * *** empty log message ***
 *
 * Revision 1.24  2004/06/06 15:44:34  perki
 * added controler
 *
 * Revision 1.23  2004/04/09 07:16:52  perki
 * Lot of cleaning
 *
 * Revision 1.22  2004/03/22 14:32:30  carlito
 * *** empty log message ***
 *
 * Revision 1.21  2004/03/12 02:52:51  carlito
 * *** empty log message ***
 *
 * Revision 1.20  2004/02/19 20:00:57  perki
 * The dream is coming a little bit more true
 *
 * Revision 1.19  2004/02/17 11:39:24  carlito
 * *** empty log message ***
 *
 */
