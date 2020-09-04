/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.log4j.Logger;

import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datatools.AutosaveTask;
import com.simpledata.bc.datatools.FileManagement;
import com.simpledata.bc.help.Helper;
import com.simpledata.bc.tarifmanager.ListLoaderHTTP;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uicomponents.PreferencePanel;
import com.simpledata.bc.uicomponents.money.CurrencyRatesManager;
import com.simpledata.bc.uitools.About;
import com.simpledata.bc.uitools.BCLookAndFeel;
import com.simpledata.bc.uitools.Console;
import com.simpledata.bc.uitools.InternalFrameDescriptor;
import com.simpledata.bc.uitools.MiniBrowser;
import com.simpledata.bc.uitools.ModalDialogBox;
import com.simpledata.bc.uitools.SDesktopPane;
import com.simpledata.bc.webcontrol.TariffEyeQuery;

/**
* Desktop and Application manager
*/
public class Desktop extends JFrame{
	
	private static final Logger m_log = Logger.getLogger( Desktop.class ); 
	
	/** Locale */
	private static final String RECOVER_MSG = "It seems that your last "+
	   "Tariff Eye session had not "+
	   "terminated correcty and some work hasn't been saved. Would you like to "+
	   "continue this work now, to continue the next time you open Tariff Eye "+
	   "or to drop this work ?";
	
	private static final String MSG_EXPIRED = "Your subscription to Tariff Eye"+
	   " tariffs has expired. Purchase new subscription, by visiting the "+
	   " tariffeye.com website.";
    
	
	/** the name of the Creation Tool.. to be translated by lang **/
	public static final String MENU_TITLE_CREATION = "Creation";
	/** the name of the Creation Live Tool.. to be translated by lang **/
	public static final String MENU_TITLE_CREATION_LIVE = "Creation Gold";
	/** the name of the Simulation Tool.. to be translated by lang **/
	public static final String MENU_TITLE_SIMULATION = "Simulation";
    
	SDesktopPane desktop;
	private JScrollPane scrollPane;
	
	private MoneyMenu moneyMenu;
	
	private MiniBrowser browser;
	
	// TODO make a memory for used menmonics to avoid setting a character
	// to a menu which name doesn't contain this character
	
	public Desktop() {
	    super("Tariff Eye");
	    
	    setIconImage(Resources.appIcon1616.getImage());
	   
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                quit();
            }
        });
	    
	    desktop = new SDesktopPane(); 
		scrollPane = new JScrollPane();
	    
	    boolean setStdSize = true;
	    Rectangle rec = (Rectangle)BC.getParameter(Params.KEY_DESKTOP_BOUNDS,
	            Rectangle.class);
	    Dimension screenSize= Toolkit.getDefaultToolkit().getScreenSize();
	    
	    if (rec != null) {
	        
	        // We will see if we can restore its old bounds
	        int width = rec.width;
	        int maxX = width + rec.x;
	        
	        int height = rec.height;
	        int maxY = height + rec.y;
	        
	        // First we verify the whole positioning
	        if ((maxX <= screenSize.width) && (maxY <= screenSize.height)) {
	            setBounds(rec);
	            setStdSize = false;
	        } else {
	            // We now verify the overall size
	            if ((width <= screenSize.width) && 
	                    (height <= screenSize.height)) {
	                setBounds(0,0,width,height);
	                setStdSize = false;
	            }
	        }
	        
	    } else {
	        m_log.fatal("Received a null parameter from Params");
	    }
	    
	    if (setStdSize) {
//	        //Make the big window be indented 50 pixels from each edge
//	        //of the screen.
//	        int inset= 50;
//	        
//	        setBounds(
//	                inset,
//	                inset,
//	                screenSize.width - inset * 2,
//	                screenSize.height - inset * 2);
	        setBounds((Rectangle)BC.getParameter(Params.KEY_DESKTOP_BOUNDS,
	                Rectangle.class));
	    }
	    
	    desktop.setBorder(null);
	    
	    //TODO remove the small border on top and left of the Desktop
	    scrollPane.getViewport().add(desktop);
	    //scrollPane.setBorder(new LineBorder(null,0));
	    scrollPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
	    getContentPane().setLayout(new BorderLayout(0,0));
	    getContentPane().add(scrollPane,BorderLayout.CENTER);
	    
	    setJMenuBar(createMenuBar());
	    
	    //Make dragging a little faster but perhaps uglier.
	    desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	    //desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
	    
	    // the HTML Panel
	    browser = new MiniBrowser();
	    
	    // check expiration date. DISABLED
	    // checkExpiration();
	    
	}
	
	protected JMenuBar createMenuBar() {
		JMenuBar menuBar= new JMenuBar();

		//Set up the lone menu.
		JMenu menu= new JMenu();
		BC.langManager.register(menu,"File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		// Money Manager
		// Set up the first menu item.
		JMenuItem menuItem = new JMenuItem();
		BC.langManager.register(menuItem,"Manage Currencies");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCurrencyManagement();
			}
		});
		menu.add(menuItem);
		
		
		// Tariff updates
		menuItem = new JMenuItem();
		BC.langManager.register(menuItem,"Update Tariffs database");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchTariffUpdate();
			}
		});
		menu.add(menuItem);
		
		// ASSISTANT
		menuItem = new JMenuItem();
		BC.langManager.register(menuItem,"Assistant");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchAssistant();
			}
		});
		menu.add(menuItem);
		
		
		// CONSOLE
