/*
 * @(#)UnsupportedEncodingException.java	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.io;

/**
 * The Character Encoding is not supported
 *
 * @author  Asmus Freytag
 * @version 1.10, 11/29/01
 * @since   JDK1.1
 */
public class UnsupportedEncodingException
    extends IOException
{
    /**
     * no detailed message
     */
    public UnsupportedEncodingException() {
        super();
    }
    /**
     * detailed message
     * @param s - detailed message
     */
    public UnsupportedEncodingException(String s) {
        super(s);
    }
}
