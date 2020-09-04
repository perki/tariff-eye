/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: TarifViewerConception.java,v 1.2 2007/04/02 17:04:22 perki Exp $
 */
package com.simpledata.bc.uicomponents.conception;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.actions.ActionDropTarif;
import com.simpledata.bc.components.worksheet.WorkSheetManager;
import com.simpledata.bc.components.worksheet.workplace.*;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.pair.Pairable;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uicomponents.tarif.TarifInfoViewer;
import com.simpledata.bc.uicomponents.tools.PairingDialog;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.*;
import com.simpledata.bc.uitools.SButtonIcon;

/**
 * 
 */
public class TarifViewerConception extends TarifViewer {
    
    /** Logger */
    private static Logger m_log = Logger.getLogger( TarifViewerConception.class );
    
    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;
    private static final int CURRENT_TARIF_ORIENTATION = VERTICAL;
    
    private static final String DELETE_CONFIRM = "Warning. Current tarif will " +
	"be permanently deleted.";
    private static final String REPLACE_BY_TRANSFEROPTIONS_CONFIRM = 
        "TarifViewerConception:confirmReplaceByTransferOptions";
    
	//	UI PANELS
	private JScrollPane docScrollPane;

	private JSplitPane centerTopHSplit;
	private JSplitPane centerVSplit;

    //private JPanel currentTarifControlPanel;
    
    /** Shared graphical basical components */
	private SButtonIcon deleteTarifButton;
	private SButtonIcon pairTarifButton;
	private SButtonIcon transferOptionsTarifButton;
	
	//UI TOOLS
	private WorkSheetTree wsTree;
	private TarifInfoViewer tarifInfo;
	
	// Creator used to get the control panel for 
	private Creator owner;

    
	
	public TarifViewerConception(Creator c) {
	    this.owner = c;
	}
	
	/**
	 * return true if root options should be displayed.
	 * (for example transactions and amounts)
	 * (false for edit mode, true for simu)
	 */
	public boolean showRootOptions() {
	    return owner.souldTarifViewerShowsRootOption();
	}
	
	
	public int getEditWorkPlaceState() {
		return WorkSheetPanel.WSIf.EDIT_STATE_FULL;
	}
	
	public int getEditReductionsState() {
		return WorkSheetPanel.WSIf.EDIT_REDUCTION_FULL;
	}
	
	/**
	 * return true if options can be edited
	 * (true for edit mode, false for simu)
	 */
	public int getEditOptionState() {
		return OptionDefaultPanel.EditStates.FULL;
	}
	
	/**
	* refresh the display (all components)
	*/
	public void refresh() {

		//	get a WorkSheet Explorer and add it to the DocPanel

		getWorkSheetPanel(workSheetATW).refreshRecursive();
		changeDocContent(getWorkSheetPanel(workSheetATW).getPanel());
		if (workSheetATW != null) {
			if (wsTree.getSTree() != null) {
				wsTree.getSTree().repaint();
			}
		}
		wsTree.refresh();
		tarifInfo.refresh();
		updateButtonsStatus();
	}
	
	/**
	 * This tarifViewer accepts depthOfView
	 */
	public boolean isDepthOfViewEnabled() {
        return true;
    }
    
