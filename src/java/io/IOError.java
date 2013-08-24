/*
 * @(#)IOError.java	1.3 06/05/02
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Thrown when a serious I/O error has occurred. 
 *
 * @author  Xueming Shen
 * @version 1.3 05/02/06
 * @since   1.6
 */
public class IOError extends Error {
    /**
     * Constructs a new instance of IOError with the specified cause. The
     * IOError is created with the detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically
     * contains the class and detail message of cause).
     *
     * @param  cause
     *         The cause of this error, or <tt>null</tt> if the cause
     *         is not known
     */
    public IOError(Throwable cause) {
	super(cause);
    }

    private static final long serialVersionUID = 67100927991680413L;
}
