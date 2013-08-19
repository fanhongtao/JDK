/*
 * @(#)BasicTreeUI.java	1.156 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.beans.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.TooManyListenersException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.*;
import javax.swing.text.Position;

/**
 * The basic L&F for a hierarchical data structure.
 * <p>
 *
 * @version 1.156 01/23/03
 * @author Scott Violet
 */

public class BasicTreeUI extends TreeUI
{
    static private final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    transient protected Icon        collapsedIcon;
    transient protected Icon        expandedIcon;

    /**
      * Color used to draw hash marks.  If <code>null</code> no hash marks
      * will be drawn.
      */
    private Color hashColor;

    /** Distance between left margin and where vertical dashes will be
      * drawn. */
    protected int               leftChildIndent;
    /** Distance to add to leftChildIndent to determine where cell
      * contents will be drawn. */
    protected int               rightChildIndent;
    /** Total distance that will be indented.  The sum of leftChildIndent
      * and rightChildIndent. */
    protected int               totalChildIndent;

    /** Minimum preferred size. */
    protected Dimension         preferredMinSize;

    /** Index of the row that was last selected. */
    protected int               lastSelectedRow;

    /** Component that we're going to be drawing into. */
    protected JTree             tree;

    /** Renderer that is being used to do the actual cell drawing. */
    transient protected TreeCellRenderer   currentCellRenderer;

    /** Set to true if the renderer that is currently in the tree was
     * created by this instance. */
    protected boolean           createdRenderer;

    /** Editor for the tree. */
    transient protected TreeCellEditor     cellEditor;

    /** Set to true if editor that is currently in the tree was
     * created by this instance. */
    protected boolean           createdCellEditor;

    /** Set to false when editing and shouldSelectCell() returns true meaning
      * the node should be selected before editing, used in completeEditing. */
    protected boolean           stopEditingInCompleteEditing;

    /** Used to paint the TreeCellRenderer. */
    protected CellRendererPane  rendererPane;

    /** Size needed to completely display all the nodes. */
    protected Dimension         preferredSize;

    /** Is the preferredSize valid? */
    protected boolean           validCachedPreferredSize;

    /** Object responsible for handling sizing and expanded issues. */
    protected AbstractLayoutCache  treeState;


    /** Used for minimizing the drawing of vertical lines. */
    protected Hashtable         drawingCache;

    /** True if doing optimizations for a largeModel. Subclasses that
     * don't support this may wish to override createLayoutCache to not
     * return a FixedHeightLayoutCache instance. */
    protected boolean           largeModel;

    /** Reponsible for telling the TreeState the size needed for a node. */
    protected AbstractLayoutCache.NodeDimensions     nodeDimensions;

    /** Used to determine what to display. */
    protected TreeModel         treeModel;

    /** Model maintaing the selection. */
    protected TreeSelectionModel treeSelectionModel;

    /** How much the depth should be offset to properly calculate
     * x locations. This is based on whether or not the root is visible,
     * and if the root handles are visible. */
    protected int               depthOffset;

    /** Last width the tree was at when painted. This is used when
     * !leftToRigth to notice the bounds have changed so that we can instruct
     * the TreeState to relayout. */
    private int                 lastWidth;

    // Following 4 ivars are only valid when editing.

    /** When editing, this will be the Component that is doing the actual
      * editing. */
    protected Component         editingComponent;

    /** Path that is being edited. */
    protected TreePath          editingPath;

    /** Row that is being edited. Should only be referenced if
     * editingComponent is not null. */
    protected int               editingRow;

    /** Set to true if the editor has a different size than the renderer. */
    protected boolean           editorHasDifferentSize;

    /** Row correspondin to lead path. */
    private int                 leadRow;
    /** If true, the property change event for LEAD_SELECTION_PATH_PROPERTY,
     * or ANCHOR_SELECTION_PATH_PROPERTY will not generate a repaint. */
    private boolean             ignoreLAChange;

    /** Indicates the orientation. */
    private boolean             leftToRight;

    // Cached listeners
    private PropertyChangeListener propertyChangeListener;
    private PropertyChangeListener selectionModelPropertyChangeListener;
    private MouseListener mouseListener;
    private FocusListener focusListener;
    private KeyListener keyListener;
    /** Used for large models, listens for moved/resized events and
     * updates the validCachedPreferredSize bit accordingly. */
    private ComponentListener   componentListener;
    /** Listens for CellEditor events. */
    private CellEditorListener  cellEditorListener;
    /** Updates the display when the selection changes. */
    private TreeSelectionListener treeSelectionListener;
    /** Is responsible for updating the display based on model events. */
    private TreeModelListener treeModelListener;
    /** Updates the treestate as the nodes expand. */
    private TreeExpansionListener treeExpansionListener;

    public static ComponentUI createUI(JComponent x) {
	return new BasicTreeUI();
    }

    public BasicTreeUI() {
	super();
    }

    protected Color getHashColor() {
        return hashColor;
    }

    protected void setHashColor(Color color) {
        hashColor = color;
    }

    public void setLeftChildIndent(int newAmount) {
	leftChildIndent = newAmount;
	totalChildIndent = leftChildIndent + rightChildIndent;
	if(treeState != null)
	    treeState.invalidateSizes();
	updateSize();
    }

    public int getLeftChildIndent() {
	return leftChildIndent;
    }

    public void setRightChildIndent(int newAmount) {
	rightChildIndent = newAmount;
	totalChildIndent = leftChildIndent + rightChildIndent;
	if(treeState != null)
	    treeState.invalidateSizes();
	updateSize();
    }

    public int getRightChildIndent() {
	return rightChildIndent;
    }

    public void setExpandedIcon(Icon newG) {
	expandedIcon = newG;
    }

    public Icon getExpandedIcon() {
	return expandedIcon;
    }

    public void setCollapsedIcon(Icon newG) {
	collapsedIcon = newG;
    }

    public Icon getCollapsedIcon() {
	return collapsedIcon;
    }

    //
    // Methods for configuring the behavior of the tree. None of them
    // push the value to the JTree instance. You should really only
    // call these methods on the JTree.
    //

    /**
     * Updates the componentListener, if necessary.
     */
    protected void setLargeModel(boolean largeModel) {
	if(getRowHeight() < 1)
	    largeModel = false;
	if(this.largeModel != largeModel) {
	    completeEditing();
	    this.largeModel = largeModel;
	    treeState = createLayoutCache();
	    configureLayoutCache();
	    updateLayoutCacheExpandedNodes();
	    updateSize();
	}
    }

    protected boolean isLargeModel() {
	return largeModel;
    }

    /**
     * Sets the row height, this is forwarded to the treeState.
     */
    protected void setRowHeight(int rowHeight) {
	completeEditing();
	if(treeState != null) {
	    setLargeModel(tree.isLargeModel());
	    treeState.setRowHeight(rowHeight);
	    updateSize();
	}
    }

    protected int getRowHeight() {
	return (tree == null) ? -1 : tree.getRowHeight();
    }

    /**
     * Sets the TreeCellRenderer to <code>tcr</code>. This invokes
     * <code>updateRenderer</code>.
     */
    protected void setCellRenderer(TreeCellRenderer tcr) {
	completeEditing();
	updateRenderer();
	if(treeState != null) {
	    treeState.invalidateSizes();
	    updateSize();
	}
    }

    /**
     * Return currentCellRenderer, which will either be the trees
     * renderer, or defaultCellRenderer, which ever wasn't null.
     */
    protected TreeCellRenderer getCellRenderer() {
	return currentCellRenderer;
    }

    /**
     * Sets the TreeModel.
     */
    protected void setModel(TreeModel model) {
	completeEditing();
	if(treeModel != null && treeModelListener != null)
	    treeModel.removeTreeModelListener(treeModelListener);
	treeModel = model;
	if(treeModel != null) {
	    if(treeModelListener != null)
		treeModel.addTreeModelListener(treeModelListener);
	}
	if(treeState != null) {
	    treeState.setModel(model);
	    updateLayoutCacheExpandedNodes();
	    updateSize();
	}
    }

    protected TreeModel getModel() {
	return treeModel;
    }

    /**
     * Sets the root to being visible.
     */
    protected void setRootVisible(boolean newValue) {
	completeEditing();
	updateDepthOffset();
	if(treeState != null) {
	    treeState.setRootVisible(newValue);
	    treeState.invalidateSizes();
	    updateSize();
	}
    }

    protected boolean isRootVisible() {
	return (tree != null) ? tree.isRootVisible() : false;
    }

    /**
     * Determines whether the node handles are to be displayed.
     */
    protected void setShowsRootHandles(boolean newValue) {
	completeEditing();
	updateDepthOffset();
	if(treeState != null) {
	    treeState.invalidateSizes();
	    updateSize();
	}
    }

    protected boolean getShowsRootHandles() {
	return (tree != null) ? tree.getShowsRootHandles() : false;
    }

    /**
     * Sets the cell editor.
     */
    protected void setCellEditor(TreeCellEditor editor) {
	updateCellEditor();
    }

    protected TreeCellEditor getCellEditor() {
	return (tree != null) ? tree.getCellEditor() : null;
    }

    /**
     * Configures the receiver to allow, or not allow, editing.
     */
    protected void setEditable(boolean newValue) {
	updateCellEditor();
    }

    protected boolean isEditable() {
	return (tree != null) ? tree.isEditable() : false;
    }

    /**
     * Resets the selection model. The appropriate listener are installed
     * on the model.
     */
    protected void setSelectionModel(TreeSelectionModel newLSM) {
	completeEditing();
	if(selectionModelPropertyChangeListener != null &&
	   treeSelectionModel != null)
	    treeSelectionModel.removePropertyChangeListener
		              (selectionModelPropertyChangeListener);
	if(treeSelectionListener != null && treeSelectionModel != null)
	    treeSelectionModel.removeTreeSelectionListener
		               (treeSelectionListener);
	treeSelectionModel = newLSM;
	if(treeSelectionModel != null) {
	    if(selectionModelPropertyChangeListener != null)
		treeSelectionModel.addPropertyChangeListener
		              (selectionModelPropertyChangeListener);
	    if(treeSelectionListener != null)
		treeSelectionModel.addTreeSelectionListener
		                   (treeSelectionListener);
	    if(treeState != null)
		treeState.setSelectionModel(treeSelectionModel);
	}
	else if(treeState != null)
	    treeState.setSelectionModel(null);
	if(tree != null)
	    tree.repaint();
    }

    protected TreeSelectionModel getSelectionModel() {
	return treeSelectionModel;
    }

    //
    // TreeUI methods
    //

    /**
      * Returns the Rectangle enclosing the label portion that the
      * last item in path will be drawn into.  Will return null if
      * any component in path is currently valid.
      */
    public Rectangle getPathBounds(JTree tree, TreePath path) {
	if(tree != null && treeState != null) {
	    Insets           i = tree.getInsets();
	    Rectangle        bounds = treeState.getBounds(path, null);

	    if(bounds != null && i != null) {
		bounds.x += i.left;
		bounds.y += i.top;
	    }
	    return bounds;
	}
	return null;
    }

    /**
      * Returns the path for passed in row.  If row is not visible
      * null is returned.
      */
    public TreePath getPathForRow(JTree tree, int row) {
	return (treeState != null) ? treeState.getPathForRow(row) : null;
    }

    /**
      * Returns the row that the last item identified in path is visible
      * at.  Will return -1 if any of the elements in path are not
      * currently visible.
      */
    public int getRowForPath(JTree tree, TreePath path) {
	return (treeState != null) ? treeState.getRowForPath(path) : -1;
    }

    /**
      * Returns the number of rows that are being displayed.
      */
    public int getRowCount(JTree tree) {
	return (treeState != null) ? treeState.getRowCount() : 0;
    }

    /**
      * Returns the path to the node that is closest to x,y.  If
      * there is nothing currently visible this will return null, otherwise
      * it'll always return a valid path.  If you need to test if the
      * returned object is exactly at x, y you should get the bounds for
      * the returned path and test x, y against that.
      */
    public TreePath getClosestPathForLocation(JTree tree, int x, int y) {
	if(tree != null && treeState != null) {
	    Insets          i = tree.getInsets();

	    if(i == null)
		i = EMPTY_INSETS;

	    return treeState.getPathClosestTo(x - i.left, y - i.top);
	}
	return null;
    }

    /**
      * Returns true if the tree is being edited.  The item that is being
      * edited can be returned by getEditingPath().
      */
    public boolean isEditing(JTree tree) {
	return (editingComponent != null);
    }

    /**
      * Stops the current editing session.  This has no effect if the
      * tree isn't being edited.  Returns true if the editor allows the
      * editing session to stop.
      */
    public boolean stopEditing(JTree tree) {
	if(editingComponent != null && cellEditor.stopCellEditing()) {
	    completeEditing(false, false, true);
	    return true;
	}
	return false;
    }

    /**
      * Cancels the current editing session.
      */
    public void cancelEditing(JTree tree) {
	if(editingComponent != null) {
	    completeEditing(false, true, false);
	}
    }

    /**
      * Selects the last item in path and tries to edit it.  Editing will
      * fail if the CellEditor won't allow it for the selected item.
      */
    public void startEditingAtPath(JTree tree, TreePath path) {
	tree.scrollPathToVisible(path);
	if(path != null && tree.isVisible(path))
	    startEditing(path, null);
    }

    /**
     * Returns the path to the element that is being edited.
     */
    public TreePath getEditingPath(JTree tree) {
	return editingPath;
    }

    //
    // Install methods
    //

    public void installUI(JComponent c) {
        if ( c == null ) {
	    throw new NullPointerException( "null component passed to BasicTreeUI.installUI()" );
        }

	tree = (JTree)c;

	prepareForUIInstall();

	// Boilerplate install block
	installDefaults();
	installListeners();
	installKeyboardActions();
	installComponents();

	completeUIInstall();
    }

