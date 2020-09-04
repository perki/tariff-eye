/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.filetools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.simpledata.filetools.encoders.SelfD;
import com.simpledata.filetools.encoders.SelfDC_DES;
import com.simpledata.filetools.encoders.SelfDC_GZIP;
import com.simpledata.filetools.encoders.SelfDT_Serializable;
import com.simpledata.filetools.encoders.SelfDT_SerializableXMLArmored;
import com.simpledata.filetools.encoders.SelfDT_XMLEncoder;
import com.simpledata.filetools.encoders.SelfDTerminal;
import com.simpledata.filetools.encoders.ZCountIS;
import com.simpledata.filetools.encoders.ZCountOS;
import com.simpledata.filetools.encoders.SelfD.DecodeFlow;


/**
* Contains all security stuff 
* <BR>has the hability to load /save object
*
*/
public class Secu {
	private static final Logger m_log = Logger.getLogger( Secu.class );
	
	/** All files are encoded eith a stamp of 27 bytes. 
	 * This is it! never change this value **/
	public final static int STAMP_SIZE= 27;

	/** Saving Method Modifier - uses objects (ObjectOutputStream)**/
	public final static int METHOD_OBJECT_DES= 0;

	/** Saving Method Modifier - uses objects (XMLEncoder)**/
	public final static int METHOD_XML_DES= 1;

	/** Saving Method Modifier - 
	 * uses objects (ObjectOutputStream) and compress**/
	public final static int METHOD_OBJECT_COMPRESS_DES= 2;

	/** Saving Method Modifier - uses objects (XMLEncoder) and compress**/
	public final static int METHOD_XML_COMPRESS_DES = 3; 
	
	/** Saving Method Modifier - uses objects (XMLEncoder) and compress**/
	public final static int METHOD_XML_NON_COMPRESS_NON_ENCRYPTED = 4;
		
	
	
	public final static int METHOD_SELF_DESCRIBING_STRUCTURE = 6;

	/** Contains all the methods types .. should be updated for each addons **/
	protected static byte[][] METHODS = { // METHOD_OBJECT
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 1.0"), // METHOD_OBJECT_DES
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 2.0"), // METHOD_XML_DES
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 1.1"), // METHOD_OBJECT_COMPRESS_DES
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 2.1"), // METHOD_XML_COMPRESS_DES
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 2.2"), // METHOD_XML_NON_COMPRESS_NON_ENCRYPTED
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 3.0"), // METHOD_XML_COMPRESS_RSA
		Secu.get27bytesOf("SIMPLE DATA HEADER SE V 4.0")  // SELF DESCRIBING STRUCTURE
	}; 
	
	/** monitor titles **/
	public final static String MONITOR_FILE_WRITING= "WRITING TO FILE";
	public final static String MONITOR_CREATE_HEADER= "HEADER";
	public final static String MONITOR_SERIALIZE_DATA= "DATA";
	


	

	/** preferred METHOD for saving daencta **/
	private static int METHOD_PREFERRED= METHOD_XML_COMPRESS_DES;

	/** set the preferred method for saving DATA 
	*
	* One of METHOD_*
	**/
	public static void setPreferredMethod(int method) {
		METHOD_PREFERRED= checkMethod(method);
	}

	/**
	 * check if this method is valid
	 */
	public static int checkMethod(int method) {
		if ((method < 0) || (method >= METHODS.length))
			method= getPreferredMethod();
		return method;
	}

	/** get the prefered method for saving DATA 
	*
	* One of METHOD_*
	**/
	public static int getPreferredMethod() {
		return METHOD_PREFERRED;
	}
	
	
	
	/** 
	 * Return 27 bytes of this String; Pads String with 'A'
	 * if it is shorter than 27 bytes. 
	 */
	public static byte[] get27bytesOf(String s) {
		byte[] res= new byte[STAMP_SIZE];
		byte[] temp= s.getBytes();
		//fill res with A chars
		for (int i= 0; i < STAMP_SIZE; i++) {
			res[i]= 65;
		}
		//fill res with The string chars
		for (int i= 0;((i < STAMP_SIZE) && (i < temp.length)); i++) {
			res[i]= temp[i];
		}
		return res;
	}

