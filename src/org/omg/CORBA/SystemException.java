/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import java.util.*;

/**
 * The root class for all CORBA standard exceptions. These exceptions
 * may be thrown as a result of any CORBA operation invocation and may
 * also be returned by many standard CORBA API methods. The standard
 * exceptions contain a minor code, allowing more detailed specification, and a
 * completion status. This class is subclassed to
 * generate each one of the set of standard ORB exceptions.
 * <code>SystemException</code> extends
 * <code>java.lang.RuntimeException</code>; thus none of the
 * <code>SystemException</code> exceptions need to be
 * declared in signatures of the Java methods mapped from operations in
 * IDL interfaces.
 *
 * @see <A href="../../../../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 */

public abstract class SystemException extends java.lang.RuntimeException {

    /**
     * Constructs a <code>SystemException</code> exception with the specified detail
     * message, minor code, and completion status.
     * A detail message is a String that describes this particular exception.
     * @param reason the String containing a detail message
     * @param minor the minor code
     * @param completed the completion status
     */
    protected SystemException(String reason, int minor, CompletionStatus completed) {
	super(reason);
	this.minor = minor;
	this.completed = completed;
    }

    /**
     * Converts this exception to a representative string.
     */
    public String toString() {
	String completedString;

	String superString = super.toString();
	String minorString = "  minor code: " + minor;

	switch (completed.value()) {
	case CompletionStatus._COMPLETED_YES:
	    completedString = "  completed: Yes";
	    break;
	case CompletionStatus._COMPLETED_NO:
	    completedString = "  completed: No";
	    break;
	case CompletionStatus._COMPLETED_MAYBE:
	default:
	    completedString = " completed: Maybe";
	    break;
	}
	return superString + minorString + completedString;
    }


    /**
     * The CORBA Exception minor code.
     * @serial
     */
    public int minor;

    /**
     * The status of the operation that threw this exception.
     * @serial
     */
    public CompletionStatus completed;
}
