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
 * @author  Josh Bloch
 * @version 1.11, 02/06/02
 * @see ThreadLocal
 * @since 1.2
 */

public class InheritableThreadLocal extends ThreadLocal {
    /**
     * Initial capacity of per-thread HashMap from ThreadLocal to value.
     * The size should be approximately twice the the number of inheritable
     * thread local variables that thread might have.
     */
    private static final int INITIAL_CAPACITY = 11;

    /**
     * Creates an InheritableThreadLocal variable.
     */
    public InheritableThreadLocal() {
    }

    /**
     * Returns the value in the calling thread's copy of this ThreadLocal
     * variable.  Creates and initializes the copy if this is the first time
     * the thread has called this method.
     */
    public Object get() {
	Thread ourThread = Thread.currentThread();
        Map map = ourThread.inheritableThreadLocals;
	Object value = map.get(key);
        if (value==null && !map.containsKey(key)) {
            if (map == Collections.EMPTY_MAP)
                map = ourThread.inheritableThreadLocals
                    = new HashMap(INITIAL_CAPACITY);
            value = initialValue();
            map.put(key, value);
        }
	return value;
    }

    /**
     * Sets the calling thread's instance of this ThreadLocal variable
     * to the given value.  This is only used to change the value from
     * the one assigned by the initialValue method, and many applications
     * will have no need for this functionality.
     *
     * @param value the value to be stored in the calling threads' copy of
     *	      this ThreadLocal.
     */
    public void set(Object value) {
	Thread ourThread = Thread.currentThread();
        Map map = ourThread.inheritableThreadLocals;
        if (map == Collections.EMPTY_MAP)
                map = ourThread.inheritableThreadLocals
                    = new HashMap(INITIAL_CAPACITY);
        map.put(key, value);
    }

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
     * Passes the ThreadLocal values represented by the specified list of
     * Entries onto the specified child Thread.  (The child's value is
     * computed from the parent's as per the childValue method.)  This
     * method is invoked (only) at Thread creation time, by Thread.init.
     */
    static void bequeath(Thread parent, Thread child) {
        Map parentMap = parent.inheritableThreadLocals;
        if (parentMap == Collections.EMPTY_MAP)
            return;
        Map childMap = child.inheritableThreadLocals
                     = new HashMap(INITIAL_CAPACITY);

        for (Iterator i=parentMap.entrySet().iterator(); i.hasNext(); ) {
          Map.Entry e = (Map.Entry) i.next();
          SecureKey k = (SecureKey) e.getKey();
          InheritableThreadLocal itl = (InheritableThreadLocal)k.threadLocal();
          childMap.put(k, itl.childValue(e.getValue()));
        }
    }
}
