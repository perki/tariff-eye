/*
 * $Id: TabbedDispatcherAbstract.java,v 1.1 2006/12/03 12:48:44 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher.tools;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.SButton;
import com.simpledata.bc.uitools.SButtonIcon;

/**
 * An abstract clas for all Dispactcher Panels that uses Tabs
 */
public abstract class TabbedDispatcherAbstract 
	extends WorkSheetPanel 
	implements NamedEventListener {
	
    private final static Logger m_log = Logger.getLogger(
            TabbedDispatcherAbstract.class);
    
	/** set to FALSE to get a vertical display **/
	public static boolean HORIZONTAL_LAY= true;

	/** a memory for the last selected tab index **/
	private int lastSelectedIndex;
	
	protected JTabbedPane tabs;
	
	/** The tab index of the currently seen WorkSheetPanel */
	private int atWorkIndex = -6;
	
	/** A flag allowing to avoid index loss during refresh */
	private boolean refreshing = false;
	
	/**
	 *
	 */
	protected TabbedDispatcherAbstract (Dispatcher d, TarifViewer tv) {
	    super(d, tv);
	    init();
		refresh();
		lastSelectedIndex = -1;
	}
	
	
	// ---- to be or can be overridden ------------------//
	/**
	 * get the WorkSHeetPanel Title .. can be overwriten
	 */
	public String getTitleOf(int index) {
	    if (getWorkSheetPanelAt(index) == null || 
	            getWorkSheetPanelAt(index).getWorkSheet() == null
	    ) return "";
		return getWorkSheetPanelAt(index).getWorkSheet().getTitle();
	}
	
	/*
	 * get the WorkSHeetPanel Icon .. can be overwriten
	 */
	public ImageIcon getIconOf(int index) {
	    if (getWorkSheetPanelAt(index) == null || 
	            getWorkSheetPanelAt(index).getWorkSheet() == null
	    ) return Resources.wsDefaultWorkSheet;
		return getWorkSheetPanelAt(index).getIcon();
	}
	
	/**
	 * 
	 * @return the index of the selected tab
	 */
	public final int getSelectedIndex() {
	    if (this.tabs != null) {
	        return tabs.getSelectedIndex();
	    } 
	    return -1;
	}
	
	/**
	 * This method is used by dispatchers inheriting from this class to 
	 * change tab selection.
	 * @param index
	 */
	public final void setSelectedIndex(int index) {
	    //int nbTabs = this.getTabCount();
	    
	    // We'd rather count the REAL existing tabs to avoid null Pointer
	    // allready experienced
	    int nbTabs = tabs.getTabCount();
	    // We verify index validity
	    if ((index < -1) || (index >= nbTabs)) return;
	    
	    this.tabs.setSelectedIndex(index);
	}
	
	/**
	 * Inform the WorkSheetPanel that the tab at index has been selected
	 * @param index
	 */
	public abstract void indexHasBeenSelected(int index);
	
	/**
	 * get the number of tabs
	 */
	public abstract int getTabCount();
	
	/**
	 * get the worksheet panel at this index
	 * @param index position of the workSheetPanel desired
	 */
	public abstract WorkSheetPanel getWorkSheetPanelAt(int index);
	
	/**
	 * get the index of this workSheet
	 * @return -1 if not found
	 */
	public abstract int getWorkSheetIndex(WorkSheet ws);
	

	/**
	 * get the index of the worksheet at work (for refresh purposes).
	 * This is call after "refresh" to point the same ws 
	 */
	public final int getAtWorkWorkSheetIndex() {
	    //int 	atWorkIndex = getSelectedIndex();
	    if (atWorkIndex < 0) atWorkIndex = -1;
	    if (atWorkIndex > (getTabCount() -1 )) 
	        atWorkIndex = (getTabCount() -1 );
	    return atWorkIndex;
	}
	
	
	// ------------------ finals ----------------------//
	
	private final void init() {
		getContents().removeAll();
		tabs= new JTabbedPane();
		
		//TODO Validate the use of the new TabbedPaneUI
		//tabs.setUI(new MyTabbedPaneUI(this));
		tabs.setBorder(new EmptyBorder(0,0,0,0));
		tabs.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent evt) {
				showPopup(evt);
			}
		});
		
	
		getContents().setLayout(new BorderLayout());

		//getContents().setBorder(new EtchedBorder());
		getContents().setAlignmentY(0.53F);
		getContents().setMaximumSize(new Dimension(32767, 30));
		tabs.setTabPlacement(
			HORIZONTAL_LAY ? SwingConstants.TOP : SwingConstants.LEFT);

		getContents().add(tabs, BorderLayout.CENTER);
		
		tabs.addChangeListener(new ChangeListener() {

		    private int oldIndex = -5;
		    
            public void stateChanged(ChangeEvent e) {
                if (refreshing) return;
                JTabbedPane jtp = (JTabbedPane)e.getSource();
                int newIndex = jtp.getSelectedIndex();
                if (oldIndex != newIndex) {
                    oldIndex = newIndex;
                    atWorkIndex = newIndex;
                    indexHasBeenSelected(newIndex);
                }
            }
		    
		});

	}
	
	
	
	/** refresh the tabs **/
	public final void refresh() {
		refreshing = true;
		tabs.removeAll();
		
		int max = getTabCount();
		
		
		    
		    for (int i = 0; i < max; i++) {
		        WorkSheetPanel wsp = getWorkSheetPanelAt(i);
		        if (wsp == null) return;
		        //wsp.showWorkSheetPanelBorder(false);
		        //m_log.warn("Showing border in DispatcherSequencerPanel...");
		        wsp.showWorkSheetPanelBorder(true);
		        if (wsp.getWorkSheet() != null) {
		            wsp.getWorkSheet().addNamedEventListener(this);
		        }
		        tabs.addTab(getTitleOf(i), wsp.getIcon(), wsp.getPanel());
		    }
		    
		    refreshing = false;
		    
		    if (getAtWorkWorkSheetIndex() > -1)
		        tabs.setSelectedIndex(getAtWorkWorkSheetIndex());
		    lastSelectedIndex = tabs.getSelectedIndex();
		
	}
	
	
	/**
	 * Show a popup with actions for the selected tab
	 */
	public final void showPopup(MouseEvent e) {
		
		
		if (lastSelectedIndex != tabs.getSelectedIndex()) {
			lastSelectedIndex = tabs.getSelectedIndex();
			return;
		}
		if (lastSelectedIndex < 0) return;
		
		Rectangle b = tabs.getBoundsAt(lastSelectedIndex);
		if (b == null) return;
		
		if (! b.contains(e.getX(),e.getY())) return;
		
		WorkSheetPanel z = getWorkSheetPanelAt(lastSelectedIndex);
		JPopupMenu jPopupMenu = z.getJPopupMenu();
		if (jPopupMenu != null) {
			jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		} 
		 
	}
	
	
	
	private JPanel jp;
	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public final JPanel getContents() {
		if (jp == null)
			jp= new JPanel();
		return jp;
	}

	/**
	 * catch title changes of worksheets
	 */
	public final void eventOccured(NamedEvent e) {
		if (! (e.getSource() instanceof WorkSheet)) return;
		int pos = getWorkSheetIndex((WorkSheet) e.getSource());
		if (pos < 0) return ;
		if (pos >= tabs.getTabCount()) return;
		
		if (e.getEventCode() == NamedEvent.TITLE_MODIFIED)
		tabs.setTitleAt(pos,getTitleOf(pos));
		
		if (e.getEventCode() == NamedEvent.WORKSHEET_DATA_MODIFIED||
				e.getEventCode() == NamedEvent.WORKSHEET_OPTION_ADDED ||
				e.getEventCode() == NamedEvent.WORKSHEET_OPTION_REMOVED ||
				e.getEventCode() == 
					NamedEvent.WORKSHEET_REDUC_OR_FIXED_ADD_REMOVE) {
			tabs.setIconAt(pos,getWorkSheetPanelAt(pos).getIcon());
		}
	
	}


}

