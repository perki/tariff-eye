/* Copyright (c) 2003 to 2007 SimpleData Sarl http://simpledata.ch  
 * All rights reserved.
 *
 * This file is part of TariffEye Software  realeased under the 
 * GNU Public License. Redistributions of source code and binaries 
 * must retain the above copyright notice; see COPYING_Tariff-Eye.txt 
 *
 */
package com.simpledata.uitools;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.*;

/**
* Jtree that implements multiple selections using checkboxes.<BR>
* CheckNodeTree extends JScrollPane to be directly used a compenent un your UI<BR>
* Use CheckNode to construct a tree<BR>
* <PRE>
* Example
		class CNI implements CheckNodeTreeInterface {
			
			public void checkNodeSelected (CheckNode cn) { }
			public void checkNodeMenuEvent (String button, CheckNode cn) { }
			public boolean checkNodeMoved (CheckNode source, CheckNode destination) {
				return true;
			}
		}
		
		String[] strs = {"administrator", "manager", "vice manager", "staff", "client"}; 
		boolean[] child = {true,true,false,false,true}; 
		CheckNode[] nodes = new CheckNode[strs.length];
		for (int i=0;i<strs.length;i++) {
			nodes[i] = new CheckNode(strs[i],child[i]);
		}
		nodes[0].add(nodes[1]);
		nodes[1].add(nodes[2]);
		nodes[1].add(nodes[3]);
		nodes[0].add(nodes[4]);
		nodes[3].setSelected(true); 
		CheckNodeTree cnt = new CheckNodeTree(new CNI(), nodes, CheckNodeTree.MULTIPLE_PROPAGATE,true,strs);
		
		JFrame jf = new JFrame();
		jf.getContentPane().add(cnt);
		jf.show();
* </PRE>
* @see CheckNode
*/
public class CheckNodeTree extends JPanel {
	private CheckNode[] nodes ;
	private DnDJTree tree;
	JPopupMenu jPopupMenu;
	private JMenu jMenu;
	
	public final static int SINGLE_SELECTION = 0;
	public final static int MULTIPLE_PROPAGATE = 1;
	private int selectionMode;
	private CheckNodeTreeInterface myIF;
	private CheckNode selectedNode;
	
	public CheckNodeTree(final CheckNodeTreeInterface myIF, int selectionMode, boolean allowsDnD, String[] menuOptions) {
		this.selectionMode = selectionMode;
		this.myIF = myIF;
		
		tree = new DnDJTree(allowsDnD,myIF);
		tree.addMouseListener(new NodeSelectionListener());
		
		JScrollPane js = new JScrollPane();
		js.setViewportView(tree);
		setLayout(new java.awt.BorderLayout());
		
		
		// tools : Popup and Menu
		if (menuOptions != null)
		if (menuOptions.length > 0) {
			tree.addMouseListener(new PopupListener(jPopupMenu));
			jPopupMenu = new JPopupMenu();
			jMenu = new JMenu(menuOptions[0],false);
			for (int i = 1; i < menuOptions.length; i++) {
				final String str = menuOptions[i];
				JMenuItem jm2 = jMenu.add(menuOptions[i]);
				jm2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						myIF.checkNodeMenuEvent(str,getSelectedNode());
					}
				});
				
				JMenuItem jm = new JMenuItem(menuOptions[i]);
				jPopupMenu.add(jm);
				
