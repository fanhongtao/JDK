/*
 * @(#)ContainerAdapter.java	1.4 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

/**
 * The adapter which receives container events.
 * The methods in this class are empty;  this class is provided as a
 * convenience for easily creating listeners by extending this class
 * and overriding only the methods of interest.
 *
 * @version 1.4 12/10/01
 * @author Amy Fowler
 */
public abstract class ContainerAdapter implements ContainerListener {
    public void componentAdded(ContainerEvent e) {}
    public void componentRemoved(ContainerEvent e) {}
}
