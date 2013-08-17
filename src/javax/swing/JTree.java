/*
 * @(#)JTree.java	1.78 98/05/11
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.*;
import javax.accessibility.*;


/**
 * A control that displays a set of hierarchical data as an outline.
 * A specific node can be identified either by a TreePath (an object
 * that encapsulates a node and all of its ancestors), or by its
 * display row, where each row in the display area displays one node.
 * <p>
 * An <i>expanded</i> node is one displays its children. A <i>collapsed</i>
 * node is one which hides them. A <i>hidden</i> node is one which is
 * under a collapsed parent. A <i>viewable</i> node is under a collapsed 
 * parent, but may or may not be displayed. A <i>displayed</i> node
 * is both viewable and in the display area, where it can be seen.
 * <p>
 * These JTree methods use "visible" to mean "displayed":<ul>
 * <li><code>isRootVisible()</code>
 * <li><code>setRootVisible()</code>
 * <li><code>scrollPathToVisible()</code>
 * <li><code>scrollRowToVisible()</code>
 * <li><code>getVisibleRowCount()</code>
 * <li><code>setVisibleRowCount()</code>
 * </ul>
 * <p>
 * These JTree methods use "visible" to mean "viewable" (under an
 * expanded parent):<ul>
 * <li><code>isVisible()</code>
 * <li><code>makeVisible()</code>
 * </ul>
 * <p>
 * If you are interested in knowing when the selection changes implement
 * the TreeSelectionListener interface and add the instance using the
 * method addTreeSelectionListener. valueChanged will be invoked when the
 * selection changes, that is if the user clicks twice on the same
 * node valueChanged will only be invoked once.
 * <p>
 * If you are interested in knowing either double clicks events or when
 * a user clicks on a node, regardless of whether or not it was selected
 * it is recommended you do the following:
 * <pre>
 * final JTree tree = ...;
 *
 * MouseListener ml = new MouseAdapter() {
 *     public void <b>mouseClicked</b>(MouseEvent e) {
 *         int selRow = tree.getRowForLocation(e.getX(), e.getY());
 *         TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
 *         if(selRow != -1) {
 *             if(e.getClickCount() == 1) {
 *                 mySingleClick(selRow, selPath);
 *             }
 *             else if(e.getClickCount() == 2) {
 *                 myDoubleClick(selRow, selPath);
 *             }
 *         }
 *     }
 * };
 * tree.addMouseListener(ml);
 * </pre>
 * NOTE: This example obtains both the path and row, but you only need to
 * get the one you're interested in.
 * <p>
 * To use JTree to display compound nodes (for example, nodes containing both
 * a graphic icon and text), subclass {@link TreeCellRenderer} and use 
 * {@link #setTreeCellRenderer} to tell the tree to use it. To edit such nodes,
 * subclass {@link TreeCellEditor} and use {@link #setTreeCellEditor}.
 * <p>
 * Like all JComponent classes, you can use {@link JComponent#registerKeyboardAction}
 * to associate an {@link Action} object with a {@link KeyStroke} and execute the
 * action under specified conditions.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/tree.html">How to Use Trees</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JTree">JTree</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer false
 *
 * @version 1.78 05/11/98
 * @author Rob Davis
 * @author Ray Ryan
 * @author Scott Violet
 */
