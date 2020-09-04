/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.bc;

import java.awt.*;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import com.simpledata.bc.tools.BCCurrencyMap;

/**
 * Object (Hashtable) that stores all the user parameters
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: Params.java,v 1.2 2007/04/02 17:04:28 perki Exp $
 */
public class Params extends Hashtable {
    
	private final static Logger m_log = Logger.getLogger(Params.class);
	
    //  This is defined for serialization compatibility
    static final long serialVersionUID = 2000000001; 
    //  This should be increased to reflect class definition changes
    private static final String thisClassVersion = "1.00"; 
    //	 This store the class version that was instanciated
    public String myClassVersion = null; 

    /** Map containing all default parameters */
    private static Hashtable defaultParamsMap;
    static {
        // IMPORTANT !!!!!!!!!!
        // This block must be the first init block of this file
        m_log.info("Default parameters initialization started ... ");
        defaultParamsMap = new Hashtable();
    }
    
    /** Key for application wide language used <br>
     * Param type : String */
    public static final String KEY_LANG = "lang";
    static { defaultParamsMap.put(KEY_LANG, "en");}
    
    /** This parameter represents the place where the tarification are saved <br>
     * Param type : String */
    public static final String KEY_DEFAULT_SAVED_TARIFICATION_PATH = 
    	"def_tarification_path";
    static {
        defaultParamsMap.put(KEY_DEFAULT_SAVED_TARIFICATION_PATH,
                Resources.findMyDocumentFolder());
    }
    
    /** This parameter represents the place where the build in tarifications
     * are stored. It should not be modified by the user until the directory
     * still exists. <br>
     * Param Type:  String */
    public static final String KEY_TARIFICATION_LIBRARY_PARTH =
    	"tarification_library_path";
    static {
        defaultParamsMap.put(KEY_TARIFICATION_LIBRARY_PARTH,
                Resources.defaultTarificationLibraryPath());
    }
    
    /** This parameter tells if the user prefers look up for his files into
     * the default directory for saved tarifications or into the last used
     * dir. <BR>
     * Must be one of PREF_LAST_FOLDER or PREF_DEFAULT_FOLDER */
    public static final String KEY_OPEN_FOLDER_PREF = "open_folder_pref";
	/** last seen folder when open */
	public final static int PREF_LAST_FOLDER = 0;
	/** default folder when save */
	public final static int PREF_DEFAULT_FOLDER = 1;
    static {
        defaultParamsMap.put(KEY_OPEN_FOLDER_PREF,new Integer(PREF_LAST_FOLDER));
    }
    
	/** This parameter contains the last used directory for the load/save
	 * operation on a tarification */
	public static final String KEY_LAST_TARIFICATION_FOLDER =
		"location_last_tarification_folder";

	/**
	 * This parameter contains the last used directory for the load/save
	 * operation on a simulation
	 */
	public static final String KEY_LAST_SIMULATION_FOLDER =
		"location_last_simulation_folder";
	
	/**
	 * Keyring that gets distributed by Simpledata. This contains 
	 * normally just the public key of the master key pair and both
	 * private and public key of the client. 
	 */
	public static final String KEY_INITIAL_KEYRING = "keyring_initial"; 
		
	/**
	 * This is the location user defined keyrings get saved to. The
	 * user defined keyring is saved on the first launch of the program
	 * and everytime access permissions are imported. 
	 */
	public static final String KEY_WORK_KEYRING = "keyring_work_v1"; 
	
	/**
	 * This parameter defines if the autosave feature is enabled. <br>
	 * Param type: Boolean
	 * Default   : true
	 */
	public static final String KEY_AUTOSAVE_ENABLE = "autosave_enable";
	static {
	    defaultParamsMap.put(KEY_AUTOSAVE_ENABLE,new Boolean(true));
	}
	
	
	/**
	 * This parameter defines the period the timer uses to peform autosave
	 * task. In minutes.<br>
	 * Param type: Integer
	 * Default   : 8 minutes
	 */
	public static final String KEY_AUTOSAVE_PERIOD = "autosave_period";
	static {
	    defaultParamsMap.put(KEY_AUTOSAVE_PERIOD,new Integer(8));
	}
	
