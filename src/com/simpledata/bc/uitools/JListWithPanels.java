/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/**
* $Id: JListWithPanels.java,v 1.2 2007/04/02 17:04:26 perki Exp $
*/
package com.simpledata.bc.uitools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

import com.simpledata.bc.uicomponents.tools.ActionEventHandler;

/**
* A JList that accepts JPanels instead of Strings
*/
public class JListWithPanels extends JScrollPane {

	private JPanel dataView;

	private JPanel pSelected;

	public static Border DEFAULT_BORDER= new JLWPBorder(false);

	public static Border SELECTED_BORDER= new JLWPBorder(true);
	private static JListWithPanels last; //for debugging

	private boolean selectionVisible= true;
	
	/** focus an element when added to the list **/
	private boolean focusOnAdd;

	/**
	* Note! you can still manipulate the passed ArrayList and call refresh().. 
	* the data list will be refreshed!
	*/
	public JListWithPanels() {
		super();
		last= this;
		dataView= new JPanel();
		selectionVisible= true;
		dataView.setLayout(new BoxLayout(dataView, BoxLayout.Y_AXIS));

		JPanel dummy0= new JPanel();
		dummy0.setLayout(new BorderLayout());
		dummy0.add(dataView, BorderLayout.NORTH);
		dummy0.add(new JPanel(), BorderLayout.CENTER);

		getViewport().add(dummy0);
		
		focusOnAdd = true;
	}

	/**
	* Add a JPanel to the List
	* @param index -1 add at the end
	*/
	public void addPanel(final JPanel jp, int index) {
		if (index < 0 || index > dataView.getComponentCount()) {
			index= -1;
		}
		// add listeners
		jp.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				clicked(jp);
			}
		});
		addFocusListener(jp, jp);
		
		
		jp.setBorder(DEFAULT_BORDER);
		dataView.add(jp,index);
		refresh();
		if (focusOnAdd)
		    clicked(jp);
	}

	/**
	* Add a JPanel to the List
	*/
	public void addPanel(JPanel jp) {
		addPanel(jp,-1);
	}
	
	/**
	 * recursively add listeners to all components
	 */
	private void addFocusListener(final Component c, final JPanel jp) {
		c.addFocusListener(new FocusAdapter() {
			boolean alreadySelected= false;
			public void focusLost(FocusEvent e) {
				alreadySelected= false;
			}
			public void focusGained(FocusEvent e) {
				if (!alreadySelected)
					clicked(jp);
				alreadySelected= true;
			}
		});
		if (c instanceof Container) {
			Component[] cs= ((Container) c).getComponents();
			for (int i= 0; i < cs.length; i++)
				addFocusListener(cs[i], jp);
		}
	}

	/**
	 * set the selection to the Panel in parameter.. null to select none
	 */
	public void setSelectedPanel(JPanel jp) {
		if (pSelected == jp) return; // already selected
		pSelected= jp;
		if (selectionVisible)
			for (int i= 0; i < dataView.getComponentCount(); i++) {
				((JPanel) dataView.getComponent(i)).setBorder(DEFAULT_BORDER);
			}

		if (jp != null) {
			
			if (selectionVisible)
				jp.setBorder(SELECTED_BORDER);
			jp.setVisible(true);
			JViewport viewport= this.getViewport();
			for (int i= 0; i < 2; i++) {
				Rectangle scrollTo= jp.getBounds();
				scrollTo.x -= viewport.getViewPosition().x;
				scrollTo.y -= viewport.getViewPosition().y;
				getViewport().scrollRectToVisible(scrollTo);
			}
		}
	}
	
	/**
	* Called when one of the component has been clicked
	*/
	protected void clicked(JPanel jp) {
		if (! selectionVisible) return;
		if (pSelected == jp) return; // already selected
		setSelectedPanel(jp);
		fireActionEvent();
	}
	/**
	* Remove an entry from the list
	*/
	public void removePanel(JPanel jp) {
		dataView.remove(jp);
		clicked(null);
		refresh();
	}

	/**
	* Remove an entry from the list at the specified index
	*/
	public void removePanel(int index) {
		if (index < 0 || dataView.getComponentCount() < index)
			return;
		dataView.remove(index);
		clicked(null);
		refresh();
	}

	/**
	* Remove an entry from the list at the specified index
	*/
	public void removeAll() {
		dataView.removeAll();
		clicked(null);
		refresh();
	}

	/**
	* refresh the List
	*/
	public void refresh() {
		revalidate();
		repaint();
		fireActionEvent();
	}

	//---------- Events ---------------//

	private ActionEventHandler eventHandler;

	/** Add an action listener for event change **/
	public void addActionListener(ActionListener listener) {
		if (eventHandler == null) eventHandler = new ActionEventHandler();
		eventHandler.addActionListener(listener);
	}

	private void fireActionEvent() {
		if (eventHandler == null)
			return;
		eventHandler.fireActionEvent("");
	}

	//----------- Debug ---------------//
	public JPanel getDataView() {
		return dataView;
	}

	public static JListWithPanels getLast() {
		return last;
	}
	/**
	 * get the Selected Panel
	 */
	public JPanel getSelectedPanel() {
		return pSelected;
	}
	
	/**
	 * get an arraylist containing all the panels
	 */
	public ArrayList/*<JPanel>*/ getAllPanels() {
	    ArrayList/*<JPanel>*/ res = new ArrayList/*<JPAnel>*/();
	    Component c[] = dataView.getComponents();
	    for (int i = 0; i < c.length; i++) {
	        res.add(c[i]);
	    }
	    
		return res;
	}

	/**
	 * return the state of selectionVisible
	 */
	public boolean isSelectionVisible() {
		return selectionVisible;
	}

	/**
	 * set to true (default) if you want the selection to be highlighted
	 */
	public void setSelectionVisible(boolean b) {
		selectionVisible= b;
	}
	
	/**
	 * set to true (default) if you want panel to be selected when added 
	 */
	public void setSelectionOnAdd(boolean b) {
		focusOnAdd = b;
	}

}

