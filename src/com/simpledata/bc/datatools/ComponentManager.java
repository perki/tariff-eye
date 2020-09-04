/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @version $Id: ComponentManager.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.datatools;

import org.apache.log4j.Logger;

/**
 * This class is a toolbox for routines that permit object
 * instanciation from their class instances. 
 */
public class ComponentManager {
	private static final Logger m_log = Logger.getLogger( ComponentManager.class ); 

	/**
	 * Get an instance of this Class
	 * @param className the class you want
	 * @param paramsType an array containg the Class type of the initArgs
	 * @param initArgs the argument for the constructor
	 */
	public static Object getInstanceOf
	  (String className,Class[] paramsType,Object[] initArgs)
	{
		Class dummy = null;
		try {
			dummy = Class.forName(className);
		} catch (ClassNotFoundException e) {
			m_log.error("ComponentManager: ClassNotFoundException ",e) ;
			return null;
		}
		return getInstanceOf(dummy,paramsType,initArgs);
	}
	
	/**
	 * Get an instance of this Class
	 * @param className the class you want
	 * @param paramsType an array containg the Class type of the initArgs
	 * @param initArgs the argument for the constructor
	 */
	public static Object getInstanceOf
	  (Class className, Class[] paramsType, Object[] initArgs)
 	{
		try {
			java.lang.reflect.Constructor cs = 
				className.getConstructor(paramsType);
			return cs.newInstance(initArgs);
		} catch (NoSuchMethodException e) {
			m_log.error( "ComponentManager: NoSuchMethodException ",e ) ;
		} catch (InstantiationException e) {
			m_log.error( "ComponentManager: InstantiationException ",e ) ;
		} catch (IllegalAccessException e) {
			m_log.error( "ComponentManager: IllegalAccessException ",e ) ;
		} catch (java.lang.reflect.InvocationTargetException e) {
			Throwable cause = e.getCause();
			m_log.error(
				"ComponentManager: InvocationTargetException ("
				+cause.getMessage()+")", cause 
			);
		} 
		return null;
 	}
 	
	/**
	 * Get an array representing the classes of this array of objects.
	 * @param args Arguments to retrieve classes from. 
	 * @return Array of size args.length that contains the classes of 
	 *         all objects in args. 
	 */
	public static Class[] getClassArray(Object[] args){
		Class[]paramsType = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			assert args[i] != null : 
				"Parameters must not be null"; 
			
			paramsType[i] = args[i].getClass();
		}	
		return paramsType;
	}
}
/**
 * $Log: ComponentManager.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.7  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.6  2004/09/01 09:03:19  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/08/26 12:10:40  kaspar
 * ! Transformed NullPointerException into ERROR, see trouble ticket
 *   #30
 *
 * Revision 1.4  2004/08/17 13:51:49  kaspar
 * ! #26: crash on RateOnAmount fixed. Cause: assert in the wrong place,
 *   mixup between object init and event callbacks (as usual)
 *
 * Revision 1.3  2004/07/26 20:36:09  kaspar
 * + trRateBySlice subreport that shows for all
 *   RateBySlice Workplaces. First Workplace subreport.
 * + Code comments in a lot of classes. Beautifying, moving
 *   of $Id: ComponentManager.java,v 1.2 2007/04/02 17:04:27 perki Exp $ tag.
 * + Long promised caching of reports, plus some rudimentary
 *   progress tracking.
 *
 * Revision 1.2  2004/02/26 13:24:34  perki
 * new componenents
 *
 * Revision 1.1  2004/02/01 17:15:12  perki
 * good day number 2.. lots of class loading improvement
 *
 */