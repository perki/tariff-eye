/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ListLoaderHTTP.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.tarifmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.simpledata.bc.BC;
import com.simpledata.bc.Params;
import com.simpledata.bc.datamodel.TarificationHeader;
import com.simpledata.bc.datatools.TarificationFileView;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.JListWithPanels;
import com.simpledata.bc.uitools.SButton;
import com.simpledata.bc.uitools.ToolKit;
import com.simpledata.bc.webcontrol.TariffEyeQuery;
import com.simpledata.filetools.CopyFile;
import com.simpledata.filetools.FileUtilities;
import com.simpledata.filetools.Secu;
import com.simpledata.filetools.SimpleException;
import com.simpledata.filetools.Secu.Monitor;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * Contains the method to retrive the list of tarif from an HTTP connection
 */
public class ListLoaderHTTP extends JDialog implements Secu.Monitor {
    
  
    
    
    private static final Logger m_log = 
        Logger.getLogger( ListLoaderHTTP.class ); 
    
    /** filename of the update file (RunFirst object) **/
    public static final String RUNFIRST_FILENAME = "_update";
    
    public static final String DES_KEY = "ZimplZOO";
    
    private static final Dimension DIM_LIST
    	= new Dimension(300, 400);
    private static final Dimension DIM_VIEW
		= new Dimension(300, 400);
    
    /** ID of the License **/
    private String id_license;
    
    /** 
     * list of known tariffs<BR>
     * in fact STring made of: title+publication_date+modification_date
     **/
    private HashSet/*<String>*/ knownTariffs;
    
    public static void main(String args[]) {
        //Log4jInitializer.doInit();
       new ListLoaderHTTP(new JFrame(),"BOBBY");
    }
    
    /** destination directory **/
    File destDir ;
    
    public ListLoaderHTTP(JFrame owner,String id) {
        super(owner,Lang.translate("Tariffs Updates"),true);
       
        m_subscriptionInfoPanel = new JPanel();
        
        // get destination directory
        if (BC.bc != null) {
            destDir = new File(
                    BC.getParameterStr(Params.KEY_TARIFICATION_LIBRARY_PARTH));
        } else {
            m_log.error("How could this happen");
        }
        knownTariffs = new HashSet/*<String>*/();
        refreshKnowntarifs();
        
        id_license = id;
        
       
        
      
        
        // Update Buttons
        JPanel leftButtons = createButtonPanel();
        leftButtons.add(upInternetBut());
        leftButtons.add(upFileBut());
        
        // List of updates & LEFT panel
        JPanel left = new JPanel(new BorderLayout());
        JComponent temp0 = listOfUpdates();  
        temp0.setSize(DIM_LIST);
        temp0.setPreferredSize(DIM_VIEW);
        left.add(temp0,BorderLayout.CENTER);
        left.add(leftButtons,BorderLayout.SOUTH);
        
        
        // Import / Close Buttons
        JPanel rightButtons = createButtonPanel();
        rightButtons.add(importBut());
        rightButtons.add(closeBut());
        
        // Preview of tarifInformations
        JPanel right = new JPanel(new BorderLayout());
        JPanel temp=new JPanel(new BorderLayout());
        temp.setSize(DIM_VIEW);
        temp.setPreferredSize(DIM_VIEW);
        temp.add(infoViewer());
        right.add(temp,BorderLayout.CENTER);
        right.add(rightButtons,BorderLayout.SOUTH);
     
        
        // splitPane in the center
        getContentPane().setLayout(new BorderLayout());
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        getContentPane().add(jsp,BorderLayout.CENTER);
        
        jsp.add(left);
        jsp.add(right);
        
        // subscription info on top
        buildSubscriptionInfoPanel();
        getContentPane().add(m_subscriptionInfoPanel, BorderLayout.NORTH);
        pack();
        
        ToolKit.centerOnScreen(this);
        
        
        setVisible(true);
        
    }
    
    
    /** create panels that contains 2 vertically aligned Buttons **/
    private JPanel createButtonPanel() {
        JPanel jp = new JPanel();
        GridLayout layout = new GridLayout(2,1,10,10);
        jp.setLayout(layout);
        return jp;
    }
    