//		menuItem= new JMenuItem();
//		BC.langManager.register(menuItem,"Console");
//		menuItem.setMnemonic(KeyEvent.VK_K);
//		menuItem.setAccelerator(
//			KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
//		menuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//			    launchConsole();
//			}
//		});
//		menu.add(menuItem);

		//Set up the second menu item.
		menuItem= new JMenuItem();
		BC.langManager.register(menuItem,"Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
	
			}
		});
		menu.add(menuItem);

		if (SoftInfos.canGoCreation()) {
		    menu = new JMenu();
		    BC.langManager.register(menu, MENU_TITLE_CREATION);
		    menu.setMnemonic(KeyEvent.VK_C);
		    menuBar.add(menu);
		    
		    menuItem = new JMenuItem();
		    BC.langManager.register(menuItem,"New Tarification");
		    menuItem.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            loadNewTarification();
		        }
		    });
		    menu.add(menuItem);
		    
		    menuItem= new JMenuItem();
		    BC.langManager.register(menuItem,"Open Tarification");
		    menuItem.setMnemonic(KeyEvent.VK_N);
		    menuItem.setAccelerator(
		            KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		    menuItem.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            loadTarification();
		        }
		    });
		    menu.add(menuItem);
		}
		
		if (SoftInfos.canGoCreationGold()) {
		    menu = new JMenu();
		    BC.langManager.register(menu, MENU_TITLE_CREATION_LIVE);
		    menu.setMnemonic(KeyEvent.VK_G);
		    menuBar.add(menu);
		    
		    
		    menuItem = new JMenuItem();
		    BC.langManager.register(menuItem,"New Tarification");
		    menuItem.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            loadNewGoldTarification();
		        }
		    });
		    menu.add(menuItem);
		    
		    menuItem= new JMenuItem();
		    BC.langManager.register(menuItem,"Open Tarification");
		    menuItem.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            loadGoldTarification();
		        }
		    });
		    menu.add(menuItem);
		}
		
		menu = new JMenu();
		BC.langManager.register(menu, MENU_TITLE_SIMULATION);
		menu.setMnemonic(KeyEvent.VK_I);
		menuBar.add(menu);

		menuItem = new JMenuItem();
		BC.langManager.register(menuItem,"New Simulation");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadNewSimulation();
			}
		});
		menu.add(menuItem);
		
		menuItem= new JMenuItem();
		BC.langManager.register(menuItem,"Open Portofolio");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadSimulation();	
			}
		});
		menu.add(menuItem);
		
		// Set up the option menu.
		menu= new JMenu();
		BC.langManager.register(menu,"Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(menu);

		//Set up the second menu item.
		menuItem= new JMenuItem();
		BC.langManager.register(menuItem,"Preferences");
		menuItem.setMnemonic(KeyEvent.VK_COMMA);
		menuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_COMMA,ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchPreferences();
			}
		});
		menu.add(menuItem);
		
		// Themes
		BCLookAndFeel.addThemeMenu(menu);
		
		// the window menu item
		menuBar.add(new WindowMenu(desktop));

		// Help menu
		JMenu helpMenu = new JMenu();
		BC.langManager.register(helpMenu, "Help");
		
		JMenuItem tariffEyeHelp = new JMenuItem();
		BC.langManager.register(tariffEyeHelp, "TariffEye help...");
		//tariffEyeHelp.addActionListener(Helper.actionListener());
		tariffEyeHelp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				Helper.showHelp();
			}});
		
		helpMenu.add(tariffEyeHelp);
		
		JMenuItem aboutMenu = new JMenuItem();
		BC.langManager.register(aboutMenu, "About");
		aboutMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launchAbout();
            }
		});
		
		helpMenu.add(aboutMenu);
		
		menuBar.add(helpMenu);
		
		// Glue
		menuBar.add(Box.createHorizontalGlue());
		
		moneyMenu = new MoneyMenu();
		BC.getCurrencyManager().addWeakCurrencyChangeListener(moneyMenu);
		
		menuBar.add(moneyMenu);
		
		return menuBar;
	}

	private boolean firstConsoleLaunch = true;
	/**
     * Launch the console
     */
    public void launchConsole() {
        if (firstConsoleLaunch) {
            JInternalFrame jif = Console.getDisplay();
            InternalFrameDescriptor ifd = 
                new InternalFrameDescriptor(Params.KEY_CONSOLE_BOUNDS);
//            ifd.setInitialBounds(
//                    (Rectangle)Params.getDefaultValue(Params.PARAM_CONSOLE_BOUNDS));
            
            BC.bc.popupJIFrame(jif , ifd);
            firstConsoleLaunch = !firstConsoleLaunch;
        }
		Console.getDisplay().show();

		// Giving focus to console
		try {
			Console.getDisplay().setSelected(true);
		} catch (java.beans.PropertyVetoException pve) {
			m_log.error(
				"Desktop: Problems while giving focus to Console : ",
				pve);
		}
		Console.focusOnText();
    }

    /**
	 * Open a file chooser to load a tarification
	 */
	protected void loadTarification() {
		FileManagement.promptFileAndStartApp(this, FileManagement.CREATOR_OPEN);	
	}
	
	/**
	 * Open a new tarification
	 */
	protected void loadNewTarification() {
	    FileManagement.promptFileAndStartApp(this, FileManagement.CREATOR_NEW);
	}
	
	/**
	 * Open a file chooser to load a tarification
	 */
	protected void loadSimulation() {
		FileManagement.promptFileAndStartApp(this, FileManagement.SIMULATOR_OPEN);	
	}
	
	/**
	 * Open a clean tarification to start a simulation
	 */
	protected void loadNewSimulation() {
	    FileManagement.promptFileAndStartApp(this, FileManagement.SIMULATOR_NEW);
	}
	
	/**
	 * Create a new tarification in gold edition
	 */
	protected void loadNewGoldTarification() {
	    FileManagement.promptFileAndStartApp(this, FileManagement.CREATOR_GOLD_NEW);
	}
	
	/**
	 * Open a tarification in the GoldEdition
	 */
	protected void loadGoldTarification() {
	    FileManagement.promptFileAndStartApp(this, FileManagement.CREATOR_GOLD_OPEN);
	}
	
	protected void launchAbout() {
	    new About(this);
	}
	
	protected void launchCurrencyManagement() {
		CurrencyRatesManager currencyRatesManager = new CurrencyRatesManager();
		currencyRatesManager.setVisible(true);
		InternalFrameDescriptor ifd = new InternalFrameDescriptor(
		        Params.KEY_CURRENCY_MANAGER_BOUNDS);
//		ifd.setInitialBounds(
//		        (Rectangle)Params.getDefaultValue(Params.PARAM_CURRENCY_MANAGER_BOUNDS)
//		);
		
		BC.bc.popupJIFrame(currencyRatesManager, ifd);
	}
	

	public boolean assistantLaunched = false;
	public void launchAssistant() {
	    if (! assistantLaunched) {
	        assistantLaunched = true;
	        InternalFrameDescriptor ifd = 
	            new InternalFrameDescriptor(Params.KEY_ASSISTANT_BOUNDS);
	        //InternalFrameDescriptor ifd = new InternalFrameDescriptor();
	        ifd.setInitialBounds(
	                (Rectangle)Params.getDefaultValue(Params.KEY_ASSISTANT_BOUNDS)
	        );
	        ifd.setCenterOnOpen(true);
	        ifd.setInitialBoundsAreMinimum(true);

	        BC.bc.popupJIFrame(browser, ifd);
	        
	        // set the content of the browser either TariffEyeInfo or Welcome.jsp
		    boolean infoEnable = 
		    	((Boolean)BC.getParameter(Params.KEY_TARIFF_EYE_INFO)).booleanValue();
		    
		    browser.goToHome();
		    if (infoEnable) {
		        TariffEyeQuery infoQuery = new TariffEyeQuery("INFO"); 
		        String infoDoc = "";
		        if (infoQuery.connect() && 
		                (infoDoc = infoQuery.document()) != null) {
		            browser.setHTML(infoDoc);
		            
		        } else {
		            m_log.warn("Failed to retrieve TariffEyeInfo; no exception");
		        }
		    }
	    }
	    browser.show();
	    browser.moveToFront();
	    try {
	        browser.setSelected(true);
	    } catch (PropertyVetoException e) {
	        e.printStackTrace();
	    }
	    
	    
	}
	
	ListLoaderHTTP tariffUpdate;
	/** open the tariff update window **/
	public void launchTariffUpdate() {
	    if (tariffUpdate == null) {
	        tariffUpdate = new ListLoaderHTTP(this,SoftInfos.id());
	    } else {
	        tariffUpdate.setVisible(true);
	    }
	}
	
	private void launchPreferences() {
	    PreferencePanel pp = new PreferencePanel();
	    InternalFrameDescriptor ifd = new InternalFrameDescriptor();
	    ifd.setCenterOnOpen(true);
	    ifd.setInitialBounds(new Rectangle(10,10,530,350));
	    
	    BC.bc.popupJIFrame(pp, ifd);
	}
	
	
	//Quit the application.
	protected void quit() {
	    if (!getDesktopPane().closeAllWindows()) {
	        return;
	    }
	    
	    // Lets save application's size...
	    Rectangle rec = this.getBounds();
	    BC.setParameter(Params.KEY_DESKTOP_BOUNDS, rec);
	    
	    // Commit the changes (since saving is threaded)
	    BC.commitChanges();
	    
	    // Killall...
		System.exit(0);
	}

	/**
	* Create the GUI .  For thread safety,
	* this method should be invoked from the
	* event-dispatching thread.<BR>
	* You still need to call <BR>
	* .setVisible(true);
	*/
	public static Desktop create() {
		//Make sure we have nice window decorations.
		//JFrame.setDefaultLookAndFeelDecorated(true);

		//Create and set up the window.
		Desktop frame= new Desktop();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false);
		return frame;
	}

	/**
	 * Get JDesktopPane 
	 */
	public SDesktopPane getDesktopPane() {
		return this.desktop;
	}
	
	/**
	 * This methods looks for autosave file in the .bcdata directory. 
	 * It then prompts a dialog to know if he wants to recover these files.
	 * If he does, the method will resume the work if it can.
	 */
	void checkCrash() {
		
		// Check for autosave files
		File autoSaveDir = new File(Resources.dataPath());
		String[] openedFileBeforeCrash = autoSaveDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith(AutosaveTask.AUTOSAVE_PREFIX);
			}});
		m_log.debug("Found "+openedFileBeforeCrash.length+" lost file(s).");
		
		// Prompt user for the future of the lost files
		if (openedFileBeforeCrash.length > 0) {
			String[] buttons = {Lang.translate("Recover"), Lang.translate("Later"),
					Lang.translate("Drop")};
			switch (ModalDialogBox.custom(BC.bc.getMajorComponent(),
					"<HTML>"+Lang.translate(RECOVER_MSG)+"</HTML>",
					buttons,
					UIManager.getIcon("OptionPane.questionIcon"))){
					case 0: // Recover
						recoverFiles(openedFileBeforeCrash);
						break;
					case 1: // Later
						break;
					case 2: // drop
						for (int i = 0; i < openedFileBeforeCrash.length; i++) {
							File aFile = 
								new File(Resources.dataPath(),openedFileBeforeCrash[i]);
							aFile.delete();
						}
						break;
			}
		}
	}
	
	/** Recover lost files */
	private void recoverFiles(final String[] files) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				
				public void run() {
					
					for (int i = 0; i<files.length; i++) {
						File aFile = new File(Resources.dataPath(),files[i]);
					
						if (files[i].startsWith(AutosaveTask.SIM_PREFIX)) {
						    FileManagement.openExternal(
						            aFile, BC.bc.getMajorComponent(),1);
						} else 
						if (files[i].startsWith(AutosaveTask.CREATOR_PREFIX)) {
						    FileManagement.openExternal(
						            aFile, BC.bc.getMajorComponent(),2);
						} else 
						 if (files[i].startsWith(AutosaveTask.LIVE_PREFIX)) {
						    FileManagement.openExternal(
						            aFile, BC.bc.getMajorComponent(),3);
						}
						if (!aFile.delete())
						m_log.warn("Unable to delete autosave file: "+aFile);
				}
			}});
		} catch (Exception e) {
			m_log.error("Problem during file recovery.",e);
		}
	}
}


