/*
 * @(#)BlockingQueue.java	1.8 04/07/12
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.concurrent;

import java.util.Collection;
import java.util.Queue;

/**
 * A {@link java.util.Queue} that additionally supports operations
 * that wait for the queue to become non-empty when retrieving an element,
 * and wait for space to become available in the queue when storing an 
 * element.
 *
 * <p>A <tt>BlockingQueue</tt> does not accept <tt>null</tt> elements.
 * Implementations throw <tt>NullPointerException</tt> on attempts
 * to <tt>add</tt>, <tt>put</tt> or <tt>offer</tt> a <tt>null</tt>.  A
 * <tt>null</tt> is used as a sentinel value to indicate failure of
 * <tt>poll</tt> operations.
 *
 * <p>A <tt>BlockingQueue</tt> may be capacity bounded. At any given
 * time it may have a <tt>remainingCapacity</tt> beyond which no
 * additional elements can be <tt>put</tt> without blocking.
 * A <tt>BlockingQueue</tt> without any intrinsic capacity constraints always
 * reports a remaining capacity of <tt>Integer.MAX_VALUE</tt>.
 *
 * <p> <tt>BlockingQueue</tt> implementations are designed to be used
 * primarily for producer-consumer queues, but additionally support
 * the {@link java.util.Collection} interface.  So, for example, it is
 * possible to remove an arbitrary element from a queue using
 * <tt>remove(x)</tt>. However, such operations are in general
 * <em>not</em> performed very efficiently, and are intended for only
 * occasional use, such as when a queued message is cancelled.
 *
 * <p> <tt>BlockingQueue</tt> implementations are thread-safe.  All
 * queuing methods achieve their effects atomically using internal
 * locks or other forms of concurrency control. However, the
 * <em>bulk</em> Collection operations <tt>addAll</tt>,
 * <tt>containsAll</tt>, <tt>retainAll</tt> and <tt>removeAll</tt> are
 * <em>not</em> necessarily performed atomically unless specified
 * otherwise in an implementation. So it is possible, for example, for
 * <tt>addAll(c)</tt> to fail (throwing an exception) after adding
 * only some of the elements in <tt>c</tt>.
 *
 * <p>A <tt>BlockingQueue</tt> does <em>not</em> intrinsically support
 * any kind of &quot;close&quot; or &quot;shutdown&quot; operation to
 * indicate that no more items will be added.  The needs and usage of
 * such features tend to be implementation-dependent. For example, a
 * common tactic is for producers to insert special
 * <em>end-of-stream</em> or <em>poison</em> objects, that are
 * interpreted accordingly when taken by consumers.
 *
 * <p>
 * Usage example, based on a typical producer-consumer scenario.
 * Note that a <tt>BlockingQueue</tt> can safely be used with multiple
 * producers and multiple consumers.
 * <pre>
 * class Producer implements Runnable {
 *   private final BlockingQueue queue;
 *   Producer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while(true) { queue.put(produce()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   Object produce() { ... }
 * }
 *
 * class Consumer implements Runnable {
 *   private final BlockingQueue queue;
 *   Consumer(BlockingQueue q) { queue = q; }
 *   public void run() {
 *     try {
 *       while(true) { consume(queue.take()); }
 *     } catch (InterruptedException ex) { ... handle ...}
 *   }
 *   void consume(Object x) { ... }
 * }
 *
 * class Setup {
 *   void main() {
 *     BlockingQueue q = new SomeQueueImplementation();
 *     Producer p = new Producer(q);
 *     Consumer c1 = new Consumer(q);
 *     Consumer c2 = new Consumer(q);
 *     new Thread(p).start();
 *     new Thread(c1).start();
 *     new Thread(c2).start();
 *   }
 * }
 * </pre>
 *
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *  
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public interface BlockingQueue<E> extends Queue<E> {

    /**
     * Inserts the specified element into this queue, if possible.  When
     * using queues that may impose insertion restrictions (for
     * example capacity bounds), method <tt>offer</tt> is generally
     * preferable to method {@link Collection#add}, which can fail to
     * insert an element only by throwing an exception.
     *
     * @param o the element to add.
     * @return <tt>true</tt> if it was possible to add the element to
     *         this queue, else <tt>false</tt>
     * @throws NullPointerException if the specified element is <tt>null</tt>
     */
    boolean offer(E o);
    
    /**
     * Inserts the specified element into this queue, waiting if necessary
     * up to the specified wait time for space to become available.
     * @param o the element to add
     * @param timeout how long to wait before giving up, in units of
     * <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     * <tt>timeout</tt> parameter
     * @return <tt>true</tt> if successful, or <tt>false</tt> if
     * the specified waiting time elapses before space is available.
     * @throws InterruptedException if interrupted while waiting.
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    boolean offer(E o, long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Retrieves and removes the head of this queue, waiting
     * if necessary up to the specified wait time if no elements are
     * present on this queue.
     * @param timeout how long to wait before giving up, in units of
     * <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     * <tt>timeout</tt> parameter
     * @return the head of this queue, or <tt>null</tt> if the
     * specified waiting time elapses before an element is present.
     * @throws InterruptedException if interrupted while waiting.
     */
    E poll(long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * Retrieves and removes the head of this queue, waiting
     * if no elements are present on this queue.
     * @return the head of this queue
     * @throws InterruptedException if interrupted while waiting.
     */
    E take() throws InterruptedException;

    /**
     * Adds the specified element to this queue, waiting if necessary for
     * space to become available.
     * @param o the element to add
     * @throws InterruptedException if interrupted while waiting.
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    void put(E o) throws InterruptedException;

    /**
     * Returns the number of elements that this queue can ideally (in
     * the absence of memory or resource constraints) accept without
     * blocking, or <tt>Integer.MAX_VALUE</tt> if there is no
     * intrinsic limit.
     * <p>Note that you <em>cannot</em> always tell if
     * an attempt to <tt>add</tt> an element will succeed by
     * inspecting <tt>remainingCapacity</tt> because it may be the
     * case that another thread is about to <tt>put</tt> or <tt>take</tt> an
     * element.
     * @return the remaining capacity
     */
    int remainingCapacity();

    /**
     * Adds the specified element to this queue if it is possible to
     * do so immediately, returning <tt>true</tt> upon success, else
     * throwing an IllegalStateException.  
     * @param o the element
     * @return <tt>true</tt> (as per the general contract of
     *         <tt>Collection.add</tt>).
     *
     * @throws NullPointerException if the specified element is <tt>null</tt>
     * @throws IllegalStateException if element cannot be added
     */
    boolean add(E o);

    /**
     * Removes all available elements from this queue and adds them
     * into the given collection.  This operation may be more
     * efficient than repeatedly polling this queue.  A failure
     * encountered while attempting to <tt>add</tt> elements to
     * collection <tt>c</tt> may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown. Attempts to drain a queue to itself result in
     * <tt>IllegalArgumentException</tt>. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * @param c the collection to transfer elements into
     * @return the number of elements transferred.
     * @throws NullPointerException if c is null
     * @throws IllegalArgumentException if c is this queue
     * 
     */
    int drainTo(Collection<? super E> c);
    
    /**
     * Removes at most the given number of available elements from
     * this queue and adds them into the given collection.  A failure
     * encountered while attempting to <tt>add</tt> elements to
     * collection <tt>c</tt> may result in elements being in neither,
     * either or both collections when the associated exception is
     * thrown. Attempts to drain a queue to itself result in
     * <tt>IllegalArgumentException</tt>. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     * @param c the collection to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     * @return the number of elements transferred.
     * @throws NullPointerException if c is null
     * @throws IllegalArgumentException if c is this queue
     */
    int drainTo(Collection<? super E> c, int maxElements);
}
