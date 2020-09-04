/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 11 nov. 2004
 */
package com.simpledata.bc.datamodel.event;

import junit.framework.TestCase;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.datamodel.BCTree;
import com.simpledata.bc.datamodel.Named;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.sdl.log.Log4jInitializer;

/**
 * JUnit Test for the NamedEvent Framework.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: TestNamedEvent.java,v 1.2 2007/04/02 17:04:23 perki Exp $
 */
public class TestNamedEvent extends TestCase {
	
	static {
		Log4jInitializer.doInit(Resources.log4jpropsPath());
	}
	
	boolean m_success[] = {false, false};
	
	int m_int = 0;
	
	public TestNamedEvent(String name) {
		super(name);
	}
	
	/**
	 * Tests the CommonNamedEventFilter filter. Verify that it matches what
	 * he has to match.
	 */
	public void testCommonFilter() {
		// TODO Verify the sense of an event with code ALL_EVENTS
		Named named = new Named();
		Tarification tarification = new Tarification();
		
		CommonNamedEventFilter filter1 = 
			new CommonNamedEventFilter(NamedEvent.COM_VALUE_CHANGED_TARIFICATION, Tarification.class);
		CommonNamedEventFilter filter2 =
			new CommonNamedEventFilter(NamedEvent.ALL_EVENTS, Tarification.class);
		CommonNamedEventFilter filter3 =
			new CommonNamedEventFilter(NamedEvent.COM_VALUE_CHANGED_TARIFICATION, null);
		CommonNamedEventFilter filter4 =
			new CommonNamedEventFilter(NamedEvent.ALL_EVENTS, null);
		
		// filter 1 tests
		assertTrue(filter1.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertFalse(filter1.match(new NamedEvent(tarification,NamedEvent.OPTION_DATA_CHANGED)));
		assertFalse(filter1.match(new NamedEvent(named,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertFalse(filter1.match(new NamedEvent(tarification,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		
		// filter 2 tests
		assertTrue(filter2.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(filter2.match(new NamedEvent(tarification,NamedEvent.OPTION_DATA_CHANGED)));
		assertFalse(filter2.match(new NamedEvent(named,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(filter2.match(new NamedEvent(tarification,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		
		// filter 3 tests
		assertTrue(filter3.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertFalse(filter3.match(new NamedEvent(tarification,NamedEvent.OPTION_DATA_CHANGED)));
		assertTrue(filter3.match(new NamedEvent(named,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertFalse(filter3.match(new NamedEvent(tarification,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		
		// filter 4 tests
		assertTrue(filter4.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(filter4.match(new NamedEvent(tarification,NamedEvent.OPTION_DATA_CHANGED)));
		assertTrue(filter4.match(new NamedEvent(named,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(filter4.match(new NamedEvent(tarification,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
	}
	
	/**
	 * Tests the behviour of the DynamicEventFilter
	 */
	public void testDynamicEventFilter() {
		Named named = new Named();
		Tarification tarification = new Tarification();
		WorkSheet ws = new DispatcherIf();
		
		int[] matchedEc = {NamedEvent.COM_VALUE_CHANGED_TARIFICATION,
				NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED};
		Class[] matchedCl = {Tarification.class, Named.class};
		
		DynamicEventFilter def = new DynamicEventFilter(matchedEc, matchedCl);
		assertTrue(def.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(def.match(new NamedEvent(named,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.DESCRIPTION_MODIFIED)));
		assertFalse(def.match(new NamedEvent(ws,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		
		def.removeEventCode(NamedEvent.COM_VALUE_CHANGED_TARIFICATION);
		assertFalse(def.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(def.match(new NamedEvent(named,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.DESCRIPTION_MODIFIED)));
		assertFalse(def.match(new NamedEvent(ws,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		
		def.addEventCode(NamedEvent.COM_VALUE_CHANGED_TARIFICATION);
		assertTrue(def.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertTrue(def.match(new NamedEvent(named,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.DESCRIPTION_MODIFIED)));
		assertFalse(def.match(new NamedEvent(ws,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		
		def.removeSource(Named.class);
		assertTrue(def.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.DESCRIPTION_MODIFIED)));
		assertFalse(def.match(new NamedEvent(ws,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		
		def.addSource(DispatcherIf.class);
		assertTrue(def.match(new NamedEvent(tarification,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED)));
		assertFalse(def.match(new NamedEvent(named,NamedEvent.DESCRIPTION_MODIFIED)));
		assertTrue(def.match(new NamedEvent(ws,NamedEvent.COM_VALUE_CHANGED_TARIFICATION)));
		
	}
	
	/**
	 * Tests the NamedEvent fire / eventOccurs framework.
	 */
	public void testEventQueue() {
		Tarification tarification = new Tarification();
		m_success[0] = false;
		tarification.addNamedEventListener(new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				m_success[0] = true;
			}});
		
		tarification.fireNamedEvent(NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED);
		validateSuccess(0);
	}
	
	/**
	 * Tests the container passing event feature.
	 */
	public void testEventPassing() {
		Tarification difParent = new Tarification();
		Tarification difChild = new Tarification();
		difChild.setContainer(difParent);
		assertTrue (difChild.getContainer() == difParent);
		m_success[0] = false;
		difParent.addNamedEventListener(new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				m_success[0] = true;
			}});
		
		difChild.fireNamedEvent(NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED);
		validateSuccess(0);
	}
	
	/**
	 * Tests the add / remove purpose for listeners
	 */
	public void testDynamicListeners() {
		Tarification difParent = new Tarification();
		Tarification difChild = new Tarification();
		difChild.setContainer(difParent);
		assertTrue (difChild.getContainer() == difParent);
		m_success[0] = false;
		m_success[1] = false;
		difParent.addNamedEventListener(new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				m_success[0] = true;
			}});
		NamedEventListener nel = new NamedEventListener() {
			public void eventOccured(NamedEvent e) {
				assertFalse(m_success[1]); // work once
				m_success[1] = true;
			};
		};
		difChild.addNamedEventListener(nel);
			
		difChild.fireNamedEvent(NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED);
		validateSuccess(0);
		validateSuccess(1);
		m_success[0] = false;
		difChild.removeNamedEventListener(nel);
		difChild.fireNamedEvent(NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED);
		validateSuccess(0);
	}
	
	/**
	 * Test the atomicity for self destroying listener.
	 */
	public void testSelfModifingListener() {
		Tarification t = new Tarification();
		m_success[0] = false;
		t.addNamedEventListener(new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				assertFalse(m_success[0]); // work once
				m_success[0] = true;
				Named source = e.getSource();
				source.removeNamedEventListener(this);
			}});
		
		t.fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
		validateSuccess(0);
		t.fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
	}
	
	/**
	 * Test the sending of a lot of events.
	 */
	public void testFlooding() {
		Tarification t = new Tarification();
		m_success[0] = false;
		t.addNamedEventListener(new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				m_success[0] = true;
				Named source = e.getSource();
				source.removeNamedEventListener(this);
			}},NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
		
		t.addNamedEventListener(new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				m_int++;
				if (m_int >= 600) {
					Named source = e.getSource();
					source.removeNamedEventListener(this);
				}
			}}, 
				NamedEvent.COM_VALUE_CHANGED_TARIFICATION);
		for (int i = 0; i<1000 ;i++)
			t.fireNamedEvent(NamedEvent.COM_VALUE_CHANGED_TARIFICATION);
		t.fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
		validateSuccess(0);
		assertEquals(600, m_int);
	}
	
	/**
	 * Test the filter replace feature.
	 * @throws InterruptedException
	 */
	public void testFilterUpdate() throws InterruptedException {
		Tarification t = new Tarification();
		m_success[0] = false;
		m_int = 0;
		NamedEventListener nel = new NamedEventListener() {

			public void eventOccured(NamedEvent e) {
				m_success[0] = true;
				m_int++;
				
			}};
		t.addNamedEventListener(nel,NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
		t.fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
		validateSuccess(0);
		assertEquals(m_int,1);
		m_success[0] = false;
		t.addNamedEventListener(nel,NamedEvent.COM_VALUE_CHANGED_TARIFICATION);
		t.fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED); // should not be catch
		m_success[0] = false;
		t.fireNamedEvent(NamedEvent.COM_VALUE_CHANGED_TARIFICATION);
		validateSuccess(0);
		assertEquals(m_int,2);
		
	}
	
	private void validateSuccess(int index) {
		try {
			int counter = 0;
			while(!m_success[index] && counter <=10) {
				Thread.currentThread().sleep(1000);
				counter++;
			}
			System.out.println("Event occurs time: "+counter+" seconds");
			assertTrue(m_success[index]);
		} catch (InterruptedException e) {
			System.err.println("Interrupted");
			e.printStackTrace();
		}
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
