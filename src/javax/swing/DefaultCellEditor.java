/*
 * @(#)DefaultCellEditor.java	1.38 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.38 11/29/01
 * @author Philip Milne
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
    public DefaultCellEditor(final JTextField textField) {
        editorComponent = textField;
	this.clickCountToStart = 2;
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
		textField.setText((value != null) ? value.toString() : "");
            }

	    public Object getCellEditorValue() {
		return textField.getText();
	    }
        };
	textField.addActionListener(delegate);
    }

    /**
     * Constructs a DefaultCellEditor object that uses a check box.
     *
     * @param x  a JCheckBox object ...
     */
    public DefaultCellEditor(final JCheckBox checkBox) {
        editorComponent = checkBox;
        delegate = new EditorDelegate() {
            public void setValue(Object value) { 
            	boolean selected = false; 
		if (value instanceof Boolean) {
		    selected = ((Boolean)value).booleanValue();
		}
		else if (value instanceof String) {
		    selected = value.equals("true");
		}
		checkBox.setSelected(selected);
            }

	    public Object getCellEditorValue() {
		return new Boolean(checkBox.isSelected());
	    }
        };
	checkBox.addActionListener(delegate);
    }

    /**
     * Constructs a DefaultCellEditor object that uses a combo box.
     *
     * @param x  a JComboBox object ...
     */
    public DefaultCellEditor(final JComboBox comboBox) {
        editorComponent = comboBox;
        comboBox.putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
        delegate = new EditorDelegate() {
            public void setValue(Object value) {
		comboBox.setSelectedItem(value);
            }

	    public Object getCellEditorValue() {
		return comboBox.getSelectedItem();
	    }
                
            boolean shouldSelectCell(EventObject anEvent) { 
                if (anEvent instanceof MouseEvent) { 
                    MouseEvent e = (MouseEvent)anEvent;
                    return e.getID() != MouseEvent.MOUSE_DRAGGED;
                }
                return true;
            }
        };
	comboBox.addActionListener(delegate);
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
     *  ClickCountToStart controls the number of clicks required to start
     *  editing.
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
            return ((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
        }
    	return true;
    }
    
    // implements javax.swing.CellEditor
    public boolean shouldSelectCell(EventObject anEvent) { 
	return delegate.shouldSelectCell(anEvent); 
    }

    // implements javax.swing.CellEditor
    public boolean stopCellEditing() {
	fireEditingStopped();
    	return true;
    }

    // implements javax.swing.CellEditor
    public void cancelCellEditing() {
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
        delegate.setValue(value);
	return editorComponent;
    }


//
//  Protected EditorDelegate class
//

    protected class EditorDelegate implements ActionListener, ItemListener, Serializable {

        /** Not implemented. */
        protected Object value;

        /** Not implemented. */
        public Object getCellEditorValue() {
            return null;
        }

        /** Not implemented. */
    	public void setValue(Object x) {}

        /** Not implemented. */
        public boolean isCellEditable(EventObject anEvent) {
	    return true;
	}
    	
        /** Unfortunately, restrictions on API changes force us to 
          * declare this method package private. 
          */
        boolean shouldSelectCell(EventObject anEvent) { 
            return true; 
        }


        /** Not implemented. */
        public boolean startCellEditing(EventObject anEvent) {
	    return true;
	}

        /** Not implemented. */
        public boolean stopCellEditing() {
	    return true;
	}

        /** Not implemented. */
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
