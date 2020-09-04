/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: OptionsViewer.java,v 1.2 2007/04/02 17:04:27 perki Exp $
*/
package com.simpledata.bc.uicomponents.bcoption.viewers;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datatools.ComponentManager;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uitools.*;

import org.apache.log4j.Logger;

/**
* Option Viewer.<BR>
* UI Tools that browses Options of a WorkSheet
*/
public class OptionsViewer extends JPanel implements OptionsViewerInterface {
	private static final Logger m_log = Logger.getLogger( OptionsViewer.class ); 
	
	private JListWithPanels jwp;
	private WorkSheet workSheet;
	private SButtonIcon createButton;
	private SButtonIcon deleteButton;

	private OptionDefaultPanel.EditStates stateController;
	
	private boolean withMenu;
	
	/**
	 * 
	 * @param ws
	 * @param withMenu true if you want a menu on it
	 */
	public OptionsViewer(WorkSheet ws, boolean withMenu, 
	        OptionDefaultPanel.EditStates editStateController) {
		super();
		this.withMenu = withMenu;
		this.stateController = editStateController;
		
		setLayout(new BorderLayout());
		this.workSheet= ws;
		
		if (withMenu) {
			JPanel jp1= new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			jp1.setPreferredSize(new Dimension(20, 20));
			jp1.setMinimumSize(new Dimension(20, 20));
			jp1.setSize(new Dimension(20, 20));

			createButton= new SButtonIcon();
			createButton.setIcon(Resources.iconPlus);
			createButton.setPreferredSize(new Dimension(20, 20));
			createButton.setToolTipText(Lang.translate("Create New Option"));
			createButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					createButtonActionPerformed();
				}
			});

			deleteButton= new SButtonIcon(Resources.iconDelete);
			deleteButton.setPreferredSize(new Dimension(20, 20));
			deleteButton.setToolTipText(
				Lang.translate("Delete Selected Option"));
			deleteButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					deleteButtonActionPerformed();
				}
			});

			jp1.add(createButton);
			jp1.add(deleteButton);
			add(jp1, BorderLayout.WEST);
		}

		// The option List
		jwp= new JListWithPanels();
		jwp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshButtons();
			}
		});
		add(jwp, BorderLayout.CENTER);

		// get the options list
		ArrayList v= ws.getOptions();
		refreshButtons();
		for (int i= 0; i < v.size(); i++) {
			_addToList((BCOption) v.get(i));
		}
	}

	/**
	 * The delete button has been triggered
	 */
	public void deleteButtonActionPerformed() {
		BCOption selectedO= getSelectedOption();
		if (!workSheet.canRemoveOption(selectedO))
			return;
		if (workSheet.removeOption(selectedO)) {
			jwp.removePanel(jwp.getSelectedPanel());
		}
		fireActionEvent();
	}

	/**
	 * get the Actually selected option
	 */
	private BCOption getSelectedOption() {
		if (jwp.getSelectedPanel() == null)
			return null;
		if (workSheet == null)
			return null;
		return ((OptionDefaultPanel) jwp.getSelectedPanel()).getOption();
	}

	/**
	 * The create button has been triggered
	 */
	public void createButtonActionPerformed() {
		JPopupMenu jpm= OptionMenu.getPopupMenu(this);
		jpm.show(createButton, 5, 5);
	}

	/**
	* create an option of this type to this WorkSheet
	*/
	public void createOption(Class c) {
		_addToList(OptionManager.createOption(workSheet, c));
	}

	/**
	* add an option of this type to this WorkSheet
	*/
	public void addRemoteOption(BCOption o) {
		if (OptionManager.addRemoteOptionTo(workSheet, o)) {
			_addToList(o);
		}
	}

	/**
	* Add Option to list
	*/
	private void _addToList(BCOption option) {
		assert option != null; 
		
		OptionDefaultPanel panel = getOptionPanel(option);
		//panel.setEditState(optionEditState);
		jwp.addPanel(panel);
		fireActionEvent();
		refreshButtons();
	}

	/** refresh the buttons states **/
	public void refreshButtons() {
		if (! withMenu) return;
		
		deleteButton.setEnabled(false);
		createButton.setEnabled(false);
		if (workSheet == null)
			return;
		deleteButton.setEnabled(workSheet.canRemoveOption(getSelectedOption()));
		createButton.setEnabled(workSheet.getAcceptedNewOptions().length > 0);
	}

	/**
	 * the workSheet I belong To
	 */
	public WorkSheet getWorkSheet() {
		return workSheet;
	}

	/** memory for option panels **/
	private HashMap optionPanels;
	
	/** option panel list **/
	private OptionDefaultPanel getOptionPanel(BCOption option) {
		if (optionPanels == null) {
					optionPanels = new HashMap();
				}
		if (! optionPanels.containsKey(option)) {
			optionPanels.put(option,createOptionPanel(stateController ,option));
		}
		return (OptionDefaultPanel) optionPanels.get(option);
	}

	/**
	* return an instance of the OptionPanel desired for this Option
	*/
	private static OptionDefaultPanel createOptionPanel(
			OptionDefaultPanel.EditStates controller, BCOption option) {
		final String headToRemove= "com.simpledata.bc.components";
		final String headToAdd= "com.simpledata.bc.uicomponents";
		final String tailToAdd= "Panel";
		String name= option.getClass().getName();

		// compose UI class names
		if (name.indexOf(headToRemove) != 0) {
			m_log.error( "OptionsViewer: wrong class type ->" + name );
			return null;
		}

		name= headToAdd + name.substring(headToRemove.length()) + tailToAdd;


		// create parameters Array
		Object[] initArgs= new Object[2];
		initArgs[0]= controller;
		initArgs[1]= option;
		

		// create parameters class type to get the right constructor
		Class[] paramsType= ComponentManager.getClassArray(initArgs);
		paramsType[0] = OptionDefaultPanel.EditStates.class;
			
		OptionDefaultPanel odp=
			(OptionDefaultPanel) ComponentManager.getInstanceOf(
				name,
				paramsType,
				initArgs);

		if (odp != null) {
			return odp;
		}

		return new ErrorOptionPanel(controller,option);
	}

	/**
	* Create Menu For Options Creation
	*/
	public static class OptionMenu {

		public static JPopupMenu getPopupMenu(OptionsViewerInterface ov) {
			JPopupMenu res= new JPopupMenu();
			addToMenu(res, ov);
			return res;
		}

		public static JMenu getJMenu(OptionsViewerInterface ov) {
			JMenu res= new JMenu();
			addToMenu(res, ov);
			return res;
		}

		private static void addToMenu(
			JComponent menu,
			final OptionsViewerInterface ov) {
			OptionManager.OptionDef[] opts=
				OptionManager.getOptionsFor(ov.getWorkSheet());

			for (int i= 0; i < opts.length; i++) {
				final OptionManager.OptionDef c= opts[i];
				String s= OptionManager.getOptionTitle(c.getMyClass());
				if (s != null) {
					JComponent myMenu= menu;
					String remoteString= s;
					String createString= s;
					if (c.canCreate() && c.canRemote()) {
						myMenu= new JMenu(s);
						menu.add(myMenu);
						remoteString= Lang.translate("Use known option");
						createString= Lang.translate("Create new");
					}

					if (c.canCreate()) {
						JMenuItem jmi= new JMenuItem();
						jmi.setText(Lang.translate(createString));
						myMenu.add(jmi);

						jmi.addActionListener(new ActionListener() {
							public void actionPerformed(
								java.awt.event.ActionEvent evt) {
								ov.createOption(c.getMyClass());
							}
						});
					}

					if (c.canRemote()) {
						// TODO change this with a compactNode popup
						JMenu jm= new JMenu(remoteString);
						myMenu.add(jm);

						Iterator e= c.getRemotes().iterator();
						while (e.hasNext()) {
							final BCOption bco = (BCOption) e.next();
							
							JMenuItem jmi= new JMenuItem();
							jmi.setText(bco.getTitle());
							jm.add(jmi);

							jmi.addActionListener(new ActionListener() {
								public void actionPerformed(
									java.awt.event.ActionEvent evt) {
									ov.addRemoteOption(bco);
								}
							});
						}

//						JMenuItem jmi= new JMenuItem();
//						jmi.setText(Lang.translate(remoteString));
//						myMenu.add(jmi);
//
//						jmi.addActionListener(new ActionListener() {
//							public void actionPerformed(
//								java.awt.event.ActionEvent evt) {
//								ov.createOption(c.getMyClass());
//							}
//						});
					}

				}
			}
		}
	}

	//--------------------------- EVENTS ----------------------//
	private ArrayList actionListeners;

	/** Add an action listener for event change **/
	public void addActionListener(ActionListener listener) {
		if (actionListeners == null)
			actionListeners= new ArrayList();
		actionListeners.add(listener);
	}

	private void fireActionEvent() {
		if (actionListeners == null)
			return;
		ActionEvent e= new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
		synchronized (actionListeners) {
			Iterator en= actionListeners.iterator();
			while (en.hasNext()) {
				((ActionListener) en.next()).actionPerformed(e);
			}
		}
	}
	
}

