/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on Mar 11, 2004
 *
 * $Id: SDesktopPane.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.*;

import javax.swing.*;

import com.simpledata.bc.BC;

import org.apache.log4j.Logger;

/**
 * This class is a super class of JDesktopPane for better window management
 */
public class SDesktopPane extends JDesktopPane {
	private static final Logger m_log = Logger.getLogger( SDesktopPane.class );
	
    private static int FRAME_OFFSET=20;
    
	/** 
	 * This represents the minimum width and height for a new frame
	 * which bounds are not set
	 **/
	private final static int INIT_FRAME_WIDTH = 100;
	private final static int INIT_FRAME_HEIGHT = 100;
    
    private SDesktopManager manager;
    
    private HashMap/*<JInternalFrame>,<InternalFrameDescriptor>*/ frameDescriptions;

    /**
     * CONSTRUCTOR
     */
    public SDesktopPane() {
        manager=new SDesktopManager(this);
        setDesktopManager(manager);
        setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x,y,w,h);
        checkDesktopSize();
    }

    private HashMap getFrameDescriptions() {
        if (frameDescriptions  == null) {
            frameDescriptions = new HashMap();
        }
        return frameDescriptions;
    }
    
    /**
     * 
     * @param frame
     * @return
     */
    public Component add(JInternalFrame frame) {
//        // All already added frames...
//        JInternalFrame[] array = getAllFrames();
//        Point p;
//        int w;
//        int h;
//
//        Component retval=super.add(frame);
//        checkDesktopSize();
//        if (array.length > 0) {
//            p = array[0].getLocation();
//            p.x = p.x + FRAME_OFFSET;
//            p.y = p.y + FRAME_OFFSET;
//        }
//        else {
//            p = new Point(0, 0);
//        }
//        frame.setLocation(p.x, p.y);
//        if (frame.isResizable()) {
//            w = getWidth() - (getWidth()/3);
//            h = getHeight() - (getHeight()/3);
//            
//            // Addon to avoid auto splash for sized windows
//            if ((frame.getSize().width != 0) && (w > frame.getSize().width)) w = frame.getSize().width;
//            if ((frame.getSize().height != 0) && (h > frame.getSize().height)) h = frame.getSize().height;
//            
//            if (w < frame.getMinimumSize().getWidth()) w = (int)frame.getMinimumSize().getWidth();
//            if (h < frame.getMinimumSize().getHeight()) h = (int)frame.getMinimumSize().getHeight();
//            frame.setSize(w, h);
//        }
//        moveToFront(frame);
//        frame.setVisible(true);
//        try {
//            frame.setSelected(true);
//        } catch (PropertyVetoException e) {
//            frame.toBack();
//        }
//        return retval;
        m_log.warn("THIS METHOD SHOULD NOT BE USE... DEPRECATED ;)");
        return add(frame, null);
    }
    
    /**
     * DO NOT USE DIRECTLY : USE BC.popupJIframe INSTEAD
     * @param frame
     * @param descriptor
     * @return
     */
    public Component add(JInternalFrame frame, InternalFrameDescriptor descriptor) {
        InternalFrameDescriptor myDescriptor = descriptor;
        if (myDescriptor == null) {
            Rectangle bounds = new Rectangle(frame.getX(), frame.getY(), 
                    frame.getWidth(), frame.getHeight());
            
            myDescriptor = new InternalFrameDescriptor();
            myDescriptor.setInitialBounds(bounds);
        }
        
        Rectangle desiredBounds = myDescriptor.getBounds();
        int x = (int)desiredBounds.getX();
        int y = (int)desiredBounds.getY();
        int width = (int)desiredBounds.getWidth();
        int height = (int)desiredBounds.getHeight();
        
        // First we analyze the current size of the frame to see if it fits within the 
        // visible desktop...
        Dimension actualVisibleSurface = this.manager.getDisplayedDesktopSize();
        
        // We determine the location of the actual selected frame
        JInternalFrame[] array = getAllFrames();
        int selFrameX = -1;
        int selFrameY = -1;
        if (array.length > 0) {
            selFrameX = array[0].getX();
            selFrameY = array[0].getY();
        }
        
        // Control variables
        boolean tooWide = false;
        boolean tooHigh = false;
        
        boolean outRight = false;
        boolean outBottom = false;
        
        boolean overLast = false;
        
        if ((x == selFrameX) && (y == selFrameY)) overLast = true;
        
        if (width > actualVisibleSurface.width) tooWide = true;
        
        if (height > actualVisibleSurface.height) tooHigh = true;
        
        if ((x+width) > actualVisibleSurface.width) outRight = true;
        if ((y+height) > actualVisibleSurface.height) outBottom = true;
        
        if (myDescriptor.isCenterOnOpen()) {
            // We want to center the window (we don't care about other frames)
            if (frame.isResizable()) {
                if (tooWide) width = actualVisibleSurface.width - (2*FRAME_OFFSET);
                if (tooHigh) height = actualVisibleSurface.height - (2*FRAME_OFFSET);
                if (width <= 0) width = INIT_FRAME_WIDTH;
                if (height <= 0) height = INIT_FRAME_HEIGHT;
                
                // Lets determine correct position...
                int horizontalDiff = actualVisibleSurface.width - width;
                if (horizontalDiff < 0) horizontalDiff = 0;
                int verticalDiff = actualVisibleSurface.height - height;
                if (verticalDiff < 0) verticalDiff = 0;
                
                x = (int)(horizontalDiff/2);
                y = (int)(verticalDiff/2);
            } else {
                if (tooWide) x = 0;
                if (tooHigh) y = 0;
            }
        } else {
            // We place it where it requires
            if (overLast) {
                // We don't want to place it right over the ancient selected
                if (!((selFrameX == 0) && (selFrameY == 0))) {
                    // We place it on the origin
                    x = 0;
                    y = 0;
                } else {
                    x = FRAME_OFFSET;
                    y = FRAME_OFFSET;
                }
            }
            
            // We analyze whole pos and size
            if (frame.isResizable()) {
                // We can resize the window if there is any problem
                if (outRight) {
                    if (tooWide) {
                        width = actualVisibleSurface.width - (2*FRAME_OFFSET);
                        if (width <= 0) width = INIT_FRAME_WIDTH;
                        x = FRAME_OFFSET;
                    } else {
                        x = actualVisibleSurface.width - width;
                    }
                }
                if (outBottom) {
                    if (tooHigh) {
                        height = actualVisibleSurface.height - (2*FRAME_OFFSET);
                        if (height <= 0) height = INIT_FRAME_HEIGHT;
                        y = FRAME_OFFSET;
                    } else {
                        y = actualVisibleSurface.height - height;
                    }
                }
                
            } else {
                // frame is not resizable
                if (outRight) {
                    if (tooWide) {
                        x = 0;
                    } else {
                        x = actualVisibleSurface.width - width;
                    }
                }
                
                if (outBottom) {
                    if (tooHigh) {
                        y = 0;
                    } else {
                        y = actualVisibleSurface.height - height;
                    }
                }
                
            }
        }
        
        // We verifiy positioning again
        overLast = false;
        if ((x == selFrameX) && (y == selFrameY)) overLast = true;
        
        if (overLast) {
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
        // We register the new bounds
        frame.setBounds(x,y,width,height);
        
        if (getFrameDescriptions().containsKey(frame)) {
            m_log.error("Frame : "+frame.getTitle()+" has allready been added");
            return null;
        }
        
        getFrameDescriptions().put(frame, myDescriptor);
        
        Component retval = super.add(frame);
        
        moveToFront(frame);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            frame.toBack();
        }
        
        return retval;        
    }

    public void remove(Component c) {
        super.remove(c);
        checkDesktopSize();
    }

    protected void informClose(JInternalFrame f) {
        if (getFrameDescriptions().containsKey(f)) {
            // We will save parameters if required
            InternalFrameDescriptor ifd = (InternalFrameDescriptor)getFrameDescriptions().get(f);
            if (ifd != null) {
                if (ifd.isSaveOnExit()) {
                    String key = ifd.getParameterKey();
                    Rectangle bounds = f.getBounds();
                    BC.setParameter(key, bounds);
                }
            }
            getFrameDescriptions().remove(f);
        }
    }
    
    /**
     * Cascade all internal frames
     */
    public void cascadeFrames() {
        int x = 0;
        int y = 0;
        JInternalFrame allFrames[] = getAllFrames();

        allFrames = getShowingFrames(allFrames);
        
        manager.setNormalSize();
        int frameHeight = (getBounds().height - 5) - allFrames.length * FRAME_OFFSET;
        int frameWidth = (getBounds().width - 5) - allFrames.length * FRAME_OFFSET;
        
        int length = allFrames.length;
        for (int i = length - 1; i >= 0; i--) {
            allFrames[i].setSize(frameWidth,frameHeight);
            allFrames[i].setLocation(x,y);
            x = x + FRAME_OFFSET;
            y = y + FRAME_OFFSET;
        }
    }

    /**
     * Tile all internal frames
     */
    public void tileFrames() {
        JInternalFrame allFrames[] = getAllFrames();
        
        allFrames = getShowingFrames(allFrames);
        
        manager.setNormalSize();
        int frameHeight = getBounds().height/allFrames.length;
        int y = 0;
        int length = allFrames.length;
        int width = getBounds().width;
        for (int i = 0; i < length; i++) {
            allFrames[i].setSize(width,frameHeight);
            allFrames[i].setLocation(0,y);
            y = y + frameHeight;
        }
    }
    
    private JInternalFrame[] getShowingFrames(JInternalFrame[] frames) {
        ArrayList/*<JInternalFrame>*/ temp = new ArrayList(/*<JInternalFrame>*/);
        int length = frames.length;
        for (int i=0 ; i < length; i++) {
            if (frames[i].isShowing()) {
                temp.add(frames[i]);
            }
        }
        
        length = temp.size();
        JInternalFrame[] resultFrames = new JInternalFrame[length];
        
        for (int i = 0; i < length; i++) {
            resultFrames[i] = (JInternalFrame)temp.get(i);
        }
        
        return resultFrames;
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given dimension.
     */
    public void setAllSize(Dimension d){
        setMinimumSize(d);
        setMaximumSize(d);
        setPreferredSize(d);
    }

    /**
     * Sets all component size properties ( maximum, minimum, preferred)
     * to the given width and height.
     */
    public void setAllSize(int width, int height){
        setAllSize(new Dimension(width,height));
    }

    private void checkDesktopSize() {
        if (getParent()!=null&&isVisible()) manager.resizeDesktop();
    }
    
    /**
     * Call this when you want all windows to be closed properly
     * i.e. before exiting the main app
     * @return false if one of the window has not closed properly
     * for example if the user has canceled a save
     */
    public boolean closeAllWindows() {
        HashMap bob = (HashMap)getFrameDescriptions().clone();
        for (Iterator i = (bob.keySet()).iterator(); i.hasNext();) {
            JInternalFrame frame = (JInternalFrame)i.next();
            frame.dispose();
        }
        if (getFrameDescriptions().size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * This method allows to access all frames that have been registered in the 
     * desktopPane through a InternalFrameDescriptor
     * @return an arraylist of those InternalFrames
     */
    public ArrayList getRegisteredFrames() {
        ArrayList res = new ArrayList();
        for (Iterator i = getFrameDescriptions().keySet().iterator(); i.hasNext(); ) {
            res.add(i.next());
        }
        return res;
    }
    
}

/**
 * Private class used to replace the standard DesktopManager for JDesktopPane.
 * Used to provide scrollbar functionality.
 */
class SDesktopManager extends DefaultDesktopManager {
    private SDesktopPane desktop;
    
    private ArrayList/*<JInternalFrame>*/ iconifiedFrames;
    
    private ArrayList/*<JInternalFrame>*/ maximizedFrames;
    
    public SDesktopManager(SDesktopPane desktop) {
        this.desktop = desktop;
        this.iconifiedFrames = new ArrayList(/*<JInternalFrame>*/);
        this.maximizedFrames = new ArrayList(/*<JInternalFrame>*/);
    }

    public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
        resizeDesktop();
    }
    
    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
        int x = f.getX();
        int y = f.getY();
        boolean changeOccured = false;
        if (x < 0) {
            x = 0;
            changeOccured = true;
        }
        if (y < 0) {
            y = 0;
            changeOccured = true;
        }
        if (changeOccured) {
            // We reposition the frame so that its negative(s) position(s) --> 0
            f.setLocation(x,y);
        }
        resizeDesktop();
    }
    
    public void closeFrame(JInternalFrame f) {
        this.desktop.informClose(f);
        super.closeFrame(f);
    }
    
    public void iconifyFrame(JInternalFrame jf) {
        this.iconifiedFrames.add(jf);
//        resizeDesktop();
//        jf.hide();
        super.iconifyFrame(jf);
    }
    
    public void deiconifyFrame(JInternalFrame jf) {
        this.iconifiedFrames.remove(jf);
        super.deiconifyFrame(jf);
//        jf.show();
//        resizeDesktop();
    }


    /**
     * Overrided method to allow special case treatment
     * Force the desktop to resize to the size it should have
     * if the frame to be maximized were not displayed. And then maximize
     * the frame within the resized desktop.
     * @param JInternalFrame jf the internal frame to be maximized
     */
    public void maximizeFrame(JInternalFrame jf) {
        //Log.out("we are maximizing a window");
        if (!this.maximizedFrames.contains(jf)) {
            this.maximizedFrames.add(jf);
        }
        resizeDesktop();
        super.maximizeFrame(jf);
    }
    
    /**
     * Called whenever a frame is being deminimized
     */
    public void minimizeFrame(JInternalFrame jf) {
        //Log.out("we are minimizing a window");
        if (this.maximizedFrames.contains(jf)) {
            this.maximizedFrames.remove(jf);
        }
        super.minimizeFrame(jf);
        resizeDesktop();
    }
    
    public void setNormalSize() {
        JScrollPane scrollPane=getScrollPane();
        int x = 0;
        int y = 0;
        Insets scrollInsets = getScrollPaneInsets();

        if (scrollPane != null) {
            Dimension d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
               d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                         d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }

            d.setSize(d.getWidth() - 20, d.getHeight() - 20);
            desktop.setAllSize(x,y);
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }

    private Insets getScrollPaneInsets() {
        JScrollPane scrollPane=getScrollPane();
        if (scrollPane==null) return new Insets(0,0,0,0);
        return getScrollPane().getBorder().getBorderInsets(scrollPane);
    }

    private JScrollPane getScrollPane() {
        if (desktop.getParent() instanceof JViewport) {
            JViewport viewPort = (JViewport)desktop.getParent();
            if (viewPort.getParent() instanceof JScrollPane)
                return (JScrollPane)viewPort.getParent();
        }
        return null;
    }

    protected void  resizeDesktop() {	
        int x = 0;
        int y = 0;
        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();
        
        if (scrollPane != null) {
            JInternalFrame allFrames[] = desktop.getAllFrames();
            boolean isEluded = false;
            for (int i = 0; i < allFrames.length; i++) {
                JInternalFrame jif = allFrames[i];
                
                // We verify that the frame is not iconified or maximized
                isEluded = iconifiedFrames.contains(jif) || maximizedFrames.contains(jif);
                if (!isEluded) {
                    if (jif.getX()+jif.getWidth()>x) {
                        x = jif.getX() + jif.getWidth();
                    }
                    if (jif.getY()+jif.getHeight()>y) {
                        y = jif.getY() + jif.getHeight();
                    }
                } 
                /*
                else {
                    Log.out("Eluding frame : "+jif);
                }
                */
            }
            Dimension d=scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }
            
            if (x <= d.getWidth()) x = ((int)d.getWidth()) - 20;
            if (y <= d.getHeight()) y = ((int)d.getHeight()) - 20;
            
            //Log.out("desired size : x:"+x+", y:"+y);
            
            desktop.setAllSize(x,y);
            
            scrollPane.invalidate();
            scrollPane.validate();
        }
    }	
    
    public Dimension getDisplayedDesktopSize() {
        JScrollPane scrollPane = getScrollPane();
        Insets scrollInsets = getScrollPaneInsets();
        
        Dimension d = null;
        if (scrollPane != null) {
            d = scrollPane.getVisibleRect().getSize();
            if (scrollPane.getBorder() != null) {
                d.setSize(d.getWidth() - scrollInsets.left - scrollInsets.right,
                        d.getHeight() - scrollInsets.top - scrollInsets.bottom);
            }
        }
        return d;
    }
    
}

/*
 * $Log: SDesktopPane.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.14  2004/09/07 10:13:43  kaspar
 * ! Replacing Log.out
 *
 * Revision 1.13  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.12  2004/07/20 18:30:36  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/07/20 14:26:21  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/07/19 20:00:33  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/07/19 15:27:46  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/07/12 17:27:25  carlito
 * desktop improvements
 *
 * Revision 1.7  2004/07/09 19:10:25  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/07/07 13:44:44  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/07/06 17:31:25  carlito
 * Desktop manager enhanced
SButton with border on macs
desktop size persistent
 *
 * Revision 1.4  2004/07/01 14:45:14  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/03/22 14:32:30  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/03/12 14:06:10  perki
 * Vaseline machine
 *
 * Revision 1.1  2004/03/12 02:52:51  carlito
 * *** empty log message ***
 *
 */