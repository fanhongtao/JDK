/*
 * @(#)WriteAbortedException.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
