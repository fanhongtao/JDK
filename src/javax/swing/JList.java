/*
 * @(#)JList.java	1.65 99/04/22
 *
 * Copyright 1997-1999 by Sun Microsystems, Inc.,
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

import java.awt.event.*;
import java.awt.*;

import java.util.Vector;
import java.util.Locale;

import java.beans.*;

import javax.swing.event.*;
import javax.accessibility.*;
import javax.swing.plaf.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * A component that allows the user to select one or more objects from a
 * list.  A separate model, <code>ListModel</code>, represents the contents
 * of the list.  It's easy to display an array or vector of objects, using
 * a <code>JList</code> constructor that builds an <code>ListModel</code> 
 * instance for you:
 * <pre>
 * // Create a JList that displays the strings in data[]
 *
 * String[] data = {"one", "two", "free", "four"};
 * JList dataList = new JList(data);
 * 
 * // The value of the JList model property is an object that provides
 * // a read-only view of the data.  It was constructed automatically.
 *
 * for(int i = 0; i < dataList.getModel().getSize(); i++) {
 *     System.out.println(dataList.getModel().getElementAt(i));
 * }
 *
 * // Create a JList that displays the superclass of JList.class.
 * // We store the superclasses in a java.util.Vector.
 *
 * Vector superClasses = new Vector();
 * Class rootClass = javax.swing.JList.class;
 * for(Class cls = rootClass; cls != null; cls = cls.getSuperclass()) {
 *     superClasses.addElement(cls);
 * }
 * JList classList = new JList(superClasses);
 * </pre>
 * <p>
 * JList doesn't support scrolling directly.  To create a scrolling
 * list you make the JList the viewport view of a JScrollPane, e.g.
 * <pre>
 * JScrollPane scrollPane = new JScrollPane(dataList);
 * // Or in two steps:
 * JScrollPane scrollPane = new JScrollPane();
 * scrollPane.getViewport().setView(dataList);
 * </pre>
 * <p>
 * By default <code>JList</code> supports single selection, i.e. zero or one
 * index can be selected.  The selection state is actually managed
 * by a separate delegate object, an implementation of <code>ListSelectionModel</code>
 * however <code>JList</code> provides convenient properties for managing the selection.
 * <pre>
 * String[] data = {"one", "two", "free", "four"};
 * JList dataList = new JList(data);
 *
 * dataList.setSelectedIndex(1);  // select "two"
 * dataList.getSelectedValue();   // returns "two"
 * </pre>
 * <p>
 * The contents of a <code>JList</code> can be dynamic, i.e. the list elements can
 * change value and the size of the list can change after the JList has
 * been created.  The <code>JList</code> observes changes in its model with a
 * <code>swing.event.ListDataListener</code> implementation.  A correct 
 * implementation of <code>ListModel</code> notifies
 * it's listeners each time a change occurs.  The changes are
 * characterized by a <code>swing.event.ListDataEvent</code>, which identifies
 * the range of List indices that have been modified, added, or removed.
 * Simple dynamic-content <code>JList</code> applications can use the
 * <code>DefaultListModel</code> class to store list elements.  This class
 * implements the <code>ListModel</code> interface and provides the
 * <code>java.util.Vector</code> API as well.  Applications that need to 
 * provide custom <code>ListModel</code> implementations can subclass 
 * <code>AbstractListModel</code>, which provides basic 
 * <code>ListDataListener</code> support.  For example:
 * <pre>
 * // This list model has about 2^16 elements.  Enjoy scrolling.
 *
 * ListModel bigData = new AbstractListModel() {
 *     public int getSize() { return Short.MAX_VALUE; }
 *     public Object getElementAt(int index) { return "Index " + index; }
 * };
 *
 * JList bigDataList = new List(bigData);
 *
 * // We don't want the JList implementation to compute the width
 * // or height of all of the list cells, so we give it a String
 * // that's as big as we'll need for any cell.  It uses this to
 * // compute values for the fixedCellWidth and fixedCellHeight
 * // properties.
 *
 * bigDataList.setPrototypeCellValue("Index 1234567890");
 * </pre>
 * <p>
 * <code>JList</code> uses a <code>java.awt.Component</code>, provided by 
 * a delegate called the
 * <code>cellRendererer</code>, to paint the visible cells in the list.
 * The cell renderer component is used like a "rubber stamp" to paint
 * each visible row.  Each time the <code>JList</code> needs to paint a cell
 * it asks the cell renderer for the component, moves it into place
 * using <code>setBounds()</code> and then draws it by calling its paint method.
 * The default cell renderer uses a <code>JLabel</code> component to render
 * the string value of each component.   You can substitute your
 * own cell renderer, using code like this:
 * <pre>
 *  // Display an icon and a string for each object in the list.
 *
 * class MyCellRenderer extends JLabel implements ListCellRenderer {
 *     final static ImageIcon longIcon = new ImageIcon("long.gif");
 *     final static ImageIcon shortIcon = new ImageIcon("short.gif");
 *
 *     // This is the only method defined by ListCellRenderer.  We just
 *     // reconfigure the Jlabel each time we're called.
 *
 *     public Component getListCellRendererComponent(
 *       JList list,
 *       Object value,            // value to display
 *       int index,               // cell index
 *       boolean isSelected,      // is the cell selected
 *       boolean cellHasFocus)    // the list and the cell have the focus
 *     {
 *         String s = value.toString();
 *         setText(s);
 *         setIcon((s.length() > 10) ? longIcon : shortIcon);
 *   	   if (isSelected) {
 *             setBackground(list.getSelectionBackground());
 *	       setForeground(list.getSelectionForeground());
 *	   }
 *         else {
 *	       setBackground(list.getBackground());
 *	       setForeground(list.getForeground());
 *	   }
 *	   setEnabled(list.isEnabled());
 *	   setFont(list.getFont());
 *         return this;
 *     }
 * }
 *
 * String[] data = {"one", "two", "free", "four"};
 * JList dataList = new JList(data);
 * dataList.setCellRenderer(new MyCellRenderer());
 * </pre>
 * <p>
 * <code>JList</code> doesn't provide any special support for handling double or
 * triple (or N) mouse clicks however it's easy to handle them using
 * a <code>MouseListener</code>.  Use the <code>JList</code> method 
 * <code>locationToIndex()</code> to
 * determine what cell was clicked.  For example:
 * <pre>
 * final JList list = new JList(dataModel);
 * MouseListener mouseListener = new MouseAdapter() {
 *     public void mouseClicked(MouseEvent e) {
 *         if (e.getClickCount() == 2) {
 *             int index = list.locationToIndex(e.getPoint());
 *             System.out.println("Double clicked on Item " + index);
 *          }
 *     }
 * };
 * list.addMouseListener(mouseListener);
 * </pre>
 * Note that in this example the JList variable is <code>final</code>
 * because it's referred to by the anonymous MouseListener class.
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JList">JList</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see ListModel
 * @see AbstractListModel
 * @see DefaultListModel
 * @see ListSelectionModel
 * @see DefaultListSelectionModel
 * @see ListCellRenderer
 *
 * @beaninfo
 *   attribute: isContainer false
 *
 * @version 1.65 04/22/99
 * @author Hans Muller
 */