    /**
     * Invoked after the <code>tree</code> instance variable has been
     * set, but before any defaults/listeners have been installed.
     */
    protected void prepareForUIInstall() {
	drawingCache = new Hashtable(7);

	// Data member initializations
	leftToRight = BasicGraphicsUtils.isLeftToRight(tree);
	lastWidth = tree.getWidth();
	stopEditingInCompleteEditing = true;
	lastSelectedRow = -1;
	leadRow = -1;
	preferredSize = new Dimension();
	tree.setRowHeight(UIManager.getInt("Tree.rowHeight"));

	Object     b = UIManager.get("Tree.scrollsOnExpand");

	if(b != null)
	    tree.setScrollsOnExpand(((Boolean)b).booleanValue());

	largeModel = tree.isLargeModel();
	if(getRowHeight() <= 0)
	    largeModel = false;
	setModel(tree.getModel());
    }

    /**
     * Invoked from installUI after all the defaults/listeners have been
     * installed.
     */
    protected void completeUIInstall() {
	// Custom install code

	this.setShowsRootHandles(tree.getShowsRootHandles());

	updateRenderer();

	updateDepthOffset();

	setSelectionModel(tree.getSelectionModel());

	// Create, if necessary, the TreeState instance.
	treeState = createLayoutCache();
	configureLayoutCache();

	updateSize();
    }

    protected void installDefaults() {
	if(tree.getBackground() == null ||
	   tree.getBackground() instanceof UIResource) {
	    tree.setBackground(UIManager.getColor("Tree.background"));
	} 
	if(getHashColor() == null || getHashColor() instanceof UIResource) {
	    setHashColor(UIManager.getColor("Tree.hash"));
	}
	if (tree.getFont() == null || tree.getFont() instanceof UIResource)
	    tree.setFont( UIManager.getFont("Tree.font") );

	setExpandedIcon( (Icon)UIManager.get( "Tree.expandedIcon" ) );
	setCollapsedIcon( (Icon)UIManager.get( "Tree.collapsedIcon" ) );

	setLeftChildIndent(((Integer)UIManager.get("Tree.leftChildIndent")).
			   intValue());
	setRightChildIndent(((Integer)UIManager.get("Tree.rightChildIndent")).
			   intValue());

	TransferHandler th = tree.getTransferHandler();
	if (th == null || th instanceof UIResource) {
	    tree.setTransferHandler(defaultTransferHandler);
	}
	DropTarget dropTarget = tree.getDropTarget();
	if (dropTarget instanceof UIResource) {
            if (defaultDropTargetListener == null) {
                defaultDropTargetListener = new TreeDropTargetListener();
            }
	    try {
		dropTarget.addDropTargetListener(defaultDropTargetListener);
	    } catch (TooManyListenersException tmle) {
		// should not happen... swing drop target is multicast
	    }
	}
    }

    protected void installListeners() {
        if ( (propertyChangeListener = createPropertyChangeListener())
	     != null ) {
	    tree.addPropertyChangeListener(propertyChangeListener);
	}
	tree.addMouseListener(defaultDragRecognizer);
	tree.addMouseMotionListener(defaultDragRecognizer);
        if ( (mouseListener = createMouseListener()) != null ) {
	    tree.addMouseListener(mouseListener);
	    if (mouseListener instanceof MouseMotionListener) {
		tree.addMouseMotionListener((MouseMotionListener)mouseListener);
	    }
	}
        if ((focusListener = createFocusListener()) != null ) {
	    tree.addFocusListener(focusListener);
	}
        if ((keyListener = createKeyListener()) != null) {
	    tree.addKeyListener(keyListener);
	}
	if((treeExpansionListener = createTreeExpansionListener()) != null) {
	    tree.addTreeExpansionListener(treeExpansionListener);
	}
	if((treeModelListener = createTreeModelListener()) != null &&
	   treeModel != null) {
	    treeModel.addTreeModelListener(treeModelListener);
	}
	if((selectionModelPropertyChangeListener =
	    createSelectionModelPropertyChangeListener()) != null &&
	   treeSelectionModel != null) {
	    treeSelectionModel.addPropertyChangeListener
		(selectionModelPropertyChangeListener);
	}
	if((treeSelectionListener = createTreeSelectionListener()) != null &&
	   treeSelectionModel != null) {
	    treeSelectionModel.addTreeSelectionListener(treeSelectionListener);
	}
    }

    protected void installKeyboardActions() {
	InputMap km = getInputMap(JComponent.
				  WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

	SwingUtilities.replaceUIInputMap(tree, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
					 km);
	km = getInputMap(JComponent.WHEN_FOCUSED);
	SwingUtilities.replaceUIInputMap(tree, JComponent.WHEN_FOCUSED, km);

	ActionMap am = getActionMap();

	SwingUtilities.replaceUIActionMap(tree, am);
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    return (InputMap)UIManager.get("Tree.ancestorInputMap");
	}
	else if (condition == JComponent.WHEN_FOCUSED) {
	    InputMap keyMap = (InputMap)UIManager.get("Tree.focusInputMap");
	    InputMap rtlKeyMap;

	    if (tree.getComponentOrientation().isLeftToRight() ||
		((rtlKeyMap = (InputMap)UIManager.get("Tree.focusInputMap.RightToLeft")) == null)) {
		return keyMap;
	    } else {
		rtlKeyMap.setParent(keyMap);
		return rtlKeyMap;
	    }
	}
	return null;
    }

    ActionMap getActionMap() {
	return createActionMap();
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();

	map.put("selectPrevious", new TreeIncrementAction(-1, "selectPrevious",
							  false, true));
	map.put("selectPreviousChangeLead", new TreeIncrementAction
		(-1, "selectPreviousLead", false, false));
	map.put("selectPreviousExtendSelection", new TreeIncrementAction
		(-1, "selectPreviousExtendSelection", true, true));

	map.put("selectNext", new TreeIncrementAction
		(1, "selectNext", false, true));
	map.put("selectNextChangeLead", new TreeIncrementAction
		(1, "selectNextLead", false, false));
	map.put("selectNextExtendSelection", new TreeIncrementAction
		(1, "selectNextExtendSelection", true, true));

	map.put("selectChild", new TreeTraverseAction
		(1, "selectChild", true));
	map.put("selectChildChangeLead", new TreeTraverseAction
		(1, "selectChildLead", false));

	map.put("selectParent", new TreeTraverseAction
		(-1, "selectParent", true));
	map.put("selectParentChangeLead", new TreeTraverseAction
		(-1, "selectParentLead", false));	

	map.put("scrollUpChangeSelection", new TreePageAction
		(-1, "scrollUpChangeSelection", false, true));
	map.put("scrollUpChangeLead", new TreePageAction
		(-1, "scrollUpChangeLead", false, false));
	map.put("scrollUpExtendSelection", new TreePageAction
		(-1, "scrollUpExtendSelection", true, true));

	map.put("scrollDownChangeSelection", new TreePageAction
		(1, "scrollDownChangeSelection", false, true));
	map.put("scrollDownExtendSelection", new TreePageAction
		(1, "scrollDownExtendSelection", true, true));
	map.put("scrollDownChangeLead", new TreePageAction
		(1, "scrollDownChangeLead", false, false));

	map.put("selectFirst", new TreeHomeAction
		(-1, "selectFirst", false, true));
	map.put("selectFirstChangeLead", new TreeHomeAction
		(-1, "selectFirst", false, false));
	map.put("selectFirstExtendSelection",new TreeHomeAction
		(-1, "selectFirstExtendSelection", true, true));

	map.put("selectLast", new TreeHomeAction
		(1, "selectLast", false, true));
	map.put("selectLastChangeLead", new TreeHomeAction
		(1, "selectLast", false, false));
	map.put("selectLastExtendSelection", new TreeHomeAction
		(1, "selectLastExtendSelection", true, true));

	map.put("toggle", new TreeToggleAction("toggle"));

	map.put("cancel", new TreeCancelEditingAction("cancel"));

	map.put("startEditing", new TreeEditAction("startEditing"));

	map.put("selectAll", new TreeSelectAllAction("selectAll", true));

	map.put("clearSelection", new TreeSelectAllAction
		("clearSelection", false));

	map.put("toggleSelectionPreserveAnchor",
		new TreeAddSelectionAction("toggleSelectionPreserveAnchor",
					   false));
	map.put("toggleSelection",
		new TreeAddSelectionAction("toggleSelection", true));

	map.put("extendSelection", new TreeExtendSelectionAction
		("extendSelection"));

	map.put("scrollLeft", new ScrollAction
		(tree, SwingConstants.HORIZONTAL, -10));
	map.put("scrollLeftExtendSelection", new TreeScrollLRAction
		(-1, "scrollLeftExtendSelection", true, true));
	map.put("scrollRight", new ScrollAction
		(tree, SwingConstants.HORIZONTAL, 10));
	map.put("scrollRightExtendSelection", new TreeScrollLRAction
		(1, "scrollRightExtendSelection", true, true));

	map.put("scrollRightChangeLead", new TreeScrollLRAction
		(1, "scrollRightChangeLead", false, false));
	map.put("scrollLeftChangeLead", new TreeScrollLRAction
		(-1, "scrollLeftChangeLead", false, false));

        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
	return map;
    }

    /**
     * Intalls the subcomponents of the tree, which is the renderer pane.
     */
    protected void installComponents() {
	if ((rendererPane = createCellRendererPane()) != null) {
	    tree.add( rendererPane );
	}
    }

    //
    // Create methods.
    //

