/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */

package com.simpledata.uitools.stree;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.apache.log4j.Logger;

public class STree
	extends JTree
	implements DragSourceListener, DragGestureListener, Autoscroll, TreeModelListener {
		
	private static final Logger m_log = Logger.getLogger( STree.class );

	// If true behavior info will be logged
	private boolean debugTree= false;
	
	// Contains check presence status
	private boolean usesCheck= false;

	// CellRenderer
	private STreeCellRenderer renderer= null;

	// STreeCellPanel
	private STreeCellPanel cellPanel= null;
	// JPopupMenu
	private JPopupMenu jPopupMenu= null;

	// Internal fields
	STreeModel treeModel;
	private STreeNode root;

	// Fields...
	private TreePath _pathSource; // The path being dragged
	STreeNode _sourceNode; // The node being dragged

	TreePath _pathDestination; // The eventual drop path
	STreeNode _destinationNode; //  "     "       "  node
	int _dropDestinationPos= 0; // The position for the drop within
	// _pathDestination children
	boolean _dropOk= false; // Indicates wether drop is permited or not
	boolean _showDropLocation= true;
	// Drop location will not be shown if just around source node

	boolean _paintOverPath= true;
	// Indicates where line or background should be painted
	boolean _paintTiny= false;
	// False if beetween node true if over node

	BufferedImage _imgGhost; // The 'drag image' 
	public Point _ptOffset= new Point();
	// Where, in the drag image, the mouse was clicked


	protected static Color selectionBackground;
	protected static Color selectionForeground;
	
	/**
	 * Constructor
	 * @param dstn root node for the tree
	 * @param withCheck true for checkBoxes, false otherwise
	 */
	// Constructors...	
	public STree(STreeNode dstn, boolean withCheck) {
		root= dstn;
		this.usesCheck= withCheck;
		cellPanel= new STreeCellPanel(withCheck);
		treeModel= new STreeModel(root);
		setModel(treeModel);
		renderer= new STreeCellRenderer(withCheck);
		setCellRenderer(renderer);

		// Set default selection colors
		if (STree.selectionBackground == null) {
		    STree.selectionBackground = UIManager.getColor("Tree.selectionBackground");
		}
		if (STree.selectionForeground == null) {
		    STree.selectionForeground = UIManager.getColor("Tree.selectionForeground");
		}
		
		this.addMouseListener(new CheckAndPopupListener(this));

		putClientProperty("JTree.lineStyle", "Angled");

		// Make this JTree a drag source
		DragSource dragSource= DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(
			this,
			DnDConstants.ACTION_COPY_OR_MOVE,
			this);

		// Also, make this JTree a drag target
		DropTarget dropTarget= new DropTarget(this, new CDropTargetListener());
		dropTarget.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);

		//loadImages();
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.getContentPane().setLayout(new BorderLayout());
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension d = new Dimension(200,400);
		
		JScrollPane scroll = new JScrollPane();
		scroll.setMinimumSize(d);
		scroll.setPreferredSize(d);
		
		STree tree = new STree();
		tree.setRootVisible(false);
		//JTree tree = new JTree();
		scroll.setViewportView(tree);
		jf.getContentPane().add(scroll, BorderLayout.CENTER);
		jf.pack();
		jf.show();
	}
	
	/**
	 * Constructor (default without checkBoxes)
	 * @param dstn
	 */
	public STree(STreeNode dstn) {
		this(dstn, false);
	}

	/**
	 * Constructor (dummy)
	 * constructs a simple tree with sample data
	 */
	public STree() {
		this(new DefaultSTreeNode("temp_root"), true);

		STreeNode stree= new DefaultSTreeNode("STree");

		STreeNode colors= new DefaultSTreeNode("colors");

		STreeNode blue= new DefaultSTreeNode("blue");
		STreeNode violet= new DefaultSTreeNode("violet");
		STreeNode red= new DefaultSTreeNode("red");
		STreeNode yellow= new DefaultSTreeNode("yellow");
		colors.addChild(blue);
		colors.addChild(violet);
		colors.addChild(red);
		colors.addChild(yellow);
		stree.addChild(colors);

		STreeNode sports= new DefaultSTreeNode("sports");

		STreeNode basketball= new DefaultSTreeNode("basketball");
		STreeNode soccer= new DefaultSTreeNode("soccer");
		STreeNode football= new DefaultSTreeNode("football");
		STreeNode hockey= new DefaultSTreeNode("hockey");
		sports.addChild(basketball);
		sports.addChild(soccer);
		sports.addChild(football);
		sports.addChild(hockey);
		stree.addChild(sports);

		STreeNode food= new DefaultSTreeNode("food");

		STreeNode hotdogs= new DefaultSTreeNode("hotdogs");
		STreeNode pizza= new DefaultSTreeNode("pizza");
		STreeNode ravioli= new DefaultSTreeNode("ravioli");
		STreeNode bananas= new DefaultSTreeNode("bananas");
		food.addChild(hotdogs);
		food.addChild(pizza);
		food.addChild(ravioli);
		food.addChild(bananas);
		stree.addChild(food);

		this.changeRoot(stree);
		
		refreshHeightQuarter();
	}

	/**
	 * Tells the tree to show checkBoxes or not
	 * @param withCheck
	 */
	public void setCheckVisible(boolean withCheck) {
		// Lets avoid some management
		if (this.usesCheck == withCheck)
			return;

		this.usesCheck= withCheck;
		cellPanel= new STreeCellPanel(withCheck);
		this.renderer.setCheckVisible(withCheck);

		ArrayList al = this.getExpandedNodes();

		this.fireTreeStructureChanged(this.root);
		this.openOldExpanded(al);
		this.fireRefresh();
	}
	
	/**************************************************************************/
	/**       UTILITIES
	/**************************************************************************/

	public void setRowHeight(int newHeight) {
	    super.setRowHeight(newHeight);
	    refreshHeightQuarter();
	}
	
	private int heightQuarter;
	/** Evaluating closest int value to quarter of line height */
	private void refreshHeightQuarter() {
	    heightQuarter= (STree.this.getRowHeight() / 4);
		double calcRes=
			(((double) (STree.this.getRowHeight() / 4)) - heightQuarter);
		if (calcRes > 0.5) {
			heightQuarter++;
		}
	}
	
	/**
	* Update drag destination
	* drag status : drop authorized on destination
	* destination position
	
	private TreePath         _pathDestination;          // The eventual drop path
	private STreeNode        _destinationNode;          //  "     "       "  node
	private int              _dropDestinationPos = 0;   // The position for the drop within
	                                                    // _pathDestination children
	
	*/
	void updateDragStatus(Point mousePos) {

		TreePath path= this.getClosestPathForLocation(mousePos.x, mousePos.y);
		int targetAltitude= (int) (STree.this.getPathBounds(path)).getY();

		int nSr= this.getRowForPath(this._pathSource); // source row
		int nOr= this.getRowForPath(path); // node over row

		int nAr= nOr - 1; // node above row
		int nUr= nOr + 1; // node under row

		// Filling in above, over and under nodes
		STreeNode nA= null;
		STreeNode nO= (STreeNode) path.getLastPathComponent();
		STreeNode nU= null;

		if (nAr >= 0) {
			nA= (STreeNode) (this.getPathForRow(nAr)).getLastPathComponent();
		}
		if (nUr < this.getRowCount()) {
			nU= (STreeNode) (this.getPathForRow(nUr)).getLastPathComponent();
		}

		// Usefull variables
		STreeNode nD= null; // Destination node
		STreeNode nS= this._sourceNode;
		int nDp= 0; // drop position

		boolean show= true; // show drop location
		boolean thin= false; // selection size line(true) or band(false)
		boolean paintOver= true; // false only when we shift down (case under)

		// Evaluating position of mouse relative to the closest node
		////////////////////////////////////////////////////////////
		int relPos= mousePos.y - targetAltitude;
		out("Entering position evaluation");
		evalPos : while (true) {
			if (relPos <= heightQuarter) {
				// We are on the upper quarter of the node
				out("We are over uper part of " + nO);

				thin= true;

				// Treating 3 first border effects
				if (nOr == 0) {
					out("Treating root side effect");
					nD= nO;
					nDp= 0;
					thin= false;
					if (nSr == nOr) {
						// root node is source
						show= false;
					}
					
					// Evaluating the case when root is not shown
					if (!isRootVisible()) {
					    // We could drop something here
					    nD = getRoot();
					    thin = true;
					}
					
					break evalPos;
				}

				int nOd= nO.getDepth();
				int nAd= nA.getDepth();
				if (nOd != nAd) {
					if (nAd > nOd) {
						out("Node above is deeper than node over");
						// We are trying to insert just after node above
						// under node above's parent
						nD= nA.getParent();
						nDp= nD.getChildCount();
						if (nSr == nAr) {
							show= false;
						}
					} else {
						out("Node over is deeper than node above");
						// Node above is node over's parent
						nD= nA;
						nDp= 0;
						if (nSr == nOr) {
							show= false;
						}
					}
					break evalPos;
				}

				// Normal behavior
				nD= nO.getParent();
				nDp= nO.getIndex();
				if ((nSr == nOr) || (nSr == nAr)) {
					show= false;
				}

				break evalPos;
			} else if (
				(relPos > heightQuarter) && (relPos <= (3 * heightQuarter))) {
				// we are over the middle half of the node
				out("We are on middle half of " + nO);

				thin= false;

				nD= nO;
				nDp= nD.getChildCount();
				if (nSr == nOr) {
					show= false;
				}

				break evalPos;
			} else {
				// We are over the bottom quarter of the node
				out("We are over bottom part of " + nO);

				thin= true;
				paintOver= false;

				if (nU == null) {
					// Under last node
					out("We are under the last node");

					nD= nO.getParent();
					nDp= nD.getChildCount();
					if (nOr == nSr) {
						show= false;
					}
					break evalPos;
				}

				int nOd= nO.getDepth();
				int nUd= nU.getDepth();
				if (nUd != nOd) {
					if (nUd > nOd) {
						// node under is deeper than node over
						// node over is node under's parent
						out("node under is deeper than node over");

						nD= nO;
						nDp= 0;
						if (nUr == nSr) {
							show= false;
						}
					} else {
						// node over is deeper than node under
						out("node over is deeper than node under");

						nD= nO.getParent();
						nDp= nD.getChildCount();
						if (nOr == nSr) {
							show= false;
						}
					}
					break evalPos;
				}

				// normal behavior
				nD= nO.getParent();
				nDp= nU.getIndex();
				if ((nSr == nOr) || (nSr == nUr)) {
					show= false;
				}
				break evalPos;
			}
		}

		if (show) {
			_dropOk= nD.acceptDrop(nS);
		} else {
			_dropOk= false;
		}

		// transcription des variables
		this._showDropLocation= show;
		this._paintOverPath= paintOver;
		this._paintTiny= thin;
		this._destinationNode= nD;
		this._dropDestinationPos= nDp;
		this._pathDestination= getPath(nD);

	}

	/**
	* Select desired node in the tree
	*/
	public void selectNode(STreeNode node) {
		this.setSelectionPath(getPath(node));
	}

	/**
	* Select multiple nodes in the tree
	* from a Vector
	*/
	public void selectNodes(Vector nodes) {
		TreePath[] paths= new TreePath[nodes.size()];
		STreeNode current;
		for (int i= 0; i < nodes.size(); i++) {
			current= (STreeNode) nodes.elementAt(i);
			paths[i]= this.getPath(current);
		}

		if (paths.length > 0) {
			this.setSelectionPaths(paths);
		}
	}

	/**
	 * Select multiple nodes in the tree
	 * from an arrayList
	 */
	public void selectNodes(ArrayList nodes) {
		TreePath[] paths= new TreePath[nodes.size()];
		STreeNode current;
		int count = 0;
		for (Iterator i = nodes.iterator(); i.hasNext();) {
		    current = (STreeNode) i.next();
		    paths[count]= this.getPath(current);
		    count++;
		}

		if (paths.length > 0) {
			this.setSelectionPaths(paths);
		}
	}	
	
	/**
	* Returns TreePath implied by node
	*/
	public TreePath getPath(STreeNode node) {
		if (node == null) {
			m_log.error( "Can not make treePath from null node" );
			return null;
		}
		
		ArrayList al1 = new ArrayList();
		ArrayList al2 = new ArrayList();
		STreeNode cur= node;
		STreeNode curParent= node.getParent();
		al1.add(cur);
		while (curParent != null) {
			cur= cur.getParent();
			curParent= curParent.getParent();
			al1.add(cur);
		}
		for (int i=al1.size()-1; i > -1 ; i--) {
		    al2.add(al1.get(i));
		}
		
		return new TreePath(al2.toArray());
		
		
	}

	/**
	* Debug system.out
	* if debugTree = true
	*/
	void out(String s) {
		if (this.debugTree) {
			m_log.debug( s );
		}
	}

	/**
	* Returns a new Frame
	*/
	public void createDevelWindow() {
		createDevelWindow("Unknown window");
	}

	/**
	* Returns a new frame with title
	*/
	public void createDevelWindow(String title) {
		this.setPreferredSize(new Dimension(300, 300));
		//tree.setRowHeight(32);

		JScrollPane scrollPane= new JScrollPane(this);

		JFrame frame= new JFrame(title);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.pack();

		Dimension dimScreen= Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dimFrame= frame.getSize();
		frame.setLocation(
			(dimScreen.width - dimFrame.width) / 2,
			(dimScreen.height - dimFrame.height) / 2);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				out("Program successfully exited");
				System.exit(0);
			}
		});

		frame.show();
	}

	/**
	* ADDED BY PERKI
	* return this STree in an internal Frame
	*/
	public JInternalFrame createDevelInternalFrame(String title) {
		JInternalFrame jif= new JInternalFrame(title, true, false, true, true);
		jif.setSize(300, 300);
		setLocation(10, 310);
		JScrollPane scrollPane= new JScrollPane(this);
		jif.getContentPane().add(scrollPane, BorderLayout.CENTER);
		jif.setVisible(true);
		return jif;
	}

	// Interface: DragGestureListener
	public void dragGestureRecognized(DragGestureEvent e) {

		Point ptDragOrigin= e.getDragOrigin();
		TreePath path= getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
		if (path == null)
			return;
		if (isRootPath(path))
			return; // Ignore user trying to drag the root node

		STreeNode sTemp= (STreeNode) (path.getLastPathComponent());
		if (sTemp != null) {
			if (!sTemp.acceptDrag())
				return;
		}

		// Work out the offset of the drag point from the TreePath bounding rectangle origin
		Rectangle raPath= getPathBounds(path);
		_ptOffset.setLocation(
			ptDragOrigin.x - raPath.x,
			ptDragOrigin.y - raPath.y);

		// Get the cell renderer (which is a JLabel) for the path being dragged
			JLabel lbl=
				((STreeCellPanel) getCellRenderer()
					.getTreeCellRendererComponent(this,
		// tree
		path.getLastPathComponent(), // value
		false, // isSelected	(dont want a colored background)
		isExpanded(path), // isExpanded
		getModel().isLeaf(path.getLastPathComponent()), // isLeaf
		0, // row			(not important for rendering)
		false // hasFocus		(dont want a focus rectangle)
	)).getLabel();

		lbl.setSize((int) raPath.getWidth(), (int) raPath.getHeight());
		// <-- The layout manager would normally do this

		// Get a buffered image of the selection for dragging a ghost image
		_imgGhost=
			new BufferedImage(
				(int) raPath.getWidth(),
				(int) raPath.getHeight(),
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g2= _imgGhost.createGraphics();

		// Ask the cell renderer to paint itself into the BufferedImage
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
		// Make the image ghostlike
		lbl.paint(g2);

		// Now paint a gradient UNDER the ghosted JLabel text (but not under the icon if any)
		// Note: this will need tweaking if your icon is not positioned to the left of the text
		Icon icon= lbl.getIcon();
		int nStartOfText=
			(icon == null) ? 0 : icon.getIconWidth() + lbl.getIconTextGap();
		g2.setComposite(
			AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.5f));
		// Make the gradient ghostlike
		g2.setPaint(
			new GradientPaint(
				nStartOfText,
				0,
				SystemColor.controlShadow,
				getWidth(),
				0,
				new Color(255, 255, 255, 0)));
		g2.fillRect(nStartOfText, 0, getWidth(), _imgGhost.getHeight());

		g2.dispose();

		setSelectionPath(path); // Select this path in the tree

		out("DRAGGING: " + path.getLastPathComponent());

		// Wrap the path being transferred into a Transferable object
		Transferable transferable= new CTransferableTreePath(path);

		// Remember the path being dragged (because if it is being moved, we will have to delete it later)
		_pathSource= path;
		// Now remember the node being dragged
		_sourceNode= sTemp;

		// We pass our drag image just in case it IS supported by the platform
		e.startDrag(null, _imgGhost, new Point(5, 5), transferable, this);
	}

	// Interface: DragSourceListener
	public void dragEnter(DragSourceDragEvent e) {}
	public void dragOver(DragSourceDragEvent e) {}
	public void dragExit(DragSourceEvent e) {}
	public void dropActionChanged(DragSourceDragEvent e) {}
	public void dragDropEnd(DragSourceDropEvent e) {
		if (e.getDropSuccess()) {
			int nAction= e.getDropAction();
			if (nAction == DnDConstants.ACTION_MOVE) {
				// The dragged item (_pathSource) has been inserted at the target selected by the user.
				// Now it is time to delete it from its original location.
				out("REMOVING: " + _pathSource.getLastPathComponent());

				// .
				// .. ask your TreeModel to delete the node 
				// .

				_pathSource= null;
			}
		}
	}

	// DropTargetListener interface object...
	class CDropTargetListener implements DropTargetListener {
		// Fields...
		TreePath _pathLast= null;
		private Rectangle2D _raCueLine= new Rectangle2D.Float();
		private Rectangle2D _rforbidCueLine= new Rectangle2D.Float();
		private Rectangle2D _raGhost= new Rectangle2D.Float();

		private Color _colorCueLine;
		private Color _colorCueLineTransparent;
		private Color _colorCueLineRed;
		private Point _ptLast= new Point();
		private Timer _timerHover;

		//private boolean _infoDisplayed;

		// Info about extras to display ...
		//private boolean _displayExtras= false;
		//private int _extraX= 0;
		//private int _extraY= 0;

		//private DefaultMutableTreeNode  _graphicNodeLast = null;
		//private STreeNode _nodeLast= null;
		//private STreeNode _nodeLastParent  = null;

		// Constructor...
		public CDropTargetListener() {
			_colorCueLine=
				new Color(
					SystemColor.controlShadow.getRed(),
					SystemColor.controlShadow.getGreen(),
					SystemColor.controlShadow.getBlue(),
					64);
			_colorCueLineTransparent=
				new Color(
					SystemColor.controlShadow.getRed(),
					SystemColor.controlShadow.getGreen(),
					SystemColor.controlShadow.getBlue(),
					0);

			_colorCueLineRed=
				new Color(SystemColor.controlShadow.getRed(), 0, 0, 100);

			// Set up a hover timer, so that a node will be automatically expanded or collapsed
			// if the user lingers on it for more than a short time
			_timerHover= new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						//_nLeftRight = 0;	// Reset left/right movement trend
	if (isRootPath(_pathLast))
						return;
					// Do nothing if we are hovering over the root node
					if (isExpanded(_pathLast)) {
						collapsePath(_pathLast);
					} else {
						expandPath(_pathLast);
					}
				}
			});
			_timerHover.setRepeats(false); // Set timer to one-shot mode
		}

		// DropTargetListener interface
		public void dragEnter(DropTargetDragEvent e) {
			if (!isDragAcceptable(e))
				e.rejectDrag();
			else
				e.acceptDrag(e.getDropAction());
		}

		public void dragExit(DropTargetEvent e) {
			if (!DragSource.isDragImageSupported()) {
				repaint(_raGhost.getBounds());
			}
		}

		/**
		* This is where the ghost image is drawn
		*/
		public void dragOver(DropTargetDragEvent e) {
			// Even if the mouse is not moving, this method is still invoked 10 times per second
			Point pt= e.getLocation();
			if (pt.equals(_ptLast))
				return;

			_ptLast= pt;

			Graphics2D g2= (Graphics2D) getGraphics();

			//Lets create a second Graphics2D to have floating infos to be placed
			//Graphics2D transparentFlyingPanel = (Graphics2D) getGraphics();
			//transparentFlyingPanel.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));		// Make the image ghostlike

			// If a drag image is not supported by the platform, then draw my own drag image
			if (!DragSource.isDragImageSupported()) {
				paintImmediately(_raGhost.getBounds());
				// Rub out the last ghost image and cue line
				// And remember where we are about to draw the new ghost image
				_raGhost.setRect(
					pt.x - _ptOffset.x,
					pt.y - _ptOffset.y,
					_imgGhost.getWidth(),
					_imgGhost.getHeight());
				g2.drawImage(
					_imgGhost,
					AffineTransform.getTranslateInstance(
						_raGhost.getX(),
						_raGhost.getY()),
					null);
			} else { // Just rub out the last cue line
				paintImmediately(_raCueLine.getBounds());
				paintImmediately(_rforbidCueLine.getBounds());
				// Lets repaint all tree just to see
				/*
				if (_infoDisplayed) {
					paintImmediately(getBounds());
					_infoDisplayed = false;
				}
				*/
			}

			TreePath path= getClosestPathForLocation(pt.x, pt.y);

			if (!(path == _pathLast)) {
				_pathLast= path;

				// Analyzing implied STreeNodes
				//_graphicNodeLast = (DefaultMutableTreeNode)_pathLast.getLastPathComponent();
				//_nodeLast = (STreeNode)( ((DefaultMutableTreeNode)_pathLast.getLastPathComponent()).getUserObject() );
				//_nodeLast= (STreeNode) (_pathLast.getLastPathComponent());
				//_nodeLastParent = _nodeLast.getParent();

				_timerHover.restart();
			}

			// Rectangle for parent component in case we need it for displaying info
			//Rectangle parentPathRec = getPathBounds(path.getParentPath());

			// Rectangle for drop path
			Rectangle dropPathRec= getPathBounds(_pathDestination);

			// In any case draw (over the ghost image if necessary) a cue line indicating where a drop will occur
			Rectangle raPath= getPathBounds(path);

			//int rowSource= getRowForPath(_pathSource);
			//int rowCurrent= getRowForPath(path);

			int sel_height= 2;
			int y_origin= raPath.y + (int) raPath.getHeight();
			Color selCol= _colorCueLine;

			//int relPos = relativePosition(pt);
			updateDragStatus(pt);

			// ADDON
			if (STree.this._paintOverPath) {
				y_origin= raPath.y;
			} else {
				y_origin= raPath.y + (int) raPath.getHeight();
			}

			if (STree.this._paintTiny) {
				sel_height= 2;
			} else {
				sel_height= (int) raPath.getHeight();
			}

			if (STree.this._showDropLocation && _dropOk) {
				selCol= _colorCueLine;
			} else {
				selCol= _colorCueLineTransparent;
			}

			// Adding drop line to graphics
			g2.setColor(selCol);
			_raCueLine.setRect(0, y_origin, getWidth(), sel_height);
			g2.fill(_raCueLine);

			if (!_dropOk && _showDropLocation) {
				g2.setColor(_colorCueLineRed);
			} else {
				g2.setColor(_colorCueLineTransparent);
			}

			if (dropPathRec != null) {
				_rforbidCueLine.setRect(
					0,
					dropPathRec.y,
					getWidth(),
					(int) dropPathRec.getHeight());
				g2.fill(_rforbidCueLine);
			}
			/*
			if (_dropOk) {
				paintImmediately(getBounds());
			} else {
				paintImmediately(getBounds());
				g2.drawImage(dropNotOkIcon, AffineTransform.getTranslateInstance(dropPathRec.x, dropPathRec.y), null);
				
			}
			*/

			// SuperImpose a prohibited image if needed
			// Unused for the moment due to remanence problems
			//g2.drawImage(dropNotOkIcon, AffineTransform.getTranslateInstance(pt.x - _ptOffset.x, pt.y - _ptOffset.y), null);			
			/*
			if (!isDropable) {
				if (!_infoDisplayed) {
					if (!prohibitParent) {
						g2.drawImage(dropNotOkIcon, AffineTransform.getTranslateInstance(raPath.x, raPath.y), null);
					} else {
						g2.drawImage(dropNotOkIcon, AffineTransform.getTranslateInstance(parentPathRec.x, parentPathRec.y), null);
					}
					_infoDisplayed = true;
				}
			}
			*/

			/*
			if (_dropOk) {
				if (!_infoDisplayed) {
					if (_paintTiny) {
						g2.drawImage(dropNotOkIcon, AffineTransform.getTranslateInstance(parentPathRec.x, parentPathRec.y), null);
					} else {
						g2.drawImage(dropNotOkIcon, AffineTransform.getTranslateInstance(raPath.x, raPath.y), null);
					}
					_infoDisplayed = true;
				}
			}
			*/

			// And include the cue line in the area to be rubbed out next time
			_raGhost= _raGhost.createUnion(_raCueLine);
			_raGhost= _raGhost.createUnion(_rforbidCueLine);

			// Do this if you want to prohibit dropping onto the drag source
			//if (path.equals(_pathSource))
			if (!_dropOk)
				e.rejectDrag();
			else
				e.acceptDrag(e.getDropAction());
		}

		public void dropActionChanged(DropTargetDragEvent e) {
			if (!isDragAcceptable(e))
				e.rejectDrag();
			else
				e.acceptDrag(e.getDropAction());
		}

		public void drop(DropTargetDropEvent e) {
			_timerHover.stop();
			// Prevent hover timer from doing an unwanted expandPath or collapsePath

			if (!isDropAcceptable(e)) {
				e.rejectDrop();
				return;
			}

			e.acceptDrop(e.getDropAction());

			Transferable transferable= e.getTransferable();

			DataFlavor[] flavors= transferable.getTransferDataFlavors();
			for (int i= 0; i < flavors.length; i++) {
				DataFlavor flavor= flavors[i];
				if (flavor
					.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType)) {
					//try {
						Point pt= e.getLocation();
						//TreePath pathTarget = getClosestPathForLocation(pt.x, pt.y);
						//TreePath pathSource= (TreePath) transferable.getTransferData(flavor);

						//TreeModel model= getModel();
						TreePath pathNewChild= null;

						updateDragStatus(pt);

						STreeNode dad= STree.this._sourceNode.getParent();

						out(
							"Dropping : "
								+ _sourceNode
								+ ", under : "
								+ _destinationNode
								+ ", at index : "
								+ _dropDestinationPos);
						//out("Are we in an order situation... "+(dad).equals(_destinationNode));
						//out("Source node parent : "+dad+", destination Node : "+_destinationNode+".. Are they equals? "+(dad).equals(_destinationNode));

						if ((_sourceNode.getParent())
							.equals(_destinationNode)) {
							if (_sourceNode.order(_dropDestinationPos)) {
								out("order successful");
								treeModel.fireTreeStructureChanged(
									new TreeModelEvent(
										this,
										STree.this.getPath(dad)));
								//pathNewChild = STree.this.getPath(_destinationNode.getChildAt(_dropDestinationPos));
								pathNewChild= STree.this.getPath(_sourceNode);
							}
						} else {

							if (_sourceNode
								.move(_destinationNode, _dropDestinationPos)) {
								out("move successful");

								treeModel.fireTreeNodesRemoved(
									new TreeModelEvent(
										this,
										STree.this.getPath(dad)));
								treeModel.fireTreeStructureChanged(
									new TreeModelEvent(
										this,
										STree.this.getPath(dad)));
								treeModel.fireTreeNodesInserted(
									new TreeModelEvent(
										this,
										STree.this.getPath(_destinationNode)));
								treeModel.fireTreeStructureChanged(
									new TreeModelEvent(
										this,
										STree.this.getPath(_destinationNode)));
								if (_destinationNode.getChildCount() > 0) {
									STree.this.expandPath(
										STree.this.getPath(_destinationNode));
								}
								//pathNewChild = STree.this.getPath(_destinationNode.getChildAt(_dropDestinationPos));
								pathNewChild= STree.this.getPath(_sourceNode);
							}
						}

						// .
						// .. Add your code here to ask your TreeModel to copy the node and act on the mouse gestures...
						// .

						// For example:

						// If pathTarget is an expanded BRANCH, 
						// 		then insert source UNDER it (before the first child if any)
						// If pathTarget is a collapsed BRANCH (or a LEAF), 
						//		then insert source AFTER it
						// 		Note: a leaf node is always marked as collapsed
						// You ask the model to do the copying...
						// ...and you supply the copyNode method in the model as well of course.
						//						if (_nShift == 0)
						//							pathNewChild = model.copyNode(pathSource, pathTarget, isExpanded(pathTarget)); 
						//						else if (_nShift > 0)	// The mouse is being flicked to the right (so move the node right)
						//							pathNewChild = model.copyNodeRight(pathSource, pathTarget); 
						//						else					// The mouse is being flicked to the left (so move the node left)
						//							pathNewChild = model.copyNodeLeft(pathSource); 

						if (pathNewChild != null) {
							setSelectionPath(pathNewChild);
							// Mark this as the selected path in the tree
							STree.this.revalidate();
							STree.this.repaint();
						}
						break; // No need to check remaining flavors
//					} catch (UnsupportedFlavorException ufe) {
//						m_log.error( ufe );
//						e.dropComplete(false);
//						return;
//					} catch (IOException ioe) {
//						m_log.error( ioe );
//						e.dropComplete(false);
//						return;
//					}
				}
			}

			e.dropComplete(true);

			STree.this.repaint();
		}

		// Helpers...
		public boolean isDragAcceptable(DropTargetDragEvent e) {
			// Only accept COPY or MOVE gestures (ie LINK is not supported)
			if ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
				return false;

			// Only accept TreePath Flavor	
			if (!e.isDataFlavorSupported(CTransferableTreePath.TREEPATH_FLAVOR))
				return false;

			/*				
						// Do this if you want to prohibit dropping onto the drag source...
						Point pt = e.getLocation();
						TreePath path = getClosestPathForLocation(pt.x, pt.y);
						if (path.equals(_pathSource))			
							return false;
			
			*/

			/*				
						// Do this if you want to select the best flavor on offer...
						DataFlavor[] flavors = e.getCurrentDataFlavors();
						for (int i = 0; i < flavors.length; i++ )
						{
							DataFlavor flavor = flavors[i];
							if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
								return true;
						}
			*/
			return true;
		}

		public boolean isDropAcceptable(DropTargetDropEvent e) {
			// Only accept COPY or MOVE gestures (ie LINK is not supported)
			if ((e.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) == 0)
				return false;

			// Only accept this particular flavor	
			if (!e
				.isDataFlavorSupported(CTransferableTreePath.TREEPATH_FLAVOR))
				return false;

			/*				
						// Do this if you want to prohibit dropping onto the drag source...
						Point pt = e.getLocation();
						TreePath path = getClosestPathForLocation(pt.x, pt.y);
						if (path.equals(_pathSource))			
							return false;
			*/

			/*				
						// Do this if you want to select the best flavor on offer...
						DataFlavor[] flavors = e.getCurrentDataFlavors();
						for (int i = 0; i < flavors.length; i++ )
						{
							DataFlavor flavor = flavors[i];
							if (flavor.isMimeTypeEqual(DataFlavor.javaJVMLocalObjectMimeType))
								return true;
						}
			*/
			return true;
		}

	}

	// Autoscroll Interface...
	// The following code was borrowed from the book:
	//		Java Swing
	//		By Robert Eckstein, Marc Loy & Dave Wood
	//		Paperback - 1221 pages 1 Ed edition (September 1998) 
	//		O'Reilly & Associates; ISBN: 156592455X 
	//
	// The relevant chapter of which can be found at:
	//		http://www.oreilly.com/catalog/jswing/chapter/dnd.beta.pdf

	private static final int AUTOSCROLL_MARGIN= 12;
	// Ok, we've been told to scroll because the mouse cursor is in our
	// scroll zone.
	public void autoscroll(Point pt) {
		// Figure out which row we're on.
		int nRow= getRowForLocation(pt.x, pt.y);

		// If we are not on a row then ignore this autoscroll request
		if (nRow < 0)
			return;

		Rectangle raOuter= getBounds();
		// Now decide if the row is at the top of the screen or at the
		// bottom. We do this to make the previous row (or the next
		// row) visible as appropriate. If we're at the absolute top or
		// bottom, just return the first or last row respectively.

			nRow=
				(pt.y + raOuter.y <= AUTOSCROLL_MARGIN)
		// Is row at top of screen?
		? (nRow <= 0 ? 0 : nRow - 1) // Yes, scroll up one row
	: (nRow < getRowCount() - 1 ? nRow + 1 : nRow); // No, scroll down one row

		scrollRowToVisible(nRow);
	}
	// Calculate the insets for the *JTREE*, not the viewport
	// the tree is in. This makes it a bit messy.
	public Insets getAutoscrollInsets() {
		Rectangle raOuter= getBounds();
		Rectangle raInner= getParent().getBounds();
		return new Insets(
			raInner.y - raOuter.y + AUTOSCROLL_MARGIN,
			raInner.x - raOuter.x + AUTOSCROLL_MARGIN,
			raOuter.height
				- raInner.height
				- raInner.y
				+ raOuter.y
				+ AUTOSCROLL_MARGIN,
			raOuter.width
				- raInner.width
				- raInner.x
				+ raOuter.x
				+ AUTOSCROLL_MARGIN);
	}
	/*	
		// Use this method if you want to see the boundaries of the
		// autoscroll active region. Toss it out, otherwise.
		public void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			Rectangle raOuter = getBounds();
			Rectangle raInner = getParent().getBounds();
			g.setColor(Color.red);
			g.drawRect(-raOuter.x + 12, -raOuter.y + 12,
				raInner.width - 24, raInner.height - 24);
		}
		
	*/

	// TreeModelListener interface...
	public void treeNodesChanged(TreeModelEvent e) {
		out("treeNodesChanged");
		sayWhat(e);
		// We dont need to reset the selection path, since it has not moved
	}

	public void treeNodesInserted(TreeModelEvent e) {
		out("treeNodesInserted ");
		sayWhat(e);

		// We need to reset the selection path to the node just inserted
		int nChildIndex= e.getChildIndices()[0];
		TreePath pathParent= e.getTreePath();
		setSelectionPath(getChildPath(pathParent, nChildIndex));
	}

	public void treeNodesRemoved(TreeModelEvent e) {
		out("treeNodesRemoved ");
		sayWhat(e);
	}

	public void treeStructureChanged(TreeModelEvent e) {
		out("treeStructureChanged ");
		sayWhat(e);
	}

	// More helpers...
	private TreePath getChildPath(TreePath pathParent, int nChildIndex) {
		TreeModel model= getModel();
		return pathParent.pathByAddingChild(
			model.getChild(pathParent.getLastPathComponent(), nChildIndex));
	}

	boolean isRootPath(TreePath path) {
		return isRootVisible() && getRowForPath(path) == 0;
	}

	private void sayWhat(TreeModelEvent e) {
		out("" + e.getTreePath().getLastPathComponent());
		int[] nIndex= e.getChildIndices();
		for (int i= 0; i < nIndex.length; i++) {
			out(i + ". " + nIndex[i]);
		}
	}

	/**
	 * This class defines a TreeCellRenderer for STree Component based on
	 * STreeCellPanel for rendering<br>
	 * 
	 * @see com.simpledata.uitools.stree.STree
	 * @see com.simpledata.uitools.stree.STreeCellPanel
	 */
	class STreeCellRenderer implements TreeCellRenderer {

		/**		
		 * Fields used are :
		 * panel : STreeCellPanel meant to be shown
		 */
		private STreeCellPanel panel;
		//private STreeCellPanel emptyPanel;
		private boolean useCheck;

		/**
		 * Constructor
		 * @param withCheck : defines if we are or not displaying check capabilities
		 */
		public STreeCellRenderer(boolean withCheck) {
			this.useCheck= withCheck;
			panel= new STreeCellPanel(useCheck);
			//emptyPanel= new STreeCellPanel(false);
		}

		/**
		 * This methods informs the renderer wether we want to display 
		 * checkboxes or not<br>
		 * @param withCheck if true, display with checkBoxes
		 */
		public void setCheckVisible(boolean withCheck) {
			this.useCheck= withCheck;
			panel= new STreeCellPanel(withCheck);
		}

		/* Implementation of TreeCellRenderer interface method
		 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
		 */
		public Component getTreeCellRendererComponent(
			JTree tree,
			Object value,
			boolean selected,
			boolean expanded,
			boolean leaf,
			int row,
			boolean hasFocus) {

			// We first have to extract the exact STreeNode concerned by the getTreeCellRenderer method
			STreeNode stNode= null;

			// Casting value...
			try {
				stNode= (STreeNode) value;
			} catch (ClassCastException cce) {
				m_log.error( "value passed was not a STreeNode", cce );
			}

			if (stNode != null) {
				// Setting the correct display for panel
				panel.adaptOnNode(stNode, expanded);

				// Set correct colors to display
				if (selected) {
					// TODO special management for cell highlighting
					panel.setIconsBackground(
						UIManager.getColor("Tree.textBackground"));
					panel.setTextBackground(
						UIManager.getColor("Tree.selectionBackground"));
					panel.setTextForeground(
						UIManager.getColor("Tree.selectionForeground"));
				} else {
					if (stNode.isHighlighted()) {
					    panel.setIconsBackground(UIManager.getColor("Tree.textBackground"));
					    panel.setTextBackground(STree.selectionBackground);
					    panel.setTextForeground(STree.selectionForeground);
					} else {
						panel.setIconsBackground(
							UIManager.getColor("Tree.textBackground"));
						panel.setTextBackground(
							UIManager.getColor("Tree.textBackground"));
						panel.setTextForeground(
							UIManager.getColor("Tree.textForeground"));
					}
				}

				return this.panel;
			} 
			return (new JLabel("empty"));
		}

	}

	/**
	 * This class is a unified mouse listener for STree<br>
	 * Listening for popup triggering clicks and checkClicks
	 */
	class CheckAndPopupListener extends MouseAdapter {
		private STree tree;

		public CheckAndPopupListener(STree tree) {
			this.tree= tree;
		}

		public void mousePressed(MouseEvent e) {
			manageEvent(e, true);
		}

		public void mouseReleased(MouseEvent e) {
			if (!e.isConsumed()) {
				manageEvent(e, false);
			}
		}

		private void manageEvent(MouseEvent e, boolean pressed) {
			if (e.isPopupTrigger()) {
				// Right-click configuration
				tree.showPopup(e.getComponent(), e.getPoint());
			} else {
				// Normal click
				if (tree.usesCheck() && pressed) {
					tree.testForCheck(e.getPoint());
				}
			}
		}
	}

	/**
	 * Returns CheckBoxes tree state
	 * @return 
	 */
	public boolean usesCheck() {
		return this.usesCheck;
	}

	/**
	 * Analyze a click to see if has been done over a check icon
	 * @param p origin point of the click
	 */
	public void testForCheck(Point p) {
		out("We are clicking the tree at " + p.toString());
		// Examination of surroundings
		TreePath tp= getClosestPathForLocation(p.x, p.y);
		Rectangle rp= getPathBounds(tp);
		out("Closest rectangle : " + rp.toString());

		// First we test if the click is inside the rectangle
		if (((rp.x < p.x) && (p.x < (rp.x + rp.width)))
			&& ((rp.y < p.y) && (p.y < (rp.y + rp.height)))) {
			// We are inside rectangle
			out("inside rectangle");
			STreeNode st= (STreeNode) tp.getLastPathComponent();
			this.cellPanel.adaptOnNode(st, false);
			out("The check is " + cellPanel.getCheckLimit() + " wide");
			if ((p.x < (rp.x + cellPanel.getCheckLimit()))) {
				out("this was understood as a check gesture");
				st.check();
				this.revalidate();
				this.repaint();
			}
		}
	}

	/**
	 * Tells if this point is over the node text or not
	 */
	public boolean isOverText(Point p) {
		boolean res= false;
		// Examination of surroundings
		TreePath tp= getClosestPathForLocation(p.x, p.y);
		Rectangle rp= getPathBounds(tp);
		// First we test if the click is inside the rectangle
		if (((rp.x < p.x) && (p.x < (rp.x + rp.width)))
			&& ((rp.y < p.y) && (p.y < (rp.y + rp.height)))) {
			// We are inside rectangle
			STreeNode st= (STreeNode) tp.getLastPathComponent();
			this.cellPanel.adaptOnNode(st, false);
			if ((p.x >= (rp.x + cellPanel.getTextStartX()))) {
				// Point is over the text zone
				res= true;
			}
		}

		return res;
	}

	/**
	 * Utilities to get the closest STreeNode to this click
	 * @param p origin point of the click
	 */
	public STreeNode getClosestStreeNode(Point p) {
		return (STreeNode) getClosestPathForLocation(p.x, p.y)
			.getLastPathComponent();
	}

	/**
	 * Show a Popup if the point is within a node rectangle, and not over the check if any
	 * @param p point to analyze
	 */
	void showPopup(Component c, Point p) {
		// Examination of surroundings
		TreePath tp= getClosestPathForLocation(p.x, p.y);
		Rectangle rp= getPathBounds(tp);

		// First we test if the click is inside the rectangle
		if (((rp.x < p.x) && (p.x < (rp.x + rp.width)))
			&& ((rp.y < p.y) && (p.y < (rp.y + rp.height)))) {
			// We are inside rectangle
			STreeNode st= (STreeNode) tp.getLastPathComponent();
			this.cellPanel.adaptOnNode(st, false);
			int xOffset= 0;
			if (this.usesCheck) {
				xOffset= cellPanel.getCheckLimit() + 4;
			}
			if ((p.x > (rp.x + xOffset))) {
				jPopupMenu= st.getPopupMenu();
				if (jPopupMenu != null) {
					setSelectionPath(tp);
					//m_log.debug( "ZZZ"+c.isShowing()+"   c:"+c );
					jPopupMenu.show(c, p.x, p.y);
				}
			}
		}
	}

	/**
	 * Static method used by STreeNodes to generate a Simple JPopupMenu
	 * based upon a list of actions with their title
	 * @param source Calling STreeNode
	 * @param actionNames the titles which are meant to be displayed in the JMenus of the JPopupMenu
	 * @param actionKeys the keys for method call : doAction(String actionKey) in STreeNode
	 * @return
	 */
	public static JPopupMenu getPopup(
		STreeNode source,
		String[] actionNames,
		String[] actionKeys) {
		final STreeNode _source= source;
		JPopupMenu res= new JPopupMenu();
		int namesSize= actionNames.length;
		int keysSize= actionKeys.length;
		if ((namesSize > 0) && (namesSize == keysSize) && (source != null)) {
			// Conditions to avoid exceptions are met ;)
			for (int i= 0; i < namesSize; i++) {
				final String key= actionKeys[i];
				JMenuItem jmi= new JMenuItem(actionNames[i]);
				jmi.addActionListener(new ActionListener() {
					public void actionPerformed(
						java.awt.event.ActionEvent evt) {
						_source.doAction(key);
					}
				});
				res.add(jmi);
			}
		} else {
			m_log.error(
				"Source node was null, or actionNames and actionKeys have size coherence problems"
			);
		}
		return res;
	}

	private ArrayList getExpandedNodes() {
	    ArrayList exp = new ArrayList();
	    getExpandedNodes(exp, this.root);
	    return exp;
	}

	
	private boolean getExpandedNodes(ArrayList curExpanded, STreeNode curNode) {
	    boolean foundExpandedChildren= false;
	    
		for (int i=0; i<curNode.getChildCount(); i++) {
		    STreeNode curChild= curNode.getChildAt(i);
		    if (!curChild.isLeaf()) {
				if (this.isExpanded(curChild)) {
					if (getExpandedNodes(curExpanded, curChild)) {
						foundExpandedChildren= true;
					}
				}
			}
		}
		
		if (!foundExpandedChildren) {
			// Last expanded of his path
			// We add him
			curExpanded.add(curNode);
		}
	    
	    return foundExpandedChildren;
	}
	
	private void openOldExpanded(ArrayList nodes) {
	    if (nodes != null) {
	        for (Iterator i = nodes.iterator(); i.hasNext();) {
	            this.expandNode((STreeNode) i.next());
	        }
	    }
	}
	
	/**
	 * Informs the tree that its structure has changed under stn
	 * @param stn node under which structure has changed
	 */
	public void fireTreeStructureChanged(STreeNode stn) {
		TreePath tp= this.getPath(stn);
		if (tp != null)
			this.treeModel.fireTreeStructureChanged(
				new TreeModelEvent(this, tp));
	}

	public void fireTreeNodesChanged(STreeNode stn) {
		TreePath tp= this.getPath(stn);
		if (tp != null)
			this.treeModel.fireTreeNodesChanged(new TreeModelEvent(this, tp));
	}

	/**
	 * Tells the tree to refresh its view<br>
	 * Use it for example when node icons have changed
	 */
	public void fireRefresh() {
		this.revalidate();
		this.repaint();
	}

	/**
	 * Returns bounds of node stn within STree<br>
	 * Rectangle includes origin of the rectangle, height and width<br>
	 * If returned value is null, this node was not in this tree
	 * @param stn node which bounds are requested
	 * @return rectangle representing node bounds
	 */
	public Rectangle getNodeBounds(STreeNode stn) {
		Rectangle res= null;
		TreePath tp= getPath(stn);
		if (tp != null)
			res= this.getPathBounds(tp);
		return res;
	}

	/**
	 * Changes the tree root node<br>
	 * ie. change the tree
	 * @param stn
	 */
	public void changeRoot(STreeNode stn) {
		this.root= stn;
		this.setModel(null);
		this.treeModel= new STreeModel(stn);
		this.setModel(treeModel);
		if (root != null)
			this.fireTreeStructureChanged(this.root);
		this.fireRefresh();
	}

	/**
	 * Indicates if a node is expanded in tree
	 * @param stn the node which state we want to know 
	 */
	public boolean isExpanded(STreeNode stn) {
		boolean res= false;
		TreePath tp= this.getPath(stn);
		if (tp != null) {
			res= this.isExpanded(tp);
		}
		return res;
	}

	/**
	 * Return every expanded descendant which does not have expanded children
	 * @param stn
	 * @return
	 */
	public ArrayList getExpandedChildren(STreeNode stn) {
	    ArrayList res = new ArrayList();
	    if (isExpanded(stn)) {
			getExpandedChildren(stn, res);
		}
	    return res;
	}
	
	private void getExpandedChildren(STreeNode stn, ArrayList al) {
	    boolean hasExpandedChild = false;
	    for (int i=0;i<stn.getChildCount(); i++) {
	        STreeNode child = stn.getChildAt(i);
	        if (isExpanded(child)) {
				getExpandedChildren(child, al);
				hasExpandedChild = true;
			}
	    }
	    if (!hasExpandedChild) {
			al.add(stn);
		}
	}
	
	/**
	 * Tell the tree to expand itself to show node
	 * @param stn node we wish to expand
	 */
	public void expandNode(STreeNode stn) {
		if (stn == null) {
			m_log.error( "Tried to expand a null STreeNode" );
			return;
		}
		TreePath tp= null;
		if (stn.isLeaf()) {
			STreeNode dad= stn.getParent();
			if (dad != null) {
				tp= this.getPath(dad);
			}
		} else {
			tp= this.getPath(stn);
		}
		this.expandPath(tp);
	}

	/**
	 * Tells the tree to collapse at specified node
	 * @param stn
	 */
	public void collapseNode(STreeNode stn) {
		if (stn == null) {
			m_log.error( "Tried to collpase a null STreeNode" );
			return;
		}

		if (stn.isLeaf())
			return;

		TreePath tp= null;
		tp= this.getPath(stn);
		if (tp != null) {
			this.collapsePath(tp);
		}
	}

	/**
	 * Collapse all nodes in tree
	 */
	public void collapseAll() {
		this.collapseFrom(this.root);
		// Displays first list of children
		this.expandNode(this.root);
	}
	
	/**
	 * Internal method used by collapseAll
	 * @param stn node from which we whish to collapse the tree
	 */
	private void collapseFrom(STreeNode stn) {
	    for (int i=0;i<stn.getChildCount();i++) {
	        STreeNode curStn = stn.getChildAt(i);
	        this.collapseFrom(curStn);
	    }
	    this.collapseNode(stn);
	}
	
	/**
	 * Expand all nodes in tree
	 */
	public void expandAll() {
		boolean oldState = this.getScrollsOnExpand();
		this.setScrollsOnExpand(false);
		if (root != null)
			this.expandFromPath(new TreePath(this.root));
		this.setScrollsOnExpand(oldState);
	}

	/**
	 * Internal method used by expandAll
	 * @param tp TreePath from which we whish to expand the tree
	 */
	private void expandFromPath(TreePath tp) {
		STreeNode stn = (STreeNode)tp.getLastPathComponent();
		if (!stn.isLeaf()) {
			this.expandPath(tp);
			for (int i=0;i<stn.getChildCount();i++) {
			    TreePath nextPath = tp.pathByAddingChild(stn.getChildAt(i));
			    expandFromPath(nextPath);
			}
		}
	}

	/**
	 * Overrides JComponent's getToolTipText  method in order to allow 
	 * renderer's tips to be used if it has text set.
	 */
	public String getToolTipText(MouseEvent evt) {
		TreePath tp = this.getClosestPathForLocation(evt.getX(), evt.getY());
		Rectangle rp = this.getPathBounds(tp);
		
		if (!rp.contains(evt.getX(), evt.getY())) return null; 
		
		STreeNode stn = (STreeNode)tp.getLastPathComponent();
		
		return stn.getToolTipText();
	}
	
	/**
	 * Tells this tree that it should show ToolTips
	 */
	public void enableToolTips() {
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	/**
	 * @return the actual RootNode
	 */
	public STreeNode getRoot() {
		return root;
	}

	/**
	 * Sets the colors for highlighted nodes display
	 * @param bg background color for highlight
	 * @param fg foreground color for highlight
	 */
	public void setHighLightColors(Color bg, Color fg) {
	    STree.selectionBackground = bg;
	    STree.selectionForeground = fg;
	}
	
}

