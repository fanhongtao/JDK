/*
 * @(#)UnknownServiceException.java	1.8 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that an unknown service exception has 
 * occurred. Either the MIME type returned by a URL connection does 
 * not make sense, or the application is attempting to write to a 
 * read-only URL connection. 
 *
 * @author  unascribed
 * @version 1.8, 12/10/01
 * @since   JDK1.0
 */
public class UnknownServiceException extends IOException {
    /**
     * Constructs a new <code>UnknownServiceException</code> with no 
     * detail message. 
     *
     * @since   JDK1.0
     */
    public UnknownServiceException() {
    }

    /**
     * Constructs a new <code>UnknownServiceException</code> with the 
     * specified detail message. 
     *
     * @param   msg   the detail message.
     * @since   JDK1.0
     */
    public UnknownServiceException(String msg) {
	super(msg);
    }
}
