/*
 * @(#)CharConversionException.java	1.12 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package java.io;

/**
 * Base class for character conversion exceptions.
 *
 * @author      Asmus Freytag
 * @version 	1.12, 02/02/00
 * @since       JDK1.1
 */
public class CharConversionException
    extends java.io.IOException
{
    /**
     * This provides no detailed message.
     */
    public CharConversionException() {
    }
    /**
     * This provides a detailed message.
     *
     * @param s the detailed message associated with the exception.
     */
    public CharConversionException(String s) {
        super(s);
    }
}
