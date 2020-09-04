/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: OptionDefaultPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
*/

package com.simpledata.bc.uicomponents.bcoption;

import java.awt.*;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.BCOption;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.uicomponents.tools.JTextFieldNamedTitle;

/**
* This is the default model for options panels
*/
public abstract class OptionDefaultPanel
	extends JPanel
	implements NamedEventListener {
		
	protected BCOption bcOption= null;

	private EditStates container;
	
	/** to display the status (green/red ball) **/
	private JLabel status= null;
	/** to display an "equal" sign **/
	private JLabel equalLabel= null;
	/** to display the title **/
	private JTextFieldNamedTitle titleField= null;

	private ImageIcon iconOK;
	private ImageIcon iconNOK;

	/** set to true if editable **/
	protected int editableState;

	// all option panel should be this width
	public static int DEF_WIDTH= 350;
	public static int DEF_HEIGHT= 30;
	public static int DEF_COMPONENT_H= 20;
	public static int DEF_TITLE_W= 130;
	public static int DEF_ICON_W= 20;
	public static int DEF_STATUS_W= DEF_ICON_W;

	/**
	 * call this(option,true);
	 * @see #OptionDefaultPanel(EditStates container,BCOption option)
	 */
	protected OptionDefaultPanel(EditStates container, BCOption option) {
		super();
		
		this.container = container;
		
		this.bcOption= option;
		this.editableState= this.container.getEditOptionState();

		// load icons
		iconOK= Resources.greenBall;
		iconNOK= Resources.redBall;

		// init layout
		setDim(this, DEF_WIDTH, DEF_HEIGHT);

		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		// add event listener
		option.addNamedEventListener(
				this,NamedEvent.OPTION_DATA_CHANGED,null);

	}

	public EditStates getStateControler() {
	    return this.container;
	}
	

	/**
	 * This method initializes the status label
	 * 
	 */
	protected JLabel getStatus() {
		if (status == null) {
			status= new JLabel("");
			status.setIcon(iconOK);
			status.setToolTipText("ID=" + bcOption.getNID());
			setDim(status, DEF_STATUS_W, DEF_COMPONENT_H);
		}
		return status;
	}

	/**
	 * affect the green / red ball
	 */
	protected void setStatus(boolean ok) {
		if (status != null) {
			if (ok) {
				status.setIcon(iconOK);
			} else {
				status.setIcon(iconNOK);
			}
		}
	}

	/**
	 * This method initializes the "=" Label
	 * 
	 */
	protected JLabel getEqualLabel() {
		if (equalLabel == null) {
			equalLabel= new JLabel("=");
		}
		return equalLabel;
	}

	/**
	 * Get the title InputField
	 */
	protected JTextFieldNamedTitle getTitleTextField() {
		if (titleField == null) {
			boolean isEditable = 
				container.getEditOptionState() == EditStates.FULL;
			titleField= new JTextFieldNamedTitle(bcOption,isEditable) {
				public void editionStopped() {
					setStatus(true);
				}
				public void editionStarted() {
					setStatus(false);
				}
			};
			setDim(titleField, DEF_TITLE_W, DEF_COMPONENT_H);
		}
		return titleField;
	}

	/**
	 * get the option in this panel
	 */
	public BCOption getOption() {
		return bcOption;
	}

	/**
	 * @return one of EDIT_STATE_*
	 */
	public int getEditState() {
		return editableState;
	}

	/** called when a data change occurs on the option **/
	public void eventOccured(NamedEvent e) {
		if (e.getSource() == bcOption) // for me
			refresh();
	}

	/** called when a refresh is needed **/
	public abstract void refresh();

	/** a static tool to set dimensions to panels **/
	public static void setDim(JComponent c, int w, int h) {
		Dimension d= new Dimension(w, h);
		c.setPreferredSize(d);
		c.setMinimumSize(d);
	}

	public interface EditStates {
	    
		public final static int NONE = 0;
		public final static int FULL = NONE+1;
		public final static int VALUE = FULL+1;
		public final static int DEFAULT = FULL;
	    
	    /**
	     * Must return one of the static states contained
	     * in OptionDefaultPanel.EditStates interface
	     */
	    public int getEditOptionState();
	    
	}
	
}



/* $Log: OptionDefaultPanel.java,v $
/* Revision 1.2  2007/04/02 17:04:24  perki
/* changed copyright dates
/*
/* Revision 1.1  2006/12/03 12:48:38  perki
/* First commit on sourceforge
/*
/* Revision 1.21  2004/11/12 14:28:39  jvaucher
/* New NamedEvent framework. New bugs ?
/*
/* Revision 1.20  2004/10/11 10:19:16  perki
/* Percentage on Transactions
/*
/* Revision 1.19  2004/05/27 08:43:33  carlito
/* *** empty log message ***
/*
/* Revision 1.18  2004/05/22 17:49:17  perki
/* *** empty log message ***
/*
/* Revision 1.17  2004/05/22 17:30:20  carlito
/* *** empty log message ***
/*
/* Revision 1.16  2004/05/22 08:39:35  perki
/* Lot of cleaning
/*
/* Revision 1.15  2004/05/21 13:19:50  perki
/* new states
/*
/* Revision 1.14  2004/05/21 12:15:12  carlito
/* *** empty log message ***
/*
/* Revision 1.13  2004/05/18 17:04:26  perki
/* Better icons management
/*
/* Revision 1.12  2004/05/06 07:06:25  perki
/* WorkSheetPanel has now two new methods
/*
/* Revision 1.11  2004/03/18 15:43:33  perki
/* new option model
/*
/* Revision 1.10  2004/03/12 14:06:10  perki
/* Vaseline machine
/*
* Revision 1.9  2004/03/02 00:32:54  carlito
* *** empty log message ***
*
* Revision 1.8  2004/02/26 13:24:34  perki
* new componenents
*
* Revision 1.7  2004/02/26 08:55:03  perki
* *** empty log message ***
*
* Revision 1.6  2004/02/23 18:34:48  carlito
* *** empty log message ***
*
* Revision 1.5  2004/02/20 05:45:05  perki
* appris un truc
*
* Revision 1.4  2004/02/06 08:05:41  perki
* lot of cleaning in UIs
*
* Revision 1.3  2004/02/06 07:44:55  perki
* lot of cleaning in UIs
*
* Revision 1.2  2004/02/05 07:45:52  perki
* *** empty log message ***
*
* Revision 1.1  2004/01/28 15:31:48  perki
* Il neige plus
*
*/