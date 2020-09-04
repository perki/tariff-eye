/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ModalJPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */

package com.simpledata.bc.uitools;

import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MouseInputAdapter;

import org.apache.log4j.Logger;

/**
* Copyright 2004 SimpleData Sarl http://simpledata.ch<BR>
* Provided "As This" Can be used or modified freely as long as a
* reference to it's initial author and to SimpleData is kept.<BR><BR>
* 
* <HR>
* A Container that shows up on a Frame as Modal<BR>
* Was initially written to get JInternalFrame as modal 
* but it finally shows a great way of having floating 
* Modal Components<BR>
* <BR><BR>
* TO USE THIS CODE REMOVE all references to "m_log"
* 
* <BR><BR>
* 
* Known Problem: When using an alpha background to shade the frame when 
* going modal. Moving the window leaves lots of messy pixels.<BR>
* 
* <BR><BR>
* Known BUG: doesn't grab all keyboard strokes 
* (exemple ALT+... for menus)<BR>
* @author Pierre-Mikael Legris http://simpledata.ch
*/
public class ModalJPanel implements AWTEventListener {
    
    private static final Logger m_log = Logger.getLogger( ModalJPanel.class );
	

	// to avoid loops
	private boolean closing= false;

	// the Frame I belong To
	private JFrame frame;

	// the layered pane of frame
	private JLayeredPane jlpa;

	// the Panel I use for drawing everything
	private JPanel jp;

	// I don't not trust isVisible() call ;)
	private boolean myVisible;

	// The Container I'm displaying
	private Container content;

