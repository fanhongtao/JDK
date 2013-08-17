/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;

import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;



/**
 * A Windows L&F implementation of ListUI.
 * <p>
 *
 * @version 1.55 02/06/02
 * @author Hans Muller
 * @author Philip Milne
 */
public class BasicListUI extends ListUI
{
    protected JList list = null;
    protected CellRendererPane rendererPane;

    // Listeners that this UI attaches to the JList
    protected FocusListener focusListener;
    protected MouseInputListener mouseInputListener;
    protected ListSelectionListener listSelectionListener;
    protected ListDataListener listDataListener;
    protected PropertyChangeListener propertyChangeListener;

    // PENDING(hmuller) need a doc pointer to #getRowHeight, #maybeUpdateLayoutState
    protected int[] cellHeights = null;
    protected int cellHeight = -1;
    protected int cellWidth = -1;
    protected int updateLayoutStateNeeded = modelChanged;

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
    public void paint(Graphics g, JComponent c)
    {
        maybeUpdateLayoutState();

        ListCellRenderer renderer = list.getCellRenderer();
        ListModel dataModel = list.getModel();
        ListSelectionModel selModel = list.getSelectionModel();

        if ((renderer == null) || (dataModel.getSize() == 0)) {
            return;
        }

        /* Compute the area we're going to paint in terms of the affected
         * rows (firstPaintRow, lastPaintRow), and the clip bounds.
         */

        Rectangle paintBounds = g.getClipBounds();
        int firstPaintRow = convertYToRow(paintBounds.y);
        int lastPaintRow = convertYToRow((paintBounds.y + paintBounds.height) - 1);

        if (firstPaintRow == -1) {
            firstPaintRow = 0;
        }
        if (lastPaintRow == -1) {
            lastPaintRow = dataModel.getSize() - 1;
        }

        Rectangle rowBounds = getCellBounds(list, firstPaintRow, firstPaintRow);
        if (rowBounds == null) {
            return;
        }

        int leadIndex = list.getLeadSelectionIndex();

        for(int row = firstPaintRow; row <= lastPaintRow; row++) {
            rowBounds.height = getRowHeight(row);

            /* Set the clip rect to be the intersection of rowBounds
             * and paintBounds and then paint the cell.
             */

            g.setClip(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height);
            g.clipRect(paintBounds.x, paintBounds.y, paintBounds.width, paintBounds.height);

            paintCell(g, row, rowBounds, renderer, dataModel, selModel, leadIndex);

            rowBounds.y += rowBounds.height;
        }
    }