class WindowMenu extends JMenu {
	private static final Logger m_log = Logger.getLogger( WindowMenu.class ); 
	SDesktopPane desktop;
	private JMenuItem cascade=new JMenuItem("Cascade");
	private JMenuItem tile=new JMenuItem("Tile");

	public WindowMenu(SDesktopPane deskt) {
		this.desktop=deskt;
		BC.langManager.register(cascade,"Cascade");
		BC.langManager.register(tile,"Tile");
		setText("Window");
		setMnemonic(KeyEvent.VK_W);
		cascade.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				WindowMenu.this.desktop.cascadeFrames();
			}
		});
		tile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				WindowMenu.this.desktop.tileFrames();
			}
		});
		addMenuListener(new MenuListener() {
			public void menuCanceled (MenuEvent e) {}

			public void menuDeselected (MenuEvent e) {
				removeAll();
			}

			public void menuSelected (MenuEvent e) {
				buildChildMenus();
			}
		});
	}

	/* Sets up the children menus depending on the current desktop state */
	void buildChildMenus() {
		int i;
		ChildMenuItem menu;
		JInternalFrame[] array = desktop.getAllFrames();

		ArrayList/*<JInternalFrame>*/ iconifiedFrames 
				= new ArrayList(/*<JInternalFrame>*/);
		
		add(cascade);
		add(tile);

		boolean firstMenu = true;
		int showingCount = 0;
		for (i = 0; i < array.length; i++) {
		    // We skip every hidden frames...
		    if (!array[i].isShowing()) {
		        // If this frame is iconified we add it to the ArrayList
		        if (array[i].isIcon()) {
		            iconifiedFrames.add(array[i]);
		        }
		        continue;
		    } 
		    
		    if (firstMenu) {
		        addSeparator();
		        firstMenu = !firstMenu;
		    }
		    showingCount++;
		    menu = new ChildMenuItem(array[i]);
		    /*
		    menu.setState(array[i].isSelected());
		    menu.setIconTextGap(3);
		    Icon ico = array[i].getFrameIcon();
		    if (ico == null) {
				ico = Resources.iconStdWindow;
			}
		    menu.setIcon(ico);
		    */
		    menu.addActionListener(new ActionListener() {
		        public void actionPerformed(ActionEvent ae) {
		            JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
		            frame.show();
		            frame.moveToFront();
		            try {
		                /*
		                 if (frame.isIcon()) {
		                 frame.setIcon(false);
		                 }
		                 */
		                frame.setSelected(true);
		            } catch (PropertyVetoException e) {
		               m_log.error( "cannot select frame",e );
		            }
		        }
		    });
			
			add(menu);
		}
		
		cascade.setEnabled(showingCount > 0);
		tile.setEnabled(showingCount > 0);
		
		if (iconifiedFrames.size() > 0) {
		    // We build a special menu for iconified frames...
		    
		    add(new JSeparator());
		    
		    JMenu iconifiedMenu = new JMenu();
		    iconifiedMenu.setText(
		            Lang.translate("Desktop:iconifiedFrames subMenu"));
		    
		    for (Iterator ite = iconifiedFrames.iterator(); ite.hasNext();) {
		        JInternalFrame jif = (JInternalFrame)ite.next();
		        menu = new ChildMenuItem(jif);
		        
		        menu.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent ae) {
		                JInternalFrame frame = ((ChildMenuItem)ae.getSource()).getFrame();
		                try {
		                    frame.setIcon(false);
		                    frame.setSelected(true);
		                } catch (PropertyVetoException e) {
		                    m_log.error( "Could not deiconify frame : "+frame.getTitle(), e );
		                }
		            }
		        });
				
		        iconifiedMenu.add(menu);
		    }
		        
		    add(iconifiedMenu);
		}
	}

	/* This JCheckBoxMenuItem descendant is used to track 
	 * the child frame that corresponds
	   to a give menu. */
	class ChildMenuItem extends JCheckBoxMenuItem {
		private JInternalFrame frame;

		public ChildMenuItem(JInternalFrame fram) {
			super(fram.getTitle());
			this.frame=fram;
			setIconTextGap(3);
			setState(fram.isSelected());
		    Icon ico = fram.getFrameIcon();
		    if (ico == null) {
				ico = Resources.iconStdWindow;
			}
		    setIcon(ico);
		}

		public JInternalFrame getFrame() {
			return frame;
		}
	}
}

