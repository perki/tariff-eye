/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 *
* $Id: TextFileUtils.java,v 1.2 2007/04/02 17:04:25 perki Exp $
*/


package com.simpledata.filetools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
/**
* Utilities to retreive, or save TextFiles
*/
public class TextFileUtils {
	
	private static final Logger m_log = Logger.getLogger( TextFileUtils.class );

	/** 
	 * get the content of a stream as a string..
	 * !! the stream is closed after reading
	 */
	public static String getString(InputStream is) throws IOException {
		StringBuffer result= new StringBuffer();
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader in= new BufferedReader(isr);
			
			String s;
			boolean first = true;
			do {
				s= in.readLine();
				
				if (s != null) {
					if (! first) {
						result.append("\n");
					} else
						first = false; 
					result.append(s);
				}
				
			} while (s != null);
		} finally {
			is.close();
		}
		return result.toString();
	}
	
	/**
	* return the whole content of a TextFile into a String
	*/
	public static String getString(File file) throws SimpleException {
		String result = null;
		try {
			InputStream is = new FileInputStream(file);
			result = getString(is);
		} catch (IOException e) {
			throw new SimpleException(SimpleException.IOException,e);
		}
		return result;
	}
			

	/**
	* Return an Hashtable from a file.<BR>
	* File format key[separator]value<BR>
	* Empty lines and lines staring with [separator] will  be omitted.<BR>
	* Only this first occurence of [separator] will be treated<BR>
	* for example line : "toto;zoulou;carrot" will produce toto -> "zoulou;carrot"
	*
	*@param file the File to read
	*@param separator the char that separate the key from the value
	*@param newlines when found this char will be replaced by \n
	*/
	public static Hashtable getHashtable(
		File file,
		char separator,
		char newlines)
		throws SimpleException {
		Hashtable result= new Hashtable();
		getHashtableUpdate(result, file, separator, newlines);
		return result;
	}

	/**
	 * Same as getHashtable but update the passed hashtable instead of creating a new one
	 */
	public static void getHashtableUpdate(
		Hashtable result,
		File file,
		char separator,
		char newlines)
		throws SimpleException {
		String key;
		String msg;
		int pos;
		int lineCounter= 0;
		BufferedReader in= null;
		try {
			in= new BufferedReader(new FileReader(file));

			while (true) {
				try {
					String s= in.readLine();
					lineCounter++;
					if (s == null) {
						break;
					}
					pos= s.indexOf(separator);
					if (pos > 1) {
						key= s.substring(0, pos);
						msg= s.substring(pos + 1);
						msg= msg.replace(newlines, '\n');
						if (msg.length() > 0) {
							if (result.containsKey(key)) {
								m_log.warn(
									"duplicate key ["
										+ key
										+ "] in file ["
										+ file
										+ "] line:"
										+ lineCounter
								);
							}
							result.put(key, msg);
						}
					
					}
				} catch (IOException e) {
					throw (new SimpleException(SimpleException.IOException, e));
				}
			}
			in.close();
		} catch (IOException e1) {
			throw (new SimpleException(SimpleException.IOException, e1));
		}
	}

	/**
	 * Read a file with the following structure:<BR>
	 * # as title char ; as separator  | as new lines
	* <PRE>
	* #Title1
	* key1;value1
	* key2;value2
	* 
	* #Title2
	* key3;value3
	* </PRE><BR>
	* Will return the following structure
	* <PRE>
	* HashMap:{ 
	* 	key:String:Title1 
	* 	value:HashMap:{
	* 			key:String:key1 
	* 			value:String value1
	* 		
	* 			key:String:key2 
	* 			value:String value2
	* 	}
	* 	
	* 	key:String::Title2 
	* 	value:HashMap:{
	* 			key:String:key1 
	* 			value:String value1
	* 	}
	* }
	*</PRE>
	*If no title is found "" will be used. 
	*@param file the File to read
	*@param titlechar the first char of title line
	*@param separator the char that separate the key from the value
	*@param newlines when found this char will be replaced by \n
	*/
	public static HashMap getHashMapHashMap(
		File file,
		char titlechar,
		char separator,
		char newlines)
		throws SimpleException {

		String title= "";
		HashMap result= new HashMap();
		HashMap keyValues= new HashMap();
		String key;
		String msg;
		int pos;

		BufferedReader in= null;
		try {
			in= new BufferedReader(new FileReader(file));

			while (true) {
				try {
					String s= in.readLine();
					if (s == null) {
						break;
					}
					// look for titles
					pos= s.indexOf(titlechar);
					if (pos == 0) {
						if (title.equals("") && keyValues.size() == 0) {
							// do nothing
						} else {
							//store previous data
							result.put(title, keyValues);
						}
						title= s.substring(1);
						keyValues= new HashMap();
					} else {
						// look for keys
						pos= s.indexOf(separator);
						if (pos > 1) {
							key= s.substring(0, pos);
							msg= s.substring(pos + 1);
							msg= msg.replace(newlines, '\n');
							keyValues.put(key, msg);
						}
					}
				} catch (IOException e) {
					throw (new SimpleException(SimpleException.IOException, e));
				}
			}
			in.close();
		} catch (IOException e1) {
			throw (new SimpleException(SimpleException.IOException, e1));
		}
		// flush remaing data
		if (title.equals("") && keyValues.size() == 0) {
			// do nothing
		} else {
			//store previous data
			result.put(title, keyValues);
		}
		
		return result;
	}

	/**
	 * reverse of getHashMapHashMap
	 *
	 */
	public static void setHashMapHashMap(
		HashMap data,
		File file,
		char titlechar,
		char separator,
		char newlines)
		throws SimpleException {
		BufferedWriter bos= null;
		try {
			bos= new BufferedWriter(new FileWriter(file));

			// get and order titles
			Vector v= new Vector(data.keySet());
			Collections.sort(v);

			// walk titles
			String title= "";
			HashMap keyValues= null;
			String key= "";
			String value= "";
			for (Enumeration e= v.elements(); e.hasMoreElements();) {
				try {
					title= (String) e.nextElement();
					keyValues= (HashMap) data.get(title);
					//write title
					bos.write("\n" + titlechar + title + "\n");

				} catch (ClassCastException e2) {
					throw (
						new SimpleException(
							SimpleException.WRONGDATASOURCE,
							e2));
				}

				//get and order keys
				Vector v2= new Vector(keyValues.keySet());
				Collections.sort(v2);
				// write data
				for (Enumeration e3= v2.elements(); e3.hasMoreElements();) {
					key= e3.nextElement().toString();
					value= keyValues.get(key).toString();
					value= value.replace('\n', newlines);
					bos.write(key + separator + value + "\n");
				}

			}

			bos.close();
		} catch (IOException e1) {
			throw (new SimpleException(SimpleException.IOException, e1));
		}

	}

}
/*
* $Log: TextFileUtils.java,v $
* Revision 1.2  2007/04/02 17:04:25  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:38  perki
* First commit on sourceforge
*
* Revision 1.8  2004/11/27 10:48:47  perki
* *** empty log message ***
*
* Revision 1.7  2004/11/26 10:05:05  jvaucher
* Begining of TariffEyeInfo feature
*
* Revision 1.6  2004/11/20 11:09:51  perki
* *** empty log message ***
*
* Revision 1.5  2004/10/18 15:07:46  perki
* *** empty log message ***
*
* Revision 1.4  2004/09/04 18:10:15  kaspar
* ! Log.out -> log4j, last part
*
* Revision 1.3  2004/03/06 11:48:23  perki
* Tirelipapon sur le chiwawa
*
* Revision 1.2  2003/10/30 07:38:11  perki
* Text files modified
*
* Revision 1.1  2003/10/10 16:07:56  perki
* glop
*
*/