/**
 * TODO finish this with caching etc... it's just there to validate
 * the faisability of JPanels in Tabbed pane.
 * 
 * TODOLIST:
 * - Make a Single class of this one and add it to the SDL
 * - Caching of panels
 * - better integration with actual Lokk and Feel
 */
class MyTabbedPaneUI extends BasicTabbedPaneUI {
    public final static Logger m_log = Logger.getLogger( MyTabbedPaneUI.class );
   
    int borderWidth = 5;
    
    If owner;
    
    /** temp constructor **/
    MyTabbedPaneUI(final TabbedDispatcherAbstract tda) {
        owner = new If() {

            public JTabbedPane getTabbedPane() {
                return tda.tabs;
            }

            public JPanel getTabPanel(int i) {
                JPanel myPanel;
                myPanel = new JPanel();
                JLabel title = new JLabel(tda.getIconOf(i));
                title.setText(tda.getTitleOf(i));
                myPanel.add(title);
                SButtonIcon sb = new SButtonIcon(Resources.iconOK);
                myPanel.add(sb);
                
                myPanel.setMaximumSize(new Dimension(1000000,20));
                return myPanel;
            }
            
        };
        init();
    }
    
    
    public MyTabbedPaneUI(If tda) {
        owner = tda;
        init();
    }
    
