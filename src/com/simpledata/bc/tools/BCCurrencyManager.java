/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
/*
 * Created on 31 mai 2004
 * $Id: BCCurrencyManager.java,v 1.2 2007/04/02 17:04:25 perki Exp $
 */
package com.simpledata.bc.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.simpledata.bc.*;
import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.datamodel.money.Currency;
import com.simpledata.bc.datamodel.money.CurrencyManager;
import com.simpledata.bc.uicomponents.tools.CurrencyChooserCombo;
import com.simpledata.bc.uitools.ModalJPanel;
import com.simpledata.bc.uitools.SNumField;

/**
 * 
 * 
 */
public class BCCurrencyManager implements CurrencyManager {
	private static final Logger m_log = 
	    Logger.getLogger( BCCurrencyManager.class ); 
	
	//private String[] symbols;
	private BCCurrencyMap bananas;
	private Currency defaultCurrency;
	
	//private final static String PARAMS_RATE = "currenciesRates";
	//private final static String PARAMS_CODE = "defaultCurrencyCode";
	
	public BCCurrencyManager() {
		
		Currency.setCurrencyManager(this);
		loadParameters();
	}
	
	private void loadParameters() {
	    bananas = (BCCurrencyMap)BC.getParameter(Params.KEY_CURRENCY_MAP,
	            BCCurrencyMap.class);
	    String defaultCurrencyCode = 
	        BC.getParameterStr(Params.KEY_DEFAULT_CURRENCY_CODE);
	    
	    if (bananas.getValue(defaultCurrencyCode) < 0) {
	        defaultCurrencyCode = 
	            (String)BC.forceDefaultParam(Params.KEY_DEFAULT_CURRENCY_CODE);
	    }
	    
	    defaultCurrency = new Currency(defaultCurrencyCode);
		
//	  String myDefaultCurrency = "EUR";
//	    
//	    // Params rates
////		String[] statical_symbol_list =
////		{"$","?","CHF","CAD","CNY","�","�","SGD"};
////		this.symbols = statical_symbol_list;
//		
//		Object o = BC.getParameter(Params.KEY_CURRENCY_MAP);
//		if (o == null || ! (o instanceof BCCurrencyMap)) {
//		    
//		    
//		    String[] statical_code_list = 
//			{"USD","CHF","CAD","CNY","GBP","JPY","SGD"};
//			double[] statical_rate_list = 
//			{1.2585,1.5674,1.6837,10.416,0.6847,132.80,2.1335};
//			
//			bananas = new BCCurrencyMap(myDefaultCurrency);
//			for (int i = 0; i < statical_code_list.length; i++)
//			    bananas.put(statical_code_list[i],statical_rate_list[i]);
//		    
//			
//			saveMap() ;
//		} else {
//		    bananas = (BCCurrencyMap) o;
//		}
//		bananas.getCurrencies();
//		
//		if (bananas.size() == 0) {
//		    m_log.fatal("There is no money in the Currency list!");
//		    bananas.put(myDefaultCurrency,1d);
//		}
//		
//		// Default Currency
//	    String s = BC.getParameterStr(PARAMS_CODE);
//	    if (s == null || bananas.getValue(s) < 0) {
//	        defaultCurrency = new Currency(myDefaultCurrency);
//	        saveDefault();
//	    } else {
//	        defaultCurrency = new Currency(s);
//	    }
		
	}
	
	/** save the default currency to the parameters **/
	private void saveDefault() {
	    BC.setParameter(Params.KEY_DEFAULT_CURRENCY_CODE, 
	            defaultCurrency.currencyCode());
	}
	
	/** save my map to the parameters **/
	private void saveMap() {
	    BC.setParameter(Params.KEY_CURRENCY_MAP, bananas);
	}
	
	

//	public String[] getSymbols() {
//		return this.symbols;
//	}

	public Currency defaultCurrency() {
		return this.defaultCurrency;
	}
	
	
	public void setDefaultCurrency(Currency c) {
		if (c == null || c.xequals(defaultCurrency)) return;
		defaultCurrency = (Currency) c.copy();
		fireCurrencyChangeEvent(c.currencyCode());
		saveDefault();
	}
	
	/** return the value of a currency in bananas **/
	private double getValue(Currency c) {
	    return getValue(c.getCurrencyCode());
	}
	/** return the value of a currency code in bananas **/
	private double getValue(String c) {
	    double res = bananas.getValue(c);
	    while (res < 0) {
	        m_log.warn("new Currency Found"+c);
	        new NewCurrencyFoundUI(c);
	        res = bananas.getValue(c);
	    }
	    return res;
	}
	
	/** set the value of a currency in bananas **/
	private void setValue(Currency c,double v) {
	    bananas.put(c.currencyCode(),v);
	}
	
	public void setValue(Currency fixed, Currency toChange, double value) {
		// get banana value	
		setValue(toChange,getValue(fixed) * value);
		
		// We save the new banana table
		saveMap();
		fireCurrencyChangeEvent(toChange.currencyCode());
	}
	
	public void addCurrency(String currencyCode,Currency reference,double value)
	{
	    bananas.put(currencyCode,getValue(reference) * value);
	    fireCurrencyChangeEvent(currencyCode);
	}

	public double getValue(Currency reference, Currency c) {
		return getValue(c) / getValue(reference);
	}

	public Currency[] getCurrencies() {
		return bananas.getCurrencies();
	}

	public boolean currencyCodeExists(String cCode) {
		return getValue(cCode) > 0;
	}

	
	/**************** Currency change events ****************/
	
