/*
 * @(#)ZipException.java	1.9 98/09/21
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

package java.util.zip;

/**
 * Signals that a Zip exception of some sort has occurred.
 *
 * @author  unascribed
 * @version 1.9 09/21/98
 * @see     java.io.IOExcpetion
 * @since   JDK1.0
 */

import java.io.IOException;

public
class ZipException extends IOException {
    /**
     * Constructs an <code>ZipException</code> with <code>null</code> 
     * as its error detail message. 
     */
    public ZipException() {
	super();
    }

    /**
     * Constructs an <code>ZipException</code> with the specified detail 
     * message.
     *
     * @param   s   the detail message.
     */

    public ZipException(String s) {
	super(s);
    }
}
