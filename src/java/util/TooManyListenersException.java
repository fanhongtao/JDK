/*
 * @(#)TooManyListenersException.java	1.2 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.util;

/**
 * <p>
 * The <code> TooManyListenersException </code> Exception is used as part of
 * the Java Event model to annotate and implement a unicast special case of
 * a multicast Event Source.
 * </p>
 * <p>
 * The presence of a <code> throws TooManyListenersException </code> clause
 * on any given concrete implementation of the normally multicast semantic
 * <italic> void add &lt EventListenerType &gt () </italic> event listener
 * registration pattern is used to annotate that interface as implementing
 * a unicast Listener special case, that is, that one and only one Listener
 * may be registered on the particular event listener source concurrently.
 * </p>
 *
 * @see java.util.EventObject
 * @see java.util.EventListener
 * 
 * @version 1.2 98/07/01
 * @author Laurence P. G. Cable
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