	/** the vector of my weak listeners **/
	private transient WeakHashMap currencyEventListeners;
	
	/** 
	 * add an Weak Currency Event Listener<BR>
	 * They will be automticaly removed by the GarbageCollector.<BR>
	 * Note that EventListeners must be declared and cannot be anonymous!
	 **/
	public void addWeakCurrencyChangeListener(ChangeListener el) {
		if (currencyEventListeners == null) 
			currencyEventListeners = new WeakHashMap();
		currencyEventListeners.put(el,null);
	}
	
	/** 
	 * fire an Cuurency Change event
	 **/
	private void fireCurrencyChangeEvent(String currencyCode) {
		if (currencyEventListeners == null) return;
		Iterator i= currencyEventListeners.keySet().iterator();
		ChangeEvent e = new ChangeEvent(currencyCode);
		
		while (i.hasNext()) 
			 ((ChangeListener) i.next()).stateChanged(e);
	}	
	
	
	/**
	 * UI INTERFACE POPUPED UP WHEN A NEW CURRENCY HAS BEEN FOUND<BR>
	 * It ask the user for the value of this currency
	 */
	class NewCurrencyFoundUI  {
	    
	    boolean done ;
	    
	    double value;
	    
	    Currency myCurrency;
	    
	    public NewCurrencyFoundUI(final String currencyCode) {
	        done = false;
	        value = 1d;
	        myCurrency = new Currency(null);
	        
	        final JButton jb = new JButton(Lang.translate("Ok"));
	        
	        SNumField sn = new SNumField(1d) {
                public void stopEditing() {
                   value = 1d;
                   if (getDouble() != null) {
                       value = getDouble().doubleValue();
                       if (value <= 0) value = 1d;
                   }
                   setDouble(value);
                   jb.setEnabled(true);
                }
                public void startEditing() {
                    jb.setEnabled(false);
                }};
	        sn.setPreferredSize(new Dimension(100,30));
            CurrencyChooserCombo ccc = 
                new CurrencyChooserCombo(myCurrency){
                    protected void valueChanged() {}};
                
	        sn.setDigitAfterComa(5);
	        
	        
	        JLabel title = new JLabel( "<HTML>"+Lang.translate(
	               "new currency found")+"</HTML>");
	        
	        JLabel currency = new JLabel(" 1.00 "+currencyCode+"=");
	        
	        JPanel jp = new JPanel(new BorderLayout());
	        jp.add(title,BorderLayout.NORTH);
	        
	        JPanel center = new JPanel(new FlowLayout());
	        center.add(currency);
	        center.add(sn);
	        center.add(ccc);
	        jp.add(center,BorderLayout.CENTER);
	        
	        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
	        south.add(jb);
	       
	        jp.add(south,BorderLayout.SOUTH);
	       
	        
	        
	        
	       final ModalJPanel mjp = ModalJPanel.createSimpleModalJInternalFrame(
	                jp,BC.bc.getMajorComponent(),new Point(50,50),
	                false,Resources.iconCurrencyManager,Resources.modalBgColor);
	        final Object tthis = this;
	        jb.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e) {
                    addCurrency(currencyCode,myCurrency,value);
                    done = true;
                    synchronized (tthis) {
                    	tthis.notify();	
                    }
                    mjp.close();
                }});
	        
	        while (! done())
	        	synchronized(this) {
	            try {
	                wait();
	            } catch (InterruptedException e) {
	                m_log.error("",e);
	            }     
	        	}
	    }
	    
      

        /**
         * @see com.simpledata.bc.tools.BCWait.ForMe#done()
         */
        public boolean done() {
            return done;
        }
	    
	}
	
}




/*
 * $Log: BCCurrencyManager.java,v $
 * Revision 1.2  2007/04/02 17:04:25  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:39  perki
 * First commit on sourceforge
 *
 * Revision 1.16  2004/11/16 18:30:51  carlito
 * New parameter management ...
 *
 * Revision 1.15  2004/10/15 06:38:59  perki
 * Lot of cleaning in code (comments and todos
 *
 * Revision 1.14  2004/10/07 08:21:08  perki
 * Keyring ok
 *
 * Revision 1.13  2004/10/04 10:10:31  jvaucher
 * - Minor changes in FileManagement, allowing to choose the dialogType
 * - Helper skeleton
 * - Improved rendering of Tarification Report
 * - Dispatcher bound can yet disable the upper bound
 *
 * Revision 1.12  2004/09/24 17:31:24  perki
 * New Currency is now handeled
 *
 * Revision 1.11  2004/09/23 14:45:48  perki
 * bouhouhou
 *
 * Revision 1.10  2004/09/22 06:47:05  perki
 * A la recherche du bug de Currency
 *
 * Revision 1.9  2004/09/03 13:25:34  kaspar
 * ! Log.out -> log4j part four
 *
 * Revision 1.8  2004/07/06 17:31:25  carlito
 * Desktop manager enhanced
SButton with border on macs
desktop size persistent
 *
 * Revision 1.7  2004/06/28 16:47:54  perki
 * icons for tarif in simu
 *
 * Revision 1.6  2004/05/31 17:57:22  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/05/31 17:08:05  perki
 * *** empty log message ***
 *
 * Revision 1.4  2004/05/31 16:59:49  carlito
 * *** empty log message ***
 *
 * Revision 1.3  2004/05/31 16:56:31  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/05/31 16:38:49  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/05/31 16:12:52  carlito
 * *** empty log message ***
 *
 */