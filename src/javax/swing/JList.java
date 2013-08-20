/*
 * @(#)JList.java	1.112 04/05/05
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
import javax.swing.text.Position;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.Serializable;


/**
 * A component that allows the user to select one or more objects from a
 * list.  A separate model, <code>ListModel</code>, represents the contents
 * of the list.  It's easy to display an array or vector of objects, using
 * a <code>JList</code> constructor that builds a <code>ListModel</code> 
 * instance for you:
 * <pre>
 * // Create a JList that displays the strings in data[]
 *
 * String[] data = {"one", "two", "three", "four"};
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
 * <code>JList</code> doesn't support scrolling directly. 
 * To create a scrolling
 * list you make the <code>JList</code> the viewport view of a
 * <code>JScrollPane</code>.  For example:
 * <pre>
 * JScrollPane scrollPane = new JScrollPane(dataList);
 * // Or in two steps:
 * JScrollPane scrollPane = new JScrollPane();
 * scrollPane.getViewport().setView(dataList);
 * </pre>
 * <p>
 * By default the <code>JList</code> selection model allows any
 * combination of items to be selected at a time, using the constant
 * <code>MULTIPLE_INTERVAL_SELECTION</code>.  
 * The selection state is actually managed
 * by a separate delegate object, an instance of
 * <code>ListSelectionModel</code>.
 * However <code>JList</code> provides convenient properties for
 * managing the selection.
 * <pre>
 * String[] data = {"one", "two", "three", "four"};
 * JList dataList = new JList(data);
 *
 * dataList.setSelectedIndex(1);  // select "two"
 * dataList.getSelectedValue();   // returns "two"
 * </pre>
 * <p>
 * The contents of a <code>JList</code> can be dynamic,
 * in other words, the list elements can
 * change value and the size of the list can change after the
 * <code>JList</code> has
 * been created.  The <code>JList</code> observes changes in its model with a
 * <code>swing.event.ListDataListener</code> implementation.  A correct 
 * implementation of <code>ListModel</code> notifies
 * it's listeners each time a change occurs.  The changes are
 * characterized by a <code>swing.event.ListDataEvent</code>, which identifies
 * the range of list indices that have been modified, added, or removed.
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
 * <a name="prototype_example">
 * ListModel bigData = new AbstractListModel() {
 *     public int getSize() { return Short.MAX_VALUE; }
 *     public Object getElementAt(int index) { return "Index " + index; }
 * };
 *
 * JList bigDataList = new JList(bigData);
 *
 * // We don't want the JList implementation to compute the width
 * // or height of all of the list cells, so we give it a string
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
 * <a name="cellrenderer_example">
 * class MyCellRenderer extends JLabel implements ListCellRenderer {
 *     final static ImageIcon longIcon = new ImageIcon("long.gif");
 *     final static ImageIcon shortIcon = new ImageIcon("short.gif");
 *
 *     // This is the only method defined by ListCellRenderer.
 *     // We just reconfigure the JLabel each time we're called.
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
 *         setOpaque(true);
 *         return this;
 *     }
 * }
 *
 * String[] data = {"one", "two", "three", "four"};
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
 * Note that in this example the <code>dataList</code> is <code>final</code>
 * because it's referred to by the anonymous <code>MouseListener</code> class.
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
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/list.html">How to Use Lists</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
 * Also see the article <a href="http://java.sun.com/products/jfc/tsc/tech_topics/jlist_1/jlist.html">Advanced JList Programming</a>
 * in <a href="http://java.sun.com/products/jfc/tsc"><em>The Swing Connection</em></a>.
 * <p>
 * @see ListModel
 * @see AbstractListModel
 * @see DefaultListModel
 * @see ListSelectionModel
 * @see DefaultListSelectionModel
 * @see ListCellRenderer
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: A component which allows for the selection of one or more objects from a list.
 *
 * @version 1.112 05/05/04
 * @author Hans Muller
 */
