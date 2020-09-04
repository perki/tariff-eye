/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: RunFirst.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.tarifmanager;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import bsh.EvalError;
import bsh.Interpreter;

import com.simpledata.bc.datamodel.TString;
import com.simpledata.bc.tools.Lang;
import com.simpledata.util.Base64;

/**
 * This class will be packaged first in the ArrayList of new 
 * Tarifications references..<BR>
 * An will be runned before updating tariffs..<BR>
 * It contains: a bsh script to evaluate<BR>
 * a message (alert to display)<BR>
 * a byte array that will be passed as parameters to the bsh script<BR>
 * <BR>
 * the byte array will be available to the bsh script with the variable name 
 * "data"<BR>
 * the componet it runs on will be available under the var name "owner"
 */
public class RunFirst implements Serializable{
    private static final Logger m_log = 
        Logger.getLogger( RunFirst.class ); 
    
    private TString message;
    private String bshScript;
    private byte[] data;
    
    public RunFirst(TString message,String bshScript,byte[] data) {
        assert data != null;
        this.message = message;
        this.bshScript = bshScript;
        this.data = data;
    }
    
    
    /** display the message to the user in an alert BOX **/
    public void alert(Component c) {
        String m;
        if (message == null || (m = message.toString()).equals("")) 
            return;
        JOptionPane.showMessageDialog(c,m);
    }
    
    /** run this **/
    public void run(Component c) {
        assert c != null;
        if (bshScript == null || bshScript.equals("")) return;
        Interpreter i = new Interpreter();
        
        try {
            if (data == null) {
                m_log.warn("data is null");
            } else {
                i.set("data",data);
            }
            i.set("owner",c);
            i.eval(bshScript);
        } catch (EvalError e) {
            JOptionPane.showMessageDialog(c,
                    Lang.translate(
                          "An error happend while trying to retrieve tariffs"));
            m_log.error("While evaluation of bsh script",e);
        }
        
    }
    
    //----------------------- XML -----------------------//
    
    /**XML*/
    public RunFirst() {   
    }
    
    /**XML*/
    public String getXBshScript() {   
        return bshScript;
    }
    /**XML*/
    public void setXBshScript(String bsgScript) {
        this.bshScript = bsgScript;
    }
    /**XML*/
    public TString getXMessage() {
        return message;
    }
    /**XML*/
    public void setXMessage(TString message) {
        this.message = message;
    }
    
    /**
	 * XML.. return the byte array as a String (base64)
	 */
	public String getXData() {
	    if (data == null) {
	        data = new byte[0];
	    }
	    return Base64.encodeBytes(data);
	}
	
	/**
	 * XML... convert a String to a byte array (base64)
	 */
	public void setXData(String base64Str) {
	    data =  Base64.decode(base64Str);
	}
}
