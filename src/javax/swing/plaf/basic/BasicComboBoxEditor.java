/*
 * @(#)BasicComboBoxEditor.java	1.9 98/05/09
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
package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.border.*;
import java.io.Serializable;
import java.awt.*;
import java.awt.event.*;

/**
 * The default editor for editable combo boxes
 *
 * @version 1.9 05/09/98
 * @author Arnaud Weber
 */
public class BasicComboBoxEditor implements ComboBoxEditor,FocusListener {
    protected JTextField editor;

    public BasicComboBoxEditor() {
        editor = new BorderlessTextField("",9);
        editor.addFocusListener(this);
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

    public void focusGained(FocusEvent e) {}
    public void focusLost(FocusEvent e) {
        if ( !e.isTemporary() ) {
            editor.postActionEvent();
        }
    }

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

