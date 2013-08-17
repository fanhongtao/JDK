/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;
import java.util.*;

/**
 * This class extends ThreadLocal to provide inheritance of values from parent
 * Thread to child Thread: when a child thread is created, the child receives
 * initial values for all InheritableThreadLocals for which the parent has
 * values.  Normally the child's values will be identical to the parent's;
 * however, the child's value can be made an arbitrary function of the parent's
 * by overriding the childValue method in this class.
 * 
 * <p>InheritableThreadLocal variables are used in preference to ordinary
 * ThreadLocal variables when the per-thread-attribute being maintained in the
 * variable (e.g., User ID, Transaction ID) must be automatically transmitted
 * to any child threads that are created.
 *
 * @author  Josh Bloch and Doug Lea
 * @version 1.12, 05/29/02
 * @see ThreadLocal
 * @since 1.2
 */

public class InheritableThreadLocal extends ThreadLocal {
    /**
     * Computes the child's initial value for this InheritableThreadLocal
     * as a function of the parent's value at the time the child Thread is
     * created.  This method is called from within the parent thread before
     * the child is started.
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     *
     * @param parentValue the parent thread's value
     * @return the child thread's initial value
     */
    protected Object childValue(Object parentValue) {
        return parentValue;
    }

    /**
     * Get the map associated with a ThreadLocal.
     *
     * @param t the current thread
     */
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal.
     *
     * @param t the current thread
     * @param firstValue value for the initial entry of the table.
     * @param map the map to store.
     */
    void createMap(Thread t, Object firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }

    /**
     * Returns the value in the calling thread's copy of this ThreadLocal
     * variable.  Creates and initializes the copy if this is the first time
     * the thread has called this method.
     */
    public Object get() {
        return super.get();
    }

    /**
     * Sets the calling thread's instance of this ThreadLocal variable
     * to the given value.  This is only used to change the value from
     * the one assigned by the initialValue method, and many applications
     * will have no need for this functionality.
     *
     * @param value the value to be stored in the calling threads' copy of
     *        this ThreadLocal.
     */
    public void set(Object value) {
        super.set(value);
    }
}
