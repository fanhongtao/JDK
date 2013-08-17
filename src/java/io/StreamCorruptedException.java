/*
 * @(#)StreamCorruptedException.java	1.5 98/07/01
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

/**
 * Raised when control information read from an object stream
 * violates internal consistency checks.
 *
 * @author  unascribed
 * @version 1.5, 07/01/98
 * @since   JDK1.1
 */
public class StreamCorruptedException extends ObjectStreamException {
    /**
     * Create a StreamCorruptedException with a reason.
     * @since   JDK1.1
     */
    public StreamCorruptedException(String reason) {
	super(reason);
    }

    /**
     * Create a StreamCorruptedException with no reason.
     * @since   JDK1.1
     */
    public StreamCorruptedException() {
	super();
    }
}