    MouseEvent mouseLastEvent;
    
    private void init() {
        owner.getTabbedPane().addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
			    mouseLastEvent = e;
			    owner.getTabbedPane().repaint();
			}
			
			public void mouseClicked(MouseEvent e) {
			    mouseLastEvent = e;
			    owner.getTabbedPane().repaint();
			}
			
			public void mouseReleased(MouseEvent e) {
			    mouseLastEvent = e;
			    owner.getTabbedPane().repaint();
			}
			public void mouseExited(MouseEvent e) {
			    mouseLastEvent = e;
			    owner.getTabbedPane().repaint();
			}
		});
        owner.getTabbedPane().addMouseMotionListener(new MouseMotionListener(){

            public void mouseDragged(MouseEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void mouseMoved(MouseEvent e) {
                mouseLastEvent = e;
			    owner.getTabbedPane().repaint();
            }});
		
	
    }
    
    /** return the panel for this tab **/
    protected JPanel getPanel(int i) {
        JPanel myPanel = owner.getTabPanel(i);
        // create an off screen Frame
        JFrame f = new JFrame("Show remain invisible");
        f.setContentPane(myPanel);
        f.pack();
        return myPanel;
    }
    /** retrun an image representation of the panel for this tab **/
    protected BufferedImage getPanelImage(int i,int mouseX,int mouseY) {
        JPanel myPanel = getPanel(i);
        
        int w = myPanel.getWidth();
		int h = myPanel.getHeight();
		BufferedImage image = new BufferedImage(w , h,
		        BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		
		if (mouseX > 0 && mouseY > 0) {
		   
		    Component c = myPanel.getComponentAt(mouseX,mouseY);
		    if (c != null) {
		        
			    int x = mouseX-c.getX();
			    int y = mouseY-c.getY();
			    
			    MouseEvent e = mouseLastEvent;
			    MouseEvent me = new MouseEvent(c,
			            e.getID(),
			            e.getWhen(),
			            e.getModifiers(),
			            x,y,e.getClickCount(),e.isPopupTrigger()
			    );
			    
			    MouseMotionListener[] ml = c.getMouseMotionListeners();
			    for (int j = 0; j < ml.length ; j++) {
			       // m_log.warn("Mouse "+c+" "+x+" "+y);
					
			        //ml[j].mouseMoved(me);
			    }
			    MouseListener[] ml2 = c.getMouseListeners();
			    for (int j = 0; j < ml2.length ; j++) {
			       // m_log.warn("Mouse "+c+" "+x+" "+y);
					
			        ml2[j].mouseEntered(me);
			    }
			    
		    }
		    
		}
		
		myPanel.paint(g2);
		g2.dispose();
		
		return image;
    }
    
    protected int 
    calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
        return getPanel(tabIndex).getWidth()+borderWidth*2;  
    }
    
    protected int 
    calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        return getPanel(tabIndex).getHeight()+borderWidth*2; 
    }
    
    public void paintTab(Graphics g, int tabPlacement,
    Rectangle[] rects, int tabIndex, 
    Rectangle iconRect, Rectangle textRect) {
        Rectangle tabRect = rects[tabIndex];
		int selectedIndex = tabPane.getSelectedIndex();
		boolean isSelected = selectedIndex == tabIndex;
		

		

		paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
				tabRect.width, tabRect.height, isSelected);

		paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y,
				tabRect.width, tabRect.height, isSelected);
		
		
		// Mouse handeling
		int x = -1, y = -1;
		if (mouseLastEvent != null) {
		    MouseEvent e = mouseLastEvent;
		    x = e.getX()-tabRect.x-borderWidth;
		    y = e.getY()-tabRect.y-borderWidth;
		  
		}
		// end of mouse handleing
		
		BufferedImage image = getPanelImage(tabIndex,x,y);
		g.drawImage(image,tabRect.x+borderWidth,tabRect.y+borderWidth,null);

		paintFocusIndicator(g, tabPlacement, rects, tabIndex, iconRect,
				textRect, isSelected);

		
    }
    
    /** interface for this tool **/
    public interface If {
        /** 
         * return the tabbed pane to monitor<BR>
         * **/
        public JTabbedPane getTabbedPane();
        
        /**
         * return the panel to display for this tab
         */
        public JPanel getTabPanel(int i);
    }
}

