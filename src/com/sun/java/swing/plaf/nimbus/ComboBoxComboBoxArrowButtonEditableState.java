/*
 * ComboBoxComboBoxArrowButtonEditableState.java 07/12/12
 *
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.nimbus;

import java.awt.*;
import javax.swing.*;

/**
 */
class ComboBoxComboBoxArrowButtonEditableState extends State {
    ComboBoxComboBoxArrowButtonEditableState() {
        super("Editable");
    }

    @Override protected boolean isInState(JComponent c) {

                                Component parent = c.getParent();
                                return parent instanceof JComboBox && ((JComboBox)parent).isEditable();
    }
}

