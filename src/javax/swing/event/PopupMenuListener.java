/*
 * @(#)PopupMenuListener.java	1.6 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing.event;

import java.util.EventListener;

/**
 * A popup menu listener
 *
 * @version 1.6 02/02/00
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

