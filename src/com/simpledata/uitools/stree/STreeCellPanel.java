/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.uitools.stree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.simpledata.bc.Resources;


/**
 * This class is used by STreeCellRenderer to render a node in an STree<br>
 * It offers some methods to adapt its content to a specified STreeNode
 * @see STreeNode
 * @see STree 
 */	
public class STreeCellPanel extends JPanel {

	protected JLabel checkIcon;
	protected JLabel nodeIcon;
	protected JLabel nodeText;
	protected boolean withCheck;
	
	private boolean iconOldStyle = false;
	
	public static Icon defaultOpenedIcon = null;
	public static Icon defaultClosedIcon = null;
	public static Icon defaultLeafIcon = null;
	public static ImageIcon notCheckedIcon = null;
	public static ImageIcon partiallyCheckedIcon = null;
	public static ImageIcon fullyCheckedIcon = null;
	
	// Icons for checkBoxes
	public static ImageIcon ICON_NOT_CHECKED_DISABLED;
	public static ImageIcon ICON_NOT_CHECKED_ENABLED;
	public static ImageIcon ICON_PARTIALLY_CHECKED_DISABLED;
	public static ImageIcon ICON_PARTIALLY_CHECKED_ENABLED;
	public static ImageIcon ICON_FULLY_CHECKED_DISABLED;
	public static ImageIcon ICON_FULLY_CHECKED_ENABLED;
	
	// Static initialization block
	static {
		// Icons for checkBoxes
		STreeCellPanel.notCheckedIcon =  Resources.getIcon("images"+File.separator+"stree","noCheck.gif") ;
		STreeCellPanel.partiallyCheckedIcon =  Resources.getIcon("images"+File.separator+"stree","partialCheck.gif") ;
		STreeCellPanel.fullyCheckedIcon =  Resources.getIcon("images"+File.separator+"stree","fullCheck.gif") ;
		
		// Icons for checkBoxes
		ICON_NOT_CHECKED_DISABLED = Resources.getIcon("images"+File.separator+"stree","not_checked_disabled.gif");
		ICON_NOT_CHECKED_ENABLED = Resources.getIcon("images"+File.separator+"stree","not_checked_enabled.gif");
		ICON_PARTIALLY_CHECKED_DISABLED = Resources.getIcon("images"+File.separator+"stree","partially_checked_disabled.gif");
		ICON_PARTIALLY_CHECKED_ENABLED = Resources.getIcon("images"+File.separator+"stree","partially_checked_enabled.gif");
		ICON_FULLY_CHECKED_DISABLED = Resources.getIcon("images"+File.separator+"stree","fully_checked_disabled.gif");
		ICON_FULLY_CHECKED_ENABLED = Resources.getIcon("images"+File.separator+"stree","fully_checked_enabled.gif");
		
		// Icons for treeNodes
		DefaultTreeCellRenderer dtcr = new DefaultTreeCellRenderer();
		STreeCellPanel.defaultOpenedIcon = dtcr.getDefaultOpenIcon();
		STreeCellPanel.defaultClosedIcon = dtcr.getDefaultClosedIcon();
		STreeCellPanel.defaultLeafIcon = dtcr.getDefaultLeafIcon();
	}
	
	
	/**
	* Constructor
	* @param withCheck determines if we want to display checkboxes
	*/
	public STreeCellPanel(boolean withCheck) {
		this.withCheck = withCheck;
		checkIcon = new JLabel();
		nodeIcon = new JLabel();
		nodeText = new JLabel();
		nodeText.setOpaque(true);
		setLayout(withCheck);
	}
	
	/**
	* Internal method used to construct the JPanel Layout
	*/
	private void setLayout(boolean withCheck) {
		this.removeAll();
		
		GridBagConstraints gridBagConstraints;
		
		this.setLayout(new java.awt.GridBagLayout());
		
		// Internal counter used to correctly place components
		int componentCounter = 0;
		
		// Adding checkbox if wanted
		if (withCheck) {
			//checkIcon.setText("check");
			
			gridBagConstraints = new java.awt.GridBagConstraints();		
			gridBagConstraints.gridx = componentCounter;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
			this.add(checkIcon, gridBagConstraints);
			componentCounter++;
		}
		
		//nodeIcon.setText("icon");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = componentCounter;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 1);
		this.add(nodeIcon, gridBagConstraints);
		componentCounter++;
		
		//nodeText.setText("text");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = componentCounter;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(2, 1, 2, 5);
		this.add(nodeText, gridBagConstraints);
		
	}
	
