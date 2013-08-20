/*
 * @(#)CopyOnWriteArraySet.java	1.7 04/06/11
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.concurrent;
import java.util.*;

/**
 * A {@link java.util.Set} that uses {@link
 * java.util.concurrent.CopyOnWriteArrayList} for all of its
 * operations.  Thus, it shares the same basic properties:
 * <ul>
 *  <li>It is best suited for applications in which set sizes generally
 *       stay small, read-only operations
 *       vastly outnumber mutative operations, and you need
 *       to prevent interference among threads during traversal.
 *  <li>It is thread-safe.
 *  <li>Mutative operations(add, set, remove, etc) are expensive
 *      since they usually entail copying the entire underlying array.
 *  <li>Iterators do not support the mutative remove operation.
 *  <li>Traversal via iterators is fast and cannot encounter
 *      interference from other threads. Iterators rely on
 *      unchanging snapshots of the array at the time the iterators were
 *     constructed.
 * </ul>
 *
 * <p> <b>Sample Usage.</b> The following code sketch uses a
 * copy-on-write set to maintain a set of Handler objects that 
 * perform some action upon state updates. 
 *
 * <pre>
 * class Handler { void handle(); ... }
 *
 * class X {
 *    private final CopyOnWriteArraySet&lt;Handler&gt; handlers = new CopyOnWriteArraySet&lt;Handler&gt;();
 *    public void addHandler(Handler h) { handlers.add(h); }
 *
 *    private long internalState;
 *    private synchronized void changeState() { internalState = ...; }
 *
 *    public void update() {
 *       changeState();
 *       for (Handler handler : handlers)
 *           handler.handle();
 *    }
 * }
 * </pre>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @see CopyOnWriteArrayList
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public class CopyOnWriteArraySet<E> extends AbstractSet<E>
        implements java.io.Serializable {
    private static final long serialVersionUID = 5457747651344034263L;

    private final CopyOnWriteArrayList<E> al;

    /**
     * Creates an empty set.
     */
    public CopyOnWriteArraySet() {
        al = new CopyOnWriteArrayList<E>();
    }

    /**
     * Creates a set containing all of the elements of the specified
     * Collection.
     * @param c the collection
     */
    public CopyOnWriteArraySet(Collection<? extends E> c) {
        al = new CopyOnWriteArrayList<E>();
        al.addAllAbsent(c);
    }


    public int      size()                    { return al.size(); }
    public boolean  isEmpty()                 { return al.isEmpty(); }
    public boolean  contains(Object o)        { return al.contains(o); }
    public Object[] toArray()                 { return al.toArray(); }
    public <T> T[]  toArray(T[] a)            { return al.toArray(a); }
    public void     clear()                   {        al.clear(); }
    public Iterator<E>  iterator()            { return al.iterator(); }
    public boolean  remove(Object o)          { return al.remove(o); }
    public boolean  add(E o)                  { return al.addIfAbsent(o); }
    public boolean  containsAll(Collection<?> c)      { return al.containsAll(c); }
    public boolean  addAll(Collection<? extends E> c) { return al.addAllAbsent(c) > 0; }
    public boolean  removeAll(Collection<?> c)        { return al.removeAll(c); }
    public boolean  retainAll(Collection<?> c)        { return al.retainAll(c); }

}
