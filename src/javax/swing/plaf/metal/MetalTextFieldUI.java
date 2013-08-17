/*
 * @(#)MetalTextFieldUI.java	1.6 98/08/28
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.plaf.metal;

import java.awt.*;
import java.beans.*;

import javax.swing.*;

import javax.swing.text.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * Basis of a look and feel for a JTextField.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @author  Steve Wilson
 * @version 1.6 08/28/98
 */
public class MetalTextFieldUI extends BasicTextFieldUI {

    public static ComponentUI createUI(JComponent c) {
        return new MetalTextFieldUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
	editableChanged(c, ((JTextComponent)c).isEditable());
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("editable")) {
	    JComponent source = (JComponent)e.getSource();
	    Color background = source.getBackground();
	    boolean editable =  ((Boolean)e.getNewValue()).booleanValue();
	    editableChanged( source, editable);

	}
    }
    private void editableChanged(JComponent c, boolean editable) {
	    Color background = c.getBackground();
	    if (editable == false) {
 	        if (background instanceof UIResource) {
		   c.setBackground(UIManager.getColor("control"));
		}
	    } else {
	         if (background instanceof UIResource) {
		    c.setBackground(UIManager.getColor("TextField.background"));
		}
	    }
    }

}
