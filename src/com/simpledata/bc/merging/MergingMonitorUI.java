/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * $Id: MergingMonitorUI.java,v 1.2 2007/04/02 17:04:27 perki Exp $
 */
package com.simpledata.bc.merging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.actions.ActionClearSimulation;
import com.simpledata.bc.datamodel.Tarif;
import com.simpledata.bc.datamodel.Tarification;
import com.simpledata.bc.datamodel.pair.Pairable;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.JListWithPanels;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SButton;
import com.simpledata.bc.uitools.TableTools;

import foxtrot.Job;
import foxtrot.Worker;

/**
 * A user interface to monitor 
 */
public class MergingMonitorUI 
			extends JInternalFrame
			implements Mergeator.FillMonitor {
	
	private static final Logger m_log =Logger.getLogger(MergingMonitorUI.class); 
	
	/** AutoImport parameter **/
	public static final String PARAM_AUTO_IMPORT="MergingMonitor:AutoImport";
	
	/** the destination tarification **/
	Tarification destinationT;
	
	/** the source tarification **/
	Tarification sourceT;
	
	/** the Enumeration containon the tarifs to process **/
	private Iterator/*<Proposition>*/ tarifsToProcess;

	/** the list of tarif that need to be processed **/
	private Tarif[] listOfTarifToProcess;
	
	/** the actual Proposition at Work **/
	private Proposition propAtWork;
	
	/** this boolean is set to true if autoImport on already choosen is on **/
	private boolean autoImportAlreadySee;
	
	/** the position in the Tarif List (-1 means not started yet)**/
	int position = -1;
	
	/** a Map that contains all TarifChooserElement keyed by TarifMatch */
	private HashMap/*<TarifMatch,TarifChooserElement>*/ tceMap;
	
	/**
	 * this map will contains node that will be auto imported<BR>
	 * For now it is used when an import has been done on a paired tarif
	 * then paired Tarifs of dest and src, will be kept to be autoImported
	 * <BR>
	 * key: src<BR>
	 * value: dest
	 */
	private HashMap/*<Tarif,Tarif>*/ autoImportTable ;
		
	//-------------- Grade Titles --------------//
	private static class GradeTitle {
		public int value; String title;
		public GradeTitle (int v, String t) { value = v; title = t ;}
		public String toString() {
			return Lang.translate(title);
		}
 	}
	
	/** the following text must be translated **/
	private static GradeTitle[] gradeTitles = new GradeTitle[] {
			new GradeTitle(100,"Perfect"),
			new GradeTitle(70,"Fair"),
			new GradeTitle(30,"Average"),
			new GradeTitle(0,"Poor")
	};
	
	/** the following text must be translated **/
	private static GradeTitle[] autoImportTitles = new GradeTitle[] {
			new GradeTitle(-1,"never. (Manual Import)"),
			new GradeTitle(100,"if perfect match"),
			new GradeTitle(70,"if better than fair"),
			new GradeTitle(30,"if better than average")
	};
													

	/**
	* construct a Meging Interface to control a merging process<BR>
	* @dest is the destination Tarification
	*/
	public MergingMonitorUI(
			Tarification dest,Tarification source,Component origin) {
		super("",true,false);
		autoImportAlreadySee = false;
		destinationT = dest;
		sourceT = source;
		tceMap = new HashMap/*<TarifMatch,TarifChooserElement>*/();
		autoImportTable = new HashMap/*<Tarif,Tarif>*/();
		initUI(origin);
	}

	
	//----------------------------- UI ------------------------------//
	
	
	/** UI component initializaion **/
	private void initUI(Component origin) {
		this.getContentPane().setLayout(new BorderLayout());
		
		// dimensions
		Dimension d = new Dimension(300,400);
		getSourceTree().setMinimumSize(d);
		getDestinationTree().setMinimumSize(d);
		Dimension d2 = new Dimension(500,400);
		getTarifChooser().setMinimumSize(d2);
		
		JSplitPane leftSP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				getSourceTree(),
				getChooserPanel(getTarifChooser()));
		leftSP.setResizeWeight(0.5);
		JSplitPane rightSP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftSP,
				getDestinationTree());
		rightSP.setResizeWeight(0.5);
		
		// center
		this.getContentPane().add(rightSP,BorderLayout.CENTER);
		
		
		//Bottom panel with Buttons
		JPanel jp =new JPanel(new FlowLayout(FlowLayout.CENTER,20,0));
		jp.add(getProgressBar());
		jp.add(getContinueButton());
		jp.add(getStopButton());
		this.getContentPane().add(jp,BorderLayout.SOUTH);
		
		this.setPreferredSize(new Dimension(800,400));
		
		ModalJPanel.warpJInternalFrame(
				this,origin,new Point(10,10),Resources.modalBgColor
		);
	}
	
	//---- source Tree --//
	private MergingMonitorUISourceTree sourceTree;
	/** get the tree that shows the source Tarifs **/
	private MergingMonitorUISourceTree getSourceTree() {
		if (sourceTree == null) {
			sourceTree = new MergingMonitorUISourceTree(sourceT);
			
			// remove selection of nodes
			sourceTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					Tarif temp = 
						MergingMonitorUISourceTree.getTarifFromTreePath(
							 e.getNewLeadSelectionPath()
							);
					
					if (propAtWork != null) {
						if (temp != propAtWork.tarifSource)
							sourceTree.setSelectedTarif(propAtWork.tarifSource);
					} else {
						sourceTree.setSelectedTarif(null);
					}
				}
			});
			
		}
		return sourceTree;
	}
	
	//---- Title Panels -----------//
	
	/** create center JPanel **/
	private JPanel getChooserPanel(Component chooser) {
		JPanel jp = new JPanel(new BorderLayout());
		// add the Merginfo Title with and Arrow
		JPanel header = new JPanel(new BorderLayout());
		header.add(new JLabel(Lang.translate("Import")),BorderLayout.NORTH);
		header.add(new JLabel(Resources.importArrow),BorderLayout.CENTER);
		
		// add the auto import controls
		JPanel auto = new JPanel(new BorderLayout());
		auto.add(new JLabel(Lang.translate("Auto Import :")),BorderLayout.WEST);
		auto.add(getAutoImport(),BorderLayout.CENTER);
		
		header.add(auto,BorderLayout.SOUTH);
		
		jp.add(header,BorderLayout.NORTH);
		jp.add(chooser,BorderLayout.CENTER);
		return jp;
	}

	/** create the left and right JPanels **/
