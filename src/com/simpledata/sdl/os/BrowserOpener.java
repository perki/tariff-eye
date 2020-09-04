/*
 * $Id: BrowserOpener.java,v 1.1 2006/12/03 12:48:45 perki Exp $
 */
package com.simpledata.sdl.os;

/*
 * Control a web browser from your java application.
 * Copyright (C) 2001-2002 Stephen Ostermiller
 * http://ostermiller.org/contact.pl?regarding=Java+Utilities
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Allows URLs to be opened in the system browser on Windows and Unix.
 * More information about this class is available from <a target="_top" href=
 * "http://ostermiller.org/utils/Browser.html">ostermiller.org</a>.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public class BrowserOpener {

    /**
     * The dialog that allows user configuration of the options for this class.
     *
     * @since ostermillerutils 1.00.00
     */
    protected static BrowserDialog dialog;

    /**
     * Locale specific strings displayed to the user.
     *
     * @since ostermillerutils 1.00.00
     */
    protected static ResourceBundle labels 
    = new ResourceBundle(){

        public Enumeration getKeys() {
            // TODO Auto-generated method stub
            return null;
        }

        protected Object handleGetObject(String key) {
            // TODO Auto-generated method stub
            return key;
        }};

    /**
     * Set the locale used for getting localized
     * strings.
     *
     * @param locale Locale used to for i18n.
     *
     * @since ostermillerutils 1.00.00
     */
    public static void setLocale(Locale locale){
        labels = ResourceBundle.getBundle("com.simpledata.sdl.os.BrowserOpener",  
                locale);
    }

    /**
     * A list of commands to try in order to display the url.
     * The url is put into the command using MessageFormat, so
     * the URL will be specified as {0} in the command.
     * Some examples of commands to try might be:<br>
     * <code>rundll32 url.dll,FileProtocolHandler {0}</code></br>
     * <code>netscape {0}</code><br>
     * These commands are passed in order to exec until something works
     * when displayURL is used.
     *
     * @since ostermillerutils 1.00.00
     */
    public static String[] exec = null;

    /**
     * Determine appropriate commands to start a browser on the current
     * operating system.  On windows: <br>
     * <code>rundll32 url.dll,FileProtocolHandler {0}</code></br>
     * On other operating systems, the "which" command is used to
     * test if Mozilla, netscape, and lynx(xterm) are available (in that
     * order).
     *
     * @since ostermillerutils 1.00.00
     */
    public static void init(){
        exec = defaultCommands();
    }

    /**
     * Retrieve the default commands to open a browser for this system.
     *
     * @since ostermillerutils 1.00.00
     */
    public static String[] defaultCommands(){
        String[] exec = null;
        if ( System.getProperty("os.name").startsWith("Windows")){
            exec = new String[]{
                "rundll32 url.dll,FileProtocolHandler {0}",
            };
        } else if (System.getProperty("os.name").startsWith("Mac")){
            Vector browsers = new Vector();
            try {
                Process p = Runtime.getRuntime().exec("which open");
                if (p.waitFor() == 0){
                    browsers.add("open {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            if (browsers.size() == 0){
                exec = null;
            } else {
                exec = (String[])browsers.toArray(new String[0]);
            }
        } else {
            Vector browsers = new Vector();
            try {
                Process p = Runtime.getRuntime().exec("which firebird");
                if (p.waitFor() == 0){
                    browsers.add("firebird -remote openURL({0})");
                    browsers.add("firebird {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }try {
                Process p = Runtime.getRuntime().exec("which mozilla");
                if (p.waitFor() == 0){
                    browsers.add("mozilla -remote openURL({0})");
                    browsers.add("mozilla {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which opera");
                if (p.waitFor() == 0){
                    browsers.add("opera -remote openURL({0})");
                    browsers.add("opera {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which galeon");
                if (p.waitFor() == 0){
                    browsers.add("galeon {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which konqueror");
                if (p.waitFor() == 0){
                    browsers.add("konqueror {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which netscape");
                if (p.waitFor() == 0){
                    browsers.add("netscape -remote openURL({0})");
                    browsers.add("netscape {0}");
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            try {
                Process p = Runtime.getRuntime().exec("which xterm");
                if (p.waitFor() == 0){
                    p = Runtime.getRuntime().exec("which lynx");
                    if (p.waitFor() == 0){
                        browsers.add("xterm -e lynx {0}");
                    }
                }
            } catch (IOException e){
            } catch (InterruptedException e){
            }
            if (browsers.size() == 0){
                exec = null;
            } else {
                exec = (String[])browsers.toArray(new String[0]);
            }
        }
        return exec;
    }

    /**
     * Save the options used to the given properties file.
     * Property names used will all start with com.Ostermiller.util.Browser
     * Properties are saved in such a way that a call to load(props); will
     * restore the state of this class.
     * If the default commands to open a browser are being used then
     * they are not saved in the properties file, assuming that the user
     * will want to use the defaults next time even if the defaults change.
     *
     * @param props properties file to which configuration is saved.
     *
     * @since ostermillerutils 1.00.00
     */
    public static void save(Properties props){
        boolean saveBrowser = false;
        if (BrowserOpener.exec != null && BrowserOpener.exec.length > 0){
            String[] exec = BrowserOpener.defaultCommands();
            if (exec != null && exec.length == BrowserOpener.exec.length){
                for (int i=0; i<exec.length; i++){
                    if (!exec[i].equals(BrowserOpener.exec[i])){
                        saveBrowser = true;
                    }
                }
            } else {
                saveBrowser = true;
            }
        }
        if (saveBrowser){
            StringBuffer sb = new StringBuffer();
            for (int i=0; BrowserOpener.exec != null && i < BrowserOpener.exec.length; i++){
                sb.append(BrowserOpener.exec[i]).append('\n');
            }
            props.put("com.Ostermiller.util.BrowserOpener.open", sb.toString());
        } else {
            props.remove("com.Ostermiller.util.BrowserOpener.open");
        }
    }

    /**
     * Load the options for this class from the given properties file.
     * This method is designed to work with the save(props) method.  All
     * properties used will start with com.Ostermiller.util.BrowserOpener.  If
     * no configuration is found, the default configuration will be used.
     * If this method is used, a call to BrowserOpener.init(); is not needed.
     *
     * @param props properties file from which configuration is loaded.
     *
     * @since ostermillerutils 1.00.00
     */
    public static void load(Properties props){
        if (props.containsKey("com.Ostermiller.util.BrowserOpener.open")){
            java.util.StringTokenizer tok = new java.util.StringTokenizer(props.getProperty("com.Ostermiller.util.BrowserOpener.open"), "\r\n", false);
            int count = tok.countTokens();
            String[] exec = new String[count];
            for (int i=0; i < count; i++){
                exec[i] = tok.nextToken();
            }
            BrowserOpener.exec = exec;
        } else {
            BrowserOpener.init();
        }
    }

    /**
     * Display a URL in the system BrowserOpener.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * For security reasons, the URL will may not be passed directly to the
     * browser as it is passed to this method.  The URL may be made safe for
     * the exec command by URLEncoding the URL before passing it.
     *
     * @param url the url to display
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURL(String url) throws IOException {
        if (exec == null || exec.length == 0){
            if (System.getProperty("os.name").startsWith("Mac")){
                boolean success = false;
            try {
                Class nSWorkspace;
                    if (new File("/System/Library/Java/com/apple/cocoa/application/NSWorkspace.class").exists()){
                         // Mac OS X has NSWorkspace, but it is not in the classpath, add it.
                         ClassLoader classLoader = new URLClassLoader(new URL[]{new File("/System/Library/Java").toURL()});
                         nSWorkspace = Class.forName("com.apple.cocoa.application.NSWorkspace", true, classLoader);
                    } else {
                         nSWorkspace = Class.forName("com.apple.cocoa.application.NSWorkspace");
                    }
                    Method sharedWorkspace = nSWorkspace.getMethod("sharedWorkspace", new Class[] {});
                    Object workspace = sharedWorkspace.invoke(null, new Object[] {});
                    Method openURL = nSWorkspace.getMethod("openURL", new Class[] {Class.forName("java.net.URL")});
                    success = ((Boolean)openURL.invoke(workspace, new Object[] {new java.net.URL(url)})).booleanValue();
                //success = com.apple.cocoa.application.NSWorkspace.sharedWorkspace().openURL(new java.net.URL(url));
            } catch (Exception x) {}
                if (!success){
                    try {
                         Class mrjFileUtils = Class.forName(
                                 "com.apple.mrj.MRJFileUtils");
                         Method openURL = mrjFileUtils.getMethod(
                                 "openURL", new Class[] {
                                         Class.forName("java.lang.String")});
                         openURL.invoke(null, new Object[] {url});
                         //com.apple.mrj.MRJFileUtils.openURL(url);
                    } catch (Exception x){
                         System.err.println(x.getMessage());
                         throw new IOException(labels.getString("failed"));
                    }
                }
            } else {
                throw new IOException(labels.getString("nocommand"));
            }
        } else {
            // for security, see if the url is valid.
            // this is primarily to catch an attack in which the url
            // starts with a - to fool the command line flags, bu
            // it could catch other stuff as well, and will throw a
            // MalformedURLException which will give the caller of this
            // function useful information.
            new URL(url);
            // escape any weird characters in the url.  This is primarily
            // to prevent an attacker from putting in spaces
            // that might fool exec into allowing
            // the attacker to execute arbitrary code.
            StringBuffer sb = new StringBuffer(url.length());
            for (int i=0; i<url.length(); i++){
                char c = url.charAt(i);
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                        || c == '.' || c == ':' || c == '&' || c == '@' || c == '/' || c == '?'
                        || c == '%' || c =='+' || c == '=' || c == '#' || c == '-' || c == '\\'){
                    //characters that are necessary for URLs and should be safe
                    //to pass to exec.  Exec uses a default string tokenizer with
                    //the default arguments (whitespace) to separate command line
                    //arguments, so there should be no problem with anything bu
                    //whitespace.
                    sb.append(c);
                } else {
                    c = (char)(c & 0xFF); // get the lowest 8 bits (URLEncoding)
                    if (c < 0x10){
                        sb.append("%0" + Integer.toHexString(c));
                    } else {
                        sb.append("%" + Integer.toHexString(c));
                    }
                }
            }
            String[] messageArray = new String[1];
            messageArray[0] = sb.toString();
            String command = null;
            boolean found = false;
            // try each of the exec commands until something works
            try {
                for (int i=0; i<exec.length && !found; i++){
                    try {
                        // stick the url into the command
                        command = MessageFormat.format(exec[i], (Object[]) messageArray);
                        // parse the command line.
                        Vector argsVector = new Vector();
                        BrowserCommandLexer lex 
                        = new BrowserCommandLexer(new StringReader(command));
                        String t;
                        while ((t = lex.getNextToken()) != null) {
                            argsVector.add(t);
                        }
                        String[] args = new String[argsVector.size()];
                        args = (String[])argsVector.toArray(args);
                        // the windows url protocol handler doesn't work well with file URLs.
                        // Correct those problems here before continuing
                        // Java File.toURL() gives only one / following file: bu
                        // we need two.
                        // If there are escaped characters in the url, we will have
                        // to create an Internet shortcut and open that, as the command
                        // line version of the rundll doesn't like them.
                        boolean useShortCut = false;
                        if (args[0].equals("rundll32") && args[1].equals("url.dll,FileProtocolHandler")){
                            if (args[2].startsWith("file:/")){
                                if (args[2].charAt(6) != '/'){
                                    args[2] = "file://" + args[2].substring(6);
                                }
                                if (args[2].charAt(7) != '/'){
                                    args[2] = "file:///" + args[2].substring(7);
                                }
                                useShortCut = true;
                            } else if (args[2].toLowerCase().endsWith("html") || args[2].toLowerCase().endsWith("htm")){
                                useShortCut = true;
                            }
                        }
                        if (useShortCut){
                            File shortcut = File.createTempFile("OpenInBrowser", ".url");
                            shortcut = shortcut.getCanonicalFile();
                            shortcut.deleteOnExit();
                            PrintWriter out = new PrintWriter(new FileWriter(shortcut));
                            out.println("[InternetShortcut]");
                            out.println("URL=" + args[2]);
                            out.close();
                            args[2] = shortcut.getCanonicalPath();
                        }
                        // start the browser
                        Process p = Runtime.getRuntime().exec(args);

                        // give the browser a bit of time to fail.
                        // I have found that sometimes sleep doesn't work
                        // the first time, so do it twice.  My tests
                        // seem to show that 1000 milliseconds is enough
                        // time for the browsers I'm using.
                        for (int j=0; j<2; j++){
                             try{
                                    Thread.currentThread().sleep(1000);
                             } catch (InterruptedException inte){
                             }
                        }
                        if (p.exitValue() == 0){
                             // this is a weird case.  The browser exited after
                             // a couple seconds saying that it successfully
                             // displayed the url.  Either the browser is lying
                             // or the user closed it *really* quickly.  Oh well.
                             found = true;
                        }
                    } catch (IOException x){
                        // the command was not a valid command.
                        System.err.println(labels.getString("warning") + " " + x.getMessage());
                    }
                }
                if (!found){
                    // we never found a command that didn't terminate with an error.
                    throw new IOException(labels.getString("failed"));
                }
            } catch (IllegalThreadStateException e){
                // the browser is still running.  This is a good sign.
                // lets just say that it is displaying the url right now!
            }
        }
    }

    /**
     * Display the URLs, each in their own window, in the system BrowserOpener.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * If more than one URL is given an HTML page containing JavaScript will
     * be written to the local drive, that page will be opened, and it will
     * open the rest of the URLs.
     *
     * @param urls the list of urls to display
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURLs(String[] urls) throws IOException {
        if (urls == null || urls.length == 0){
            return;
        }
        if (urls.length == 1){
            displayURL(urls[0]);
            return;
        }
        File shortcut = File.createTempFile("DisplayURLs", ".html");
        shortcut = shortcut.getCanonicalFile();
        shortcut.deleteOnExit();
        PrintWriter out = new PrintWriter(new FileWriter(shortcut));
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + labels.getString("html.openurls") + "</title>");
        out.println("<script language=\"javascript\" type=\"text/javascript\">");
        out.println("function displayURLs(){");
        for (int i=1; i<urls.length; i++){
            out.println("window.open(\"" + urls[i] + "\", \"_blank\", \"toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes\");");
        }
        out.println("location.href=\"" + urls[0] + "\";");
        out.println("}");
        out.println("</script>");
        out.println("</head>");
        out.println("<body onload=\"javascript:displayURLs()\">");
        out.println("<noscript>");
        for (int i=0; i<urls.length; i++){
            out.println("<a target=\"_blank\" href=\"" + urls[i] + "\">" + urls[i] + "</a><br>");
        }
        out.println("</noscript>");
        out.println("</body>");
        out.println("</html>");
        out.close();
        displayURL(shortcut.toURL().toString());
    }

    /**
     * Display the URL in a new window.
     *
     * Uses javascript to check history.length to determine if the browser opened a
     * new window already.  If it did, the url is shown in that window, if not, it is
     * shown in new window.
     *
     * Some browsers do not allow the length of history to be viewed by a web page.  In that
     * case, the url will be displayed in the current window.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * @param url the url to display in a new window.
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURLinNew(String url) throws IOException {
        displayURLsinNew (new String[] {url});
    }

    /**
     * Display the URLs, each in their own window, in the system browser and the first in
     * the named window.
     *
     * The first URL will only be opened in the named window if the browser did no
     * open it in a new window to begin with.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * An html page containing javascript will
     * be written to the local drive, that page will be opened, and it will
     * open all the urls.
     *
     * @param urls the list of urls to display
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURLsinNew(String[] urls) throws IOException {
        if (urls == null || urls.length == 0){
            return;
        }
        File shortcut = File.createTempFile("DisplayURLs", ".html");
        shortcut.deleteOnExit();
        shortcut = shortcut.getCanonicalFile();
        PrintWriter out = new PrintWriter(new FileWriter(shortcut));
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + labels.getString("html.openurls") + "</title>");
        out.println("<script language=\"javascript\" type=\"text/javascript\">");
        out.println("function displayURLs(){");
        out.println("var hlength = 0;");
        out.println("try {");
        out.println("hlength = history.length;");
        out.println("} catch (e) {}");
        out.println("if (hlength>0) {");
        out.println("window.open(\"" + urls[0] + "\", \"_blank\", \"toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes\");");
        out.println("}");
        for (int i=1; i<urls.length; i++){
            out.println("window.open(\"" + urls[i] + "\", \"_blank\", \"toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes\");");
        }
        out.println("if (hlength==0) {");
        out.println("location.href=\"" + urls[0] + "\";");
        out.println("} else {");
        out.println("history.back()");
        out.println("}");
        out.println("}");
        out.println("</script>");
        out.println("</head>");
        out.println("<body onload=\"javascript:displayURLs()\">");
        out.println("<noscript>");
        for (int i=0; i<urls.length; i++){
            out.println("<a target=\"_blank\" href=\"" + urls[i] + "\">" + urls[i] + "</a><br>");
        }
        out.println("</noscript>");
        out.println("</body>");
        out.println("</html>");
        out.close();
        displayURL(shortcut.toURL().toString());
    }

    /**
     * Display the URL in the named window.
     *
     * If the browser opens a new window by default, this will likely cause a duplicate window
     * to be opened.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * @param url the url to display
     * @param namedWindow the name of the desired window.
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURL(String url, String namedWindow) throws IOException {
        displayURLs (new String[] {url}, new String[] {namedWindow});
    }

    /**
     * Display the URLs in the named windows.
     *
     * If the browser opens a new window by default, this will likely cause a duplicate window
     * to be opened.  This method relies on the browser to support javascript.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * Extra names for windows will be ignored, and if there are too few names, the remaining
     * windows will be named "_blank".
     *
     * @param urls the list of urls to display
     * @param namedWindows the list of names for the windows.
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURLs(String[] urls, String[] namedWindows) throws IOException {
        if (urls == null || urls.length == 0){
            return;
        }
        File shortcut = File.createTempFile("DisplayURLs", ".html");
        shortcut.deleteOnExit();
        shortcut = shortcut.getCanonicalFile();
        PrintWriter out = new PrintWriter(new FileWriter(shortcut));
        out.println("<html>");
        out.println("<head>");
        out.println("<title>" + labels.getString("html.openurls") + "</title>");
        out.println("<base target=\"" + ((namedWindows==null||namedWindows.length==0||namedWindows[0]==null)?"_blank":namedWindows[0]) + "\">");
        out.println("<script language=\"javascript\" type=\"text/javascript\">");
        for (int i=1; i<urls.length; i++){
            out.println("window.open(\"" + urls[i] + "\", \"" + ((namedWindows==null||namedWindows.length<=i||namedWindows[i]==null)?"_blank":namedWindows[i]) + "\", \"toolbar=yes,location=yes,directories=yes,status=yes,menubar=yes,scrollbars=yes,resizable=yes\");");
        }
        out.println("location.href=\"" + urls[0] + "\";");
        out.println("</script>");
        out.println("</head>");
        out.println("<body onload=\"javascript:displayURLs()\">");
        out.println("<noscript>");
        for (int i=0; i<urls.length; i++){
            out.println("<a target=\"" + ((namedWindows==null||namedWindows.length==0||namedWindows[0]==null)?"_blank":namedWindows[0]) + "\" href=\"" + urls[i] + "\">" + urls[i] + "</a><br>");
        }
        out.println("</noscript>");
        out.println("</body>");
        out.println("</html>");
        out.close();
        displayURL(shortcut.toURL().toString());
    }

    /**
     * Display the URLs the first in the given named window.
     *
     * If the browser opens a new window by default, this will likely cause a duplicate window
     * to be opened.  This method relies on the browser to support javascript.
     *
     * BrowserOpener.init() should be called before calling this function or
     * BrowserOpener.exec should be set explicitly.
     *
     * @param urls the list of urls to display
     * @param namedWindow the name of the first window to use.
     * @throws IOException if the url is not valid or the browser fails to star
     *
     * @since ostermillerutils 1.00.00
     */
    public static void displayURLs(String[] urls, String namedWindow) throws IOException {
        displayURLs(urls, new String[] {namedWindow});
    }

    /**
     * Open the url(s) specified on the command line in your BrowserOpener.
     *
     * @param args Command line arguments (URLs)
     */
    public static void main(String[] args){
        try {
            BrowserOpener.init();
            if (BrowserOpener.dialogConfiguration(null)){
                if (args.length == 0){
                    BrowserOpener.displayURLs(new String[]{
                        "http://www.google.com/",
                        "http://dmoz.org/",
                        "http://ostermiller.org",
                    }, "fun");
                } else if (args.length == 1){
                    BrowserOpener.displayURL(args[0], "fun");
                } else {
                    BrowserOpener.displayURLs(args, "fun");
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException x){
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
        System.exit(0);
    }

    /**
     * Show a dialog that allows the user to configure the
     * command lines used for starting a browser on their system.
     *
     * @param owner The frame that owns the dialog.
     *
     * @since ostermillerutils 1.00.00
     */
    public static boolean dialogConfiguration(Frame owner){
        dialogConfiguration(owner, null);
        return BrowserOpener.dialog.changed();
    }

    /**
     * Show a dialog that allows the user to configure the
     * command lines used for starting a browser on their system.
     * String used in the dialog are taken from the given
     * properties.  This dialog can be customized or displayed in
     * multiple languages.
     * <P>
     * Properties that are used:
     * com.Ostermiller.util.BrowserDialog.title<br>
     * com.Ostermiller.util.BrowserDialog.description<br>
     * com.Ostermiller.util.BrowserDialog.label<br>
     * com.Ostermiller.util.BrowserDialog.defaults<br>
     * com.Ostermiller.util.BrowserDialog.browse<br>
     * com.Ostermiller.util.BrowserDialog.ok<br>
     * com.Ostermiller.util.BrowserDialog.cancel<br>
     *
     * @param owner The frame that owns this dialog.
     * @param props contains the strings used in the dialog.
     * @deprecated  Use the com.Ostermiller.util.Browser resource bundle to set strings for the given locale.
     *
     * @since ostermillerutils 1.00.00
     */
    public static boolean dialogConfiguration(Frame owner, Properties props){
        if (BrowserOpener.dialog == null){
            BrowserOpener.dialog = new BrowserDialog(owner);
        }
        if (props != null){
            BrowserOpener.dialog.setProps(props);
        }
        BrowserOpener.dialog.show();
        return BrowserOpener.dialog.changed();
    }

    /**
     * Where the command lines are typed.
     *
     * @since ostermillerutils 1.00.00
     */
    private static JTextArea description;

    /**
     * Where the command lines are typed.
     *
     * @since ostermillerutils 1.00.00
     */
    private static JTextArea commandLinesArea;

    /**
     * The reset button.
     *
     * @since ostermillerutils 1.00.00
     */
    private static JButton resetButton;

    /**
     * The browse button.
     *
     * @since ostermillerutils 1.00.00
     */
    private static JButton browseButton;

    /**
     * The label for the field in which the name is typed.
     *
     * @since ostermillerutils 1.00.00
     */
    private static JLabel commandLinesLabel;

    /**
     * File dialog for choosing a browser
     *
     * @since ostermillerutils 1.00.00
     */
    private static JFileChooser fileChooser;

    /**
     * A panel used in the options dialog.  Null until getDialogPanel() is called.
     */
    private static JPanel dialogPanel = null;
    private static Window dialogParent = null;

    /**
     * If you wish to add to your own dialog box rather than have a separate
     * one just for the browser, use this method to get a JPanel that can
     * be added to your own dialog.
     *
     * mydialog.add(BrowserOpener.getDialogPanel(mydialog));
     * BrowserOpener.initPanel();
     * mydialog.show();
     * if (ok_pressed){
     * &nbsp;&nbsp;BrowserOpener.userOKedPanelChanges();
     * }
     *
     * @param parent window into which panel with eventually be placed.
     * @since ostermillerutils 1.02.22
     */
    public static JPanel getDialogPanel(Window parent){
        dialogParent = parent;
        if (dialogPanel == null){
            commandLinesArea = new JTextArea("", 8, 40);
            JScrollPane scrollpane = new JScrollPane(commandLinesArea);
            resetButton = new JButton(labels.getString("dialog.reset"));
            browseButton = new JButton(labels.getString("dialog.browse"));
            commandLinesLabel = new JLabel(labels.getString("dialog.commandLines"));
            description = new JTextArea(labels.getString("dialog.description"));
            description.setEditable(false);
            description.setOpaque( false );

            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    Object source = e.getSource();
                    if (source == resetButton){
                        setCommands(BrowserOpener.defaultCommands());
                    } else if (source == browseButton){
                        if (fileChooser == null){
                            fileChooser = new JFileChooser();
                        }
                        if (fileChooser.showOpenDialog(dialogParent) == JFileChooser.APPROVE_OPTION){
                            String app = fileChooser.getSelectedFile().getPath();
                            StringBuffer sb = new StringBuffer(2 * app.length());
                            for (int i=0; i<app.length(); i++){
                                char c = app.charAt(i);
                                // escape these two characters so that we can later parse the stuff
                                if (c == '\"' || c == '\\') {
                                    sb.append('\\');
                                }
                                sb.append(c);
                            }
                            app = sb.toString();
                            if (app.indexOf(" ") != -1){
                                app = '"' + app + '"';
                            }
                            String commands = commandLinesArea.getText();
                            if (commands.length() != 0 && !commands.endsWith("\n") && !commands.endsWith("\r")){
                                commands += "\n";
                            }
                            commandLinesArea.setText(commands + app + " {0}");
                        }
                    }
                }
            };

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            c.insets.top = 5;
            c.insets.bottom = 5;
            dialogPanel = new JPanel(gridbag);
            dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
            JLabel label;


            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(description, c);
            dialogPanel.add(description);

            c.gridy = 1;
            c.gridwidth = GridBagConstraints.RELATIVE;
            gridbag.setConstraints(commandLinesLabel, c);
            dialogPanel.add(commandLinesLabel);
            JPanel buttonPanel = new JPanel();
            c.anchor = GridBagConstraints.EAST;
            browseButton.addActionListener(actionListener);
            buttonPanel.add(browseButton);
            resetButton.addActionListener(actionListener);
            buttonPanel.add(resetButton);
            gridbag.setConstraints(buttonPanel, c);
            dialogPanel.add(buttonPanel);

            c.gridy = 2;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.WEST;
            gridbag.setConstraints(scrollpane, c);
            dialogPanel.add(scrollpane);
        }
        return dialogPanel;
    }


    /**
     * A modal dialog that presents configuration option for this class.
     *
     * @since ostermillerutils 1.00.00
     */
    private static class BrowserDialog extends JDialog {

        /**
         * The OK button.
         *
         * @since ostermillerutils 1.00.00
         */
        private JButton okButton;

        /**
         * The cancel button.
         *
         * @since ostermillerutils 1.00.00
         */
        private JButton cancelButton;

        /**
         * The label for the field in which the name is typed.
         *
         * @since ostermillerutils 1.00.00
         */
        private JLabel commandLinesLabel;

        /**
         * update this variable when the user makes an action
         *
         * @since ostermillerutils 1.00.00
         */
        private boolean pressed_OK = false;


        /**
         * Properties that are used:
         * com.Ostermiller.util.BrowserDialog.title<br>
         * com.Ostermiller.util.BrowserDialog.description<br>
         * com.Ostermiller.util.BrowserDialog.label<br>
         * com.Ostermiller.util.BrowserDialog.defaults<br>
         * com.Ostermiller.util.BrowserDialog.browse<br>
         * com.Ostermiller.util.BrowserDialog.ok<br>
         * com.Ostermiller.util.BrowserDialog.cancel<br>
         *
         * @deprecated  Use the com.Ostermiller.util.Browser resource bundle to set strings for the given locale.
         *
         * @since ostermillerutils 1.00.00
         */
        private void setProps(Properties props){
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.title")){
                setTitle(props.getProperty("com.Ostermiller.util.BrowserDialog.title"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.description")){
                description.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.description"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.label")){
                commandLinesLabel.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.label"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.defaults")){
                resetButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.defaults"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.browse")){
                browseButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.browse"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.ok")){
                okButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.ok"));
            }
            if (props.containsKey("com.Ostermiller.util.BrowserDialog.cancel")){
                cancelButton.setText(props.getProperty("com.Ostermiller.util.BrowserDialog.cancel"));
            }
            pack();
        }

        /**
         * Whether the user pressed the applied changes.
         * true if OK was pressed or the user otherwise applied new changes,
         * false if cancel was pressed or dialog was closed with no changes.
         * If called before the dialog is displayed and closed, the results
         * are not defined.
         *
         * @returns if the user made changes to the browser configuration.
         *
         * @since ostermillerutils 1.00.00
         */
        public boolean changed() {
            return pressed_OK;
        }

        /**
         * Create this dialog with the given parent and title.
         *
         * @param parent window from which this dialog is launched
         * @param title the title for the dialog box window
         *
         * @since ostermillerutils 1.00.00
         */
        public BrowserDialog(Frame parent) {
            super(parent, labels.getString("dialog.title"), true);
            setLocationRelativeTo(parent);
            // super calls dialogInit, so we don't need to do it again.
        }

        /**
         * Called by constructors to initialize the dialog.
         *
         * @since ostermillerutils 1.00.00
         */
        protected void dialogInit(){

            super.dialogInit();

            getContentPane().setLayout(new BorderLayout());

            getContentPane().add(getDialogPanel(this), BorderLayout.CENTER);

            JPanel panel = new JPanel(new FlowLayout());
            okButton = new JButton(labels.getString("dialog.ok"));
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    pressed_OK = true;
                    BrowserDialog.this.hide();
                }
            });
            panel.add(okButton);
            cancelButton = new JButton(labels.getString("dialog.cancel"));
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e){
                    pressed_OK = false;
                    BrowserDialog.this.hide();
                }
            });
            panel.add(cancelButton);

            getContentPane().add(panel, BorderLayout.SOUTH);

            pack();
        }

        /**
         * Shows the dialog.
         *
         * @since ostermillerutils 1.00.00
         */
        public void show(){
            initPanel();
            super.show();
            if (pressed_OK){
                userOKedPanelChanges();
            }
        }
    }

    private static void setCommands(String[] newExec){
        StringBuffer sb = new StringBuffer();
        for (int i=0; newExec != null && i < newExec.length; i++){
            sb.append(newExec[i]).append('\n');
        }
        commandLinesArea.setText(sb.toString());
    }

    /**
     * If you are using the getDialogPanel() method to create your own dialog, this
     * method should be called every time before you display the dialog.
     *
     * mydialog.add(BrowserOpener.getDialogPanel(mydialog));
     * BrowserOpener.initPanel();
     * mydialog.show();
     * if (ok_pressed){
     * &nbsp;&nbsp;BrowserOpener.userOKedPanelChanges();
     * }
     *
     * @since ostermillerutils 1.02.22
     */
    public static void initPanel(){
        setCommands(exec);
    }

    /**
     * If you are using the getDialogPanel() method to create your own dialog, this
     * method should be called after you display the dialog if the user pressed ok.
     *
     * mydialog.add(BrowserOpener.getDialogPanel(mydialog));
     * BrowserOpener.initPanel();
     * mydialog.show();
     * if (ok_pressed){
     * &nbsp;&nbsp;BrowserOpener.userOKedPanelChanges();
     * }
     *
     * @since ostermillerutils 1.02.22
     */
    public static void userOKedPanelChanges(){
        java.util.StringTokenizer tok = new java.util.StringTokenizer(commandLinesArea.getText(), "\r\n", false);
        int count = tok.countTokens();
        String[] exec = new String[count];
        for (int i=0; i < count; i++){
            exec[i] = tok.nextToken();
        }
        BrowserOpener.exec = exec;
    }
}
/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.3.5
 * on 8/21/02 2:32 PM from the specification file
 * <tt>file:/home/steveo/personal/projects/java/com/Ostermiller/util/BrowserCommandLexer.lex</tt>
 */
class BrowserCommandLexer {

    /** This character denotes the end of file */
    final public static int YYEOF = -1;

    /** initial size of the lookahead buffer */
    final private static int YY_BUFFERSIZE = 16384;

    /** lexical states */
    final public static int YYINITIAL = 0;

    /** 
     * Translates characters to character classes
     */
    final private static String yycmap_packed = 
        "\11\0\2\2\1\0\2\2\22\0\1\2\1\0\1\3\36\0\1\0"+
        "\32\0\1\1\uffa3\0";

    /** 
     * Translates characters to character classes
     */
    final private static char [] yycmap = yy_unpack_cmap(yycmap_packed);

    /** 
     * Translates a state to a row index in the transition table
     */
    final private static int yy_rowMap [] = { 
                0,     4,     8,    12,    16,     8,    20,    24,     4,    28, 
             16,    32,    12,    24
    };

    /** 
     * The packed transition table of the DFA (part 0)
     */
    final private static String yy_packed0 = 
        "\1\2\1\3\1\4\1\5\1\2\1\6\1\0\5\2"+
        "\4\0\1\5\1\7\1\10\1\11\1\5\1\12\1\5"+
        "\1\13\1\10\1\14\1\10\1\15\1\5\1\7\1\10"+
        "\1\13\1\10\1\14\1\10\1\16";

    /** 
     * The transition table of the DFA
     */
    final private static int yytrans [] = yy_unpack();


    /* error codes */
    final private static int YY_UNKNOWN_ERROR = 0;
    final private static int YY_ILLEGAL_STATE = 1;
    final private static int YY_NO_MATCH = 2;
    final private static int YY_PUSHBACK_2BIG = 3;

    /* error messages for the codes above */
    final private static String YY_ERROR_MSG[] = {
        "Unkown internal scanner error",
        "Internal error: unknown state",
        "Error: could not match input",
        "Error: pushback value was too large"
    };

    /**
     * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
     */
    private final static byte YY_ATTRIBUTE[] = {
         1,  1,  1,  9,  1,  0,  0,  0,  1,  1,  1,  0,  9,  1
    };

    /** the input device */
    private java.io.Reader yy_reader;

    /** the current state of the DFA */
    private int yy_state;

    /** the current lexical state */
    private int yy_lexical_state = YYINITIAL;

    /** this buffer contains the current text to be matched and is
            the source of the yytext() string */
    private char yy_buffer[] = new char[YY_BUFFERSIZE];

    /** the textposition at the last accepting state */
    private int yy_markedPos;

    /** the textposition at the last state to be included in yytext */
    private int yy_pushbackPos;

    /** the current text position in the buffer */
    private int yy_currentPos;

    /** startRead marks the beginning of the yytext() string in the buffer */
    private int yy_startRead;

    /** endRead marks the last character in the buffer, that has been read
            from input */
    private int yy_endRead;

    /** number of newlines encountered up to the start of the matched text */
    private int yyline;

    /** the number of characters up to the start of the matched text */
    private int yychar;

    /**
     * the number of characters from the last newline up to the start of the 
     * matched text
     */
    private int yycolumn; 

    /** 
     * yy_atBOL == true <=> the scanner is currently at the beginning of a line
     */
    private boolean yy_atBOL = true;

    /** yy_atEOF == true <=> the scanner is at the EOF */
    private boolean yy_atEOF;

    /* user code: */
        /**
         * Prints out tokens and line numbers from a file or System.in.
         * If no arguments are given, System.in will be used for input.
         * If more arguments are given, the first argument will be used as
         * the name of the file to use as input
         *
         * @param args program arguments, of which the first is a filename
         */
        private static void main(String[] args) {
                InputStream in;
                try {
                        if (args.length > 0){
                                File f = new File(args[0]);
                                if (f.exists()){
                                        if (f.canRead()){
                                                in = new FileInputStream(f);
                                        } else {
                                                throw new IOException("Could not open " + args[0]);
                                        }
                                } else {
                                        throw new IOException("Could not find " + args[0]);
                                }                   
                        } else {
                                in = System.in;
                        }
                        BrowserCommandLexer shredder = new BrowserCommandLexer(in);
                        String t;
                        while ((t = shredder.getNextToken()) != null) {
                                System.out.println(t);
                        }
                } catch (IOException e){
                        System.out.println(e.getMessage());
                }
        }

        private static String unescape(String s){
                StringBuffer sb = new StringBuffer(s.length());
                for (int i=0; i<s.length(); i++){
                        if (s.charAt(i) == '\\' && i<s.length()){
                                i++;
                        }
                        sb.append(s.charAt(i));
                }
                return sb.toString();
        }


    /**
     * Creates a new scanner
     * There is also a java.io.InputStream version of this constructor.
     *
     * @param   in  the java.io.Reader to read input from.
     */
    BrowserCommandLexer(java.io.Reader in) {
        this.yy_reader = in;
    }

    /**
     * Creates a new scanner.
     * There is also java.io.Reader version of this constructor.
     *
     * @param   in  the java.io.Inputstream to read input from.
     */
    BrowserCommandLexer(java.io.InputStream in) {
        this(new java.io.InputStreamReader(in));
    }

    /** 
     * Unpacks the split, compressed DFA transition table.
     *
     * @return the unpacked transition table
     */
    private static int [] yy_unpack() {
        int [] trans = new int[36];
        int offset = 0;
        offset = yy_unpack(yy_packed0, offset, trans);
        return trans;
    }

    /** 
     * Unpacks the compressed DFA transition table.
     *
     * @param packed   the packed transition table
     * @return         the index of the last entry
     */
    private static int yy_unpack(String packed, int offset, int [] trans) {
        int i = 0;       /* index in packed string  */
        int j = offset;  /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            value--;
            do trans[j++] = value; while (--count > 0);
        }
        return j;
    }

    /** 
     * Unpacks the compressed character translation table.
     *
     * @param packed   the packed character translation table
     * @return         the unpacked character translation table
     */
    private static char [] yy_unpack_cmap(String packed) {
        char [] map = new char[0x10000];
        int i = 0;  /* index in packed string  */
        int j = 0;  /* index in unpacked array */
        while (i < 26) {
            int  count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do map[j++] = value; while (--count > 0);
        }
        return map;
    }


    /**
     * Refills the input buffer.
     *
     * @return      <code>false</code>, iff there was new input.
     * 
     * @exception   IOException  if any I/O-Error occurs
     */
    private boolean yy_refill() throws java.io.IOException {

        /* first: make room (if you can) */
        if (yy_startRead > 0) {
            System.arraycopy(yy_buffer, yy_startRead, 
                                             yy_buffer, 0, 
                                             yy_endRead-yy_startRead);

            /* translate stored positions */
            yy_endRead-= yy_startRead;
            yy_currentPos-= yy_startRead;
            yy_markedPos-= yy_startRead;
            yy_pushbackPos-= yy_startRead;
            yy_startRead = 0;
        }

        /* is the buffer big enough? */
        if (yy_currentPos >= yy_buffer.length) {
            /* if not: blow it up */
            char newBuffer[] = new char[yy_currentPos*2];
            System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
            yy_buffer = newBuffer;
        }

        /* finally: fill the buffer with new input */
        int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                                                                        yy_buffer.length-yy_endRead);

        if (numRead < 0) {
            return true;
        }
        else {
            yy_endRead+= numRead;  
            return false;
        }
    }


    /**
     * Closes the input stream.
     */
    final public void yyclose() throws java.io.IOException {
        yy_atEOF = true;            /* indicate end of file */
        yy_endRead = yy_startRead;  /* invalidate buffer    */

        if (yy_reader != null)
            yy_reader.close();
    }


    /**
     * Closes the current stream, and resets the
     * scanner to read from a new input stream.
     *
     * All internal variables are reset, the old input stream 
     * <b>cannot</b> be reused (internal buffer is discarded and lost).
     * Lexical state is set to <tt>YY_INITIAL</tt>.
     *
     * @param reader   the new input stream 
     */
    final public void yyreset(java.io.Reader reader) throws java.io.IOException {
        yyclose();
        yy_reader = reader;
        yy_atBOL  = true;
        yy_atEOF  = false;
        yy_endRead = yy_startRead = 0;
        yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
        yyline = yychar = yycolumn = 0;
        yy_lexical_state = YYINITIAL;
    }


    /**
     * Returns the current lexical state.
     */
    final public int yystate() {
        return yy_lexical_state;
    }


    /**
     * Enters a new lexical state
     *
     * @param newState the new lexical state
     */
    final public void yybegin(int newState) {
        yy_lexical_state = newState;
    }


    /**
     * Returns the text matched by the current regular expression.
     */
    final public String yytext() {
        return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
    }


    /**
     * Returns the character at position <tt>pos</tt> from the 
     * matched text. 
     * 
     * It is equivalent to yytext().charAt(pos), but faster
     *
     * @param pos the position of the character to fetch. 
     *            A value from 0 to yylength()-1.
     *
     * @return the character at position pos
     */
    final public char yycharat(int pos) {
        return yy_buffer[yy_startRead+pos];
    }


    /**
     * Returns the length of the matched text region.
     */
    final public int yylength() {
        return yy_markedPos-yy_startRead;
    }


    /**
     * Reports an error that occured while scanning.
     *
     * In a wellformed scanner (no or only correct usage of 
     * yypushback(int) and a match-all fallback rule) this method 
     * will only be called with things that "Can't Possibly Happen".
     * If this method is called, something is seriously wrong
     * (e.g. a JFlex bug producing a faulty scanner etc.).
     *
     * Usual syntax/scanner level error handling should be done
     * in error fallback rules.
     *
     * @param   errorCode  the code of the errormessage to display
     */
    private void yy_ScanError(int errorCode) {
        String message;
        try {
            message = YY_ERROR_MSG[errorCode];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
        }

        throw new Error(message);
    } 


    /**
     * Pushes the specified amount of characters back into the input stream.
     *
     * They will be read again by then next call of the scanning method
     *
     * @param number  the number of characters to be read again.
     *                This number must not be greater than yylength()!
     */
    private void yypushback(int number)  {
        if ( number > yylength() )
            yy_ScanError(YY_PUSHBACK_2BIG);

        yy_markedPos -= number;
    }


    /**
     * Resumes scanning until the next regular expression is matched,
     * the end of input is encountered or an I/O-Error occurs.
     *
     * @return      the next token
     * @exception   IOException  if any I/O-Error occurs
     */
    public String getNextToken() throws java.io.IOException {
        int yy_input;
        int yy_action;

        // cached fields:
        int yy_currentPos_l;
        int yy_startRead_l;
        int yy_markedPos_l;
        int yy_endRead_l = yy_endRead;
        char [] yy_buffer_l = yy_buffer;
        char [] yycmap_l = yycmap;

        int [] yytrans_l = yytrans;
        int [] yy_rowMap_l = yy_rowMap;
        byte [] yy_attr_l = YY_ATTRIBUTE;

        while (true) {
            yy_markedPos_l = yy_markedPos;

            yy_action = -1;

            yy_startRead_l = yy_currentPos_l = yy_currentPos = 
                                             yy_startRead = yy_markedPos_l;

            yy_state = yy_lexical_state;


            yy_forAction: {
                while (true) {

                    if (yy_currentPos_l < yy_endRead_l)
                        yy_input = yy_buffer_l[yy_currentPos_l++];
                    else if (yy_atEOF) {
                        yy_input = YYEOF;
                        break yy_forAction;
                    }
                    else {
                        // store back cached positions
                        yy_currentPos  = yy_currentPos_l;
                        yy_markedPos   = yy_markedPos_l;
                        boolean eof = yy_refill();
                        // get translated positions and possibly new buffer
                        yy_currentPos_l  = yy_currentPos;
                        yy_markedPos_l   = yy_markedPos;
                        yy_buffer_l      = yy_buffer;
                        yy_endRead_l     = yy_endRead;
                        if (eof) {
                            yy_input = YYEOF;
                            break yy_forAction;
                        }
                        else {
                            yy_input = yy_buffer_l[yy_currentPos_l++];
                        }
                    }
                    int yy_next = yytrans_l[ yy_rowMap_l[yy_state] + yycmap_l[yy_input] ];
                    if (yy_next == -1) break yy_forAction;
                    yy_state = yy_next;

                    int yy_attributes = yy_attr_l[yy_state];
                    if ( (yy_attributes & 1) == 1 ) {
                        yy_action = yy_state; 
                        yy_markedPos_l = yy_currentPos_l; 
                        if ( (yy_attributes & 8) == 8 ) break yy_forAction;
                    }

                }
            }

            // store back cached position
            yy_markedPos = yy_markedPos_l;

            switch (yy_action) {

                case 0: 
                case 1: 
                case 4: 
                case 9: 
                    { 
        return unescape(yytext());
 }
                case 15: break;
                case 2: 
                case 3: 
                    { 
 }
                case 16: break;
                case 8: 
                case 10: 
                case 12: 
                case 13: 
                    { 
        return unescape(yytext().substring(1, yytext().length()-1));
 }
                case 17: break;
                default: 
                    if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
                        yy_atEOF = true;
                        return null;
                    } 
                    else {
                        yy_ScanError(YY_NO_MATCH);
                    }
            }
        }
    }


}