	/**
	* Save the data
	* @param head Unencrypted Object to save 
	* @param data Object to save (Encrypted)
	* @param f file
	* @param key (8 chars)
	*/
	public static synchronized void save (
		Object head,
		Object data,
		File f,
		String key)
		throws SimpleException {
		save(head, data, f, key, -1, null);
	}
	
	/**
	* Save the data<BR>
	* USE OF THIS METHOD IS NOT BACKWARD COMPATIBLE WITH V1 -> V3 :
	* save_old() does this
	* 
	* @param head Unencrypted Object to save 
	* @param data Object to save (Encrypted)
	* @param f file
	* @param key (8 chars)
	* @param method the method to use (if -1) METHOD_PREFERED will be used
	* @param monitor a monitor (can be null)
	*/
	public static synchronized void save(
	        Object head,
	        Object data,
	        File f,
	        String key,
	        int method,
	        Secu.Monitor monitor)
	throws SimpleException {
	    
	    method = checkMethod(method);
	    
	    // NOW WE ALWAYS SAVE THE HEADER IN XML!!!
	    
	    // set final encoding
	    SelfDTerminal tHead = new SelfDT_XMLEncoder(head);
	    SelfDTerminal tData = null;
	    switch (method) {
	    case METHOD_OBJECT_DES:
	    case METHOD_OBJECT_COMPRESS_DES:
	        tData = new SelfDT_Serializable((Serializable) data);
	        break;
	    case METHOD_XML_DES:
	    case METHOD_XML_COMPRESS_DES :
	    case METHOD_XML_NON_COMPRESS_NON_ENCRYPTED :
	        tData = new SelfDT_SerializableXMLArmored((Serializable)data);
	        break;
	    default :
	        throw new SimpleException(0,"UNKOWN METHOD "+method);
	    }
	    SecuSelf ss = new SecuSelf(tHead,tData);
	    
	    //set compression
	    switch (method) {
	    case METHOD_XML_COMPRESS_DES :
	    case METHOD_OBJECT_COMPRESS_DES:
	        ss.insertDataEncoder(new SelfDC_GZIP());
	    }
	    
	    // set encoding
	    switch (method) {
	    case METHOD_OBJECT_DES:
	    case METHOD_OBJECT_COMPRESS_DES:
	    case METHOD_XML_DES:
	    case METHOD_XML_COMPRESS_DES :
	    	if (key != null) {
	    		ss.insertDataEncoder(new SelfDC_DES(key));
	    	} else {
	    		m_log.warn( "DES Key is null, skiping DES encoding" );
	    	}
	        break;
	    }
	    
	    
	    if (monitor == null) monitor = new DummyMonitor();
	    method= checkMethod(method);
	    monitor.setMonitors(new String[] {MONITOR_FILE_WRITING});
	    
	    //	  open the stream
	    OutputStream out;
        try {
            out = new ZCountOS(
	                monitor,
	                MONITOR_FILE_WRITING,
	                new FileOutputStream(f),
	                0,
	                0);
            ss.commit(out);
            monitor.done();
        } catch (FileNotFoundException e) {
            monitor.error(0,"Cannot open file:"+f,e);
           throw new SimpleException(SimpleException.IOException,e);
        } catch (IOException e) {
            monitor.error(0,"IOException:",e);
            throw new SimpleException(SimpleException.IOException,e);
        } catch (SimpleException e) {
		    monitor.error(0,
		            "Error while reading",e);
		    throw e;
		}catch (Exception e) {
		    monitor.error(Monitor.ERROR_IO,
		            "Error while reading",e);
		    throw new SimpleException(0, e);
		} catch (Error e) {
		    monitor.error(Monitor.ERROR_IO,
		            "Error while reading",e);
		    throw new SimpleException(0, e);
		}
       
			
	}
	
	
	/**
	 * Compares the method stamp with all known methods and returns
	 * the corresponding <code>METHOD_</code> constant. 
	 * 
	 * @return -1 if failed
	 */
	public static int getMethod(byte[] stamp) {
		if (stamp.length != STAMP_SIZE)
			return -1;

		// for each data signature type
		for (int k= 0; k < METHODS.length; k++) {
			// check if equals
			boolean ok= true;
			for (int i= 0; i < STAMP_SIZE; i++) {
				if (METHODS[k][i] != stamp[i]) {
					ok= false;
				}
			}
			// ok found return the position in METHODS[]
			if (ok) {
				//m_log.debug( "FOUND METHOD:" + k  );
				return k;
			}
		}
		m_log.error( "Cannot find method with stamp:" + new String(stamp) );
		return -1;
	}