	/**
	 * Tells the panel to adapt its display to be representative of the specified STreeNode
	 * @param stn tree node to be represented
	 * @param expanded true if node is expanded in tree (false if leaf or )
	 */
	public void adaptOnNode(STreeNode stn, boolean expanded) {
		ImageIcon icon;
		
		if (this.withCheck) {
			// We assign correct icon to checkIcon
			// We first get the check state of stn
			icon = stn.getCheckIcon();
			if (icon != null) {
				this.checkIcon.setIcon(icon);
				//this.checkIcon.setPreferredSize(new Dimension(icon.getIconWidth()+2,icon.getIconHeight()+4));
			} else {
				int checkState = stn.getCheckState();
				boolean isEnabled = stn.isCheckable();
				switch(checkState) {
				case STreeNode.FULLY_CHECKED :
					if (iconOldStyle) {
						this.checkIcon.setIcon(STreeCellPanel.fullyCheckedIcon);
						break;
					}
					if (isEnabled) {
						this.checkIcon.setIcon(STreeCellPanel.ICON_FULLY_CHECKED_ENABLED);
					} else {
						this.checkIcon.setIcon(STreeCellPanel.ICON_FULLY_CHECKED_DISABLED);
					}
					//this.checkIcon.setPreferredSize(new Dimension(STreeCellPanel.fullyCheckedIcon.getIconWidth()+2,STreeCellPanel.fullyCheckedIcon.getIconHeight()+4));
					break;
				case STreeNode.PARTIALLY_CHECKED :
					if (iconOldStyle) {
						this.checkIcon.setIcon(STreeCellPanel.partiallyCheckedIcon);
						break;
					}
					if (isEnabled) {
						this.checkIcon.setIcon(STreeCellPanel.ICON_PARTIALLY_CHECKED_ENABLED);
					} else {
						this.checkIcon.setIcon(STreeCellPanel.ICON_PARTIALLY_CHECKED_DISABLED);
					}
					//this.checkIcon.setPreferredSize(new Dimension(STreeCellPanel.partiallyCheckedIcon.getIconWidth()+2,STreeCellPanel.partiallyCheckedIcon.getIconHeight()+4));
					break;
				default :
					if (iconOldStyle) {
						this.checkIcon.setIcon(STreeCellPanel.notCheckedIcon);
						break;
					}
					if (isEnabled) {
						this.checkIcon.setIcon(STreeCellPanel.ICON_NOT_CHECKED_ENABLED);
					} else {
						this.checkIcon.setIcon(STreeCellPanel.ICON_NOT_CHECKED_DISABLED);
					}
					//this.checkIcon.setPreferredSize(new Dimension(STreeCellPanel.notCheckedIcon.getIconWidth()+2,STreeCellPanel.notCheckedIcon.getIconHeight()+4));
				}
				
				// We should be considering enabled/disabled checkIcon
			}
		}
		
		icon = stn.getIcon();
		if (icon != null) {
		    this.nodeIcon.setIcon(icon);
		} else {
		    // Assign correct icon to node
		    if (expanded) {
		        // Node is an open non-leaf node
		        icon = stn.getOpenedIcon();
		        if (icon != null) {
		            this.nodeIcon.setIcon(icon);
		            //this.nodeIcon.setPreferredSize(new Dimension(icon.getIconWidth()+1,icon.getIconHeight()+4));
		        } else {
		            this.nodeIcon.setIcon(STreeCellPanel.defaultOpenedIcon);
		            //this.nodeIcon.setPreferredSize(new Dimension(STreeCellPanel.defaultOpenedIcon.getIconWidth()+1,STreeCellPanel.defaultOpenedIcon.getIconHeight()+4));
		        }
		    } else if (stn.isLeaf()) {
		        icon = stn.getLeafIcon();
		        if (icon != null) {
		            this.nodeIcon.setIcon(icon);
		            //this.nodeIcon.setPreferredSize(new Dimension(icon.getIconWidth()+1,icon.getIconHeight()+4));
		        } else {
		            this.nodeIcon.setIcon(STreeCellPanel.defaultLeafIcon);
		            //this.nodeIcon.setPreferredSize(new Dimension(STreeCellPanel.defaultLeafIcon.getIconWidth()+1,STreeCellPanel.defaultLeafIcon.getIconHeight()+4));
		        }
		    } else {
		        icon = stn.getClosedIcon();
		        if (icon != null) {
		            this.nodeIcon.setIcon(icon);
		            //this.nodeIcon.setPreferredSize(new Dimension(icon.getIconWidth()+1,icon.getIconHeight()+4));
		        } else {
		            this.nodeIcon.setIcon(STreeCellPanel.defaultClosedIcon);
		            //this.nodeIcon.setPreferredSize(new Dimension(STreeCellPanel.defaultClosedIcon.getIconWidth(),STreeCellPanel.defaultClosedIcon.getIconHeight()+4));
		        }
		    }
		}
		
		// Set correct text 
		this.nodeText.setText(stn.toString());
		//Dimension d = this.nodeText.getSize();
		//this.nodeText.setPreferredSize(new Dimension(d.width, d.height));

	}
	
