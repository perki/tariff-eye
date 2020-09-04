/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 16 juin 2004
 * $Id: DispatcherCasePanel.java,v 1.2 2007/04/02 17:04:24 perki Exp $
 */
package com.simpledata.bc.uicomponents.worksheet.dispatcher;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import org.apache.log4j.Logger;

import com.simpledata.bc.Resources;
import com.simpledata.bc.components.bcoption.*;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherCase;
import com.simpledata.bc.datamodel.*;
import com.simpledata.bc.datamodel.event.*;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.TarifViewer;
import com.simpledata.bc.uicomponents.bcoption.OptionCasePanel;
import com.simpledata.bc.uicomponents.bcoption.viewers.*;
import com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel;
import com.simpledata.bc.uitools.SButton;


/**
 * This Panel is the representation of a mutiple choice option panel
 * 
 */
public class DispatcherCasePanel 
			extends WorkSheetPanel {
    
    private static final Logger m_log 
    		= Logger.getLogger( DispatcherCasePanel.class );
    
    private MyOptionPanel optionPanel;
    
    private JPanel selectedWorkSheetPanel;
    
    private WSComboChooser actionCombo;
    
    
    /** my Icon **/
	public static ImageIcon defaultClassTreeIcon = Resources.wsDispatcherCase;
    
    /**
     * @param d
     * @param tv
     */
    public DispatcherCasePanel(DispatcherCase d, TarifViewer tv) {
        super(d, tv);
        
        DispatcherCase dcTemp = getDispatcherCase();
        if (dcTemp != null) {
            refresh();
        }
        
        initEventListener();
    }
    
    protected DispatcherCase getDispatcherCase() {
        WorkSheet ws = this.getWorkSheet();
        return (DispatcherCase)(ws);
    }
    
    
    public JPanel getOptionPanel() {
        if ( getDisplayController().getEditWorkPlaceState() == 
            WorkSheetPanel.WSIf.EDIT_STATE_NONE ) {
            // We are in simulation mode
            // option shall not be shown
            return null;
        }
        if (optionPanel == null) {
            optionPanel = new MyOptionPanel(this);
        }
        return optionPanel;
    }

    public void save() {}

    protected ImageIcon getTreeIcon() {
        return Resources.wsDispatcherCase;
    }
    
    /**
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#getContents()
     */
    public JPanel getContents() {
        if (selectedWorkSheetPanel == null) {
            selectedWorkSheetPanel = new JPanel();
            selectedWorkSheetPanel.setLayout(new BorderLayout());
        }
            
        return selectedWorkSheetPanel;

    }

	/**
	 * return a JPanel to be included in the border (tool bar)<BR>
	 * Overide this method if needed;
	 */
    public JPanel getActionPanel() {
        if (actionCombo == null) {
            // create it
            if ( getDisplayController().getEditWorkPlaceState() == 
                WorkSheetPanel.WSIf.EDIT_STATE_NONE ) {
                // We are in simulation mode, we choose to display name
                actionCombo = new WSComboChooser(this,true);
            } else {            
                actionCombo = new WSComboChooser(this,false);
            }
        }
        return actionCombo;
    }
    
    /**
     * @see com.simpledata.bc.uicomponents.worksheet.WorkSheetPanel#refresh()
     */
    public void refresh() {
        getContents().removeAll();
        DispatcherCase dcTemp = getDispatcherCase();
        if (dcTemp != null) {
            OptionCase ocTemp = dcTemp.getOptionCase();
            if (ocTemp != null) {
                String key = ocTemp.getSelectedKey();
                WorkSheet ws = dcTemp.getWorkSheetAt(key);
                if (ws != null) {
                    WorkSheetPanel wsp = getDisplayController().getWorkSheetPanel(ws);
                    if (wsp != null) {
                        //wsp.showWorkSheetPanelBorder(false);
                        
                        getContents().add(wsp.getPanel(), BorderLayout.CENTER);
                    }
                } else {
                	m_log.warn("the dispatcher child ws key is null.");
                }
            }
        }
        getContents().revalidate();
        getContents().repaint();
    }

    
	/** init my Event listner **/
	private void initEventListener() {
		if (getWorkSheet() == null) return;
		myEventListener = new MyEventListener(this);
		getWorkSheet().addNamedEventListener(
				myEventListener,NamedEvent.WORKSHEET_OPTION_DATA_CHANGED,null);
	}
	
	private MyEventListener myEventListener;
	class MyEventListener  implements NamedEventListener {
	    
	    private DispatcherCasePanel owner;
	    
	    public MyEventListener(DispatcherCasePanel dcp) {
	        this.owner = dcp;
	    }
	    
		public void eventOccured(NamedEvent e) {
			if ( e.getSource() != getWorkSheet()) return;
			if ( ! (e.getUserObject() instanceof OptionCase)) return;
			OptionCase ob = (OptionCase) e.getUserObject();
			ArrayList opts = this.owner.getWorkSheet().getOptions(
					OptionCase.class);
			if (opts.size() > 0) {
			    OptionCase myOb = (OptionCase)opts.get(0);
				if (myOb == ob) {
				    this.owner.refresh();
				    
				    // Updating dispatcher title
				    // TODO refine to avoid title change on every option change
				    // such as different case selection
//				    m_log.warn("Changing dispatcher Title");
//					owner.getDispatcherCase().setTitle(ob.getTitle());
				}
			}
		}
		
	}
	
	/**
	 * A specific option panel for the dispatcher case
	 */
	static class MyOptionPanel extends JPanel implements NamedEventListener, 
		OptionsViewerInterface {

	    private DispatcherCasePanel owner;
	    private NoOptionPanel noOptionPanel;
	    private OptionCasePanel optionPanel;
	    	    
	    public MyOptionPanel(DispatcherCasePanel dad) {
	        this.owner = dad;

	        this.setLayout(new GridBagLayout());
	        this.owner.getDispatcherCase().addNamedEventListener(this,
	               -1, DispatcherCase.class);
	        refresh();
	    }
	    
	    /**
	     * Event management refresh whenever
	     * an option has been removed or added
	     */
	    public void eventOccured(NamedEvent e) {
	        int code = e.getEventCode();
	        if (e.getSource() == this.owner.getDispatcherCase()) {
	            if ((code == NamedEvent.WORKSHEET_OPTION_ADDED)
	                    || (code == NamedEvent.WORKSHEET_OPTION_REMOVED)) {
	                refresh();
	            }
	        }
        }
	    
        /**
         * Called whenever a graphical refresh is needed
         */
        private void refresh() {
            
            JPanel jp = null;
            
            ArrayList opts = this.owner.getWorkSheet().getOptions(
					OptionCase.class);
            int size = opts.size();
			if (size == 0) {
			    // Panel without any option...
			    jp = getNoOptionPanel();
			} else if (size == 1) {
			    // Panel with option...
			    OptionCase opt = null;
			    try {
			        opt = (OptionCase)opts.get(0);
			    } catch (ClassCastException cce) {
			        m_log.error(
			        "Dispatcher case contained an option of wrong type..",cce );
			    }
			    
			    if (opt == null) {
			        jp = getNoOptionPanel();
			    } else {
			        jp = getOptionPanel(opt);
			    }
			} else {
			    m_log.error(
			      "We have a DispatcherCase with more than one optionCase...");
			    jp = new JPanel(new BorderLayout());
			    jp.add(new JLabel(Lang.translate("Dispatcher Case contains more than one option...")));
			    // TODO maybe order a clean to the DispatcherCase itself
			}
			
		    // Replace content			
		    this.removeAll();
			
			// Constructing contents (add every non null Panel...)
		    GridBagConstraints gb = new GridBagConstraints();
		    gb.fill = GridBagConstraints.HORIZONTAL;
		    gb.weightx = 1.0;
		    gb.insets = new Insets(2, 5, 2, 5);
		    
		    this.add(jp, gb);
        }
        
        /**
         * Returns the existing OptionCasePanel
         * Should not be given a null option
         * @param opt the option we are seeking
         */
        private JPanel getOptionPanel(OptionCase opt) {
            assert (opt != null) : "OptionCase should not be null";
            boolean haveToRecreate = false;
            if (this.optionPanel == null) {
                haveToRecreate = true;
            } else {
                // We check the option
                OptionCase oc = (OptionCase)this.optionPanel.getOption();
                if (opt != oc) {
                    // The option has changed
                    // We have to recreate the panel
                    haveToRecreate = true;                    
                }
            }
            
            if (haveToRecreate) {
                this.optionPanel = new OptionCasePanel(this.owner.getOptionStateControler(), 
                        (OptionCase)opt);
            }
            
            return this.optionPanel;
        }

        /**
         * Returns the noOptionPanel and will create it if it does not exist
         * @return
         */
        private NoOptionPanel getNoOptionPanel() {
		    if (this.noOptionPanel == null) {
		        // Create it
		        this.noOptionPanel = new NoOptionPanel(this);
		    }
		    return this.noOptionPanel;
        }
        
        
        static class NoOptionPanel extends JPanel {
            private MyOptionPanel owner;
            private JLabel creationLabel;
            private SButton creationButton;
            
            
            public NoOptionPanel(MyOptionPanel dad) {
                this.owner = dad;
                initComponents();
            }
            
            private void initComponents() {
                creationLabel = new JLabel();
                creationButton = new SButton();
                
                this.setLayout(new GridBagLayout());
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                
                //this.setBackground(new Color(153, 153, 153));
                creationLabel.setHorizontalAlignment(SwingConstants.CENTER);
                creationLabel.setLabelFor(creationButton);
                creationLabel.setText(Lang.translate("Select or create an option"));
                
                gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new Insets(2, 5, 2, 5);
                this.add(creationLabel, gridBagConstraints);

                creationButton.setText(Lang.translate("create"));
                creationButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        creationButtonActionPerformed(evt);
                    }
                });

                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.insets = new Insets(2, 5, 2, 5);
                this.add(creationButton, gridBagConstraints);
            }
            
            /**
             * Called when the button is pressed to call the little
             * Option Popup
             */
            private void creationButtonActionPerformed(ActionEvent evt) {
                JPopupMenu popup = OptionsViewer.OptionMenu.getPopupMenu(this.owner);
                popup.show(this.creationButton, 0 ,0);
            }
            
        }

        public WorkSheet getWorkSheet() {
            WorkSheet res = null;
            if (this.owner != null) {
                res = this.owner.getWorkSheet();
            }
            return res;
        }

        public void createOption(Class c) {
            if (c != OptionCase.class) {
                m_log.error("Cannot create another type of option " +
                		"than OptionCase, asked for : '"+c+"'");
            }
            OptionManager.createOption(getWorkSheet(), c);
        }

        public void addRemoteOption(BCOption option) {
            if (option != null) {
                OptionManager.addRemoteOptionTo(getWorkSheet(), option);
                // process the added cases
                assert (option instanceof OptionCase) :
                	"The remote option should be on option case.";
                OptionCase oc = (OptionCase) option;
                DispatcherCase dc = owner.getDispatcherCase();
                int size = oc.getNumberOfCases();
                for (int i=0; i<size; i++) {
                	String key = oc.getKeyForPos(i);
                	dc.optionChanged(OptionCase.CASE_ADDED,key);
                }
            }	else {
                m_log.error("You would not want to add a remote " +
                		"option which is null..!!");
            }
        }
        
	}


	/** 
	 * A class linking a ConboBox to the dispatcher case worksheets<br>
	 * meant to be displayed as actionPanel in the Border.... 
	 */
	static class WSComboChooser extends JPanel implements NamedEventListener {

	    private DispatcherCasePanel owner;
	    
	    private JComboBox combo;
	    private JLabel nameLabel;
	    
	    private boolean displayName = false;
	    
	    
	    
	    public WSComboChooser(DispatcherCasePanel daddy, boolean displayName) {
	        owner = daddy;
	        this.displayName = displayName;
	        initComponents();
	    }
	    
        /**
         * @see com.simpledata.bc.datamodel.event.NamedEventListener#eventOccured(com.simpledata.bc.datamodel.event.NamedEvent)
         */
        public void eventOccured(NamedEvent e) {
            if ( e.getSource() != owner.getWorkSheet()) return;

            // We shall have received a WorksheetData Changed Event
            // Therefore we order a refresh on the combo
            // TODO refine to narrow refresh firing
            refreshCombo();
        }
        
        private void initComponents() {
            setLayout(new BorderLayout());
            setOpaque(false);
            
            nameLabel = new JLabel();
            nameLabel.setOpaque(false);
            Font f = nameLabel.getFont().deriveFont(Font.ITALIC);
            nameLabel.setFont(f);
            
            add(nameLabel, BorderLayout.WEST);
            
            combo = new JComboBox();
            combo.setOpaque(false);
            combo.setRenderer(new WSComboChooserRenderer(this));
            combo.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    doSelection();
                }
                
            });
            refreshCombo();
            owner.getWorkSheet().addNamedEventListener(
    				this,NamedEvent.WORKSHEET_OPTION_DATA_CHANGED,null);
            this.add(combo, BorderLayout.CENTER);
        }
	    
        private boolean refreshing = false;
        private void refreshCombo() {
            refreshing = true;
            
            nameLabel.setText("");
            
            combo.removeAllItems();
            OptionCase oc = owner.getDispatcherCase().getOptionCase();
            if (oc != null) { 
                
                if (displayName) {
                    nameLabel.setText("- "+oc.getTitle()+" :");
                }
                //m_log.warn("We found an option");
                int len = oc.getNumberOfCases();
                String selKey = oc.getSelectedKey();
                String curKey;
                for (int i=0; i< len; i++) {
                    curKey = oc.getKeyForPos(i);
                    if (curKey != null) {
                        combo.addItem(curKey);
                        if (curKey.equals(selKey)) {
                            combo.setSelectedIndex(i);
                        }
                    }
                }
            } else {
                //m_log.warn("We did not find any option");
            }
            revalidate();
            repaint();
            refreshing = false;
        }
        
        private int getSelectedIndex() {
            if (combo != null) {
                return combo.getSelectedIndex();
            }
            return -1;
        }
        
        private void doSelection() {
            if (refreshing) return;
            String key = (String)combo.getSelectedItem();
            if (key != null) {
                OptionCase oc = owner.getDispatcherCase().getOptionCase();
                String selKey = oc.getSelectedKey();
                if (!key.equals(selKey)) {
                    // We want to select a new option....
                    oc.setSelectedCase(key);
                }
            }
        }
        
        /** 
         * Returns the WorkSheetPanel at index index
         * @param index
         * @return
         */
        protected WorkSheetPanel getWorkSheetPanelAt(int index) {
            WorkSheetPanel wsp = null;
            if (index > -1) {
                WorkSheet ws =  owner.getDispatcherCase().getWorkSheetAtIndex(index);
                if (ws != null) {
                    wsp = owner.getDisplayController().getWorkSheetPanel(ws);
                }
            }
            return wsp;
        }
        
        
        /** 
         * The renderer for the comboBox
         */
        static class WSComboChooserRenderer extends DefaultListCellRenderer {

            private WSComboChooser owner;
            
            public WSComboChooserRenderer(WSComboChooser daddy) {
                super();
                owner = daddy;
            }
            
            /**
             * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
             */
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Modify the entering value
                
                JLabel jb = (JLabel)super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                WorkSheetPanel wsp = owner.getWorkSheetPanelAt(index);
                if (wsp == null) {
                    // We could be displaying the combo in non popup mode...
                    wsp = owner.getWorkSheetPanelAt(owner.getSelectedIndex());
                }
                if (wsp != null) {
                    //m_log.warn("We should have rendered icon and text ... "+cellHasFocus);
                    jb.setIcon(wsp.getIcon());
                    jb.setText(wsp.getWorkSheet().getTitle());
                }
                return jb;
            }
            
        }
        
	}
	

	
}


