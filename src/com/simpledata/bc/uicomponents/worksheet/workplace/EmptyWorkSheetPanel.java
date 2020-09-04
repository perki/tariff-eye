/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * Package that contains all the different panels for displaying
 * any given Workplace.
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import com.simpledata.bc.*;
import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.CopyItem;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.SButton;

/**
 * EmptyWorkSheet <BR>
 * used when no WorkSheet has been set .. this interface is used to
 * to create new WorkSheets
 * @version $Id: EmptyWorkSheetPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class EmptyWorkSheetPanel extends WorkSheetPanel {
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon= Resources.wsEmptyWorkSheet;

	// ---- UI
	private JLabel jLabel1;
	private SButton createButton;
	private EmptyWorkSheet ews;
	private TarifViewer tv;

	public EmptyWorkSheetPanel(EmptyWorkSheet ews, TarifViewer tv) {
		super(ews, tv);
		this.ews= ews;
		this.tv= tv;

		GridBagConstraints gridBagConstraints;

		jLabel1= new JLabel();
		createButton= new SButton();

		getContents().setLayout(new GridBagLayout());
		getContents().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				docPanelMouseEvent(evt);
			}
			public void mouseReleased(MouseEvent evt) {
				docPanelMouseEvent(evt);
			}
		});
		jLabel1.setText(Lang.translate("Empty"));
		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 0;
		gridBagConstraints.insets= new Insets(0, 0, 20, 0);
		getContents().add(jLabel1, gridBagConstraints);

		createButton.setText(Lang.translate("Create WorkSheet"));
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				createButtonActionPerformed();
			}
		});

		gridBagConstraints= new GridBagConstraints();
		gridBagConstraints.gridx= 0;
		gridBagConstraints.gridy= 1;
		gridBagConstraints.insets= new Insets(28, 0, 0, 0);
		getContents().add(createButton, gridBagConstraints);
	}

	void docPanelMouseEvent(MouseEvent e) {
		if (e.isPopupTrigger()) {
			showPopup(e.getComponent(), e.getX(), e.getY());
		}

	}

	void createButtonActionPerformed() {
		showPopup(createButton, 15, 5);
	}

	/**
	* show a WorkSheet Chooser Menu
	* @param c Component on which to draw the Popup
	* @param x the horizontal position of the popup
	* @param y the vertical position of the popup
	*/
	private void showPopup(Component c, int x, int y) {
		getJPopupMenu().show(c, x, y);
	}

	/**
	* Create Popup Menu For WorkSheet Creation
	*/
	class CreateWorkSheetPopup extends JPopupMenu {
		// UI Components 
		private JMenu menuDispatcher;
		private JMenu menuWorkPlace;

		/** Tarif or Dispatcher **/
		private WorkSheetContainer wsc;

		/** Key for setWorkSheet **/
		private String key= null;

		/** The garphical Object I'm attached To **/
		private TarifViewer tv;

		/** constructor @see WorkSheetExplorer#showWorkSheetChooser (
		WorkSheetContainer wc,
		String key,
		Component c,
		int x,
		int y)
		**/
		CreateWorkSheetPopup(
			final TarifViewer tv,
			final WorkSheetContainer wsc,
			final String key) {
			super();
			this.tv= tv;
			this.wsc= wsc;
			this.key= key;

			Class[] workSheets= wsc.getAcceptedNewWorkSheets(key);
			if (workSheets == null) {
				workSheets = WorkSheetManager.defaultsWorksheets();
			}
			if (workSheets.length == 0) {
				this.setEnabled(false);
				return; //empty Menu
			}

			// We now remove all workSheets invisible to users.
			if (! BC.isSimple()) {
			    ArrayList/*<Class>*/ visibleWorkSheets = new ArrayList/*<Class>*/();
			    int visSize = workSheets.length;
			    for (int i=0; i< visSize; i++) {
			        visibleWorkSheets.add(workSheets[i]);
			    }
			    
			    int invSize = WorkSheetManager.USER_INVISIBLE_CLASSES.length;
			    for (int j=0; j<invSize; j++) {
			        visibleWorkSheets.remove(
			                WorkSheetManager.USER_INVISIBLE_CLASSES[j]);
			    }
			    
			    // Producing the revised workSheets
			    workSheets = (Class[])(visibleWorkSheets.toArray(new Class[0]));
			}
			
			menuDispatcher= new JMenu(Lang.translate("Create a dispatcher"));
			menuDispatcher.setIcon(Resources.wsDefaultDispatcher);

			menuWorkPlace= new JMenu();
			menuWorkPlace.setText(Lang.translate("Create a workPlace"));
			menuWorkPlace.setIcon(Resources.wsDefaultWorkPlace);

			int dispatcherCounter= 0;
			int workPlaceCounter= 0;
			for (int i= 0; i < workSheets.length; i++) {
				if (WorkPlace.class.isAssignableFrom(workSheets[i])) {
					addToMenu(menuWorkPlace, workSheets[i]);
					workPlaceCounter++;
				}
				if (Dispatcher.class.isAssignableFrom(workSheets[i])) {
					addToMenu(menuDispatcher, workSheets[i]);
					dispatcherCounter++;
				}
			}
			if (dispatcherCounter > 0)
				add(menuDispatcher);
			if (workPlaceCounter > 0)
				add(menuWorkPlace);
				
				
			//------------ Paste --------------//
			ArrayList pastables = tv.getCopyItems(WorkSheet.class);
			if (pastables.size() == 0) return;
			
			addSeparator(); // add a separator
			int counterPaste = 0;
			JMenu paste = new JMenu();
			paste.setText(Lang.translate("Paste"));
			paste.setIcon(Resources.iconPaste);
			
			int counterPasted = 0;
			JMenu pasted = new JMenu();
			pasted.setText(Lang.translate("Paste from trash"));
			pasted.setIcon(Resources.iconTrashFull);
			
			for (int i = 0; i < pastables.size(); i++) {
				final CopyItem item =  (CopyItem) pastables.get(i);
				final WorkSheet ws = (WorkSheet) item.copiable;
				
				JMenuItem temp = new JMenuItem(item.title);
				temp.setIcon(item.icon);
				temp.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						ws.setParent(wsc,key);
						tv.setWorkSheet(ws);
						item.drop();
					}});
				
				// disable if not accepted here
				boolean ok = false;
				for (int j= 0; j < workSheets.length; j++) {
					if (workSheets[j].isInstance(ws)) {
						ok = true;
						break;
					}
				}
				temp.setEnabled(ok);
				if (item.deleted) {
					pasted.add(temp);
					counterPasted++;
				} else {
					paste.add(temp);
					counterPaste++;
				}
			}
			
			if (counterPaste > 0) {
				// add clear All
				paste.addSeparator();
				JMenuItem clear = new JMenuItem(Lang.translate("Clear All"));
				clear.setIcon(Resources.iconDelete);
				clear.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					tv.clearCopyItems(false);
				}});
				paste.add(clear);
				
				add(paste);
			}
			
			if (counterPasted > 0) {
				// add clear All
				pasted.addSeparator();
				JMenuItem clear = new JMenuItem(Lang.translate("Empty Trash"));
				clear.setIcon(Resources.iconTrashEmpty);
				clear.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					tv.clearCopyItems(true);
				}});
				pasted.add(clear);
				
				add(pasted);
			}
		}
		

		void createWorkSheet(Class c) {
			WorkSheet ws= WorkSheetManager.createWorkSheet(wsc, c, key);
			if (ws != null) {
				// If I'm the monitored worksheet change the focus to the new one
				if (tv.getWorkSheetAtWork() == getWorkSheet()) {
					tv.setWorkSheet(ws);
				}
			}
		}

		private void addToMenu(JMenu jm, final Class c) {
			final CreateWorkSheetPopup wsp= this;

			String s= WorkSheetManager.getWorkSheetTitle(c);
			ImageIcon im= WorkSheetPanel.getWorkSheetIcon(c);
			if (s != null) {

				JMenuItem jmi= new JMenuItem(Lang.translate(s), im);
				jm.add(jmi);

				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						wsp.createWorkSheet(c);
					}
				});

			}
		}
	}
	/* (non-Javadoc)
	 * @see WorkSheetPanel#save()
	 */
	public void save() {
		// nothing to do
	}

	/**
	 * @see WorkSheetPanel
	 */
	public JPopupMenu getJPopupMenu() {
		if (getDisplayController().getEditWorkPlaceState() !=  
			WSIf.EDIT_STATE_FULL) return null;
		return new CreateWorkSheetPopup(
			tv,
			ews.getWscontainer(),
			ews.getContainerKey());
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {
		boolean b = (getDisplayController().getEditWorkPlaceState() ==  
			WSIf.EDIT_STATE_FULL);
		
			createButton.setEnabled(b);
			createButton.setVisible(b);
			
	}

	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	/**
	 */
	public JPanel getOptionPanel() {
		return null;
	}
	private JPanel jp;
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		if (jp == null) jp = new JPanel();
		return jp;
	}
}

