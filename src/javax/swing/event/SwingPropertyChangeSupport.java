/*
 * @(#)SwingPropertyChangeSupport.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.beans.PropertyChangeSupport;

/**
 * This subclass of java.beans.PropertyChangeSupport is identical
 * in functionality -- it sacrifices thread-safety (not a Swing
 * concern) for reduce memory consumption, which helps performance
 * (both big Swing concerns).  Most of the overridden methods are
 * only necessary because all of PropertyChangeSupport's instance
 * data is private, without accessor methods.
 *
 * @version 1.20 12/19/03
 * @author unattributed
 */

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {

    /**
     * Constructs a SwingPropertyChangeSupport object.
     *
     * @param sourceBean  The bean to be given as the source for any events.
     */
    public SwingPropertyChangeSupport(Object sourceBean) {
        super(sourceBean);
    }

    // Serialization version ID
    static final long serialVersionUID = 7162625831330845068L;
}
