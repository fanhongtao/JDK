/*
 * @(#)RestorableInputStream.java	1.5 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
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