/**
 *  $Log: EmptyWorkSheetPanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:41  perki
 *  First commit on sourceforge
 *
 *  Revision 1.24  2004/11/15 18:41:24  perki
 *  Introduction to inserts
 *
 *  Revision 1.23  2004/09/23 09:23:41  carlito
 *  WorkSheets hidden in popup
TransferOption button activated..
 *
 *  Revision 1.22  2004/09/07 13:35:03  carlito
 *  *** empty log message ***
 *
 *  Revision 1.21  2004/07/26 20:36:10  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: EmptyWorkSheetPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.20  2004/07/26 16:46:09  carlito
 *  *** empty log message ***
 *
 *  Revision 1.19  2004/07/08 14:59:00  perki
 *  Vectors to ArrayList
 *
 *  Revision 1.18  2004/05/31 15:59:06  perki
 *  *** empty log message ***
 *
 *  Revision 1.16  2004/05/14 08:46:18  perki
 *  *** empty log message ***
 *
 *  Revision 1.15  2004/05/06 07:06:25  perki
 *  WorkSheetPanel has now two new methods
 *
 *  Revision 1.14  2004/04/09 07:16:52  perki
 *  Lot of cleaning
 *
 *  Revision 1.13  2004/03/24 13:11:14  perki
 *  Better Tarif Viewer no more null except
 *
 *  Revision 1.12  2004/03/23 13:39:19  perki
 *  New WorkSHeet Panel model
 *
 *  Revision 1.11  2004/03/04 18:44:23  perki
 *  *** empty log message ***
 *
 *  Revision 1.10  2004/03/04 14:37:18  perki
 *  copy goes to hollywood
 *
 *  Revision 1.9  2004/03/04 14:32:07  perki
 *  copy goes to hollywood
 *
 *  Revision 1.8  2004/02/19 19:47:34  perki
 *  The dream is coming true
 *
 *  Revision 1.7  2004/02/19 16:21:25  perki
 *  Tango Bravo
 *
 *  Revision 1.6  2004/02/18 11:00:57  perki
 *  *** empty log message ***
 *
 *  Revision 1.5  2004/02/17 16:57:39  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.4  2004/02/17 15:55:02  perki
 *  zobi la mouche n'a pas de bouche
 *
 *  Revision 1.3  2004/02/17 09:40:06  perki
 *  zibouw
 *
 *  Revision 1.2  2004/02/17 08:54:07  perki
 *  zibouw
 *
 *  Revision 1.1  2004/02/16 18:59:15  perki
 *  bouarf
 *
 */