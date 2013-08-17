/*
 * @(#)JTableHeader.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.table;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

import java.beans.PropertyChangeListener;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * This is the column header part of a JTable.  I allow the user to
 * change column widths and column ordering.  I share the same
 * TableColumnModel with a JTable.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.36 10/08/98
 * @author Alan Chung
 * @author Philip Milne
 * @see javax.swing.JTable
 */
public class JTableHeader extends JComponent implements TableColumnModelListener, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "TableHeaderUI";

//
// Instance Variables
//
    protected JTable table;

    /** The TableColumnModel of the table header*/
    protected TableColumnModel	columnModel;

    /** Reordering of columns are allowed by the user */
    protected boolean	reorderingAllowed;

    /** Resizing of columns are allowed by the user */
    protected boolean	resizingAllowed;

    /**
     * If this flag is true, then the header will repaint the table as
     * a column is dragged or resized.
     */
    protected boolean	updateTableInRealTime;

    /** The index of the column being resized. 0 if not resizing */
    transient protected TableColumn	resizingColumn;

    /** The index of the column being dragged. 0 if not dragging */
    transient protected TableColumn	draggedColumn;

    /** The distance from its original position the column has been dragged */
    transient protected int	draggedDistance;

//
// Constructors
//

    /**
     *  Constructs a JTableHeader with a default TableColumnModel
     *
     * @see #createDefaultColumnModel()
     */
    public JTableHeader() {
	this(null);
    }

    /**
     *  Constructs a JTableHeader which is initialized with
     *  <i>cm</i> as the column model.  If <i>cm</i> is
     *  <b>null</b> this method will initialize the table header
     *  with a default TableColumnModel.
     *
     * @param cm	The column model for the table
     * @see #createDefaultColumnModel()
     */
    public JTableHeader(TableColumnModel cm) {
	super();

	if (cm == null)
	    cm = createDefaultColumnModel();
	setColumnModel(cm);

	// Initalize local ivars
	initializeLocalVars();

	// Get UI going
	updateUI();
    }