	/** Offset from screen border */
    private final static int SCREEN_BORDER_SPACING = 50;
	
	/** Default bounds for desktop */
	public final static String KEY_DESKTOP_BOUNDS = "Desktop:Bounds";
    static {
	    /** ScreenSize used by some default params */
	    Dimension screenSize= Toolkit.getDefaultToolkit().getScreenSize();
	    Rectangle rec = new Rectangle(SCREEN_BORDER_SPACING,
        								SCREEN_BORDER_SPACING,
        								screenSize.width - SCREEN_BORDER_SPACING * 2,
        								screenSize.height - SCREEN_BORDER_SPACING * 2);
	    defaultParamsMap.put(KEY_DESKTOP_BOUNDS, rec);
    }
	
	/** Default bounds for assistant */
    public final static String KEY_ASSISTANT_BOUNDS = "Desktop:Assistant:Bounds";
	static {
	    defaultParamsMap.put( KEY_ASSISTANT_BOUNDS,new Rectangle(0,0,520,440) );
	}
    
    /** Default bounds for console */
    public final static String KEY_CONSOLE_BOUNDS = "Desktop:Console:Bounds";
    static {
        defaultParamsMap.put( KEY_CONSOLE_BOUNDS, new Rectangle(3, 3,500, 400) );
    }
    
    /** Default bounds for currency manager */
    public final static String KEY_CURRENCY_MANAGER_BOUNDS = 
        "Desktop:CurrencyManager:Bounds";
    static {
        defaultParamsMap.put(KEY_CURRENCY_MANAGER_BOUNDS,
                new Rectangle(10,10,350, 400)
	    );
    }
    
    /** default currency code */
    private final static String PREF_DEFAULT_CURRENCY_CODE = "EUR";
    
    /** all currencies but default codes */
    private final static String[] PREF_CURRENCY_CODE_LIST = 
    		{"USD","CHF","CAD","CNY","GBP","JPY","SGD"};
     
    /** all currencies (non default) rate against default currency 
     * Respect the order of PREF_CURRENCY_CODE_LIST
     */
    private final static double[] PREF_CURRENCY_RATE_LIST = 
    		{1.2585,1.5674,1.6837,10.416,0.6847,132.80,2.1335};
        
    /** The key for currencymap save */
    public final static String KEY_CURRENCY_MAP = "CurrencyManager:CurrencyMap";
    static {
	    // We first check that pref params are correct
	    if (PREF_CURRENCY_CODE_LIST.length != PREF_CURRENCY_RATE_LIST.length) {
	        m_log.fatal("Currency codes list and Currency rates list have" +
	        		" different sizes!!!!!! (in com.simpledata.bc.Params.java)");
	    } else {
	        BCCurrencyMap map = new BCCurrencyMap(PREF_DEFAULT_CURRENCY_CODE);
	        for (int i = 0; i < PREF_CURRENCY_CODE_LIST.length; i++)
	            map.put(PREF_CURRENCY_CODE_LIST[i],PREF_CURRENCY_RATE_LIST[i]);
	        defaultParamsMap.put(KEY_CURRENCY_MAP,map);
	    }
    }
    
    /** the key for default currency code save */
    public final static String KEY_DEFAULT_CURRENCY_CODE = 
        "CurrencyManager:DefaultCurrencyCode";
    static {
        defaultParamsMap.put(KEY_DEFAULT_CURRENCY_CODE, PREF_DEFAULT_CURRENCY_CODE);
    }
    
    /** Fields shared by all creators */
    /** Key for tariff list bounds */
    public final static String KEY_TARIF_LIST_BOUNDS = "Creator:TarifList:Bounds";
    static {
        defaultParamsMap.put( KEY_TARIF_LIST_BOUNDS, new Rectangle(5,5,350,350) );
    }
    
    /** Key for tree orderer bounds*/
    public final static String KEY_TREE_ORDERER_BOUNDS = "Creator:TreeOrderer:Bounds";
    static {
        defaultParamsMap.put(KEY_TREE_ORDERER_BOUNDS,new Rectangle(10,10,300,300));
    }
    
