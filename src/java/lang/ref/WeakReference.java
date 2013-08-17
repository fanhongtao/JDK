/*
 * @(#)WeakReference.java	1.7 98/09/30
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

package java.lang.ref;


/**
 * Weak reference objects, which do not prevent their referents from being
 * made finalizable, finalized, and then reclaimed.  Weak references are most
 * often used to implement canonicalizing mappings.
 *
 * <p> Suppose that the garbage collector determines at a certain point in time
 * that an object is <a href="package-summary.html#reachability">weakly
 * reachable</a>.  At that time it will atomically clear all weak references to
 * that object and all weak references to any other weakly-reachable objects
 * from which that object is reachable through a chain of strong and soft
 * references.  At the same time it will declare all of the formerly
 * weakly-reachable objects to be finalizable.  At the same time or at some
 * later time it will enqueue those newly-cleared weak references that are
 * registered with reference queues.
 *
 * @version  1.7, 98/09/30
 * @author   Mark Reinhold
 * @since    JDK1.2
 */

public class WeakReference extends Reference {

    /**
     * Creates a new weak reference that refers to the given object.  The new
     * reference is not registered with any queue.
     */
    public WeakReference(Object referent) {
	super(referent);
    }

    /**
     * Creates a new weak reference that refers to the given object and is
     * registered with the given queue.
     *
     * @throws  NullPointerException  If the <code>queue</code> argument
     *                                is <code>null</code>
     *
     */
    public WeakReference(Object referent, ReferenceQueue q) {
	super(referent, q);
    }

}