//
// Local behavior attributes
//

    /**  Sets the header's partner table to <I>aTable</I> */
    public void setTable(JTable aTable) {
	table = aTable;
    }

    /** Returns the header's partner table */
    public JTable getTable() {
	return table;
    }

    /**
     *  Sets whether the user can drag column headers to reorder columns.
     *
     * @param	flag			true if the table view should allow
     *  				reordering
     * @see	#getReorderingAllowed
     */
    public void setReorderingAllowed(boolean b) {
	reorderingAllowed = b;
    }

    /**
     * Returns true if the receiver allows the user to rearrange columns by
     * dragging their headers, false otherwise. The default is true. You can
     * rearrange columns programmatically regardless of this setting.
     *
     * @return	true if the receiver allows the user to rearrange columns by
     * 		dragging their headers, false otherwise
     * @see	#setReorderingAllowed
     */
    public boolean getReorderingAllowed() {
	return reorderingAllowed;
    }

    /**
     *  Sets whether the user can resize columns by dragging between headers.
     *
     * @param	flag			true if table view should allow
     * 					resizing
     * @see	#getResizingAllowed
     */
    public void setResizingAllowed(boolean b) {
	resizingAllowed = b;
    }

    /**
     * Returns true if the receiver allows the user to resize columns by dragging
     * between their headers, false otherwise. The default is true. You can
     * resize columns programmatically regardless of this setting.
     *
     * @return	true if the receiver allows the user to resize columns by
     * 		dragging between their headers, false otherwise.
     * @see	#setResizingAllowed
     */
    public boolean getResizingAllowed() {
	return resizingAllowed;
    }

    /**
     * Returns the the dragged column, if and only if a drag is in
     * process.
     *
     * @return	the the dragged column, if and only if a drag is in
     * 		process, otherwise returns null.
     * @see	#getDraggedDistance
     */
    public TableColumn getDraggedColumn() {
	return draggedColumn;
    }

    /**
     * Returns the column's horizontal distance from its original
     * position, if and only if a drag is in process. Otherwise, the
     * the return value is meaningless.
     *
     * @return	the column's horizontal distance from its original
     *		position, if and only if a drag is in process
     * @see	#getDraggedColumn
     */
    public int getDraggedDistance() {
	return draggedDistance;
    }

    /**
     * Returns the resizing column.  If no column is being
     * resized this method returns null.
     *
     * @return	the resizing column
     */
    public TableColumn getResizingColumn() {
	return resizingColumn;
    }

    /**
     *  Sets whether the body of the table updates in real time when
     *  a column is resized or dragged.
     *
     * @param	flag			true if tableView should update
     *					the body of the table in real time
     * @see #getUpdateTableInRealTime
     */
    public void setUpdateTableInRealTime(boolean flag) {
	updateTableInRealTime = flag;
    }

    /**
     * Returns true if the receiver updates the body of the table view in real
     * time when a column is resized or dragged.  User can set this flag to
     * false to speed up the table's response to user resize or drag actions.
     * The default is true.
     *
     * @return	true if the table updates in real time
     * @see #setUpdateTableInRealTime
     */
    public boolean getUpdateTableInRealTime() {
	return updateTableInRealTime;
    }

    /**
     * Returns the index of the column that <I>point</I> lies in, or -1 if it
     * lies outside the receiver's bounds.
     *
     * @return	the index of the column that <I>point</I> lies in, or -1 if it
     *		lies outside the receiver's bounds
     */
    public int columnAtPoint(Point point) {
	return getColumnModel().getColumnIndexAtX(point.x);
    }

    /**
     * Returns the rectangle containing the header tile at <I>columnIndex</I>.
     *
     * @return	the rectangle containing the header tile at <I>columnIndex</I>
     * @exception IllegalArgumentException	If <I>columnIndex</I> is out
     *						of range
     */
    public Rectangle getHeaderRect(int columnIndex) {
        // PENDING(philiip) The implementation should be delegated to the UI.
	TableColumnModel columnModel = getColumnModel();

	if ((columnIndex < 0) || (columnIndex >= columnModel.getColumnCount())) {
	    throw new IllegalArgumentException("Column index out of range");
	}

	int rectX = 0;
	int column = 0;
	int columnMargin = getColumnModel().getColumnMargin();
	Enumeration enumeration = getColumnModel().getColumns();
	while (enumeration.hasMoreElements()) {
	    TableColumn aColumn = (TableColumn)enumeration.nextElement();

	    if (column == columnIndex) {
		return new Rectangle(rectX, 0,
				     aColumn.getWidth() + columnMargin,
				     getSize().height);
	    }
	    rectX += aColumn.getWidth() + columnMargin;
	    column++;
	}
	return new Rectangle();
    }


    /**
     * Overriding to allow renderer's tips to be used if it has
     * text set.
     */
    public String getToolTipText(MouseEvent event) {
	String tip = null;
	Point p = event.getPoint();
	int column;

	// Locate the renderer under the event location
	if ((column = columnModel.getColumnIndexAtX(p.x)) != -1) {
	    TableColumn aColumn = columnModel.getColumn(column);
	    TableCellRenderer renderer = aColumn.getHeaderRenderer();
	    Component component = renderer.getTableCellRendererComponent(
			      getTable(), aColumn.getHeaderValue(), false, false,
			      -1, column);

	    // Now have to see if the component is a JComponent before
	    // getting the tip
	    if (component instanceof JComponent) {
		// Convert the event to the renderer's coordinate system
		MouseEvent newEvent;
		Rectangle cellRect = getHeaderRect(column);

		p.translate(-cellRect.x, -cellRect.y);
		newEvent = new MouseEvent(component, event.getID(),
					  event.getWhen(), event.getModifiers(),
					  p.x, p.y, event.getClickCount(),
					  event.isPopupTrigger());

		tip = ((JComponent)component).getToolTipText(newEvent);
	    }
	}

	// No tip from the renderer get our own tip
	if (tip == null)
	    tip = getToolTipText();

	return tip;
    }