class MoneyMenu extends JMenu implements ChangeListener {
	private static final Logger m_log=Logger.getLogger(MoneyMenu.class ); 
		
	private ChildMenuItem[] children;
	
	public MoneyMenu() {
		super();
		// Setting correct title for main menu...
		//this.setText(Currency.getDefaultCurrencyRef().toString());
		this.setIconTextGap(3);
		this.setIcon(Resources.iconCurrencyManager);
		
		// Get money list and construct subMenus
		Currency[] curs = Currency.getCurrencies();
		children = new ChildMenuItem[curs.length];
		//ChildMenuItem menu;
		for (int i=0; i<curs.length; i++) {
			children[i] = new ChildMenuItem(curs[i]);
			//children[i].refresh();
			
			children[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Currency c = ((ChildMenuItem)ae.getSource()).getCurrency();
					BC.getCurrencyManager().setDefaultCurrency(c);
					refresh();
				}
			});
			
			this.add(children[i]);
			
		}
		
		
		refresh();
	}
	
	private void refresh() {
		// Change title
		this.setText(Currency.defaultCurrencyRef().toString());
		
		// Select correct subMenu
		for (int i=0; i<children.length; i++) {
			children[i].refresh();			
		}
	}
	
	public void stateChanged(ChangeEvent e) {
		refresh();
	}
	
	class ChildMenuItem extends JCheckBoxMenuItem {
		private Currency cur;
		
		public ChildMenuItem(Currency c) {
			super(c.toString());
			this.cur = c;
		}
		
		public Currency getCurrency() {
			return this.cur;
		}
		
		public void refresh() {
			this.setState(this.cur.xequals(Currency.defaultCurrencyRef()));
		}
		
	}
	
}