	/**
	 * Create a ModalJPanel
	 * @param content the Component you want to display
	 * @param origin the element that will be used as origin
	 * @param bgColor (null for transparent) the Color of the background. 
	 * You need to specify the alpha to get transparency
	 */
	public ModalJPanel(
		final Container content,
		Component origin,
		final Color bgColor) {
		this.content= content;

		assert origin != null : "I asked for a component !";
		
		myVisible= false;
		// get the first JFrame containing this Component

		if (origin instanceof JFrame) {
			frame= (JFrame) origin;
		} else {
			frame=
				(JFrame) SwingUtilities.getAncestorOfClass(
					JFrame.class,
					origin);
			if (frame == null) {
				m_log.error(
					"Cannot find a JFrame for component:" + origin);
				return;
			}
		}

		jlpa= frame.getLayeredPane();

		// The underlaying Panel that will set the background and catch
		// the mouse events
		
		if (bgColor == null) {
			jp = new JPanel();
			jp.setOpaque( false );	
		} else {
			jp = new TranslucentJPanel( bgColor);
		}
		
		//	add this to the modal layer
		jlpa.add( jp, JLayeredPane.MODAL_LAYER);
		
		// set of this glass pane to be verrrrry large
		jp.setSize(new Dimension(100000,100000));
		
		
		Point position = frame.getContentPane().getLocation();
		//if it has a JMenuBar get JMenuBar position
		if (frame.getJMenuBar() != null) {
			Point posBar = frame.getJMenuBar().getLocation();
			// if the bar is on top left corner modify position to overlap it
			if (posBar.getX() <= position.getX())
				position.setLocation(posBar.getX(),position.getY());
			if (posBar.getY() <= position.getY())
				position.setLocation(position.getX(),posBar.getY());
		}
		jp.setLocation(position);
		
		

		//Rectangle fBounds = frame.getContentPane().getBounds();
		//jp.setBounds(fBounds);
		
		jp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		// Attach modal behavior to frame
		//	Associate dummy mouse listeners
		// Otherwise mouse events pass through
		MouseInputAdapter adapter= new MouseInputAdapter() {};
		jp.addMouseListener(adapter);
		jp.addMouseMotionListener(adapter);
		jp.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {}
		});

		// add this to the modal layer
		jlpa.add(content, JLayeredPane.MODAL_LAYER);

		content.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	* close 
	*/
	public void close() {
		// Avoid infinite close loops
		if (closing)
			return;
		closing= true;

		fireActionEvent();

		// unregister listeners 
		setVisible(false);

		// release the contents
		jlpa.remove( content );
		jlpa.remove( jp );

		// release my components
		content.removeAll();
		content= null;

		frame.repaint();
	}

	/**
	* Override setVisible to install/remove key events 
	* hook that will allow
	* us to disable key events when the glass pane is visible.
	*/
	public void setVisible(boolean visible) {
		Toolkit tk= Toolkit.getDefaultToolkit();

		//	visible called and not visible yet
		if (visible && (!myVisible)) {
			myVisible= true;

			// Grab key events
			tk.addAWTEventListener(this, AWTEvent.KEY_EVENT_MASK);

			content.requestFocus();
			frame.repaint();
		}

		// ! visible called and I'm visible
		if ((!visible) && myVisible) {
			myVisible= false;

			// Release key events
			tk.removeAWTEventListener(this);

		}
		content.setVisible(visible);
	}

	/* 
	* To grab all key events
	* @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
	*/
	public void eventDispatched(AWTEvent event) {
		if (event instanceof KeyEvent
			&& event.getSource() instanceof Component) {
			Component destination= (Component) event.getSource();
			// Destination is me or one of my fellows Components :) nice.. 
			if (SwingUtilities.isDescendingFrom(destination, content))
				return;

			JFrame master=
				(JFrame) SwingUtilities.getAncestorOfClass(
					JFrame.class,
					destination);
			// Consume events only for our window
			if (master == this.frame)
				 ((KeyEvent) event).consume();
		}
	}

	/**
	* Place smartly a component on a Container. 
	* by preventing it from going out of a Frame.<BR>
	* Note: c.getSize() will be called and assumed as the final size
	* as well as c.getParent().getSize()
	* <B>Warning:</B> this method 
	* MUST BE CALLED <B>AFTER</B> the Component 
	* has been added!! and Container should have a null layout
	* @param desiredLocation is the prefered Location on this container
	* @param c the Component you want to display
	*/
	public static void smartLocation(Point desiredLocation, Component c) {
		Container master= c.getParent();
		// find out my JFrame
		if (master == null)
			return; // failed no Container

		Dimension masterD= master.getSize();
		Dimension cD= c.getSize();

		Point result= (Point) desiredLocation.clone();

		if ((result.getY() + cD.getHeight()) > masterD.getHeight())
			result.setLocation(
				result.getX(),
				masterD.getHeight() - cD.getHeight());
		if ((result.getX() + cD.getWidth()) > masterD.getWidth())
			result.setLocation(
				masterD.getWidth() - cD.getWidth(),
				result.getY());
		// allways prefers to left corner
		if (result.getX() < 0)
			result.setLocation(0d, result.getY());
		if (result.getY() < 0)
			result.setLocation(result.getX(), 0d);
		// done
		c.setLocation(result);
	}

	/**
	* Do the Math for originLocation + delta
	*/
	public static Point getDesiredLocation(
		Component c,
		Component origin,
		Point delta) {
		// set the location near it's origin
		Point pointOrigin=
			SwingUtilities.convertPoint(origin, 0, 0, c.getParent());
		Point desiredLocation= new Point();
		desiredLocation.setLocation(
			pointOrigin.getX() + delta.getX(),
			pointOrigin.getY() + delta.getY());
		return desiredLocation;
	}

	//-------------------- CREATION ----------------------------//

	/**
	 * Warp this JInternalFrame and displays it
	 * <B>Placement</B><BR>
	 * Assuming pointOrigin is the position of origin on the Frame:<BR>
	 * the component will be placed at <BR>
	 * (pointOrigin.x + delta.x, pointOrigin.y + delta y)<BR><BR>
	 * @param jif the JINternalFrame
	 * @param origin the origin component to display it
	 * @param delta the delta to apply to origin position.
	 * @param bgColor the background color with alpha, null for transparency
	*/
	public static ModalJPanel warpJInternalFrame(
		final JInternalFrame jif,
		Component origin,
		Point delta,
		Color bgColor) {

		
		//	get a ModalJpane containing this JIF
		final ModalJPanel result= new ModalJPanel(jif, origin, bgColor);

		jif.addInternalFrameListener(new InternalFrameAdapter() {
			// add a listener to call ModalJpane.close();
			public void internalFrameClosed(InternalFrameEvent e) {
				result.close();
			}
			public void internalFrameClosing(InternalFrameEvent e) {
				result.close();
			}
		});
		
		
		
		
		jif.pack();
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        jif.show();
		        jif.setVisible(true);
		    }
		});
		
		

		//		setThis item visible
		result.setVisible(true);

		// for a mysterious reason placement, position and
		// getPrefferdSize() gives valid result
		// only when result is Visible.. ???  

		jif.setMinimumSize(jif.getPreferredSize());
		smartLocation(getDesiredLocation(jif, origin, delta), jif);
		return result;
	}

	/**
	* Create a floating Modal JInternalFrame<BR>
	* <B>Placement</B><BR>
	* Assuming pointOrigin is the position of origin on the Frame:<BR>
	* the component will be placed at <BR>
	* (pointOrigin.x + delta.x, pointOrigin.y + delta y)<BR><BR>
	* @param component the component to draw
	* @param origin the origin component to display it
	* @param delta the delta to apply to origin position.
	* @param closable true if you want this frame to have a close button
	* @param frameIcon can be null
	* @param bgColor the background color with alpha, null for transparency
	*/
	public static ModalJPanel createSimpleModalJInternalFrame(
		Component component,
		Component origin,
		Point delta,
		boolean closable,ImageIcon frameIcon,
		Color bgColor) {
		
		// create a JInternalFrame
		JInternalFrame jif= new JInternalFrame("", true, closable);
		//	add the component to the JInternalFrame
		jif.getContentPane().add(component);
		
		if (frameIcon != null)
			jif.setFrameIcon(frameIcon);
		
		return warpJInternalFrame(jif, origin, delta,bgColor);
	}

	/**
	* Create a floating JPanel with a close button on it
	* Use this as an exemple If you want to use this class<BR><BR>
	* <B>Placement</B><BR>
	* Assuming pointOrigin is the position of origin on the Frame:<BR>
	* the component will be placed at <BR>
	* (pointOrigin.x + delta.x, pointOrigin.y + delta y)<BR><BR>
	* @param component the component to draw
	* @param origin the origin component to display it
	* @param delta the delta to apply to origin position.
	* @param bgColor the background color with alpha, null for transparency
	*/
	public static ModalJPanel createSimpleModalJPanel(
		Component component,
		Component origin,
		Point delta,
		Color bgColor) {
		Container c= new Container();
		final ModalJPanel result= new ModalJPanel(c, origin, bgColor);

		//		----------- ok now design of our popup ------------//
		c.setLayout(new BorderLayout());

		// close button
		SButton jb= new SButton("X");
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				result.close();
			}
		});

		// add Components
		c.add(jb, BorderLayout.NORTH);
		c.add(component, BorderLayout.CENTER);

		// debug COmponents
		c.add(new TextField(), BorderLayout.SOUTH);

		// setThis item visible
		result.setVisible(true);

		// for a mysterious reason placement, position and
		// getPrefferdSize() gives valid result
		// only when result is Visible.. ???  

		c.setSize(c.getPreferredSize());
		smartLocation(getDesiredLocation(c, origin, delta), c);

		return result;
	}

	//---------- Events ---------------//

	private ArrayList actionListeners;

	/** Add an action listener for event change **/
	public void addActionListener(ActionListener listener) {
		if (actionListeners == null)
			actionListeners= new ArrayList();
		actionListeners.add(listener);
	}

	private void fireActionEvent() {
		if (actionListeners == null)
			return;
		ActionEvent e= new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
		synchronized (actionListeners) {
			Iterator en= actionListeners.iterator();
			while (en.hasNext()) {
				((ActionListener) en.next()).actionPerformed(e);
			}
		}
	}
	
	// Internal classes -------------------------------------------------
	
	/**
	 * Translucent JPanel displays with the given background color,
	 * even if it is transparent. This is what you cannot get by
	 * setting a normal JPanel opaque and giving it a translucent 
	 * background color. 
	 */
	class TranslucentJPanel extends JPanel {
		private Color m_background; 
		private float m_alpha;
	
		/** 
		 * @param background should have an alpha value &lt; 255 to 
		 * be get a transparent layer
		 */
		public TranslucentJPanel( Color background) {
			super();
			
			setOpaque( false );
			
			m_alpha =  background.getAlpha() / 255f;
			m_background = new Color(background.getRGB()); 
		}
	
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			// Cast to Graphics2D so we can set composite information.
			Graphics2D g2d = (Graphics2D)g;
			int width = getWidth();
			int height = getHeight();
	
			// Save the original paint state
			Composite oldComp = g2d.getComposite();
	
			// Create an AlphaComposite with 50% translucency.
			Composite alphaComp = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, m_alpha 
			);
	
			// Set the composite on the Graphics2D object.
			g2d.setComposite(alphaComp);
	
			// Invoke arbitrary paint methods, which will paint
			// with 50% translucency.
								
			// save the old paint
			Paint oldPaint = g2d.getPaint();
			
			// set the paint to use for this operation
			g2d.setPaint( m_background );
			g2d.fillRect(0, 0, width, height);
					
			// restore paint state
			g2d.setPaint(oldPaint);
			g2d.setComposite(oldComp);
		}
	}
}



