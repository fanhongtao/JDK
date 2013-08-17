/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * The CORBA <code>MARSHAL</code> exception, which is thrown
 * when the ORB has a problem marshalling or unmarshalling parameters.
 * It contains a minor code, which gives more detailed information about
 * what caused the exception, and a completion status. It may also contain
 * a string describing the exception.
 * <P>
 * See the section <A href="../../../../guide/idl/jidlExceptions.html#minorcodemeanings">Minor
 * Code Meanings</A> to see the minor codes for this exception.
 *
 * @version     1.18, 09/09/97
 * @since       JDK1.2
 */

public final class MARSHAL extends SystemException {
    /**
     * Constructs a <code>MARSHAL</code> exception with a default minor code
     * of 0, a completion state of CompletionStatus.COMPLETED_NO,
     * and a null description.
     */
    public MARSHAL() {
        this("");
    }

    /**
     * Constructs a <code>MARSHAL</code> exception with the specified description message,
     * a minor code of 0, and a completion state of COMPLETED_NO.
     * @param s the String containing a description of the exception
     */
    public MARSHAL(String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }

    /**
     * Constructs a <code>MARSHAL</code> exception with the specified
     * minor code and completion status.
     * @param minor the minor code
     * @param completed the completion status
     */
    public MARSHAL(int minor, CompletionStatus completed) {
        this("", minor, completed);
    }

    /**
     * Constructs a <code>MARSHAL</code> exception with the specified description
     * message, minor code, and completion status.
     * @param s the String containing a description message
     * @param minor the minor code
     * @param completed the completion status
     */
    public MARSHAL(String s, int minor, CompletionStatus completed) {
        super(s, minor, completed);
    }
}
