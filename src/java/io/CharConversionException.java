/*
 * @(#)CharConversionException.java	1.5 97/01/22
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.io;

/**
 * base class for character conversion exceptions
 * @author      Asmus Freytag
 * @version 	1.5, 01/22/97
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
