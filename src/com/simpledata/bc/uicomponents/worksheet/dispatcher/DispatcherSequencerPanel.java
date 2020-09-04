/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: DispatcherSequencerPanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSequencer;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.dispatcher.tools.TabbedDispatcherAbstract;
import com.simpledata.bc.uitools.SButtonIcon;

/**
 * UI for DispatcherSequencer
 * @see com.simpledata.bc.components.worksheet.dispatcher.DispatcherSequencer
 */
public class DispatcherSequencerPanel extends TabbedDispatcherAbstract {
    private static final Logger m_log =
        	Logger.getLogger(DispatcherSequencerPanel.class);
	
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon=
		Resources.wsDispatcherSequence;

	/** my WorkSheet **/
	private DispatcherSequencer ds;
	
	/**
	 * The index of the last worked on worksheet.<BR>
	 * used to give the focus to the right Sheet on the UI
	 **/
	//private int atWorkIndex;
	
	/**
	 *
	 */
	public DispatcherSequencerPanel(DispatcherSequencer ds, TarifViewer tv) {
		super(ds, tv);
		this.ds= ds;
	}

	
	public void save() {
		// auto saving
	}

	
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	
	
	/**
	 * get the worksheet panel at this index
	 * @param index position of the workSheetPanel desired
	 */
	public WorkSheetPanel getWorkSheetPanelAt(int index) {
		if (ds == null) return null;
		return getDisplayController().getWorkSheetPanel(
				ds.getWorkSheetAtIndex(index));
	}
	
	/**
	 * get the index of this workSheet
	 * @return -1 if not found
	 */
	public int getWorkSheetIndex(WorkSheet ws) {
		if (ds == null) return -1;
		return ds.getWorkSheetIndex(ws);
	}
	
	/**
	 * get the index of the worksheet at work (for refresh purposes)
	 */
//	public int getAtWorkWorkSheetIndex() {
//		if (ds == null) return -1;
//		if (atWorkIndex < 0) atWorkIndex = 0;
//		if (atWorkIndex > (getTabCount() -1 )) 
//			atWorkIndex = (getTabCount() -1 );
//		return atWorkIndex;
//	}
	
	/**
	 * get the number of tabs
	 */
	public int getTabCount() {
		if (ds == null || ds.getChildWorkSheets() == null) return 0;
		return ds.getChildWorkSheets().size();
	}
	
	/**
	 * get the WorkSHeetPanel Title .. can be overwriten
	 */
	public String getTitleOf(int index) {
		return (index+1)+" "+super.getTitleOf(index);
	}
	
	
	/**
	 * 
	 */
	public JPanel getOptionPanel() {
		// no option panel for me
		return null;
	}
	
	
	
	//------------------ Action Panel ---------------------------//

	
	/**
	 * return a JPanel to be included in the border (tool bar)<BR>
	 * Overide this method if needed;
	 */
	public JPanel getActionPanel() {
	    //m_log.warn("GETACTION PANEL");
	    //We return null in simulation mode to avoid modifications 
	    // over the panels
	    if (getDisplayController().getEditWorkPlaceState() == 
	        WorkSheetPanel.WSIf.EDIT_STATE_NONE) {
	        return null;
	    }
	    
		if (jPanel5 != null)
			return jPanel5;
		
		
		upButton=
			new SButtonIcon(HORIZONTAL_LAY?Resources.moveLeft:Resources.moveUP);

		downButton=
			new SButtonIcon(
				HORIZONTAL_LAY ? Resources.moveRight : Resources.moveDown);

		plusButton= new SButtonIcon(Resources.iconPlus);

		deleteButton= new SButtonIcon(Resources.iconDelete);
		
		jPanel5 = new JPanel();
		jPanel5.setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
//			new BoxLayout(
//				jPanel5,
//				HORIZONTAL_LAY ? BoxLayout.X_AXIS : BoxLayout.Y_AXIS));

		upButton.setToolTipText(Lang.translate("Move step Up"));
		
		upButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				upButtonActionPerformed();
			}
		});

		jPanel5.add(upButton);

		downButton.setToolTipText(Lang.translate("Move step Down"));

		downButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				downButtonActionPerformed();
			}
		});

		jPanel5.add(downButton);

		plusButton.setToolTipText(
			Lang.translate("Insert a new step after this one"));
		
		plusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				plusButtonActionPerformed();
			}
		});

		jPanel5.add(plusButton);

		deleteButton.setToolTipText(Lang.translate("Delete this step"));
		
		
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				deleteButtonActionPerformed();
			}
		});

		jPanel5.add(deleteButton);
		return jPanel5;
	}

	

	void downButtonActionPerformed() {
		if (getSelectedIndex() < 0)
			return;
		//atWorkIndex = getSelectedIndex()+1;
		ds.moveWorkSheet(getSelectedIndex(), 1);
		setSelectedIndex(getSelectedIndex()+1);
		refresh();
	}

	void deleteButtonActionPerformed() {
		if (getSelectedIndex() < 0)
			return;
		//atWorkIndex = getSelectedIndex();
		ds.removeWorkSheet(getSelectedIndex());
		refresh();
	}

	void plusButtonActionPerformed() {
		//atWorkIndex = getSelectedIndex()+1;
		ds.createWorkSheetAfter(getSelectedIndex());
		setSelectedIndex(getSelectedIndex()+1);
		refresh();
	}

	void upButtonActionPerformed() {
		if (getSelectedIndex() < 0)
			return;
		//atWorkIndex = getSelectedIndex()-1;
		ds.moveWorkSheet(getSelectedIndex(), -1);
		setSelectedIndex(getSelectedIndex()-1);
		refresh();
	}

	
	
	//	 Variables declaration - do not modify
	private SButtonIcon deleteButton;
	private SButtonIcon downButton;
	private JPanel jPanel5;
	private SButtonIcon plusButton;
	
	private SButtonIcon upButton;
	// End of variables declaration


	/** nothing to do */
    public void indexHasBeenSelected(int index) {
    }
	

}

