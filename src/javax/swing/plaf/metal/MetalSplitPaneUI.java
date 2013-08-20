/*
 * @(#)MetalSplitPaneUI.java	1.10 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

/**
 * Metal split pane.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.10 12/19/03
 * @author Steve Wilson
 */
public class MetalSplitPaneUI extends BasicSplitPaneUI
{


    /**
      * Creates a new MetalSplitPaneUI instance
      */
    public static ComponentUI createUI(JComponent x) {
	return new MetalSplitPaneUI();
    }

    /**
      * Creates the default divider.
      */
    public BasicSplitPaneDivider createDefaultDivider() {
	return new MetalSplitPaneDivider(this);
    }
}
