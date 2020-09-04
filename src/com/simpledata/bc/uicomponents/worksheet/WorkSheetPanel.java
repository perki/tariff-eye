/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/* 
* $Id: WorkSheetPanel.java,v 1.2 2007/04/02 17:04:28 perki Exp $
*/
package com.simpledata.bc.uicomponents.worksheet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.Dispatcher;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.WorkSheet;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.event.NamedEventListener;
import com.simpledata.bc.datatools.ComponentManager;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.OptionDefaultPanel;
import com.simpledata.bc.uicomponents.bcoption.viewers.OptionsViewer;
import com.simpledata.bc.uicomponents.tools.NamedTitleDescriptionEditor;
import com.simpledata.bc.uicomponents.tools.ReducOrFixedEditor;
import com.simpledata.bc.uitools.ImageTools;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SLabel;

/**
* This is the default model for worksheet panels
*/
public abstract class WorkSheetPanel  {
    private static final Logger m_log 
	= Logger.getLogger( WorkSheetPanel.class );


	/**
	 * overwrite this to set a default Class Icon
	 */
	public static ImageIcon defaultClassTreeIcon= Resources.wsDefaultWorkSheet;
	
	
	protected static Dimension defaultOptionViewerDim = 
		new Dimension(0, 70);
		
	protected static Dimension defaultContentDim = 
			new Dimension(0, 300);
	
	protected WorkSheet workSheet;
	private WSIf displayControler;
	private OptionDefaultPanel.EditStates optionStateControler;
	
	private JPanel myPanel;
	// a panel that shows / hide the optionPanel
	// TODO first implementations of the 
	private JPanel optionsPanelContainer;
	protected JPanel border;
	
	private WorkSheetDescriptionPanel descriptionPanel;
	
	/** this boolean set if we see or not the option.. modified by the 
	 * open / close button of the border
	 */
	private boolean showOptions;
	
	/** show or not the WorkSheetPanel Border **/
	private boolean showWorkSheetPanelBorder;
	
	/**
	* Constructor
	*/
	protected WorkSheetPanel(WorkSheet ws, TarifViewer tv) {
		this.workSheet= ws;
		this.displayControler= tv;
		this.optionStateControler = tv;
		showWorkSheetPanelBorder = true;
		showOptions = true;
	}
	
	
	/**
	 * Get current display controller
	 */
	protected WSIf getDisplayController() {
	    return displayControler;
	}
	
	/**
	 * Returns the current option state controller
	 */
	protected OptionDefaultPanel.EditStates getOptionStateControler() {
	    return optionStateControler;
	}
	
	/**
	 * Show or not the OptionPanel.<BR>
	 */
	public void showOptions(boolean b) {
	   
	    if (  optionsPanelContainer == null) {
	        showOptions = !b;
	        optionsPanelContainer = new JPanel(new BorderLayout());
	    }
	    if (getOptionPanel() == null) return;
	    if (showOptions == b) return;
		showOptions = b;
		
		
		if (b) {
		    optionsPanelContainer.add(getOptionPanel(),BorderLayout.CENTER);
		} else {
		    optionsPanelContainer.removeAll();
		    
		}
		
		optionsPanelContainer.revalidate();
		myPanel.revalidate();
		
		_refresh();
	}
	
	/**
	 * return the state of the showOption boolean.<BR>
	 */
	public boolean showOptions() {
	    return showOptions;
	}
	