    /**
     * The preferredSize of a list is total height of the rows
     * and the maximum width of the cells.  If JList.fixedCellHeight
     * is specified then the total height of the rows is just
     * (cellVerticalMargins + fixedCellHeight) * model.getSize() where
     * rowVerticalMargins is the space we allocate for drawing
     * the yellow focus outline.  Similarly if JListfixedCellWidth is
     * specified then we just use that plus the horizontal margins.
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

        int width = cellWidth + insets.left + insets.right;
        int height = convertRowToY(lastRow) + getRowHeight(lastRow) + insets.bottom;
        return new Dimension(width, height);
    }


    /**
     * @returns The preferred size.
     * @see #getPreferredSize
     */
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }


    /**
     * @returns The preferred size.
     * @see #getPreferredSize
     */
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }


    /**
     * Selected the previous row and force it to be visible.
     * Called by the KeyEvent.VK_UP keyboard action.
     *
     * @see #installKeyboardActions
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
     * Called by the KeyEvent.VK_DOWN keyboard action.
     *
     * @see #installKeyboardActions
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
     * Register keyboard actions for the up and down arrow keys.  The
     * actions just call out to protected methods, subclasses that
     * want to override or extend keyboard behavior should consider
     * just overriding those methods.  This method is called at
     * installUI() time.
     *
     * @see #selectPreviousIndex
     * @see #selectNextIndex
     * @see #installUI
     */
    protected void installKeyboardActions() {
	InputMap inputMap = getInputMap(JComponent.WHEN_FOCUSED);

	SwingUtilities.replaceUIInputMap(list, JComponent.WHEN_FOCUSED,
					   inputMap);
	ActionMap map = getActionMap();

	if (map != null) {
	    SwingUtilities.replaceUIActionMap(list, map);
	}
    }

    InputMap getInputMap(int condition) {
	if (condition == JComponent.WHEN_FOCUSED) {
	    return (InputMap)UIManager.get("List.focusInputMap");
	}
	return null;
    }

    ActionMap getActionMap() {
	ActionMap map = (ActionMap)UIManager.get("List.actionMap");

	if (map == null) {
	    map = createActionMap();
	    if (map != null) {
		UIManager.put("List.actionMap", map);
	    }
	}
	return map;
    }

    ActionMap createActionMap() {
	ActionMap map = new ActionMapUIResource();
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
	return map;
    }

    /**
     * Unregister keyboard actions for the up and down arrow keys.
     * This method is called at uninstallUI() time - subclassess should
     * ensure that all of the keyboard actions registered at installUI
     * time are removed here.
     *
     * @see #selectPreviousIndex
     * @see #selectNextIndex
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

        list.addFocusListener(focusListener);
        list.addMouseListener(mouseInputListener);
        list.addMouseMotionListener(mouseInputListener);
        list.addPropertyChangeListener(propertyChangeListener);

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
        list.removeMouseListener(mouseInputListener);
        list.removeMouseMotionListener(mouseInputListener);
        list.removePropertyChangeListener(propertyChangeListener);

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
    protected void installDefaults()
    {
        list.setLayout(null);

        LookAndFeel.installBorder(list, "List.border");

        LookAndFeel.installColorsAndFont(list, "List.background", "List.foreground", "List.font");

        if (list.getCellRenderer() == null) {
            list.setCellRenderer((ListCellRenderer)(UIManager.get("List.cellRenderer")));
        }

        Color sbg = list.getSelectionBackground();
        if (sbg == null || sbg instanceof UIResource) {
            list.setSelectionBackground(UIManager.getColor("List.selectionBackground"));
        }

        Color sfg = list.getSelectionForeground();
        if (sfg == null || sfg instanceof UIResource) {
            list.setSelectionForeground(UIManager.getColor("List.selectionForeground"));
        }
    }


    /**
     * Set the JList properties that haven't been explicitly overriden to
     * null.  A property is considered overridden if its current value
     * is not a UIResource.
     *
     * @see #installDefaults
     * @see #uninstallUI
     * @see CellRendererPane
     */
    protected void uninstallDefaults()
    {
        if (list.getCellRenderer() instanceof UIResource) {
            list.setCellRenderer(null);
        }
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
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

        cellWidth = cellHeight = -1;
        cellHeights = null;

        list.remove(rendererPane);
        rendererPane = null;
        list = null;
    }


    /**
     * Returns a new instance of BasicListUI.  BasicListUI delegates are
     * allocated one per JList.
     *
     * @return A new ListUI implementation for the Windows look and feel.
     */
    public static ComponentUI createUI(JComponent list) {
        return new BasicListUI();
    }


    /**
     * @return The index of the cell at location, or -1.
     * @see ListUI#locationToIndex
     */
    public int locationToIndex(JList list, Point location) {
        maybeUpdateLayoutState();
        return convertYToRow(location.y);
    }


    /**
     * @return The origin of the index'th cell, null if index is invalid.
     * @see ListUI#indexToLocation
     */
    public Point indexToLocation(JList list, int index) {
        maybeUpdateLayoutState();
	int y = convertRowToY(index);
        return (y == -1) ? null : new Point(0, y);
    }


    /**
     * @return The bounds of the index'th cell.
     * @see ListUI#getCellBounds
     */
    public Rectangle getCellBounds(JList list, int index1, int index2) {
        maybeUpdateLayoutState();

        int minIndex = Math.min(index1, index2);
        int maxIndex = Math.max(index1, index2);
        int minY = convertRowToY(minIndex);
        int maxY = convertRowToY(maxIndex);

        if ((minY == -1) || (maxY == -1)) {
            return null;
        }

        Insets insets = list.getInsets();
        int x = insets.left;
        int y = minY;
        int w = list.getWidth() - (insets.left + insets.right);
        int h = (maxY + getRowHeight(maxIndex)) - minY;
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
        if ((row < 0) || (row >= list.getModel().getSize())) {
            return -1;
        }
        return (cellHeights == null) ? cellHeight : ((row < cellHeights.length) ? cellHeights[row] : -1);
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
        int nrows = list.getModel().getSize();
        Insets insets = list.getInsets();

	if (nrows <= 0) {
	    return -1;
	}
        else if (cellHeights == null) {
            int row = (cellHeight == 0) ? 0 : ((y0 - insets.top) / cellHeight);
            return ((row < 0) || (row >= nrows)) ? -1 : row;
        }
	else if (nrows > cellHeights.length) {
	    return -1;
	}
        else {
            int y = insets.top;
            int row = 0;

            for(int i = 0; i < nrows; i++) {
                if ((y0 >= y) && (y0 < y + cellHeights[i])) {
                    return row;
                }
                y += cellHeights[i];
                row += 1;
            }
            return -1;
        }
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
        int nrows = list.getModel().getSize();
        Insets insets = list.getInsets();

        if ((row < 0) || (row >= nrows)) {
            return -1;
        }

        if (cellHeights == null) {
            return insets.top + (cellHeight * row);
        }
	else if (row >= cellHeights.length) {
	    return -1;
	}
        else {
            int y = insets.top;
            for(int i = 0; i < row; i++) {
                y += cellHeights[i];
            }
            return y;
        }
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
                for(int index = 0; index < dataModelSize; index++) {
                    cellHeights[index] = 0;
                }
            }
        }

        list.invalidate();
    }


    /**
     * Mouse input, and focus handling for JList.  An instance of this
     * class is added to the appropriate java.awt.Component lists
     * at installUI() time.  Note keyboard input is handled with JComponent
     * KeyboardActions, see installKeyboardActions().
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see #createMouseInputListener
     * @see #installKeyboardActions
     * @see #installUI
     */
    public class MouseInputHandler implements MouseInputListener
    {
        public void mouseClicked(MouseEvent e) {}

        public void mouseEntered(MouseEvent e) {}

        public void mouseExited(MouseEvent e) {}

        public void mousePressed(MouseEvent e)
        {
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
            if (!list.hasFocus()) {
                list.requestFocus();
            }

            int row = convertYToRow(e.getY());
            if (row != -1) {
                list.setValueIsAdjusting(true);
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
	    if (!SwingUtilities.isLeftMouseButton(e)) {
	        return;
	    }

	    if (!list.isEnabled()) {
		return;
	    }

            if (e.isShiftDown() || e.isControlDown()) {
                return;
            }

            int row = convertYToRow(e.getY());
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
	    if (!SwingUtilities.isLeftMouseButton(e)) {
	        return;
	    }

            list.setValueIsAdjusting(false);
        }
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
     *    public class MyMouseInputHandler extends MouseInputHandler {
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
    public class FocusHandler implements FocusListener
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
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see #createListSelectionListener
     * @see #getCellBounds
     * @see #installUI
     */
    public class ListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            maybeUpdateLayoutState();

            int minY = convertRowToY(e.getFirstIndex());
            int maxY = convertRowToY(e.getLastIndex());

            if ((minY == -1) || (maxY == -1)) {
                list.repaint(0, 0, list.getWidth(), list.getHeight());
            }
            else {
                maxY += getRowHeight(e.getLastIndex());
                list.repaint(0, minY, list.getWidth(), maxY - minY);
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
     *    public class MySelectionListener extends ListSelectionHandler {
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
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see JList#getModel
     * @see #maybeUpdateLayoutState
     * @see #createListDataListener
     * @see #installUI
     */
    public class ListDataHandler implements ListDataListener
    {
        public void intervalAdded(ListDataEvent e) {
            updateLayoutStateNeeded = modelChanged;

            int minIndex = Math.min(e.getIndex0(), e.getIndex1());
            int maxIndex = Math.max(e.getIndex0(), e.getIndex1());

            /* Sync the SelectionModel with the DataModel.
             */

            ListSelectionModel sm = list.getSelectionModel();
            if (sm != null) {
                sm.insertIndexInterval(minIndex, maxIndex - minIndex, true);
            }

            /* Repaint the entire list, from the origin of
             * the first added cell, to the bottom of the
             * component.
             */

            int y = Math.max(0, convertRowToY(minIndex));
            int h = list.getHeight() - y;
            list.revalidate();
            list.repaint(0, y, list.getWidth(), h);
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

            int minIndex = Math.min(e.getIndex0(), e.getIndex1());
            int y = Math.max(0, convertRowToY(minIndex));
            int h = list.getHeight() - y;
            list.revalidate();
            list.repaint(0, y, list.getWidth(), h);
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
     *    public class MyListDataListener extends ListDataHandler {
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
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     *
     * @see #maybeUpdateLayoutState
     * @see #createPropertyChangeListener
     * @see #installUI
     */
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();

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
     *    public class MyPropertyChangeListener extends PropertyChangeHandler {
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
		index += amount;
	    }
	    return index;
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
		    int anchor = lsm.getAnchorSelectionIndex();
		    if (anchor == -1) {
			anchor = index;
		    }
		    list.setSelectionInterval(anchor, index);
		    lsm.setAnchorSelectionIndex(anchor);
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
	    list.setSelectionInterval(0, list.getModel().getSize() - 1);
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
}
