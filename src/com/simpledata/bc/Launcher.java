/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: Launcher.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
package com.simpledata.bc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.filetools.SimpleException;
import com.simpledata.filetools.TextFileUtils;
import com.simpledata.util.CollectionsToolKit;

/**
 * takes care of having one single running instance
 * And communicates files openeing to BC
 */
public class Launcher {

    public static void main(String[] args) {
        System.out.println("Launcherd");
        // look for existsing files in args
        for (int i = 0; i < args.length; i++) {
            File test = new File(args[i]);
            if (test.exists()) {
                File dir = new File(Resources.fileToOpenSpoolPath());
                dir.mkdirs();
               
                try {
                    File f2 = File.createTempFile("BOB","ETTE",dir);
                    f2.deleteOnExit();
                    OutputStream os=new FileOutputStream(f2);
                    os.write(args[i].getBytes());
                    os.close();
                    
                    f2.renameTo(new File(f2.getAbsolutePath()+".open"));
                  
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                System.out.println("args "+i+" "+args[i]);
            }
        }
        
        // Look for BC and try to launch it
        File[] fs = new File("./").listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name) {
                return name.startsWith("TariffEye");
            }});
        
        if (fs != null && fs.length == 1) {
            ArrayList a = CollectionsToolKit.getArrayList(args);
            a.add(0,fs[0].getAbsolutePath());
            try {
                Runtime.getRuntime().exec((String[]) a.toArray(args));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            System.err.println("Cannot find Tariff Eye"+fs);
        }
        
    }
    
    
    
    /**
     * This code will be called by BC in order to check if there is some 
     * new files to open
     */
    public static void startFileToOpenSpooler() {
        Thread t = new Thread("AutoOpener") {
            AutoOpener ao;
            public void run() {
                ao = new AutoOpener();
                while (true) {
	          	      
	                try {
	    	            Thread.sleep(2000);
	    	            SwingUtilities.invokeAndWait(ao);
	    	        } catch (InterruptedException e) {
	    	            // TODO Auto-generated catch block
	    	            e.printStackTrace();
	    	        } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        
        };
        t.start();
    }
}

class AutoOpener implements Runnable  {
  
    
    public void run() {
        
        try {
            
            File dir = new File(Resources.fileToOpenSpoolPath());
            
            // Look for BC and try to launch it
            File[] fs = dir.listFiles(new FilenameFilter(){
                public boolean accept(File dir, String name) {
                    return name.endsWith(".open");
                }});
            if (fs != null) {
                for (int i = 0; i < fs.length; i++) {
                    
                    String sf = TextFileUtils.getString(fs[i]).trim();
                    File f = new File(sf);
                    if (f.exists()) {
                        FileManagement.openExternal(
                                f,BC.bc.getMajorComponent(),-1);
                    } else {
                        
                        System.out.println("required files didnt exists:"+
                                "["+sf+"]");
                    }
                    fs[i].delete();
                }
            }
        } catch (SimpleException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}
