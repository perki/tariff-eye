/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.bc.reports.fee;

import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.simpledata.bc.components.TarifTreeVisitor;
import com.simpledata.bc.components.bcoption.OptionFuture;
import com.simpledata.bc.components.bcoption.OptionMoneyAmount;
import com.simpledata.bc.components.bcoption.OptionTransaction;
import com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherBounds;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherCase;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSequencer;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherSimple;
import com.simpledata.bc.components.worksheet.dispatcher.FuturesRoot0;
import com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0;
import com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceAssetsRateBySlice;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFixedFee;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceFutFeeBySlice;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateBySliceOnAmount;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateOnAmount;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceSimple;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTrRateBySlice;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions;
import com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions;
import com.simpledata.bc.datamodel.money.Money;
import com.simpledata.bc.datamodel.money.TransactionValue;
import com.simpledata.bc.reports.common.ReportRenderContext;
import com.simpledata.bc.tools.Lang;
import com.simpledata.bc.uitools.SNumField;

/**
 * This class visits a tarif tree, and constructs a SubreportAsset for
 * the fee report. The visitor is called on the root node and traverses the
 * tree. If a subreport is generated, the factory can reference it using the
 * m_subreport field. It also computes a summary value into the m_resultingValue
 * field.
 * 
 * @author Simpledata SARL, 2004, all rights reserved.
 * @version $Id: FeeConstructor.java,v 1.2 2007/04/02 17:04:30 perki Exp $
 */
class FeeConstructor implements TarifTreeVisitor {
    // CONSTANTS - Locale
    private static final String ASSET           = "Amount";
    private static final String NUMBER          = "Lines";
    private static final String VOLUME          = "Volume";
    private static final String BUY             = "Buy";
    private static final String SELL            = "Sell";
    private static final String TOTAL_ASSETS    = "Total assets";
    private static final String ON              = "on";
    private static final String ON_OPENING      = "On opening";
    private static final String ON_CLOSING      = "On closing";
    
    // FIELDS

    private static final Logger m_log = Logger.getLogger( FeeConstructor.class );

    /** Resulting SubreportAssets for this worksheet */
    SubreportAssets m_subreport;
    /** Render context */
    private final ReportRenderContext m_ctx;
    
    /**
     * Construct an instance of the visitor. The resulting subreport is
     * set to null since there's no details aviable 
     * @param ctx  Whole report generator context
     */
    FeeConstructor (ReportRenderContext ctx) {
        m_ctx = ctx;
        m_subreport = null;
    }
    
    /**
     * Takes a money object and format it into a String using to decimals, with the currency
     * symbol at the beginning.
     * @param m Money object.
     * @return formatted String.
     */
    private String formatMoney(Money m) {
    	String numPart = SNumField.formatNumber(m.getValueDouble(), 2, true);
    	return m.getCurrency() +" "+ numPart;
    }
    
    private void fillSubreportAssetsRows(LinkedList /*AssetsDetails*/ assets,
    									 Money assetsTotal) {
        
    	if (assets.size() > 0) {
            // subreport header
            m_subreport = new SubreportAssets(m_ctx.getEventManager(),
                    						  Lang.translate(NUMBER),
                    						  Lang.translate(ASSET));
            // subreport rows
            for (int i = 0; i<assets.size() ; i++) {
                AssetsDetails detail = (AssetsDetails)assets.get(i);
                SubreportAssets.DataRow row = m_subreport.produceDataRow();
                row.assetsTitle = "";
                row.assetsNumber = String.valueOf(detail.number);
                row.assetsValue = formatMoney(detail.value);
                m_subreport.addData(row);
            }
            // subreport summary
            m_subreport.setTotalAssetsValue(formatMoney(assetsTotal));
            m_subreport.setTotalAssetsText(Lang.translate(TOTAL_ASSETS));
        }
    }
    
    private void fillSubreportTransaction(LinkedList /*TransactionDetails*/ buy,
    									  LinkedList /*TransactionDetails*/ sell) {
    	if (buy.size() > 0 || sell.size() > 0) {
            // subreport header
            m_subreport = new SubreportAssets(m_ctx.getEventManager(),
                    						  Lang.translate(NUMBER),
                    						  Lang.translate(VOLUME));
            // subreport buy rows
            if (buy.size() > 0) {
            	SubreportAssets.DataRow row = m_subreport.produceDataRow();
            	row.assetsTitle = Lang.translate(BUY);
            	row.assetsNumber = "";
            	row.assetsValue = "";
            	m_subreport.addData(row);
            	for (int i = 0; i<buy.size() ; i++) {
            		TransactionDetails detail = (TransactionDetails)buy.get(i);
            		row = m_subreport.produceDataRow();
            		row.assetsTitle = "";
            		row.assetsNumber = String.valueOf(detail.number);
            		row.assetsValue = formatMoney(detail.value);
            		m_subreport.addData(row);
            	}
            }
            // subreport sell rows
            if (sell.size() > 0) {
            	SubreportAssets.DataRow row = m_subreport.produceDataRow();
            	row.assetsTitle = Lang.translate(SELL);
            	row.assetsNumber = "";
            	row.assetsValue = "";
            	m_subreport.addData(row);
            	for (int i = 0; i<sell.size() ; i++) {
            		TransactionDetails detail = (TransactionDetails)sell.get(i);
            		row = m_subreport.produceDataRow();
            		row.assetsTitle = "";
            		row.assetsNumber = String.valueOf(detail.number);
            		row.assetsValue = formatMoney(detail.value);
            		m_subreport.addData(row);
            	}
            }
            // subreport summary
            m_subreport.setTotalAssetsValue("");
            m_subreport.setTotalAssetsText("");
        }
    }
    
