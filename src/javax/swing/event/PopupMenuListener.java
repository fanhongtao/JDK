/*
 * @(#)PopupMenuListener.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.event;

import java.util.EventListener;

/**
 * A popup menu listener
 *
 * @version 1.10 11/17/05
 * @author Arnaud Weber
 */
public interface PopupMenuListener extends EventListener {
    
    /**
     *  This method is called before the popup menu becomes visible 
     */
    void popupMenuWillBecomeVisible(PopupMenuEvent e);

    /**
     * This method is called before the popup menu becomes invisible
     * Note that a JPopupMenu can become invisible any time 
     */
    void popupMenuWillBecomeInvisible(PopupMenuEvent e);

    /**
     * This method is called when the popup menu is canceled
     */
    void popupMenuCanceled(PopupMenuEvent e);
}

