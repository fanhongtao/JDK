/*
 * @(#)Process.java	1.12 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import java.io.*;

/** 
 * The <code>exec</code> methods return an 
 * instance of a subclass of <code>Process</code> that can be used to 
 * control the process and obtain information about it. 
 * <p>
 * The subprocess is not killed when there are no more references to 
 * the <code>Process</code> object, but rather the subprocess 
 * continues executing asynchronously. 
 *
 * @author  unascribed
 * @version 1.12, 12/10/01
 * @see     java.lang.Runtime#exec(java.lang.String)
 * @see     java.lang.Runtime#exec(java.lang.String, java.lang.String[])
 * @see     java.lang.Runtime#exec(java.lang.String[])
 * @see     java.lang.Runtime#exec(java.lang.String[], java.lang.String[])
 * @since   JDK1.0
 */
public abstract class Process 
{
    /**
     * Gets the output stream of the subprocess.
     * This stream is usually buffered.
     *
     * @return  the output stream connected to the normal input of the
     *          subprocess.
     * @since   JDK1.0
     */
    abstract public OutputStream getOutputStream();

    /** 
     * Gets the input stream of the subprocess.
     * This stream is usually buffered.
     *
     * @return  the input stream connected to the normal output of the
     *          subprocess.
     * @since   JDK1.0
     */
    abstract public InputStream getInputStream();

    /**
     * Gets the error stream of the subprocess.
     * This stream is usually unbuffered.
     *
     * @return  the input stream connected to the error stream of the
     *          subprocess.
     * @since   JDK1.0
     */
    abstract public InputStream getErrorStream();

    /**
     * Waits for the subprocess to complete. This method returns 
     * immediately if the subprocess has already terminated. If the
     * subprocess has not yet terminated, the calling thread will be
     * blocked until the subprocess exits.
     *
     * @return     the exit value of the process.
     * @exception  InterruptedException  if the <code>waitFor</code> was
     *               interrupted.
     * @since      JDK1.0
     */
    abstract public int waitFor() throws InterruptedException;

    /**
     * Returns the exit value for the subprocess.
     *
     * @return  the exit value of the subprocess.
     * @exception  IllegalThreadStateException  if the subprocess has not yet
     *               terminated.
     * @since      JDK1.0
     */
    abstract public int exitValue();

    /**
     * Kills the subprocess. 
     *
     * @since   JDK1.0
     */
    abstract public void destroy();
}
