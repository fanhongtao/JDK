/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.*;
import java.io.Serializable;


/**
 * CheckboxUI implementation for BasicCheckboxUI
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.34 02/06/02
 * @author Jeff Dinkins
 */
public class BasicCheckBoxUI extends BasicRadioButtonUI {

    private final static BasicCheckBoxUI checkboxUI = new BasicCheckBoxUI();

    private final static String propertyPrefix = "CheckBox" + "."; 

    // ********************************
    //            Create PLAF 
    // ********************************
    public static ComponentUI createUI(JComponent b) {
        return checkboxUI;
    }

    public String getPropertyPrefix() {
	return propertyPrefix;
    }

}
