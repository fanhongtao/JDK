/*
 * @(#)LayoutQueue.java	1.5 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.util.Vector;

/**
 * A queue of text layout tasks. 
 *
 * @author  Timothy Prinzing
 * @version 1.5 01/23/03
 * @see     AsyncBoxView
 * @since   1.3 
 */
public class LayoutQueue {

    Vector tasks;
    Thread worker;

    static LayoutQueue defaultQueue;

    /**
     * Construct a layout queue.
     */
    public LayoutQueue() {
	tasks = new Vector();
    }

    /**
     * Fetch the default layout queue.
     */
    public static LayoutQueue getDefaultQueue() {
	if (defaultQueue == null) {
	    defaultQueue = new LayoutQueue();
	}
	return defaultQueue;
    }

    /**
     * Set the default layout queue.
     *
     * @param q the new queue.
     */
    public static void setDefaultQueue(LayoutQueue q) {
	defaultQueue = q;
    }

    /**
     * Add a task that is not needed immediately because
     * the results are not believed to be visible.
     */
    public synchronized void addTask(Runnable task) {
	if (worker == null) {
	    worker = new LayoutThread();
	    worker.start();
	}
	tasks.addElement(task);
	notifyAll();
    }

    /**
     * Used by the worker thread to get a new task to execute
     */
    protected synchronized Runnable waitForWork() {
	while (tasks.size() == 0) {
	    try {
		wait();
	    } catch (InterruptedException ie) {
		return null;
	    }
	}
	Runnable work = (Runnable) tasks.firstElement();
	tasks.removeElementAt(0);
	return work;
    }

    /**
     * low priority thread to perform layout work forever
     */
    class LayoutThread extends Thread {
	
	LayoutThread() {
	    super("text-layout");
	    setPriority(Thread.MIN_PRIORITY);
	}
	
        public void run() {
	    Runnable work;
	    do {
		work = waitForWork();
		if (work != null) {
		    work.run();
		}
	    } while (work != null);
	}


    }

}
