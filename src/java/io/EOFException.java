/*
 * @(#)EOFException.java	1.3 97/01/22
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
 * Signals that an end of file or end of stream has been reached 
 * unexpectedly during input. 
 * <p>
 * This exception is mainly used by data input streams, which 
 * generally expect a binary file in a specific format, and for which 
 * an end of stream is an unusual condition. Most other input streams 
 * return a special value on end of stream. 
 *
 * @author  Frank Yellin
 * @version 1.3, 01/22/97
 * @see     java.io.DataInputStream
 * @see     java.io.IOException
 * @since   JDK1.0
 */
public
class EOFException extends IOException {
    /**
     * Constructs an <code>EOFException</code> with no detail message. 
     *
     * @since   JDK1.0
     */
    public EOFException() {
	super();
    }

    /**
     * Constructs an <code>EOFException</code> with the specified detail
     * message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public EOFException(String s) {
	super(s);
    }
}