	/**
	 * Show or not the WorkSheetPanelBorder.<BR>
	 * This does not do any refresh!! so getPanel should be called after!!
	 */
	public void showWorkSheetPanelBorder(boolean b) {
		if (b == showWorkSheetPanelBorder) return;
		showWorkSheetPanelBorder = b;
		createPanel();
	}
	
	
	/**
	 * return the Panel to display
	 */
	public final JPanel getPanel() {
		if (myPanel != null) return myPanel;
		return createPanel();
	}
	
	
	
	
	/**
	 * return the Panel to display
	 */
	private final JPanel createPanel() {
		// get the border
	    if (showWorkSheetPanelBorder) {
	        border = 
	            new WorkSheetPanelBorder(this);
	    } else {
	        border = new JPanel(new BorderLayout(0,0));
	        JPanel jp = getActionPanel();
	        if (jp != null) {
	            border.add(jp,BorderLayout.EAST);
	        }
	    }
	    
	    
	    myPanel = new JPanel(new BorderLayout());
	    myPanel.add(this.border,BorderLayout.NORTH);
	    
	    JPanel internalPanel = new JPanel(new BorderLayout());
	    
	    descriptionPanel = new WorkSheetDescriptionPanel(this);
	    
	    internalPanel.add(descriptionPanel, BorderLayout.NORTH);
	    
	    showOptions(true); // init the showOptions container
	    
	    // build the panel
	    if (getContents() != null && getOptionPanel() != null) {
	        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	        jsp.setUI(new MySplitPaneUI());
	        jsp.setBorder(null);
	        jsp.add(optionsPanelContainer,JSplitPane.TOP);
	        //jsp.setContinuousLayout(true);
	        jsp.setDividerSize(8);
	        jsp.add(getContents(),JSplitPane.BOTTOM);
	        internalPanel.add(jsp,BorderLayout.CENTER);
	    } else if (getContents() != null) {
	        internalPanel.add(getContents(),BorderLayout.CENTER);
	    } else if (getOptionPanel() != null) {
	        internalPanel.add(optionsPanelContainer,BorderLayout.CENTER);
	    }
	    
	    myPanel.add(internalPanel, BorderLayout.CENTER);
	    _refresh();
	    return myPanel;
	}
	
	/**
	 * get the content Panel to display
	 *  CAN RETURN NULL
	 */
	public abstract JPanel getContents();
	