//	private JPanel getInfoPanel(Tarification t,Component body) {
//		JPanel jp = new JPanel(new BorderLayout());
//		// add the information
//		JLabel jb = new JLabel(t.getTitle());
//		jb.setIcon(t.getHeader().getIcon());
//		
//		
//		jp.add(jb,BorderLayout.NORTH);
//		jp.add(body,BorderLayout.CENTER);
//		return jp;
//	}
	
	
	//-------------- auto import control ------//
	private JComboBox autoImport;
	private JComboBox getAutoImport() {
		if (autoImport == null) {
			autoImport = new JComboBox(autoImportTitles);
			Integer o = (Integer)BC.getParameter(PARAM_AUTO_IMPORT, 
			        Integer.class);
			if (o != null)  {
				int i = o.intValue();
				for (int j = 0; j < autoImport.getItemCount() ; j++) {
					if (((GradeTitle) autoImport.getItemAt(j)).value == i){
						autoImport.setSelectedIndex(j);
						break;
					}
				}
				
			}
			
			autoImport.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object ob = autoImport.getSelectedItem();
					if (ob == null || ! (ob instanceof GradeTitle)) return;
					BC.setParameter(PARAM_AUTO_IMPORT,
							new Integer(((GradeTitle) ob).value));
				}
			});

		}
		return autoImport;
	}
	
	//	---- destination Tree --//
	private MergingMonitorUISourceTree destTree;
	/** get the tree that shows the source Tarifs **/
	private MergingMonitorUISourceTree getDestinationTree() {
		if (destTree == null) {
			destTree = new MergingMonitorUISourceTree(destinationT);
			// apply selection on tree
			destTree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					Tarif temp = 
						MergingMonitorUISourceTree.getTarifFromTreePath(
							 e.getNewLeadSelectionPath()
							);
					setSelectedTarif(temp,false);
				}
			});
		}
		return destTree;
	}
	
	
