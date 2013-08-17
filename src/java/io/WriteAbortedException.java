/*
 * @(#)WriteAbortedException.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.io;

/*
 *
 * @author  unascribed
 * @version 1.5, 12/10/01
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