//
// Managing TableHeaderUI
//

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the TableHeaderUI object that renders this component
     */
    public TableHeaderUI getUI() {
	return (TableHeaderUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the TableHeaderUI L&F object
     * @see UIDefaults#getUI
     */
    public void setUI(TableHeaderUI ui){
        if (this.ui != ui) {
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
    public void updateUI(){
	setUI((TableHeaderUI)UIManager.getUI(this));
	resizeAndRepaint();
	invalidate();//PENDING
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "TableHeaderUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
	return uiClassID;
    }


//
// Managing models
//

    /**
     *  Sets the column model for this table to <I>newModel</I> and registers
     *  with for listner notifications from the new column model.
     *
     * @param	newModel	the new data source for this table
     * @exception IllegalArgumentException	if <I>newModel</I> is null
     * @see	#getColumnModel()
     */
    public void setColumnModel(TableColumnModel newModel) {
	if (newModel == null) {
	    throw new IllegalArgumentException("Cannot set a null ColumnModel");
	}

	TableColumnModel oldModel = columnModel;
	if (newModel != oldModel) {
	    if (oldModel != null)
		oldModel.removeColumnModelListener(this);

	    columnModel = newModel;
	    newModel.addColumnModelListener(this);

	    resizeAndRepaint();
	}
    }

    /**
     * Returns the <B>TableColumnModel</B> that contains all column inforamtion
     * of this table header.
     *
     * @return	the object that provides the column state of the table
     * @see	#setColumnModel()
     */
    public TableColumnModel getColumnModel() {
	return columnModel;
    }

//
// Implementing TableColumnModelListener interface
//

    // implements javax.swing.event.TableColumnModelListener
    public void columnAdded(TableColumnModelEvent e) { resizeAndRepaint(); }
    // implements javax.swing.event.TableColumnModelListener
    public void columnRemoved(TableColumnModelEvent e) { resizeAndRepaint(); }
    // implements javax.swing.event.TableColumnModelListener
    public void columnMoved(TableColumnModelEvent e) { repaint(); }
    // implements javax.swing.event.TableColumnModelListener
    public void columnMarginChanged(ChangeEvent e) { resizeAndRepaint(); }
    // implements javax.swing.event.TableColumnModelListener
    // --Redrawing the header is slow in cell selection mode.
    // --Since header selection is ugly and it is always cler from the
    // --view which columns are selected, don't redraw the header.
    public void columnSelectionChanged(ListSelectionEvent e) { } // repaint(); }

//
//  Package Methods
//

    /**
     *  Returns the default column model object which is
     *  a DefaultTableColumnModel.  Subclass can override this
     *  method to return a different column model object
     *
     * @return the default column model object
     */
    protected TableColumnModel createDefaultColumnModel() {
	return new DefaultTableColumnModel();
    }

    protected void initializeLocalVars() {
        setOpaque(true);
	table = null;
	reorderingAllowed = true;
	resizingAllowed = true;
	draggedColumn = null;
	draggedDistance = 0;
	resizingColumn = null;
	updateTableInRealTime = true;

	// I'm registered to do tool tips so we can draw tips for the
	// renderers
	ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
	toolTipManager.registerComponent(this);

    }

    /**
     * Properly sizes the receiver and its header view, and marks it as
     * needing display. Also resets cursor rectangles for the header view
     * and line scroll amounts for the <B>JScrollPane</B>.
     */
    public void resizeAndRepaint() {
        revalidate();
	repaint();
    }

    /**  Sets the header's draggedColumn to <I>aColumn</I> */
    public void setDraggedColumn(TableColumn aColumn) {
	draggedColumn = aColumn;
    }

    /**  Sets the header's draggedDistance to <I>distance</I> */
    public void setDraggedDistance(int distance) {
	draggedDistance = distance;
    }
    /**  Sets the header's resizingColumn to <I>aColumn</I> */
    public void setResizingColumn(TableColumn aColumn) {
	resizingColumn = aColumn;
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
     * Returns a string representation of this JTableHeader. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JTableHeader.
     */
    protected String paramString() {
        String reorderingAllowedString = (reorderingAllowed ?
					  "true" : "false");
        String resizingAllowedString = (resizingAllowed ?
					"true" : "false");
        String updateTableInRealTimeString = (updateTableInRealTime ?
					      "true" : "false");

        return super.paramString() +
        ",draggedDistance=" + draggedDistance +
        ",reorderingAllowed=" + reorderingAllowedString +
        ",resizingAllowed=" + resizingAllowedString +
        ",updateTableInRealTime=" + updateTableInRealTimeString;
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
	    accessibleContext = new AccessibleJTableHeader();
	}
	return accessibleContext;
    }

    //
    // *** should also implement AccessibleSelction?
    // *** and what's up with keyboard navigation/manipulation?
    //
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
    protected class AccessibleJTableHeader extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
	 * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.PANEL;
        }

        /**
         * Returns the Accessible child, if one exists, contained at the local
         * coordinate Point.
         *
         * @param p The point defining the top-left corner of the Accessible,
         * given in the coordinate space of the object's parent.
         * @return the Accessible, if it exists, at the specified location;
         * else null
         */
        public Accessible getAccessibleAt(Point p) {
            int column;

            // Locate the renderer under the Point
            if ((column = JTableHeader.this.columnAtPoint(p)) != -1) {
                TableColumn aColumn = JTableHeader.this.columnModel.getColumn(column);
                TableCellRenderer renderer = aColumn.getHeaderRenderer();
                Component component = renderer.getTableCellRendererComponent(
                                  JTableHeader.this.getTable(),
                                  aColumn.getHeaderValue(), false, false,
                                  -1, column);

                return new AccessibleJTableHeaderEntry(column, JTableHeader.this, JTableHeader.this.table);
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
            return JTableHeader.this.columnModel.getColumnCount();
        }

        /**
         * Return the nth Accessible child of the object.
         *
         * @param i zero-based index of child
         * @return the nth Accessible child of the object
         */
        public Accessible getAccessibleChild(int i) {
            if (i < 0 || i >= getAccessibleChildrenCount()) {
                return null;
            } else {
                TableColumn aColumn = JTableHeader.this.columnModel.getColumn(i)
;
                TableCellRenderer renderer = aColumn.getHeaderRenderer();
                Component component = renderer.getTableCellRendererComponent(
                                  JTableHeader.this.getTable(),
                                  aColumn.getHeaderValue(), false, false,
                                  -1, i);

                return new AccessibleJTableHeaderEntry(i, JTableHeader.this, JTableHeader.this.table);
            }
        }

        protected class AccessibleJTableHeaderEntry extends AccessibleContext
            implements Accessible, AccessibleComponent  {

            private JTableHeader parent;
            private int column;
	    private JTable table;

            /**
             *  Constructs an AccessiblJTableHeaaderEntry
             */
            public AccessibleJTableHeaderEntry(int c, JTableHeader p, JTable t) {
                parent = p;
                column = c;
		table = t;
		this.setAccessibleParent(parent);
            }

            /**
             * Get the AccessibleContext associated with this
             *
             * @return the AccessibleContext of this JComponent
             */
            public AccessibleContext getAccessibleContext() {
                return this;
            }

	    private AccessibleContext getCurrentAccessibleContext() {
		TableColumnModel tcm = table.getColumnModel();
                if (tcm != null) {
		    TableColumn aColumn = tcm.getColumn(column);
		    TableCellRenderer renderer = aColumn.getHeaderRenderer();
		    Component c = renderer.getTableCellRendererComponent(
				      JTableHeader.this.getTable(),
				      aColumn.getHeaderValue(), false, false,
				      -1, column);
		    if (c instanceof Accessible) {
			return ((Accessible) c).getAccessibleContext();
		    }
		}
		return null;
	    }

	    private Component getCurrentComponent() {
		TableColumnModel tcm = table.getColumnModel();
                if (tcm != null) {
		    TableColumn aColumn = tcm.getColumn(column);
		    TableCellRenderer renderer = aColumn.getHeaderRenderer();
		    return renderer.getTableCellRendererComponent(
				      JTableHeader.this.getTable(),
				      aColumn.getHeaderValue(), false, false,
				      -1, column);
		} else {
		    return null;
		}
            }

        // AccessibleContext methods

            public String getAccessibleName() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
		    String name = ac.getAccessibleName();
                    if ((name != null) && (name != "")) {
			return ac.getAccessibleName();
		    }
                }
		if ((accessibleName != null) && (accessibleName != "")) {
		    return accessibleName;
		} else {
                    return table.getColumnName(column);
                }
            }

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
            public String getAccessibleDescription() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleDescription();
                } else {
                    return super.getAccessibleDescription();
                }
            }

            public void setAccessibleDescription(String s) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.setAccessibleDescription(s);
                } else {
		    super.setAccessibleDescription(s);
		}
            }

            public AccessibleRole getAccessibleRole() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    return ac.getAccessibleRole();
                } else {
                    return AccessibleRole.COLUMN_HEADER;
                }
            }

            public AccessibleStateSet getAccessibleStateSet() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    AccessibleStateSet states = ac.getAccessibleStateSet();
                    if (isShowing()) {
                        states.add(AccessibleState.SHOWING);
                    }
                    return states;
                } else {
                    return new AccessibleStateSet();  // must be non null?
                }
            }

            public int getAccessibleIndexInParent() {
                return column;
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
                } else {
		    super.addPropertyChangeListener(l);
		}
            }

            public void removePropertyChangeListener(PropertyChangeListener l) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac != null) {
                    ac.removePropertyChangeListener(l);
                } else {
		    super.removePropertyChangeListener(l);
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
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    return ((AccessibleComponent) ac).isVisible();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        return c.isVisible();
                    } else {
                        return false;
                    }
                }
            }

            public void setVisible(boolean b) {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    ((AccessibleComponent) ac).setVisible(b);
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
                        c.setVisible(b);
                    }
                }
            }

            public boolean isShowing() {
                if (isVisible() && JTableHeader.this.isShowing()) {
                    return true;
                } else {
                    return false;
                }
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
                    Point parentLocation = parent.getLocationOnScreen();
                    Point componentLocation = getLocation();
                    componentLocation.translate(parentLocation.x, parentLocation.y);
                    return componentLocation;
                } else {
                    return null;
                }
            }

            public Point getLocation() {
                AccessibleContext ac = getCurrentAccessibleContext();
                if (ac instanceof AccessibleComponent) {
                    Rectangle r = ((AccessibleComponent) ac).getBounds();
                    return r.getLocation();
                } else {
                    Component c = getCurrentComponent();
                    if (c != null) {
			Rectangle r = c.getBounds();
                        return r.getLocation();
                    } else {
                        return getBounds().getLocation();
                    }
		}
            }

            public void setLocation(Point p) {
//                if ((parent != null)  && (parent.contains(p))) {
//                    ensureIndexIsVisible(indexInParent);
//                }
            }

            public Rectangle getBounds() {
                  Rectangle r = table.getCellRect(-1, column, false);
                  r.y = 0;
                  return r;

//                AccessibleContext ac = getCurrentAccessibleContext();
//                if (ac instanceof AccessibleComponent) {
//                    return ((AccessibleComponent) ac).getBounds();
//                } else {
//		    Component c = getCurrentComponent();
//		    if (c != null) {
//			return c.getBounds();
//		    } else {
//			Rectangle r = table.getCellRect(-1, column, false);
//			r.y = 0;
//			return r;
//		    }
//		}
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
//                AccessibleContext ac = getCurrentAccessibleContext();
//                if (ac instanceof AccessibleComponent) {
//                    Rectangle r = ((AccessibleComponent) ac).getBounds();
//                    return r.getSize();
//                } else {
//                    Component c = getCurrentComponent();
//                    if (c != null) {
//                        Rectangle r = c.getBounds();
//                        return r.getSize();
//                    } else {
//                        return getBounds().getSize();
//                    }
//                }
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

        } // inner class AccessibleJTableHeaderElement

    }  // inner class AccessibleJTableHeader

}  // End of Class JTableHeader