    /** Description modifier for CreatorLight <br>
     * Suffix added to KEY_CREATOR_BOUNDS or KEY_CREATOR_DIVIDER_POS to obtain 
     * bounds or divider pos for CreatorLight
     */
    public final static String KEY_CREATOR_LIGHT_DESCRIPTION_MODIFIER = "Light";
    
    /** Description modifier for CreatorGold <br>
     * Suffix added to KEY_CREATOR_BOUNDS or KEY_CREATOR_DIVIDER_POS to obtain 
     * bounds or divider pos for CreatorGold
     */
    public final static String KEY_CREATOR_GOLD_DESCRIPTION_MODIFIER = "Gold";

    /** Prefix for creators bounds key */
    public final static String KEY_CREATOR_BOUNDS = "Creator:Bounds:";
    static {
        defaultParamsMap.put(
                KEY_CREATOR_BOUNDS+KEY_CREATOR_LIGHT_DESCRIPTION_MODIFIER,
	            new Rectangle(10,10,900, 550)
	    );
	    defaultParamsMap.put(
	            KEY_CREATOR_BOUNDS+KEY_CREATOR_GOLD_DESCRIPTION_MODIFIER,
	            new Rectangle(10,10,970, 550)
	    );
    }
    
    /** Prefix for creators dividers positions key */
    public final static String KEY_CREATOR_DIVIDER_POS = "Creator:DividerProportions:";
    static {
        double[] props = new double[2];
        props[0] = 0.28;
        props[1] = 0.61;
        defaultParamsMap.put(
                KEY_CREATOR_DIVIDER_POS+KEY_CREATOR_LIGHT_DESCRIPTION_MODIFIER,
                props
	    );
        
        props[0] = 0.28;
        props[1] = 0.61;
	    defaultParamsMap.put(
	            KEY_CREATOR_DIVIDER_POS+KEY_CREATOR_GOLD_DESCRIPTION_MODIFIER,
	            props
	    );
    }
    
    /** key for CreatorGold compactTreeTable proportions */
    public static final String KEY_CREATOR_GOLD_COMPACT_TREE_TABLE_POS = 
        "CreatorGold:CompactTreeTable:Proportions";
    static {
        double[] props = new double[3];
        props[0] = 0.75;
        props[1] = 0.25;
        props[2] = 0.0;
        defaultParamsMap.put(KEY_CREATOR_GOLD_COMPACT_TREE_TABLE_POS,
                props);
    }
    
    /** Simulator bounds key */
    public final static String KEY_SIMULATOR_BOUNDS = "Simulator:Bounds";
    static {
        defaultParamsMap.put(KEY_SIMULATOR_BOUNDS,new Rectangle(10,10,900,500));
    }
    
    /** Simulator divider positions key */
    public final static String KEY_SIMULATOR_DIVIDERS_POS = "Simulator:DividerPositions";
    static {
	    double[] props = new double[3];
	    props[0] = 0.7;
	    props[1] = 0.3;
	    props[2] = 0.35;
        defaultParamsMap.put(KEY_SIMULATOR_DIVIDERS_POS,
                props);
    }
    
	/** key for parameter maxDepth **/
	public static final String KEY_MAX_DEPTH = "TarifViewer:MaxDepthView";
    static {
        defaultParamsMap.put(KEY_MAX_DEPTH, new Integer(10));
    }
	
	/** key for isSImple flag */
	public static final String KEY_IS_SIPMLE = "BC:isSimple";
	static {
	    defaultParamsMap.put(KEY_IS_SIPMLE, new Boolean(false));
	}
	
	/** 
	 * Switch to enable / disable tariffEye infos. The TariffEye infos page
	 * contains information about software update and tariff updates.
	 * <br>
	 * Param type: Boolean
	 * Default   : false
	 */
	public static final String KEY_TARIFF_EYE_INFO = "tariff_eye_info";
	static {
		defaultParamsMap.put(KEY_TARIFF_EYE_INFO, new Boolean(false));
	}
	