public class JTree extends JComponent implements Scrollable, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TreeUI";

    /**
     * The model that defines the tree displayed by this object.
     */
    transient protected TreeModel        treeModel;

    /**
     * Models the set of selected nodes in this tree.
     */
    transient protected TreeSelectionModel selectionModel;

    /**
     * True if the root node is displayed, false if its children are
     * the highest visible nodes.
     */
    protected boolean                    rootVisible;

    /**
     * The cell used to draw nodes. If null, the UI uses a default
     * cellRenderer.
     */
    transient protected TreeCellRenderer  cellRenderer;

    /**
     * Height to use for each display row. If this is <= 0 the renderer 
     * determines the height for each row.
     */
    protected int                         rowHeight;

    /**
     * Maps from TreePath to Boolean indicating whether or not the
     * pareticular path is expanded. This ONLY indicates whether a 
     * given path is expanded, and NOT if it is visible or not. That
     * information must be determined by visiting all the parent
     * paths and seeing if they are visible.
     */
    transient private Hashtable           expandedState;


    /**
     * True if handles are displayed at the topmost level of the tree.
     * <p>
     * A handle is a small icon that displays adjacent to the node which 
     * allows the user to click once to expand or collapse the node. A
     * common interface shows a plus sign (+) for a node which can be
     * expanded and a minus sign (-) for a node which can be collapsed.
     * Handles are always shown for nodes below the topmost level.
     * <p>
     * If the <code>rootVisible</code> setting specifies that the root 
     * node is to be displayed, then that is the only node at the topmost
     * level. If the root node is not displayed, then all of its 
     * children are at the topmost level of the tree. Handles are 
     * always displayed for nodes other than the topmost.
     * <p> 
     * If the root node isn't visible, it is generally a good to make 
     * this value true. Otherwise, the tree looks exactly like a list,
     * and users may not know that the "list entries" are actually
     * tree nodes.
     *
     * @see #rootVisible
     */
    protected boolean           showsRootHandles;

    /**
     * Creates a new event and passed it off the selectionListeners.
     */
    protected transient TreeSelectionRedirector selectionRedirector;

    /**
     * Editor for the entries.  Default is null (tree is not editable).
     */
    transient protected TreeCellEditor          cellEditor;

    /**
     * Is the tree editable? Default is false.
     */
    protected boolean                 editable;

    /**
     * Is this tree a large model? This is a code-optimization setting.
     * A large model can be used when the cell height is the same for all
     * nodes. The UI will then cache very little information and instead
     * continually message the model. Without a large model the UI caches 
     * most of the information, resulting in fewer method calls to the model.
     * <p>
     * This value is only a suggestion to the UI. Not all UIs will
     * take advantage of it. Default value is false.
     */
    protected boolean                 largeModel;

    /**
     * Number of rows to make visible at one time. This value is used for
     * the Scrollable interface. It determines the preferred size of the 
     * display area.
     */
    protected int                     visibleRowCount;

    /**
     * If true, when editing is to be stopped by way of selection changing,
     * data in tree changing or other means stopCellEditing is invoked, and
     * changes are saved. If false, cancelCellEditing is invoked, and changes
     * are discarded. Default is false.
     */
    protected boolean                 invokesStopCellEditing;

    /**
     * If true, when a node is expanded, as many of the descendants are 
     * scrolled to be visible.
     */
    protected boolean                 scrollsOnExpand;

    /**
     * Number of mouse clicks before a node is expanded.
     */
    protected int                     toggleClickCount;

    /**
     * Updates the expandedState.
     */
    transient protected TreeModelListener       treeModelListener;

    /**
     * Used when setExpandedState is invoked, will be a Stack of Stacks.
     */
    transient private Stack           expandedStack;

    /**
     * Max number of stacks to keep around.
     */
    private static int                TEMP_STACK_SIZE = 11;

    //
    // Bound propery names
    //
    /** Bound property name for cellRenderer. */
    public final static String        CELL_RENDERER_PROPERTY = "cellRenderer";
    /** Bound property name for treeModel. */
    public final static String        TREE_MODEL_PROPERTY = "treeModel";
    /** Bound property name for rootVisible. */
    public final static String        ROOT_VISIBLE_PROPERTY = "rootVisible";
    /** Bound property name for showsRootHandles. */
    public final static String        SHOWS_ROOT_HANDLES_PROPERTY = "showsRootHandles";
    /** Bound property name for rowHeight. */
    public final static String        ROW_HEIGHT_PROPERTY = "rowHeight";
    /** Bound property name for cellEditor. */
    public final static String        CELL_EDITOR_PROPERTY = "cellEditor";
    /** Bound property name for editable. */
    public final static String        EDITABLE_PROPERTY = "editable";
    /** Bound property name for largeModel. */
    public final static String        LARGE_MODEL_PROPERTY = "largeModel";
    /** Bound property name for selectionModel. */
    public final static String        SELECTION_MODEL_PROPERTY = "selectionModel";
    /** Bound property name for visibleRowCount. */
    public final static String        VISIBLE_ROW_COUNT_PROPERTY = "visibleRowCount";
    /** Bound property name for messagesStopCellEditing. */
    public final static String        INVOKES_STOP_CELL_EDITING_PROPERTY = "messagesStopCellEditing";
    /** Bound property name for scrollsOnExpand. */
    public final static String        SCROLLS_ON_EXPAND_PROPERTY = "scrollsOnExpand";
    /** Bound property name for toggleClickCount. */
    //public final static String        TOGGLE_CLICK_COUNT_PROPERTY = "toggleClickCount";


    /**
     * Creates and returns a sample TreeModel. Used primarily for beanbuilders.
     * to show something interesting.
     *
     * @return the default TreeModel
     */
    protected static TreeModel getDefaultTreeModel() {
        DefaultMutableTreeNode      root = new DefaultMutableTreeNode("JTree");
	DefaultMutableTreeNode      parent;

	parent = new DefaultMutableTreeNode("colors");
	root.add(parent);
	parent.add(new DefaultMutableTreeNode("blue"));
	parent.add(new DefaultMutableTreeNode("violet"));
	parent.add(new DefaultMutableTreeNode("red"));
	parent.add(new DefaultMutableTreeNode("yellow"));

	parent = new DefaultMutableTreeNode("sports");
	root.add(parent);
	parent.add(new DefaultMutableTreeNode("basketball"));
	parent.add(new DefaultMutableTreeNode("soccer"));
	parent.add(new DefaultMutableTreeNode("football"));
	parent.add(new DefaultMutableTreeNode("hockey"));

	parent = new DefaultMutableTreeNode("food");
	root.add(parent);
	parent.add(new DefaultMutableTreeNode("hot dogs"));
	parent.add(new DefaultMutableTreeNode("pizza"));
	parent.add(new DefaultMutableTreeNode("ravioli"));
	parent.add(new DefaultMutableTreeNode("bananas"));
        return new DefaultTreeModel(root);
    }

    /**
     * Returns a TreeModel wrapping the specified object. If the object
     * is:<ul>
     * <li>an array of Objects,
     * <li>a Hashtable, or
     * <li>a Vector
     * </ul>then a new root node is created with each of the incoming 
     * objects as children. Otherwise, a new root is created with the 
     * specified object as its value.
     *
     * @param value  the Object used as the foundation for the TreeModel
     * @return a TreeModel wrapping the specified object
     */
    protected static TreeModel createTreeModel(Object value) {
        DefaultMutableTreeNode           root;

        if((value instanceof Object[]) || (value instanceof Hashtable) ||
           (value instanceof Vector)) {
            root = new DefaultMutableTreeNode("root");
            DynamicUtilTreeNode.createChildren(root, value);
        }
        else {
            root = new DynamicUtilTreeNode("root", value);
        }
        return new DefaultTreeModel(root, false);
    }

    /**
     * Returns a JTree with a sample model.
     * The default model used by the tree defines a leaf node as any node without
     * children.
     *
     * @return a JTree with the default model, which defines a leaf node
     *         as any node without children.
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public JTree() {
        this(getDefaultTreeModel());
    }

    /**
     * Returns a JTree with each element of the specified array as the
     * child of a new root node which is not displayed.
     * By default, the tree defines a leaf node as any node without
     * children.
     *
     * @param value  an array of Objects
     * @return a JTree with the contents of the array as children of
     *         the root node
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public JTree(Object[] value) {
        this(createTreeModel(value));
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
    }

    /**
     * Returns a JTree with each element of the specified Vector as the
     * child of a new root node which is not displayed. By default, the
     * tree defines a leaf node as any node without children.
     *
     * @param value  a Vector
     * @return a JTree with the contents of the Vector as children of
     *         the root node
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public JTree(Vector value) {
        this(createTreeModel(value));
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
    }

    /**
     * Returns a JTree created from a Hashtable which does not display
     * the root. Each value-half of the key/value pairs in the HashTable
     * becomes a child of the new root node. By default, the tree defines
     * a leaf node as any node without children.
     *
     * @param value  a Hashtable
     * @return a JTree with the contents of the Hashtable as children of
     *         the root node
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public JTree(Hashtable value) {
        this(createTreeModel(value));
        this.setRootVisible(false);
        this.setShowsRootHandles(true);
    }

    /**
     * Returns a JTree with the specified TreeNode as its root, which  
     * displays the root node. By default, the tree defines a leaf node as any node
     * without children.
     *
     * @param root  a TreeNode object
     * @return a JTree with the specified root node
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public JTree(TreeNode root) {
        this(root, false);
    }

    /**
     * Returns a JTree with the specified TreeNode as its root, which 
     * displays the root node and which decides whether a node is a 
     * leaf node in the specified manner.
     *
     * @param root  a TreeNode object
     * @param asksAllowsChildren  if false, any node without children is a 
     *              leaf node. If true, only nodes that do not allow 
     *              children are leaf nodes.
     * @return a JTree with the specified root node
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public JTree(TreeNode root, boolean asksAllowsChildren) {
        this(new DefaultTreeModel(root, asksAllowsChildren));
    }

    /**
     * Returns an instance of JTree which displays the root node 
     * -- the tree is created using the specified data model.
     *
     * @param newModel  the TreeModel to use as the data model
     * @return a JTree based on the TreeModel
     */
    public JTree(TreeModel newModel) {
        super();
	expandedStack = new Stack();
	toggleClickCount = 2;
	expandedState = new Hashtable();
        setLayout(null);
        rowHeight = 16;
        visibleRowCount = 20;
        rootVisible = true;
        selectionModel = new DefaultTreeSelectionModel();
        cellRenderer = null;
	scrollsOnExpand = true;
        setOpaque(true);
        updateUI();
        setModel(newModel);
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the TreeUI object that renders this component
     */
    public TreeUI getUI() {
        return (TreeUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the TreeUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(TreeUI ui) {
        if ((TreeUI)this.ui != ui) {
            super.setUI(ui);
            repaint();
        }
    }

    /**
     * Notification from the UIManager that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * UIManager.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((TreeUI)UIManager.getUI(this));
        invalidate();
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "TreeUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Returns the current TreeCellRenderer that is rendering each cell.
     *
     * @return the TreeCellRenderer that is rendering each cell
     */
    public TreeCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    /**
     * Sets the TreeCellRenderer that will be used to draw each cell.
     *
     * @param x  the TreeCellRenderer that is to render each cell
     * @beaninfo
     *        bound: true
     *  description: The TreeCellRenderer that will be used to draw
     *               each cell.
     */
    public void setCellRenderer(TreeCellRenderer x) {
        TreeCellRenderer oldValue = cellRenderer;

        cellRenderer = x;
        firePropertyChange(CELL_RENDERER_PROPERTY, oldValue, cellRenderer);
        invalidate();
    }

    /**
      * Determines whether the tree is editable. Fires a property
      * change event if the new setting is different from the existing
      * setting.
      *
      * @param flag  a boolean value, true if the tree is editable
      * @beaninfo
      *        bound: true
      *  description: Whether the tree is editable.
      */
    public void setEditable(boolean flag) {
        boolean                 oldValue = this.editable;

        this.editable = flag;
        firePropertyChange(EDITABLE_PROPERTY, oldValue, flag);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_STATE_PROPERTY, 
                (oldValue ? AccessibleState.EDITABLE : null),
                (flag ? AccessibleState.EDITABLE : null));
        }
    }

    /**
     * Returns true if the tree is editable.
     *
     * @return true if the tree is editable.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Sets the cell editor.  A null value implies that the
     * tree cannot be edited.  If this represents a change in the
     * cellEditor, the propertyChange method is invoked on all
     * listeners.
     *
     * @param cellEditor the TreeCellEditor to use
     * @beaninfo
     *        bound: true
     *  description: The cell editor. A null value implies the tree
     *               cannot be edited.
     */
    public void setCellEditor(TreeCellEditor cellEditor) {
        TreeCellEditor        oldEditor = this.cellEditor;

        this.cellEditor = cellEditor;
        firePropertyChange(CELL_EDITOR_PROPERTY, oldEditor, cellEditor);
        invalidate();
    }

    /**
     * Returns the editor used to edit entries in the tree.
     *
     * @return the TreeCellEditor in use, or null if the tree cannot
     *         be edited
     */
    public TreeCellEditor getCellEditor() {
        return cellEditor;
    }

    /**
     * Returns the TreeModel that is providing the data.
     *
     * @return the TreeModel that is providing the data
     */
    public TreeModel getModel() {
        return treeModel;
    }

    /**
     * Sets the TreeModel that will provide the data.
     *
     * @param newModel the TreeModel that is to provide the data
     * @beaninfo
     *        bound: true
     *  description: The TreeModel that will provide the data.
     */
    public void setModel(TreeModel newModel) {
        TreeModel oldModel = treeModel;

	if(treeModel != null && treeModelListener != null)
	    treeModel.removeTreeModelListener(treeModelListener);

        if (accessibleContext != null) {
	    if (treeModel != null) {
                treeModel.removeTreeModelListener((TreeModelListener)accessibleContext);
	    }
            if (newModel != null) {
	        newModel.addTreeModelListener((TreeModelListener)accessibleContext);
	    }
        }

        treeModel = newModel;
	clearToggledPaths();
	if(treeModel != null) {
	    if(treeModelListener == null)
		treeModelListener = createTreeModelListener();
	    if(treeModelListener != null)
		treeModel.addTreeModelListener(treeModelListener);
	    // Mark the root as expanded, if it isn't a leaf.
	    if(!treeModel.isLeaf(treeModel.getRoot()))
		expandedState.put(new TreePath(treeModel.getRoot()),
				  Boolean.TRUE);
	}
        firePropertyChange(TREE_MODEL_PROPERTY, oldModel, treeModel);
        invalidate();
    }

    /**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     * @see #rootVisible
     */
    public boolean isRootVisible() {
        return rootVisible;
    }

    /**
     * Determines whether or not the root node from
     * the TreeModel is visible.
     *
     * @param rootVisible true if the root node of the tree is to be displayed
     * @see #rootVisible
     * @beaninfo
     *        bound: true
     *  description: Whether or not the root node 
     *               from the TreeModel is visible.
     */
    public void setRootVisible(boolean rootVisible) {
        boolean                oldValue = this.rootVisible;

        this.rootVisible = rootVisible;
        firePropertyChange(ROOT_VISIBLE_PROPERTY, oldValue, this.rootVisible);
        if (accessibleContext != null) {
            ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
        }
    }

    /**
     * Determines whether the node handles are to be displayed.
     * 
     * @param newValue true if root handles are to be displayed
     * @see #showsRootHandles
     * @beaninfo
     *        bound: true
     *  description: Whether the node handles are to be
     *               displayed.
     */
    public void setShowsRootHandles(boolean newValue) {
        boolean                oldValue = showsRootHandles;
	TreeModel              model = getModel();

        showsRootHandles = newValue;
        firePropertyChange(SHOWS_ROOT_HANDLES_PROPERTY, oldValue,
                           showsRootHandles);
        if (accessibleContext != null) {
            ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
        }
	// Make SURE the root is expanded
	if(model != null) {
	    expandPath(new TreePath(model.getRoot()));
	}
        invalidate();
    }

    /**
     * Returns true if handles for the root nodes are displayed.
     * 
     * @return true if root handles are displayed
     * @see #showsRootHandles
     */
    public boolean getShowsRootHandles()
    {
        return showsRootHandles;
    }

    /**
     * Sets the height of each cell.  If the specified value
     * is less than or equal to zero the current cell renderer is
     * queried for each row's height.
     *
     * @param rowHeight the height of each cell, in pixels
     * @beaninfo
     *        bound: true
     *  description: The height of each cell.
     */
    public void setRowHeight(int rowHeight)
    {
        int                oldValue = this.rowHeight;

        this.rowHeight = rowHeight;
        firePropertyChange(ROW_HEIGHT_PROPERTY, oldValue, this.rowHeight);
        invalidate();
    }

    /**
     * Returns the height of each row.  If the returned value is less than
     * or equal to 0 the height for each row is determined by the
     * renderer.
     *
     * @param the height of each cell, in pixels. Zero or negative if the
     *        height of each row is determined by the tree cell renderer
     */
    public int getRowHeight()
    {
        return rowHeight;
    }

    /**
     * Returns true if the height of each display row is a fixed size.
     *
     * @return true if the height of each row is a fixed size
     */
    public boolean isFixedRowHeight()
    {
        return (rowHeight > 0);
    }

    /**
     * Specifies whether the UI should use a large model.
     * (Not all UIs will implement this.) Fires a property change
     * for the LARGE_MODEL_PROPERTY.
     * 
     * @param newValue true to suggest a large model to the UI
     * @see #largeModel
     * @beaninfo
     *        bound: true
     *  description: Whether the UI should use a 
     *               large model.
     */
    public void setLargeModel(boolean newValue) {
        boolean                oldValue = largeModel;

        largeModel = newValue;
        firePropertyChange(LARGE_MODEL_PROPERTY, oldValue, newValue);
    }

    /**
     * Returns true if the tree is configured for a large model.
     * 
     * @return true if a large model is suggested
     * @see #largeModel
     */
    public boolean isLargeModel() {
        return largeModel;
    }

    /**
     * Determines what happens when editing is interrupted by selecting
     * another node in the tree, a change in the tree's data, or by some
     * other means. Setting this property to <code>true</code> causes the
     * changes to be automatically saved when editing is interrupted.
     * <p>
     * Fires a property change for the INVOKES_STOP_CELL_EDITING_PROPERTY.
     *
     * @param newValue true means that stopCellEditing is invoked when
     *        editing is interruped, and data is saved. False means that
     *        cancelCellEditing is invoked, and changes are lost.
     * @beaninfo
     *        bound: true
     *  description: Determines what happens when editing is interrupted,
     *               selecting another node in the tree, a change in the
     *               tree's data, or some other means.
     */
    public void setInvokesStopCellEditing(boolean newValue) {
        boolean                  oldValue = invokesStopCellEditing;

        invokesStopCellEditing = newValue;
        firePropertyChange(INVOKES_STOP_CELL_EDITING_PROPERTY, oldValue,
                           newValue);
    }

    /**
     * Returns the indicator that tells what happens when editing is 
     * interrupted.
     *
     * @return the indicator that tells what happens when editing is 
     *         interrupted
     * @see #setInvokesStopCellEditing
     */
    public boolean getInvokesStopCellEditing() {
        return invokesStopCellEditing;
    }

    /**
     * Determines whether or not when a node is expanded, as many of
     * the descendants are scrolled to be inside the viewport as
     * possible. The default is true.
     */
    public void setScrollsOnExpand(boolean newValue) {
	boolean           oldValue = scrollsOnExpand;

	scrollsOnExpand = newValue;
        firePropertyChange(SCROLLS_ON_EXPAND_PROPERTY, oldValue,
                           newValue);
    }

    /**
     * Returns true if the tree scrolls to show previously hidden children.
     * @return true if when a node is expanded as many of the descendants
     * as possible are scrolled to be visible.
     */
    public boolean getScrollsOnExpand() {
	return scrollsOnExpand;
    }

    // NOTE: This property will be enabled in a future release.
    /**
     * Sets the number of mouse clicks before a node will expand or close.
     * The default is two. 
     */
/*
    public void setToggleClickCount(int clickCount) {
	int         oldCount = toggleClickCount;

	toggleClickCount = clickCount;
	firePropertyChange(TOGGLE_CLICK_COUNT_PROPERTY, oldCount,
			   clickCount);
    }
*/

    /**
     * Returns the number of mouse clicks needed to expand or close a node.
     * @return number of mouse clicks before node is expanded.
     */
/*
    public int getToggleClickCount() {
	return toggleClickCount;
    }
*/

    /**
     * Returns <code>isEditable</code>. This is invoked from the UI before
     * editing begins to insure that the given path can be edited. This
     * is provided as an entry point for subclassers to add filtered
     * editing without having to resort to creating a new editor.
     *
     * @return true if every parent node and the node itself is editabled
     * @see #isEditable
     */
    public boolean isPathEditable(TreePath path) {
        return isEditable();
    }

    /**
     * Overrides JComponent's getToolTipText method in order to allow 
     * renderer's tips to be used if it has text set.
     * <p>
     * NOTE: For JTree to properly display tooltips of its renderers
     *       JTree must be a registered component with the ToolTipManager.
     *       This can be done by invoking
     *       <code>ToolTipManager.sharedInstance().registerComponent(tree)</code>.
     *       This is not done automaticly!
     *
     * @param event the MouseEvent that initiated the ToolTip display
     */
    public String getToolTipText(MouseEvent event) {
        if(event != null) {
            Point p = event.getPoint();
            int selRow = getRowForLocation(p.x, p.y);
            TreeCellRenderer       r = getCellRenderer();

            if(selRow != -1 && r != null) {
                TreePath     path = getPathForRow(selRow);
                Object       lastPath = path.getLastPathComponent();
                Component    rComponent = r.getTreeCellRendererComponent
                    (this, lastPath, isRowSelected(selRow),
                     isExpanded(selRow), getModel().isLeaf(lastPath), selRow,
                     true);

                if(rComponent instanceof JComponent) {
                    MouseEvent      newEvent;
                    Rectangle       pathBounds = getPathBounds(path);

                    p.translate(-pathBounds.x, -pathBounds.y);
                    newEvent = new MouseEvent(rComponent, event.getID(),
                                          event.getWhen(),
                                              event.getModifiers(),
                                              p.x, p.y, event.getClickCount(),
                                              event.isPopupTrigger());
                    
                    return ((JComponent)rComponent).getToolTipText(newEvent);
                }
            }
        }
        return null;
    }
    
    /**
     * Called by the renderers to convert the specified value to
     * text. This implementation returns value.toString(), ignoring
     * all other arguments. To control the conversion, subclass this 
     * method and use any of the arguments you need.
     * 
     * @param value the Object to convert to text
     * @param selected true if the node is selected
     * @param expanded true if the node is expanded
     * @param leaf  true if the node is a leaf node
     * @param row  an int specifying the node's display row, where 0 is 
     *             the first row in the display
     * @param hasFocus true if the node has the focus
     * @return the String representation of the node's value
     */
    public String convertValueToText(Object value, boolean selected,
                                     boolean expanded, boolean leaf, int row,
                                     boolean hasFocus) {
        if(value != null)
            return value.toString();
        return "";
    }

    //
    // The following are convenience methods that get forwarded to the
    // current TreeUI.
    //

    /**
     * Returns the number of rows that are currently being displayed.
     *
     * @return the number of rows that are being displayed
     */
    public int getRowCount() {
        TreeUI            tree = getUI();

        if(tree != null)
            return tree.getRowCount(this);
        return 0;
    }

    /** 
     * Selects the node identified by the specified path.  If any
     * component of the path is hidden (under a collapsed node), it is 
     * exposed (made viewable).
     *
     * @param path the TreePath specifying the node to select
     */
    public void setSelectionPath(TreePath path) {
	makeVisible(path);
        getSelectionModel().setSelectionPath(path);
    }

    /** 
     * Selects the nodes identified by the specified array of paths.
     * If any component in any of the paths is hidden (under a collapsed
     * node), it is exposed (made viewable).
     *
     * @param paths an array of TreePath objects that specifies the nodes
     *        to select
     */
    public void setSelectionPaths(TreePath[] paths) {
	if(paths != null) {
	    for(int counter = paths.length - 1; counter >= 0; counter--)
		makeVisible(paths[counter]);
	}
        getSelectionModel().setSelectionPaths(paths);
    }

    /**
     * Selects the node at the specified row in the display.
     *
     * @param row  the row to select, where 0 is the first row in
     *             the display
     */
    public void setSelectionRow(int row) {
        int[]             rows = { row };

        setSelectionRows(rows);
    }

    /**
     * Selects the nodes corresponding to each of the specified rows
     * in the display. If a particular element of <code>rows</code> is
     * < 0 or >= getRowCount, it will be ignored. If none of the elements
     * in <code>rows</code> are valid rows, the selection will
     * be cleared. That is it will be as if <code>clearSelection</code>
     * was invoked.
     * 
     * @param rows  an array of ints specifying the rows to select,
     *              where 0 indicates the first row in the display
     */
    public void setSelectionRows(int[] rows) {
        TreeUI               ui = getUI();

        if(ui != null && rows != null) {
            int                  numRows = rows.length;
            TreePath[]           paths = new TreePath[numRows];

            for(int counter = 0; counter < numRows; counter++)
                paths[counter] = ui.getPathForRow(this, rows[counter]);
            setSelectionPaths(paths);
        }
    }

    /**
     * Adds the node identified by the specified TreePath to the current
     * selection. If any component of the path isn't viewable, it is 
     * made viewable.
     *
     * @param path the TreePath to add
     */
    public void addSelectionPath(TreePath path) {
	makeVisible(path);
        getSelectionModel().addSelectionPath(path);
    }

    /**
     * Adds each path in the array of paths to the current selection. If
     * any component of any of the paths isn't viewable, it is
     * made viewable.
     *
     * @param paths an array of TreePath objects that specifies the nodes
     *              to add
     */
    public void addSelectionPaths(TreePath[] paths) {
	if(paths != null) {
	    for(int counter = paths.length - 1; counter >= 0; counter--)
		makeVisible(paths[counter]);
	}
	getSelectionModel().addSelectionPaths(paths);
    }

    /**
     * Adds the path at the specified row to the current selection.
     *
     * @param row  an int specifying the row of the node to add,
     *             where 0 is the first row in the display
     */
    public void addSelectionRow(int row) {
        int[]      rows = { row };

        addSelectionRows(rows);
    }

    /**
     * Adds the paths at each of the specified rows to the current selection.
     * 
     * @param rows  an array of ints specifying the rows to add,
     *              where 0 indicates the first row in the display
     */
    public void addSelectionRows(int[] rows) {
        TreeUI             ui = getUI();

        if(ui != null && rows != null) {
            int                  numRows = rows.length;
            TreePath[]           paths = new TreePath[numRows];

            for(int counter = 0; counter < numRows; counter++)
                paths[counter] = ui.getPathForRow(this, rows[counter]);
            addSelectionPaths(paths);
        }
    }

    /**
     * Returns the last path component in the first node of the current 
     * selection.
     *
     * @return the last Object in the first selected node's TreePath,
     *         or null if nothing is selected
     * @see TreePath#getLastPathComponent
     */
    public Object getLastSelectedPathComponent() {
        TreePath     selPath = getSelectionModel().getSelectionPath();

        if(selPath != null)
            return selPath.getLastPathComponent();
        return null;
    }

    /**
     * Returns the path to the first selected node.
     *
     * @return the TreePath for the first selected node, or null if
     *         nothing is currently selected
     */
    public TreePath getSelectionPath() {
        return getSelectionModel().getSelectionPath();
    }

    /**
     * Returns the paths of all selected values.
     *
     * @return an array of TreePath objects indicating the selected
     *         nodes, or null if nothing is currently selected.
     */
    public TreePath[] getSelectionPaths() {
        return getSelectionModel().getSelectionPaths();
    }

    /**
     * Returns all of the currently selected rows. This method is simply
     * forwarded to the TreeSelectionModel. If nothing is selected null
     * or an empty array with be returned, based on the TreeSelectionModel
     * implementation.
     *
     * @return an array of ints that identifies all currently selected rows
     *         where 0 is the first row in the display
     */
    public int[] getSelectionRows() {
        return getSelectionModel().getSelectionRows();
    }

    /**
     * Returns the number of nodes selected.
     *
     * @return the number of nodes selected
     */
    public int getSelectionCount() {
        return selectionModel.getSelectionCount();
    }

    /**
     * Gets the first selected row.
     *
     * @return an int designating the first selected row, where 0 is the 
     *         first row in the display
     */
    public int getMinSelectionRow() {
        return getSelectionModel().getMinSelectionRow();
    }

    /**
     * Gets the last selected row.
     *
     * @return an int designating the last selected row, where 0 is the 
     *         first row in the display
     */
    public int getMaxSelectionRow() {
        return getSelectionModel().getMaxSelectionRow();
    }

    /**
     * Returns the row index of the last node added to the selection.
     *
     * @return an int giving the row index of the last node added to the
     *         selection, where 0 is the first row in the display
     */
    public int getLeadSelectionRow() {
        return getSelectionModel().getLeadSelectionRow();
    }

    /**
     * Returns the path of the last node added to the selection.
     *
     * @return the TreePath of the last node added to the selection.
     */
    public TreePath getLeadSelectionPath() {
        return getSelectionModel().getLeadSelectionPath();
    }

    /**
     * Returns true if the item identified by the path is currently selected.
     *
     * @param path a TreePath identifying a node
     * @return true if the node is selected
     */
    public boolean isPathSelected(TreePath path) {
        return getSelectionModel().isPathSelected(path);
    }

    /**
     * Returns true if the node identitifed by row is selected.
     *
     * @param row  an int specifying a display row, where 0 is the first
     *             row in the display
     * @return true if the node is selected
     */
    public boolean isRowSelected(int row) {
        return getSelectionModel().isRowSelected(row);
    }

    /**
     * Returns an Enumeration of the descendants of <code>path</code> that
     * are currently expanded. If <code>path</code> is not currently
     * expanded, this will return null. If you expand/collapse nodes while
     * iterating over the returned Enumeration this may not return all
     * the expanded paths, or may return paths that are no longer expanded.
     */
    public Enumeration getExpandedDescendants(TreePath parent) {
	if(!isExpanded(parent))
	    return null;

	Enumeration       toggledPaths = expandedState.keys();
	Vector            elements = new Vector();
	TreePath          path;
	Object            value;

	if(toggledPaths != null) {
	    while(toggledPaths.hasMoreElements()) {
		path = (TreePath)toggledPaths.nextElement();
		value = expandedState.get(path);
		// Add the path if it is expanded, a descendant of parent,
		// and it is visible (all parents expanded). This is rather
		// expensive!
		if(value != null && ((Boolean)value).booleanValue() &&
		   parent.isDescendant(path) && isVisible(path)) {
		    elements.addElement(path);
		}
	    }
	}
	return elements.elements();
    }

    /**
     * Returns true if the node identified by the path has ever been
     * expanded.
     */
    public boolean hasBeenExpanded(TreePath path) {
	return (path != null && expandedState.get(path) != null);
    }

    /**
     * Returns true if the node identified by the path is currently expanded,
     * 
     * @param path  the TreePath specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed, 
     *               true if all nodes in the path are expanded
     */
    public boolean isExpanded(TreePath path) {
	if(path == null)
	    return false;

	// Is this node expanded?
	Object          value = expandedState.get(path);

	if(value == null || !((Boolean)value).booleanValue())
	    return false;

	// It is, make sure its parent is also expanded.
	TreePath        parentPath = path.getParentPath();

	if(parentPath != null)
	    return isExpanded(parentPath);
        return true;
    }

    /**
     * Returns true if the node at the specified display row is currently
     * expanded.
     * 
     * @param row  the row to check, where 0 is the first row in the 
     *             display
     * @return true if the node is currently expanded, otherwise false
     */
    public boolean isExpanded(int row) {
        TreeUI                  tree = getUI();

        if(tree != null) {
	    TreePath         path = tree.getPathForRow(this, row);

	    if(path != null)
		return isExpanded(path);
	}
        return false;
    }

    /**
     * Returns true if the value identified by path is currently collapsed,
     * this will return false if any of the values in path are currently
     * not being displayed.
     * 
     * @param path  the TreePath to check
     * @return true if any of the nodes in the node's path are collapsed, 
     *               false if all nodes in the path are expanded
     */
    public boolean isCollapsed(TreePath path) {
	return !isExpanded(path);
    }

    /**
     * Returns true if the node at the specified display row is collapsed.
     * 
     * @param row  the row to check, where 0 is the first row in the 
     *             display
     * @return true if the node is currently collapsed, otherwise false
     */
    public boolean isCollapsed(int row) {
	return !isExpanded(row);
    }

    /**
     * Ensures that the node identified by path is currently viewable.
     *
     * @param path  the TreePath to make visible
     */
    public void makeVisible(TreePath path) {
        if(path != null) {
	    TreePath        parentPath = path.getParentPath();

	    if(parentPath != null) {
		expandPath(parentPath);
	    }
        }
    }

    /**
     * Returns true if the value identified by path is currently viewable,
     * which means it is either the root or all of its parents are exapnded  ,
     * Otherwise, this method returns false. 
     *
     * @return true if the node is viewable, otherwise false
     */
    public boolean isVisible(TreePath path) {
        if(path != null) {
	    TreePath        parentPath = path.getParentPath();

	    if(parentPath != null)
		return isExpanded(parentPath);
	    // Root.
	    return true;
	}
        return false;
    }

    /**
     * Returns the Rectangle that the specified node will be drawn
     * into. Returns null if any component in the path is hidden
     * (under a collapsed parent).
     * <p>
     * Note:<br>
     * This method returns a valid rectangle, even if the specified
     * node is not currently displayed.
     *
     * @param path the TreePath identifying the node
     * @return the Rectangle the node is drawn in, or null 
     */
    public Rectangle getPathBounds(TreePath path) {
        TreeUI                   tree = getUI();

        if(tree != null)
            return tree.getPathBounds(this, path);
        return null;
    }

    /**
     * Returns the Rectangle that the node at the specified row is
     * drawn in.
     *
     * @param row  the row to be drawn, where 0 is the first row in the 
     *             display
     * @return the Rectangle the node is drawn in 
     */
    public Rectangle getRowBounds(int row) {
	TreePath          path = getPathForRow(row);

	return getPathBounds(getPathForRow(row));
    }

    /**
     * Makes sure all the path components in path are expanded (except
     * for the last path component) and scrolls so that the 
     * node identified by the path is displayed. Only works when this
     * JTree is contained in a JSrollPane.
     * 
     * @param path  the TreePath identifying the node to bring into view
     */
    public void scrollPathToVisible(TreePath path) {
	if(path != null) {
	    makeVisible(path);

	    Rectangle          bounds = getPathBounds(path);

	    if(bounds != null) {
		scrollRectToVisible(bounds);
		if (accessibleContext != null) {
		    ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
		}
	    }
	}
    }

    /**
     * Scrolls the item identified by row until it is displayed. The minimum
     * of amount of scrolling necessary to bring the row into view
     * is performed. Only works when this JTree is contained in a
     * JSrollPane.
     *
     * @param row  an int specifying the row to scroll, where 0 is the
     *             first row in the display
     */
    public void scrollRowToVisible(int row) {
	scrollPathToVisible(getPathForRow(row));
    }

    /**
     * Returns the path for the specified row.
     * <!-->If row is not visible null is returned.<-->
     *
     * @param row  an int specifying a row
     * @return the TreePath to the specified node, null if
     *         row < 0 or row > getRowCount()
     */
    public TreePath getPathForRow(int row) {
        TreeUI                  tree = getUI();

        if(tree != null)
            return tree.getPathForRow(this, row);
        return null;
    }

    /**
     * Returns the row that displays the node identified by the specified
     * path. 
     * 
     * @param path  the TreePath identifying a node
     * @return an int specifying the display row, where 0 is the first
     *         row in the display, or -1 if any of the elements in path
     *         are hidden under a collapsed parent.
     */
    public int getRowForPath(TreePath path) {
        TreeUI                  tree = getUI();

        if(tree != null)
            return tree.getRowForPath(this, path);
        return -1;
    }

    /**
     * Ensures that the node identified by the specified path is 
     * expanded and viewable.
     * 
     * @param path  the TreePath identifying a node
     */
    public void expandPath(TreePath path) {
	// Only expand if not leaf!
	TreeModel          model = getModel();

	if(path != null && model != null && 
	   !model.isLeaf(path.getLastPathComponent())) {
	    setExpandedState(path, true);
	}
    }

    /**
     * Ensures that the node in the specified row is expanded and
     * viewable. <p> If <code>row</code> is < 0 or >= getRowCount this
     * will have no effect.
     *
     * @param row  an int specifying a display row, where 0 is the
     *             first row in the display
     */
    public void expandRow(int row) {
	expandPath(getPathForRow(row));
    }

    /**
     * Ensures that the node identified by the specified path is 
     * collapsed and viewable.
     * 
     * @param path  the TreePath identifying a node
      */
    public void collapsePath(TreePath path) {
	setExpandedState(path, false);
    }

    /**
     * Ensures that the node in the specified row is collapsed.
     * <p> If <code>row</code> is < 0 or >= getRowCount this
     * will have no effect.
     *
     * @param row  an int specifying a display row, where 0 is the
     *             first row in the display
      */
    public void collapseRow(int row) {
	collapsePath(getPathForRow(row));
    }

    /**
     * Returns the path for the node at the specified location.
     *
     * @param x an int giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an int giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return  the TreePath for the node at that location
     */
    public TreePath getPathForLocation(int x, int y) {
        TreePath          closestPath = getClosestPathForLocation(x, y);

        if(closestPath != null) {
            Rectangle       pathBounds = getPathBounds(closestPath);

            if(x >= pathBounds.x && x < (pathBounds.x + pathBounds.width) &&
               y >= pathBounds.y && y < (pathBounds.y + pathBounds.height))
                return closestPath;
        }
        return null;
    }

    /**
     * Returns the row for the specified location. 
     *
     * @param x an int giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an int giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return the row corresponding to the location, or -1 if the
     *         location is not within the bounds of a displayed cell
     * @see #getClosestRowForLocation
     */
    public int getRowForLocation(int x, int y) {
	return getRowForPath(getPathForLocation(x, y));
    }

    /**
     * Returns the path to the node that is closest to x,y.  If
     * no nodes are currently viewable, or there is no model, returns
     * null, otherwise it always returns a valid path.  To test if
     * the node is exactly at x, y, get the node's bounds and
     * test x, y against that.
     *
     * @param x an int giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an int giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return  the TreePath for the node closest to that location,
     *          null if nothing is viewable or there is no model
     *
     * @see #getPathForLocation
     * @see #getPathBounds
     */
    public TreePath getClosestPathForLocation(int x, int y) {
        TreeUI                  tree = getUI();

        if(tree != null)
            return tree.getClosestPathForLocation(this, x, y);
        return null;
    }

    /**
     * Returns the row to the node that is closest to x,y.  If no nodes
     * are viewable or there is no model, returns -1. Otherwise,
     * it always returns a valid row.  To test if the returned object is 
     * exactly at x, y, get the bounds for the node at the returned
     * row and test x, y against that.
     *
     * @param x an int giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an int giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return the row closest to the location, -1 if nothing is
     *         viewable or there is no model
     *
     * @see #getRowForLocation
     * @see #getRowBounds
     */
    public int getClosestRowForLocation(int x, int y) {
	return getRowForPath(getClosestPathForLocation(x, y));
    }

    /**
     * Returns true if the tree is being edited. The item that is being
     * edited can be obtained using <code>getSelectionPath</code>.
     *
     * @return true if the user is currently editing a node
     * @see #getSelectionPath
     */
    public boolean isEditing() {
        TreeUI                  tree = getUI();

        if(tree != null)
            return tree.isEditing(this);
        return false;
    }

    /**
     * Ends the current editing session. (The DefaultTreeCellEditor 
     * object saves any edits that are currently in progress on a cell.
     * Other implementations may operate differently.) 
     * Has no effect if the tree isn't being edited.
     * <blockquote>
     * <b>Note:</b><br>
     * To make edit-saves automatic whenever the user changes
     * their position in the tree, use {@link #setInvokesStopCellEditing}.
     * </blockquote>
     *
     * @return true if editing was in progress and is now stopped,
     *              false if editing was not in progress
     */
    public boolean stopEditing() {
        TreeUI                  tree = getUI();

        if(tree != null)
            return tree.stopEditing(this);
        return false;
    }

    /**
     * Cancels the current editing session. Has no effect if the
     * tree isn't being edited.
     */
    public void  cancelEditing() {
        TreeUI                  tree = getUI();

        if(tree != null)
	    tree.cancelEditing(this);
    }

    /**
     * Selects the node identified by the specified path and initiates
     * editing.  The edit-attempt fails if the CellEditor does not allow
     * editing for the specified item.
     * 
     * @param path  the TreePath identifying a node
     */
    public void startEditingAtPath(TreePath path) {
        TreeUI                  tree = getUI();

        if(tree != null)
            tree.startEditingAtPath(this, path);
    }

    /**
     * Returns the path to the element that is currently being edited.
     *
     * @return  the TreePath for the node being edited
     */
    public TreePath getEditingPath() {
        TreeUI                  tree = getUI();

        if(tree != null)
            return tree.getEditingPath(this);
        return null;
    }

    //
    // Following are primarily convenience methods for mapping from
    // row based selections to path selections.  Sometimes it is
    // easier to deal with these than paths (mouse downs, key downs
    // usually just deal with index based selections).
    // Since row based selections require a UI many of these won't work
    // without one.
    //

    /**
     * Sets the tree's selection model. When a null value is specified
     * an empty electionModel is used, which does not allow selections.
     *
     * @param selectionModel the TreeSelectionModel to use, or null to
     *        disable selections
     * @see TreeSelectionModel
     * @beaninfo
     *        bound: true
     *  description: The tree's selection model.
     */
    public void setSelectionModel(TreeSelectionModel selectionModel) {
        if(selectionModel == null)
            selectionModel = EmptySelectionModel.sharedInstance();

        TreeSelectionModel         oldValue = this.selectionModel;

        if (accessibleContext != null) {
           this.selectionModel.removeTreeSelectionListener((TreeSelectionListener)accessibleContext);
           selectionModel.addTreeSelectionListener((TreeSelectionListener)accessibleContext);
        }

        this.selectionModel = selectionModel;
        firePropertyChange(SELECTION_MODEL_PROPERTY, oldValue,
                           this.selectionModel);

        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
                    new Boolean(false), new Boolean(true));
        }
    }

    /**
     * Returns the model for selections. This should always return a 
     * non-null value. If you don't want to allow anything to be selected
     * set the selection model to null, which forces an empty
     * selection model to be used.
     *
     * @param the TreeSelectionModel in use
     * @see #setSelectionModel
     */
    public TreeSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * Returns JTreePath instances representing the path between index0
     * and index1 (including index1).  Returns null if there is no tree.
     *
     * @param index0  an int specifying a display row, where 0 is the
     *                first row in the display
     * @param index0  an int specifying a second display row
     * @return an array of TreePath objects, one for each node between
     *         index0 and index1, inclusive
     */
    protected TreePath[] getPathBetweenRows(int index0, int index1) {
        int              newMinIndex, newMaxIndex;
        TreeUI           tree = getUI();

        newMinIndex = Math.min(index0, index1);
        newMaxIndex = Math.max(index0, index1);

        if(tree != null) {
            TreePath[]            selection = new TreePath[newMaxIndex -
                                                            newMinIndex + 1];

            for(int counter = newMinIndex; counter <= newMaxIndex; counter++)
                selection[counter - newMinIndex] = tree.getPathForRow(this,
								      counter);
            return selection;
        }
        return null;
    }

    /**
     * Selects the nodes between index0 and index1, inclusive.
     *
     * @param index0  an int specifying a display row, where 0 is the
     *                first row in the display
     * @param index0  an int specifying a second display row
    */
    public void setSelectionInterval(int index0, int index1) {
        TreePath[]         paths = getPathBetweenRows(index0, index1);

        this.getSelectionModel().setSelectionPaths(paths);
    }

    /**
     * Adds the paths between index0 and index1, inclusive, to the 
     * selection.
     *
     * @param index0  an int specifying a display row, where 0 is the
     *                first row in the display
     * @param index0  an int specifying a second display row
     */
    public void addSelectionInterval(int index0, int index1) {
        TreePath[]         paths = getPathBetweenRows(index0, index1);

        this.getSelectionModel().addSelectionPaths(paths);
    }

    /**
     * Removes the nodes between index0 and index1, inclusive, from the 
     * selection.
     *
     * @param index0  an int specifying a display row, where 0 is the
     *                first row in the display
     * @param index0  an int specifying a second display row
     */
    public void removeSelectionInterval(int index0, int index1) {
        TreePath[]         paths = getPathBetweenRows(index0, index1);

        this.getSelectionModel().removeSelectionPaths(paths);
    }

    /**
     * Removes the node identified by the specified path from the current
     * selection.
     * 
     * @param path  the TreePath identifying a node
     */
    public void removeSelectionPath(TreePath path) {
        this.getSelectionModel().removeSelectionPath(path);
    }

    /**
     * Removes the nodes identified by the specified paths from the 
     * current selection.
     *
     * @param paths an array of TreePath objects that specifies the nodes
     *              to remove
     */
    public void removeSelectionPaths(TreePath[] paths) {
        this.getSelectionModel().removeSelectionPaths(paths);
    }

    /**
     * Removes the path at the index <code>row</code> from the current
     * selection.
     * 
     * @param path  the TreePath identifying the node to remove
     */
    public void removeSelectionRow(int row) {
        int[]             rows = { row };

        removeSelectionRows(rows);
    }

    /**
     * Removes the paths that are selected at each of the specified
     * rows.
     *
     * @param row  an array of ints specifying display rows, where 0 is 
     *             the first row in the display
     */
    public void removeSelectionRows(int[] rows) {
        TreeUI             ui = getUI();

        if(ui != null && rows != null) {
            int                  numRows = rows.length;
            TreePath[]           paths = new TreePath[numRows];

            for(int counter = 0; counter < numRows; counter++)
                paths[counter] = ui.getPathForRow(this, rows[counter]);
            removeSelectionPaths(paths);
        }
    }

    /**
     * Clears the selection.
     */
    public void clearSelection() {
        getSelectionModel().clearSelection();
    }

    /**
     * Returns true if the selection is currently empty.
     *
     * @return true if the selection is currently empty
     */
    public boolean isSelectionEmpty() {
        return getSelectionModel().isSelectionEmpty();
    }

    /**
     * Adds a listener for TreeExpansion events.
     *
     * @param tel a TreeExpansionListener that will be notified when
     *            a tree node is expanded or collapsed (a "negative
     *            expansion")
     */
    public void addTreeExpansionListener(TreeExpansionListener tel) {
        listenerList.add(TreeExpansionListener.class, tel);
    }

    /**
     * Removes a listener for TreeExpansion events.
     *
     * @param tel the TreeExpansionListener to remove
     */
    public void removeTreeExpansionListener(TreeExpansionListener tel) {
        listenerList.remove(TreeExpansionListener.class, tel);
    }

    /**
     * Adds a listener for TreeWillExpand events.
     *
     * @param tel a TreeWillExpandListener that will be notified when
     *            a tree node will be expanded or collapsed (a "negative
     *            expansion")
     */
    public void addTreeWillExpandListener(TreeWillExpandListener tel) {
        listenerList.add(TreeWillExpandListener.class, tel);
    }

    /**
     * Removes a listener for TreeWillExpand events.
     *
     * @param tel the TreeWillExpandListener to remove
     */
    public void removeTreeWillExpandListener(TreeWillExpandListener tel) {
        listenerList.remove(TreeWillExpandListener.class, tel);
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param path the TreePath indicating the node that was expanded
     * @see EventListenerList
     */
     public void fireTreeExpanded(TreePath path) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeExpansionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeExpansionListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeExpansionEvent(this, path);
                ((TreeExpansionListener)listeners[i+1]).
                    treeExpanded(e);
            }          
        }
    }   

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param path the TreePath indicating the node that was collapsed
     * @see EventListenerList
     */
    public void fireTreeCollapsed(TreePath path) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeExpansionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeExpansionListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeExpansionEvent(this, path);
                ((TreeExpansionListener)listeners[i+1]).
                    treeCollapsed(e);
            }          
        }
    }   

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param path the TreePath indicating the node that was expanded
     * @see EventListenerList
     */
     public void fireTreeWillExpand(TreePath path) throws ExpandVetoException {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeExpansionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeWillExpandListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeExpansionEvent(this, path);
                ((TreeWillExpandListener)listeners[i+1]).
                    treeWillExpand(e);
            }          
        }
    }   

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param path the TreePath indicating the node that was expanded
     * @see EventListenerList
     */
     public void fireTreeWillCollapse(TreePath path) throws ExpandVetoException {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeExpansionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeWillExpandListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeExpansionEvent(this, path);
                ((TreeWillExpandListener)listeners[i+1]).
                    treeWillCollapse(e);
            }          
        }
    }   

    /**
     * Adds a listener for TreeSelection events.
     *
     * @param tsl the TreeSelectionListener that will be notified when
     *            a node is selected or deselected (a "negative
     *            selection")
     */
    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        listenerList.add(TreeSelectionListener.class,tsl);
        if(listenerList.getListenerCount(TreeSelectionListener.class) != 0
           && selectionRedirector == null) {
            selectionRedirector = new TreeSelectionRedirector();
            selectionModel.addTreeSelectionListener(selectionRedirector);
        }
    }

    /**
     * Removes a TreeSelection listener.
     *
     * @param tsl the TreeSelectionListener to remove
     */
    public void removeTreeSelectionListener(TreeSelectionListener tsl) {
        listenerList.remove(TreeSelectionListener.class,tsl);
        if(listenerList.getListenerCount(TreeSelectionListener.class) == 0
           && selectionRedirector != null) {
            selectionModel.removeTreeSelectionListener
                (selectionRedirector);
            selectionRedirector = null;
        }
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param e the TreeSelectionEvent generated by the TreeSelectionModel
     *          when a node is selected or deselected
     * @see EventListenerList
     */
    protected void fireValueChanged(TreeSelectionEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            // TreeSelectionEvent e = null;
            if (listeners[i]==TreeSelectionListener.class) {
                // Lazily create the event:
                // if (e == null)
                // e = new ListSelectionEvent(this, firstIndex, lastIndex);
                ((TreeSelectionListener)listeners[i+1]).valueChanged(e);
            }          
        }
    }

    /**
     * Sent when the tree has changed enough that we need to resize
     * the bounds, but not enough that we need to remove the
     * expanded node set (e.g nodes were expanded or collapsed, or
     * nodes were inserted into the tree). You should never have to
     * invoke this, the UI will invoke this as it needs to.
     */
    public void treeDidChange() {
        revalidate();
        repaint();
    }

    /**
     * Sets the number of rows that are to be displayed.
     * This will only work if the reciever is contained in a JScrollPane,
     * and will adjust the preferred size and size of that scrollpane.
     *
     * @param newCount the number of rows to display
     * @beaninfo
     *        bound: true
     *  description: The number of rows that are to be displayed.
     */
    public void setVisibleRowCount(int newCount) {
        int                 oldCount = visibleRowCount;

        visibleRowCount = newCount;
        firePropertyChange(VISIBLE_ROW_COUNT_PROPERTY, oldCount,
                           visibleRowCount);
        invalidate();
        if (accessibleContext != null) {
            ((AccessibleJTree)accessibleContext).fireVisibleDataPropertyChange();
        }
    }

    /**
     * Returns the number of rows that are displayed in the display area.
     *
     * @return the number of rows displayed
     */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    // Serialization support.  
    private void writeObject(ObjectOutputStream s) throws IOException {
        Vector      values = new Vector();

        s.defaultWriteObject();
        // Save the cellRenderer, if its Serializable.
        if(cellRenderer != null && cellRenderer instanceof Serializable) {
            values.addElement("cellRenderer");
            values.addElement(cellRenderer);
        }
        // Save the cellEditor, if its Serializable.
        if(cellEditor != null && cellEditor instanceof Serializable) {
            values.addElement("cellEditor");
            values.addElement(cellEditor);
        }
        // Save the treeModel, if its Serializable.
        if(treeModel != null && treeModel instanceof Serializable) {
            values.addElement("treeModel");
            values.addElement(treeModel);
        }
        // Save the selectionModel, if its Serializable.
        if(selectionModel != null && selectionModel instanceof Serializable) {
            values.addElement("selectionModel");
            values.addElement(selectionModel);
        }

	Object      expandedData = getArchivableExpandedState();

	if(expandedData != null) {
            values.addElement("expandedState");
            values.addElement(expandedData);
	}

        s.writeObject(values);
    }

    private void readObject(ObjectInputStream s) 
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();

	// Create an instance of expanded state.

	expandedState = new Hashtable();

	expandedStack = new Stack();

        Vector          values = (Vector)s.readObject();
        int             indexCounter = 0;
        int             maxCounter = values.size();

        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("cellRenderer")) {
            cellRenderer = (TreeCellRenderer)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("cellEditor")) {
            cellEditor = (TreeCellEditor)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("treeModel")) {
            treeModel = (TreeModel)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("selectionModel")) {
            selectionModel = (TreeSelectionModel)values.elementAt(++indexCounter);
            indexCounter++;
        }
        if(indexCounter < maxCounter && values.elementAt(indexCounter).
           equals("expandedState")) {
	    unarchiveExpandedState(values.elementAt(++indexCounter));
            indexCounter++;
        }
	// Reinstall the redirector.
        if(listenerList.getListenerCount(TreeSelectionListener.class) != 0) {
            selectionRedirector = new TreeSelectionRedirector();
            selectionModel.addTreeSelectionListener(selectionRedirector);
        }
	// Listener to TreeModel.
	if(treeModel != null) {
	    treeModelListener = createTreeModelListener();
	    if(treeModelListener != null)
		treeModel.addTreeModelListener(treeModelListener);
	}

	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }

    /**
     * Returns an object that can be archived indicating what nodes are
     * expanded and what aren't. The objects from the model are NOT
     * written out.
     */
    private Object getArchivableExpandedState() {
	TreeModel       model = getModel();

	if(model != null) {
	    Enumeration        paths = expandedState.keys();

	    if(paths != null) {
		Vector         state = new Vector();

		while(paths.hasMoreElements()) {
		    TreePath   path = (TreePath)paths.nextElement();
		    Object     archivePath;

		    try {
			archivePath = getModelIndexsForPath(path);
		    } catch (Error error) {
			archivePath = null;
		    }
		    if(archivePath != null) {
			state.addElement(archivePath);
			state.addElement(expandedState.get(path));
		    }
		}
		return state;
	    }
	}
	return null;
    }

    /**
     * Updates the expanded state of nodes in the tree based on the 
     * previously archived state <code>state</code>.
     */
    private void unarchiveExpandedState(Object state) {
	if(state instanceof Vector) {
	    Vector          paths = (Vector)state;

	    for(int counter = paths.size() - 1; counter >= 0; counter--) {
		Boolean        eState = (Boolean)paths.elementAt(counter--);
		TreePath       path;

		try {
		    path = getPathForIndexs((int[])paths.elementAt(counter));
		    if(path != null)
			expandedState.put(path, eState);
		} catch (Error error) {}
	    }
	}
    }

    /**
     * Returns an array of integers specifying the indexs of the
     * components in the <code>path</code>. If <code>path</code> is
     * the root, this will return an empty array.
     */
    private int[] getModelIndexsForPath(TreePath path) {
	if(path != null) {
	    TreeModel   model = getModel();
	    int         count = path.getPathCount();
	    int[]       indexs = new int[count - 1];
	    Object      parent = model.getRoot();

	    for(int counter = 1; counter < count; counter++) {
		indexs[counter - 1] = model.getIndexOfChild
			           (parent, path.getPathComponent(counter));
		parent = path.getPathComponent(counter);
		if(indexs[counter - 1] < 0)
		    return null;
	    }
	    return indexs;
	}
	return null;
    }

    /**
     * Returns a TreePath created by obtaining the children for each of
     * the indices in <code>indexs</code>.
     */
    private TreePath getPathForIndexs(int[] indexs) {
	if(indexs == null)
	    return null;

	TreeModel    model = getModel();

	if(model == null)
	    return null;

	int          count = indexs.length;
	Object       parent = model.getRoot();
	TreePath     parentPath = new TreePath(parent);

	for(int counter = 0; counter < count; counter++) {
	    parent = model.getChild(parent, indexs[counter]);
	    if(parent == null)
		return null;
	    parentPath = parentPath.pathByAddingChild(parent);
	}
	return parentPath;
    }

    /**
     * EmptySelectionModel is a TreeSelectionModel that does not allow
     * anything to be selected.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected static class EmptySelectionModel extends
              DefaultTreeSelectionModel
    {
        /** Unique shared instance. */
        protected static final EmptySelectionModel sharedInstance =
            new EmptySelectionModel();

        /** Returns a shared instance of an empty selection model */
        static public EmptySelectionModel sharedInstance() {
            return sharedInstance;
        }

        /** A null implementation that selects nothing */
        public void setSelectionPaths(TreePath[] pPaths) {}
        /** A null implementation that adds nothing */
        public void addSelectionPaths(TreePath[] paths) {}
        /** A null implementation that removes nothing */
        public void removeSelectionPaths(TreePath[] paths) {}
    }


    /**
     * Handles creating a new TreeSelectionEvent with the JTree as the
     * source and passing it off to all the listeners.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class TreeSelectionRedirector implements Serializable,
                    TreeSelectionListener
    {
        /**
         * Invoked by the TreeSelectionModel when the selection changes.
         * 
         * @param e the TreeSelectionEvent generated by the TreeSelectionModel
         */
        public void valueChanged(TreeSelectionEvent e) {
            TreeSelectionEvent       newE;

            newE = (TreeSelectionEvent)e.cloneWithSource(JTree.this);
            fireValueChanged(newE);
        }
    } // End of class JTree.TreeSelectionRedirector

    //
    // Scrollable interface
    //

    /**
     * Returns the preferred display size of a JTree. The height is
     * determined from <code>getVisibleRowCount</code> and the width
     * is the current preferred width.
     *
     * @return a Dimension object containing the preferred size
     */
    public Dimension getPreferredScrollableViewportSize() {
        int                 width = getPreferredSize().width;
        int                 visRows = getVisibleRowCount();
        int                 height;

        if(isFixedRowHeight())
            height = visRows * getRowHeight();
        else {
            TreeUI          ui = getUI();

            if(ui != null && ui.getRowCount(this) > 0)
                height = getRowBounds(0).height * visRows;
            else
                height = 16 * visRows;
        }
        return new Dimension(width, height);
    }

    /**
     * Returns the amount to increment when scrolling. The amount is
     * the height of the first displayed row that isn't completely in view
     * or, if it is totally displayed, the height of the next row in the
     * scrolling direction.
     * 
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation, int direction) {
        if(orientation == SwingConstants.VERTICAL) {
            Rectangle       rowBounds;
            int             firstIndex = getClosestRowForLocation
                                         (0, visibleRect.y);

            if(firstIndex != -1) {
                rowBounds = getRowBounds(firstIndex);
                if(rowBounds.y != visibleRect.y) {
                    if(direction < 0) // UP
                        return (visibleRect.y - rowBounds.y);
                    return (rowBounds.y + rowBounds.height - visibleRect.y);
                }
                if(direction < 0) { // UP
                    if(firstIndex != 0) {
                        rowBounds = getRowBounds(firstIndex - 1);
                        return rowBounds.height;
                    }
                }
                else {
                    return rowBounds.height;
                }
            }
            return 0;
        }
        return 4;
    }


    /**
     * Returns the amount for a block inrecment, which is the height or
     * width of <code>visibleRect</code>, based on <code>orientation</code>.
     * 
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation, int direction) {
        return (orientation == SwingConstants.VERTICAL) ? visibleRect.height :
            visibleRect.width;
    }
    
    /**
     * Returns false to indicate that the width of the viewport does not 
     * determine the width of the table, unless the preferred width of 
     * the tree is smaller than the viewports width.  In other words: 
     * ensure that the tree is never smaller than its viewport.
     * 
     * @return false
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportWidth() {
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	}
	return false;
    }

    /**
     * Returns false to indicate that the height of the viewport does not 
     * determine the height of the table, unless the preferred height
     * of the tree is smaller than the viewports height.  In other words: 
     * ensure that the tree is never smaller than its viewport.
     * 
     * @return false
     * @see Scrollable#getScrollableTracksViewportHeight
     */
    public boolean getScrollableTracksViewportHeight() {
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
	}
	return false;
    }

    /**
     * Sets the expanded state of the receiver. If <code>state</code> is
     * true, all parents of <code>path</code> and path are marked as
     * expanded. If <code>state</code> is false, all parents of 
     * <code>path</code> are marked EXPANDED, but <code>path</code> itself
     * is marked collapsed.<p>
     * This will fail if a TreeWillExpandListener vetos it.
     */
    protected void setExpandedState(TreePath path, boolean state) {
	if(path != null) {
	    // Make sure all parents of path are expanded.
	    Stack         stack;
	    TreePath      parentPath = path.getParentPath();

	    if (expandedStack.size() == 0) {
		stack = new Stack();
	    }
	    else {
		stack = (Stack)expandedStack.pop();
	    }

	    try {
		while(parentPath != null) {
		    if(isExpanded(parentPath)) {
			parentPath = null;
		    }
		    else {
			stack.push(parentPath);
			parentPath = parentPath.getParentPath();
		    }
		}
		for(int counter = stack.size() - 1; counter >= 0; counter--) {
		    parentPath = (TreePath)stack.pop();
		    if(!isExpanded(parentPath)) {
			try {
			    fireTreeWillExpand(parentPath);
			} catch (ExpandVetoException eve) {
			    // Expand vetoed!
			    return;
			}
			expandedState.put(parentPath, Boolean.TRUE);
			fireTreeExpanded(parentPath);
			if (accessibleContext != null) {
			    ((AccessibleJTree)accessibleContext).
			                      fireVisibleDataPropertyChange();
			}
		    }
		}
	    }
	    finally {
		if (expandedStack.size() < TEMP_STACK_SIZE) {
		    stack.removeAllElements();
		    expandedStack.push(stack);
		}
	    }
	    if(!state) {
		// collapse last path.
		Object          cValue = expandedState.get(path);

		if(cValue != null && ((Boolean)cValue).booleanValue()) {
		    try {
			fireTreeWillCollapse(path);
		    }
		    catch (ExpandVetoException eve) {
			return;
		    }
		    expandedState.put(path, Boolean.FALSE);
		    fireTreeCollapsed(path);
		    if (accessibleContext != null) {
			((AccessibleJTree)accessibleContext).
			            fireVisibleDataPropertyChange();
		    }
		}
	    }
	    else {
		// Expand last path.
		Object          cValue = expandedState.get(path);

		if(cValue == null || !((Boolean)cValue).booleanValue()) {
		    try {
			fireTreeWillExpand(path);
		    }
		    catch (ExpandVetoException eve) {
			return;
		    }
		    expandedState.put(path, Boolean.TRUE);
		    fireTreeExpanded(path);
		    if (accessibleContext != null) {
			((AccessibleJTree)accessibleContext).
			                  fireVisibleDataPropertyChange();
		    }
		}
	    }
	}
    }

    /**
     * Returns an Enumeration of TreePaths that have been expanded that
     * are descendants of <code>parent</code>.
     */
    protected Enumeration getDescendantToggledPaths(TreePath parent) {
	if(parent == null)
	    return null;

	Vector            descendants = new Vector();
	Enumeration       nodes = expandedState.keys();
	TreePath          path;

	while(nodes.hasMoreElements()) {
	    path = (TreePath)nodes.nextElement();
	    if(parent.isDescendant(path))
		descendants.addElement(path);
	}
	return descendants.elements();
    }
    
    /**
     * Removes any descendants of the TreePaths in <code>toRemove</code>
     * that have been expanded.
     */
     protected void removeDescendantToggledPaths(Enumeration toRemove) {
	 if(toRemove != null) {
	     while(toRemove.hasMoreElements()) {
		 Enumeration         descendants = getDescendantToggledPaths
		                         ((TreePath)toRemove.nextElement());

		 if(descendants != null) {
		     while(descendants.hasMoreElements()) {
			 expandedState.remove(descendants.nextElement());
		     }
		 }
	     }
	 }
     }

     /**
      * Clears the cache of toggled tree paths. This does NOT send out
      * any TreeExpansionListener events.
      */
     protected void clearToggledPaths() {
	 expandedState.clear();
     }

     /**
      * Creates and returns an instance of TreeModelHandler. The returned
      * object is responsible for updating the expanded state when the
      * TreeModel changes.
      */
     protected TreeModelListener createTreeModelListener() {
	 return new TreeModelHandler();
     }


     /**
      * Listens to the model and updates the expandedState accordingly
      * when nodes are removed, or changed.
      */
    protected class TreeModelHandler implements TreeModelListener {
	public void treeNodesChanged(TreeModelEvent e) { }

	public void treeNodesInserted(TreeModelEvent e) { }

	public void treeStructureChanged(TreeModelEvent e) {
	    if(e == null)
		return;

	    // NOTE: If I change this to NOT remove the descendants
	    // and update BasicTreeUIs treeStructureChanged method
	    // to update descendants in response to a treeStructureChanged
	    // event, all the children of the event won't collapse!
	    TreePath            parent = e.getTreePath();

	    if(parent == null)
		return;

	    if(expandedState.get(parent) != null) {
		Vector              toRemove = new Vector(1);
		boolean             isExpanded = isExpanded(parent);

		toRemove.addElement(parent);
		removeDescendantToggledPaths(toRemove.elements());
		if(isExpanded) {
		    TreeModel         model = getModel();

		    if(model == null || model.isLeaf
		       (parent.getLastPathComponent()))
			collapsePath(parent);
		    else
			expandedState.put(parent, Boolean.TRUE);
		}
	    }
	}

	public void treeNodesRemoved(TreeModelEvent e) {
	    if(e == null)
		return;

	    TreePath            parent = e.getTreePath();
	    Object[]            children = e.getChildren();

	    if(children == null)
		return;

	    TreePath            rPath;
	    Vector              toRemove = new Vector(Math.max
						      (1, children.length));

	    for(int counter = children.length - 1; counter >= 0; counter--) {
		rPath = parent.pathByAddingChild(children[counter]);
		if(expandedState.get(rPath) != null)
		    toRemove.addElement(rPath);
	    }
	    if(toRemove.size() > 0)
		removeDescendantToggledPaths(toRemove.elements());

	    TreeModel         model = getModel();

	    if(model == null || model.isLeaf(parent.getLastPathComponent()))
		expandedState.remove(parent);
	}
    }


    /**
     * DynamicUtilTreeNode can wrap vectors/hashtables/arrays/strings and
     * create the appropriate children tree nodes as necessary. It is
     * dynamic in that it'll only create the children as necessary.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class DynamicUtilTreeNode extends DefaultMutableTreeNode {
        /* Does the receiver have children? */
        protected boolean            hasChildren;
        /** Value to create children with. */
        protected Object             childValue;
        /* Have the children been loaded yet? */
        protected boolean            loadedChildren;

        /**
         * Adds to parent all the children in <code>children</code>.
         * If <code>children</code> is an array or Vector all of its
         * elements are added is children, otherwise if <code>children</code>
         * is a Hashtable all the key/value pairs are added in the order
         * Enumeration returns them.
         */
        public static void createChildren(DefaultMutableTreeNode parent,
                                          Object children) {
            if(children instanceof Vector) {
                Vector          childVector = (Vector)children;

                for(int counter = 0, maxCounter = childVector.size();
                    counter < maxCounter; counter++)
                    parent.add(new DynamicUtilTreeNode
                               (childVector.elementAt(counter),
                                childVector.elementAt(counter)));
            }
            else if(children instanceof Hashtable) {
                Hashtable           childHT = (Hashtable)children;
                Enumeration         keys = childHT.keys();
                Object              aKey;

                while(keys.hasMoreElements()) {
                    aKey = keys.nextElement();
                    parent.add(new DynamicUtilTreeNode(aKey,
                                                       childHT.get(aKey)));
                }
            }
            else if(children instanceof Object[]) {
                Object[]             childArray = (Object[])children;

                for(int counter = 0, maxCounter = childArray.length;
                    counter < maxCounter; counter++)
                    parent.add(new DynamicUtilTreeNode(childArray[counter],
                                                       childArray[counter]));
            }
        }

        /**
         * Creates a node with the specified object as its value and
         * with the specified children. For the node to allow children,
         * the children-object must be an array of objects, a Vector,
         * or a Hashtable -- even if empty. Otherwise, the node is not
         * allowed to have children.
         *
         * @param value  the Object that is the value for the new node
         * @param children an array of Objects, a Vector, or a Hashtable
         *                 used to create the child nodes. If any other
         *                 object is specified, or if the value is null,
         *                 then the node is not allowed to have children.
         */
        public DynamicUtilTreeNode(Object value, Object children) {
            super(value);
            loadedChildren = false;
            childValue = children;
            if(children != null) {
                if(children instanceof Vector)
                    setAllowsChildren(true);
                else if(children instanceof Hashtable)
                    setAllowsChildren(true);
                else if(children instanceof Object[])
                    setAllowsChildren(true);
                else
                    setAllowsChildren(false);
            }
            else
                setAllowsChildren(false);
        }

        /**
         * Returns true if this node allows children. Whether the node
         * allows children depends on how it was created.
         *
         * @return true if this node allows children, false otherwise.
         * @see JTree.DynamicUtilTreeNode#DynamicUtilTreeNode(Object, Object)
         */
        public boolean isLeaf() {
            return !getAllowsChildren();
        }

        /**
         * Returns the number of child nodes.
         *
         * @return the number of child nodes
         */
        public int getChildCount() {
            if(!loadedChildren)
                loadChildren();
            return super.getChildCount();
        }

        /**
         * Loads the children based on childValue. If childValue is
         * a Vector orarray each element  added as a child, if childValue
         * is a Hashtable each key/value pair is added in the order that
         * Enumeration returns the keys.
         */
        protected void loadChildren() {
            loadedChildren = true;
            createChildren(this, childValue);
        }

	/**
	 * Subclassed to load the children, if necessary.
	 */
	public TreeNode getChildAt(int index) {
	    if(!loadedChildren)
		loadChildren();
	    return super.getChildAt(index);
	}

	/**
	 * Subclassed to load the children, if necessary.
	 */
	public Enumeration children() {
	    if(!loadedChildren)
		loadChildren();
	    return super.children();
	}
    }


    /**
     * Returns a string representation of this JTree. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JTree.
     */
    protected String paramString() {
        String rootVisibleString = (rootVisible ?
                                    "true" : "false");
        String showsRootHandlesString = (showsRootHandles ?
					 "true" : "false");
        String editableString = (editable ?
				 "true" : "false");
        String largeModelString = (largeModel ?
				   "true" : "false");
        String invokesStopCellEditingString = (invokesStopCellEditing ?
					       "true" : "false");
        String scrollsOnExpandString = (scrollsOnExpand ?
					"true" : "false");

        return super.paramString() +
        ",editable=" + editableString +
        ",invokesStopCellEditing=" + invokesStopCellEditingString +
        ",largeModel=" + largeModelString +
        ",rootVisible=" + rootVisibleString +
        ",rowHeight=" + rowHeight +
        ",scrollsOnExpand=" + scrollsOnExpandString +
        ",showsRootHandles=" + showsRootHandlesString +
        ",toggleClickCount=" + toggleClickCount +
        ",visibleRowCount=" + visibleRowCount;
    }

