/*
 * @(#)CorbaResponseWaitingRoom.java	1.8 03/12/19
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.spi.transport;

import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import org.omg.CORBA.SystemException;

/**
 * @author Harold Carr
 */
public interface CorbaResponseWaitingRoom
    extends
	ResponseWaitingRoom
{
    public void signalExceptionToAllWaiters(SystemException systemException);

    public MessageMediator getMessageMediator(int requestId);
}

// End of file.