	/**
	 * Switch the http proxy on / off
	 * <br>
	 * Param type: Boolean
	 * Default   : false
	 */
	public static final String KEY_USE_PROXY = "use_proxy";
	static {
		defaultParamsMap.put(KEY_USE_PROXY, new Boolean(false));
	}
	
	/**
	 * http proxy host
	 * <br>
	 * Param type: String
	 * Default   : *empty String*
	 */
	public static final String KEY_PROXY_HOST = "http_proxy_host";
	static {
		defaultParamsMap.put(KEY_PROXY_HOST, "");
	}
	
	/**
	 * http proxy port
	 * <br>
	 * Param type: String
	 * Default   : *empty String*
	 */
	public static final String KEY_PROXY_PORT = "http_proxy_port";
	static {
		defaultParamsMap.put(KEY_PROXY_PORT, "");
	}
	
	// *********** UPDATES
	
	/** base URL for all updates **/
	public final static String KEY_UPDATE_URL = "HTTPUpdate:url";
	static {
	    defaultParamsMap.put(KEY_UPDATE_URL,
	            "http://update.tariffeye.com/");
	}
	
	/** 
	 * Subscription expiration expires date <BR>
	 * Type:   Date
	 */
	public final static String KEY_SUBSCRIBTION_EXPIRES = "subscr_expires";
	
	/**
	 * Last time once connect to TariffEye.com (server time) <br>
	 * Type:   Date
	 */
	public final static String KEY_LAST_UPDATE_REMOTE_TIMESTAMP = 
		"last_update_remote_timestamp";
	
	/**
	 * Last time once connect to TariffEye.com (localtime) <br>
	 * Type:   Date
	 */
	public final static String KEY_LAST_UPDATE_LOCAL_TIMESTAMP = 
		"last_update_local_timestamp";
	
	
	// *********** INSTALLATION ID
	/** this key keeps in memory the last lock code used **/
	public final static String KEY_ID_USED_LOCK_CODE = "ID:lockCodeUsed";
	static {
	    defaultParamsMap.put(KEY_ID_USED_LOCK_CODE,"NEVERUNLOCKED");
	}
	
	/************************ CODE *********************************/
	

	public Params () { 
		this.myClassVersion = thisClassVersion; 
		// Mark this instance with the class definition version used
	}
	
	/**
	 * This method retrieves the value of a user preference or parameter.
	 * If key is not found, the method try to find a default value (and
	 * set the parameter to this default value). If no default value
	 * is defined for this parameter method returns null.
	 * @param c is the return class we are expecting (if class found is different
	 * then switching to default value)
	 * 
	 * NB : do not call directly (use BC.getParam... instead)
	 * @see java.util.Dictionary#get(java.lang.Object)
	 */
	public Object obtain(Object key, Class c) {
		Object result = super.get(key);
		// Handle default values for common keys
		if (result == null) 
			result = getDefaultValue(key);

		if (result != null) {
		    if ( !(c.isAssignableFrom(result.getClass()) ) ) {
//		        m_log.warn("Class "+c.toString()+", was not assignable from "+
//		                result.getClass().toString());
		        result = forceDefault(key);
		    } 
//		    else {
//		        m_log.debug("Class "+c.toString()+", was assignable from "+
//		                result.getClass().toString());
//		    }
		}
		return result;
	}
	
	/**
	 * Whenever a value is bogus, remove bogus value and force default
	 * @param key the key for the desired object
	 * @return the default value
	 * 
	 * NB : do not call (use BC.forceDefaultParam instead)
	 */
	public Object forceDefault(Object key) {
	    super.remove(key);
	    return getDefaultValue(key);
	}
	
	/** Put here default values for the parameters 
	 * Try to avoid its use (prefer forceDefault)*/
	public static Object getDefaultValue(Object key) {
		Object res = defaultParamsMap.get(key);
		if (res != null) {
		    return res;
		} 
		m_log.error("Need to define a default value for key : "+key.toString());
		return null;
	}

	
	
	/** 
	 * Leave this block at the end of the file
	 */
	static {
	    m_log.info("Default parameters initialization terminated ... ");
	}

	
}
