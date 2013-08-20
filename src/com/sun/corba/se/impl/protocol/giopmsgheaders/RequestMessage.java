/*
 * @(#)RequestMessage.java	1.11 04/06/21
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Principal;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;

/**
 * This interface captures the RequestMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface RequestMessage extends Message {

    byte RESPONSE_EXPECTED_BIT = 0x01;

    ServiceContexts getServiceContexts();
    int getRequestId();
    boolean isResponseExpected();
    byte[] getReserved();
    ObjectKey getObjectKey();
    String getOperation();
    Principal getPrincipal();

    // NOTE: This is a SUN PROPRIETARY EXTENSION
    void setThreadPoolToUse(int poolToUse);


} // interface RequestMessage
