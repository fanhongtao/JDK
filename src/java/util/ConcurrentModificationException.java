/*
 * @(#)ConcurrentModificationException.java	1.11 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.util;

/**
 * This exception may be thrown by methods that have detected concurrent
 * modification of a backing object when such modification is not permissible.
 * <p>
 * For example, it is not permssible for one thread to modify a Collection
 * while another thread is iterating over it.  In general, the results of the
 * iteration are undefined under these circumstances.  Some Iterator
 * implementations (including those of all the collection implementations
 * provided by the SDK) may choose to throw this exception if this behavior is
 * detected.  Iterators that do this are known as <i>fail-fast</i> iterators,
 * as they fail quickly and cleanly, rather that risking arbitrary,
 * non-deterministic behavior at an undetermined time in the future.
 *
 * @author  Josh Bloch
 * @version 1.11, 02/02/00
 * @see	    Collection
 * @see     Iterator
 * @see     ListIterator
 * @see	    Vector
 * @see	    LinkedList
 * @see	    HashSet
 * @see	    Hashtable
 * @see	    TreeMap
 * @see	    AbstractList
 * @since   1.2
 */
public class ConcurrentModificationException extends RuntimeException {
    /**
     * Constructs a ConcurrentModificationException with no
     * detail message.
     */
    public ConcurrentModificationException() {
    }

    /**
     * Constructs a <tt>ConcurrentModificationException</tt> with the
     * specified detail message.
     *
     * @param message the detail message pertaining to this exception.
     */
    public ConcurrentModificationException(String message) {
	super(message);
    }
}
