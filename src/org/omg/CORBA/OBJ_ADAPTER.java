/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * The CORBA <code>OBJ_ADAPTER</code> exception, which is thrown
 * by the object adapter on the server to indicate some error.
 * It contains a minor code, which gives more detailed information about
 * what caused the exception, and a completion status. It may also contain
 * a string describing the exception.
 * <P>
 * See the section <A href="../../../../guide/idl/jidlExceptions.html#minorcodemeanings">Minor
 * Code Meanings</A> to see the minor codes for this exception.
 *
 * @see <A href="../../../../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 * @version     1.17, 09/09/97
 * @since       JDK1.2
 */

public final class OBJ_ADAPTER extends SystemException {
    /**
     * Constructs an <code>OBJ_ADAPTER</code> exception with a default minor code
     * of 0, a completion state of CompletionStatus.COMPLETED_NO,
     * and a null description.
     */
    public OBJ_ADAPTER() {
        this("");
    }

    /**
     * Constructs an <code>OBJ_ADAPTER</code> exception with the specified description,
     * a minor code of 0, and a completion state of COMPLETED_NO.
     * @param s the String containing a description message
     */
    public OBJ_ADAPTER(String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }

    /**
     * Constructs an <code>OBJ_ADAPTER</code> exception with the specified
     * minor code and completion status.
     * @param minor the minor code
     * @param completed the completion status
     */
    public OBJ_ADAPTER(int minor, CompletionStatus completed) {
        this("", minor, completed);
    }

    /**
     * Constructs an <code>OBJ_ADAPTER</code> exception with the specified description
     * message, minor code, and completion status.
     * @param s the String containing a description message
     * @param minor the minor code
     * @param completed the completion status
     */
    public OBJ_ADAPTER(String s, int minor, CompletionStatus completed) {
        super(s, minor, completed);
    }
}
