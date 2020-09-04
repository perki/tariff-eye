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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;

import com.simpledata.uitools.PrintUtilities;

import org.apache.log4j.Logger;

/**
 * HelpViewer.java
 *
 *
 * Created: Wed Mar 24 06:47:29 1999
 *
 * @author Ph.R.
 * @version 1.0
 */

public class HtmlViewer extends JFrame {
		private static final Logger m_log = Logger.getLogger( HtmlViewer.class );
	
    URL home = null;
    URL currentURL;
    MyHtmlPanel hp;
    BrowserToolBar tools = new BrowserToolBar();
    Stack forward = new Stack();
    Stack backward = new Stack();

	public static void main (String[] args) {
		if (args.length < 1) {
			System.out.println("java  HtmlViewer <url>");
		}
		try {
			HtmlViewer hv = new HtmlViewer(new URL(args[0]));
			hv.show();
		} catch (IOException e) {
			System.out.println("java  HtmlViewer <url>");
		}
	}
	
	
    public HtmlViewer(URL url) {
		home = url;
        currentURL=url;
		initGUI();
    }

    public HtmlViewer(String title, URL url) {
		super(title);
        home = url;
        currentURL=url;
		initGUI();
    }

    public void setHome(URL url) {
        home = url;
    }
    public void initGUI() {
        try {
			hp = new MyHtmlPanel(home);
            System.out.println("Pushing home: "+home);
        	getContentPane().add(hp, BorderLayout.CENTER);
        } catch (IOException e) {
            System.out.println("(HtmlViewer) Cannot open file: "+e.getMessage());
            // Should display it in the window !!!
        }
        tools.initGUI();
		getContentPane().add(tools, BorderLayout.NORTH);
        setBounds(new java.awt.Rectangle(0, 0, 485, 572));
        setSize(new java.awt.Dimension(600,600));

		/**
        setBackIcon(new javax.swing.ImageIcon(HtmlViewer.class.getResource("/resources/images/browser_back.png")), "Back");
        setForIcon(new javax.swing.ImageIcon(HtmlViewer.class.getResource("/resources/images/browser_back.png")), "Forward");
		setHomeIcon(new javax.swing.ImageIcon(HtmlViewer.class.getResource("/resources/images/browser_forward.png")), "Home");
        setPrintIcon(new javax.swing.ImageIcon(HtmlViewer.class.getResource("/resources/images/browser_print.png")), "Print");
		**/
    }


    public void goTo(URL u) {
        hp.goToNew(u);
    }

    public void setBackIcon(ImageIcon i, String desc) {
        tools.bleft.setIcon(i);
        tools.bleft.setText(desc);
    }

    public void setForIcon(ImageIcon i, String desc) {
        tools.bright.setIcon(i);
        tools.bright.setText(desc);
    }
    public void setHomeIcon(ImageIcon i, String desc) {
	tools.bhome.setIcon(i);
        tools.bhome.setText(desc);
    }
	
	 public void setPrintIcon(ImageIcon i, String desc) {
        tools.bprint.setIcon(i);
        tools.bprint.setText(desc);
    }
	
	public void setApplicationIcon(ImageIcon i) {
		this.setIconImage(i.getImage());
	}
	
	
    /**
     * HtmlPanel.java
     *
     *
     * Created: Sat Aug 22 14:16:27 1998
     *
     * @author Philippe Rochat
     * @version 1.0
     */
    class MyHtmlPanel extends JPanel implements HyperlinkListener {
      JEditorPane html;
        
      public MyHtmlPanel(URL url) throws IOException {
    	setLayout(new BorderLayout());
    	html = new JEditorPane(url);
    	home = url;
    	html.setEditable(false);
    	html.addHyperlinkListener(this);
    	JScrollPane scroller = new JScrollPane();
    	JViewport vp = scroller.getViewport();
    	vp.add(html);
    	vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE );
    	add(scroller, BorderLayout.CENTER); 
      }
      /**
       * Notification of a change relative to a 
       * hyperlink.
       */
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            goToNew(e.getURL());
        }
      }

      void goBack() {
      	m_log.debug("Back");
        if(!backward.empty()) {
			forward.push(currentURL);
            currentURL = (URL)backward.pop();
			linkActivated();
            tools.bright.setEnabled(true);
        }
        if(backward.empty())
            tools.bleft.setEnabled(false);
      }

      void goFor() {
      	m_log.info("Going forward");
        if(!forward.empty())
        	goTo((URL)forward.pop());
        if(forward.empty())
            tools.bright.setEnabled(false);
      }

      public void goToNew(URL u) {
      	m_log.info("Going to new url");
        goTo(u);
		forward.clear();
        tools.bright.setEnabled(false);
      }

      void goTo(URL u) {
      	m_log.info("Going to url: "+u);
		backward.push(currentURL);
        tools.bleft.setEnabled(true);
		currentURL=u;
		linkActivated();
	  }
	  
	  void print() {
		  PrintUtilities.printComponent(html);
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
    class BrowserToolBar extends JToolBar {
		JButton bleft = new JButton();
        JButton bright = new JButton();
        JButton bhome = new JButton();
		JButton bprint = new JButton();
		
        public void initGUI() {
			bleft.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hp.goBack();
				}
			});
			bright.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hp.goFor();
				}
			});
			bhome.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hp.goToNew(home);
				}
			});
			bprint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					hp.print();
				}
			});
       		bhome.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        	bhome.setMargin(new java.awt.Insets(0, 0, 0, 0));
        	bhome.setVerticalTextPosition(SwingConstants.BOTTOM);
        	bhome.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            add(bhome);
       		bleft.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        	bleft.setMargin(new java.awt.Insets(0, 0, 0, 0));
        	bleft.setVerticalTextPosition(SwingConstants.BOTTOM);
        	bleft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            add(bleft);
       		bright.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        	bright.setMargin(new java.awt.Insets(0, 0, 0, 0));
        	bright.setVerticalTextPosition(SwingConstants.BOTTOM);
        	bright.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            add(bright);
			bprint.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 10));
        	bprint.setMargin(new java.awt.Insets(0, 0, 0, 0));
        	bprint.setVerticalTextPosition(SwingConstants.BOTTOM);
        	bprint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            add(bprint);
            bright.setEnabled(false);
            bleft.setEnabled(false);
        }
    }
} // HelpViewer




