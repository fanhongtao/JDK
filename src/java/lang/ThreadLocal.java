/*
 * @(#)ThreadLocal.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;
import java.util.*;

/**
 * This class provides ThreadLocal variables.  These variables differ from
 * their normal counterparts in that each thread that accesses one (via its
 * get or set method) has its own, independently initialized copy of the
 * variable.  ThreadLocal objects are typically private static variables in
 * classes that wish to associate state with a thread (e.g., a user ID or
 * Transaction ID).
 * <p>
 * Each thread holds an implicit reference to its copy of a ThreadLocal
 * as long as the thread is alive and the ThreadLocal object is accessible;
 * after a thread goes away, all of its copies of ThreadLocal variables are
 * subject to garbage collection (unless other references to these copies
 * exist).
 *
 * @author  Josh Bloch
 * @version 1.9 11/29/01
 * @since   JDK1.2
 */

public class ThreadLocal {
    /**
     * Maps each Thread that has a value for this ThreadLocal to an Entry
     * containing its value.  The initial size is 53 because that is twice
     * the maximum concurrency that one might reasonably expect.
     */
    Map map = Collections.synchronizedMap(new WeakHashMap(53));

    /**
     * Creates a ThreadLocal variable.
     */
    public ThreadLocal() {
    }

    /**
     * Returns the calling thread's initial value for this ThreadLocal
     * variable. This method will be called once per accessing thread for
     * each ThreadLocal, the first time each thread accesses the variable
     * with get or set.  If the programmer desires ThreadLocal variables
     * to be initialized to some value other than null, ThreadLocal must
     * be subclassed, and this method overridden.  Typically, an anonymous
     * inner class will be used.  Typical implementations of initialValue
     * will call an appropriate constructor and return the newly constructed
     * object.
     */
    protected Object initialValue() {
	return null;
    }

    /**
     * Returns the value in the calling thread's copy of this ThreadLocal
     * variable.  Creates and initializes the copy if this is the first time
     * the thread has called this method.
     */
    public Object get() {
	Entry ourEntry = ourEntry(true);
	return ourEntry.value;
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
	Entry ourEntry = ourEntry(false);
        ourEntry.value = value;
    }

    /**
     * Returns the calling thread's Entry for this ThreadLocal, creating a new
     * one and putting it in the map if none exists.  If initUponCreate is
     * true, this call will use initialValue() to set the value in a newly
     * created Entry; otherwise, it will set the value in a newly created
     * Entry to null.
     */
    private Entry ourEntry(boolean initUponCreate) {
	Thread ourThread = Thread.currentThread();
	Entry ourEntry = (Entry)(map.get(ourThread));
	if (ourEntry == null) {
            ourEntry = newEntry(initUponCreate ? initialValue() : null);
	    map.put(ourThread, ourEntry);
        }
        return ourEntry;
    }

    /**
     * This factory method is overriden by InheritableThreadLocal to
     * specialize behavior.
     */
    Entry newEntry(Object value) {
        return new Entry(value);
    }

    /**
     * The value associated with a (ThreadLocal, Thread) pair.
     */
    static class Entry {
        Entry(Object value) {this.value = value;}

        Object value;
    }
}