/**
 *  $Log: DispatcherSequencerPanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:38  perki
 *  First commit on sourceforge
 *
 *  Revision 1.31  2004/11/23 10:37:28  perki
 *  *** empty log message ***
 *
 *  Revision 1.30  2004/11/19 09:59:57  perki
 *  *** empty log message ***
 *
 *  Revision 1.29  2004/11/17 16:04:35  perki
 *  *** empty log message ***
 *
 *  Revision 1.28  2004/09/22 15:39:55  carlito
 *  Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 *  Revision 1.27  2004/09/09 17:24:08  carlito
 *  Creator Gold and Light are there for their first breathe
 *
 *  Revision 1.26  2004/09/07 13:35:03  carlito
 *  *** empty log message ***
 *
 *  Revision 1.25  2004/07/22 15:12:35  carlito
 *  lots of cleaning
 *
 *  Revision 1.24  2004/05/22 18:33:22  perki
 *  *** empty log message ***
 *
 *  Revision 1.23  2004/05/21 16:12:05  perki
 *  *** empty log message ***
 *
 *  Revision 1.22  2004/05/18 11:29:22  perki
 *  Abstract Tabbed
 *
 *  Revision 1.21  2004/05/18 10:10:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.20  2004/05/14 14:20:19  perki
 *  *** empty log message ***
 *
 *  Revision 1.19  2004/05/14 11:48:27  perki
 *  *** empty log message ***
 *
 *  Revision 1.18  2004/05/14 08:46:18  perki
 *  *** empty log message ***
 *
 *  Revision 1.17  2004/05/14 07:52:53  perki
 *  baby dispatcher is going nicer
 *
 *  Revision 1.16  2004/05/06 07:06:25  perki
 *  WorkSheetPanel has now two new methods
 *
 *  Revision 1.15  2004/04/09 07:16:52  perki
 *  Lot of cleaning
 *
 *  Revision 1.14  2004/03/23 19:19:33  carlito
 *  *** empty log message ***
 *
 *  Revision 1.13  2004/03/23 18:02:18  perki
 *  New WorkSHeet Panel model
 *
 *  Revision 1.12  2004/03/23 13:39:19  perki
 *  New WorkSHeet Panel model
 *
 *  Revision 1.11  2004/03/22 21:40:19  perki
 *  dodo
 *
 *  Revision 1.10  2004/03/08 09:02:20  perki
 *  houba houba hop
 *
 *  Revision 1.9  2004/03/06 11:49:22  perki
 *  *** empty log message ***
 *
 *  Revision 1.8  2004/03/02 17:59:15  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.7  2004/03/02 14:42:48  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.6  2004/02/20 05:45:05  perki
 *  appris un truc
 *
 *  Revision 1.5  2004/02/19 23:57:25  perki
 *  now 1Gig of ram
 *
 *  Revision 1.4  2004/02/19 16:21:25  perki
 *  Tango Bravo
 *
 *  Revision 1.3  2004/02/17 16:57:39  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.2  2004/02/17 15:55:02  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.1  2004/02/17 11:39:37  perki
 *  zobi la mouche n'a pas de bouche
 *
 */