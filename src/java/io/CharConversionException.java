/*
 * @(#)CharConversionException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.io;

/**
 * base class for character conversion exceptions
 * @author      Asmus Freytag
 * @version 	1.8, 09/21/98
 * @since       JDK1.1
 */
public class CharConversionException
    extends java.io.IOException
{
    /**
     * provides no detailed message
     */
    public CharConversionException() {
    }
    /**
     * provides a detailed message
     * @param s detailed message
     */
    public CharConversionException(String s) {
        super(s);
    }
}
