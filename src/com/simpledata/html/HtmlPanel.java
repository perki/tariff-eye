/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.html;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import com.simpledata.uitools.PrintUtilities;

import org.apache.log4j.Logger;

/**
 * A simpler HTML Panel
 */
public class HtmlPanel extends JPanel implements HyperlinkListener {
	private static final Logger m_log = Logger.getLogger( HtmlPanel.class );
	
	JEditorPane html;
	URL home = null;
	URL currentURL;
	Stack forward = new Stack();
	Stack backward = new Stack();
	
	public HtmlPanel(JEditorPane jp) {
	    this(null,jp);
	}
	
	public HtmlPanel(URL url) throws IOException {
	    this(url,new JEditorPane(url));
	}
	
	
	public HtmlPanel(URL url,JEditorPane jp) {
		setLayout(new BorderLayout());
		html = jp;
		home = url;
		html.setEditable(false);
		html.addHyperlinkListener(this);
		JScrollPane scroller = new JScrollPane();
		JViewport vp = scroller.getViewport();
		vp.add(html);
		vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE );
		add(scroller, BorderLayout.CENTER); 	
	}
	
	public JEditorPane getEditorPane() {
	    return html;
	}
	
	
	/**
	 * Notification of a change relative to a 
	 * hyperlink.
	 */
	int i = 0;
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			goToNew(e.getURL());
		}
	}
	
	void goBack() {
		m_log.info( "Back" );
		if(!backward.empty()) {
			forward.push(currentURL);
			currentURL = (URL)backward.pop();
			linkActivated();
		}
	}
	
	void goFor() {
		m_log.info( "Going forward" );
		if(!forward.empty())
			goTo((URL)forward.pop());
	}
	
	public void goToNew(URL u) {
		m_log.info( "Going to new url" );
		goTo(u);
		forward.clear();
	}
	
	public void goTo(String url) {
	 try {
	        goToNew(new URL(url));
	    } catch (MalformedURLException e) {
	        m_log.error("",e);
	    } 
	}
	
	void goTo(URL u) {
		m_log.info( "Going to url: "+u );
		backward.push(currentURL);
		currentURL=u;
		linkActivated();
	}
	
	void print() {
		PrintUtilities.printComponent(html);
	}
	
	public void setText(String text) {
		html.setText(text);
	}
	
	/**
	 * Follows the reference in an
	 * link.  The given url is the requested reference.
	 * By default this calls <a href="#setPage">setPage</a>,
	 * and if an exception is thrown the original previous
	 * document is restored and a beep sounded.  If an 
	 * attempt was made to follow a link, but it represented
	 * a malformed url, this method will be called with a
	 * null argument.
	 *
	 */
	public void linkActivated() {
		Cursor c = html.getCursor();
		Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
		html.setCursor(waitCursor);
		SwingUtilities.invokeLater(new PageLoader(currentURL, c));
	}
	
	
	
	/**
	 * temporary class that loads synchronously (although
	 * later than the request so that a cursor change
	 * can be done).
	 */
	class PageLoader implements Runnable {
		
		PageLoader(URL u, Cursor c) {
			url = u;
			cursor = c;
		}
		public void run() {
			if (url == null) {
				// restore the original cursor
				html.setCursor(cursor);
				// PENDING(prinz) remove this hack when 
				// automatic validation is activated.
				Container parent = html.getParent();
				parent.repaint();
			} else {
				Document doc = html.getDocument();
				try {
					html.setPage(url);
				} catch (IOException ioe) {
					html.setDocument(doc);
					getToolkit().beep();
				} finally {
					// schedule the cursor to revert after
					// the paint has happended.
					url = null;
					SwingUtilities.invokeLater(this);
				}
			}
		}
		URL url;
		Cursor cursor;
	}  
}

/*

* $Log: HtmlPanel.java,v $
* Revision 1.2  2007/04/02 17:04:31  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:45  perki
* First commit on sourceforge
*
* Revision 1.7  2004/11/26 14:06:41  perki
* *** empty log message ***
*
* Revision 1.6  2004/11/26 10:05:05  jvaucher
* Begining of TariffEyeInfo feature
*
* Revision 1.5  2004/09/27 14:02:30  perki
* Browser Opener
*
* Revision 1.4  2004/09/24 09:57:01  perki
* *** empty log message ***
*
* Revision 1.3  2004/09/24 07:03:03  perki
* Hurm..
*
* Revision 1.2  2004/09/04 18:10:15  kaspar
* ! Log.out -> log4j, last part
*
* Revision 1.1  2004/06/06 16:25:47  perki
* *** empty log message ***
*
 */