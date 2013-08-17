/*
 * @(#)DefaultCellEditor.java	1.30 98/08/28
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

import java.awt.Component;
import java.awt.event.*;
import java.awt.AWTEvent;
import java.lang.Boolean;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.EventObject;
import javax.swing.tree.*;
import java.io.Serializable;

/**
 * The default editor for table and tree cells.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.30 08/28/98
 * @author Alan Chung
 */

public class DefaultCellEditor implements TableCellEditor, TreeCellEditor,
					  Serializable {
//
//  Instance Variables
//

    /** Event listeners */
    protected EventListenerList listenerList = new EventListenerList();
    transient protected ChangeEvent changeEvent = null;

    protected JComponent editorComponent;
    protected EditorDelegate delegate;
    protected int clickCountToStart = 1;

//
//  Constructors
//

    /**
     * Constructs a DefaultCellEditor that uses a text field.
     *
     * @param x  a JTextField object ...
     */
    public DefaultCellEditor(JTextField x) {
        this.editorComponent = x;
	this.clickCountToStart = 2;
        this.delegate = new EditorDelegate() {
            public void setValue(Object x) {
                super.setValue(x);
		if (x != null)
		    ((JTextField)editorComponent).setText(x.toString());
		else
		    ((JTextField)editorComponent).setText("");
            }

	    public Object getCellEditorValue() {
		return ((JTextField)editorComponent).getText();
	    }

	    public boolean startCellEditing(EventObject anEvent) {
		if(anEvent == null)
		    editorComponent.requestFocus();
		return true;
	    }

	    public boolean stopCellEditing() {
		return true;
	    }
        };
	((JTextField)editorComponent).addActionListener(delegate);
    }

    /**
     * Constructs a DefaultCellEditor object that uses a check box.
     *
     * @param x  a JCheckBox object ...
     */
    public DefaultCellEditor(JCheckBox x) {
        this.editorComponent = x;
        this.delegate = new EditorDelegate() {
            public void setValue(Object x) {
                super.setValue(x);

		// Try my best to do the right thing with x
		if (x instanceof Boolean) {
		    ((JCheckBox)editorComponent).setSelected(((Boolean)x).booleanValue());
		}
		else if (x instanceof String) {
		    Boolean b = new Boolean((String)x);
		    ((JCheckBox)editorComponent).setSelected(b.booleanValue());
		}
		else {
		    ((JCheckBox)editorComponent).setSelected(false);
		}
            }

	    public Object getCellEditorValue() {
		return new Boolean(((JCheckBox)editorComponent).isSelected());
	    }

	    public boolean startCellEditing(EventObject anEvent) {
		// PENDING(alan)
		if (anEvent instanceof AWTEvent) {
		    return true;
		}
		return false;
	    }

	    public boolean stopCellEditing() {
		return true;
	    }
        };
	((JCheckBox)editorComponent).addActionListener(delegate);
    }

    /**
     * Constructs a DefaultCellEditor object that uses a combo box.
     *
     * @param x  a JComboBox object ...
     */
    public DefaultCellEditor(JComboBox x) {
        this.editorComponent = x;
        this.delegate = new EditorDelegate() {
            public void setValue(Object x) {
                super.setValue(x);
		((JComboBox)editorComponent).setSelectedItem(x);
            }

	    public Object getCellEditorValue() {
		return ((JComboBox)editorComponent).getSelectedItem();
	    }

	    public boolean startCellEditing(EventObject anEvent) {
		if (anEvent instanceof AWTEvent) {
		    return true;
		}
		return false;
	    }

	    public boolean stopCellEditing() {
		return true;
	    }
        };
	((JComboBox)editorComponent).addItemListener(delegate);
    }

    /**
     * Returns the a reference to the editor component.
     *
     * @return the editor Component
     */
    public Component getComponent() {
	return editorComponent;
    }

//
//  Modifying
//

    /**
     * Specifies the number of clicks needed to start editing.
     *
     * @param count  an int specifying the number of clicks needed to start editing
     * @see #getClickCountToStart
     */
    public void setClickCountToStart(int count) {
	clickCountToStart = count;
    }

    /**
     *  clickCountToStart controls the number of clicks required to start
     *  editing if the event passed to isCellEditable() or startCellEditing() is
     *  a MouseEvent.  For example, by default the clickCountToStart for
     *  a JTextField is set to 2, so in a JTable the user will need to
     *  double click to begin editing a cell.
     */
    public int getClickCountToStart() {
	return clickCountToStart;
    }

//
//  Implementing the CellEditor Interface
//

    // implements javax.swing.CellEditor
    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    // implements javax.swing.CellEditor
    public boolean isCellEditable(EventObject anEvent) {
	if (anEvent instanceof MouseEvent) {
	    if (((MouseEvent)anEvent).getClickCount() < clickCountToStart)
		return false;
	}
	return delegate.isCellEditable(anEvent);
    }
    
    // implements javax.swing.CellEditor
    public boolean shouldSelectCell(EventObject anEvent) {
	boolean         retValue = true;

	if (this.isCellEditable(anEvent)) {
	    if (anEvent == null || ((MouseEvent)anEvent).getClickCount() >= 
		clickCountToStart)
		retValue = delegate.startCellEditing(anEvent);
	}

	// By default we want the cell the be selected so
	// we return true
	return retValue;
    }

    // implements javax.swing.CellEditor
    public boolean stopCellEditing() {
	boolean stopped = delegate.stopCellEditing();

	if (stopped) {
	    fireEditingStopped();
	}
	
	return stopped;
    }

    // implements javax.swing.CellEditor
    public void cancelCellEditing() {
	delegate.cancelCellEditing();
	fireEditingCanceled();
    }

//
//  Handle the event listener bookkeeping
//
    // implements javax.swing.CellEditor
    public void addCellEditorListener(CellEditorListener l) {
	listenerList.add(CellEditorListener.class, l);
    }

    // implements javax.swing.CellEditor
    public void removeCellEditorListener(CellEditorListener l) {
	listenerList.remove(CellEditorListener.class, l);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingStopped() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingStopped(changeEvent);
	    }	       
	}
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireEditingCanceled() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==CellEditorListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((CellEditorListener)listeners[i+1]).editingCanceled(changeEvent);
	    }	       
	}
    }