	/**
	 * get the option Panel to display.<BR>
	 * You can return getStandardOptionViewer() if you need 
	 * a standard option viewer.<BR>
	 * CAN RETURN NULL
	 */
	public abstract JPanel getOptionPanel();
	
	
	// ----------- Option viewer helper ---------------//
	private OptionsViewer ov;
	/** 
	 * create an standard option viewer<BR>
	 * changes on the OptionViewer will call refresh();
	 * Dimension will be set to a default value but can be changed
	 **/
	protected final OptionsViewer getStandardOptionViewer() {
		if (ov != null) return ov;	
		boolean b = 
			
		optionStateControler.getEditOptionState() == 
				OptionDefaultPanel.EditStates.FULL;
		
		ov = new OptionsViewer(workSheet, 
				b, 
				optionStateControler);
		ov.setMinimumSize(defaultOptionViewerDim);
		ov.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    _refresh();
			    if (getContents() != null) {
			        getContents().revalidate();
			        getContents().repaint();
			    }
			}
		});
		return ov;
	}
	
	/**
	 * return the icon for this WorkSheet 
	 */
	public final ImageIcon getIcon() {
		ImageIcon ii = getTreeIcon();
		if (getWorkSheet() == null) return ii;
		if (displayControler.getEditReductionsState() !=
			WSIf.EDIT_REDUCTION_NONE) {
			if (getWorkSheet().getReductionOrFixed() != null)
				ii = ImageTools.drawIconOnIcon(ii,Resources.reductionTag, null);
		}
		if (! getWorkSheet().isValid()) 
			return ImageTools.drawIconOnIcon(ii,Resources.stdTagError,null);
		
		return ii;
	}
	
	//	----------- to be implemented -----------//
	
	
	/**
	* called when a UI has to save it's data into its components
	*/
	public abstract void save();

	/**
	 * return the icon for this WorkSheet 
	 */
	protected abstract ImageIcon getTreeIcon();

	/**
	 * return a JPanel to be included in the border (tool bar)<BR>
	 * Overide this method if needed;
	 */
	public JPanel getActionPanel() {
		return null;
	}
	
	
	/** @param o can be null.. mypanel will be used 
	 * @param delta can be null.. (0,0) will be used
	 * **/
	
	public ModalJPanel getNamedTitleDescriptionEditor
		(Component o,Point delta) {
		if (o == null) o = myPanel;
		if (delta == null) delta = new Point(0,0);
		return NamedTitleDescriptionEditor.getModalJInternalFrame(
			workSheet,
			o,
			delta,
			true);
		
	}
	/**
	 * return the jmenu containing actions for this panel
	 * The default create a Remove action
	 */
	public JPopupMenu getJPopupMenu() {
		return displayControler.getJPopupMenuFor(this);
	}
	
	/**
	 * refresh
	 */
	private void _refresh() {
	    refresh();
	}

	/**
	 * refresh the UI (loading new data from the data model)
	 */
	public abstract void refresh();

	/**
	 * refresh this and childrens UI 
	 */
	public void refreshRecursive() {
		_refresh();

		WorkSheetPanel[] childs= getChildrenWorkSheetPanels();
		for (int i= 0; i < childs.length; i++) {
			childs[i].refreshRecursive();
		}
	}
	

	/**
	 * get a list of Childrens as Panels
	 */
	public WorkSheetPanel[] getChildrenWorkSheetPanels() {
		if (workSheet == null) return new WorkSheetPanel[0];
		if (!workSheet.isDispatcher()) {
			return new WorkSheetPanel[0];
		}
		ArrayList workSheets= ((Dispatcher) workSheet).getChildWorkSheets();
		WorkSheetPanel[] list= new WorkSheetPanel[workSheets.size()];
		for (int i= 0; i < list.length; i++) {
			list[i]=
			  displayControler.getWorkSheetPanel((WorkSheet)workSheets.get(i));
		}
		return list; 
	}

	/**
	 * get the parent of this panel. return null if root WorkSheet
	 */
	public WorkSheetPanel getParentWorkSheetPanel() {
		if (workSheet == null) return null;
		if (!(workSheet.getWscontainer() instanceof WorkSheet))
			return null;
		return displayControler.getWorkSheetPanel(
			(WorkSheet) workSheet.getWscontainer());
	}

	//----------- common tools ---------------//

	/**
	* get the tarifViewer containg this panel
	
	public TarifViewer getTarifViewer() {
		return tarifViewer;
	}*/

	/**
	 * return true if this Panel shoudl be Highlighted
	 */
	public boolean isHighLighted() {
		return true;
	}

	//private TitledBorder tb = null;
	
	/**
	 * return the used border
	 */
	public Border getPanelBorder() {
		return new LineBorder(null, 0);
//		if (tb == null) {
//			String title= Lang.translate("Empty");
//			if (getWorkSheet() != null)
//				title = getWorkSheet().getTitle();
//			tb = new TitledBorder(title);
//		}
//		return tb;
	}

	//-------------- Panel Loading ----------------------//
	/**
	* return an instance of the WorkSheetPanel desired for this WorkSheet
	* @param ws the workSheet to display
	* @param tv the tarif viewer monitoring this 
	*/
	public static WorkSheetPanel getWorkSheetPanel(WorkSheet ws, TarifViewer tv) {
		WorkSheetPanel wsdp= _getWorkSheetPanel(ws, tv);

		// post manipulation

		wsdp.getPanel().setBorder(wsdp.getPanelBorder());

		return wsdp;
	}

	/**
	 * get the corresponding Panel Class name for this WorkShee
	 */
	private static String getWorkSheetPanelClassName(Class ws) {
		if (ws == null)
			return null;
		final String headToRemove= "com.simpledata.bc.components";
		final String headToAdd= "com.simpledata.bc.uicomponents";
		final String tailToAdd= "Panel";
		String name= ws.getName();

		// compose UI class names
		if (name.indexOf(headToRemove) != 0) {
		    m_log.error("WorkSheetExplorer: wrong class type ->" + name);
			return null;
		}

		name= headToAdd + name.substring(headToRemove.length()) + tailToAdd;
		return name;
	}

	/**
	* return an instance of the WorkSheetPanel desired for this WorkSheet
	* @param ws the workSheet to display
	* @param tv the tarif viewer monitoring this 
	*/
	private static WorkSheetPanel _getWorkSheetPanel(
		WorkSheet ws,
		TarifViewer tv) {

		// null worksheet propose an empty workSheet
		if (ws == null) {
			return new ErrorPanel("Cannot deal with Empty WorkSheets", tv);
		}

		String name= getWorkSheetPanelClassName(ws.getClass());
		if (name == null) {
			return new ErrorPanel(
				"Cannot find UI for " + ws.getClass().getName(),
				tv);
		}

		// create parameters Array
		Object[] initArgs= new Object[2];
		initArgs[0]= ws;
		initArgs[1]= tv;

		// create parameters class type to get the right constructor
		Class[] paramsType= ComponentManager.getClassArray(initArgs);
		paramsType[1] = TarifViewer.class;
		WorkSheetPanel wsdp=
			(WorkSheetPanel) ComponentManager.getInstanceOf(
				name,
				paramsType,
				initArgs);

		if (wsdp == null)
			return new ErrorPanel(
				"Cannot find UI for " + ws.getClass().getName(),
				tv);
		return wsdp;
	}

	/**
	* get the default icon for this type of WorkSheet
	* @param wsc worksheet class name
	*/
	public static ImageIcon getWorkSheetIcon(Class wsc) {
		Class c= null;
		try {
			c= Class.forName(getWorkSheetPanelClassName(wsc));
		} catch (ClassNotFoundException e1) {
			m_log.error("WorkSheetPanel: ClassNotFoundException", e1);
		}

		if (c == null) {
		    m_log.error("Class c is null");
			return null;
		}
		ImageIcon s= null;
		try {
			s=
				(ImageIcon) c.getField("defaultClassTreeIcon").get(
					new ImageIcon());
		} catch (NoSuchFieldException e) {
		    m_log.error("WorkSheetPanel: NoSuchFieldException", e);
		} catch (IllegalAccessException e) {
		    m_log.error("WorkSheetPanel: IllegalAccessException", e);
		}
		return s;
	}

	/**
	 * @return the monitored WorkSheet
	 */
	public final WorkSheet getWorkSheet() {
		return workSheet;
	}
	
	/**
	 * convinience tool to avoid getWorkSheet().getTarif()
	 * @return the monitored WorkSheet
	 */
	public Tarif getTarif() {
		return workSheet != null ? workSheet.getTarif() : null;
	}

	public String toString() {
		if (this.workSheet != null) {
			return this.workSheet.getTitle();
		} 
		return new String("No workSheet");
	}
	
	
	//----------------------- Reductions ---------------------------//
	/** show the reduction Panel **/
	public final void showReductionPanel() {
		ReducOrFixedEditor.getPopup(this,
				myPanel,
				new Point(0,0));
	}
	
	public final int getEditReductionsState() {
		return displayControler.getEditReductionsState();
	}
	
	
	//----------------------- INTERFACE ----------------------------//
	
	/** an interface to control the way WorkSeet Panel are displayed **/
	public interface WSIf {
		
		
		/**
		 * return true if root options should be displayed.
		 * (for example transactions and amounts)
		 * (false for edit mode, true for simu)
		 */
		public boolean showRootOptions();
		
		
		// edit state **/
		/** Use in simulation mode **/
		public final static int EDIT_STATE_NONE = 0;
		/** Use in creation mode **/
		public final static int EDIT_STATE_FULL = 1;

		/**
		 * return one of WorkSheetPanel.WSIf.EDIT_STATE_*
		 */
		public int getEditWorkPlaceState();
		
		
		// edit state **/
		/** cannot edit or view reductions **/
		public final static int EDIT_REDUCTION_NONE = 0;
		/** can edit reductions **/
		public final static int EDIT_REDUCTION_FULL = 1;
		/** can view reductions **/
		public final static int EDIT_REDUCTION_VIEW = 2;
		/**
		 * return one of WorkSheetPanel.EDIT_REDUCTION_*
		 */
		public int getEditReductionsState();
		
		/**
		 * return a JPopupMenu with action that can be taken on this
		 * WorkSheetPanel
		 */
		public JPopupMenu getJPopupMenuFor(WorkSheetPanel wsp);
		
		/** 
		 * return the WorkSheet Panel corresponding to this WorkSheet
		 */
		public WorkSheetPanel getWorkSheetPanel(WorkSheet ws);
		
		/**
		 * return true if the tarifViewer is meant to support
		 * depth of view
		 */
		public boolean isDepthOfViewEnabled();
		
	}
	
}

