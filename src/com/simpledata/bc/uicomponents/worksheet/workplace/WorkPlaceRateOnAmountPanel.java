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

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.OptionManager;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateOnAmount;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionsViewer;
import com.simpledata.bc.uicomponents.tools.NamedTitleDescriptionEditor;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.*;
import com.simpledata.bc.uitools.JListWithPanels;
import com.simpledata.bc.uitools.SButton;

/**
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: WorkPlaceRateOnAmountPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
public class WorkPlaceRateOnAmountPanel extends WorkSheetPanel {
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon=
		Resources.wsWorkPlaceRateOnAmount;

	/** TITLE OF THIS WORK SHEET -- should be translated in Lang Directories **/
	public final static String WORKSHEET_TITLE= "Rate On Amount";
	
	private final static String INFO_PANEL_HELP_MESSAGE = 
	    "WorkPlaceRateOnAmountPanel:InfoPanel:HelpMessage";


	WorkPlaceRateOnAmount roa;
	OptionsViewer ov;
	JPanel infos;

	public WorkPlaceRateOnAmountPanel(
		WorkPlaceRateOnAmount roa,
		TarifViewer tv) {
		super(roa, tv);
		this.roa= roa;

		infos= new JPanel();
		ov = getStandardOptionViewer();
		
		refresh();
	}

	public void refresh() {
		infos.removeAll();
		if (! roa.isValid()) {
			infos.add(new InfoPanel());
		}
		infos.revalidate();
		infos.repaint();
	}

	/**
	* implementation of @see WorkSheetPanel#save();
	*/
	public void save() {}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getJMenu()
	 */
	public JMenu getJMenu() {
		return null;
	}

	
	public ImageIcon getTreeIcon() {
		return defaultClassTreeIcon;
	}

	class InfoPanel extends JPanel {
		private JListWithPanels jwp;
		//private JTextArea helpArea;
		private SLabel helpArea;
		
		public InfoPanel() {
			initComponents();
		}

		private void initComponents() {
			java.awt.GridBagConstraints gridBagConstraints;

//			helpArea= new JTextArea();
//			helpArea.setText(
//				"     HELP GOES HERE!\nIn a word: you need to select one more amount option in the folowing list\n  Or use the + button on the OptionViewer");

			String text = Lang.translate(INFO_PANEL_HELP_MESSAGE);
			helpArea = new SLabel(text, 
			        NamedTitleDescriptionEditor.PREF_LABEL_WIDTH);
			
			jwp= new JListWithPanels();
			jwp.setSelectionVisible(false);

			//ADD OPTIONS
			Class[] classes= roa.getAcceptedNewOptions();

			for (int i= 0; i < classes.length; i++) {
				final Class c= classes[i];
				String s= OptionManager.getOptionTitle(c);
				if (s != null) {
					JPanel jp = new JPanel();
					SButton jb = new SButton(Lang.translate(s));
					jb.setSize(200,30);
				
					jb.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
							java.awt.event.ActionEvent evt) {
							ov.createOption(c);
						}
					});
					jp.add(jb);
					jwp.addPanel(jp);
				}

			}

			//DO DISPLAY

			setLayout(new java.awt.GridBagLayout());

			gridBagConstraints= new java.awt.GridBagConstraints();
			gridBagConstraints.gridx= 0;
			gridBagConstraints.gridy= 0;
			gridBagConstraints.fill= java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.insets= new java.awt.Insets(10, 20, 10, 20);
			gridBagConstraints.weightx= 1.0;
			gridBagConstraints.weighty= 1.0;
			add(helpArea, gridBagConstraints);

			jwp.setBorder(new EtchedBorder());
			gridBagConstraints= new java.awt.GridBagConstraints();
			gridBagConstraints.gridx= 0;
			gridBagConstraints.gridy= 1;
			gridBagConstraints.fill= java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.insets= new java.awt.Insets(10, 10, 10, 10);
			gridBagConstraints.weightx= 1.0;
			gridBagConstraints.weighty= 1.5;
			add(jwp, gridBagConstraints);
		}

	}
	
	/**
	 */
	public JPanel getOptionPanel() {
		return ov;
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		return infos;
	}
}
/**
 *  $Log: WorkPlaceRateOnAmountPanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:41  perki
 *  First commit on sourceforge
 *
 *  Revision 1.13  2004/12/01 14:36:45  carlito
 *  Help upgraded, Rate on amount panel slightly modified
 *
 *  Revision 1.12  2004/09/09 18:38:46  perki
 *  Rate by slice on amount are welcome aboard
 *
 *  Revision 1.11  2004/07/26 20:36:10  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: WorkPlaceRateOnAmountPanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.10  2004/07/26 16:46:09  carlito
 *  *** empty log message ***
 *
 *  Revision 1.9  2004/05/18 15:11:25  perki
 *  Better icons management
 *
 *  Revision 1.8  2004/05/06 07:27:32  perki
 *  OptionViewer moved
 *
 *  Revision 1.7  2004/05/06 07:06:25  perki
 *  WorkSheetPanel has now two new methods
 *
 *  Revision 1.6  2004/03/23 13:39:19  perki
 *  New WorkSHeet Panel model
 *
 *  Revision 1.5  2004/03/08 17:53:18  perki
 *  *** empty log message ***
 *
 *  Revision 1.4  2004/03/08 15:40:48  perki
 *  *** empty log message ***
 *
 *  Revision 1.3  2004/03/02 14:42:48  perki
 *  breizh cola. le cola du phare ouest
 *
 *  Revision 1.2  2004/02/26 14:34:20  perki
 *  Ou alors la terre est un croissant comme la lune
 *
 *  Revision 1.1  2004/02/26 13:27:08  perki
 *  Mais la terre est carree
 *
 */