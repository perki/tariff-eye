/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
 * $Id: CopyItem.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents;

import javax.swing.ImageIcon;

import com.simpledata.bc.datamodel.Copiable;

/**
 * A class to keep Copy item 
 */
public class CopyItem {
	public Copiable copiable;
	public String title;
	public ImageIcon icon;
	private TarifViewer owner;
	public boolean deleted;
	
	CopyItem(TarifViewer owner, Copiable copiable, 
			String title, ImageIcon icon,boolean deleted) {
		this.copiable = copiable;
		this.title = title;
		this.icon = icon;
		this.owner = owner;
		this.deleted = deleted;
	}	
	
	public void drop() {
		owner.removeCopyItem(this);
	}
}

/**
 *  $Log: CopyItem.java,v $
 *  Revision 1.2  2007/04/02 17:04:24  perki
 *  changed copyright dates
 *
 *  Revision 1.1  2006/12/03 12:48:37  perki
 *  First commit on sourceforge
 *
 *  Revision 1.2  2004/05/14 08:46:18  perki
 *  *** empty log message ***
 *
 *  Revision 1.1  2004/03/04 14:37:34  perki
 *  copy goes to hollywood
 *
 */