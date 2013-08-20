/*
 * @(#)TabbedPaneUI.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTabbedPane;

/**
 * Pluggable look and feel interface for JTabbedPane.
 *
 * @version 1.15 12/19/03
 * @author Dave Moore
 * @author Amy Fowler
 */
public abstract class TabbedPaneUI extends ComponentUI {
    public abstract int tabForCoordinate(JTabbedPane pane, int x, int y);
    public abstract Rectangle getTabBounds(JTabbedPane pane, int index);
    public abstract int getTabRunCount(JTabbedPane pane);
}
