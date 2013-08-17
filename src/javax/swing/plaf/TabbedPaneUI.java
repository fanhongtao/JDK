/*
 * @(#)TabbedPaneUI.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf;

import java.awt.Rectangle;
import javax.swing.JTabbedPane;

/**
 * Pluggable look and feel interface for JTabbedPane.
 *
 * @version 1.10 08/26/98
 * @author Dave Moore
 * @author Amy Fowler
 */
public abstract class TabbedPaneUI extends ComponentUI {
    public abstract int tabForCoordinate(JTabbedPane pane, int x, int y);
    public abstract Rectangle getTabBounds(JTabbedPane pane, int index);
    public abstract int getTabRunCount(JTabbedPane pane);
}
