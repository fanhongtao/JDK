/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * The CORBA <code>UNKNOWN</code> exception, which is thrown when there is
 * an error whose cause is unknown to the ORB.
 * It contains a minor code, which gives more detailed information about
 * what caused the exception, and a completion status. It may also contain
 * a string describing the exception.
 * <P>
 * See the section <A href="../../../../guide/idl/jidlExceptions.html#minorcodemeanings">Minor
 * Code Meanings</A> to see the minor codes for this exception.
 *
 * @see <A href="../../../../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 * @version     1.10 07 Nov 1996
 */

public final class UNKNOWN extends SystemException {
    /**
     * Constructs an <code>UNKNOWN</code> exception with a default minor code
     * of 0, a completion state of CompletionStatus.COMPLETED_NO,
     * and a null description.
     */
    public UNKNOWN() {
        this("");
    }

    /**
     * Constructs an <code>UNKNOWN</code> exception with the specified description message,
     * a minor code of 0, and a completion state of COMPLETED_NO.
     * @param s the String containing a detail message
     */
    public UNKNOWN(String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }

    /**
     * Constructs an <code>UNKNOWN</code> exception with the specified
     * minor code and completion status.
     * @param minor the minor code
     * @param completed the completion status
     */
    public UNKNOWN(int minor, CompletionStatus completed) {
        this("", minor, completed);
    }

    /**
     * Constructs an <code>UNKNOWN</code> exception with the specified description
     * message, minor code, and completion status.
     * @param s the String containing a description message
     * @param minor the minor code
     * @param completed the completion status
     */
    public UNKNOWN(String s, int minor, CompletionStatus completed) {
        super(s, minor, completed);
    }
}