	/**
	* return the unencrypted head object of a file
	* @param f file 
	*/
	public static synchronized Object getHeader(File f)
	throws SimpleException {
	    return getHeader(f,null);
	}
	
	/**
	* return the unencrypted head object of a file
	* @param f file 
	*/
	public static synchronized Object getHeader(File f,SelfD.DecodeFlow df)
	throws SimpleException {
	    InputStream st1= null;
	    
	    try {
	        // open stream
	        st1= new FileInputStream(f);
	    } catch (FileNotFoundException e) {
	        throw new SimpleException(102, e);
	    }
	    return getHeader(st1,f.length(),df);
	}
	
	/**
	* return the unencrypted head object of a stream
	* @param is the input stream
	* @param length the length of the header
	*/
	public static synchronized Object getHeader(InputStream is,double length)
		throws SimpleException {
	    return getHeader(is,length,null);
	}
	
	
	/**
	* return the unencrypted head object of a stream
	* @param is the input stream
	* @param length the length of the stream, -1 if unkown 
	* @param df the Decoder Flow if any
	*/
	private static synchronized Object 
	getHeader(InputStream is,double length,SelfD.DecodeFlow df)
		throws SimpleException {
		DataInputStream din = null;
		try {
		    din= new DataInputStream(is);
	
			int method= -1;
			// check the signature (stamp)
			if (length < 0 || length >= STAMP_SIZE) {
				byte[] temp2= new byte[STAMP_SIZE];
				din.readFully(temp2);
	
				method= getMethod(temp2);
			} else {
				throw new SimpleException(102, "Invalid Data File B2");
			}
			
			if (df == null) {
				
				df=
				    getDefaultDF("DUMMYKEY",new ActionListener(){
	                public void actionPerformed(ActionEvent e) {
	                    m_log.debug("I do not care about"+e);
	                }});
			    
			}
			
			// NEW METHOD
			if (method == METHOD_SELF_DESCRIBING_STRUCTURE) {
			   Object o =  SecuSelf.getHeader(is,df);
			   is.close();
			   return o;
			}
			    
			
			
			// ------------------------OLD METHODS
			
			// get head size
			int hSize= din.readInt();

			// skip the head
			
			// conversion of methods
			
			byte[] data_encoders = null;
			switch (method) {
			case METHOD_OBJECT_DES:
			    data_encoders = new byte[] {
			        SecuSelf.T_SERIALIZABLE};
			    break;
			case METHOD_OBJECT_COMPRESS_DES:
			    data_encoders = new byte[] {
			        SecuSelf.C_GZIP,
			        SecuSelf.T_SERIALIZABLE};
			    break;
			case METHOD_XML_DES:
			case Secu.METHOD_XML_NON_COMPRESS_NON_ENCRYPTED :
			    data_encoders = new byte[] {
			        SecuSelf.T_XMLENCODER};
			    break;
			case Secu.METHOD_XML_COMPRESS_DES:
			    data_encoders = new byte[] {
			        SecuSelf.C_GZIP,
			        SecuSelf.T_XMLENCODER};
			break;
			default :
			    m_log.error("Unkown method :"+method);
			}
			
			is.close();
   
			return SecuSelf.readPart(data_encoders,
			        new BoundedInputStream(is,hSize,false),df);
			
			
	    } catch (IOException e) {
	    	m_log.warn("IOException. Closing the stream...");
	    	try {
	    		is.close();
	    		m_log.warn("...success.");
	    	} catch (IOException e2) {
	    		m_log.warn("...fails.",e2);
	    	}
			throw new SimpleException(100, e);
		}
		
	}

