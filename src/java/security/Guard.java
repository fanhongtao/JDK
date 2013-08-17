/*
 * @(#)Guard.java	1.6 98/06/29
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

package java.security;

/**
 * <p> This interface represents a guard, which is an object that is used
 * to protect access to another object.
 *
 * <p>This interface contains a single method, <code>checkGuard</code>,
 * with a single <code>object</code> argument. <code>checkGuard</code> is
 * invoked (by the GuardedObject <code>getObject</code> method)
 * to determine whether or not to allow access to the object.
 *
 * @see GuardedObject
 *
 * @version 1.6 00/05/10
 * @author Roland Schemers
 * @author Li Gong
 */

public interface Guard {

    /**
     * Determines whether or not to allow access to the guarded object
     * <code>object</code>. Returns silently if access is allowed.
     * Otherwise, throws a SecurityException.
     *
     * @param object the object being protected by the guard.
     *
     * @exception SecurityException if access is denied.
     *
     */
    void checkGuard(Object object) throws SecurityException;
}
