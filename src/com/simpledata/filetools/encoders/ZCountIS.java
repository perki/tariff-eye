/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.filetools.encoders; 

import java.io.IOException;
import java.io.InputStream;

import com.simpledata.filetools.Secu;

/**
 * Counts the bytes that are read from a given input stream 
 * and informs a monitor about it. 
 *
 * @version $Id: ZCountIS.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 * @author Simpledata SARL, 2004, all rights reserved. 
 */
public class ZCountIS extends InputStream {
	InputStream is;
	Counted c;

	public ZCountIS(
		Secu.Monitor monitor,
		String title,
		InputStream is,
		long zeroAt,
		long finalAt) {
		c= new Counted(monitor, title, zeroAt, finalAt);
		this.is= is;
	}

	public int read() throws IOException {
		// forward the readed data to Counted that will tells the monitors about
		return c.count(is.read());
	}

	public int read(byte b[]) throws IOException {
		c.countPlus(b.length);
		return is.read(b);
	}

	public int read(byte b[], int off, int len) throws IOException {
		c.countPlus(len);
		return is.read(b, off, len);
	}

	public long skip(long n) throws IOException {
		c.countPlus(n);
		return is.skip(n);
	}

	public int available() throws IOException {
		return is.available();
	}

	public void close() throws IOException {
		c.count(-1);
		is.close();
	}

	public synchronized void mark(int readlimit) {
		is.mark(readlimit);
	}

	public synchronized void reset() throws IOException {
		is.reset();
	}

	public boolean markSupported() {
		return is.markSupported();
	}
}

