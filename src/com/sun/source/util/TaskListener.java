/*
 * @(#)TaskListener.java	1.3 06/02/23
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Use and Distribution is subject to the Java Research License available
 * at <http://wwws.sun.com/software/communitysource/jrl.html>.
 */

package com.sun.source.util;


/**
 * Provides a listener to monitor the activity of the Sun Java Compiler, javac.
 *
 * @author Jonathan Gibbons
 * @since 1.6
 */
public interface TaskListener
{
    public void started(TaskEvent e);

    public void finished(TaskEvent e);
}