				jm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						myIF.checkNodeMenuEvent(str,getSelectedNode());
					}
					
				});
			}
			
			JMenuBar jb = new JMenuBar();
			jb.add(jMenu);
			add(jb,BorderLayout.NORTH);
		}
		
		add(js,BorderLayout.CENTER);
	}
	
	/*
	* Takes an array of CheckNode describing the Tree<BR>
	* @param selectionMode is one of : SINGLE_SELECTION, MULTIPLE_PROPAGATE
	* @param allowsDnD allow drag and drop. Move Listener should be implemented.
	* @param menuOptions is an array of Strings that will proposed as options. menuOptions[0] is the title for the menu.
	*/
	public CheckNodeTree(CheckNodeTreeInterface myIF, CheckNode[] nodes, int selectionMode, 
	boolean allowsDnD, String[] menuOptions) {
		this(myIF, selectionMode,allowsDnD,menuOptions);
		setNodeData(nodes);
	}
	
	public void refresh() { tree.refresh(); }
	
	
	/**
	* use this to change the tree data
	*/
	public void setNodeData(CheckNode[] nodes) {
		if (nodes == null) return;
		if (nodes.length == 0) return;
		this.nodes = nodes;
		for (int i=0;i<nodes.length;i++) {
			nodes[i].setSelectionMode(selectionMode);
		}

		tree.setModel(new DefaultTreeModel(nodes[0]));
		
		tree.setCellRenderer(new CheckRenderer(selectionMode));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.putClientProperty("JTree.lineStyle", "Angled");

	}

	public static void main (String args[]) {
		class CNI implements CheckNodeTreeInterface {
			
			public void checkNodeSelected (CheckNode cn) { }
			public void checkNodeMenuEvent (String button, CheckNode cn) { }
			public boolean checkNodeMoved (CheckNode source, CheckNode destination) {
				return true;
			}
		}
		
		String[] strs = {"administrator", "manager", "vice manager", "staff", "client"}; 
		boolean[] child = {true,true,false,false,true}; 
		CheckNode[] nodes = new CheckNode[strs.length];
		for (int i=0;i<strs.length;i++) {
			nodes[i] = new CheckNode(strs[i],child[i]);
		}
		nodes[0].add(nodes[1]);
		nodes[1].add(nodes[2]);
		nodes[1].add(nodes[3]);
		nodes[0].add(nodes[4]);
		nodes[3].setSelected(true); 
		CheckNodeTree cnt = new CheckNodeTree(new CNI(), nodes, CheckNodeTree.MULTIPLE_PROPAGATE,true,strs);
		
		JFrame jf = new JFrame();
		jf.getContentPane().add(cnt);
		jf.show();
	}
	
	
	/** MouseListener for popupMenus */
	class PopupListener extends MouseAdapter {
		JPopupMenu popup;
		
		PopupListener(JPopupMenu popupMenu) { popup = popupMenu; }
		
		public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
		
		public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
		
		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				selectElementAt(e.getX(),e.getY(),false);
				jPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	
	class NodeSelectionListener extends MouseAdapter {
		
		public void mouseClicked(MouseEvent e) {
			selectElementAt(e.getX(),e.getY(),true);
		}
	}
	
	void selectElementAt(int x,int y, boolean doICheckIt) {
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		
		if (path != null) {
			CheckNode node = (CheckNode) path.getLastPathComponent();
			selectedNode = node;
			// deslect all nodes 
			for (int i=0;i<nodes.length;i++) 
				if (nodes[i] != node)
					nodes[i].setSelected(false);	
			
			node.setSelected(true,doICheckIt);
			
			((DefaultTreeModel)tree.getModel()).nodeChanged(node);
			if (row == 0) {
				tree.revalidate();
				tree.repaint();
			}
			myIF.checkNodeSelected(node);
			tree.setSelectionPath(path);
		}
	}
	
	/**
	* Return a Vector containing selected User Objects (no folders)
	*/
	public Vector getMultipleSelectedObjects() {
		Vector v = new Vector();
		for (int i=1;i<nodes.length;i++) {
			if (nodes[i].isChecked() && nodes[i].isObject()) {
				v.add(nodes[i].getUserObject());
			}
		}
		return v ;
	}
	
	/**
	* Return a Vector containing selected Nodes<BR>
	* Nodes could be Folders and Objects.
	*/
	public Vector getMultipleSelected() {
		Vector v = new Vector();
		for (int i=1;i<nodes.length;i++) {
			if (nodes[i].isChecked()) {
				v.add(nodes[i]);
			}
		}
		return v ;
	}
	
	/**
	* Return the last selected Node 
	*/
	public CheckNode getSelectedNode() {
		return selectedNode ;
	}
	
	/**
	* Return the last selected UserObject (no folders)
	*/
	public Object getSelectedObject() {
		if (getSelectedNode() == null) return null;
		if (! getSelectedNode().isObject()) return null;
		return getSelectedNode().getUserObject();
	}
}

	
class CheckRenderer extends JPanel implements TreeCellRenderer {
	protected JCheckBox check;
	protected TreeLabel label;
	protected int selectionMode;
	
	public CheckRenderer(int selectionMode) {
		this.selectionMode = selectionMode;
		setLayout(null);
		check = new JCheckBox();
		
	
		
		if (selectionMode == CheckNodeTree.MULTIPLE_PROPAGATE) add(check);
		add(label = new TreeLabel());
		check.setBackground(UIManager.getColor("Tree.textBackground"));
		label.setForeground(UIManager.getColor("Tree.textForeground"));
	}
	
	public Component getTreeCellRendererComponent(JTree tree, Object value,
	boolean isSelected, boolean expanded,
	boolean leaf, int row, boolean hasFocus) 
	{
		//String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
		CheckNode cn = (CheckNode) value;
		setEnabled(tree.isEnabled());
		check.setSelected(cn.isChecked());
		label.setFont(tree.getFont());
		label.setText(cn.toString());
		label.setSelected(isSelected);
		label.setFocus(hasFocus);
		label.setIcon(cn.getIcon(expanded));
		return this;
	}
	