//	--- continue Button --//
	private SButton stopButton;
	/** get the button that proposes to continue **/
	private JButton getStopButton() {
	    final MergingMonitorUI tthis = this;
		if (stopButton == null) {
		    stopButton = new SButton(Lang.translate("Stop"));
		    stopButton.setPreferredSize(new Dimension(100,20));
		    stopButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    if (ModalDialogBox.ANS_OK 
				            == ModalDialogBox.confirm(tthis,Lang.translate(
				            "Stop this operation")))
				        tthis.dispose();
					
				}
			});
			
		}
		return stopButton;
	}
	
	//--- continue Button --//
	private SButton continueButton;
	/** get the button that proposes to continue **/
	private JButton getContinueButton() {
		if (continueButton == null) {
			continueButton = new SButton(Lang.translate("Continue"));
			continueButton.setPreferredSize(new Dimension(100,25));
			continueButton.setEnabled(false);
			continueButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    Worker.post(new Job(){

                        public Object run()  {
                            commitProposition();
                            continueButton.grabFocus();
                            return null;
                        }});
					
				}
			});
			
		}
		return continueButton;
	}
	
	//--- progressbar --//
	private JProgressBar progressBar;
	/** get the progressbar, that shows up the process state **/
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar(0,100);
			progressBar.setMinimumSize(new Dimension(20,400));
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			progressBar.setString("?/?");
		}
		return progressBar;
	}
	
	
	//--- chooser ---//
	private JListWithPanels tarifChooser;
	/** get the component that permit to choose a Tarif **/
	private JListWithPanels getTarifChooser() {
		if (tarifChooser == null) {
			tarifChooser = new JListWithPanels();
			tarifChooser.setSelectionOnAdd(false);
			tarifChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				JPanel selected = tarifChooser.getSelectedPanel();
					if (selected == null || 
					        ! (selected instanceof TarifChooserElement)) {
						setSelectedTarif((TarifMatch) null,false);
					} else {
						TarifMatch t = 
							((TarifChooserElement) selected).tarifMatch;
						setSelectedTarif(t,false);
					}
				}
			});
		}
		return tarifChooser;
	}
	
	/** a class that defines object in chooser **/
	class TarifChooserElement extends JPanel {
		public TarifMatch tarifMatch;
		public TarifChooserElement(TarifMatch tm,Color bgColor) {
			super(new BorderLayout());
			setBackground(bgColor);
			JProgressBar jpb = new JProgressBar(0,100);
			jpb.setBackground(bgColor);
			
			jpb.setBorderPainted(false);
			jpb.setBorder(new EmptyBorder(3,3,3,3));
			jpb.setPreferredSize(new Dimension(120,20));
			jpb.setValue((int) tm.getGrade());
			jpb.setStringPainted(true);
			jpb.setString(getGradeTitle((int) tm.getGrade()));
			JLabel jb = new JLabel(tm.toString());
			
			// debug line enabled only if isSImple
			if (BC.isSimple()) {
			    jb.setToolTipText(tm.toStringDebug());
			}
			
			jb.setBackground(bgColor);
			add(jpb,BorderLayout.WEST);
			add(jb,BorderLayout.CENTER);
			tarifMatch = tm;
		}
	}
	
	// -------------- grade titles -----------------------------//
	
	public static String getGradeTitle(int grade) {
		for (int i = 0; i < gradeTitles.length ; i++) {
			if (grade >= gradeTitles[i].value) 
				return Lang.translate(gradeTitles[i].title.toString());
		}
		
		return "....";
	}
	
	
	//------------------- selection handlers ---------------------//
	
	
	/** memory to keep the last selected Tarif **/
	private TarifMatch selectedTarif;
	
	/** 
	 * set the selected Tarif (can be null) <BR>
	 * will show the selection in the componentS that shows this tarifs
	 * @param isAutoImport set to true, 
	 * if this setSelection was done by autoImport
	 **/
	private void setSelectedTarif(Tarif t,boolean isAutoImport) {
	    if (t== null) {
	        setSelectedTarif((TarifMatch) null,isAutoImport);
	    }
	    
	    
		// look for the corresponding TarifMatch
		// scan tceMap for a TarifMatch with this Tarif
		Iterator/*<TarifMatch>*/ i = tceMap.keySet().iterator();
		TarifMatch temp = null;
		while (i.hasNext()) {
			temp = (TarifMatch) i.next();
			if (temp.getTarif() == t) {
				setSelectedTarif(temp,isAutoImport);
				return;
			}
		}
		m_log.warn("Cannot find corresponding tarif match");
		setSelectedTarif((TarifMatch) null,isAutoImport);
	}
	
	/** 
	 * set the selected Tarif (can be null) <BR>
	 * will show the selection in the componentS that shows this tarifs
	 *  @param isAutoImport set to true, 
	 * if this setSelection was done by autoImport
	 **/
	private void setSelectedTarif(TarifMatch t,boolean isAutoImport) {
		// prevent continue to be disabled if no proposition
		if (tceMap.size() == 0) {
			getContinueButton().setEnabled(true);
		}
		
		if (t == selectedTarif) return; //avoid non necessary operations
		selectedTarif = t;
		
		if (isAutoImport) return; // no need to update the UI
		
		getTarifChooser().setSelectedPanel(
				t == null ? null :
				(TarifChooserElement) tceMap.get(t));
		
		getContinueButton().setEnabled(selectedTarif != null);
		
		// update the destination Tree
		getDestinationTree().setSelectedTarif(
				selectedTarif == null ? null : selectedTarif.getTarif());
	}
	
	//---------------- wait and continues ---------------//
	
	
	/**
	 * fill actually selected Tarif with the proposition
	 */
	private synchronized void commitProposition() {
	    
	    if (propAtWork != null && selectedTarif != null) {
	        // fill tarif with choosen proposition
			selectedTarif.fillWithTarif(propAtWork);
			
			
			// if this proposition and destination are paired then rememeber 
			// the choice made
			if (selectedTarif.getTarif() instanceof Pairable &&
			        propAtWork.tarifSource instanceof Pairable) {
			    Tarif dest=((Pairable) selectedTarif.getTarif()).pairedGet();
			    Tarif src =((Pairable) propAtWork.tarifSource).pairedGet();
			    if (dest != null && src != null) {
			        // remember this pairing for auto Import
			        autoImportTable.put(src,dest);
			    }
			}
			
			
		} else {
		    // DID NOT IMPORT BECAUSE:
		    m_log.warn("propAtWork: "+propAtWork+" " +
		    		"selectedtarif:"+selectedTarif);
		}
	    nextTarif();
	    
	}
	
	
	
	
	
	/** 
	 * continue button has been hitten. 
	 * free the Time wainting<BR>
	 * Give the answer to the Mergeator 
	 **/
	public synchronized void nextTarif() {
	    if (tarifsToProcess == null || ! tarifsToProcess.hasNext()) {
	        this.dispose();
	        return;
	    }
	    
	    propAtWork = (Proposition) tarifsToProcess.next();
	    
	    
	    // move the progressBar
	    position++;
	    if (listOfTarifToProcess.length > 0) {
	        int prog = (position * 100) / listOfTarifToProcess.length;
	        getProgressBar().setString(
	                position+"/"+listOfTarifToProcess.length);
	        getProgressBar().setValue(prog);
	    }
	    
	    
	    //		----------------- auto import -----------------------//
	    
	    
	    
	    
	    //	  autoImport only if not first loop
	   
	    if (firstTarif) {
	        firstTarif = false;
	    } else {
	        TarifMatch t = checkAutoImportTarif();
	        if (t!=null) {
		        setSelectedTarif(t,true);
		        commitProposition();
	        }
	    }
	   
	  
	    // ---------update trees-----------
	    
	    // update left tree
	    getSourceTree().setSelectedTarif(propAtWork.tarifSource);
	    
	    // update the right tree
	    ArrayList/*<Tarif>*/ matchT = new ArrayList/*<Tarif>*/();
	    for (int i = 0; i < propAtWork.matches.length; i++) {
	        matchT.add(propAtWork.matches[i].getTarif());
	    }
	    getDestinationTree().setTarifs(matchT);
	  
	    
	    
	    m_log.info( "GOING FOR :"+propAtWork.tarifSource );
	    //----------------------------------//
	    
	    
	    
	    
	    
	    getTarifChooser().removeAll();
	    
	    getContinueButton().setEnabled(false);
	    tceMap.clear();
	    
	    Color init = UIManager.getColor("Label.background");
	    Color[] backgrounds = TableTools.getColors(init,20);
	    int ci = 0;
	    
	    float lastGrade = 0;
	    
	    // separator added
	    boolean addedSep = false;
	    
	    //fill the Panel with TarifMatches
	    for (int i = 0; i < propAtWork.matches.length ; i++) {
	        if (lastGrade != propAtWork.matches[i].getGrade()) {
	            lastGrade = propAtWork.matches[i].getGrade();
	            ci++;
	            if (ci > (backgrounds.length - 1)) ci = 0;
	            
	        }
	        
	        if ((!addedSep) && (! propAtWork.matches[i].isKnown())) {
	            addedSep = true;
	            if (i > 0) {
	                JPanel jp = new JPanel(new BorderLayout());
	                JPanel sep = new JPanel();
	                sep.setLayout(new BoxLayout(sep,BoxLayout.Y_AXIS));
	                JLabel jl = new JLabel(
	                Lang.translate(
	                  "Proposition above have already been choosen once.")        
	                );
	                jl.setFont(
	                        jl.getFont().deriveFont(Resources.fontSmall()));
	                
	                sep.add(jl);
	                final JCheckBox jcb = new JCheckBox(
	                        Lang.translate("Auto Import if already choosen.")
	                        );
	                jcb.setSelected(autoImportAlreadySee);
	                jcb.setFont(
	                        jcb.getFont().deriveFont(Resources.fontSmall()));
	                
	                jcb.addActionListener(new ActionListener(){

                        public void actionPerformed(ActionEvent e) {
                            autoImportAlreadySee = jcb.isSelected();
                            setSelectedTarif(
                                    propAtWork.matches.length > 0 ? 
                            	            propAtWork.matches[0] : null
                            	            ,false
                            );
                        }});
	                
	                
	                sep.add(jcb);
	                jp.add(sep);
	                sep.add(new JSeparator());
	                
	                //sep.setMinimumSize(new Dimension(10,80));
	                getTarifChooser().addPanel(jp);
	            }
	        }
	        
	        
	        TarifChooserElement tce = new TarifChooserElement(
	                propAtWork.matches[i],backgrounds[ci]
	        );
	        tceMap.put(propAtWork.matches[i],tce);
	        //m_log.info( "added "+propAtWork.matches[i] );
	        getTarifChooser().addPanel(tce);
	    }
	    
	    
	    
	    // set the first Tarif as selected 
	    setSelectedTarif(propAtWork.matches.length > 0 ? 
	            propAtWork.matches[0] : null
	            ,false);
	    
	}
	/** even if autoImport matches.. we wait one step */
	private boolean firstTarif = true;
	
	
	/** 
	 * method that checks if i can auto Import a Tarif from the propositions
	 * @return the Tarif to import, or null if none
	 * **/
	private TarifMatch checkAutoImportTarif() {
	    
	    // CHECK ID I HAVE AUTO IMPORT IN MY CACHE 
	    Tarif potential = (Tarif) autoImportTable.get(propAtWork.tarifSource);
	    if (potential != null) {
	        // check if i can find it my proposition table
	        for (int i = 0; i < propAtWork.matches.length; i++) {
	            if (propAtWork.matches[i].getTarif() == potential)
	                return propAtWork.matches[i];
	        }
	    }
	    
	    // AUTO FROM Already choosen
	    if (autoImportAlreadySee)
	    {
	        TarifMatch alreadyChoosen = null;
	        for (int i = 0; i < propAtWork.matches.length ; i++) {
	            if (propAtWork.matches[i].isKnown()) {
	                if (alreadyChoosen != null) {
	                    alreadyChoosen = null;
	                    break;
	                }
	                alreadyChoosen = propAtWork.matches[i];
	                    
	            }
	        }
	        if (alreadyChoosen != null)
	            return alreadyChoosen;
	    }
	    
	    
	    // AUTO FROM GRADING
	    boolean doAutoImport = false;
	    
	    
	    Integer o = (Integer)BC.getParameter(PARAM_AUTO_IMPORT, Integer.class);
	    int autoImportInt = -1;
	    if (o != null) { 
	        autoImportInt = ((Integer) o).intValue();
	    }
	    if (autoImportInt > -1) {
	        // check that first proposition is acceptable 
	        if (propAtWork.matches.length > 0) {
	            if (propAtWork.matches[0].getGrade() >= autoImportInt) {
	                // ok passed one.. now verify there are no twins
	                if (propAtWork.matches.length > 1) {
	                    if (propAtWork.matches[0].getGrade() >
	                            propAtWork.matches[1].getGrade()) {
	                        // ok next tarif is different than me
	                        doAutoImport = true;
	                    }
	                } else {
	                    // import I'm the only match
	                    doAutoImport = true;
	                }
	            }
	        }
	    }
	    
	    
	    
	    if (doAutoImport) {
	        return propAtWork.matches.length > 0 ? 
	                propAtWork.matches[0] : null;
	    }
	    
	    
	    
	    return null;
	    
	}
	
	
	//--------------------implements Mergeator.FillMonitor------------------//
	/**
	 * <B>Implements Mergeator.FillMonitor</B><BR>
	 * Called at start
	 * @see com.simpledata.bc.merging.Mergeator.FillMonitor
	 * #init(com.simpledata.bc.datamodel.Tarif[])
	 */
	public void init(Tarif[] listOfTarifToProcess) {
		this.listOfTarifToProcess = listOfTarifToProcess;
		getSourceTree().setTarifs(listOfTarifToProcess);
		if (listOfTarifToProcess.length == 0) {
			ModalDialogBox.alert(BC.bc.getMajorComponent(), Lang.translate(
			"There is nothing to import from the selected portfolio"));
			return;
		}
		
		
		String[] buttons = new String[] {
		        Lang.translate("Replace"), Lang.translate("Sum")
		};
		String message =  "<HTML>"+
		    Lang.translate("The actual data may either be " +
				"REPLACED or SUMMED with the imported portfolio.")+"<HTML>";
		int result = ModalDialogBox.custom(this,message,buttons,
		        UIManager.getIcon("OptionPane.questionIcon"));
		
		
		// REPLACE (clean)
		if (result == 0) {
		   ActionClearSimulation.doAction(destinationT);
		}
		
	}
	
	
	/**
	 * <B>Implements Mergeator.FillMonitor</B><BR>
	 * Starting Tarif processing
	 * @see com.simpledata.bc.merging.Mergeator.FillMonitor
	 * #startProcessOf(com.simpledata.bc.datamodel.Tarif)
	 */
	public void startProcessOf(Tarif tarifSource) {
		position++;
		if (listOfTarifToProcess.length > 0) {
		    
			getProgressBar().
			setValue((position*100)/listOfTarifToProcess.length);
			getProgressBar().setString(
			position+"/"+listOfTarifToProcess.length);
			
		}
	}
	
	
	/**
	 * <B>Implements Mergeator.FillMonitor</B><BR>
	 * I must return the Tarif i do prefer in those proposition
	 * @see com.simpledata.bc.merging.Mergeator.FillMonitor
	 * #chooseMapping(ArrayList)
	 */
	public synchronized void 
		chooseMapping(ArrayList/*<Proposition>*/ props) {
		tarifsToProcess = props.iterator();
		// reset position
		position = 0;
		
		nextTarif();
		
	}
	
	/** quit override super.dispose() **/
	public void dispose() {
	    MergingMemory.save();
	    super.dispose();
	}
	
}

