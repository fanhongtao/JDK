/*
 * @(#)SplitPaneUI.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.JSplitPane;
import java.awt.Graphics;

/**
 * Pluggable look and feel interface for JSplitPane.
 *
 * @version 1.15 01/23/03
 * @author Scott Violet
 */
public abstract class SplitPaneUI extends ComponentUI
{
    /**
     * Messaged to relayout the JSplitPane based on the preferred size
     * of the children components.
     */
    public abstract void resetToPreferredSizes(JSplitPane jc);

    /**
     * Sets the location of the divider to location.
     */
    public abstract void setDividerLocation(JSplitPane jc, int location);

    /**
     * Returns the location of the divider.
     */
    public abstract int getDividerLocation(JSplitPane jc);

    /**
     * Returns the minimum possible location of the divider.
     */
    public abstract int getMinimumDividerLocation(JSplitPane jc);

    /**
     * Returns the maximum possible location of the divider.
     */
    public abstract int getMaximumDividerLocation(JSplitPane jc);

    /**
     * Messaged after the JSplitPane the receiver is providing the look
     * and feel for paints its children.
     */
    public abstract void finishedPaintingChildren(JSplitPane jc, Graphics g);
}