/**
* $Id: Desktop.java,v 1.2 2007/04/02 17:04:28 perki Exp $
* $Log: Desktop.java,v $
* Revision 1.2  2007/04/02 17:04:28  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:44  perki
* First commit on sourceforge
*
* Revision 1.75  2004/12/01 09:33:25  jvaucher
* Subscription delay information
*
* Revision 1.74  2004/11/30 14:43:49  jvaucher
* - Emergency release
*
* Revision 1.73  2004/11/30 13:41:35  carlito
* minimum size for internal frames
*
* Revision 1.72  2004/11/30 12:36:20  perki
* *** empty log message ***
*
* Revision 1.71  2004/11/29 09:09:09  jvaucher
* - Bug with Proxies
* - Fixed swing problem at startup
*
* Revision 1.70  2004/11/27 10:48:39  perki
* *** empty log message ***
*
* Revision 1.69  2004/11/26 15:43:31  perki
* *** empty log message ***
*
* Revision 1.68  2004/11/26 14:31:47  jvaucher
* TariffEyeInfo, replaced by the TariffEyeQuery class for generalisation purpose
*
* Revision 1.67  2004/11/26 14:01:03  perki
* *** empty log message ***
*
* Revision 1.66  2004/11/26 10:06:00  jvaucher
* Begining of TariffEyeInfo feature
*
* Revision 1.65  2004/11/20 10:56:25  perki
* Launcher is now ok
*
* Revision 1.64  2004/11/17 15:26:22  carlito
* New design for WorkSheetPanel, WorkSheetPanelBorder and DispatcherCasePanel advanced
*
* Revision 1.63  2004/11/17 15:20:36  jvaucher
* ModalDialogBox behaves correctly whatever the thread we call it from
*
* Revision 1.62  2004/11/16 18:30:51  carlito
* New parameter management ...
*
* Revision 1.61  2004/11/16 15:17:55  jvaucher
* Refactor of load / save methods.
*
* Revision 1.60  2004/11/08 16:42:35  jvaucher
* - Ticket # 40: Autosave. At work. Some tunning is still necessary
*
* Revision 1.59  2004/10/19 17:10:42  carlito
* assistant modified
*
* Revision 1.58  2004/10/15 08:05:27  perki
* *** empty log message ***
*
* Revision 1.57  2004/10/14 13:53:28  perki
* *** empty log message ***
*
* Revision 1.56  2004/10/14 10:06:38  jvaucher
* - Helper integrated. First version. Still no contextual behaviour
* - Report. Implemented the TocManager system.
* - FileChooser: Minor changes (name of buttons)
*
* Revision 1.55  2004/10/05 05:40:52  carlito
* About fully functionnal, added a little toolkit for windows in uitools
*
* Revision 1.54  2004/10/04 16:57:02  carlito
* About finished
*
* Revision 1.53  2004/10/04 15:49:18  carlito
* *** empty log message ***
*
* Revision 1.52  2004/10/04 10:10:31  jvaucher
* - Minor changes in FileManagement, allowing to choose the dialogType
* - Helper skeleton
* - Improved rendering of Tarification Report
* - Dispatcher bound can yet disable the upper bound
*
* Revision 1.51  2004/10/02 17:03:30  perki
* *** empty log message ***
*
* Revision 1.50  2004/09/30 15:25:18  perki
* Better Startp process
*
* Revision 1.49  2004/09/24 10:08:28  perki
* *** empty log message ***
*
* Revision 1.48  2004/09/23 14:45:47  perki
* bouhouhou
*
* Revision 1.47  2004/09/22 15:39:55  carlito
* Simulator : toolbar removed
Simulator : treeIcons expand and collapse moved to right
Simulator : tarif title and description now appear
WorkSheetPanel : modified to accept WorkSheet descriptions
Desktop : closing button problem solved
DispatcherSequencePanel : modified for simulation mode
ModalDialogBox : modified for insets...
Currency : null pointer removed...
*
* Revision 1.46  2004/09/22 09:17:36  perki
* *** empty log message ***
*
* Revision 1.45  2004/09/22 06:47:04  perki
* A la recherche du bug de Currency
*
* Revision 1.44  2004/09/21 17:07:03  jvaucher
* Implemented load and save preferences
* Need perhaps (certainly) to test the case where one refered folder is deleted
*
* Revision 1.43  2004/09/09 12:26:08  perki
* Cleaning
*
* Revision 1.42  2004/09/03 13:25:34  kaspar
* ! Log.out -> log4j part four
*
* Revision 1.41  2004/07/20 16:19:46  perki
* merging menus
*
* Revision 1.40  2004/07/19 20:00:33  carlito
* *** empty log message ***
*
* Revision 1.39  2004/07/12 17:27:25  carlito
* desktop improvements
*
* Revision 1.38  2004/07/09 19:10:24  carlito
* *** empty log message ***
*
* Revision 1.37  2004/07/07 17:27:09  perki
* *** empty log message ***
*
* Revision 1.36  2004/07/06 17:31:25  carlito
* Desktop manager enhanced
SButton with border on macs
desktop size persistent
*
* Revision 1.35  2004/07/01 15:04:50  perki
* *** empty log message ***
*
* Revision 1.34  2004/06/30 17:33:24  carlito
* MiniBrowser replaced by Kaspar Browser.
launchable from desktop
*
* Revision 1.33  2004/06/30 08:59:18  carlito
* web improvment and dispatcher case debugging
*
* Revision 1.32  2004/06/28 16:47:54  perki
* icons for tarif in simu
*
* Revision 1.31  2004/05/31 17:55:30  carlito
* *** empty log message ***
*
* Revision 1.30  2004/05/31 17:13:04  carlito
* *** empty log message ***
*
* Revision 1.29  2004/05/31 16:07:09  carlito
* *** empty log message ***
*
* Revision 1.28  2004/05/31 15:02:59  perki
* *** empty log message ***
*
* Revision 1.27  2004/05/31 12:40:22  perki
* *** empty log message ***
*
* Revision 1.26  2004/05/31 07:19:47  perki
* Enable and disable
*
* Revision 1.25  2004/05/28 12:42:33  perki
* *** empty log message ***
*
* Revision 1.24  2004/05/28 11:11:08  carlito
* *** empty log message ***
*
* Revision 1.23  2004/05/27 08:43:33  carlito
* *** empty log message ***
*
* Revision 1.22  2004/05/23 14:08:11  perki
* *** empty log message ***
*
* Revision 1.21  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.20  2004/03/23 13:39:19  perki
* New WorkSHeet Panel model
*
* Revision 1.19  2004/03/22 14:32:30  carlito
* *** empty log message ***
*
* Revision 1.18  2004/03/15 15:46:56  carlito
* *** empty log message ***
*
* Revision 1.17  2004/03/12 17:48:06  perki
* Monitoring file loading
*
* Revision 1.16  2004/03/12 02:52:51  carlito
* *** empty log message ***
*
* Revision 1.15  2004/03/08 06:59:29  perki
* et hop une bouteille de rhum
*
* Revision 1.14  2004/03/06 14:24:50  perki
* Tirelipapon sur le chiwawa
*
* Revision 1.13  2004/02/22 10:43:56  perki
* File loading and saving
*
* Revision 1.12  2004/02/06 14:50:29  carlito
* paouatche
*
* Revision 1.11  2004/02/04 15:42:16  perki
* cleaning
*
* Revision 1.10  2004/01/31 15:46:49  perki
* 16 heure 49
*
* Revision 1.9  2004/01/29 12:17:35  perki
* Import cleaning
*
* Revision 1.8  2004/01/22 15:40:51  perki
* Bouarf
*
* Revision 1.7  2004/01/22 13:03:31  perki
* *** empty log message ***
*
* Revision 1.6  2004/01/20 15:21:30  carlito
* Added auto focus on console textArea...
*
* Revision 1.5  2004/01/10 08:11:44  perki
* UI addons and Look And Feel
*
* Revision 1.4  2004/01/07 15:00:11  perki
* type handling for Tarifs
*
* Revision 1.3  2003/12/08 17:32:58  perki
* bean shell console
*
* Revision 1.2  2003/12/07 22:49:30  prochat
* Just as requested
*
* Revision 1.1  2003/12/05 17:43:12  perki
* Desktop en plus
*
*/
