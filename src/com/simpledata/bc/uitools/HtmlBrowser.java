/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Author : Kaspar
 * Created on 30 juin 2004
 * $Id: HtmlBrowser.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

/**
 * 
 */
import java.awt.*;
import java.io.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.webcontrol.ControlServer;

/**
 * TODO XXX
 * Need to be removed
 */

public class HtmlBrowser extends JInternalFrame implements HyperlinkListener  {
    
    private static final Logger m_log = Logger.getLogger( HtmlBrowser.class );
    
	/**
	 * Construct a statistics window and have the title given.
	 * You should use this constructor for any stats window that
	 * you open. with a default size of 500x400
	 * @param firstUrl		The first URL that the browser should display
	 * @param title		    Title of the window to open.
	 */
	public HtmlBrowser(String firstUrl, String title) {
	    this(firstUrl, title, new Dimension(500,400));
	}
	
	/**
	 * Construct a statistics window and have the title given.
	 * You should use this constructor for any stats window that
	 * you open.
	 * @param firstUrl		The first URL that the browser should display
	 * @param title		    Title of the window to open.
	 * @param size           initial dimension for the browser
	 */
	public HtmlBrowser(String firstUrl, String title, Dimension size) {
	    
		super(title, true, true, true, true);
		// resizable, closable, maximizable, iconifyable
		
		setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		
		this.backColor = new java.awt.Color(102,153,255);

		if (firstUrl != null)
			m_url = firstUrl;
		else {
		    m_log.error("You must pass an url not null");
		    m_url = "http://www.simpledata.ch";
		}

		m_layers = new JLayeredPane();
		m_layers.setPreferredSize(size);
		m_layers.setLayout(new BorderLayout());
		m_layers.setBackground(backColor);

		// construct a html editor pane
		initPane(m_bpanes[0] = new XBrowser());
		initPane(m_bpanes[1] = new XBrowser());

		// add it to the content pane
		loadURL(m_url);

		// add the two browsers as layers
		m_layers.add(m_bpanes[0], BorderLayout.CENTER);
		m_layers.add(m_bpanes[1], BorderLayout.CENTER);

		// add layers to content pane
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(backColor);
		contentPane.setForeground(backColor);
		contentPane.add(m_layers, BorderLayout.CENTER);

		setSize(size);

		// Ensure that both layered panes are correctly refreshed 
		// for the first time
		refreshBrowser();
		
		refreshBrowser();	    
	    
	}

	/**
	 * When a hyperlink gets manipulated, this function is called
	 * because of the HyperlinkListener.
	 * @param e				Hyperlink Event
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			m_url = e.getURL().toString();
			refreshBrowser();
		}
	}

	/**
	 * Function is called by the egg timer every n seconds. This keeps the
	 * statistics up to date.
	 */
	public void refreshBrowser() {
		// note that the active pane is the one that is not visible.
		// it will be set visible once it has finished loading !
		boolean done = false;
		while (! done) {
			done = m_bpanes[m_activePane].isDone();
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				
			}
		}
		// has the last refresh terminated ?
		if (done) {
			int active = m_activePane==1 ? 0 : 1;
			int other = m_activePane;

			m_layers.moveToFront(m_bpanes[other]);
			m_layers.moveToBack(m_bpanes[active]);

			m_activePane = active;

			// and start a hidden refresh
			m_bpanes[active].forceReload();
			loadURL(m_url);

			repaint();
		}
	}

	/**
	 * Refresh the browser with the url given. This function
	 * always accesses the active pane, meaning the one that
	 * is currently hidden.
	 * @param url			Url to load
	 */
	private void loadURL( String url ) {
	    
		try {
			m_url = url;
			m_bpanes[m_activePane].setPage(url);
		} catch (java.io.IOException ioe) {
		    m_log.error("La ressource ne peut �tre acc�d�e: "+url);
		}
	}

	/**
	 * Set up the pane given for display. This is done initially and only
	 * once.
	 * @param p				editor pane to set up
	 */
	private void initPane(JEditorPane p) {
		if (p == null) return;

		p.setEditable(false);
		p.setVisible(true);
		p.setBackground(backColor);
		p.addHyperlinkListener(this);
	}

	private String m_url;
	private int m_activePane = 0;

	// html display
	private XBrowser m_bpanes[] = new XBrowser[2];
	// ' double buffering ' - the displays are simply kept in two layers
	private JLayeredPane m_layers;

	private java.awt.Color backColor;
}

