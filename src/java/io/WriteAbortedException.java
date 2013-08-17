/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/**
 * Signals that one of the ObjectStreamExceptions was thrown
 * during a write operation.
 *
 * @author  unascribed
 * @version 1.13, 02/06/02
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
     *
     * @param s   String describing the exception.
     * @param ex  Exception causing the abort.
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
