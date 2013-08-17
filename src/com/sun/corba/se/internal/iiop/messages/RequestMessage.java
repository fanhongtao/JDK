/*
 * @(#)RequestMessage.java	1.7 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.Principal;
import com.sun.corba.se.internal.ior.ObjectKey;
import com.sun.corba.se.internal.core.ServiceContexts;

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

} // interface RequestMessage