/* $Log: ModalJPanel.java,v $
/* Revision 1.2  2007/04/02 17:04:26  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:41  perki
/* First commit on sourceforge
/*
/* Revision 1.32  2004/09/28 09:06:54  perki
/* Lock in splash maybe solved
/*
/* Revision 1.31  2004/09/24 17:31:24  perki
/* New Currency is now handeled
/*
/* Revision 1.30  2004/09/23 06:27:56  perki
/* LOt of cleaning with the Logger
/*
/* Revision 1.29  2004/08/01 09:56:44  perki
/* Background color is now centralized
/*
/* Revision 1.28  2004/07/30 15:38:19  perki
/* some changes
/*
/* Revision 1.27  2004/07/30 15:10:26  kaspar
/* ! Alpha up to 0.6
/*
/* Revision 1.26  2004/07/29 14:36:52  kaspar
/* ! Background of modal dialogs has now cool transparency
/*
/* Revision 1.25  2004/07/29 13:56:41  perki
/* *** empty log message ***
/*
/* Revision 1.24  2004/07/29 13:41:16  kaspar
/* ! Small changes preparing for threaded report generation
/* ! Alpha background for Modal frames work now
/*
/* Revision 1.23  2004/07/29 11:38:13  perki
/* Sliders should be ok now
/*
/* Revision 1.22  2004/07/26 16:46:09  carlito
/* *** empty log message ***
/*
/* Revision 1.21  2004/07/12 17:34:31  perki
/* Mid commiting for new matching system
/*
/* Revision 1.20  2004/07/09 20:25:03  perki
/* Merging UI step 1
/*
/* Revision 1.19  2004/07/08 14:59:00  perki
/* Vectors to ArrayList
/*
/* Revision 1.18  2004/06/18 18:25:39  perki
/* *** empty log message ***
/*
/* Revision 1.17  2004/05/23 10:40:06  perki
/* *** empty log message ***
/*
/* Revision 1.16  2004/05/20 06:11:17  perki
/* id tagging
/*
/* Revision 1.15  2004/03/12 19:04:15  perki
/* Monitoring file loading
/*
/* Revision 1.14  2004/03/12 14:06:10  perki
/* Vaseline machine
/*
/* Revision 1.13  2004/03/03 10:50:47  perki
/* Un petit bateau
/*
/* Revision 1.12  2004/03/03 10:17:23  perki
/* Un petit bateau
/*
/* Revision 1.11  2004/02/23 18:34:48  carlito
/* *** empty log message ***
/*
/* Revision 1.10  2004/02/23 13:00:51  perki
/* good night
/*
/* Revision 1.9  2004/02/23 12:39:31  perki
/* good night
/*
/* Revision 1.8  2004/02/17 18:03:17  carlito
/* *** empty log message ***
/*
/* Revision 1.7  2004/02/17 11:39:24  carlito
/* *** empty log message ***
/*
 * Revision 1.5  2004/02/17 10:48:57  perki
 * zigow
 *
 * Revision 1.4  2004/02/01 17:15:12  perki
 * good day number 2.. lots of class loading improvement
 *
 * Revision 1.3  2004/02/01 11:13:51  perki
 * nice job
 *
 * Revision 1.2  2004/01/31 18:21:31  perki
 * Wonderfull Day
 *
 * Revision 1.1  2004/01/31 15:47:45  perki
 * 16 heure 50
 *
 */