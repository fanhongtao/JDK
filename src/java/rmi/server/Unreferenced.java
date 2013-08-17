/*
 * @(#)Unreferenced.java	1.4 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