/**
 * This class is intended to make a panel which reflects WorkSheet description
 * to be inserted in the WorkSheetPanel layout...
 */
class WorkSheetDescriptionPanel extends JPanel implements NamedEventListener {
    
    private static final Logger m_log = Logger.getLogger( WorkSheetDescriptionPanel.class );
    
    private WorkSheetPanel owner;
    
    private SLabel textLabel;
    
    public WorkSheetDescriptionPanel(WorkSheetPanel daddy) {
        super();
        this.owner = daddy;
        buildComponents();
        refresh();
        
        // Attach listener
        if (owner != null) {
            WorkSheet ws = owner.getWorkSheet();
            if (ws != null) {
                ws.addNamedEventListener(this, 
                        NamedEvent.DESCRIPTION_MODIFIED, null);
                //m_log.warn("Listener succesfully attached...");
            }
        }
    }
    
    private void buildComponents() {
        setLayout(new GridBagLayout());
        textLabel = new SLabel();
        textLabel.setMaxLength(NamedTitleDescriptionEditor.PREF_LABEL_WIDTH);
//        textLabel.setMaximumSize(new Dimension(
//                NamedTitleDescriptionEditor.PREF_LABEL_WIDTH,
//                100000));
        Color myCol = UIManager.getColor("Label.disabledForeground");
        textLabel.setForeground(myCol);
    }
    
