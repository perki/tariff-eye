/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: Lang.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/
package com.simpledata.bc.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.filetools.*;

import org.apache.log4j.Logger;

/**
* Language management class.<BR>
* read dictionnairies in ressources/dicts/<language code><BR>
* syntax of dictionairies is <key>;<phrase>.. carriage returns are coded by '|'<BR>
*<BR>
* Object with messages or Text have to be registred with register(Object,code) or
* register(Object,code,parameter)<BR>
* parameter can be used by the programmer to be passed back to the specified object.
*/
public class Lang {
	
	private static final Logger m_log = Logger.getLogger( Lang.class ); 

	/** contains object **/
	private WeakHashMap registars;
	/** contains tooltips **/
	private WeakHashMap registarsTT;

	private static Hashtable messages;

	private Hashtable availLang;
	private String langCode;

	private static String lastLangCode;

	/** boolean field that states if Lang is in a learning mode **/
	public static boolean learning= true;

	/** character used to split strings with fields **/
	public static final char FIELDCHAR= '%';

	/** string used to backup real FIELDCHAR */
	private static final String DUMMY= "Apfelgluck71283xzsgdjhazew";

	/** constructor */
	public Lang() {
		availLang= new Hashtable();
		registars= new WeakHashMap();
		registarsTT= new WeakHashMap();

		// Define languages in the order you wish to see them appear in lists
		availLang.put("en", "English");
		availLang.put("fr", "Fran\u00e7ais");

		/*
		availLang.put("es","Espa�ol");
		availLang.put("it","Italiano");
		availLang.put("de","Deutsch");
		availLang.put("r","Romanche");
		*/
		//availLang.put("it","Italiano"); 

		this.langCode= "en";
	}

	/** 
	* this method is called when a change lang is applied <BR>
	* ########################################################<BR>
	* ### update this code for every new object ##############<BR>
	* ########################################################<BR>
	**/
	public void setLang(String l) {

		initLang(l);
		BC.setParameter("lang", l);
		this.langCode= l;

		synchronized (registars) {
			Map.Entry me;

			// objects
			Iterator i= registars.entrySet().iterator();
			while (i.hasNext()) {
				me= (Map.Entry) i.next();
				setText(me.getKey(), (TMessage) me.getValue());
			}

			// tooltips
			Iterator i2= registarsTT.entrySet().iterator();
			while (i2.hasNext()) {
				me= (Map.Entry) i2.next();
				setToolTip(me.getKey(), (TMessage) me.getValue());
			}
		}
	}
	/** 
	* set this Text for this object
	*/
	private void setText(Object o, TMessage tm) {
		String msg= Lang.translate(tm);

		// tous les types de Jboutons
		if (AbstractButton.class.isInstance(o)) {
			((AbstractButton) o).setText(msg);
			return;
		}

		// tous les JLabel
		if (JLabel.class.isInstance(o)) {
			((JLabel) o).setText(msg);
			return;
		}

		// les strings pour les bords avec titre
		if (TitledBorder.class.isInstance(o)) {
			((javax.swing.border.TitledBorder) o).setTitle(msg);
			return;
		}

		m_log.error( "Cannot set message [" + msg + "] to :" + o );
	}

	/** 
	* set this ToolTip
	*/
	private void setToolTip(Object o, TMessage tm) {
		String msg= Lang.translate(tm);

		if (JComponent.class.isInstance(o)) {
			((JComponent) o).setToolTipText(msg);
			return;
		}
		m_log.error( "Cannot set tooltip [" + msg + "] to :" + o );
	}

	public String getLang() {
		return this.langCode;
	}

	/**
	 * private void learning method
	 */
	private static void learn(String key) {
		Exception e= new Exception();
		StackTraceElement[] st= e.getStackTrace();

		String fileName= "_UNKOWN.java";
		StackTraceElement se= null;
		for (int i= 0; i < st.length; i++) {
			// get the first non "Lang.java" file
			if (!st[i].getFileName().equals("Lang.java")) {
				fileName= st[i].getFileName();
				se= st[i];
				break;
			}
		}

		// get the learning path
		File f=
			new File(
				Resources.dictionariesPath()
					+ File.separator
					+ lastLangCode
					+ "dir");

		if (!f.exists()) {
			f.mkdir();
		}

		if (!f.isDirectory()) {
			m_log.error( "cannot learn in a file:" + f );
			return;
		}

		// get a file name for this file
		f= new File(f, fileName);
		f= FileUtilities.forceExtension(f, "dico");
		try {
			HashMap hm= null;
			if (f.exists())
				hm= TextFileUtils.getHashMapHashMap(f, '#', ';', '|');
			else
				hm= new HashMap();

			if (!hm.containsKey(se.getMethodName())) {
				hm.put(se.getMethodName(), new HashMap());
			}
			HashMap keyvalues= (HashMap) hm.get(se.getMethodName());
			if (!keyvalues.containsKey(key)) {
				keyvalues.put(key, "*" + key);
				m_log.info( "learned:" + key );
				//	save the new data
				TextFileUtils.setHashMapHashMap(hm, f, '#', ';', '|');
			}

		} catch (SimpleException e1) {
			m_log.error( e1 );
		}

	}

