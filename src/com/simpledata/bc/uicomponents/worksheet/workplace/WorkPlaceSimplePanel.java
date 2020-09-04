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

import javax.swing.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceSimple;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.SButton;

/**
 * WorkPlace default UI
 * @version $Id: WorkPlaceSimplePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 * @see com.simpledata.bc.components.worksheet.workplace.WorkPlaceSimple
 */
public class WorkPlaceSimplePanel extends WorkSheetPanel {
	
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon 
		= Resources.wsWorkPlaceWithOptions;
		
		private JPanel mjp;
			
	JTextArea textArea;
	JScrollPane scrollPane;
	
	WorkPlaceSimple wps;
	
	protected JLabel status = null;
	
	private ImageIcon iconOK ;
	private ImageIcon iconNOK ;
	
	public WorkPlaceSimplePanel(WorkPlaceSimple wps,TarifViewer tv) {
		super(wps,tv);
		this.wps = wps;
		
		iconOK = Resources.greenBall;
		iconNOK = Resources.redBall;
		
		// --------- UI --------
		JPanel contents = new JPanel(new BorderLayout(0,0));
		scrollPane = new JScrollPane();
        textArea = new JTextArea();
    
        textArea.setMinimumSize(new Dimension(0, 10));
        textArea.setPreferredSize(new Dimension(0, 100));
        
        textArea.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				save();
			}
			public void focusGained(FocusEvent e) {
				setStatus(false);
			}
		});
        
        
        scrollPane.setViewportView(textArea);
        
     
		contents.add(scrollPane, java.awt.BorderLayout.CENTER);
		
		
		// save Panel
		JPanel jp = new JPanel();
       	jp.setLayout(new FlowLayout(FlowLayout.LEFT));
       	
       	jp.add(getStatus()); // staus (red / green light)
      	
      	SButton jb = new SButton();
      	jb.setSize(20,20);
      	jb.setIcon(Resources.iconSave);
       	jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				save();
			}
		});
       	jp.add(jb);
       	
       	contents.add(jp,BorderLayout.SOUTH);
	}
	
	
	public void refresh() {
		// load data into UI
		textArea.setText(wps.getMyScript());
	}
	
	/**
	 * This method initializes the status label
	 * 
	 */
	protected JLabel getStatus() {
		if (status == null) {
			status = new JLabel("");
			status.setIcon(iconOK);
			status.setSize(20,20);
		}
		return status;
	}

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
	* implementation of @see WorkSheetPanel#save();
	*/
	public void save() {
		setStatus(true);
		wps.setMyScript(textArea.getText());
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getJMenu()
	 */
	public JMenu getJMenu() {
		return null;
	}

	
	
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}
	
	/**
	 */
	public JPanel getOptionPanel() {
		return super.getStandardOptionViewer();
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		if (mjp == null) mjp = new JPanel();
		return mjp;
	}
}
/*
* $Log: WorkPlaceSimplePanel.java,v $
* Revision 1.2  2007/04/02 17:04:26  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:41  perki
* First commit on sourceforge
*
* Revision 1.19  2004/07/26 20:36:10  kaspar
* + trRateBySlice subreport that shows for all
*   RateBySlice Workplaces. First Workplace subreport.
* + Code comments in a lot of classes. Beautifying, moving
*   of $Id: WorkPlaceSimplePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
* + Long promised caching of reports, plus some rudimentary
*   progress tracking.
*
* Revision 1.18  2004/07/26 16:46:09  carlito
* *** empty log message ***
*
* Revision 1.17  2004/07/04 10:57:45  perki
* *** empty log message ***
*
* Revision 1.16  2004/05/06 07:06:25  perki
* WorkSheetPanel has now two new methods
*
* Revision 1.15  2004/03/23 13:39:19  perki
* New WorkSHeet Panel model
*
* Revision 1.14  2004/03/12 17:48:06  perki
* Monitoring file loading
*
* Revision 1.13  2004/03/08 15:40:48  perki
* *** empty log message ***
*
* Revision 1.12  2004/03/08 09:02:20  perki
* houba houba hop
*
* Revision 1.11  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.10  2004/02/19 19:47:34  perki
* The dream is coming true
*
* Revision 1.9  2004/02/19 16:21:25  perki
* Tango Bravo
*
* Revision 1.8  2004/02/17 15:55:02  perki
* zobi la mouche n'a pas de bouche
*
* Revision 1.7  2004/02/17 08:54:07  perki
* zibouw
*
* Revision 1.6  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.5  2004/02/16 13:07:53  perki
* new event model
*
* Revision 1.4  2004/02/06 08:05:41  perki
* lot of cleaning in UIs
*
* Revision 1.3  2004/02/06 07:44:55  perki
* lot of cleaning in UIs
*
* Revision 1.2  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.1  2004/01/20 16:35:33  perki
* A l'aube le soleil ne se couche jamais
*
*/
