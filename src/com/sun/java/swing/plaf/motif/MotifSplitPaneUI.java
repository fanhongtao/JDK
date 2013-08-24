/*
 * @(#)MotifSplitPaneUI.java	1.16 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.motif;

import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

/**
 * Motif rendition of a split pane.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.16 11/17/05
 * @author Jeff Dinkins
 */
public class MotifSplitPaneUI extends BasicSplitPaneUI
{
    public MotifSplitPaneUI() {
	super();
    }

    /**
      * Creates a new MotifSplitPaneUI instance
      */
    public static ComponentUI createUI(JComponent x) {
	return new MotifSplitPaneUI();
    }

    /**
      * Creates the default divider.
      */
    public BasicSplitPaneDivider createDefaultDivider() {
	return new MotifSplitPaneDivider(this);
    }

}