/*
 * $Log: STree.java,v $
 * Revision 1.2  2007/04/02 17:04:27  perki
 * changed copyright dates
 *
 * Revision 1.1  2006/12/03 12:48:42  perki
 * First commit on sourceforge
 *
 * Revision 1.59  2004/11/15 16:10:41  carlito
 * ZZZ true removed ....
 *
 * Revision 1.58  2004/10/18 17:15:15  carlito
 * Stree enabling drag over root node when show root handle false
 *
 * Revision 1.57  2004/09/14 10:17:07  carlito
 * FileBrowser updated for macs
 *
 * Revision 1.56  2004/09/04 18:10:15  kaspar
 * ! Log.out -> log4j, last part
 *
 * Revision 1.55  2004/07/19 12:13:47  perki
 * Merging finished?
 *
 * Revision 1.54  2004/07/12 17:18:34  carlito
 * Some cleaning in trees
 *
 * Revision 1.53  2004/07/12 09:38:27  carlito
 * STree passed to ArrayList
 *
 * Revision 1.52  2004/07/09 20:53:38  perki
 * Merging UI step 1.5
 *
 * Revision 1.51  2004/06/16 10:12:28  carlito
 * *** empty log message ***
 *
 * Revision 1.50  2004/04/12 16:10:13  carlito
 * *** empty log message ***
 *
 * Revision 1.49  2004/04/09 07:16:37  perki
 * Lot of cleaning
 *
 * Revision 1.48  2004/04/01 15:50:12  perki
 * *** empty log message ***
 *
 * Revision 1.47  2004/03/17 15:54:11  carlito
 * *** empty log message ***
 *
 * Revision 1.46  2004/03/08 09:02:46  perki
 * houba houba hop
 *
 * Revision 1.45  2004/03/03 18:46:20  carlito
 * *** empty log message ***
 *
 * Revision 1.44  2004/02/25 15:36:13  carlito
 * *** empty log message ***
 *
 * Revision 1.43  2004/02/25 15:14:25  carlito
 * *** empty log message ***
 *
 * Revision 1.42  2004/02/25 13:22:20  carlito
 * *** empty log message ***
 *
 * Revision 1.41  2004/02/25 10:35:03  carlito
 * *** empty log message ***
 *
 * Revision 1.40  2004/02/25 09:54:33  perki
 * *** empty log message ***
 *
 * Revision 1.39  2004/02/24 10:12:32  carlito
 * *** empty log message ***
 *
 * Revision 1.38  2004/02/14 21:53:51  carlito
 * *** empty log message ***
 *
 * Revision 1.37  2004/02/05 14:05:51  carlito
 * STree now can be set with or without check live... getPreferredSize problem solved for STreeCellPanel
 *
 * Revision 1.36  2004/02/05 10:02:10  carlito
 * Expand node, expands leaf, catch null STreeNodes
 *
 * Revision 1.35  2004/02/04 17:12:52  carlito
 * expandNode in STree now show nodes if they are leaf
 *
 * Revision 1.34  2004/02/04 12:05:08  carlito
 * correction nullPointer dans STree
 *
 * Revision 1.33  2004/02/02 14:43:40  carlito
 * Expand capabilities added to STree
 *
 * Revision 1.32  2004/02/02 12:29:25  carlito
 * removed unused fireTree...Events
 *
 * Revision 1.31  2004/02/02 12:08:36  carlito
 * fire methods upgrade
 *
 * Revision 1.30  2004/02/02 11:20:22  carlito
 * removed TreeCellEditor
 *
 * Revision 1.29  2004/01/31 09:31:59  perki
 * Changed Color selction Text
 *
 * Revision 1.28  2004/01/31 08:10:38  perki
 * Added Editors
 *
 * Revision 1.27  2004/01/30 18:56:41  carlito
 * *** empty log message ***
 *
 * Revision 1.26  2004/01/30 15:22:30  carlito
 * Popups and checks functional
 *
 * Revision 1.25  2004/01/29 20:08:24  carlito
 * CheckBox fonctionnent
 *
 * Revision 1.24  2004/01/29 14:31:15  carlito
 * New renderer for STree with checkIcons
 *
 * Revision 1.23  2004/01/29 13:03:05  carlito
 * warnings and imports corrected ...
 *
 * Revision 1.22  2004/01/23 16:30:05  carlito
 * *** empty log message ***
 *
 * Revision 1.21  2004/01/23 15:03:34  carlito
 * *** empty log message ***
 *
 * Revision 1.20  2004/01/23 13:39:27  carlito
 * *** empty log message ***
 *
 * Revision 1.19  2004/01/23 13:33:25  carlito
 * Gere les node null pour getPath
 *
 * Revision 1.18  2004/01/22 19:31:34  carlito
 * normalement le bug de l'espace est repare
 *
 * Revision 1.17  2004/01/22 18:12:04  carlito
 * *** empty log message ***
 *
 * Revision 1.16  2004/01/22 18:10:40  carlito
 * *** empty log message ***
 *
 * Revision 1.15  2004/01/22 17:34:00  carlito
 * *** empty log message ***
 *
 * Revision 1.14  2004/01/21 10:54:48  perki
 * *** empty log message ***
 *
 * Revision 1.13  2004/01/20 23:20:17  carlito
 * red background for prohibition
 *
 * Revision 1.12  2004/01/20 15:59:57  carlito
 * multiple select v2
 *
 * Revision 1.11  2004/01/20 15:32:30  carlito
 * Added multiple selection
 *
 * Revision 1.10  2004/01/20 14:24:20  carlito
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/20 10:45:05  carlito
 * *** empty log message ***
 *
 * Revision 1.8  2004/01/19 18:10:18  carlito
 * *** empty log message ***
 *
 * Revision 1.7  2004/01/19 18:01:49  carlito
 * Stree has better drop detection
 * new test image for windows forbidden1_16x16.gif
 *
 * Revision 1.6  2004/01/17 06:04:30  carlito
 * *** empty log message ***
 *
 * Revision 1.5  2004/01/13 16:03:31  carlito
 * *** empty log message ***
 *
 * Revision 1.4  2004/01/10 08:12:01  perki
 * Internal Frames
 *
 * Revision 1.3  2004/01/09 17:51:15  carlito
 * *** empty log message ***
 *
 * Revision 1.2  2004/01/09 16:51:02  carlito
 * *** empty log message ***
 *
 * Revision 1.1  2004/01/09 14:53:59  carlito
 * *** empty log message ***
 *
 *
 */
