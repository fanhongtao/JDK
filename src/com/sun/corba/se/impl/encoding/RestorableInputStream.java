/*
 * @(#)RestorableInputStream.java	1.6 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.impl.encoding;

/**
 * Defines the methods on an input stream which provide
 * a way to get and restore its internal state without
 * violating encapsulation.
 */
interface RestorableInputStream
{
    Object createStreamMemento();

    void restoreInternalState(Object streamMemento);
}
