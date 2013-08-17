/*
 * @(#)CharConversionException.java	1.8 98/09/21
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
