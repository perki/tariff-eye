/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: BCNodeEditor.java,v 1.2 2007/04/02 17:04:31 perki Exp $
 */
package com.simpledata.bc.uicomponents.bcnode;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.simpledata.bc.BC;
import com.simpledata.bc.components.tarif.TarifManager;
import com.simpledata.bc.datamodel.BCNode;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.tools.JTextFieldBC;
import com.simpledata.bc.uicomponents.tools.JTextFieldNamedTitle;
import com.simpledata.bc.uitools.SButton;

/**
 * A class that provide tools for editing BCNode
 */
public class BCNodeEditor {
	final static String[] boolProps = {BCNode.PROP_BOOL_MOVEABLE, 
			BCNode.PROP_BOOL_EXTENDABLE, BCNode.PROP_BOOL_DROPABLE};
	
	/**
	 * Util to propagate (copy) the setting of this node to its childrens..
	 * and to childrens of childrens..
	 * @param start
	 */
	public static void propagateSettingsToChildren(BCNode start) {
		Iterator e = start.getChildrens().iterator();
		
		while (e.hasNext()) {
			BCNode child = (BCNode) e.next();
			child.setAcceptedTarifTypesReliesOnParent(true);
			for (int i = 0; i < boolProps.length ; i++)
				child.setBoolProperty(boolProps[i],
						start.getBoolProperty(boolProps[i]));
			
			// recursive
			propagateSettingsToChildren(child);
		}
		
	}
	
	/**
	 * Util to propagate the "keep_under" setting
	 * @param parent if null, then the first parent is used
	 */
	public static void propagateKeepUnder(BCNode start,BCNode parent) {
			if (! start.isRoot()) {
				BCNode temp = parent == null ? start.getParent() : parent;
				if (temp.isAncestorOf(start) && temp != start) {
					start.setProperty(BCNode.PROP_NODE_TO_KEEP_AS_ANCESTOR, 
							temp);
				}
			}
			Iterator e = start.getChildrens().iterator();
			while (e.hasNext()) 
				propagateKeepUnder((BCNode)e.next(),parent);
	}
	

