/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.bc.datamodel;

import java.util.*;

import com.simpledata.bc.BC;
import com.simpledata.bc.Resources;
import com.simpledata.bc.SoftInfos;
import com.simpledata.bc.components.tarif.PairManagerAssetsTransactions;
import com.simpledata.bc.components.tarif.TarifManager;
import com.simpledata.bc.components.worksheet.dispatcher.DispatcherIf;
import com.simpledata.bc.datamodel.calculus.ComCalc;
import com.simpledata.bc.datamodel.compact.CompactTree;
import com.simpledata.bc.datamodel.compact.CompactTreeNode;
import com.simpledata.bc.datamodel.event.NamedEvent;
import com.simpledata.bc.datamodel.pair.PairManager;
import com.simpledata.bc.tools.DoubleSideMap;
import com.simpledata.bc.tools.SerializableWeakHashMap;

import com.simpledata.util.CollectionsToolKit;

import org.apache.log4j.Logger;

/**
* Tarification
* Object that contains and handle all the information concerning a 
* Tarification for a bank at agiven time.
*/
public class Tarification extends Named {
	private static final Logger m_log = Logger.getLogger( Tarification.class ); 
	/** Class type for Named @see Named**/
	public final static String CLASS_TYPE = "TARIFICATION";
	
	/** References the root Tree **/
	protected BCTree treeBase;
	
	/** 
	 * Contains all the trees by prefered order<BR>
	 * Order of trees is important for son / parent research
	 * **/
	protected ArrayList treesN;
	
	/** 
	 * Contains all the (visual version), which is only used by the 
	 * simulation.
	 * trees by prefered order 
	 **/
	protected ArrayList treesNVisual;
	
	/** 
	 * Contains all the options and their link to WorkSheets 
	 * options are on the left,
	 * worksheet on the right
	 * **/
	protected DoubleSideMap optionsLinks;
	
	/** 
	 * Contains all the tarifs and their link to WorkSheets 
	 * tarifs are on the left,
	 * bcnodes on the right
	 * 
	 **/
	protected DoubleSideMap tarifsNodesMap;
	
	/**
	 * Containes depencies between certain options and tarifs
	 * (exemple sum of amount under tarif)<BR>
	 * options are on the left, tarif on the right side
	 */
	protected DoubleSideMap optionTarifDependecies;
	
	/** 
	 * Contains all the named objects <BR>
	 * Do not save as XML causes StackOverflow (possible loop)
	 * Named is in charge of loading the new IDS
	 **/
	protected SerializableWeakHashMap named ;
	
	/** Counter for tarif creation **/
	protected int namedCounter = 100000;

	/** 
	 * The object that makes calculus 
	 * Must no be saved!!
	 * **/
	private transient ComCalc comCalculator;
	
	/** 
	 * the creation tag that will be added to created objects 
	 * this tag comes from the license ...
	 **/
	private transient String namedCreationTagHid;
	
	/**
	 * this boolean is set to true or false when it is ready or not for calcul
	 * AND for object manipulation
	 */
	public transient boolean readyForCalculusAndUse; 
	
	/**
	 * this the container for all informations concerning this
	 * Tarification
	 */
	private TarificationHeader header;
	
	
	/**
	 * the PairManager (for Assets/Transaction)
	 */
	private PairManager pairManager;

	// ------------------- INITIALIZATION ---------------------------- //
	
