/*
 * @(#)INV_POLICY.java	1.2 98/08/13
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

package org.omg.CORBA;

/**
 * The CORBA <code>INV_POLICY</code> standard exception, which is thrown
 * when an invocation cannot be made because of an incompatibility between 
 * Policy overrides.
 * It contains a minor code, which gives more detailed information about
 * what caused the exception, and a completion status. It may also contain
 * a string describing the exception.
 *
 * @see <A href="../../../../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 * @version     1.5 09/09/97
 */

public class INV_POLICY extends SystemException {
    /**
     * Constructs a <code>INV_POLICY</code> exception with a default minor code
     * of 0, a completion state of CompletionStatus.COMPLETED_NO,
     * and a null description.
     */
    public INV_POLICY() {
	this("");
    }

    /**
     * Constructs a <code>INV_POLICY</code> exception with the
     * specified description message,
     * a minor code of 0, and a completion state of COMPLETED_NO.
     * @param s the String containing a detail message
     */
    public INV_POLICY(String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }

    /**
     * Constructs a <code>INV_POLICY</code> exception with the specified
     * minor code and completion status.
     * @param minor the minor code
     * @param completed the completion status
     */
    public INV_POLICY(int minor, CompletionStatus completed) {
        this("", minor, completed);
    }

    /**
     * Constructs a <code>INV_POLICY</code> exception with the
     * specified description message, minor code, and completion status.
     * @param s the String containing a description message
     * @param minor the minor code
     * @param completed the completion status
     */
    public INV_POLICY(String s, int minor, CompletionStatus completed) {
        super(s, minor, completed);
    }
}