    private void fillSubreportFutures(LinkedList /*<FuturesDetails>*/ details) {
    	if (details.size() > 0) {
            // subreport header
            m_subreport = new SubreportAssets(m_ctx.getEventManager(),
                    						  Lang.translate(ON),
                    						  Lang.translate(NUMBER));
            // subreport rows
            for (int i = 0; i<details.size() ; i++) {
                FuturesDetails detail = (FuturesDetails) details.get(i);
                SubreportAssets.DataRow row = m_subreport.produceDataRow();
                row.assetsTitle = "";
                if (detail.operation == FuturesDetails.CLOSING)
                	row.assetsNumber = Lang.translate(ON_CLOSING);
                else // on opening
                	row.assetsNumber = Lang.translate(ON_OPENING);
                row.assetsValue = String.valueOf(detail.number);
                m_subreport.addData(row);
            }
            // no subreport summary
            m_subreport.setTotalAssetsText("");
            m_subreport.setTotalAssetsValue("");
    	}
    }
    
    /**
     * @see TarifTreeVisitor#caseWorkPlaceRateBySliceOnAmount(WorkPlaceRateBySliceOnAmount)
     */
    public void caseWorkPlaceRateBySliceOnAmount(WorkPlaceRateBySliceOnAmount node) {
        m_log.debug("Visited a WorkPlaceRateBySliceOnAmount node");
    }
    
