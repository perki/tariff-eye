/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: SoftInfos.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc;



/**
 * Simple class that keeps all the information acessible in one place
 */
public class SoftInfos {
    
    /** VESRION IS CHANGED BY BC AT START **/
    public static String VERSION = "DEVEL";
    
    /**
    * return true if in locked (need to be registred)
    */
    public static boolean isLocked() {
       return false;
    }
    
    /**
     * return true if this software can go in Gold Mode
     */
     public static boolean canGoCreationGold() {
     	return true;
     }
    
     /**
      * return true if this software can go in Creation mode
      */
      public static boolean canGoCreation() {
      	return true;
      }
      
      /**
       * Should return an id of this product / user .. 
       * for now return the version
       */
       public static String id() {
       	return softVersion();
       }
    
    /**
    * get the software version
    */
    public static String softVersion() {
        return VERSION;
    }
    

}
