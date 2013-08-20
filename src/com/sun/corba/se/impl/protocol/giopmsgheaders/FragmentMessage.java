/*
 * @(#)FragmentMessage.java	1.9 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.protocol.giopmsgheaders;

/**
 * This interface captures the FragmentMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface FragmentMessage extends Message {
    int getRequestId();
    int getHeaderLength();
}
