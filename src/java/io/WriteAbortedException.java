/*
 * @(#)WriteAbortedException.java	1.4 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
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

/*
 *
 * @author  unascribed
 * @version 1.4, 07/01/98
 * @since   JDK1.1
 */
public class WriteAbortedException extends ObjectStreamException {
    /*
     * @since   JDK1.1
     */
    public Exception detail;

    /**
     * A WriteAbortedException is thrown during a read when one of the
     * ObjectStreamExceptions was thrown during writing.  The exception
     * that terminated the write can be found in the detail field.
     * The stream is reset to it's initial state, all references to
     * objects already deserialized are discarded.
     * @since   JDK1.1
     */
    public WriteAbortedException(String s, Exception ex) { 
	super(s); 
	detail = ex;
    }

    /**
     * Produce the message, include the message from the nested
     * exception if there is one.
     * @since   JDK1.1
     */
    public String getMessage() {
	if (detail == null) 
	    return super.getMessage();
	else
	    return super.getMessage() + "; " + detail.toString();
    }
}
