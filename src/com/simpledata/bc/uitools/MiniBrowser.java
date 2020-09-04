/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: MiniBrowser.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.webcontrol.ControlServer;
import com.simpledata.html.HtmlPanel;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * A mini Web browser.. 
 */
public class MiniBrowser extends JInternalFrame {
    private static final Logger m_log = Logger.getLogger( MiniBrowser.class );
	
	HtmlPanel hp;
	MyEditorPane mep;
	
	public MiniBrowser() {
		super(Lang.translate("Mini Browser"), true, true, true, true);
		
		setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		
		this.setFrameIcon(Resources.iconConsole);
		setSize(500,400);
		setLocation(3,3);
		
		hp = getBCHtmlPanel();
		mep = (MyEditorPane) hp.getEditorPane();
		
		// add a home toolbar / buttons
		JMenuBar jtb = new JMenuBar();
		JMenuItem home = new JMenuItem(Resources.iconHome);
		home.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                goToHome();
            }});
		jtb.add(home);
		setJMenuBar(jtb);
		
		getContentPane().add(hp, java.awt.BorderLayout.CENTER);
		
	}
	
	public void setPage(String url) {
	   hp.goTo(url);
	}
	
	public void setHTML(final String content) {
	    if (! mep.firstPageLoaded) {
	        Thread t = new Thread() {
	            public void run() {
	                while (! mep.firstPageLoaded) {
	                    try {
	                        Thread.sleep(1000);
	                        m_log.warn("Waiting for first page to load");
	                    } catch (InterruptedException e1) {
	                        m_log.error("",e1);
	                    }
	                }
	                try {
	                    mep.setHTML(content);
	                } catch (IOException e) {
	                    m_log.error("Failed :",e);
	                }
	            }
	        };
	        t.start();
	        return;
	    }
	  
	    try {
            mep.setHTML(content);
        } catch (IOException e) {
           m_log.error("Failed :",e);
        }
	}
	
	public static HtmlPanel getBCHtmlPanel() {
	    return new HtmlPanel(new MyEditorPane());
	}

    /**
     * go To the HomePage
     */
    public void goToHome() {
        setPage(ControlServer.BASE_URL+"Welcome.jsp");
    }
	
}

class MyEditorPane extends JEditorPane {
    private static final Logger m_log = Logger.getLogger( MyEditorPane.class );
    
    MyEditorPane me;
    
    protected boolean firstPageLoaded = false;
    
    public MyEditorPane() {
        super();
        setBorder(new LineBorder(Color.WHITE, 0));
    }
    
    /** change content of this pane with this html **/
    public void setHTML(final String s)  throws IOException {
        // generate a random URL to be sure not to be trapped in already loaded
        // document handled by URL
        getDocument().putProperty(
                Document.StreamDescriptionProperty,
                new URL("http://"+Math.random()));
        
        if (! (getEditorKit() instanceof HTMLEditorKit)) {
            setEditorKit(new HTMLEditorKit());
        }
      
        if (me == null) me = this;

        final Runnable r =  new Runnable() {
            public void run() {
                me.setText(s);
                firstPageLoaded = true;
            }};

        if (SwingUtilities.isEventDispatchThread()) {
            Worker.post(new Job(){
                public Object run() { r.run();
                    return null;
                }});
           
        } else {
            SwingUtilities.invokeLater(r);
         
        }
	}
    
    

    public void setPage(URL u) throws IOException {
    
        //	  check if this URL applies to me
        if (! u.toString().startsWith(ControlServer.BASE_URL)) {
            super.setPage(u);
            return;
        }

        //    maybe this is a direct command request I should handle 
        if (u.toString().startsWith(ControlServer.BASE_URL_DIRECT)) {
            BC.bc.getControlServer().handleDirect(u);
            
            return; // nothing to do
        }
        
        //      to be sure I'm ready to change my text
        setHTML(BC.bc.getControlServer().getDocument(u));
    }
        
        
}

/*
 * $Log: MiniBrowser.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.18  2004/11/26 15:43:31  perki
 * *** empty log message ***
 *
 * Revision 1.17  2004/11/26 14:01:03  perki
 * *** empty log message ***
 *
 * Revision 1.16  2004/11/26 13:48:35  perki
 * *** empty log message ***
 *
 * Revision 1.15  2004/11/26 11:15:18  perki
 * *** empty log message ***
 *
 * Revision 1.14  2004/11/26 10:06:00  jvaucher
 * Begining of TariffEyeInfo feature
 *
 * Revision 1.13  2004/10/18 10:34:53  carlito
 * editor pane without border
 *
 * Revision 1.12  2004/09/28 09:41:51  perki
 * Clear action added to CreatorGold
 *
 * Revision 1.11  2004/09/28 09:06:54  perki
 * Lock in splash maybe solved
 *
 * Revision 1.10  2004/09/27 13:19:10  perki
 * register Dialog OK
 *
 * Revision 1.9  2004/09/24 10:08:28  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/09/24 00:09:08  kaspar
 * + Added 'Armored File Format' which is for now just encoding
 *   using XStream (and containing some minor hickups).
 * + Added Jars that implement XStream library.
 * ! New SDL
 * ! Bugfix in MiniBrowser.java; must have been debug code that was
 *   only partially added to CVS
 *
 * Revision 1.7  2004/09/23 16:29:03  perki
 * Pfuuuu--- got the web interfac to work
 *
 * Revision 1.6  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.5  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.4  2004/09/10 16:51:05  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/08/02 07:59:52  kaspar
 * ! CVS merge
 *
 * Revision 1.2  2004/06/07 14:32:43  perki
 * *** empty log message ***
 *
 * Revision 1.1  2004/06/06 17:28:09  perki
 * *** empty log message ***
 *
 */