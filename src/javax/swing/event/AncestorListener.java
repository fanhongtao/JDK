/*
 * @(#)AncestorListener.java	1.7 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.event;

import java.awt.event.*;
import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 * AncestorListener
 *
 * Interface to support notification when changes occur to a JComponent or one
 * of its ancestors.  These include movement and when the component becomes
 * visible or invisible, either by the setVisible() method or by being added
 * or removed from the component hierarchy.
 *
 * @version 1.7 09/21/98
 * @author Dave Moore
 */
public interface AncestorListener extends EventListener {
    /**
     * Called when the source or one of its ancestors is made visible
     * either by setVisible(true) being called or by its being
     * added to the component hierarchy.  The method is only called
     * if the source has actually become visible.  For this to be true
     * all its parents must be visible and it must be in a hierarchy
     * rooted at a Window
     */
    public void ancestorAdded(AncestorEvent event);

    /**
     * Called when the source or one of its ancestors is made invisible
     * either by setVisible(false) being called or by its being
     * remove from the component hierarchy.  The method is only called
     * if the source has actually become invisible.  For this to be true
     * at least one of its parents must by invisible or it is not in
     * a hierarchy rooted at a Window
     */
    public void ancestorRemoved(AncestorEvent event);

    /**
     * Called when either the source or one of its ancestors is moved.
     */
    public void ancestorMoved(AncestorEvent event);

}