	/**
	 * Create a panel with BCNode properties edition
	 */
	public static JPanel getPropertiesEditor (final BCNode bcnode) {

	    JPanel jp = new JPanel();
	    jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
	    
	    if (BC.isSimple()) {
	        JTextFieldBC idEditor = new JTextFieldBC() {
	            public void stopEditing() {
	                bcnode.changeNID(getText());
	            }
	            
	            public void startEditing() {
	                // TODO Auto-generated method stub
	                
	            }
	        };
	        idEditor.setText(bcnode.getNID());
	        idEditor.setMinimumSize(new Dimension(160, 22));
	        jp.add(idEditor);
	        
	    }
			
		

		// NODE TITLE EDITION PART
		JPanel titlePanel = new JPanel(new GridBagLayout());
		JLabel titleLabel = new JLabel(Lang.translate("TITLE"));

		
		
		JTextFieldNamedTitle titleField = 
		    new JTextFieldNamedTitle(bcnode,true){
			public void editionStopped() {	
			}
			public void editionStarted() {
			}};
		
		
		
		GridBagConstraints gridBagConstraints;
		
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		titlePanel.add(titleLabel, gridBagConstraints);
		
		//titleField.setText(bcnode.getTitle());
		titleField.setMinimumSize(new Dimension(160, 22));
		//titleField.setPreferredSize(new Dimension(160, 22));
		Dimension dim = titleField.getPreferredSize();
		int x = (dim.width > 160) ? dim.width + 20 : 160;
		dim = new Dimension(x, 22);
		titleField.setPreferredSize(dim);
		
	
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(3, 3, 3, 6);
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		titlePanel.add(titleField, gridBagConstraints);
		
		jp.add(titlePanel);
		
		
		// ---- stop -----//
		if (! BC.isSimple()) return jp;
		
		// END OF NODE TITLE EDITION PART
		
		// add the booleans options for BCNode
		
		for (int i = 0; i < boolProps.length; i++) {
			final String key = boolProps[i];
			final JCheckBox jcb = new JCheckBox();
			JPanel jpt = new JPanel(new BorderLayout());
		
			jpt.add(new JLabel(Lang.translate("PROP:"+key))
				,BorderLayout.CENTER);
		
		
			jcb.setSelected(bcnode.getBoolProperty(key));
			jcb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					bcnode.setBoolProperty(key,jcb.isSelected());
				}
			});
		
			jpt.add(jcb,BorderLayout.EAST);
		
			jp.add(jpt);
		}
	
		//		add the PROP_NODE_TO_KEEP_AS_ANCESTOR option
		 final String key2 = BCNode.PROP_NODE_TO_KEEP_AS_ANCESTOR;
		 JPanel jpt2 = new JPanel(new BorderLayout());
		 jpt2.add(new JLabel(Lang.translate("PROP:"+key2))
						 ,BorderLayout.CENTER);
				
		 // get list of parents 
		 ArrayList parents = bcnode.getTree().getAncestorsOf(bcnode);
		 // remove myself from this list
		 parents.remove(bcnode);
		 final JComboBox jcob = new JComboBox(parents.toArray());
		//jcob.setLightWeightPopupEnabled(false);
		 BCNode temp = (BCNode) bcnode.getProperty(key2);
		 if (temp != null) {
			 jcob.setSelectedItem(temp);
		 }
		 jcob.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 bcnode.setProperty(key2, jcob.getSelectedItem());
			 }
		 });
		 jpt2.add(jcob,BorderLayout.EAST);

		 //jp.add(jpt2);
		 
	
		//		the relies on parent checkBox
		JPanel jpt3 = new JPanel(new BorderLayout());
		jpt3.add(new JLabel(Lang.translate("Tarif relies on Parent"))
						   ,BorderLayout.CENTER);
		final JCheckBox jcb3 = new JCheckBox();
		jpt3.add(jcb3,BorderLayout.EAST);
		
		//jp.add(jpt3);
		if (parents.size() > 0) {
		 	// We don't attach this panel if node has no parents...
			jp.add(jpt2);
			jp.add(jpt3);
		 }
		
		jcb3.setSelected(bcnode.isRelingOnParentForTarifTypes());
	
	
		//	add the Accepted Tarif Type for this BCNode
		ArrayList allTarifTypes = TarifManager.getTarifTypes();
	
		for (int i = 0; i < allTarifTypes.size(); i++) {
			final Object key = allTarifTypes.get(i);
			final JCheckBox jcb = new JCheckBox();

			JPanel jpt = new JPanel(new BorderLayout());
		
			jpt.add(new JLabel(Lang.translate("Accept "+key))
				,BorderLayout.CENTER);
			jcb.setSelected(bcnode.acceptThisTarifType(key));
			jcb.setEnabled(! jcb3.isSelected());
			jcb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (jcb.isSelected()) {
						bcnode.addAcceptedTarifType(key);
					} else {
						bcnode.removeAcceptedTarifType(key);
					}
				}
			});
		
			// add an action listener for changes on jcb3
			jcb3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jcb.setEnabled(! jcb3.isSelected());
					if (bcnode.isRelingOnParentForTarifTypes()
						!= jcb3.isSelected()) { // update bcnode if needed
						bcnode.setAcceptedTarifTypesReliesOnParent(
								jcb3.isSelected());
					}
					jcb.setSelected(bcnode.acceptThisTarifType(key));
				}
			});
		
		
			jpt.add(jcb,BorderLayout.EAST);
		
			jp.add(jpt);
		}
		
		
		//------------------------- PROPAGATE --------------------------//
		// add the propagate buttons
		JPanel propagate = new JPanel();
		propagate.setLayout(new BoxLayout(propagate,BoxLayout.Y_AXIS));
		propagate.setBorder(new TitledBorder(
				Lang.translate("Propagate settings")));
		
		// the "keep under" selector
		 // get list of parents 
		 ArrayList parents2 = new ArrayList();
		 parents2.add(new OptionPJCB(false,null));
		 parents2.add(new OptionPJCB(true,null));
		 Iterator e = bcnode.getTree().getAncestorsOf(bcnode).iterator();
		 while (e.hasNext()) {
		 	parents2.add(new OptionPJCB(true,(BCNode) e.next()));
		 }
		
		 final JComboBox jcob2 = new JComboBox(parents2.toArray());
		
		 
		 // the propagate button
		SButton jb1 = new SButton(Lang.translate("Propagate settings!"));
		jb1.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				propagateSettingsToChildren(bcnode);
				OptionPJCB o3 = (OptionPJCB)  jcob2.getSelectedItem();
				if (o3.doit) {
					propagateKeepUnder(bcnode,o3.parent);
				}
			}});
		propagate.add(jb1);
		propagate.add(jcob2);
		
		jp.add(propagate);
		
		// Focus on title
		String s = titleField.getText();
		titleField.grabFocus();
		titleField.setCaretPosition(s.length());
		titleField.setSelectionStart(0);
		titleField.setSelectionEnd(s.length());
		
		
		return jp;
	}
	
	/** a small class for the propagate keep under Jcombobox **/
	static class OptionPJCB {
		public boolean doit;
		public BCNode parent;
		
		public OptionPJCB(boolean doit,BCNode parent) {
			this.doit = doit;
			this.parent = parent;
		}
		
		public String toString() {
			if (! doit) {
				return Lang.translate("! Do not propagate \"Keep Under\"");
			}
			if (parent == null) {
				return Lang.translate("Keep childs under their actual parent");
			}
			return Lang.translate("Keep childrens under:")+parent.getTitle();
		}
	}

}
/* $Log: BCNodeEditor.java,v $
/* Revision 1.2  2007/04/02 17:04:31  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:45  perki
/* First commit on sourceforge
/*
/* Revision 1.15  2004/09/14 14:46:29  perki
/* *** empty log message ***
/*
/* Revision 1.14  2004/07/31 16:45:56  perki
/* Pairing step1
/*
/* Revision 1.13  2004/07/22 15:12:35  carlito
/* lots of cleaning
/*
/* Revision 1.12  2004/07/08 14:59:00  perki
/* Vectors to ArrayList
/*
/* Revision 1.11  2004/05/20 09:39:43  perki
/* *** empty log message ***
/*
/* Revision 1.10  2004/05/06 07:06:25  perki
/* WorkSheetPanel has now two new methods
/*
/* Revision 1.9  2004/03/12 14:06:10  perki
/* Vaseline machine
/*
/* Revision 1.8  2004/03/06 11:49:22  perki
/* *** empty log message ***
/*
/* Revision 1.7  2004/03/04 17:16:44  perki
/* copy goes to hollywood
/*
 * Revision 1.6  2004/03/04 16:56:49  perki
 * copy goes to hollywood
 *
 * Revision 1.5  2004/03/03 11:39:49  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/03/03 10:17:23  perki
 * Un petit bateau
 *
 * Revision 1.3  2004/02/23 18:34:48  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/02/18 16:57:29  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/02/01 17:15:12  perki
 * good day number 2.. lots of class loading improvement
 *
 */