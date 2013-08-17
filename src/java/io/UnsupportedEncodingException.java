/*
 * @(#)UnsupportedEncodingException.java	1.13 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.io;

/**
 * The Character Encoding is not supported.
 *
 * @author  Asmus Freytag
 * @version 1.13, 02/02/00
 * @since   JDK1.1
 */
public class UnsupportedEncodingException
    extends IOException
{
    /**
     * Constructs an UnsupportedEncodingException without a detail message.
     */
    public UnsupportedEncodingException() {
        super();
    }

    /**
     * Constructs an UnsupportedEncodingException with a detail message.
     * @param s Describes the reason for the exception.
     */
    public UnsupportedEncodingException(String s) {
        super(s);
    }
}
