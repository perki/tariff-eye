/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: DistributionManager.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.uicomponents.filler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComboBox;

import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.datatools.ComponentManager;
import com.simpledata.bc.tools.Lang;

/**
 * Contains a list of all distribution methods and knows how
 * to link a distribution method<BR> plus take care of constraints
 */
public class DistributionManager {
	
	private static Class[] methodsAssets = 
		new Class[] {DistribRelyOnParent.class , 
			DistribNumberOfLines.class,
			DistribSizeOfLines.class};
	
	private static Class[] methodsTransactions = 
		new Class[] {DistribRelyOnParent.class , 
			DistribTrSizeOfLines.class,
			DistribTrPercentOfAssets.class};
	
	/** 
	 * contains the classes Titles<BR> 
	 * Those titles will go thru Lang.translate
	 * **/
	private static HashMap/*<Class,String>*/ titles ;
	
	static {
		titles = new HashMap();
		titles.put(DistribRelyOnParent.class,"Relies on parent");
		titles.put(DistribNumberOfLines.class,"Set the number of lines");
		titles.put(DistribSizeOfLines.class,"Set the size of a line");
		titles.put(DistribTrSizeOfLines.class,"Set the size of a line");
		titles.put(DistribTrPercentOfAssets.class,
		        "Size of a line depends on Assets amount");
	}
	
	/** return the <B>translated</B> title corresponding to this Clas **/
	public static String getTitle(Class c) {
		Object s = titles.get(c);
		if (s == null) s = "Unkown Method";
		return Lang.translate(s.toString());
	}
	
	/** return the Accepted method for this FillerNode **/
	public static Class[] getAcceptedMethods(Class type,FillerNode fn) {
		assert fn != null : "Cannot work on null FillerNode";

		if (type == AssetsRoot0.class) {
			if (fn.getParent() == null ) 
				return new Class[] {
					DistribNumberOfLines.class,
					DistribSizeOfLines.class};
			return methodsAssets;
		}
		if (type == TransactionsRoot0.class) {
			if (fn.getParent() == null ) 
				return new Class[] {
					DistribTrSizeOfLines.class ,
					DistribTrPercentOfAssets.class};
			return methodsTransactions;
		}
		
		return null;
	}
	
	/** return true if this FillerNode accepts this method **/
	public static boolean 
		fillerNodeAccepts(Class type,FillerNode fn, Class dmClass) {
		Class[] a = getAcceptedMethods(type,fn);
		for (int i = 0; i < a.length ; i++) {
			if (a[i] == dmClass) return true;
		}
		return false;
	}
	
	/** 
	 * attach to this FillerNode the prefered DistributionMethod
	 **/
	public static void setPreferedDistribMethod(Class type,FillerNode fn) {
		
		if (fn.getParent() == null) {
			if (type == AssetsRoot0.class)
				attachDistributionMethod(type,DistribSizeOfLines.class,fn);
			if (type == TransactionsRoot0.class) {
			    attachDistributionMethod(type,
			            DistribTrPercentOfAssets.class,fn);
			    //attachDistributionMethod(type,DistribTrSizeOfLines.class,fn);
			}
				
			return;
		}
		attachDistributionMethod(type,DistribRelyOnParent.class,fn);
		
	}
	
	/** attach new distribution method **/
	public static void 
		attachDistributionMethod(Class type,Class c,FillerNode fn) {
		//		 create parameters Array
		Object[] initArgs= new Object[] {fn};
		
		// create parameters class type to get the right constructor
		Class[] paramsType= new Class[] {FillerNode.class};
		
		DistributionMethod dm=
			(DistributionMethod) 
				ComponentManager.getInstanceOf(c, paramsType, initArgs);
		if (dm != null) {
			if (dm instanceof DistributionMethod.Typeable) 
				((DistributionMethod.Typeable) dm).setType(type);
			fn.setDistributionMethod(type,dm);
		}
	}
	
	
	/** 
	 * A JCombobox for the FillerNode <BR>
	 * Will take care of all event handeling
	 * **/
	public static JComboBox getComboBox(final Class type,FillerNode fn) {
		//		-------- JCOMBOBOX -----------//
		class ComboChoice {
			Class method;
			ComboChoice(Class method) {
				this.method = method;
				
			}
			
			
			
			public String toString() {
				String s = (type == AssetsRoot0.class) ? 
						"Asset Repartition :" : "Transactions :";
				return Lang.translate(s)+getTitle(method);
			}
		}
		
		Vector v = new Vector();
		Class[] cs = getAcceptedMethods(type,fn);
		int selected = 0;
		for (int i = 0; i < cs.length; i++) {
			v.add(new ComboChoice(cs[i]));
			if (cs[i] == fn.getDistributionMethod(type).getClass())
				selected = i;
		}
		
		final FillerNode fnf = fn;
		final JComboBox jcb = new JComboBox(v);
		jcb.setSelectedIndex(selected);
		
		jcb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Class c = ((ComboChoice) jcb.getSelectedItem()).method;
				
				if (c != fnf.getDistributionMethod(type).getClass()) {
					attachDistributionMethod(type,c,fnf);
					fnf.redistributeRepartition(type);
				}
			}});
		
		return jcb;
	}
	
}
/*
*$Log: DistributionManager.java,v $
*Revision 1.2  2007/04/02 17:04:27  perki
*changed copyright dates
*
*Revision 1.1  2006/12/03 12:48:42  perki
*First commit on sourceforge
*
*Revision 1.9  2004/11/17 16:48:36  perki
*cleverer distribution
*
*Revision 1.8  2004/10/11 10:19:16  perki
*Percentage on Transactions
*
*Revision 1.7  2004/08/02 14:17:11  perki
*Repartitions on Transactions Youhoucvs commit -m cvs commit -m
*
*Revision 1.6  2004/08/02 10:41:13  perki
**** empty log message ***
*
*Revision 1.5  2004/08/02 10:08:43  perki
*introducing distribution for transactions
*
*Revision 1.4  2004/08/02 08:32:36  perki
**** empty log message ***
*
*Revision 1.3  2004/07/31 12:01:00  perki
*Still have problems with the progressbar
*
*Revision 1.2  2004/07/31 11:06:55  perki
*Still have problems with the progressbar
*
*Revision 1.1  2004/07/26 17:39:36  perki
*Filler is now home
*
*/