    private final JPanel m_subscriptionInfoPanel;
    private void buildSubscriptionInfoPanel() {
    	if (SubscriptionToolBox.expiresTimeAviable()) {
    		JLabel label;
    		JLabel icon;
    		if (SubscriptionToolBox.hasExpired()) {
    			label = new JLabel("<HTML><b>"+Lang.translate("Warning: ")+
    					"</b>"+Lang.translate("Your tarification subscription" +
    							" has expired.")+"</HTML>");
    			icon = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
    		} else {
    			StringBuffer msg = new StringBuffer("<HTML>");
    			if (SubscriptionToolBox.expiresTimeInDay() < 30) {
    				icon = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
    				msg.append("<b>"+Lang.translate("Warning: ")+"</b>");
    			} else {
    				icon = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
    			}
    			Date expDate = (Date)BC.getParameter(Params.KEY_SUBSCRIBTION_EXPIRES);
    			String expDateStr = DateFormat.getDateInstance().format(expDate);
    			msg.append(Lang.translate("Your tarification subscription expires on "));
				msg.append(expDateStr+"</HTML>");
				label = new JLabel(msg.toString());
    		}
    		m_subscriptionInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    		m_subscriptionInfoPanel.add(icon);
    		m_subscriptionInfoPanel.add(label);
    	} else {
    		m_log.info("Expiration data not aviable.");
    	}
    }
    