	/**
	* force the change of the Content Panel
	*/
	private void changeDocContent(Component jp) {
		JViewport vp= new JViewport();
		vp.setView(jp);
		docScrollPane.setViewport(vp);
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	protected void initComponents() {
		// init tools
		tarifInfo= new TarifInfoViewer(this);
		wsTree= new WorkSheetTree(this);

		// layout
		setLayout(new BorderLayout());

		JPanel bottomP= new JPanel(new BorderLayout());

		centerVSplit= new JSplitPane();
		centerTopHSplit= new JSplitPane();

		JPanel wsPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		wsPanel.add(buildCurrentTarifControlPanel(), gbc);
		
		gbc.gridx++;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		
		wsTree.setBorder(new EtchedBorder());
		wsTree.setMaximumSize(new Dimension(150, 120));
		wsTree.setMinimumSize(new Dimension(150, 120));
		wsTree.setPreferredSize(new Dimension(150, 120));

		wsPanel.add(wsTree, gbc);
		
		centerTopHSplit.setLeftComponent(wsPanel);
		centerTopHSplit.setRightComponent(tarifInfo);
		centerTopHSplit.setResizeWeight(0.5);
		centerTopHSplit.setDividerLocation(0.4);
		

		docScrollPane= new JScrollPane();
		docScrollPane.setBorder(null);
		bottomP.add(docScrollPane, BorderLayout.CENTER);

		centerVSplit.setBorder(new EtchedBorder());
		centerVSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		centerTopHSplit.setBorder(null);
		centerVSplit.setTopComponent(centerTopHSplit);
		centerVSplit.setBottomComponent(bottomP);

		add(centerVSplit, BorderLayout.CENTER);

		setVisible(true);
	}
	
    /** We build the control panel for currently edited tarif
     * Therefore it will contain three buttons (delete, pair and transfer)
     */
    private JPanel buildCurrentTarifControlPanel() {
        JPanel currentTarifControlPanel = new JPanel(new GridBagLayout());
        
        boolean vert = (CURRENT_TARIF_ORIENTATION == VERTICAL);
        
        GridBagConstraints constra = new GridBagConstraints();
        constra.gridx = 0;
        constra.gridy = 0;
        constra.anchor = GridBagConstraints.CENTER;
        if (vert) {
            constra.insets = new Insets(1,1,0,1);
        } else {
            constra.insets = new Insets(1,1,1,0);
        }
        
        // delete button
        deleteTarifButton= ActionDropTarif.createButton(this,
		        new ActionDropTarif.Interface(){
            public Tarif actionDropTarif_getTarifToDrop() {
                return owner.selectedTarif();
            }
            public void actionDropTarif_tarifDroped(Tarif t) {
               owner.cleanUI();
            }}

);
        
        currentTarifControlPanel.add(deleteTarifButton, constra);

        // Adding a small space
        if (vert) {
            constra.gridy++;
            constra.insets = new Insets(1,1,10,1);
        } else {
            constra.gridx++;
            constra.insets = new Insets(1,1,1,10);
        }       
        
        currentTarifControlPanel.add(new JLabel(), constra);
        
        // pair button
        if (vert) {
            constra.gridy++;
            constra.insets = new Insets(1,1,0,1);
        } else {
            constra.gridx++;
            constra.insets = new Insets(1,1,1,0);
        }
        
    		pairTarifButton = new SButtonIcon(Resources.stdTagPaired);
		BC.langManager.registerTooltip(pairTarifButton,
				"Access to pairing properties of this Tarif");
		pairTarifButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pairTarifButtonActionPerformed(pairTarifButton);
			}
		});
		currentTarifControlPanel.add(pairTarifButton, constra);
        
        // transfer button
        if (vert) {
            constra.gridy++;
        } else {
            constra.gridx++;
        }
        constra.insets = new Insets(1,1,1,1);

        transferOptionsTarifButton = new SButtonIcon(Resources.iconTransferOptions);
        BC.langManager.registerTooltip(transferOptionsTarifButton, 
                "Transfer options to upper tarif");
        transferOptionsTarifButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			    transferOptionsTarifButtonActionPerformed();
			}
		});
        currentTarifControlPanel.add(transferOptionsTarifButton, constra);
        
        return currentTarifControlPanel;
    }
	
	
	
	/**
	 * Pair tarif action performed
	 */
	private void pairTarifButtonActionPerformed(Component origin) {
		if (owner.selectedTarif() != null && (owner.selectedTarif() instanceof Pairable)) {
			
			PairingDialog.showPairingDialog(
					(Pairable) owner.selectedTarif(),origin,
					new PairingDialog.Controler(){

						public void jumpTo(Tarif t) {
						    owner.setCurrentWorkSheet(t.getWorkSheet());
						}});
		}
	}
	
	/**  Transfer selected tarif's options to upper tarif */
    private void transferOptionsTarifButtonActionPerformed() {
        Tarif t = owner.selectedTarif();
        if (t != null) {
            WorkSheet ws = t.getWorkSheet();
            if (ws == null) return;
            	if (ws instanceof WorkSheetContainer) {
            	    if (ModalDialogBox.confirm(this, 
            	            Lang.translate(REPLACE_BY_TRANSFEROPTIONS_CONFIRM)) == 
            	                ModalDialogBox.ANS_OK) {
            	    WorkSheetManager.createWorkSheet((WorkSheetContainer)ws, 
            	            WorkPlaceTransferOptions.class, "");
            	    }
            	}
        } else {
            m_log.error("Starting tranferOptions on a null tariff");
        }
    }
    
    
    protected void updateButtonsStatus() {
		// Here do the button enabling gestion...
		if (owner.selectedTarif() == null) {
			deleteTarifButton.setEnabled(false);
			pairTarifButton.setEnabled(false);
			transferOptionsTarifButton.setEnabled(false);
		} else {
			pairTarifButton.setEnabled((owner.selectedTarif() instanceof Pairable));
			deleteTarifButton.setEnabled(true);
			
			// Examining the transferoptions possibility
			transferOptionsTarifButton.setEnabled(false);
			WorkSheet ws = owner.selectedTarif().getWorkSheet();
			if (ws instanceof WorkSheetContainer) {
			    if (((WorkSheetContainer)ws).
			            acceptsNewWorkSheet(WorkPlaceTransferOptions.class, "")) {
			        // TODO if acceptsNewWorkSheet is revised
			        // take in account that :
			        // - here we want to know if ws WITHOUT children workSheet
			        //   would accept WorkPlaceTransferOption
			        transferOptionsTarifButton.setEnabled(true);
			    }
			}
			
		}
    }


}

