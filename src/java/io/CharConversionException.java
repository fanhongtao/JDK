/*
 * @(#)CharConversionException.java	1.7 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.io;

/**
 * base class for character conversion exceptions
 * @author      Asmus Freytag
 * @version 	1.7, 12/10/01
 * @since       JDK1.1
 */
public class CharConversionException
    extends java.io.IOException
{
    /**
     * provides no detailed message
     * @since   JDK1.1
     */
    public CharConversionException() {
    }
    /**
     * provides a detailed message
     * @param s detailed message
     * @since   JDK1.1
     */
    public CharConversionException(String s) {
        super(s);
    }
}
