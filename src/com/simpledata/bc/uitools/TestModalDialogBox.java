/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 nov. 2004
 */
package com.simpledata.bc.uitools;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.event.TestNamedEvent;
import com.simpledata.sdl.log.Log4jInitializer;

import foxtrot.Task;
import foxtrot.Worker;

/**
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: TestModalDialogBox.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class TestModalDialogBox extends TestCase {
	static {
		Log4jInitializer.doInit(Resources.log4jpropsPath());
	}
	
	private final JFrame m_testFrame;
	private static final String[] buttons = {"Fail","Success"};
	private static final Logger m_log = Logger.getLogger(TestModalDialogBox.class);
	
	public TestModalDialogBox(String name) {
		super(name);
		
		m_testFrame = new JFrame("TestModalDialogBox");
		m_testFrame.setBounds(100,100,500,500);
		m_testFrame.show();
	}
	
	public void testLambdaThread() {
		int res = ModalDialogBox.custom(m_testFrame, "testLambaThread", buttons, null);
		m_log.info("testLambdaThread finished with result "+res);
		assertEquals(1,res);
	}
	
	public void testAWTThread() {
		try {
		SwingUtilities.invokeAndWait(new Runnable(){
			
			public void run() {
				int res = 0;
				res = ModalDialogBox.custom(m_testFrame, "testAWTThread", buttons, null);
				m_log.info("testAWTThread finished with result "+res);
				assertEquals(1,res);
			}});
		} catch (Exception e) {}
		
	}
	
	public void testFoxtrotThread() {
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run() {
					try {
					Worker.post(new Task(){
						
						public Object run() {
							int res = 0;
							res = ModalDialogBox.custom(m_testFrame, "testFoxtrotThread", buttons, null);
							m_log.info("testFoxtrotThread finished with result "+res);
							assertEquals(1,res);
							return null;
						}});
					} catch (Exception e) {}
				}});
				} catch (Exception e) {}
		
	}
	
	/**
	 * Run the tests from the command line.  
	 */ 
	public static void main(String[] args) {
		System.out.println ("Starting NamedEvent tests.");
		System.out.println ("log4j path: "+Resources.log4jpropsPath());
		
		junit.textui.TestRunner.run( TestNamedEvent.class );
	}
}
