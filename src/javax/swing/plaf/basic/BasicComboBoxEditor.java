/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.border.*;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

/**
 * The default editor for editable combo boxes
 *
 * @version 1.18 02/06/02
 * @author Arnaud Weber
 */
public class BasicComboBoxEditor implements ComboBoxEditor,FocusListener {
    protected JTextField editor;

    public BasicComboBoxEditor() {
        editor = new BorderlessTextField("",9);
        //editor.addFocusListener(this);
        editor.setBorder(null);
    }

    public Component getEditorComponent() {
        return editor;
    }

    public void setItem(Object anObject) {
        if ( anObject != null )
            editor.setText(anObject.toString());
        else
            editor.setText("");
    }

    public Object getItem() {
        return editor.getText();
    }

    public void selectAll() {
        editor.selectAll();
        editor.requestFocus();
    }

    // This used to do something but now it doesn't.  It couldn't be
    // removed because it would be an API change to do so.
    public void focusGained(FocusEvent e) {}
    
    // This used to do something but now it doesn't.  It couldn't be
    // removed because it would be an API change to do so.
    public void focusLost(FocusEvent e) {}

    public void addActionListener(ActionListener l) {
        editor.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        editor.removeActionListener(l);
    }

    static class BorderlessTextField extends JTextField {
        public BorderlessTextField(String value,int n) {
            super(value,n);
        }

        public void setBorder(Border b) {}
    }

    /**
     * A subclass of BasicComboBoxEditor that implements UIResource.
     * BasicComboBoxEditor doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with BasicListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    public static class UIResource extends BasicComboBoxEditor
    implements javax.swing.plaf.UIResource {
    }
}