	/**
	* Load a new Language from file
	*/
	private void initLang(String l) {
		if (messages == null)
			messages= new Hashtable();
		lastLangCode= l;
		File f=
			new File(Resources.dictionariesPath() + File.separator + l + "dir");

		if (!f.isDirectory()) {
			m_log.error( "cannot find language dir:" + f );
			return;
		}
		messages.clear();

		try {

			// load general dicos first
			String[] exts= { "gendico", "dico" };

			for (int j= 0; j < exts.length; j++) {
				final String ext= exts[j];
				File[] fs= f.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return FileUtilities.getExtension(name).equals(ext);
					}
				});
				for (int i= 0; i < fs.length; i++) {
					TextFileUtils.getHashtableUpdate(messages, fs[i], ';', '|');
				}
			}
		} catch (SimpleException e1) {
			m_log.error( e1 );
		}
	}

	//----------------------- register ---------------------//

	/**
	* Register an object and the appropriate message code.
	*/
	public void registerTooltip(Object o, String key) {
		registerTooltip(o, key, new Object[0]);
	}

	/**
	* Register an object and the appropriate message code.
	* @see #translate(String key,Object[] fields)
	*/
	public void registerTooltip(Object o, String key, Object[] fields) {
		register(o, key, fields, true);
	}

	/**
	* Register an object and the appropriate message code.
	*/
	public void register(Object o, String key) {
		register(o, key, new Object[0]);
	}

	/**
	* Register an object and the appropriate message code.
	* @see #translate(String key,Object[] fields)
	*/
	public void register(Object o, String key, Object[] fields) {
		register(o, key, fields, false);
	}

	/** register an object for a message and a tool tip **/
	public void registerBoth(Object o, String key, String tooltip) {
		register(o, key, new Object[0], false);
		register(o, tooltip, new Object[0], true);
	}

	/** the real register **/
	private void register(
		Object o,
		String key,
		Object[] fields,
		boolean tooltip) {

		TMessage tm= new TMessage(key, fields);
		synchronized (registars) {
			if (tooltip) {
				registarsTT.put(o, tm);
				setToolTip(o, tm);
			} else {
				registars.put(o, tm);
				setText(o, tm);
			}
		}

	}

	/** get a list of the available languages and their name<BR>
	 * KEY: code <BR>
	 * VALUE: name
	 * */
	public Hashtable getAvailLang() {
		return this.availLang;
	}

	/**
	* get the translation of this message for the actual language.
	* Should be used for volatile dialogs and UI componenents.<BR>
	* this is an EXTEND version of translate.. to translate a string with arguments (fields)<BR>
	* <PRE>
	* EXAMPLE:
	* the string: "I've %0 out of %1 apples which represent %2%% of the apples."
	* is registred with the key: "apples"
	* String[] values = {"5" "20" "25"}
	* 
	* translate("apple",values); return:
	*    -> "I've 5 out of 20 apples which represent 25% of the apples."
	*
	* if the string: "On a total of %1 apples i've %0 which represent %2%% "
	* is registred with the key: "apples2"
	*
	* translate("apple2",values); return:
	*    ->  "On a total of 20 apples i've 5 which represent 25% o"
	*
	* Note: %% is replaced with %
	* </PRE>
	* @param fields it the list of object.. method toString() will be called on those objects
	*/
	public static String translate(String key, Object[] fields) {

		if (messages == null)
			return "<not init>" + key;

		if (!messages.containsKey(key)) {
			m_log.warn( "key unknown: " + key );
			messages.put(key, "#" + key);
			if (learning)
				learn(key);
		}

		String m= (String) messages.get(key);

		// backup %% in another dimension
		m= m.replaceAll("" + FIELDCHAR + FIELDCHAR, DUMMY);

		for (int i= 0; i < fields.length; i++) {
			m= m.replaceAll("" + FIELDCHAR + i, fields[i].toString());
		}
		m= m.replaceAll(DUMMY, "" + FIELDCHAR);
		return m;
	}

	/**
	* translate a TMessage
	*/
	private static String translate(TMessage m) {
		if (m.fields == null)
			return translate(m.key);
		return translate(m.key, m.fields);
	}

	/**
	* convenience method, that will create an array of size 0
	* and call translate(String key,Object[] fields)
	*/
	public static String translate(String key) {
		return translate(key, new Object[0]);
	}

	/**
	* convenience method, that will create an array of size 1
	* and call translate(String key,Object[] fields)
	*/
	public static String translate(String key, Object field0) {
		Object[] o= new Object[1];
		o[0]= field0;
		return translate(key, o);
	}
}

/** 
 * a TMessage object contains a key and a set of Objects to be translated 
 * It will be used to call translate(String key,Object[] fields)
 * **/
class TMessage {
	public String key;
	public Object[] fields;

	/**
	 * 
	 * @param key
	 * @param fields can be null
	 */
	public TMessage(String key, Object[] fields) {
		this.key= key;
		if (fields == null)
			fields= new Object[0];
		this.fields= fields;
	}
}

/** $Log: Lang.java,v $
/** Revision 1.2  2007/04/02 17:04:25  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:39  perki
/** First commit on sourceforge
/**
/** Revision 1.12  2004/09/03 13:25:34  kaspar
/** ! Log.out -> log4j part four
/**
/** Revision 1.11  2004/05/22 08:39:35  perki
/** Lot of cleaning
/**
/** Revision 1.10  2004/05/12 13:38:06  perki
/** Log is clever
/**
/** Revision 1.9  2004/03/06 14:24:50  perki
/** Tirelipapon sur le chiwawa
/**
/** Revision 1.8  2004/03/06 11:49:22  perki
/** *** empty log message ***
/**
/** Revision 1.7  2004/03/03 15:04:18  carlito
/** *** empty log message ***
/**
/** Revision 1.6  2004/02/04 15:42:16  perki
/** cleaning
/**
* Revision 1.5  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.4  2004/01/22 15:40:51  perki
* Bouarf
*
* Revision 1.3  2004/01/22 13:03:32  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/07 18:28:33  perki
* Translation way better
*
* Revision 1.1  2003/12/05 15:53:39  perki
* start
*
*
*
* Lang.java
*
* Created on 22 juin 2003, 16:00
*/