/**
 * Enhanced Editor Pane, supports reload and other stuff.
 * Tricks in here are from the open source project
 * http://cjos.sourceforge.net/redist/xbrowser/xbrowser.renderer.custom.XCustomRenderer.src.html
 */
class XBrowser extends JEditorPane {
    private static final Logger m_log = Logger.getLogger( XBrowser.class );
    
    
	public void forceReload()
	{
	   if( getDocument()!=null )
		{
			// Force to reload !!
			getDocument().putProperty(Document.StreamDescriptionProperty, null);
		}
	}

	/**
	 * Retrieve an <code>InputStream</code> for a given URL.
	 * This method is used internally to construct an input stream from
	 * the url. This override calls the old method but keeps the input
	 * stream for the isDone procedure.
	 */
	protected InputStream getStreamOld(URL page) throws IOException {
		InputStream is = super.getStream(page);

		m_loading = true;
		m_currStream = is;

		return is;
	}
	
	public void setPage(URL u) throws IOException {
	    //	  check if this URL applies to me
	    if (! u.toString().startsWith(ControlServer.BASE_URL)) {
	       super.setPage(u);
	       return;
	    }
	     
	   if (! (getEditorKit() instanceof HTMLEditorKit))
	       setEditorKit(new HTMLEditorKit());
	   
	   setText(BC.bc.getControlServer().getDocument(u));
	}
	
	
	/**
	 * Retrieve an <code>InputStream</code> for a given URL.
	 * This method is used internally to construct an input stream from
	 * the url. This override calls the old method but keeps the input
	 * stream for the isDone procedure.
	 */
//	protected InputStream getStreamOld2(final URL page) throws java.io.IOException {
//	   
//	    URL temp= BC.bc.getControlServer().getURLForJEditorPane(page);   
//     
//        InputStream is = super.getStream(temp);
//	    
//		m_loading = true;
//		m_currStream = is;
//		return is;
//	}
	


	/**
	 * Checks if the last document has been loaded successfully.
	 * @return		true if the JEditorPane has finished loading
	 */
	public boolean isDone() {
		if (!m_loading)
			return true;

		try {
			if (m_loading && 
					!( m_currStream!=null && m_currStream.available()>0 )) {
				m_loading = false;
				return true;
			}
		} catch (java.io.IOException ioe) {
			// this means the stream is closed, so be happy.
			m_currStream = null;
			return true;
		}

		return false;
	}

	private boolean m_loading = false;
	private InputStream m_currStream;

};



/*
 * $Log: HtmlBrowser.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.12  2004/10/18 10:34:53  carlito
 * editor pane without border
 *
 * Revision 1.11  2004/09/23 16:29:03  perki
 * Pfuuuu--- got the web interfac to work
 *
 * Revision 1.10  2004/09/23 15:01:56  perki
 * *** empty log message ***
 *
 * Revision 1.9  2004/09/23 14:59:25  perki
 * *** empty log message ***
 *
 * Revision 1.8  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.7  2004/09/23 08:21:26  perki
 * removed all the code relative to the WebServer
 *
 * Revision 1.6  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.5  2004/09/22 14:59:03  perki
 * Web server off
 *
 * Revision 1.4  2004/08/02 07:59:52  kaspar
 * ! CVS merge
 *
 * Revision 1.3  2004/07/01 15:04:50  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/06/30 17:34:26  carlito
 * hide on close
 *
 * Revision 1.1  2004/06/30 17:33:25  carlito
 * MiniBrowser replaced by Kaspar Browser.
launchable from desktop
 *
 */