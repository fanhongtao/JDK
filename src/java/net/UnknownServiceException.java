/*
 * @(#)UnknownServiceException.java	1.7 98/07/01
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

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that an unknown service exception has 
 * occurred. Either the MIME type returned by a URL connection does 
 * not make sense, or the application is attempting to write to a 
 * read-only URL connection. 
 *
 * @author  unascribed
 * @version 1.7, 07/01/98
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