    /**
     * @see TarifTreeVisitor#caseFuturesRoot0(FuturesRoot0)
     */
    public void caseWorkPlaceFutFeeBySlice(WorkPlaceFutFeeBySlice node) {
        m_log.debug("Visited a FutFeeBySlice node");
    }
    
    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseWorkPlaceWithOnlyOptions(com.simpledata.bc.components.worksheet.workplace.WorkPlaceWithOnlyOptions)
     */
    public void caseWorkPlaceWithOnlyOptions(WorkPlaceWithOnlyOptions node) {
        m_log.debug("Visit a WithOnlyOptions node.");
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseWorkPlaceTransferOptions(com.simpledata.bc.components.worksheet.workplace.WorkPlaceTransferOptions)
     */
    public void caseWorkPlaceTransferOptions(WorkPlaceTransferOptions node) {
        m_log.debug("Visit a TransferOption node.");
    } 

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseWorkPlaceTrRateBySlice(com.simpledata.bc.components.worksheet.workplace.WorkPlaceTrRateBySlice)
     */
    public void caseWorkPlaceTrRateBySlice(WorkPlaceTrRateBySlice node) {
        m_log.debug("Visit a TrRateBySlice node.");
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseWorkPlaceSimple(com.simpledata.bc.components.worksheet.workplace.WorkPlaceSimple)
     */
    public void caseWorkPlaceSimple(WorkPlaceSimple node) {
        m_log.debug("Visit a Simple node.");
    }

    public void caseWorkPlaceAssetsRateBySlice(WorkPlaceAssetsRateBySlice node) {
        m_log.debug("Visit an AssetRateBySlice node.");
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseWorkPlaceRateOnAmount(com.simpledata.bc.components.worksheet.workplace.WorkPlaceRateOnAmount)
     */
    public void caseWorkPlaceRateOnAmount(WorkPlaceRateOnAmount node) {
        m_log.debug("Visit a RateOnAmount node.");
    }

    public void caseWorkPlaceFixedFee(WorkPlaceFixedFee node) {
        m_log.debug("Visit a FixedFee node.");
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseEmptyWorkSheet(com.simpledata.bc.components.worksheet.workplace.EmptyWorkSheet)
     */
    public void caseEmptyWorkSheet(EmptyWorkSheet node) {
        m_log.debug("Visit a EmptyWS node.");
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseDispatcherSimple(com.simpledata.bc.components.worksheet.dispatcher.DispatcherSimple)
     */
    public void caseDispatcherSimple(DispatcherSimple node) {
        m_log.debug("Visit a DispatcherSimple node.");
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseDispatcherSequencer(com.simpledata.bc.components.worksheet.dispatcher.DispatcherSequencer)
     */
    public void caseDispatcherSequencer(DispatcherSequencer node) {
        m_log.debug("Visit a DispatcherSeq node.");
    }

    public void caseDispatcherIf(DispatcherIf node) {
    	m_log.debug("Visit a DispatcherIf node.");
    }

    public void caseDispatcherCase(DispatcherCase node) {
        m_log.debug("Visit a DispatcherCase node.");
        
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseAssetsRoot0(com.simpledata.bc.components.worksheet.dispatcher.AssetsRoot0)
     */
    public void caseAssetsRoot0(AssetsRoot0 node) {
        m_log.debug("Visit a AssetRoot0 node: "+node.getTitle());
        
        ArrayList arr = node.getOptions(OptionMoneyAmount.class);
        m_log.debug("...found "+arr.size()+" options."); 
       
        // Looks for every assets
        LinkedList /*AssetsDetails*/ assets = new LinkedList(); 
        for (int i = 0; i<arr.size() ; i++ ) {
            OptionMoneyAmount bco = (OptionMoneyAmount) arr.get(i);
           	Money asset = bco.moneyValue(null);
           	int number = bco.numberOfLines(null);
           	assets.add(new FeeConstructor.AssetsDetails(number, asset));   	
        }  
        Money totalAssets = node.getSumOfAmount();
        fillSubreportAssetsRows(assets, totalAssets);
    }

    /** (non-Javadoc)
     * @see com.simpledata.bc.components.TarifTreeVisitor#caseTransactionsRoot0(com.simpledata.bc.components.worksheet.dispatcher.TransactionsRoot0)
     */
    public void caseTransactionsRoot0(TransactionsRoot0 node) {
        m_log.debug("Visit a TransactionRoot0 node.");
    
        // looks for every transaction. Bought and sold transactions are
        // in differents object, it avoids to sort the list after.
        ArrayList options = node.getOptions(OptionTransaction.class);
        LinkedList buyTransactions = new LinkedList();
        LinkedList sellTransactions = new LinkedList();
        for (int i=0; i<options.size(); i++) {
        	OptionTransaction ot = (OptionTransaction)options.get(i);
        	TransactionValue tv = ot.getTransactionValue();
        	Money value = tv.getMoneyValue();
        	boolean direction = (tv.inGoingToBank()) ? TransactionDetails.BUY : TransactionDetails.SELL;
        	int number = tv.getAverageNumber();
        	TransactionDetails aTransaction = new TransactionDetails(direction, number, value);
        	if (direction == TransactionDetails.BUY)
        		buyTransactions.addLast(aTransaction);
        	else
        		sellTransactions.addLast(aTransaction);
        }
        fillSubreportTransaction(buyTransactions, sellTransactions);
    }
    
    /**
     * @see TarifTreeVisitor#caseFuturesRoot0(FuturesRoot0)
     */
    public void caseFuturesRoot0(FuturesRoot0 node) {
        m_log.debug("Visit a futuresRoot0 node");
        
        // looks for every future and add it
        ArrayList options = node.getOptions(OptionFuture.class);
        LinkedList /*<FuturesDetails>*/ details = new LinkedList();
        for (int i=0; i<options.size(); i++) {
        	OptionFuture of = (OptionFuture) options.get(i);
        	FuturesDetails fd = new FuturesDetails(of.onOpening(), of.numberOfContracts());
        	details.addLast(fd);
        }
        fillSubreportFutures(details);
    }
    
    /**
	 * @see com.simpledata.bc.components.TarifTreeVisitor#caseDispatcherBounds(com.simpledata.bc.components.worksheet.dispatcher.DispatcherBounds)
	 */
	public void caseDispatcherBounds(DispatcherBounds node) {
		m_log.debug("Visit a Bounds node.");
	}
	
    /**
     * This class contains information for a single transaction.
     */
    static private class TransactionDetails {
    	// CONSTANTS
    	final static boolean BUY  = true;
    	final static boolean SELL = false;
        
    	// FIELDS
    	final Money value;
        final int number;
        final boolean direction;
        
        // CONSTRUCTOR
        TransactionDetails (boolean direction, int number, Money value) {
            this.direction = direction;
            this.number = number;
            this.value = value;
        }
    }
    
    /**
     * This class contains information for a single asset.
     */
    static private class AssetsDetails {
    	// FIELDS
    	final Money value;
        final int number;
        
        // CONSTRUCTOR
        AssetsDetails (int number, Money value) {
            this.number = number;
            this.value = value;
        }
    }
    
    /**
     * This class contains inforamtion for a single future
     */
    static private class FuturesDetails {
    	// CONSTANTS
    	static final boolean CLOSING = false;
    	static final boolean OPENING = true;
    	
    	// FIELDS
    	final boolean operation;
    	final int number;
    	
    	// CONSTRUCTOR
    	FuturesDetails(boolean operation, int number) {
    		this.operation = operation;
    		this.number = number;
    	}
    }

  

   

	
}