class ErrorOptionPanel extends OptionDefaultPanel {

	/**
	 */
	protected ErrorOptionPanel(OptionDefaultPanel.EditStates editStateControler,BCOption opt) {
		super(editStateControler,opt);
		this.setLayout(new FlowLayout());
		this.add(new JTextField("ERROR: " + opt.getClass()));
	}

	/**
	 * @see com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel#refresh()
	 */
	public void refresh() {
		// nothing to reload
	}

}

/** $Log: OptionsViewer.java,v $
/** Revision 1.2  2007/04/02 17:04:27  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:41  perki
/** First commit on sourceforge
/**
/** Revision 1.10  2004/09/03 13:25:34  kaspar
/** ! Log.out -> log4j part four
/**
/** Revision 1.9  2004/08/17 13:51:49  kaspar
/** ! #26: crash on RateOnAmount fixed. Cause: assert in the wrong place,
/**   mixup between object init and event callbacks (as usual)
/**
/** Revision 1.8  2004/08/05 00:23:44  carlito
/** DispatcherCase bugs corrected and aspect improved
/**
/** Revision 1.7  2004/07/22 15:12:35  carlito
/** lots of cleaning
/**
/** Revision 1.6  2004/07/08 14:59:00  perki
/** Vectors to ArrayList
/**
/** Revision 1.5  2004/05/22 17:49:17  perki
/** *** empty log message ***
/**
/** Revision 1.4  2004/05/22 17:30:20  carlito
/** *** empty log message ***
/**
/** Revision 1.3  2004/05/22 08:39:36  perki
/** Lot of cleaning
/**
/** Revision 1.2  2004/05/21 13:19:50  perki
/** new states
/**
/** Revision 1.1  2004/05/06 07:27:32  perki
/** OptionViewer moved
/**
/** Revision 1.24  2004/05/06 07:06:25  perki
/** WorkSheetPanel has now two new methods
/**
/** Revision 1.23  2004/04/09 07:16:51  perki
/** Lot of cleaning
/**
/** Revision 1.22  2004/03/24 14:33:46  perki
/** Better Tarif Viewer no more null except
/**
/** Revision 1.21  2004/03/18 15:43:33  perki
/** new option model
/**
/** Revision 1.20  2004/03/12 14:06:10  perki
/** Vaseline machine
/**
/** Revision 1.19  2004/03/06 11:49:22  perki
/** *** empty log message ***
/**
/** Revision 1.18  2004/03/02 17:59:15  perki
/** breizh cola. le cola du phare ouest
/**
/** Revision 1.17  2004/03/02 14:42:48  perki
/** breizh cola. le cola du phare ouest
/**
/** Revision 1.16  2004/02/26 14:34:20  perki
/** Ou alors la terre est un croissant comme la lune
/**
/** Revision 1.15  2004/02/26 13:24:34  perki
/** new componenents
/**
/** Revision 1.14  2004/02/26 08:55:03  perki
/** *** empty log message ***
/**
/** Revision 1.13  2004/02/23 18:34:48  carlito
/** *** empty log message ***
/**
/** Revision 1.12  2004/02/20 05:45:05  perki
/** appris un truc
/**
/** Revision 1.11  2004/02/06 08:05:41  perki
/** lot of cleaning in UIs
/**
/** Revision 1.10  2004/02/06 07:44:55  perki
/** lot of cleaning in UIs
/**
/** Revision 1.9  2004/02/05 15:11:39  perki
/** Zigouuuuuuuuuuuuuu
/**
/** Revision 1.8  2004/02/04 15:42:17  perki
/** cleaning
/**
* Revision 1.7  2004/02/01 18:27:51  perki
* dimmanche soir
*
* Revision 1.6  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.5  2004/01/29 13:40:40  perki
* *** empty log message ***
*
* Revision 1.4  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.3  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.2  2004/01/20 15:28:45  perki
* got milk?
*
* Revision 1.1  2004/01/20 14:30:56  perki
* Mort aux vaches..........
*
*/