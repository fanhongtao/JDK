/*
 * @(#)MimeTypeParseException.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt.datatransfer;


/**
 *    A class to encapsulate MimeType parsing related exceptions
 */
class MimeTypeParseException extends Exception {

    /**
     * Constructs a MimeTypeParseException with no specified detail message. 
     */
    public MimeTypeParseException() {
     	super();
    }

    /**
     * Constructs a MimeTypeParseException with the specified detail message. 
     *
     * @param   s   the detail message.
     */
    public MimeTypeParseException(String s) {
        super(s);
    }

}
