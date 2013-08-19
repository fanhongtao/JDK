/*
 * @(#)SynthListUI.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.text.Position;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import java.util.ArrayList;
import java.util.TooManyListenersException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * A Windows L&F implementation of ListUI.
 * <p>
 *
 * @version 1.8, 01/23/03 (based on BasicListUI v 1.91)
 * @author Hans Muller
 * @author Philip Milne
 */
class SynthListUI extends ListUI implements SynthUI {
    protected JList list = null;
    private SynthStyle style;
    protected CellRendererPane rendererPane;

    // Listeners that this UI attaches to the JList
    protected FocusListener focusListener;
    protected MouseInputListener mouseInputListener;
    protected ListSelectionListener listSelectionListener;
    protected ListDataListener listDataListener;
    protected PropertyChangeListener propertyChangeListener;
    private KeyListener keyListener;

    // PENDING(hmuller) need a doc pointer to #getRowHeight, #maybeUpdateLayoutState
    protected int[] cellHeights = null;
    protected int cellHeight = -1;
    protected int cellWidth = -1;
    protected int updateLayoutStateNeeded = modelChanged;
    /**
     * Height of the list. When asked to paint, if the current size of
     * the list differs, this will update the layout state.
     */
    private int listHeight;

    /**
     * Width of the list. When asked to paint, if the current size of
     * the list differs, this will update the layout state.
     */
    private int listWidth;

    /**
     * The layout orientation of the list.
     */
    private int layoutOrientation;

    // Following ivars are used if the list is laying out horizontally

    /**
     * Number of columns to create.
     */
    private int columnCount;
    /**
     * Preferred height to make the list, this is only used if the 
     * the list is layed out horizontally.
     */
    private int preferredHeight;
    /**
     * Number of rows per column. This is only used if the row height is
     * fixed.
     */
    private int rowsPerColumn;

    /* The bits below define JList property changes that affect layout.
     * When one of these properties changes we set a bit in
     * updateLayoutStateNeeded.  The change is dealt with lazily, see
     * maybeUpdateLayoutState.  Changes to the JLists model, e.g. the
     * models length changed, are handled similarly, see DataListener.
     */

    protected final static int modelChanged = 1 << 0;
    protected final static int selectionModelChanged = 1 << 1;
    protected final static int fontChanged = 1 << 2;
    protected final static int fixedCellWidthChanged = 1 << 3;
    protected final static int fixedCellHeightChanged = 1 << 4;
    protected final static int prototypeCellValueChanged = 1 << 5;
    protected final static int cellRendererChanged = 1 << 6;
    private final static int layoutOrientationChanged = 1 << 7;
    private final static int heightChanged = 1 << 8;
    private final static int widthChanged = 1 << 9;
    private final static int componentOrientationChanged = 1 << 10;


    public static void loadActionMap(ActionMap map) {
        // NOTE: this needs to remain static. If you have a need to
        // have Actions that reference the UI in the ActionMap,
        // then you'll also need to change the registeration of the
        // ActionMap.
	map.put("selectPreviousColumn",
		    new IncrementLeadByColumnAction("selectPreviousColumn",
						     CHANGE_SELECTION, -1));
	map.put("selectPreviousColumnExtendSelection",
		    new IncrementLeadByColumnAction
		    ("selectPreviousColumnExtendSelection",
                     EXTEND_SELECTION, -1));
	map.put("selectNextColumn",
		    new IncrementLeadByColumnAction("selectNextColumn",
						     CHANGE_SELECTION, 1));
	map.put("selectNextColumnExtendSelection",
		    new IncrementLeadByColumnAction
		    ("selectNextColumnExtendSelection", EXTEND_SELECTION, 1));
	map.put("selectPreviousRow",
		    new IncrementLeadSelectionAction("selectPreviousRow",
						     CHANGE_SELECTION, -1));
	map.put("selectPreviousRowExtendSelection",
		    new IncrementLeadSelectionAction
		    ("selectPreviousRowExtendSelection",EXTEND_SELECTION, -1));
	map.put("selectNextRow",
		    new IncrementLeadSelectionAction("selectNextRow",
						     CHANGE_SELECTION, 1));
	map.put("selectNextRowExtendSelection",
		    new IncrementLeadSelectionAction
		    ("selectNextRowExtendSelection", EXTEND_SELECTION, 1));
	map.put("selectFirstRow",
		    new HomeAction("selectFirstRow", CHANGE_SELECTION));
	map.put("selectFirstRowExtendSelection",
		    new HomeAction("selectFirstRowExtendSelection",
				   EXTEND_SELECTION));
	map.put("selectLastRow",
		    new EndAction("selctLastRow", CHANGE_SELECTION));
	map.put("selectLastRowExtendSelection",
		    new EndAction("selectLastRowExtendSelection",
				  EXTEND_SELECTION));
	map.put("scrollUp",
		    new PageUpAction("scrollUp", CHANGE_SELECTION));
	map.put("scrollUpExtendSelection",
		    new PageUpAction("scrollUpExtendSelection",
				     EXTEND_SELECTION));
	map.put("scrollDown",
		    new PageDownAction("scrollDown", CHANGE_SELECTION));
	map.put("scrollDownExtendSelection",
		    new PageDownAction("scrollDownExtendSelection",
				       EXTEND_SELECTION));
	map.put("selectAll", new SelectAllAction("selectAll"));
	map.put("clearSelection", new 
		    ClearSelectionAction("clearSelection"));

        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
    }


    /**
     * Paint one List cell: compute the relevant state, get the "rubber stamp"
     * cell renderer component, and then use the CellRendererPane to paint it.
     * Subclasses may want to override this method rather than paint().
     *
     * @see #paint
     */
    protected void paintCell(
        Graphics g,
        int row,
        Rectangle rowBounds,
        ListCellRenderer cellRenderer,
        ListModel dataModel,
        ListSelectionModel selModel,
        int leadIndex)
    {
        Object value = dataModel.getElementAt(row);
        boolean cellHasFocus = list.hasFocus() && (row == leadIndex);
        boolean isSelected = selModel.isSelectedIndex(row);

        Component rendererComponent =
            cellRenderer.getListCellRendererComponent(list, value, row, isSelected, cellHasFocus);

        int cx = rowBounds.x;
        int cy = rowBounds.y;
        int cw = rowBounds.width;
        int ch = rowBounds.height;
        rendererPane.paintComponent(g, rendererComponent, list, cx, cy, cw, ch, true);
    }


    /**
     * Paint the rows that intersect the Graphics objects clipRect.  This
     * method calls paintCell as necessary.  Subclasses
     * may want to override these methods.
     *
     * @see #paintCell
     */
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
        switch (layoutOrientation) {
        case JList.VERTICAL_WRAP:
            if (list.getHeight() != listHeight) {
                updateLayoutStateNeeded |= heightChanged;
                redrawList();
            }
            break;
        case JList.HORIZONTAL_WRAP:
            if (list.getWidth() != listWidth) {
                updateLayoutStateNeeded |= widthChanged;
                redrawList();
            }
            break;
        default:
            break;
        }
        maybeUpdateLayoutState();

        ListCellRenderer renderer = list.getCellRenderer();
        ListModel dataModel = list.getModel();
        ListSelectionModel selModel = list.getSelectionModel();
        int size;

        if ((renderer == null) || (size = dataModel.getSize()) == 0) {
            return;
        }

        // Determine how many columns we need to paint
        Rectangle paintBounds = g.getClipBounds();

	int startColumn, endColumn;
	if (SynthLookAndFeel.isLeftToRight(list)) {
            startColumn = convertLocationToColumn(paintBounds.x,
                                                  paintBounds.y);
	    endColumn = convertLocationToColumn(paintBounds.x +
                                                paintBounds.width,
                                                paintBounds.y);
	} else {
	    startColumn = convertLocationToColumn(paintBounds.x +
                                                paintBounds.width,
                                                paintBounds.y);
            endColumn = convertLocationToColumn(paintBounds.x,
                                                  paintBounds.y);
	}
        int maxY = paintBounds.y + paintBounds.height;
        int leadIndex = list.getLeadSelectionIndex();
        int rowIncrement = (layoutOrientation == JList.HORIZONTAL_WRAP) ?
                           columnCount : 1;


