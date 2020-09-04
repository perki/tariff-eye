/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 7 juil. 2004
 * $Id: InternalFrameDescriptor.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uitools;

import java.awt.Rectangle;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;

/**
 * This class is meant to define a property descriptor for
 * internal frames used in this app.<br>
 * When passed in parameter to 
 * BC.popupJIFrame(JInternalFrame jif, InternalFrameDescriptor ifd) ,
 * The desktop manager will be able to do specific management, such as 
 * size remembering, centering on open etc...
 */
public class InternalFrameDescriptor {

    private final static Logger log = Logger.getLogger(InternalFrameDescriptor.class);
    
    /** Indicates wether this ifd should be centered on open*/
    private boolean centerOnOpen;
    
    /** Indicates wether the initialBounds should be considered as a minimum 
     * for frame open */
    private boolean initialBoundsAreMinimum;
    
    private String parameterKey;
    private Rectangle initialBounds;
    
    public InternalFrameDescriptor() {
        // By default no parameter key and not centered on screen
        this(null, null, false, false);
    }
    
    
    public InternalFrameDescriptor(String parameterKey) {
        this(parameterKey, null,false, false);
    }
    
//    private InternalFrameDescriptor(String parameterKey, 
//            Rectangle initialBounds, 
//            boolean centerOnOpen) {
//        this(parameterKey, initialBounds, centerOnOpen, false);
//    }
    
    private InternalFrameDescriptor(String parameterKey, 
            Rectangle initialBounds, 
            boolean centerOnOpen,
            boolean initialBoundsAreMinimum) {
        this.parameterKey = parameterKey;
        this.initialBounds = initialBounds;
        this.centerOnOpen = centerOnOpen;
        this.initialBoundsAreMinimum = initialBoundsAreMinimum;
    }
    
    
    
    /**
     * Returns true if the JIF has to be centered when opened
     */
    public boolean isCenterOnOpen() {
        return centerOnOpen;
    }
   
    /**
     * set to true if the jif is meant to be centered on open
     */
    public void setCenterOnOpen(boolean centerOnOpen) {
        this.centerOnOpen = centerOnOpen;
    }
    
    public void setInitialBoundsAreMinimum(boolean initialBoundsAreMinimum) {
        this.initialBoundsAreMinimum = initialBoundsAreMinimum;
    }
    
    
    /**
     * Returns the Parameter key used to store params
     * @return Returns the parameter key 
     */
    public String getParameterKey() {
        return parameterKey;
    }
    /**
     * Sets the correct key for parameter storage
     * @param parameterKey The parameter key for bounds persistance.
     */
    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey;
    }

    
    
    /**
     * Determines wether a frame's bounds should be saved on close or not.<br>
     * It will consider that this is the case only if there is a key that has
     * been defined to save the parameters
     * @return true if this frame bounds have to be saved
     */
    public boolean isSaveOnExit() {
        boolean res = false;
        if (parameterKey != null) {
            res = true;
        }
        return res;
    }
    
    /**
     * If a parameter key is set, it will look for saved bounds
     * If there is no parameter or no saved bounds it will return
     * initialBounds if set
     * else will return null
     * @return
     */
    public Rectangle getBounds() {
        Rectangle rec = null;
        String key = getParameterKey();
        if (key != null) {
            rec = (Rectangle)BC.getParameter(key, Rectangle.class);
        }
        if (rec == null) {
            if (this.initialBounds != null) {
                rec = this.initialBounds;
            }
        } else {
            if (initialBoundsAreMinimum) {
                if (this.initialBounds != null) {
                    // We now check that the actual rec size is greater or equal
                    // to that of the initialBounds
                    int destW = rec.width;
                    int destH = rec.height;
                    
                    if (initialBounds.width > rec.width) {
                        destW = initialBounds.width;
                    }
                    if (initialBounds.height > rec.height) {
                        destH = initialBounds.height;
                    }
                    rec.setSize(destW, destH);                    
                }
            }
        }
        return rec;
    }
    
    /**
     * Set the bounds that should be used on initialization
     * @param initialBounds The initialBounds to set.
     */
    public void setInitialBounds(Rectangle initialBounds) {
        this.initialBounds = initialBounds;
    }

    
}


/*
 * $Log: InternalFrameDescriptor.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:41  perki
 * First commit on sourceforge
 *
 * Revision 1.4  2004/11/30 13:41:35  carlito
 * minimum size for internal frames
 *
 * Revision 1.3  2004/11/30 12:36:20  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.1  2004/07/09 19:10:25  carlito
 * *** empty log message ***
 *
 */