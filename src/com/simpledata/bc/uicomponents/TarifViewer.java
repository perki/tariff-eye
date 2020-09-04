/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
* $Id: TarifViewer.java,v 1.2 2007/04/02 17:04:23 perki Exp $
*/
package com.simpledata.bc.uicomponents;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.datamodel.Copiable;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.WorkSheetContainer;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uicomponents.tools.ReducOrFixedEditor;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel.WSIf;
import com.simpledata.bc.uitools.ImageTools;
import com.simpledata.bc.uitools.SButtonIcon;

/**
* Window that shows the content of a tarif
*/
public abstract class TarifViewer extends JPanel 
								  implements OptionDefaultPanel.EditStates ,
								  			 WorkSheetPanel.WSIf{
	
	private static final Logger m_log = Logger.getLogger( TarifViewer.class );
	
	/** for temporary purpose **/
	private final static Integer NON_MOVABLE_DEPTH_OF_VIEW = new Integer(100);
	
	//------------- EDITION STATES ---------------------//
	
	
	

	/** actually viewed WorkSheet (as root) **/
	protected WorkSheet workSheetATW;

	/** hashmap containig the worksheet panel corresponding to ws **/
	protected HashMap workSheetPanels;

	/** same as  workSheetPanels but their hidden version**/
	protected HashMap hiddenWorkSheetPanels;

	public TarifViewer() {
		workSheetPanels= new HashMap();
		hiddenWorkSheetPanels= new HashMap();
		workSheetATW= null;
		initComponents();
	}
	
	//------------- abstracts ---------------------//
	
	/**
	 * init the graphical components
	 */
	protected abstract void initComponents();
	
	/**
	* refresh the display (all components)
	*/
	public abstract void refresh();
	
	
	
	//------------- finals ------------------------//
	
	
	/** 
	 * set the tarif to display 
	 * @return the root WorkSheetPanel or null if none
	 **/
	public  final WorkSheetPanel setTarif(Tarif t) {
		if (workSheetATW != null) {
			if (workSheetATW.getTarif() == t)
				return (WorkSheetPanel) workSheetPanels.get(workSheetATW);
		}

		// update viewed workSheet
		if (t == null) {
			return setWorkSheet(null);
		}
		return setWorkSheet(t.getWorkSheet());
	}

	/** 
	 * set the workSheet to display 
	 * @return the WorkSheetPanel corresponding to this WorkSheet
	 **/
	public  final WorkSheetPanel setWorkSheet(WorkSheet ws) {
		return setWorkSheetPanel(getWorkSheetPanel(ws));
	}

	/** 
	 * set the workSheetPanel to display 
	 * @return the passed WorkSheetPanel
	 **/
	public  final WorkSheetPanel setWorkSheetPanel(WorkSheetPanel wsdp) {
		if (wsdp == null)
			return null;
		if (getWorkSheetAtWork() == wsdp.getWorkSheet()) {
			m_log.info( "returned current wsdp" );
			return wsdp; // to avoid loops
		}

		// are we switch tarifs ??
		if (getTarifAtWork() != wsdp.getTarif()) {

			//	clear the memory maps 
			// TODO CLEAR MAPS WHEN IT TAKES TO MUCH MEMORY (maybe timer or something)
//			workSheetPanels.clear();
//			hiddenWorkSheetPanels.clear();

		}
		workSheetATW= wsdp.getWorkSheet();
		refresh();
		return wsdp;
	}

	/**
	 * return the RootWorkSheetPanel
	 */
	public final WorkSheetPanel getRootWorkSheetPanel() {
		if (getTarifAtWork() == null)
			return getWorkSheetPanel(null);
		return getWorkSheetPanel(getTarifAtWork().getWorkSheet());
	}

	/**
	 * return the WorkSheetPanel corresponding to this WorkSheet<BR>
	 * @param ws can be null
	 */
	public  final WorkSheetPanel getWorkSheetPanel(WorkSheet ws) {
		WorkSheetPanel wsp = _getWorkSheetPanel(ws);
		if (ws == null) return wsp;
		
		// check if depth is reached
		if ((getMaxDepthModified().intValue() - 1) < getWorkSheetDepth(ws)) {
			return getHiddenWorkSheetPanel(wsp);
		}

		return wsp;
	}
	/**
	 * return the WorkSheetPanel corresponding to this WorkSheet<BR>
	 * @param ws can be null
	 */
	public  final WorkSheetPanel _getWorkSheetPanel(WorkSheet ws) {
		
		if (ws == null) {
			return new NoTarif(this);
		}
		
		
		if (!workSheetPanels.containsKey(ws)) {
			workSheetPanels.put(ws, WorkSheetPanel.getWorkSheetPanel(ws, this));
		} 

		return (WorkSheetPanel) workSheetPanels.get(ws);
	
	}

	/**
	 * return an hidden version of the panel (if hidden)
	 * has a map to avoid mutiple creation of instances
	 */
	private  final WorkSheetPanel getHiddenWorkSheetPanel(WorkSheetPanel wsp) {
		
		if (!hiddenWorkSheetPanels.containsKey(wsp)) {
			hiddenWorkSheetPanels.put(wsp, new HiddenWorkSheetPanel(wsp,this));
		}
		return (WorkSheetPanel) hiddenWorkSheetPanels.get(wsp);
	}

	/**
	 * return the depth of this WorkSheet in references of the actual
	 * workSheetDP
	 */
	private  final int getWorkSheetDepth(WorkSheet ws) {
		if (workSheetATW == null)
			return 1000; // no root yet
		
		if (ws == null) {
			m_log.info( "Ws == null" );
			return 0; // I'm the actually shown WS
		}
			
		if (ws == workSheetATW)
			return 0; // I'm the actually shown WS

		if (ws.getWscontainer() instanceof Tarif)
			return 1000; // I reached the root Container 

		return 1 + getWorkSheetDepth((Dispatcher) ws.getWscontainer());
	}

	/**
	 * return the maxDepth value
	 */
	public  final static Integer getMaxDepth() {
		//Integer defaultValue= new Integer(10);
		return (Integer)BC.getParameter(Params.KEY_MAX_DEPTH,
		        Integer.class);
		
//		if (res == null) {
//			BC.setParameter(PARAM_KEY_DEPTH, defaultValue);
//		} else {
//			try {
//				defaultValue= (Integer) res;
//			} catch (Exception e) {
//				BC.setParameter(PARAM_KEY_DEPTH, defaultValue);
//			}
//		}
//		return defaultValue;
	}

	/**
	 * This method is used while no solution is found to replace the 
	 * scrolling method, it enables to have two different depth of view :
	 * for tarifViewers that have isDepthOfViewEnabled and those who don't
	 * TODO review before release
	 */
	public final Integer getMaxDepthModified() {
	    if (isDepthOfViewEnabled()) {
	      return getMaxDepth();  
	    } 
	    return NON_MOVABLE_DEPTH_OF_VIEW;
	}
	
	public final static void setMaxDepthStatic(int depth) {
		if (getMaxDepth().intValue() != depth) {
			BC.setParameter(Params.KEY_MAX_DEPTH, new Integer(depth));
		}
	}
	
	/**
	 * set the maxDepth of View()
	 */
	public  final void setMaxDepth(int depth) {
		if (getMaxDepth().intValue() != depth) {
			setMaxDepthStatic(depth);
			refresh();
		}
	}

	


	/**
	 * @return the actually displayed worksheet
	 */
	public  final WorkSheet getWorkSheetAtWork() {
		return workSheetATW;
	}

	/**
	 * util to get the Tarif relative to the WorkSheet At Work
	 * @return the actually displayed tarif
	 */
	public  final Tarif getTarifAtWork() {
		if (workSheetATW == null)
			return null;
		return workSheetATW.getTarif();
	}

	// -------------------- events -------------------//
	
	public JPopupMenu getJPopupMenuFor(final WorkSheetPanel wsp) {
		
		if (wsp.getWorkSheet() == null) return null;
		
		
		JPopupMenu jp= new JPopupMenu();
		
		
		
		// ----- VIEW / EDIT : Reductions -----------//
		if (getEditReductionsState() != WSIf.EDIT_REDUCTION_NONE) {
			String msg = Lang.translate("Discount (unkown state)");
			if (getEditReductionsState() == WSIf.EDIT_REDUCTION_FULL) {
				msg = Lang.translate("Edit discount");
			}
			
			if (getEditReductionsState() == WSIf.EDIT_REDUCTION_VIEW) {
				msg = Lang.translate("View discount");
			}
			
			if (wsp.getWorkSheet().getAcceptedReducType() 
					!= ReducOrFixed.ACCEPT_REDUC_NO) {
				JMenuItem reduction=
					new JMenuItem(msg, 
							Resources.reductionButton);
				reduction.setToolTipText(
					ReducOrFixedEditor.getStatusHTML(wsp.getWorkSheet()));
				reduction.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						wsp.showReductionPanel();
					}
				});
				jp.add(reduction);
				jp.add(new JSeparator());
			}
		}
		
		//		 ----- INSERT -----------------------------//
		if (getEditWorkPlaceState() == WSIf.EDIT_STATE_FULL) {
		 
		    JMenu reduction=
				new JMenu(Lang.translate("Insert Dispatcher"));
		    reduction.setIcon(Resources.arrowBoth);
			
			Class[] ds = WorkSheetManager.insertableDispatchers(
			        wsp.getWorkSheet());
			for (int i = 0; i < ds.length; i++) {
			    final Class c = ds[i];
			    
				String s= WorkSheetManager.getWorkSheetTitle(c);
				ImageIcon im= WorkSheetPanel.getWorkSheetIcon(c);
				if (s != null) {

					JMenuItem jmi= new JMenuItem(Lang.translate(s), im);
					reduction.add(jmi);

					jmi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							WorkSheetManager.insertDispatcher(
							        wsp.getWorkSheet(),c);
						}
					});

				}
			}
			if (ds.length > 0)
			    jp.add(reduction);
		    
		}
		
		// ----- RENAME / DELETE / COPY -----------//
		if (getEditWorkPlaceState() == WSIf.EDIT_STATE_FULL) { 
			
			//	------- RENAME / COPY----------//
			final JMenuItem rename=
				new JMenuItem(Lang.translate("Rename"), Resources.iconEdit);
			rename.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					wsp.getNamedTitleDescriptionEditor(null,null);
				}
			});
			jp.add(rename);
			
			
			JMenuItem copy =
				new JMenuItem(Lang.translate("Copy"), Resources.iconCopy);
			copy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addCopyItem(wsp.getWorkSheet().copy(),
								wsp.getWorkSheet().getTitle(),
								wsp.getIcon());
				}
			});
			
			jp.add(copy);
			jp.add(new JSeparator());
			
			//-------- DELETE ---------//
			JMenuItem delete=
				new JMenuItem(Lang.translate("Delete"), Resources.iconDelete);
			delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// XXX Do not use this feature since in produce
					// bugs when deleting a dispatcher case.
					
					// first of all copy the deleted Item
					addDeletedItem(wsp.getWorkSheet().copy(),
											wsp.getWorkSheet().getTitle(),
											wsp.getIcon());
					
					WorkSheetContainer temp=wsp.getWorkSheet().getWscontainer();
					String keyTemp=
						wsp.getWorkSheet().getWscontainer().getWorkSheetKey(
								wsp.getWorkSheet());
					wsp.getWorkSheet().drop();

					setWorkSheet(temp.getWorkSheetAt(keyTemp));
				}
			});
			jp.add(delete);
			jp.add(new JSeparator());
		}
		// return null if nothing has been added
		if (jp.getComponentCount() == 0) return null;
		return jp;
	}
	
	
	// -------------------- copy / paste -------------//

	/** ArrayList that keep the copied Items **/
	private ArrayList copyItems;

	/** get the list of copiable Items **/
	public  final ArrayList getCopyItems() {
		if (copyItems == null)
			copyItems= new ArrayList();
		return copyItems;
	}

	/** get the list of copiable Items that are subclasses of this class **/
	public  final ArrayList getCopyItems(Class superClass) {
		ArrayList result= new ArrayList();
		Iterator e= getCopyItems().iterator();
		CopyItem temp= null;
		while (e.hasNext()) {
			temp= (CopyItem) e.next();
			if (superClass.isInstance(temp.copiable)) {
				result.add(temp);
			}
		}

		return result;
	}

	/**
	 * Add a copiable Item to the memory
	 */
	public  final void 
		addCopyItem(Copiable theObject, String title, ImageIcon icon) {
		getCopyItems().add(new CopyItem(this, theObject, title, icon,false));
	}
	
	/**
	 * Add a deleted Item to the memory
	 */
	public  final void 
		addDeletedItem(Copiable theObject, String title, ImageIcon icon) {
		getCopyItems().add(new CopyItem(this, theObject, title, icon,true));
	}

	/** remove a copiable Item from the memory **/
	public  final void removeCopyItem(CopyItem ci) {
		getCopyItems().remove(ci);
	}

	/**
	 * remove all copiable item from memory
	 */
	public  final void clearCopyItems(boolean isDeleted) {
		for (int i = (getCopyItems().size() - 1); i > -1 ; i-- ){
			if (((CopyItem) getCopyItems().get(i)).deleted == isDeleted) { 
				getCopyItems().remove(i);
			}
		}
	}
	

}