	/**
	* Constructor
	* Note!! Tarification.clean() must be called before closing! 
	* @see Tarification#clean()
	*/
	public Tarification (String id) {
		// call named
		super(CLASS_TYPE,null,"No Title",id);
		
		header = new TarificationHeader();
		header.setPublishingDate(new Date());
		header.changeIcon(Resources.tarificationDefaultIcon3232);
		readyForCalculusAndUse = false;
		
		
		
		init();
		
		
		// create trees
		Iterator i = BCTree.TREE_TITLES.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			_createTree((String) me.getKey(),(String) me.getValue());
		}
		
		
		treeBase = getTree(BCTree.TYPE_BASE);
		
		
		readyForCalculusAndUse = true;
	}
	
	
	// init vars
	private void init() {
		treesN = new ArrayList();
		optionsLinks = new DoubleSideMap(true);
		tarifsNodesMap = new DoubleSideMap(true);
		optionTarifDependecies = new DoubleSideMap(true);
	}
	
	// --------------------- To get the actual calculator ------------//
	
	/** 
	 * return the actual comCalculator  
	 * Can return null if not ready for calculus;
	 * **/
	public synchronized ComCalc comCalc() {
		if (comCalculator == null)
			comCalculator = new ComCalc(this);
		return comCalculator;
	}
	
	/**
	 * call this when this tarification ready for calculus<BR>
	 * Normally <B>AFTER</B> opening a file on the system.
	 */
	public void setReadyForCalcul () {
		readyForCalculusAndUse = true;
		//TODO remove this line when bug for shadow BCNodes is found
		clean();  clean();
	}
	
	// --------------------- ID AND INSTANCE HANDELING ---------------//
	
	/** 
	 * this is the tag that is used to identify if an object 
	 * is an standard (non user object)<BR>
	 * If a NID starts with this Tag it means it has been created
	 * by SimpleData and that this object is unique
	 * @see #getCreationTag()
	 * @see #changeCreationTag(String s)
	 * @see Named#isUserInstance()
	 */
	public static final String publishingCreationTag = "00BASE";
	
	
	/** 
	 * get the header of id creation 
	 * The header look like this:
	 * TarificationID|startedworking on at|licenseid
	 **/
	public synchronized String getCreationTag() {
		if (namedCreationTagHid == null) changeCreationTag("");
		return namedCreationTagHid;
	}
	
	/** 
	 * set the header of id creation 
	 * @param s if set to null or (length == 0) then default licensed 
	 * to id will be used
	 * **/
	public synchronized void changeCreationTag(String s) {
		if (s == null || s.length() < 1) {
			s = getNID()+"|"+(new Date()).getTime()+"|"+
				SoftInfos.id();
			m_log.debug( "header set to default:" + s );
		}
		namedCreationTagHid = s;
	}
	
	
	/** generate Ids for named objects **/
	public synchronized String getNextUniqueId() {
	    String res ;
	    do {
	        res = getCreationTag()+""+namedCounter++;
	    } while (getInstanceForNID(res) != null);
		return res;
	}
	
	
	
	/** 
	 * register a new named object 
	 **/
	public void newNamed(Named n,boolean change) {
		String nNID = n.getNID();
		if (readyForCalculusAndUse) {
			
			if (change) {
				getNamedMap().remove(n);
				m_log.debug( "Changed nid for"+n);
			}
			Named temp = getInstanceForNID(nNID);
			if (temp == n) return;
			if (temp != null) {
				m_log.error( "Cannot add object ["+n
						+"]with id ["+xNID+"] : id ["+nNID
						+"]  already taken by["+temp+"]");
				return;
			}
		}
		getNamedMap().put(n,""); 
	}
	
	/** get the instance corresponding to this NID **/
	public Named getInstanceForNID(String nID) {
		Iterator i = getNamedMap().keySet().iterator();
		Named n = null;
		while (i.hasNext()) {
			n = (Named) i.next();
			if (n.getNID().equals(nID))
				return n;
		}
		return null;
	}
	
	/** 
	 * get all the instances of this class <BR>
	 * Take care it's not up-to date and can contains steals instances
	 * **/
	public ArrayList getAllInstancesOf(Class c) {
		ArrayList result = new ArrayList();
		Iterator i = getNamedMap().keySet().iterator();
		Object temp = null;
		while (i.hasNext()) {
			temp = i.next();
			if (c.isInstance(temp))
				result.add(temp);
		}
		return result;
	}
	
	/**
	 * get the Named WeakHashMap
	 */
	private SerializableWeakHashMap getNamedMap() {
		if (named == null)
			named = new SerializableWeakHashMap();
		return named;
	}
	

	
	/**
	 * return true if this Tarification is valid
	 */
	public boolean isValid() {
		// look up all Tarifs and check if they have a valid mapping
		Iterator e = getAllTarifs().iterator();
		while (e.hasNext()) {
			if (! ((Tarif) e.next()).isValid())
				return false;
		}
		// look up on all WorkSheet if they are valid
		
		return true;
	}
	
	// --------------------- OPTIONS MANIPULATION --------------------//
	
	
	
	/**
	* get all the options
	*/
	public ArrayList getAllOptions() {
		return optionsLinks.getLeftObjects();
	}
	
	
	/** 
	 * check if options having a dependency on a Tarif still
	 * belongs to a WorkSheet. and remove them if needed
	 */ 
	public void checkOptionTarifDependecies() {
		Iterator e = optionTarifDependecies.getLeftObjects().iterator();
		BCOption bco = null;
		while (e.hasNext()) {
			bco = (BCOption) e.next();
			if (optionsLinks.getRightOf(bco).size() == 0) {
				bco.fireNamedEvent(NamedEvent.TARIF_OPTION_LINK_DROPED);
				optionTarifDependecies.remove(bco,null);
			}
		}
	}
	
	/**
	 * get the Tarifs Linked to an Option
	 * @return ArrayList&lt;Tarif>
	 */
	public ArrayList/*<Tarif>*/ getOptionTarifsDependenciesFor(BCOption bco) {
		return optionTarifDependecies.getRightOf(bco);
	}
	
	
	// --------------------- TARIFS MANIPULATION ---------------------- //

	/**
	* create a new Tarif
	* @param title (code) of this tarif
	* @param tarifType one of TypesAndConstraints#getTarifTypes();
	* @return null if failed
	* @see TypesAndConstraints;
	*/
	public synchronized Tarif createTarif(String title,String tarifType) {
		if (! TarifManager.checkTarifExists(tarifType)) {
			m_log.error( "Tried to create a tarif with unkwon Type:"+
			             tarifType);
			return null;
		}
		
		Tarif tf = TarifManager.createTarif(this,title,tarifType);
		return tf;
	}
	
	/**
	* get a list of the known tarifs
	*/
	public ArrayList getAllTarifs() {
		return tarifsNodesMap.getLeftObjects();
	}
	
	/**
	* get a list of the known tarifs that matches this set of Class (subclass or class)
	*/
	public ArrayList getTarifsListOfClass(Class[] classes) {
		ArrayList result = new ArrayList();
		Tarif[] tfs = (Tarif[]) getAllTarifs().toArray(new Tarif[0]);
		for (int i = 0; i < tfs.length; i++) {
			for (int j = 0; j < classes.length; j++) {
				if (classes[j].isInstance(tfs[i]) &&
					(! result.contains(tfs[i])))
					result.add(tfs[i]);		
			}
		}
		
		return result;
	}
	
	
	/**
	* get the tarifs that are spatialized exactly at this position.<BR>
	*/
	public ArrayList/*<Tarif>*/ getTarifsAt(ArrayList/*<BCnode>*/ positions) {
		ArrayList/*<Tarif>*/ result = new ArrayList/*<Tarif>*/();
		if (positions.size() == 0) return result;
		
		// get an array for faster manipulation ?? is it real?
		BCNode[] position = (BCNode[]) positions.toArray(new BCNode[0]);
	
		// get the Tarif Mapping for the first node
		result = position[0].getTarifMapping();
		
		// remove from this list the Tarifs not present
		for (int i = 0; ((i < position.length) && (result.size() > 0)); i++) {
			CollectionsToolKit.collectionsInclusion(result, 
					position[i].getTarifMapping());
		}
		return result;
	}
	
	
	
	// ------------------- TREE MANIPULATION ------------------------- //
	
	
	/** 
	 * copy the system tree to the visual tree list
	 */
	public void synchronizeTrees() {
	    treesNVisual = (ArrayList) treesN.clone();
	}
	
	/**
	* create a new BCTree of the specified type
	* @param tid the type of tree
	* @return if a Tree is already defined for this type return the known tree. null otherwise. 
	*/
	private synchronized BCTree _createTree(String tid,String title) {
		if (tid == null) { 
			m_log.error( "tried to create a  tree with a null id" );
			return null;
		}
		if (getTree(tid) != null) return getTree(tid);
		BCTree bct = new BCTree(this,tid,title,tid);
		treesN.add(bct);
		synchronizeTrees(); // synchronize the visual Tree to this one
		return bct;
	}
	
	
	/**
	* Return the Tree  denoting this dimension.<BR>
	* @return null if this dimension does not extists
	* @param type references one of TypesAndConstraints.getTreesTypes()
	*/
	private BCTree getTree(String type) {
		Iterator e = treesN.iterator();
		BCTree temp ;
		while (e.hasNext()) {
			temp = (BCTree) e.next();
			if (temp.getType().equals(type))
				return temp;
		}
		return null;
	}
	
	/**
	* Return the (ordered) Trees of this Tarification
	*/
	public BCTree[] getMyTrees() {
		return (BCTree[]) treesN.toArray(new BCTree[0]);
	}
	
	/**
	* Return the (ordered) Visual Trees marked as visible
	*/
	public BCTree[] getMyTreesVisible() {
		return getMyTreesVisibleAndIgnore(null);
	}
		
	/**
	* Return the (ordered) Visual Trees marked as visible<BR>
	* But ignore the tree passed by parameter<BR>
	* this is used by BCTree.isVisible() to avoid infinite loops<BR>
	* Note: the ignored tree will be added to the list of BCTrees!!
	*/
	protected BCTree[] getMyTreesVisibleAndIgnore(BCTree ignoredTree) {
		ArrayList temp = new ArrayList();
		
		// VERSIONING CODE
		// Previous versions (up to Tarification 1.95) does not have
		// treesNVisual array
		if (treesNVisual == null) {
		    m_log.warn("VERSIONNIG: treesNVisual");
		    synchronizeTrees();
		}
		
		Iterator e = treesNVisual.iterator();
		BCTree t = null;
		if (ignoredTree != null) {
			temp.add(ignoredTree);
		}
		while (e.hasNext()) {
			t = (BCTree) e.next();
			if (ignoredTree != t && t.isVisible()) {
				temp.add(t);
			}
		}
		
		// If no trees are visible then add the BaseTree
		// should never happen
		if (temp.size() == 0) {
			temp.add(getTreeBase());
			m_log.fatal( "I am in an unstable state" );
		}
		
		return (BCTree[]) temp.toArray(new BCTree[0]);
	}
	
	/**
	 * Change tree order.<BR>
	 * Position 0 cannot be moved<BR>
	 * Note: manipulationwill filr a
	 *  NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED
	 * @param onlyVisualTree true if we modifiy the visual tree (SIMULATION)
	 * else will also affect the system tree
	 */
	public void treesReorder(BCTree tree,int newPos,boolean onlyVisualTree) {
	    int oldPos = treesNVisual.indexOf(tree);
	    if ((oldPos < 0) || (newPos < 0) || (treesNVisual.size() <= newPos)) {
	        m_log.debug( "Invalid position" +newPos
					              +" required for tree:"+tree );
			return;
	    }		
		if (oldPos == newPos) return;
		
		if (newPos > oldPos) {
			// We must insert a left shift to compensate
			// node removal
			newPos--;
		}
		
		treesNVisual.remove(tree);
		treesNVisual.add( newPos, tree);
		
		
		 // synchronize the system Tree to this one
		if (! onlyVisualTree) {
		    treesN = (ArrayList) treesNVisual.clone();
		}
		fireNamedEvent(NamedEvent.BCTREE_ORDER_OR_VISIBLE_STATE_CHANGED);
	}
	

	/**
	 * Get the base Tree <BR>
	 * Note : also used by XML!!
	 */
	public BCTree getTreeBase() {
		return treeBase;
	}
	
	
	//---------------------- CompactTree -------------------//

	
	private transient CompactTree compactTree;
	
	/** 
	 *  get the compactTree (a compressed version) resulting from this 
	 * Tarification<BR>
	 * To get a custom version of this tree use:
	 * CompactTreeNode getTreeForTarifs(ArrayList tarifs, BCTree[] trees)
	 */
	public CompactTree getCompactTree() {
		if (compactTree == null) compactTree 
			= new CompactTree(this,false,-1);
		return compactTree;
	}
	
	private transient CompactTree compactVisibleTree;
	/** 
	 * same as getCompactTree but directly get the root node and
	 * only for visible trees<BR> 
	 * Note: changes on this tree fires : 
	 * NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED
	 * **/
	public CompactTreeNode getCompactVisibleTreeRoot() {
		if (compactVisibleTree == null) 
		    compactVisibleTree = new CompactTree(this,true,
					NamedEvent.COMPACT_TREE_STRUCTURE_CHANGED);
		return compactVisibleTree.getRoot();
	}
	
	
	/**
	 * Do no use this directly<BR>
	 * This method is reserved for CompactTree that uses tarification
	 * To fire structure changes.
	 */
	public void _fireCompactTreeStructureChange(int eventCode) {
		fireNamedEvent(eventCode);
	}
	
	
	
	//-------------------- header generation -------------//
	
	/**
	 * clean the header, set values depending on the state of this
	 * tarification. The returned object is mainly used to save
	 * a description of the Tarification.
	 */
	public TarificationHeader updateAndGetHeader() {
		if (header == null) {
			m_log.warn( "my header was null" );
			header = new TarificationHeader();
			
		}
		// update the title of this tarification
		header.setXTitle(getXTitle().copyTS());
		// update the description of this tarification
		header.setXDescription(getXDescription().copyTS());
		
		return header;
	}
	
	
	//-------------------- debuging ---------------------//
	/**
	* return a textual reprensentation of this Tarification
	*/
	public String toString() {
	    
	    
	    
		//return getCompactTree().toString();
		
		return getFullNID();
		/**
		StringBuffer sb = new StringBuffer();
		sb.append("###########################\n###Title:").append(getTitle());
		sb.append("###\n####").append(getFullNID()).
		append("###\n####################\n");
		
		BCTree[] bcts = (BCTree[]) trees.values().toArray(new BCTree[0]);
		for (int i = 0; i < bcts.length; i++ ) {
			sb.append(bcts[i].getFullNID()).append("\n");
			toStringTree(bcts[i].getRoot(),sb,new Integer(0));
		}
		
		return sb.toString();
		**/
	}
	
	/** get the list of Options and their Mapping **/
	public String toStringOptions() {
		StringBuffer sb = new StringBuffer();
		sb.append("Option/WorkSheet table\n");
		sb.append(optionsLinks.toString());
		return sb.toString();
	}
	
	
	/** 
	 * to String helper.. 
	 * DEBUG... 
	 * TODO remove if not used
	 * **/
	public void toStringTree(BCNode node,StringBuffer res,Integer depth) {
		String dummy = "                                     ";
		depth = new Integer(depth.intValue() + 1);
		res.append(dummy.substring(0,depth.intValue())).append("-").
		append(node.getNID()).append(":").append(node.getTitle()).append("\n");
		ArrayList tarifs = node.getTarifMapping();
		Tarif t = null;
		for (int i = 0; i < tarifs.size(); i++) {
			t = ((Tarif) tarifs.get(i));
			res.append(dummy.substring(0,depth.intValue())).
			append(" .").append(t.getNID()).append(":");
			res.append(t.getTitle()).append("\n");
		}
		ArrayList childs = node.getChildrens();
		for (int i = 0; i < childs.size(); i++) {
			toStringTree((BCNode) childs.get(i),res,depth);
		}
		depth = new Integer(depth.intValue() - 1);
	}
	
	
	
	
	
	//---------------------- CLEAN
	
	/**
	* Inspect all the data and clean unkown links<BR>
	* For known only update TSTrings
	*/
	public void clean() {
	    m_log.warn("CLEAN");
		// Tarifs
		// remove all tarifs that are not mapped
	    Iterator/*<Tarif>*/ i = getAllInstancesOf(Tarif.class).iterator();
	    Tarif tarif;
	    while (i.hasNext()) {
	        tarif = (Tarif) i.next();
	        if (tarif.getMyMapping().size() == 0) {
	            m_log.warn("CLEANED TARIF "+tarif);
	            tarif.drop();
	            named.remove(tarif);
	        }
	    }
	    
	    
	    // WorkSheets
	    // remove all worksheets that have no real parents
	    Iterator/*<Tarif>*/ j = getAllInstancesOf(WorkSheet.class).iterator();
	    WorkSheet ws;
	    while (j.hasNext()) {
	        ws = (WorkSheet) j.next();
	        
	        Tarif t = ws.getTarif();
	        boolean dropMe = false;
	        if (t == null || t.xWorkSheet == null) {
	            m_log.warn("My Container tarif is null or bogus : "+
                        ws.getTitle());
	            dropMe = true;
	        } else {
	            if (!ws.getWscontainer().getChildWorkSheets().contains(ws)) {
	                m_log.warn("My container does not contain me !!!!! "+
	                        ws.getTitle()+", Contained by Tarif : "+
	                        ws.getTarif().getTitle());
	                dropMe = true;
	                
	            }
	        }
	        if (dropMe) {
	            m_log.warn("CLEANED WS "+ws);
	            ws.setContainer(null);
	            ws.drop();
	        } else {
	            // TODO remove after DispatcherIf erradication has been confirmed
		        if (ws instanceof DispatcherIf) {
		            m_log.error("Found a dispatcher if  : "+ws.getTitle()+
		            		", contained by tarif : "+ws.getTarif().getTitle());
		        }
	        }
	    }
	    
	    // Options 
	    // remove all shadow options
	    Iterator/*<BCOption>*/ k = getAllInstancesOf(BCOption.class).iterator();
	    BCOption opt;
	    while (k.hasNext()) {
	        opt = (BCOption)k.next();
	        ArrayList al = opt.getWorkSheets();
	        if ((al == null) || (al.size() == 0)) {
	            m_log.warn("CLEANED OPT "+opt);
	            opt.setContainer(null);
	            named.remove(opt);
	        }
	        
	    }
	    
	}
	
	
	/**
	 * <B>ALSO USED BY XML</B>
	 * @return Returns the pairManager.
	 */
	public PairManager getPairManager() {
		if (pairManager == null) 
			pairManager = new PairManagerAssetsTransactions(this);
		return pairManager;
	}
	/**
	 *  <B>ALSO USED BY XML</B>
	 * @param pairManager The pairManager to set.
	 */
	public void setPairManager(PairManager pairManager) {
		this.pairManager = pairManager;
	}
	
	
		
	//------------------------ XML ----------------------//
	/** XML **/
	public Tarification() {
		readyForCalculusAndUse = false;
		init();
	}
	
	/** XML **/
	public synchronized void setCreationTag(String s) {
	    // VERSIONNING TODO REMOVE
		m_log.debug("VERSIONNING");
	}
	

	/**
	 * XML
	 */
	public DoubleSideMap getOptionsLinks() {
		return optionsLinks;
	}

	
	/**
	 * XML
	 */
	public DoubleSideMap getTarifsNodesMap() {
		return tarifsNodesMap;
	}





	/**
	 * XML
	 */
	public void setOptionsLinks(DoubleSideMap map) {
		optionsLinks= map;
	}


	/**
	 * XML
	 */
	public void setTarifsNodesMap(DoubleSideMap map) {
		tarifsNodesMap= map;
	}

	/**
	 * XML
	 */
	public void setTreeBase(BCTree tree) {
		treeBase= tree;
	}
	
	/**
	 * XML
	 */
	public void setTreesN(ArrayList map) {
		treesN = map;
	}
	
	/**
	 * XML
	 */
	public ArrayList getTreesN() {
		return treesN;
	}

	/**
	 * XML
	 */
	public void setTreesNVisual(ArrayList map) {
		treesNVisual = map;
	}
	
	/**
	 * XML
	 */
	public ArrayList getTreesNVisual() {
		return treesNVisual;
	}
	
	/**
	 * XML
	 */
	public int getNamedCounter() {
		return namedCounter;
	}

	/**
	 * XML
	 */
	public DoubleSideMap getOptionTarifDependecies() {
		return optionTarifDependecies;
	}

	/**
	 * XML
	 */
	public void setNamedCounter(int i) {
		namedCounter= i;
	}

	/**
	 * XML
	 */
	public void setOptionTarifDependecies(DoubleSideMap map) {
		optionTarifDependecies= map;
	}


	/**
	 * XML<BR>
	 * see updateAndGetHeader() to get the header of this tarification.
	 * @see #updateAndGetHeader()
	 */
	public TarificationHeader getHeader() {
		if (header == null) header = new TarificationHeader();
		return header;
	}
	/**
	 * XML
	 */
	public void setHeader(TarificationHeader header) {
		this.header = header;
	}
	
}