	public Dimension getPreferredSize() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		if (selectionMode == CheckNodeTree.SINGLE_SELECTION) return d_label;
		return new Dimension(d_check.width + d_label.width,
		(d_check.height < d_label.height ?
		d_label.height : d_check.height));
		
	}
	
	public void doLayout() {
		Dimension d_check = check.getPreferredSize();
		Dimension d_label = label.getPreferredSize();
		int y_check = 0;
		int y_label = 0;
		if (d_check.height < d_label.height) {
			y_check = (d_label.height - d_check.height)/2;
		} else {
			y_label = (d_check.height - d_label.height)/2;
		}
		if (selectionMode == CheckNodeTree.SINGLE_SELECTION) 
		{
			label.setLocation(0,y_label);
			label.setBounds(0,y_label,d_label.width,d_label.height);
		} else {
			check.setLocation(0,y_check);
			check.setBounds(0,y_check,d_check.width,d_check.height);
			label.setLocation(d_check.width,y_label);
			label.setBounds(d_check.width,y_label,d_label.width,d_label.height);
		}
	}
	
	public void setBackground(Color color) {
		if (color instanceof ColorUIResource)
			color = null;
		super.setBackground(color);
	}
	
	public class TreeLabel extends JLabel {
		boolean isSelected;
		boolean hasFocus;
		
		public TreeLabel() {
		}
		
		public void setBackground(Color color) {
			if(color instanceof ColorUIResource)
				color = null;
			super.setBackground(color);
		}
		
		public void paint(Graphics g) {
			String str;
			if ((str = getText()) != null) {
				if (0 < str.length()) {
					if (isSelected) {
						g.setColor(UIManager.getColor("Tree.selectionBackground"));
					} else {
						g.setColor(UIManager.getColor("Tree.textBackground"));
					}
					Dimension d = getPreferredSize();
					int imageOffset = 0;
					Icon currentI = getIcon();
					if (currentI != null) {
						imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
					}
					g.fillRect(imageOffset, 0, d.width -1 - imageOffset, d.height);
					if (hasFocus) {
						g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
						g.drawRect(imageOffset, 0, d.width -1 - imageOffset, d.height -1);
					}
				}
			}
			super.paint(g);
		}
		
		public Dimension getPreferredSize() {
			Dimension retDimension = super.getPreferredSize();
			if (retDimension != null) {
				retDimension = new Dimension(retDimension.width + 3,
				retDimension.height);
			}
			return retDimension;
		}
		
		public void setSelected(boolean isSelected) {
			this.isSelected = isSelected;
		}
		
		public void setFocus(boolean hasFocus) {
			this.hasFocus = hasFocus;
		}
	}
}