/**
 * A terminal WorkSheet with a view option
 */
class HiddenWorkSheetPanel extends WorkSheetPanel {
	WorkSheetPanel hidden;
	
	TarifViewer myTv;
	/**
	 * 
	 * @param hidden the WorkSheetPanel to hide
	 */
	protected HiddenWorkSheetPanel(WorkSheetPanel hidden,TarifViewer tv) {
		this(hidden.getWorkSheet(), tv);
		this.myTv = tv;
		this.hidden= hidden;

		ImageIcon magni=
			ImageTools.drawIconOnIcon(
				Resources.wsMagnifier,
				getIcon(),
				new Point(15, 13));

		getContents().setBorder(hidden.getPanelBorder());

		SButtonIcon jb= new SButtonIcon(magni);
		jb.setBorderPainted(false);
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMe();
			}
		});

		getContents().add(jb);
	}

	/**
	 * NEVER USES IT!!
	 */
	protected HiddenWorkSheetPanel(WorkSheet ws, TarifViewer tv) {
		super(ws, tv);
	}

	/**
	 * set the first parent of mww to be shown
	 */
	void showMe() {
		WorkSheet toShow= hidden.getWorkSheet();

		for (int i= 0; i < (TarifViewer.getMaxDepth().intValue() - 1); i++) {
			if (toShow.getWscontainer() == null)
				break;
			if (toShow.getWscontainer() instanceof Tarif)
				break;
			toShow= (WorkSheet) toShow.getWscontainer();
		}
		myTv.setWorkSheet(toShow);

	}

	/**
	 * @see WorkSheetPanel#save()
	 */
	public void save() {
		hidden.save();
	}

	/**
	 * @see WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
	    ImageIcon ii = hidden.getIcon();
	    if (ii == null) ii = new ImageIcon();
		return ii;
	}

	/**
	 * @see WorkSheetPanel#getJPopupMenu()
	 */
	public JPopupMenu getJPopupMenu() {
		return hidden.getJPopupMenu();
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {}

	/**
	 * do not HighLight hidden WorkSheets
	 */
	public boolean isHighLighted() {
		return false;
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
		if (jp == null)
			jp= new JPanel();
		return jp;
	}
}

