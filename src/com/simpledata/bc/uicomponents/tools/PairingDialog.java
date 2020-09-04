/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: PairingDialog.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.tools;

import java.awt.*;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.pair.Pairable;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SButton;

/**
 * This dialog permit to Edit the Pairing properties of a 
 * Tarif
 */
public class PairingDialog {
	
	/** show Pairing Dialog
	 * @param tarif the Tarif
	 * @param c (optional) an interface for the JumpTo button
	 * @param origin the Component on which the Dialog will be printed
	 * **/
	public static void 
	showPairingDialog(final Pairable tarif,Component origin,final Controler c) {
		assert tarif != null;
		assert origin != null;
		
		//		 LOGIC AND DATA
		
		int ACTION_NONE = 0;
		int ACTION_CREATE = 1;
		int ACTION_BREAK = 2;
		
		int action = ACTION_NONE;
		Tarif jump = tarif.pairedGet();
		if (jump == null) jump = tarif.pairedGetProposition();
		
		// UI
		
		final JPanel contents = new JPanel(new BorderLayout());
		JLabel infosLabel = new JLabel();
		contents.add(infosLabel,BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));
		contents.add(bottom,BorderLayout.SOUTH);
		
		
		
		
		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
		JButton ok = new JButton(Lang.translate("OK"));
		JButton no = new JButton(Lang.translate("Cancel"));
		buttonPanel.add(ok);
		buttonPanel.add(no);
		
		//JUMP button
		SButton jumpButton = new SButton();
		if (jump != null) {
			JPanel jumpTo = new JPanel(new FlowLayout(FlowLayout.TRAILING,2,2));
			JLabel jumpNodeName = new JLabel(jump.getTitle());
			jumpTo.add(jumpNodeName);
			if (c != null) {
				jumpButton.setText(Lang.translate("Jump To"));
				jumpTo.add(jumpButton);
			}
			bottom.add(jumpTo);
		}
		bottom.add(buttonPanel);
		
		
		String infos = "<HTML><CENTER>" +
				"<B>"+Lang.translate("Pairing Infos")+"</B></CENTER><HR>";
		
		// STATUS DETECTION
		
		switch (tarif.pairedCanBe()) {
			
			case Pairable.CAN_BE_NOK_ALREADY_PAIRED:
				infos += Lang.translate("This tarif is paired with:");
				action = ACTION_BREAK;
				ok.setText(Lang.translate("Break Pairing"));
			break;
			case Pairable.CAN_BE_NOK_NO_PAIR_POSITION:
				infos += Lang.translate("This tarif cannot be paired there is no" 
						+" no equivalent position in pair tree");
			break;
			case Pairable.CAN_BE_NOK_PROPOSITION_NOT_VALID:
				infos += Lang.translate("You cannot pair it because a "
						+"problem has been detected on tarif:");
			break;
			case Pairable.CAN_BE_OK_ATTACHED:
				infos += Lang.translate("Do you want to pair this"+
				" Tarif with :");
				action = ACTION_CREATE;
			break;
			case Pairable.CAN_BE_OK_CREATE:
				infos += Lang.translate("Do you want to create a new "+
				"paired Tarif ?");
				action = ACTION_CREATE;
			break;
			default:
				infos += Lang.translate("Something wrong happend");
			break;
		}
		
		
		infosLabel.setText(infos+"</HTML>");
		
		
		// CREATE THE PANEL
		
		final ModalJPanel mjp = ModalJPanel.createSimpleModalJInternalFrame(
				contents,origin,new Point(-10,-60),
				true,Resources.iconEdit,null);

		
		
		// add listeners after because the need a reference on mjp;
		
		if (jump != null) {
			final Tarif myJump = jump;
			jumpButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					c.jumpTo(myJump);
					mjp.close();
				}});
		}
		
		if (action == ACTION_CREATE) {
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tarif.pairedCreate();
					mjp.close();
				}
			});
		} 
		if (action == ACTION_BREAK) {
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					tarif.pairedBreak();
					mjp.close();
				}
			});
		}
		if (action == ACTION_NONE) {
			ok.setEnabled(false);
		}
		no.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mjp.close();
			}
		});
		
	}

	
	public interface Controler {
		public void jumpTo(Tarif t);
	}
	
}

/*
 * $Log: PairingDialog.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.4  2004/08/01 14:15:26  perki
 * introducing rollout
 *
 * Revision 1.3  2004/08/01 12:23:08  perki
 * Better show/hide extra parameter
 *
 * Revision 1.2  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.1  2004/07/31 16:45:57  perki
 * Pairing step1
 *
 */