    private void refresh() {
        removeAll();
        if (owner != null) {
            WorkSheet ws = owner.getWorkSheet();
            if (ws != null) {
                String desc = ws.getDescription();
                
                if ((desc != null) && (!desc.equals(""))) {
                    
                    textLabel.setText(desc);
                    
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.anchor = GridBagConstraints.NORTHWEST;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.weightx = 1.0;
                    gbc.insets = new Insets(3,5,3,5);
                    
                    add(textLabel, gbc);

                }  else {
                    // Ensure a little spacing between borders...
                    JLabel nullLabel = new JLabel();
                    nullLabel.setPreferredSize(new Dimension(1,1));
//                    GridBagConstraints gbc = new GridBagConstraints();
//                    gbc.anchor = GridBagConstraints.NORTHWEST;
//                    gbc.fill = GridBagConstraints.HORIZONTAL;
//                    gbc.weightx = 1.0;
//                    gbc.insets = new Insets(1,1,1,1);
                    add(nullLabel);
                }
            } 
        }
    }

    public void eventOccured(NamedEvent e) {
        if (WorkSheet.class.isAssignableFrom(e.getSource().getClass())) {
            //m_log.warn("Received an event....");
            refresh();
        }
    }
    
}

/**
* Simple Panel that display an error message
*/
class ErrorPanel extends WorkSheetPanel {
	JPanel panel;
	
	public ErrorPanel(String message, TarifViewer tv) {
		super(null, tv);
		panel = new JPanel();
		panel.add(new JLabel("ERROR :" + message));
	}
	
	/**
	* implementation of @see WorkSheetPanel#save();
	*/
	public void save() {
		// nothing to save on error panels
	}

	/**
	 * @see WorkSheetPanel#getTreeIcon()
	 */
	public ImageIcon getTreeIcon() {
		return ImageTools.drawIconOnIcon(
			Resources.stdIconDispatcher,
			Resources.stdTagError,
			new Point(0, 0));
	}

	/* (non-Javadoc)
	 * @see WorkSheetPanel#getJPopupMenu()
	 */
	public JPopupMenu getJPopupMenu() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
	 */
	public void refresh() {
		// nothing to do
	}

	/* (non-Javadoc)
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#childrens()
	 */
	public WorkSheetPanel[] childrens() {
		return new WorkSheetPanel[0];
	}

	/**
	 * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
	 */
	public JPanel getContents() {
		return panel;
	}
	/**
	 * 
	 */
	public JPanel getOptionPanel() {
		return null;
	}

}

class MySplitPaneUI extends BasicSplitPaneUI {
	
	public MySplitPaneUI() {
		super();
	}
	
	/**
	 * Creates a new MetalSplitPaneUI instance
	 */
   public static ComponentUI createUI(JComponent x) {
   	return new MySplitPaneUI();
   }

   /**
	 * Creates the default divider.
	 */
   public BasicSplitPaneDivider createDefaultDivider() {
  	 return new MySplitPaneDivider(this);
   }
   // TODO find a good way to make double buffering... 
   class MySplitPaneDivider extends BasicSplitPaneDivider {
   	
	//     Cr�ation du Graphics secondaire
	 //  Graphics offScreen;
	//        Et l'image correspondante
	   //Image offScreenImg;
    
