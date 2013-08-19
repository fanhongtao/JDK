/*
 * @(#)ReplyMessage.java	1.10 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.iiop.messages;

import com.sun.corba.se.internal.core.ServiceContexts;
import org.omg.CORBA.SystemException;
import com.sun.corba.se.internal.core.IOR;

/**
 * This interface captures the ReplyMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface ReplyMessage extends Message {

    // Note: If the value, order, or number of these constants change,
    // please update the REPLY_MESSAGE_TO_PI_REPLY_STATUS table in PIORB.
    int NO_EXCEPTION = 0;
    int USER_EXCEPTION = 1;
    int SYSTEM_EXCEPTION = 2;
    int LOCATION_FORWARD = 3;
    int LOCATION_FORWARD_PERM = 4;  // 1.2
    int NEEDS_ADDRESSING_MODE = 5;  // 1.2

    int getRequestId();
    int getReplyStatus();
    ServiceContexts getServiceContexts();
    void setServiceContexts( ServiceContexts sc );
    SystemException getSystemException();
    IOR getIOR();
    void setIOR( IOR newIOR );
    short getAddrDisposition();
}
