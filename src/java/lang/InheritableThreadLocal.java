/*
 * @(#)InheritableThreadLocal.java	1.2 98/06/29
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

/**
 * This class extends ThreadLocal to provide inheritance of values from parent
 * Thread to child Thread: when a child thread is created, the child receives
 * initial values for all InheritableThreadLocals for which the parent has
 * values.  Normally the child's values will be identical to the parent's;
 * however, the child's value can be made an arbitrary function of the parent's
 * by overriding the childValue method in this class.
 * <p>
 * InheritableThreadLocal variables are used in preference to ordinary
 * ThreadLocal variables when the per-thread-attribute being maintained in the
 * variable (e.g., User ID, Transaction ID) must be automatically transmitted
 * to any child threads that are created.
 *
 * @author  Josh Bloch
 * @version 1.2 06/29/98
 * @see ThreadLocal
 * @since JDK1.2
 */

public class InheritableThreadLocal extends ThreadLocal {
    /**
     * Creates an InheritableThreadLocal variable.
     */
    public InheritableThreadLocal() {
    }

    /**
     * Computes the child's initial value for this InheritableThreadLocal
     * as a function of the parent's value at the time the child Thread is
     * created.  This method is called from within the parent thread before
     * the child is started.
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     */
    protected Object childValue(Object parentValue) {
        return parentValue;
    }

    /**
     * Passes the ThreadLocal values represented by the specified list of
     * Entries onto the specified child Thread.  (The child's value is
     * computed from the parent's as per the childValue method.)  This
     * method is invoked (only) at Thread creation time, by Thread.init.
     */
    static void bequeath(Thread parent, Thread child) {
        for (Entry e = parent.values; e != null; e = e.next)
            e.bequeath(child);
    }

    /**
     * Overrides method in ThreadLocal and implements inheritability,
     * in conjunction with the bequeath method.
     */
    ThreadLocal.Entry newEntry(Object value) {
        return new Entry(value);
    }

    /**
     * The information associated with an (InheritableThreadLocal,Thread) pair.
     */
    class Entry extends ThreadLocal.Entry {
        /**
         * This constructor places the newly constructed Entry on the
         * specified thread's values list.
         */
        private Entry(Object value, Thread t) {
            super(value);
            next = t.values;
            t.values = this;
        }

        /**
         * This constructor places the newly constructed Entry on the
         * calling thread's values list.
         */
        Entry(Object value) {
            this(value, Thread.currentThread());
        }

        /**
         * Passes the ThreadLocal value represented by this Entry on to the
         * specified child Thread.
         */
        void bequeath(Thread child) {
            Entry e = new Entry(childValue(value), child);
            map.put(child, e);
        }

        Entry  next;	// Allows entries to be linked onto a list
    }
}
