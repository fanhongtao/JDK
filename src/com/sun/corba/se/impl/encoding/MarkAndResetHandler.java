/*
 * @(#)MarkAndResetHandler.java	1.7 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.impl.encoding;


/**
 * Defines an abstraction for a RestorableInputStream to 
 * implement mark/reset.
 */
interface MarkAndResetHandler
{
    void mark(RestorableInputStream inputStream);

    void fragmentationOccured(ByteBufferWithInfo newFragment);

    void reset();
}
