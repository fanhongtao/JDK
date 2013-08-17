/*
 * @(#)ZipException.java	1.14 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util.zip;

import java.io.IOException;

/**
 * Signals that a Zip exception of some sort has occurred.
 *
 * @author  unascribed
 * @version 1.14 12/03/01
 * @see     java.io.IOException
 * @since   JDK1.0
 */

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