//
//  Implementing the TreeCellEditor Interface
//

    // implements javax.swing.tree.TreeCellEditor
    public Component getTreeCellEditorComponent(JTree tree, Object value,
						boolean isSelected,
						boolean expanded,
						boolean leaf, int row) {
	String         stringValue = tree.convertValueToText(value, isSelected,
					    expanded, leaf, row, false);

	delegate.setValue(stringValue);
	return editorComponent;
    }

//
//  Implementing the CellEditor Interface
//

    // implements javax.swing.table.TableCellEditor
    public Component getTableCellEditorComponent(JTable table, Object value,
						 boolean isSelected,
						 int row, int column) {

	// Modify component colors to reflect selection state
	// PENDING(alan)
	/*if (isSelected) {
	    component.setBackground(selectedBackgroundColor);
	    component.setForeground(selectedForegroundColor);
	}
	else {
	    component.setBackground(backgroundColor);
	    component.setForeground(foregroundColor);
	}*/

        delegate.setValue(value);
	return editorComponent;
    }


//
//  Protected EditorDelegate class
//

    protected class EditorDelegate implements ActionListener, ItemListener, Serializable {

        protected Object value;

        public Object getCellEditorValue() {
            return value;
        }

        public void setValue(Object x) {
            this.value = x;
        }

        public boolean isCellEditable(EventObject anEvent) {
	    return true;
	}

        public boolean startCellEditing(EventObject anEvent) {
	    return true;
	}

        public boolean stopCellEditing() {
	    return true;
	}

        public void cancelCellEditing() {
	}

	// Implementing ActionListener interface
        public void actionPerformed(ActionEvent e) {
	    fireEditingStopped();
	}

	// Implementing ItemListener interface
        public void itemStateChanged(ItemEvent e) {
	    fireEditingStopped();
	}
    }

} // End of class JCellEditor
