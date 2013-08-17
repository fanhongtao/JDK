/*
 * @(#)Process.java	1.10 97/01/22
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 * @version 1.10, 01/22/97
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