/*
 * $Log: DispatcherCasePanel.java,v $
 * Revision 1.2  2007/04/02 17:04:24  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:38  perki
 * First commit on sourceforge
 *
 * Revision 1.25  2005/01/24 11:49:17  jvaucher
 * Issue#67/#68: OptionCase re-use problem
 * Issue#69: Tariff Delete bug.
 * Issue#66: Description dans les dispatcher sur le TarificationReport
 *
 * Revision 1.24  2004/11/23 10:24:03  carlito
 * Auto naming disabled... added option name in action panel in simulation mode
 *
 * Revision 1.23  2004/11/22 18:44:39  carlito
 * Affichage des DispatcherCase modifications quasi-finale
Modification du WorkSheetPanelBorder pour qu'il n'ai plus la bougeotte avec le titre...
 *
 * Revision 1.22  2004/11/17 18:28:09  carlito
 * arggg
 *
 * Revision 1.21  2004/11/17 15:26:22  carlito
 * New design for WorkSheetPanel, WorkSheetPanelBorder and DispatcherCasePanel advanced
 *
 * Revision 1.20  2004/11/15 18:54:04  carlito
 * DispatcherIf removed... workSheet and option cleaning...
 *
 * Revision 1.19  2004/11/15 11:33:57  carlito
 * bug w case solved
 *
 * Revision 1.18  2004/11/12 14:28:39  jvaucher
 * New NamedEvent framework. New bugs ?
 *
 * Revision 1.17  2004/11/09 18:29:26  carlito
 * Dispatcher case upgraded according to issue 43
 *
 * Revision 1.16  2004/10/19 16:57:18  carlito
 * dispatcher case upgraded
some catchs in CompactTarifLinkNode
 *
 * Revision 1.15  2004/10/01 15:00:05  carlito
 * Dispatcher case now refresh on option changes
 *
 * Revision 1.14  2004/09/23 06:27:56  perki
 * LOt of cleaning with the Logger
 *
 * Revision 1.13  2004/09/08 16:35:15  perki
 * New Calculus System
 *
 * Revision 1.12  2004/09/07 13:40:42  carlito
 * *** empty log message ***
 *
 * Revision 1.11  2004/09/07 13:35:03  carlito
 * *** empty log message ***
 *
 * Revision 1.10  2004/09/07 09:18:40  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/08/05 00:23:44  carlito
 * DispatcherCase bugs corrected and aspect improved
 *
 * Revision 1.8  2004/07/31 11:06:55  perki
 * Still have problems with the progressbar
 *
 * Revision 1.7  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.6  2004/07/08 14:59:00  perki
 * Vectors to ArrayList
 *
 * Revision 1.5  2004/07/01 14:45:14  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/06/28 19:25:42  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/06/28 12:48:49  carlito
 * Dispatcher case++
 *
 * Revision 1.2  2004/06/23 18:33:13  carlito
 * Tree orderer
 *
 * Revision 1.1  2004/06/16 10:17:00  carlito
 * *** empty log message ***
 *
 */