/**
 * JLWPBorder
 */
class JLWPBorder extends AbstractBorder
{

	protected Color lineColor = Color.BLACK;
	protected Color selectedColor = Color.WHITE;
	protected boolean selected;
	

	public JLWPBorder(boolean selected) {
		this(selected, null, null);
	}
	
	public JLWPBorder(boolean selected, Color lineCol, Color selectedCol) {
		this.selected = selected;
		this.lineColor = lineCol;
		this.selectedColor = selectedCol;
	}


	/**
	 * Paints the border for the specified component with the 
	 * specified position and size.
	 * @param c the component for which this border is being painted
	 * @param g the paint graphics
	 * @param x the x position of the painted border
	 * @param y the y position of the painted border
	 * @param width the width of the painted border
	 * @param height the height of the painted border
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		
		if (selected) {
			Color sel = this.selectedColor;
			if (sel == null) {
				sel = UIManager.getColor("List.selectionForeground");
			}
			g.setColor(sel);
			g.drawRect(x, y, width-1, height-2);
		}
		
		Color lin = this.lineColor;
		if (lin == null) {
			lin = UIManager.getColor("Separator.foreground");
		}
		g.setColor(lin);
		g.drawLine(x, height-1, width-1, height-1);
		
		g.setColor(oldColor);
	}

	/**
	 * Returns the insets of the border.
	 * @param c the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c)       {
		return getBorderInsets(c,new Insets(0, 0, 0, 0)); //dummy insets
	}

	/** 
	 * Reinitialize the insets parameter with this Border's current Insets. 
	 * @param c the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.left = 1;
		insets.top = 1;
		insets.right = 1;
		insets.bottom = 2;
		return insets;
	}

	/**
	 * Returns the color of the border.
	 */
	public Color getLineColor()     {
		return lineColor;
	}


	/**
	 * Returns whether or not the border is opaque.
	 */
	public boolean isBorderOpaque() { 
		return true; 
	}

}


/** $Log: JListWithPanels.java,v $
/** Revision 1.2  2007/04/02 17:04:26  perki
/** changed copyright dates
/**
/** Revision 1.1  2006/12/03 12:48:40  perki
/** First commit on sourceforge
/**
/** Revision 1.22  2004/10/14 11:16:33  perki
/** Ehanced security in demo mode
/**
/** Revision 1.21  2004/09/28 17:19:59  perki
/** *** empty log message ***
/**
/** Revision 1.20  2004/07/30 15:38:19  perki
/** some changes
/**
/** Revision 1.19  2004/07/26 17:39:37  perki
/** Filler is now home
/**
/** Revision 1.18  2004/07/09 20:25:03  perki
/** Merging UI step 1
/**
/** Revision 1.17  2004/07/08 14:59:00  perki
/** Vectors to ArrayList
/**
/** Revision 1.16  2004/04/09 07:16:52  perki
/** Lot of cleaning
/**
/** Revision 1.15  2004/03/22 18:21:30  carlito
/** *** empty log message ***
/**
/** Revision 1.14  2004/03/12 14:06:10  perki
/** Vaseline machine
/**
/** Revision 1.13  2004/03/06 11:49:22  perki
/** *** empty log message ***
/**
/** Revision 1.12  2004/03/03 11:35:07  perki
/** Un petit bateau
/**
/** Revision 1.11  2004/02/26 14:34:20  perki
/** Ou alors la terre est un croissant comme la lune
/**
/** Revision 1.10  2004/02/26 13:24:34  perki
/** new componenents
/**
/** Revision 1.9  2004/02/20 05:45:05  perki
/** appris un truc
/**
/** Revision 1.8  2004/02/06 10:04:22  perki
/** Lots of cleaning
/**
* Revision 1.7  2004/02/06 07:44:55  perki
* lot of cleaning in UIs
*
* Revision 1.6  2004/02/05 18:35:59  perki
* *** empty log message ***
*
* Revision 1.5  2004/02/05 15:11:39  perki
* Zigouuuuuuuuuuuuuu
*
* Revision 1.4  2004/02/02 11:21:05  perki
* *** empty log message ***
*
* Revision 1.3  2004/01/29 15:00:53  perki
* Option Layout Trick
*
* Revision 1.2  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.1  2004/01/28 15:32:16  perki
* Il neige plus
* 
*/
