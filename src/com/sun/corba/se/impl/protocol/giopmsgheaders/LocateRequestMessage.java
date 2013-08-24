/*
 * @(#)LocateRequestMessage.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.protocol.giopmsgheaders;

import com.sun.corba.se.spi.ior.ObjectKey;

/**
 * This interface captures the LocateRequestMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface LocateRequestMessage extends Message {
    int getRequestId();
    ObjectKey getObjectKey();
}
