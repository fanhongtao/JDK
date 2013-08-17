/*
 * @(#)WriteAbortedException.java	1.8 98/06/29
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

/*
 *
 * @author  unascribed
 * @version 1.8, 06/29/98
 * @since   JDK1.1
 */
public class WriteAbortedException extends ObjectStreamException {
    /**
     * Exception that was caught while writing the ObjectStream.
     * @serial
     */
    public Exception detail;

    /**
     * Thrown during a read operation when one of the
     * ObjectStreamExceptions was thrown during a write operation.
     * The exception that terminated the write can be found in the detail
     * field. The stream is reset to it's initial state andd all references
     * to objects already deserialized are discarded.
     */
    public WriteAbortedException(String s, Exception ex) {
	super(s);
	detail = ex;
    }

    /**
     * Produce the message and include the message from the nested
     * exception, if there is one.
     */
    public String getMessage() {
	if (detail == null)
	    return super.getMessage();
	else
	    return super.getMessage() + "; " + detail.toString();
    }
}
