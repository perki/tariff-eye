/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: BoundedInputStream.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.filetools;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/** 
 * an input stream that prevent reading too far <BR>
 * used to Surround the header data in the current Stream
 * **/
public class BoundedInputStream extends InputStream {
    private static final 
    	Logger m_log=Logger.getLogger(BoundedInputStream.class);
    
    InputStream is;
    int length;
    boolean forwardClose;
    
    /**
     * 
     * @param is the inputstream to apply bounds on
     * @param length the length of this bounding
     * @param forwardClose set to true if you allow stream to close superStream
     */
    public BoundedInputStream(InputStream is,int length,boolean forwardClose) {
        this.is = is;
        this.length = length;
        this.forwardClose = forwardClose;
        //out(0,"int");
    }
    
    
	public int read() throws IOException {
	    if (length <= 0) {
	        return -1;
	    }
	    
	    int val = is.read();
	    count(1,val < 0 ? -1 : 1);
		return val;
	}

	public int read(byte b[]) throws IOException {
	    if (length <= 0) { 
	        return -1; 
	    }
	    if (length <= b.length) {
	        return read(b,0,length);
	    }
		return count(2,is.read(b));
	}

	public int read(byte b[], int off, int len) throws IOException {
	    if (length <= 0) {  return -1;  }
	    count(3,off);
	    if (length < len) {len = length;
	    }
		return count(3,is.read(b, off, len));
	}

	public long skip(long n) throws IOException {
	    out(4,"skipped request:"+n+" did:"+((length <= n) ? length : n));
	    if (length <= n) n = length;
		return count(4,is.skip(n));
	}

	public int available() throws IOException {
	    int av = is.available();
	    int realAv = length <= av ? length : av;
		return realAv;
	}

	public void close() throws IOException {
		count(6,-1);
		if (forwardClose) {
		    is.close();
		} 
	}

	public synchronized void mark(int readlimit) {
	    m_log.warn("mark called");
		is.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
	    m_log.warn("reset called");
		is.reset();
	}

	public boolean markSupported() {
	    m_log.warn("markSupported called");
		return is.markSupported();
	}
	
	/** counter **/
	private long count(int who,long val) {
	    //out(-1,who+"");
	    length -= val;
	    if (val < 0) length = 0;
	    return val;
	}
	
	private int count(int who, int val) {
	    return (int) count(who,(long) val);
	}
    
	private void out(int who,String s) {
	    m_log.warn("out l:"+length+" who:"+who+" "+s);
	}
}
