/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.tools;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * InstanceFinder should find all occurrences of an instance
 * in a nested class hierarchy or container. 
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: InstanceFinder.java,v 1.2 2007/04/02 17:04:25 perki Exp $ 
 */
public class InstanceFinder {
	private Object m_container;
	private Object m_instance; 
	private Set m_visited;
	private Map m_path;
	private LinkedList m_leftToVisit;
	
	private PrintWriter m_out;
	
	private static final Logger m_log = Logger.getLogger( InstanceFinder.class );
	
	private InstanceFinder( String filename, Object container, Object instance ) {
		m_container = container;
		m_instance = instance;
		m_visited = new HashSet();
		m_path = new HashMap();
		m_leftToVisit = new LinkedList();
		
		try {
			m_out = new PrintWriter( new FileOutputStream( filename ) );
		}
		catch( Exception e ) {
			m_log.error( e );
		}
	}
	
	private void go() {
		try {
			recurse( m_container );
			
			while ( m_leftToVisit.size() > 0 ) {
				Object c = m_leftToVisit.removeFirst();
				recurse( c );
			} 
		}
		catch ( Exception e ) {
			m_log.error( e );
		}
		finally {
			m_out.close();
		}
	}
	
	private void recurse( Object container ) {
		if ( container == null ) return;
		if ( alreadyVisited( container ) ) return;
		
		// m_out.println( "Inspecting container "+container.hashCode()+" of class "+container.getClass().toString());
		
		m_visited.add( container );
		
		// get class of container and its defined fields
		final Class klass = container.getClass();
		
		// add contents of java array
		if ( klass.isArray() ) {
			int len = Array.getLength( container );
			
			for (int i=0; i<len; ++i) {
				Object element = Array.get( container, i );
				if ( element != null ) 
					doVisit( container, element );
			}
		}
		
		List fields = (List) AccessController.doPrivileged( 
				new PrivilegedAction() {
					public Object run() {
						return getAllFields( klass );
					}
				}
		);
		
		ListIterator it = fields.listIterator();
		
		// for each fields, recurse into content
		while ( it.hasNext() ) {
			final Field f = (Field) it.next();
			final Object c = container; 
			Object contContainer = null;
			
			contContainer = AccessController.doPrivileged(
				new PrivilegedAction() {
	        public Object run() {
	        	try {
	        		f.setAccessible( true );
	        		Object o = f.get( c );
	        		/*m_out.println(
        				" inspecting field "
        				+f.getName()
								+" contains "
								+( o == null ? 
										"null" : 
										o.getClass()+"("+o.hashCode()+")" )
	        		);*/
	        		return o;
	        	}
	        	catch ( IllegalAccessException e ) {
	        		return null;
	        	}
	        }
				}
	    );
			
			if ( contContainer != null && contContainer == m_instance ) {
				m_out.println( " -> contains a reference to given object in field "+f.getName());
				printPathTo( container );
			}
			else {
				// container might itself contain references, 
				// visit it
				if ( contContainer != null ) 
					doVisit( container, contContainer );
			}
			
		}
	}
	
	private void printPathTo( Object o ) {
		m_out.println( " retracing path: " );
		Object last = o;
		
		while ( o != null ) {
			m_out.println( o.getClass().toString()+" ("+o.hashCode()+") ->"+o);
			
			o = m_path.get( o );
			
			if ( o == last ) 
				break; 
			
			last = o;
		}
		m_out.println();
	}
	
	private void doVisit( Object c, Object cc ) {
		m_leftToVisit.add( cc );
		if ( ! m_path.containsKey( cc ) )
			m_path.put( cc, c );
	}
	
	private List getAllFields( Class c ) {
		List l = new ArrayList();
		if ( c == null) return l;
		
		l.addAll( Arrays.asList( c.getDeclaredFields() ) );
		l.addAll( getAllFields( c.getSuperclass() ) );
		
		return l;
	}
	
	private boolean alreadyVisited( Object obj ) {
		if ( obj == null ) return true; 
		if ( obj == this ) return true; 
		if ( obj.getClass().getName().startsWith("java.lang.reflect.") ) return true;
		return m_visited.contains( obj );
	}
	
	
	public static void find( String filename, Object container, Object instance ) {
		InstanceFinder finder = new InstanceFinder( filename, container, instance );
		finder.go(); 
	}
	
}
