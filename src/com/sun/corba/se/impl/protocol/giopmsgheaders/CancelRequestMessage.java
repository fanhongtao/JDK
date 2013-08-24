/*
 * @(#)CancelRequestMessage.java	1.10 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.protocol.giopmsgheaders;

/**
 * This interface captures the CancelRequestMessage contract.
 *
 * @author Ram Jeyaraman 05/14/2000
 * @version 1.0
 */

public interface CancelRequestMessage extends Message {
    int CANCEL_REQ_MSG_SIZE = 4;
    int getRequestId();
}