/**
* $Id: Tarification.java,v 1.2 2007/04/02 17:04:23 perki Exp $
* $Log: Tarification.java,v $
* Revision 1.2  2007/04/02 17:04:23  perki
* changed copyright dates
*
* Revision 1.1  2006/12/03 12:48:36  perki
* First commit on sourceforge
*
* Revision 1.100  2004/11/23 14:06:35  perki
* updated tariffs
*
* Revision 1.99  2004/11/19 06:46:37  perki
* better image handeling
*
* Revision 1.98  2004/11/17 15:14:46  perki
* Discount DISPLAY RC1
*
* Revision 1.97  2004/11/15 18:54:04  carlito
* DispatcherIf removed... workSheet and option cleaning...
*
* Revision 1.96  2004/11/10 17:35:46  perki
* Tree ordering is now correct
*
* Revision 1.95  2004/11/09 15:56:04  perki
* *** empty log message ***
*
* Revision 1.94  2004/11/09 12:48:26  perki
* *** empty log message ***
*
* Revision 1.93  2004/10/15 06:38:59  perki
* Lot of cleaning in code (comments and todos
*
* Revision 1.92  2004/10/04 13:28:44  perki
* SoftInfo +
*
* Revision 1.91  2004/09/30 13:57:36  perki
* DoubleSideMap is now much stringor.. abandoned ArrayLists for Objects
*
* Revision 1.90  2004/09/22 06:47:04  perki
* A la recherche du bug de Currency
*
* Revision 1.89  2004/09/16 17:26:37  perki
* *** empty log message ***
*
* Revision 1.88  2004/09/09 16:38:44  jvaucher
* - Finished the OptionCommissionAmountUnder, used by RateOnAmount WorkPlace
* - A bit of cleaning in the DoubleSideMap
*
* Revision 1.87  2004/09/08 16:35:14  perki
* New Calculus System
*
* Revision 1.86  2004/09/03 14:30:02  perki
* *** empty log message ***
*
* Revision 1.85  2004/09/03 12:22:28  kaspar
* ! Log.out -> log4j second part
*
* Revision 1.84  2004/09/03 11:09:30  perki
* *** empty log message ***
*
* Revision 1.83  2004/09/02 15:51:46  perki
* Lot of change in calculus method
*
* Revision 1.82  2004/07/31 16:45:56  perki
* Pairing step1
*
* Revision 1.81  2004/07/08 14:59:00  perki
* Vectors to ArrayList
*
* Revision 1.80  2004/07/08 09:43:20  perki
* *** empty log message ***
*
* Revision 1.79  2004/07/07 05:55:14  perki
* No more loops in Amount Under links
*
* Revision 1.78  2004/07/05 07:24:03  perki
* parent / sons forward is now working
*
* Revision 1.77  2004/07/04 14:54:53  perki
* *** empty log message ***
*
* Revision 1.76  2004/06/28 10:38:47  perki
* Finished sons detection for Tarif, and half corrected bug for edition in STable
*
* Revision 1.75  2004/06/25 10:09:49  perki
* added first step for first sons detection
*
* Revision 1.74  2004/06/25 08:30:55  perki
* oordering in tree modified
*
* Revision 1.73  2004/06/23 18:38:04  perki
* *** empty log message ***
*
* Revision 1.72  2004/06/22 11:06:50  carlito
* Tree orderer v0.1
*
* Revision 1.71  2004/06/22 08:59:05  perki
* Added CompactTree for CompactNode management and first sync with CompactExplorer
*
* Revision 1.70  2004/06/21 17:26:14  perki
* added compact tree node
*
* Revision 1.69  2004/06/21 16:27:31  perki
* added compact tree node and visibility / reorder for bctree
*
* Revision 1.68  2004/06/21 14:52:32  perki
* Now BCTrees are stored into a vector
*
* Revision 1.67  2004/06/21 14:45:06  perki
* Now BCTrees are stored into a vector
*
* Revision 1.66  2004/06/20 16:09:03  perki
* *** empty log message ***
*
* Revision 1.65  2004/06/18 18:25:39  perki
* *** empty log message ***
*
* Revision 1.64  2004/06/16 09:58:28  perki
* *** empty log message ***
*
* Revision 1.63  2004/06/16 07:49:28  perki
* *** empty log message ***
*
* Revision 1.62  2004/05/31 17:08:05  perki
* *** empty log message ***
*
* Revision 1.61  2004/05/31 16:12:40  carlito
* *** empty log message ***
*
* Revision 1.60  2004/05/31 12:40:22  perki
* *** empty log message ***
*
* Revision 1.59  2004/05/27 14:41:56  perki
* added merging state alpha
*
* Revision 1.58  2004/05/23 14:08:11  perki
* *** empty log message ***
*
* Revision 1.57  2004/05/20 10:36:15  perki
* *** empty log message ***
*
* Revision 1.56  2004/05/20 06:11:17  perki
* id tagging
*
* Revision 1.55  2004/05/19 16:39:58  perki
* *** empty log message ***
*
* Revision 1.54  2004/05/18 15:11:25  perki
* Better icons management
*
* Revision 1.53  2004/04/12 12:33:09  perki
* Calculus
*
* Revision 1.52  2004/04/12 12:30:28  perki
* Calculus
*
* Revision 1.51  2004/04/09 07:16:51  perki
* Lot of cleaning
*
* Revision 1.50  2004/03/18 18:08:59  perki
* barbapapa
*
* Revision 1.49  2004/03/18 15:43:33  perki
* new option model
*
* Revision 1.48  2004/03/17 14:28:53  perki
* *** empty log message ***
*
* Revision 1.47  2004/03/17 10:54:45  perki
* Thread for params
*
* Revision 1.46  2004/03/16 14:09:31  perki
* Big Numbers are welcome aboard
*
* Revision 1.45  2004/03/06 11:49:22  perki
* *** empty log message ***
*
* Revision 1.44  2004/03/04 18:44:23  perki
* *** empty log message ***
*
* Revision 1.43  2004/03/04 16:38:05  perki
* copy goes to hollywood
*
* Revision 1.42  2004/03/04 14:32:07  perki
* copy goes to hollywood
*
* Revision 1.41  2004/03/03 20:36:48  perki
* bonne nuit les petits
*
* Revision 1.40  2004/03/03 10:17:23  perki
* Un petit bateau
*
* Revision 1.39  2004/02/26 10:27:37  perki
* TAC goes to hollywood
*
* Revision 1.38  2004/02/26 08:55:03  perki
* *** empty log message ***
*
* Revision 1.37  2004/02/25 19:01:33  carlito
* *** empty log message ***
*
* Revision 1.36  2004/02/25 17:36:54  perki
* *** empty log message ***
*
* Revision 1.35  2004/02/25 16:34:05  perki
* *** empty log message ***
*
* Revision 1.34  2004/02/23 12:39:31  perki
* good night
*
* Revision 1.33  2004/02/22 18:09:20  perki
* good night
*
* Revision 1.32  2004/02/22 10:43:57  perki
* File loading and saving
*
* Revision 1.31  2004/02/19 23:57:25  perki
* now 1Gig of ram
*
* Revision 1.30  2004/02/06 10:04:22  perki
* Lots of cleaning
*
* Revision 1.29  2004/02/04 19:04:19  perki
* *** empty log message ***
*
* Revision 1.28  2004/02/04 15:42:16  perki
* cleaning
*
* Revision 1.27  2004/02/04 11:11:35  perki
* *** empty log message ***
*
* Revision 1.26  2004/02/02 16:32:06  perki
* yupeee
*
* Revision 1.25  2004/02/02 11:21:05  perki
* *** empty log message ***
*
* Revision 1.24  2004/02/01 17:15:12  perki
* good day number 2.. lots of class loading improvement
*
* Revision 1.23  2004/01/30 15:18:12  perki
* *** empty log message ***
*
* Revision 1.22  2004/01/28 15:31:48  perki
* Il neige plus
*
* Revision 1.21  2004/01/23 17:30:59  perki
* *** empty log message ***
*
* Revision 1.20  2004/01/18 18:43:41  perki
* *** empty log message ***
*
* Revision 1.19  2004/01/18 15:21:18  perki
* named and jdoc debugging
*
* Revision 1.18  2004/01/18 14:23:52  perki
* Naming about to be finished
*
* Revision 1.17  2004/01/17 17:21:16  perki
* Naming + et +
*
* Revision 1.16  2004/01/17 14:27:54  perki
* Better (Best?) Named implementation
*
* Revision 1.15  2004/01/17 09:34:44  perki
* Debug ui improvement
*
* Revision 1.14  2004/01/17 08:00:35  perki
* better beans comliance but not done yet
*
* Revision 1.13  2004/01/14 14:43:30  perki
* Double Side Maps and Options
*
* Revision 1.12  2004/01/14 13:56:48  perki
* *** empty log message ***
*
* Revision 1.11  2004/01/10 08:11:44  perki
* UI addons and Look And Feel
*
* Revision 1.10  2004/01/06 17:46:40  perki
* better constraints handling for TNode
*
* Revision 1.9  2003/12/17 17:57:13  perki
* *** empty log message ***
*
* Revision 1.8  2003/12/16 17:10:42  perki
* *** empty log message ***
*
* Revision 1.7  2003/12/16 12:52:50  perki
* Type and constraints + improvements on naming
*
* Revision 1.6  2003/12/15 16:45:11  perki
* *** empty log message ***
*
* Revision 1.5  2003/12/12 12:12:31  perki
* Sevral debuging
*
* Revision 1.4  2003/12/11 18:33:50  perki
* *** empty log message ***
*
* Revision 1.3  2003/12/11 17:25:31  perki
* + Tarif
*
* Revision 1.2  2003/12/11 16:38:47  perki
* + Tarif
*
* Revision 1.1  2003/12/10 16:38:40  perki
* *** empty log message ***
*
*/
