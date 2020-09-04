/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 17 mars 2004
 * $Id: SimuTarifViewer.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
package com.simpledata.bc.uicomponents.simulation;

import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.*;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.SLabel;

/**
 * 
 */
public class SimuTarifViewer extends TarifViewer {
	
    private JScrollPane docScrollPane;
  
	private TitlePanel titlePanel;
	private PreResultsPanel resultPanel;
	private JSplitPane splitV;
	
	private JLabel noSelectionLabel;
	
    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.TarifViewer#initComponents()
     */
    protected void initComponents() {

        this.titlePanel = new TitlePanel("Make your choice","Make your choice clicking on the left tree");
        this.resultPanel = new PreResultsPanel();
        
		// layout
		setLayout(new BorderLayout());

		add(this.titlePanel, BorderLayout.NORTH);
		
		this.splitV = new JSplitPane();
		this.splitV.setOrientation(JSplitPane.VERTICAL_SPLIT);
		

		docScrollPane= new JScrollPane();
		docScrollPane.setBorder(null);

		//JLabel jb = new JLabel("<html><body><b>Simu Tarif Viewer</b><br>ZOBI<br>ZOBA</body></html>");
		noSelectionLabel = new JLabel(new ImageIcon(Resources.splashImagePath()));
		
		/*
		Dimension d = new Dimension(200, 350);
		jb.setPreferredSize(d);
		jb.setMinimumSize(d);
		*/
		
		docScrollPane.setViewportView(noSelectionLabel);
		
		this.splitV.setTopComponent(docScrollPane);
		
		this.splitV.setBottomComponent(this.resultPanel);

		//bottomP.add(docScrollPane, BorderLayout.CENTER);

		//this.splitV.setDividerLocation(0.7);
		//this.splitV.setResizeWeight(0.7);
		
		// For demo purpose --> TODO rollback (see above)
		this.splitV.remove(2);
		this.splitV.setDividerLocation(1.0);
		this.splitV.setDividerSize(0);
		this.splitV.setResizeWeight(1.0);
		
		//add(resultPanel, BorderLayout.CENTER);
		add(this.splitV, BorderLayout.CENTER);

		setVisible(true);
        
    }

	/**
	* force the change of the Content Panel
	*/
	private void changeDocContent(Component jp) {
	    JViewport vp= new JViewport();
	    if (jp == null) {
	        vp.setView(noSelectionLabel);
	    } else {	        
	        vp.setView(jp);
	    }
	    docScrollPane.setViewport(vp);
	}
    
    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.TarifViewer#refresh()
     */
    public void refresh() {
        WorkSheet atWork = getWorkSheetAtWork();
        if (atWork == null) {
            changeDocContent(null);
        } else {
            getWorkSheetPanel(atWork).refreshRecursive();
            changeDocContent(getWorkSheetPanel(atWork).getPanel());
            this.resultPanel.setWorkSheet(atWork);
            this.titlePanel.setWorkSheet(atWork);
            revalidate();
            repaint();
        }
    }

    /* (non-Javadoc)
     * @see com.simpledata.bc.uicomponents.TarifViewer#showRootOptions()
     */
    public boolean showRootOptions() {
        return true;
    }

    public int getEditWorkPlaceState() {
		return WorkSheetPanel.WSIf.EDIT_STATE_NONE;
	}
    
    public int getEditReductionsState() {
		return WorkSheetPanel.WSIf.EDIT_REDUCTION_FULL;
	}
	
	/**
	 * return true if options can be edited
	 * (true for edit mode, false for simu)
	 */
	public int getEditOptionState() {
		return OptionDefaultPanel.EditStates.VALUE;
	}

	/**
	 * This tarifViewer does not accept depth of view
	 */
	public boolean isDepthOfViewEnabled() {
        return false;
    }
	
}

class TitlePanel extends JPanel {
 
    private JLabel titleLabel;
    private SLabel descriptionLabel;
    
    private String title;
    private String description;
    
    public TitlePanel(String title, String description) {
        super();
        this.title = title;
        this.description = description;
        
        buildComponents();
        
        refresh();
    }
    
    private void buildComponents() {
        this.setLayout(new GridBagLayout());
        
        titleLabel = new JLabel();
        Font f = titleLabel.getFont();
        titleLabel.setFont(f.deriveFont(Font.BOLD, f.getSize() + 2));
        
        descriptionLabel = new SLabel();
        descriptionLabel.setForeground(Color.DARK_GRAY);

    }
    
    public void setWorkSheet(WorkSheet ws) {
        if (ws == null) {
            clear();
            return;
        }
        Tarif t = ws.getTarif();
        if (t != null) {
            title = t.getTitle();
            description = t.getDescription();
            refresh();
        }
    }
    
    
    private void clear() {
        title = Lang.translate("Make your choice");
        description = Lang.translate("Make your choice clicking on the left tree");
        refresh();
    }
    
    private void refresh() {
        this.removeAll();
        
        GridBagConstraints cons = new GridBagConstraints();
        cons.anchor = GridBagConstraints.NORTHWEST;
        cons.gridy = 0;
        cons.weightx = 1.0;
        cons.fill = GridBagConstraints.HORIZONTAL;
        cons.insets = new Insets(3,5,3,5);
        
        if (title != null) {            
            titleLabel.setText(title);
            add(titleLabel, cons);
            cons.gridy ++;
        }
        if (description != null) {
            descriptionLabel.setText(description);
            add(descriptionLabel, cons);
        }
    
        revalidate();
        repaint();
    }
}

/*
 * $Log: SimuTarifViewer.java,v $
 * Revision 1.2  2007/04/02 17:04:26  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:40  perki
 * First commit on sourceforge
 *
 * Revision 1.16  2004/10/15 17:50:04  carlito
 * SLabel
 *
 * Revision 1.15  2004/09/22 15:39:55  carlito
 * Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
 *
 * Revision 1.14  2004/09/13 15:27:31  carlito
 * *** empty log message ***
 *
 * Revision 1.13  2004/06/28 19:25:41  carlito
 * *** empty log message ***
 *
 * Revision 1.12  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/22 18:33:22  perki
 * *** empty log message ***
 *
 * Revision 1.10  2004/05/22 17:30:20  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/05/22 08:39:36  perki
 * Lot of cleaning
 *
 * Revision 1.8  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.7  2004/05/18 19:09:47  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/11 17:57:01  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/10 17:43:54  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/06 07:06:25  perki
 * WorkSheetPanel has now two new methods
 *
 * Revision 1.3  2004/05/05 16:52:29  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/03/22 14:32:30  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/03/17 17:11:20  carlito
 * *** empty log message ***
 *
 */