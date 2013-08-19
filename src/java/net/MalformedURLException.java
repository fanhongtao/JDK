/*
 * @(#)MalformedURLException.java	1.15 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.net;

import java.io.IOException;

/**
 * Thrown to indicate that a malformed URL has occurred. Either no 
 * legal protocol could be found in a specification string or the 
 * string could not be parsed. 
 *
 * @author  Arthur van Hoff
 * @version 1.15, 01/23/03
 * @since   JDK1.0
 */
public class MalformedURLException extends IOException {
    /**
     * Constructs a <code>MalformedURLException</code> with no detail message.
     */
    public MalformedURLException() {
    }

    /**
     * Constructs a <code>MalformedURLException</code> with the 
     * specified detail message. 
     *
     * @param   msg   the detail message.
     */
    public MalformedURLException(String msg) {
	super(msg);
    }
}