	/**
	* return the encrypted data object in this file
	* @param key (8 chars)
	* @param f file containing the data
	*/
	public static synchronized Object getData(File f, String key)
		throws SimpleException {
		return getData(f, key, null);
	}

	/**
	* return the encrypted data object in this file
	* @param key (8 chars)
	* @param f file containing the data
	* @param monitor if you want to monitor the progress of file loading
	*/
	public static synchronized Object getData(
		File f,
		String key,
		Secu.Monitor monitor)
		throws SimpleException {
	    InputStream st1= null;
	    try {
	        // open stream
	        st1= new FileInputStream(f);
	    } catch (FileNotFoundException e) {
	        throw new SimpleException(102, e);
	    }
	    return getData(st1,f.length(),key,monitor);
	}
	    
    /**
	* return the encrypted data object in this stream
	* @param key (8 chars)
	* @param is stream containing the data
	* @param monitor if you want to monitor the progress of file loading
	* @param length -1 if unkown
	*/
	public static synchronized Object getData(
		InputStream is,
		long length,
		final String key,
		Secu.Monitor monitor)
		throws SimpleException {
	    
	    
	    
	    
	    if (monitor == null) 
	        monitor = new DummyMonitor();
	    
	    is = new ZCountIS(monitor,"Loading File",is,0,length);
	    monitor.setMonitors(new String[] {"Loading File"});
	    try {
		    DataInputStream din= new DataInputStream(is);
	
			int method= -1;
			// check the signature (stamp)
			if (length < 0 || length >= STAMP_SIZE) {
				byte[] temp2= new byte[STAMP_SIZE];
				din.readFully(temp2);
	
				method= getMethod(temp2);
			} else {
			    monitor.error(Monitor.ERROR_INVALID_FILE,
			            "File format is not recognized",null);
				throw new SimpleException(102, "Invalid Data File Z");
			}
			
			
			SelfD.DecodeFlow df = getDefaultDF(key,new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    m_log.debug("I do not care about"+e);
                }});
			
			
			// NEW METHOD
			if (method == METHOD_SELF_DESCRIBING_STRUCTURE){
			    Object o =  SecuSelf.getData(is,df);
			    is.close();
			    monitor.done();
			    return o;
			}

			// conversion of previous methods

			// get head size
			int hSize= din.readInt();

			// skip the head
			din.skipBytes(hSize);
			
		
			byte[] data_encoders = null;
			switch (method) {
			case METHOD_OBJECT_DES:
			    data_encoders = new byte[] {
			        SecuSelf.C_DES,
			        SecuSelf.T_SERIALIZABLE};
			    break;
			case METHOD_OBJECT_COMPRESS_DES:
			    data_encoders = new byte[] {
			        SecuSelf.C_DES,
			        SecuSelf.C_GZIP,
			        SecuSelf.T_SERIALIZABLE};
			    break;
			
			case METHOD_XML_DES:
			    data_encoders = new byte[] {
			        SecuSelf.C_DES,
			        SecuSelf.T_XMLENCODER};
			    break;
			case METHOD_XML_COMPRESS_DES :
			    data_encoders = new byte[] {
			        SecuSelf.C_DES,
			        SecuSelf.C_GZIP,
			        SecuSelf.T_XMLENCODER};
			    break;
			case METHOD_XML_NON_COMPRESS_NON_ENCRYPTED :
			    data_encoders = new byte[] {
			        SecuSelf.C_IntConsumer,
			        SecuSelf.T_XMLENCODER};
			    
			    break;
			default :
			    m_log.error("Unkown method :"+method,new Exception());
			}
			