	   /**
		* @param paneUI
		*/
	   public MySplitPaneDivider(MySplitPaneUI paneUI) {
		   super(paneUI);
		   super.setBorder(null);
		 
	   }
	   
//	   public void init() {
//		   offScreenImg = createImage(getWidth(), getHeight());
//		   offScreen = offScreenImg.getGraphics();
//	   }
//	  
//	   
//	   public void paint(Graphics g) {
//	       if (offScreen == null) init();
//	       offScreen.clearRect(0,0,getWidth(),getHeight()); 
//	       Rectangle clip = g.getClipBounds();
//	       
//	       offScreen.drawImage(
//	               Resources.splitHorizontal.getImage(),clip.x,clip.y,null);
//		   g.drawImage(offScreenImg,0,0,this);
//	   }
	  
   }
}



/*
* $Log: WorkSheetPanel.java,v $
* Revision 1.2  2007/04/02 17:04:28  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:44  perki
* First commit on sourceforge
*
* Revision 1.70  2004/11/17 18:28:09  carlito
* arggg
*
* Revision 1.69  2004/11/17 15:26:22  carlito
* New design for WorkSheetPanel, WorkSheetPanelBorder and DispatcherCasePanel advanced
*
* Revision 1.68  2004/11/15 09:11:03  perki
* *** empty log message ***
*
* Revision 1.67  2004/11/12 14:28:39  jvaucher
* New NamedEvent framework. New bugs ?
*
* Revision 1.66  2004/11/11 10:57:44  perki
* Intro to new Dispachers design
*
* Revision 1.65  2004/10/15 17:50:04  carlito
* SLabel
*
* Revision 1.64  2004/10/12 17:49:10  carlito
* Simulator split problems solved...
* description pb solved
*
* Revision 1.63  2004/10/11 17:48:08  perki
* Bobby
*
* Revision 1.62  2004/09/23 06:27:56  perki
* LOt of cleaning with the Logger
*
* Revision 1.61  2004/09/22 15:39:55  carlito
* Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
*
* Revision 1.60  2004/09/14 13:06:32  perki
* *** empty log message ***
*
* Revision 1.59  2004/09/13 15:27:31  carlito
* *** empty log message ***
*
* Revision 1.58  2004/09/09 18:38:46  perki
* Rate by slice on amount are welcome aboard
*
* Revision 1.57  2004/09/07 13:35:04  carlito
* *** empty log message ***
*
* Revision 1.56  2004/08/05 00:23:44  carlito
* DispatcherCase bugs corrected and aspect improved
*
* Revision 1.55  2004/07/27 11:11:48  kaspar
* + Added AssetsRateBySlice report
*
* Revision 1.54  2004/07/22 15:12:35  carlito
* lots of cleaning
*
* Revision 1.53  2004/07/08 14:59:00  perki
* Vectors to ArrayList
*
* Revision 1.52  2004/05/27 08:43:33  carlito
* *** empty log message ***
*
* Revision 1.51  2004/05/23 12:16:22  perki
* new dicos
*
* Revision 1.50  2004/05/23 10:40:06  perki
* *** empty log message ***
*
* Revision 1.49  2004/05/22 18:33:22  perki
* *** empty log message ***
*
* Revision 1.48  2004/05/22 17:58:19  carlito
* *** empty log message ***
*
* Revision 1.47  2004/05/22 17:30:20  carlito
* *** empty log message ***
*
* Revision 1.46  2004/05/22 17:20:46  perki
* Reducs are visibles
*
* Revision 1.45  2004/05/22 08:39:36  perki
* Lot of cleaning
*
* Revision 1.44  2004/05/21 13:19:50  perki
* new states
*
* Revision 1.43  2004/05/20 17:05:30  perki
* One step ahead
*
* Revision 1.42  2004/05/19 16:39:58  perki
* *** empty log message ***
*
* Revision 1.41  2004/05/18 19:09:47  carlito
* *** empty log message ***
*
* Revision 1.40  2004/05/18 15:11:25  perki
* Better icons management
*
* Revision 1.39  2004/05/18 13:49:46  perki
* Better copy / paste
*
* Revision 1.38  2004/05/14 08:46:18  perki
* *** empty log message ***
*
* Revision 1.37  2004/05/14 07:52:53  perki
* baby dispatcher is going nicer
*
* Revision 1.36  2004/05/06 07:27:32  perki
* OptionViewer moved
*
* Revision 1.35  2004/05/06 07:06:25  perki
* WorkSheetPanel has now two new methods
*
* Revision 1.34  2004/05/05 15:38:01  perki
* balooo
*
* Revision 1.33  2004/05/05 15:23:49  perki
* balooo
*
* Revision 1.32  2004/04/13 21:30:14  perki
* *** empty log message ***
*
* Revision 1.31  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.30  2004/03/24 14:33:46  perki
* Better Tarif Viewer no more null except
*
* Revision 1.29  2004/03/24 13:29:12  carlito
* *** empty log message ***
*
* Revision 1.28  2004/03/24 13:11:14  perki
* Better Tarif Viewer no more null except
*
* Revision 1.27  2004/03/24 11:16:38  carlito
* *** empty log message ***
*
* Revision 1.26  2004/03/23 19:19:33  carlito
* *** empty log message ***
*
* Revision 1.25  2004/03/23 18:02:18  perki
* New WorkSHeet Panel model
*
* Revision 1.24  2004/03/23 13:39:19  perki
* New WorkSHeet Panel model
*
* Revision 1.23  2004/03/22 18:21:30  carlito
* *** empty log message ***
*
* Revision 1.22  2004/03/22 16:40:47  perki
* step 1
*
* Revision 1.21  2004/03/22 14:32:30  carlito
* *** empty log message ***
*
* Revision 1.20  2004/03/18 16:26:54  perki
* new option model
*
* Revision 1.19  2004/03/08 17:53:18  perki
* *** empty log message ***
*
* Revision 1.18  2004/03/08 15:40:48  perki
* *** empty log message ***
*
* Revision 1.17  2004/03/04 14:32:07  perki
* copy goes to hollywood
*
* Revision 1.16  2004/03/03 11:35:07  perki
* Un petit bateau
*
* Revision 1.15  2004/03/03 11:00:17  carlito
* *** empty log message ***
*
* Revision 1.14  2004/03/03 10:47:48  carlito
* *** empty log message ***
*
* Revision 1.13  2004/03/02 16:54:42  carlito
* Tarif viewer WorkSheetPanels as listener of workSheets
*
* Revision 1.12  2004/03/02 16:47:38  carlito
* *** empty log message ***
*
* Revision 1.11  2004/03/02 16:36:26  perki
* breizh cola. le cola du phare ouest
*
* Revision 1.10  2004/03/02 16:30:26  perki
* breizh cola. le cola du phare ouest
*
* Revision 1.9  2004/03/02 14:42:48  perki
* breizh cola. le cola du phare ouest
*
* Revision 1.8  2004/02/20 05:45:05  perki
* appris un truc
*
* Revision 1.7  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.6  2004/02/19 16:21:25  perki
* Tango Bravo
*
* Revision 1.5  2004/02/18 11:00:57  perki
* *** empty log message ***
*
* Revision 1.4  2004/02/17 18:00:57  perki
* les crocos
*
* Revision 1.3  2004/02/17 10:45:03  carlito
* new Tarif Wizard and WorkSheetTree adapted to WorkSheetPanel
*
* Revision 1.2  2004/02/17 09:40:06  perki
* zibouw
*
* Revision 1.1  2004/02/17 08:54:07  perki
* zibouw
*
* Revision 1.12  2004/02/17 08:50:22  perki
* zibouw
*
* Revision 1.11  2004/02/16 18:59:15  perki
* bouarf
*
* Revision 1.10  2004/02/16 13:07:53  perki
* new event model
*
* Revision 1.9  2004/02/06 15:16:58  perki
* *** empty log message ***
*
* Revision 1.8  2004/02/01 18:27:51  perki
* dimmanche soir
*
* Revision 1.7  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.6  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.5  2004/01/20 14:06:53  perki
* Et au fond du noir, le noir le plus profond une limiere ..
*
* Revision 1.4  2004/01/20 12:35:27  perki
* Prosper YOupla boum. c'est le roi du pain d'epice
*
* Revision 1.3  2004/01/20 11:17:31  perki
* A la recherche de la Foi
*
* Revision 1.2  2004/01/20 10:29:59  perki
* The Dispatcher Force be with you my son
*
* Revision 1.1  2004/01/19 19:22:20  perki
* Goldorak Go
*
*/
