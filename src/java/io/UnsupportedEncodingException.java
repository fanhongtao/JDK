/*
 * @(#)UnsupportedEncodingException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.io;

/**
 * The Character Encoding is not supported
 *
 * @author  Asmus Freytag
 * @version 1.9, 09/21/98
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
