/*
 * @(#)PropertyChangeListener.java	1.17 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans;

/**
 * A "PropertyChange" event gets fired whenever a bean changes a "bound"
 * property.  You can register a PropertyChangeListener with a source
 * bean so as to be notified of any bound property updates.
 */

public interface PropertyChangeListener extends java.util.EventListener {

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    void propertyChange(PropertyChangeEvent evt);

}