        for (int colCounter = startColumn; colCounter <= endColumn;
             colCounter++) {
            // And then how many rows in this columnn
            int row = convertLocationToRowInColumn(paintBounds.y, colCounter);
            int rowCount = getRowCount(colCounter);
            int index = getModelIndex(colCounter, row);
            Rectangle rowBounds = getCellBounds(list, index, index);

            if (rowBounds == null) {
                // Not valid, bail!
                return;
            }
            while (row < rowCount && rowBounds.y < maxY &&
                   index < size) {
                rowBounds.height = getHeight(colCounter, row);
                g.setClip(rowBounds.x, rowBounds.y, rowBounds.width,
                          rowBounds.height);
                g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width,
                           paintBounds.height);
                paintCell(g, index, rowBounds, renderer, dataModel, selModel,
                          leadIndex);
                rowBounds.y += rowBounds.height;
                index += rowIncrement;
                row++;
            }
        }
    }


    /**
     * The preferredSize of the list depends upon the layout orientation.
     * <table><tr><td>Layout Orientation</td><td>Preferred Size</td></tr>
     * <tr>
     *   <td>JList.VERTICAL
     *   <td>The preferredSize of the list is total height of the rows
     *       and the maximum width of the cells.  If JList.fixedCellHeight
     *       is specified then the total height of the rows is just
     *       (cellVerticalMargins + fixedCellHeight) * model.getSize() where
     *       rowVerticalMargins is the space we allocate for drawing
     *       the yellow focus outline.  Similarly if fixedCellWidth is
     *       specified then we just use that.
     *   </td>
     * <tr>
     *   <td>JList.VERTICAL_WRAP
     *   <td>If the visible row count is greater than zero, the preferredHeight
     *       is the maximum cell height * visibleRowCount. If the visible row
     *       count is <= 0, the preferred height is either the current height
     *       of the list, or the maximum cell height, whichever is
     *       bigger. The preferred width is than the maximum cell width *
     *       number of columns needed. Where the number of columns needs is
     *       list.height / max cell height. Max cell height is either the fixed
     *       cell height, or is determined by iterating through all the cells
     *       to find the maximum height from the ListCellRenderer.
     * <tr>
     *   <td>JList.HORIZONTAL_WRAP
     *   <td>If the visible row count is greater than zero, the preferredHeight
     *       is the maximum cell height * adjustedRowCount.  Where
     *       visibleRowCount is used to determine the number of columns.
     *       Because this lays out horizontally the number of rows is
     *       then determined from the column count.  For example, lets say
     *       you have a model with 10 items and the visible row count is 8.
     *       The number of columns needed to display this is 2, but you no
     *       longer need 8 rows to display this, you only need 5, thus
     *       the adjustedRowCount is 5.
     *       <p>If the visible row
     *       count is <= 0, the preferred height is dictated by the 
     *       number of columns, which will be as many as can fit in the width
     *       of the <code>JList</code> (width / max cell width), with at
     *       least one column.  The preferred height then becomes the
     *       model size / number of columns * maximum cell height.
     *       Max cell height is either the fixed
     *       cell height, or is determined by iterating through all the cells
     *       to find the maximum height from the ListCellRenderer.
     * </table>
     * The above specifies the raw preferred width and height. The resulting
     * preferred width is the above width + insets.left + insets.right and
     * the resulting preferred height is the above height + insets.top +
     * insets.bottom. Where the <code>Insets</code> are determined from
     * <code>list.getInsets()</code>.
     *
     * @param c The JList component.
     * @return The total size of the list.
     */
    public Dimension getPreferredSize(JComponent c) {
        maybeUpdateLayoutState();

        int lastRow = list.getModel().getSize() - 1;
        if (lastRow < 0) {
            return new Dimension(0, 0);
        }

        Insets insets = list.getInsets();
        int width = cellWidth * columnCount + insets.left + insets.right;
        int height;

        if (layoutOrientation != JList.VERTICAL) {
            height = preferredHeight;
        }
        else {
            Rectangle bounds = getCellBounds(list, lastRow);

            if (bounds != null) {
                height = bounds.y + bounds.height + insets.bottom;
            }
            else {
                height = 0;
            }
        }
        return new Dimension(width, height);
    }


    /**
     * @return the preferred size
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }


    /**
     * @return the preferred size
     * @see #getPreferredSize
     */
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }


    /**
     * Selected the previous row and force it to be visible.
     *
     * @see JList#ensureIndexIsVisible
     */
    protected void selectPreviousIndex() {
        int s = list.getSelectedIndex();
        if(s > 0) {
            s -= 1;
            list.setSelectedIndex(s);
            list.ensureIndexIsVisible(s);
        }
    }


    /**
     * Selected the previous row and force it to be visible.
     *
     * @see JList#ensureIndexIsVisible
     */
    protected void selectNextIndex()
    {
        int s = list.getSelectedIndex();
        if((s + 1) < list.getModel().getSize()) {
            s += 1;
            list.setSelectedIndex(s);
            list.ensureIndexIsVisible(s);
        }
    }


    /**
     * Registers the keyboard bindings on the <code>JList</code> that the
     * <code>BasicListUI</code> is associated with. This method is called at
     * installUI() time.
     *
     * @see #installUI
     */
    protected void installKeyboardActions() {
	InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);

	SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED,
					   inputMap);

        LazyActionMap.installLazyActionMap(list, SynthListUI.class,
                                           "List.actionMap");
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_FOCUSED) {
            SynthContext context = getContext(list, ENABLED);
	    InputMap keyMap = (InputMap)context.getStyle().get(context,
                                                        "List.focusInputMap");
	    InputMap rtlKeyMap;

	    if (!SynthLookAndFeel.isLeftToRight(list) && 
                      (rtlKeyMap = (InputMap)context.getStyle().get(context,
                      "List.focusInputMap.RightToLeft")) != null) {
                rtlKeyMap.setParent(keyMap);
                keyMap = rtlKeyMap;
	    }
            context.dispose();
            return keyMap;
	}
	return null;
    }

    /**
     * Unregisters keyboard actions installed from
     * <code>installKeyboardActions</code>.
     * This method is called at uninstallUI() time - subclassess should
     * ensure that all of the keyboard actions registered at installUI
     * time are removed here.
     *
     * @see #installUI
     */
    protected void uninstallKeyboardActions() {
	SwingUtilities.replaceUIActionMap(list, null);
	SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED, null);
    }


    /**
     * Create and install the listeners for the JList, its model, and its
     * selectionModel.  This method is called at installUI() time.
     *
     * @see #installUI
     * @see #uninstallListeners
     */
    protected void installListeners()
    {
        focusListener = createFocusListener();
        mouseInputListener = createMouseInputListener();
        propertyChangeListener = createPropertyChangeListener();
        listSelectionListener = createListSelectionListener();
        listDataListener = createListDataListener();
        keyListener = createKeyListener();

        list.addFocusListener(focusListener);
	list.addMouseListener(defaultDragRecognizer);
	list.addMouseMotionListener(defaultDragRecognizer);
        list.addMouseListener(mouseInputListener);
        list.addMouseMotionListener(mouseInputListener);
        list.addPropertyChangeListener(propertyChangeListener);
        list.addKeyListener(keyListener);

        ListModel model = list.getModel();
        if (model != null) {
            model.addListDataListener(listDataListener);
        }

        ListSelectionModel selectionModel = list.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.addListSelectionListener(listSelectionListener);
        }
    }


    /**
     * Remove the listeners for the JList, its model, and its
     * selectionModel.  All of the listener fields, are reset to
     * null here.  This method is called at uninstallUI() time,
     * it should be kept in sync with installListeners.
     *
     * @see #uninstallUI
     * @see #installListeners
     */
    protected void uninstallListeners()
    {
        list.removeFocusListener(focusListener);
	list.removeMouseListener(defaultDragRecognizer);
	list.removeMouseMotionListener(defaultDragRecognizer);
        list.removeMouseListener(mouseInputListener);
        list.removeMouseMotionListener(mouseInputListener);
        list.removePropertyChangeListener(propertyChangeListener);
        list.removeKeyListener(keyListener);

        ListModel model = list.getModel();
        if (model != null) {
            model.removeListDataListener(listDataListener);
        }

        ListSelectionModel selectionModel = list.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.removeListSelectionListener(listSelectionListener);
        }

        focusListener = null;
        mouseInputListener  = null;
        listSelectionListener = null;
        listDataListener = null;
        propertyChangeListener = null;
        keyListener = null;
    }


    /**
     * Initialize JList properties, e.g. font, foreground, and background,
     * and add the CellRendererPane.  The font, foreground, and background
     * properties are only set if their current value is either null
     * or a UIResource, other properties are set if the current
     * value is null.
     *
     * @see #uninstallDefaults
     * @see #installUI
     * @see CellRendererPane
     */
    protected void installDefaults() {
        columnCount = 1;

        list.setLayout(null);

        fetchStyle(list);

        if (list.getCellRenderer() == null) {
            list.setCellRenderer(new DefaultListCellRenderer.UIResource());
        }

	TransferHandler th = list.getTransferHandler();
	if (th == null || th instanceof UIResource) {
	    list.setTransferHandler(defaultTransferHandler);
	}
	DropTarget dropTarget = list.getDropTarget();
	if (dropTarget instanceof UIResource) {
	    try {
		dropTarget.addDropTargetListener(new ListDropTargetListener());
	    } catch (TooManyListenersException tmle) {
		// should not happen... swing drop target is multicast
	    }
	}
    }

    private void fetchStyle(JComponent c) {
        SynthContext context = getContext(list, ENABLED);
        SynthStyle oldStyle = style;

        style = SynthLookAndFeel.updateStyle(context, this);

        if (style != oldStyle) {
            context.setComponentState(SELECTED);
            Color sbg = list.getSelectionBackground();
            if (sbg == null || sbg instanceof UIResource) {
                list.setSelectionBackground(style.getColor(
                                 context, ColorType.TEXT_BACKGROUND));
            }

            Color sfg = list.getSelectionForeground();
            if (sfg == null || sfg instanceof UIResource) {
                list.setSelectionForeground(style.getColor(
                                 context, ColorType.TEXT_FOREGROUND));
            }
        }
        context.dispose();
        
    }

    /**
     * Set the JList properties that haven't been explicitly overridden to
     * null.  A property is considered overridden if its current value
     * is not a UIResource.
     *
     * @see #installDefaults
     * @see #uninstallUI
     * @see CellRendererPane
     */
    protected void uninstallDefaults() {
        if (list.getSelectionBackground() instanceof UIResource) {
            list.setSelectionBackground(null);
        }
        if (list.getSelectionForeground() instanceof UIResource) {
            list.setSelectionForeground(null);
        }
        if (list.getCellRenderer() instanceof UIResource) {
            list.setCellRenderer(null);
        }
	if (list.getTransferHandler() instanceof UIResource) {
	    list.setTransferHandler(null);
	}

        SynthContext context = getContext(list, ENABLED);

        style.uninstallDefaults(context);
        // Uninstall any class specific state that was installed in
        // fetchStyle here.
        context.dispose();
        style = null;
    }


    /**
     * Initializes <code>this.list</code> by calling <code>installDefaults()</code>,
     * <code>installListeners()</code>, and <code>installKeyboardActions()</code>
     * in order.
     *
     * @see #installDefaults
     * @see #installListeners
     * @see #installKeyboardActions
     */
    public void installUI(JComponent c)
    {
        list = (JList)c;

        layoutOrientation = list.getLayoutOrientation();

        rendererPane = new CellRendererPane();
        list.add(rendererPane);

        installDefaults();
        installListeners();
        installKeyboardActions();
    }


    /**
     * Uninitializes <code>this.list</code> by calling <code>uninstallListeners()</code>,
     * <code>uninstallKeyboardActions()</code>, and <code>uninstallDefaults()</code>
     * in order.  Sets this.list to null.
     *
     * @see #uninstallListeners
     * @see #uninstallKeyboardActions
     * @see #uninstallDefaults
     */
    public void uninstallUI(JComponent c)
    {
        uninstallListeners();
        uninstallDefaults();
        uninstallKeyboardActions();

        cellWidth = cellHeight = -1;
        cellHeights = null;

        listWidth = listHeight = -1;

        list.remove(rendererPane);
        rendererPane = null;
        list = null;
    }


    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    /**
     * Returns a new instance of BasicListUI.  BasicListUI delegates are
     * allocated one per JList.
     *
     * @return A new ListUI implementation for the Windows look and feel.
     */
    public static ComponentUI createUI(JComponent list) {
        return new SynthListUI();
    }


    /**
     * Convert a point in <code>JList</code> coordinates to the closest index
     * of the cell at that location. To determine if the cell actually
     * contains the specified location use a combination of this method and
     * <code>getCellBounds</code>.  Returns -1 if the model is empty.
     *
     * @return The index of the cell at location, or -1.
     * @see ListUI#locationToIndex
     */
    public int locationToIndex(JList list, Point location) {
        maybeUpdateLayoutState();
        return convertLocationToModel(location.x, location.y);
    }


    /**
     * @return The origin of the index'th cell, null if index is invalid.
     * @see ListUI#indexToLocation
     */
    public Point indexToLocation(JList list, int index) {
        maybeUpdateLayoutState();
        Rectangle rect = getCellBounds(list, index, index);

        if (rect != null) {
            return new Point(rect.x, rect.y);
        }
        return null;
    }


    /**
     * @return The bounds of the index'th cell.
     * @see ListUI#getCellBounds
     */
    public Rectangle getCellBounds(JList list, int index1, int index2) {
        maybeUpdateLayoutState();

        int minIndex = Math.min(index1, index2);
        int maxIndex = Math.max(index1, index2);

        if (minIndex >= list.getModel().getSize()) {
            return null;
        }

        Rectangle minBounds = getCellBounds(list, minIndex);

        if (minBounds == null) {
            return null;
        }
        if (minIndex == maxIndex) {
            return minBounds;
        }
        Rectangle maxBounds = getCellBounds(list, maxIndex);

        if (maxBounds != null) {
            if (layoutOrientation == JList.HORIZONTAL_WRAP) {
                int minRow = convertModelToRow(minIndex);
                int maxRow = convertModelToRow(maxIndex);

                if (minRow != maxRow) {
                    minBounds.x = 0;
                    minBounds.width = list.getWidth();
                }
            }
            else if (minBounds.x != maxBounds.x) {
                // Different columns
                minBounds.y = 0;
                minBounds.height = list.getHeight();
            }
            minBounds.add(maxBounds);
        }
        return minBounds;
    }

    /**
     * Gets the bounds of the specified model index, returning the resulting
     * bounds, or null if <code>index</code> is not valid.
     */
    private Rectangle getCellBounds(JList list, int index) {
        maybeUpdateLayoutState();

        int row = convertModelToRow(index);
        int column = convertModelToColumn(index);

        if (row == -1 || column == -1) {
            return null;
        }

        Insets insets = list.getInsets();
        int x;
        int w = cellWidth;
        int y = insets.top;
        int h;
        switch (layoutOrientation) {
        case JList.VERTICAL_WRAP:
        case JList.HORIZONTAL_WRAP:
            if (list.getComponentOrientation().isLeftToRight()) {
                x = insets.left + column * cellWidth;
            } else {
                x = list.getWidth() - insets.right - (column+1) * cellWidth;
            }
            y += cellHeight * row;
            h = cellHeight;
            break;
        default:
            x = insets.left;
            if (cellHeights == null) {
                y += (cellHeight * row);
            }
            else if (row >= cellHeights.length) {
                y = 0;
            }
            else {
                for(int i = 0; i < row; i++) {
                    y += cellHeights[i];
                }
            }
            w = list.getWidth() - (insets.left + insets.right);
            h = getRowHeight(index);
            break;
        }
        return new Rectangle(x, y, w, h);
    }

    // PENDING(hmuller) explain the cell geometry abstraction in
    // the getRowHeight javadoc

    /**
     * Returns the height of the specified row based on the current layout.
     *
     * @return The specified row height or -1 if row isn't valid.
     * @see #convertYToRow
     * @see #convertRowToY
     * @see #updateLayoutState
     */
    protected int getRowHeight(int row)
    {
        return getHeight(0, row);
    }


    /**
     * Convert the JList relative coordinate to the row that contains it,
     * based on the current layout.  If y0 doesn't fall within any row,
     * return -1.
     *
     * @return The row that contains y0, or -1.
     * @see #getRowHeight
     * @see #updateLayoutState
     */
    protected int convertYToRow(int y0)
    {
        return convertLocationToRow(0, y0, false);
    }


    /**
     * Return the JList relative Y coordinate of the origin of the specified
     * row or -1 if row isn't valid.
     *
     * @return The Y coordinate of the origin of row, or -1.
     * @see #getRowHeight
     * @see #updateLayoutState
     */
    protected int convertRowToY(int row)
    {
        if (getRowCount(0) >= row || row < 0) {
            return -1;
        }
        Rectangle bounds = getCellBounds(list, row, row);
        return bounds.y;
    }

    /**
     * Returns the height of the cell at the passed in location.
     */
    private int getHeight(int column, int row) {
        if (column < 0 || column > columnCount || row < 0) {
            return -1;
        }
        if (layoutOrientation != JList.VERTICAL) {
            return cellHeight;
        }
        if (row >= list.getModel().getSize()) {
            return -1;
        }
        return (cellHeights == null) ? cellHeight :
                           ((row < cellHeights.length) ? cellHeights[row] : -1);
    }

    /**
     * Returns the row at location x/y.
     *
     * @param closest If true and the location doesn't exactly match a
     *                particular location, this will return the closest row.
     */
    private int convertLocationToRow(int x, int y0, boolean closest) {
        int size = list.getModel().getSize();

        if (size <= 0) {
            return -1;
        }
        Insets insets = list.getInsets();
        if (cellHeights == null) {
            int row = (cellHeight == 0) ? 0 :
                           ((y0 - insets.top) / cellHeight);
            if (closest) {
                if (row < 0) {
                    row = 0;
                }
                else if (row >= size) {
                    row = size - 1;
                }
            }
            return row;
        }
        else if (size > cellHeights.length) {
            return -1;
        }
        else {
            int y = insets.top;
            int row = 0;

            if (closest && y0 < y) {
                return 0;
            }
            int i;
            for (i = 0; i < size; i++) {
                if ((y0 >= y) && (y0 < y + cellHeights[i])) {
                    return row;
                }
                y += cellHeights[i];
                row += 1;
            }
            return i - 1;
        }
    }

    /**
     * Returns the closest row that starts at the specified y-location
     * in the passed in column.
     */
    private int convertLocationToRowInColumn(int y, int column) {
        int x = 0;

        if (layoutOrientation != JList.VERTICAL) {
            if (list.getComponentOrientation().isLeftToRight()) {
                x = column * cellWidth;
            } else {
                x = list.getWidth() - (column+1)*cellWidth - list.getInsets().right;
            } 
        }
        return convertLocationToRow(x, y, true);
    }

    /**
     * Returns the closest location to the model index of the passed in
     * location.
     */
    private int convertLocationToModel(int x, int y) {
        int row = convertLocationToRow(x, y, true);
        int column = convertLocationToColumn(x, y);

        if (row >= 0 && column >= 0) {
            return getModelIndex(column, row);
        }
        return -1;
    }

    /**
     * Returns the number of rows in the given column.
     */
    private int getRowCount(int column) {
        if (column < 0 || column >= columnCount) {
            return -1;
        }
        if (layoutOrientation == JList.VERTICAL ||
                  (column == 0 && columnCount == 1)) {
            return list.getModel().getSize();
        }
        if (column >= columnCount) {
            return -1;
        }
        if (layoutOrientation == JList.VERTICAL_WRAP) {
            if (column < (columnCount - 1)) {
                return rowsPerColumn;
            }
            return list.getModel().getSize() - (columnCount - 1) *
                        rowsPerColumn;
        }
        // JList.HORIZONTAL_WRAP
        int diff = columnCount - (columnCount * rowsPerColumn -
                                  list.getModel().getSize());

        if (column >= diff) {
            return Math.max(0, rowsPerColumn - 1);
        }
        return rowsPerColumn;
    }

    /**
     * Returns the model index for the specified display location.
     * If <code>column</code>x<code>row</code> is beyond the length of the
     * model, this will return the model size - 1.
     */
    private int getModelIndex(int column, int row) {
        switch (layoutOrientation) {
        case JList.VERTICAL_WRAP:
            return Math.min(list.getModel().getSize() - 1, rowsPerColumn *
                            column + Math.min(row, rowsPerColumn-1));
        case JList.HORIZONTAL_WRAP:
            return Math.min(list.getModel().getSize() - 1, row * columnCount +
                            column);
        default:
            return row;
        }
    }

    /**
     * Returns the closest column to the passed in location.
     */
    private int convertLocationToColumn(int x, int y) {
        if (cellWidth > 0) {
            if (layoutOrientation == JList.VERTICAL) {
                return 0;
            }
            Insets insets = list.getInsets();
            int col;
            if (list.getComponentOrientation().isLeftToRight()) {
                col = (x - insets.left) / cellWidth;
            } else { 
                col = (list.getWidth() - x - insets.right) / cellWidth;
            }
            if (col < 0) {
                return 0;
            }
            else if (col >= columnCount) {
                return columnCount - 1;
            }
            return col;
        }
        return 0;
    }

    /**
     * Returns the row that the model index <code>index</code> will be
     * displayed in..
     */
    private int convertModelToRow(int index) {
        int size = list.getModel().getSize();

        if ((index < 0) || (index >= size)) {
            return -1;
        }

        if (layoutOrientation != JList.VERTICAL && columnCount > 1 &&
                                                   rowsPerColumn > 0) {
            if (layoutOrientation == JList.VERTICAL_WRAP) {
                return index % rowsPerColumn;
            }
            return index / columnCount;
        }
        return index;
    }

    /**
     * Returns the column that the model index <code>index</code> will be
     * displayed in.
     */
    private int convertModelToColumn(int index) {
        int size = list.getModel().getSize();

        if ((index < 0) || (index >= size)) {
            return -1;
        }

        if (layoutOrientation != JList.VERTICAL && rowsPerColumn > 0 &&
                                                   columnCount > 1) {
            if (layoutOrientation == JList.VERTICAL_WRAP) {
                return index / rowsPerColumn;
            }
            return index % columnCount;
        }
        return 0;
    }

    /**
     * If updateLayoutStateNeeded is non zero, call updateLayoutState() and reset
     * updateLayoutStateNeeded.  This method should be called by methods
     * before doing any computation based on the geometry of the list.
     * For example it's the first call in paint() and getPreferredSize().
     *
     * @see #updateLayoutState
     */
    protected void maybeUpdateLayoutState()
    {
        if (updateLayoutStateNeeded != 0) {
            updateLayoutState();
            updateLayoutStateNeeded = 0;
        }
    }


    /**
     * Recompute the value of cellHeight or cellHeights based
     * and cellWidth, based on the current font and the current
     * values of fixedCellWidth, fixedCellHeight, and prototypeCellValue.
     *
     * @see #maybeUpdateLayoutState
     */
    protected void updateLayoutState()
    {
        /* If both JList fixedCellWidth and fixedCellHeight have been
         * set, then initialize cellWidth and cellHeight, and set
         * cellHeights to null.
         */

        int fixedCellHeight = list.getFixedCellHeight();
        int fixedCellWidth = list.getFixedCellWidth();

        cellWidth = (fixedCellWidth != -1) ? fixedCellWidth : -1;

        if (fixedCellHeight != -1) {
            cellHeight = fixedCellHeight;
            cellHeights = null;
        }
        else {
            cellHeight = -1;
            cellHeights = new int[list.getModel().getSize()];
        }

        /* If either of  JList fixedCellWidth and fixedCellHeight haven't
         * been set, then initialize cellWidth and cellHeights by
         * scanning through the entire model.  Note: if the renderer is
         * null, we just set cellWidth and cellHeights[*] to zero,
         * if they're not set already.
         */

        if ((fixedCellWidth == -1) || (fixedCellHeight == -1)) {

            ListModel dataModel = list.getModel();
            int dataModelSize = dataModel.getSize();
            ListCellRenderer renderer = list.getCellRenderer();

            if (renderer != null) {
                for(int index = 0; index < dataModelSize; index++) {
                    Object value = dataModel.getElementAt(index);
                    Component c = renderer.getListCellRendererComponent(list, value, index, false, false);
                    rendererPane.add(c);
                    Dimension cellSize = c.getPreferredSize();
                    if (fixedCellWidth == -1) {
                        cellWidth = Math.max(cellSize.width, cellWidth);
                    }
                    if (fixedCellHeight == -1) {
                        cellHeights[index] = cellSize.height;
                    }
                }
            }
            else {
                if (cellWidth == -1) {
                    cellWidth = 0;
                }
                if (cellHeights == null) {
                    cellHeights = new int[dataModelSize];
                }
                for(int index = 0; index < dataModelSize; index++) {
                    cellHeights[index] = 0;
                }
            }
        }

        columnCount = 1;
        if (layoutOrientation != JList.VERTICAL) {
            updateHorizontalLayoutState(fixedCellWidth, fixedCellHeight);
        }
    }

    /**
     * Invoked when the list is layed out horizontally to determine how
     * many columns to create.
     * <p>
     * This updates the <code>rowsPerColumn, </code><code>columnCount</code>,
     * <code>preferredHeight</code> and potentially <code>cellHeight</code>
     * instance variables.
     */
    private void updateHorizontalLayoutState(int fixedCellWidth,
                                             int fixedCellHeight) {
        int visRows = list.getVisibleRowCount();
        int dataModelSize = list.getModel().getSize();
        Insets insets = list.getInsets();

        listHeight = list.getHeight();
        listWidth = list.getWidth();

        if (dataModelSize == 0) {
            rowsPerColumn = columnCount = 0;
            preferredHeight = insets.top + insets.bottom;
            return;
        }

        int height;

        if (fixedCellHeight != -1) {
            height = fixedCellHeight;
        }
        else {
            // Determine the max of the renderer heights.
            int maxHeight = 0;
            if (cellHeights.length > 0) {
                maxHeight = cellHeights[cellHeights.length - 1];
                for (int counter = cellHeights.length - 2;
                     counter >= 0; counter--) {
                    maxHeight = Math.max(maxHeight, cellHeights[counter]);
                }
            }
            height = cellHeight = maxHeight;
            cellHeights = null;
        }
        // The number of rows is either determined by the visible row
        // count, or by the height of the list.
        rowsPerColumn = dataModelSize;
        if (visRows > 0) {
            rowsPerColumn = visRows;
            columnCount = Math.max(1, dataModelSize / rowsPerColumn);
            if (dataModelSize > 0 && dataModelSize > rowsPerColumn &&
                dataModelSize % rowsPerColumn != 0) {
                columnCount++;
            }
            if (layoutOrientation == JList.HORIZONTAL_WRAP) {
                // Because HORIZONTAL_WRAP flows differently, the 
                // rowsPerColumn needs to be adjusted.
                rowsPerColumn = (dataModelSize / columnCount);
                if (dataModelSize % columnCount > 0) {
                    rowsPerColumn++;
                }
            }
        }
        else if (layoutOrientation == JList.VERTICAL_WRAP && height != 0) {
            rowsPerColumn = Math.max(1, (listHeight - insets.top -
                                         insets.bottom) / height);
            columnCount = Math.max(1, dataModelSize / rowsPerColumn);
            if (dataModelSize > 0 && dataModelSize > rowsPerColumn &&
                dataModelSize % rowsPerColumn != 0) {
                columnCount++;
            }
        }
        else if (layoutOrientation == JList.HORIZONTAL_WRAP && cellWidth > 0 &&
                 listWidth > 0) {
            columnCount = Math.max(1, (listWidth - insets.left -
                                       insets.right) / cellWidth);
            rowsPerColumn = dataModelSize / columnCount;
            if (dataModelSize % columnCount > 0) {
                rowsPerColumn++;
            }
        }
        preferredHeight = rowsPerColumn * cellHeight + insets.top +
                              insets.bottom;
    }


    /**
     * Mouse input, and focus handling for JList.  An instance of this
     * class is added to the appropriate java.awt.Component lists
     * at installUI() time.  Note keyboard input is handled with JComponent
     * KeyboardActions, see installKeyboardActions().
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     *
     * @see #createMouseInputListener
     * @see #installKeyboardActions
     * @see #installUI
     */
    class MouseInputHandler implements MouseInputListener
    {
        public void mouseClicked(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mousePressed(MouseEvent e)
        {
	    if (e.isConsumed()) {
		selectedOnPress = false;
		return;
	    }
	    selectedOnPress = true;
	    adjustFocusAndSelection(e);
	}

	void adjustFocusAndSelection(MouseEvent e) {
	    if (!SwingUtilities.isLeftMouseButton(e)) {
	        return;
	    }

	    if (!list.isEnabled()) {
		return;
	    }

	    /* Request focus before updating the list selection.  This implies
	     * that the current focus owner will see a focusLost() event
	     * before the lists selection is updated IF requestFocus() is
	     * synchronous (it is on Windows).  See bug 4122345
	     */
            if (!list.hasFocus() && list.isRequestFocusEnabled()) {
                list.requestFocus();
            }

            int row = convertLocationToModel(e.getX(), e.getY());
            if (row != -1) {
		boolean adjusting = (e.getID() == MouseEvent.MOUSE_PRESSED) ? true : false;
                list.setValueIsAdjusting(adjusting);
                int anchorIndex = list.getAnchorSelectionIndex();
                if (e.isControlDown()) {
                    if (list.isSelectedIndex(row)) {
                        list.removeSelectionInterval(row, row);
                    }
                    else {
                        list.addSelectionInterval(row, row);
                    }
                }
                else if (e.isShiftDown() && (anchorIndex != -1)) {
                    list.setSelectionInterval(anchorIndex, row);
                }
                else {
                    list.setSelectionInterval(row, row);
                }
            }
        }

        public void mouseDragged(MouseEvent e) {
	    if (e.isConsumed()) {
		return;
	    }
	    if (!SwingUtilities.isLeftMouseButton(e)) {
	        return;
	    }
	    if (!list.isEnabled()) {
		return;
	    }
            if (e.isShiftDown() || e.isControlDown()) {
                return;
            }

            int row = convertLocationToModel(e.getX(), e.getY());
            if (row != -1) {
                Rectangle cellBounds = getCellBounds(list, row, row);
                if (cellBounds != null) {
                    list.scrollRectToVisible(cellBounds);
                    list.setSelectionInterval(row, row);
                }
            }
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
	    if (selectedOnPress) {
		if (!SwingUtilities.isLeftMouseButton(e)) {
		    return;
		}

		list.setValueIsAdjusting(false);
	    } else {
		adjustFocusAndSelection(e);
	    }
        }

	private boolean selectedOnPress;
    }


    /**
     * Creates a delegate that implements MouseInputListener.
     * The delegate is added to the corresponding java.awt.Component listener 
     * lists at installUI() time. Subclasses can override this method to return 
     * a custom MouseInputListener, e.g.
     * <pre>
     * class MyListUI extends BasicListUI {
     *    protected MouseInputListener <b>createMouseInputListener</b>() {
     *        return new MyMouseInputHandler();
     *    }
     *    class MyMouseInputHandler extends MouseInputHandler {
     *        public void mouseMoved(MouseEvent e) {
     *            // do some extra work when the mouse moves
     *            super.mouseMoved(e);
     *        }
     *    }
     * }
     * </pre>
     *
     * @see MouseInputHandler
     * @see #installUI
     */
    protected MouseInputListener createMouseInputListener() {
        return new MouseInputHandler();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    class FocusHandler implements FocusListener
    {
        protected void repaintCellFocus()
        {
            int leadIndex = list.getLeadSelectionIndex();
            if (leadIndex != -1) {
                Rectangle r = getCellBounds(list, leadIndex, leadIndex);
                if (r != null) {
                    list.repaint(r.x, r.y, r.width, r.height);
                }
            }
        }

        /* The focusGained() focusLost() methods run when the JList
         * focus changes.
         */

        public void focusGained(FocusEvent e) {
            // hasFocus = true;
            repaintCellFocus();
        }

        public void focusLost(FocusEvent e) {
            // hasFocus = false;
            repaintCellFocus();
        }
    }

    protected FocusListener createFocusListener() {
        return new FocusHandler();
    }

    /**
     * The ListSelectionListener that's added to the JLists selection
     * model at installUI time, and whenever the JList.selectionModel property
     * changes.  When the selection changes we repaint the affected rows.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     *
     * @see #createListSelectionListener
     * @see #getCellBounds
     * @see #installUI
     */
    class ListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            maybeUpdateLayoutState();

            Rectangle bounds = getCellBounds(list, e.getFirstIndex(),
                                             e.getLastIndex());

            if (bounds != null) {
                list.repaint(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }


    /**
     * Creates an instance of ListSelectionHandler that's added to
     * the JLists by selectionModel as needed.  Subclasses can override
     * this method to return a custom ListSelectionListener, e.g.
     * <pre>
     * class MyListUI extends BasicListUI {
     *    protected ListSelectionListener <b>createListSelectionListener</b>() {
     *        return new MySelectionListener();
     *    }
     *    class MySelectionListener extends ListSelectionHandler {
     *        public void valueChanged(ListSelectionEvent e) {
     *            // do some extra work when the selection changes
     *            super.valueChange(e);
     *        }
     *    }
     * }
     * </pre>
     *
     * @see ListSelectionHandler
     * @see #installUI
     */
    protected ListSelectionListener createListSelectionListener() {
        return new ListSelectionHandler();
    }


    private void redrawList() {
	list.revalidate();
	list.repaint();
    }


    /**
     * The ListDataListener that's added to the JLists model at
     * installUI time, and whenever the JList.model property changes.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     *
     * @see JList#getModel
     * @see #maybeUpdateLayoutState
     * @see #createListDataListener
     * @see #installUI
     */
    class ListDataHandler implements ListDataListener
    {
        public void intervalAdded(ListDataEvent e) {
            updateLayoutStateNeeded = modelChanged;

            int minIndex = Math.min(e.getIndex0(), e.getIndex1());
            int maxIndex = Math.max(e.getIndex0(), e.getIndex1());

            /* Sync the SelectionModel with the DataModel.
             */

            ListSelectionModel sm = list.getSelectionModel();
            if (sm != null) {
                sm.insertIndexInterval(minIndex, maxIndex - minIndex+1, true);
            }

            /* Repaint the entire list, from the origin of
             * the first added cell, to the bottom of the
             * component.
             */
            redrawList();
        }


        public void intervalRemoved(ListDataEvent e)
        {
            updateLayoutStateNeeded = modelChanged;

            /* Sync the SelectionModel with the DataModel.
             */

            ListSelectionModel sm = list.getSelectionModel();
            if (sm != null) {
                sm.removeIndexInterval(e.getIndex0(), e.getIndex1());
            }

            /* Repaint the entire list, from the origin of
             * the first removed cell, to the bottom of the
             * component.
             */

            redrawList();
        }


        public void contentsChanged(ListDataEvent e) {
            updateLayoutStateNeeded = modelChanged;
	    redrawList();
        }
    }


    /**
     * Creates an instance of ListDataListener that's added to
     * the JLists by model as needed.  Subclasses can override
     * this method to return a custom ListDataListener, e.g.
     * <pre>
     * class MyListUI extends BasicListUI {
     *    protected ListDataListener <b>createListDataListener</b>() {
     *        return new MyListDataListener();
     *    }
     *    class MyListDataListener extends ListDataHandler {
     *        public void contentsChanged(ListDataEvent e) {
     *            // do some extra work when the models contents change
     *            super.contentsChange(e);
     *        }
     *    }
     * }
     * </pre>
     *
     * @see ListDataListener
     * @see JList#getModel
     * @see #installUI
     */
    protected ListDataListener createListDataListener() {
        return new ListDataHandler();
    }


    /**
     * The PropertyChangeListener that's added to the JList at
     * installUI time.  When the value of a JList property that
     * affects layout changes, we set a bit in updateLayoutStateNeeded.
     * If the JLists model changes we additionally remove our listeners
     * from the old model.  Likewise for the JList selectionModel.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     *
     * @see #maybeUpdateLayoutState
     * @see #createPropertyChangeListener
     * @see #installUI
     */
    class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();

            if (SynthLookAndFeel.shouldUpdateStyle(e)) {
                fetchStyle((JList)e.getSource());
            }

            /* If the JList.model property changes, remove our listener,
             * listDataListener from the old model and add it to the new one.
             */
            if (propertyName.equals("model")) {
                ListModel oldModel = (ListModel)e.getOldValue();
                ListModel newModel = (ListModel)e.getNewValue();
                if (oldModel != null) {
                    oldModel.removeListDataListener(listDataListener);
                }
                if (newModel != null) {
                    newModel.addListDataListener(listDataListener);
                }
                updateLayoutStateNeeded |= modelChanged;
		redrawList();
            }

            /* If the JList.selectionModel property changes, remove our listener,
             * listSelectionListener from the old selectionModel and add it to the new one.
             */
            else if (propertyName.equals("selectionModel")) {
                ListSelectionModel oldModel = (ListSelectionModel)e.getOldValue();
                ListSelectionModel newModel = (ListSelectionModel)e.getNewValue();
                if (oldModel != null) {
                    oldModel.removeListSelectionListener(listSelectionListener);
                }
                if (newModel != null) {
                    newModel.addListSelectionListener(listSelectionListener);
                }
                updateLayoutStateNeeded |= modelChanged;
		redrawList();
            }
            else if (propertyName.equals("cellRenderer")) {
                updateLayoutStateNeeded |= cellRendererChanged;
		redrawList();
            }
            else if (propertyName.equals("font")) {
                updateLayoutStateNeeded |= fontChanged;
		redrawList();
            }
            else if (propertyName.equals("prototypeCellValue")) {
                updateLayoutStateNeeded |= prototypeCellValueChanged;
		redrawList();
            }
            else if (propertyName.equals("fixedCellHeight")) {
                updateLayoutStateNeeded |= fixedCellHeightChanged;
		redrawList();
            }
            else if (propertyName.equals("fixedCellWidth")) {
                updateLayoutStateNeeded |= fixedCellWidthChanged;
		redrawList();
            }
            else if (propertyName.equals("cellRenderer")) {
                updateLayoutStateNeeded |= cellRendererChanged;
		redrawList();
            }
            else if (propertyName.equals("selectionForeground")) {
		list.repaint();
            }
            else if (propertyName.equals("selectionBackground")) {
		list.repaint();
            }
            else if ("layoutOrientation".equals(propertyName)) {
                updateLayoutStateNeeded |= layoutOrientationChanged;
                layoutOrientation = list.getLayoutOrientation();
                redrawList();
            }
            else if ("visibleRowCount".equals(propertyName)) {
                if (layoutOrientation != JList.VERTICAL) {
                    updateLayoutStateNeeded |= layoutOrientationChanged;
                    redrawList();
                }
            }
            else if ("componentOrientation".equals(propertyName)) {
                updateLayoutStateNeeded |= componentOrientationChanged;
                redrawList();

		InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);
		SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED,
						 inputMap);
            } else if ("transferHandler".equals(propertyName)) {
                DropTarget dropTarget = list.getDropTarget();
                if (dropTarget instanceof UIResource) {
                    try {
                        dropTarget.addDropTargetListener(new ListDropTargetListener());
                    } catch (TooManyListenersException tmle) {
                        // should not happen... swing drop target is multicast
                    }
                }
            }
        }
    }


    /**
     * Creates an instance of PropertyChangeHandler that's added to
     * the JList by installUI().  Subclasses can override this method
     * to return a custom PropertyChangeListener, e.g.
     * <pre>
     * class MyListUI extends BasicListUI {
     *    protected PropertyChangeListener <b>createPropertyChangeListener</b>() {
     *        return new MyPropertyChangeListener();
     *    }
     *    class MyPropertyChangeListener extends PropertyChangeHandler {
     *        public void propertyChange(PropertyChangeEvent e) {
     *            if (e.getPropertyName().equals("model")) {
     *                // do some extra work when the model changes
     *            }
     *            super.propertyChange(e);
     *        }
     *    }
     * }
     * </pre>
     *
     * @see PropertyChangeListener
     * @see #installUI
     */
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Creates an instance of KeyHandler that's added to
     * the JList by installUI().  
     */
    private KeyListener createKeyListener() {
	return new KeyHandler();
    }

    private static class KeyHandler implements KeyListener {

	private String prefix = "";
	private long lastTime = 0L;

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
	    JList src = (JList)e.getSource();
	    ListModel model = src.getModel();

            if (model.getSize() == 0 || e.isAltDown() || e.isControlDown() || e.isMetaDown()) {
                // Nothing to select
                return;
            }
	    boolean startingFromSelection = true;

	    char c = e.getKeyChar();

	    long time = e.getWhen();
	    int startIndex;
	    if (time - lastTime < 1000L && (prefix.length() != 1 || c != prefix.charAt(0))) {
		prefix += c;
		startIndex = src.getSelectedIndex();
	    } else {
		prefix = "" + c;
		startIndex = src.getSelectedIndex() + 1;
	    }
	    lastTime = time;

	    if (startIndex < 0 || startIndex >= model.getSize()) {
		startingFromSelection = false;
		startIndex = 0;
	    }
	    int index = src.getNextMatch(prefix, startIndex,
					 Position.Bias.Forward);
	    if (index >= 0) {
		src.setSelectedIndex(index);
	    } else if (startingFromSelection) { // wrap
		index = src.getNextMatch(prefix, 0,
					 Position.Bias.Forward);
		if (index >= 0) {
		    src.setSelectedIndex(index);
		}
	    }
	}
	
	/**
	 * Invoked when a key has been pressed.
	 */
	public void keyPressed(KeyEvent e) {
	}
	
	/**
	 * Invoked when a key has been released.
	 * See the class description for {@link KeyEvent} for a definition of 
	 * a key released event.
	 */
	public void keyReleased(KeyEvent e) {
	}
    }


    // Keyboard navigation actions.
    // NOTE: DefaultListSelectionModel.setAnchorSelectionIndex and
    // DefaultListSelectionModel.setLeadSelectionIndex both force the
    // new index to be selected. Because of this not all the bindings
    // could be appropriately implemented. Specifically those that
    // change the lead/anchor without selecting are not enabled.
    // Once this has been fixed the following actions will appropriately
    // work with selectionType == CHANGE_LEAD.

    /** Used by IncrementLeadSelectionAction. Indicates the action should
     * change the lead, and not select it. */
    private static final int CHANGE_LEAD = 0;
    /** Used by IncrementLeadSelectionAction. Indicates the action should
     * change the selection and lead. */
    private static final int CHANGE_SELECTION = 1;
    /** Used by IncrementLeadSelectionAction. Indicates the action should
     * extend the selection from the anchor to the next index. */
    private static final int EXTEND_SELECTION = 2;


    /**
     * Action to increment the selection in the list up/down a row at
     * a type. This also has the option to extend the selection, or
     * only move the lead.
     */
    private static class IncrementLeadSelectionAction extends AbstractAction {
	/** Amount to offset, subclasses will define what this means. */
	protected int amount;
	/** One of CHANGE_LEAD, CHANGE_SELECTION or EXTEND_SELECTION. */
	protected int selectionType;

	protected IncrementLeadSelectionAction(String name, int type) {
	    this(name, type, -1);
	}

	protected IncrementLeadSelectionAction(String name, int type,
					       int amount) {
	    super(name);
	    this.amount = amount;
	    this.selectionType = type;
	}

	/**
	 * Returns the next index to select. This is based on the lead
	 * selected index and the <code>amount</code> ivar.
	 */
	protected int getNextIndex(JList list) {
	    int index = list.getLeadSelectionIndex();
	    int size = list.getModel().getSize();

	    if (index == -1) {
		if (size > 0) {
		    if (amount > 0) {
			index = 0;
		    }
		    else {
			index = size - 1;
		    }
		}
	    }
	    else {
		index += getAmount(list);
	    }
	    return index;
	}

        /**
         * Returns the amount to increment by.
         */
        protected int getAmount(JList list) {
            if (list.getLayoutOrientation() == JList.HORIZONTAL_WRAP) {
                ListUI ui = list.getUI();

                // PENDING:
                if (ui instanceof SynthListUI) {
                    return ((SynthListUI)ui).columnCount * amount;
                }
            }
            return amount;
        }

	/**
	 * Ensures the particular index is visible. This simply forwards
	 * the method to list.
	 */
	protected void ensureIndexIsVisible(JList list, int index) {
	    list.ensureIndexIsVisible(index);
	}

	/**
	 * Invokes <code>getNextIndex</code> to determine the next index
	 * to select. If the index is valid (not -1 and < size of the model),
	 * this will either: move the selection to the new index if
	 * the selectionType == CHANGE_SELECTION, move the lead to the
	 * new index if selectionType == CHANGE_LEAD, otherwise the
	 * selection is extend from the anchor to the new index and the
	 * lead is set to the new index.
	 */
	public void actionPerformed(ActionEvent e) {
	    JList list = (JList)e.getSource();
	    int index = getNextIndex(list);
	    if (index >= 0 && index < list.getModel().getSize()) {
		ListSelectionModel lsm = list.getSelectionModel();

		if (selectionType == EXTEND_SELECTION) {
		    /* 
		       The following block is supposed to handle the 
		       case when the control modifier is used 
		       to move the lead without changing the 
		       selection. The DefaultListSelectionModel 
		       needs a new property here, to change the  
		       behavior of "setLeadSelectionIndex" so 
		       that it does not adjust the selection. 
		       Until then, this cannot be implemented 
		       properly and we will remove this code 
		       altogether to fix bug #4317662. 
		    */
		    /*
		    int anchor = lsm.getAnchorSelectionIndex();
		    if (anchor == -1) {
			anchor = index;
		    }
		    list.setSelectionInterval(anchor, index);
		    lsm.setAnchorSelectionIndex(anchor);
		    */
		    lsm.setLeadSelectionIndex(index);
		}
		else if (selectionType == CHANGE_SELECTION) {
		    list.setSelectedIndex(index);
		}
		else {
		    lsm.setLeadSelectionIndex(index);
		}
		ensureIndexIsVisible(list, index);
	    }
	}
    }


    /**
      * IncrementLeadByColumnAction extends the selection to the
      * next column. If there is only one column in the list, this will
      * not change the selection in anyway.
      */
    private static class IncrementLeadByColumnAction extends
                    IncrementLeadSelectionAction {
	IncrementLeadByColumnAction(String name, int type, int amount) {
            super(name, type, amount);
        }

        /**
          * Maps the current lead to a column, and adds the amount passed
          * into the constructor to it. This will return -1 if the
          * list is currently not layed out horizontally.
          */
	protected int getNextIndex(JList list) {
            if (list.getLayoutOrientation() != JList.VERTICAL) {
                ListUI ui = list.getUI();

                // PENDING:
                if (ui instanceof SynthListUI) {
                    SynthListUI bui = (SynthListUI)ui;

                    if (bui.columnCount > 1) {
                        int index = list.getLeadSelectionIndex();

                        if (index == -1) {
                            return 0;
                        }
                        int size = list.getModel().getSize();
                        int column = bui.convertModelToColumn(index);
                        int row = bui.convertModelToRow(index);

                        column += amount;
                        if (column >= bui.columnCount || column < 0) {
                            // No wrapping.
                            return -1;
                        }
                        int maxRowCount = bui.getRowCount(column);
                        if (row >= maxRowCount) {
                            row = maxRowCount - 1;
                        }
                        return bui.getModelIndex(column, row);
                    }
                }
            }
            // Won't change the selection.
            return -1;
        }        
    }


    /**
     * Action to move the selection to the first item in the list.
     */
    private static class HomeAction extends IncrementLeadSelectionAction {
	protected HomeAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    return 0;
	}
    }


    /**
     * Action to move the selection to the last item in the list.
     */
    private static class EndAction extends IncrementLeadSelectionAction {
	protected EndAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    return list.getModel().getSize() - 1;
	}
    }


    /**
     * Action to move up one page.
     */
    private static class PageUpAction extends IncrementLeadSelectionAction {
	protected PageUpAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    int index = list.getFirstVisibleIndex();
	    ListSelectionModel lsm = list.getSelectionModel();

	    if (lsm.getLeadSelectionIndex() == index) {
		Rectangle visRect = list.getVisibleRect();
		visRect.y = Math.max(0, visRect.y - visRect.height);
		index = list.locationToIndex(visRect.getLocation());
	    }
	    return index;
	}

	protected void ensureIndexIsVisible(JList list, int index) {
	    Rectangle visRect = list.getVisibleRect();
	    Rectangle cellBounds = list.getCellBounds(index, index);
	    cellBounds.height = visRect.height;
	    list.scrollRectToVisible(cellBounds);
	}
    }


    /**
     * Action to move down one page.
     */
    private static class PageDownAction extends IncrementLeadSelectionAction {
	protected PageDownAction(String name, int type) {
	    super(name, type);
	}

	protected int getNextIndex(JList list) {
	    int index = list.getLastVisibleIndex();
	    ListSelectionModel lsm = list.getSelectionModel();

	    if (index == -1) {
		// Will happen if size < viewport size.
		index = list.getModel().getSize() - 1;
	    }
	    if (lsm.getLeadSelectionIndex() == index) {
		Rectangle visRect = list.getVisibleRect();
		visRect.y += visRect.height + visRect.height - 1;
		index = list.locationToIndex(visRect.getLocation());
		if (index == -1) {
		    index = list.getModel().getSize() - 1;
		}
	    }
	    return index;
	}

	protected void ensureIndexIsVisible(JList list, int index) {
	    Rectangle visRect = list.getVisibleRect();
	    Rectangle cellBounds = list.getCellBounds(index, index);
	    cellBounds.y = Math.max(0, cellBounds.y + cellBounds.height -
				    visRect.height);
	    cellBounds.height = visRect.height;
	    list.scrollRectToVisible(cellBounds);
	}
    }


    /**
     * Action to select all the items in the list.
     */
    private static class SelectAllAction extends AbstractAction {
	private SelectAllAction(String name) {
	    super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    JList list = (JList)e.getSource();
	    // Select all should not alter the lead and anchor.
	    // ListSelectionModel encforces the selection to the anchor/lead,
	    // so it is commented out.

	    // ListSelectionModel lsm = list.getSelectionModel();
	    // int anchor = lsm.getAnchorSelectionIndex();
	    // int lead = lsm.getLeadSelectionIndex();
            int size = list.getModel().getSize();
            if (size > 0) {
                ListSelectionModel lsm = list.getSelectionModel();
                if (lsm.getSelectionMode() == ListSelectionModel.
                                             SINGLE_SELECTION){
                    if (list.getMinSelectionIndex() == -1) {
                        list.setSelectionInterval(0, 0);
                    }
                }
                else {
                    list.setSelectionInterval(0, size - 1);
                    list.ensureIndexIsVisible(list.getLeadSelectionIndex());
                }
            }
	    // lsm.setAnchorSelectionIndex(anchor);
	    // lsm.setLeadSelectionIndex(lead);
	}
    }


    /**
     * Action to clear the selection in the list.
     */
    private static class ClearSelectionAction extends AbstractAction {
	private ClearSelectionAction(String name) {
	    super(name);
	}

	public void actionPerformed(ActionEvent e) {
	    JList list = (JList)e.getSource();
	    // Unselect all should not alter the lead and anchor.
	    // ListSelectionModel encforces the selection to the anchor/lead,
	    // so it is commented out.

	    // ListSelectionModel lsm = list.getSelectionModel();
	    // int anchor = lsm.getAnchorSelectionIndex();
	    // int lead = lsm.getLeadSelectionIndex();
	    list.clearSelection();
	    // lsm.setAnchorSelectionIndex(anchor);
	    // lsm.setLeadSelectionIndex(lead);
	}
    }

    private static final ListDragGestureRecognizer defaultDragRecognizer = new ListDragGestureRecognizer();

    /**
     * Drag gesture recognizer for JList components
     */
    static class ListDragGestureRecognizer extends SynthDragGestureRecognizer {

        /**
         * Determines if the following are true:
         * <ul>
         * <li>the press event is located over a selection
         * <li>the dragEnabled property is true
         * <li>A TranferHandler is installed
         * </ul>
         * <p>
         * This is implemented to perform the superclass behavior
         * followed by a check if the dragEnabled 
         * property is set and if the location picked is selected.
         */
        protected boolean isDragPossible(MouseEvent e) {
            if (super.isDragPossible(e)) {
                JList list = (JList) this.getComponent(e);
                if (list.getDragEnabled()) {
                    ListUI ui = list.getUI();
                    int row = ui.locationToIndex(list, new Point(e.getX(),e.getY()));
                    if ((row != -1) && list.isSelectedIndex(row)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * A DropTargetListener to extend the default Swing handling of drop operations
     * by moving the list selection to the nearest location to the mouse pointer.
     * Also adds autoscroll.
     */
    class ListDropTargetListener extends SynthDropTargetListener {
        /**
         * called to save the state of a component in case it needs to
         * be restored because a drop is not performed.
         */
        protected void saveComponentState(JComponent comp) {
            JList list = (JList) comp;
            selectedIndices = list.getSelectedIndices();
        }

        /**
         * called to restore the state of a component 
         * because a drop was not performed.
         */
        protected void restoreComponentState(JComponent comp) {
            JList list = (JList) comp;
            list.setSelectedIndices(selectedIndices);
        }

        /**
         * called to set the insertion location to match the current
         * mouse pointer coordinates.
         */
        protected void updateInsertionLocation(JComponent comp, Point p) {
            JList list = (JList) comp;
            int index = convertLocationToModel(p.x, p.y);
            if (index != -1) {
                list.setSelectionInterval(index, index);
            }
        }

        private int[] selectedIndices;
    }

    private static final TransferHandler defaultTransferHandler = new ListTransferHandler();

    static class ListTransferHandler extends TransferHandler implements UIResource {

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
            if (c instanceof JList) {
                JList list = (JList) c;
                Object[] values = list.getSelectedValues();

                if (values == null || values.length == 0) {
                    return null;
                }
                
                StringBuffer plainBuf = new StringBuffer();
                StringBuffer htmlBuf = new StringBuffer();
                
                htmlBuf.append("<html>\n<body>\n<ul>\n");

                for (int i = 0; i < values.length; i++) {
                    Object obj = values[i];
                    String val = ((obj == null) ? "" : obj.toString());
                    plainBuf.append(val + "\n");
                    htmlBuf.append("  <li>" + val + "\n");
                }
                
                // remove the last newline
                plainBuf.deleteCharAt(plainBuf.length() - 1);
                htmlBuf.append("</ul>\n</body>\n</html>");
                
                return new SynthTransferable(plainBuf.toString(), htmlBuf.toString());
            }

            return null;
        }

        public int getSourceActions(JComponent c) {
            return COPY;
        }

    }
}
