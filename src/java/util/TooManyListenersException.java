/*
 * @(#)TooManyListenersException.java	1.13 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * <p>
 * The <code> TooManyListenersException </code> Exception is used as part of
 * the Java Event model to annotate and implement a unicast special case of
 * a multicast Event Source.
 * </p>
 * <p>
 * The presence of a "throws TooManyListenersException" clause on any given
 * concrete implementation of the normally multicast "void addXyzEventListener"
 * event listener registration pattern is used to annotate that interface as
 * implementing a unicast Listener special case, that is, that one and only
 * one Listener may be registered on the particular event listener source
 * concurrently.
 * </p>
 *
 * @see java.util.EventObject
 * @see java.util.EventListener
 * 
 * @version 1.13 03/12/19
 * @author Laurence P. G. Cable
 * @since  JDK1.1
 */

public class TooManyListenersException extends Exception {

    /**
     * Constructs a TooManyListenersException with no detail message.
     * A detail message is a String that describes this particular exception.
     */

    public TooManyListenersException() {
	super();
    }

    /**
     * Constructs a TooManyListenersException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */

    public TooManyListenersException(String s) {
	super(s);
    }
}

