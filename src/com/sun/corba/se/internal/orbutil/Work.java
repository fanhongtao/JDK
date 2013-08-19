/*
 * @(#)Work.java	1.3 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.orbutil;

/**
 * Defines the methods necessary for a subclass to
 * be processed by a thread in a ThreadPool.  Implementing
 * classes define the actual work to do.
 */
public interface Work
{
    /**
     * Get the name for this type of work.  The thread that
     * does this type of Work will set it's name to what is
     * returned by this method.  This is only useful for
     * debugging (so you can easily see what type of thread
     * it is).
     */
    String getName();

    /**
     * Do the actual work.  This should perform all its own
     * error handling.  Any Exceptions that escape will be
     * ignored.
     */
    void process();
}