	/* (non-Javadoc)
	 * @see java.awt.Component#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		int h = 0;
		int w = 0;
		Dimension d_checkIcon;
		Dimension d_nodeIcon = nodeIcon.getPreferredSize();
		Dimension d_nodeText = nodeText.getPreferredSize();
		if (this.withCheck) {
			// STreeCellPanel contains checkBoxes
			d_checkIcon = checkIcon.getPreferredSize();
			h = Math.max(Math.max(d_checkIcon.height, d_nodeIcon.height) , d_nodeText.height) + 4;
			w = (d_checkIcon.width+2) + (d_nodeIcon.width+1) + (d_nodeText.width+6);
		} else {
			h = Math.max( d_nodeIcon.height, d_nodeText.height) + 4;
			w = (d_nodeIcon.width+1) + (d_nodeText.width+6);
		}
		
		return new Dimension(w,h);
	}
	
	/**
	 * Set background color for the checkbox and treeNode icons
	 * @param color color to apply
	 */
	public void setIconsBackground(Color color) {
		this.setBackground(color);
	}
	
	/**
	 * Set background color for tree node text
	 * @param color color to apply
	 */
	public void setTextBackground(Color color) {
		this.nodeText.setBackground(color);
	}
	
	/**
	 * Set foreground color for tree node text
	 * @param color color to apply
	 */
	public void setTextForeground(Color color) {
		this.nodeText.setForeground(color);
	}
	
	/**
	 * Get the correct JLabel to paint in drag drop gesture
	 * @return JLabel to be painted
	 */
	public JLabel getLabel() {
		JLabel res = new JLabel();
		res.setIcon(nodeIcon.getIcon());
		res.setText(nodeText.getText());
		return res;
	}
	
	public int getCheckLimit() {
		int res = 0;
		if (withCheck) {
			res = this.checkIcon.getIcon().getIconWidth();
		}
		return res;
	}
	
	public int getTextStartX() {
		int res = 0;
		if (withCheck) {
			res += this.checkIcon.getIcon().getIconWidth()+2;
		}
		res += this.nodeIcon.getIcon().getIconWidth()+1;
		return res;
	}
	
}

/*
 * $Log: STreeCellPanel.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.11  2004/06/28 13:16:06  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/04/12 16:10:14  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/03/03 18:46:20  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/02/24 10:12:32  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/02/14 21:53:51  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/02/05 14:05:51  carlito
 * STree now can be set with or without check live... getPreferredSize problem solved for STreeCellPanel
 *
 * Revision 1.5  2004/02/02 12:08:19  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/31 09:31:59  perki
 * Changed Color selction Text
 *
 * Revision 1.3  2004/01/30 15:22:30  carlito
 * Popups and checks functional
 *
 * Revision 1.2  2004/01/29 20:08:24  carlito
 * CheckBox fonctionnent
 *
 * Revision 1.1  2004/01/29 16:17:56  carlito
 * new file
 *
 * 
 */
