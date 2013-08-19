/*
 * @(#)LocateReplyMessage.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import org.omg.CORBA.SystemException;
import com.sun.corba.se.internal.core.IOR;

/**
 * This interface captures the LocateReplyMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface LocateReplyMessage extends Message {

    int UNKNOWN_OBJECT = 0;
    int OBJECT_HERE = 1;
    int OBJECT_FORWARD = 2;
    int OBJECT_FORWARD_PERM = 3; // 1.2
    int LOC_SYSTEM_EXCEPTION = 4; // 1.2
    int LOC_NEEDS_ADDRESSING_MODE = 5; // 1.2

    int getRequestId();
    int getReplyStatus();
    SystemException getSystemException();
    IOR getIOR();
    short getAddrDisposition();
}