/*
 * $Log: TabbedDispatcherAbstract.java,v $
 * Revision 1.1  2006/12/03 12:48:44  perki
 * First commit on sourceforge
 *
 * Revision 1.24  2004/11/23 14:06:35  perki
 * updated tariffs
 *
 * Revision 1.23  2004/11/23 10:37:28  perki
 * *** empty log message ***
 *
 * Revision 1.22  2004/11/22 18:44:39  carlito
 * Affichage des DispatcherCase modifications quasi-finale
Modification du WorkSheetPanelBorder pour qu'il n'ai plus la bougeotte avec le titre...
 *
 * Revision 1.21  2004/11/15 09:11:03  perki
 * *** empty log message ***
 *
 * Revision 1.20  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.19  2004/11/11 10:57:44  perki
 * Intro to new Dispachers design
 *
 * Revision 1.18  2004/11/09 18:29:26  carlito
 * Dispatcher case upgraded according to issue 43
 *
 * Revision 1.17  2004/10/01 15:00:05  carlito
 * Dispatcher case now refresh on option changes
 *
 * Revision 1.16  2004/09/08 16:35:15  perki
 * New Calculus System
 *
 * Revision 1.15  2004/09/07 13:35:04  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/07/01 14:45:14  carlito
 * *** empty log message ***
 *
 * Revision 1.13  2004/06/28 12:48:49  carlito
 * Dispatcher case++
 *
 * Revision 1.12  2004/05/27 08:43:33  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/05/23 10:40:06  perki
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
 * Revision 1.7  2004/05/21 12:15:12  carlito
 * *** empty log message ***
 *
 * Revision 1.6  2004/05/19 16:39:58  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/18 19:09:47  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/18 17:10:19  perki
 * Better icons management
 *
 * Revision 1.3  2004/05/18 15:41:45  perki
 * Better icons management
 *
 * Revision 1.2  2004/05/18 15:11:25  perki
 * Better icons management
 *
 * Revision 1.1  2004/05/18 11:29:22  perki
 * Abstract Tabbed
 *
 */