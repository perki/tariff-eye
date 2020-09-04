/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: ReducOrFixedEditor.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.calculus.ReducOrFixed;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.*;

/**
 * An Editor for Reduction or Fixed Prices
 */
public class ReducOrFixedEditor extends JPanel 
implements NamedEventListener  ,Serializable {
	WorkSheet ws;
	
	
	public static int PANEL_WIDTH = 500;
	public static int PANEL_HEIGHT = 150;
	
	public static int STATUS_W = 455;
	public static int STATUS_H = 60;
	
	public static int MONEY_VALUE_WIDTH = MoneyValueInput.DEF_W;
	public static int RATE_FIELD_W = 40;
	public static int DEF_H = 20;
	
	private int last ;
	
	private int display_mode = 0;
	
	/**
	 * That's right it takes a WorkSheet as parameter, as Reduc or Fixed
	 * have no lives without WorkSheets
	 * @param wsp
	 */
	public ReducOrFixedEditor(WorkSheetPanel wsp) {
		super();
		if (wsp == null || wsp.getWorkSheet() == null) {
			return;
		}
		
		display_mode = wsp.getEditReductionsState();
		
		this.ws = wsp.getWorkSheet();
		ws.addNamedEventListener(this);
		last = -1;
		
		refresh();
		
	}
	
	ReducOrFixed getROF() {
		return ws.getReductionOrFixed();
	}
	

	
	private void refresh() {
		removeAll();
		setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
		if (getROF() == null && last != 0) {
			cleanAll();
			createEmpty();
			last = 0;
		} else {
			if (last != 1) {
				initAll();
				createFull();
				last = 1;
			}
			load();
		}
		
		revalidate();
		repaint();
		setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
	}
	

	private String getStatusHTML() {
		return getStatusHTML(ws);
	}
	
	/**
	 * return a Status String to display informations about a Discount
	 * in HTML FORMAT
	 * @see #getStatus(WorkSheet ws)
	 */
	public static String getStatusHTML(WorkSheet ws) {
		return "<HTML><BODY>"+
		getStatus(ws).replaceAll("\n","<BR>")+
		"</HTML></BODY>";
	}
	
	
	/**
	 * return a Status String to display informations about a Discount
	 * <BR>
	 * !! Note longest phrase is : 
	 * <B>Discount rate of %0%% with a maximum 
	 * 	  fee of %2 %3 and a minimum of %1 %3.</B>
	 */
	public static String getStatus(WorkSheet ws) {
		ReducOrFixed rof = ws.getReductionOrFixed();
		if (rof == null) {
			return Lang.translate("No discount !");
		}
		
		double rate = rof.getReduRate();
		double max = rof.getMax();
		double min = rof.getMin();
		
		Object[] values = new Object[4];
		values[0] = SNumField.formatNumber(rate*100,2,true);
		values[1] = SNumField.formatNumber(min,2,true);
		values[2] = SNumField.formatNumber(max,2,true);
		values[3] = rof.getCurrency().currencyCode();
		
		
		String strFree = Lang.translate("Free !");
		String sA =Lang.translate("Discount is a fixed fee of %1 %3.",values);
		String sB =Lang.translate("Discount is null !");
		String sC =Lang.translate("Discount is a maximum fee of %2 %3.",values);
		String sD =Lang.translate("Discount is a minimum fee of %1 %3.",values);
		String sE =Lang.translate("Discount is a maximum fee of %2 %3|"+
				"and a minimum of %1 %3.",values);
		String sF = Lang.translate("Discount rate of %0%% ",values);
		String sG = Lang.translate("Discount rate of %0%% |"+
				"with a maximum fee of %2 %3.",values);
		String sH = Lang.translate("Discount rate of %0%% "+
				"with a minimum fee of %1 %3.",values);
		String sI = Lang.translate("Discount rate of %0%% |"+
				"with a maximum fee of %2 %3 "+
				"and a minimum of %1 %3.",values);
		String sZ = Lang.translate("Cannot determine this discount type");
		// 0 //
		//	Free
		if ((rate == 1 && min == 0) || (max == 0)) return strFree;
		
		// Fixed Fee
		if ( (rate == 1 && min > 0) || (min > 0 && min == max)) return sA;
		
		// 1 //
		if (rate == 0 && min == 0 && max < 0) return sB;
		if (rate == 0 && min == 0 && max > 0) return sC;
		if (rate == 0 && min > 0 && max < 0) return sD;
		if (rate == 0 && min > 0 && max > 0) return sE;
		// 2 //
		if (rate > 0 && min == 0 && max < 0) return sF;
		if (rate > 0 && min == 0 && max > 0) return sG;
		if (rate > 0 && min > 0 && max < 0) return sH;
		if (rate > 0 && min > 0 && max > 0) return sI;
		return sZ;
	}
	
	
	
	
	private void createEmpty() {
		this.setLayout(new FlowLayout());
		String s = Lang.translate("This worksheet does not accept discount");
		if (display_mode == WorkSheetPanel.WSIf.EDIT_REDUCTION_VIEW) {
			add(new JLabel(getStatusHTML()));
			return;
		}
		
		if (ws.getAcceptedReducType() == ReducOrFixed.ACCEPT_REDUC_NO) {
			add(new JLabel(s));
			return;
		}
		add(getCreationButton());
	}
	
	/**
	 * init all graphical components
	 */
	private void initAll() {
		getMinEditor();
		getMaxSelector();
		getMaxEditor();
		getRateField();
		getCCC();
	}
	
	/**
	 * clean all graphical components
	 */
	private void cleanAll() {
		minEditor = null;
		maxSelector = null;
		maxEditor = null;
		rateField = null;
		ccc = null;
	}
	
	
	private void createFull() {
		if (getROF() == null) return;
		this.setLayout(new BorderLayout());
		
		
		
		// in any case set the informations
		add(getStatusLabel(),BorderLayout.NORTH);
		
		// stop here!! if needed
		if (display_mode != WorkSheetPanel.WSIf.EDIT_REDUCTION_FULL)
			return;
		
		if (ws.getAcceptedReducType() != ReducOrFixed.ACCEPT_REDUC_NO) {
			JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
				top.add(new JLabel("Discount rate:"));
				top.add(getRateField());
				top.add(new JLabel("%"));
				top.add(new JLabel("             "));
				top.add(getDropROF());
				
			add(top,BorderLayout.CENTER);
		}
		
		if (ws.getAcceptedReducType() == ReducOrFixed.ACCEPT_REDUC_FULL) {
			JPanel cent = new JPanel(new FlowLayout(FlowLayout.LEFT));
				cent.add(new JLabel("Minimum Fee:"));
				cent.add(getMinEditor());
				cent.add(getCCC());
				cent.add(getMaxSelector());
				cent.add(getMaxEditor());
			add(cent,BorderLayout.SOUTH);
		}
		
		
	}

	/**
	 * Called on events
	 */
	public void eventOccured(NamedEvent e) {
		//Log.out("-->"+e);
		if (e.getSource() != ws) return;
		if (e.getEventCode() 
				== NamedEvent.WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE) {
			refresh();
			return;
		}
		if (e.getEventCode() == NamedEvent.WORKSHEET_DATA_MODIFIED) {
			
			load();
		}
	}
	
	
	private void load() {
		if (getROF() == null) {
			cleanAll();
			return;
		}
		initAll();
		minEditor.setText(SNumField.formatNumber(getROF().getMin()));
		rateField.setText(SNumField.formatNumber(getROF().getReduRate() * 100));
		if (getROF().getMax() < 0) {
			maxEditor.setEditable(false);
			maxEditor.setVisible(false);
			maxEditor.setText("");
			maxSelector.setSelectedIndex(1);
			revalidate();
			repaint();
		} else {
			maxEditor.setEditable(true);
			maxEditor.setVisible(true);
			maxEditor.setText(SNumField.formatNumber(getROF().getMax()));
			maxSelector.setSelectedIndex(0);
			revalidate();
			repaint();
		}
		getStatusLabel().setText(getStatusHTML());
	}
	
	//------------------------ Components Creation --------------------//
	
	private SButton creationButton;
	private JButton getCreationButton() {
		if (creationButton == null) {
			creationButton = new SButton("Add a discount to this worksheet");
			creationButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					ws.createReductionOrFixed();
				}});
		}
		return creationButton;
	}
	
	
	private JComboBox maxSelector;
	private JComboBox getMaxSelector() {
		if (maxSelector == null) {
			String[] options = new String[] {Lang.translate("Max Fee:"),
					Lang.translate("No Max!")};
			maxSelector = new JComboBox(options);
			maxSelector.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					validateSelectorState();
				}});
			
		}
		return maxSelector;
	}
	
	 void validateSelectorState() {
		if (maxSelector.getSelectedIndex() == 0) {
				if (getROF().getMax() == -1)
					getROF().setMax(getROF().getMin());
		} else {
			if (getROF().getMax() >= 0)
					getROF().setMax(-1);
		}
	}

	
	
	private SNumField maxEditor;
	private SNumField getMaxEditor() {
		if (maxEditor == null) {
			maxEditor = new SNumField(getROF().getMax()){
				
				public void stopEditing() {
					getROF().setMax(getDouble().doubleValue());
				}
				
				public void startEditing() {}};
			maxEditor.setPreferredSize(new Dimension(MONEY_VALUE_WIDTH,DEF_H));
		}
		return maxEditor;
	}
	
	
	private  SNumField minEditor;
	private  SNumField getMinEditor() {
		if (minEditor == null) {
			minEditor = new SNumField(getROF().getMin()){

				public void stopEditing() {
					getROF().setMin(getDouble().doubleValue());
				}

				public void startEditing() {}};
			minEditor.setPreferredSize(new Dimension(MONEY_VALUE_WIDTH,DEF_H));
		}
		return minEditor;
	}
	
	private CurrencyChooserCombo ccc;
	private CurrencyChooserCombo getCCC() {
		if (ccc == null) {
			ccc = new CurrencyChooserCombo(getROF().getCurrency()){

				protected void valueChanged() {
					getROF().setCurrency(getSelectedCurrency());
				}};
		}
		return ccc;
	}
	
	private JLabel statusLabel;
	private JLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new JLabel(getStatusHTML(),SwingConstants.LEFT);
		}
		statusLabel.setBorder(new TitledBorder(Lang.translate("Informations")));
		Dimension d = new Dimension(STATUS_W,STATUS_H);
		statusLabel.setSize(d);
		statusLabel.setPreferredSize(d);
		return statusLabel;
	}
	
	private SNumField rateField;
	private SNumField getRateField() {
		if (rateField == null) {
			rateField = new SNumField(getROF().getReduRate()){
				
				public void stopEditing() {
					getROF().setReduRate(getDouble().doubleValue() / 100);
				}
				
				public void startEditing() {}};
				rateField.setPreferredSize(new Dimension(RATE_FIELD_W,DEF_H));
		}
		return rateField;
	}
	
	private SButton dropROF;
	private JButton getDropROF() {
		if (dropROF == null) {
			dropROF = new SButton(Lang.translate("Clear this discount"));
			dropROF.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					ws.dropReductionOrFixed();
				}});
		}
		return dropROF;
	}
	
	
	// ----------------- MODAL_JPANEL -------------------//
	
	/**
	 * display a Modal JPanel
	 */
	public static void getPopup
		(final WorkSheetPanel wsp,Component origin,Point delta) {
		final WorkSheet ws = wsp.getWorkSheet();
		// create a JInternalFrame
		final JInternalFrame jif= new JInternalFrame(
				Lang.translate("Discount") , true, true);
		jif.setFrameIcon(Resources.reductionButton);
		JPanel jp = new JPanel(new BorderLayout());
		
		JPanel head = new JPanel(new BorderLayout());
		SButtonIcon close = new SButtonIcon(Resources.iconOK);
		close.setToolTipText(Lang.translate("OK : close"));
		close.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				jif.dispose();
			}});
		
		
		head.add(close,BorderLayout.EAST);
		
		JLabel intro = new JLabel(ws.getTitle());
		intro.setFont(intro.getFont().deriveFont(Font.BOLD));
		head.add(intro,BorderLayout.CENTER);
		
		jp.add(head,BorderLayout.NORTH);
		final ReducOrFixedEditor rofe = new ReducOrFixedEditor(wsp);
		
		jp.add(rofe,BorderLayout.SOUTH);
		
		jif.addInternalFrameListener(new InternalFrameAdapter(){
			public void internalFrameClosed(InternalFrameEvent e) {
				// if it's a null discount delete
				if (rofe.getROF() == null) return;
				if (rofe.getROF().getReduRate() == 0 && 
						rofe.getROF().getMin() == 0 &&  
						rofe.getROF().getMax() < 0) {
					ws.dropReductionOrFixed();
				}
			}});
		
		
		//	add the component to the JInternalFrame
		jif.getContentPane().add(jp);
		ModalJPanel.warpJInternalFrame(jif,origin,delta,Resources.modalBgColor);
	}
}


/*
 * $Log: ReducOrFixedEditor.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.13  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.12  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.11  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.10  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.9  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/07/22 15:12:34  carlito
 * lots of cleaning
 *
 * Revision 1.7  2004/05/23 14:08:12  perki
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/23 12:16:22  perki
 * new dicos
 *
 * Revision 1.5  2004/05/23 10:40:06  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/22 17:20:46  perki
 * Reducs are visibles
 *
 * Revision 1.3  2004/05/21 16:50:31  perki
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/21 13:19:50  perki
 * new states
 *
 * Revision 1.1  2004/05/20 17:05:56  perki
 * One step ahead
 *
 */