			Object data = SecuSelf.readPart(
			        data_encoders,is,df);
			monitor.done();
			is.close();
			return data;
			
			
	    } catch (IOException e) {
	    	m_log.error("IOException",e);
			monitor.error(Monitor.ERROR_IO,
		            "IO Error while reading",e);
			throw new SimpleException(100, e);
		} catch (SimpleException e) {
			m_log.error("SimpleException",e);
			monitor.error(Monitor.ERROR_IO,
		            "IO Error while reading",e);
			throw e;
		} catch (Exception e) {
			m_log.error("Exception",e);
			monitor.error(Monitor.ERROR_IO,
		            "Error while reading",e);
		    throw new SimpleException(0, e);
		} catch (Error e) {
				m_log.error("Error",e);
				monitor.error(Monitor.ERROR_IO,
			            "Error while reading",e);
			    throw new SimpleException(0, e); 
		} finally {
			m_log.debug("Closing stream...");
			try {
				is.close();
				m_log.debug("...success");
			} catch (IOException e1) {
				m_log.debug("...fails",e1);
				m_log.error("Unable to close the stream",e1);
			}
		}

	}

	
	/**
	 * processes how want to monitor file loading should use this Interface
	 */
	public interface Monitor {
	    /** General Error Unkown Reason **/
	    public static final int ERROR_UNDEF = 0;
	    /** IO Exeception **/
	    public static final int ERROR_IO = 1;
	    /** READ : File is not valid **/
	    public static final int ERROR_INVALID_FILE = 2;
	    /** READ : Cannot read crypted file **/
	    public static final int ERROR_CRYPT = 3;
	    
	    /** 
	     * if an error occures, this method is called with the reason
	     * message as parameter, and an Error code which is one one of
	     * ERROR_XXXX<BR>
	     * When an error occure the loading must be finished
	     * @param code one of Monitor.ERROR_XXXX
	     * @param textual informations about the error
	     */
	    public void error(int code,String message,Throwable e);
	    
		/** at start you will get a list of the monitors **/
		public void setMonitors(String[] monitors);

		/** event on monitor 
		 * @param value -1 if the position is unkown otherwise, between 0 and 100
		 * @param pos is the position of the counter (amount of data traversed)
		 **/
		public void valueChange(String monitor, long value, long pos);

		/**
		 * called when finished
		 */
		public void done();
	}

	/**
	 * TODO Change this
	 * This is a fast HACK to make Secu use V4 system<BR>
	 * Create a DF that should suit most imediat problems<BR>
	 * @param  al is called when error occures
	 */
	public static SelfD.DecodeFlow 
		getDefaultDF(String desKEY,final ActionListener al) {
	    /** TODO make this better **/
	    class DF extends HashMap implements SelfD.DecodeFlow {
	        
	        public DF() {
	            super();
	            put(SelfDC_DES.PARAM_KEY,"SooSIMPL");
	            
	        }
	        
	        /**
	         * @see DecodeFlow#cannotDecode(int, byte, java.lang.String)
	         */
	        public void cannotDecode(int at, byte id, String messsage) {
	            al.actionPerformed(
	                    new ActionEvent("DUMMY",0,
	                            "Problem at:"+at+" SID:"+id+" ["+messsage+"]"));
	            m_log.warn("Problem at:"+at+" SID:"+id+" ["+messsage+"]");
	        }
	        
	        public Object getParams(Object k) {
	            return get(k);
	        }

            
            public void 
            	decodeWarning(int at,byte id,String messsage,Exception e) 
            {
	            m_log.warn("WARNING at:"+at+" SID:"+id+" ["+messsage+"]",e);
            }

            public void 
            	decodeFatal(int at, byte id, String messsage, Exception e) 
            	throws SimpleException 
            {
                cannotDecode(at,id,"FATAL :"+e);
            }
	    };
	    return new DF();
	}
}

/** a Dummy Monitor to prevent having null informations **/
class DummyMonitor implements Secu.Monitor {
    public void error(int code, String message,Throwable e) {}
    public void setMonitors(String[] monitors) {}
    public void valueChange(String monitor, long value, long pos) {}
    public void done() {} }



