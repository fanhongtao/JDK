/*
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
 * @version 1.39 02/06/02
 * @author Alan Chung
 * @author Philip Milne
 */

public class DefaultCellEditor extends AbstractCellEditor 
    implements TableCellEditor, TreeCellEditor { 

//
//  Instance Variables
//

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
                
            public boolean shouldSelectCell(EventObject anEvent) { 
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
//  Override the implementations of the superclass, forwarding all methods 
//  from the CellEditor interface to our delegate. 
//

    public Object getCellEditorValue() {
        return delegate.getCellEditorValue();
    }

    public boolean isCellEditable(EventObject anEvent) { 
	return delegate.isCellEditable(anEvent); 
    }
    
    public boolean shouldSelectCell(EventObject anEvent) { 
	return delegate.shouldSelectCell(anEvent); 
    }

    public boolean stopCellEditing() {
	return delegate.stopCellEditing();
    }

    public void cancelCellEditing() {
	delegate.cancelCellEditing();
    }

//
//  Implementing the TreeCellEditor Interface
//

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

        protected Object value;

        public Object getCellEditorValue() {
            return value;
        }

    	public void setValue(Object value) { 
	    this.value = value; 
	}

        public boolean isCellEditable(EventObject anEvent) {
	    if (anEvent instanceof MouseEvent) { 
		return ((MouseEvent)anEvent).getClickCount() >= clickCountToStart;
	    }
	    return true;
	}
    	
        public boolean shouldSelectCell(EventObject anEvent) { 
            return true; 
        }

        public boolean startCellEditing(EventObject anEvent) {
	    return true;
	}

        public boolean stopCellEditing() { 
	    fireEditingStopped(); 
	    return true;
	}

       public void cancelCellEditing() { 
	   fireEditingCanceled(); 
       }

        public void actionPerformed(ActionEvent e) {
            DefaultCellEditor.this.stopCellEditing();
	}

        public void itemStateChanged(ItemEvent e) {
	    DefaultCellEditor.this.stopCellEditing();
	}
    }

} // End of class JCellEditor
