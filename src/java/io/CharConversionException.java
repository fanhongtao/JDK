/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.io;

/**
 * Base class for character conversion exceptions.
 *
 * @author      Asmus Freytag
 * @version 	1.13, 02/06/02
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
