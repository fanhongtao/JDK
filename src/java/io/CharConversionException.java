/*
 * @(#)CharConversionException.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.io;

/**
 * base class for character conversion exceptions
 * @author      Asmus Freytag
 * @version 	1.9, 11/29/01
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