/////////////////
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJTree();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJTree extends AccessibleJComponent 
            implements AccessibleSelection, TreeSelectionListener, 
	               TreeModelListener, TreeExpansionListener  {

        TreePath   leadSelectionPath;
	Accessible leadSelectionAccessible;

        public AccessibleJTree() {
            // Add a tree model listener for JTree
             JTree.this.getModel().addTreeModelListener(this);
	    JTree.this.addTreeExpansionListener(this);      
	    JTree.this.addTreeSelectionListener(this);      
            leadSelectionPath = JTree.this.getLeadSelectionPath();
	    leadSelectionAccessible = (leadSelectionPath != null) 
		    ? new AccessibleJTreeNode(JTree.this,
		                              leadSelectionPath,
		                              JTree.this)
		    : null;
        }
 
        /**
         * Tree Selection Listener value change method. Used to fire the 
	 * property change
         *
         * @param e ListSelectionEvent
         *
         */
        public void valueChanged(TreeSelectionEvent e) {
	    TreePath oldLeadSelectionPath = leadSelectionPath;
            leadSelectionPath = JTree.this.getLeadSelectionPath();
	    if (oldLeadSelectionPath != leadSelectionPath) {
		Accessible oldLSA = leadSelectionAccessible;
		leadSelectionAccessible = (leadSelectionPath != null) 
			? new AccessibleJTreeNode(JTree.this,
						  leadSelectionPath,
		                  		  JTree.this)
			: null;
                firePropertyChange(AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY,
                                   oldLSA, leadSelectionAccessible);
	    }
            firePropertyChange(AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
                               new Boolean(false), new Boolean(true));
	}

        /**
         * Fire a visible data property change notification.
         * A 'visible' data property is that it represents
         * something about the way the component appears on the
         * display, where that appearance isn't bound to any other
         * property. It notifies screen readers  that the visual 
         * appearance of the component has changed, so they can 
         * notify the user.
         */
        public void fireVisibleDataPropertyChange() {
           firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                              new Boolean(false), new Boolean(true));
        }
 
        // Fire the visible data changes for the model changes.
 
        /**
         * Tree Model Node change notification.
         *
         * @param e  a Tree Model event
         */
        public void treeNodesChanged(TreeModelEvent e) {
           fireVisibleDataPropertyChange();
        }
 
        /**
         * Tree Model Node change notification.
         *
         * @param e  a Tree node insertion event
         */
        public void treeNodesInserted(TreeModelEvent e) {
           fireVisibleDataPropertyChange();
        }
 
        /**
         * Tree Model Node change notification.
         *
         * @param e  a Tree node(s) removal event
         */
        public  void treeNodesRemoved(TreeModelEvent e) {
           fireVisibleDataPropertyChange();
        }
 
        /**
         * Tree Model structure change change notification.
         *
         * @param e  a Tree Model event
         */
        public  void treeStructureChanged(TreeModelEvent e) {
           fireVisibleDataPropertyChange();
        }
 
        /**
         * Tree Collapsed notification.
         *
         * @param e  a TreeExpansionEvent
         */
        public  void treeCollapsed(TreeExpansionEvent e) {
           fireVisibleDataPropertyChange();
        }
 
        /**
         * Tree Model Expansion notification.
         *
         * @param e  a Tree node insertion event
         */
        public  void treeExpanded(TreeExpansionEvent e) {
            fireVisibleDataPropertyChange();
         }

 
        private AccessibleContext getCurrentAccessibleContext() {
            Component c = getCurrentComponent();
            if (c instanceof Accessible) {
                return (((Accessible) c).getAccessibleContext());
            } else {
                return null;
            }
        }
 
        private Component getCurrentComponent() {
            // is the object visible?
            // if so, get row, selected, focus & leaf state, 
            // and then get the renderer component and return it
            TreeModel model = JTree.this.getModel();
            TreePath path = new TreePath(model.getRoot());
            if (JTree.this.isVisible(path)) {
                TreeCellRenderer r = JTree.this.getCellRenderer();
                TreeUI ui = JTree.this.getUI();
                if (ui != null) {
                    int row = ui.getRowForPath(JTree.this, path);
		    int lsr = JTree.this.getLeadSelectionRow();
                    boolean hasFocus = JTree.this.hasFocus()
				       && (lsr == row);
                    boolean selected = JTree.this.isPathSelected(path);
                    boolean expanded = JTree.this.isExpanded(path);

                    return r.getTreeCellRendererComponent(JTree.this, 
                        model.getRoot(), selected, expanded, 
                        model.isLeaf(model.getRoot()), row, hasFocus);
                }
            } 
            return null;
        }

        // Overridden methods from AccessibleJComponent

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.TREE;
        }

        /**
         * Returns the Accessible child, if one exists, contained at the local
         * coordinate Point.
         *
         * @param p point in local coordinates of the this Accessible
         * @return the Accessible, if it exists, at the specified location;
         * else null
         */
        public Accessible getAccessibleAt(Point p) {
            TreePath path = getClosestPathForLocation(p.x, p.y);
            if (path != null) {
                return new AccessibleJTreeNode(JTree.this, path, JTree.this);
            } else {
                return null;
            }
        }

        /**
         * Returns the number of top-level children nodes of this 
         * JTree.  Each of these nodes may in turn have children nodes.
         *
         * @return the number of accessible children nodes in the tree.
         */
        public int getAccessibleChildrenCount() {
	    TreeModel model = JTree.this.getModel();
	    if (model != null) {
		return 1;
	    } else {
		return 0;
	    }
        }

        /**
         * Return the nth Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            TreeModel model = JTree.this.getModel();
            if (model != null) {
                if (i != 0) {
                    return null;
                } else {
                    Object[] objPath = {model.getRoot()};
                    TreePath path = new TreePath(objPath);
                    return new AccessibleJTreeNode(JTree.this, path, JTree.this);
                }
            }
            return null;
        }

        /**
         * Get the index of this object in its accessible parent. 
         *
         * @return the index of this object in its parent; -1 if this 
         * object does not have an accessible parent.
         * @see #getAccessibleParent
         */
        public int getAccessibleIndexInParent() {
            return 0;
	}

        // AccessibleSelection methods

        public AccessibleSelection getAccessibleSelection() {
            return this;
        }

        /**
         * Returns the number of items currently selected.
         * If no items are selected, the return value will be 0.
         *
         * @return the number of items currently selected.
         */
        public int getAccessibleSelectionCount() {
            return JTree.this.getSelectionCount();
        }

        /**
         * Returns an Accessible representing the specified selected item
         * in the object.  If there isn't a selection, or there are 
         * fewer items selcted than the integer passed in, the return
         * value will be null.
         *
         * @param i the zero-based index of selected items
         * @return an Accessible containing the selected item
         */
        public Accessible getAccessibleSelection(int i) {
            TreePath[] paths = JTree.this.getSelectionPaths();
            if (i < 0 || i >= paths.length) {
                return null;
            } else {
                return new AccessibleJTreeNode(JTree.this, paths[i], JTree.this);
            }
        }

        /**
         * Returns true if the current child of this object is selected.
         *
         * @param i the zero-based index of the child in this Accessible object.
         * @see AccessibleContext#getAccessibleChild
         */
        public boolean isAccessibleChildSelected(int i) {
            TreePath[] paths = JTree.this.getSelectionPaths();
            TreeModel treeModel = JTree.this.getModel();
            Object o;
            for (int j = 0; j < paths.length; j++) {
                o = paths[j].getLastPathComponent();
                if (i == treeModel.getIndexOfChild(treeModel.getRoot(), o)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Adds the specified selected item in the object to the object's
         * selection.  If the object supports multiple selections,
         * the specified item is added to any existing selection, otherwise
         * it replaces any existing selection in the object.  If the
         * specified item is already selected, this method has no effect.
         *
         * @param i the zero-based index of selectable items
         */
        public void addAccessibleSelection(int i) {
           TreeModel model = JTree.this.getModel();
           if (model != null) {
               if (i == 0) {
                   Object[] objPath = {model.getRoot()};
                   TreePath path = new TreePath(objPath);
                   JTree.this.addSelectionPath(path);
                }
            }
        }

        /**
         * Removes the specified selected item in the object from the object's
         * selection.  If the specified item isn't currently selected, this
         * method has no effect.
         *
         * @param i the zero-based index of selectable items
         */
        public void removeAccessibleSelection(int i) {
	    TreeModel model = JTree.this.getModel();
	    if (model != null) {
                if (i == 0) {
                    Object[] objPath = {model.getRoot()};
                    TreePath path = new TreePath(objPath);
                    JTree.this.removeSelectionPath(path);
                }
            }
        }

        /**
         * Clears the selection in the object, so that nothing in the
         * object is selected.
         */
        public void clearAccessibleSelection() {
            int childCount = getAccessibleChildrenCount();
            for (int i = 0; i < childCount; i++) {
                removeAccessibleSelection(i);
            }
        }

        /**
         * Causes every selected item in the object to be selected
         * if the object supports multiple selections.
         */
        public void selectAllAccessibleSelection() {
            TreeModel model = JTree.this.getModel();
            if (model != null) {
                Object[] objPath = {model.getRoot()};
                TreePath path = new TreePath(objPath);
                JTree.this.addSelectionPath(path);
            }
        }

        /**
         *
         */
        protected class AccessibleJTreeNode extends AccessibleContext
            implements Accessible, AccessibleComponent, AccessibleSelection, 
            AccessibleAction {

            private JTree tree = null;
            private TreeModel treeModel = null;
            private Object obj = null;
            private Object objParent = null;
            private TreePath path = null;
            private Accessible accessibleParent = null;
            private int index = -1;
            private boolean isLeaf = false;

            /**
             *  Constructs an AccessibleJTreeNode
             */
            public AccessibleJTreeNode(JTree t, TreePath p, Accessible ap) {
                tree = t;
                path = p;
                accessibleParent = ap;
                treeModel = t.getModel();
                obj = p.getLastPathComponent();
                Object[] objPath = p.getPath();
                if (objPath.length > 1) {
                    objParent = objPath[objPath.length-2];
                    if (treeModel != null) {
                        index = treeModel.getIndexOfChild(objParent, obj);
                    }
                    Object[] objParentPath = new Object[objPath.length-1];
                    java.lang.System.arraycopy(objPath, 0, objParentPath, 0, objPath.length-1);
                    TreePath parentPath = new TreePath(objParentPath);
                    this.setAccessibleParent(accessibleParent);
                } else {
                    if (treeModel != null) {
                        index = treeModel.getIndexOfChild(treeModel.getRoot(), obj);
                        this.setAccessibleParent(tree);
                    }
                }
                if (treeModel != null) {
                    isLeaf = treeModel.isLeaf(obj);
                }
            }

            private TreePath getChildTreePath(int i) {
                // Tree nodes can't be so complex that they have
                // two sets of children -> we're ignoring that case
                if (i < 0 || i >= getAccessibleChildrenCount()) {
                    return null;
                } else {
                    Object childObj = treeModel.getChild(obj, i);
                    Object[] objPath = path.getPath();
                    Object[] objChildPath = new Object[objPath.length+1];
                    java.lang.System.arraycopy(objPath, 0, objChildPath, 0, objPath.length);
                    objChildPath[objChildPath.length-1] = childObj;
                    return new TreePath(objChildPath);
                }
            }

            /**
             * Get the AccessibleContext associated with this tree node
             *
             * @return the AccessibleContext of this JComponent
             */
            public AccessibleContext getAccessibleContext() {
                return this;
            }

            private AccessibleContext getCurrentAccessibleContext() {
                Component c = getCurrentComponent();
                if (c instanceof Accessible) {
                    return (((Accessible) c).getAccessibleContext());
                } else {
                    return null;
                }
            }

            private Component getCurrentComponent() {
                // is the object visible?
                // if so, get row, selected, focus & leaf state, 
                // and then get the renderer component and return it
                if (tree.isVisible(path)) {
                    TreeCellRenderer r = tree.getCellRenderer();
		    if (r == null) {
			return null;
		    }
                    TreeUI ui = tree.getUI();
                    if (ui != null) {
                        int row = ui.getRowForPath(JTree.this, path);
                        boolean selected = tree.isPathSelected(path);
                        boolean expanded = tree.isExpanded(path);
                        boolean hasFocus = false; // how to tell?? -PK
                        return r.getTreeCellRendererComponent(tree, obj, 
                            selected, expanded, isLeaf, row, hasFocus);
                    }
                } 
                return null;
            }

        // AccessibleContext methods
    
             /**
              * Get the accessible name of this object.
              *
              * @return the localized name of the object; null if this 
              * object does not have a name
              */
             public String getAccessibleName() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    String name = ac.getAccessibleName();
                    if ((name != null) && (name != "")) {
                        return ac.getAccessibleName();
                    } else {
                        return null;
                    }
                }
                if ((accessibleName != null) && (accessibleName != "")) {
                    return accessibleName;
                } else {
                    return null;
                }
            }
    
            /**
             * Set the localized accessible name of this object.
             *
             * @param s the new localized name of the object.
             */
            public void setAccessibleName(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleName(s);
                } else {
                    super.setAccessibleName(s);
                }
            }
    
            //
            // *** should check toolip text for desc. (needs MouseEvent)
            //
            /**
             * Get the accessible description of this object.
             *
             * @return the localized description of the object; null if 
             * this object does not have a description
             */
            public String getAccessibleDescription() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleDescription();
                } else {
                    return super.getAccessibleDescription();
                }
            }
    
            /**
             * Set the accessible description of this object.
             *
             * @param s the new localized description of the object
             */
            public void setAccessibleDescription(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleDescription(s);
                } else {
                    super.setAccessibleDescription(s);
                }
            }
    
            /**
             * Get the role of this object.
             *
             * @return an instance of AccessibleRole describing the role of the object
             * @see AccessibleRole
             */
            public AccessibleRole getAccessibleRole() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleRole();
                } else {
                    return AccessibleRole.UNKNOWN;
                }
            }
    
            /**
             * Get the state set of this object.
             *
             * @return an instance of AccessibleStateSet containing the 
             * current state set of the object
             * @see AccessibleState
             */
            public AccessibleStateSet getAccessibleStateSet() {
                AccessibleContext ac = getCurrentAccessibleContext();
                AccessibleStateSet states;
		int row = tree.getUI().getRowForPath(tree,path);
		int lsr = tree.getLeadSelectionRow();
                if (ac != null) {
                    states = ac.getAccessibleStateSet();
                } else {
                    states = new AccessibleStateSet();
                }
                // need to test here, 'cause the underlying component 
                // is a cellRenderer, which is never showing...
                if (isShowing()) {
                    states.add(AccessibleState.SHOWING);
                } else if (states.contains(AccessibleState.SHOWING)) {
                    states.remove(AccessibleState.SHOWING);
                }
                if (isVisible()) {
                    states.add(AccessibleState.VISIBLE);
                } else if (states.contains(AccessibleState.VISIBLE)) {
                    states.remove(AccessibleState.VISIBLE);
                }
                if (tree.isPathSelected(path)){
                    states.add(AccessibleState.SELECTED);
                }
		if (lsr == row) {
                    states.add(AccessibleState.ACTIVE);
                }
                if (!isLeaf) {
                    states.add(AccessibleState.EXPANDABLE);
                }
                if (tree.isExpanded(path)) {
                    states.add(AccessibleState.EXPANDED);
                } else {
                    states.add(AccessibleState.COLLAPSED);
                }
                if (tree.isEditable()) {
                    states.add(AccessibleState.EDITABLE);
                }
                return states;
            }
    
            /**
             * Get the Accessible parent of this object.
             *
             * @return the Accessible parent of this object; null if this
             * object does not have an Accessible parent
             */
            public Accessible getAccessibleParent() {
                return accessibleParent;
            }
    
            /**
             * Get the index of this object in its accessible parent. 
             *
             * @return the index of this object in its parent; -1 if this 
             * object does not have an accessible parent.
             * @see #getAccessibleParent
             */
            public int getAccessibleIndexInParent() {
                return index;
            }
    
            /**
             * Returns the number of accessible children in the object.
             *
             * @return the number of accessible children in the object.
             */
            public int getAccessibleChildrenCount() {
                // Tree nodes can't be so complex that they have 
                // two sets of children -> we're ignoring that case
                return treeModel.getChildCount(obj);
            }
    
            /**
             * Return the specified Accessible child of the object.
             *
             * @param i zero-based index of child
             * @return the Accessible child of the object
             */
            public Accessible getAccessibleChild(int i) {
                // Tree nodes can't be so complex that they have 
                // two sets of children -> we're ignoring that case
                if (i < 0 || i >= getAccessibleChildrenCount()) {
                    return null;
                } else {
                    Object childObj = treeModel.getChild(obj, i);
                    Object[] objPath = path.getPath();
                    Object[] objChildPath = new Object[objPath.length+1];
                    java.lang.System.arraycopy(objPath, 0, objChildPath, 0, objPath.length);
                    objChildPath[objChildPath.length-1] = childObj;
                    TreePath childPath = new TreePath(objChildPath);
                    return new AccessibleJTreeNode(JTree.this, childPath, this);
                }
            }
    
            /** 
             * Gets the locale of the component. If the component does not have a 
             * locale, then the locale of its parent is returned.  
             *
             * @return This component's locale. If this component does not have a locale, the locale of its parent is returned.
             * @exception IllegalComponentStateException 
             * If the Component does not have its own locale and has not yet been added to a containment hierarchy such that the locale can be
             * determined from the containing parent. 
             * @see setLocale
             */
            public Locale getLocale() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getLocale();
                } else {
                    return tree.getLocale();
                }
            }
    
            /**
             * Add a PropertyChangeListener to the listener list.
             * The listener is registered for all properties.
             *
             * @param listener  The PropertyChangeListener to be added
             */
            public void addPropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.addPropertyChangeListener(l);
                } else {
                    super.addPropertyChangeListener(l);
                }
            }
    
            /**
             * Remove a PropertyChangeListener from the listener list.
             * This removes a PropertyChangeListener that was registered
             * for all properties.
             *
             * @param listener  The PropertyChangeListener to be removed
             */
            public void removePropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.removePropertyChangeListener(l);
                } else {
                    super.removePropertyChangeListener(l);
                }
            }
    
            /**
             * Get the AccessibleAction associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleAction, or null
             */
            public AccessibleAction getAccessibleAction() {
                return this;
            }

            /**
             * Get the AccessibleComponent associated with this tree node
             * NOTE: if the node is not displayed (either scrolled off of
             * the screen, or not expanded), this will return null
             *
             * @return the AccessibleComponent of this tree node
             */
            public AccessibleComponent getAccessibleComponent() {
                return this; // to override getBounds()
            }

            /**
             * Get the AccessibleSelection associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleSelection, or null
             */
            public AccessibleSelection getAccessibleSelection() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null && isLeaf) {
                    return getCurrentAccessibleContext().getAccessibleSelection();
                } else {
                    return this;
                }
            }

            /**
             * Get the AccessibleText associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleText, or null
             */
            public AccessibleText getAccessibleText() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return getCurrentAccessibleContext().getAccessibleText();
                } else {
                    return null;
                }
            }

            /**
             * Get the AccessibleValue associated with this object if one
             * exists.  Otherwise return null.
             *
             * @return the AccessibleValue, or null
             */
            public AccessibleValue getAccessibleValue() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return getCurrentAccessibleContext().getAccessibleValue();
                } else {
                    return null;
                }
            }


        // AccessibleComponent methods
    
            /**
             * Get the background color of this object.
             *
             * @return the background color, if supported, of the object; 
             * otherwise, null
             */
            public Color getBackground() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getBackground();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getBackground();
                    } else {
                        return null;
                    }
                }
            }
    
            /**
             * Set the background color of this object.
             *
             * @param c the new Color for the background
             */
            public void setBackground(Color c) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setBackground(c);
                } else {
                    Component cp = getCurrentComponent();
                    if (cp != null) {
                        cp.setBackground(c);
                    }
                }
            }
    
        
            /**
             * Get the foreground color of this object.
             *
             * @return the foreground color, if supported, of the object; 
             * otherwise, null
             */
            public Color getForeground() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getForeground();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getForeground();
                    } else {
                        return null;
                    }
                }
            }
    
            public void setForeground(Color c) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setForeground(c);
                } else {
                    Component cp = getCurrentComponent();
                    if (cp != null) {
                        cp.setForeground(c);
                    }
                }
            }
    
            public Cursor getCursor() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getCursor();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getCursor();
                    } else {
                        Accessible ap = getAccessibleParent();
                        if (ap instanceof AccessibleComponent) {
                            return ((AccessibleComponent) ap).getCursor();
                        } else {
                            return null;
                        }
                    }
                }
            }
    
            public void setCursor(Cursor c) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setCursor(c);
                } else {
                    Component cp = getCurrentComponent();
                    if (cp != null) {
                        cp.setCursor(c);
                    }
                }
            }
    
            public Font getFont() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getFont();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getFont();
                    } else {
                        return null;
                    }
                }
            }
    
            public void setFont(Font f) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setFont(f);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setFont(f);
                    }
                }
            }
    
            public FontMetrics getFontMetrics(Font f) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getFontMetrics(f);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.getFontMetrics(f);
                    } else {
                        return null;
                    }
                }
            }
    
            public boolean isEnabled() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isEnabled();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isEnabled();
                    } else {
                        return false;
                    }
                }
            }
    
            public void setEnabled(boolean b) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setEnabled(b);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setEnabled(b);
                    }
                }
            }
    
            public boolean isVisible() {
                Rectangle pathBounds = tree.getPathBounds(path);
                Rectangle parentBounds = tree.getVisibleRect();
                if (pathBounds != null && parentBounds != null && 
                    parentBounds.intersects(pathBounds)) {
                    return true;
                } else {
                    return false;
                }
            }
    
            public void setVisible(boolean b) {
            }
    
            public boolean isShowing() {
                return (tree.isShowing() && isVisible());
            }
    
            public boolean contains(Point p) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    Rectangle r = ((AccessibleComponent) ac).getBounds();
                    return r.contains(p);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        Rectangle r = c.getBounds();
                        return r.contains(p);
                    } else {
                        return getBounds().contains(p);
                    }
                }
            }
    
            public Point getLocationOnScreen() {
                if (tree != null) {
                    Point parentLocation = tree.getLocationOnScreen();
                    Point componentLocation = getLocation();
                    componentLocation.translate(parentLocation.x, parentLocation.y);
                    return componentLocation;
                } else {
                    return null;
                }
            }
    
            protected Point getLocationInJTree() {
                Rectangle r = tree.getPathBounds(path);
                if (r != null) {
                    return r.getLocation();
                } else {
                    return null;
                }
            }

            public Point getLocation() {
                Rectangle r = getBounds();
                if (r != null) {
                    return r.getLocation();
                } else {
                    return null;
                }
            }
    
            public void setLocation(Point p) {
            }
                
            public Rectangle getBounds() {
                Rectangle r = tree.getPathBounds(path);
                Accessible parent = getAccessibleParent();
                if (parent != null) {
                    if (parent instanceof AccessibleJTreeNode) {
                        Point parentLoc = ((AccessibleJTreeNode) parent).getLocationInJTree();
                        if (parentLoc != null && r != null) {
                            r.translate(-parentLoc.x, -parentLoc.y);
                        } else {
                            return null;        // not visible!
                        }
                    } 
                }
                return r;
            }
    
            public void setBounds(Rectangle r) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setBounds(r);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setBounds(r);
                    }
                }
            }
    
            public Dimension getSize() {
                return getBounds().getSize();
            }
    
            public void setSize (Dimension d) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setSize(d);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setSize(d);
                    }
                }
            }
    
            public Accessible getAccessibleAt(Point p) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).getAccessibleAt(p);
                } else {
                    return null;
                }
            }
    
            public boolean isFocusTraversable() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isFocusTraversable();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isFocusTraversable();
                    } else {
                        return false;
                    }
                }
            }
    
            public void requestFocus() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).requestFocus();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.requestFocus();
                    }
                }
            }
    
            public void addFocusListener(FocusListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).addFocusListener(l);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.addFocusListener(l);
                    }
                }
            }
    
            public void removeFocusListener(FocusListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).removeFocusListener(l);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.removeFocusListener(l);
                    }
                }
            }

        // AccessibleSelection methods

            /**
             * Returns the number of items currently selected.
             * If no items are selected, the return value will be 0.
             *
             * @return the number of items currently selected.
             */
            public int getAccessibleSelectionCount() {
                int count = 0;
                int childCount = getAccessibleChildrenCount();
                for (int i = 0; i < childCount; i++) {
                    TreePath childPath = getChildTreePath(i);
                    if (tree.isPathSelected(childPath)) {
                       count++;
                    }
                } 
                return count;
            }

            /**
             * Returns an Accessible representing the specified selected item
             * in the object.  If there isn't a selection, or there are 
             * fewer items selcted than the integer passed in, the return
             * value will be null.
             *
             * @param i the zero-based index of selected items
             * @return an Accessible containing the selected item
             */
            public Accessible getAccessibleSelection(int i) {
                int childCount = getAccessibleChildrenCount();
                if (i < 0 || i >= childCount) {
                    return null;        // out of range
                }
                int count = 0;
                for (int j = 0; j < childCount && i >= count; j++) {
                    TreePath childPath = getChildTreePath(j);
                    if (tree.isPathSelected(childPath)) { 
                        if (count == i) {
                            return new AccessibleJTreeNode(tree, childPath, this);
                        } else {
                            count++;
                        }
                    }
                }
                return null;
            }

            /**
             * Returns true if the current child of this object is selected.
             *
             * @param i the zero-based index of the child in this Accessible 
             * object.
             * @see AccessibleContext#getAccessibleChild
             */
            public boolean isAccessibleChildSelected(int i) {
                int childCount = getAccessibleChildrenCount();
                if (i < 0 || i >= childCount) {
                    return false;       // out of range
                } else {
                    TreePath childPath = getChildTreePath(i);
                    return tree.isPathSelected(childPath);
                }
            }

            /**
             * Adds the specified selected item in the object to the object's
             * selection.  If the object supports multiple selections,
             * the specified item is added to any existing selection, otherwise
             * it replaces any existing selection in the object.  If the
             * specified item is already selected, this method has no effect.
             *
             * @param i the zero-based index of selectable items
             */
            public void addAccessibleSelection(int i) {
               TreeModel model = JTree.this.getModel();
               if (model != null) {
                   if (i >= 0 && i < getAccessibleChildrenCount()) {
                       TreePath path = getChildTreePath(i);
                       JTree.this.addSelectionPath(path);
                    }
                }
            }

            /**
             * Removes the specified selected item in the object from the 
             * object's
             * selection.  If the specified item isn't currently selected, this
             * method has no effect.
             *
             * @param i the zero-based index of selectable items
             */
            public void removeAccessibleSelection(int i) {
               TreeModel model = JTree.this.getModel();
               if (model != null) {
                   if (i >= 0 && i < getAccessibleChildrenCount()) {
                       TreePath path = getChildTreePath(i);
                       JTree.this.removeSelectionPath(path);
                    }
                }
            }

            /**
             * Clears the selection in the object, so that nothing in the
             * object is selected.
             */
            public void clearAccessibleSelection() {
                int childCount = getAccessibleChildrenCount();
                for (int i = 0; i < childCount; i++) {
                    removeAccessibleSelection(i);
                }
            }

            /**
             * Causes every selected item in the object to be selected
             * if the object supports multiple selections.
             */
            public void selectAllAccessibleSelection() {
               TreeModel model = JTree.this.getModel();
               if (model != null) {
                   int childCount = getAccessibleChildrenCount();
                   TreePath path;
                   for (int i = 0; i < childCount; i++) {
                       path = getChildTreePath(i);
                       JTree.this.addSelectionPath(path);
                   }
                }
            }

        // AccessibleAction methods

            /**
             * Returns the number of accessible actions available in this 
             * tree node.  If this node is not a leaf, there is at least 
             * one action (toggle expand), in addition to any available
             * on the object behind the TreeCellRenderer.
             *
             * @return the number of Actions in this object
             */
            public int getAccessibleActionCount() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    AccessibleAction aa = ac.getAccessibleAction();
                    if (aa != null) {
                        return (aa.getAccessibleActionCount() + (isLeaf ? 0 : 1));
                    }
                }
                return isLeaf ? 0 : 1;
            }

            /**
             * Return a description of the specified action of the tree node.
             * If this node is not a leaf, there is at least one action
             * description (toggle expand), in addition to any available
             * on the object behind the TreeCellRenderer.
             *
             * @param i zero-based index of the actions
             * @return a description of the action
             */
            public String getAccessibleActionDescription(int i) {
                if (i < 0 || i >= getAccessibleActionCount()) {
                    return null;
                }
                AccessibleContext ac = getCurrentAccessibleContext();
                if (i == 0) {
                    return "toggle expand";
                } else if (ac != null) {
                    AccessibleAction aa = ac.getAccessibleAction();
                    if (aa != null) {
                        return aa.getAccessibleActionDescription(i - 1);
                    }
                }
                return null;
            }

            /**
             * Perform the specified Action on the tree node.  If this node
             * is not a leaf, there is at least one action which can be
             * done (toggle expand), in addition to any available on the 
             * object behind the TreeCellRenderer.
             *
             * @param i zero-based index of actions
             * @return true if the the action was performed; else false.
             */
            public boolean doAccessibleAction(int i) {
                if (i < 0 || i >= getAccessibleActionCount()) {
                    return false;
                }
                AccessibleContext ac = getCurrentAccessibleContext();
                if (i == 0) {
                    if (JTree.this.isExpanded(path)) {
                        JTree.this.collapsePath(path);
                    } else {
                        JTree.this.expandPath(path);
                    }
                    return true;
                } else if (ac != null) {
                    AccessibleAction aa = ac.getAccessibleAction();
                    if (aa != null) {
                        return aa.doAccessibleAction(i - 1);
                    }
                }
                return false;
            }

        } // inner class AccessibleJTreeNode

    }  // inner class AccessibleJTree

} // End of class JTree