class DnDJTree extends JTree
implements TreeSelectionListener, 
DragGestureListener, DropTargetListener,
DragSourceListener {
	
	
	
	/** Stores the selected node info */
	protected TreePath SelectedTreePath = null;
	protected CheckNode SelectedNode = null;
	
	/** Variables needed for DnD */
	private DragSource dragSource = null;
	
	private DragSourceContext dsc = null;
	private CheckNodeTreeInterface myIF;
	//private boolean allowsDnD ;
	
	/** Constructor 
	@param allowsDnd allow or not Drag and Drop
	*/
	public DnDJTree(  boolean allowsDnd, CheckNodeTreeInterface myIF) {
		super();
		//this.allowsDnD = allowsDnd;
		this.myIF = myIF;
		
		if (allowsDnd) {
			addTreeSelectionListener(this);
			
			
			dragSource = DragSource.getDefaultDragSource() ;
			
			DragGestureRecognizer dgr = 
			dragSource.createDefaultDragGestureRecognizer(
			this,                             //DragSource
			DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
			this                              //DragGestureListener
			);
			
			
			/* Eliminates right mouse clicks as valid actions - useful especially
			* if you implement a JPopupMenu for the JTree
			*/
			dgr.setSourceActions(dgr.getSourceActions() & ~InputEvent.BUTTON3_MASK);
			
			/* First argument:  Component to associate the target with
			* Second argument: DropTargetListener 
			*/
			new DropTarget(this, this);
		}
		
		//unnecessary, but gives FileManager look
		putClientProperty("JTree.lineStyle", "Angled");
	}
	
	/** Returns The selected node */
	public CheckNode getSelectedNode() {
		return SelectedNode;
	}
	
	///////////////////////// Interface stuff ////////////////////
	
	
	/** DragGestureListener interface method */
	public void dragGestureRecognized(DragGestureEvent e) {
		//Get the selected node
		CheckNode dragNode = getSelectedNode();
		if (dragNode != null) {
			//Get the Transferable Object
			Transferable transferable = (Transferable) dragNode;
			/* ********************** CHANGED ********************** */
			
			//Select the appropriate cursor;
			Cursor cursor = DragSource.DefaultCopyNoDrop;
			int action = e.getDragAction();
			if (action == DnDConstants.ACTION_MOVE) {
				cursor = DragSource.DefaultMoveNoDrop;
			}
			
			dragSource.startDrag(e, cursor, transferable, this);
		}
	}
	
	/** DragSourceListener interface method */
	public void dragDropEnd(DragSourceDropEvent dsde) {
	}
	
	/** DragSourceListener interface method */
	public void dragEnter(DragSourceDragEvent dsde) { }
	
	
	/** DragSourceListener interface method */
	public void dropActionChanged(DragSourceDragEvent dsde) { }
	
	/** DragSourceListener interface method */
	public void dragExit(DragSourceEvent dsde) { }
	
	
	/** DropTargetListener interface method - What we do when drag is released */
	public void drop(DropTargetDropEvent e) {
		
		//get new parent node
		Point loc = e.getLocation();
		TreePath destinationPath = getPathForLocation(loc.x, loc.y);
		
		final String msg = testDropTarget(destinationPath, SelectedTreePath);
		if (msg != null) {
			e.rejectDrop();
		} else {
			if (! myIF.checkNodeMoved( 
				(CheckNode) SelectedTreePath.getLastPathComponent(),
			(CheckNode) destinationPath.getLastPathComponent()))
			e.rejectDrop();
		}  
		
		CheckNode newParent =
		(CheckNode) destinationPath.getLastPathComponent();
		
		//get old parent node
		CheckNode oldParent = (CheckNode) getSelectedNode().getParent();
		
		int action = e.getDropAction();
		boolean copyAction = (action == DnDConstants.ACTION_COPY);
		
		//make new child node
		CheckNode newChild = getSelectedNode();
		
		try { 
			if (!copyAction) oldParent.remove(getSelectedNode());
			newParent.add(newChild);
			
			if (copyAction) e.acceptDrop (DnDConstants.ACTION_COPY);
			else e.acceptDrop (DnDConstants.ACTION_MOVE);
		}
		catch (java.lang.IllegalStateException ils) {
			e.rejectDrop();
		}
		
		newChild.autoSelectMyParent();
		e.getDropTargetContext().dropComplete(true);
		
		//expand nodes appropriately - this probably isnt the best way...
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.reload(oldParent);
		model.reload(newParent);
		TreePath parentPath = new TreePath(newParent.getPath());
		expandPath(parentPath);
		
	} //end of method
	
	
	/** Refresh Tree **/
	public void refresh () {
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.reload();
	}
	
	/** DropTaregetListener interface method */
	public void dragEnter(DropTargetDragEvent e) {
	}
	
	/** DropTaregetListener interface method */
	public void dragExit(DropTargetEvent e) { 
	}
	
	/** DragSourceListener interface method */
	public void dragOver(DragSourceDragEvent e) { 
		dsc = e.getDragSourceContext();
		
	}
	
	
	/** DropTaregetListener interface method */
	public void dragOver(DropTargetDragEvent e) {
		Point cursorLocationBis = e.getLocation();
		TreePath destinationPath = getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);
		
		// if destination path is okay accept drop...
		if (testDropTarget(destinationPath, SelectedTreePath) == null){
			e.acceptDrag(DnDConstants.ACTION_MOVE ) ;
			if (dsc != null)  dsc.setCursor(DragSource.DefaultMoveDrop); 
		} else {
			e.rejectDrag() ; 
			if (dsc != null)  dsc.setCursor(DragSource.DefaultMoveNoDrop); 
		}
	}
	
	/** DropTaregetListener interface method */
	public void dropActionChanged(DropTargetDragEvent e) {
	}
	
	
	/** TreeSelectionListener - sets selected node */
	public void valueChanged(TreeSelectionEvent evt) {
		SelectedTreePath = evt.getNewLeadSelectionPath();
		if (SelectedTreePath == null) {
			
			SelectedNode = null;
			return;
		}
		CheckNode cn = (CheckNode)SelectedTreePath.getLastPathComponent();
		if (cn.isRoot()) {
			setSelectionPath(null);
			SelectedNode = null;
			return;
		}
		SelectedNode = (CheckNode)SelectedTreePath.getLastPathComponent();
	}
	
	/** Convenience method to test whether drop location is valid
	@param destination The destination path 
	@param dropper The path for the node to be dropped
	@return null if no problems, otherwise an explanation
	*/
	private String testDropTarget(TreePath destination, TreePath dropper) {
		if (destination == null) return "no destination";
		if (dropper == null) return "no dropper";
		CheckNode dest = (CheckNode) destination.getLastPathComponent();
		CheckNode node = (CheckNode) dropper.getLastPathComponent();
		if (dest == node) return "no same";
		if (node.isNodeDescendant(dest)) return "no parent";
		if (dest.getAllowsChildren()) return null ;
		return "nope";
	}
	
	
	
} //end of DnDJTree