    /**
     * Creates an instance of NodeDimensions that is able to determine
     * the size of a given node in the tree.
     */
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
	return new NodeDimensionsHandler();
    }

    /**
     * Creates a listener that is responsible that updates the UI based on
     * how the tree changes.
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Creates the listener responsible for updating the selection based on
     * mouse events.
     */
    protected MouseListener createMouseListener() {
        return new MouseHandler();
    }

    /**
     * Creates a listener that is responsible for updating the display
     * when focus is lost/gained.
     */
    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    /**
     * Creates the listener reponsible for getting key events from
     * the tree.
     */
    protected KeyListener createKeyListener() {
        return new KeyHandler();
    }

    /**
     * Creates the listener responsible for getting property change
     * events from the selection model.
     */
    protected PropertyChangeListener createSelectionModelPropertyChangeListener() {
	return new SelectionModelPropertyChangeHandler();
    }

    /**
     * Creates the listener that updates the display based on selection change
     * methods.
     */
    protected TreeSelectionListener createTreeSelectionListener() {
	return new TreeSelectionHandler();
    }

    /**
     * Creates a listener to handle events from the current editor.
     */
    protected CellEditorListener createCellEditorListener() {
	return new CellEditorHandler();
    }

    /**
     * Creates and returns a new ComponentHandler. This is used for
     * the large model to mark the validCachedPreferredSize as invalid
     * when the component moves.
     */
    protected ComponentListener createComponentListener() {
	return new ComponentHandler();
    }

    /**
     * Creates and returns the object responsible for updating the treestate
     * when nodes expanded state changes.
     */
    protected TreeExpansionListener createTreeExpansionListener() {
	return new TreeExpansionHandler();
    }

    /**
     * Creates the object responsible for managing what is expanded, as
     * well as the size of nodes.
     */
    protected AbstractLayoutCache createLayoutCache() {
	if(isLargeModel() && getRowHeight() > 0) {
	    return new FixedHeightLayoutCache();
	}
	return new VariableHeightLayoutCache();
    }

    /**
     * Returns the renderer pane that renderer components are placed in.
     */
    protected CellRendererPane createCellRendererPane() {
        return new CellRendererPane();
    }

    /**
      * Creates a default cell editor.
      */
    protected TreeCellEditor createDefaultCellEditor() {
	if(currentCellRenderer != null &&
	   (currentCellRenderer instanceof DefaultTreeCellRenderer)) {
	    DefaultTreeCellEditor editor = new DefaultTreeCellEditor
		        (tree, (DefaultTreeCellRenderer)currentCellRenderer);

	    return editor;
	}
	return new DefaultTreeCellEditor(tree, null);
    }

    /**
      * Returns the default cell renderer that is used to do the
      * stamping of each node.
      */
    protected TreeCellRenderer createDefaultCellRenderer() {
	return new DefaultTreeCellRenderer();
    }

    /**
     * Returns a listener that can update the tree when the model changes.
     */
    protected TreeModelListener createTreeModelListener() {
	return new TreeModelHandler();
    }

    //
    // Uninstall methods
    //

    public void uninstallUI(JComponent c) {
	completeEditing();

	prepareForUIUninstall();

	uninstallDefaults();
	uninstallListeners();
	uninstallKeyboardActions();
	uninstallComponents();

	completeUIUninstall();
    }

    protected void prepareForUIUninstall() {
    }

    protected void completeUIUninstall() {
	if(createdRenderer) {
	    tree.setCellRenderer(null);
	}
	if(createdCellEditor) {
	    tree.setCellEditor(null);
	}
	cellEditor = null;
	currentCellRenderer = null;
	rendererPane = null;
        componentListener = null;
	propertyChangeListener = null;
	mouseListener = null;
	focusListener = null;
	keyListener = null;
	setSelectionModel(null);
	treeState = null;
	drawingCache = null;
	selectionModelPropertyChangeListener = null;
	tree = null;
	treeModel = null;
	treeSelectionModel = null;
	treeSelectionListener = null;
	treeExpansionListener = null;
    }

    protected void uninstallDefaults() {
	if (tree.getTransferHandler() instanceof UIResource) {
	    tree.setTransferHandler(null);
	}
    }

    protected void uninstallListeners() {
	if(componentListener != null) {
	    tree.removeComponentListener(componentListener);
	}
        if (propertyChangeListener != null) {
	    tree.removePropertyChangeListener(propertyChangeListener);
	}
	tree.removeMouseListener(defaultDragRecognizer);
	tree.removeMouseMotionListener(defaultDragRecognizer);
        if (mouseListener != null) {
	    tree.removeMouseListener(mouseListener);
	    if (mouseListener instanceof MouseMotionListener) {
		tree.removeMouseMotionListener((MouseMotionListener)mouseListener);
	    }
	}
        if (focusListener != null) {
	    tree.removeFocusListener(focusListener);
	}
        if (keyListener != null) {
	    tree.removeKeyListener(keyListener);
	}
	if(treeExpansionListener != null) {
	    tree.removeTreeExpansionListener(treeExpansionListener);
	}
	if(treeModel != null && treeModelListener != null) {
	    treeModel.removeTreeModelListener(treeModelListener);
	}
	if(selectionModelPropertyChangeListener != null &&
	   treeSelectionModel != null) {
	    treeSelectionModel.removePropertyChangeListener
		(selectionModelPropertyChangeListener);
	}
	if(treeSelectionListener != null && treeSelectionModel != null) {
	    treeSelectionModel.removeTreeSelectionListener
		               (treeSelectionListener);
	}
    }

    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(tree, null);
	SwingUtilities.replaceUIInputMap(tree, JComponent.
					 WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
					 null);
	SwingUtilities.replaceUIInputMap(tree, JComponent.WHEN_FOCUSED, null);
    }

    /**
     * Uninstalls the renderer pane.
     */
    protected void uninstallComponents() {
	if(rendererPane != null) {
	    tree.remove(rendererPane);
	}
    }

    /**
     * Recomputes the right margin, and invalidates any tree states
     */
    private void redoTheLayout() {
	if (treeState != null) {
	    treeState.invalidateSizes();
	}
    }

    //
    // Painting routines.
    //

    public void paint(Graphics g, JComponent c) {
	if (tree != c) {
	    throw new InternalError("incorrect component");
	}

	// Should never happen if installed for a UI
	if(treeState == null) {
	    return;
	}

	// Update the lastWidth if necessary.
	// This should really come from a ComponentListener installed on
	// the JTree, but for the time being it is here.
	int              width = tree.getWidth();

	if (width != lastWidth) {
	    lastWidth = width;
	    if (!leftToRight) {
		// For RTL when the size changes, we have to refresh the
		// cache as the X position is based off the width.
		redoTheLayout();
		updateSize();
	    }
	}

	Rectangle        paintBounds = g.getClipBounds();
	Insets           insets = tree.getInsets();

	if(insets == null)
	    insets = EMPTY_INSETS;

	TreePath         initialPath = getClosestPathForLocation
	                               (tree, 0, paintBounds.y);
	Enumeration      paintingEnumerator = treeState.getVisiblePathsFrom
	                                      (initialPath);
	int              row = treeState.getRowForPath(initialPath);
	int              endY = paintBounds.y + paintBounds.height;

	drawingCache.clear();

	if(initialPath != null && paintingEnumerator != null) {
	    TreePath   parentPath = initialPath;

	    // Draw the lines, knobs, and rows

	    // Find each parent and have them draw a line to their last child
	    parentPath = parentPath.getParentPath();
	    while(parentPath != null) {
		paintVerticalPartOfLeg(g, paintBounds, insets, parentPath);
		drawingCache.put(parentPath, Boolean.TRUE);
		parentPath = parentPath.getParentPath();
	    }

	    boolean         done = false;
	    // Information for the node being rendered.
	    boolean         isExpanded;
	    boolean         hasBeenExpanded;
	    boolean         isLeaf;
	    Rectangle       boundsBuffer = new Rectangle();
	    Rectangle       bounds;
	    TreePath        path;
	    boolean         rootVisible = isRootVisible();

	    while(!done && paintingEnumerator.hasMoreElements()) {
		path = (TreePath)paintingEnumerator.nextElement();
		if(path != null) {
		    isLeaf = treeModel.isLeaf(path.getLastPathComponent());
		    if(isLeaf)
			isExpanded = hasBeenExpanded = false;
		    else {
			isExpanded = treeState.getExpandedState(path);
			hasBeenExpanded = tree.hasBeenExpanded(path);
		    }
		    bounds = treeState.getBounds(path, boundsBuffer);
		    if(bounds == null)
			// This will only happen if the model changes out
			// from under us (usually in another thread).
			// Swing isn't multithreaded, but I'll put this
			// check in anyway.
			return;
		    bounds.x += insets.left;
		    bounds.y += insets.top;
		    // See if the vertical line to the parent has been drawn.
		    parentPath = path.getParentPath();
		    if(parentPath != null) {
			if(drawingCache.get(parentPath) == null) {
			    paintVerticalPartOfLeg(g, paintBounds,
						   insets, parentPath);
			    drawingCache.put(parentPath, Boolean.TRUE);
			}
			paintHorizontalPartOfLeg(g, paintBounds, insets,
						 bounds, path, row,
						 isExpanded,
						 hasBeenExpanded, isLeaf);
		    }
		    else if(rootVisible && row == 0) {
			paintHorizontalPartOfLeg(g, paintBounds, insets,
						 bounds, path, row,
						 isExpanded,
						 hasBeenExpanded, isLeaf);
		    }
		    if(shouldPaintExpandControl(path, row, isExpanded,
						hasBeenExpanded, isLeaf)) {
			paintExpandControl(g, paintBounds, insets, bounds,
					   path, row, isExpanded,
					   hasBeenExpanded, isLeaf);
		    }
                    //This is the quick fix for bug 4259260.  Somewhere we
                    //are out by 4 pixels in the RTL layout.  Its probably
                    //due to built in right-side padding in some icons.  Rather
                    //than ferret out problem at the source, this compensates. 
            	    if (!leftToRight) {
                        bounds.x +=4;
                    }
		    paintRow(g, paintBounds, insets, bounds, path,
				 row, isExpanded, hasBeenExpanded, isLeaf);
		    if((bounds.y + bounds.height) >= endY)
			done = true;
		}
		else {
		    done = true;
		}
		row++;
	    }
	}
	// Empty out the renderer pane, allowing renderers to be gc'ed.
	rendererPane.removeAll();
    }

    /**
     * Paints the horizontal part of the leg. The receiver should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.<p>
     * NOTE: <code>parentRow</code> can be -1 if the root is not visible.
     */
    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
					    Insets insets, Rectangle bounds,
					    TreePath path, int row,
					    boolean isExpanded,
					    boolean hasBeenExpanded, boolean
					    isLeaf) {
        // Don't paint the legs for the root'ish node if the
        int depth = path.getPathCount() - 1;
	if((depth == 0 || (depth == 1 && !isRootVisible())) &&
	   !getShowsRootHandles()) {
	    return;
        }

	int clipLeft = clipBounds.x;
	int clipRight = clipBounds.x + (clipBounds.width - 1);
	int clipTop = clipBounds.y;
	int clipBottom = clipBounds.y + (clipBounds.height - 1);
	int lineY = bounds.y + bounds.height / 2;
	// Offset leftX from parents indent.
	if (leftToRight) {
	    int leftX = bounds.x - getRightChildIndent();
	    int nodeX = bounds.x - getHorizontalLegBuffer();
	
	    if(lineY >= clipTop && lineY <= clipBottom && nodeX >= clipLeft &&
	                                                 leftX <= clipRight ) {
	        leftX = Math.max(Math.max(insets.left, leftX), clipLeft);
		nodeX = Math.min(Math.max(insets.left, nodeX), clipRight);

                if (leftX != nodeX) {
                    g.setColor(getHashColor());
                    paintHorizontalLine(g, tree, lineY, leftX, nodeX);
                }
	    }
	}
	else {
	    int leftX = bounds.x + bounds.width + getRightChildIndent();
	    int nodeX = bounds.x + bounds.width + 
	                                  getHorizontalLegBuffer() - 1;

	    if(lineY >= clipTop && lineY <= clipBottom &&
	       leftX >= clipLeft && nodeX <= clipRight) {
	        leftX = Math.min(leftX, clipRight);
		nodeX = Math.max(nodeX, clipLeft);

		g.setColor(getHashColor());
		paintHorizontalLine(g, tree, lineY, nodeX, leftX);
	    }
	}
    }

    /**
     * Paints the vertical part of the leg. The receiver should
     * NOT modify <code>clipBounds</code>, <code>insets</code>.<p>
     */
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
					  Insets insets, TreePath path) {
        int depth = path.getPathCount() - 1;
	if (depth == 0 && !getShowsRootHandles() && !isRootVisible()) {
	    return;
        }
	int lineX;
	if (leftToRight) {
	    lineX = ((depth + 1 + depthOffset) *
		     totalChildIndent) - getRightChildIndent() + insets.left;
	}
	else {
	    lineX = lastWidth - ((depth + depthOffset) *
				                       totalChildIndent) - 9;
	}
	int clipLeft = clipBounds.x;
	int clipRight = clipBounds.x + (clipBounds.width - 1);

	if (lineX >= clipLeft && lineX <= clipRight) {
	    int clipTop = clipBounds.y;
	    int clipBottom = clipBounds.y + clipBounds.height;
	    Rectangle parentBounds = getPathBounds(tree, path);
	    Rectangle lastChildBounds = getPathBounds(tree,
						     getLastChildPath(path));

	    if(lastChildBounds == null)
		// This shouldn't happen, but if the model is modified
		// in another thread it is possible for this to happen.
		// Swing isn't multithreaded, but I'll add this check in
		// anyway.
		return;

	    int       top;

	    if(parentBounds == null) {
		top = Math.max(insets.top + getVerticalLegBuffer(),
			       clipTop);
	    }
	    else
		top = Math.max(parentBounds.y + parentBounds.height +
			       getVerticalLegBuffer(), clipTop);
	    if(depth == 0 && !isRootVisible()) {
		TreeModel      model = getModel();

		if(model != null) {
		    Object        root = model.getRoot();

		    if(model.getChildCount(root) > 0) {
			parentBounds = getPathBounds(tree, path.
				  pathByAddingChild(model.getChild(root, 0)));
			if(parentBounds != null)
			    top = Math.max(insets.top + getVerticalLegBuffer(),
					   parentBounds.y +
					   parentBounds.height / 2);
		    }
		}
	    }

	    int bottom = Math.min(lastChildBounds.y +
				  (lastChildBounds.height / 2), clipBottom);

            if (top <= bottom) {
                g.setColor(getHashColor());
                paintVerticalLine(g, tree, lineX, top, bottom);
            }
	}
    }

    /**
     * Paints the expand (toggle) part of a row. The receiver should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.
     */
    protected void paintExpandControl(Graphics g,
				      Rectangle clipBounds, Insets insets,
				      Rectangle bounds, TreePath path,
				      int row, boolean isExpanded,
				      boolean hasBeenExpanded,
				      boolean isLeaf) {
	Object       value = path.getLastPathComponent();

	// Draw icons if not a leaf and either hasn't been loaded,
	// or the model child count is > 0.
	if (!isLeaf && (!hasBeenExpanded ||
			treeModel.getChildCount(value) > 0)) {
	    int middleXOfKnob;
	    if (leftToRight) {
	        middleXOfKnob = bounds.x - (getRightChildIndent() - 1);
	    }
	    else {
	        middleXOfKnob = bounds.x + bounds.width + getRightChildIndent();
	    }
	    int middleYOfKnob = bounds.y + (bounds.height / 2);

	    if (isExpanded) {
		Icon expandedIcon = getExpandedIcon();
		if(expandedIcon != null)
		  drawCentered(tree, g, expandedIcon, middleXOfKnob,
			       middleYOfKnob );
	    }
	    else {
		Icon collapsedIcon = getCollapsedIcon();
		if(collapsedIcon != null)
		  drawCentered(tree, g, collapsedIcon, middleXOfKnob,
			       middleYOfKnob);
	    }
	}
    }

    /**
     * Paints the renderer part of a row. The receiver should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.
     */
    protected void paintRow(Graphics g, Rectangle clipBounds,
			    Insets insets, Rectangle bounds, TreePath path,
			    int row, boolean isExpanded,
			    boolean hasBeenExpanded, boolean isLeaf) {
	// Don't paint the renderer if editing this row.
	if(editingComponent != null && editingRow == row)
	    return;

	int leadIndex;

	if(tree.hasFocus()) {
	    leadIndex = getLeadSelectionRow();
	}
	else
	    leadIndex = -1;

	Component component;

	component = currentCellRenderer.getTreeCellRendererComponent
	              (tree, path.getLastPathComponent(),
		       tree.isRowSelected(row), isExpanded, isLeaf, row,
		       (leadIndex == row));
	
	rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
				    bounds.width, bounds.height, true);	
    }

    /**
     * Returns true if the expand (toggle) control should be drawn for
     * the specified row.
     */
    protected boolean shouldPaintExpandControl(TreePath path, int row,
					       boolean isExpanded,
					       boolean hasBeenExpanded,
					       boolean isLeaf) {
	if(isLeaf)
	    return false;

	int              depth = path.getPathCount() - 1;

	if((depth == 0 || (depth == 1 && !isRootVisible())) &&
	   !getShowsRootHandles())
	    return false;
	return true;
    }

    /**
     * Paints a vertical line.
     */
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top,
				    int bottom) {
	g.drawLine(x, top, x, bottom);
    }

    /**
     * Paints a horizontal line.
     */
    protected void paintHorizontalLine(Graphics g, JComponent c, int y,
				      int left, int right) {
	g.drawLine(left, y, right, y);
    }

    /**
     * The vertical element of legs between nodes starts at the bottom of the
     * parent node by default.  This method makes the leg start below that.
     */
    protected int getVerticalLegBuffer() {
	return 0;
    } 

    /**
     * The horizontal element of legs between nodes starts at the
     * right of the left-hand side of the child node by default.  This
     * method makes the leg end before that.
     */
    protected int getHorizontalLegBuffer() {
	return 0;
    } 

    //
    // Generic painting methods
    //

    // Draws the icon centered at (x,y)
    protected void drawCentered(Component c, Graphics graphics, Icon icon,
				int x, int y) {
	icon.paintIcon(c, graphics, x - icon.getIconWidth()/2, y -
		       icon.getIconHeight()/2);
    }

    // This method is slow -- revisit when Java2D is ready.
    // assumes x1 <= x2
    protected void drawDashedHorizontalLine(Graphics g, int y, int x1, int x2){
	// Drawing only even coordinates helps join line segments so they
	// appear as one line.  This can be defeated by translating the
	// Graphics by an odd amount.
	x1 += (x1 % 2);

	for (int x = x1; x <= x2; x+=2) {
	    g.drawLine(x, y, x, y);
	}
    }

    // This method is slow -- revisit when Java2D is ready.
    // assumes y1 <= y2
    protected void drawDashedVerticalLine(Graphics g, int x, int y1, int y2) {
	// Drawing only even coordinates helps join line segments so they
	// appear as one line.  This can be defeated by translating the
	// Graphics by an odd amount.
	y1 += (y1 % 2);

	for (int y = y1; y <= y2; y+=2) {
	    g.drawLine(x, y, x, y);
	}
    }

    //
    // Various local methods
    //

    /**
     * Makes all the nodes that are expanded in JTree expanded in LayoutCache.
     * This invokes updateExpandedDescendants with the root path.
     */
    protected void updateLayoutCacheExpandedNodes() {
	if(treeModel != null && treeModel.getRoot() != null)
	    updateExpandedDescendants(new TreePath(treeModel.getRoot()));
    }

    /**
     * Updates the expanded state of all the descendants of <code>path</code>
     * by getting the expanded descendants from the tree and forwarding
     * to the tree state.
     */
    protected void updateExpandedDescendants(TreePath path) {
	completeEditing();
	if(treeState != null) {
	    treeState.setExpandedState(path, true);

	    Enumeration   descendants = tree.getExpandedDescendants(path);

	    if(descendants != null) {
		while(descendants.hasMoreElements()) {
		    path = (TreePath)descendants.nextElement();
		    treeState.setExpandedState(path, true);
		}
	    }
	    updateLeadRow();
	    updateSize();
	}
    }

    /**
     * Returns a path to the last child of <code>parent</code>.
     */
    protected TreePath getLastChildPath(TreePath parent) {
	if(treeModel != null) {
	    int         childCount = treeModel.getChildCount
		(parent.getLastPathComponent());
	    
	    if(childCount > 0)
		return parent.pathByAddingChild(treeModel.getChild
			   (parent.getLastPathComponent(), childCount - 1));
	}
	return null;
    }

    /**
     * Updates how much each depth should be offset by.
     */
    protected void updateDepthOffset() {
	if(isRootVisible()) {
	    if(getShowsRootHandles())
		depthOffset = 1;
	    else
		depthOffset = 0;
	}
	else if(!getShowsRootHandles())
	    depthOffset = -1;
	else
	    depthOffset = 0;
    }

    /** 
      * Updates the cellEditor based on the editability of the JTree that
      * we're contained in.  If the tree is editable but doesn't have a
      * cellEditor, a basic one will be used.
      */
    protected void updateCellEditor() {
	TreeCellEditor        newEditor;

	completeEditing();
	if(tree == null)
	    newEditor = null;
	else {
	    if(tree.isEditable()) {
		newEditor = tree.getCellEditor();
		if(newEditor == null) {
		    newEditor = createDefaultCellEditor();
		    if(newEditor != null) {
			tree.setCellEditor(newEditor);
			createdCellEditor = true;
		    }
		}
	    }
	    else
		newEditor = null;
	}
	if(newEditor != cellEditor) {
	    if(cellEditor != null && cellEditorListener != null)
		cellEditor.removeCellEditorListener(cellEditorListener);
	    cellEditor = newEditor;
	    if(cellEditorListener == null)
		cellEditorListener = createCellEditorListener();
	    if(newEditor != null && cellEditorListener != null)
		newEditor.addCellEditorListener(cellEditorListener);
	    createdCellEditor = false;
	}
    }

    /**
      * Messaged from the tree we're in when the renderer has changed.
      */
    protected void updateRenderer() {
	if(tree != null) {
	    TreeCellRenderer      newCellRenderer;

	    newCellRenderer = tree.getCellRenderer();
	    if(newCellRenderer == null) {
		tree.setCellRenderer(createDefaultCellRenderer());
		createdRenderer = true;
	    }
	    else {
		createdRenderer = false;
		currentCellRenderer = newCellRenderer;
		if(createdCellEditor) {
		    tree.setCellEditor(null);
		}
	    }
	}
	else {
	    createdRenderer = false;
	    currentCellRenderer = null;
	}
	updateCellEditor();
    }

    /**
     * Resets the TreeState instance based on the tree we're providing the
     * look and feel for.
     */
    protected void configureLayoutCache() {
	if(treeState != null && tree != null) {
	    if(nodeDimensions == null)
		nodeDimensions = createNodeDimensions();
	    treeState.setNodeDimensions(nodeDimensions);
	    treeState.setRootVisible(tree.isRootVisible());
	    treeState.setRowHeight(tree.getRowHeight());
	    treeState.setSelectionModel(getSelectionModel());
	    // Only do this if necessary, may loss state if call with
	    // same model as it currently has.
	    if(treeState.getModel() != tree.getModel())
		treeState.setModel(tree.getModel());
	    updateLayoutCacheExpandedNodes();
	    // Create a listener to update preferred size when bounds
	    // changes, if necessary.
	    if(isLargeModel()) {
		if(componentListener == null) {
		    componentListener = createComponentListener();
		    if(componentListener != null)
			tree.addComponentListener(componentListener);
		}
	    }
	    else if(componentListener != null) {
		tree.removeComponentListener(componentListener);
		componentListener = null;
	    }
	}
	else if(componentListener != null) {
	    tree.removeComponentListener(componentListener);
	    componentListener = null;
	}
    }

    /**
     * Marks the cached size as being invalid, and messages the
     * tree with <code>treeDidChange</code>.
     */
    protected void updateSize() {
	validCachedPreferredSize = false;
	tree.treeDidChange();
    }

    /**
     * Updates the <code>preferredSize</code> instance variable,
     * which is returned from <code>getPreferredSize()</code>.<p>
     * For left to right orientations, the size is determined from the
     * current AbstractLayoutCache. For RTL orientations, the preferred size
     * becomes the width minus the minimum x position.
     */
    protected void updateCachedPreferredSize() {
	if(treeState != null) {
	    Insets               i = tree.getInsets();

	    if(isLargeModel()) {
		Rectangle            visRect = tree.getVisibleRect();

		if(i != null) {
		    visRect.x -= i.left;
		    visRect.y -= i.top;
		}
		if (leftToRight) {
		    preferredSize.width = treeState.getPreferredWidth(visRect);
		}
		else {
		    if (getRowCount(tree) == 0) {
			preferredSize.width = 0;
		    }
		    else {
			preferredSize.width = lastWidth - getMinX(visRect);
		    }
		}
	    }
	    else if (leftToRight) {
		preferredSize.width = treeState.getPreferredWidth(null);
	    }
	    else {
		Rectangle tempRect = null;
		int rowCount = tree.getRowCount();
		int width = 0;
		for (int counter = 0; counter < rowCount; counter++) {
		    tempRect = treeState.getBounds
			       (treeState.getPathForRow(counter), tempRect);
		    if (tempRect != null) {
			width = Math.max(lastWidth - tempRect.x, width);
		    }
		}
		preferredSize.width = width;
	    }
	    preferredSize.height = treeState.getPreferredHeight();
	    if(i != null) {
		preferredSize.width += i.left + i.right;
		preferredSize.height += i.top + i.bottom;
	    }
	}
	validCachedPreferredSize = true;
    }

    /**
     * Returns the minimum x location for the nodes in <code>bounds</code>.
     */
    private int getMinX(Rectangle bounds) {
	TreePath      firstPath;
	int           endY;

	if(bounds == null) {
	    firstPath = getPathForRow(tree, 0);
	    endY = Integer.MAX_VALUE;
	}
	else {
	    firstPath = treeState.getPathClosestTo(bounds.x, bounds.y);
	    endY = bounds.height + bounds.y;
	}

	Enumeration   paths = treeState.getVisiblePathsFrom(firstPath);
	int           minX = 0;

	if(paths != null && paths.hasMoreElements()) {
	    Rectangle   pBounds = treeState.getBounds
		                  ((TreePath)paths.nextElement(), null);
	    int         width;

	    if(pBounds != null) {
		minX = pBounds.x + pBounds.width;
		if (pBounds.y >= endY) {
		    return minX;
		}
	    }
	    while (pBounds != null && paths.hasMoreElements()) {
		pBounds = treeState.getBounds((TreePath)paths.nextElement(),
					      pBounds);
		if (pBounds != null && pBounds.y < endY) {
		    minX = Math.min(minX, pBounds.x);
		}
		else {
		    pBounds = null;
		}
	    }
	    return minX;
	}
	return minX;
    }

    /**
      * Messaged from the VisibleTreeNode after it has been expanded.
      */
    protected void pathWasExpanded(TreePath path) {
	if(tree != null) {
	    tree.fireTreeExpanded(path);
	}
    }

    /**
      * Messaged from the VisibleTreeNode after it has collapsed.
      */
    protected void pathWasCollapsed(TreePath path) {
	if(tree != null) {
	    tree.fireTreeCollapsed(path);
	}
    }

    /**
      * Ensures that the rows identified by beginRow through endRow are
      * visible.
      */
    protected void ensureRowsAreVisible(int beginRow, int endRow) {
	if(tree != null && beginRow >= 0 && endRow < getRowCount(tree)) {
	    if(beginRow == endRow) {
		Rectangle     scrollBounds = getPathBounds(tree, getPathForRow
							   (tree, beginRow));

		if(scrollBounds != null) {
		    tree.scrollRectToVisible(scrollBounds);
		}
	    }
	    else {
		Rectangle   beginRect = getPathBounds(tree, getPathForRow
						      (tree, beginRow));
		Rectangle   visRect = tree.getVisibleRect();
		Rectangle   testRect = beginRect;
		int         beginY = beginRect.y;
		int         maxY = beginY + visRect.height;

		for(int counter = beginRow + 1; counter <= endRow; counter++) {
		    testRect = getPathBounds(tree,
					     getPathForRow(tree, counter));
		    if((testRect.y + testRect.height) > maxY)
			counter = endRow;
		}
		tree.scrollRectToVisible(new Rectangle(visRect.x, beginY, 1,
						  testRect.y + testRect.height-
						  beginY));
	    }
	}
    }

    /** Sets the preferred minimum size.
      */
    public void setPreferredMinSize(Dimension newSize) {
	preferredMinSize = newSize;
    }

    /** Returns the minimum preferred size.
      */
    public Dimension getPreferredMinSize() {
	if(preferredMinSize == null)
	    return null;
	return new Dimension(preferredMinSize);
    }

    /** Returns the preferred size to properly display the tree,
      * this is a cover method for getPreferredSize(c, false).
      */
    public Dimension getPreferredSize(JComponent c) {
	return getPreferredSize(c, true);
    }

    /** Returns the preferred size to represent the tree in
      * <I>c</I>.  If <I>checkConsistancy</I> is true
      * <b>checkConsistancy</b> is messaged first.
      */
    public Dimension getPreferredSize(JComponent c,
				      boolean checkConsistancy) {
	Dimension       pSize = this.getPreferredMinSize();

	if(!validCachedPreferredSize)
	    updateCachedPreferredSize();
	if(tree != null) {
	    if(pSize != null)
		return new Dimension(Math.max(pSize.width,
					      preferredSize.width),
			      Math.max(pSize.height, preferredSize.height));
	    return new Dimension(preferredSize.width, preferredSize.height);
	}
	else if(pSize != null)
	    return pSize;
	else
	    return new Dimension(0, 0);
    }

    /**
      * Returns the minimum size for this component.  Which will be
      * the min preferred size or 0, 0.
      */
    public Dimension getMinimumSize(JComponent c) {
	if(this.getPreferredMinSize() != null)
	    return this.getPreferredMinSize();
	return new Dimension(0, 0);
    }

    /**
      * Returns the maximum size for this component, which will be the
      * preferred size if the instance is currently in a JTree, or 0, 0.
      */
    public Dimension getMaximumSize(JComponent c) {
	if(tree != null)
	    return getPreferredSize(tree);
	if(this.getPreferredMinSize() != null)
	    return this.getPreferredMinSize();
	return new Dimension(0, 0);
    }


    /**
     * Messages to stop the editing session. If the UI the receiver
     * is providing the look and feel for returns true from
     * <code>getInvokesStopCellEditing</code>, stopCellEditing will
     * invoked on the current editor. Then completeEditing will
     * be messaged with false, true, false to cancel any lingering
     * editing.
     */
    protected void completeEditing() {
	/* If should invoke stopCellEditing, try that */
	if(tree.getInvokesStopCellEditing() &&
	   stopEditingInCompleteEditing && editingComponent != null) {
	    cellEditor.stopCellEditing();
	}
	/* Invoke cancelCellEditing, this will do nothing if stopCellEditing
	   was successful. */
	completeEditing(false, true, false);
    }

    /**
      * Stops the editing session.  If messageStop is true the editor
      * is messaged with stopEditing, if messageCancel is true the
      * editor is messaged with cancelEditing. If messageTree is true
      * the treeModel is messaged with valueForPathChanged.
      */
    protected void completeEditing(boolean messageStop,
				   boolean messageCancel,
				   boolean messageTree) {
	if(stopEditingInCompleteEditing && editingComponent != null) {
	    Component             oldComponent = editingComponent;
	    TreePath              oldPath = editingPath;
	    TreeCellEditor        oldEditor = cellEditor;
	    Object                newValue = oldEditor.getCellEditorValue();
	    Rectangle             editingBounds = getPathBounds(tree, 
								editingPath);
	    boolean               requestFocus = (tree != null &&
		                   (tree.hasFocus() || SwingUtilities.
				    findFocusOwner(editingComponent) != null));

	    editingComponent = null;
	    editingPath = null;
	    if(messageStop)
		oldEditor.stopCellEditing();
	    else if(messageCancel)
		oldEditor.cancelCellEditing();
	    tree.remove(oldComponent);
	    if(editorHasDifferentSize) {
		treeState.invalidatePathBounds(oldPath);
		updateSize();
	    }
	    else {
		editingBounds.x = 0;
		editingBounds.width = tree.getSize().width;
		tree.repaint(editingBounds);
	    }
	    if(requestFocus)
		tree.requestFocus();
	    if(messageTree)
		treeModel.valueForPathChanged(oldPath, newValue);
	}
    }

    /**
      * Will start editing for node if there is a cellEditor and
      * shouldSelectCell returns true.<p>
      * This assumes that path is valid and visible.
      */
    protected boolean startEditing(TreePath path, MouseEvent event) {
        if (isEditing(tree) && tree.getInvokesStopCellEditing() &&
                               !stopEditing(tree)) {
            return false;
        }
	completeEditing();
	if(cellEditor != null && tree.isPathEditable(path)) {
	    int           row = getRowForPath(tree, path);

	    if(cellEditor.isCellEditable(event)) {
                editingComponent = cellEditor.getTreeCellEditorComponent
		      (tree, path.getLastPathComponent(),
		       tree.isPathSelected(path), tree.isExpanded(path),
		       treeModel.isLeaf(path.getLastPathComponent()), row);

		Rectangle           nodeBounds = getPathBounds(tree, path);

		editingRow = row;

		Dimension editorSize = editingComponent.getPreferredSize();

		// Only allow odd heights if explicitly set.
		if(editorSize.height != nodeBounds.height &&
		   getRowHeight() > 0)
		    editorSize.height = getRowHeight();

		if(editorSize.width != nodeBounds.width ||
		   editorSize.height != nodeBounds.height) {
		    // Editor wants different width or height, invalidate 
		    // treeState and relayout.
		    editorHasDifferentSize = true;
		    treeState.invalidatePathBounds(path);
		    updateSize();
		}
		else
		    editorHasDifferentSize = false;
		tree.add(editingComponent);
		editingComponent.setBounds(nodeBounds.x, nodeBounds.y,
					   editorSize.width,
					   editorSize.height);
		editingPath = path;
		editingComponent.validate();

		Rectangle              visRect = tree.getVisibleRect();

		tree.paintImmediately(nodeBounds.x, nodeBounds.y,
				      visRect.width + visRect.x - nodeBounds.x,
				      editorSize.height);
		if(cellEditor.shouldSelectCell(event)) {
		    stopEditingInCompleteEditing = false;
		    try {
			tree.setSelectionRow(row);
		    } catch (Exception e) {
			System.err.println("Editing exception: " + e);
		    }
		    stopEditingInCompleteEditing = true;
		}

		BasicLookAndFeel.compositeRequestFocus(editingComponent);
		
		if(event != null && event instanceof MouseEvent) {
		    /* Find the component that will get forwarded all the
		       mouse events until mouseReleased. */
		    Point          componentPoint = SwingUtilities.convertPoint
			(tree, new Point(event.getX(), event.getY()),
			 editingComponent);

		    /* Create an instance of BasicTreeMouseListener to handle
		       passing the mouse/motion events to the necessary
		       component. */
		    // We really want similar behavior to getMouseEventTarget,
		    // but it is package private.
		    Component activeComponent = SwingUtilities.
			            getDeepestComponentAt(editingComponent,
				       componentPoint.x, componentPoint.y);
		    if (activeComponent != null) {
			new MouseInputHandler(tree, activeComponent, event);
		    }
		}
		return true;
	    }
	    else
		editingComponent = null;
	}
	return false;
    }

    //
    // Following are primarily for handling mouse events.
    //

    /**
     * If the <code>mouseX</code> and <code>mouseY</code> are in the
     * expand/collapse region of the <code>row</code>, this will toggle
     * the row.
     */
    protected void checkForClickInExpandControl(TreePath path,
						int mouseX, int mouseY) {
      if (isLocationInExpandControl(path, mouseX, mouseY)) {
	  handleExpandControlClick(path, mouseX, mouseY);
	}
    }

    /**
     * Returns true if <code>mouseX</code> and <code>mouseY</code> fall
     * in the area of row that is used to expand/collapse the node and
     * the node at <code>row</code> does not represent a leaf.
     */
    protected boolean isLocationInExpandControl(TreePath path, 
						int mouseX, int mouseY) {
	if(path != null && !treeModel.isLeaf(path.getLastPathComponent())){
	    int                     boxWidth;
	    Insets                  i = tree.getInsets();

	    if(getExpandedIcon() != null)
		boxWidth = getExpandedIcon().getIconWidth();
	    else
		boxWidth = 8;

	    int                     boxLeftX = (i != null) ? i.left : 0;

	    if (leftToRight) {
	        boxLeftX += (((path.getPathCount() + depthOffset - 2) *
			      totalChildIndent) + getLeftChildIndent()) -
		              boxWidth / 2;
	    }
	    else {
	        boxLeftX += lastWidth - 1 - 
		            ((path.getPathCount() - 2 + depthOffset) *
			     totalChildIndent) - getLeftChildIndent() -
		            boxWidth / 2;
	    }
	    int boxRightX = boxLeftX + boxWidth;

	    return mouseX >= boxLeftX && mouseX <= boxRightX;
	}
	return false;
    }

    /**
     * Messaged when the user clicks the particular row, this invokes
     * toggleExpandState.
     */
    protected void handleExpandControlClick(TreePath path, int mouseX,
					    int mouseY) {
	toggleExpandState(path);
    }

    /**
     * Expands path if it is not expanded, or collapses row if it is expanded.
     * If expanding a path and JTree scrolls on expand, ensureRowsAreVisible
     * is invoked to scroll as many of the children to visible as possible
     * (tries to scroll to last visible descendant of path).
     */
    protected void toggleExpandState(TreePath path) {
	if(!tree.isExpanded(path)) {
	    int       row = getRowForPath(tree, path);

	    tree.expandPath(path);
	    updateSize();
	    if(row != -1) {
		if(tree.getScrollsOnExpand())
		    ensureRowsAreVisible(row, row + treeState.
					 getVisibleChildCount(path));
		else
		    ensureRowsAreVisible(row, row);
	    }
	}
	else {
	    tree.collapsePath(path);
	    updateSize();
	}
    }

    /**
     * Returning true signifies a mouse event on the node should toggle
     * the selection of only the row under mouse.
     */
    protected boolean isToggleSelectionEvent(MouseEvent event) {
	return (SwingUtilities.isLeftMouseButton(event) &&
		event.isControlDown());
    }

    /**
     * Returning true signifies a mouse event on the node should select
     * from the anchor point.
     */
    protected boolean isMultiSelectEvent(MouseEvent event) {
	return (SwingUtilities.isLeftMouseButton(event) &&
		event.isShiftDown());
    }

    /**
     * Returning true indicates the row under the mouse should be toggled
     * based on the event. This is invoked after checkForClickInExpandControl,
     * implying the location is not in the expand (toggle) control
     */
    protected boolean isToggleEvent(MouseEvent event) {
	if(!SwingUtilities.isLeftMouseButton(event)) {
	    return false;
	}
	int           clickCount = tree.getToggleClickCount();

	if(clickCount <= 0) {
	    return false;
	}
	return (event.getClickCount() == clickCount);
    }

    /**
     * Messaged to update the selection based on a MouseEvent over a
     * particular row. If the event is a toggle selection event, the
     * row is either selected, or deselected. If the event identifies
     * a multi selection event, the selection is updated from the
     * anchor point. Otherwise the row is selected, and if the event
     * specified a toggle event the row is expanded/collapsed.
     */
    protected void selectPathForEvent(TreePath path, MouseEvent event) {
	// Should this event toggle the selection of this row?
	/* Control toggles just this node. */
	if(isToggleSelectionEvent(event)) {
	    if(tree.isPathSelected(path))
		tree.removeSelectionPath(path);
	    else
		tree.addSelectionPath(path);
	    lastSelectedRow = getRowForPath(tree, path);
	    setAnchorSelectionPath(path);
	    setLeadSelectionPath(path);
	}
	/* Adjust from the anchor point. */
	else if(isMultiSelectEvent(event)) {
	    TreePath    anchor = getAnchorSelectionPath();
	    int         anchorRow = (anchor == null) ? -1 :
		                    getRowForPath(tree, anchor);

	    if(anchorRow == -1 || tree.getSelectionModel().
                      getSelectionMode() == TreeSelectionModel.
                      SINGLE_TREE_SELECTION) {
		tree.setSelectionPath(path);
	    }
	    else {
		int          row = getRowForPath(tree, path);
		TreePath     lastAnchorPath = anchor;

		if(row < anchorRow)
		    tree.setSelectionInterval(row, anchorRow);
		else
		    tree.setSelectionInterval(anchorRow, row);
		lastSelectedRow = row;
		setAnchorSelectionPath(lastAnchorPath);
		setLeadSelectionPath(path);
	    }
	}
	/* Otherwise set the selection to just this interval. */
	else if(SwingUtilities.isLeftMouseButton(event)) {
	    tree.setSelectionPath(path);
	    if(isToggleEvent(event)) {
		toggleExpandState(path);
	    }
	}
    }

    /**
     * @return true if the node at <code>row</code> is a leaf.
     */
    protected boolean isLeaf(int row) {
	TreePath          path = getPathForRow(tree, row);

	if(path != null)
	    return treeModel.isLeaf(path.getLastPathComponent());
	// Have to return something here...
	return true;
    }

    //
    // The following selection methods (lead/anchor) are covers for the
    // methods in JTree.
    //
    private void setAnchorSelectionPath(TreePath newPath) {
	ignoreLAChange = true;
	try {
	    tree.setAnchorSelectionPath(newPath);
	} finally{ 
	    ignoreLAChange = false;
	}
    }

    private TreePath getAnchorSelectionPath() {
	return tree.getAnchorSelectionPath();
    }

    private void setLeadSelectionPath(TreePath newPath) {
	setLeadSelectionPath(newPath, false);
    }

    private void setLeadSelectionPath(TreePath newPath, boolean repaint) {
	Rectangle       bounds = repaint ?
	                    getPathBounds(tree, getLeadSelectionPath()) : null;

	ignoreLAChange = true;
	try {
	    tree.setLeadSelectionPath(newPath);
	} finally {
	    ignoreLAChange = false;
	}
	leadRow = getRowForPath(tree, newPath);

	if(repaint) {
	    if(bounds != null)
		tree.repaint(bounds);
	    bounds = getPathBounds(tree, newPath);
	    if(bounds != null)
		tree.repaint(bounds);
	}
    }

    private TreePath getLeadSelectionPath() {
	return tree.getLeadSelectionPath();
    }

    private void updateLeadRow() {
	leadRow = getRowForPath(tree, getLeadSelectionPath());
    }

    private int getLeadSelectionRow() {
	return leadRow;
    }

    /**
     * Extends the selection from the anchor to make <code>newLead</code>
     * the lead of the selection. This does not scroll.
     */
    private void extendSelection(TreePath newLead) {
	TreePath           aPath = getAnchorSelectionPath();
	int                aRow = (aPath == null) ? -1 :
			          getRowForPath(tree, aPath);
	int                newIndex = getRowForPath(tree, newLead);

	if(aRow == -1) {
	    tree.setSelectionRow(newIndex);
	}
	else {
	    if(aRow < newIndex) {
		tree.setSelectionInterval(aRow, newIndex);
	    }
	    else {
		tree.setSelectionInterval(newIndex, aRow);
	    }
	    setAnchorSelectionPath(aPath);
	    setLeadSelectionPath(newLead);
	}
    }

    /**
     * Invokes <code>repaint</code> on the JTree for the passed in TreePath,
     * <code>path</code>.
     */
    private void repaintPath(TreePath path) {
	if (path != null) {
	    Rectangle bounds = getPathBounds(tree, path);
	    if (bounds != null) {
		tree.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
	    }
	}
    }

    /**
     * Updates the TreeState in response to nodes expanding/collapsing.
     */
    public class TreeExpansionHandler implements TreeExpansionListener {
	/**
	 * Called whenever an item in the tree has been expanded.
	 */
	public void treeExpanded(TreeExpansionEvent event) {
	    if(event != null && tree != null) {
		TreePath      path = event.getPath();

		updateExpandedDescendants(path);
	    }
	}

	/**
	 * Called whenever an item in the tree has been collapsed.
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
	    if(event != null && tree != null) {
		TreePath        path = event.getPath();

		completeEditing();
		if(path != null && tree.isVisible(path)) {
		    treeState.setExpandedState(path, false);
		    updateLeadRow();
		    updateSize();
		}
	    }
	}
    } // BasicTreeUI.TreeExpansionHandler


    /**
     * Updates the preferred size when scrolling (if necessary).
     */
    public class ComponentHandler extends ComponentAdapter implements
                 ActionListener {
	/** Timer used when inside a scrollpane and the scrollbar is
	 * adjusting. */
	protected Timer                timer;
	/** ScrollBar that is being adjusted. */
	protected JScrollBar           scrollBar;

	public void componentMoved(ComponentEvent e) {
	    if(timer == null) {
		JScrollPane   scrollPane = getScrollPane();

		if(scrollPane == null)
		    updateSize();
		else {
		    scrollBar = scrollPane.getVerticalScrollBar();
		    if(scrollBar == null || 
			!scrollBar.getValueIsAdjusting()) {
			// Try the horizontal scrollbar.
			if((scrollBar = scrollPane.getHorizontalScrollBar())
			    != null && scrollBar.getValueIsAdjusting())
			    startTimer();
			else
			    updateSize();
		    }
		    else
			startTimer();
		}
	    }
	}

	/**
	 * Creates, if necessary, and starts a Timer to check if need to
	 * resize the bounds.
	 */
	protected void startTimer() {
	    if(timer == null) {
		timer = new Timer(200, this);
		timer.setRepeats(true);
	    }
	    timer.start();
	}

	/**
	 * Returns the JScrollPane housing the JTree, or null if one isn't
	 * found.
	 */
	protected JScrollPane getScrollPane() {
	    Component       c = tree.getParent();

	    while(c != null && !(c instanceof JScrollPane))
		c = c.getParent();
	    if(c instanceof JScrollPane)
		return (JScrollPane)c;
	    return null;
	}

	/**
	 * Public as a result of Timer. If the scrollBar is null, or
	 * not adjusting, this stops the timer and updates the sizing.
	 */
	public void actionPerformed(ActionEvent ae) {
	    if(scrollBar == null || !scrollBar.getValueIsAdjusting()) {
		if(timer != null)
		    timer.stop();
		updateSize();
		timer = null;
		scrollBar = null;
	    }
	}
    } // End of BasicTreeUI.ComponentHandler


    /**
     * Forwards all TreeModel events to the TreeState.
     */
    public class TreeModelHandler implements TreeModelListener {

	public void treeNodesChanged(TreeModelEvent e) {
	    if(treeState != null && e != null) {
		treeState.treeNodesChanged(e);

		TreePath       pPath = e.getTreePath().getParentPath();

		if(pPath == null || treeState.isExpanded(pPath))
		    updateSize();
	    }
	}

	public void treeNodesInserted(TreeModelEvent e) {
	    if(treeState != null && e != null) {
		treeState.treeNodesInserted(e);

		updateLeadRow();

		TreePath       path = e.getTreePath();

		if(treeState.isExpanded(path)) {
		    updateSize();
		}
		else {
		    // PENDING(sky): Need a method in TreeModelEvent
		    // that can return the count, getChildIndices allocs
		    // a new array!
		    int[]      indices = e.getChildIndices();
		    int        childCount = treeModel.getChildCount
			                    (path.getLastPathComponent());

		    if(indices != null && (childCount - indices.length) == 0)
			updateSize();
		}
	    }
	}

	public void treeNodesRemoved(TreeModelEvent e) {
	    if(treeState != null && e != null) {
		treeState.treeNodesRemoved(e);

		updateLeadRow();

		TreePath       path = e.getTreePath();

		if(treeState.isExpanded(path) ||
		   treeModel.getChildCount(path.getLastPathComponent()) == 0)
		    updateSize();
	    }
	}

	public void treeStructureChanged(TreeModelEvent e) {
	    if(treeState != null && e != null) {
		treeState.treeStructureChanged(e);

		updateLeadRow();

		TreePath       pPath = e.getTreePath();

                if (pPath != null) {
                    pPath = pPath.getParentPath();
                }
                if(pPath == null || treeState.isExpanded(pPath))
                    updateSize();
	    }
	}
    } // End of BasicTreeUI.TreeModelHandler


    /**
     * Listens for changes in the selection model and updates the display
     * accordingly.
     */
    public class TreeSelectionHandler implements TreeSelectionListener {
	/**
	 * Messaged when the selection changes in the tree we're displaying
	 * for.  Stops editing, messages super and displays the changed paths.
	 */
	public void valueChanged(TreeSelectionEvent event) {
	    // Stop editing
	    completeEditing();
	    // Make sure all the paths are visible, if necessary.
            // PENDING: This should be tweaked when isAdjusting is added
	    if(tree.getExpandsSelectedPaths() && treeSelectionModel != null) {
		TreePath[]           paths = treeSelectionModel
		                         .getSelectionPaths();

		if(paths != null) {
		    for(int counter = paths.length - 1; counter >= 0;
			counter--) {
                        TreePath path = paths[counter].getParentPath();
                        boolean expand = true;

                        while (path != null) {
                            // Indicates this path isn't valid anymore,
                            // we shouldn't attempt to expand it then.
                            if (treeModel.isLeaf(path.getLastPathComponent())){
                                expand = false;
                                path = null;
                            }
                            else {
                                path = path.getParentPath();
                            }
                        }
                        if (expand) {
                            tree.makeVisible(paths[counter]);
                        }
		    }
		}
	    }

	    TreePath oldLead = getLeadSelectionPath();
	    lastSelectedRow = tree.getMinSelectionRow();
	    TreePath lead = tree.getSelectionModel().getLeadSelectionPath();
	    setAnchorSelectionPath(lead);
	    setLeadSelectionPath(lead);

	    TreePath[]       changedPaths = event.getPaths();
	    Rectangle        nodeBounds;
	    Rectangle        visRect = tree.getVisibleRect();
	    boolean          paintPaths = true;
	    int              nWidth = tree.getWidth();

	    if(changedPaths != null) {
		int              counter, maxCounter = changedPaths.length;

		if(maxCounter > 4) {
		    tree.repaint();
		    paintPaths = false;
		}
		else {
		    for (counter = 0; counter < maxCounter; counter++) {
			nodeBounds = getPathBounds(tree,
						   changedPaths[counter]);
			if(nodeBounds != null &&
			   visRect.intersects(nodeBounds))
			    tree.repaint(0, nodeBounds.y, nWidth,
					 nodeBounds.height);
		    }
		}
	    }
	    if(paintPaths) {
		nodeBounds = getPathBounds(tree, oldLead);
		if(nodeBounds != null && visRect.intersects(nodeBounds))
		    tree.repaint(0, nodeBounds.y, nWidth, nodeBounds.height);
		nodeBounds = getPathBounds(tree, lead);
		if(nodeBounds != null && visRect.intersects(nodeBounds))
		    tree.repaint(0, nodeBounds.y, nWidth, nodeBounds.height);
	    }
	}
    }// End of BasicTreeUI.TreeSelectionHandler


    /**
     * Listener responsible for getting cell editing events and updating
     * the tree accordingly.
     */
    public class CellEditorHandler implements CellEditorListener {
	/** Messaged when editing has stopped in the tree. */
	public void editingStopped(ChangeEvent e) {
	    completeEditing(false, false, true);
	}

	/** Messaged when editing has been canceled in the tree. */
	public void editingCanceled(ChangeEvent e) {
	    completeEditing(false, false, false);
	}
    } // BasicTreeUI.CellEditorHandler


    /**
     * This is used to get mutliple key down events to appropriately generate
     * events.
     */
    // PENDING(sky): Is this still needed?
    public class KeyHandler extends KeyAdapter {
	/** Key code that is being generated for. */
	protected Action              repeatKeyAction;

	/** Set to true while keyPressed is active. */
	protected boolean            isKeyDown;

	/**
	 * Invoked when a key has been typed.
	 * 
	 * Moves the keyboard focus to the first element
	 * whose first letter matches the alphanumeric key 
	 * pressed by the user. Subsequent same key presses 
	 * move the keyboard focus to the next object that 
	 * starts with the same letter.
	 */
	public void keyTyped(KeyEvent e) {
	    // handle first letter navigation
	    if(tree != null && tree.getRowCount()>0 && tree.hasFocus() && tree.isEnabled()) {
		if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
		    return;
		}
		boolean startingFromSelection = true;

		char [] c = new char[1];
		c[0] = e.getKeyChar();
		String prefix = new String(c);

		int startingRow = tree.getMinSelectionRow() + 1;
		if (startingRow < 0 || startingRow >= tree.getRowCount()) {
		    startingFromSelection = false;
		    startingRow = 0;
		}
		TreePath path = tree.getNextMatch(prefix, startingRow,
						  Position.Bias.Forward);
		if (path != null) {
                    tree.setSelectionPath(path);
		} else if (startingFromSelection) {
		    path = tree.getNextMatch(prefix, 0,
					     Position.Bias.Forward);
		    if (path != null) {
			tree.setSelectionPath(path);
		    }
		}
	    }
	}

	public void keyPressed(KeyEvent e) {
	    if(tree != null && tree.hasFocus() && tree.isEnabled()) {
		KeyStroke       keyStroke = KeyStroke.getKeyStroke
		                     (e.getKeyCode(), e.getModifiers());

		if(tree.getConditionForKeyStroke(keyStroke) ==
		   JComponent.WHEN_FOCUSED) {
		    ActionListener     listener = tree.
		                           getActionForKeyStroke(keyStroke);

		    if(listener instanceof Action) {
			repeatKeyAction = (Action)listener;
                        if (!repeatKeyAction.isEnabled()) {
                            repeatKeyAction = null;
                        }
                    }
		    else
			repeatKeyAction = null;
		}
		else
		    repeatKeyAction = null;
		if(isKeyDown && repeatKeyAction != null) {
		    repeatKeyAction.actionPerformed
                        (new ActionEvent(tree, ActionEvent.ACTION_PERFORMED,
                                         "" /* tree.getActionCommand() */,
                                         e.getWhen(), e.getModifiers()));
		    e.consume();
		}
		else
		    isKeyDown = true;
	    }
	}

	public void keyReleased(KeyEvent e) {
	    isKeyDown = false;
	}

    } // End of BasicTreeUI.KeyHandler


    /**
     * Repaints the lead selection row when focus is lost/gained.
     */
    public class FocusHandler implements FocusListener {
	/**
	 * Invoked when focus is activated on the tree we're in, redraws the
	 * lead row.
	 */
	public void focusGained(FocusEvent e) {
	    if(tree != null) {
		Rectangle                 pBounds;

		pBounds = getPathBounds(tree, tree.getLeadSelectionPath());
		if(pBounds != null)
		    tree.repaint(pBounds);
		pBounds = getPathBounds(tree, getLeadSelectionPath());
		if(pBounds != null)
		    tree.repaint(pBounds);
	    }
	}

	/**
	 * Invoked when focus is activated on the tree we're in, redraws the
	 * lead row.
	 */
	public void focusLost(FocusEvent e) {
	    focusGained(e);
	}
    } // End of class BasicTreeUI.FocusHandler


    /**
     * Class responsible for getting size of node, method is forwarded
     * to BasicTreeUI method. X location does not include insets, that is
     * handled in getPathBounds.
     */
    // This returns locations that don't include any Insets.
    public class NodeDimensionsHandler extends
	         AbstractLayoutCache.NodeDimensions {
	/**
	 * Responsible for getting the size of a particular node.
	 */
	public Rectangle getNodeDimensions(Object value, int row,
					   int depth, boolean expanded,
					   Rectangle size) {
	    // Return size of editing component, if editing and asking
	    // for editing row.
	    if(editingComponent != null && editingRow == row) {
		Dimension        prefSize = editingComponent.
		                              getPreferredSize();
		int              rh = getRowHeight();

		if(rh > 0 && rh != prefSize.height)
		    prefSize.height = rh;
		if(size != null) {
		    size.x = getRowX(row, depth);
		    size.width = prefSize.width;
		    size.height = prefSize.height;
		}
		else {
		    size = new Rectangle(getRowX(row, depth), 0,
					 prefSize.width, prefSize.height);
		}

		if(!leftToRight) {
		    size.x = lastWidth - size.width - size.x - 2;
		}
		return size;
	    }
	    // Not editing, use renderer.
	    if(currentCellRenderer != null) {
		Component          aComponent;

		aComponent = currentCellRenderer.getTreeCellRendererComponent
		    (tree, value, tree.isRowSelected(row),
		     expanded, treeModel.isLeaf(value), row,
		     false);
		if(tree != null) {
		    // Only ever removed when UI changes, this is OK!
		    rendererPane.add(aComponent);
		    aComponent.validate();
		}
		Dimension        prefSize = aComponent.getPreferredSize();

		if(size != null) {
		    size.x = getRowX(row, depth);
		    size.width = prefSize.width;
		    size.height = prefSize.height;
		}
		else {
		    size = new Rectangle(getRowX(row, depth), 0,
					 prefSize.width, prefSize.height);
		}

		if(!leftToRight) {
		    size.x = lastWidth - size.width - size.x - 2;
		}
		return size;
	    }
	    return null;
	}

	/**
	 * @return amount to indent the given row.
	 */
	protected int getRowX(int row, int depth) {
	    return totalChildIndent * (depth + depthOffset);
	}

    } // End of class BasicTreeUI.NodeDimensionsHandler


    /**
     * TreeMouseListener is responsible for updating the selection
     * based on mouse events.
     */
    public class MouseHandler extends MouseAdapter implements MouseMotionListener
 {
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {
	    if (! e.isConsumed()) {
		handleSelection(e);
		selectedOnPress = true;
	    } else {
		selectedOnPress = false;
	    }
	}

        void handleSelection(MouseEvent e) {
	    if(tree != null && tree.isEnabled()) {
                if (isEditing(tree) && tree.getInvokesStopCellEditing() &&
                                       !stopEditing(tree)) {
                    return;
                }

                if (tree.isRequestFocusEnabled()) {
		    tree.requestFocus();
                }
		TreePath     path = getClosestPathForLocation(tree, e.getX(),
							      e.getY());

		if(path != null) {
		    Rectangle       bounds = getPathBounds(tree, path);

		    if(e.getY() > (bounds.y + bounds.height)) {
			return;
		    }

		    // Preferably checkForClickInExpandControl could take
		    // the Event to do this it self!
		    if(SwingUtilities.isLeftMouseButton(e))
			checkForClickInExpandControl(path, e.getX(), e.getY());
		    
		    int x = e.getX();
		    
		    // Perhaps they clicked the cell itself. If so,
		    // select it.
		    if (x > bounds.x) {
			if (x <= (bounds.x + bounds.width) && 
			    !startEditing(path, e)) {
			    selectPathForEvent(path, e);
			}
		    }
		    // PENDING: Should select on mouse down, start a drag if
		    // the mouse moves, and fire selection change notice on
		    // mouse up. That is, the explorer highlights on mouse
		    // down, but doesn't update the pane to the right (and
		    // open the folder icon) until mouse up.
		}
	    }
	}

        public void mouseDragged(MouseEvent e) {
	}

        /**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 */
        public void mouseMoved(MouseEvent e) {
	}

        public void mouseReleased(MouseEvent e) {
	    if ((! e.isConsumed()) && (! selectedOnPress)) {
		handleSelection(e);
	    }
        }

        boolean selectedOnPress;
    } // End of BasicTreeUI.MouseHandler


    /**
     * PropertyChangeListener for the tree. Updates the appropriate
     * varaible, or TreeState, based on what changes.
     */
    public class PropertyChangeHandler implements
	               PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent event) {
	    if(event.getSource() == tree) {
		String              changeName = event.getPropertyName();

		if (changeName.equals(JTree.LEAD_SELECTION_PATH_PROPERTY)) {
		    if (!ignoreLAChange) {
			updateLeadRow();
			repaintPath((TreePath)event.getOldValue());
			repaintPath((TreePath)event.getNewValue());
		    }
		}
		else if (changeName.equals(JTree.
					   ANCHOR_SELECTION_PATH_PROPERTY)) {
		    if (!ignoreLAChange) {
			repaintPath((TreePath)event.getOldValue());
			repaintPath((TreePath)event.getNewValue());
		    }
		}
		if(changeName.equals(JTree.CELL_RENDERER_PROPERTY)) {
		    setCellRenderer((TreeCellRenderer)event.getNewValue());
		    redoTheLayout();
		}
		else if(changeName.equals(JTree.TREE_MODEL_PROPERTY)) {
		    setModel((TreeModel)event.getNewValue());
		}
		else if(changeName.equals(JTree.ROOT_VISIBLE_PROPERTY)) {
		    setRootVisible(((Boolean)event.getNewValue()).
				   booleanValue());
		}
		else if(changeName.equals(JTree.SHOWS_ROOT_HANDLES_PROPERTY)) {
		    setShowsRootHandles(((Boolean)event.getNewValue()).
					booleanValue());
		}
		else if(changeName.equals(JTree.ROW_HEIGHT_PROPERTY)) {
		    setRowHeight(((Integer)event.getNewValue()).
				 intValue());
		}
		else if(changeName.equals(JTree.CELL_EDITOR_PROPERTY)) {
		    setCellEditor((TreeCellEditor)event.getNewValue());
		}
		else if(changeName.equals(JTree.EDITABLE_PROPERTY)) {
		    setEditable(((Boolean)event.getNewValue()).booleanValue());
		}
		else if(changeName.equals(JTree.LARGE_MODEL_PROPERTY)) {
		    setLargeModel(tree.isLargeModel());
		}
		else if(changeName.equals(JTree.SELECTION_MODEL_PROPERTY)) {
		    setSelectionModel(tree.getSelectionModel());
		}
		else if(changeName.equals("font")) {
		    completeEditing();
		    if(treeState != null)
			treeState.invalidateSizes();
		    updateSize();
		}
		else if (changeName.equals("componentOrientation")) {
		    if (tree != null) {
			leftToRight = BasicGraphicsUtils.isLeftToRight(tree);
			redoTheLayout();
			tree.treeDidChange();

			InputMap km = getInputMap(JComponent.WHEN_FOCUSED);
			SwingUtilities.replaceUIInputMap(tree,
						JComponent.WHEN_FOCUSED, km);
		    }
                } else if ("transferHandler".equals(changeName)) {
                    DropTarget dropTarget = tree.getDropTarget();
                    if (dropTarget instanceof UIResource) {
                        if (defaultDropTargetListener == null) {
                            defaultDropTargetListener = new TreeDropTargetListener();
                        }
                        try {
                            dropTarget.addDropTargetListener(defaultDropTargetListener);
                        } catch (TooManyListenersException tmle) {
                            // should not happen... swing drop target is multicast
                        }
                    }
		}
	    }
	}
    } // End of BasicTreeUI.PropertyChangeHandler


    /**
     * Listener on the TreeSelectionModel, resets the row selection if
     * any of the properties of the model change.
     */
    public class SelectionModelPropertyChangeHandler implements
	              PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent event) {
	    if(event.getSource() == treeSelectionModel)
		treeSelectionModel.resetRowSelection();
	}
    } // End of BasicTreeUI.SelectionModelPropertyChangeHandler


    /**
     * <code>TreeTraverseAction</code> is the action used for left/right keys.
     * Will toggle the expandedness of a node, as well as potentially
     * incrementing the selection.
     */
    public class TreeTraverseAction extends AbstractAction {
	/** Determines direction to traverse, 1 means expand, -1 means
	  * collapse. */
	protected int direction;
	/** True if the selection is reset, false means only the lead path
	 * changes. */
	private boolean changeSelection;

	public TreeTraverseAction(int direction, String name) {
	    this(direction, name, true);
	}

	private TreeTraverseAction(int direction, String name,
				   boolean changeSelection) {
	    this.direction = direction;
	    this.changeSelection = changeSelection;
	}

	public void actionPerformed(ActionEvent e) {
	    int                rowCount;

	    if(tree != null && (rowCount = getRowCount(tree)) > 0) {
		int               minSelIndex = getLeadSelectionRow();
		int               newIndex;

		if(minSelIndex == -1)
		    newIndex = 0;
		else {
		    /* Try and expand the node, otherwise go to next
		       node. */
		    if(direction == 1) {
			if(!isLeaf(minSelIndex) &&
			   !tree.isExpanded(minSelIndex)) {
			    toggleExpandState(getPathForRow
					      (tree, minSelIndex));
			    newIndex = -1;
			}
			else
			    newIndex = Math.min(minSelIndex + 1, rowCount - 1);
		    }
		    /* Try to collapse node. */
		    else {
			if(!isLeaf(minSelIndex) &&
			   tree.isExpanded(minSelIndex)) {
			    toggleExpandState(getPathForRow
					      (tree, minSelIndex));
			    newIndex = -1;
			}
			else {
			    TreePath         path = getPathForRow(tree,
								  minSelIndex);

			    if(path != null && path.getPathCount() > 1) {
				newIndex = getRowForPath(tree, path.
							 getParentPath());
			    }
			    else
				newIndex = -1;
			}
		    }
		}
		if(newIndex != -1) {
		    if(changeSelection) {
			tree.setSelectionInterval(newIndex, newIndex);
		    }
		    else {
			setLeadSelectionPath(getPathForRow(tree, newIndex),
					     true);
		    }
		    ensureRowsAreVisible(newIndex, newIndex);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }
    } // BasicTreeUI.TreeTraverseAction


    /** TreePageAction handles page up and page down events.
      */
    public class TreePageAction extends AbstractAction {
	/** Specifies the direction to adjust the selection by. */
	protected int         direction;
	/** True indicates should set selection from anchor path. */
	private boolean       addToSelection;
	private boolean       changeSelection;

	public TreePageAction(int direction, String name) {
	    this(direction, name, false, true);
	}

	private TreePageAction(int direction, String name,
			       boolean addToSelection,
			       boolean changeSelection) {
	    this.direction = direction;
	    this.addToSelection = addToSelection;
	    this.changeSelection = changeSelection;
	}

	public void actionPerformed(ActionEvent e) {
	    int           rowCount;

	    if(tree != null && (rowCount = getRowCount(tree)) > 0 &&
		treeSelectionModel != null) {
		Dimension         maxSize = tree.getSize();
		TreePath          lead = getLeadSelectionPath();
		TreePath          newPath;
		Rectangle         visRect = tree.getVisibleRect();

		if(direction == -1) {
		    // up.
		    newPath = getClosestPathForLocation(tree, visRect.x,
							 visRect.y);
		    if(newPath.equals(lead)) {
			visRect.y = Math.max(0, visRect.y - visRect.height);
			newPath = tree.getClosestPathForLocation(visRect.x,
								 visRect.y);
		    }
		}
		else {
		    // down
		    visRect.y = Math.min(maxSize.height, visRect.y +
					 visRect.height - 1);
		    newPath = tree.getClosestPathForLocation(visRect.x,
							     visRect.y);
		    if(newPath.equals(lead)) {
			visRect.y = Math.min(maxSize.height, visRect.y +
					     visRect.height - 1);
			newPath = tree.getClosestPathForLocation(visRect.x,
								 visRect.y);
		    }
		}
		Rectangle            newRect = getPathBounds(tree, newPath);

		newRect.x = visRect.x;
		newRect.width = visRect.width;
		if(direction == -1) {
		    newRect.height = visRect.height;
		}
		else {
		    newRect.y -= (visRect.height - newRect.height);
		    newRect.height = visRect.height;
		}

		if(addToSelection) {
		    extendSelection(newPath);
		}
		else if(changeSelection) {
		    tree.setSelectionPath(newPath);
		}
		else {
		    setLeadSelectionPath(newPath, true);
		}
		tree.scrollRectToVisible(newRect);
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }

    } // BasicTreeUI.TreePageAction

    /**
     * Scrolls the tree left/right the visible width of the tree. Will select
     * either the first/last visible node.
     */
    // Will be made public later.
    private class TreeScrollLRAction extends AbstractAction {
	/** Specifies the direction to adjust the selection by. */
	protected int         direction;
	private boolean       addToSelection;
	private boolean       changeSelection;

	TreeScrollLRAction(int direction, String name, boolean addToSelection,
			   boolean changeSelection) {
	    this.direction = direction;
	    this.addToSelection = addToSelection;
	    this.changeSelection = changeSelection;
	}

	public void actionPerformed(ActionEvent e) {
	    int           rowCount;

	    if(tree != null && (rowCount = getRowCount(tree)) > 0 &&
		treeSelectionModel != null) {
		TreePath          newPath;
		Rectangle         visRect = tree.getVisibleRect();

		if (direction == -1) {
		    newPath = getClosestPathForLocation(tree, visRect.x,
							visRect.y);
		    visRect.x = Math.max(0, visRect.x - visRect.width);
		}
		else {
		    visRect.x = Math.min(Math.max(0, tree.getWidth() -
				   visRect.width), visRect.x + visRect.width);
		    newPath = getClosestPathForLocation(tree, visRect.x,
						 visRect.y + visRect.height);
		}
		// Scroll
		tree.scrollRectToVisible(visRect);
		// select
		if (addToSelection) {
		    extendSelection(newPath);
		}
		else if(changeSelection) {
		    tree.setSelectionPath(newPath);
		}
		else {
		    setLeadSelectionPath(newPath, true);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }

    } // End of BasicTreeUI.TreeScrollLRAction

    /** TreeIncrementAction is used to handle up/down actions.  Selection
      * is moved up or down based on direction.
      */
    public class TreeIncrementAction extends AbstractAction  {
	/** Specifies the direction to adjust the selection by. */
	protected int         direction;
	/** If true the new item is added to the selection, if false the
	 * selection is reset. */
	private boolean       addToSelection;
	private boolean       changeSelection;

	public TreeIncrementAction(int direction, String name) {
	    this(direction, name, false, true);
	}

	private TreeIncrementAction(int direction, String name,
				   boolean addToSelection,
				    boolean changeSelection) {
	    this.direction = direction;
	    this.addToSelection = addToSelection;
	    this.changeSelection = changeSelection;
	}

	public void actionPerformed(ActionEvent e) {
	    int              rowCount;

	    if(tree != null && treeSelectionModel != null &&
		(rowCount = getRowCount(tree)) > 0) {
		int                  selIndex = getLeadSelectionRow();
		int                  newIndex;

		if(selIndex == -1) {
		    if(direction == 1)
			newIndex = 0;
		    else
			newIndex = rowCount - 1;
		}
		else
		    /* Aparently people don't like wrapping;( */
		    newIndex = Math.min(rowCount - 1, Math.max
					(0, (selIndex + direction)));
		if(addToSelection && treeSelectionModel.getSelectionMode() !=
                          TreeSelectionModel.SINGLE_TREE_SELECTION) {
		    extendSelection(getPathForRow(tree, newIndex));
		}
		else if(changeSelection) {
		    tree.setSelectionInterval(newIndex, newIndex);
		}
		else {
		    setLeadSelectionPath(getPathForRow(tree, newIndex), true);
		}
		ensureRowsAreVisible(newIndex, newIndex);
		lastSelectedRow = newIndex;
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }

    } // End of class BasicTreeUI.TreeIncrementAction

    /**
      * TreeHomeAction is used to handle end/home actions.
      * Scrolls either the first or last cell to be visible based on
      * direction.
      */
    public class TreeHomeAction extends AbstractAction {
	protected int            direction;
	/** Set to true if append to selection. */
	private boolean          addToSelection;
	private boolean          changeSelection;

	public TreeHomeAction(int direction, String name) {
	    this(direction, name, false, true);
	}

	private TreeHomeAction(int direction, String name,
			       boolean addToSelection,
			       boolean changeSelection) {
	    this.direction = direction;
	    this.changeSelection = changeSelection;
	    this.addToSelection = addToSelection;
	}

	public void actionPerformed(ActionEvent e) {
	    int                   rowCount = getRowCount(tree);

	    if(tree != null && rowCount > 0) {
		if(direction == -1) {
		    ensureRowsAreVisible(0, 0);
		    if (addToSelection) {
			TreePath        aPath = getAnchorSelectionPath();
			int             aRow = (aPath == null) ? -1 :
			                getRowForPath(tree, aPath);

			if (aRow == -1) {
			    tree.setSelectionInterval(0, 0);
			}
			else {
			    tree.setSelectionInterval(0, aRow);
			    setAnchorSelectionPath(aPath);
			    setLeadSelectionPath(getPathForRow(tree, 0));
			}
		    }
		    else if(changeSelection) {
			tree.setSelectionInterval(0, 0);
		    }
		    else {
			setLeadSelectionPath(getPathForRow(tree, 0), true);
		    }
		}
		else {
		    ensureRowsAreVisible(rowCount - 1, rowCount - 1);
		    if (addToSelection) {
			TreePath        aPath = getAnchorSelectionPath();
			int             aRow = (aPath == null) ? -1 :
			                getRowForPath(tree, aPath);

			if (aRow == -1) {
			    tree.setSelectionInterval(rowCount - 1,
						      rowCount -1);
			}
			else {
			    tree.setSelectionInterval(aRow, rowCount - 1);
			    setAnchorSelectionPath(aPath);
			    setLeadSelectionPath(getPathForRow(tree,
							       rowCount -1));
			}
		    }
		    else if(changeSelection) {
			tree.setSelectionInterval(rowCount - 1, rowCount - 1);
		    }
		    else {
			setLeadSelectionPath(getPathForRow(tree,
							  rowCount - 1), true);
		    }
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }

    } // End of class BasicTreeUI.TreeHomeAction


    /**
      * For the first selected row expandedness will be toggled.
      */
    public class TreeToggleAction extends AbstractAction {
	public TreeToggleAction(String name) {
	}

	public void actionPerformed(ActionEvent e) {
	    if(tree != null) {
		int            selRow = getLeadSelectionRow();

		if(selRow != -1 && !isLeaf(selRow)) {
		    TreePath aPath = getAnchorSelectionPath();
		    TreePath lPath = getLeadSelectionPath();

		    toggleExpandState(getPathForRow(tree, selRow));
		    setAnchorSelectionPath(aPath);
		    setLeadSelectionPath(lPath);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }

    } // End of class BasicTreeUI.TreeToggleAction

    /**
     * Scrolls the component it is created with a specified amount.
     */
    private static class ScrollAction extends AbstractAction {
	private JComponent component;
	private int direction;
	private int amount;

	public ScrollAction(JComponent component, int direction, int amount) {
	    this.component = component;
	    this.direction = direction;
	    this.amount = amount;
	}

	public void actionPerformed(ActionEvent e) {
	    Rectangle visRect = component.getVisibleRect();
	    Dimension size = component.getSize();
	    if (direction == SwingConstants.HORIZONTAL) {
		visRect.x += amount;
		visRect.x = Math.max(0, visRect.x);
		visRect.x = Math.min(Math.max(0, size.width - visRect.width),
				     visRect.x);
	    }
	    else {
		visRect.y += amount;
		visRect.y = Math.max(0, visRect.y);
		visRect.y = Math.min(Math.max(0, size.width - visRect.height),
				     visRect.y);
	    }
	    component.scrollRectToVisible(visRect);
	}
	
    }


    /**
     * ActionListener that invokes cancelEditing when action performed.
     */
    public class TreeCancelEditingAction extends AbstractAction {
	public TreeCancelEditingAction(String name) {
	}

	public void actionPerformed(ActionEvent e) {
	    if(tree != null) {
		tree.cancelEditing();
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled() &&
                                             isEditing(tree)); }
    } // End of class BasicTreeUI.TreeCancelEditingAction


    /**
     * ActionListener invoked to start editing on the leadPath.
     */
    private class TreeEditAction extends AbstractAction {
	public TreeEditAction(String name) {
	}

	public void actionPerformed(ActionEvent ae) {
	    if(tree != null && tree.isEnabled()) {
		TreePath   lead = getLeadSelectionPath();
		int        editRow = (lead != null) ?
		                     getRowForPath(tree, lead) : -1;

		if(editRow != -1) {
		    tree.startEditingAtPath(lead);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }
    } // End of BasicTreeUI.TreeEditAction


    /**
     * Action to select everything in tree.
     */
    private class TreeSelectAllAction extends AbstractAction {
	private boolean       selectAll;

	public TreeSelectAllAction(String name, boolean selectAll) {
	    this.selectAll = selectAll;
	}

	public void actionPerformed(ActionEvent ae) {
	    int                   rowCount = getRowCount(tree);

	    if(tree != null && rowCount > 0) {
		if(selectAll) {
		    TreePath      lastPath = getLeadSelectionPath();
		    TreePath      aPath = getAnchorSelectionPath();

		    if(lastPath != null && !tree.isVisible(lastPath)) {
			lastPath = null;
		    }
		    tree.setSelectionInterval(0, rowCount - 1);
		    if(lastPath != null) {
			setLeadSelectionPath(lastPath);
		    }
		    if(aPath != null && tree.isVisible(aPath)) {
			setAnchorSelectionPath(aPath);
		    }
		    else if(lastPath != null) {
			setAnchorSelectionPath(lastPath);
		    }
		}
		else {
		    TreePath      lastPath = getLeadSelectionPath();
		    TreePath      aPath = getAnchorSelectionPath();

		    tree.clearSelection();
		    setAnchorSelectionPath(aPath);
		    setLeadSelectionPath(lastPath);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }
    } // End of BasicTreeUI.TreeSelectAllAction

    /**
     * Action to select everything in tree.
     */
    private class TreeAddSelectionAction extends AbstractAction {
	private boolean       changeAnchor;

	public TreeAddSelectionAction(String name, boolean changeAnchor) {
	    this.changeAnchor = changeAnchor;
	}

	public void actionPerformed(ActionEvent ae) {
	    int                   rowCount = getRowCount(tree);

	    if(tree != null && rowCount > 0) {
		int       lead = getLeadSelectionRow();
		TreePath  aPath = getAnchorSelectionPath();
		TreePath  lPath = getLeadSelectionPath();

		if(lead == -1) {
		    lead = 0;
		}
		if(!changeAnchor) {
		    if(tree.isRowSelected(lead)) {
			tree.removeSelectionRow(lead);
			setLeadSelectionPath(lPath);
		    }
		    else {
			tree.addSelectionRow(lead);
		    }
		    setAnchorSelectionPath(aPath);
		}
		else {
		    tree.setSelectionRow(lead);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }
    } // End of BasicTreeUI.TreeAddSelectionAction


    /**
     * Action to select everything in tree.
     */
    private class TreeExtendSelectionAction extends AbstractAction {
	public TreeExtendSelectionAction(String name) {
	}

	public void actionPerformed(ActionEvent ae) {
	    if(tree != null && getRowCount(tree) > 0) {
		int       lead = getLeadSelectionRow();

		if(lead != -1) {
		    TreePath      leadP = getLeadSelectionPath();
		    TreePath      aPath = getAnchorSelectionPath();
		    int           aRow = getRowForPath(tree, aPath);

		    if(aRow == -1)
			aRow = 0;
		    tree.setSelectionInterval(aRow, lead);
		    setLeadSelectionPath(leadP);
		    setAnchorSelectionPath(aPath);
		}
	    }
	}

	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled()); }
    } // End of BasicTreeUI.TreeExtendSelectionAction


    /**
      * MouseInputHandler handles passing all mouse events,
      * including mouse motion events, until the mouse is released to
      * the destination it is constructed with. It is assumed all the
      * events are currently target at source.
      */
    // PENDING(sky): this could actually be moved into a general
    // location, no reason to be in here.
    public class MouseInputHandler extends Object implements
	             MouseInputListener
    {
	/** Source that events are coming from. */
	protected Component        source;
	/** Destination that receives all events. */
	protected Component        destination;

	public MouseInputHandler(Component source, Component destination,
	                              MouseEvent event){
	    this.source = source;
	    this.destination = destination;
	    this.source.addMouseListener(this);
	    this.source.addMouseMotionListener(this);
	    /* Dispatch the editing event! */
	    destination.dispatchEvent(SwingUtilities.convertMouseEvent
					  (source, event, destination));
	}

	public void mouseClicked(MouseEvent e) {
	    if(destination != null)
		destination.dispatchEvent(SwingUtilities.convertMouseEvent
					  (source, e, destination));
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	    if(destination != null)
		destination.dispatchEvent(SwingUtilities.convertMouseEvent
					  (source, e, destination));
	    removeFromSource();
	}

	public void mouseEntered(MouseEvent e) {
	    if (!SwingUtilities.isLeftMouseButton(e)) {
		removeFromSource();
	    }
	}
	
	public void mouseExited(MouseEvent e) {
	    if (!SwingUtilities.isLeftMouseButton(e)) {
		removeFromSource();
	    }
	}

	public void mouseDragged(MouseEvent e) {
	    if(destination != null)
		destination.dispatchEvent(SwingUtilities.convertMouseEvent
					  (source, e, destination));
	}

	public void mouseMoved(MouseEvent e) {
	    removeFromSource();
	}

	protected void removeFromSource() {
	    if(source != null) {
		source.removeMouseListener(this);
		source.removeMouseMotionListener(this);
	    }
	    source = destination = null;
	}

    } // End of class BasicTreeUI.MouseInputHandler

    private static final TreeDragGestureRecognizer defaultDragRecognizer = new TreeDragGestureRecognizer();

    /**
     * Drag gesture recognizer for JTree components
     */
    static class TreeDragGestureRecognizer extends BasicDragGestureRecognizer {

	/**
	 * Determines if the following are true:
	 * <ul>
	 * <li>the press event is located over a selection
	 * <li>the dragEnabled property is true
	 * <li>A TranferHandler is installed
	 * </ul>
	 * <p>
	 * This is implemented to check for a TransferHandler.
	 * Subclasses should perform the remaining conditions.
	 */
        protected boolean isDragPossible(MouseEvent e) {
	    if (super.isDragPossible(e)) {
		JTree tree = (JTree) this.getComponent(e);
		if (tree.getDragEnabled()) {
		    TreeUI ui = tree.getUI();
		    TreePath path = ui.getClosestPathForLocation(tree, e.getX(),
								 e.getY());
		    if ((path != null) && tree.isPathSelected(path)) {
			return true;
		    }
		}
	    }
	    return false;
	}
    }

    private static DropTargetListener defaultDropTargetListener = null;

    /**
     * A DropTargetListener to extend the default Swing handling of drop operations
     * by moving the tree selection to the nearest location to the mouse pointer.
     * Also adds autoscroll capability.
     */
    static class TreeDropTargetListener extends BasicDropTargetListener {

	/**
	 * called to save the state of a component in case it needs to
	 * be restored because a drop is not performed.
	 */
        protected void saveComponentState(JComponent comp) {
	    JTree tree = (JTree) comp;
	    selectedIndices = tree.getSelectionRows();
	}

	/**
	 * called to restore the state of a component 
	 * because a drop was not performed.
	 */
        protected void restoreComponentState(JComponent comp) {
	    JTree tree = (JTree) comp;
	    tree.setSelectionRows(selectedIndices);
	}

	/**
	 * called to set the insertion location to match the current
	 * mouse pointer coordinates.
	 */
        protected void updateInsertionLocation(JComponent comp, Point p) {
	    JTree tree = (JTree) comp;
	    BasicTreeUI ui = (BasicTreeUI) tree.getUI();
	    TreePath path = ui.getClosestPathForLocation(tree, p.x, p.y);
	    if (path != null) {
		tree.setSelectionPath(path);
	    }
	}

	private int[] selectedIndices;
    }

    private static final TransferHandler defaultTransferHandler = new TreeTransferHandler();

    static class TreeTransferHandler extends TransferHandler implements UIResource, Comparator {
	
	private JTree tree;

	/**
	 * Create a Transferable to use as the source for a data transfer.
	 *
	 * @param c  The component holding the data to be transfered.  This
	 *  argument is provided to enable sharing of TransferHandlers by
	 *  multiple components.
	 * @return  The representation of the data to be transfered. 
	 *  
	 */
        protected Transferable createTransferable(JComponent c) {
	    if (c instanceof JTree) {
		tree = (JTree) c;
		TreePath[] paths = tree.getSelectionPaths();
		
		if (paths == null || paths.length == 0) {
		    return null;
		}
		
                StringBuffer plainBuf = new StringBuffer();
                StringBuffer htmlBuf = new StringBuffer();
                
                htmlBuf.append("<html>\n<body>\n<ul>\n");
                
                TreeModel model = tree.getModel();
                TreePath lastPath = null;
                TreePath[] displayPaths = getDisplayOrderPaths(paths);

                for (int i = 0; i < displayPaths.length; i++) {
                    TreePath path = displayPaths[i];
                    
                    Object node = path.getLastPathComponent();
                    boolean leaf = model.isLeaf(node);
                    String label = getDisplayString(path, true, leaf);
                    
                    plainBuf.append(label + "\n");
                    htmlBuf.append("  <li>" + label + "\n");
                }
                
                // remove the last newline
                plainBuf.deleteCharAt(plainBuf.length() - 1);
                htmlBuf.append("</ul>\n</body>\n</html>");
                
                tree = null;
                
                return new BasicTransferable(plainBuf.toString(), htmlBuf.toString());
	    }

	    return null;
	}
	
        public int compare(Object o1, Object o2) {
            int row1 = tree.getRowForPath((TreePath)o1);
            int row2 = tree.getRowForPath((TreePath)o2);
            return row1 - row2;
        }

        String getDisplayString(TreePath path, boolean selected, boolean leaf) {
            int row = tree.getRowForPath(path);
            boolean hasFocus = tree.getLeadSelectionRow() == row;
            Object node = path.getLastPathComponent();
            return tree.convertValueToText(node, selected, tree.isExpanded(row), 
                                           leaf, row, hasFocus);
        }
        
        /**
         * Selection paths are in selection order.  The conversion to
         * HTML requires display order.  This method resorts the paths
         * to be in the display order.
         */
        TreePath[] getDisplayOrderPaths(TreePath[] paths) {
            // sort the paths to display order rather than selection order
            ArrayList selOrder = new ArrayList();
            for (int i = 0; i < paths.length; i++) {
                selOrder.add(paths[i]);
            }
            Collections.sort(selOrder, this);
            int n = selOrder.size();
            TreePath[] displayPaths = new TreePath[n];
            for (int i = 0; i < n; i++) {
                displayPaths[i] = (TreePath) selOrder.get(i);
            }
            return displayPaths;
        }

        public int getSourceActions(JComponent c) {
	    return COPY;
	}

    }

} // End of class BasicTreeUI