/**
* NoTarif
*/
class NoTarif extends WorkSheetPanel {
	NoTarif(TarifViewer tv) {
		super(null, tv);

		getContents().setLayout(new FlowLayout());
		getContents().add(
		        new JLabel(new ImageIcon(Resources.splashSmallImagePath())));
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
	 */
	public void save() {}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return null;
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {}

	
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
	    // TOD Mettre descrition Tarification
		if (jp == null)
			jp= new JPanel();
		return jp;
	}
}

/**
* $Log: TarifViewer.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:37  perki
* First commit on sourceforge
*
* Revision 1.75  2005/01/26 14:36:28  jvaucher
* Issue#73: Copy bug yields the soft to be unable to save with XML encoder.
* Added a null constructor to DummyWorkSheetContainer to bypass the problem
*
* Revision 1.74  2004/11/16 18:30:51  carlito
* New parameter management ...
*
* Revision 1.73  2004/11/16 07:08:17  perki
* Now license is loaded from the jar file directly
*
* Revision 1.72  2004/11/15 18:41:24  perki
* Introduction to inserts
*
* Revision 1.71  2004/11/10 15:18:41  jvaucher
* Ticket #49 : Event deadlock corrected
*
* Revision 1.70  2004/10/20 08:19:39  perki
* *** empty log message ***
*
* Revision 1.69  2004/09/14 13:06:32  perki
* *** empty log message ***
*
* Revision 1.68  2004/09/13 15:27:32  carlito
* *** empty log message ***
*
* Revision 1.67  2004/09/07 10:13:43  kaspar
* ! Replacing Log.out
*
* Revision 1.66  2004/09/02 15:51:46  perki
* Lot of change in calculus method
*
* Revision 1.65  2004/07/22 15:12:34  carlito
* lots of cleaning
*
* Revision 1.64  2004/07/08 14:59:00  perki
* Vectors to ArrayList
*
* Revision 1.63  2004/05/23 12:16:22  perki
* new dicos
*
* Revision 1.62  2004/05/23 10:40:06  perki
* *** empty log message ***
*
* Revision 1.61  2004/05/22 18:33:22  perki
* *** empty log message ***
*
* Revision 1.60  2004/05/22 17:30:20  carlito
* *** empty log message ***
*
* Revision 1.59  2004/05/21 13:19:50  perki
* new states
*
* Revision 1.58  2004/05/18 15:11:25  perki
* Better icons management
*
* Revision 1.57  2004/05/14 08:46:18  perki
* *** empty log message ***
*
* Revision 1.56  2004/05/14 07:52:53  perki
* baby dispatcher is going nicer
*
* Revision 1.55  2004/05/06 07:06:25  perki
* WorkSheetPanel has now two new methods
*
* Revision 1.54  2004/05/05 15:26:38  perki
* balooo
*
* Revision 1.53  2004/05/05 15:23:49  perki
* balooo
*
* Revision 1.52  2004/05/05 15:00:44  perki
* TarifViewer modifications
*
* Revision 1.51  2004/05/05 14:55:07  perki
* TarifViewer modifications
*
* Revision 1.50  2004/05/05 10:44:59  perki
* tarif viewer is better now
*
* Revision 1.49  2004/04/13 21:30:14  perki
* *** empty log message ***
*
* Revision 1.48  2004/04/12 12:30:28  perki
* Calculus
*
* Revision 1.47  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.46  2004/03/24 14:33:46  perki
* Better Tarif Viewer no more null except
*
* Revision 1.45  2004/03/24 13:11:14  perki
* Better Tarif Viewer no more null except
*
* Revision 1.44  2004/03/23 19:45:18  perki
* New Calculus Model
*
* Revision 1.43  2004/03/23 13:39:19  perki
* New WorkSHeet Panel model
*
* Revision 1.42  2004/03/22 21:40:19  perki
* dodo
*
* Revision 1.41  2004/03/22 19:32:45  perki
* step 1
*
* Revision 1.40  2004/03/22 18:59:02  perki
* step 1
*
* Revision 1.39  2004/03/22 16:40:47  perki
* step 1
*
* Revision 1.38  2004/03/22 14:32:30  carlito
* *** empty log message ***
*
* Revision 1.37  2004/03/06 14:24:50  perki
* Tirelipapon sur le chiwawa
*
* Revision 1.36  2004/03/06 11:49:22  perki
* *** empty log message ***
*
* Revision 1.35  2004/03/04 14:37:18  perki
* copy goes to hollywood
*
* Revision 1.34  2004/03/04 14:32:07  perki
* copy goes to hollywood
*
* Revision 1.32  2004/03/02 14:42:48  perki
* breizh cola. le cola du phare ouest
*
* Revision 1.31  2004/02/25 17:36:54  perki
* *** empty log message ***
*
* Revision 1.30  2004/02/25 13:21:15  perki
* *** empty log message ***
*
* Revision 1.29  2004/02/24 13:33:48  carlito
* *** empty log message ***
*
* Revision 1.28  2004/02/19 20:00:57  perki
* The dream is coming a little bit more true
*
* Revision 1.27  2004/02/19 19:47:34  perki
* The dream is coming true
*
* Revision 1.26  2004/02/18 16:59:29  perki
* turlututu
*
* Revision 1.25  2004/02/18 13:08:40  perki
* le lion est mort ce soir
*
* Revision 1.24  2004/02/18 11:00:56  perki
* *** empty log message ***
*
* Revision 1.23  2004/02/17 18:23:02  perki
* les crocos
*
* Revision 1.22  2004/02/17 18:03:42  perki
* les crocos
*
* Revision 1.21  2004/02/17 18:00:57  perki
* les crocos
*
* Revision 1.20  2004/02/17 09:51:05  perki
* zibouw
*
* Revision 1.19  2004/02/17 09:40:06  perki
* zibouw
*
* Revision 1.18  2004/02/17 08:54:07  perki
* zibouw
*
* Revision 1.17  2004/02/17 08:50:22  perki
* zibouw
*
* Revision 1.15  2004/02/16 13:07:53  perki
* new event model
*
* Revision 1.14  2004/02/06 15:16:58  perki
* *** empty log message ***
*
* Revision 1.13  2004/02/02 11:21:05  perki
* *** empty log message ***
*
* Revision 1.12  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.11  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.10  2004/01/20 15:28:45  perki
* got milk?
*
* Revision 1.9  2004/01/20 14:30:56  perki
* Mort aux vaches..........
*
* Revision 1.8  2004/01/20 14:06:53  perki
* Et au fond du noir, le noir le plus profond une limiere ..
*
* Revision 1.7  2004/01/20 10:29:59  perki
* The Dispatcher Force be with you my son
*
* Revision 1.6  2004/01/20 08:37:10  perki
* Better WorkSheetContainer design
*
* Revision 1.5  2004/01/19 19:20:46  perki
* *** empty log message ***
*
* Revision 1.4  2004/01/19 17:04:29  perki
* pfiuuu
*
* Revision 1.3  2004/01/19 17:02:05  perki
* *** empty log message ***
*
* Revision 1.2  2004/01/19 09:45:34  perki
* WorkPlace creation done
*
* Revision 1.1  2004/01/18 18:43:41  perki
* *** empty log message ***
*
*/