    private JButton upInternetBut;
    private JButton upInternetBut() {
        if (upInternetBut == null) {
            upInternetBut = new SButton(Lang.translate("From Internet"));
            upInternetBut.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    refresh(getTarificationsWEB());
                }});
            
            
            
        }
        return upInternetBut;
    }
    
    private JButton upFileBut;
    private JButton upFileBut() {
        if (upFileBut == null) {
            upFileBut = new SButton(Lang.translate("From Directory"));
            upFileBut.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    
                   JFileChooser fc =
                       new JFileChooser(new File("tarifications2"));
                   fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                   int returnVal = fc.showOpenDialog(ListLoaderHTTP.this);
                   if (returnVal == JFileChooser.APPROVE_OPTION) {
                       File file = fc.getSelectedFile();
                       refresh(getTarificationsFile(file));
                       
      
                   }
                }});
           
        }
        return upFileBut;
    }
    
    
    private JListWithPanels listOfUpdates;
    private JListWithPanels listOfUpdates() {
        if (listOfUpdates == null) {
            listOfUpdates = new JListWithPanels();
            
            listOfUpdates.setSelectionOnAdd(false);
            
            listOfUpdates.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    JPanel jp = listOfUpdates.getSelectedPanel();
                    TarificationHeader th = null;
                    if (jp != null) {
                        th = ((TariffInfo) jp).th;
                    }
                    refreshInfoViewer(th);
                }});
            
        }
        return listOfUpdates;
    }
    
    
    private JScrollPane infoViewer;
    private JScrollPane infoViewer() {
        if (infoViewer == null) {
            infoViewer = new JScrollPane();
            infoViewer.setHorizontalScrollBarPolicy(
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			JPanel jp = new JPanel();
			
			infoViewer.setViewportView(jp);
			
        }
        return infoViewer;
    }
    
    private JButton importBut;
    private JButton importBut() {
        if (importBut == null) {
            importBut = new SButton(Lang.translate("Import"));
            importBut.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    actionImportFiles();
                }
            });
        }
        return importBut;
    }
    
    private JButton closeBut;
    private JButton closeBut() {
        if (closeBut == null) {
            closeBut = new SButton(Lang.translate("Close"));
            closeBut.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    
                }
            });
        }
        return closeBut;
    }
   
    /************** end of UI genral elements **************************/
    
    /** from the HardDrive update known tarif list **/
    private void refreshKnowntarifs() {
        ArrayList al = getTarificationsFile(destDir).refs();
        knownTariffs.clear();
        if (al == null) {
            m_log.warn("Cannot find Tarifs in ["+destDir+"]");
            return;
        }
        for (Iterator i=al.iterator();i.hasNext();) {
            knownTariffs.add(
                    getTarificationSignature((TarificationReference)i.next()));
        }
    }
    
    private static String getTarificationSignature(TarificationReference tr) {
        TarificationHeader th = tr.getTarificationHeader();
        String res = 
            th.getTitle()+th.getModificationDate()+th.getPublishingDate();
        return res;
    }
    
    /** update the ui with the following tarifs infos **/
    private void refreshInfoViewer(TarificationHeader th) {
       JPanel jp = (th == null) ? new JPanel() :  
           TarificationFileView.getTarificationHeaderPanel(th);
       
       infoViewer().setViewportView(jp);
    }
    
    /** update the ui with the following tarifs infos **/
    private void refresh(UpdatePackage pack) {
        if (pack == null) {
            m_log.warn("pack is null");
            return;
        }
        if (pack.runFirst() != null) {
            pack.runFirst().alert(this);
            pack.runFirst().run(this);
        } else {
            m_log.warn("runFirst is null");
        }
        
        
        listOfUpdates().removeAll();
        if (pack.refs() == null) {
            JOptionPane.showMessageDialog(this,
                    Lang.translate(
                          "An error happend while trying to retrieve tariffs"));
            return;
        }
        
        if (pack.refs().size() == 0) {
            JOptionPane.showMessageDialog(this,
                    Lang.translate(
                          "The selected source does not contain any tariff"));
            return;
        }
      
        
        TarificationReference tr;
        int counter = 0;
        for (Iterator i=pack.refs().iterator();i.hasNext();)
        {
            tr = (TarificationReference) i.next();
            if (!  knownTariffs.contains(getTarificationSignature(tr))) {
                counter++;
                listOfUpdates().addPanel(new TariffInfo(tr));
            }
        }
        
        if (counter == 0) {
            JOptionPane.showMessageDialog(this,
                    Lang.translate("Your database is already up to date"));
        }
        
        
    }
    
    class TariffInfo extends JPanel{
        TarificationReference tr;
        TarificationHeader th;
        JCheckBox jc;
        TariffInfo(TarificationReference tr) {
            this.tr = tr;
            this.th = tr.getTarificationHeader();
            
            // UI
            setLayout(new FlowLayout(FlowLayout.LEADING,5,5));
           
            String text="<HTML><B>"+th.getTitle()+"</B><BR>" +
            		"<SMALL>"+th.getPublishingDate()+"</SMALL>"+
            		"</HTML>";
            jc = new JCheckBox(null,null,true);
            add(jc);
            add(new JLabel(text,th.getIcon(),JLabel.LEADING));
        }
        public boolean isSelected() {return jc.isSelected();}
        public void select(boolean b) {jc.setSelected(b);}
    }
    
    /************** import selected files into destination directory **/
    private void actionImportFiles() {
        Worker.post(new Job(){
            public Object run() { _actionImportFiles();
                return null;
            }});
    }
    private void _actionImportFiles() {
        Iterator i=listOfUpdates().getAllPanels().iterator();
        TariffInfo ti;
        int counter = 0;
        while (i.hasNext()) {
            ti = (TariffInfo) i.next();
            if (ti.isSelected()) {
                try {
                    // look if it's a file or a web t
                    String su = ti.tr.getUrl(); 
                    
                    TariffEyeQuery teq = null;
                    if (! su.startsWith("file://")) {
                        teq = new TariffEyeQuery("TARIF");
                        teq.addParam("TARIF",su);
                    }
                    m_log.warn("****"+su);
                   
                    // extract FileName
                    String temp = su;
                    int z = temp.lastIndexOf('/');
                    if (z >= 0) {
                        try {
                            temp=URLDecoder.decode(temp.substring(z+1),"UTF-8");
                        } catch (UnsupportedEncodingException e1) {
                            m_log.error("failed",e1);
                        }
                    }
                    
                    File dest = new File(destDir,temp);
                    
                    int count = 0;
                    while (dest.exists()) {
                        String ext = FileUtilities.getExtension(dest);
                        String s = FileUtilities.getNameWithoutExtension(dest);
                        dest = new File(s+count+"."+ext);
                    }
                    
                    counter++;
                    
                    if (teq == null) {
                        CopyFile.to(new URL(su),dest);
                    } else {
                        CopyFile.to(teq.queryInputStream(),dest);
                    }
                    
                    listOfUpdates().removePanel(ti);
                    
                    
                } catch (MalformedURLException e) { m_log.error("",e);  }

                
                listOfUpdates().removePanel(ti);
            }
        }
        
        
        if (counter == 0) {
            JOptionPane.showMessageDialog(this,
                    Lang.translate("No tariff selected."));
        } else {
            refreshKnowntarifs();
        }
    }
    
    /************** source access methods **************************/
    
    
    /** retreive a Tariff List From a directory **/
    private UpdatePackage getTarificationsFile(File dir) {
       
        if (! dir.exists() || dir.isFile()) {
            error(
                    Secu.Monitor.ERROR_UNDEF,dir+" is not a directory",null);
            return null;
        }
        
        // look for update file
        RunFirst runFirst = null;
        File updateFile = new File(dir,RUNFIRST_FILENAME);
        if (updateFile.exists()) {
            Object o;
            try {
                o = Secu.getData(updateFile,DES_KEY);
                if (o instanceof RunFirst) {
                    runFirst = (RunFirst) o;
                }
            } catch (SimpleException e1) {
               m_log.error("Invalid RunFirst file",e1);
            }
        }
        
        if (runFirst == null) {
            m_log.warn("Cannot find runFirst Object:"+updateFile);
            runFirst = new RunFirst(null,null,new byte[0]);
        }
        
        
        // look for tarifs
        File[] tariffs = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return FileUtilities.getExtension(name).equals("tye");
            }});
        
        //Header List
        ArrayList/*<TarificationHeader>*/ headers = 
            new ArrayList()/*<TarificationHeader>*/;
        TarificationHeader th;
        try {
            for (int i =0; i < tariffs.length; i++) {
                
                th = (TarificationHeader) Secu.getHeader(tariffs[i]);
                if (th != null) {
                    headers.add(new TarificationReference(
                            th,tariffs[i].length(),tariffs[i].toURI()+""));
                }
            }
        } catch (SimpleException e) {
           
        }
        
        return new UpdatePackage(headers,runFirst);
    }
    
   
    
    /** retreive a Tariff List From an internet adress **/
    private UpdatePackage getTarificationsWEB() {
        onlyOneError = false;
        
        try {
            TariffEyeQuery teq = new TariffEyeQuery("TARIFUPDATES");
            m_log.debug(teq.getURL());
            InputStream is = teq.queryInputStream();
            Object o = Secu.getData(is,-1,DES_KEY,this);
            is.close();
            if (o instanceof UpdatePackage) {
                return (UpdatePackage) o;
            } 
            
            
        } catch(SimpleException se) {
            m_log.error("",se);
            error(
                    Secu.Monitor.ERROR_INVALID_FILE,"Invalid document",se);
        } catch (IOException e1) {
            m_log.error("",e1);
            error(
                    Secu.Monitor.ERROR_INVALID_FILE,"Cannot connect to server",
                    e1);
        };
        m_log.error("",new Exception());
        error(
                Secu.Monitor.ERROR_INVALID_FILE,"Invalid document",
                new Exception());
        done();
        return null;
    }
    
    
    //-------------------------------- MONITOR -------------------------//
    /**
     * @see Monitor#setMonitors(java.lang.String[])
     */
    public void setMonitors(String[] monitors) {
       
    }

    /**
     * @see Monitor#valueChange(java.lang.String, long, long)
     */
    public void valueChange(String monitor, long value, long pos) {
       //m_log.debug(monitor+" "+value+" "+pos);
    }

    /**
     * @see Monitor#done()
     */
    public void done() {
        onlyOneError = false;
    }

    boolean onlyOneError = false;
    /**
     * @see Monitor#error(int, String, Exception)
     */
    public void error(int code, String message, Throwable e) {
        if (onlyOneError) return;
        onlyOneError = true;
        message = 
            "<HTML><B>"+
            Lang.translate("An error occured, operation cannot be completed.")
            +"</B><HR><SMALL>"+message+"</SMALL><HR>"
            +"<SMALL>Guru meditation:"+e+"</SMALL>"
            +"</HTML>";
        JOptionPane.showMessageDialog(this,message);
    }
    
}
