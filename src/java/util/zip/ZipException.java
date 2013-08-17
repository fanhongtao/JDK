/*
 * @(#)ZipException.java	1.2 00/01/12
 *
 * Copyright 1995-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