public class JList extends JComponent implements Scrollable, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ListUI";

    private int fixedCellWidth = -1;
    private int fixedCellHeight = -1;
    private int horizontalScrollIncrement = -1;
    private Object prototypeCellValue;
    private int visibleRowCount = 8;
    private Color selectionForeground;
    private Color selectionBackground;

    private ListSelectionModel selectionModel;
    private ListModel dataModel;
    private ListCellRenderer cellRenderer;
    private ListSelectionListener selectionListener;


    /**
     * Construct a JList that displays the elements in the specified,
     * non-null model.  All JList constructors delegate to this one.
     */
    public JList(ListModel dataModel)
    {
        if (dataModel == null) {
            throw new IllegalArgumentException("dataModel must be non null");
        }

        this.dataModel = dataModel;
        selectionModel = createSelectionModel();
        setAutoscrolls(true);
        setOpaque(true);
        updateUI();
    }


    /**
     * Construct a JList that displays the elements in the specified
     * array.  This constructor just delegates to the ListModel
     * constructor.
     */
    public JList(final Object[] listData)
    {
        this (
            new AbstractListModel() {
                public int getSize() { return listData.length; }
                public Object getElementAt(int i) { return listData[i]; }
            }
        );
    }


    /**
     * Construct a JList that displays the elements in the specified
     * Vector.  This constructor just delegates to the ListModel
     * constructor.
     */
    public JList(final Vector listData) {
        this (
            new AbstractListModel() {
                public int getSize() { return listData.size(); }
                public Object getElementAt(int i) { return listData.elementAt(i); }
            }
        );
    }


    /**
     * Constructs a JList with an empty model.
     */
    public JList() {
        this (
            new AbstractListModel() {
              public int getSize() { return 0; }
              public Object getElementAt(int i) { return "No Data Model"; }
            }
        );
    }


    /**
     * Returns the L&F object that renders this component.
     *
     * @return the ListUI object that renders this component
     */
    public ListUI getUI() {
        return (ListUI)ui;
    }


    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the ListUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(ListUI ui) {
        super.setUI(ui);
    }


    /**
     * Set the UI property with the "ListUI" from the current default
     * UIFactory.  This method is called by the JList constructor and
     * to update the Lists look and feel at runtime.
     *
     * @see UIManager#getUI
     */
    public void updateUI() {
        setUI((ListUI)UIManager.getUI(this));
        invalidate();
    }


    /**
     * Returns the name of the UIFactory class that generates the
     * look and feel for this component.
     *
     * @return "ListUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /* -----private-----
     * This method is called by setPrototypeCellValue and setCellRenderer
     * to update the fixedCellWidth and fixedCellHeight properties from the
     * current value of prototypeCellValue (if it's non null).
     * <p>
     * This method sets fixedCellWidth and fixedCellHeight but does <b>not</b>
     * generate PropertyChangeEvents for them.
     *
     * @see #setPrototypeCellValue
     * @see #setCellRenderer
     */
    private void updateFixedCellSize()
    {
        ListCellRenderer cr = getCellRenderer();
        Object value = getPrototypeCellValue();

        if ((cr != null) && (value != null)) {
            Component c = cr.getListCellRendererComponent(this, value, 0, false, false);

            /* The ListUI implementation will add Component c to its private
             * CellRendererPane however we can't assume that's already
             * been done here.  So we temporarilty set the one "inherited"
             * property that may affect the renderer components preferred size:
             * its font.
             */
            Font f = c.getFont();
            c.setFont(getFont());

            Dimension d = c.getPreferredSize();
            fixedCellWidth = d.width;
            fixedCellHeight = d.height;

            c.setFont(f);
        }
    }


    /**
     * Returns the cell width of the "prototypical cell" -- a cell used
     * for the calculation of cell widths, because it has the same value
     * as all other list items, instead of forcing the calculation to
     * inspect every item in the list.
     *
     * @return the value of the prototypeCellValue property
     * @see #setPrototypeCellValue
     */
    public Object getPrototypeCellValue() {
        return prototypeCellValue;
    }

    /**
     * If this value is non-null it's used to compute fixedCellWidth
     * and fixedCellHeight by configuring the cellRenderer at index equals
     * zero for the specified value and then computing the renderer components
     * preferred size.  This property is useful when the list is too long
     * to allow JList to just compute the width/height of each cell
     * and there's single cell value that's known to occupy as much space
     * as any of the others.
     * <p>
     * The default value of this property is null.
     * <p>
     * This is a JavaBeans bound property.  Note that we do set
     * the fixedCellWidth and fixedCellHeight properties here but
     * only a prototypeCellValue PropertyChangeEvent is fired.
     *
     * @param the value to base fixedCellWidth and fixedCellHeight on
     * @see #getPrototypeCellValue
     * @see #setFixedCellWidth
     * @see #setFixedCellHeight
     * @see JComponent#addPropertyChangeListener
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The cell prototype value, used to compute cell width and height.
     */
    public void setPrototypeCellValue(Object prototypeCellValue) {
        Object oldValue = this.prototypeCellValue;
        this.prototypeCellValue = prototypeCellValue;

        /* If the cellRenderer has changed and prototypeCellValue
         * was set, then recompute fixedCellWidth and fixedCellHeight.
         */

        if ((prototypeCellValue != null) && !prototypeCellValue.equals(oldValue)) {
            updateFixedCellSize();
        }

        firePropertyChange("prototypeCellValue", oldValue, prototypeCellValue);
    }


    /**
     * Returns the fixed cell width value -- the value specified by setting
     * the fixedCellWidth property, rather than calculated from the list
     * elements.
     *
     * @return the fixed cell width
     * @see #setFixedCellWidth
     */
    public int getFixedCellWidth() {
        return fixedCellWidth;
    }

    /**
     * If this value is greater than zero it defines the width of
     * every cell in the list.  Otherwise cell widths are computed
     * by applying getPreferredSize() to the cellRenderer component
     * for each list element.
     * <p>
     * The default value of this property is -1.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param the width for all cells in this list
     * @see #getPrototypeCellValue
     * @see #setFixedCellWidth
     * @see JComponent#addPropertyChangeListener
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: Defines a fixed cell width when greater than zero.
     */
    public void setFixedCellWidth(int width) {
        int oldValue = fixedCellWidth;
        fixedCellWidth = width;
        firePropertyChange("fixedCellWidth", oldValue, fixedCellWidth);
    }


    /**
     * Returns the fixed cell width value -- the value specified by setting
     * the fixedCellHeight property, rather than calculated from the list
     * elements.
     *
     * @return the fixed cell height
     * @see #setFixedCellHeight
     */
    public int getFixedCellHeight() {
        return fixedCellHeight;
    }

    /**
     * If this value is greater than zero it defines the height of
     * every cell in the list.  Otherwise cell heights are computed
     * by applying getPreferredSize() to the cellRenderer component
     * for each list element.
     * <p>
     * The default value of this property is -1.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param height an int giving the height in pixels for all cells 
     *        in this list
     * @see #getPrototypeCellValue
     * @see #setFixedCellWidth
     * @see JComponent#addPropertyChangeListener
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: Defines a fixed cell height when greater than zero.
     */
    public void setFixedCellHeight(int height) {
        int oldValue = fixedCellHeight;
        fixedCellHeight = height;
        firePropertyChange("fixedCellHeight", oldValue, fixedCellHeight);
    }


    /**
     * Returns the object that renders the list items.
     *
     * @return the ListCellRenderer
     * @see #setCellRenderer
     */
    public ListCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    /**
     * Sets the delegate that's used to paint each cell in the list.  If
     * prototypeCellValue was set then the fixedCellWidth and fixedCellHeight
     * properties are set as well.  Only one PropertyChangeEvent is generated
     * however - for the "cellRenderer" property.
     * <p>
     * The default value of this property is provided by the ListUI
     * delegate, i.e. by the look and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param cellRenderer the ListCellRenderer that paints list cells
     * @see #getCellRenderer
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The component used to draw the cells.
     */
    public void setCellRenderer(ListCellRenderer cellRenderer) {
        ListCellRenderer oldValue = this.cellRenderer;
        this.cellRenderer = cellRenderer;

        /* If the cellRenderer has changed and prototypeCellValue
         * was set, then recompute fixedCellWidth and fixedCellHeight.
         */
        if ((cellRenderer != null) && !cellRenderer.equals(oldValue)) {
            updateFixedCellSize();
        }

        firePropertyChange("cellRenderer", oldValue, cellRenderer);
    }


    /**
     * Returns the foreground color.
     *
     * @return the Color object for the foreground property
     * @see #setSelectionForeground
     * @see #setSelectionBackground
     */
    public Color getSelectionForeground() {
        return selectionForeground;
    }


    /**
     * Set the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionForeground  the Color to use in the foreground
     *                             for selected list items
     * @see #getSelectionForeground
     * @see #setSelectionBackground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The foreground color of selected cells.
     */
    public void setSelectionForeground(Color selectionForeground) {
        Color oldValue = this.selectionForeground;
        this.selectionForeground = selectionForeground;
        firePropertyChange("selectionForeground", oldValue, selectionForeground);
    }


    /**
     * Returns the background color for selected cells.
     *
     * @return the Color used for the background of selected list items
     * @see #setSelectionBackground
     * @see #setSelectionForeground
     */
    public Color getSelectionBackground() {
        return selectionBackground;
    }


    /**
     * Set the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionBackground  the Color to use for the background
     *                             of selected cells
     * @see #getSelectionBackground
     * @see #setSelectionForeground
     * @see #setForeground
     * @see #setBackground
     * @see #setFont
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The background color of selected cells.
     */
    public void setSelectionBackground(Color selectionBackground) {
        Color oldValue = this.selectionBackground;
        this.selectionBackground = selectionBackground;
        firePropertyChange("selectionBackground", oldValue, selectionBackground);
    }


    /**
     * Return the preferred number of visible rows.
     *
     * @return an int indicating the preferred number of rows to display
     *         without using a scrollbar
     * @see #setVisibleRowCount
     */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    /**
     * Set the preferred number of rows in the list that can be displayed
     * without a scollbar, as determined by the nearest JViewport ancestor,
     * if any.  The value of this property only affects the value of
     * the JLists preferredScrollableViewportSize.
     * <p>
     * The default value of this property is 8.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param visibleRowCount  an int specifying the preferred number of
     *                         visible rows
     * @see #getVisibleRowCount
     * @see JComponent#getVisibleRect
     * @see JViewport
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The preferred number of cells that can be displayed without a scrollbar.
     */
    public void setVisibleRowCount(int visibleRowCount) {
        int oldValue = this.visibleRowCount;
        this.visibleRowCount = Math.max(0, visibleRowCount);
        firePropertyChange("visibleRowCount", oldValue, visibleRowCount);
    }


    /**
     * Return the index of the cell in the upper left corner of the JList
     * or -1 if nothing is visible or the list is empty.  Note that this
     * cell may only be partially visible.
     *
     * @return an int -- the index of the first visible cell.
     * @see #getLastVisibleIndex
     * @see JComponent#getVisibleRect
     */
    public int getFirstVisibleIndex() {
        Point visibleUL = getVisibleRect().getLocation();
        return locationToIndex(visibleUL);
    }


    /**
     * Return the index of the cell in the lower right corner of the JList
     * or -1 if nothing is visible or the list is empty.  Note that this
     * cell may only be partially visible.
     *
     * @return an int -- the index of the last visible cell.
     * @see #getLastVisibleIndex
     * @see JComponent#getVisibleRect
     */
    public int getLastVisibleIndex() {
        Rectangle r = getVisibleRect();
        Point visibleLR = new Point((r.x + r.width) - 1, (r.y + r.height) - 1);
        return locationToIndex(visibleLR);
    }


    /**
     * If this JList is being displayed within a JViewport and the
     * specified cell isn't completely visible, scroll the viewport.
     *
     * @param an int -- the index of the cell to make visible
     * @see JComponent#scrollRectToVisible
     * @see #getVisibleRect
     */
    public void ensureIndexIsVisible(int index) {
        Rectangle cellBounds = getCellBounds(index, index);
        if (cellBounds != null) {
            scrollRectToVisible(cellBounds);
        }
    }


    /**
     * --- ListUI Delegations ---
     */


    /**
     * Convert a point in JList coordinates to the index
     * of the cell at that location.  Returns -1 if there's no
     * cell the specified location.
     *
     * @param location The JList relative coordinates of the cell
     * @return an int -- the index of the cell at the given location, or -1.
     */
    public int locationToIndex(Point location) {
        ListUI ui = getUI();
        return (ui != null) ? ui.locationToIndex(this, location) : -1;
    }


    /**
     * Returns the origin of the specified item in JList
     * coordinates, null if index isn't valid.
     *
     * @param index The index of the JList cell.
     * @return The origin of the index'th cell.
     */
    public Point indexToLocation(int index) {
        ListUI ui = getUI();
        return (ui != null) ? ui.indexToLocation(this, index) : null;
    }


    /**
     * Returns the bounds of the specified range of items in JList
     * coordinates, null if index isn't valid.
     *
     * @param index1  the index of the first JList cell in the range
     * @param index2  the index of the last JList cell in the range
     * @return the bounds of the indexed cells
     */
    public Rectangle getCellBounds(int index1, int index2) {
        ListUI ui = getUI();
        return (ui != null) ? ui.getCellBounds(this, index1, index2) : null;
    }


    /**
     * --- ListModel Support ---
     */


    /**
     * Returns the data model that holds the list of items displayed
     * by the JList component.
     *
     * @return the ListModel that provides the displayed list of items
     * @see #setModel
     */
    public ListModel getModel() {
        return dataModel;
    }

    /**
     * Sets the model that represents the contents or "value" of the
     * list and clears the list selection after notifying PropertyChangeListeners.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param model  the ListModel that provides the list of items for display
     * @see #getModel
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The object that contains the data to be drawn by this JList.
     */
    public void setModel(ListModel model) {
        if (model == null) {
            throw new IllegalArgumentException("model must be non null");
        }
        ListModel oldValue = dataModel;
        dataModel = model;
        firePropertyChange("model", oldValue, dataModel);
        clearSelection();
    }


    /**
     * A convenience method that constructs a ListModel from an array of Objects
     * and then applies setModel to it.
     *
     * @param listData an array of Objects containing the items to display
     *                 in the list
     * @see #setModel
     */
    public void setListData(final Object[] listData) {
        setModel (
            new AbstractListModel() {
                public int getSize() { return listData.length; }
                public Object getElementAt(int i) { return listData[i]; }
            }
        );
    }


    /**
     * A convenience method that constructs a ListModel from a Vector
     * and then applies setModel to it.
     *
     * @param listData a Vector containing the items to display in the list
     * @see #setModel
     */
    public void setListData(final Vector listData) {
        setModel (
            new AbstractListModel() {
                public int getSize() { return listData.size(); }
                public Object getElementAt(int i) { return listData.elementAt(i); }
            }
        );
    }


    /**
     * --- ListSelectionModel delegations and extensions ---
     */


    /**
     * Returns an instance of DefaultListSelectionModel.  This
     * method is used by the constructor to initialize the
     * selectionModel property.
     *
     * @return The ListSelectionModel used by this JList.
     * @see #setSelectionModel
     * @see DefaultListSelectionModel
     */
    protected ListSelectionModel createSelectionModel() {
        return new DefaultListSelectionModel();
    }


    /**
     * Returns the value of the current selection model. The selection
     * model handles the task of making single selections, selections
     * of contiguous ranges, and non-contiguous selections.
     *
     * @return the ListSelectionModel that implements list selections
     * @see #setSelectionModel
     * @see ListSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }


    /**
     * This method notifies JList ListSelectionListeners that
     * the selection model has changed.  It's used to forward
     * ListSelectionEvents from the selectionModel to the
     * ListSelectionListeners added directly to the JList.
     *
     * @see #addListSelectionListener
     * @see #removeListSelectionListener
     * @see EventListenerList
     */
    protected void fireSelectionValueChanged(int firstIndex, int lastIndex,
                                             boolean isAdjusting)
    {
        Object[] listeners = listenerList.getListenerList();
        ListSelectionEvent e = null;

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (e == null) {
                    e = new ListSelectionEvent(this, firstIndex, lastIndex,
                                               isAdjusting);
                }
                ((ListSelectionListener)listeners[i+1]).valueChanged(e);
            }
        }
    }


    /* A ListSelectionListener that forwards ListSelectionEvents from 
     * the selectionModel to the JList ListSelectionListeners.  The 
     * forwarded events only differ from the originals in that their 
     * source is the JList instead of the selectionModel itself.
     */
    private class ListSelectionHandler implements ListSelectionListener, Serializable 
    {
	public void valueChanged(ListSelectionEvent e) {
	    fireSelectionValueChanged(e.getFirstIndex(),
				      e.getLastIndex(),
				      e.getValueIsAdjusting());
	}
    }
	

    /**
     * Add a listener to the list that's notified each time a change
     * to the selection occurs.  Listeners added directly to the JList
     * will have their ListSelectionEvent.getSource() == this JList
     * (instead of the ListSelectionModel).
     *
     * @param listener The ListSelectionListener to add.
     * @see #getSelectionModel
     */
    public void addListSelectionListener(ListSelectionListener listener) 
    {
        if (selectionListener == null) {
            selectionListener = new ListSelectionHandler();
            getSelectionModel().addListSelectionListener(selectionListener);
        }

        listenerList.add(ListSelectionListener.class, listener);
    }


    /**
     * Remove a listener from the list that's notified each time a
     * change to the selection occurs.
     *
     * @param listener The ListSelectionListener to remove.
     * @see #addListSelectionListener
     * @see #getSelectionModel
     */
    public void removeListSelectionListener(ListSelectionListener listener) {
        listenerList.remove(ListSelectionListener.class, listener);
    }


    /**
     * Set the selectionModel for the list to a non-null ListSelectionModel
     * implementation. The selection model handles the task of making single
     * selections, selections of contiguous ranges, and non-contiguous
     * selections.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @return selectionModel  the ListSelectionModel that implements
     *                         list selections
     * @see #getSelectionModel
     * @beaninfo
     *       bound: true
     * description: The selection model, recording which cells are selected.
     */
    public void setSelectionModel(ListSelectionModel selectionModel) {
        if (selectionModel == null) {
            throw new IllegalArgumentException("selectionModel must be non null");
        }

        /* Remove the forwarding ListSelectionListener from the old
         * selectionModel, and add it to the new one, if neccessary.
         */
        if (selectionListener != null) {
            this.selectionModel.removeListSelectionListener(selectionListener);
            selectionModel.addListSelectionListener(selectionListener);
        }

        ListSelectionModel oldValue = this.selectionModel;
        this.selectionModel = selectionModel;
        firePropertyChange("selectionModel", oldValue, selectionModel);
    }


    /**
     * Determines whether single-item or multiple-item
     * selections are allowed.
     * The following selectionMode values are allowed:
     * <ul>
     * <li> <code>SINGLE_SELECTION</code>
     *   Only one list index can be selected at a time.  In this
     *   mode the setSelectionInterval and addSelectionInterval
     *   methods are equivalent, and they only the first index
     *   argument is used.
     * <li> <code>SINGLE_INTERVAL_SELECTION</code>
     *   One contiguous index interval can be selected at a time.
     *   In this mode setSelectionInterval and addSelectionInterval
     *   are equivalent.
     * <li> <code>MULTIPLE_INTERVAL_SELECTION</code>
     *   In this mode, there's no restriction on what can be selected.
     * </ul>
     *
     * @param selectionMode an int specifying the type of selections
     *        that are permissible
     * @see #getSelectionMode
     * @beaninfo
     * description: The selection mode.
     *        enum: SINGLE_SELECTION            ListSelectionModel.SINGLE_SELECTION
     *              SINGLE_INTERVAL_SELECTION   ListSelectionModel.SINGLE_INTERVAL_SELECTION
     *              MULTIPLE_INTERVAL_SELECTION ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
     */
    public void setSelectionMode(int selectionMode) {
        getSelectionModel().setSelectionMode(selectionMode);
    }

    /**
     * Returns whether single-item or multiple-item selections are allowed.
     * @return The value of the selectionMode property.
     * @see #setSelectionMode
     */
    public int getSelectionMode() {
        return getSelectionModel().getSelectionMode();
    }


    /**
     * Returns the first index argument from the most recent addSelectionInterval
     * or setSelectionInterval call.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @return The index that most recently anchored an interval selection.
     * @see ListSelectionModel#getAnchorSelectionIndex
     * @see #addSelectionInterval
     * @see #setSelectionInterval
     * @see #addListSelectionListener
     */
    public int getAnchorSelectionIndex() {
        return getSelectionModel().getAnchorSelectionIndex();
    }


    /**
     * Returns the second index argument from the most recent addSelectionInterval
     * or setSelectionInterval call.
     * This is a convenience method that just  delegates to the selectionModel.
     *
     * @return The index that most recently ended a interval selection.
     * @see ListSelectionModel#getLeadSelectionIndex
     * @see #addSelectionInterval
     * @see #setSelectionInterval
     * @see #addListSelectionListener
     * @beaninfo
     * description: The lead selection index.
     */
    public int getLeadSelectionIndex() {
        return getSelectionModel().getLeadSelectionIndex();
    }


    /**
     * Returns the smallest selected cell index.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @return The smallest selected cell index.
     * @see ListSelectionModel#getMinSelectionIndex
     * @see #addListSelectionListener
     */
    public int getMinSelectionIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }


    /**
     * Returns the largest selected cell index.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @return The largest selected cell index.
     * @see ListSelectionModel#getMaxSelectionIndex
     * @see #addListSelectionListener
     */
    public int getMaxSelectionIndex() {
        return getSelectionModel().getMaxSelectionIndex();
    }


    /**
     * Returns true if the specified index is selected.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @return True if the specified index is selected.
     * @see ListSelectionModel#isSelectedIndex
     * @see #setSelectedIndex
     * @see #addListSelectionListener
     */
    public boolean isSelectedIndex(int index) {
        return getSelectionModel().isSelectedIndex(index);
    }


    /**
     * Returns true if nothing is selected
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @return True if nothing is selected
     * @see ListSelectionModel#isSelectionEmpty
     * @see #clearSelection
     * @see #addListSelectionListener
     */
    public boolean isSelectionEmpty() {
        return getSelectionModel().isSelectionEmpty();
    }


    /**
     * Clears the selection - after calling this method isSelectionEmpty()
     * will return true.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @see ListSelectionModel#clearSelection
     * @see #isSelectionEmpty
     * @see #addListSelectionListener
     */
    public void clearSelection() {
        getSelectionModel().clearSelection();
    }


    /**
     * Select the specified interval.  Both the anchor and lead indices are
     * included.  It's not neccessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @param anchor The first index to select
     * @param lead The last index to select
     * @see ListSelectionModel#setSelectionInterval
     * @see #addSelectionInterval
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    public void setSelectionInterval(int anchor, int lead) {
        getSelectionModel().setSelectionInterval(anchor, lead);
    }


    /**
     * Set the selection to be the union of the specified interval with current
     * selection.  Both the anchor and lead indices are
     * included.  It's not neccessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @param anchor The first index to add to the selection
     * @param lead The last index to add to the selection
     * @see ListSelectionModel#addSelectionInterval
     * @see #setSelectionInterval
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    public void addSelectionInterval(int anchor, int lead) {
        getSelectionModel().addSelectionInterval(anchor, lead);
    }


    /**
     * Set the selection to be the set difference of the specified interval
     * and the current selection.  Both the anchor and lead indices are
     * removed.  It's not neccessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the selectionModel.
     *
     * @param anchor The first index to remove from the selection
     * @param lead The last index to remove from the selection
     * @see ListSelectionModel#removeSelectionInterval
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     * @see #addListSelectionListener
     */
    public void removeSelectionInterval(int index0, int index1) {
        getSelectionModel().removeSelectionInterval(index0, index1);
    }


    /**
     * Sets the data model's isAdjusting property true, so that
     * a single event will be generated when all of the selection
     * events have finished (for example, when the mouse is being
     * dragged over the list in selection mode).
     *
     * @param b the boolean value for the property value
     * @see ListSelectionModel#setValueIsAdjusting
     */
    public void setValueIsAdjusting(boolean b) {
        getSelectionModel().setValueIsAdjusting(b);
    }


    /**
     * Returns the value of the data model's isAdjusting property.
     * This value is true if multiple changes are being made.
     *
     * @return true if multiple selection-changes are occuring, as
     *         when the mouse is being dragged over the list
     * @see ListSelectionModel#getValueIsAdjusting
     */
    public boolean getValueIsAdjusting() {
        return getSelectionModel().getValueIsAdjusting();
    }


    /**
     * Return an array of all of the selected indices in increasing
     * order.
     *
     * @return All of the selected indices, in increasing order.
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    public int[] getSelectedIndices() {
        ListSelectionModel sm = getSelectionModel();
        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return new int[0];
        }

        int[] rvTmp = new int[1+ (iMax - iMin)];
        int n = 0;
        for(int i = iMin; i <= iMax; i++) {
            if (sm.isSelectedIndex(i)) {
                rvTmp[n++] = i;
            }
        }
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }


    /**
     * Select a single cell.
     *
     * @param index The index of the one cell to select
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     * @beaninfo
     * description: The index of the selected cell.
     */
    public void setSelectedIndex(int index) {
        getSelectionModel().setSelectionInterval(index, index);
    }


    /**
     * Select a set of cells.
     *
     * @param indices The indices of the cells to select
     * @see ListSelectionModel#addSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    public void setSelectedIndices(int[] indices) {
        ListSelectionModel sm = getSelectionModel();
        sm.clearSelection();
        for(int i = 0; i < indices.length; i++) {
            sm.addSelectionInterval(indices[i], indices[i]);
        }
    }


    /**
     * Return an array of the values for the selected cells.
     * The returned values are sorted in increasing index order.
     *
     * @return the selected values
     * @see #isSelectedIndex
     * @see #getModel
     * @see #addListSelectionListener
     */
    public Object[] getSelectedValues() {
        ListSelectionModel sm = getSelectionModel();
        ListModel dm = getModel();

        int iMin = sm.getMinSelectionIndex();
        int iMax = sm.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return new Object[0];
        }

        Object[] rvTmp = new Object[1+ (iMax - iMin)];
        int n = 0;
        for(int i = iMin; i <= iMax; i++) {
            if (sm.isSelectedIndex(i)) {
                rvTmp[n++] = dm.getElementAt(i);
            }
        }
        Object[] rv = new Object[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }


    /**
     * A convenience method that returns the first selected index.
     * Returns -1 if there is no selected item.
     *
     * @return The first selected index.
     * @see #getMinSelectionIndex
     * @see #addListSelectionListener
     */
    public int getSelectedIndex() {
        return getMinSelectionIndex();
    }


    /**
     * A convenience method that returns the first selected value
     * or null, if the selection is empty.
     *
     * @return The first selected value.
     * @see #getMinSelectionIndex
     * @see #getModel
     * @see #addListSelectionListener
     */
    public Object getSelectedValue() {
        int i = getMinSelectionIndex();
        return (i == -1) ? null : getModel().getElementAt(i);
    }


    // PENDING(hmuller) this should move to BasicComboBoxUI
    /**
     * Selects the specified object from the list.
     *
     * @param anObject      the Object to select     
     * @param shouldScroll  true if the list should scroll to display
     *                      the selected object
     */
    public void setSelectedValue(Object anObject,boolean shouldScroll) {
        if(anObject == null)
            setSelectedIndex(-1);
        else if(!anObject.equals(getSelectedValue())) {
            int i,c;
            ListModel dm = getModel();
            for(i=0,c=dm.getSize();i<c;i++)
                if(anObject.equals(dm.getElementAt(i))){
                    setSelectedIndex(i);
                    if(shouldScroll)
                        ensureIndexIsVisible(i);
                    repaint();  /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
                    return;
                }
            setSelectedIndex(-1);
        }
        repaint(); /** FIX-ME setSelectedIndex does not redraw all the time with the basic l&f**/
    }



    /**
     * --- The Scrollable Implementation ---
     */

    /**
     * Compute the size of the viewport needed to display visibleRowCount
     * rows.  This is trivial if fixedCellWidth and fixedCellHeight
     * were specified.  Note that they can specified implicitly with
     * the prototypeCellValue property.  If fixedCellWidth wasn't specified,
     * it's computed by finding the widest list element.  If fixedCellHeight
     * wasn't specified then we resort to heuristics:
     * <ul>
     * <li>
     * If the model isn't empty we just multiply the height of the first row
     * by visibleRowCount.
     * <li>
     * If the model is empty, i.e. JList.getModel().getSize() == 0, then
     * we just allocate 16 pixels per visible row, and 256 pixels
     * for the width (unless fixedCellWidth was set), and hope for the best.
     * </ul>
     *
     * @see #getPreferredScrollableViewportSize
     * @see #setPrototypeCellValue
     */
    public Dimension getPreferredScrollableViewportSize()
    {
        Insets insets = getInsets();
        int dx = insets.left + insets.right;
        int dy = insets.top + insets.bottom;

        int visibleRowCount = getVisibleRowCount();
        int fixedCellWidth = getFixedCellWidth();
        int fixedCellHeight = getFixedCellHeight();

        if ((fixedCellWidth > 0) && (fixedCellHeight > 0)) {
            int width = fixedCellWidth + dx;
            int height = (visibleRowCount * fixedCellHeight) + dy;
            return new Dimension(width, height);
        }
        else if (getModel().getSize() > 0) {
            int width = getPreferredSize().width;
            Rectangle r = getCellBounds(0, 0);
            int height = (visibleRowCount * r.height) + dy;
            return new Dimension(width, height);
        }
        else {
            fixedCellWidth = (fixedCellWidth > 0) ? fixedCellWidth : 256;
            fixedCellHeight = (fixedCellHeight > 0) ? fixedCellHeight : 16;
            return new Dimension(fixedCellWidth, fixedCellHeight * visibleRowCount);
        }
    }


    /**
     * Horizontal scrolling: return the lists font size or 1 if the font is null.
     * We're using the font size instead of the width of some canonical string,
     * e.g. "m", because it's cheaper.
     * <p>
     * Vertical scrolling: if we're scrolling downwards (<code>direction</code> is
     * greater than 0), and the first row is completely visible with respect
     * to <code>visibleRect</code>, then return its height.  If
     * we're scrolling downwards and the first row is only partially visible,
     * return the height of the visible part of the first row.  Similarly
     * if we're scrolling upwards we return the height of the row above
     * the first row, unless the first row is partially visible.
     *
     * @return The distance to scroll to expose the next or previous row.
     * @see Scrollable#getScrollableUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        if (orientation == SwingConstants.HORIZONTAL) {
	    Font f = getFont();
	    return (f != null) ? f.getSize() : 1;
	}
        else {
            int row = getFirstVisibleIndex();

            if (row == -1) {
                return 0;
            }
            else {
                /* Scroll Down */
                if (direction > 0) {
                    Rectangle r = getCellBounds(row, row);
                    return (r == null) ? 0 : r.height - (visibleRect.y - r.y);
                }
                /* Scroll Up */
                else {
                    Rectangle r = getCellBounds(row, row);

                    /* The first row is completely visible and it's row 0.
                     * We're done.
                     */
                    if ((r.y == visibleRect.y) && (row == 0))  {
                        return 0;
                    }
                    /* The first row is completely visible, return the
                     * height of the previous row.
                     */
                    else if (r.y == visibleRect.y) {
                        Rectangle prevR = getCellBounds(row - 1, row - 1);
                        return (prevR== null) ? 0 : prevR.height;
                    }
                    /* The first row is partially visible, return the
                     * height of hidden part.
                     */
                    else {
                        return visibleRect.y - r.y;
                    }
                }
            }
        }
    }


    /**
     * Returns the block increment amount.
     * @return The visibleRect.height or visibleRect.width per the orientation.
     * @see Scrollable#getScrollableUnitIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
    }


    /**
     * If this JList is displayed in a JViewport, don't change its width
     * when the viewports width changes.  This allows horizontal
     * scrolling if the JViewport is itself embedded in a JScrollPane.
     *
     * @return False - don't track the viewports width.
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportWidth() {
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	}
	return false;
    }

    /**
     * If this JList is displayed in a JViewport, don't change its height
     * when the viewports height changes.  This allows vertical
     * scrolling if the JViewport is itself embedded in a JScrollPane.
     *
     * @return False - don't track the viewports width.
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportHeight() {
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
	}
	return false;
    }


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JList. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JList.
     */
    protected String paramString() {
        String selectionForegroundString = (selectionForeground != null ?
                                            selectionForeground.toString() :
                                            "");
        String selectionBackgroundString = (selectionBackground != null ?
                                            selectionBackground.toString() :
                                            "");

	return super.paramString() +
        ",fixedCellHeight=" + fixedCellHeight +
        ",fixedCellWidth=" + fixedCellWidth +
        ",horizontalScrollIncrement=" + horizontalScrollIncrement +
        ",selectionBackground=" + selectionBackgroundString +
        ",selectionForeground=" + selectionForegroundString +
        ",visibleRowCount=" + visibleRowCount;
    }


    /**
     * --- Accessibility Support ---
     */

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJList();
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
    protected class AccessibleJList extends AccessibleJComponent
        implements AccessibleSelection, PropertyChangeListener, 
	ListSelectionListener, ListDataListener {

	int leadSelectionIndex;

        public AccessibleJList() {
            super();
            JList.this.addPropertyChangeListener(this);
            JList.this.getSelectionModel().addListSelectionListener(this);
            JList.this.getModel().addListDataListener(this);
            leadSelectionIndex = JList.this.getLeadSelectionIndex();
        }

        /**
         * Property Change Listener change method. Used to track changes
	 * to the DataModel and ListSelectionModel, in order to re-set
	 * listeners to those for reporting changes there via the Accessibility
	 * PropertyChange mechanism.
         *
         * @param e PropertyChangeEvent
         */
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            Object oldValue = e.getOldValue();
            Object newValue = e.getNewValue();

                // re-set listData listeners
            if (name.compareTo("model") == 0) {

                if (oldValue != null && oldValue instanceof ListModel) {
                    ((ListModel) oldValue).removeListDataListener(this);
                }
                if (newValue != null && newValue instanceof ListModel) {
                    ((ListModel) newValue).addListDataListener(this);
                }

                // re-set listSelectionModel listeners
            } else if (name.compareTo("selectionModel") == 0) {

                if (oldValue != null && oldValue instanceof ListSelectionModel) {
                    ((ListSelectionModel) oldValue).removeListSelectionListener(this);
                }
                if (newValue != null && newValue instanceof ListSelectionModel) {
                    ((ListSelectionModel) newValue).addListSelectionListener(this);
                }

		firePropertyChange(
		    AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
		    new Boolean(false), new Boolean(true));
	    }
	}

        /**
         * List Selection Listener value change method. Used to fire 
	 * the property change
         *
         * @param e ListSelectionEvent
         *
         */
        public void valueChanged(ListSelectionEvent e) {
	    int oldLeadSelectionIndex = leadSelectionIndex;
            leadSelectionIndex = JList.this.getLeadSelectionIndex();
	    if (oldLeadSelectionIndex != leadSelectionIndex) {
		Accessible oldLS, newLS;
		oldLS = (oldLeadSelectionIndex >= 0) 
			? getAccessibleChild(oldLeadSelectionIndex)
			: null;
		newLS = (leadSelectionIndex >= 0) 
			? getAccessibleChild(leadSelectionIndex)
			: null;
                firePropertyChange(AccessibleContext.ACCESSIBLE_ACTIVE_DESCENDANT_PROPERTY,
                                   oldLS, newLS);
	    }

            firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                               new Boolean(false), new Boolean(true));
            firePropertyChange(AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
                               new Boolean(false), new Boolean(true));

            // Process the State changes for Multiselectable
            AccessibleStateSet s = getAccessibleStateSet();
            ListSelectionModel lsm = JList.this.getSelectionModel();
            if (lsm.getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
                if (!s.contains(AccessibleState.MULTISELECTABLE)) {
                    s.add(AccessibleState.MULTISELECTABLE);
                    firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                       null, AccessibleState.MULTISELECTABLE);
                }
            } else {
                if (s.contains(AccessibleState.MULTISELECTABLE)) {
                    s.remove(AccessibleState.MULTISELECTABLE);
                    firePropertyChange(AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                                       AccessibleState.MULTISELECTABLE, null);
                }
            }
        }

        /**
         * List Data Listener interval added method. Used to fire the visible data property change
         *
         * @param e ListDataEvent
         *
         */
        public void intervalAdded(ListDataEvent e) {
            firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                               new Boolean(false), new Boolean(true));
        }

        /**
         * List Data Listener interval removed method. Used to fire the visible data property change
         *
         * @param e ListDataEvent
         *
         */
        public void intervalRemoved(ListDataEvent e) {
            firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                               new Boolean(false), new Boolean(true));
        }

        /**
         * List Data Listener contents changed method. Used to fire the visible data property change
         *
         * @param e ListDataEvent
         *
         */
         public void contentsChanged(ListDataEvent e) {
             firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                                new Boolean(false), new Boolean(true));
         }

    // AccessibleContext methods

        /**
         * Get the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state
         * of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
            AccessibleStateSet states = super.getAccessibleStateSet();
            if (selectionModel.getSelectionMode() !=
                ListSelectionModel.SINGLE_SELECTION) {
                states.add(AccessibleState.MULTISELECTABLE);
            }
            return states;
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LIST;
        }

        /**
         * Returns the Accessible child contained at the local coordinate
         * Point, if one exists.
         *
         * @return the Accessible at the specified location, if it exists
         */
        public Accessible getAccessibleAt(Point p) {
            int i = locationToIndex(p);
            if (i >= 0) {
                return new AccessibleJListChild(JList.this, i);
            } else {
                return null;
            }
        }

        /**
         * Returns the number of accessible children in the object.  If all
         * of the children of this object implement Accessible, than this
         * method should return the number of children of this object.
         *
         * @return the number of accessible children in the object.
         */
        public int getAccessibleChildrenCount() {
            return getModel().getSize();
        }

        /**
         * Return the nth Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            if (i >= getModel().getSize()) {
                return null;
            } else {
                return new AccessibleJListChild(JList.this, i);
            }
        }

        /**
         * Get the AccessibleSelection associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleSelection getAccessibleSelection() {
            return this;
        }


    // AccessibleSelection methods

        /**
         * Returns the number of items currently selected.
         * If no items are selected, the return value will be 0.
         *
         * @return the number of items currently selected.
         */
         public int getAccessibleSelectionCount() {
             return JList.this.getSelectedIndices().length;
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
             int len = getAccessibleSelectionCount();
             if (i < 0 || i >= len) {
                 return null;
             } else {
                 return getAccessibleChild(JList.this.getSelectedIndices()[i]);
             }
         }

        /**
         * Returns true if the current child of this object is selected.
         *
         * @param i the zero-based index of the child in this Accessible
         * object.
         * @see AccessibleContext#getAccessibleChild
         */
        public boolean isAccessibleChildSelected(int i) {
            return isSelectedIndex(i);
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
             JList.this.addSelectionInterval(i, i);
         }

        /**
         * Removes the specified selected item in the object from the object's
         * selection.  If the specified item isn't currently selected, this
         * method has no effect.
         *
         * @param i the zero-based index of selectable items
         */
         public void removeAccessibleSelection(int i) {
             JList.this.removeSelectionInterval(i, i);
         }

        /**
         * Clears the selection in the object, so that nothing in the
         * object is selected.
         */
         public void clearAccessibleSelection() {
             JList.this.clearSelection();
         }

        /**
         * Causes every selected item in the object to be selected
         * if the object supports multiple selections.
         */
         public void selectAllAccessibleSelection() {
             JList.this.addSelectionInterval(0, getAccessibleChildrenCount() -1);
         }


        protected class AccessibleJListChild extends AccessibleContext
                implements Accessible, AccessibleComponent {
            private JList     parent = null;
            private int       indexInParent;
            private Component component = null;
            private AccessibleContext accessibleContext = null;
            private ListModel listModel;
            private ListCellRenderer cellRenderer = null;

            public AccessibleJListChild(JList parent, int indexInParent) {
                this.parent = parent;
                this.setAccessibleParent(parent);
                this.indexInParent = indexInParent;
                if (parent != null) {
                    listModel = parent.getModel();
                    cellRenderer = parent.getCellRenderer();
                }
            }

            private Component getCurrentComponent() {
                return getComponentAtIndex(indexInParent);
            }

            private AccessibleContext getCurrentAccessibleContext() {
                Component c = getComponentAtIndex(indexInParent);
                if (c instanceof Accessible) {
                    return ((Accessible) c).getAccessibleContext();
                } else {
                    return null;
                }
            }

            private Component getComponentAtIndex(int index) {
                if (index < 0 || index >= listModel.getSize()) {
                    return null;
                }
                if ((parent != null)
                        && (listModel != null)
                        && cellRenderer != null) {
                    Object value = listModel.getElementAt(index);
                    boolean isSelected = parent.isSelectedIndex(index);
                    boolean isFocussed = parent.hasFocus()
                            && (index == parent.getLeadSelectionIndex());
                    return cellRenderer.getListCellRendererComponent(
                            parent,
                            value,
                            index,
                            isSelected,
                            isFocussed);
                } else {
                    return null;
                }
            }


            // Accessible Methods

            public AccessibleContext getAccessibleContext() {
                return this;
            }


            // AccessibleContext methods

            public String getAccessibleName() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleName();
                } else {
                    return null;
                }
            }

            public void setAccessibleName(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleName(s);
                }
            }

            public String getAccessibleDescription() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleDescription();
                } else {
                    return null;
                }
            }

            public void setAccessibleDescription(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleDescription(s);
                }
            }

            public AccessibleRole getAccessibleRole() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleRole();
                } else {
                    return null;
                }
            }

            public AccessibleStateSet getAccessibleStateSet() {
                AccessibleContext ac = getCurrentAccessibleContext();
                AccessibleStateSet s;
                if (ac != null) {
                    s = ac.getAccessibleStateSet();
                } else {
                    s = new AccessibleStateSet();
                }
                s = ac.getAccessibleStateSet();
                s.add(AccessibleState.SELECTABLE);
	        if (parent.hasFocus() 
		    && (indexInParent == parent.getLeadSelectionIndex())) {
                    s.add(AccessibleState.ACTIVE);
	        }
                if (parent.isSelectedIndex(indexInParent)) {
                    s.add(AccessibleState.SELECTED);
                }
                if (this.isShowing()) {
                    s.add(AccessibleState.SHOWING);
                } else if (s.contains(AccessibleState.SHOWING)) {
                    s.remove(AccessibleState.SHOWING);
                }
                if (this.isVisible()) {
                    s.add(AccessibleState.VISIBLE);
                } else if (s.contains(AccessibleState.VISIBLE)) {
                    s.remove(AccessibleState.VISIBLE);
                }
                s.add(AccessibleState.TRANSIENT); // cell-rendered
                return s;
            }

            public int getAccessibleIndexInParent() {
                return indexInParent;
            }

            public int getAccessibleChildrenCount() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleChildrenCount();
                } else {
                    return 0;
                }
            }

            public Accessible getAccessibleChild(int i) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    Accessible accessibleChild = ac.getAccessibleChild(i);
                    ac.setAccessibleParent(this);
                    return accessibleChild;
                } else {
                    return null;
                }
            }

            public Locale getLocale() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getLocale();
                } else {
                    return null;
                }
            }

            public void addPropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.addPropertyChangeListener(l);
                }
            }

            public void removePropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.removePropertyChangeListener(l);
                }
            }

            public AccessibleAction getAccessibleAction() {
                return getCurrentAccessibleContext().getAccessibleAction();
            }

            public AccessibleComponent getAccessibleComponent() {
                return this; // to override getBounds()
            }

            public AccessibleSelection getAccessibleSelection() {
                return getCurrentAccessibleContext().getAccessibleSelection();
            }

            public AccessibleText getAccessibleText() {
                return getCurrentAccessibleContext().getAccessibleText();
            }

            public AccessibleValue getAccessibleValue() {
                return getCurrentAccessibleContext().getAccessibleValue();
            }


            // AccessibleComponent methods

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
                int fi = parent.getFirstVisibleIndex();
                int li = parent.getLastVisibleIndex();
                // The UI incorrectly returns a -1 for the last
                // visible index if the list is smaller than the
                // viewport size.
                if (li == -1) {
                    li = parent.getModel().getSize() - 1;
                }
                return ((indexInParent >= fi)
                        && (indexInParent <= li));
            }

            public void setVisible(boolean b) {
            }

            public boolean isShowing() {
                return (parent.isShowing() && isVisible());
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
                if (parent != null) {
                    Point listLocation = parent.getLocationOnScreen();
                    Point componentLocation = parent.indexToLocation(indexInParent);
                    componentLocation.translate(listLocation.x, listLocation.y);
                    return componentLocation;
                } else {
                    return null;
                }
            }

            public Point getLocation() {
                if (parent != null) {
                    return parent.indexToLocation(indexInParent);
                } else {
                    return null;
                }
            }

            public void setLocation(Point p) {
                if ((parent != null)  && (parent.contains(p))) {
                    ensureIndexIsVisible(indexInParent);
                }
            }

            public Rectangle getBounds() {
                if (parent != null) {
                    return parent.getCellBounds(indexInParent,indexInParent);
                } else {
                    return null;
                }
            }

            public void setBounds(Rectangle r) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setBounds(r);
                }
            }

            public Dimension getSize() {
                Rectangle cellBounds = this.getBounds();
                if (cellBounds != null) {
                    return cellBounds.getSize();
                } else {
                    return null;
                }
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
        } // inner class AccessibleJListChild
    } // inner class AccessibleJList
}

