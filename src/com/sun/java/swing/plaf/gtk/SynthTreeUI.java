/*
 * @(#)SynthTreeUI.java	1.18 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.plaf.ActionMapUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.*;
import javax.swing.text.Position;

/**
 * SynthTreeUI provides space for a control icon and the renderer
 * portion of a node. The leading edge of the renderer is calculated
 * by <code>Tree.indent</code> * depth - 1 + <code>Tree.controlSize</code>.
 * The icon is centered at leading renderer edge -
 * <code>Tree.trailingControlOffset</code>. The horizontal separators,
 * if drawn, are drawn from the leading edge of the renderer -
 * <code>Tree.trailingControlOffset</code> to the leading edge of
 * renderer - <code>Tree.trailingLegBufferOffset</code>.
 * <p>
 *
 * @version 1.18, 01/23/03 (based on BasicTreeUI v 1.155)
 * @author Scott Violet
 */
class SynthTreeUI extends TreeUI implements PropertyChangeListener, SynthUI,
           LazyActionMap.Loader {

    private static final TreeDragGestureRecognizer defaultDragRecognizer
        = new TreeDragGestureRecognizer();

    private static DropTargetListener defaultDropTargetListener = null;
    
    private static final TransferHandler defaultTransferHandler
        = new TreeTransferHandler();

    /**
     * Icon to use for nodes that are collapsed.
     */
    private Icon collapsedIcon;

    /**
     * Icon to use when a node is expanded.
     */
    private Icon expandedIcon;

    /**
     * The Tree!
     */
    private JTree tree;

    /**
     * Used during drawing to track which nodes have had their separators
     * drawn.
     */
    private Map drawingCache;

    /**
     * Offset, from trailing margin, to center of control icon.
     */
    private int trailingControlOffset;

    /**
     * Total size to allow to expand/collapse icon. This includes the
     * leading padding.
     */
    private int controlSize;
 
    /**
     * Pixels to indent children, this is not necessarily controlSize.
     */
    private int indent;

   /**
     * Amount of space to leave between node and horizontal separators as
     * measured from trailing margin.
     */
    private int trailingLegBufferOffset;

    /**
     * Indicates if the horizontal legs should be drawn.
     */
    private boolean drawHorizontalLegs;

    /**
     * Indicates if the vertical legs should be drawn.
     */
    private boolean drawVerticalLegs;

    /**
     * Whether or not the focus border should be drawn around the renderer.
     */
    private boolean drawsFocusBorder;

    /**
     * The offset the root should be rendered at. This isn't a pixel
     * value, rather an integer that will be offset by the getIndent.
     * The value depends upon if the root is visible, and if the root handles
     * are visible.
     */
    private int rootOffset;

    /**
     * If true, when TreeSelectionListener.valueChanged is invoked it will
     * NOT invoke stopCellEditing. This is used when the selection is changed
     * as a result of editing so that the editing session isn't canceled
     * immediately.
     */
    private boolean stopEditingWhenSelectionChanges;

    /**
     * Used to paint the TreeCellRenderer.
     */
    private CellRendererPane  rendererPane;

    /**
     * Preferred size, calculated from the AbstractLayoutCache.
     */
    private Dimension preferredSize;

    /**
     * Is the preferredSize valid?
     */
    protected boolean validCachedPreferredSize;

    /**
     * Object responsible for handling sizing and expanded issues.
     */
    private AbstractLayoutCache  treeState;

    /**
     * True if doing optimizations for a largeModel. This is used
     * to determine the type of AbstractLayoutCache to create.
     */
    private boolean largeModel;

    /**
     * Reponsible for telling the TreeState the size needed for a node.
     */
    private AbstractLayoutCache.NodeDimensions     nodeDimensions;

    /**
     * Used during editing to hold editing related state.
     */
    private EditingState editingState;

    /**
     * Indicates the orientation. This is cached as used quite extensively
     * while painting.
     */
    private boolean leftToRight;

    /**
     * Row corresponding to the lead path.
     */
    private int leadRow;

    // Cached listeners
    private MouseListener mouseListener;
    private FocusListener focusListener;
    private KeyListener keyListener;
    /**
     * Used for large models, listens for moved/resized events and
     * updates the validCachedPreferredSize bit accordingly.
     */
    private ComponentListener componentListener;
    private CellEditorListener cellEditorListener;
    private TreeSelectionListener treeSelectionListener;
    private TreeModelListener treeModelListener;
    private TreeExpansionListener treeExpansionListener;

    private SynthStyle style;
    private SynthStyle cellStyle;


    public static ComponentUI createUI(JComponent x) {
	return new SynthTreeUI();
    }

    /**
     * Ensures that the rows identified by beginRow through endRow are
     * visible.
     */
    private static void ensureRowsAreVisible(JTree tree, int beginRow,
                                             int endRow) {
	if (beginRow >= 0 && endRow < tree.getRowCount()) {
            // PENDING: needs to be updated.
            SynthTreeUI ui = (SynthTreeUI)tree.getUI();
            SynthContext context = ui.getContext(tree);
            boolean scrollVert = context.getStyle().getBoolean(context,
                              "Tree.scrollsHorizontallyAndVertically", false);
            context.dispose();

	    if (beginRow == endRow) {
		Rectangle scrollBounds = tree.getPathBounds(
                                tree.getPathForRow(beginRow));

		if (scrollBounds != null) {
                    if (!scrollVert) {
                        scrollBounds.x = tree.getVisibleRect().x;
                        scrollBounds.width = 1;
                    }
		    tree.scrollRectToVisible(scrollBounds);
		}
	    }
	    else {
		Rectangle beginRect = tree.getPathBounds(tree.getPathForRow
                                                           (beginRow));
		Rectangle visRect = tree.getVisibleRect();
		Rectangle testRect = beginRect;
		int beginY = beginRect.y;
		int maxY = beginY + visRect.height;

		for (int counter = beginRow + 1; counter <= endRow; counter++){
		    testRect = tree.getPathBounds(tree.getPathForRow(counter));
		    if ((testRect.y + testRect.height) > maxY) {
			counter = endRow;
                    }
		}
		tree.scrollRectToVisible(new Rectangle(visRect.x, beginY, 1,
						  testRect.y + testRect.height-
						  beginY));
	    }
	}
    }


    //
    // ComponentUI methods
    //

    /**
     * Returns the preferred size needed to properly renderer the tree.
     */
    public Dimension getPreferredSize(JComponent c) {
	if (!validCachedPreferredSize) {
	    updateCachedPreferredSize();
        }
        return new Dimension(preferredSize);
    }

    /**
      * Returns the minimum size for this component.  Which will be
      * the min preferred size or 0, 0.
      */
    public Dimension getMinimumSize(JComponent c) {
	return new Dimension(0, 0);
    }

    /**
      * Returns the maximum size for this component, which will be the
      * preferred size if the instance is currently in a JTree, or 0, 0.
      */
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(tree);
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
	if (tree != null && treeState != null) {
	    Rectangle        bounds = treeState.getBounds(path, null);

            adjustCellBounds(tree, bounds, null);
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
	if (tree != null && treeState != null) {
	    Insets          i = tree.getInsets();

            if (leftToRight) {
                x -= i.left;
            }
            else {
                x = tree.getWidth() - x - i.right;
            }
	    return treeState.getPathClosestTo(x, y - i.top);
	}
	return null;
    }

    /**
      * Returns true if the tree is being edited.  The item that is being
      * edited can be returned by getEditingPath().
      */
    public boolean isEditing(JTree tree) {
	return (editingState != null);
    }

    /**
      * Stops the current editing session.  This has no effect if the
      * tree isn't being edited.  Returns true if the editor allows the
      * editing session to stop.
      */
    public boolean stopEditing(JTree tree) {
	if (editingState != null && tree.getCellEditor().stopCellEditing()) {
	    completeEditing(false, false, true);
	    return true;
	}
	return false;
    }

    /**
      * Cancels the current editing session.
      */
    public void cancelEditing(JTree tree) {
	if (editingState != null) {
	    completeEditing(false, true, false);
	}
    }

    /**
      * Selects the last item in path and tries to edit it.  Editing will
      * fail if the CellEditor won't allow it for the selected item.
      */
    public void startEditingAtPath(JTree tree, TreePath path) {
	tree.scrollPathToVisible(path);
	if (path != null && tree.isVisible(path)) {
	    startEditing(path, null);
        }
    }

    /**
     * Returns the path to the element that is being edited.
     */
    public TreePath getEditingPath(JTree tree) {
	return (editingState != null) ? editingState.path : null;
    }

    //
    // Install methods
    //

    public SynthTreeUI() {
        preferredSize = new Dimension();
    }

    public void installUI(JComponent c) {
        if (c == null) {
	    throw new NullPointerException(
                          "null component passed to SynthTreeUI.installUI()");
        }

	tree = (JTree)c;

	drawingCache = new HashMap(7);
	leftToRight = tree.getComponentOrientation().isLeftToRight();
        stopEditingWhenSelectionChanges = true;
        validCachedPreferredSize = false;

	installDefaults();
	installKeyboardActions();
	installComponents();
	installListeners();
    }

    protected void installDefaults() {
        // Installs the renderer, and potentially the editor
        updateRenderer();

        fetchStyle(tree);

        // This has to be here so that createLayoutCache doesn't get an NPE
        // when the renderer calls into the NodeDimensions.
	rendererPane = createCellRendererPane();
        tree.add(rendererPane);

	treeState = createLayoutCache();
	configureLayoutCache();

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

    private void fetchStyle(JTree tree) {
        SynthContext context = getContext(tree, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            drawHorizontalLegs = style.getBoolean(
                          context, "Tree.drawHorizontalLegs",true);
            drawVerticalLegs = style.getBoolean(
                        context, "Tree.drawVerticalLegs", true);

            tree.setRowHeight(style.getInt(context, "Tree.rowHeight", -1));

            tree.setScrollsOnExpand(style.getBoolean(
                                     context, "Tree.scrollsOnExpand", true));

            largeModel = (tree.isLargeModel() && tree.getRowHeight() > 0);

            expandedIcon = style.getIcon(context, "Tree.expandedIcon");
            collapsedIcon = style.getIcon(context, "Tree.collapsedIcon");

            trailingLegBufferOffset = style.getInt(
                      context, "Tree.trailingLegBufferOffset", 0);
            trailingControlOffset = style.getInt(
                        context, "Tree.trailingControlOffset", 0);
            controlSize = style.getInt(context, "Tree.controlSize", 0);
            indent = style.getInt(context, "Tree.indent", 0);
            updateRootOffset();

            drawsFocusBorder = style.getBoolean(
                      context, "Tree.drawsFocusBorder", true);
        }
        context.dispose();

        context = getContext(tree, Region.TREE_CELL, ENABLED);
        cellStyle = SynthLookAndFeel.updateStyle(context, this);
        context.dispose();
    }

    protected void installListeners() {
        tree.addPropertyChangeListener(this);

	tree.addMouseListener(defaultDragRecognizer);
	tree.addMouseMotionListener(defaultDragRecognizer);

        if ((mouseListener = createMouseListener()) != null) {
	    tree.addMouseListener(mouseListener);
	    if (mouseListener instanceof MouseMotionListener) {
		tree.addMouseMotionListener(
                             (MouseMotionListener)mouseListener);
	    }
	}
        if ((focusListener = createFocusListener()) != null) {
	    tree.addFocusListener(focusListener);
	}
        if ((keyListener = createKeyListener()) != null) {
	    tree.addKeyListener(keyListener);
	}
	if ((treeExpansionListener = createTreeExpansionListener()) != null) {
	    tree.addTreeExpansionListener(treeExpansionListener);
	}
        // TreeModel can be null
        TreeModel model = tree.getModel();
	if ((treeModelListener = createTreeModelListener()) != null &&
	                         model != null) {
	    model.addTreeModelListener(treeModelListener);
	}
        TreeSelectionModel treeSelectionModel = tree.getSelectionModel();
	if ((treeSelectionListener = createTreeSelectionListener()) != null) {
	    treeSelectionModel.addTreeSelectionListener(treeSelectionListener);
	}

        TreeCellEditor editor = tree.getCellEditor();

        if (editor != null) {
            cellEditorListener = createCellEditorListener();

            if (cellEditorListener != null) {
                editor.addCellEditorListener(cellEditorListener);
            }
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

        LazyActionMap.installLazyActionMap(tree, this);
    }

    InputMap getInputMap(int condition) {
        SynthContext context = getContext(tree, ENABLED);
        SynthStyle style = context.getStyle();
        InputMap map = null;

	if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
	    map = (InputMap)style.get(context, "Tree.ancestorInputMap");
	}
	else if (condition == JComponent.WHEN_FOCUSED) {
	    map = (InputMap)style.get(context, "Tree.focusInputMap");

	    InputMap rtlKeyMap;

	    if (!leftToRight && ((rtlKeyMap = (InputMap)style.get(context,
                                 "Tree.focusInputMap.RightToLeft")) != null)) {
		rtlKeyMap.setParent(map);
                map = rtlKeyMap;
	    }
	}
        context.dispose();
	return map;
    }

    public void loadActionMap(JComponent c, ActionMap map) {
	map.put("selectPrevious", new IncrementAction("selectPrevious", -1,
							  false, true));
	map.put("selectPreviousChangeLead", new IncrementAction
		("selectPreviousLead", -1, false, false));
	map.put("selectPreviousExtendSelection", new IncrementAction
		("selectPreviousExtendSelection", -1, true, true));

	map.put("selectNext", new IncrementAction
		("selectNext", 1, false, true));
	map.put("selectNextChangeLead", new IncrementAction
		("selectNextLead", 1, false, false));
	map.put("selectNextExtendSelection", new IncrementAction
		("selectNextExtendSelection", 1, true, true));

	map.put("selectChild", new TraverseAction
		("selectChild", 1, true));
	map.put("selectChildChangeLead", new TraverseAction
		("selectChildLead", 1, false));

	map.put("selectParent", new TraverseAction
		("selectParent", -1, true));
	map.put("selectParentChangeLead", new TraverseAction
		("selectParentLead", -1, false));	

	map.put("scrollUpChangeSelection", new ScrollAndSelectAction
		("scrollUpChangeSelection", SwingConstants.NORTH,false, true));
	map.put("scrollUpChangeLead", new ScrollAndSelectAction
		("scrollUpChangeLead", SwingConstants.NORTH, false, false));
	map.put("scrollUpExtendSelection", new ScrollAndSelectAction
		("scrollUpExtendSelection", SwingConstants.NORTH, true, true));

	map.put("scrollDownChangeSelection", new ScrollAndSelectAction
		("scrollDownChangeSelection", SwingConstants.SOUTH, false,
                 true));
	map.put("scrollDownExtendSelection", new ScrollAndSelectAction
		("scrollDownExtendSelection", SwingConstants.SOUTH, true,
                 true));
	map.put("scrollDownChangeLead", new ScrollAndSelectAction
		("scrollDownChangeLead", SwingConstants.SOUTH, false, false));

	map.put("selectFirst", new HomeAction
		("selectFirst", -1, false, true));
	map.put("selectFirstChangeLead", new HomeAction
		("selectFirst", -1, false, false));
	map.put("selectFirstExtendSelection",new HomeAction
		("selectFirstExtendSelection", -1, true, true));

	map.put("selectLast", new HomeAction
		("selectLast", 1, false, true));
	map.put("selectLastChangeLead", new HomeAction
		("selectLast", 1, false, false));
	map.put("selectLastExtendSelection", new HomeAction
		("selectLastExtendSelection", 1, true, true));

	map.put("toggle", new ToggleAction("toggle"));

	map.put("cancel", new CancelEditingAction("cancel"));

	map.put("startEditing", new EditAction("startEditing"));

	map.put("selectAll", new SelectAllAction("selectAll", true));

	map.put("clearSelection", new SelectAllAction
		("clearSelection", false));

	map.put("toggleSelectionPreserveAnchor",
		new AddSelectionAction("toggleSelectionPreserveAnchor",
					   false));
	map.put("toggleSelection",
		new AddSelectionAction("toggleSelection", true));

	map.put("extendSelection", new ExtendSelectionAction
		("extendSelection"));

	map.put("scrollLeft", new ScrollAction
		("scrollLeft", SwingConstants.HORIZONTAL, -10));
	map.put("scrollLeftExtendSelection", new ScrollAndSelectAction
		("scrollLeftExtendSelection", SwingConstants.WEST,true, true));
	map.put("scrollRight", new ScrollAction
		("scrollRight", SwingConstants.HORIZONTAL, 10));
	map.put("scrollRightExtendSelection", new ScrollAndSelectAction
		("scrollRightExtendSelection", SwingConstants.EAST,true,true));

	map.put("scrollRightChangeLead", new ScrollAndSelectAction
		("scrollRightChangeLead", SwingConstants.EAST, false, false));
	map.put("scrollLeftChangeLead", new ScrollAndSelectAction
		("scrollLeftChangeLead", SwingConstants.WEST, false, false));

        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
    }

    /**
     * Intalls the subcomponents of the tree, which is the renderer pane.
     */
    protected void installComponents() {
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JTree c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    private SynthContext getContext(JComponent c, Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    private SynthContext getContext(JComponent c, Region region, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                                       region, cellStyle, state);
    }

    private int getComponentState(JComponent c, Region region) {
        // Always treat the cell as selected, will be adjusted appropriately
        // when painted.
        return ENABLED | SELECTED;
    }

    //
    // Create methods.
    //

    /**
     * Adjusts the bounds, as returned from the TreeState to the insets
     * and orientation of the Tree.
     */
    private void adjustCellBounds(JTree tree, Rectangle bounds, Insets i){
        if (bounds != null) {
            if (i == null) {
                i = tree.getInsets();
            }
            bounds.y += i.top;
            if (leftToRight) {
                bounds.x += i.left;
            }
            else {
                bounds.x = tree.getWidth() - i.right - bounds.x - bounds.width;
            }
        }
    }

    /**
     * Creates an instance of NodeDimensions that is able to determine
     * the size of a given node in the tree.
     */
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
	return new NodeDimensionsHandler();
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
	if (largeModel && tree.getRowHeight() > 0) {
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
    protected TreeCellEditor createCellEditor() {
        TreeCellRenderer renderer = tree.getCellRenderer();
        DefaultTreeCellEditor editor;

	if(renderer != null && (renderer instanceof DefaultTreeCellRenderer)) {
	    editor = new SynthTreeCellEditor(tree, (DefaultTreeCellRenderer)
                                             renderer);
	}
        else {
            editor = new SynthTreeCellEditor(tree, null);
        }
        SynthContext context = getContext(tree, ENABLED);
        context.setComponentState(ENABLED | SELECTED);
        context.dispose();
        return editor;
    }

    /**
      * Returns the default cell renderer that is used to do the
      * stamping of each node.
      */
    protected TreeCellRenderer createCellRenderer() {
        return new SynthTreeCellRenderer();
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

	uninstallListeners();
	uninstallComponents();
	uninstallKeyboardActions();
	uninstallDefaults();
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(tree, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;

        context = getContext(tree, Region.TREE_CELL, ENABLED);
        cellStyle.uninstallDefaults(context);
        context.dispose();
        cellStyle = null;


	if (tree.getTransferHandler() instanceof UIResource) {
	    tree.setTransferHandler(null);
	}
        if (tree.getCellRenderer() instanceof UIResource) {
	    tree.setCellRenderer(null);
	}
        if (tree.getCellEditor() instanceof UIResource) {
	    tree.setCellEditor(null);
	}
        tree = null;
    }

    protected void uninstallListeners() {
	if (componentListener != null) {
	    tree.removeComponentListener(componentListener);
	}
        tree.removePropertyChangeListener(this);

	tree.removeMouseListener(defaultDragRecognizer);
	tree.removeMouseMotionListener(defaultDragRecognizer);

        if (mouseListener != null) {
	    tree.removeMouseListener(mouseListener);
	    if (mouseListener instanceof MouseMotionListener) {
		tree.removeMouseMotionListener((MouseMotionListener)
                                               mouseListener);
	    }
	}
        if (focusListener != null) {
	    tree.removeFocusListener(focusListener);
	}
        if (keyListener != null) {
	    tree.removeKeyListener(keyListener);
	}
	if (treeExpansionListener != null) {
	    tree.removeTreeExpansionListener(treeExpansionListener);
	}
        TreeModel treeModel = tree.getModel();

	if (treeModel != null && treeModelListener != null) {
	    treeModel.removeTreeModelListener(treeModelListener);
	}
        TreeSelectionModel selectionModel = tree.getSelectionModel();
	if (treeSelectionListener != null) {
	    tree.getSelectionModel().removeTreeSelectionListener
		               (treeSelectionListener);
	}

        TreeCellEditor editor = tree.getCellEditor();
        if (editor != null && cellEditorListener != null) {
            editor.removeCellEditorListener(cellEditorListener);
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
	if (rendererPane != null) {
	    tree.remove(rendererPane);
	}
    }

    //
    // Painting routines.
    //

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
	Rectangle paintBounds = g.getClipBounds();
	Insets insets = tree.getInsets();
	TreePath initialPath = getClosestPathForLocation(tree, 0,
                                                         paintBounds.y);
	Enumeration paintingEnumerator = treeState.getVisiblePathsFrom
	                                      (initialPath);
	int row = treeState.getRowForPath(initialPath);
	int endY = paintBounds.y + paintBounds.height;
        TreeModel treeModel = tree.getModel();
        SynthContext cellContext = getContext(tree, Region.TREE_CELL);

	drawingCache.clear();

	if (paintingEnumerator != null) {
            // First pass, draw the rows

	    boolean done = false;
	    boolean isExpanded;
	    boolean hasBeenExpanded;
	    boolean isLeaf;
	    Rectangle boundsBuffer = new Rectangle();
            Rectangle rowBounds = new Rectangle(0, 0, tree.getWidth(),0);
	    Rectangle bounds;
	    TreePath path;
            TreeCellRenderer renderer = tree.getCellRenderer();
            DefaultTreeCellRenderer dtcr = (renderer instanceof
                       DefaultTreeCellRenderer) ? (DefaultTreeCellRenderer)
                       renderer : null;

            configureRenderer(cellContext);
	    while (!done && paintingEnumerator.hasMoreElements()) {
		path = (TreePath)paintingEnumerator.nextElement();
		if (path != null) {
		    isLeaf = treeModel.isLeaf(path.getLastPathComponent());
		    if (isLeaf) {
			isExpanded = hasBeenExpanded = false;
                    }
		    else {
			isExpanded = treeState.getExpandedState(path);
			hasBeenExpanded = tree.hasBeenExpanded(path);
		    }
		    bounds = treeState.getBounds(path, boundsBuffer);
                    adjustCellBounds(tree, bounds, insets);
                    rowBounds.y = bounds.y;
                    rowBounds.height = bounds.height;
		    paintRow(renderer, dtcr, context, cellContext, g,
                             paintBounds, insets, bounds, rowBounds, path,
                             row, isExpanded, hasBeenExpanded, isLeaf);
		    if ((bounds.y + bounds.height) >= endY) {
			done = true;
                    }
		}
		else {
		    done = true;
		}
		row++;
	    }


	    // Draw the connecting lines and controls.
	    // Find each parent and have them draw a line to their last child
            boolean rootVisible = tree.isRootVisible();
            TreePath parentPath = initialPath;
	    parentPath = parentPath.getParentPath();
	    while (parentPath != null) {
                paintVerticalPartOfLeg(context, g, paintBounds,
                                       insets, parentPath);
		drawingCache.put(parentPath, Boolean.TRUE);
		parentPath = parentPath.getParentPath();
	    }
	    done = false;
            paintingEnumerator = treeState.getVisiblePathsFrom(initialPath);
	    while (!done && paintingEnumerator.hasMoreElements()) {
		path = (TreePath)paintingEnumerator.nextElement();
		if (path != null) {
		    isLeaf = treeModel.isLeaf(path.getLastPathComponent());
		    if (isLeaf) {
			isExpanded = hasBeenExpanded = false;
                    }
		    else {
			isExpanded = treeState.getExpandedState(path);
			hasBeenExpanded = tree.hasBeenExpanded(path);
		    }
		    bounds = treeState.getBounds(path, boundsBuffer);
                    adjustCellBounds(tree, bounds, insets);
		    // See if the vertical line to the parent has been drawn.
		    parentPath = path.getParentPath();
		    if (parentPath != null) {
			if (drawingCache.get(parentPath) == null) {
                            paintVerticalPartOfLeg(context, g,
                                                   paintBounds, insets,
                                                   parentPath);
			    drawingCache.put(parentPath, Boolean.TRUE);
			}
			paintHorizontalPartOfLeg(context, g,
                                                 paintBounds, insets, bounds,
                                                 path, row, isExpanded,
						 hasBeenExpanded, isLeaf);
		    }
		    else if (rootVisible && row == 0) {
			paintHorizontalPartOfLeg(context, g,
                                                 paintBounds, insets, bounds,
                                                 path, row, isExpanded,
						 hasBeenExpanded, isLeaf);
		    }
		    if (shouldPaintExpandControl(path, row, isExpanded,
                                                 hasBeenExpanded, isLeaf)) {
			paintExpandControl(context, g, paintBounds,
                                           insets, bounds, path, row,
                                           isExpanded, hasBeenExpanded,isLeaf);
		    }
		    if ((bounds.y + bounds.height) >= endY) {
			done = true;
                    }
		}
		else {
		    done = true;
		}
		row++;
	    }
	}
        cellContext.dispose();
	// Empty out the renderer pane, allowing renderers to be gc'ed.
	rendererPane.removeAll();
    }

    private void configureRenderer(SynthContext context) {
        TreeCellRenderer renderer = tree.getCellRenderer();

        if (renderer instanceof DefaultTreeCellRenderer) {
            DefaultTreeCellRenderer r = (DefaultTreeCellRenderer)renderer;
            SynthStyle style = context.getStyle();

            context.setComponentState(ENABLED | SELECTED);
            Color color = r.getTextSelectionColor();
            if (color == null || (color instanceof UIResource)) {
                r.setTextSelectionColor(style.getColor(
                                     context, ColorType.TEXT_FOREGROUND));
            }
            color = r.getBackgroundSelectionColor();
            if (color == null || (color instanceof UIResource)) {
                r.setBackgroundSelectionColor(style.getColor(
                                        context, ColorType.TEXT_BACKGROUND));
            }

            context.setComponentState(ENABLED);
            color = r.getTextNonSelectionColor();
            if (color == null || color instanceof UIResource) {
                r.setTextNonSelectionColor(style.getColor(
                                        context, ColorType.TEXT_FOREGROUND));
            }
            color = r.getBackgroundNonSelectionColor();
            if (color instanceof UIResource) {
                r.setBackgroundNonSelectionColor(style.getColor(
                                  context, ColorType.TEXT_BACKGROUND));
            }
        }
    }

    /**
     * Paints the horizontal part of the leg. The receiver should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.<p>
     * NOTE: <code>parentRow</code> can be -1 if the root is not visible.
     */
    protected void paintHorizontalPartOfLeg(SynthContext context, Graphics g,
                                          Rectangle clipBounds, Insets insets,
                                          Rectangle bounds, TreePath path,
                                          int row, boolean isExpanded,
                                          boolean hasBeenExpanded,
                                          boolean isLeaf) {
        if (!drawHorizontalLegs) {
            return;
        }
        int depth = path.getPathCount() - 1;
	if ((depth == 0 || (depth == 1 && !tree.isRootVisible())) &&
            !tree.getShowsRootHandles()) {
	    return;
        }

	int clipLeft = clipBounds.x;
	int clipRight = clipBounds.x + (clipBounds.width - 1);
	int clipBottom = clipBounds.y + (clipBounds.height - 1);
	int y = bounds.y + bounds.height / 2;
        int x0;
        int x1;

        if (leftToRight) {
            x0 = bounds.x - getIndent() - getTrailingControlOffset();
            x1 = bounds.x - trailingLegBufferOffset;
        }
        else {
            x0 = bounds.x + bounds.width + trailingLegBufferOffset;
            x1 = bounds.x + bounds.width + getIndent() +
                            getTrailingControlOffset();
        }
        if (y >= clipBounds.y && y <= clipBottom && x1 >= clipLeft &&
	                                            x0 <= clipRight ) {
            x0 = Math.max(x0, clipLeft);
            x1 = Math.min(x1, clipRight);

            if (x0 < x1) {
                SynthGraphics engine = context.getStyle().
                                               getSynthGraphics(context);

                g.setColor(context.getStyle().getColor(context,
                                                       ColorType.FOREGROUND));
                engine.drawLine(context, "Tree.horizontalLine", g, x0, y,
                                x1, y);
	    }
	}
    }

    /**
     * Paints the vertical part of the leg. The receiver should
     * NOT modify <code>clipBounds</code>, <code>insets</code>.<p>
     */
    protected void paintVerticalPartOfLeg(SynthContext context, Graphics g,
                                          Rectangle clipBounds, Insets insets,
                                          TreePath path) {
        if (!drawVerticalLegs) {
            return;
        }
        int depth = path.getPathCount() - 1;
	if (depth == 0 && !tree.getShowsRootHandles() &&
                          !tree.isRootVisible()) {
	    return;
        }
	int lineX;
        int offset = getRowX(-1, depth) - getTrailingControlOffset();
	if (leftToRight) {
	    lineX = offset + insets.left;
	}
	else {
	    lineX = tree.getWidth() - offset - insets.right;
	}
	int clipLeft = clipBounds.x;
	int clipRight = clipBounds.x + (clipBounds.width - 1);

	if (lineX >= clipLeft && lineX <= clipRight) {
	    int clipTop = clipBounds.y;
	    int clipBottom = clipBounds.y + clipBounds.height;
	    Rectangle parentBounds = getPathBounds(tree, path);
	    Rectangle lastChildBounds = getPathBounds(tree,
						     getLastChildPath(path));
	    int       top;

	    if (parentBounds == null) {
		top = Math.max(insets.top, clipTop);
	    }
	    else {
		top = Math.max(parentBounds.y + parentBounds.height, clipTop);
            }
	    if (depth == 0 && !tree.isRootVisible()) {
		TreeModel     model = tree.getModel();
                Object        root = model.getRoot();

                if (model.getChildCount(root) > 0) {
                    parentBounds = getPathBounds(tree, path.
				  pathByAddingChild(model.getChild(root, 0)));
                    if (parentBounds != null) {
                        top = Math.max(insets.top, parentBounds.y +
                                       parentBounds.height / 2);
                    }
		}
	    }

	    int bottom = Math.min(lastChildBounds.y +
				  (lastChildBounds.height / 2), clipBottom);

            if (top < bottom) {
                SynthGraphics engine = context.getStyle().
                                               getSynthGraphics(context);

                g.setColor(context.getStyle().getColor(context,
                                                       ColorType.FOREGROUND));
                engine.drawLine(context, "Tree.verticalLine", g, lineX, top,
                                lineX, bottom);
            }
	}
    }

    /**
     * Paints the expand (toggle) part of a row. The receiver should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.
     */
    protected void paintExpandControl(SynthContext context, Graphics g,
                                    Rectangle clipBounds, Insets insets,
                                    Rectangle bounds, TreePath path,
                                    int row, boolean isExpanded,
                                    boolean hasBeenExpanded,
                                    boolean isLeaf) {
	Object       value = path.getLastPathComponent();

	// Draw icons if not a leaf and either hasn't been loaded,
	// or the model child count is > 0.
	if (!isLeaf && (!hasBeenExpanded ||
			tree.getModel().getChildCount(value) > 0)) {
	    int middleXOfKnob;

	    if (leftToRight) {
	        middleXOfKnob = bounds.x - getTrailingControlOffset() + 1;
	    }
	    else {
	        middleXOfKnob = bounds.x + bounds.width +
                                         getTrailingControlOffset() - 1;
	    }
	    int middleYOfKnob = bounds.y + (bounds.height / 2);

	    if (isExpanded) {
		Icon expandedIcon = getExpandedIcon();

		if (expandedIcon != null) {
                    paintIconCenteredAt(context, tree, g, expandedIcon,
                                        middleXOfKnob, middleYOfKnob);
                }
	    }
	    else {
		Icon collapsedIcon = getCollapsedIcon();

		if (collapsedIcon != null) {
                    paintIconCenteredAt(context, tree, g, collapsedIcon,
                                        middleXOfKnob, middleYOfKnob);
                }
	    }
	}
    }

    /**
     * Paints the renderer part of a row. The receiver should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.
     */
    protected void paintRow(TreeCellRenderer renderer,
               DefaultTreeCellRenderer dtcr, SynthContext treeContext,
               SynthContext cellContext, Graphics g, Rectangle clipBounds,
               Insets insets, Rectangle bounds, Rectangle rowBounds,
               TreePath path, int row, boolean isExpanded,
               boolean hasBeenExpanded, boolean isLeaf) {
	// Don't paint the renderer if editing this row.
        boolean selected = tree.isRowSelected(row);

        if (selected) {
            cellContext.setComponentState(ENABLED | SELECTED);
        }
        else {
            cellContext.setComponentState(ENABLED);
        }
        if (dtcr != null && (dtcr.getBorderSelectionColor() instanceof
                             UIResource)) {
            dtcr.setBorderSelectionColor(style.getColor(
                                             cellContext, ColorType.FOCUS));
        }
        SynthLookAndFeel.updateSubregion(cellContext, g, rowBounds);
	if (getEditingRow() == row) {
	    return;
        }

	int leadIndex;

	if (tree.hasFocus()) {
	    leadIndex = getLeadSelectionRow();
	}
	else {
	    leadIndex = -1;
        }

	Component component = renderer.getTreeCellRendererComponent(
                         tree, path.getLastPathComponent(),
                         selected, isExpanded, isLeaf, row,
                         (leadIndex == row));
	
	rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
				    bounds.width, bounds.height, true);	
    }

    /**
     * Returns true if the expand (toggle) control should be drawn for
     * the specified row.
     */
    private boolean shouldPaintExpandControl(TreePath path, int row,
                                             boolean isExpanded,
                                             boolean hasBeenExpanded,
                                             boolean isLeaf) {
	if (isLeaf) {
	    return false;
        }

	int              depth = path.getPathCount() - 1;

	if ((depth == 0 || (depth == 1 && !tree.isRootVisible())) &&
                           !tree.getShowsRootHandles()) {
	    return false;
        }
	return true;
    }

    //
    // Generic painting methods
    //

    /**
     * Paints the Icon centered at the specified location.
     */
    private void paintIconCenteredAt(SynthContext context, Component c,
                                     Graphics graphics, Icon icon,
                                     int x, int y) {
        int w = SynthIcon.getIconWidth(icon, context);
        int h = SynthIcon.getIconHeight(icon, context);
	SynthIcon.paintIcon(icon, context, graphics, x - w/2, y - h/2, w, h);
    }

    //
    // Various local methods
    //

    /**
     * Resets the selection model. The appropriate listener are installed
     * on the model.
     */
    private void selectionModelChanged(TreeSelectionModel oldModel,
                                    TreeSelectionModel newModel) {
	completeEditing();
        if (newModel != null) {
            if (treeSelectionListener != null) {
                newModel.removeTreeSelectionListener
		               (treeSelectionListener);
            }
        }
	if (newModel != null) {
	    if (treeSelectionListener != null) {
		newModel.addTreeSelectionListener
		                   (treeSelectionListener);
            }
	    if (treeState != null) {
		treeState.setSelectionModel(newModel);
            }
	}
	else if (treeState != null) {
	    treeState.setSelectionModel(null);
        }
	if (tree != null) {
	    tree.repaint();
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getSource() == tree) {
            String              changeName = event.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(event)) {
                fetchStyle((JTree)event.getSource());
            }
            if (JTree.LEAD_SELECTION_PATH_PROPERTY.equals(changeName)) {
                updateLeadRow();
                repaintPath((TreePath)event.getOldValue());
                repaintPath((TreePath)event.getNewValue());
            }
            else if (JTree.ANCHOR_SELECTION_PATH_PROPERTY.equals(changeName)) {
                repaintPath((TreePath)event.getOldValue());
                repaintPath((TreePath)event.getNewValue());
            }
            else if (JTree.CELL_RENDERER_PROPERTY.equals(changeName)) {
                completeEditing();
                updateRenderer();
                invalidateStateAndSize();
            }
            else if (JTree.TREE_MODEL_PROPERTY.equals(changeName)) {
                TreeModel oldModel = (TreeModel)event.getOldValue();

                completeEditing();
                if (oldModel != null && treeModelListener != null) {
                    oldModel.removeTreeModelListener(treeModelListener);
                }
                TreeModel newModel = (TreeModel)event.getNewValue();
                if (newModel != null && treeModelListener != null) {
                    newModel.addTreeModelListener(treeModelListener);
                }
                if (treeState != null) {
                    treeState.setModel(newModel);
                    updateLayoutCacheExpandedNodes();
                    invalidateSize();
                }
            }
            else if (JTree.ROOT_VISIBLE_PROPERTY.equals(changeName)) {
                completeEditing();
                updateRootOffset();
                if (treeState != null) {
                    treeState.setRootVisible(((Boolean)event.getNewValue()).
                                             booleanValue());
                    invalidateStateAndSize();
                }
            }
            else if (JTree.SHOWS_ROOT_HANDLES_PROPERTY.equals(changeName)) {
                completeEditing();
                updateRootOffset();
                invalidateStateAndSize();
            }
            else if (JTree.ROW_HEIGHT_PROPERTY.equals(changeName)) {
                completeEditing();
                if (treeState != null) {
                    updateLargeModel();
                    treeState.setRowHeight(tree.getRowHeight());
                    invalidateSize();
                }
            }
            else if (JTree.CELL_EDITOR_PROPERTY.equals(changeName)) {
                completeEditing();
                TreeCellEditor oldEditor = (TreeCellEditor)event.getOldValue();

                if (oldEditor != null && cellEditorListener != null) {
                    oldEditor.removeCellEditorListener(cellEditorListener);
                }

                TreeCellEditor newEditor = (TreeCellEditor)event.getNewValue();
                if (newEditor != null) {
                    if (cellEditorListener == null) {
                        cellEditorListener = createCellEditorListener();
                    }
                    if (cellEditorListener != null) {
                        newEditor.addCellEditorListener(cellEditorListener);
                    }
                }
            }
            else if (JTree.EDITABLE_PROPERTY.equals(changeName)) {
                completeEditing();

                TreeCellEditor editor = tree.getCellEditor();

                if (tree.isEditable()) {
                    if (editor == null) {
                        editor = createCellEditor();
                        if (editor != null) {
                            tree.setCellEditor(editor);
                        }
                    }
                    else {
                        if (cellEditorListener == null) {
                            cellEditorListener = createCellEditorListener();
                        }
                        if (cellEditorListener != null) {
                            editor.addCellEditorListener(cellEditorListener);
                        }
                    }
                }
                else if (editor instanceof UIResource) {
                    tree.setCellEditor(null);
                }
            }
            else if (JTree.LARGE_MODEL_PROPERTY.equals(changeName)) {
                updateLargeModel();
            }
            else if(JTree.SELECTION_MODEL_PROPERTY.equals(changeName)) {
                selectionModelChanged((TreeSelectionModel)event.
                                      getOldValue(), tree.getSelectionModel());
            }
            else if("font".equals(changeName)) {
                completeEditing();
                invalidateStateAndSize();
            }
            else if ("componentOrientation".equals(changeName)) {
                leftToRight = tree.getComponentOrientation().isLeftToRight();
                invalidateStateAndSize();

                InputMap km = getInputMap(JComponent.WHEN_FOCUSED);
                SwingUtilities.replaceUIInputMap(tree,
						JComponent.WHEN_FOCUSED, km);
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


    private int getTrailingControlOffset() {
	return trailingControlOffset;
    }

    private int getIndent() {
        return indent;
    }

    /**
     * Returns the icon to draw when the node is expanded.
     */
    private Icon getExpandedIcon() {
	return expandedIcon;
    }

    /**
     * Returns the Icon to draw when the node is collapsed.
     */
    private Icon getCollapsedIcon() {
	return collapsedIcon;
    }

    /**
     * Updates the necessary state in response to the large model, or rowHeight
     * property changing.
     */
    private void updateLargeModel() {
        boolean largeModel = tree.isLargeModel();

	if (tree.getRowHeight() < 1) {
	    largeModel = false;
        }
	if (this.largeModel != largeModel) {
	    completeEditing();
	    this.largeModel = largeModel;
	    treeState = createLayoutCache();
	    configureLayoutCache();
	    invalidateSize();
	}
    }

    /**
     * Returns true if operating in large model mode.
     */
    private boolean isLargeModel() {
	return largeModel;
    }

    /**
     * Returns the amount to indent the given row. NOTE: This does not
     * include insets, nor should it!
     */
    private int getRowX(int row, int depth) {
        return getIndent() * (depth + rootOffset - 1) + controlSize;
    }


    /**
     * Makes all the nodes that are expanded in JTree expanded in LayoutCache.
     * This invokes updateExpandedDescendants with the root path.
     */
    private void updateLayoutCacheExpandedNodes() {
        TreeModel treeModel = tree.getModel();

	if (treeModel != null && treeModel.getRoot() != null) {
	    updateExpandedDescendants(new TreePath(treeModel.getRoot()));
        }
    }

    /**
     * Updates the expanded state of all the descendants of <code>path</code>
     * by getting the expanded descendants from the tree and forwarding
     * to the tree state.
     */
    private void updateExpandedDescendants(TreePath path) {
	completeEditing();
        treeState.setExpandedState(path, true);

        Enumeration   descendants = tree.getExpandedDescendants(path);

        if (descendants != null) {
            while (descendants.hasMoreElements()) {
                path = (TreePath)descendants.nextElement();
                treeState.setExpandedState(path, true);
            }
        }
        updateLeadRow();
        invalidateSize();
    }

    /**
     * Returns a path to the last child of <code>parent</code>.
     */
    private TreePath getLastChildPath(TreePath parent) {
        TreeModel treeModel = tree.getModel();

	if (treeModel != null) {
	    int         childCount = treeModel.getChildCount
		                         (parent.getLastPathComponent());
	    
	    if(childCount > 0) {
		return parent.pathByAddingChild(treeModel.getChild
			   (parent.getLastPathComponent(), childCount - 1));
            }
	}
	return null;
    }

    /**
     * Updates the offset for the root. This should be invoked as the
     * properties the rootOffset depends upon change, eg root visibility,
     * showing root handles...
     */
    private void updateRootOffset() {
	if (tree.isRootVisible()) {
	    if (tree.getShowsRootHandles()) {
		rootOffset = 1;
            }
	    else {
		rootOffset = 0;
            }
	}
	else if (!tree.getShowsRootHandles()) {
	    rootOffset = -1;
        }
	else {
	    rootOffset = 0;
        }
    }

    /**
     * If the JTree's renderer is null, this will install a default one.
     * Additionaly if we have installed an editor, we're reinstall it.
      */
    private void updateRenderer() {
        TreeCellRenderer      newCellRenderer = tree.getCellRenderer();

        if (newCellRenderer == null) {
            tree.setCellRenderer(createCellRenderer());
        }
        if (tree.isEditable() && (tree.getCellEditor() instanceof
                                  UIResource)) {
            // We do this as the editor gets state from the renderer,
            // so that any time one changes we need to update the other.
            tree.setCellEditor(createCellEditor());
        }
    }

    /**
     * Configures the AbstractLayoutCache based on the tree we're
     * providing the look and feel for.
     */
    private void configureLayoutCache() {
        if (nodeDimensions == null) {
            nodeDimensions = createNodeDimensions();
        }
        treeState.setNodeDimensions(nodeDimensions);
        treeState.setRootVisible(tree.isRootVisible());
        treeState.setRowHeight(tree.getRowHeight());
        treeState.setSelectionModel(tree.getSelectionModel());
        // Only do this if necessary, may loss state if call with
        // same model as it currently has.
        if (treeState.getModel() != tree.getModel()) {
            treeState.setModel(tree.getModel());
        }
        updateLayoutCacheExpandedNodes();
        // Create a listener to update preferred size when bounds
        // changes, if necessary.
        if (largeModel) {
            if (componentListener == null) {
                componentListener = createComponentListener();
                if (componentListener != null) {
                    tree.addComponentListener(componentListener);
                }
            }
        }
	else if(componentListener != null) {
	    tree.removeComponentListener(componentListener);
	    componentListener = null;
	}
    }

    /**
     * Invalides the size of the AbstractLayoutCache and marks the tree as
     * needing to redisplay.
     */
    private void invalidateStateAndSize() {
        treeState.invalidateSizes();
	invalidateSize();
    }

    /**
     * Marks the cached size as being invalid, and messages the
     * tree with <code>treeDidChange</code>.
     */
    private void invalidateSize() {
	validCachedPreferredSize = false;
	tree.treeDidChange();
    }

    /**
     * Recalculates the preferred size needed to display the tree.
     */
    private void updateCachedPreferredSize() {
        Insets               i = tree.getInsets();

        if (largeModel) {
            Rectangle            visRect = tree.getVisibleRect();

            visRect.x -= i.left;
            visRect.y -= i.top;
            preferredSize.width = treeState.getPreferredWidth(visRect);
        }
        else if (leftToRight) {
            preferredSize.width = treeState.getPreferredWidth(null);
        }
        preferredSize.height = treeState.getPreferredHeight();
        preferredSize.width += i.left + i.right;
        preferredSize.height += i.top + i.bottom;
	validCachedPreferredSize = true;
    }

    // 
    // Editing related methods
    //

    /**
     * Returns the row being edited, -1 if not editing.
     */
    private int getEditingRow() {
        return (editingState != null) ? editingState.row : -1;
    }

    /**
     * Returns the Component responsible for editing, or null if not
     * editing.
     */
    private Component getEditingComponent() {
        return (editingState != null) ? editingState.component : null;
    }

    /**
     * Messaged to stop the editing session. If the tree the receiver
     * is providing the look and feel for returns true from
     * <code>getInvokesStopCellEditing</code>, stopCellEditing will be
     * invoked on the current editor, otherwise cancelCellEditing will be
     * invoked
     */
    private void completeEditing() {
	if (tree.getInvokesStopCellEditing() && getEditingComponent() != null){
	    tree.getCellEditor().stopCellEditing();
	}
	// Invoke cancelCellEditing, this will do nothing if stopCellEditing
        // was successful.
	completeEditing(false, true, false);
    }

    /**
      * Stops the editing session.  If messageStop is true the editor
      * is messaged with stopEditing, if messageCancel is true the
      * editor is messaged with cancelEditing. If messageTree is true
      * the treeModel is messaged with valueForPathChanged.
      */
    private void completeEditing(boolean messageStop,
                                 boolean messageCancel,
                                 boolean messageTree) {
	if (editingState != null) {
            EditingState oldState = editingState;
	    Object newValue = editingState.editor.getCellEditorValue();
	    Rectangle editingBounds = tree.getPathBounds(oldState.path);
	    boolean requestFocus = (tree != null && (tree.hasFocus() ||
               SwingUtilities.findFocusOwner(editingState.component) != null));

            editingState = null;
	    if (messageStop) {
		oldState.editor.stopCellEditing();
            }
	    else if (messageCancel) {
		oldState.editor.cancelCellEditing();
            }
	    tree.remove(oldState.component);
	    if (oldState.editorHasDifferentSize) {
		treeState.invalidatePathBounds(oldState.path);
		invalidateSize();
	    }
	    else {
		editingBounds.x = 0;
		editingBounds.width = tree.getSize().width;
		tree.repaint(editingBounds);
	    }
	    if (requestFocus) {
		tree.requestFocus();
            }
	    if (messageTree) {
		tree.getModel().valueForPathChanged(oldState.path, newValue);
            }
	}
    }

    /**
      * Will start editing for node if there is a cellEditor and
      * shouldSelectCell returns true.<p>
      * This assumes that path is valid and visible.
      */
    private boolean startEditing(TreePath path, MouseEvent event) {
        if (tree.isEditing() && tree.getInvokesStopCellEditing() &&
                               !tree.stopEditing()) {
            return false;
        }
	completeEditing();
        TreeCellEditor cellEditor = tree.getCellEditor();
	if (cellEditor != null && tree.isPathEditable(path)) {
	    int           row = getRowForPath(tree, path);

	    if (cellEditor.isCellEditable(event)) {
                EditingState state = new EditingState();

                editingState = state;
                state.component = cellEditor.getTreeCellEditorComponent
		      (tree, path.getLastPathComponent(),
		       tree.isPathSelected(path), tree.isExpanded(path),
		       tree.getModel().isLeaf(path.getLastPathComponent()),
                       row);
                state.editor = cellEditor;

		Rectangle           nodeBounds = getPathBounds(tree, path);

		state.row = row;

		Dimension editorSize = state.component.getPreferredSize();

		// Only allow odd heights if explicitly set.
		if (editorSize.height != nodeBounds.height &&
                                         tree.getRowHeight() > 0) {
		    editorSize.height = tree.getRowHeight();
                }

		if (editorSize.width != nodeBounds.width ||
		               editorSize.height != nodeBounds.height) {
		    // Editor wants different width or height, invalidate 
		    // treeState and relayout.
		    state.editorHasDifferentSize = true;
		    treeState.invalidatePathBounds(path);
		    invalidateSize();
		}
		else {
		    state.editorHasDifferentSize = false;
                }
		tree.add(state.component);
		state.component.setBounds(nodeBounds.x, nodeBounds.y,
					   editorSize.width,
					   editorSize.height);
		state.path = path;
		state.component.validate();

		Rectangle              visRect = tree.getVisibleRect();

                tree.repaint(nodeBounds.x, nodeBounds.y, visRect.width +
                             visRect.x - nodeBounds.x, editorSize.height);
		if (cellEditor.shouldSelectCell(event)) {
                    stopEditingWhenSelectionChanges = false;
                    tree.setSelectionRow(row);
                    stopEditingWhenSelectionChanges = true;
		}
		SynthLookAndFeel.compositeRequestFocus(state.component);
		return true;
	    }
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
    protected void expandPathIfNecessary(SynthContext context,
                                         TreePath path, int mouseX,
                                         int mouseY) {
        if (isLocationInExpandControl(context, path, mouseX, mouseY)) {
            handleExpandControlClick(path, mouseX, mouseY);
	}
    }

    /**
     * Returns true if <code>mouseX</code> and <code>mouseY</code> fall
     * in the area of row that is used to expand/collapse the node and
     * the node at <code>row</code> does not represent a leaf.
     */
    protected boolean isLocationInExpandControl(SynthContext context,
                                  TreePath path, int mouseX, int mouseY) {
        TreeModel treeModel = tree.getModel();

	if (path != null && !treeModel.isLeaf(path.getLastPathComponent())){
	    int                     boxWidth;

	    if (getExpandedIcon() != null) {
		boxWidth = SynthIcon.getIconWidth(getExpandedIcon(), context);
            }
	    else {
		boxWidth = 8;
            }

	    Insets i = tree.getInsets();
            int boxX = getRowX(tree.getRowForPath(path),
                               path.getPathCount() - 1) -
                          getTrailingControlOffset() - boxWidth / 2;

            if (leftToRight) {
                boxX += i.left;
            }
            else {
                boxX = tree.getWidth() - getRowX(
                       tree.getRowForPath(path), path.getPathCount() - 1) +
                       getTrailingControlOffset() - boxWidth / 2;
            }
	    return mouseX >= boxX && (mouseX <= boxX + boxWidth);
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
    private void toggleExpandState(TreePath path) {
	if (!tree.isExpanded(path)) {
	    int       row = tree.getRowForPath(path);

	    tree.expandPath(path);
	    invalidateSize();
	    if (row != -1) {
		if(tree.getScrollsOnExpand()) {
		    ensureRowsAreVisible(tree, row, row + treeState.
					 getVisibleChildCount(path));
                }
		else {
		    ensureRowsAreVisible(tree, row, row);
                }
	    }
	}
	else {
	    tree.collapsePath(path);
	    invalidateSize();
	}
    }

    /**
     * Returning true signifies a mouse event on the node should toggle
     * the selection of only the row under mouse.
     */
    protected boolean isToggleSelectionEvent(MouseEvent event) {
	return (SynthLookAndFeel.isPrimaryMouseButton(event) &&
		event.isControlDown());
    }

    /**
     * Returning true signifies a mouse event on the node should select
     * from the anchor point.
     */
    protected boolean isMultiSelectEvent(MouseEvent event) {
	return (SynthLookAndFeel.isPrimaryMouseButton(event) &&
		event.isShiftDown());
    }

    /**
     * Returning true indicates the row under the mouse should be toggled
     * based on the event. This is invoked after checkForClickInExpandControl,
     * implying the location is not in the expand (toggle) control
     */
    protected boolean isToggleEvent(MouseEvent event) {
	if (!SynthLookAndFeel.isPrimaryMouseButton(event)) {
	    return false;
	}
	int           clickCount = tree.getToggleClickCount();

	if (clickCount <= 0) {
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
	if (isToggleSelectionEvent(event)) {
	    if (tree.isPathSelected(path)) {
		tree.removeSelectionPath(path);
            }
	    else {
		tree.addSelectionPath(path);
            }
	    tree.setAnchorSelectionPath(path);
	    tree.setLeadSelectionPath(path);
	}
	/* Adjust from the anchor point. */
	else if (isMultiSelectEvent(event)) {
	    TreePath anchor = tree.getAnchorSelectionPath();
	    int anchorRow = (anchor == null) ? -1 : tree.getRowForPath(anchor);

	    if (anchorRow == -1 || tree.getSelectionModel().
                      getSelectionMode() == TreeSelectionModel.
                      SINGLE_TREE_SELECTION) {
		tree.setSelectionPath(path);
	    }
	    else {
		int row = getRowForPath(tree, path);
		TreePath lastAnchorPath = anchor;

		if (row < anchorRow) {
		    tree.setSelectionInterval(row, anchorRow);
                }
		else {
		    tree.setSelectionInterval(anchorRow, row);
                }
		tree.setAnchorSelectionPath(lastAnchorPath);
		tree.setLeadSelectionPath(path);
	    }
	}
	/* Otherwise set the selection to just this interval. */
	else if(SynthLookAndFeel.isPrimaryMouseButton(event)) {
	    tree.setSelectionPath(path);
	    if(isToggleEvent(event)) {
		toggleExpandState(path);
	    }
	}
    }

    /**
     * Updates the lead row.
     */
    private void updateLeadRow() {
	leadRow = tree.getLeadSelectionRow();
    }

    /**
     * Returns the lead selection row. This is cached as painting makes
     * extensive use of this.
     */
    private int getLeadSelectionRow() {
	return leadRow;
    }

    /**
     * Invokes <code>repaint</code> on the JTree for the passed in TreePath,
     * <code>path</code>.
     */
    private void repaintPath(TreePath path) {
	if (path != null) {
	    Rectangle bounds = tree.getPathBounds(path);
	    if (bounds != null) {
		tree.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
	    }
	}
    }

    /**
     * Updates the TreeState in response to nodes expanding/collapsing.
     */
    private static class TreeExpansionHandler implements
                             TreeExpansionListener {
	/**
	 * Called whenever an item in the tree has been expanded.
	 */
	public void treeExpanded(TreeExpansionEvent event) {
            JTree tree = (JTree)event.getSource();
            // PENDING: needs to be updated.
            SynthTreeUI ui = (SynthTreeUI)tree.getUI();
            TreePath      path = event.getPath();

            ui.updateExpandedDescendants(path);
	}

	/**
	 * Called whenever an item in the tree has been collapsed.
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
            JTree tree = (JTree)event.getSource();
            SynthTreeUI ui = (SynthTreeUI)tree.getUI();
            TreePath path = event.getPath();

            ui.completeEditing();
            if (path != null && tree.isVisible(path)) {
                ui.treeState.setExpandedState(path, false);
                ui.updateLeadRow();
                ui.invalidateSize();
	    }
	}
    }


    /**
     * Updates the preferred size when scrolling (if necessary). This class
     * is used when FixedHeightLayoutCache is used by SynthTreeUI. Because
     * FixedHeightLayoutCache only returns the preferred width of the visible
     * paths, you need to revalidate on every move otherwise certain regions
     * may not be visible. This class does this.
     */
    private static class ComponentHandler extends ComponentAdapter implements
                 ActionListener {
	/**
         * Timer used when inside a scrollpane and the scrollbar is
	 * adjusting.
         */
	private javax.swing.Timer timer;
	/**
         * ScrollBar that is being adjusted.
         */
	private JScrollBar scrollBar;

        /**
         * Last SynthTreeUI that we were in.
         */
        private SynthTreeUI ui;


	public void componentMoved(ComponentEvent e) {
            JTree tree = (JTree)e.getSource();
            // PENDING: change me
            SynthTreeUI ui = (SynthTreeUI)tree.getUI();

            if (this.ui != ui) {
                adjust();
            }
	    if (timer == null) {
		JScrollPane scrollPane = (JScrollPane)SwingUtilities.
                                  getAncestorOfClass(JScrollPane.class, tree);

		if (scrollPane == null) {
		    ui.invalidateSize();
                }
		else {
		    JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

		    if (scrollBar == null || !scrollBar.getValueIsAdjusting()){
			// Try the horizontal scrollbar.
			if((scrollBar = scrollPane.getHorizontalScrollBar())
                                 != null && scrollBar.getValueIsAdjusting()) {
                            queue(ui, scrollBar);
                        }
			else {
			    ui.invalidateSize();
                        }
		    }
		    else {
                        queue(ui, scrollBar);
                    }
		}
	    }
	}

	/**
	 * Creates, if necessary, and starts a Timer to check if need to
	 * resize the bounds.
	 */
	private void queue(SynthTreeUI ui, JScrollBar bar) {
	    if (timer == null) {
		timer = new javax.swing.Timer(200, this);
		timer.setRepeats(true);
	    }
	    timer.start();
            this.ui = ui;
            this.scrollBar = bar;
	}

	/**
	 * Public as a result of Timer. If the scrollBar is null, or
	 * not adjusting, this stops the timer and updates the sizing.
	 */
	public void actionPerformed(ActionEvent ae) {
	    if (!scrollBar.getValueIsAdjusting()) {
                adjust();
            }
        }

        /**
         * Stops the timer and schedules a repaint/revalidate on the tree.
         */
        private void adjust() {
            if (timer != null) {
                timer.stop();
            }
            ui.invalidateSize();
            timer = null;
            scrollBar = null;
            ui = null;
	}
    }


    /**
     * Forwards all TreeModel events to the TreeState.
     */
    private class TreeModelHandler implements TreeModelListener {
	public void treeNodesChanged(TreeModelEvent e) {
	    if (treeState != null && e != null) {
		treeState.treeNodesChanged(e);

		TreePath       pPath = e.getTreePath().getParentPath();

		if (pPath == null || treeState.isExpanded(pPath)) {
		    invalidateSize();
                }
	    }
	}

	public void treeNodesInserted(TreeModelEvent e) {
	    if (treeState != null && e != null) {
		treeState.treeNodesInserted(e);

		updateLeadRow();

		TreePath       path = e.getTreePath();

		if (treeState.isExpanded(path)) {
		    invalidateSize();
		}
		else {
		    // PENDING(sky): Need a method in TreeModelEvent
		    // that can return the count, getChildIndices allocs
		    // a new array!
		    int[] indices = e.getChildIndices();
		    int childCount = tree.getModel().getChildCount
			                    (path.getLastPathComponent());

		    if (indices != null && (childCount - indices.length) == 0){
			invalidateSize();
                    }
		}
	    }
	}

	public void treeNodesRemoved(TreeModelEvent e) {
	    if (treeState != null && e != null) {
		treeState.treeNodesRemoved(e);

		updateLeadRow();

		TreePath       path = e.getTreePath();

		if (treeState.isExpanded(path) || tree.getModel().
                            getChildCount(path.getLastPathComponent()) == 0) {
		    invalidateSize();
                }
	    }
	}

	public void treeStructureChanged(TreeModelEvent e) {
	    if (treeState != null && e != null) {
		treeState.treeStructureChanged(e);

		updateLeadRow();

		TreePath       pPath = e.getTreePath();

                if (pPath != null) {
                    pPath = pPath.getParentPath();
                }
                if(pPath == null || treeState.isExpanded(pPath)) {
                    invalidateSize();
                }
	    }
	}
    }


    /**
     * Listens for changes in the selection model and updates the display
     * accordingly.
     */
    private class TreeSelectionHandler implements TreeSelectionListener {
	/**
	 * Messaged when the selection changes in the tree we're displaying
	 * for.  Stops editing, messages super and displays the changed paths.
	 */
	public void valueChanged(TreeSelectionEvent event) {
	    // Stop editing
            if (stopEditingWhenSelectionChanges) {
                completeEditing();
            }

	    // Make sure all the paths are visible, if necessary.
            TreeSelectionModel model = tree.getSelectionModel();
            TreeModel treeModel = tree.getModel();

	    if (tree.getExpandsSelectedPaths()) {
		TreePath[]           paths = model.getSelectionPaths();

		if (paths != null) {
		    for(int counter = paths.length - 1; counter >= 0;
			    counter--) {
                        TreePath path = paths[counter].getParentPath();
                        boolean expand = true;

                        while (path != null) {
                            // Indicates this path isn't valid anymore,
                            // we shouldn't attempt to expand it then.
                            if (treeModel != null && treeModel.isLeaf(
                                             path.getLastPathComponent())){
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

	    TreePath oldLead = tree.getLeadSelectionPath();
	    TreePath lead = tree.getSelectionModel().getLeadSelectionPath();

	    tree.setAnchorSelectionPath(lead);
	    tree.setLeadSelectionPath(lead);

	    TreePath[]       changedPaths = event.getPaths();
	    Rectangle        nodeBounds;
	    Rectangle        visRect = tree.getVisibleRect();
	    boolean          paintPaths = true;
	    int              nWidth = tree.getWidth();

	    if (changedPaths != null) {
		int              counter, maxCounter = changedPaths.length;

		if (maxCounter > 4) {
		    tree.repaint();
		    paintPaths = false;
		}
		else {
		    for (counter = 0; counter < maxCounter; counter++) {
			nodeBounds = getPathBounds(tree,
						   changedPaths[counter]);
			if (nodeBounds != null && visRect.
                                          intersects(nodeBounds)) {
			    tree.repaint(0, nodeBounds.y, nWidth,
					 nodeBounds.height);
                        }
		    }
		}
	    }
	    if (paintPaths) {
		nodeBounds = getPathBounds(tree, oldLead);
		if (nodeBounds != null && visRect.intersects(nodeBounds)) {
		    tree.repaint(0, nodeBounds.y, nWidth, nodeBounds.height);
                }
		nodeBounds = getPathBounds(tree, lead);
		if(nodeBounds != null && visRect.intersects(nodeBounds)) {
		    tree.repaint(0, nodeBounds.y, nWidth, nodeBounds.height);
                }
	    }
        }
    }


    /**
     * Listener responsible for getting cell editing events and updating
     * the tree accordingly.
     */
    class CellEditorHandler implements CellEditorListener {
	/** Messaged when editing has stopped in the tree. */
	public void editingStopped(ChangeEvent e) {
	    completeEditing(false, false, true);
	}

	/** Messaged when editing has been canceled in the tree. */
	public void editingCanceled(ChangeEvent e) {
	    completeEditing(false, false, false);
	}
    }


    /**
     * This class is used to handle selecting nodes based on a typed
     * sequence.
     */
    private static class KeyHandler extends KeyAdapter {
	public void keyTyped(KeyEvent e) {
            JTree tree = (JTree)e.getSource();

            if (e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
                return;
            }

	    // handle first letter navigation
	    if(tree != null && tree.getRowCount() > 0 && tree.hasFocus() &&
                               tree.isEnabled()) {
		boolean startingFromSelection = true;
		String prefix = Character.toString(e.getKeyChar());
		int startingRow = tree.getMinSelectionRow() + 1;

		if (startingRow >= tree.getRowCount()) {
		    startingFromSelection = false;
		    startingRow = 0;
		}
		TreePath path = tree.getNextMatch(prefix, startingRow,
						  Position.Bias.Forward);
		if (path != null) {
                    tree.setSelectionPath(path);
		}
                else if (startingFromSelection) {
		    path = tree.getNextMatch(prefix, 0,
					     Position.Bias.Forward);
		    if (path != null) {
			tree.setSelectionPath(path);
		    }
		}
	    }
	}
    }


    /**
     * Repaints the lead selection row when focus is lost/gained.
     */
    private static class FocusHandler implements FocusListener {
	/**
	 * Invoked when focus is activated on the tree we're in, redraws the
	 * lead row.
	 */
	public void focusGained(FocusEvent e) {
            paintLead((JTree)e.getSource());
	}

	/**
	 * Invoked when focus is activated on the tree we're in, redraws the
	 * lead row.
	 */
	public void focusLost(FocusEvent e) {
            paintLead((JTree)e.getSource());
	}

        /**
         * Pains the lead selection of the tree.
         */
        private void paintLead(JTree tree) {
            Rectangle                 bounds;

            bounds = tree.getPathBounds(tree.getLeadSelectionPath());
            if (bounds != null) {
                tree.repaint(bounds);
	    }
        }
    }


    /**
     * Class responsible for getting size of node
     */
    // This returns locations that don't include any Insets.
    private class NodeDimensionsHandler extends
	         AbstractLayoutCache.NodeDimensions {
	/**
	 * Responsible for getting the size of a particular node. Returned
         * location does not include insets, that is handled by SynthTreeUI.
	 */
	public Rectangle getNodeDimensions(Object value, int row,
					   int depth, boolean expanded,
					   Rectangle size) {
	    // Return size of editing component, if editing and asking
	    // for editing row.
            Component c = getEditingComponent();
            int editingRow = getEditingRow();

	    if (c != null && editingRow == row) {
		Dimension        prefSize = c.getPreferredSize();
		int              rh = tree.getRowHeight();

		if (rh > 0 && rh != prefSize.height) {
		    prefSize.height = rh;
                }
		if (size != null) {
		    size.x = getRowX(row, depth);
		    size.width = prefSize.width;
		    size.height = prefSize.height;
		}
		else {
		    size = new Rectangle(getRowX(row, depth), 0,
					 prefSize.width, prefSize.height);
		}
		return size;
	    }
	    // Not editing, use renderer.
            TreeCellRenderer renderer = tree.getCellRenderer();

	    if (renderer != null) {
		c = renderer.getTreeCellRendererComponent
		             (tree, value, tree.isRowSelected(row),
                              expanded, tree.getModel().isLeaf(value), row,
                              false);
                rendererPane.add(c);
                c.validate();
		Dimension        prefSize = c.getPreferredSize();

		if (size != null) {
		    size.x = getRowX(row, depth);
		    size.width = prefSize.width;
		    size.height = prefSize.height;
		}
		else {
		    size = new Rectangle(getRowX(row, depth), 0,
					 prefSize.width, prefSize.height);
		}
		return size;
	    }
	    return null;
	}
    }


    /**
     * TreeMouseListener is responsible for updating the selection
     * based on mouse events, it will also redispatch events to the editing
     * component.
     */
    private static class MouseHandler extends MouseAdapter implements
                              MouseMotionListener {
        /**
         * Indicates if in the process of doing a drag and drop session.
         * This affects how the selection will be changed.
         */
        private boolean inDND;
        /**
         * Destination Component that events are dispatched to.
         */
        private Component destination;

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {
	    if (!e.isConsumed()) {
		handleSelection(e);
                redispatchEventIfNecessary(e);
                inDND = false;
	    }
            else {
                inDND = true;
	    }
	}

        /**
         * Updates the selection as necessary based on the MoueEvent.
         */
        private void handleSelection(MouseEvent e) {
            JTree tree = (JTree)e.getSource();
            // PENDING: Change to new getUI method
            SynthTreeUI ui = (SynthTreeUI)tree.getUI();

	    if (tree != null && tree.isEnabled()) {
                if (tree.isEditing() && tree.getInvokesStopCellEditing() &&
                                        !tree.stopEditing()) {
                    return;
                }

                if (tree.isRequestFocusEnabled()) {
		    tree.requestFocus();
                }
		TreePath     path = tree.getClosestPathForLocation(e.getX(),
                                                                   e.getY());

		if (path != null) {
		    Rectangle bounds = tree.getPathBounds(path);

		    if (e.getY() > (bounds.y + bounds.height)) {
			return;
		    }

		    if (SynthLookAndFeel.isPrimaryMouseButton(e)) {
                        SynthContext context = ui.getContext(tree);
			ui.expandPathIfNecessary(context, path, e.getX(),
                                                 e.getY());
                        context.dispose();
                    }
		    
		    int x = e.getX();
		    if (x > bounds.x && x <= (bounds.x + bounds.width) && 
			    !ui.startEditing(path, e)) {
                        ui.selectPathForEvent(path, e);
		    }
		}
	    }
            cacheDestination(ui.getEditingComponent(), e.getX(), e.getY());
	}

        public void mouseDragged(MouseEvent e) {
            redispatchEventIfNecessary(e);
        }

        /**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 */
        public void mouseMoved(MouseEvent e) {
	}

        public void mouseReleased(MouseEvent e) {
	    if (!e.isConsumed() && inDND) {
		handleSelection(e);
                redispatchEventIfNecessary(e);
	    }
        }

        /**
         * Caches the component used to redirect events to.
         */
        private void cacheDestination(Component editingComponent, int x,
                                      int y) {
            if (editingComponent != null) {
                destination = SwingUtilities.getDeepestComponentAt(
                                             editingComponent, x, y);
            }
            else {
                destination = null;
            }
        }

        /**
         * Dispatches the MouseEvent to the current destination.
         */
        private void redispatchEventIfNecessary(MouseEvent e) {
            if (destination != null) {
                JTree tree = (JTree)e.getSource();

                destination.dispatchEvent(SwingUtilities.convertMouseEvent
					  (tree, e, destination));
            }
        }
    }


    // 
    // Actions used for keyboard navigation
    //

    private abstract static class GenericTreeAction extends AbstractAction {
	/**
         * Specifies the direction to adjust the selection by, intepretation of
         * this is left to subclasses.
         */
	protected int         direction;
	/**
         * True indicates should set selection from anchor path.
         */
	protected boolean       addToSelection;
        /**
         * True indicates the selection should be reset.
         */
	protected boolean       changeSelection;

        GenericTreeAction(String name) {
            super(name);
        }

        GenericTreeAction(String name, int direction, boolean addToSelection,
                          boolean changeSelection) {
            super(name);
            this.direction = direction;
            this.addToSelection = addToSelection;
            this.changeSelection = changeSelection;
        }

	public void actionPerformed(ActionEvent e) {
            JTree tree = (JTree)e.getSource();
	    int rowCount = tree.getRowCount();

            if (rowCount > 0) {
                actionPerformed(e, tree, rowCount);
            }
        }

        protected abstract void actionPerformed(ActionEvent e, JTree tree,
                                                int rowCount);

        protected void adjustSelection(JTree tree, TreePath path) {
            if (addToSelection) {
                extendSelection(tree, path);
            }
            else if(changeSelection) {
                tree.setSelectionPath(path);
            }
            else {
                tree.setLeadSelectionPath(path);
            }
        }

        /**
         * Extends the selection from the anchor to make <code>newLead</code>
         * the lead of the selection. This does not scroll.
         */
        private void extendSelection(JTree tree, TreePath newLead) {
            TreePath aPath = tree.getAnchorSelectionPath();
            int aRow = (aPath == null) ? -1 : tree.getRowForPath(aPath);
            int newIndex = tree.getRowForPath(newLead);

            if(aRow == -1) {
                tree.setSelectionRow(newIndex);
            }
            else {
                if (aRow < newIndex) {
                    tree.setSelectionInterval(aRow, newIndex);
                }
                else {
                    tree.setSelectionInterval(newIndex, aRow);
                }
                tree.setAnchorSelectionPath(aPath);
                tree.setLeadSelectionPath(newLead);
            }
        }
    }

    /**
     * <code>TraverseAction</code> is the action used for left/right keys.
     * Will toggle the expandedness of a node, as well as potentially
     * incrementing the selection.
     */
    private static class TraverseAction extends GenericTreeAction {
        TraverseAction(String name, int direction, boolean changeSelection) {
            super(name, direction, false, changeSelection);
	}

	protected void actionPerformed(ActionEvent e, JTree tree,
                                       int rowCount) {
            int minSelIndex = tree.getLeadSelectionRow();
            int newIndex;
            TreeModel model = tree.getModel();

            if (minSelIndex == -1) {
                newIndex = 0;
            }
            else {
                // PENDING: needs to be updated.
                SynthTreeUI ui = (SynthTreeUI)tree.getUI();
                TreePath path = tree.getPathForRow(minSelIndex);

                // Try and expand the node, otherwise go to next
                // node.
                if (direction == 1) {
                    if (!model.isLeaf(path.getLastPathComponent()) &&
                               !tree.isExpanded(minSelIndex)) {
                        ui.toggleExpandState(path);
                        newIndex = -1;
                    }
                    else {
                        newIndex = Math.min(minSelIndex + 1, rowCount - 1);
                    }
                }
                // Try to collapse node.
                else {
                    if (!model.isLeaf(path.getLastPathComponent()) &&
                               tree.isExpanded(minSelIndex)) {
                        ui.toggleExpandState(tree.getPathForRow(minSelIndex));
                        newIndex = -1;
                    }
                    else {
                        path = tree.getPathForRow(minSelIndex);
                        if (path != null && path.getPathCount() > 1) {
                            newIndex = tree.getRowForPath(
                                            path.getParentPath());
                        }
                        else {
                            newIndex = -1;
                        }
                    }
                }
            }
            if (newIndex != -1) {
                if (changeSelection) {
                    tree.setSelectionInterval(newIndex, newIndex);
                }
                else {
                    tree.setLeadSelectionPath(tree.getPathForRow(newIndex));
                }
                ensureRowsAreVisible(tree, newIndex, newIndex);
	    }
	}
    }


    /**
     * ScrollAction handles scrolling left/right/up/down and changing
     * the selection.
     */
    private static class ScrollAndSelectAction extends GenericTreeAction {
	private ScrollAndSelectAction(String name, int direction,
                                      boolean
                                      addToSelection, boolean changeSelection){
            super(name, direction, addToSelection, changeSelection);
	}

	protected void actionPerformed(ActionEvent e, JTree tree,
                                       int rowCount) {
            Dimension maxSize = tree.getSize();
            TreePath lead = tree.getLeadSelectionPath();
            TreePath newPath = null;
            Rectangle visRect = tree.getVisibleRect();
            Rectangle newRect;

            switch(direction) {
            case SwingConstants.NORTH:
                // up.
                newPath = tree.getClosestPathForLocation(visRect.x, visRect.y);
                if (newPath.equals(lead)) {
                    visRect.y = Math.max(0, visRect.y - visRect.height);
                    newPath = tree.getClosestPathForLocation(visRect.x,
                                                             visRect.y);
                }
                newRect = tree.getPathBounds(newPath);
                visRect.y = newRect.y;
                break;
            case SwingConstants.SOUTH:
                // down
                visRect.y = Math.min(maxSize.height, visRect.y +
                                     visRect.height - 1);
                newPath = tree.getClosestPathForLocation(visRect.x, visRect.y);
                if (newPath.equals(lead)) {
                    visRect.y = Math.min(maxSize.height, visRect.y +
                                         visRect.height - 1);
                    newPath = tree.getClosestPathForLocation(visRect.x,
                                                             visRect.y);
                }
                newRect = tree.getPathBounds(newPath);
                visRect.y = newRect.y - (visRect.height - newRect.height);
                break;
            case SwingConstants.WEST:
                newPath = tree.getClosestPathForLocation(visRect.x, visRect.y);
                visRect.x = Math.max(0, visRect.x - visRect.width);
                break;
            case SwingConstants.EAST:
                visRect.x = Math.min(Math.max(0, tree.getWidth() -
				   visRect.width), visRect.x + visRect.width);
                newPath = tree.getClosestPathForLocation(visRect.x,
						 visRect.y + visRect.height);
                break;
            }
            adjustSelection(tree, newPath);
            tree.scrollRectToVisible(visRect);
	}
    }


    /**
     * Moves the selection up/down.
     */
    private static class IncrementAction extends GenericTreeAction  {
        IncrementAction(String name, int direction, boolean addToSelection,
                            boolean changeSelection) {
            super(name);
	    this.direction = direction;
	    this.addToSelection = addToSelection;
	    this.changeSelection = changeSelection;
	}

	protected void actionPerformed(ActionEvent e, JTree tree,
                                       int rowCount) {
            int selIndex = tree.getLeadSelectionRow();
            int newIndex;
            TreeSelectionModel selectionModel = tree.getSelectionModel();

            if (selIndex == -1) {
                if (direction == 1) {
                    newIndex = 0;
                }
                else {
                    newIndex = rowCount - 1;
                }
            }
            else {
                //  Aparently people don't like wrapping;(
                newIndex = Math.min(rowCount - 1, Math.max
                                    (0, (selIndex + direction)));
            }
            adjustSelection(tree, tree.getPathForRow(newIndex));
            ensureRowsAreVisible(tree, newIndex, newIndex);
	}
    }


    /**
      * HomeAction is used to handle end/home actions.
      * Scrolls either the first or last cell to be visible based on
      * direction.
      */
    private static class HomeAction extends GenericTreeAction {
        HomeAction(String name, int direction, boolean addToSelection,
                   boolean changeSelection) {
            super(name, direction, addToSelection, changeSelection);
	}

	protected void actionPerformed(ActionEvent e, JTree tree,
                                       int rowCount) {
            if (direction == -1) {
                ensureRowsAreVisible(tree, 0, 0);
                if (addToSelection) {
                    TreePath aPath = tree.getAnchorSelectionPath();
                    int aRow = (aPath == null) ? -1 :tree.getRowForPath(aPath);

                    if (aRow == -1) {
                        tree.setSelectionInterval(0, 0);
                    }
                    else {
                        tree.setSelectionInterval(0, aRow);
                        tree.setAnchorSelectionPath(aPath);
                        tree.setLeadSelectionPath(tree.getPathForRow(0));
                    }
                }
                else if (changeSelection) {
                    tree.setSelectionInterval(0, 0);
                }
                else {
                    tree.setLeadSelectionPath(tree.getPathForRow(0));
                }
            }
            else {
                ensureRowsAreVisible(tree, rowCount - 1, rowCount - 1);
                if (addToSelection) {
                    TreePath        aPath = tree.getAnchorSelectionPath();
                    int             aRow = (aPath == null) ? -1 :
			                   tree.getRowForPath(aPath);

                    if (aRow == -1) {
                        tree.setSelectionInterval(rowCount - 1, rowCount -1);
                    }
                    else {
                        tree.setSelectionInterval(aRow, rowCount - 1);
                        tree.setAnchorSelectionPath(aPath);
                        tree.setLeadSelectionPath(tree.getPathForRow(
                                             rowCount -1));
                    }
                }
                else if (changeSelection) {
                    tree.setSelectionInterval(rowCount - 1, rowCount - 1);
                }
                else {
                    tree.setLeadSelectionPath(tree.getPathForRow(
                                         rowCount - 1));
                }
            }
        }
    }


    /**
     * Toggles the expanded state of the first row.
      */
    private static class ToggleAction extends GenericTreeAction {
	ToggleAction(String name) {
            super(name);
	}

	protected void actionPerformed(ActionEvent e, JTree tree,
                                       int rowCount) {
            int            selRow = tree.getLeadSelectionRow();

            if (selRow != -1) {
                TreePath path = tree.getPathForRow(selRow);

                if (!tree.getModel().isLeaf(path.getLastPathComponent())) {
		    TreePath aPath = tree.getAnchorSelectionPath();
		    TreePath lPath = tree.getLeadSelectionPath();

                    // PENDING:
		    ((SynthTreeUI)tree.getUI()).toggleExpandState(path);
		    tree.setAnchorSelectionPath(aPath);
		    tree.setLeadSelectionPath(lPath);
		}
	    }
	}
    }


    /**
     * Scrolls the component it is created with a specified amount.
     */
    private static class ScrollAction extends AbstractAction {
        /**
         * Direction to scroll.
         */
	private int direction;
        /**
         * Amount to scroll.
         */
	private int amount;

	public ScrollAction(String name, int direction, int amount) {
            super(name);
	    this.direction = direction;
	    this.amount = amount;
	}

	public void actionPerformed(ActionEvent e) {
            JComponent component = (JComponent)e.getSource();
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
    // PENDING: Make this static when isEnabled can be changed.
    private class CancelEditingAction extends AbstractAction {
	public CancelEditingAction(String name) {
            super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    if (tree != null) {
		tree.cancelEditing();
	    }
	}

        // PENDING: Need an isEnabled that takes a Component
	public boolean isEnabled() { return (tree != null &&
					     tree.isEnabled() &&
                                             isEditing(tree)); }
    }


    /**
     * ActionListener invoked to start editing on the leadPath.
     */
    private static class EditAction extends GenericTreeAction {
	public EditAction(String name) {
            super(name);
	}

	protected void actionPerformed(ActionEvent ae, JTree tree,
                                       int rowCount) {
            TreePath path = tree.getLeadSelectionPath();

            if (path != null) {
                tree.startEditingAtPath(path);
	    }
	}
    }


    /**
     * Action to select everything in tree.
     */
    private static class SelectAllAction extends GenericTreeAction {
        /**
         * If true, everything is selected, if false, the selection is cleared.
         */
	private boolean       selectAll;

	public SelectAllAction(String name, boolean selectAll) {
            super(name);
	    this.selectAll = selectAll;
	}

	protected void actionPerformed(ActionEvent ae, JTree tree,
                                       int rowCount) {
            if (selectAll) {
                TreePath lastPath = tree.getLeadSelectionPath();
                TreePath aPath = tree.getAnchorSelectionPath();

                if (lastPath != null && !tree.isVisible(lastPath)) {
                    lastPath = null;
                }
                tree.setSelectionInterval(0, rowCount - 1);
                if (lastPath != null) {
                    tree.setLeadSelectionPath(lastPath);
                }
                if (aPath != null && tree.isVisible(aPath)) {
                    tree.setAnchorSelectionPath(aPath);
                }
                else if (lastPath != null) {
                    tree.setAnchorSelectionPath(lastPath);
                }
            }
            else {
                TreePath lastPath = tree.getLeadSelectionPath();
                TreePath aPath = tree.getAnchorSelectionPath();

                tree.clearSelection();
                tree.setAnchorSelectionPath(aPath);
                tree.setLeadSelectionPath(lastPath);
            }
        }
    }


    /**
     * Toggles the selection of the lead selected row.
     */
    private static class AddSelectionAction extends GenericTreeAction {
	private boolean       changeAnchor;

        AddSelectionAction(String name, boolean changeAnchor) {
            super(name);
	    this.changeAnchor = changeAnchor;
	}

	protected void actionPerformed(ActionEvent ae, JTree tree,
                                       int rowCount) {
            int lead = tree.getLeadSelectionRow();
            TreePath aPath = tree.getAnchorSelectionPath();
            TreePath lPath = tree.getLeadSelectionPath();

            if (lead == -1) {
                lead = 0;
            }
            if (!changeAnchor) {
                if (tree.isRowSelected(lead)) {
                    tree.removeSelectionRow(lead);
                    tree.setLeadSelectionPath(lPath);
                }
                else {
                    tree.addSelectionRow(lead);
                }
                tree.setAnchorSelectionPath(aPath);
            }
            else {
                tree.setSelectionRow(lead);
            }
        }
    }


    /**
     * Extends the selection from the anchor to the lead.
     */
    private static class ExtendSelectionAction extends GenericTreeAction {
        ExtendSelectionAction(String name) {
            super(name);
	}

	protected void actionPerformed(ActionEvent ae, JTree tree,
                                       int rowCount) {
            int lead = tree.getLeadSelectionRow();

            if (lead != -1) {
                TreePath leadP = tree.getLeadSelectionPath();
                TreePath aPath = tree.getAnchorSelectionPath();
                int aRow = tree.getRowForPath(aPath);

                if (aRow == -1) {
                    aRow = 0;
                }
                tree.setSelectionInterval(aRow, lead);
                tree.setLeadSelectionPath(leadP);
                tree.setAnchorSelectionPath(aPath);
	    }
	}
    }


    private static class SynthTreeCellEditor extends DefaultTreeCellEditor {
        public SynthTreeCellEditor(JTree tree,
                                   DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
            setBorderSelectionColor(null);
        }

        protected TreeCellEditor createTreeCellEditor() {
            // PENDING: should custom border be allowed from style?
            JTextField tf = new JTextField();
            tf.setName("Tree.cellEditor");
            // PENDING: rethink this
            ((SynthTextUI)tf.getUI()).forceFetchStyle(tf);

            DefaultCellEditor editor = new DefaultCellEditor(tf);

            // One click to edit.
            editor.setClickCountToStart(1);
            return editor;
        }
    }

    /**
     * Used to track the editing state.
     */
    private static class EditingState {
        /**
         * Component doing the editing.
         */
        public Component component;

        /**
         * Path being edited.
         */
        public TreePath path;

        /**
         *   Row that is being edited.
         */
        public int row;

        /**
         * Indicates if the editor has a different size than the renderer.
         */
        public boolean editorHasDifferentSize;

        /**
         * The editor.
         */
        public CellEditor editor;
    }


    private class SynthTreeCellRenderer extends DefaultTreeCellRenderer
                               implements UIResource {
        SynthTreeCellRenderer() {
            setName("Tree.cellRenderer");
            // PENDING: rethink this, we shouldn't have to force this.
            ((SynthLabelUI)getUI()).forceFetchStyle(this);
        }
        public void paint(Graphics g) {
            paintComponent(g);
            if (hasFocus) {
                SynthContext context = getContext(tree, Region.TREE_CELL);

                if (context.getStyle() == null) {
                    assert false: "SynthTreeCellRenderer is being used " +
                        "outside of UI that created it";
                    return;
                }
                SynthPainter painter = (SynthPainter)context.getStyle().
                                           get(context, "focus");
                if (painter != null) {
                    int imageOffset = 0;
                    Icon currentI = getIcon();

                    if(currentI != null && getText() != null) {
                        imageOffset = currentI.getIconWidth() +
                                          Math.max(0, getIconTextGap() - 1);
                    }
                    if (selected) {
                        context.setComponentState(ENABLED | SELECTED);
                    }
                    else {
                        context.setComponentState(ENABLED);
                    }
                    if(getComponentOrientation().isLeftToRight()) {
                        painter.paint(context, "focus", g, imageOffset, 0,
                                      getWidth() - imageOffset, getHeight());
                    }
                    else {
                        painter.paint(context, "focus", g, 0, 0, getWidth() -
                                      imageOffset, getHeight());
                    }
                }
                context.dispose();
            }
        }
    }

    /**
     * Drag gesture recognizer for JTree components
     */
    static class TreeDragGestureRecognizer extends SynthDragGestureRecognizer {

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

    /**
     * A DropTargetListener to extend the default Swing handling of drop operations
     * by moving the tree selection to the nearest location to the mouse pointer.
     * Also adds autoscroll capability.
     */
    static class TreeDropTargetListener extends SynthDropTargetListener {

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
            TreeUI ui = tree.getUI();
            TreePath path = ui.getClosestPathForLocation(tree, p.x, p.y);
            if (path != null) {
                tree.setSelectionPath(path);
            }
        }

        private int[] selectedIndices;
    }

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
                
                return new SynthTransferable(plainBuf.toString(), htmlBuf.toString());
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
