/*
 * @(#)MetalSplitPaneUI.java	1.6 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.6 02/02/00
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