public class JList extends JComponent implements Scrollable, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ListUI";

    /** 
     * Indicates the default layout: one column of cells.
     * @see #setLayoutOrientation
     * @since 1.4
     */
    public static final int VERTICAL = 0;

    /** 
     * Indicates "newspaper style" layout with the cells flowing vertically
     * then horizontally.
     * @see #setLayoutOrientation
     * @since 1.4
     */
    public static final int VERTICAL_WRAP = 1;

    /** 
     * Indicates "newspaper style" with the cells flowing horizontally
     * then vertically.
     * @see #setLayoutOrientation
     * @since 1.4
     */
    public static final int HORIZONTAL_WRAP = 2;

    private int fixedCellWidth = -1;
    private int fixedCellHeight = -1;
    private int horizontalScrollIncrement = -1;
    private Object prototypeCellValue;
    private int visibleRowCount = 8;
    private Color selectionForeground;
    private Color selectionBackground;
    private boolean dragEnabled;

    private ListSelectionModel selectionModel;
    private ListModel dataModel;
    private ListCellRenderer cellRenderer;
    private ListSelectionListener selectionListener;

    /**
     * How to layout the cells, defaults to <code>VERTICAL</code>.
     */
    private int layoutOrientation;

    /**
     * Constructs a <code>JList</code> that displays the elements in the
     * specified, non-<code>null</code> model. 
     * All <code>JList</code> constructors delegate to this one.
     *
     * @param dataModel   the data model for this list
     * @exception IllegalArgumentException   if <code>dataModel</code>
     *						is <code>null</code>
     */
    public JList(ListModel dataModel)
    {
        if (dataModel == null) {
            throw new IllegalArgumentException("dataModel must be non null");
        }

        // Register with the ToolTipManager so that tooltips from the
        // renderer show through.
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        toolTipManager.registerComponent(this);
        
        layoutOrientation = VERTICAL;

        this.dataModel = dataModel;
        selectionModel = createSelectionModel();
        setAutoscrolls(true);
        setOpaque(true);
        updateUI();
    }


    /**
     * Constructs a <code>JList</code> that displays the elements in
     * the specified array.  This constructor just delegates to the
     * <code>ListModel</code> constructor.
     *
     * @param  listData  the array of Objects to be loaded into the data model
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
     * Constructs a <code>JList</code> that displays the elements in
     * the specified <code>Vector</code>.  This constructor just
     * delegates to the <code>ListModel</code> constructor.
     *
     * @param  listData  the <code>Vector</code> to be loaded into the
     *		data model
     */
    public JList(final Vector<?> listData) {
        this (
            new AbstractListModel() {
                public int getSize() { return listData.size(); }
                public Object getElementAt(int i) { return listData.elementAt(i); }
            }
        );
    }


    /**
     * Constructs a <code>JList</code> with an empty model.
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
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the <code>ListUI</code> object that renders this component
     */
    public ListUI getUI() {
        return (ListUI)ui;
    }


    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui  the <code>ListUI</code> L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *        bound: true
     *       hidden: true
     *    attribute: visualUpdate true
     *  description: The UI object that implements the Component's LookAndFeel. 
     */
    public void setUI(ListUI ui) {
        super.setUI(ui);
    }


    /**
     * Resets the UI property with the value from the current look and feel.
     *
     * @see UIManager#getUI
     */
    public void updateUI() {
        setUI((ListUI)UIManager.getUI(this));
        invalidate();
    }


    /**
     * Returns the suffix used to construct the name of the look and feel 
     * (L&F) class used to render this component.
     *
     * @return the string "ListUI"
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
             * been done here.  So we temporarily set the one "inherited"
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
     * as all other list items.
     *
     * @return the value of the <code>prototypeCellValue</code> property
     * @see #setPrototypeCellValue
     */
    public Object getPrototypeCellValue() {
        return prototypeCellValue;
    }

    /**
     * Computes the <code>fixedCellWidth</code> and 
     * <code>fixedCellHeight</code> properties
     * by configuring the <code>cellRenderer</code> to index equals
     * zero for the specified value and then computing the renderer
     * component's preferred size.  These properties are useful when the
     * list is too long to allow <code>JList</code> to compute the
     * width/height of each cell and there is a single cell value that is
     * known to occupy as much space as any of the others.
     * <p>
     * Note that we do set the <code>fixedCellWidth</code> and 
     * <code>fixedCellHeight</code> properties here but only a 
     * <code>prototypeCellValue PropertyChangeEvent</code> is fired.
     * <p>
     * To see an example which sets this property, 
     * see the <a href = #prototype_example>class description</a> above.
     * <p>
     * The default value of this property is <code>null</code>.
     * <p>
     * This is a JavaBeans bound property.  
     *
     * @param prototypeCellValue  the value on which to base
     *				<code>fixedCellWidth</code> and
     * 				<code>fixedCellHeight</code>
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
     * the <code>fixedCellWidth</code> property, rather than that calculated
     * from the list elements.
     *
     * @return the fixed cell width
     * @see #setFixedCellWidth
     */
    public int getFixedCellWidth() {
        return fixedCellWidth;
    }

    /**
     * Sets the width of every cell in the list.  If <code>width</code> is -1,
     * cell widths are computed by applying <code>getPreferredSize</code>
     * to the <code>cellRenderer</code> component for each list element.
     * <p>
     * The default value of this property is -1.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param width   the width, in pixels, for all cells in this list
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
     * Returns the fixed cell height value -- the value specified by setting
     * the <code>fixedCellHeight</code> property,
     * rather than that calculated from the list elements.
     * 
     * @return the fixed cell height, in pixels 
     * @see #setFixedCellHeight
     */
    public int getFixedCellHeight() {
        return fixedCellHeight;
    }

    /**
     * Sets the height of every cell in the list.  If <code>height</code>
     * is -1, cell
     * heights are computed by applying <code>getPreferredSize</code>
     * to the <code>cellRenderer</code> component for each list element.
     * <p>
     * The default value of this property is -1.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param height an integer giving the height, in pixels, for all cells 
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
     * @return the <code>ListCellRenderer</code>
     * @see #setCellRenderer
     */
    public ListCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    /**
     * Sets the delegate that's used to paint each cell in the list.  If
     * <code>prototypeCellValue</code> was set then the 
     * <code>fixedCellWidth</code> and <code>fixedCellHeight</code>
     * properties are set as well.  Only one <code>PropertyChangeEvent</code>
     * is generated however - for the <code>cellRenderer</code> property.
     * <p>
     * The default value of this property is provided by the ListUI
     * delegate, i.e. by the look and feel implementation.
     * <p>
     * To see an example which sets the cell renderer, 
     * see the <a href = #cellrenderer_example>class description</a> above.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param cellRenderer the <code>ListCellRenderer</code>
     * 				that paints list cells
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
     * Returns the selection foreground color.
     *
     * @return the <code>Color</code> object for the foreground property
     * @see #setSelectionForeground
     * @see #setSelectionBackground
     */
    public Color getSelectionForeground() {
        return selectionForeground;
    }


    /**
     * Sets the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionForeground  the <code>Color</code> to use in the foreground
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
     * @return the <code>Color</code> used for the background of
     * selected list items
     * @see #setSelectionBackground
     * @see #setSelectionForeground
     */
    public Color getSelectionBackground() {
        return selectionBackground;
    }


    /**
     * Sets the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionBackground  the <code>Color</code> to use for the 
     *                             background of selected cells
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
     * Returns the preferred number of visible rows.
     *
     * @return an integer indicating the preferred number of rows to display
     *         without using a scroll bar
     * @see #setVisibleRowCount
     */
    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    /**
     * Sets the preferred number of rows in the list that can be displayed
     * without a scrollbar, as determined by the nearest
     * <code>JViewport</code> ancestor, if any.
     * The value of this property only affects the value of
     * the <code>JList</code>'s <code>preferredScrollableViewportSize</code>.
     * <p>
     * The default value of this property is 8.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param visibleRowCount  an integer specifying the preferred number of
     *                         visible rows
     * @see #getVisibleRowCount
     * @see JComponent#getVisibleRect
     * @see JViewport
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: The preferred number of cells that can be displayed without a scroll bar.
     */
    public void setVisibleRowCount(int visibleRowCount) {
        int oldValue = this.visibleRowCount;
        this.visibleRowCount = Math.max(0, visibleRowCount);
        firePropertyChange("visibleRowCount", oldValue, visibleRowCount);
    }


    /**
     * Returns <code>JList.VERTICAL</code> if the layout is a single
     * column of cells, or <code>JList.VERTICAL_WRAP</code> if the layout
     * is "newspaper style" with the content flowing vertically then
     * horizontally or <code>JList.HORIZONTAL_WRAP</code> if the layout is
     * "newspaper style" with the content flowing horizontally then
     * vertically.
     * 
     * @return the value of the layoutOrientation property
     * @see #setLayoutOrientation
     * @since 1.4
     */
    public int getLayoutOrientation() {
	return layoutOrientation;
    }


    /**
     * Defines the way list cells are layed out. Consider a <code>JList</code>
     * with four cells, this can be layed out in one of the following ways:
     * <pre>
     *   0
     *   1
     *   2
     *   3
     * </pre>
     * <pre>
     *   0  1
     *   2  3
     * </pre>
     * <pre>
     *   0  2
     *   1  3
     * </pre>
     * <p>
     * These correspond to the following values:
     *
     * <table border="1" 
     *  summary="Describes layouts VERTICAL, HORIZONTAL_WRAP, and VERTICAL_WRAP">
     *   <tr><th><p align="left">Value</p></th><th><p align="left">Description</p></th></tr>
     *   <tr><td><code>JList.VERTICAL</code>
     *       <td>The cells should be layed out vertically in one column.
     *   <tr><td><code>JList.HORIZONTAL_WRAP</code>
     *       <td>The cells should be layed out horizontally, wrapping to
     *           a new row as necessary.  The number
     *           of rows to use will either be defined by
     *           <code>getVisibleRowCount</code> if > 0, otherwise the
     *           number of rows will be determined by the width of the 
     *           <code>JList</code>.
     *   <tr><td><code>JList.VERTICAL_WRAP</code>
     *       <td>The cells should be layed out vertically, wrapping to a
     *           new column as necessary.  The number
     *           of rows to use will either be defined by
     *           <code>getVisibleRowCount</code> if > 0, otherwise the
     *           number of rows will be determined by the height of the 
     *           <code>JList</code>.
     *  </table>
     * The default value of this property is <code>JList.VERTICAL</code>.
     * <p>
     * This will throw an <code>IllegalArgumentException</code> if 
     * <code>layoutOrientation</code> is not one of
     * <code>JList.HORIZONTAL_WRAP</code> or <code>JList.VERTICAL</code> or
     * <code>JList.VERTICAL_WRAP</code>
     *
     * @param layoutOrientation New orientation, one of
     *        <code>JList.HORIZONTAL_WRAP</code>,  <code>JList.VERTICAL</code>
     *        or <code>JList.VERTICAL_WRAP</code>.
     * @see #getLayoutOrientation
     * @see #setVisibleRowCount
     * @see #getScrollableTracksViewportHeight
     * @since 1.4
     * @beaninfo
     *       bound: true
     *   attribute: visualUpdate true
     * description: Defines the way list cells are layed out.
     *        enum: VERTICAL JList.VERTICAL 
     *              HORIZONTAL_WRAP JList.HORIZONTAL_WRAP
     *              VERTICAL_WRAP JList.VERTICAL_WRAP
     */
    public void setLayoutOrientation(int layoutOrientation) {
	int oldValue = this.layoutOrientation;
	switch (layoutOrientation) {
	case VERTICAL:
	case VERTICAL_WRAP:
        case HORIZONTAL_WRAP:
	    this.layoutOrientation = layoutOrientation;
	    firePropertyChange("layoutOrientation", oldValue, layoutOrientation);
	    break;
	default:
            throw new IllegalArgumentException("layoutOrientation must be one of: VERTICAL, HORIZONTAL_WRAP or VERTICAL_WRAP");
	}
    }


    /**
     * Returns the index of the first visible cell.  The cell considered
     * to be "first" depends on the list's <code>componentOrientation</code>
     * property.  If the orientation is horizontal left-to-right, then
     * the first visible cell is in the list's upper-left corner.  If
     * the orientation is horizontal right-to-left, then the first
     * visible cell is in the list's upper-right corner.  If nothing is
     * visible or the list is empty, a -1 is returned.  Note that the returned
     * cell may only be partially visible.
     *
     * @return the index of the first visible cell
     * @see #getLastVisibleIndex
     * @see JComponent#getVisibleRect
     */
    public int getFirstVisibleIndex() {
	Rectangle r = getVisibleRect();
        int first;
        if (this.getComponentOrientation().isLeftToRight()) {
            first = locationToIndex(r.getLocation());
        } else {
            first = locationToIndex(new Point((r.x + r.width) - 1, r.y));
        }
	if (first != -1) {
	    Rectangle bounds = getCellBounds(first, first);
	    if (bounds != null) {
                SwingUtilities.computeIntersection(r.x, r.y, r.width, r.height, bounds);
                if (bounds.width == 0 || bounds.height == 0) {		
		    first = -1;
		}
	    }
	}
	return first;
    }


    /**
     * Returns the index of the last visible cell. The cell considered
     * to be "last" depends on the list's <code>componentOrientation</code>
     * property.  If the orientation is horizontal left-to-right, then
     * the last visible cell is in the JList's lower-right corner.  If
     * the orientation is horizontal right-to-left, then the last visible
     * cell is in the JList's lower-left corner.  If nothing is visible
     * or the list is empty, a -1 is returned.  Note that the returned
     * cell may only be partially visible.
     *
     * @return the index of the last visible cell
     * @see #getFirstVisibleIndex
     * @see JComponent#getVisibleRect
     */
    public int getLastVisibleIndex() {
        boolean leftToRight = this.getComponentOrientation().isLeftToRight();
        Rectangle r = getVisibleRect();
        Point lastPoint;
        if (leftToRight) {
            lastPoint = new Point((r.x + r.width) - 1, (r.y + r.height) - 1);
        } else {
            lastPoint = new Point(r.x, (r.y + r.height) - 1);
        }
        int location = locationToIndex(lastPoint);

        if (location != -1) {
            Rectangle bounds = getCellBounds(location, location);

            if (bounds != null) {
                SwingUtilities.computeIntersection(r.x, r.y, r.width, r.height, bounds);
                if (bounds.width == 0 || bounds.height == 0) {
		    // Try the lower left corner, and then go across checking
		    // each cell.
		    Point visibleLL = new Point(r.x, lastPoint.y);
		    int last;
		    int llIndex = -1;
		    int lrIndex = location;
		    location = -1;

		    do {
			last = llIndex;
			llIndex = locationToIndex(visibleLL);

			if (llIndex != -1) {
			    bounds = getCellBounds(llIndex, llIndex);
			    if (llIndex != lrIndex && bounds != null &&
				bounds.contains(visibleLL)) {
				location = llIndex;
				visibleLL.x = bounds.x + bounds.width + 1;
				if (visibleLL.x >= lastPoint.x) {
                                // Past visible region, bail.
				    last = llIndex;
				}
			    }
			    else {
				last = llIndex;
			    }
			}
		    } while (llIndex != -1 && last != llIndex);
		}
            }
        }
        return location;
    }


    /**
     * Scrolls the viewport to make the specified cell completely visible.
     * Note, for this method to work, the <code>JList</code> must be
     * displayed within a <code>JViewport</code>.
     *
     * @param index  the index of the cell to make visible
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
     * Sets the <code>dragEnabled</code> property,
     * which must be <code>true</code> to enable
     * automatic drag handling (the first part of drag and drop)
     * on this component.
     * The <code>transferHandler</code> property needs to be set
     * to a non-<code>null</code> value for the drag to do
     * anything.  The default value of the <code>dragEnabled</code>
     * property
     * is <code>false</code>.
     *
     * <p>
     *
     * When automatic drag handling is enabled,
     * most look and feels begin a drag-and-drop operation
     * whenever the user presses the mouse button over a selection
     * and then moves the mouse a few pixels. 
     * Setting this property to <code>true</code>
     * can therefore have a subtle effect on
     * how selections behave.
     *
     * <p>
     *
     * Some look and feels might not support automatic drag and drop;
     * they will ignore this property.  You can work around such
     * look and feels by modifying the component
     * to directly call the <code>exportAsDrag</code> method of a
     * <code>TransferHandler</code>.
     *
     * @param b the value to set the <code>dragEnabled</code> property to
     * @exception HeadlessException if
     *            <code>b</code> is <code>true</code> and
     *            <code>GraphicsEnvironment.isHeadless()</code>
     *            returns <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     * @see #getDragEnabled
     * @see #setTransferHandler
     * @see TransferHandler
     * @since 1.4
     *
     * @beaninfo
     *  description: determines whether automatic drag handling is enabled
     *        bound: false
     */
    public void setDragEnabled(boolean b) {
        if (b && GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
	dragEnabled = b;
    }

    /**
     * Gets the <code>dragEnabled</code> property.
     *
     * @return the value of the <code>dragEnabled</code> property
     * @see #setDragEnabled
     * @since 1.4
     */
    public boolean getDragEnabled() {
	return dragEnabled;
    }

    /**
     * Returns the next list element that starts with 
     * a prefix.
     *
     * @param prefix the string to test for a match
     * @param startIndex the index for starting the search
     * @param bias the search direction, either 
     * Position.Bias.Forward or Position.Bias.Backward.
     * @return the index of the next list element that
     * starts with the prefix; otherwise -1
     * @exception IllegalArgumentException if prefix is null
     * or startIndex is out of bounds
     * @since 1.4
     */
    public int getNextMatch(String prefix, int startIndex, Position.Bias bias) {
	ListModel model = getModel();
	int max = model.getSize();
	if (prefix == null) {
	    throw new IllegalArgumentException();
	}
	if (startIndex < 0 || startIndex >= max) {
	    throw new IllegalArgumentException();
	}
	prefix = prefix.toUpperCase();

	// start search from the next element after the selected element
	int increment = (bias == Position.Bias.Forward) ? 1 : -1;
	int index = startIndex;
	do {
	    Object o = model.getElementAt(index);
	    
	    if (o != null) {
		String string;
		
		if (o instanceof String) {
		    string = ((String)o).toUpperCase();
		}
		else {
		    string = o.toString();
		    if (string != null) {
			string = string.toUpperCase();
		    }
		}
		
		if (string != null && string.startsWith(prefix)) {
		    return index;
		}
	    }
	    index = (index + increment + max) % max;
	} while (index != startIndex);
	return -1;
    }

    /**
     * Overrides <code>JComponent</code>'s <code>getToolTipText</code>
     * method in order to allow the renderer's tips to be used
     * if it has text set.
     * <p>
     * <bold>Note:</bold> For <code>JList</code> to properly display
     * tooltips of its renderers
     * <code>JList</code> must be a registered component with the
     * <code>ToolTipManager</code>.
     * This is done automatically in the constructor,
     * but if at a later point <code>JList</code> is told
     * <code>setToolTipText(null)</code> it will unregister the list
     * component, and no tips from renderers will display anymore.
     *
     * @see JComponent#getToolTipText
     */
    public String getToolTipText(MouseEvent event) {
        if(event != null) {
            Point p = event.getPoint();
            int index = locationToIndex(p);
            ListCellRenderer r = getCellRenderer();
            Rectangle cellBounds;

            if (index != -1 && r != null && (cellBounds =
                               getCellBounds(index, index)) != null &&
                               cellBounds.contains(p.x, p.y)) {
                ListSelectionModel lsm = getSelectionModel();
                Component rComponent = r.getListCellRendererComponent(
                           this, getModel().getElementAt(index), index,
                           lsm.isSelectedIndex(index),
                           (hasFocus() && (lsm.getLeadSelectionIndex() ==
                                           index)));

                if(rComponent instanceof JComponent) {
                    MouseEvent      newEvent;

                    p.translate(-cellBounds.x, -cellBounds.y);
                    newEvent = new MouseEvent(rComponent, event.getID(),
                                              event.getWhen(),
                                              event.getModifiers(),
                                              p.x, p.y, event.getClickCount(),
                                              event.isPopupTrigger());

                    String tip = ((JComponent)rComponent).getToolTipText(
                                              newEvent);

                    if (tip != null) {
                        return tip;
                    }
                }
            }
        }
        return super.getToolTipText();
    }

    /**
     * --- ListUI Delegations ---
     */


    /**
     * Convert a point in <code>JList</code> coordinates to the closest index
     * of the cell at that location. To determine if the cell actually
     * contains the specified location use a combination of this method and
     * <code>getCellBounds</code>.  Returns -1 if the model is empty.
     *
     * @param location the coordinates of the cell, relative to
     *			<code>JList</code>
     * @return an integer -- the index of the cell at the given location, or -1.
     */
    public int locationToIndex(Point location) {
        ListUI ui = getUI();
        return (ui != null) ? ui.locationToIndex(this, location) : -1;
    }


    /**
     * Returns the origin of the specified item in <code>JList</code>
     * coordinates. Returns <code>null</code> if <code>index</code> isn't valid.
     *
     * @param index the index of the <code>JList</code> cell
     * @return the origin of the index'th cell
     */
    public Point indexToLocation(int index) {
        ListUI ui = getUI();
        return (ui != null) ? ui.indexToLocation(this, index) : null;
    }


    /**
     * Returns the bounds of the specified range of items in <code>JList</code>
     * coordinates. Returns <code>null</code> if index isn't valid.
     *
     * @param index0  the index of the first <code>JList</code> cell in the range
     * @param index1  the index of the last <code>JList</code> cell in the range
     * @return the bounds of the indexed cells in pixels
     */
    public Rectangle getCellBounds(int index0, int index1) {
        ListUI ui = getUI();
        return (ui != null) ? ui.getCellBounds(this, index0, index1) : null;
    }


    /**
     * --- ListModel Support ---
     */


    /**
     * Returns the data model that holds the list of items displayed
     * by the <code>JList</code> component.
     *
     * @return the <code>ListModel</code> that provides the displayed
     *				list of items
     * @see #setModel
     */
    public ListModel getModel() {
        return dataModel;
    }

    /**
     * Sets the model that represents the contents or "value" of the
     * list and clears the list selection after notifying
     * <code>PropertyChangeListeners</code>.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param model  the <code>ListModel</code> that provides the
     *						list of items for display
     * @exception IllegalArgumentException  if <code>model</code> is 
     *						<code>null</code>
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
     * Constructs a <code>ListModel</code> from an array of objects and then
     * applies <code>setModel</code> to it.
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
     * Constructs a <code>ListModel</code> from a <code>Vector</code> and then
     * applies <code>setModel</code> to it.
     *
     * @param listData a <code>Vector</code> containing the items to
     *						display in the list
     * @see #setModel
     */
    public void setListData(final Vector<?> listData) {
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
     * Returns an instance of <code>DefaultListSelectionModel</code>.  This
     * method is used by the constructor to initialize the
     * <code>selectionModel</code> property.
     *
     * @return the <code>ListSelectionModel</code> used by this
     *					<code>JList</code>.
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
     * @return the <code>ListSelectionModel</code> that implements
     *					list selections
     * @see #setSelectionModel
     * @see ListSelectionModel
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }


    /**
     * Notifies <code>JList</code> <code>ListSelectionListener</code>s that
     * the selection model has changed.  It's used to forward
     * <code>ListSelectionEvents</code> from the <code>selectionModel</code>
     * to the <code>ListSelectionListener</code>s added directly to the
     * <code>JList</code>.
     * @param firstIndex   the first selected index
     * @param lastIndex	   the last selected index
     * @param isAdjusting  true if multiple changes are being made
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
     * Adds a listener to the list that's notified each time a change
     * to the selection occurs.  Listeners added directly to the
     * <code>JList</code>
     * will have their <code>ListSelectionEvent.getSource() == 
     * this JList</code>
     * (instead of the <code>ListSelectionModel</code>).
     *
     * @param listener the <code>ListSelectionListener</code> to add
     * @see #getSelectionModel
     * @see #getListSelectionListeners
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
     * Removes a listener from the list that's notified each time a
     * change to the selection occurs.
     *
     * @param listener the <code>ListSelectionListener</code> to remove
     * @see #addListSelectionListener
     * @see #getSelectionModel
     */
    public void removeListSelectionListener(ListSelectionListener listener) {
        listenerList.remove(ListSelectionListener.class, listener);
    }


    /**
     * Returns an array of all the <code>ListSelectionListener</code>s added
     * to this JList with addListSelectionListener().
     *
     * @return all of the <code>ListSelectionListener</code>s added or an empty
     *         array if no listeners have been added
     * @see #addListSelectionListener
     * @since 1.4
     */
    public ListSelectionListener[] getListSelectionListeners() {
        return (ListSelectionListener[])listenerList.getListeners(
                ListSelectionListener.class);
    }


    /**
     * Sets the <code>selectionModel</code> for the list to a
     * non-<code>null</code> <code>ListSelectionModel</code>
     * implementation. The selection model handles the task of making single
     * selections, selections of contiguous ranges, and non-contiguous
     * selections.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param selectionModel  the <code>ListSelectionModel</code> that
     *				implements the selections
     * @exception IllegalArgumentException   if <code>selectionModel</code>
     * 						is <code>null</code>
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
         * selectionModel, and add it to the new one, if necessary.
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
     * The following <code>selectionMode</code> values are allowed:
     * <ul>
     * <li> <code>ListSelectionModel.SINGLE_SELECTION</code>
     *   Only one list index can be selected at a time.  In this
     *   mode the <code>setSelectionInterval</code> and 
     *   <code>addSelectionInterval</code>
     *   methods are equivalent, and only the second index
     *   argument is used.
     * <li> <code>ListSelectionModel.SINGLE_INTERVAL_SELECTION</code>
     *   One contiguous index interval can be selected at a time.
     *   In this mode <code>setSelectionInterval</code> and 
     *   <code>addSelectionInterval</code>
     *   are equivalent.
     * <li> <code>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</code>
     *   In this mode, there's no restriction on what can be selected.
     *   This is the default.
     * </ul>
     *
     * @param selectionMode an integer specifying the type of selections 
     *                         that are permissible
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
     *
     * @return the value of the <code>selectionMode</code> property
     * @see #setSelectionMode
     */
    public int getSelectionMode() {
        return getSelectionModel().getSelectionMode();
    }


    /**
     * Returns the first index argument from the most recent 
     * <code>addSelectionModel</code> or <code>setSelectionInterval</code> call.
     * This is a convenience method that just delegates to the
     * <code>selectionModel</code>.
     *
     * @return the index that most recently anchored an interval selection
     * @see ListSelectionModel#getAnchorSelectionIndex
     * @see #addSelectionInterval
     * @see #setSelectionInterval
     * @see #addListSelectionListener
     */
    public int getAnchorSelectionIndex() {
        return getSelectionModel().getAnchorSelectionIndex();
    }


    /**
     * Returns the second index argument from the most recent
     * <code>addSelectionInterval</code> or <code>setSelectionInterval</code>
     * call.
     * This is a convenience method that just  delegates to the 
     * <code>selectionModel</code>.
     *
     * @return the index that most recently ended a interval selection
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
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     *
     * @return the smallest selected cell index
     * @see ListSelectionModel#getMinSelectionIndex
     * @see #addListSelectionListener
     */
    public int getMinSelectionIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }


    /**
     * Returns the largest selected cell index.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     *
     * @return the largest selected cell index
     * @see ListSelectionModel#getMaxSelectionIndex
     * @see #addListSelectionListener
     */
    public int getMaxSelectionIndex() {
        return getSelectionModel().getMaxSelectionIndex();
    }


    /**
     * Returns true if the specified index is selected.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     *
     * @param index index to be queried for selection state
     * @return true if the specified index is selected
     * @see ListSelectionModel#isSelectedIndex
     * @see #setSelectedIndex
     * @see #addListSelectionListener
     */
    public boolean isSelectedIndex(int index) {
        return getSelectionModel().isSelectedIndex(index);
    }


    /**
     * Returns true if nothing is selected.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     *
     * @return true if nothing is selected
     * @see ListSelectionModel#isSelectionEmpty
     * @see #clearSelection
     * @see #addListSelectionListener
     */
    public boolean isSelectionEmpty() {
        return getSelectionModel().isSelectionEmpty();
    }


    /**
     * Clears the selection - after calling this method 
     * <code>isSelectionEmpty</code> will return true.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     *
     * @see ListSelectionModel#clearSelection
     * @see #isSelectionEmpty
     * @see #addListSelectionListener
     */
    public void clearSelection() {
        getSelectionModel().clearSelection();
    }


    /**
     * Selects the specified interval.  Both the <code>anchor</code>
     *  and <code>lead</code> indices are included.  It's not
     * necessary for <code>anchor</code> to be less than <code>lead</code>.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     * The <code>DefaultListSelectionModel</code> implementation 
     * will do nothing if either <code>anchor</code> or
     * <code>lead</code> are -1.
     * If <code>anchor</code> or <code>lead</code> are less than -1,
     * <code>IndexOutOfBoundsException</code> is thrown.
     *
     * @param anchor the first index to select
     * @param lead the last index to select
     * @exception IndexOutOfBoundsException if either <code>anchor</code>
     *    or <code>lead</code> are less than -1
     * @see ListSelectionModel#setSelectionInterval
     * @see #addSelectionInterval
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     */
    public void setSelectionInterval(int anchor, int lead) {
        getSelectionModel().setSelectionInterval(anchor, lead);
    }


    /**
     * Sets the selection to be the union of the specified interval with current
     * selection.  Both the anchor and lead indices are
     * included.  It's not necessary for anchor to be less than lead.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.  The
     * <code>DefaultListSelectionModel</code> implementation 
     * will do nothing if either <code>anchor</code> or
     * <code>lead</code> are -1.
     * If <code>anchor</code> or <code>lead</code> are less than -1,
     * <code>IndexOutOfBoundsException</code> is thrown.
     *
     * @param anchor the first index to add to the selection
     * @param lead the last index to add to the selection
     * @see ListSelectionModel#addSelectionInterval
     * @see #setSelectionInterval
     * @see #removeSelectionInterval
     * @see #addListSelectionListener
     * @exception IndexOutOfBoundsException if either <code>anchor</code>
     *    or <code>lead</code> are less than -1
     */
    public void addSelectionInterval(int anchor, int lead) {
        getSelectionModel().addSelectionInterval(anchor, lead);
    }


    /**
     * Sets the selection to be the set difference of the specified interval
     * and the current selection.  Both the <code>index0</code> and
     * <code>index1</code> indices are removed.  It's not necessary for
     * <code>index0</code> to be less than <code>index1</code>.
     * This is a convenience method that just delegates to the 
     * <code>selectionModel</code>.
     * The <code>DefaultListSelectionModel</code> implementation 
     * will do nothing if either <code>index0</code> or
     * <code>index1</code> are -1.
     * If <code>index0</code> or <code>index1</code> are less than -1,
     * <code>IndexOutOfBoundsException</code> is thrown.
     *
     * @param index0 the first index to remove from the selection
     * @param index1 the last index to remove from the selection
     * @exception IndexOutOfBoundsException if either <code>index0</code>
     *    or <code>index1</code> are less than -1
     * @see ListSelectionModel#removeSelectionInterval
     * @see #setSelectionInterval
     * @see #addSelectionInterval
     * @see #addListSelectionListener
     */
    public void removeSelectionInterval(int index0, int index1) {
        getSelectionModel().removeSelectionInterval(index0, index1);
    }


    /**
     * Sets the data model's <code>isAdjusting</code> property to true,
     * so that a single event will be generated when all of the selection
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
     * Returns the value of the data model's <code>isAdjusting</code> property.
     * This value is true if multiple changes are being made.
     *
     * @return true if multiple selection-changes are occurring, as
     *         when the mouse is being dragged over the list
     * @see ListSelectionModel#getValueIsAdjusting
     */
    public boolean getValueIsAdjusting() {
        return getSelectionModel().getValueIsAdjusting();
    }


    /**
     * Returns an array of all of the selected indices in increasing
     * order.
     *
     * @return all of the selected indices, in increasing order
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
     * Selects a single cell.
     *
     * @param index the index of the one cell to select
     * @see ListSelectionModel#setSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     * @beaninfo
     * description: The index of the selected cell.
     */
    public void setSelectedIndex(int index) {
	if (index >= getModel().getSize()) {
	    return;
	}
        getSelectionModel().setSelectionInterval(index, index);
    }


    /**
     * Selects a set of cells.
     *
     * @param indices an array of the indices of the cells to select
     * @see ListSelectionModel#addSelectionInterval
     * @see #isSelectedIndex
     * @see #addListSelectionListener
     */
    public void setSelectedIndices(int[] indices) {
        ListSelectionModel sm = getSelectionModel();
        sm.clearSelection();
	int size = getModel().getSize();
        for(int i = 0; i < indices.length; i++) {
	    if (indices[i] < size) {
		sm.addSelectionInterval(indices[i], indices[i]);
	    }
        }
    }


    /**
     * Returns an array of the values for the selected cells.
     * The returned values are sorted in increasing index order.
     *
     * @return the selected values or an empty list if
     *    nothing is selected
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
     * Returns the first selected index; returns -1 if there is no
     * selected item.
     *
     * @return the value of <code>getMinSelectionIndex</code>
     * @see #getMinSelectionIndex
     * @see #addListSelectionListener
     */
    public int getSelectedIndex() {
        return getMinSelectionIndex();
    }


    /**
     * Returns the first selected value, or <code>null</code> if the 
     * selection is empty.
     *
     * @return the first selected value
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
     * @param anObject      the object to select     
     * @param shouldScroll  true if the list should scroll to display
     *                      the selected object, if one exists; otherwise false
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

    private void checkScrollableParameters(Rectangle visibleRect, int orientation) {
	if (visibleRect == null) {
	    throw new IllegalArgumentException("visibleRect must be non-null");
	}
        switch (orientation) {
        case SwingConstants.VERTICAL:
        case SwingConstants.HORIZONTAL:
            break;
        default:
            throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
        }
    }


    /**
     * Computes the size of the viewport needed to display 
     * <code>visibleRowCount</code>
     * rows.  This is trivial if 
     * <code>fixedCellWidth</code> and <code>fixedCellHeight</code>
     * were specified.  Note that they can be specified implicitly with
     * the <code>prototypeCellValue</code> property.  
     * If <code>fixedCellWidth</code> wasn't specified,
     * it's computed by finding the widest list element.  
     * If <code>fixedCellHeight</code>
     * wasn't specified then we resort to heuristics:
     * <ul>
     * <li>
     * If the model isn't empty we just multiply the height of the first row
     * by <code>visibleRowCount</code>.
     * <li>
     * If the model is empty, (<code>JList.getModel().getSize() == 0</code>),
     * then we just allocate 16 pixels per visible row, and 256 pixels
     * for the width (unless <code>fixedCellWidth</code> was set),
     * and hope for the best.
     * </ul>
     * If the layout orientation is not <code>VERTICAL</code>, than this will
     * return the value from <code>getPreferredSize</code>. The current
     * <code>ListUI</code> is expected to override
     * <code>getPreferredSize</code> to return an appropriate value.
     *
     * @return a dimension containing the size of the viewport needed
     *		to display <code>visibleRowCount</code> rows
     * @see #getPreferredScrollableViewportSize
     * @see #setPrototypeCellValue
     */
    public Dimension getPreferredScrollableViewportSize()
    {
        if (getLayoutOrientation() != VERTICAL) {
            return getPreferredSize();
        }
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
            int height;
            Rectangle r = getCellBounds(0, 0);
            if (r != null) {
                height = (visibleRowCount * r.height) + dy;
            }
            else {
                // Will only happen if UI null, shouldn't matter what we return
                height = 1;
            }
            return new Dimension(width, height);
        }
        else {
            fixedCellWidth = (fixedCellWidth > 0) ? fixedCellWidth : 256;
            fixedCellHeight = (fixedCellHeight > 0) ? fixedCellHeight : 16;
            return new Dimension(fixedCellWidth, fixedCellHeight * visibleRowCount);
        }
    }


    /**
     * Returns the distance to scroll to expose the next or previous
     * row (for vertical scrolling) or column (for horizontal scrolling).
     * <p>
     * For horizontal scrolling if the list is layed out vertically
     * (the orientation is <code>VERTICAL</code>) than the lists font size
     * or 1 is returned if the font is <code>null</code> is used.
     * <p>
     * Note that the value of <code>visibleRect</code> must be the equal to 
     * <code>this.getVisibleRect()</code>.
     *
     * @param visibleRect  the visible rectangle
     * @param orientation  HORIZONTAL or VERTICAL  
     * @param direction    if <= 0, then scroll UP; if > 0, then scroll DOWN
     * @return the distance, in pixels, to scroll to expose the
     *			next or previous unit
     * @see Scrollable#getScrollableUnitIncrement
     * @throws IllegalArgumentException  if visibleRect is <code>null<code>, or
     *         orientation isn't one of SwingConstants.VERTICAL,
     *         SwingConstants.HORIZONTAL.
     * 
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
	checkScrollableParameters(visibleRect, orientation);

        if (orientation == SwingConstants.VERTICAL) {
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
                     * height of the previous row or 0 if the first row
                     * is the top row of the list.
                     */
                    else if (r.y == visibleRect.y) {
			Point loc = r.getLocation();
			loc.y--;
			int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);

                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                    /* The first row is partially visible, return the
                     * height of hidden part.
                     */
                    else {
                        return visibleRect.y - r.y;
                    }
                }
            }
        } else if (orientation == SwingConstants.HORIZONTAL &&
                           getLayoutOrientation() != JList.VERTICAL) {
	    int index = locationToIndex(visibleRect.getLocation());

            if (index != -1) {
                Rectangle cellBounds = getCellBounds(index, index);

                if (cellBounds != null) {
                    if (cellBounds.x != visibleRect.x) {
                        if (direction < 0) {
                            return Math.abs(cellBounds.x - visibleRect.x);
                        }
                        return cellBounds.width + cellBounds.x - visibleRect.x;
                    }
                    return cellBounds.width;
                }
            }
        }
	Font f = getFont();
	return (f != null) ? f.getSize() : 1;
    }


    /**
     * Returns the distance to scroll to expose the next or previous block.
     * For vertical scrolling we are using the follows rules:
     * <ul>
     * <li>if scrolling down (<code>direction</code> is greater than 0),
     * the last visible element should become the first completely
     * visible element
     * <li>if scrolling up, the first visible element should become the last
     * completely visible element
     * <li>visibleRect.height if the list is empty
     * </ul>
     * <p>
     * For horizontal scrolling if the list is layed out horizontally
     * (the orientation is <code>VERTICAL_WRAP</code> or
     *  <code>HORIZONTAL_WRAP</code>):
     * <ul>
     * </ul>
     * <li>if scrolling right (<code>direction</code> is greater than 0),
     * the last visible element should become the first completely
     * visible element
     * <li>if scrolling left, the first visible element should become the last
     * completely visible element
     * <li>visibleRect.width if the list is empty
     * <p>
     * Return visibleRect.width if the list is layed out vertically.
     * <p>
     * Note that the value of <code>visibleRect</code> must be the equal to 
     * <code>this.getVisibleRect()</code>.
     *
     * @param visibleRect  the visible rectangle
     * @param orientation  HORIZONTAL or VERTICAL  
     * @param direction    if <= 0, then scroll UP; if > 0, then scroll DOWN
     * @return the block increment amount.
     * @see Scrollable#getScrollableUnitIncrement
     * @throws IllegalArgumentException   if visibleRect is <code>null</code>, or
     *            orientation isn't one of SwingConstants.VERTICAL,
     *            SwingConstants.HORIZONTAL.
     * 
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
	checkScrollableParameters(visibleRect, orientation);
        if (orientation == SwingConstants.VERTICAL) {
            int inc = visibleRect.height;
            /* Scroll Down */
            if (direction > 0) {
                // last cell is the lowest left cell
                int last = locationToIndex(new Point(visibleRect.x, visibleRect.y+visibleRect.height-1));
                if (last != -1) {
		    Rectangle lastRect = getCellBounds(last,last);
		    if (lastRect != null) {
			inc = lastRect.y - visibleRect.y;
			if ( (inc == 0) && (last < getModel().getSize()-1) ) {
			    inc = lastRect.height;
			}
		    }
                }
            }
            /* Scroll Up */
            else {
                int newFirst = locationToIndex(new Point(visibleRect.x, visibleRect.y-visibleRect.height));
                int first = getFirstVisibleIndex();
                if (newFirst != -1) {
		    if (first == -1) {
			first = locationToIndex(visibleRect.getLocation());
		    }
                    Rectangle newFirstRect = getCellBounds(newFirst,newFirst);
                    Rectangle firstRect = getCellBounds(first,first);
		    if ((newFirstRect != null) && (firstRect!=null)) {
			while ( (newFirstRect.y + visibleRect.height <
				 firstRect.y + firstRect.height) &&
				(newFirstRect.y < firstRect.y) ) {
			    newFirst++;
			    newFirstRect = getCellBounds(newFirst,newFirst);
			}
			inc = visibleRect.y - newFirstRect.y;
			if ( (inc <= 0) && (newFirstRect.y > 0)) {
			    newFirst--;
			    newFirstRect = getCellBounds(newFirst,newFirst);
			    if (newFirstRect != null) {
				inc = visibleRect.y - newFirstRect.y;
			    }
			}
		    }    
		}
            }
            return inc;
        }
	else if (orientation == SwingConstants.HORIZONTAL &&
		 getLayoutOrientation() != JList.VERTICAL) {
	    int inc = visibleRect.width;
	    /* Scroll Right */
	    if (direction > 0) {
		// last cell is an upper right cell
		int last = locationToIndex(new Point(visibleRect.x + visibleRect.width - 1,
						     visibleRect.y));
		if (last != -1) {
		    Rectangle lastRect = getCellBounds(last,last);
		    if (lastRect != null) {
			inc = lastRect.x - visibleRect.x;
			if (inc < 0) {
			    inc += lastRect.width;
			} else if ( (inc == 0) && (last < getModel().getSize()-1) ) {
			    inc = lastRect.width;
			}
		    }
		}
	    }
	    /* Scroll Left */
	    else {
		// first cell is a cell at the upper left corner of the visibleRect
		// shifted left by the visibleRect.width
		int first = locationToIndex(new Point(visibleRect.x - visibleRect.width,
							 visibleRect.y));
		if (first != -1) {
		    Rectangle firstRect = getCellBounds(first,first);
		    if (firstRect != null) {
			if (firstRect.x < visibleRect.x - visibleRect.width) {
			    if (firstRect.x + firstRect.width >= visibleRect.x) {
				inc = visibleRect.x - firstRect.x;
			    } else {
				inc = visibleRect.x - firstRect.x - firstRect.width;
			    }
			} else {
			    inc = visibleRect.x - firstRect.x;
			}
		    }
		}
	    }
	    return inc;
	}
        return visibleRect.width;
    }


    /**
     * Returns true if this <code>JList</code> is displayed in a 
     * <code>JViewport</code> and the viewport is wider than
     * <code>JList</code>'s preferred width; or if the 
     * layout orientation is <code>HORIZONTAL_WRAP</code> and the
     * visible row count is <= 0; otherwise returns
     * false.
     * If false, then don't track the viewport's width. This allows horizontal
     * scrolling if the <code>JViewport</code> is itself embedded in a
     * <code>JScrollPane</code>.
     *
     * @return true if viewport is wider than the <code>JList</code>'s
     *				preferred width, otherwise false
     * @see Scrollable#getScrollableTracksViewportWidth
     */
    public boolean getScrollableTracksViewportWidth() {
        if (getLayoutOrientation() == HORIZONTAL_WRAP &&
                                      getVisibleRowCount() <= 0) {
            return true;
        }
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	}
	return false;
    }

    /**
     * Returns true if this <code>JList</code> is displayed in a 
     * <code>JViewport</code> and the viewport is taller than
     * <code>JList</code>'s preferred height, or if the layout orientation is
     * <code>VERTICAL_WRAP</code> and the number of visible rows is <= 0;
     * otherwise returns false.
     * If false, then don't track the viewport's height. This allows vertical
     * scrolling if the <code>JViewport</code> is itself embedded in a
     * <code>JScrollPane</code>.
     *
     * @return true if viewport is taller than <code>Jlist</code>'s
     *				preferred height, otherwise false
     * @see Scrollable#getScrollableTracksViewportHeight
     */
    public boolean getScrollableTracksViewportHeight() {
        if (getLayoutOrientation() == VERTICAL_WRAP &&
                     getVisibleRowCount() <= 0) {
            return true;
        }
	if (getParent() instanceof JViewport) {
	    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
	}
	return false;
    }


    /*
     * See readObject and writeObject in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }


    /**
     * Returns a string representation of this <code>JList</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JList</code>.
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
        ",visibleRowCount=" + visibleRowCount +
        ",layoutOrientation=" + layoutOrientation;
    }


    /**
     * --- Accessibility Support ---
     */

    /**
     * Gets the AccessibleContext associated with this JList. 
     * For JLists, the AccessibleContext takes the form of an 
     * AccessibleJList. 
     * A new AccessibleJList instance is created if necessary.
     *
     * @return an AccessibleJList that serves as the 
     *         AccessibleContext of this JList
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJList();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JList</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to list user-interface 
     * elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
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
		    Boolean.valueOf(false), Boolean.valueOf(true));
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
                               Boolean.valueOf(false), Boolean.valueOf(true));
            firePropertyChange(AccessibleContext.ACCESSIBLE_SELECTION_PROPERTY,
                               Boolean.valueOf(false), Boolean.valueOf(true));

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
                               Boolean.valueOf(false), Boolean.valueOf(true));
        }

        /**
         * List Data Listener interval removed method. Used to fire the visible data property change
         *
         * @param e ListDataEvent
         *
         */
        public void intervalRemoved(ListDataEvent e) {
            firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                               Boolean.valueOf(false), Boolean.valueOf(true));
        }

        /**
         * List Data Listener contents changed method. Used to fire the visible data property change
         *
         * @param e ListDataEvent
         *
         */
         public void contentsChanged(ListDataEvent e) {
             firePropertyChange(AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                                Boolean.valueOf(false), Boolean.valueOf(true));
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
         * Returns the <code>Accessible</code> child contained at
         * the local coordinate <code>Point</code>, if one exists.
         * Otherwise returns <code>null</code>.
         *
         * @return the <code>Accessible</code> at the specified 
         *    location, if it exists
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
         * Get the AccessibleSelection associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleSelection interface on behalf of itself.
	 * 
	 * @return this object
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
         * fewer items selected than the integer passed in, the return
         * value will be <code>null</code>.
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

	  /**
	   * This class implements accessibility support appropriate 
	   * for list children.
	   */
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
                    boolean isFocussed = parent.isFocusOwner()
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
	   /**
	    * Get the AccessibleContext for this object. In the 
	    * implementation of the Java Accessibility API for this class, 
	    * returns this object, which is its own AccessibleContext.
	    * 
	    * @return this object
	    */
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
	        if (parent.isFocusOwner() 
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

	   /**
            * Get the AccessibleComponent associated with this object.  In the
            * implementation of the Java Accessibility API for this class, 
	    * return this object, which is responsible for implementing the
            * AccessibleComponent interface on behalf of itself.
	    * 
	    * @return this object
	    */
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
		    if (componentLocation != null) {
			componentLocation.translate(listLocation.x, listLocation.y);
			return componentLocation;
		    } else {
			return null;
		    }
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

            // TIGER - 4733624
            /**
             * Returns an AccessibleIcon
             *
             * @return the AccessibleIcon for the element renderer.
             */
            public AccessibleIcon [] getAccessibleIcon() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleIcon();
                } else {
                    return null;
                }
            }
        } // inner class AccessibleJListChild
    } // inner class AccessibleJList
}

