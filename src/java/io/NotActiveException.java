/*
 * @(#)NotActiveException.java	1.6 98/07/01
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
 * The NotActiveException is thrown when serialization or deserialization
 * is not active.
 *
 * @author  unascribed
 * @version 1.6, 07/01/98
 * @since   JDK1.1
 */
public class NotActiveException extends ObjectStreamException {
    /**
     * Constructor to create a new NotActiveException with the reason given.
     * @since   JDK1.1
     */
    public NotActiveException(String reason) {
	super(reason);
    }

    /**
     * Constructor to create a new NotActiveException with no reason.
     * @since   JDK1.1
     */
    public NotActiveException() {
	super();
    }
}
