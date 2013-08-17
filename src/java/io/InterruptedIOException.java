/*
 * @(#)InterruptedIOException.java	1.7 97/02/10
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
 * Signals that an I/O operation has been interrupted. 
 *
 * @author  unascribed
 * @version 1.7, 02/10/97
 * @see     java.io.InputStream
 * @see     java.io.OutputStream
 * @see     java.lang.Thread#interrupt()
 * @since   JDK1.0
 */
public
class InterruptedIOException extends IOException {
    /**
     * Constructs an <code>InterruptedIOException</code> with no detail 
     * message. 
     *
     * @since   JDK1.0
     */
    public InterruptedIOException() {
	super();
    }

    /**
     * Constructs an <code>InterruptedIOException</code> with the 
     * specified detail message. 
     *
     * @param   s   the detail message.
     * @since   JDK1.0
     */
    public InterruptedIOException(String s) {
	super(s);
    }

    /**
     * Reports how many bytes had been transferred as part of the I/O 
     * operation before it was interrupted. 
     *
     * @since   JDK1.0
     */ 
    public int bytesTransferred = 0;
}
