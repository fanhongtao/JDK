/*
 * @(#)ThreadLocal.java	1.16 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
 * 
 * <p>Each thread holds an implicit reference to its copy of a ThreadLocal
 * as long as the thread is alive and the ThreadLocal object is accessible;
 * after a thread goes away, all of its copies of ThreadLocal variables are
 * subject to garbage collection (unless other references to these copies
 * exist).
 *
 * @author  Josh Bloch
 * @version 1.16, 02/02/00
 * @since   1.2
 */

public class ThreadLocal {
    /**
     * Initial capacity of per-thread HashMap from ThreadLocal to value.
     * The size should be approximately twice the the number of thread local
     * variables that thread might have.
     */
    private static final int INITIAL_CAPACITY = 11;

    /**
     * Creates a ThreadLocal variable.
     */
    public ThreadLocal() {
        key = new SecureKey();
    }

    /**
     * Secure key representing this thread local variable.  This key is used
     * in preference to the ThreadLocal object itself, to map this variable to
     * its per-thread value, to prevent an attacker from overriding the equals
     * method to spoof another ThreadLocal and steal its value.
     */
    Object key;

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
     *
     * @return the initial value for this ThreadLocal
     */
    protected Object initialValue() {
	return null;
    }

    /**
     * Returns the value in the calling thread's copy of this ThreadLocal
     * variable.  Creates and initializes the copy if this is the first time
     * the thread has called this method.
     *
     * @return the value of this ThreadLocal
     */
    public Object get() {
	Thread ourThread = Thread.currentThread();
        Map map = ourThread.threadLocals;
	Object value = map.get(key);
        if (value==null && !map.containsKey(key)) {
            if (map == Collections.EMPTY_MAP)
                map = ourThread.threadLocals = new HashMap(INITIAL_CAPACITY);
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
        Map map = ourThread.threadLocals;
        if (map == Collections.EMPTY_MAP)
            map = ourThread.threadLocals = new HashMap(INITIAL_CAPACITY);
        map.put(key, value);
    }

    /**
     * An object of this class is used in place of the ThreadLocal itself
     * as a key in the per-Thread ThreadLocal->value Map.  This prevents
     * a "spoof attack" where one ThreadLocal pretends to be another
     * by overriding the equals (and hashCode) method and returning true
     * when it should return false.
     *
     * The sole method (which returns the backing ThreadLocal object)
     * is used solely by InheritableThreadLocal, so that the
     * bequeath method can invoke the relevant childValue method.  Arguably
     * this inner class belongs in InheritableThreadLocal, but putting it
     * there would complicate implementation.
     */
    class SecureKey{
        ThreadLocal threadLocal() {
            return ThreadLocal.this;
        }
    }
}
