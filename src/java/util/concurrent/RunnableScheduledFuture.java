/*
 * @(#)RunnableScheduledFuture.java	1.3 06/01/30
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.concurrent;

/**
 * A {@link ScheduledFuture} that is {@link Runnable}. Successful
 * execution of the <tt>run</tt> method causes completion of the
 * <tt>Future</tt> and allows access to its results.
 * @see FutureTask
 * @see Executor
 * @since 1.6
 * @author Doug Lea
 * @param <V> The result type returned by this Future's <tt>get</tt> method
 */
public interface RunnableScheduledFuture<V> extends RunnableFuture<V>, ScheduledFuture<V> {

    /**
     * Returns true if this is a periodic task. A periodic task may
     * re-run according to some schedule. A non-periodic task can be
     * run only once.
     *
     * @return true if this task is periodic
     */
    boolean isPeriodic();
}
