/*
 * @(#)ThreadDeath.java	1.8 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.lang;

/**
 * An instance of <code>ThreadDeath</code> is thrown in the victim 
 * thread when the <code>stop</code> method with zero arguments in 
 * class <code>Thread</code> is called. 
 * <p>
 * An application should catch instances of this class only if it 
 * must clean up after being terminated asynchronously. If 
 * <code>ThreadDeath</code> is caught by a method, it is important 
 * that it be rethrown so that the thread actually dies. 
 * <p>
 * The top-level error handler does not print out a message if 
 * <code>ThreadDeath</code> is never caught. 
 * <p>
 * The class <code>ThreadDeath</code> is specifically a subclass of 
 * <code>Error</code> rather than <code>Exception</code>, even though 
 * it is a "normal occurrence", because many applications 
 * catch all occurrences of <code>Exception</code> and then discard 
 * the exception. 
 *
 * @author unascribed
 * @version 1.8, 07/01/98
 * @see     java.lang.Thread#stop()
 * @since   JDK1.0
 */

public class ThreadDeath extends Error {}
