/*
 * @(#)ThreadPool.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Generic way to create a pool of threads.  Users define classes that implement
 * the Work interface for different kinds of tasks.
 *
 * Possible future improvements:
 *
 * Management of the pooled threads (further inactivity work, pool size, etc)
 *
 * Priority of work
 *
 * Debug messages
 */
public final class ThreadPool
{
    private static int threadCounter = 0; // serial counter useful for debugging

    private WorkQueue workToDo = new WorkQueue();
    private int availableWorkers = 0;
    private int inactivityTimeout;

    public ThreadPool() {
        inactivityTimeout = ORBConstants.DEFAULT_INACTIVITY_TIMEOUT;
    }

    public ThreadPool(int inactivityTimeout) {
        this.inactivityTimeout = inactivityTimeout;
    }

    private static synchronized int getUniqueThreadId() {
        return ThreadPool.threadCounter++;
    }

    /*
     * Note that the methods are not synchronized since they are
     * only used in the requestWork and addWork methods which
     * are already synchronized.
     */
    private static final class WorkQueue
    {
        private LinkedList workToDo = new LinkedList();

        final void enqueue(Work work)
        {
            workToDo.add(work);
        }

        final Work dequeue() throws NoSuchElementException
        {
            return (Work)workToDo.removeFirst();
        }

        final boolean isEmpty()
        {
            return workToDo.isEmpty();
        }
    }

    /**
     * Generic thread in the pool.  Simply requests and
     * processes Work instances.  The while(true) loop
     * could be replaced by something that checks for a
     * shutdown value set by the ThreadPool.
     */
    private class PooledThread extends Thread
    {
        private Work currentWork;
        private int threadId = 0; // unique id for the thread

        PooledThread() {
            this.threadId = ThreadPool.getUniqueThreadId();
        }

        public void run()
        {
            while (true) {
                try {

                    // Set the name to Idle for debugging
                    setName("Idle");

                    // Get some work to do
                    currentWork = requestWork();

                    // Set the name to the specific type
                    // of work for debugging.
                    StringBuffer buff = new StringBuffer();
                    buff.append(currentWork.getName());
                    buff.append('[');
                    buff.append(this.threadId);
                    buff.append(']');
                    setName(buff.toString());

                    // Do the work
                    currentWork.process();

                } catch (TimeoutException e) {
                    // This thread timed out waiting for something to do,
                    // so it can exit.
                    return;

                } catch (Exception e) {

                    // Ignore any exceptions that currentWork.process
                    // accidently lets through, but let Errors pass.
                    // Add debugging output?  REVISIT

                    // Note that InterruptedExceptions are
                    // caught here.  Thus, threads can be forced out of
                    // requestWork and so they have to reacquire the lock.
                    // Other options include ignoring or
                    // letting this thread die.
                }
            }
        }
    }

    /**
     * Called by a PooledThread to get the next appropriate
     * Work.
     */
    private synchronized Work requestWork()
        throws TimeoutException, InterruptedException
    {
        availableWorkers++;

        try {

            if (workToDo.isEmpty()) {

                this.wait(inactivityTimeout);

                // Did we time out?
                if (workToDo.isEmpty())
                    throw new TimeoutException();
            }

        } finally {

            availableWorkers--;
        }

        return workToDo.dequeue();
    }

    /**
     * Used by classes which access the ThreadPool to add new
     * tasks.
     */
    public synchronized void addWork(Work work)
    {
        workToDo.enqueue(work);

        if (availableWorkers == 0) {

            PooledThread thread = new PooledThread();

            // The thread must be set to a daemon thread so the
            // VM can exit if the only threads left are PooledThreads
            // or other daemons.  We don't want to rely on the
            // calling thread always being a daemon.

            // Catch exceptions since setDaemon can cause a
            // security exception to be thrown under netscape
            // in the Applet mode
            try {
                thread.setDaemon(true);
            } catch (Exception e) {}

            thread.start();
        } else
            this.notify();
    }

    private static class TimeoutException extends Exception {}
}