/*
 * $Log: TarifViewerConception.java,v $
 * Revision 1.2  2007/04/02 17:04:22  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:34  perki
 * First commit on sourceforge
 *
 * Revision 1.22  2004/11/08 13:45:29  carlito
 * New pairing nodes color policy
 *
 * Revision 1.21  2004/10/20 08:19:39  perki
 * *** empty log message ***
 *
 * Revision 1.20  2004/09/23 09:23:41  carlito
 * WorkSheets hidden in popup
TransferOption button activated..
 *
 * Revision 1.19  2004/09/14 15:52:58  carlito
 * *** empty log message ***
 *
 * Revision 1.18  2004/09/14 12:07:26  carlito
 * commit de protection
 *
 * Revision 1.17  2004/09/13 15:27:31  carlito
 * *** empty log message ***
 *
 * Revision 1.16  2004/09/10 14:48:50  perki
 * Welcome Futures......
 *
 * Revision 1.15  2004/09/09 17:27:44  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/09/09 17:24:08  carlito
 * Creator Gold and Light are there for their first breathe
 *
 * Revision 1.13  2004/07/08 12:02:32  kaspar
 * * Documentation changes, Added some debug code into
 *   the main view of the creator
 *
 * Revision 1.12  2004/05/31 15:02:59  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/31 07:19:47  perki
 * Enable and disable
 *
 * Revision 1.10  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/05/23 12:16:22  perki
 * new dicos
 *
 * Revision 1.8  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.7  2004/05/22 18:33:22  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/22 17:30:20  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.4  2004/05/07 17:22:37  perki
 * installer ok
 *
 * Revision 1.3  2004/05/06 07:06:25  perki
 * WorkSheetPanel has now two new methods
 *
 * Revision 1.2  2004/05/05 15:00:44  perki
 * TarifViewer modifications
 *
 * Revision 1.1  2004/05/05 14:55:07  perki
 * TarifViewer modifications
 *
 */