/*
 * $Log: MergingMonitorUI.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.22  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.21  2004/11/16 17:22:11  perki
 * Merging now remembers of last picks
 *
 * Revision 1.20  2004/10/19 16:59:35  perki
 * *** empty log message ***
 *
 * Revision 1.19  2004/10/16 06:51:41  perki
 * Smart Auto Import for paired Tarifs
 *
 * Revision 1.18  2004/10/08 14:56:04  perki
 * Better Matching / merging logic
 *
 * Revision 1.17  2004/09/28 17:19:59  perki
 * *** empty log message ***
 *
 * Revision 1.16  2004/09/28 08:55:22  perki
 * Minor changes
 *
 * Revision 1.15  2004/09/24 10:38:05  perki
 * Better Merginging UI
 *
 * Revision 1.14  2004/09/24 10:08:28  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/09/23 11:00:48  jvaucher
 * Improved filechooser rendering
 *
 * Revision 1.12  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.11  2004/08/01 09:56:44  perki
 * Background color is now centralized
 *
 * Revision 1.10  2004/07/26 16:46:09  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/07/22 15:12:35  carlito
 * lots of cleaning
 *
 * Revision 1.8  2004/07/19 12:25:03  perki
 * Merging finished?
 *
 * Revision 1.7  2004/07/16 19:02:36  perki
 * About ready with merging
 *
 * Revision 1.6  2004/07/16 18:39:20  perki
 * *** empty log message ***
 *
 * Revision 1.5  2004/07/15 17:49:56  perki
 * grading better
 *
 * Revision 1.4  2004/07/15 07:49:57  perki
 * *** empty log message ***
 *
 * Revision 1.3  2004/07/12 17:34:31  perki
 * Mid commiting for new matching system
 *
 * Revision 1.2  2004/07/09 20:53:31  perki
 * Merging UI step 1.5
 *
 * Revision 1.1  2004/07/09 20:25:02  perki
 * Merging UI step 1
 *
 */