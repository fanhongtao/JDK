/*
 * @(#)Unreferenced.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

/**
 * A remote object should implement this interface to receive notification
 * when there are no more remote references to it.
 */
public interface Unreferenced {
    /**
     * Called when there are no current references to this remote object.
     */
    public void unreferenced();
}
