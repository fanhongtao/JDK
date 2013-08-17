/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
 * @version 1.10 02/06/02
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
        super.propertyChange(e);
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
