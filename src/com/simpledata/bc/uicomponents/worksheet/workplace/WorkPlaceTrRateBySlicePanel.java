/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * @author Simpledata SARL, 2004, all rights reserved. 
 * @version $Id: WorkPlaceTrRateBySlicePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $
 */
 
/**
 * Package that contains all the different panels for displaying
 * any given Workplace. 
 */
package com.simpledata.bc.uicomponents.worksheet.workplace;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTrRateBySlice;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.AbstractBySlicePanel;
import com.simpledata.bc.uicomponents.worksheet.workplace.tools.RateBySlicePanel;
import com.simpledata.bc.uitools.ImageTools;

/**
 * A work place for transaction by slice. 
 */
public class WorkPlaceTrRateBySlicePanel 
	extends WorkSheetPanel 
	implements AbstractBySlicePanel.RBSPListener 
{
    /** log4j Logger */
    private static final Logger m_log = Logger.getLogger(
            WorkPlaceTrRateBySlicePanel.class);
    
	/** my Icon **/
	public static ImageIcon defaultClassTreeIcon 
		= Resources.wsWorkPlaceTrRateBySlice;
	
	// Locale
	private static final String EACH_TRANSATION = 
		"Rate slice computed for each transation";
	private static final String TOTAL_VOLUME =
		"Rate slice computed for the total transaction volume";
	
	WorkPlaceTrRateBySlice wptrbs;
	private RateBySlicePanel rbsp = null;
	
	private JPanel jp;
	private JComboBox applyChooser;
	
	private int stateMemory;
		
	public WorkPlaceTrRateBySlicePanel(WorkPlaceTrRateBySlice wptrbs, 
			TarifViewer tv) {
		super(wptrbs, tv);
		this.wptrbs = wptrbs;
		
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		JPanel paramPanel = new JPanel(new GridLayout(2,1));
		paramPanel.add(getApplyChooser());
		paramPanel.add(getMethodChooser());
		
		jp = new JPanel();
		jp.setLayout(new BorderLayout(0,0));
		jp.add(paramPanel, BorderLayout.NORTH);
        jp.add(getRbsp(), BorderLayout.CENTER); 
        
        stateMemory = -1;
        refresh();
			
	}
	
	/**
	 * return the applies on Combo
	 */
	private JComboBox getApplyChooser() {
		if (applyChooser == null) {
			ApplyOption[] aos = new ApplyOption[3];
			aos[0] = new ApplyOption(
					WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_AND_OUTGOING);
			aos[1] = new ApplyOption(
					WorkPlaceTrRateBySlice.APPLY_ON_OUTGOING_FROM_BANK);
			aos[2] = new ApplyOption(
					WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_TO_BANK);
			
			applyChooser = new JComboBox(aos);
			applyChooser.setRenderer(new CBR());
			applyChooser.setSelectedItem(new ApplyOption(wptrbs.getApplyOn()));
			
			applyChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					save();
				}});
			
			
		}
		return applyChooser;
	}
	
	/**
	 * return the Combo box for the calculus method selection
	 */
	private JComboBox getMethodChooser() {
		String[] options = {
				Lang.translate(EACH_TRANSATION),
				Lang.translate(TOTAL_VOLUME)
		};
		
		JComboBox result = new JComboBox(options);
		int selectIndex = wptrbs.getCalculusMethod();
		result.setSelectedIndex(selectIndex);
		
		result.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) {    
			    JComboBox source = (JComboBox)e.getSource();
			    int newIndex = source.getSelectedIndex();			    
				wptrbs.setCalculusMethod(newIndex);
			}
		});
		
		return result;
	}
	
	//class CBR extends BasicComboBoxRenderer {
	class CBR implements ListCellRenderer {
	    
	    public Component getListCellRendererComponent(
	            JList list,
	            Object value,
	            int index,
	            boolean isSelected,
	            boolean cellHasFocus) {
	        
	        ApplyOption ao = (ApplyOption) value;
	        
	        JPanel jp = (ao).getComponent();
	        
	        //m_log.error("Demande de rendering : "+ao.toString());
	        
	        if (isSelected) {
	            jp.setBackground(list.getSelectionBackground());
	            jp.setForeground(list.getSelectionForeground());
	        } else {
	            jp.setBackground(list.getBackground());
	            jp.setForeground(list.getForeground());
	        }
	        return jp;
	    }
	    
	    
	}
	
	/** A class that encapsulates the Drop Down select listbox behaviour. 
	 */
	class ApplyOption {
		public int applyType;
		JPanel jp ;
		
		public JPanel getComponent() {
			return jp;
		}
		
		public ApplyOption(int applyType) {
			this.applyType = applyType;
			String txt = Lang.translate("Unkown type");
			switch (applyType) {
				case WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_AND_OUTGOING:
					txt =  Lang.translate("Applies on all transactions");
				break;
				case WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_TO_BANK:
					txt =  Lang.translate("Applies on transactions to bank");
				break;
				case WorkPlaceTrRateBySlice.APPLY_ON_OUTGOING_FROM_BANK:
					txt =  Lang.translate("Applies on transactions from bank");
				break;
			}
			
			
			final JLabel jl = new JLabel(txt, getIconFor(applyType), SwingConstants.LEFT);
			jp = new JPanel(new BorderLayout(0,0)) {
			    	
			    public void setBackground(Color col) {
			        super.setBackground(col);
			        if (jl != null)
			            jl.setBackground(col);
			    }
			    
			    public void setForeground(Color col) {
			        super.setForeground(col);
			        if (jl != null)
			            jl.setForeground(col);
			    }
			    
			};
			jp.add(jl,BorderLayout.CENTER);
			
		}
		
		public boolean equals(Object o) {
			if (! (o instanceof ApplyOption)) return false;
			return ((ApplyOption) o).applyType == applyType;
		}
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#save()
	 */
	public void save() {
		wptrbs.setApplyOn(
			((ApplyOption) applyChooser.getSelectedItem()).applyType
		);
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {
		applyChooser.setSelectedItem(new ApplyOption(wptrbs.getApplyOn()));
		
		if (stateMemory == getDisplayController().getEditWorkPlaceState()) return;
		stateMemory = getDisplayController().getEditWorkPlaceState();
		
		switch (stateMemory) {
			case WSIf.EDIT_STATE_NONE:
				getRbsp().setEditable(false);
				getApplyChooser().setEnabled(false);
				break;
			default:
				getRbsp().setEditable(true);
				getApplyChooser().setEnabled(true);
				break;
			
		}
		
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private RateBySlicePanel getRbsp() {
		if(rbsp == null) {
			rbsp = new RateBySlicePanel(wptrbs.getRbs(),this);
			rbsp.setMinimumSize(new Dimension(0,50));
			rbsp.setPreferredSize(new Dimension(100,50)); 
		}
		return rbsp;
	}
	
	
	public ImageIcon getTreeIcon() {
		return ImageTools.drawIconOnIcon(
				defaultClassTreeIcon,
				getIconFor(wptrbs.getApplyOn()),
				new Point(0,0)
				);
	}

	/**
	 */
	public JPanel getOptionPanel() {
		return null;
	}
	
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		return jp;
	}
	
	/**
	 * interface to RateBySlicePanel.RBSPListener <BR>
	 * called when data is modified on the panel
	 */
	public void rbsDataChanged() {
		wptrbs.optionDataChanged(null,null);
	}
	
	/**
	 * get the type icon
	 */
	public static ImageIcon getIconFor(int applyType) {
		switch (applyType) {
			case WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_AND_OUTGOING:
				return Resources.arrowBoth;
			case WorkPlaceTrRateBySlice.APPLY_ON_INCOMING_TO_BANK:
				return Resources.arrowRight;
			case WorkPlaceTrRateBySlice.APPLY_ON_OUTGOING_FROM_BANK:
				return Resources.arrowLeft;
		}
		return null;
	}
}


/**
 *  $Log: WorkPlaceTrRateBySlicePanel.java,v $
 *  Revision 1.2  2007/04/02 17:04:26  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:41  perki
 *  First commit on sourceforge
 *
 *  Revision 1.25  2004/10/19 17:02:32  perki
 *  pfiuuu
 *
 *  Revision 1.24  2004/10/18 16:48:10  carlito
 *  JComboBox bug corrected
 *
 *  Revision 1.23  2004/10/11 10:19:16  perki
 *  Percentage on Transactions
 *
 *  Revision 1.22  2004/09/16 09:55:50  jvaucher
 *  Introduced the total volume calculation for the transactions rateBySlice workplace.
 *
 *  Revision 1.21  2004/09/14 13:22:30  perki
 *  *** empty log message ***
 *
 *  Revision 1.20  2004/09/07 13:35:03  carlito
 *  *** empty log message ***
 *
 *  Revision 1.19  2004/07/26 20:36:10  kaspar
 *  + trRateBySlice subreport that shows for all
 *    RateBySlice Workplaces. First Workplace subreport.
 *  + Code comments in a lot of classes. Beautifying, moving
 *    of $Id: WorkPlaceTrRateBySlicePanel.java,v 1.2 2007/04/02 17:04:26 perki Exp $ tag.
 *  + Long promised caching of reports, plus some rudimentary
 *    progress tracking.
 *
 *  Revision 1.18  2004/05/31 15:02:59  perki
 *  *** empty log message ***
 *
 */